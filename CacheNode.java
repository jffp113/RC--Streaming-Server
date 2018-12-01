import java.io.File;

public class CacheNode {
	private long lastAccessTime;
	private File file;
	
	private boolean toBeDeleted;
	private boolean removed;
	
	private int numberOfFileUsers;
	
	public CacheNode(File file) {
		updateTime();
		this.file = file;
	}
		
	private void updateTime() {
		lastAccessTime = System.currentTimeMillis();
	}
	
	public void use(){
		synchronized (file) {
			numberOfFileUsers++;
			updateTime();
		}
	}
	
	public File getFile() {
		return this.file;
	}
	
	private void remove() {
		if(toBeDeleted && numberOfFileUsers == 0) {
			removed = true;
			file.delete();
			System.out.printf("File Removed %s \n " ,file.getName());
		}
	}
	
	public void stopUsing(){
		synchronized (file) {
			numberOfFileUsers--;
			remove();
		}
	}
	
	public void setRemovelTag() {
		toBeDeleted = true;
		remove();
	}
	
	public boolean isObsolent() {
		return removed;
	}
	
	public boolean isOlder(CacheNode f) {
		return this.lastAccessTime < f.lastAccessedTime();
	}
	
	public long lastAccessedTime() {
		return this.lastAccessTime;
	}
}
