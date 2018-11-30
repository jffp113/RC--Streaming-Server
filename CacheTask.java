class CacheTask extends Thread{
	   private  Cache c;
	   private  String fileName;
	   private String contentServerURLPrefix;
	   public CacheTask(String fileName,String contentServerURLPrefix) {
		   c = new Cache();
		   this.fileName = fileName;
		   this.contentServerURLPrefix = contentServerURLPrefix;
	   }
	   
	   public void run() {
		   try {
			c.requestFileToServer(contentServerURLPrefix,fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		   
	   }
   }