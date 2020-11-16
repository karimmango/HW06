import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import space.harbour.java.hw6.WebCrawler;

public class Tests {
    private WebCrawler crawler = new WebCrawler();

    @Before
    public void setUp() {
        this.crawler = new WebCrawler();
    }

    @Test
    public void alreadyVisitedSize() throws MalformedURLException {
        WebCrawler crawler = new WebCrawler();
        ExecutorService table = Executors.newFixedThreadPool(4);
        Future<CopyOnWriteArraySet> future = null;
        int numOfUrls = 0;

        try {
            crawler.toVisit.add(new URL("https://karimmango.github.io/HW06/site/index.html"));
            while (!crawler.toVisit.isEmpty()) {
                future = table.submit(new WebCrawler.UrlVisitor());
                System.out.println("get : " + future.get());
                numOfUrls = future.get().size();
            }
            table.shutdown();
            System.out.println("number of urls is : " + numOfUrls);
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        Assert.assertEquals(6, crawler.alreadyVisited.size());
        Assert.assertEquals(6, numOfUrls);
    }

    @Test
    public void emptyToVisit() {
        WebCrawler crawler = new WebCrawler();
        ExecutorService table = Executors.newFixedThreadPool(4);
        Future<CopyOnWriteArraySet> future = null;
        try {
            crawler.toVisit.add(new URL("https://karimmango.github.io/HW06/site/index.html"));
            while (!crawler.toVisit.isEmpty()) {
                future = table.submit(new WebCrawler.UrlVisitor());
                System.out.println("get : " + future.get());
            }
            table.shutdown();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        Assert.assertEquals(0, crawler.toVisit.size());
        Assert.assertTrue(crawler.toVisit.isEmpty());
    }

    @Test
    public void futuresTest() throws ExecutionException, InterruptedException {
        WebCrawler crawler = new WebCrawler();
        ExecutorService table = Executors.newFixedThreadPool(4);
        Future<CopyOnWriteArraySet> future = null;
        String futureSet = "[https://karimmango.github.io/HW06/site/index.html, https://karimmango.github.io/HW06/site/work.html,"
                +
                " https://karimmango.github.io/HW06/site/academics.html, https://karimmango.github.io/HW06/site/hobbies.html, "
                +
                "https://karimmango.github.io/HW06/site/personal.html, https://karimmango.github.io/HW06/site/sports.html]";
        String res = null;
        try {
            crawler.toVisit.add(new URL("https://karimmango.github.io/HW06/site/index.html"));
            while (!crawler.toVisit.isEmpty()) {
                future = table.submit(new WebCrawler.UrlVisitor());
                res = (future.get().toString());
            }
            table.shutdown();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        Assert.assertEquals(futureSet, res);
    }


}
