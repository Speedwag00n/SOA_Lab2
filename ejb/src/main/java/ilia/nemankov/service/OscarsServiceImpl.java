package ilia.nemankov.service;

import ilia.nemankov.dto.MovieDTO;
import ilia.nemankov.dto.PersonDTO;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class OscarsServiceImpl implements OscarsService {

    private final Client client;
    private final String targetUrl;

    public OscarsServiceImpl() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        String filename = System.getenv("KEYSTORE_LOCATION");
        FileInputStream is = new FileInputStream(filename);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        String password = System.getenv("KEYSTORE_PASSWORD");
        keystore.load(is, password.toCharArray());
        this.client = ClientBuilder.newBuilder().trustStore(keystore).build();
        targetUrl = System.getenv("MAIN_INSTANCE_LOCATION");
    }

    @Override
    public List<PersonDTO> getLoosers() throws BadResponseException {
        Response response = client
                .target(targetUrl + "/api/movies")
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != 200 && response.getStatus() != 404) {
            throw new BadResponseException("Could not process movies", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
    public void humiliateByGenre(String genre) throws BadResponseException {
        validate(genre);

        Response response = client
                .target(targetUrl + "/api/movies")
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != 200 && response.getStatus() != 404) {
            throw new BadResponseException("Could not process movies", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
                        throw new BadResponseException("Could not update some movie", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                }
            }
        }
    }

    private void validate(String genre) throws BadResponseException {
        if (genre == null) {
            throw new BadResponseException("'Genre' must be specified", HttpServletResponse.SC_BAD_REQUEST);
        }

        if (!(genre.length() < 32) || !(genre.length() > 0)) {
            throw new BadResponseException("Length of field 'genre' must be bigger than 0 and less than 32", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
