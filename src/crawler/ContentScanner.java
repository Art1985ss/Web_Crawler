package crawler;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ContentScanner extends Thread {
    private int depth = 1;
    private long timeLimit = 120000;
    private long startMillis;
    private long passedMillis;
    private int maxWorkers;
    private final Queue<Link> linksToProcess = new ConcurrentLinkedQueue<>();
    private TableModel tableModel;
    private Link originalLink;
    private LinkRepository linkRepository;
    private final LinkRepository checkedRepo = new LinkRepository();
    private JLabel timeLabel;
    private JLabel parsedLabel;
    private JToggleButton buttonToReset;
    private final List<CrawlerWorker> workers = new ArrayList<>();

    @Override
    public void run() {
        scan();
    }

    private void scan() {
        startMillis = System.currentTimeMillis();
        passedMillis = System.currentTimeMillis() - startMillis;
        showTime();
        tableModel.update();
        linksToProcess.add(originalLink);
        do {
            if (workers.size() < maxWorkers && !interrupted() && passedMillis < timeLimit) {
                Link link = linksToProcess.poll();
                if (link == null) {
                    break;
                }
                if (depth >= link.getDepth()) {
                    CrawlerWorker crawlerWorker = new CrawlerWorker().builder()
                            .setLink(link)
                            .setLinkRepository(linkRepository)
                            .setLinksToProcess(linksToProcess)
                            .setCheckedRepo(checkedRepo)
                            .setMaxDepth(depth)
                            .build();
                    crawlerWorker.start();
                    workers.add(crawlerWorker);
                    if (linksToProcess.size() < 2) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            updateData();
            checkWorkers(false);
        } while (linksToProcess.size() > 0 && !interrupted() && passedMillis < timeLimit);
        do {
            updateData();
        } while (checkWorkers(true));
        updateData();
        if (buttonToReset.isSelected()) {
            buttonToReset.setSelected(false);
        }
    }

    private synchronized void updateData() {
        passedMillis = System.currentTimeMillis() - startMillis;
        showTime();
        showParsed();
        tableModel.update();
    }

    private boolean checkWorkers(boolean interrupt) {
        boolean stillWorking = false;
        for (int i = 0; i < workers.size(); i++) {
            CrawlerWorker worker = workers.get(i);
            if (interrupt) {
                worker.interrupt();
            }
            if (worker.getState().equals(State.TERMINATED)) {
                workers.remove(worker);
            } else {
                stillWorking = true;
            }
        }
        return stillWorking;
    }

    private void showTime() {
        Date date = new Date(passedMillis);
        DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
        timeLabel.setText(formatter.format(date));
        timeLabel.repaint();
    }

    private void showParsed() {
        parsedLabel.setText(String.valueOf(linkRepository.size()));
    }


    public Builder builder() {
        return new ContentScanner().new Builder();
    }

    public class Builder {

        private Builder() {

        }

        public Builder setDepth(int depth) {
            ContentScanner.this.depth = depth;
            return this;
        }

        public Builder setTimeLimit(long timeLimit) {
            ContentScanner.this.timeLimit = timeLimit;
            return this;
        }

        public Builder setTableModel(TableModel tableModel) {
            ContentScanner.this.tableModel = tableModel;
            return this;
        }

        public Builder setOriginalLink(Link originalLink) {
            ContentScanner.this.originalLink = originalLink;
            return this;
        }

        public Builder setLinkRepository(LinkRepository linkRepository) {
            ContentScanner.this.linkRepository = linkRepository;
            return this;
        }

        public Builder setTimerLabel(JLabel label) {
            ContentScanner.this.timeLabel = label;
            return this;
        }

        public Builder setMaxWorkers(int maxWorkers) {
            ContentScanner.this.maxWorkers = maxWorkers;
            return this;
        }

        public Builder setParsedLabel(JLabel parsedLabel) {
            ContentScanner.this.parsedLabel = parsedLabel;
            return this;
        }

        public Builder setButtonToReset(JToggleButton buttonToReset) {
            ContentScanner.this.buttonToReset = buttonToReset;
            return this;
        }

        public ContentScanner build() {
            return ContentScanner.this;
        }


    }
}
