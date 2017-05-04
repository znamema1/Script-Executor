package znamema1.executor;

import com.spotify.docker.client.exceptions.DockerException;
import java.util.concurrent.Semaphore;
import znamema1.entities.ErrResult;
import znamema1.entities.OkResult;
import znamema1.entities.RepoConfiguration;
import znamema1.entities.Result;
import znamema1.entities.ScriptConfiguration;

/**
 *
 * @author martin
 */
public class ScriptExecutorService {

    private static final Semaphore SEMAPHORE = new Semaphore(2, true);//config - threads
    private static final IOHolder IO = new IOHolder();

    public Result executeScript(ScriptConfiguration conf) {
        try {
            SEMAPHORE.acquire();

            String res = executeRepos(conf);

            return new OkResult(res);
        } catch (ScriptExecutorException ex) {

            return new ErrResult(ex);
        } catch (Exception ex) {

            return new ErrResult("Internal Error!");
        } finally {
            SEMAPHORE.release();
        }
    }

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

    private void executeRepo(RepoConfiguration conf, Integer id) throws ScriptExecutorException {
        RepoExecutor executor = new RepoExecutor(conf, id);
        try {
            executor.exec();
        } catch (InterruptedException | DockerException ex) {
            throw new ScriptExecutorException("Script number " + conf.getOrder() + " failed execution.");
        } finally {
            executor.cleanup();
        }
    }

    private void validateConfiguration(ScriptConfiguration conf) throws ScriptExecutorException {
        if (conf.getRepos() == null || conf.getRepos().length <= 0) {
            throw new ScriptExecutorException("No repos to run!");
        }
        for (int i = 0; i < conf.getRepos().length; i++) {
            String git = conf.getRepos()[i].getGit();
            String file = conf.getRepos()[i].getFile();
            String comm = conf.getRepos()[i].getComm();
            Integer order = conf.getRepos()[i].getOrder();
            if (git == null || file == null || comm == null || order == null) {
                throw new ScriptExecutorException("Script " + i + " contains nonvalid data!");
            }
        }
    }

}
