package clicks;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import main.Menu;
import pasteFromExcel.ExcelAdapter;
import editableTableHeaders.ComboRenderer;
import editableTableHeaders.EditableHeader;
import editableTableHeaders.EditableHeaderTableColumn;
import fileManagement.SaveClicks;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class RunClicks extends JFrame implements ActionListener, TableColumnModelListener, TableModelListener, Observer {

	private static final long serialVersionUID = 1L; //Added to prevent warning
	private JPanel panel;
	private LinkedList<LinkedList<Integer>> clicks = new LinkedList<LinkedList<Integer>>();
	private LinkedList<Integer> cActions, cDelay;
	private JTable dataTable, delayTable;
	private JScrollPane dataScroll, delayScroll;
	private JButton submit, save, menu, rerun, stepThrough;
	private JComboBox<String>[] actions, delay;
	private int dataNum, width, height, noOfClicks;
	private JTextArea description;
	private boolean testing, loaded;
	private GridBagConstraints gbc;
	private ClickData clicksData;
	private int nextClick = 0;
	private boolean active = false, escPress = false;
	private TableColumnModel dataColModel, delayColModel;
	private EscListener listener;
	
	/**
	 * Create the frame.
	 * 
	 */
	public RunClicks(ClickData clicksData, int dataNum, boolean testing, boolean loaded) {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    width = screenSize.width-200;
	    height = screenSize.height-400;
	    this.clicksData = clicksData;
	    this.dataNum = dataNum;
	    this.testing = testing;
	    this.loaded = loaded;
	    clicks = clicksData.getClickPos();
	    cActions = clicksData.getActions();
	    cDelay = clicksData.getDelays();
	    noOfClicks = clicks.size();
	    
		buildUI();

		setIconImage(new ImageIcon(this.getClass().getResource("/images/AA Logo.png")).getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Automate Anything");

		setPreferredSize(new Dimension(width,height));
		pack();
		setLocationRelativeTo(null);
		active = true;
		setVisible(true);
		
	}
	
	@SuppressWarnings("unchecked")
	private void buildUI() {
		
		String [] clickAction = {"Click","Double Click", "Right Click", "Click Then Input Text", "Click then press ENTER",
				"Click then press TAB" , "Press ENTER", "Press TAB", "Shift + Right Click"};
		String[] delayOptions = new String[30];
				
		for(int i=0;i<30;i++) {
			delayOptions[i] = (i+1) + " seconds";
		}
		
		actions = (JComboBox<String>[]) new JComboBox[noOfClicks];
		delay = (JComboBox<String>[]) new JComboBox[noOfClicks];
		for(int i=0;i<noOfClicks;i++) {
			actions[i] = new JComboBox<String>(clickAction);
			delay[i] = new JComboBox<String>(delayOptions);
		}
				
		panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);
		
		/*
		 * Description Label
		 */
		description = new JTextArea();
		description.setText("Please use the options below to indicate what action you want the program to carry out at each click. "
				+ "These will be carried out in sequence from left to right. "
				+ "You can choose to extend the delay after the action has been carried out to alow the program to wait for another system to update. "
				+ "The delay will be applied after the action which is directly below the dropdown. "
				+ "\nBefore clicking Run please ensure that the screen you want to start on is behind this window. "
				+ "You can press ESC to stop running at any time!");
		description.setBackground(UIManager.getColor("ColorChooser.background"));
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setEditable(false);
		description.setFont(new Font("Tahoma", Font.PLAIN, 14));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridwidth = 5;
		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(description, gbc);
		
		/*
		 * Delay Options
		 */
	    if(dataNum < 26) {
	    	DefaultTableModel delayModel = new DefaultTableModel(0, noOfClicks);
	    	//delayModel.addTableModelListener(this);
	    	delayTable = new JTable(delayModel);
	    } else {
	    	DefaultTableModel delayModel = new DefaultTableModel(2, noOfClicks);
	    	//delayModel.addTableModelListener(this);
	    	delayTable = new JTable(delayModel);
	    }
	    
	    delayTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
	    delayColModel = delayTable.getColumnModel();
	    delayColModel.addColumnModelListener(this);
	    delayTable.setTableHeader(new EditableHeader(delayColModel));
	    ComboRenderer renderer2 = new ComboRenderer(delayOptions);
	    EditableHeaderTableColumn col2;
	    
	    for(int i=0;i<noOfClicks;i++) {
		    col2 = (EditableHeaderTableColumn) delayTable.getColumnModel().getColumn(i);
		    if(loaded) {
		    	col2.setHeaderValue(delay[i].getItemAt(cDelay.get(i)));
		    	delay[i].setSelectedIndex(cDelay.get(i));
		    } else {
		    	col2.setHeaderValue(delay[i].getItemAt(0));
		    }
		    col2.setHeaderRenderer(renderer2);
		    col2.setHeaderEditor(new DefaultCellEditor(delay[i]));	
		    delayTable.getColumnModel().getColumn(i).setPreferredWidth(150);
	    }
		
		delayScroll = new JScrollPane(delayTable);
		delayScroll.setPreferredSize(new Dimension(width-50,45));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridwidth = 5;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(delayScroll, gbc);
		
		/*
		 * Click options and data entry
		 */
		DefaultTableModel dataModel = new DefaultTableModel(dataNum, noOfClicks);
		//dataModel.addTableModelListener(this);
		dataTable = new JTable(dataModel) {
			private static final long serialVersionUID = 1L;
			
			public Component prepareRenderer(TableCellRenderer r, int row, int column) {
			       Component c = super.prepareRenderer(r, row, column);  
				   
			       c.setBackground(Color.WHITE);
			       c.setForeground(Color.BLACK);
				   if(column == nextClick) {
					   c.setBackground(Color.ORANGE);
				   }
				   if(isCellSelected(row,column)) {
					   c.setBackground(new Color(0,68,255));
				   }
			      return c;
			   }
			
		};
		
		InputMap inputMap = dataTable.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap actionMap = dataTable.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		actionMap.put("delete", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
		       int row = dataTable.getSelectedRow();
		       int col = dataTable.getSelectedColumn();
		       if (row >= 0 && col >= 0) {
		           row = dataTable.convertRowIndexToModel(row);
		           col = dataTable.convertColumnIndexToModel(col);
		           dataTable.getModel().setValueAt(null, row, col);
		       }
		    }
		});
		
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		dataTable.setCellSelectionEnabled(true);
	    dataColModel = dataTable.getColumnModel();
	    dataColModel.addColumnModelListener(this);
	    dataTable.setTableHeader(new EditableHeader(dataColModel));
	    ComboRenderer renderer = new ComboRenderer(clickAction);
	    EditableHeaderTableColumn col;
	    
	    for(int i=0;i<noOfClicks;i++) {
		    col = (EditableHeaderTableColumn) dataTable.getColumnModel().getColumn(i);
		    if(loaded) {
		    	col.setHeaderValue(actions[i].getItemAt(cActions.get(i)));
		    	actions[i].setSelectedIndex(cActions.get(i));
		    } else {
		    	col.setHeaderValue(actions[i].getItemAt(0));
		    }
		    col.setHeaderRenderer(renderer);
		    col.setHeaderEditor(new DefaultCellEditor(actions[i]));	
		    dataTable.getColumnModel().getColumn(i).setPreferredWidth(150);
	    }
	    new ExcelAdapter(dataTable);
	  
		dataScroll = new JScrollPane(dataTable);
		dataScroll.getHorizontalScrollBar().setModel(delayScroll.getHorizontalScrollBar().getModel());
		dataScroll.setPreferredSize(new Dimension(width-50,(height/3)*2));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridwidth = 5;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(dataScroll, gbc);
		
		/*
		 *Submit Button 
		 */
		submit = new JButton("Run Data Input");
		submit.setPreferredSize(new Dimension(190,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 0;
		gbc.gridy = 3;
		submit.addActionListener(this);
		panel.add(submit, gbc);
		
		/*
		 * Save Button
		 */
		save = new JButton("Save Sequence");
		save.setPreferredSize(new Dimension(190,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 1;
		gbc.gridy = 3;
		save.addActionListener(this);
		panel.add(save, gbc);
		
		/*
		 * Rerun with different number of lines button
		 */
		rerun = new JButton("Change number of repeats");
		rerun.setPreferredSize(new Dimension(190,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 2;
		gbc.gridy = 3;
		rerun.addActionListener(this);
		panel.add(rerun, gbc);
		
		/*
		 * Run click by click button
		 */
		stepThrough = new JButton("Test Next Click");
		stepThrough.setFont(new Font("Tahoma", Font.BOLD, 13));
		stepThrough.setForeground(Color.ORANGE);
		stepThrough.setPreferredSize(new Dimension(190,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 3;
		gbc.gridy = 3;
		stepThrough.addActionListener(this);
		panel.add(stepThrough, gbc);
		
		/*
		 * Menu button
		 */
		menu = new JButton("Return to Main Menu");
		menu.setPreferredSize(new Dimension(190,25));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,10,5,10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 4;
		gbc.gridy = 3;
		menu.addActionListener(this);
		panel.add(menu, gbc);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == submit) {
			boolean invalidInput = true;
			
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
			
			listener = new EscListener();
			listener.addObserver(this);
			GlobalScreen.addNativeKeyListener(listener);
			
			setVisible(false);
			for(int i=0;i<dataNum;i++) {
				runClicks(i);	
			}
			setVisible(true);
			
			if(testing) {
				int testSeq = JOptionPane.showConfirmDialog(null,"Would you like to run the test again?","Testing",JOptionPane.YES_NO_OPTION);
				
				if(testSeq == JOptionPane.NO_OPTION) {
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
					
					LinkedList<Integer> cActions = new LinkedList<Integer>(), cDelay= new LinkedList<Integer>();
					for(int i=0;i<noOfClicks;i++) {
						cActions.add(actions[i].getSelectedIndex());
						cDelay.add(delay[i].getSelectedIndex());
					}
					
					clicksData.setClickData(cActions, cDelay);
					
					dispose();
					new RunClicks(clicksData,dataNum,false, true);
				}
			} else {
				if(escPress) {
					JOptionPane.showMessageDialog(null, "Data Input Interrupted. Programm will restart from the beginning");
				} else {
					JOptionPane.showMessageDialog(null, "Data Input Complete");	
				}
				escPress = false;
			}
		}
		
		if(e.getSource() == save) {
			//Saves the click x&y data, the delay options and the click options to file
			
			int saveConfirm = JOptionPane.showConfirmDialog(null,"This will save all the data required to run this sequence again."
					+ "Saved data will only run correctly on this computer. Do you wish to continue?","Confirm Save",JOptionPane.YES_NO_OPTION);
			
			if(saveConfirm == JOptionPane.YES_OPTION) {
				
				LinkedList<Integer> cActions = new LinkedList<Integer>(), cDelay= new LinkedList<Integer>();
				for(int i=0;i<noOfClicks;i++) {
					cActions.add(actions[i].getSelectedIndex());
					cDelay.add(delay[i].getSelectedIndex());
				}
				
				clicksData.setClickData(cActions, cDelay);
				
				SaveClicks saver = new SaveClicks();
				
				if(saver.SaveFile(clicksData)) {
					JOptionPane.showMessageDialog(this, "Sequence has been saved!");	
				}				
				
			} else {
				
			}
		}
		
		if(e.getSource() == menu) {
			this.dispose();
			new Menu();
		}
		
		if(e.getSource() == rerun) {
			
			boolean invalidInput = true;
			
			while(invalidInput) {
				try {
					String repeats = JOptionPane.showInputDialog(null, "How many times is this task to be repeated?");
					if(repeats == null) {
						return;
					} else {
						dataNum = Integer.parseInt(repeats);
						if(dataNum > 0){
							invalidInput = false;	
						}
					}
					
				} catch (Exception ex) {
					//empty catch block 
				}
			}
			
			LinkedList<Integer> cActions = new LinkedList<Integer>(), cDelay= new LinkedList<Integer>();
			for(int i=0;i<noOfClicks;i++) {
				cActions.add(actions[i].getSelectedIndex());
				cDelay.add(delay[i].getSelectedIndex());
			}
			
			clicksData.setClickData(cActions, cDelay);
			
			dispose();
			new RunClicks(clicksData,dataNum,false, true);
			
		}
		
		if(e.getSource() == stepThrough) {
			
			setVisible(false);
			singleClick(nextClick);
			if(nextClick < (noOfClicks-1)) {
				nextClick++;
			} else {
				nextClick = 0;
			}		
			setVisible(true);
		}
		
	}
	
    private void runClicks(int rowNum) {
    	
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        int i = 0;
        int delayTime;
        
        try {
            Robot bot = new Robot();
            
            for(LinkedList<Integer> x : clicks) {
            
            	if(escPress) {
            		break;
            	}
            	
            	delayTime = (delay[i].getSelectedIndex() + 1) * 1000;
            	
            	switch(actions[i].getSelectedItem().toString()) {
            	
            	case "Click": 
            		bot.mouseMove(x.get(0),x.get(1));
                    bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                    bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                    
                    Thread.sleep(delayTime);
             
            		break;
            	
            	case "Double Click":
            		bot.mouseMove(x.get(0),x.get(1));
                    bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                    bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                    
                    bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                    bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                    
                    Thread.sleep(delayTime);
            		
                    break;
            	
            	case "Right Click":
            		bot.mouseMove(x.get(0),x.get(1));
                    bot.mousePress(java.awt.event.InputEvent.BUTTON3_MASK);
                    bot.mouseRelease(java.awt.event.InputEvent.BUTTON3_MASK);
                    
                    
                    Thread.sleep(delayTime);
            		
                    break;

            	case "Click Then Input Text":
            		bot.mouseMove(x.get(0),x.get(1));
                    bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                    bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
            		
                    clipboard.setContents(new StringSelection((String) dataTable.getModel().getValueAt(rowNum, i)), null);
            		
            		Thread.sleep(500);
            		bot.keyPress(KeyEvent.VK_CONTROL);
            		bot.keyPress(KeyEvent.VK_V);
            		Thread.sleep(500);
            		bot.keyRelease(KeyEvent.VK_CONTROL);
            		bot.keyRelease(KeyEvent.VK_V);
                    
            		Thread.sleep(delayTime);
            		
            		break;
            		
            	case "Click then press ENTER":
            		bot.mouseMove(x.get(0),x.get(1));
                    bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                    bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                    Thread.sleep(500);
                    
            		bot.keyPress(KeyEvent.VK_ENTER);
            		Thread.sleep(500);
            		bot.keyRelease(KeyEvent.VK_ENTER);
            		
            		Thread.sleep(delayTime);
            		break;
            		
            	case "Click then press TAB":
            		bot.mouseMove(x.get(0),x.get(1));
                    bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                    bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                    Thread.sleep(500);
                    
            		bot.keyPress(KeyEvent.VK_TAB);
            		Thread.sleep(500);
            		bot.keyRelease(KeyEvent.VK_TAB);
            		
            		Thread.sleep(delayTime);
            		
            		break;
            	
            	case "Press ENTER":
            		bot.keyPress(KeyEvent.VK_ENTER);
            		Thread.sleep(500);
            		bot.keyRelease(KeyEvent.VK_ENTER);
            		
            		Thread.sleep(delayTime);
            		
            		break;

            	case "Press TAB":
            		bot.keyPress(KeyEvent.VK_TAB);
            		Thread.sleep(500);
            		bot.keyRelease(KeyEvent.VK_TAB);
            		
            		Thread.sleep(delayTime);
            		break;
            		
            	case "Shift + Right Click":
            		bot.mouseMove(x.get(0),x.get(1));
            		bot.keyPress(KeyEvent.VK_SHIFT);
                    bot.mousePress(java.awt.event.InputEvent.BUTTON3_MASK);
                    bot.mouseRelease(java.awt.event.InputEvent.BUTTON3_MASK);
                    bot.keyRelease(KeyEvent.VK_SHIFT);
                    
                    Thread.sleep(delayTime);
            		
            	}
            	i++;
            }

        } catch (Exception e) {
            System.out.println("Exception occured :" + e.getMessage());
        }
    }
    
    private void singleClick(int clickNum) {
    	
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        int delayTime;
        LinkedList<Integer> x = clicks.get(clickNum);
        
        try {
            Robot bot = new Robot();
            	
        	delayTime = (delay[clickNum].getSelectedIndex() + 1) * 1000;
        	
        	switch(actions[clickNum].getSelectedIndex()) {
        	
        	//Click
        	case 0: 
        		bot.mouseMove(x.get(0),x.get(1));
                bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                
                Thread.sleep(delayTime);
         
        		break;
        	
        	//Double Click
        	case 1:
        		bot.mouseMove(x.get(0),x.get(1));
                bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                
                bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                
                Thread.sleep(delayTime);
        		
                break;
        	
        	//Right Click
        	case 2:
        		bot.mouseMove(x.get(0),x.get(1));
                bot.mousePress(java.awt.event.InputEvent.BUTTON3_MASK);
                bot.mouseRelease(java.awt.event.InputEvent.BUTTON3_MASK);
                
                
                Thread.sleep(delayTime);
        		
                break;

        	//Click then input
        	case 3:
        		bot.mouseMove(x.get(0),x.get(1));
                bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
        		
                clipboard.setContents(new StringSelection((String) dataTable.getModel().getValueAt(0, clickNum)), null);
        		
        		Thread.sleep(500);
        		bot.keyPress(KeyEvent.VK_CONTROL);
        		bot.keyPress(KeyEvent.VK_V);
        		Thread.sleep(500);
        		bot.keyRelease(KeyEvent.VK_CONTROL);
        		bot.keyRelease(KeyEvent.VK_V);
                
        		Thread.sleep(delayTime);
        		
        		break;
        		
        	//Click then ENTER
        	case 4:
        		bot.mouseMove(x.get(0),x.get(1));
                bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                Thread.sleep(500);
                
        		bot.keyPress(KeyEvent.VK_ENTER);
        		Thread.sleep(500);
        		bot.keyRelease(KeyEvent.VK_ENTER);
        		
        		Thread.sleep(delayTime);
        		break;
        		
        	//Click then TAB
        	case 5:
        		bot.mouseMove(x.get(0),x.get(1));
                bot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                bot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                Thread.sleep(500);
                
        		bot.keyPress(KeyEvent.VK_TAB);
        		Thread.sleep(500);
        		bot.keyRelease(KeyEvent.VK_TAB);
        		
        		Thread.sleep(delayTime);
        		
        		break;
        	
        	//Press ENTER
        	case 6:
        		bot.keyPress(KeyEvent.VK_ENTER);
        		Thread.sleep(500);
        		bot.keyRelease(KeyEvent.VK_ENTER);
        		
        		Thread.sleep(delayTime);
        		
        		break;

        	//Press Tab
        	case 7:
        		bot.keyPress(KeyEvent.VK_TAB);
        		Thread.sleep(500);
        		bot.keyRelease(KeyEvent.VK_TAB);
        		
        		Thread.sleep(delayTime);
        		break;
        	}

        } catch (Exception e) {
            System.out.println("Exception occured :" + e.getMessage());
        }
    }
    
	@Override
	public void columnMarginChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
			
		if(active) {
			for(int i=0;i<dataTable.getColumnCount();i++) {
				TableColumn delayCol = delayTable.getColumnModel().getColumn(i);
				TableColumn dataCol = dataTable.getColumnModel().getColumn(i);
				
				int dataWidth = dataCol.getWidth();
				int delayWidth = delayCol.getWidth();
				
				if(e.getSource() == dataColModel) {
						delayCol.setWidth(dataWidth);
						delayCol.setPreferredWidth(dataWidth);
				} else {
						dataCol.setWidth(delayWidth);
						dataCol.setPreferredWidth(delayWidth);
				}
			}
		}	

		
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnAdded(TableColumnModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnMoved(TableColumnModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnRemoved(TableColumnModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		escPress = true;
	}

}
