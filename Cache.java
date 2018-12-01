
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Cache {
	private  Map<String,CacheNode> files;
	
	private int cacheSize;
	
	public Cache(int size) {
		this.cacheSize = size;
		files = new Hashtable<String,CacheNode>();
		loadFiles();
	}
	
	private void cleanCache() {
		Iterator<String> it;
		int pos = -1;
		CacheNode n = null;
		String fileName = null;
		String tmp = null;
		
		Set<String> keys = files.keySet();
		
		if (cacheSize > files.size())
				return;
		
		it = keys.iterator();
		while(it.hasNext()) {
			tmp = it.next();
			n = files.get(tmp);
			if(fileName == null || n.isOlder(files.get(fileName))) {
				fileName = tmp;
			}
		}
		
		if(fileName != null) {
			System.out.println("Removed " + fileName);
			files.get(fileName).setRemovelTag();
			files.remove(fileName);
		}
	
	}

	private void loadFiles() {
		File dir = new File(Stream.SERVER_FILES);
		File[] directoryListing = dir.listFiles();
		
		for (File child : directoryListing) {
			files.put(child.getName(), new CacheNode(child));
		}
	}
	
	public synchronized CacheNode requestFile(String fileName,String contentServerURLPrefix) {
		CacheNode n = files.get(fileName);
		
		if(n != null)
			return n;
		
		cleanCache();
		
		(new CacheTask(fileName,contentServerURLPrefix)).start();
		
		n = new CacheNode(new File(Stream.SERVER_FILES + fileName));
		files.put(fileName,n);
		
		return n;
	}

	
	
}
