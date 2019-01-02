import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
/**
 * This class controls the streaming to the client
 * @author Jorge Pereira 49771
 * @author Ana Josefa Matos 49938
 *
 */
public class Stream {
	public static final int PLAYBACKDELAY = 1 * 1000;
	public static final String SERVER_FILES = "Files/";
	private static final int WAIT_TRIES = 100;
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
		fileNode = cache.requestFile(fileName,contentServerURLPrefix);
		int i = 0;
		
		while(i < WAIT_TRIES) {
			try {
				dis = new DataInputStream(new FileInputStream(fileNode.getFile()));
				return dis;
			} catch (FileNotFoundException e) {
					Thread.sleep(PLAYBACKDELAY);
					i++;
			}		
		}
		
		return null;
	}
	
	public void streamFile(int port,String ip,String fileName) throws Exception {
		byte[] buffer = new byte[65000];
		int size;
		long timeStamp = -1;
		long starttime = System.nanoTime();
		long timeStamp0 = -1;
		long waitTime = 0;
		
		DataInputStream dis = getFileFromWebServer(fileName);
		
		if(dis == null)
			return;
		int i = 0;
		try (DatagramSocket ms = new DatagramSocket()) {
			fileNode.use();
			System.out.println("Stream Started");
			while (true) {
				i++;
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
			System.out.println(i);
		}
	}
	
}
