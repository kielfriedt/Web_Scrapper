import java.io.File;
import java.util.ArrayList;

public class FileExplorer {
	public static ArrayList<String> searchScratchFiles(String dir){
		 File root = new File(dir);
	     File[] filesOrDirs = root.listFiles();
	     ArrayList<String> result = new ArrayList<String>();
	     for (int i = 0; i < filesOrDirs.length; i++){
	    	 ArrayList<String> temp = null;
	    	 if (filesOrDirs[i].isDirectory()){
	    		 temp = searchScratchFiles(filesOrDirs[i].getAbsolutePath());
	    	 }
	    	 else if(filesOrDirs[i].getAbsolutePath().endsWith(".sb")){
	    		 result.add(filesOrDirs[i].getAbsolutePath());
	    	 }
	    	 if(temp != null && temp.size() != 0){
	    		 for(int j = 0; j < temp.size(); j++){
	    			 result.add(temp.get(j));
	    		 }
	    	 }
	     }
	     return result;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			// TODO Auto-generated method stub
			if(args.length <= 0){
				System.err.println("Please enter filename or directory path");
				System.exit(1);
			}
			
			String code_dir = "/bin";
			String current_file = "";
			if(args[0].endsWith(".sb")){
				Parser p = new Parser(args[0], code_dir);
				p.init();
			}
			else{
				ArrayList<String> files = searchScratchFiles(args[0]);
				if(files != null && files.size() != 0){
					for(int j = 0; j < files.size(); j++){
						System.out.println(files.get(j));
						current_file = files.get(j);
						Parser p = new Parser(current_file, code_dir);
						p.init();
		   		    }
				}
			}
			Thread.sleep(10000);
			System.exit(0);
		}
		catch(Exception exp){
			System.out.println("exception: " + exp.getMessage());
			try{
				Thread.sleep(10000);
			}
			catch(InterruptedException exp2){
			
			}
			System.exit(1);
		}
		catch(OutOfMemoryError me){
			System.out.println("exception: " + me.getMessage());
			try{
				Thread.sleep(10000);
			}
			catch(InterruptedException exp2){
			
			}
			System.exit(1);
		}
		
	}
}
