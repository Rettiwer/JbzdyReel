package com.rettiwer.jbzdyreel.scrapebot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScrapeBot implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ScrapeBot.class);

    @Override
    public void run() {
        try {
            scrapeLatestPages();
            //scrapeSinglePage(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void scrapeLatestPages() throws IOException {
        log.warn("Scraping latest pages started");

        for (int i = 10; i > 0; i--)
            scrapeSinglePage(i);
    }

    public int scrapeSinglePage(int pageNumber) throws IOException {
        log.warn("Scraping page no.: " + pageNumber);

        Document doc = Jsoup.connect("https://jbzd.com.pl/str/" + pageNumber).get();

        List<Element> posts = doc.select("#content-container > article");

        for (Element post : posts) {
            if (post.select(".article-locked").isEmpty()) {
                scrapePost(post);
            }
        }

        return 0;
    }

    public int scrapePost(Element post) {
        int id = Integer.parseInt(post.attributes().get("data-content-id"));
        String title = post.selectFirst(".article-title > a").text().strip();
        String postUrl = post.selectFirst(".article-title > a").attributes().get("href");

        log.warn(postUrl);

        String createdAtStr = post.selectFirst(".article-info > span").attributes().get("data-date");
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime lds = LocalDateTime.parse(createdAtStr, dTF);

        getPostMediaType(post);

        boolean isMediaVideo = post.select(".video-player").isEmpty();
        String mediaUrl = "";
      /*  if (!isMediaVideo) {
            mediaUrl = post.selectFirst("videoplyr").attributes().get("video_url");
        } else
            mediaUrl = post.selectFirst("img.article-image").attributes().get("src");
*/

       /* log.warn("ID " + id + "\n" +
                "Title " + title + "\n" +
                "PostURL " + postUrl + "\n" +
                "CreatedAt " + lds.toString() + "\n" +
                "MediaUrl " + mediaUrl + "\n"
        );*/
        return 0;
    }

    public PostMediaType getPostMediaType(Element element) {
        Element articleContent = element.selectFirst(".article-content").lastElementChild().firstElementChild();

        log.warn(articleContent.classNames().toString());


        return PostMediaType.GALLERY;
    }
}