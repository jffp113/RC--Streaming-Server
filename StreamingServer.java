import java.net.ServerSocket;
import java.net.Socket;

public class StreamingServer {
   private static final int CACHE_SIZE = 4;
   private static int  port;
   //private static String  contentServerURLPrefix;

   
   public static void main(String[] args) throws Exception {
      Cache c = new Cache(CACHE_SIZE);
	   
	   switch(args.length) {
	   		case 1:
	   			port = Integer.parseInt(args[0]);
	   			break;
	   		default: System.err.println("usage: java   -cp .:vlcproxy.jar StreamingServer port");
	   				 System.exit(-1);
	   }
	  
      Socket s;
      proxy.VlcProxy.start( port );
      
      while(true) {
    	  s = null;
	      try(ServerSocket ss = new ServerSocket ( port );){
	    	 System.out.println("Waiting");
	         s = ss.accept();
	         System.out.println("Accepted");
	         (new ClientHandler(s,c)).start();
	      }
      }
   }
  

}
