package znamema1.executor;

import java.util.TimerTask;

/**
 *
 * @author martin
 */
public class ScriptKiller extends TimerTask {

    private final Thread target;

    public ScriptKiller(Thread target) {
        this.target = target;
    }

    @Override
    public void run() {
        target.interrupt();
    }
}
