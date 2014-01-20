package com.dberm22.DBoard.Settings;

import java.awt.Color;
import java.util.ArrayList;
 
public class Settings
{
	public ArrayList<ArrayList<ArrayList<SoundFile>>> mediafiles = new ArrayList<ArrayList<ArrayList<SoundFile>>>();
	private String backgroundImage = null;
	private Color backgroundColor = new Color(200,200,200);
	private Float buttonAlpha = 1.0f;
	private Color buttonColor = null;
	private String position = "Center";
	private boolean	singularAudio = false;
	
    public Settings(){}
    
    public Settings(ArrayList<ArrayList<ArrayList<SoundFile>>> mediafiles)
    {
    	if(mediafiles != null) this.mediafiles = mediafiles;
    }
    
    
    public Object getBackground()
    {
    	if("None".equals(position))
    	{
    		return this.backgroundColor;
    	}
    	else
    	{
    		return this.backgroundImage;
    	}
    }
    
    public void setBackgroundImage(String image)
    {
    	this.backgroundImage = image;
    }
    
    public String getBackgroundImage()
    {
    	if(this.backgroundImage != null) return this.backgroundImage;
    	else return "";
    }
    
    public void setBackgroundImagePosition(String pos)
    {
    	this.position = pos;
    }
    
    public String getBackgroundImagePosition()
    {
    	return this.position;
    }
    
    public void setBackgroundColor(Color color)
    {
    	this.backgroundColor = color;
    }
    
    public Color getBackgroundColor()
    {
    	return this.backgroundColor;
    }
    
    public void setButtonAlpha(float alpha)
    {
    	this.buttonAlpha = alpha;
    }
    
    public float getButtonAlpha()
    {
    	return this.buttonAlpha;
    }
    
    public void setButtonColor(Color color)
    {
    	this.buttonColor = color;
    }
    
    public Color getButtonColor()
    {
    	return this.buttonColor;
    }
    
    public void setMediaFiles(ArrayList<ArrayList<ArrayList<SoundFile>>> mediafiles)
    {
    	this.mediafiles = mediafiles;
    }
    
    public ArrayList<ArrayList<ArrayList<SoundFile>>> getMediaFiles()
    {
    	return this.mediafiles;
    }
    
    public void setSingularAudioSetting(boolean setSingular)
    {
    	this.singularAudio = setSingular;
    }
    
    public boolean getSingularAudioSetting()
    {
    	return this.singularAudio;
    }

   
}