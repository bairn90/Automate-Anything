package fileManagement;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class OpenSaveDialog {

	private JFileChooser chooser;

	public String getFileName() {
		chooser = new JFileChooser();
		
		int userSelection = chooser.showOpenDialog(null);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File filename = chooser.getSelectedFile();
		    return filename.getAbsolutePath();
		} else {
			return null;
		}
	}

	public String getFileNameFilters(String[] fileNames, String[] fileExt) {
		chooser = new JFileChooser();

		chooser.setFileFilter(new FileNameExtensionFilter(fileNames[0],fileExt[0]));
		for(int i=1;i<fileNames.length;i++) {
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(fileNames[i],fileExt[i]));
		}
	    
		int userSelection = chooser.showOpenDialog(null);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File filename = chooser.getSelectedFile();
		    return filename.getAbsolutePath();
		} else {
			return null;
		}
	}

	public String getFolderName() {
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int userSelection = chooser.showOpenDialog(null);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File filename = chooser.getSelectedFile();
		    return filename.getAbsolutePath();
		} else {
			return null;
		}
		
	}
	
	public String getSaveName() {
		
		chooser = new JFileChooser();
		chooser.setDialogTitle("Enter Filename");   
		 
		int userSelection = chooser.showSaveDialog(null);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File filename = chooser.getSelectedFile();
		    return filename.getAbsolutePath();
		} else {
			return null;
		}
		
	}
	
}

