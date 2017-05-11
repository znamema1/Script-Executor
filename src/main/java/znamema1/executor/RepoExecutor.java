package znamema1.executor;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import znamema1.ConfigLoader;
import znamema1.entities.RepoConfiguration;

/**
 *
 * @author martin
 */
public class RepoExecutor {

    // Docker daemon
    private static final String HOST = ConfigLoader.getString("DOCKERD_HOST");
    private static final int PORT = (int) ConfigLoader.getLong("DOCKERD_PORT");
    // Docker image
    private static final String IMAGE = ConfigLoader.getString("DOCKER_IMG");
    // Master script
    private static final String MASTER_SCRIPT = ConfigLoader.getString("MASTER_SCRIPT");
    // Resources limits
    private static final long RUNTIME_TIMEOUT = ConfigLoader.getLong("DOCKER_RUN_TIMEOUT");
    private static final String STORAGE = ConfigLoader.getString("DOCKER_STORAGE");
    private static final long MEMORY_LIMIT = ConfigLoader.getLong("DOCKER_MEMORY");
    private static final long MEMORY_SWAP_LIMIT = ConfigLoader.getLong("DOCKER_MEMORY_SWAP");
    private static final long CPU_PERIOD = ConfigLoader.getLong("DOCKER_CPU_PERIOD");
    private static final long CPU_QUOTA = ConfigLoader.getLong("DOCKER_CPU_QUOTA");

    private final DockerClient client;
    private final Timer timer;

    private final Integer orderNo;
    private final ArrayList<String> command;

    public RepoExecutor(RepoConfiguration conf, Integer id) {
        client = DefaultDockerClient.builder()
                .uri(URI.create("http://" + HOST + ":" + PORT))
                .build();

        timer = new Timer();

        command = new ArrayList<String>() {
            {
                add(MASTER_SCRIPT);
                add(conf.getGit());
                add(conf.getComm());
                add(conf.getFile());
                add(id.toString());
            }
        };

        this.orderNo = conf.getOrder();
    }

    public void exec() throws ScriptExecutorException, DockerException, InterruptedException {
        Map<String,String> storageOpts = new HashMap<String,String>() {
            {
                put("size", STORAGE);
            }
        };
        
        HostConfig hostConfig = HostConfig.builder()
                .cpuPeriod(CPU_PERIOD)
                .cpuQuota(CPU_QUOTA)
                .memory(MEMORY_LIMIT)
                .memorySwap(MEMORY_SWAP_LIMIT)
                .capAdd("NET_ADMIN")
                .storageOpt(storageOpts)
                .build();

        ContainerConfig config = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(IMAGE)
                .cmd(command)
                .build();

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
            throw new ScriptExecutorException("Script " + orderNo + " did not finish within timeout: " + RUNTIME_TIMEOUT / 1000 + " s");
        } finally {
            client.removeContainer(creation.id());
        }

        if (statusCode > 0) {
            String errMsg = "Script " + orderNo + ": ";
            switch (statusCode) {
                case 1:
                    errMsg += "Clone repository failed";
                    break;
                case 2:
                    errMsg += "Commmit checkout failed";
                    break;
                case 3:
                    errMsg += "Script not found";
                    break;
                case 5:
                    errMsg += "Execution error";
                    break;
                case 4:
                case 6:
                default:
                    errMsg += "Internal error";
                    if (ConfigLoader.debug()) {
                        errMsg += ": " + statusCode;
                    }
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
    }

    public void cleanup() {
        client.close();
        timer.cancel();
    }

}
