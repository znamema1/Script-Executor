package znamema1.executor;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Timer;
import znamema1.entities.RepoConfiguration;

/**
 *
 * @author martin
 */
public class RepoExecutor {

    private static final String HOST = "localhost"; //config
    private static final int PORT = 2375; // config
    private static final String IMAGE = "test"; //config

    private final DockerClient client;
    private final Timer timer;

    private final ArrayList<String> command;

    public RepoExecutor(RepoConfiguration conf, Integer id) {
        client = DefaultDockerClient.builder()
                .uri(URI.create("http://" + HOST + ":" + PORT))
                .build();

        timer = new Timer();

        command = new ArrayList<>();
        command.add("./masterscript.sh"); //config
        command.add(conf.getGit());
        command.add(conf.getFile());
        command.add(conf.getComm());
        command.add(id.toString());
    }

    public void exec() throws ScriptExecutorException, DockerException, InterruptedException {// při nepodařeném clone atd - scriptexecutorexception
        ContainerConfig config = ContainerConfig.builder().
                image(IMAGE).
                cmd(command).
                build();

        ContainerCreation creation = client.createContainer(config);

        ScriptKiller killer = new ScriptKiller(Thread.currentThread());

        client.startContainer(creation.id());

        try {
            timer.schedule(killer, 5 * 1000);
            client.waitContainer(creation.id());
            killer.cancel();
        } catch (InterruptedException ex) {
            System.out.println("Killing container: " + creation.id());
            client.killContainer(creation.id());
            return;
        }

        // debug
        LogStream stdout = client.logs(creation.id(), DockerClient.LogsParam.stdout());
        String output = stdout.readFully();
        System.out.println("Output stdout: ---");
        System.out.print(output);
        System.out.println("\n------------------");

        LogStream stderr = client.logs(creation.id(), DockerClient.LogsParam.stderr());
        String errput = stderr.readFully();
        System.out.println("Output stderr: ---");
        System.out.print(errput);
        System.out.println("\n------------------");

        client.removeContainer(creation.id());
    }

    public void cleanup() {
        client.close();
        timer.cancel();
    }

}
