package com.trial;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {

    private Color titleColor;
    private int cornerRadius;

    public CustomButton(String title, Color backgroundColor, Color titleColor, int cornerRadius) {
        super(title);
        this.titleColor = titleColor;
        this.cornerRadius = cornerRadius;
        setBackground(backgroundColor);

        // Add MouseListener for hover and click effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(backgroundColor.brighter()); // Set hover color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(backgroundColor); // Restore original color on exit
            }
        });

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);

    }


    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2d.setColor(titleColor);
        super.paintComponent(g2d);

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width += cornerRadius * 2;
        size.height += cornerRadius * 2;
        return size;
    }
}
