package com.rettiwer.jbzdyreel.reel;

import com.rettiwer.jbzdyreel.scrapebot.PostMediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reel {
    @Id
    private Long id;
    private String title;
    @Column(name = "post_url")
    private String postUrl;
    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "media_type")
    private PostMediaType mediaType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public Reel(String title, String postUrl, String mediaUrl, PostMediaType mediaType, LocalDateTime createdAt) {
        this.title = title;
        this.postUrl = postUrl;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.createdAt = createdAt;
    }
}