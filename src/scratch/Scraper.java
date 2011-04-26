package scratch;
import java.io.*;
import java.util.*;
class commentsType{
	String username;
	String timestamp;
	String text;
	public void add(String username, String timestamp, String text){
		this.username = username;
		this.timestamp = timestamp;
		this.text = text;
	}
}
public class Scraper {
	final static File DATA_DIR = new File("html");
	final static Map<String, List<String>> metaDataMarkerMap = new HashMap <String, List<String>> ();
	final static Map<String, String> metaDataValueMap = new HashMap <String, String> ();
	final static Map<String, Map<String, String>> metaDataMap = new HashMap <String, Map<String, String>> ();
	final static Map<String, List<String>> tagsMap = new HashMap <String, List<String>> ();
	final static Map<String, List<commentsType>> commentsMap = new HashMap <String, List<commentsType>> ();
	
	static FileWriter tags = null;
	static FileWriter comments = null;
	
	public static String getMetaDataValue(File currFile, String metaDataType) throws IOException {
	    String currString = null;
	    int sloc = 0, eloc = 0, i =0;
	    char[] buffer = new char[(int)(currFile.length())];
	    FileReader reader = new FileReader(currFile);
	    reader.read(buffer);
	    reader.close();
	    currString = new String(buffer);
	    List<String> markerList = metaDataMarkerMap.get(metaDataType);
	    int numMarkers = markerList.size();
	    for (i = 0; i < numMarkers -1 ; i++){ //numMarkers -1 is used because last marker is the end marker.
	    	sloc = currString.indexOf(markerList.get(i));
	    	if (sloc == -1){
	    		return "0";
	    	}
	    	else 
	    		sloc = sloc + markerList.get(i).length();
	    	currString = currString.substring(sloc);
	    }
	    eloc = currString.indexOf(markerList.get(i));
	    if (eloc == -1){
	    	return "0";
	    }
	    currString = currString.substring(0, eloc);
	    
	    //Extra logic added for "download" due to lack of promising pattern.
	    if (metaDataType.equals("download")){
	    	eloc = currString.indexOf("download");
	    	if (eloc == -1){
		    	return "0";
		    }
	    	currString = currString.substring(0, eloc).trim();
	    	currString = currString.substring((currString.lastIndexOf(',') + 2));
	    }// End of "download"
	    
	    //Extra logic for "tags" since it has multiple values and this cannot be grouped
	    if (metaDataType.equals("tag")){
	    	int numTags = 0;
	    	List<String> tempTagsMap = new ArrayList <String> ();
	    	if (currString.indexOf("<span class=\"tag_size") == -1){
	    		tagsMap.put(currFile.getName(), null);
	    	//	tags.append(currFile.getName() + "#0\n");
	    		return "0";
	    	}
	   // 	tags.append(currFile.getName() + "#");
	    	while (currString.indexOf("<span class=\"tag_size") != -1){
	    		String subString = null;
	    		sloc = currString.indexOf("<a href=\"/tags/view/") + 20;
	    		if (sloc == -1){
	    //			tags.append("0");
	    			return "0";
	    		}
	    		subString = currString.substring(sloc);
	    		eloc = subString.indexOf("\">");
	    		if (eloc == -1){
	  //  			tags.append("0");
	    			return "0";
	    		}
	    		subString = subString.substring(0, eloc);
	    		currString = currString.substring(sloc + eloc + 23);
	    		numTags = numTags + 1;
	    		tempTagsMap.add(subString);
	    //		tags.append( subString + "#");
	    	}
	 //   	tags.append("\n");
	    	sloc = currString.indexOf("<a href=\"/tags/view/");
	    	tagsMap.put(currFile.getName(), tempTagsMap);
	    	return Integer.toString(numTags);
	    }//End of "tags"
	    
	    if (metaDataType.equals("comments")){ //Special logic for comments
	    	int numComments = 0;
	    	List<commentsType> tempCommentsList = new ArrayList <commentsType> ();
	    	if (currString.indexOf("<span class=\"flag_comment\">") == -1){
	    		commentsMap.put(currFile.getName(), null);
	    //		comments.append(currFile.getName() + "#0\n");
	    		return "0";
	    	}
	  //  	comments.append(currFile.getName() + "#");
	    	//while (currString.indexOf("<span class=\"flag_comment\">") != -1){
	    	while (currString.indexOf("<p>") != -1){
	    		String username = null, timestamp = null, text = null;
	    		commentsType currCommentsType = new commentsType();
	    		sloc = currString.indexOf("<a href=\"/users/") + 16;
	    		if (sloc == -1){
	    	//		comments.append("0");
	    			return "0";
	    		}
	    		username = currString.substring(sloc);
	    		eloc = username.indexOf("\">");
	    		if (eloc == -1){
	    	//		comments.append("0");
	    			return "0";
	    		}
	    		username = username.substring(0, eloc);
	    		currString = currString.substring(sloc + (2*eloc) + 6);
	    		numComments = numComments + 1;
	    	//	comments.append( username + "#");
	    		//sloc = currString.indexOf("a>") + 3;
	    		eloc = currString.indexOf(" ago");
	    		sloc = currString.substring(0,eloc).lastIndexOf(">") + 2;
	    		if (eloc == -1 || sloc == -1 ){
	    			System.out.print("Problemo..");
	    		}	    			
	    		timestamp = currString.substring(sloc, eloc).trim().replace("\t", "_").replace("\n", "").replace("\r", "");
	    		currString = currString.substring(eloc);
	    //		comments.append(timestamp + "#");
	    		sloc = currString.indexOf("<p>") + 4;
	    		eloc = currString.indexOf("</p>") - 1;
	    		text = currString.substring(sloc, eloc).trim();
	    		currString = currString.substring(eloc + 9);
	    //		comments.append(text + "\n#");
	    		currCommentsType.add(username, timestamp, text);
	    		tempCommentsList.add(currCommentsType);
	    	}
	   // 	comments.append("\n");
	    	sloc = currString.indexOf("<a href=\"/tags/view/");
	    	commentsMap.put(currFile.getName(),tempCommentsList);
	    	
	    	return Integer.toString(numComments);
	    	
	    }
		
		return currString;
	  }

	public static void initMetaData(){ // Represents markers representing respective tags..
		//title
		List<String> tempList = new ArrayList<String> ();
		tempList.add("<h2 id=\"project_title\">");
		tempList.add("</h2>");
		metaDataMarkerMap.put("title", tempList);
		//sprite
		tempList = new ArrayList<String> ();
		tempList.add("Download this project!");
		tempList.add("Download the ");
		tempList.add(" sprite");
		metaDataMarkerMap.put("sprite", tempList);
		//script
		tempList = new ArrayList<String> ();
		tempList.add("Download this project!");
		tempList.add(" and ");
		tempList.add(" script");
		metaDataMarkerMap.put("script", tempList);
		//view
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"pactivity\">");
		tempList.add("<h5>");
		tempList.add(" view");
		metaDataMarkerMap.put("view", tempList);
		//tagger
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"pactivity\">");
		tempList.add("<span id='taggers-count'>");
		tempList.add(" tagger");
		metaDataMarkerMap.put("tagger", tempList);
		//loveit 
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"pactivity\">");
		tempList.add("<span id='lovers-count'>");
		tempList.add("</span>");
		metaDataMarkerMap.put("loveit", tempList);
		//download
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"pactivity\">");
		tempList.add("<!-- ::: End lovers, taggers and favoriters ::: -->");
		metaDataMarkerMap.put("download", tempList);
		//gallery
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"pactivity\">");
		tempList.add("gallerylist'>");
		tempList.add(" galler");
		metaDataMarkerMap.put("gallery", tempList);
		//remix
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"pactivity\">");
		tempList.add("mods'>");
		tempList.add(" remix");
		metaDataMarkerMap.put("remix", tempList);
		//remix by people
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"pactivity\">");
		tempList.add("remix");
		tempList.add("  by ");
		tempList.add(" people");
		metaDataMarkerMap.put("remixpeople", tempList);
		//based on  
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"creation\">");
		tempList.add("Based on");
		tempList.add("'s <a href='");
		tempList.add("'>project");
		metaDataMarkerMap.put("basedon", tempList);
		//tag
		tempList = new ArrayList<String> ();
		tempList.add("<ul id=\"taglist\">");
		tempList.add("</ul>");
		metaDataMarkerMap.put("tag", tempList);
		//notes
		tempList = new ArrayList<String> ();
		tempList.add("<p id=\"pdesc\">");
		tempList.add("</p>");
		metaDataMarkerMap.put("notes", tempList);
		//comments
		tempList = new ArrayList<String> ();
		tempList.add("<p id=\"pdesc\">");
		tempList.add("</p>");
		//creationDate
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"creation\">");
		tempList.add("shared it ");
		tempList.add(" ago");
		metaDataMarkerMap.put("creationDate", tempList);
		//comments
		tempList = new ArrayList<String> ();
		tempList.add("<div id=\"comments\"");
		tempList.add("<div class=\"project_pagination_container\">");
		metaDataMarkerMap.put("comments", tempList);
		
	}

	public static void main(String[] args) {
		System.out.println("Initializing metadata");
		initMetaData();
		try{
			FileWriter MetaData = new FileWriter("MetaData.csv");
			FileWriter NotesData = new FileWriter("NotesData.csv");
			File[] files = DATA_DIR.listFiles(new FilenameFilter() {
			       public boolean accept(File file, String name) {
			         return name.endsWith(".html");
			       }
			    });
		    if (files == null || files.length == 0 || !files[0].exists()) 
		    	System.out.println("Filename pattern not found in mentioned directory");
		    else{
		    	System.out.println(Integer.toString(files.length) + " Files found..");
		    	tags = new FileWriter ("tags.csv");
		    	comments = new FileWriter ("comments.csv");
		    	int counter = 0;
		    	//MetaData.append("Filename,views,remix,downloads,remix by people,loveit,script,title,tags,num of taggers,based on,sprite,gallery"); //Header output to file
		    	for (File currFile : files){
		    		System.out.println("#" + (++counter) + " : " + currFile.getName());
		    		MetaData.append("\n" + currFile.getName() + ","); 		// output to file
		    		//NotesData.append("\n" + currFile.getName() + "#"); 		// output to Notes file
		    		for (String metaDataType : metaDataMarkerMap.keySet()){
		    			String metaDataValue = getMetaDataValue(currFile, metaDataType);
		    			//Extra logic for cases in which 1 is represented as one.. 
		    			if (metaDataValue.equalsIgnoreCase("one"))
		    				metaDataValue = "1";
		    		//	System.out.println(currFile.getName() + metaDataType + metaDataValue);
		    			if (metaDataType == "notes"){	//Ouput to Notes file
		    		//		NotesData.append(metaDataValue.replace("\n", "_").replace("#", "_").replace("\t", "_").replace("\r", "_") + "\n");
		    			}
		    			else{
		    				System.out.print(metaDataType + ",");
		    				MetaData.append( metaDataValue + ","); 					//output to file
		    			}
		    			metaDataValueMap.put(metaDataType, metaDataValue);
		    			metaDataMap.put(currFile.getName(), metaDataValueMap);
		    		}
		    		System.out.println();
		    	}
		    	MetaData.close();
		    	NotesData.close();
		    	tags.close();
		    	comments.close();
		    }
		    	
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

}