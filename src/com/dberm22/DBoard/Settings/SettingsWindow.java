package com.dberm22.DBoard.Settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.dberm22.DBoard.DBoard;
import com.dberm22.utils.ExtendedArrayList;

public class SettingsWindow extends JFrame
{
	private static final long serialVersionUID = -8142977027588217381L;
	
	private Settings originalSettings;
	private Settings newSettings;
	private JList<String> settingsList;
	private JPanel applyPanel = new JPanel( new FlowLayout(FlowLayout.RIGHT));
	private JPanel contentPanel = new JPanel(new BorderLayout());
	private JPanel generalSettingsPanel = new JPanel(new BorderLayout());
	private JPanel backgroundSettingsPanel = new JPanel(new BorderLayout());
	private JPanel buttonSettingsPanel = new JPanel(new BorderLayout());
	private JButton resetButtons = new JButton("Reset Button Settings");
	private JButton applyButton = new JButton("Apply");
	private JButton applyAndCloseButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
    
    public SettingsWindow(final DBoard dboard, Settings settings)
    {
    	this.originalSettings = settings;
    	this.newSettings = settings;
    	
    	setLayout(new BorderLayout());
    	
    	//General Settings Panel
		JPanel generalSettingsList = new JPanel(new FlowLayout(FlowLayout.LEFT));
    		final JCheckBox singularAudioSetting = new JCheckBox("Allow only one audio file to be played at a time",originalSettings.getSingularAudioSetting());
    			singularAudioSetting.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) 
    			{ 
    				newSettings.setSingularAudioSetting(singularAudioSetting.isSelected());
    			}});
    			generalSettingsList.add(singularAudioSetting);
        JScrollPane generalSettingsScrollPane = new JScrollPane(generalSettingsList);
        	generalSettingsScrollPane.setBorder(BorderFactory.createEmptyBorder());
    	generalSettingsPanel.add(generalSettingsScrollPane);
    	
    	//Background Settings Panel
    	Box backgroundImageChooserPanel = Box.createHorizontalBox();
    		final JTextField backgroundImageChooserTextField = new JTextField(originalSettings.getBackgroundImage(), 40);
    		backgroundImageChooserTextField.getDocument().addDocumentListener(new DocumentListener() {
				@Override public void changedUpdate(DocumentEvent arg0) {warn();}
				@Override public void insertUpdate(DocumentEvent arg0) {warn();}
				@Override public void removeUpdate(DocumentEvent arg0) { warn();}
				private void warn(){newSettings.setBackgroundImage(backgroundImageChooserTextField.getText());}
			});
    		JButton backgroundImageChooserButton = new JButton("Select Image");
    			backgroundImageChooserButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) 
    			{ 
    				JFileChooser chooser = new JFileChooser("Choose Image");
    				int returnVal = chooser.showOpenDialog(SettingsWindow.this);
    				if(returnVal == JFileChooser.APPROVE_OPTION) {
    					backgroundImageChooserTextField.setText(chooser.getSelectedFile().toString());
    				}
    				
    			}});
    		String[] posStrings = { "Stretch", "Center", "Fit", "Fill", "None"};
    		JComboBox<String> backgroundImagePositionComboBox = new JComboBox<String>(posStrings);
    			backgroundImagePositionComboBox.setSelectedIndex((new ExtendedArrayList<String>(posStrings)).indexOf(originalSettings.getBackgroundImagePosition()));
    			backgroundImagePositionComboBox.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) 
    			{ 
    				@SuppressWarnings("unchecked")
					JComboBox<String> cb = (JComboBox<String>)e.getSource();
    		        newSettings.setBackgroundImagePosition((String)cb.getSelectedItem());
    				
    			}});

    		backgroundImageChooserPanel.add(backgroundImageChooserTextField);
    		backgroundImageChooserPanel.add(backgroundImageChooserButton);
    		backgroundImageChooserPanel.add(backgroundImagePositionComboBox);
    	backgroundSettingsPanel.add(backgroundImageChooserPanel,BorderLayout.PAGE_START);
    			
    	final JColorChooser backgroundColorChooser = new JColorChooser();
    	backgroundColorChooser.setColor(originalSettings.getBackgroundColor());
    	backgroundColorChooser.getSelectionModel().addChangeListener(new ChangeListener(){
    		public void stateChanged(ChangeEvent e) 
    		{
    			newSettings.setBackgroundColor(backgroundColorChooser.getColor());
    		}
    	});
    	backgroundSettingsPanel.add(backgroundColorChooser);
    	contentPanel.setBorder(BorderFactory.createTitledBorder("Background Settings"));
    	contentPanel.add(backgroundSettingsPanel);
    	add(contentPanel,BorderLayout.CENTER);
    	
    	
    	//Button Settings Panel
    	Box buttonTransparencyBox = Box.createHorizontalBox();
    		buttonTransparencyBox.add(new JLabel("Alpha:  "));
	    	final JSlider buttonTransparencySlider = new JSlider(0, 255, 255);
	    	buttonTransparencySlider.setValue(Math.round(255*originalSettings.getButtonAlpha()));
	    	buttonTransparencySlider.addChangeListener(new ChangeListener() {
	            public void stateChanged(ChangeEvent e) 
	            {
	            	newSettings.setButtonAlpha(((float)buttonTransparencySlider.getValue())/255f);
	            }
	        });
	    	buttonTransparencyBox.add(buttonTransparencySlider);
    	buttonSettingsPanel.add(buttonTransparencyBox,BorderLayout.PAGE_START);

    	final JColorChooser buttonColorChooser = new JColorChooser();
    	buttonColorChooser.setColor(originalSettings.getButtonColor());
    	buttonColorChooser.getSelectionModel().addChangeListener(new ChangeListener(){
    		public void stateChanged(ChangeEvent e) 
    		{
    			newSettings.setButtonColor(buttonColorChooser.getColor());
    		}
    	});
    	buttonSettingsPanel.add(buttonColorChooser);
    	
    	resetButtons.addActionListener(new ActionListener() { 
    		public void actionPerformed(ActionEvent e)
    		{
    			newSettings.setButtonAlpha(1.0f);
    			newSettings.setButtonColor(null);
    			
    			buttonTransparencySlider.setValue(255);
    			buttonColorChooser.setColor(new Color(221,231,241,255));
    		}
    	});
    	buttonSettingsPanel.add(resetButtons,BorderLayout.PAGE_END);
    	
    	
    	// Sidebar Settings Selector

    	DefaultListModel<String> settingsListModel = new DefaultListModel<String>();
    	settingsListModel.addElement("General      ");
    	settingsListModel.addElement("Background     ");
    	settingsListModel.addElement("Buttons      ");
    	settingsList = new JList<String>(settingsListModel);
    	settingsList.addListSelectionListener( new SettingsListSelectionHandler());
    	settingsList.setSelectedIndex(0);
    	settingsList.setBorder(BorderFactory.createTitledBorder("Categories"));
    	add(settingsList, BorderLayout.LINE_START);
    	
    	
    	// Apply, Save, and Close Buttons
    	applyPanel.add(Box.createHorizontalGlue() );
    	applyButton.addActionListener(new ActionListener() { 
    		public void actionPerformed(ActionEvent e)
    		{
    			dboard.setSettings(newSettings);
    		}
    	});
    	applyPanel.add(applyButton);
    	applyAndCloseButton.addActionListener(new ActionListener() { 
    		public void actionPerformed(ActionEvent e)
    		{
    			dboard.setSettings(newSettings); 
    			dispose();
    		}
    	});
    	applyPanel.add(applyAndCloseButton);
    	cancelButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			dispose();
    		}
    	});
    	applyPanel.add(cancelButton);
    	add(applyPanel,BorderLayout.PAGE_END);
    	
    	//Finish Creating Window
    	setSize( new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width/2,Toolkit.getDefaultToolkit().getScreenSize().height/2));
		setPreferredSize(  new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width/2,Toolkit.getDefaultToolkit().getScreenSize().height/2));
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	pack();
    	setVisible(true);
    	setAlwaysOnTop(true);
    	setLocationRelativeTo(null);
    }
    
    class SettingsListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) 
        {
        	contentPanel.removeAll();
        	if("General".equals(settingsList.getModel().getElementAt(settingsList.getSelectedIndex()).trim()))
            {
        		contentPanel.setBorder(BorderFactory.createTitledBorder("General Settings"));
            	contentPanel.add(generalSettingsPanel);
            }
        	else if("Background".equals(settingsList.getModel().getElementAt(settingsList.getSelectedIndex()).trim()))
            {
        		contentPanel.setBorder(BorderFactory.createTitledBorder("Background Settings"));
            	contentPanel.add(backgroundSettingsPanel);
            }
        	else if("Buttons".equals(settingsList.getModel().getElementAt(settingsList.getSelectedIndex()).trim()))
            {
            	contentPanel.setBorder(BorderFactory.createTitledBorder("Button Settings"));
            	contentPanel.add(buttonSettingsPanel);
            }
            
            validate();
            repaint();
        }
    }
    
}
