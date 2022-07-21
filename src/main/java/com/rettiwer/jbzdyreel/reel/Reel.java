package com.rettiwer.jbzdyreel.reel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reel {
    @Id
    private Long id;
    private String title;
    private String author;
    private String authorAvatarUrl;
    private String dataUrl;

    public Reel(String title, String author, String dataUrl) {
        this.title = title;
        this.author = author;
        this.dataUrl = dataUrl;
    }
}
