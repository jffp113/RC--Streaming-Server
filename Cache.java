import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

public class Cache {
	private final int REQUEST_SIZE = 50000;
	private int cacheSize;
	//private List<FileNode> files;
	
	
	/*private void writeToFile(String fileName, byte[] buffer, int start) throws Exception {
		File f = new File(fileName);	
			try(RandomAccessFile file = new RandomAccessFile ( f, "rw" )){
				file.seek(start);
				file.write(buffer);		
			}	
			f.
	}*/
	
	private void removeOldestFile() {
		  File dir = new File(Stream.SERVER_FILES);
		  File[] directoryListing = dir.listFiles();
		  File oldestFile = null;
		  
		    for (File child : directoryListing) {
		    		oldestFile = child;
		    }
		    
		    if(oldestFile != null) {
		    	oldestFile.deleteOnExit();
		    }
		    	    
	}
	
	public void requestFileToServer(String server,String fileName) throws Exception {
		long min = 0;
		long max = min + REQUEST_SIZE;

		long fileSize;
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
