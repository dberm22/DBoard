package com.dberm22.utils;

import com.thirdparty.utils.BlendComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import javax.swing.JButton;
 
public class TransparentJButton extends JButton
{
	private static final long serialVersionUID = -2108498799027795095L;

	private float alpha = 1.0f;
	private Color backgroundColor = new Color(221,231,241,255);
	private Image image = null;
	
	public TransparentJButton()
	{
		super();
		setAlpha(alpha);
	}
	
	public TransparentJButton(String text)
	{
		super(text);
		setAlpha(alpha);
	}
	
	public TransparentJButton(Image image)
	{
		super();
		this.image = image;
		setAlpha(alpha);
	}
	
	public TransparentJButton(float alpha)
	{
		super();
		setAlpha(alpha);
	}
	
	public TransparentJButton(String text, float alpha)
	{
		super(text);
		setAlpha(alpha);
	}
	
	public TransparentJButton(Image image, float alpha)
	{
		super();
		this.image = image;
		setAlpha(alpha);
	}

	@Override
	public void paint(Graphics g) 
	{   
		
		setOpaque(false);
	    Graphics2D g2 = (Graphics2D) g.create();
	    
	    Color color;
	    
	    if(backgroundColor != null)
		{
	    	color = new Color(backgroundColor.getRed(),backgroundColor.getGreen(),backgroundColor.getBlue(),Integer.valueOf((int) (255*this.alpha)));
		}
	    else
	    {
	    	color = new Color(221,231,241,Integer.valueOf((int) (255*this.alpha)));
	    }
		
		g2.setColor(new Color(0,0,0,0));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setPaint(new GradientPaint(
                new Point(0, 0), 
                color, 
                new Point(0, getHeight()/4), 
                new Color(255,255,255,Integer.valueOf((int) (255*this.alpha)))));
	    g2.fillRect(0, 0, getWidth(), getHeight()/4);
	    g2.setPaint(new GradientPaint(
	    		new Point(0, getHeight()/4),
	    		new Color(255,255,255,Integer.valueOf((int) (255*this.alpha))), 
                new Point(0, getHeight()), 
                new Color(Math.max(0,color.getRed()-15),Math.max(0,color.getGreen()-15),Math.max(0,color.getBlue()-15),color.getAlpha())));
	    g2.fillRect(0, getHeight()/4, getWidth(), getHeight());
	    
	    g2.setComposite(BlendComposite.getInstance(BlendComposite.BlendingMode.SUBTRACT, this.alpha));
	    if (this.image != null)
	    {
	    	g2.drawImage(this.image,(this.getWidth()-this.image.getWidth(this))/2,(this.getHeight()-this.image.getHeight(this))/2,null);
	    }
	    
	    super.paint(g2);
	    g2.dispose();
		
	}
	
	@Override
	public void setBackground(Color bg)
	{
		this.backgroundColor = bg;
		super.setBackground(bg);
	}
	
	public void setAlpha(float alpha) {
	    this.alpha = alpha;
	    repaint();
	}
	
	public void setAlpha(int alpha)
	{
		setAlpha(((float) alpha)/255f);
	}

	public float getAlpha(){
	    return this.alpha;
	}

   
}