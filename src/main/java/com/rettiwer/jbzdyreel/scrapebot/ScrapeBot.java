package com.rettiwer.jbzdyreel.scrapebot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ScrapeBot implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ScrapeBot.class);

    private WebDriver webDriver;

    @Override
    public void run() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);
        webDriver = new ChromeDriver(chromeOptions);

        try {
            scrapeSinglePage(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //scrapeWholePage();
        /*while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }



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

    public int scrapeSinglePage(int pageNumber) throws IOException {
       webDriver.get("https://jbzd.com.pl/str/" + pageNumber);

        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        String s = (String) js.executeScript(webDriver.findElement(By.tagName("script")).getText());

        log.warn(s);
       /* HtmlPage page = webClient.getPage("https://jbzd.com.pl/str/" + pageNumber);

        DomNodeList<DomNode> reels = page.querySelector("div#content-container > article");

        for (DomNode reel : reels) {
            //String content = element.toString();
            log.warn(reel.toString());
        }*/
        return 0;
    }

    public int scrapeReel(Element element) {
        //Element avatar = element.selectFirst(".article-avatar > img");
        log.warn(element.toString());
        return 0;
    }
}
