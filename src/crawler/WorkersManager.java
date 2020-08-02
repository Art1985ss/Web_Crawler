package crawler;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorkersManager extends Thread {
    private static final long MIN_PASSED_MILLIS = 1500;

    private final List<CrawlerWorker> workers = new ArrayList<>();
    private final Queue<Link> linksToProcess = new ConcurrentLinkedQueue<>();
    private final LinkRepository checkedRepo = new LinkRepository();

    private int depth = 1;
    private long timeLimit = 120000;
    private long startMillis;
    private long passedMillis;
    private int maxWorkers;

    private TableModel tableModel;
    private Link originalLink;
    private LinkRepository linkRepository;
    private JLabel timeLabel;
    private JLabel parsedLabel;
    private JToggleButton buttonToReset;

    @Override
    public void run() {
        scan();
    }

    private void scan() {
        startMillis = System.currentTimeMillis();
        updateData();
        linksToProcess.add(originalLink);
        do {
            Link link = linksToProcess.poll();
            assignWorker(link);
            updateData();
            checkWorkers(false);
        } while (linksToProcess.size() > 0 && !isInterrupted() || passedMillis < MIN_PASSED_MILLIS);
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

    @Override
    public boolean isInterrupted() {
        if (interrupted() || passedMillis >= timeLimit) {
            interrupt();
            return true;
        }
        return false;
    }

    private void assignWorker(Link link) {
        if (link == null) {
            return;
        }
        boolean notAssigned = true;
        while (notAssigned && !isInterrupted()) {
            if (workers.size() < maxWorkers) {
                CrawlerWorker crawlerWorker = new CrawlerWorker().builder()
                        .setLink(link)
                        .setLinkRepository(linkRepository)
                        .setLinksToProcess(linksToProcess)
                        .setCheckedRepo(checkedRepo)
                        .setMaxDepth(depth)
                        .build();
                crawlerWorker.start();
                workers.add(crawlerWorker);
                notAssigned = false;
            } else {
                checkWorkers(false);
            }
            updateData();
        }
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
        return new WorkersManager().new Builder();
    }

    public class Builder {

        private Builder() {

        }

        public Builder setDepth(int depth) {
            WorkersManager.this.depth = depth;
            return this;
        }

        public Builder setTimeLimit(long timeLimit) {
            WorkersManager.this.timeLimit = timeLimit;
            return this;
        }

        public Builder setTableModel(TableModel tableModel) {
            WorkersManager.this.tableModel = tableModel;
            return this;
        }

        public Builder setOriginalLink(Link originalLink) {
            WorkersManager.this.originalLink = originalLink;
            return this;
        }

        public Builder setLinkRepository(LinkRepository linkRepository) {
            WorkersManager.this.linkRepository = linkRepository;
            return this;
        }

        public Builder setTimerLabel(JLabel label) {
            WorkersManager.this.timeLabel = label;
            return this;
        }

        public Builder setMaxWorkers(int maxWorkers) {
            WorkersManager.this.maxWorkers = maxWorkers;
            return this;
        }

        public Builder setParsedLabel(JLabel parsedLabel) {
            WorkersManager.this.parsedLabel = parsedLabel;
            return this;
        }

        public Builder setButtonToReset(JToggleButton buttonToReset) {
            WorkersManager.this.buttonToReset = buttonToReset;
            return this;
        }

        public WorkersManager build() {
            return WorkersManager.this;
        }


    }
}
