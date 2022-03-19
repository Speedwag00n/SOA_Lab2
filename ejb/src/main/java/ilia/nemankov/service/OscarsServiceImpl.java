package ilia.nemankov.service;

import ilia.nemankov.dto.MovieDTO;
import ilia.nemankov.dto.PersonDTO;
import net.sf.corn.converter.json.JsTypeComplex;
import net.sf.corn.converter.json.JsonStringParser;
import net.sf.corn.httpclient.HttpClient;
import net.sf.corn.httpclient.HttpResponse;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
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
    private String targetUrl;
    private final String targetId = "l1";

    public OscarsServiceImpl() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        this.client = ClientBuilder.newBuilder().build();

        initServices();
    }

    @Override
    public List<PersonDTO> getLoosers() throws BadResponseException {
        Response response = client
                .target(targetUrl + "/api/movies")
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != 200 && response.getStatus() != 404) {
            initServices();

            response = client
                    .target(targetUrl + "/api/movies")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() != 200 && response.getStatus() != 404) {
                throw new BadResponseException("Could not process movies", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
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
            initServices();

            response = client
                    .target(targetUrl + "/api/movies")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() != 200 && response.getStatus() != 404) {
                throw new BadResponseException("Could not process movies", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
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

    private boolean initServices() {
        try {
            String sdUrl = System.getenv("CONSUL_URL");

            String url = sdUrl + "/v1/agent/service/" + targetId;

            HttpClient client = new HttpClient(new URI(url));
            HttpResponse response = client.sendData(HttpClient.HTTP_METHOD.GET);
            if (!response.hasError()) {
                String jsonString = response.getData();
                JsTypeComplex jsonResponse = (JsTypeComplex) JsonStringParser.parseJsonString(jsonString);

                targetUrl = "http://" + jsonResponse.get("Address").toString().replace("\"", "") + ":" + jsonResponse.get("Port");

                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
