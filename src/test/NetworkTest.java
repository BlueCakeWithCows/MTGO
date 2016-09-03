package test;

import java.io.ObjectInputStream;
import java.net.Socket;

import bluecake.misc.CompleteTrade;

public class NetworkTest {
	public static void main(String[] args){
		

		try {
		    Socket echoSocket = new Socket("localhost", 4444);
		    ObjectInputStream in =
		        new ObjectInputStream(
		            (echoSocket.getInputStream()));
		    
		    
		    Object ob1 = in.readObject();
		    if(ob1 instanceof CompleteTrade){
		    	System.out.println(((CompleteTrade)ob1).toString());
		    	System.out.println(((CompleteTrade) ob1).getBuyer());
		    }else{
		    	System.out.println("err: "+ ob1);
		    }
		    echoSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
}
