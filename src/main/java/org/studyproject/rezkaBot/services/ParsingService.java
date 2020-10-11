package org.studyproject.rezkaBot.services;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.studyproject.rezkaBot.domain.Film;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.studyproject.rezkaBot.constants.Constants.CONNECTS_CACHE_SIZE;

@Service
public class ParsingService {
    @Value("${bot.url}")
    private String rezkaUrl;

    public String prepareUrl(String keyword) {
        return rezkaUrl + keyword;
    }

    public Document connectToPage(String url) throws IOException {
        int cacheSize = CONNECTS_CACHE_SIZE;
        Map<String, Document> cache = new HashMap<>();
        Document document;
        try {
            if (cache.containsKey(url)) {
                return cache.get(url);
            } else {
                document = Jsoup.connect(url)
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com")
                        .get();
                if (cache.size() > cacheSize) {
                    cache.clear();
                }
            }
            cache.put(url, document);
        } catch (IllegalArgumentException | SocketTimeoutException | HttpStatusException | SSLException e) {
            document = null;
        }

        return document;
    }

    public int countPages(Document document) {
        int count = 0;
        Elements pages;
        pages = document.select("div.b-content__inline_items").select("div.b-navigation").select("a");
        count = pages.size();
        return count;
    }


    public Set<Film> searchFilms(Document document) {
        Elements filmDiv = document.select("div.b-content__inline_item");
        Set<Film> films = filmDiv.stream().map(
                element -> {
                    Film film = new Film();
                    film.setId(element.attr("data-id"));
                    film.setYear(element.select("div.b-content__inline_item-link").select("div").text().replaceAll("\\D", "").substring(0, 4));
                    film.setName(element.select("img").first().attr("alt"));
                    film.setLink(element.select("a").first().attr("href"));
                    film.setType(element.select("i.entity").first().text());
                    film.setImgLink(element.select("div.b-content__inline_item-cover").select("a").select("img").first().absUrl("src"));
                    film.setShortDescription(element.select("div.b-content__inline_item-link").select("div").get(1).text());
                    return film;
                }).collect(Collectors.toCollection(LinkedHashSet::new));

        return films;
    }
}
