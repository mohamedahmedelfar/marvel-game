package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StartView extends JFrame {
	
	private JLabel startPic;
	private JTextField firstPlayerName;
	private JTextField secondPlayerName;
	private JButton startGame;
	private Font myFont = new Font("Palatino",Font.ITALIC,20);
	
	public StartView() throws IOException {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		int screenWidth = 1600;
		int screenHeight = 860;
		int frameWidth = (int) (screenWidth*0.9);
		int frameHeight = (int) (screenHeight*0.9);
		this.setBounds(screenWidth/2,screenHeight/2,screenWidth,screenHeight);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		
		startPic = new JLabel();
		startPic.setBounds(0,0,frameWidth,(int) (frameHeight*0.8));
	    BufferedImage img = null;
		img = ImageIO.read(new File("images/intro-pic.png"));
		Image dimg = img.getScaledInstance(startPic.getWidth(), startPic.getHeight(),Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(dimg);
		startPic.setIcon(icon);
		startPic.setOpaque(true);
		this.add(startPic);

		firstPlayerName = new JTextField();
		firstPlayerName.setBounds((int) (frameWidth*0.335), (int)(frameHeight*0.8), (int)(frameWidth*0.15), (int)(frameHeight*0.05));
		firstPlayerName.setFont(myFont);
		firstPlayerName.setHorizontalAlignment(JTextField.CENTER);
		this.add(firstPlayerName);

		secondPlayerName = new JTextField();
		secondPlayerName.setBounds((int) (frameWidth*0.515), (int)(frameHeight*0.8), (int)(frameWidth*0.15), (int)(frameHeight*0.05));
		secondPlayerName.setFont(myFont);
		secondPlayerName.setHorizontalAlignment(JTextField.CENTER);
		this.add(secondPlayerName);

		startGame = new JButton("Start Game");
		startGame.setBounds((int)(frameWidth*0.45), (int)(frameHeight*0.87), (int)(frameWidth*0.1), (int)(frameHeight*0.05));
		startGame.setFont(myFont);
		startGame.setForeground(Color.white);
		startGame.setBackground(Color.black);
		startGame.setFocusable(false);
		startGame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(startGame);

		this.setVisible(true);
		this.revalidate();
		this.repaint();
	}
	
	public static void main(String[] args) throws IOException {
		new StartView();
	}

	public JLabel getStartPic() {
		return startPic;
	}

	public JTextField getFirstPlayerName() {
		return firstPlayerName;
	}

	public JTextField getSecondPlayerName() {
		return secondPlayerName;
	}

	public JButton getStartGame() {
		return startGame;
	}

	public Font getMyFont() {
		return myFont;
	}

	

}
