package znamema1.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author martin
 */
public class IOHolder {

    private static final Map<Integer, String> INPUT = new HashMap<Integer, String>();
    private static final Map<Integer, String> OUTPUT = new HashMap<Integer, String>();

    public Integer insertInput(String data) {
        synchronized (INPUT) {
            Integer id = getRandomId();
            INPUT.put(id, data);
            return id;
        }
    }

    public String getInput(Integer id) {
        synchronized (INPUT) {
            return INPUT.get(id);
        }
    }

    public void insertOutput(Integer id, String data) {
        synchronized (OUTPUT) {
            OUTPUT.put(id, data);
        }
    }

    public String getOutput(Integer id) {
        synchronized (OUTPUT) {
            return OUTPUT.get(id);
        }
    }

    public void clear(Integer id) {
        synchronized (INPUT) {
            synchronized (OUTPUT) {
                INPUT.remove(id);
                OUTPUT.remove(id);
            }
        }
    }

    public void switchIO(Integer id) {
        synchronized (INPUT) {
            synchronized (OUTPUT) {
                String out = OUTPUT.remove(id);
                INPUT.put(id, out);
            }
        }
    }

    private Integer getRandomId() {
        Random r = new Random();
        Integer id;
        do {
            id = r.nextInt();
        } while (INPUT.containsKey(id));
        return id;
    }

}
