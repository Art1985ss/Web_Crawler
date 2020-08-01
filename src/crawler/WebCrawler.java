package crawler;

import javax.swing.*;
import java.awt.*;

public class WebCrawler extends JFrame {

    public WebCrawler() {
        getContentPane().setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setTitle("Web Crawler");

        LinkRepository linkRepository = new LinkRepository();

        TableModel model = new TableModel(linkRepository);

        FileLoader fileLoaderPanel = new FileLoader(model, linkRepository);


        JTable table = new JTable(model);
        table.revalidate();
        table.setName("TitlesTable");
        table.setAutoCreateRowSorter(true);
        table.setEnabled(false);
        table.setVisible(true);

        ExportPanel exportPanel = new ExportPanel(linkRepository);
        exportPanel.setVisible(true);


        setBackground(Color.RED);


        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.BOTH;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        getContentPane().add(fileLoaderPanel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 2;
        getContentPane().add(new JScrollPane(table), c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        getContentPane().add(exportPanel, c);
        pack();
        setVisible(true);
    }
}