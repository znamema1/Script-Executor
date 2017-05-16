package znamema1.executor;

import java.util.TimerTask;

/**
 * Simple Timer task to be run after a specific timeout. Its only purpose is to
 * interrupt the thread waiting for the container.
 *
 * @author Martin Znamenacek
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
