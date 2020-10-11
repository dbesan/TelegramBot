package org.studyproject.rezkaBot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyproject.rezkaBot.domain.Film;
import org.studyproject.rezkaBot.services.ParsingService;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParsingController {


    private ParsingService parsingService;

    @Autowired
    public ParsingController(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    public List<String> search(String keyword) throws IOException {
        Set<Film> films = parsingService.searchFilms(parsingService.connectToPage(parsingService.prepareUrl(keyword)));
        return films
                .stream()
                .map(film -> film.getName()
                        + "\n" + film.getShortDescription()
                        + "\n" + film.getLink())
                .collect(Collectors.toList());
    }
}
