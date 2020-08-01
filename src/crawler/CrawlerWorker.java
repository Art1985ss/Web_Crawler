package crawler;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerWorker extends Thread {
    private Queue<Link> linksToProcess;
    private LinkRepository linkRepository;
    private LinkRepository checkedRepo;
    private Link link;
    private int maxDepth;

    @Override
    public void run() {
        checkedRepo.add(link);
        try {
            setText(link);
            getTitleFrom(link);
            getLinksToProcess(link);
            linkRepository.add(link);
        } catch (CrawlerException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setText(Link link) throws CrawlerException {
        try (InputStream inputStream = new BufferedInputStream(link.getUrl().openStream())) {
            String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            link.setText(text);
        } catch (IOException e) {
            throw new CrawlerException("Could not get text from url " + link.getUrl());
        }
    }

    private void getTitleFrom(Link link) throws CrawlerException {
        Pattern pattern = Pattern.compile("<title>.*</title>");
        Matcher matcher = pattern.matcher(link.getText());
        String title = null;
        if (matcher.find()) {
            title = matcher.group();
            title = title.replaceAll("<title>", "");
            title = title.replaceAll("</title>", "");
        }
        if (title == null) {
            throw new CrawlerException("Could not get title");
        }
        link.setTitle(title);
    }

    private void getLinksToProcess(Link link) {
        Pattern linkTagPattern = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1");
        Matcher matcher = linkTagPattern.matcher(link.getText());
        while (matcher.find() && !isInterrupted()) {
            String relative = matcher.group();
            relative = relative.replaceAll(".*=", "");
            relative = relative.replaceAll("['\"]", "");
            try {
                URL url = new URL(link.getUrl(), relative);
                Link newLink = new Link(url);
                newLink.setDepth(link.getDepth() + 1);
                if (!checkedRepo.contains(newLink) && newLink.getDepth() <= maxDepth && !linkRepository.contains(newLink)) {
                    linksToProcess.add(newLink);
                }
            } catch (MalformedURLException ignored) {
                System.out.println("Malformation for " + relative);
            } catch (CrawlerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public Builder builder() {
        return new CrawlerWorker().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Builder setLinksToProcess(Queue<Link> linksToProcess) {
            CrawlerWorker.this.linksToProcess = linksToProcess;
            return this;
        }

        public Builder setLinkRepository(LinkRepository linkRepository) {
            CrawlerWorker.this.linkRepository = linkRepository;
            return this;
        }

        public Builder setLink(Link link) {
            CrawlerWorker.this.link = link;
            return this;
        }

        public Builder setMaxDepth(int depth) {
            CrawlerWorker.this.maxDepth = depth;
            return this;
        }

        public Builder setCheckedRepo(LinkRepository checkedRepo) {
            CrawlerWorker.this.checkedRepo = checkedRepo;
            return this;
        }

        public CrawlerWorker build() {
            return CrawlerWorker.this;
        }
    }
}
