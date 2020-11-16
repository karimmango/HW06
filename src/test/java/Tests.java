import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import space.harbour.java.hw6.WebCrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;

public class Tests {
    private WebCrawler crawler = new WebCrawler();

    @Before
    public void setUp() {
        this.crawler = new WebCrawler();
    }

    @Test
    public void alreadyVisitedSize() throws MalformedURLException {
        WebCrawler crawler = new WebCrawler();
        ExecutorService table = Executors.newFixedThreadPool(5);
        Future<CopyOnWriteArraySet> future = null;
        int numOfUrls = 0;

        try {
            crawler.toVisit.add(new URL("https://vasart.github.io/supreme-potato/index.html"));
            while (!crawler.toVisit.isEmpty()) {
                future = table.submit(new WebCrawler.UrlVisitor());
                System.out.println("get : " + future.get());
                numOfUrls = future.get().size();
            }
            table.shutdown();
            System.out.println("number of urls is : " + numOfUrls);
        }
        catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        Assert.assertEquals(4, crawler.alreadyVisited.size());
        Assert.assertEquals(4, numOfUrls);
    }

    @Test
    public void emptyToVisit() {
        WebCrawler crawler = new WebCrawler();
        ExecutorService table = Executors.newFixedThreadPool(8);
        Future<CopyOnWriteArraySet> future = null;
        try
        {
            crawler.toVisit.add(new URL("https://vasart.github.io/supreme-potato/index.html"));
            while (!crawler.toVisit.isEmpty()) {
                future = table.submit(new WebCrawler.UrlVisitor());
                System.out.println("get : " + future.get());
            }
            table.shutdown();
        }
        catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        Assert.assertEquals(0, crawler.toVisit.size());
        Assert.assertTrue(crawler.toVisit.isEmpty());
    }

    @Test
    public void futuresTest() throws ExecutionException, InterruptedException {
        WebCrawler crawler = new WebCrawler();
        ExecutorService table = Executors.newFixedThreadPool(8);
        Future<CopyOnWriteArraySet> future = null;
        String futureSet = "[https://vasart.github.io/supreme-potato/index.html, https://vasart.github.io/supreme-potato/experience.html, " +
                "https://vasart.github.io/supreme-potato/education.html, https://vasart.github.io/supreme-potato/social.html]";
        String res = null;
        try {
            crawler.toVisit.add(new URL("https://vasart.github.io/supreme-potato/index.html"));
            while (!crawler.toVisit.isEmpty()) {
                future = table.submit(new WebCrawler.UrlVisitor());
                res = (future.get().toString());
            }
            table.shutdown();
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        Assert.assertEquals(futureSet, res);
    }


}
