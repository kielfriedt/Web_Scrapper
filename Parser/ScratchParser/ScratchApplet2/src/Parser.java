import java.io.FileInputStream;
import java.awt.image.*;
import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.security.MessageDigest;
import java.security.MessageDigestSpi;

import javax.swing.JApplet;

public class Parser extends JApplet
{
	private static boolean newscriptflag = false;
	String filename;
	String codebase;
	PrintStream fileout;
	LContext lc;
	File output;
    public Parser(String file_to_parse, String code_base_dir)
    {
    	codebase = code_base_dir;
    	filename = file_to_parse;
    	
    	try {
    		String temp = filename.substring(0, filename.length()-3);
    	    output = new File(temp + ".xml");
    	    fileout = new PrintStream(output);
    	} catch (Exception x) {
    	    //Some other sort of failure, such as permissions.
    	    System.err.format("createFile error: %s%n", x);
    	    System.exit(1);
    	}
    }
    public static void setSensorValue(int i, int j)
    {
        if(i < 0 || i > 15)
        {
            return;
        } else
        {
            PlayerPrims.sensorValues[i] = j;
            return;
        }
    }
    public static int getSensorValue(int i)
    {
        if(i < 0 || i > 15)
            return 0;
        else
            return PlayerPrims.sensorValues[i];
    }
    
    public void generateObject(Object o){
    	if(o instanceof Object[]){
    		processObjectArray((Object[])o);
    	}
    	else if (o instanceof String|| o instanceof Character || o instanceof Number || o instanceof Sprite){
    		if(!o.toString().trim().equals("")){
    			if(o.toString().trim().equals("<")){
    				//System.out.print("&lt;");
    				fileout.print("&lt;");
    			}
    			else if(o.toString().trim().equals("&")){
    				//System.out.print("&amp;");
    				fileout.print("&amp;");
    			}
    			else{
    				//System.out.print(o.toString());
    				fileout.print(o.toString());
    			}
    		}
    	}
    }
    public void processObjectArray(Object[] obja){
    	boolean flag = false;
    	if(obja.length == 1){
    		if(obja[0] instanceof Object[]){
    			processObjectArray((Object[])obja[0]);
    		}
    		else{
    			flag = true;
    			//System.out.println();
    			fileout.println();
    			//System.out.print("<block>");
    			fileout.print("<block>");
    			generateObject(obja[0]);
    			//System.out.println("</block>");
				fileout.println("</block>");
    		}
    	}
    	else{
    		if(obja[0] instanceof Object[]){
    			processObjectArray((Object[])obja[0]);
    		}
    		else{
    			
    			//System.out.println();
    			fileout.println();
    			//System.out.print("<block>");
    			if(obja[0] instanceof Number){
    				//fileout.print("NewScript");
    				if(newscriptflag){
    					fileout.println("</scratchScript>");
    				}
    				fileout.println("<scratchScript>");
    				newscriptflag = true;
    			}
    			else{
    				flag = true;
        			fileout.print("<block>");
    				generateObject(obja[0]);
    			}
    		}
    		for(int i = 1; i < obja.length; i++){
				if(obja[i] instanceof Object[]){
					processObjectArray((Object[])obja[i]);
				}
				else{
					//System.out.print("\t" + "<args>" + "\t");
					fileout.print("\t" + "<args>" + "\t");
					generateObject(obja[i]);
					//System.out.print("\t" + "</args>");
					fileout.print("\t" + "</args>");
				}
			}
			if(flag){
				
				//System.out.println();
				fileout.println();
				//System.out.println("</block>");
				fileout.println("</block>");
			}
    	}
    	
    }
    
    public void generateScript(Object o){
    	try{
    		if(o == null){
        		return;
        	}
        	if(o instanceof Object[]){
        		if(((Object[])o).length == 0) return;
        		processObjectArray((Object[])o);
        		if(newscriptflag){
        			fileout.println("</scratchScript>");
        		}
        	}
    	}
    	catch(Exception exp){
    		System.out.println(exp.getMessage());
    	}
    }

    public boolean checkScript(Object obj){
    	if(obj instanceof Object[]){
    		for(int i=0; i < ((Object[])obj).length; i++ ){
    			if( checkScript(((Object[])obj)[i])){
    				return true;
    			}
    		}
    		return false;
    	}
    	else{
    		if(obj.toString().startsWith("EventHatMorph") || obj.toString().startsWith("KeyEventHatMorph") 
    				|| obj.toString().startsWith("MouseClickEventHatMorph")  ){
    			return true;
    		}
    		return false;
    	}
    }
    /*
    Object[] myReadObj(ObjReader r, DataInputStream s) throws IOException
	{
	    int i = s.readUnsignedByte();
	    Object aobj[];
	    if(i < 99)
	    {
	        aobj = new Object[2];
	        aobj[0] = r.readFixedFormat(i);
	        aobj[1] = new Integer(i);
	    } else
	    {
	        int j = s.readUnsignedByte();
	        int k = s.readUnsignedByte();
	        aobj = new Object[3 + k];
	        aobj[0] = ((Object) (r.empty));
	        aobj[1] = new Integer(i);
	        aobj[2] = new Integer(j);
	        for(int l = 3; l < aobj.length; l++)
	            aobj[l] = r.readField();
	
	    }
	    return aobj;
	}
    */
    static String returnHex(byte[] inBytes){
        String hexString = "";
        for (int i=0; i < inBytes.length; i++) { //for loop ID:1
            hexString +=
            Integer.toString( ( inBytes[i] & 0xff ) + 0x100, 16).substring( 1 );
        }                                   // Belongs to for loop ID:1
        return hexString;
    }
    
    public void init()
    {
    	String s = codebase;
        String s1 = filename;
        try
        {
            Thread.sleep(50L);
        }
        catch(InterruptedException interruptedexception) { }
        lc = PlayerPrims.startup(s, s1, getContentPane(), true);
        LContext context = lc;
        FileInputStream fileinputstream;
        try
        {
            fileinputstream = new FileInputStream(s1);
            Object[][] objTable;
            ObjReader objreader = new ObjReader(fileinputstream);
            Hashtable h = objreader.readInfo();
            
            //objreader.readObjTable(context);
            //objTable = objreader.objTable;
            
            byte abyte0[] = new byte[4];
            DataInputStream is = objreader.s;
            is.read(abyte0);
            if(!"ObjS".equals(new String(abyte0)) || is.readByte() != 1)
                throw new IOException();
            is.read(abyte0);
            if(!"Stch".equals(new String(abyte0)) || is.readByte() != 1)
                throw new IOException();
            
            int num = is.readInt();
            objTable = new Object[num][];
            for(int j = 0; j < num; j++)
                objTable[j] = objreader.readObj();
            
            objreader.objTable = objTable;
            objreader.createSpritesAndWatchers(context);
            objreader.buildImagesAndSounds();
            objreader.fixSounds();
            objreader.resolveReferences();
            objreader.uncompressMedia();
            
            
            //System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            fileout.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            //System.out.println("<root>");
            fileout.println("<root>");
            fileout.println("<author>");
            fileout.println(h.get("author").toString());
            fileout.println("</author>");
            for(int i = 0; i < objTable.length; i++){
            	if(!((Object)objTable[i][0] instanceof Sprite)){
            		//System.out.println(objTable[i][0].toString());
            		if(((Object)objTable[i][0]) instanceof BufferedImage){
            			fileout.print("<scratchImage" + " ");
            			BufferedImage image = (BufferedImage)objTable[i][0];
            			DataBuffer imagebuffer = image.getRaster().getDataBuffer();
            			fileout.print("imageWidth=\"");
            			fileout.print(image.getWidth() + "\"" + " ");
            			fileout.print("imageHeight=\"");
            			fileout.print(image.getHeight() +"\"" + " "); 
            			
            			byte[] sbuffer = new byte[imagebuffer.getSize() * 4];
            			if(imagebuffer.getSize() > 0){
            				for(int k = 0; k < imagebuffer.getSize(); k++){
                				int elem = imagebuffer.getElem(k);
                				sbuffer[4 * k + 0] = (byte) (elem & 0x00FF);
                				sbuffer[4 * k + 1] = (byte) ((elem >> 8) & 0x000000FF);
                				sbuffer[4 * k + 2] = (byte) ((elem >> 16) & 0x000000FF);
                				sbuffer[4 * k + 3] = (byte) ((elem >> 24) & 0x000000FF);
                			}
            			}
            			fileout.print("imageHash=\"");
            			MessageDigest m = MessageDigest.getInstance("MD5");
            			m.reset();
            			fileout.print(returnHex(m.digest(sbuffer)) + "\"");
            			fileout.println(">");
            			
            			for(int k = 0; k < imagebuffer.getSize(); k++){
            				if(k == (imagebuffer.getSize()-1))
            					fileout.print(imagebuffer.getElem(k) + " ");
            				else
            					fileout.print(imagebuffer.getElem(k) + ", ");
            			}
            			
            			fileout.println("</scratchImage>");
            		}
            		
            		if(((Object)objTable[i][0]) instanceof ScratchSound){
            			if(i != 0 && ((Object)objTable[i-1][0]) instanceof String ){
            				fileout.print("<scratchSound" + " ");
                			ScratchSound sound = (ScratchSound)objTable[i][0];
                			fileout.print("soundRate=\"");
                			fileout.print(sound.rate + "\"" + " ");
                			
                			fileout.print("soundHash=\"");

                			MessageDigest m = MessageDigest.getInstance("MD5");
                			m.reset();
                			fileout.print(returnHex(m.digest(sound.samples)) + "\"" + " ");
                			
                			if(i != 0){
                				//get the name of the sound sample
                				String soundname =objTable[i-1][0].toString();
                				fileout.print("soundName=\"");
                    			fileout.print(soundname + "\"" + " ");
                			}
                			
                			fileout.println(">");
                			
                			for(int k = 0; k < sound.samples.length; k++){
                				if(k == sound.samples.length -1)
                					fileout.print(sound.samples[k] + " ");
                				else
                					fileout.print(sound.samples[k] + ", ");
                			}
                			
                			fileout.println("</scratchSound>");
            			}
            			
            		}
    				continue;
    			}
            	//System.out.println("<scratchSprite>");
            	fileout.println("<scratchSprite>");
            	newscriptflag = false;
            	//search for script
            	for(int j = 0; j < objTable[i].length; j++){
            			if(checkScript(objTable[i][j])){
            				//System.out.println("<scratchScript>");
            				//fileout.println("<scratchScript>");
            				generateScript(objTable[i][j]);
            				//System.out.println("</scratchScript>");
            				//fileout.println("</scratchScript>");
            			}
            	}
            	//System.out.println("</scratchSprite>");
            	fileout.println("</scratchSprite>");
            }
            //System.out.println("</root>");
            fileout.println("</root>");
            fileinputstream.close();
            fileout.flush();
            fileout.close();
            this.destroy();
            return;
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            Logo.error("File not found:", lc);
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        catch(Exception exp){
        	
        }
    }

    public void destroy()
    {
        PlayerPrims.shutdown(lc);
    }
}

