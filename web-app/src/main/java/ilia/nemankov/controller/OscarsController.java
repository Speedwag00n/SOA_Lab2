package ilia.nemankov.controller;

import ilia.nemankov.dto.PersonDTO;
import ilia.nemankov.service.BadResponseException;
import ilia.nemankov.service.OscarsService;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/directors")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class OscarsController {

    private final OscarsService oscarsService;

    public OscarsController() throws NamingException {
        Context context = new ContextProviderImpl().getContext();
        Object ref = context.lookup("pool/OscarsServiceImpl!ilia.nemankov.service.OscarsService");
        this.oscarsService = (OscarsService) PortableRemoteObject.narrow(ref, OscarsService.class);
    }

    @GET
    @Path("/get-loosers")
    public Response getLoosers() {
        System.out.println(1);

        try {
            List<PersonDTO> persons = oscarsService.getLoosers();

            if (persons.size() != 0) {
                return Response.status(HttpServletResponse.SC_OK).entity(persons).build();
            } else {
                return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
            }
        } catch(BadResponseException e) {
            return Response.status(e.getResponseCode()).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/humiliate-by-genre/{genre}")
    public Response humiliateByGenre(@PathParam("genre") String genre) {
        System.out.println(1);

        try {
            oscarsService.humiliateByGenre(genre);
            return Response.status(HttpServletResponse.SC_OK).build();
        } catch(BadResponseException e) {
            return Response.status(e.getResponseCode()).entity(e.getMessage()).build();
        }
    }

}
