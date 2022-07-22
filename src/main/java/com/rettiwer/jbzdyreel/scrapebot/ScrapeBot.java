package com.rettiwer.jbzdyreel.scrapebot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScrapeBot implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ScrapeBot.class);

    private WebDriver webDriver;

    @Override
    public void run() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.setHeadless(true);
        webDriver = new ChromeDriver(chromeOptions);


        try {
            scrapeSinglePage(1);
            webDriver.quit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


/*
    public void scrapeWholePage() {
        try {
            log.warn("Scraping whole page started");

            int pagesAmount = -1;
            while (pagesAmount == -1) {
                pagesAmount = scrapePagesAmount();
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int scrapePagesAmount() throws IOException {
        Document doc = Jsoup.connect("https://jbzd.com.pl/str/1").get();
        Elements scripts = doc.select("script");

        int pagesAmount = -1;

        for (Element element : scripts) {
            String content = element.toString();

            if (content.contains("pages:")) {
                String[] partA = content.split("pages:");
                String[] partB = partA[1].split(",");

                pagesAmount = Integer.parseInt(partB[0].strip());
                log.warn("Pages amount to scrap: " + pagesAmount);
            }
        }

        return pagesAmount;
    }

 */
    public int scrapeSinglePage(int pageNumber) throws IOException {
        webDriver.get("https://jbzd.com.pl/str/" + pageNumber);

        List<WebElement> posts = webDriver.findElements(By.cssSelector("#content-container > article"));

        for (WebElement webElement : posts) {
            scrapePost(webElement);
        }
        return 0;
    }

    public int scrapePost(WebElement post) {
        int id = Integer.parseInt(post.getAttribute("data-content-id"));
        String title = post.findElement(By.cssSelector(".article-title > a")).getAttribute("innerHTML").strip();
        String postUrl = post.findElement(By.cssSelector(".article-title > a")).getAttribute("href");

        String createdAtStr = post.findElement(By.cssSelector(".article-info > span")).getAttribute("data-date");
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime lds = LocalDateTime.parse(createdAtStr, dTF);


        boolean isMediaVideo = !post.findElements(By.cssSelector(".video-player")).isEmpty();
        String mediaUrl;
        if (isMediaVideo) {
            mediaUrl = post.findElement(By.tagName("source")).getAttribute("src");
        }
        else
            mediaUrl = post.findElement(By.cssSelector(".article-image:last-child")).getAttribute("src");


        log.warn("ID " + id + "\n" +
                    "Title " + title + "\n" +
                "PostURL " + postUrl + "\n" +
                "CreatedAt " + lds.toString() + "\n" +
                "MediaUrl " + mediaUrl + "\n"
        );
        return 0;
    }
}