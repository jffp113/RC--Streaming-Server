import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Stream {
	public static final int PLAYBACKDELAY = 1 * 1000;
	public static final String SERVER_FILES = "Files/";
	private String contentServerURLPrefix;
	private Cache cache;
	private CacheNode fileNode;
	
	public Stream(String contentServerURLPrefix,Cache cache) {
		this.contentServerURLPrefix = contentServerURLPrefix;
		this.cache = cache;
		this.fileNode = null;
	}
	
	
	private DataInputStream getFileFromWebServer(String fileName) throws Exception {
		DataInputStream dis = null;
		boolean requested = false;
		fileNode = cache.requestFile(fileName,contentServerURLPrefix);
				
		while(true) {
			try {
				dis = new DataInputStream(new FileInputStream(fileNode.getFile()));
				break;
			} catch (FileNotFoundException e) {
					Thread.sleep(PLAYBACKDELAY);
			}		
		}
		
		return dis;
	}
	
	public void streamFile(int port,String ip,String fileName) throws Exception {
		byte[] buffer = new byte[65000];
		int size;
		long timeStamp = -1;
		long starttime = System.nanoTime();
		long timeStamp0 = -1;
		long waitTime = 0;
		
		DataInputStream dis = getFileFromWebServer(fileName);
		
		
		try (DatagramSocket ms = new DatagramSocket()) {
			fileNode.use();
			System.out.println("Stream Started");
			while (true) {
				
				size = dis.readShort();
				timeStamp = dis.readLong();
				dis.readFully(buffer, 0, size);
				
				if(timeStamp0 == -1) {
					timeStamp0 = timeStamp; 
				}
				waitTime = (timeStamp - timeStamp0) - (System.nanoTime() - starttime);
				
				if(waitTime < 0) {
					waitTime = 0;
				}
				 
				Thread.sleep(waitTime/1000000);
				ms.send(new DatagramPacket(buffer, size, InetAddress.getByName(ip), port));
			}

		} catch (IOException e) {
			
		}
		finally{
			dis.close();
			fileNode.stopUsing();
		}
	}
	
}
