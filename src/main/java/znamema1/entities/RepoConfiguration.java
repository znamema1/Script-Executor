package znamema1.entities;

/**
 *
 * @author martin
 */
public class RepoConfiguration {

    private String git;
    private String file;
    private String comm;
    private Integer order;

    public RepoConfiguration() {
    }

    public RepoConfiguration(String git, String file, String comm, Integer order) {
        this.git = git;
        this.file = file;
        this.comm = comm;
        this.order = order;
    }

    public String getGit() {
        return git;
    }

    public void setGit(String git) {
        this.git = git;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getComm() {
        return comm;
    }

    public void setComm(String comm) {
        this.comm = comm;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "RepoConfiguration{" + "git=" + git + ", file=" + file + ", comm=" + comm + ", order=" + order + '}';
    }

}
