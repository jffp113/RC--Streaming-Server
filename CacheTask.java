import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * This class is a cache Task
 * It will request a  movie to a specified server 
 * @author Jorge Pereira 49771
 * @author Ana Josefa Matos 49938
 *
 */

class CacheTask extends Thread {
	public static final int CACHE_SIZE = 4;
	public static final int REQUEST_SIZE = 50000;
	private String fileName;
	private String contentServerURLPrefix;



	public void requestFileToServer(String server, String fileName) throws Exception {
		BufferedInputStream in = null;
		FileOutputStream out = null;
		
		try{
			in = new BufferedInputStream(new URL(server).openStream());
			out = new FileOutputStream(Stream.SERVER_FILES + fileName);
	
			System.out.println("Requesting File From Server");
			
			int bytesRead = -1;
			byte[] buffer = new byte[REQUEST_SIZE];
	
			while ((bytesRead = in.read(buffer, 0, REQUEST_SIZE)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			
		}catch(Exception e) {
			System.out.println("BAD REQUEST");
		}
		finally {
			if(in != null)
				in.close();
			if(out != null)
				out.close();
		}
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
		
		System.out.println("Finished C request");
	}
}