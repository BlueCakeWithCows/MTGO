package bluecake.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import bluecake.misc.Log;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class GUI extends JFrame {
	public static GUI gui;
	private JPanel contentPane;
	public Table table;
	private final static String DEFAULT = "General";
	private List<GUIHandle> guiHandles;

	private JTabbedPane tabbedPane_Settings, tabbedPane_Consoles;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		createGUI();
	}

	public static void createGUI() {
		if (gui != null)
			return;

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui = new GUI();
					gui.setVisible(true);
					gui.log(DEFAULT, "GUI Created");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private GUI() {
		guiHandles = new ArrayList<GUIHandle>();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 531);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Table", null, scrollPane, null);

		table = new Table();
		scrollPane.setViewportView(table);
		tabbedPane_Consoles = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Consoles", null, tabbedPane_Consoles, null);

		tabbedPane_Settings = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Settings", null, tabbedPane_Settings, null);

		GUIHandle g = createAndAddGuiHandle(DEFAULT);
		g.setLogAll(true);

	}

	public GUIHandle createAndAddGuiHandle(String key) {

		GUIHandle g = new GUIHandle(key, tabbedPane_Consoles, tabbedPane_Settings);
		g.load();
		guiHandles.add(g);
		return g;
	}

	public void log(String key, String text) {
		log(Log.INFO, key, text);

	}

	public void log(int level, String identifier, String message) {
		String levelString = getLevel(level);
		message = levelString + "[" + identifier + "]: " + message;
		for (GUIHandle g : guiHandles) {
			g.log(identifier, message);
		}
		System.out.println(message);
	}

	public static String getLevel(int i) {
		switch (i) {
		case Log.INFO:
			return "[INFO]";
		case Log.SEVERE:
			return "[SEVERE]";
		case Log.WARNING:
			return "[WARNING]";
		}
		return "[NAN]";
	}

}
