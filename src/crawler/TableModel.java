package crawler;

import javax.swing.table.AbstractTableModel;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private final String[] columnNames = {"URL", "Title", "Depth"};
    private final LinkRepository linkRepository;
    private List<Link> data;

    public TableModel(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
        data = new ArrayList<>(linkRepository.getAll());
    }


    public synchronized void update() {
        data = new ArrayList<>(linkRepository.getAll());
        this.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return URL.class;
            case 2:
                return Integer.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int col) {
        Link link = data.get(row);
        switch (col) {
            case 0:
                return link.getUrl();
            case 1:
                return link.getTitle();
            case 2 :
                return link.getDepth();
            default:
                return null;
        }
    }
}
