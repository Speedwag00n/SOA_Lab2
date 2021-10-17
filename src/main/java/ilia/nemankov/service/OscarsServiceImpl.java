package ilia.nemankov.service;

import ilia.nemankov.dto.MovieDTO;
import ilia.nemankov.dto.PersonDTO;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

public class OscarsServiceImpl implements OscarsService {

    private final Client client;
    private final String targetUrl;

    public OscarsServiceImpl() {
        this.client = ClientBuilder.newClient();
        targetUrl = System.getenv("MAIN_INSTANCE_LOCATION");
    }

    @Override
    public List<PersonDTO> getLoosers() {
        Response response = client
                .target(targetUrl + "/api/movies")
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != 200 && response.getStatus() != 404) {
            throw new NotFoundException(Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Could not process movies").build());
        }

        List<MovieDTO> movies = response.readEntity(new GenericType<List<MovieDTO>>() {});

        Map<PersonDTO, Long> oscarsByPerson = new HashMap<>();

        for (MovieDTO movie : movies) {
            PersonDTO screenWriter = movie.getScreenWriter();
            if (screenWriter != null) {
                if (!oscarsByPerson.containsKey(screenWriter)) {
                    oscarsByPerson.put(screenWriter, (long) movie.getOscarsCount());
                } else {
                    oscarsByPerson.put(screenWriter, oscarsByPerson.get(screenWriter) + movie.getOscarsCount());
                }
            }
        }

        List<PersonDTO> result = new ArrayList<>();

        for (PersonDTO screenWriter : oscarsByPerson.keySet()) {
            if (oscarsByPerson.get(screenWriter) == 0) {
                result.add(screenWriter);
            }
        }

        return result;
    }

    @Override
    public void humiliateByGenre(String genre) {
        validate(genre);

        Response response = client
                .target(targetUrl + "/api/movies")
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != 200 && response.getStatus() != 404) {
            throw new NotFoundException(Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Could not process movies").build());
        }

        List<MovieDTO> movies = response.readEntity(new GenericType<List<MovieDTO>>() {});

        for (MovieDTO movie : movies) {
            PersonDTO screenWriter = movie.getScreenWriter();
            if (screenWriter != null) {
                if (movie.getGenre().equals(genre)) {
                    movie.setOscarsCount(0);

                    response = client
                            .target(targetUrl + "/api/movies")
                            .request(MediaType.APPLICATION_JSON)
                            .put(Entity.entity(movie, MediaType.APPLICATION_JSON));

                    if (response.getStatus() != 200) {
                        throw new NotFoundException(Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Could update some movie").build());
                    }
                }
            }
        }
    }

    private void validate(String genre) {
        if (genre == null) {
            throw new BadRequestException(Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("'Genre' must be specified").build());
        }

        if (!(genre.length() < 32) || !(genre.length() > 0)) {
            throw new BadRequestException(Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Length of field 'genre' must be bigger than 0 and less than 32").build());
        }
    }

}
