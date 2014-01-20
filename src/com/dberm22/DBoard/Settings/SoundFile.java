package com.dberm22.DBoard.Settings;

public class SoundFile {
	
	private String filepath = null;
	private float volume_db = 0.0F;
	
	public SoundFile(){}
	
	public SoundFile(String filepath) 
	{
		this.filepath = filepath;
	}
	
	public SoundFile(String filepath, float decibels) 
	{
		this.filepath = filepath;
		this.volume_db = decibels;
	}
	
	public String getPath()
	{
		return this.filepath;
	}
	
	public void setPath(String path)
	{
		this.filepath = path;
	}
	
	public float getVolume()
	{
		return (float) Math.exp((volume_db/20.0)*Math.log(10.0));
	}
	
	public void setVolume(float volume)
	{
		volume = volume<=0.0F ? 0.0001F : volume;
		setVolumeInDecibels((float)( 20.0*(Math.log(volume)/Math.log(10.0)) ));
	}
	
	public float getVolumeInDecibels()
	{
		return volume_db;
	}
	
	public void setVolumeInDecibels(float decibels)
	{
		this.volume_db = decibels;
	}

}
