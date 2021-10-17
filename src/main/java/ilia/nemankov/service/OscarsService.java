package ilia.nemankov.service;

import ilia.nemankov.dto.PersonDTO;

import java.util.List;

public interface OscarsService {

    List<PersonDTO> getLoosers();

    void humiliateByGenre(String genre);

}
