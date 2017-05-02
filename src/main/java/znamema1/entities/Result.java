package znamema1.entities;

public class Result {

    private String status;
    private String output;
    private String errorMessage;

    public Result() {
    }

    public Result(String output) {
        this.status = "OK";
        this.output = output;
    }

    public Result(String status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
    

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
}
