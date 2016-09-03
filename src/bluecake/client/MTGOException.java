package bluecake.client;

@SuppressWarnings("serial")
public abstract class MTGOException extends Exception {

	
	public static class MTGOBotOfflineException extends MTGOException {

	}

	public static class MTGORipOffException extends MTGOException {

	}

	public static class MTGOTradeFailedException extends MTGOException {

	}

	public static class MTGOWrongScreenException extends MTGOException {

	}

	public static class MTGOConnectionFailedException extends MTGOException {

	}

	public static class MTGOCannotFindCardException extends MTGOException {

	}
}
