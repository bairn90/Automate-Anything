package clicks;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import main.Main;
import main.Menu;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetClicks extends JFrame implements Observer, ActionListener, MouseListener {

	private static final long serialVersionUID = 1L; //Added to prevent warning
	
	private JPanel contentPane;
	private JButton stopRecording, startRecording, submit, reset, deleteLast, menuReturn;
	private JLabel lblInstructions;
	private ClickListener listener;
	private UpdateClicks clickListener;
	private LinkedList<LinkedList<Integer>> clicks = new LinkedList<LinkedList<Integer>>();
	private GridBagConstraints gbc;
	
	public GetClicks(UpdateClicks t) {
		
		this.clickListener = t;
		t.addObserver(this);
		
		contentPane = new JPanel(new GridBagLayout());
		getContentPane().add(contentPane);
		
		lblInstructions = new JLabel("Click stop recording before moving the window if it gets in the way");
		lblInstructions.setFont(new Font("Tahoma", Font.BOLD, 14));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(lblInstructions, gbc);
		
		startRecording = new JButton("Start Recording");
		startRecording.setPreferredSize(new Dimension(155,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 0;
		gbc.gridy = 1;
		contentPane.add(startRecording, gbc);
		
		stopRecording = new JButton("Pause Recording");
		stopRecording.setPreferredSize(new Dimension(155,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 1;
		gbc.gridy = 1;
		stopRecording.setEnabled(false);
		contentPane.add(stopRecording, gbc);

		submit = new JButton("Submit Clicks");
		submit.setPreferredSize(new Dimension(155,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 2;
		gbc.gridy = 1;
		contentPane.add(submit, gbc);
		
		reset = new JButton("Reset Clicks");
		reset.setEnabled(false);
		reset.setPreferredSize(new Dimension(155,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 0;
		gbc.gridy = 2;
		contentPane.add(reset, gbc);
		
		deleteLast = new JButton("Delete Last Click");
		deleteLast.setEnabled(false);
		deleteLast.setPreferredSize(new Dimension(155,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 1;
		gbc.gridy = 2;
		contentPane.add(deleteLast, gbc);
		
		menuReturn = new JButton("Return to Main Menu");
		menuReturn.setPreferredSize(new Dimension(155,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 2;
		gbc.gridy = 2;
		contentPane.add(menuReturn, gbc);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		stopRecording.addActionListener(this);
		startRecording.addActionListener(this);
		submit.addActionListener(this);
		reset.addActionListener(this);
		deleteLast.addActionListener(this);
		menuReturn.addActionListener(this);
		addMouseListener(this);
		
		setIconImage(new ImageIcon(this.getClass().getResource("/images/AA Logo.png")).getImage());
		pack();
		setResizable(false);
		setVisible(true);
		setLocation(d.width-530,0);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		LinkedList<Integer> click = new LinkedList<Integer>();
		int x,y;
		x = clickListener.getX();
		y = clickListener.getY();
		
		click.add(x);
		click.add(y);
		clicks.add(click);
		
	}
	
	public void returnClicks() {
		for(LinkedList<Integer> x : clicks) {
			System.out.println("X = " + x.get(0));
			System.out.println("Y = " + x.get(1));
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == stopRecording) {
			if (clickListener.isActive()) {
				clicks.removeLast();
				clickListener.makeInactive();
				stopRecording.setText("Restart Recording");
			} else {
				clickListener.makeActive();
				stopRecording.setText("Pause Recording");
			}
		}
		
		if (e.getSource() == submit) {
			int dataNum = 0;
			boolean invalidInput = true;
			
			if (clickListener.isActive()) {
				clicks.removeLast();	
			}
			
			if(clicks.isEmpty()) {
				JOptionPane.showMessageDialog(this, "You have yet to record any clicks");
				if (clickListener.isActive()) {
					clicks.removeLast();	
				}
				return;
			} else {
				clickListener.makeInactive();
			}
			
			this.dispose();
			
			int testSeq = JOptionPane.showConfirmDialog(null,"Would you like to test the sequence first?","Testing",JOptionPane.YES_NO_OPTION);
			
			if(testSeq == JOptionPane.YES_OPTION) {
				ClickData clickObj = new ClickData();
				clickObj.setClick(clicks);
				new RunClicks(clickObj, 1, true, false);
				//returnClicks();
			} else {
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
				
				
				ClickData clickObj = new ClickData();
				clickObj.setClick(clicks);
				new RunClicks(clickObj, dataNum, false, false);
			}
			
			
			//returnClicks();
		}
		
		if (e.getSource() == startRecording) {
			String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			path = path.substring(1);
			path = path.replaceAll("%20", " ");
			path = path.replaceAll("/", "\\\\");
			
			/*try {
				Runtime.getRuntime().exec("wscript \"" + path + "\"vbScripts\\AltESC.vbs");
			} catch(IOException IOe) {
				System.out.println(IOe.getCause());
				System.exit(0);
			}*/
			
	        try {
	            GlobalScreen.registerNativeHook();
	            // Get the logger for "org.jnativehook" and set the level to warning.
	            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
	            logger.setLevel(Level.WARNING);

	        }
	        catch (NativeHookException ex) {
	            System.err.println("There was a problem registering the native hook.");
	            System.err.println(ex.getMessage());
	            System.exit(1);
	        }

	        listener = new ClickListener(clickListener);
	        // Add the appropriate listeners.
	        GlobalScreen.addNativeMouseListener(listener);
	        //GlobalScreen.addNativeMouseMotionListener(listener);
	        
	        clickListener.makeActive();
	        startRecording.setEnabled(false);
	        stopRecording.setEnabled(true);
	        reset.setEnabled(true);
	        deleteLast.setEnabled(true);
		}
		
		if(e.getSource() == reset) {
			
			int conf = JOptionPane.showConfirmDialog(null, "Clear all clicks recorded so far?", "Reset", JOptionPane.YES_NO_OPTION);
			if(conf == JOptionPane.YES_OPTION) {
				clicks.clear();	
			} else {
				clicks.removeLast();
				clicks.removeLast();
			}
			
		}
		
		if(e.getSource() == deleteLast) {
			
			if(clicks.size() > 0) {
				clicks.removeLast();
				clicks.removeLast();
			}
			
		}
		
		if(e.getSource() == menuReturn) {
			dispose();
			new Menu();
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
