package centralityanalysis;

import java.io.*;
import java.util.regex.*;

/**
 *
 * @author aniket
 */
public class YearWise {

    final static File DIR = new File("forums");
    static String convertToURL(String filename) {
    	String URL = "http://scratch.mit.edu/forums/";
    	URL = URL.concat(filename.split("_")[0] + "?" + filename.split("_")[1]);
    	return URL;
    }

    public static void main(String[] args) {
        try {
            FilenameFilter filter = new FilenameFilter() {

                public boolean accept(File file, String name) {
                    return name.startsWith("viewtopic.php_id=") && name.endsWith("_p=1.html");
                }
            };

            FileWriter topics2007_1 = new FileWriter("year/2007_1_cumu.csv");
            FileWriter topics2007_2 = new FileWriter("year/2007_2_cumu.csv");
            FileWriter topics2007_3 = new FileWriter("year/2007_3_cumu.csv");
            FileWriter topics2008_1 = new FileWriter("year/2008_1_cumu.csv");
            FileWriter topics2008_2 = new FileWriter("year/2008_2_cumu.csv");
            FileWriter topics2008_3 = new FileWriter("year/2008_3_cumu.csv");
            FileWriter topics2009_1 = new FileWriter("year/2009_1_cumu.csv");
            FileWriter topics2009_2 = new FileWriter("year/2009_2_cumu.csv");
            FileWriter topics2009_3 = new FileWriter("year/2009_3_cumu.csv");
           // FileWriter topics2010 = new FileWriter("year/2010_cumu.csv");
            FileWriter errorTopics = new FileWriter("year/errorTopics_cumu.csv");
            File[] allTopics = DIR.listFiles(filter);
            String regex = "(\\d{4})-(\\d{2})-(\\d{2})";
            Pattern p = Pattern.compile(regex);
            for (File currFile : allTopics) {
                char buffer[] = new char[(int) currFile.length()];
                FileReader reader = new FileReader(currFile);
                reader.read(buffer);
                reader.close();
                String fileContents = new String(buffer);
                fileContents = fileContents.substring(fileContents.indexOf("<span><span class=\"conr\">#1"));
                Matcher match = p.matcher(fileContents);
                while (match.find()) {
                    String startDate = match.group();
                    //String startDate = fileContents.substring(fileContents.indexOf("\\d{4}-\\d\\d-\\d\\d"));
                    //System.out.println("File Name : " + currFile.getName() + "\nStart Date : " + startDate);
                    System.out.println("File Name : " + convertToURL(currFile.getName()));
                    String[] dateParts = startDate.split("-");
                    switch (Integer.parseInt(dateParts[0])) {

                        case 2007:
                            if (Integer.parseInt(dateParts[1]) <= 6) {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                            //    topics2009_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_3.append(convertToURL(currFile.getName()) + "\n");
                           //     topics2008_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2007_3.append(convertToURL(currFile.getName()) + "\n");
                           //     topics2007_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2007_1.append(convertToURL(currFile.getName()) + "\n");
                            }
                            /*else if (Integer.parseInt(dateParts[1]) <= 8) {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_3.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2007_3.append(convertToURL(currFile.getName()) + "\n");
                                topics2007_2.append(convertToURL(currFile.getName()) + "\n");
                            }
                            */
                            else {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                            //    topics2009_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_3.append(convertToURL(currFile.getName()) + "\n");
                            //    topics2008_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2007_3.append(convertToURL(currFile.getName()) + "\n");
                            }
                            break;
                        case 2008:
                            if (Integer.parseInt(dateParts[1]) <= 6) {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                           //     topics2009_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_3.append(convertToURL(currFile.getName()) + "\n");
                           //     topics2008_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_1.append(convertToURL(currFile.getName()) + "\n");
                            }/*
                            else if (Integer.parseInt(dateParts[1]) <= 8) {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_3.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_2.append(convertToURL(currFile.getName()) + "\n");
                            }*/
                            
                            else {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                           //     topics2009_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_1.append(convertToURL(currFile.getName()) + "\n");
                                topics2008_3.append(convertToURL(currFile.getName()) + "\n");
                            }
                            break;
                        case 2009:
                            if (Integer.parseInt(dateParts[1]) <= 6) {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                              //  topics2009_2.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_1.append(convertToURL(currFile.getName()) + "\n");
                            }/*
                            else if (Integer.parseInt(dateParts[1]) <= 8) {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                                topics2009_2.append(convertToURL(currFile.getName()) + "\n");
                            }
                            */
                            else {
                                topics2009_3.append(convertToURL(currFile.getName()) + "\n");
                                //topics2010.append(convertToURL(currFile.getName()) + "\n");
                            }
                            break;
                        case 2010:
                            //topics2010.append(convertToURL(currFile.getName()) + "\n");
                            break;
                        default:
                            errorTopics.append(convertToURL(currFile.getName()) + "\n");
                            break;
                    }
                    break;
                }
            }
            topics2007_1.close();
            topics2008_1.close();
            topics2009_1.close();
            topics2007_2.close();
            topics2008_2.close();
            topics2009_2.close();
            topics2007_3.close();
            topics2008_3.close();
            topics2009_3.close();
          //  topics2010.close();
            errorTopics.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
