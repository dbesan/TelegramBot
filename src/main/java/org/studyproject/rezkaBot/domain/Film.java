package org.studyproject.rezkaBot.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private String id;
    private String name;
    private String year;
    private String type;
    private String link;
    private String imgLink;
    private String shortDescription;

}
