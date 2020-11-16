package space.harbour.java.hw6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class WebCrawler {
    public static ConcurrentLinkedQueue<URL> toVisit = new ConcurrentLinkedQueue<>();
    public static CopyOnWriteArraySet<URL> alreadyVisited = new CopyOnWriteArraySet<>();

    public static class UrlVisitor implements Callable {
        public static String getContentOfWebPage(URL url) {
            final StringBuilder content = new StringBuilder();

            try (InputStream is = url.openConnection().getInputStream();
                 InputStreamReader in = new InputStreamReader(is, "UTF-8");
                 BufferedReader br = new BufferedReader(in);)
            {
                String inputLine;
                while ((inputLine = br.readLine()) != null)
                    content.append(inputLine);
            }
            catch (IOException e)
            {
                System.out.println("Failed to retrieve content of " + url.toString());
                e.printStackTrace();
            }

            return content.toString();
        }

        public static URL[] stringToUrls(String content) {
            String[] ls = content.split("\"", 0);
            int ct1 = 0;
            for (String word : ls) {
                if (word.contains("http")) {
                    ct1++;
                }
            }
            String[] ls2 = new String[ct1];
            int ct2 = 0;
            for (String word : ls) {
                if (word.contains("http") && (ct2 < ct1)) {
                    ls2[ct2] = word;
                    ct2++;
                }
            }
            URL[] urls = new URL[ct1];
            int ct = 0;
            for (String word : ls2) {
                try {
                    URL url22 = new URL(word);
                    urls[ct] = url22;
                    ct++;
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            return urls;
        }

        @Override
        public CopyOnWriteArraySet call() {
            synchronized (toVisit) {
                synchronized (alreadyVisited) {
                    AtomicReference<URL> url = new AtomicReference<>(toVisit.poll());
                    if (url != null) {
                        alreadyVisited.add(url.get());
                        System.out.println(Thread.currentThread().getName());
                        System.out.println("url to parse " + url);
                        System.out.println("already visited : " + alreadyVisited.toString());
                        String content = getContentOfWebPage(url.get());
                        if (content.isBlank() == false) {
                            URL[] urls = stringToUrls(content);
                            for (URL newUrl : urls) {
                                if (!alreadyVisited.contains(newUrl) && !toVisit.contains(newUrl)) {
                                    toVisit.add(newUrl);
                                    System.out.println("new url added to tovisit " + newUrl);
                                    System.out.println("to visit " + toVisit.toString());
                                }
                            }
                        }

                    }
                    return alreadyVisited;
                }
            }
        }
    }
    public static void main(String[] args) throws MalformedURLException, ExecutionException, InterruptedException
    {
        ExecutorService table = Executors.newFixedThreadPool(8);
        Future<CopyOnWriteArraySet> future = null;
        int numOfUrls = 0;
        toVisit.add(new URL("https://vasart.github.io/supreme-potato/index.html"));
        while (!toVisit.isEmpty()) {
            future = table.submit(new WebCrawler.UrlVisitor());
            System.out.println("get : " + future.get());
            numOfUrls = future.get().size();
        }
        table.shutdown();
        System.out.println("number of urls : " + numOfUrls);
    }
}


