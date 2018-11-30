import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/** 
* HTTP interface to get the file Bytes
* @author Jorge Pereira 49771
* @author Ana Josefa Matos 49938
*/
public class HTTPRequest {
	public static final int BUFFER_SIZE = 10*1024*1024;
	
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	private URL url;
	private long fileSize;
	
	private int getPort() {
		return url.getPort() == -1 ? 80 : url.getPort();
	}
	
	private String getPath() {
		return url.getPath() == "" ? "/" : url.getPath();
	}
	
	private void initConnection() throws Exception {
		this.socket = new Socket( url.getHost(), getPort());
		this.out = socket.getOutputStream();
		this.in = socket.getInputStream();
		this.fileSize = 0;
	}
	
	private void closeConnection() throws Exception {
		this.socket.close();
	}
	
	private void getOptionsToMap(InputStream in,Map<String,String> map) throws Exception {
		String option, value;
		int splitValue = 0;
		String answerLine = Http.readLine(in);
		
		while ( !answerLine.equals("") ) {
			splitValue = answerLine.indexOf(":");
			option = answerLine.substring(0, splitValue);
			value = answerLine.substring(splitValue+1).trim();
			map.put(option, value);
			answerLine = Http.readLine(in);
		}
		
	}
	
	public String getFileName() {
		String path = getPath();
		return path.substring(path.lastIndexOf("/") + 1, path.length());
	}
	
	private byte[] getBytes(int length) throws Exception {
		byte[] buffer = new byte[length];
		int bytesRead = 0, bytesWritten = 0;
			
		while(bytesWritten != length) {
			bytesRead = in.read(buffer,bytesWritten,buffer.length - bytesWritten);
			bytesWritten += bytesRead;
		}
		
		return buffer;
	}
			
	private static String getHeader(int min, int max,String path, String host) {
		String request = String.format("GET %s HTTP/1.0\r\n"+
				"Host: %s\r\n"+
				"Range: bytes=%d-%d\r\n"+
				"User-Agent: X-RC2018\r\n\r\n", path, host, min, max);
		
		return request;
	}
	
	public HTTPRequest(URL url) throws Exception {
		this.url = url;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public byte[] getFileBytes(int min,int max) throws Exception {
		String request = null;
		String contentLenghtHeader = null; 
		int contentLenght = 0;
		Map<String,String> options = new HashMap<String,String>();
		
		try {
			initConnection();
			request = getHeader(min,max,getPath(),url.getHost());
			out.write(request.getBytes());
			
			Http.readLine(in);
			getOptionsToMap(in,options);
			
			contentLenghtHeader = options.get("Content-Range");
			contentLenght = Integer.parseInt(options.get("Content-Length"));
			
			if(contentLenghtHeader != null)
				fileSize = Http.parseRangeValues(contentLenghtHeader)[2];
			else
				fileSize = contentLenght;
			
				return getBytes(contentLenght);
		}
		finally {
			closeConnection();
		}
	}
		
}
