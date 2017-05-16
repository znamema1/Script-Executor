package znamema1.entities;

/**
 * Bean for a configuration of multiple repository script to run.
 * Also contains input for the script chain.
 * For every run request an instance will be created.
 * @author Martin Znamenacek
 */
public class ScriptConfiguration {

    private String input;
    private RepoConfiguration[] repos;

    public ScriptConfiguration() {
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public RepoConfiguration[] getRepos() {
        return repos;
    }

    public void setRepos(RepoConfiguration[] repos) {
        this.repos = repos;
    }

    @Override
    public String toString() {
        return "ScriptConfiguration{" + "input=" + input + ", repos=" + reposToString() + '}';
    }

    private String reposToString() {
        String ret = "";
        for (RepoConfiguration repo : repos) {
            ret += repo.toString();
        }
        return ret;
    }

}
