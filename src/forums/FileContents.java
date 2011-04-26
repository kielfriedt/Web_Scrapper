package forums;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class FileContents {
	String fileName;
	String contents;
	File file;
	public FileContents(String filename, String contents, File f){
		this.fileName = filename;
		this.contents = contents;
		this.file = f;
	}

	public String getName(){
		return this.fileName;
	}

	public String getContents(){
		return this.contents;
	}

	public File getFile(){
		return this.file;
	}

	public static FileContents getFileContentsFromURL(String url) throws IOException{
		URL firstURL = new URL(url);
		String fileName = firstURL.getFile();
		fileName = fileName.replace("/forums/", "forums/"); //Can replace these 3 lines for future use
		fileName = fileName.replace("&", "_");
		fileName = fileName.replace("?", "_") + ".html";
		//System.out.println("URL File NAME : " + fileName);
		File f = new File(fileName);

		FileWriter opFile = new FileWriter (f);
		String contents = " ", currLine;
		BufferedReader buffer = new BufferedReader(new InputStreamReader(firstURL.openStream()));
		while ((currLine = buffer.readLine()) != null){
			//System.out.println(currLine);
			contents = contents + currLine;
			opFile.append(currLine);
		}
		opFile.close();
		FileContents fc = new FileContents(f.getName(), contents, f);
		return fc;
	}
}

