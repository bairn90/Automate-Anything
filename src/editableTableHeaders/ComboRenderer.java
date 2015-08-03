package editableTableHeaders;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ComboRenderer extends JComboBox<String> implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	public ComboRenderer(String[] items) {
      for (int i = 0; i < items.length; i++) {
        addItem(items[i]);
      }
    }

    public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus, int row,
        int column) {
      setSelectedItem(value);
      return this;
    }
}

