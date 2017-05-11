package znamema1.entities;

/**
 * Abstract class standing for a general result.
 * @author martin
 */
public abstract class Result {

    protected String status;
    protected static final String OK = "OK";
    protected static final String ERR = "ERROR";

    public Result(String status) {
        this.status = status;
    }

    public Result() {
    }

}
