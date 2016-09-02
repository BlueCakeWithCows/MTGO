package bluecake.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import bluecake.Main;
import bluecake.Notifables;

public class RecentTradeTable extends JTable implements Notifables {
	private static final String[] COLUMN_NAMES = { "Card", "Age", "Remove" };
	protected DefaultTableModel model;

	public RecentTradeTable() {
		super();
		setup();
		setVisible(true);
	}

	private void setup() {
		model = (DefaultTableModel) this.getModel();
		this.setAutoCreateRowSorter(true);
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
					JButton b = new JButton("Remove");
					b.addActionListener(new ActionListener(){
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
}
