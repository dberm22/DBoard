package com.dberm22.DBoard;

import com.dberm22.utils.MediaPlayer;

public class SoundPlayer extends MediaPlayer
{
	private DBoard board;
	
	
	public SoundPlayer()
    {
		super();
    }
    
    public SoundPlayer(String filename)
    {
    	super(filename);
    }
    
	
	public void setAttachedBoard(DBoard board)
	{
		this.board = board;
	}
	
	@Override
	public void onStop()
	{
		board.onPlayerEnded(this);
	}
	
	

}
