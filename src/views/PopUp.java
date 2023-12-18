package views;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class PopUp extends JFrame{
	
	public PopUp(String message) {
		
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = 1600;
		int screenHeight = 860;
		//int frameWidth = (int) (screenWidth*0.9);
		//int frameHeight = (int) (screenHeight*0.9);
		this.setBounds(screenWidth/2,screenHeight/2,800,400);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
	
		Font myBigFont = new Font("Palatino",Font.BOLD,20);
		
		JTextArea text = new JTextArea(message);
		text.setBounds(100,150,800,50);
		
		text.setBackground(null);
		text.setFont(myBigFont);
		
		text.setEditable(false);
		text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		this.add(text);
		this.setVisible(true);
		this.revalidate();
		this.repaint();
	}
	
	

	

	
}
