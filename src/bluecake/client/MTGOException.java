package bluecake.client;

@SuppressWarnings("serial")
public abstract class MTGOException extends Exception {
	public class MTGOBotOfflineException extends MTGOException {

	}

	public class MTGORipOffException extends MTGOException {

	}

	public class MTGOTradeFailedException extends MTGOException {

	}

	public class MTGOWrongScreenException extends MTGOException {

	}

	public class MTGOConnectionFailedException extends MTGOException {

	}

	public class MTGOCannotFindCardException extends MTGOException {

	}
}
