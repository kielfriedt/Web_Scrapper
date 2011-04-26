package forums;
import java.io.*;
import java.util.regex.*;
/**
 *
 * @author aniket
 */
public class YearWise {

	final static File DIR = new File("forums");
	
	public static void main(String[] args) {
		try {
			FilenameFilter filter = new FilenameFilter() {

				public boolean accept(File file, String name) {
					return name.startsWith("viewtopic.php_id=") && name.endsWith("_p=1.html");
				}
			};
			FileWriter topics2006 = new FileWriter("year/2006.csv");
			FileWriter topics2007 = new FileWriter("year/2007.csv");
			FileWriter topics2008 = new FileWriter("year/2008.csv");
			FileWriter topics2009 = new FileWriter("year/2008.csv");
			FileWriter topics2010 = new FileWriter("year/2010.csv");
			FileWriter errorTopics = new FileWriter("year/errorTopics.csv");
			File[] allTopics = DIR.listFiles(filter);
			String regex = "(\\d{4})-(\\d{2})-(\\d{2})";
			Pattern p = Pattern.compile(regex);
			for (File currFile : allTopics) {
				char buffer[] = new char[(int) currFile.length()];
				FileReader reader = new FileReader(currFile);
				reader.read(buffer);
				String fileContents = new String(buffer);
				fileContents = fileContents.substring(fileContents.indexOf("<span><span class=\"conr\">#1"));
				Matcher match = p.matcher(fileContents);
				while (match.find()){
					String startDate = match.group();
					//String startDate = fileContents.substring(fileContents.indexOf("\\d{4}-\\d\\d-\\d\\d"));
					//System.out.println("File Name : " + currFile.getName() + "\nStart Date : " + startDate);
					String []dateParts = startDate.split("-");
					switch (Integer.parseInt(dateParts[0])) {
					case 2004:
					case 2005:
					case 2006:
						topics2006.append(currFile.getName() + "," + startDate + "\n");
						break;
					case 2007:
						topics2007.append(currFile.getName() + "," + startDate + "\n");
						break;
					case 2008:
						topics2008.append(currFile.getName() + "," + startDate + "\n");
						break;
					case 2009:
						topics2009.append(currFile.getName() + "," + startDate + "\n");
						break;
					case 2010:
						topics2010.append(currFile.getName() + "," + startDate + "\n");
						break;
					default:
						errorTopics.append(currFile.getName() + "," + startDate + "\n");
						break;
					}
					break;
				}
			}
			topics2006.close();
			topics2007.close();
			topics2008.close();
			topics2009.close();
			topics2010.close();
			errorTopics.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

