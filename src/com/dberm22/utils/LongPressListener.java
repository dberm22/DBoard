package com.dberm22.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class LongPressListener extends MouseAdapter
{
	private long longpresstime = 1000;
	private long mousedowntime;
	public boolean bindRightClickToLongPress = false;
	
	@Override
    public void mousePressed(MouseEvent event)
    {
		mousedowntime = System.currentTimeMillis();
		
    }

	@Override
    public void mouseReleased(MouseEvent event)
    {
		long mouseuptime = System.currentTimeMillis();
		long elapsedtime = mouseuptime - mousedowntime;
		
		if (SwingUtilities.isLeftMouseButton(event))
		{
			if (elapsedtime < longpresstime)
			{
				onClick(event);
			}
			else
			{
				onLongPress(event);
			}
		}
		else if(SwingUtilities.isRightMouseButton(event))
		{
			onRightClick(event);
		}
		else if(SwingUtilities.isMiddleMouseButton(event))
		{
			onMiddleClick(event);
		}
    }
	
	public void onClick(MouseEvent event){}
	public void onRightClick(MouseEvent event){}
	public void onMiddleClick(MouseEvent event){}
	public void onLongPress(MouseEvent event){}
	
	public void setLongPressTime(int millis)
	{
		this.longpresstime = millis;
	}

}
