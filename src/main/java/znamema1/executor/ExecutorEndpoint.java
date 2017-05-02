package znamema1.executor;

import znamema1.entities.ScriptConfiguration;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import znamema1.entities.Result;

@Path("/runner")
public class ExecutorEndpoint {

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response runScript(ScriptConfiguration conf) {
        ScriptExecutorService service = new ScriptExecutorService();
        Result result = service.executeScript(conf);

        return Response.ok(result).build();
    }

}
