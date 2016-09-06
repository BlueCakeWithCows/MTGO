package bluecake.GUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import bluecake.Main;
import bluecake.Notifables;

public class RecentTradeTable extends JTable implements Notifables {
	private static final String[] COLUMN_NAMES = { "Card", "Age", "Remove" };
	protected DefaultTableModel model;

	public RecentTradeTable() {
		super(new DefaultTableModel(COLUMN_NAMES, 0));
		setup();
		setVisible(true);
	}

	private void setup() {
		model = (DefaultTableModel) this.getModel();
		this.setAutoCreateRowSorter(true);
		this.getColumn("Remove").setCellRenderer(new ButtonRenderer());
		this.getColumn("Remove").setCellEditor(new ButtonEditor(new JCheckBox()));
	}

	protected void updateListing(List<Object[]> list) {
		Main.planner.forceUpdate();
		update2(Main.planner.getRecentList());
	}

	public void update2(List<Object[]> trades) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				model.setRowCount(0);
				for (Object[] t : trades) {
					Vector<Object> vec = new Vector<Object>();
					vec.add(t[0]);
					vec.add(t[1]);
					JCheckBox b = new JCheckBox("Remove");
					b.addActionListener(new ActionListener() {
						int id = model.getRowCount();

						@Override
						public void actionPerformed(ActionEvent e) {
							Main.planner.unflag((String) model.getValueAt(id, 0));
						}
					});
					vec.add(b);
					model.addRow(vec);
				}
			}
		});
	}

	@Override
	public void ping() {
		updateListing(Main.planner.getRecentList());
	}

	class ButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;

		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	/**
	 * @version 1.0 11/09/98
	 */

}
