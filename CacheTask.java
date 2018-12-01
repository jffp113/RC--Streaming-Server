import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

class CacheTask extends Thread {
	public static final int CACHE_SIZE = 4;
	public static final int REQUEST_SIZE = 50000;
	private String fileName;
	private String contentServerURLPrefix;



	public void requestFileToServer(String server, String fileName) throws Exception {
		BufferedInputStream in = new BufferedInputStream(new URL(server + "/" + fileName).openStream());
		FileOutputStream out = new FileOutputStream(Stream.SERVER_FILES + fileName);

		System.out.println("Requesting File From Server");
		
		int bytesRead = -1;
		byte[] buffer = new byte[REQUEST_SIZE];

		while ((bytesRead = in.read(buffer, 0, REQUEST_SIZE)) != -1) {
			out.write(buffer, 0, bytesRead);
		}

		in.close();
		out.close();

	}

	public CacheTask(String fileName, String contentServerURLPrefix) {
		this.fileName = fileName;
		this.contentServerURLPrefix = contentServerURLPrefix;
	}

	public void run() {
		try {
			requestFileToServer(contentServerURLPrefix, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}