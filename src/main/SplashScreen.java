package main;

import java.awt.*;

import javax.swing.*;

public class SplashScreen {

    public SplashScreen(String Image, int SleepTime, String Icon) {
        JFrame frame = new JFrame();
        JLabel label = new JLabel(new ImageIcon(getClass().getResource(Image)));
        frame.getContentPane().add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        if(Icon != null) {
        	frame.setIconImage(new ImageIcon(this.getClass().getResource(Icon)).getImage());	
        }
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        
    	try {
			Thread.sleep(SleepTime);	
			frame.dispose();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}