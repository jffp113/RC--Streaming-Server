import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

class CacheTask extends Thread {
	public static final int CACHE_SIZE = 4;
	public static final int REQUEST_SIZE = 50000;
	private String fileName;
	private String contentServerURLPrefix;

	private void removeOldestFile(File[] directoryListing) {
		File oldestFile = null;

		for (File child : directoryListing) {
			if (oldestFile == null || oldestFile.lastModified() > child.lastModified())
				oldestFile = child;
		}

		if (oldestFile != null) {
			oldestFile.delete();
		}
	}

	private void cleanCache() {
		File dir = new File(Stream.SERVER_FILES);
		File[] directoryListing = dir.listFiles();

		if (CACHE_SIZE <= directoryListing.length)
			removeOldestFile(directoryListing);

	}

	public void requestFileToServer(String server, String fileName) throws Exception {
		BufferedInputStream in = new BufferedInputStream(new URL(server + "/" + fileName).openStream());
		FileOutputStream out = new FileOutputStream(Stream.SERVER_FILES + fileName);

		cleanCache();

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