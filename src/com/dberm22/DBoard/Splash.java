package com.dberm22.DBoard;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame; //imports JFrame library
import javax.swing.JButton; //imports JButton library
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dberm22.utils.TransparentJButton;
import com.thoughtworks.xstream.XStream;

import java.net.URLDecoder;
 
@SuppressWarnings("serial")
public class Splash extends JFrame
{
	String abspath; 
	XStream xstream;
    JPanel soundboard=new JPanel();
    Boolean changesmade = false;
 
    public Splash()
    {
    	
    	//Create Splash Screen Window
    	try
    	{
    		abspath = Splash.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    		abspath = URLDecoder.decode(abspath, "UTF-8");
        	abspath = abspath.substring(0, abspath.lastIndexOf("bin/"));
    	} catch (Exception e1) {e1.printStackTrace();}
    	
    	final JFrame splashscreen = new JFrame();
    	splashscreen.setLayout(null);
    	splashscreen.setSize(400, 400);
    	splashscreen.setUndecorated(true);
    	splashscreen.setBackground(new Color(0,0,0,0));
    	splashscreen.setContentPane(new JLabel("", new ImageIcon(abspath.concat("res/splash.png")), SwingConstants.CENTER));
        
       // Add minimize and close buttons
        JPanel menupanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout( menupanel , BoxLayout.LINE_AXIS );
		menupanel.setLayout( boxLayout  );
		menupanel.setBackground(new Color(0,0,0,0));
		menupanel.add( Box.createHorizontalGlue() );
		
		try
		{
			JButton min_button = new JButton(new ImageIcon(ImageIO.read(new File(abspath.concat("res/minimize.png")))));
			min_button.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { splashscreen.setState(Frame.ICONIFIED); }});
			min_button.setBorder(BorderFactory.createEmptyBorder());
			min_button.setContentAreaFilled(false);
			menupanel.add( min_button);
			
			JButton close_button = new JButton(new ImageIcon(ImageIO.read(new File(abspath.concat("res/close.png")))));
			close_button.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) 
			{ 
				splashscreen.dispose(); 
				System.exit(0);
			}});
			close_button.setBorder(BorderFactory.createEmptyBorder());
			close_button.setContentAreaFilled(false);
			menupanel.add( close_button);
		}
		catch(Exception e){}
		splashscreen.add( menupanel);
		menupanel.setBounds(0, 0, 400, 13);
        
		
		//Add content to Splash Screen		
		TransparentJButton newboard = new TransparentJButton("Create New Board", 0.8f);
		newboard.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) 
		{ 
			new DBoard(5,5);
			splashscreen.dispose();
		}});
		splashscreen.add(newboard);
		newboard.setBounds(170, 100, 200, 100);
		
		//Add content to Splash Screen		
		JLabel or = new JLabel("OR", SwingConstants.CENTER);
		splashscreen.add(or);
		or.setBounds(170, 200, 200, 64);
		
		TransparentJButton loadboard = new TransparentJButton("Load Existing Board", 0.8f);
		loadboard.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) 
		{ 
			JFileChooser chooser = new JFileChooser("Open...");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Board Files Only","board");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(new File(abspath.concat("boards/")));
			int returnVal = chooser.showOpenDialog(Splash.this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				new DBoard(chooser.getSelectedFile());
				splashscreen.dispose();
				splashscreen.dispatchEvent(new WindowEvent(splashscreen, WindowEvent.WINDOW_CLOSING)); 
			}
			
		}});
		splashscreen.add(loadboard);
		loadboard.setBounds(170, 264, 200, 100);
			
		//Final Stuff
		splashscreen.setIconImage((new ImageIcon(abspath.concat("\\res\\dboard.png"))).getImage());
		splashscreen.setLocationRelativeTo(null);
		splashscreen.toFront();
		splashscreen.setVisible(true);
    }
    
    public static void main(String[] args) 
    {
    	new Splash();
    }

   
}