import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Scanner;

class ClientHandler extends Thread {
	private static final int PLAYBACKDELAY = 1 * 1000;
	private static final String SERVER_FILES = "./Files/";
	private static final String FORMAT = ".dat";
	
	private Socket s;
	private String contentServerURLPrefix;

	private void sendStream(String fileName, String ip, int port) throws Exception {
		byte[] buffer = new byte[65000];
		int size;
		long timeStamp = -1;
		boolean requested = false;
		DataInputStream dis = null;

		while (true) {
			try {
				dis = new DataInputStream(new FileInputStream(SERVER_FILES + fileName));
				break;
			} catch (FileNotFoundException e) {

				if (!requested) {
					new CacheTask(fileName, contentServerURLPrefix).start();
					Thread.sleep(PLAYBACKDELAY);
					requested = true;
				}
			}
		}

		long starttime = System.nanoTime();
		try (DatagramSocket ms = new DatagramSocket()) {
			System.out.println("Stream Started");
			while (true) {
				// System.out.println("here");

				size = dis.readShort();
				timeStamp = dis.readLong();
				dis.readFully(buffer, 0, size);

				while (starttime + timeStamp >= System.nanoTime())
					;

				ms.send(new DatagramPacket(buffer, size, InetAddress.getByName(ip), port));
			}

		} catch (IOException e) {
			System.out.println("Ended");
		}

	}

	private String[] parseRequest(String request) {
		String[] rq = new String[3];
		int lastIndex = request.lastIndexOf("/");
		int propsStart = request.indexOf("?");
		System.out.println(request);
		if (lastIndex == 0)
			rq[0] = null;
		else
			rq[0] = request.substring(1, lastIndex);

		rq[1] = request.substring(lastIndex + 1, propsStart);
		rq[2] = request.substring(propsStart + 1);
		return rq;
	}
	
	public static Properties parseHttpPostContents( String contents)
			throws IOException {
		Properties props = new Properties();
		Scanner scanner = new Scanner(contents).useDelimiter( "&");
		while( scanner.hasNext()) {
			Scanner inScanner = new Scanner( scanner.next()).useDelimiter( "=");
			String propName = URLDecoder.decode( inScanner.next(), "UTF-8");
			String propValue = "";
			try {
				propValue = URLDecoder.decode( inScanner.next(), "UTF-8");
			} catch( Exception e) {
				// do nothing
			}
			props.setProperty( propName, propValue);
		}
		return props;
	}

	private void handlePlayerRequest(Socket s) throws Exception {
		String[] header = null;
		String[] headerParsed = null;
		InputStream in = s.getInputStream();
		Properties props = null;
		
		header = Http.parseHttpRequest(Http.readLine(in));
		System.out.println(header[0]);
		System.out.println(header[1]);
		headerParsed = parseRequest(header[1]);

		props = parseHttpPostContents(headerParsed[2]);
		contentServerURLPrefix = headerParsed[0];

		System.out.println(headerParsed[0] + " " + headerParsed[1] + " " + headerParsed[2]);

		sendStream(headerParsed[1], props.getProperty("ip"), Integer.parseInt(props.getProperty("port")));
	}

	public ClientHandler(Socket s) {
		this.s = s;
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
