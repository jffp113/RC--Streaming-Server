import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

class ClientHandler extends Thread {
	
	private Socket s;
	private String contentServerURLPrefix;
	private Cache c;

	private String[] parseRequest(String request) {
		String[] rq = new String[3];
		int lastIndex = request.lastIndexOf("/");
		int propsStart = request.indexOf("?");
		System.out.println(request);
		if (lastIndex == 0)
			rq[0] = null;
		else
			rq[0] = request.substring(0, lastIndex);

		rq[1] = request.substring(lastIndex + 1, propsStart);
		rq[2] = request.substring(propsStart + 1);
		return rq;
	}

	private void handlePlayerRequest(Socket s) throws Exception {
		String[] header = null;
		String[] headerParsed = null;
		InputStream in = s.getInputStream();
		Stream stream = null;
		
		Map<String,String> props = null;
		
		header = Http.parseHttpRequest(Http.readLine(in));
		
		headerParsed = parseRequest(header[1]);

		props = Http.parseQuery("_", header[1]);
		
		contentServerURLPrefix = headerParsed[0];

		System.out.println(headerParsed[0] + " " + headerParsed[1] + " " + headerParsed[2]);

		stream = new Stream(contentServerURLPrefix,c);
		stream.streamFile(Integer.parseInt(props.get("port")) ,props.get("ip"), headerParsed[1]);
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
