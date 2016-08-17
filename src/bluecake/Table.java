package bluecake;

import java.awt.EventQueue;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;

import javax.swing.DefaultRowSorter;
import javax.swing.JCheckBox;
import javax.swing.JSplitPane;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Table extends JFrame {

	private JPanel contentPane;
	private JTable table;

	public static Planner planner;

	private DefaultTableModel model = new DefaultTableModel(
			new Object[] { "Card", "Net", "Buyer", "Seller", "Buyer Price", "Seller Price", "Age" }, 0);

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */

	public JTextArea hotListConsole, priceWikiConsole, allConsole;

	public Table() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 682, 572);
		contentPane.add(tabbedPane);

		JScrollPane scrollPane = new JScrollPane();

		tabbedPane.addTab("Table", null, scrollPane, null);

		table = new JTable(model);
		https: // www.mtgowikiprice.com/card/DMG/19
		scrollPane.setViewportView(table);

		DefaultRowSorter rowSorter = new TableRowSorter(model);
		rowSorter.setComparator(1, new FullTrade.TradeComparator());
		table.setRowSorter(rowSorter);

		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Consoles", null, tabbedPane_1, null);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane_1.addTab("All", null, scrollPane_1, null);

		allConsole = new JTextArea();
		scrollPane_1.setViewportView(allConsole);

		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPane_1.addTab("Price Wiki", null, scrollPane_2, null);

		priceWikiConsole = new JTextArea();
		scrollPane_2.setViewportView(priceWikiConsole);

		JScrollPane scrollPane_3 = new JScrollPane();
		tabbedPane_1.addTab("Hotlist", null, scrollPane_3, null);

		hotListConsole = new JTextArea();
		scrollPane_3.setViewportView(hotListConsole);

		JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
		chckbxNewCheckBox.setBounds(28, 581, 113, 25);
		contentPane.add(chckbxNewCheckBox);

		JCheckBox checkBox = new JCheckBox("Wikiprice Seller");
		checkBox.setBounds(28, 619, 113, 25);
		contentPane.add(checkBox);

		JCheckBox checkBox_1 = new JCheckBox("Hotlist Buyer");
		checkBox_1.setBounds(145, 581, 113, 25);
		contentPane.add(checkBox_1);

		JCheckBox checkBox_2 = new JCheckBox("Wikiprice Buyer");
		checkBox_2.setBounds(145, 619, 113, 25);
		contentPane.add(checkBox_2);
		contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] { tabbedPane }));
	}

	public synchronized void addCard(HalfTrade deal) {
		if (!validate(deal))
			return;
		boolean hadRow = false;
		for (int i = 0; i < table.getRowCount(); i++) {
			if(!(table.getValueAt(i, 0) instanceof String)){
				
			}else
			if (((String) table.getValueAt(i, 0)).equalsIgnoreCase(deal.card)) {
				FullTrade trade = (FullTrade) table.getValueAt(i, 1);
				trade.improve(deal);
				Vector<Object> vals = trade.getRow();
				for (int i2 = 0; i2 < vals.size(); i2++) {
					table.setValueAt(vals.get(i2), i, i2);
				}
				hadRow = true;
				break;

			}
		}
		if (!hadRow) {
			FullTrade trade = new FullTrade(deal.card);
			trade.improve(deal);
			model.addRow(trade.getRow());
		}
		
		model.fireTableDataChanged();

	}

	private boolean validate(HalfTrade deal) {
		return true;
	}

}
