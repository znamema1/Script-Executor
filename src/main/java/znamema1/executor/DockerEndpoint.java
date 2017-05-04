package znamema1.executor;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author martin
 */
@Path("/docker")
public class DockerEndpoint {

    private static final IOHolder IO = new IOHolder();

    @GET
    @Path("/getinput/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInputData(@PathParam("id") int id) {
        String input = IO.getInput(id);
        return Response.ok(input).build();
    }

    @POST
    @Path("/postoutput/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postOutputData(@PathParam("id") int id, String data) {
        IO.insertOutput(id, data);
        return Response.ok("OK").build();
    }

    //debug
    @GET
    @Path("/response/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResponse(@PathParam("id") int id) {
        String output = IO.getOutput(id);
        return Response.ok(output).build();
    }

}
