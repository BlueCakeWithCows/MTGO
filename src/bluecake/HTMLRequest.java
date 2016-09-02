package bluecake;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HTMLRequest implements Runnable{
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4444.");
			System.exit(1);
		}

		Socket clientSocket = null;
		try {
			while (true) {
				try {
					clientSocket = serverSocket.accept();
					(new Thread(new Client(clientSocket))).start();
				} catch (IOException e) {
					System.err.println("Accept failed.");
				}
			}
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private class Client implements Runnable {
		Socket clientSocket;

		public Client(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				out.writeObject(Main.planner.getSingleTradeAndMark());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
