package bluecake.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import bluecake.Main;
import bluecake.Notifables;
import bluecake.misc.CompleteTrade;

public class FilteredTable extends Table implements Notifables{
	FilteredTable() {
		super();

 
	}

	public void update(List<CompleteTrade> trades) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				model.setRowCount(0);
				for (CompleteTrade t : trades) {
					model.addRow(Table.createRay(t));
				}
			}
		});
	}

	public JButton getUpdateButton() {
		FilteredJButton button = new FilteredJButton("Update", this);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				button.table.updateListing();
				
			}
		});
		return button;
	}

	protected void updateListing() {
		Main.planner.forceUpdate();
		this.update(Main.planner.getList());
	}

	@SuppressWarnings("serial")
	private class FilteredJButton extends JButton {

		public FilteredTable table;

		public FilteredJButton(String s, FilteredTable t) {
			super(s);
			this.table = t;
		}
	}
	private void init(){
		
	}

	@Override
	public void ping() {
		this.update(Main.planner.getList());
	}
}
