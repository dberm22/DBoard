package com.dberm22.DBoard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLDecoder;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
 
@SuppressWarnings("serial")
public class About extends JFrame
{
 
    public About()
    {
    	setLayout(new BorderLayout());
    	
    	setTitle("About");
    	
    	
    	try
    	{
    		String abspath = Splash.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    		abspath = URLDecoder.decode(abspath, "UTF-8");
        	abspath = abspath.substring(0, abspath.lastIndexOf("bin/"));
        	add(new JLabel(new ImageIcon(abspath.concat("res/icon.png"))), BorderLayout.LINE_START);
    	} catch (Exception e1) {e1.printStackTrace();}
    	
    	
    	JTextArea textarea = new JTextArea(
    	"    DBoard    \n" +
    	"\n" +
    	"    An open-source soundboard java applet         \n" +
    	"    written by David Berman, dberm22@gmail.com    \n" +
    	"\n" +
    	"    v1.0.0, released Dec 2013    \n"
    	);
    	add(textarea, BorderLayout.CENTER);
    	
    	JButton closebtn = new JButton("Close");
		closebtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { dispose();}});
		add(closebtn,BorderLayout.PAGE_END);
    	
    	//Show and Center
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	pack();
    	setVisible(true); //makes frame visible
    	setAlwaysOnTop(true);
    	setLocationRelativeTo(null);
    }
}