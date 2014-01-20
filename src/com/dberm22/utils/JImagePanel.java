package com.dberm22.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class JImagePanel extends JPanel {

	private static final long serialVersionUID = 6841876236948317038L;
	private Image img = null;
	private Position position = Position.CENTER;
  
  public enum Position{
	  STRETCH,
	  CENTER,
	  FIT,
	  FILL,
	  NONE;
  }
  
  public JImagePanel() {
	  }
  
  public JImagePanel(String img) {
	  setBackgroundImage(img);
  }

  public JImagePanel(Image img) {

	  setBackgroundImage(img);
  }

  @Override
  public void paintComponent(Graphics g)
  {
	super.paintComponent(g);
	
	Graphics2D g2 = (Graphics2D) g;
	
	g2.setColor(getBackground());
	g2.fillRect(0, 0, getWidth(), getHeight());
	
	if (this.position.equals(Position.STRETCH))
	{ 
		if(this.img != null) g2.drawImage(img, 0, 0, getWidth(), getHeight(), null); 
	}
	else if (this.position.equals(Position.FILL) || this.position.equals(Position.FIT))
	{ 
		if(this.img != null)
		{
			
			 double scaleFactor = getScaleFactor(new Dimension(img.getWidth(null), img.getHeight(null)), getSize());
			 int scaleWidth = (int) Math.round(img.getWidth(null) * scaleFactor);
			 int scaleHeight = (int) Math.round(img.getHeight(null) * scaleFactor);
			 
			 //Image img_scaled = img.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
			g2.drawImage(scaleImage(img, scaleWidth, scaleHeight, getBackground()), (getWidth() - scaleWidth)/2, (getHeight() - scaleHeight)/2, scaleWidth, scaleHeight, null); 
		}
	}
	else if (this.position.equals(Position.CENTER)) { if(this.img != null) g2.drawImage(img, (getWidth() - img.getWidth(null))/2, (getHeight() - img.getHeight(null))/2, null); }
  }
  
  public void setBackgroundImage(String img) 
  {
	  setBackgroundImage(new ImageIcon(img).getImage());
  }

  public void setBackgroundImage(Image img)
  {
	    this.img = img;
	    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    
	    revalidate();
	    repaint();
  }
  
  public static double getScaleFactor(int iMasterSize, int iTargetSize) {

	    double dScale = 1;
	    if (iMasterSize > iTargetSize) {

	        dScale = (double) iTargetSize / (double) iMasterSize;

	    } else {

	        dScale = (double) iTargetSize / (double) iMasterSize;

	    }

	    return dScale;

	}

	public double getScaleFactor(Dimension original, Dimension targetSize) {

	    double dScale = 1d;

	    if (original != null && targetSize != null) {

	        double dScaleWidth = getScaleFactor(original.width, targetSize.width);
	        double dScaleHeight = getScaleFactor(original.height, targetSize.height);

	        if (this.position.equals(Position.FIT)) dScale = Math.min(dScaleHeight, dScaleWidth);
	        else if(this.position.equals(Position.FILL)) dScale = Math.max(dScaleHeight, dScaleWidth);

	    }

	    return dScale;

	}
	
	public BufferedImage scaleImage(Image img, int width, int height, Color background) {

	    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = newImage.createGraphics();
	    try {
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        g.setBackground(background);
	        g.clearRect(0, 0, width, height);
	        g.drawImage(img, 0, 0, width, height, null);
	    } finally {
	        g.dispose();
	    }
	    return newImage;
	}
  
  public void setBackgroundImagePosition(String pos)
  {
	  if("Stretch".equals(pos)) setBackgroundImagePosition(Position.STRETCH);
	  else if("Center".equals(pos))  setBackgroundImagePosition(Position.CENTER);
	  else if("Fit".equals(pos)) setBackgroundImagePosition(Position.FIT);
	  else if("Fill".equals(pos))  setBackgroundImagePosition(Position.FILL);
	  else if("None".equals(pos)) setBackgroundImagePosition(Position.NONE);
  }
  public void setBackgroundImagePosition(Position pos)
  {
	  this.position = pos;  
	  revalidate();
	  repaint();
  }
}
