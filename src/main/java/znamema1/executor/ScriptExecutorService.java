package znamema1.executor;

import com.spotify.docker.client.exceptions.DockerException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import znamema1.ConfigLoader;
import znamema1.entities.ErrResult;
import znamema1.entities.OkResult;
import znamema1.entities.RepoConfiguration;
import znamema1.entities.Result;
import znamema1.entities.ScriptConfiguration;

/**
 * Class responsible for executing a whole script chain and piping the
 * input/output. Every script in the chain is passed to the RepoExecutor.
 *
 * @author Martin Znamenacek
 */
public class ScriptExecutorService {

    private static final Semaphore SEMAPHORE = new Semaphore((int) ConfigLoader.getLong("DOCKER_MAX_THREADS"), true);
    private static final IOHolder IO = new IOHolder();

    /**
     * Start the execution of the script chain.
     *
     * @param conf Script chain configuration
     * @return Result, either OK or Err depending on the chain result.
     */
    public Result executeScript(ScriptConfiguration conf) {
        try {
            SEMAPHORE.acquire();

            String res = executeRepos(conf);

            return new OkResult(res);
        } catch (ScriptExecutorException ex) {

            return new ErrResult(ex);
        } catch (Exception ex) {

            return new ErrResult("Internal error: " + ex.getMessage());
        } finally {
            SEMAPHORE.release();
        }
    }

    /**
     * Executes all the scripts in the chain one at a time.
     *
     * @param conf Script chain configuration
     * @return output data from the last script from the chain
     * @throws ScriptExecutorException when an error occurred
     */
    private String executeRepos(ScriptConfiguration conf) throws ScriptExecutorException {
        validateConfiguration(conf);
        Integer id = IO.insertInput(conf.getInput());
        try {
            for (RepoConfiguration repo : conf.getRepos()) {
                executeRepo(repo, id);
                IO.switchIO(id);
            }
            String output = IO.getResult(id);
            return output;
        } finally {
            IO.clear(id);
        }

    }

    /**
     *
     * @param conf Configuration fo the repository to be run
     * @param id unique id of the script chain
     * @throws ScriptExecutorException when an error occurs
     */
    private void executeRepo(RepoConfiguration conf, Integer id) throws ScriptExecutorException {
        RepoExecutor executor = new RepoExecutor(conf, id);
        try {
            executor.exec();
        } catch (InterruptedException | DockerException ex) {
            throw new ScriptExecutorException("Script number " + conf.getOrder() + " failed execution: " + ex.getMessage());
        } finally {
            executor.cleanup();
        }
    }

    /**
     * Validates Script chain configuration recevied by the Executor endpoint.
     * @param conf Script chain configuration to be validated
     * @throws ScriptExecutorException when the configuration is invalid
     */
    private void validateConfiguration(ScriptConfiguration conf) throws ScriptExecutorException {
        if (conf.getRepos() == null || conf.getRepos().length <= 0) {
            throw new ScriptExecutorException("No repos to run");
        }

        RepoConfiguration[] repos = conf.getRepos();

        Arrays.sort(repos, new Comparator<RepoConfiguration>() {
            @Override
            public int compare(RepoConfiguration o1, RepoConfiguration o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });

        for (int i = 0; i < repos.length; i++) {
            String git = repos[i].getGit();
            String file = repos[i].getFile();
            String comm = repos[i].getComm();
            Integer order = repos[i].getOrder();
            if (git == null || file == null || comm == null || order == null) {
                throw new ScriptExecutorException("Script " + i + " contains invalid data");
            }
        }

        conf.setRepos(repos);
    }

}
