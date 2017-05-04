package znamema1.executor;

import java.util.concurrent.Semaphore;
import znamema1.entities.ErrResult;
import znamema1.entities.OkResult;
import znamema1.entities.RepoConfiguration;
import znamema1.entities.Result;
import znamema1.entities.ScriptConfiguration;

public class ScriptExecutorService {

    private static final Semaphore SEMAPHORE = new Semaphore(2, true);//config - threads

    public Result executeScript(ScriptConfiguration conf) {
        try {
            SEMAPHORE.acquire();
        } catch (Exception ex) {
            return new ErrResult("Internal Error!");
        }

        try {
            String res = executeRepos(conf);
            return new OkResult(res);
        } catch (Exception ex) {
            return new ErrResult( ex.getMessage());
        } finally {
            SEMAPHORE.release();
        }
    }

    private String executeRepos(ScriptConfiguration conf) throws ScriptExecutorException {
        validateConfiguration(conf);
        String io = conf.getInput();
        for (RepoConfiguration repo : conf.getRepos()) {
            io = executeRepo(repo, io);
        }
        return io;
    }

    private String executeRepo(RepoConfiguration conf, String input) {

        return "bla";
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
