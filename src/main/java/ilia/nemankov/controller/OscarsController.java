package ilia.nemankov.controller;

import ilia.nemankov.dto.PersonDTO;
import ilia.nemankov.service.OscarsService;
import ilia.nemankov.service.OscarsServiceImpl;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/directors")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class OscarsController {

    private final OscarsService oscarsService;

    public OscarsController() {
        this.oscarsService = new OscarsServiceImpl();
    }

    @GET
    @Path("/get-loosers")
    public Response getLoosers() {
        List<PersonDTO> persons = oscarsService.getLoosers();

        if (persons.size() != 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(persons).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    @POST
    @Path("/humiliate-by-genre/{genre}")
    public Response humiliateByGenre(@PathParam("genre") String genre) {
        oscarsService.humiliateByGenre(genre);
        return Response.status(HttpServletResponse.SC_OK).build();
    }

}
