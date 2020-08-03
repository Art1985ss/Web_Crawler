package crawler;


import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

public class FileLoader extends JPanel {
    private final JTextField urlInput;
    private final JToggleButton button;
    private final JTextField workersInput;
    private final JTextField depthInput;
    private final JCheckBox depthCheckBox;
    private final JTextField timeLimitInput;
    private final JCheckBox timeLimitCheckBox;
    private final JLabel time;
    private final JLabel parsedLabel;
    private final TableModel model;
    private final LinkRepository linkRepository;
    private WorkersManager workersManager;


    public FileLoader(TableModel model, LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
        this.model = model;
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel urlLabel = new JLabel("Start URL:");
        urlLabel.setVisible(true);

        urlInput = new JTextField();
        urlInput.setName("UrlTextField");


        button = new JToggleButton("Run");
        button.setName("RunButton");
        button.addActionListener(actionEvent -> {
            if (button.isSelected()) {
                start();
            } else {
                stop();
            }
        });
        button.setVisible(true);

        JLabel workersLabel = new JLabel("Workers:");
        workersLabel.setVisible(true);

        workersInput = new JTextField();
        workersInput.setName("WorkersTextField");
        workersInput.setText("40");
        workersInput.setVisible(true);

        JLabel depthLabel = new JLabel("Maximum depth:");
        depthLabel.setVisible(true);

        depthInput = new JTextField("4");
        depthInput.setName("DepthTextField");
        depthInput.setVisible(true);

        depthCheckBox = new JCheckBox("Enabled");
        depthCheckBox.setName("DepthCheckBox");
        depthCheckBox.setSelected(true);
        depthCheckBox.setVisible(true);

        JLabel timeLimitLabel = new JLabel("Time limit:");
        timeLimitLabel.setVisible(true);

        timeLimitInput = new JTextField("60");
        timeLimitInput.setVisible(true);

        JLabel seconds = new JLabel("seconds");
        seconds.setVisible(true);

        timeLimitCheckBox = new JCheckBox("Enabled");
        timeLimitCheckBox.setSelected(true);
        timeLimitCheckBox.setVisible(true);

        JLabel elapsedTime = new JLabel("Elapsed time:");
        elapsedTime.setVisible(true);

        time = new JLabel("0:00:000");
        time.setVisible(true);

        JLabel forParsedLabel = new JLabel("Parsed pages:");
        forParsedLabel.setVisible(true);

        parsedLabel = new JLabel("0");
        parsedLabel.setName("ParsedLabel");
        parsedLabel.setVisible(true);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(3, 3, 3, 3);

        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        add(urlLabel, constraints);

        constraints.weightx = 0.8;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 6;
        add(urlInput, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 7;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        add(button, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        add(workersLabel, constraints);

        constraints.weightx = 0.9;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 8;
        add(workersInput, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        add(depthLabel, constraints);

        constraints.weightx = 0.8;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 6;
        add(depthInput, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 7;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        add(depthCheckBox, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        add(timeLimitLabel, constraints);

        constraints.weightx = 0.7;
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 5;
        add(timeLimitInput, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 6;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        add(seconds, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 7;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        add(timeLimitCheckBox, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        add(elapsedTime, constraints);

        constraints.weightx = 0.9;
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 8;
        add(time, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        add(forParsedLabel, constraints);

        constraints.weightx = 0.9;
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 8;
        add(parsedLabel, constraints);


    }

    private void start() throws CrawlerException {
        //linkRepository.clear();
        int depth = Integer.MAX_VALUE;
        if (depthCheckBox.isSelected()) {
            depth = Integer.parseInt(depthInput.getText());
        }
        long timeLimit = Long.MAX_VALUE;
        if (timeLimitCheckBox.isSelected()) {
            timeLimit = Integer.parseInt(timeLimitInput.getText()) * 1000;
        }
        int workers = Integer.parseInt(workersInput.getText());
        Link link;
        try {
            link = new Link(new URL(urlInput.getText()));
            link.setDepth(0);
        } catch (MalformedURLException ignored) {
            throw new CrawlerException("Input link is not valid");
        }
        if (workersManager != null) {
            workersManager.interrupt();
        }
        workersManager = new WorkersManager().builder()
                .setDepth(depth)
                .setOriginalLink(link)
                .setLinkRepository(linkRepository)
                .setTableModel(model)
                .setTimeLimit(timeLimit)
                .setTimerLabel(time)
                .setParsedLabel(parsedLabel)
                .setButtonToReset(button)
                .setMaxWorkers(workers)
                .build();
        workersManager.start();
    }


    private void stop() {
        if (workersManager != null) {
            workersManager.interrupt();
        }
        workersManager = null;
    }


}
