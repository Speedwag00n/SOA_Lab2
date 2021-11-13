package ilia.nemankov.service;

import ilia.nemankov.dto.PersonDTO;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface OscarsService {

    List<PersonDTO> getLoosers() throws BadResponseException;

    void humiliateByGenre(String genre) throws BadResponseException;

}
