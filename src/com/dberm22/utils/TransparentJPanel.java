package com.dberm22.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class TransparentJPanel extends JPanel {

	private static final long serialVersionUID = 5407571884817552283L;

	public void paintComponent(Graphics g)
    {
    	setOpaque(false);
        g.setColor(new Color(0,0,0,0));
        Rectangle r = g.getClipBounds();
        g.fillRect(r.x, r.y, r.width, r.height);
        super.paintComponent(g);
    }
}