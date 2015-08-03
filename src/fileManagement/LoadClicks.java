package fileManagement;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import clicks.ClickData;

public class LoadClicks {

	OpenSaveDialog loader;
	String[] fileNames = {"Auto Anything File"};
	String[] fileExt = {"AutoAnything"};
	String filename = "";
	InputStream file, buffer;
	ObjectInput input;
	
	public ClickData getClickObj() {
				
		while(!filename.contains(".AutoAnything") ) {
			loader = new OpenSaveDialog();
			filename = loader.getFileNameFilters(fileNames, fileExt);
			if(filename == null){
				return null;
			}
		}
		
	    try {
	    	file = new FileInputStream(filename);
	  	    buffer = new BufferedInputStream(file);
	  	    input = new ObjectInputStream (buffer);
		    
	  	    return (ClickData)input.readObject();
	  	    
	    } catch(IOException ex){
	    	ex.printStackTrace();
	    } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

}
