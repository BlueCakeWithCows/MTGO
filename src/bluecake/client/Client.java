package bluecake.client;

import bluecake.Planner;

public class Client implements Runnable {

	public static Client client;
	private Planner planner;

	private Client() {

	}

	@Override
	public void run() {

	}

	private void setPlanner(Planner p) {
		this.planner = p;
	}

	public static void init(Planner planner) {
		client = new Client();
		client.setPlanner(planner);
		
	}

	private void flagCard(String card){
		planner.flag(card);
	}
}
