package com.dberm22.DBoard;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout; //imports GridLayout library
import java.awt.Toolkit;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.dberm22.DBoard.Settings.Settings;
import com.dberm22.DBoard.Settings.SettingsWindow;
import com.dberm22.DBoard.Settings.SoundFile;
import com.dberm22.utils.JImagePanel;
import com.dberm22.utils.LongPressListener;
import com.dberm22.utils.TwoWayHashMap;
import com.dberm22.utils.TransparentJButton;
import com.dberm22.utils.TransparentJPanel;
import com.thirdparty.utils.GraphicsUtilities;
import com.thoughtworks.xstream.XStream;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
 
public class DBoard extends TransparentJPanel implements DropTargetListener
{
	private static final long serialVersionUID = 3249995321773818523L;
	
	String abspath;
	String boardname;
	TwoWayHashMap<TransparentJButton, SoundPlayer> currentlyPlaying = new TwoWayHashMap<TransparentJButton, SoundPlayer>();
	XStream xstream;
    JFrame frame=new JFrame(); //creates frame
    TransparentJPanel soundboard=new TransparentJPanel();
    TransparentJButton[][] grid; //names the grid of buttons
    Settings settings;
    int currentpage = 0;
    DragState state;
    boolean changesmade = false;
    boolean exitonclose = true;
    TransparentJButton prevpage;
    TransparentJButton nextpage;
    JImagePanel contentpanel = new JImagePanel();
 
    @SuppressWarnings("unchecked")
	public DBoard(int length, int width)
    {
    	
    	try
    	{
    		abspath = DBoard.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    		abspath = URLDecoder.decode(abspath, "UTF-8");
        	abspath = abspath.substring(0, abspath.lastIndexOf("bin/")).concat("boards/");
    	} catch (Exception e1) {e1.printStackTrace();}
    	
		//Initialize Button Grid
		soundboard.setLayout(new GridLayout(length,width)); //set layout
        grid=new TransparentJButton[length][width]; //allocate the size of grid
        
        //Initialize mediafiles
        ArrayList<ArrayList<ArrayList<SoundFile>>> mediafiles = new ArrayList<ArrayList<ArrayList<SoundFile>>>();
		ArrayList<SoundFile> row = new ArrayList<SoundFile>();
		for(int i=0; i<grid[0].length; i++)
        {
			row.add(new SoundFile(abspath.concat("Empty.txt"),0.0F));
			
        }
		ArrayList<ArrayList<SoundFile>> page = new ArrayList<ArrayList<SoundFile>>();
		for(int i=0; i<grid.length; i++)
        {
			page.add((ArrayList<SoundFile>) row.clone());
        }
		mediafiles.add(page);
		settings = new Settings(mediafiles);
		
		// Finish Creating Button Grid
        for(int y=0; y<length; y++){
	    	for(int x=0; x<width; x++){
	    		grid[y][x]=new TransparentJButton("Empty"); //creates new button    
                soundboard.add(grid[y][x]); //adds button to grid
                        
                new DropTarget(grid[y][x], DnDConstants.ACTION_COPY_OR_MOVE, this);
	    	}
        }
        
        //Save xml
        JFileChooser chooser = new JFileChooser("Save...");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Board Files Only","board");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(new File(abspath));
		int returnVal = chooser.showSaveDialog(DBoard.this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile().getName().contains(".board"))
			{
				boardname = chooser.getSelectedFile().getName().substring(0, chooser.getSelectedFile().getName().indexOf(".board"));
			}
			else
			{
				boardname = chooser.getSelectedFile().getName();
			}
			abspath = chooser.getSelectedFile().getAbsolutePath().substring(0, chooser.getSelectedFile().getAbsolutePath().indexOf(boardname));
	  		File xmlfile = new File(abspath.concat(boardname).concat(".board"));
	  		try{xmlfile.createNewFile();} catch (IOException ioe) {ioe.printStackTrace();}
			
	  		xstream = new XStream();
			saveBoard();
		}
        
		
		addDecoration();
        
    }
    
    public DBoard(File board)
    {
    	boardname = board.getName().substring(0, board.getName().indexOf(".board"));;
		abspath = board.getAbsolutePath().substring(0, board.getAbsolutePath().indexOf(boardname));
  		
    	//Load files
    	xstream = new XStream();
		try 
		{
			FileInputStream fis = new FileInputStream(board);
    		settings = (Settings) xstream.fromXML(fis,settings);
    		inflateandfill();
            fis.close();
            fis = null;
		}
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(frame,
				    "Error Reading the File. File might be corrupted.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		addDecoration();
        
    }

    public void inflateandfill()
    {
    	int length = settings.mediafiles.get(currentpage).size();
    	int width = settings.mediafiles.get(currentpage).get(0).size();
    	File file = null;
    	
        soundboard.setLayout(new GridLayout(length,width)); //set layout
        grid=new TransparentJButton[length][width]; //allocate the size of grid
        for(int y=0; y<length; y++){
	    	for(int x=0; x<width; x++){
	    		file = new File(settings.mediafiles.get(currentpage).get(y).get(x).getPath());
	    		String[] tokens = file.getName().split("\\.(?=[^\\.]+$)");
	    		grid[y][x]=new TransparentJButton(tokens[0], settings.getButtonAlpha()); //creates new button    
                soundboard.add(grid[y][x]); //adds button to grid
                        
                new DropTarget(grid[y][x], DnDConstants.ACTION_COPY_OR_MOVE, this);
                
                (grid[y][x]).addMouseListener(new LongPressListener() 
                {  
                    @Override  
                    public void onClick(MouseEvent event)
                    { 
                    	addplayactiontobutton(event);
                    }
                    
                    @Override  
                    public void onLongPress(MouseEvent event)
                    { 
                    	onRightClick(event);
                    } 
                    
                    @Override  
                    public void onRightClick(MouseEvent event)
                    { 
                    	addrightclickmenutobutton(event);
                    } 
                });
	    	}
        }
    }
    
    private void addDecoration()
    {
    	// Add Menu
		addMenu();
		
		//Add Arrows
		try {prevpage = new TransparentJButton(GraphicsUtilities.loadCompatibleImage(getClass().getClassLoader().getResource("left_arrow.png")), settings.getButtonAlpha());} catch (IOException e) {}
			ImageIcon leftarrow = new ImageIcon(getClass().getClassLoader().getResource("left_arrow.png"));
			final String leftArrowPressed = "Left Arrow Pressed";
			prevpage.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0,true), leftArrowPressed);
        	prevpage.getActionMap().put(leftArrowPressed, new LeftArrowAction(leftarrow));
        	prevpage.setAction(new LeftArrowAction(leftarrow));
        try {nextpage = new TransparentJButton(GraphicsUtilities.loadCompatibleImage(getClass().getClassLoader().getResource("right_arrow.png")), settings.getButtonAlpha());} catch (IOException e) {}
    		ImageIcon rightarrow = new ImageIcon(getClass().getClassLoader().getResource("right_arrow.png"));
			final String rightArrowPressed = "Right Arrow Pressed";
			nextpage.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0,true), rightArrowPressed);
			nextpage.getActionMap().put(rightArrowPressed, new RightArrowAction(rightarrow));
			nextpage.setAction(new RightArrowAction(rightarrow));
		
		contentpanel.setLayout(new BorderLayout());
		contentpanel.add(soundboard,BorderLayout.CENTER);
		
		TransparentJPanel prevpagepanel = new TransparentJPanel();
			prevpagepanel.setLayout(new BorderLayout());
			prevpagepanel.add(prevpage);
			contentpanel.add(prevpagepanel,BorderLayout.LINE_START);
			
		TransparentJPanel nextpagepanel = new TransparentJPanel();
			nextpagepanel.setLayout(new BorderLayout());
			nextpagepanel.add(nextpage);	
			contentpanel.add(nextpagepanel,BorderLayout.LINE_END);

		frame.add(contentpanel);
		
		//Add Title and image
		frame.setTitle(boardname);
		frame.setIconImage((new ImageIcon(abspath.substring(0, abspath.lastIndexOf("\\boards")).concat("\\res\\dboard.png"))).getImage());
		
		//Finish Creating Window
		setSettings(settings);
		this.changesmade = false;
		frame.setSize( Toolkit.getDefaultToolkit().getScreenSize());
		frame.setPreferredSize( Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new CloseWindowListener());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); //sets appropriate size for frame
        frame.setVisible(true); //makes frame visible
    }

    private void addMenu()
    {
		JMenuBar menubar = new JMenuBar();
		
		JMenu filemenu = new JMenu("File");
			JMenuItem filemenu_new = new JMenuItem("New");
				filemenu_new.addActionListener(new NewBoardActionListener());
				filemenu.add(filemenu_new);
			JMenuItem filemenu_open = new JMenuItem("Open");
				filemenu_open.addActionListener(new OpenBoardActionListener());
				filemenu.add(filemenu_open);
			JMenuItem filemenu_savepage = new JMenuItem("Save");
				filemenu_savepage.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { saveBoard();}});
				filemenu.add(filemenu_savepage);
			JMenuItem filemenu_exit = new JMenuItem("Exit");
				filemenu_exit.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));}});
				filemenu.add(filemenu_exit);
			menubar.add(filemenu);
				
		JMenu editmenu = new JMenu("Edit");
			JMenu editmenu_add = new JMenu("Add");
				JMenuItem editmenu_addrow = new JMenuItem("Row");
					editmenu_addrow.addActionListener(new AddRowActionListener());
					editmenu_add.add(editmenu_addrow);
				JMenuItem editmenu_addcol = new JMenuItem("Column");
					editmenu_addcol.addActionListener(new AddColActionListener());
					editmenu_add.add(editmenu_addcol);
				JMenuItem editmenu_addpage = new JMenuItem("Page");
					editmenu_addpage.addActionListener(new AddPageActionListener());
					editmenu_add.add(editmenu_addpage);
				editmenu.add(editmenu_add);
			JMenu editmenu_remove = new JMenu("Remove");
				JMenuItem editmenu_removerow = new JMenuItem("Last Row");
					editmenu_removerow.addActionListener(new RemoveRowActionListener());
					editmenu_remove.add(editmenu_removerow);
				JMenuItem editmenu_removecol = new JMenuItem("Last Column");
					editmenu_removecol.addActionListener(new RemoveColActionListener());
					editmenu_remove.add(editmenu_removecol);
				JMenuItem editmenu_removepage = new JMenuItem("Current Page");
					editmenu_removepage.addActionListener(new RemovePageActionListener());
					editmenu_remove.add(editmenu_removepage);
				editmenu.add(editmenu_remove);
			JMenuItem editmenu_prefs = new JMenuItem("Preferences");
				editmenu_prefs.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e){new SettingsWindow(DBoard.this, settings);}});
				editmenu.add(editmenu_prefs);
			menubar.add(editmenu);
			
		JMenu helpmenu = new JMenu("Help");
			JMenuItem helpmenu_about = new JMenuItem("About");
				helpmenu_about.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new About();}});
			helpmenu.add(helpmenu_about);
		menubar.add(helpmenu);
			
		
		frame.setJMenuBar(menubar);
    }
    
    public enum DragState 
    {
        Accept,
        Reject
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void dragEnter(DropTargetDragEvent dtde) {
    	state = DragState.Reject;
        Transferable t = dtde.getTransferable();
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                Object td = t.getTransferData(DataFlavor.javaFileListFlavor);
                if (td instanceof List) {
                    state = DragState.Accept;
                    for (Object value : ((List<Object>) td)) {
                        if (value instanceof File) {
                            File file = (File) value;
                            if (!isSupportedFile(file.getName())) {
                                state = DragState.Reject;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (state == DragState.Accept) {
        	dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @SuppressWarnings("unchecked")
	@Override
    public void drop(DropTargetDropEvent dtde) {
    	state = DragState.Reject;
    	File file = null;
    	dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        Transferable t = dtde.getTransferable();
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                Object td = t.getTransferData(DataFlavor.javaFileListFlavor);
                if (td instanceof List) {
                    state = DragState.Accept;
                    for (Object value : ((List<Object>) td)) {
                        if (value instanceof File) {
                            file = (File) value;
                            if (!isSupportedFile(file.getName())) {
                                state = DragState.Reject;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        if (state == DragState.Accept) 
        {
        	changesmade = true;
        	
        	//System.out.println("Dropped file " + file.toString());
        	TransparentJButton button = (TransparentJButton) dtde.getDropTargetContext().getDropTarget().getComponent();
        	String[] tokens = file.getName().split("\\.(?=[^\\.]+$)");
        	button.setText(tokens[0]);
        	frame.repaint();
        	
            for(int yloc=0; yloc<grid.length; yloc++)
            {
                for(int xloc=0; xloc<grid[0].length; xloc++)
                {
                	if ((grid[yloc][xloc]).equals(button))
                	{
                		
                		System.out.println("Found Target Button at loc ("+yloc+","+xloc+")");

                		settings.mediafiles.get(currentpage).get(yloc).set(xloc,new SoundFile(file.toString()));
                		
                		//Remove any Actionlisteners and add new one
                		for( ActionListener al : button.getActionListeners() ) {
                	        button.removeActionListener( al );
                		}
                        button.addMouseListener(new LongPressListener() 
                        {  
                            @Override  
                            public void onClick(MouseEvent event)
                            { 
                            	addplayactiontobutton(event);
                            } 
                            
                            @Override  
                            public void onLongPress(MouseEvent event)
                            { 
                            	onRightClick(event);
                            } 
                            
                            @Override  
                            public void onRightClick(MouseEvent event)
                            { 
                            	addrightclickmenutobutton(event);
                            } 
                        });
                	}
                }
            }

        } 
        else 
        {
            dtde.rejectDrop();
        }
    }
    
    public void addplayactiontobutton(MouseEvent event)
    {
    	try 
    	{
        	TransparentJButton button = (TransparentJButton) event.getSource();
        	for(int yloc=0; yloc<grid.length; yloc++)
            {
                for(int xloc=0; xloc<grid[0].length; xloc++)
                {
                	if ((grid[yloc][xloc]).equals(button))
                	{
                		System.out.println("Button at ("+yloc+","+xloc+") clicked");
                		String audiofile = settings.mediafiles.get(currentpage).get(yloc).get(xloc).getPath();
                		
                		if((!settings.getSingularAudioSetting() && !currentlyPlaying.containsKey(button)) || (settings.getSingularAudioSetting() && currentlyPlaying.isEmpty()))
                		{
                			System.out.println("Playing " + audiofile);
                			SoundPlayer player = new SoundPlayer(audiofile);
                			player.setVolumeInDecibels(settings.mediafiles.get(currentpage).get(yloc).get(xloc).getVolumeInDecibels());
                			player.setAttachedBoard(DBoard.this);
                			currentlyPlaying.put(button,player);
                			(new Thread(player)).start();
                		}
                		else if(!settings.getSingularAudioSetting() && currentlyPlaying.containsKey(button) && currentlyPlaying.get(button).isPaused())
    					{
                			System.out.println("Resuming " + audiofile);
    						currentlyPlaying.get(button).resume();
    					}
                		else if (settings.getSingularAudioSetting() && !currentlyPlaying.isEmpty())
            			{
            				System.out.println("Stopping " + audiofile);
            				for(TransparentJButton key : currentlyPlaying.keySet())
            				{
            					currentlyPlaying.getValue(key).stop();
            				}
            			}
                		else
                		{
                			System.out.println("Stopping " + audiofile);
                			currentlyPlaying.getValue(button).stop();
                		}
                		
                	}
                }
            }
    	}
        catch (Exception e) 
        {
			JOptionPane.showMessageDialog(frame,
				    "Unsupported audio file or file not found. Try converting to a new format and try again.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
    }
    
    
    public void addrightclickmenutobutton(MouseEvent event)
    {
    	String temp_audiofile = null;
    	float temp_volume_db = 0.0f;
		int temp_button_x = 0;
		int temp_button_y = 0;
    	final TransparentJButton button = (TransparentJButton) event.getSource();
    	for(int yloc=0; yloc<grid.length; yloc++)
	    {
	        for(int xloc=0; xloc<grid[0].length; xloc++)
	        {
	        	if ((grid[yloc][xloc]).equals(button))
	        	{
	        		System.out.println("Button at ("+yloc+","+xloc+") clicked");
	        		temp_audiofile = settings.mediafiles.get(currentpage).get(yloc).get(xloc).getPath();
	        		temp_volume_db = settings.mediafiles.get(currentpage).get(yloc).get(xloc).getVolumeInDecibels();
	        		
	        		temp_button_x = xloc;
	        		temp_button_y = yloc;
	        	}
	        }
	    }
    	
    	final String audiofile = temp_audiofile;
    	final float volume_db = temp_volume_db;
		final int button_x = temp_button_x;
		final int button_y = temp_button_y;
    	
    	JPopupMenu menu = new JPopupMenu();
    	
	    JMenuItem play_item = new JMenuItem("Play / Resume");
	    	play_item.addActionListener(new ActionListener()
	    	{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					if(!settings.getSingularAudioSetting() && currentlyPlaying.containsKey(button) && currentlyPlaying.get(button).isPaused())
					{
						System.out.println("Resuming " + audiofile);
						currentlyPlaying.get(button).resume();
					}
					else
					{
						System.out.println("Playing " + audiofile);
	        			SoundPlayer player = new SoundPlayer(audiofile);
	        			player.setVolumeInDecibels(volume_db);
	        			player.setAttachedBoard(DBoard.this);
	        			currentlyPlaying.put(button,player);
	        			(new Thread(player)).start();
					}
				}
			});
	    	menu.add(play_item);
	    JMenuItem pause_item = new JMenuItem("Pause");
	    	pause_item.addActionListener(new ActionListener()
	    	{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					if (settings.getSingularAudioSetting())
	    			{
	    				System.out.println("Pausing " + audiofile);
	    				for(TransparentJButton key : currentlyPlaying.keySet())
	    				{
	    					currentlyPlaying.getValue(key).pause();
	    				}
	    			}
	        		else
	        		{
	        			System.out.println("Pausing " + audiofile);
	        			currentlyPlaying.getValue(button).pause();
	        		}
				}
	    	});
	    	menu.add(pause_item);
	    JMenuItem stop_item = new JMenuItem("Stop");
		    stop_item.addActionListener(new ActionListener()
	    	{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					if (settings.getSingularAudioSetting())
	    			{
	    				System.out.println("Stopping " + audiofile);
	    				for(TransparentJButton key : currentlyPlaying.keySet())
	    				{
	    					currentlyPlaying.getValue(key).stop();
	    				}
	    			}
	        		else
	        		{
	        			System.out.println("Stopping " + audiofile);
	        			currentlyPlaying.getValue(button).stop();
	        		}
				}
	    	});
	    	menu.add(stop_item);
	    JMenu volume_menu = new JMenu("Volume");
	    
	    final JSlider volumeslider = new JSlider(JSlider.VERTICAL,-30*100, 6*100, 100*Math.round(volume_db));
	    volumeslider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				float volume = ((float)volumeslider.getValue())/100.0F;
				settings.getMediaFiles().get(currentpage).get(button_y).get(button_x).setVolumeInDecibels(volume);
				if(currentlyPlaying.containsKey(button))
				{
					currentlyPlaying.get(button).setVolumeInDecibels(volume);
				}
				changesmade=true;
			}});
	    volumeslider.setMajorTickSpacing(150);
	    volumeslider.setPaintTicks(true);

		//Create the label table
		Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 6*100 ), new JLabel("+6 dB ") );
		labelTable.put( new Integer( 3*100 ), new JLabel("+3 dB ") );
		labelTable.put( new Integer( 0 ), new JLabel("0 dB ") );
		labelTable.put( new Integer( -3*100 ), new JLabel("-3 dB ") );
		labelTable.put( new Integer( -6*100 ), new JLabel("-6 dB ") );
		labelTable.put( new Integer( -10*100 ), new JLabel("-10 dB ") );
		labelTable.put( new Integer( -20*100 ), new JLabel("-20 dB ") );
		labelTable.put( new Integer( -30*100 ), new JLabel("-30 dB ") );
		volumeslider.setLabelTable( labelTable );
		
		volumeslider.setPaintLabels(true);
	    
	    volume_menu.add(volumeslider);
	    	menu.add(volume_menu);
	    	
		
    	
    	
    	if((!settings.getSingularAudioSetting() && !currentlyPlaying.containsKey(button)) || 
    	   (!settings.getSingularAudioSetting() && currentlyPlaying.containsKey(button) && currentlyPlaying.get(button).isPaused()) || 
    	   (settings.getSingularAudioSetting() && currentlyPlaying.isEmpty())
    	  )
		{
			pause_item.setEnabled(false);
			stop_item.setEnabled(false);
		}
		else if (settings.getSingularAudioSetting() && !currentlyPlaying.isEmpty())
		{
			play_item.setEnabled(false);
		}
		else
		{
			play_item.setEnabled(false);
		}
    	
    	
    	
        menu.show(event.getComponent(), event.getX(), event.getY());
    }
    
    public boolean isSupportedFile(String FileName)
    {
    	if(FileName.toLowerCase().endsWith(".mp3") ||
    	   FileName.toLowerCase().endsWith(".wav") ||
    	   FileName.toLowerCase().endsWith(".wave") ||
    	   FileName.toLowerCase().endsWith(".ogg") ||
    	   FileName.toLowerCase().endsWith(".au") ||
    	   FileName.toLowerCase().endsWith(".aiff"))
    	{
    		return true;
    	}
    	return false;
    }
    
    class AddRowActionListener implements ActionListener 
    {
  	  public void actionPerformed(ActionEvent e) 
  	  {
		changesmade = true;
		
  		int width = settings.mediafiles.get(currentpage).get(0).size();
  		ArrayList<SoundFile> row = new ArrayList<SoundFile>();
		for(int i=0; i<width; i++)
        {
			row.add(new SoundFile(abspath.concat("Empty.txt"),0.0F));
        }
		settings.mediafiles.get(currentpage).add(row);

		int length = settings.mediafiles.get(currentpage).size();
    	soundboard.setLayout(new GridLayout(length,width)); //set layout
		TransparentJButton[][] tempgrid=new TransparentJButton[length][width]; //allocate the size of grid
		for(int x=0; x<width; x++)
		{
	    	tempgrid[length-1][x]=new TransparentJButton("Empty"); //creates new button    
            soundboard.add(tempgrid[length-1][x]); //adds button to grid
		}
		
		//save to global grid
		length = grid.length;
		width = grid[0].length;
		for(int y=0; y<length; y++)
		{
			for(int x=0; x<width; x++)
			{
				tempgrid[y][x]=grid[y][x];
			}
		}
		grid = tempgrid;
		
		setSettings(settings);
  	  }
    }

    class RemoveRowActionListener implements ActionListener 
    {
  	  public void actionPerformed(ActionEvent e) 
  	  {
  		changesmade = true;
  		
  		int length = settings.mediafiles.get(currentpage).size();
  		int width = settings.mediafiles.get(currentpage).get(0).size();
  		
  		settings.mediafiles.get(currentpage).remove(length-1);
		length=length-1;
		
    	soundboard.setLayout(new GridLayout(length,width)); //set layout
		TransparentJButton[][] tempgrid=new TransparentJButton[length][width]; //allocate the size of grid
		
		soundboard.removeAll();
		for(int y=0; y<length; y++)
		{
			for(int x=0; x<width; x++)
			{
				tempgrid[y][x]=grid[y][x];
	            soundboard.add(tempgrid[y][x]); //adds button to grid
			}
		}
		grid = tempgrid;
		
		setSettings(settings);
  	  }
    }
    
    class AddColActionListener implements ActionListener 
    {
  	  public void actionPerformed(ActionEvent e) 
  	  {
  		changesmade = true;
  		
  		int length = settings.mediafiles.get(currentpage).size();
		for(int i=0; i<length; i++)
        {
			settings.mediafiles.get(currentpage).get(i).add(new SoundFile(abspath.concat("Empty.txt"),0.0F));
        }

		//Update global grid
		int width = settings.mediafiles.get(currentpage).get(0).size();
		TransparentJButton[][] tempgrid=new TransparentJButton[length][width]; //allocate the size of grid
		for(int y=0; y<length; y++)
		{
	    	tempgrid[y][width-1]=new TransparentJButton("Empty"); //creates new button 
		}
		
		//save to global grid
		length = grid.length;
		width = grid[0].length;
		for(int y=0; y<length; y++)
		{
			for(int x=0; x<width; x++)
			{
				tempgrid[y][x]=grid[y][x];
			}
		}
		grid = tempgrid;
		
		//Update Display
		length = settings.mediafiles.get(currentpage).size();
		width = settings.mediafiles.get(currentpage).get(0).size();
    	soundboard.setLayout(new GridLayout(length,width)); //set layout
		for(int y=0; y<length; y++)
		{
			for(int x=0; x<width; x++)
			{
				soundboard.add(grid[y][x]); //adds button to grid
			}
		}
		
		setSettings(settings);
  	  }
    }

    class RemoveColActionListener implements ActionListener 
    {
  	  public void actionPerformed(ActionEvent e) 
  	  {
  		changesmade = true;
  		
  		int length = settings.mediafiles.get(currentpage).size();
  		int width = settings.mediafiles.get(currentpage).get(0).size();
  		
  		for(int i=0; i<length; i++)
		{
  			settings.mediafiles.get(currentpage).get(i).remove(width-1);
		}
  		width=width-1;	
		
    	soundboard.setLayout(new GridLayout(length,width)); //set layout
		TransparentJButton[][] tempgrid=new TransparentJButton[length][width]; //allocate the size of grid
		
		soundboard.removeAll();
		for(int y=0; y<length; y++)
		{
			for(int x=0; x<width; x++)
			{
				tempgrid[y][x]=grid[y][x];
	            soundboard.add(tempgrid[y][x]); //adds button to grid
			}
		}
		grid = tempgrid;
		
		setSettings(settings);
  	  }
    }
    
    class AddPageActionListener implements ActionListener 
    {
  	
      @SuppressWarnings("unchecked")
	  public void actionPerformed(ActionEvent e) 
  	  {
  		changesmade = true;
  		
  		//Initialize mediafiles
		ArrayList<SoundFile> row = new ArrayList<SoundFile>();
		for(int i=0; i<grid[0].length; i++)
        {
			row.add(new SoundFile(abspath.concat("Empty.txt"),0.0F));
        }
		ArrayList<ArrayList<SoundFile>> page = new ArrayList<ArrayList<SoundFile>>();
		for(int i=0; i<grid.length; i++)
        {
			page.add((ArrayList<SoundFile>) row.clone());
        }
		settings.mediafiles.add(page);
		
		currentpage = currentpage + 1;
		soundboard.removeAll();
		inflateandfill();
		
		setSettings(settings);
  	  }
    }
    
    class RemovePageActionListener implements ActionListener 
    {
  	  public void actionPerformed(ActionEvent e) 
  	  {
  		changesmade = true;
  		
  		settings.mediafiles.remove(currentpage);
		
		if(currentpage==settings.mediafiles.size())
			currentpage = currentpage - 1;
		
		
		soundboard.removeAll();
		inflateandfill();
		
		setSettings(settings);
  	  }
    }
    
    class NewBoardActionListener implements ActionListener 
    {
    	
		public void actionPerformed(ActionEvent e)
		{
				
				if(changesmade)
	        	{
		        	Object[] options = {"Create Without Saving", "Save Changes", "Cancel"};
		        	int choice = JOptionPane.showOptionDialog(frame,
						    "Are you sure you want to create a new board? All unsaved data, if any, will be lost!",
						    null,
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[2]);
		        	
		        	if(choice==0)
		        	{
		        		exitonclose = false;
		        		new DBoard(5,5);
						frame.dispose();
		        	}
		        	else if(choice==1)
		        	{
		        		exitonclose = false;
		        		saveBoard();
		        		new DBoard(5,5);
						frame.dispose();
		        		
		        	}
		        	else{} //do nothing if "Cancel" is selected or if dialog is closed
	        	}
				else
				{
					exitonclose = false;
					new DBoard(5,5);
					frame.dispose();
				}
		}
    }
    
    class OpenBoardActionListener implements ActionListener 
    {
    	
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser chooser = new JFileChooser("Open...");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Board Files Only","board");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(new File(abspath));
			int returnVal = chooser.showOpenDialog(DBoard.this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				
				if(changesmade)
	        	{
		        	Object[] options = {"Open Without Saving", "Save Changes", "Cancel"};
		        	int choice = JOptionPane.showOptionDialog(frame,
						    "Are you sure you want to open a new board? All unsaved data, if any, will be lost!",
						    null,
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[2]);
		        	
		        	if(choice==0)
		        	{
		        		exitonclose = false;
		        		new DBoard(chooser.getSelectedFile());
						frame.dispose();
		        	}
		        	else if(choice==1)
		        	{
		        		exitonclose = false;
		        		saveBoard();
		        		new DBoard(chooser.getSelectedFile());
						frame.dispose();
		        		
		        	}
		        	else{} //do nothing if "Cancel" is selected or if dialog is closed
	        	}
				else
				{
					exitonclose = false;
					new DBoard(chooser.getSelectedFile());
					frame.dispose();
				}
			}
		}
    }
    
    class LeftArrowAction extends AbstractAction 
    {
		private static final long serialVersionUID = 1238668365044413705L;

		public LeftArrowAction(ImageIcon image) {super(null, image);}

        public void actionPerformed(ActionEvent e) 
        {
        	if(currentpage>0)
        	{
        		currentpage = currentpage - 1;
        		soundboard.removeAll();
        		inflateandfill();
    		
        		setSettings(settings);
        	}
        }
    }
    
    class RightArrowAction extends AbstractAction 
    {
		private static final long serialVersionUID = -3538512243614657969L;

		public RightArrowAction(ImageIcon image) {super(null, image);}

        public void actionPerformed(ActionEvent e) 
        {
        	if(currentpage<(settings.mediafiles.size()-1))
        	{
        		currentpage = currentpage + 1;
    			soundboard.removeAll();
    			inflateandfill();
    		
    			setSettings(settings);
        	}
        }
    }
    
    public class CloseWindowListener extends WindowAdapter {

        public CloseWindowListener() {
        }

        public void windowClosing(WindowEvent e) 
        {        	
        	if(changesmade)
        	{
	        	Object[] options = {"Close Without Saving", "Save and Close","Cancel"};
	        	int choice = JOptionPane.showOptionDialog(frame,
					    "Are you sure you want to close? All unsaved data, if any, will be lost!",
					    null,
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[2]);
	        	
	        	if(choice==0)
	        	{
	        		frame.dispose();
	        		if(exitonclose)
	            	{
	            		System.exit(0);
	            	}
	        	}
	        	else if(choice==1)
	        	{
	        		saveBoard();
	        		
	        		frame.dispose();
	            	System.exit(0);
	        		
	        	}
	        	else{} //do nothing if "Cancel" is selected or if dialog is closed
        	}
        	else
        	{
        		frame.dispose();
        		if(exitonclose)
            	{
            		System.exit(0);
            	}
        	}
        }
    }
    
    public void setSettings(Settings settings)
    {
    	this.settings = settings;

    	//Set BG
    	contentpanel.setBackground(settings.getBackgroundColor());
    	contentpanel.setBackgroundImage(settings.getBackgroundImage());
    	contentpanel.setBackgroundImagePosition(settings.getBackgroundImagePosition());
    	
    	//Color Buttons
    	for (Component button : soundboard.getComponents()) {
    	    if (button instanceof TransparentJButton) { 
    	        TransparentJButton tempbutton = (TransparentJButton) button;
    	        tempbutton.setBackground(settings.getButtonColor());
    	        tempbutton.setAlpha(settings.getButtonAlpha());
    	     }
    	 }
    	prevpage.setBackground(settings.getButtonColor());
    	prevpage.setAlpha(settings.getButtonAlpha());
    	nextpage.setBackground(settings.getButtonColor());
    	nextpage.setAlpha(settings.getButtonAlpha());
    	
    	validate();
    	repaint();	
    	
    	changesmade=true;
    }
    
    private void saveBoard()
    {
    	try
		{
			FileOutputStream fos = new FileOutputStream(abspath.concat(boardname).concat(".board"));
        	xstream.toXML(settings, fos);
        	fos.flush();
        	fos.close();
        	fos = null;
        	
        	changesmade = false;
        	
		}
    	catch (Exception e)
    	{
    		System.out.println("An error has occured while attempting to save, most likely because this is the first time saving.");
    		e.printStackTrace();
    		/*
    		JOptionPane.showMessageDialog(frame,
				    "An error has occured while attempting to save, most likely because this is" + 
				    		"your first time saving. Please save as new file.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			*/
    		
            //Choose file and try again
            JFileChooser chooser = new JFileChooser("Save...");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Board Files Only","board");
    		chooser.setFileFilter(filter);
    		chooser.setCurrentDirectory(new File(abspath));
    		int returnVal = chooser.showSaveDialog(DBoard.this);
    		if(returnVal == JFileChooser.APPROVE_OPTION) {
    			if (chooser.getSelectedFile().getName().contains(".board"))
    			{
    				boardname = chooser.getSelectedFile().getName().substring(0, chooser.getSelectedFile().getName().indexOf(".board"));
    			}
    			else
    			{
    				boardname = chooser.getSelectedFile().getName();
    			}
    			abspath = chooser.getSelectedFile().getAbsolutePath().substring(0, chooser.getSelectedFile().getAbsolutePath().indexOf(boardname));
    	  		File xmlfile = new File(abspath.concat(boardname).concat(".board"));
    	  		try{xmlfile.createNewFile();} catch (IOException ioe) {ioe.printStackTrace();}
    			
    	  		xstream = new XStream();
    			saveBoard();
    		}
		}
    }

	public void onPlayerEnded(SoundPlayer soundPlayer) 
	{
		currentlyPlaying.removeByValue(soundPlayer);
	}

}