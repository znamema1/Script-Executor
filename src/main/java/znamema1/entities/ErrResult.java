package znamema1.entities;

import znamema1.executor.ScriptExecutorException;

/**
 * Class for an error execution result.
 * @author martin
 */
public class ErrResult extends Result {

    private String errorMessage;

    public ErrResult(String errorMessage) {
        super(ERR);
        this.errorMessage = errorMessage;
    }

    public ErrResult(ScriptExecutorException ex) {
        super(ERR);
        this.errorMessage = ex.getMessage();
    }

    public ErrResult() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ErrResult{" + "errorMessage=" + errorMessage + '}';
    }

}
