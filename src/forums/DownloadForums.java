package forums;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.*;
import java.util.Map;


public class DownloadForums {
	//static int adjMatrix[][] = new int[2000][2000];
	static Map<String, Integer> userMap;
	static String[] userNames;
	static SparseMatrix adjMatrix;
	static String topicLength[];
	static int topicLengthCounter;

	static int getNumPagesTopic(FileContents fc){
		String numPagesString = fc.getContents().substring(fc.getContents().indexOf("<p class=\"pagelink conl\">"));
		numPagesString = numPagesString.substring(0,numPagesString.indexOf("></p>"));
		//System.out.println(numPagesString);
		int lastStartMarker = numPagesString.lastIndexOf(">") + 1;
		int lastEndMarker = numPagesString.lastIndexOf("<");
		int numPages = Integer.parseInt(numPagesString.substring(lastStartMarker, lastEndMarker));
		return numPages;
	}

	static void singleForumData(int forumID){
		try {
			String forumURL = "http://scratch.mit.edu/forums/viewforum.php?id=" + forumID;
			FileContents fc = FileContents.getFileContentsFromURL(forumURL);
			FileWriter currForumLinks = new FileWriter ("threads/" + forumID + ".txt");
			int numPages = getNumPagesTopic(fc);
			System.out.println("Number of pages in forum : " + numPages);
			int currPage = 1;
			String finalTopicLinksData = " ", singleLinkData;
			while (currPage <= numPages){
				//System.out.println(numPages);
				String currPageUrl = forumURL + "&p=" + currPage ;
				fc = FileContents.getFileContentsFromURL(currPageUrl);
				String currPageData = fc.getContents();
				currPageData = currPageData.substring(currPageData.indexOf("<td class=\"tcl\">"), 
						currPageData.indexOf("</table>") + 8);
				//System.out.println(currPageData + "\n ------------- \n");
				while(currPageData.contains("<td class=\"tcl\">")){
					currPageData = currPageData.substring(currPageData.indexOf("<a href=\"viewtopic.php?id="));
					singleLinkData = currPageData.substring(0, currPageData.indexOf("\">"));
					currPageData = currPageData.substring(singleLinkData.length());
					currPageData = currPageData.substring(currPageData.indexOf("<td class=\"tcl\">") + "<td class=\"tcl\">".length());
					//System.out.println(singleLinkData);
					singleLinkData = "http://scratch.mit.edu/forums" + singleLinkData.replace("<a href=\"", "/");
					//System.out.println(singleLinkData);
					//finalTopicLinksData = finalTopicLinksData + "\n" + singleLinkData;
					currForumLinks.append(singleLinkData + "\n");
				}
				currPage++;

			}

			currForumLinks.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void singleTopicData(String topicURL){
		try{
			FileContents fc = FileContents.getFileContentsFromURL(topicURL);
			String topicID = topicURL.substring(topicURL.indexOf("=") + 1);
			//FileWriter currTopicUsers = new FileWriter ("topics/" + topicID + ".txt");
			int numPages = getNumPagesTopic(fc);
			//System.out.println("Number of pages in topic : " + numPages);
			int currPage = 1;
			String finalUserData = " ", singleUserData;
			int numUsersPerTopic = 0;
			Set <Integer> uniqUsers = new HashSet<Integer> ();
			while (currPage <= numPages){
				String currPageUrl = topicURL + "&p=" + currPage ;
				fc = FileContents.getFileContentsFromURL(currPageUrl);
				String currPageData = fc.getContents();
				currPageData = currPageData.substring(currPageData.indexOf("<a href=\"/users/"), 
						currPageData.indexOf("<div class=\"postlinksb\">"));
				//Topic Starter details
				currPageData = currPageData.substring(currPageData.indexOf("<a href=\"/users/") + "<a href=\"/users/".length());
				singleUserData = currPageData.substring(0, currPageData.indexOf("\">"));
				int usermapSize = userMap.size();
				if (!userMap.containsKey(singleUserData)){
					userNames[userMap.size()] = singleUserData;
					userMap.put(singleUserData, usermapSize);
				}
				int topicStarter = userMap.get(singleUserData);
				//Topic Starter details ends here
				while(currPageData.contains("<a href=\"/users/")){
					currPageData = currPageData.substring(currPageData.indexOf("<a href=\"/users/") + "<a href=\"/users/".length());
					singleUserData = currPageData.substring(0, currPageData.indexOf("\">"));
					usermapSize = userMap.size();
					if (!userMap.containsKey(singleUserData)){
						userNames[userMap.size()] = singleUserData;
						userMap.put(singleUserData, (userMap.size()));
					}
					int currUser = userMap.get(singleUserData);
					uniqUsers.add(currUser);
					/*  ** Old implementation
					adjMatrix[topicStarter][currUser]++;
					adjMatrix[currUser][topicStarter] = adjMatrix[topicStarter][currUser];
					 */
					int val = (int)adjMatrix.get(topicStarter, currUser);
					val++;
					adjMatrix.put(topicStarter, currUser, val);
					adjMatrix.put(currUser, topicStarter, val);
					numUsersPerTopic++;					
					//currTopicUsers.append(singleUserData + "\n");
				}
				currPage++;
			}
			topicLength[topicLengthCounter++] = (numUsersPerTopic + "," + uniqUsers.size());
			//currTopicUsers.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int allForums[] = {26,11,12,13,10,31,6,7,8,9};
		for (int FORUMID : allForums) {
			System.out.println(FORUMID);
			//Initializations
			userMap = new HashMap<String, Integer> ();
			userNames = new String[50000];
			adjMatrix = new SparseMatrix(25000);
			topicLength = new String[50000];
			topicLengthCounter = 0;
			//Initializations done
			
			//singleForumData(FORUMID);  // Pass forumID 

			try{
				FileReader topicURLs = new FileReader ("threads/" + FORUMID + ".txt");
				BufferedReader bufferTopicURLs = new BufferedReader(topicURLs);
				String currURL = "";
				int topicCounter = 0;
				while ((currURL = bufferTopicURLs.readLine()) != null){
					//System.out.println("Topic : " + currURL);
					singleTopicData(currURL);
					System.out.println("Current topic : " + (++topicCounter));
				}
				//Printing adjacency Matrix to csv file
				FileWriter adjMatCSV = new FileWriter("matrices/adjMatrix" + FORUMID + ".csv");
				for (int userid = 0; userid < userMap.size(); userid++){
					adjMatCSV.append(userNames[userid] + ",");
				}
				adjMatCSV.append("\n");
				for (int x = 0; x < userMap.size(); x++){
					System.out.println("Writing : " + x);
					for (int y = 0; y < userMap.size(); y++){
						// ***Old implementation 
						//adjMatCSV.append(adjMatrix[x][y] + ",");
						// ***Old implementation ENDS
						adjMatCSV.append(adjMatrix.get(x, y) + ",");
						//System.out.println("adjmatrix[x][y] : " + adjMatrix[x][y] + " " + x + " " + y);
					}
					adjMatCSV.append("\n");
				}
				adjMatCSV.close();
				FileWriter topicLengthFile = new FileWriter("matrices/topicLength" + FORUMID + ".csv");
				for (int i =0; i < topicLengthCounter; i++) {
					topicLengthFile.append(topicLength[i] + "\n");
				}
				topicLengthFile.close();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}

	}

}
