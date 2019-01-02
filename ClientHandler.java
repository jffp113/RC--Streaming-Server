import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * This class handles client requests of the Streaming Server
 * @author Jorge Pereira 49771
 * @author Ana Josefa Matos 49938
 */

class ClientHandler extends Thread {
	
	private Socket s;
	private String contentServerURLPrefix;
	private Cache c;

	private void handlePlayerRequest(Socket s) throws Exception {
		String[] header = null;
		InputStream in = s.getInputStream();
		Stream stream = null;
		
		Map<String,String> props = null;
		
		header = Http.parseHttpRequest(Http.readLine(in));
		props = Http.parseQuery("resource", header[1]);
		
		contentServerURLPrefix = props.get("resource");

		stream = new Stream(contentServerURLPrefix,c);
		stream.streamFile(Integer.parseInt(props.get("port")) ,props.get("ip")
				, contentServerURLPrefix.substring(contentServerURLPrefix.lastIndexOf("/") + 1));
	}

	public ClientHandler(Socket s,Cache c) {
		this.s = s;
		this.c = c;
	}

	public void run() {
		try {
			handlePlayerRequest(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
