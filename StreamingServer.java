import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class StreamingServer {
   private static int  port;
   //private static String  contentServerURLPrefix;

   
   public static void main(String[] args) throws Exception {
      
	   switch(args.length) {
	   		case 1:
	   			port = Integer.parseInt(args[0]);
	   			break;
	   		default: System.err.println("usage: java   -cp .:vlcproxy.jar StreamingServer port");
	   				 System.exit(-1);
	   }
	   
      Socket s;
      
      while(true) {
	      try(ServerSocket ss = new ServerSocket ( port );){
	    	 System.out.println("Waiting");
	         s = ss.accept();
	         System.out.println("Accepted");
	         (new ClientHandler(s)).start();
	      }
      }
   }
  

}
