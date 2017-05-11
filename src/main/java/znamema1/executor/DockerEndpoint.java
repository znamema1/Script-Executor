package znamema1.executor;

import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import znamema1.ConfigLoader;

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
        Response response;
        String statusMsg;

        if (input != null) {
            statusMsg = "Sending input data";
            response = Response.ok(input).build();
        } else {
            statusMsg = "No input stored";
            response = Response.status(404).build();
        }

        if (ConfigLoader.debug()) {
            System.out.println("----------------------------------------------------");
            System.out.println(new Date());
            System.out.println("ID: " + id);
            System.out.println("Status: " + statusMsg);
        }
        return response;
    }

    @POST
    @Path("/postoutput/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postOutputData(@PathParam("id") int id, String data) {
        Response response;
        String statusMsg;

        if (IO.insertOutput(id, data)) {
            statusMsg = "Output stored";
            response = Response.ok().build();
        } else {
            statusMsg = "Output rejected";
            response = Response.status(404).build();
        }

        if (ConfigLoader.debug()) {
            System.out.println("----------------------------------------------------");
            System.out.println(new Date());
            System.out.println("ID: " + id);
            System.out.println("Status: " + statusMsg);
        }
        return response;
    }

}
