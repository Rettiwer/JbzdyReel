package com.rettiwer.jbzdyreel.scrapebot;

import com.rettiwer.jbzdyreel.reel.Reel;
import com.rettiwer.jbzdyreel.reel.ReelRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ScrapeBotService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ScrapeBotService.class);

    private final ReelRepository reelRepository;

    public ScrapeBotService(ReelRepository reelRepository) {
        this.reelRepository = reelRepository;
    }

    @Override
    public void run() {
        try {
            //Scrape 100 latest pages if repository is empty
            if (reelRepository.findAll().isEmpty()) {
                scrapeLatestPages();
            }

            //Update memes database every 10 seconds
            while (true) {
                log.warn("Updating meme database...");
                TimeUnit.SECONDS.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void scrapeLatestPages() throws IOException, InterruptedException {
        log.warn("Scraping latest pages started");

        List<Reel> scrapedPosts = new ArrayList<>();
        for (int i = 100; i > 0; i--) {
            scrapedPosts.addAll(scrapeSinglePage(i));
            Thread.sleep(500);
        }
        //
        // reelRepository.saveAll(scrapedPosts);
    }

    public List<Reel> scrapeSinglePage(int pageNumber) throws IOException {
        log.warn("Scraping page no.: " + pageNumber);

        Document doc = Jsoup.connect("https://jbzd.com.pl/str/" + pageNumber).get();

        List<Element> posts = doc.select("#content-container > article");

        List<Reel> reels = new ArrayList<>();
        for (Element post : posts) {
            PostMediaType postMediaType = getPostMediaType(post);
            if (postMediaType.equals(PostMediaType.IMAGE) || postMediaType.equals(PostMediaType.VIDEO))
                reels.add(scrapePost(post, postMediaType));
        }

        return reels;
    }

    public Reel scrapePost(Element post, PostMediaType postMediaType) {
        long id = Integer.parseInt(post.attributes().get("data-content-id"));
        String title = post.selectFirst(".article-title > a").text().strip();
        String postUrl = post.selectFirst(".article-title > a").attributes().get("href");

        log.warn(postUrl);

        String createdAtStr = post.selectFirst(".article-info > span").attributes().get("data-date");
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime lds = LocalDateTime.parse(createdAtStr, dTF);

        String mediaUrl = "";
        switch (postMediaType) {
            case IMAGE -> mediaUrl = post.selectFirst("img.article-image").attributes().get("src");
            case VIDEO -> mediaUrl = post.selectFirst("videoplyr").attributes().get("video_url");
        }

        return new Reel(id, title, postUrl, mediaUrl, lds);
    }

    public PostMediaType getPostMediaType(Element element) {
        Element articleContent = element.selectFirst(".article-content");

        if (!element.select(".article-locked").isEmpty())
            return PostMediaType.BLOCKED;

        if (!articleContent.select(".article-description").isEmpty())
            return PostMediaType.TEXT;

        String postType = articleContent.selectFirst(".article-image").firstElementChild()
                .firstElementChild().nodeName();

        return switch (postType) {
            case "img" -> PostMediaType.IMAGE;
            case "videoplyr" -> PostMediaType.VIDEO;
            case "iframe" -> PostMediaType.REMOTE;
            case "a" -> PostMediaType.GALLERY;
            default -> PostMediaType.NOT_DEFINED;
        };
    }
}