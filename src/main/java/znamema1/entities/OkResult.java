package znamema1.entities;

/**
 * Class for a successful execution result.
 * @author martin
 */
public class OkResult extends Result {

    private String content;

    public OkResult(String content) {
        super(OK);
        this.content = content;
    }

    public OkResult() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OkResult{" + "content=" + content + '}';
    }
    
}
