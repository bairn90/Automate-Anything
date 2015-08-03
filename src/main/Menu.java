package main;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import clicks.ClickData;
import clicks.GetClicks;
import clicks.RunClicks;
import clicks.UpdateClicks;
import fileManagement.LoadClicks;

public class Menu extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton newSeq, load;
	private JTextArea description;
	private GridBagConstraints gbc;
	/**
	 * Create the frame.
	 */
	public Menu() {
		
		contentPane = new JPanel(new GridBagLayout());
		getContentPane().add(contentPane);
		
		description = new JTextArea("Welcome to Automate Anything! \n\nThis program will record a series of clicks as you carry a task and then allow you to repeat that task as many times as you want and input any data you need");
		//description.setText();
		description.setSize(600, 100);
		description.setBackground(UIManager.getColor("ColorChooser.background"));
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setEditable(false);
		description.setFont(new Font("Tahoma", Font.PLAIN, 14));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(description, gbc);
		
		newSeq = new JButton("Create a new sequence");
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(newSeq,gbc);
		
		load = new JButton("Load a sequence");
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(load,gbc);
		
		load.addActionListener(this);
		newSeq.addActionListener(this);
		
		setIconImage(new ImageIcon(this.getClass().getResource("/images/AA Logo.png")).getImage());
		setTitle("Main Menu");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 250);
		pack();

		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == newSeq) {
			this.dispose();
			
			UpdateClicks observer = new UpdateClicks();
			new GetClicks(observer);
			
		}
		
		if(e.getSource() == load) {
			
			boolean invalidInput = true;
			int dataNum = 0;
			LoadClicks loader = new LoadClicks();
			ClickData clickObj = loader.getClickObj();
			
			if(clickObj != null) {
				
				while(invalidInput) {
					try {
						String test = JOptionPane.showInputDialog(null, "How many times is this task to be repeated?");
						if(test == null) {
							return;
						} else {
							dataNum = Integer.parseInt(test);
							if(dataNum > 0){
								invalidInput = false;	
							}
						}
						
					} catch (Exception ex) {
						//empty catch block 
					}
				}
				
				this.dispose();
				new RunClicks(clickObj, dataNum, false, true);	
			}
			
		}
	}

}
