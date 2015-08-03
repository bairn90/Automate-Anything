package fileManagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.JOptionPane;

import clicks.ClickData;

public class SaveClicks {
	
	FileOutputStream file;
	ObjectOutputStream outStream;
	File checker;
	OpenSaveDialog saver;
	String filename;
	int conf = JOptionPane.NO_OPTION;
	
	public boolean SaveFile(ClickData clickObj) {
		
		saver = new OpenSaveDialog();
		filename = saver.getSaveName();
		
		if(filename == null) {
			return false;
		} else if(! filename.contains(".AutoAnything")) {
			filename += ".AutoAnything";
		}
				
		checker = new File(filename);
		while(checker.exists() && conf == JOptionPane.NO_OPTION) {
			conf = JOptionPane.showConfirmDialog(null, "Overwrite File?", "Overwrite File?", JOptionPane.YES_NO_OPTION);
			if(conf == JOptionPane.NO_OPTION) {
				saver = new OpenSaveDialog();
				filename = saver.getSaveName();
				
				if(filename == null) {
					return false;
				} else if(! filename.contains(".AutoAnything")) {
					filename += ".AutoAnything";
				}
			}
			checker = new File(filename);
		}
		
		try {
			
			file = new FileOutputStream(filename);
			outStream = new ObjectOutputStream(file);   
			outStream.writeObject(clickObj);
			outStream.close();	
			
		} catch(Exception ex) {
				   ex.printStackTrace();
		}
		
		return true;
	}

}
