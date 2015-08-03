package main;

import javax.swing.UIManager;

public class Main {
	
	public static void main(String[] args)  {
		
		try { 
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		new SplashScreen("/images/Splash2.gif",3500, "/images/AA Logo.png");
		
		new Menu();	
		
	}

}