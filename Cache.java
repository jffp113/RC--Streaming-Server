import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

public class Cache {
	public static final int CACHE_SIZE = 4;
	public static final int REQUEST_SIZE = 50000;
	
	private void writeToFile(String fileName, byte[] buffer, int start) throws Exception {
		File f = new File(fileName);	
			try(RandomAccessFile file = new RandomAccessFile ( f, "rw" )){
				file.seek(start);
				file.write(buffer);		
			}	
	}
	
	private void removeOldestFile(File[] directoryListing) {
		  File oldestFile = null;
		  
		    for (File child : directoryListing) {
		    		if(oldestFile == null || oldestFile.lastModified() > child.lastModified())
		    			oldestFile = child;
		    } 
		    if(oldestFile != null) {
		    	oldestFile.deleteOnExit();
		    }
	}
	
	private void cleanCache() {
		File dir = new File(Stream.SERVER_FILES);
		File[] directoryListing = dir.listFiles();
		
		if(CACHE_SIZE == directoryListing.length)
			removeOldestFile(directoryListing);
		
	}
	
	public void requestFileToServer(String server,String fileName) throws Exception {
		long min = 0;
		long max = min + REQUEST_SIZE;
		long fileSize;
		
		cleanCache();
		
		HTTPRequest rq = new HTTPRequest(new URL(server + "/" + fileName));
		
		rq.getFileBytes(0, 1);
		fileSize = rq.getFileSize();
		
		if(max + REQUEST_SIZE > fileSize ) {
			max = fileSize - 1;
		}
			min = 0;
		
		while(max < fileSize) {
			
			writeToFile("./Files/"+fileName,rq.getFileBytes((int)min, (int)max),(int)min);
			
			min = max + 1;
			
			if(max + REQUEST_SIZE <= fileSize )
				max += REQUEST_SIZE;
			else
				max = fileSize - 1;
							
			if(min >= max) {
				break;
			}
			
		}
	}

}
