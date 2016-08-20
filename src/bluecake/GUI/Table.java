package bluecake.GUI;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import bluecake.misc.CompleteTrade;
import bluecake.misc.TradeFilter;
import bluecake.misc.TradeInfo;

public class Table extends JTable {
	private static final String[] COLUMN_NAMES = { "Net", "Card Name", "Percent", "Buyer Price", "Seller Price",
			"Buyer", "Seller", "Age", "Buyer Age", "Seller Age", "Buyer Source", "Seller Source", "Complete Trade" };
	private TableColumnManager manager;
	private DefaultTableModel model;

	public Table() {
		super(new DefaultTableModel(COLUMN_NAMES, 0){
		            @Override
		            public Class getColumnClass(int column) {
		                switch (column) {
		                    case 0:
		                        return Float.class;
		                    case 1:
		                        return String.class;
		                    case 2:
		                        return Float.class;
		                    case 3:
		                    	return Float.class;
		                    case 4:
		                    	return Float.class;
		                    case 7:
		                    	return Float.class;
		                    case 9:
		                    	return Float.class;
		                    case 8:
		                    	return Float.class;
		                    default:
		                        return String.class;
		                }
		        };
		});
		setup();
		setVisible(true);

	}

	private void setup() {
		manager = new TableColumnManager(this);
		manager.hideColumn("Percent");
		manager.hideColumn("Buyer Age");
		manager.hideColumn("Seller Age");
		manager.hideColumn("Buyer Source");
		manager.hideColumn("Seller Source");
		manager.hideColumn("Complete Trade");

		model = (DefaultTableModel) this.getModel();
		this.setAutoCreateRowSorter(true);
	}

	private static Object[] createRay(CompleteTrade t) {
		return new Object[] { t.getNet(), t.getCardName(), t.getNetPercent(), t.getBuyerPrice(), t.getSellerPrice(),
				t.getBuyer(), t.getSeller(), t.getAge(), t.getBuyerAge(), t.getSellerAge(), t.getBuyerSource(),
				t.getSellerSource(), t };
	}

	private TradeFilter filter;

	public void setFilter(TradeFilter filter) {
		this.filter = filter;
	}

	public void tryAddNewTrade(TradeInfo info) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int cardID = containsCard(info.getCard());
				if (cardID != -1) {
					CompleteTrade tInfo = (CompleteTrade) model.getValueAt(cardID, 12);
					if (tInfo.tryAdd(info)) {
						updateRow(cardID, tInfo);
					}
				} else {
					CompleteTrade tInfo = new CompleteTrade(info.card);
					tInfo.tryAdd(info);
					model.addRow(createRay(tInfo));
				}

			}
		});
	}

	private void updateRow(int row, CompleteTrade tInfo) {
		Object[] obj = createRay(tInfo);
		for (int col = 0; col < COLUMN_NAMES.length; col++) {
			model.setValueAt(obj[col], row, col);
		}
	}

	private int containsCard(String s) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (((String) model.getValueAt(i, 1)).contentEquals(s))
				return i;
		}
		return -1;
	}
	
}
