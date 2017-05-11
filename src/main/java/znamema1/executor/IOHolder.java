package znamema1.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import znamema1.ConfigLoader;

/**
 * Collection for storing input and output data from individual scripts to be
 * used for next script in chain.
 *
 * @author martin
 */
public class IOHolder {

    private static final Map<Integer, String> INPUT = new HashMap<Integer, String>();
    private static final Map<Integer, String> OUTPUT = new HashMap<Integer, String>();

    static {
        if (ConfigLoader.debug()) {
            INPUT.put(1, "toto je malý text A TOTO JE VELKÝ\nPříliš ŽLUŤOUČKÝ kůň ÚPĚL ďábelské ÓDY.");
        }
    }

    /**
     * Insert first input data into the input Map.
     * Should be called before executing the script chain.
     * @param data input data to be processed by the script chain
     * @return randomly generated unique id for the script chain
     */
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

    /**
     * Insert output data from an indiviual script into the output Map.
     * Fails if the script chain ID does not exist in the input Map.
     * @param id unique id of the script chain
     * @param data result of script execution
     * @return true if the output has been inserted, false otherwise
     */
    public boolean insertOutput(Integer id, String data) {
        synchronized (INPUT) {
            if (!INPUT.containsKey(id)) {
                return false;
            }
        }
        synchronized (OUTPUT) {
            OUTPUT.put(id, data);
        }
        return true;
    }

    public String getOutput(Integer id) {
        synchronized (OUTPUT) {
            return OUTPUT.get(id);
        }
    }

    /**
     * Returns final output data.
     * Should be called after the script chain finished.
     * @param id unique id of the script chain
     * @return output data from the last script from the chain
     */
    public String getResult(Integer id) {
        return getInput(id);
    }

    /**
     * Removes all data related to a unique script chain ID.
     * @param id unique id of the script chain
     */
    public void clear(Integer id) {
        synchronized (INPUT) {
            synchronized (OUTPUT) {
                INPUT.remove(id);
                OUTPUT.remove(id);
            }
        }
    }

    /**
     * Switches the output of one script from the output Map into the input Map,
     * waiting there for being picked up by next script in chain.
     * @param id unique id of the script chain
     */
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
