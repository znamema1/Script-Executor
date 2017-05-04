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
    private static final int RUNTIME_TIMEOUT = 5 * 1000; //config

    private final DockerClient client;
    private final Timer timer;

    private final Integer orderNo;
    private final ArrayList<String> command;

    public RepoExecutor(RepoConfiguration conf, Integer id) {
        client = DefaultDockerClient.builder()
                .uri(URI.create("http://" + HOST + ":" + PORT))
                .build();

        timer = new Timer();

        command = new ArrayList<>();
        command.add("./masterscript.sh"); //config
        command.add(conf.getGit());
        command.add(conf.getComm());
        command.add(conf.getFile());
        command.add(id.toString());
        
        this.orderNo = conf.getOrder();
    }

    public void exec() throws ScriptExecutorException, DockerException, InterruptedException {// při nepodařeném clone atd - scriptexecutorexception
        ContainerConfig config = ContainerConfig.builder().
                image(IMAGE).
                cmd(command).
                build();

        ContainerCreation creation = client.createContainer(config);

        ScriptKiller killer = new ScriptKiller(Thread.currentThread());

        client.startContainer(creation.id());

        int statusCode;
        try {
            timer.schedule(killer, RUNTIME_TIMEOUT);
            statusCode = client.waitContainer(creation.id()).statusCode();
            killer.cancel();
        } catch (InterruptedException ex) {
            System.out.println("Killing container: " + creation.id());
            client.killContainer(creation.id());
            throw new ScriptExecutorException("Script " + orderNo + " did not finish within timeout: " + RUNTIME_TIMEOUT + "ms");
        }
        
        if (statusCode > 0) {
            String errMsg = "Script " + orderNo + ": ";
            switch (statusCode) {
                case 1: errMsg += "Could not clone repository";
                    break;
                case 2: errMsg += "Could not checkout commmit";
                    break;
                case 3: errMsg += "Could not find file";
                    break;
                case 5: errMsg += "Script error: " + statusCode;
                    break;
                case 4:
                case 6: 
                default: errMsg += "Internal error";
                    break;
            }
            throw new ScriptExecutorException(errMsg);
        }
        
//        // debug
//        LogStream stdout = client.logs(creation.id(), DockerClient.LogsParam.stdout());
//        String output = stdout.readFully();
//        System.out.println("Output stdout: ---");
//        System.out.print(output);
//        System.out.println("\n------------------");
//
//        LogStream stderr = client.logs(creation.id(), DockerClient.LogsParam.stderr());
//        String errput = stderr.readFully();
//        System.out.println("Output stderr: ---");
//        System.out.print(errput);
//        System.out.println("\n------------------");

        client.removeContainer(creation.id());
    }

    public void cleanup() {
        client.close();
        timer.cancel();
    }
    
}
