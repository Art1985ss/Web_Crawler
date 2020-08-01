package crawler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportPanel extends JPanel {
    private final JTextField textField;
    private final LinkRepository linkRepository;

    public ExportPanel(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;

        this.setPreferredSize(new Dimension(500, 50));
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        textField = new JTextField();
        textField.setName("ExportUrlTextField");
        textField.setPreferredSize(new Dimension(390, 20));
        textField.setVisible(true);

        JButton button = new JButton("Save");
        button.setName("ExportButton");
        button.setPreferredSize(new Dimension(100, 30));
        button.addActionListener(actionEvent -> save());

        this.add(textField);
        this.add(button);
    }

    private synchronized void save() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File file = new File(textField.getText());
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(linkRepository.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
