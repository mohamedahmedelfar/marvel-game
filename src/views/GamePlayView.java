package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.MutableComboBoxModel;
import javax.swing.border.Border;

import engine.GameListener;
import engine.Player;
import engine.PriorityQueue;
import model.abilities.Ability;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.Champion;
import model.world.Cover;
import model.world.Damageable;

public class GamePlayView extends JFrame implements GameListener{
	
	private JPanel boardPanel;
	private Player firstPlayer;
	private Player secondPlayer;
	private JLabel[][] boardLocations;
	
	private ArrayList<Cover> allCovers;
	private ArrayList<JTextArea> allCoverHP;
	
	private JTextArea firstPlayerUSA;
	private JTextArea secondPlayerUSA;
		
	private ArrayList<Champion> firstPlayerTeamBeforeStart;
	private ArrayList<Champion> secondPlayerTeamBeforeStart;
	
	private ArrayList<JTextArea> firstPlayerTeamInfo;
	private ArrayList<JTextArea> secondPlayerTeamInfo;
	
	private ArrayList<JTextArea> firstPlayerTeamAbility;
	private ArrayList<JTextArea> secondPlayerTeamAbility;
	
	private Font smallFont = new Font("Palatino",Font.ITALIC,8);
	private Font myFont = new Font("Palatino",Font.ITALIC,10);
	private Font myBigFont = new Font("Palatino",Font.BOLD,30);
	private Font myUpMedFont = new Font("Palatino",Font.BOLD,15);
	private Font myTwentyFont = new Font("Palatino",Font.BOLD,20);
	private Font myMedFont = new Font("Palatino",Font.BOLD,12);
	
	private JButton moveRight;
	private JButton moveLeft;
	private JButton moveUp;
	private JButton moveDown;

	private JButton attackRight;
	private JButton attackLeft;
	private JButton attackUp;
	private JButton attackDown;
	
	private JButton endTurn;
	private JButton ULAButton;
	
	private JButton currentChampName;
	private JTextArea turnOrderInfo; 
	
	private JComboBox<String> castHEL;
	private JComboBox<String> castDMG;
	private JComboBox<String> castCC;

	private JTextArea directionArea;
	private JTextArea directionTitle;
	
	private JTextArea singletargetArea;
	private JTextArea targetX;
	private JTextArea targetY;
	
	Border myBorder = BorderFactory.createLineBorder(Color.black, 1);

	
	public GamePlayView(Player p1,Player p2) throws IOException {
		firstPlayer=p1;
		secondPlayer=p2;
		boardLocations = new JLabel[5][5];
		boardPanel = new JPanel();
		allCoverHP = new ArrayList<JTextArea>();
		allCovers = new ArrayList<Cover>();
		firstPlayerTeamInfo = new ArrayList<JTextArea>();
		secondPlayerTeamInfo = new ArrayList<JTextArea>();
		firstPlayerTeamAbility = new ArrayList<JTextArea>();
		secondPlayerTeamAbility = new ArrayList<JTextArea>();
		firstPlayerTeamBeforeStart = new ArrayList<Champion>();
		secondPlayerTeamBeforeStart = new ArrayList<Champion>();
		turnOrderInfo = new JTextArea();
		
		for(Champion c : firstPlayer.getTeam())
			firstPlayerTeamBeforeStart.add(c);
		
		for(Champion c : secondPlayer.getTeam())
			secondPlayerTeamBeforeStart.add(c);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = 1600;
		int screenHeight = 860;
		int frameWidth = (int) (screenWidth*0.9);
		int frameHeight = (int) (screenHeight*0.9);
		this.setBounds(screenWidth/2,screenHeight/2,screenWidth,screenHeight);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.getContentPane().setBackground(new Color(247, 228, 253));
		
		boardPanel.setBounds((int)(frameWidth*0.005),(int)(frameHeight*0.01),(int) (frameWidth*0.5),(int) (frameHeight*0.8));
		//boardPanel.setBackground(Color.gray);
		boardPanel.setOpaque(true);
		boardPanel.setLayout(new GridLayout(5,5,5,5));
		boardPanel.setBackground(null);
		
		Border border = BorderFactory.createLineBorder(Color.black, 1);
		for(int i=4 ; i>=0 ; i--) {
			for(int j=4 ; j>=0 ; j--) {
				JLabel label = new JLabel();
				label.setBackground(Color.white);
				label.setOpaque(true);
				label.setBorder(border);
				boardLocations[i][4-j] = label;
				boardPanel.add(label);
			}
		}
		this.add(boardPanel);

		//JTextArea firstName = new JTextArea(firstPlayer.getName()+"'s Team");
		JTextArea firstName = new JTextArea(firstPlayer.getName()+"'s Team");
		firstName.setFont(myTwentyFont);
		firstName.setForeground(Color.blue);
		firstName.setBounds((int)(frameWidth*0.52), (int)(frameHeight*0.01),(int) (frameWidth*0.16),(int) (frameHeight*0.04));
		firstName.setBackground(null);
		firstName.setEditable(false);
		this.add(firstName);

		firstPlayerUSA = new JTextArea("Leader Ability Not used yet!");
		firstPlayerUSA.setFont(myTwentyFont);
		firstPlayerUSA.setBounds((int)(frameWidth*0.68), (int)(frameHeight*0.01),(int) (frameWidth*0.35),(int) (frameHeight*0.04));
		firstPlayerUSA.setForeground(Color.blue);
		firstPlayerUSA.setEditable(false);
		firstPlayerUSA.setBackground(null);
		this.add(firstPlayerUSA);
		
		JPanel firstPlayerTeamPanel = new JPanel();
		firstPlayerTeamPanel.setBounds((int)(frameWidth*0.52), (int)(frameHeight*0.05),(int) (frameWidth*0.54),(int) (frameHeight*0.43));
		firstPlayerTeamPanel.setLayout(new GridLayout(1,6,5,2));
		firstPlayerTeamPanel.setBackground(null);
		
		for(int i = 0 ; i<3 ; i++) {
			JTextArea championInfo = new JTextArea();
			championInfo.setEditable(false);
			championInfo.setBackground(null);
			championInfo.setOpaque(true);
			championInfo.setText(firstPlayer.getTeam().get(i).printChampionInfo());

			if(firstPlayer.getTeam().get(i)==firstPlayer.getLeader())
				championInfo.setText(firstPlayer.getTeam().get(i).printChampionInfo()+"\n"+"TEAM LEADER");
			
			championInfo.setFont(myMedFont);
			firstPlayerTeamPanel.add(championInfo);
			firstPlayerTeamInfo.add(championInfo);
			
			JTextArea abilityAndEffectsInfo = new JTextArea();
			abilityAndEffectsInfo.setEditable(false);
			abilityAndEffectsInfo.setBackground(null);
			abilityAndEffectsInfo.setOpaque(true);
			abilityAndEffectsInfo.setText(firstPlayer.getTeam().get(i).printAbilityInfo());
			abilityAndEffectsInfo.setFont(smallFont);
			firstPlayerTeamPanel.add(abilityAndEffectsInfo);
			firstPlayerTeamAbility.add(abilityAndEffectsInfo);
			
		}
		this.add(firstPlayerTeamPanel);

		JTextArea secondName = new JTextArea(secondPlayer.getName()+"'s Team");
		secondName.setFont(myTwentyFont);
		secondName.setForeground(Color.blue);
		secondName.setBounds((int)(frameWidth*0.52), (int)(frameHeight*0.485),(int) (frameWidth*0.16),(int) (frameHeight*0.04));
		secondName.setBackground(null);
		secondName.setEditable(false);
		this.add(secondName);
		
		secondPlayerUSA = new JTextArea("Leader Ability Not used yet!");
		secondPlayerUSA.setFont(myTwentyFont);
		secondPlayerUSA.setForeground(Color.blue);
		secondPlayerUSA.setBounds((int)(frameWidth*0.68), (int)(frameHeight*0.485),(int) (frameWidth*0.35),(int) (frameHeight*0.04));
		secondPlayerUSA.setEditable(false);
		secondPlayerUSA.setBackground(null);
		this.add(secondPlayerUSA);
		
		JPanel secondPlayerTeamPanel = new JPanel();
		secondPlayerTeamPanel.setBounds((int)(frameWidth*0.52), (int)(frameHeight*0.53),(int) (frameWidth*0.54),(int) (frameHeight*0.43));
		secondPlayerTeamPanel.setLayout(new GridLayout(1,6,5,2));
		secondPlayerTeamPanel.setBackground(null);
		for(int i = 0 ; i<3 ; i++) {
			JTextArea championInfo = new JTextArea();
			championInfo.setEditable(false);
			championInfo.setBackground(null);
			championInfo.setOpaque(true);
			championInfo.setText(secondPlayer.getTeam().get(i).printChampionInfo());
			if(secondPlayer.getTeam().get(i)==secondPlayer.getLeader())
				championInfo.setText(secondPlayer.getTeam().get(i).printChampionInfo()+"\n"+"TEAM LEADER");
			championInfo.setFont(myMedFont);
			secondPlayerTeamPanel.add(championInfo);
			secondPlayerTeamInfo.add(championInfo);
			
			JTextArea abilityAndEffectsInfo = new JTextArea();
			abilityAndEffectsInfo.setEditable(false);
			abilityAndEffectsInfo.setBackground(null);
			abilityAndEffectsInfo.setOpaque(true);
			abilityAndEffectsInfo.setText(secondPlayer.getTeam().get(i).printAbilityInfo());
			abilityAndEffectsInfo.setFont(smallFont);
			secondPlayerTeamPanel.add(abilityAndEffectsInfo);
			secondPlayerTeamAbility.add(abilityAndEffectsInfo);
		}
		this.add(secondPlayerTeamPanel);
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		moveRight = new JButton("Move Right --->");
		moveRight.setBounds((int)(frameWidth*0.005), (int)(frameHeight*0.82),(int) (frameWidth*0.085),(int) (frameHeight*0.04));
		moveRight.setBackground(new java.awt.Color(7, 147, 240));
		moveRight.setForeground(Color.white);
		moveRight.setFocusable(false);
		moveRight.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(moveRight);
		
		moveLeft = new JButton("<--- Move Left");
		moveLeft.setBounds((int)(frameWidth*0.005), (int)(frameHeight*0.87),(int) (frameWidth*0.085),(int) (frameHeight*0.04));
		moveLeft.setBackground(new java.awt.Color(7, 147, 240));
		moveLeft.setForeground(Color.white);
		moveLeft.setFocusable(false);
		moveLeft.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(moveLeft);
		
		moveUp = new JButton("Move UP");
		moveUp.setBounds((int)(frameWidth*0.005), (int)(frameHeight*0.92),(int) (frameWidth*0.085),(int) (frameHeight*0.04));
		moveUp.setBackground(new java.awt.Color(7, 147, 240));
		moveUp.setForeground(Color.white);
		moveUp.setFocusable(false);
		moveUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(moveUp);
		
		moveDown = new JButton("Move Down");
		moveDown.setBounds((int)(frameWidth*0.005), (int)(frameHeight*0.97),(int) (frameWidth*0.085),(int) (frameHeight*0.04));
		moveDown.setBackground(new java.awt.Color(7, 147, 240));
		moveDown.setForeground(Color.white);
		moveDown.setFocusable(false);
		moveDown.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(moveDown);
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		attackRight = new JButton("Attack Right --->");
		attackRight.setBounds((int)(frameWidth*0.1), (int)(frameHeight*0.82),(int) (frameWidth*0.087),(int) (frameHeight*0.04));
		attackRight.setBackground(Color.red);
		attackRight.setForeground(Color.white);
		attackRight.setFocusable(false);
		attackRight.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(attackRight);
		
		attackLeft = new JButton("<--- Attack Left");
		attackLeft.setBounds((int)(frameWidth*0.1), (int)(frameHeight*0.87),(int) (frameWidth*0.087),(int) (frameHeight*0.04));
		attackLeft.setBackground(Color.red);
		attackLeft.setForeground(Color.white);
		attackLeft.setFocusable(false);
		attackLeft.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(attackLeft);
		
		attackUp = new JButton("Attack UP");
		attackUp.setBounds((int)(frameWidth*0.1), (int)(frameHeight*0.92),(int) (frameWidth*0.087),(int) (frameHeight*0.04));
		attackUp.setBackground(Color.red);
		attackUp.setForeground(Color.white);
		attackUp.setFocusable(false);
		attackUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(attackUp);
		
		attackDown = new JButton("Attack Down");
		attackDown.setBounds((int)(frameWidth*0.1), (int)(frameHeight*0.97),(int) (frameWidth*0.087),(int) (frameHeight*0.04));
		attackDown.setBackground(Color.red);
		attackDown.setForeground(Color.white);
		attackDown.setFocusable(false);
		attackDown.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(attackDown);
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
		endTurn = new JButton("End Turn");
		endTurn.setBounds((int)(frameWidth*0.52), (int) (frameHeight*0.96), 200, 37);
		endTurn.setBackground(Color.BLACK);
		endTurn.setForeground(Color.white);
		endTurn.setFont(myBigFont);
		endTurn.setFocusable(false);
		endTurn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(endTurn);
		
		ULAButton = new JButton("Use Leader Ability");
		ULAButton.setBounds((int)(frameWidth*0.66), (int) (frameHeight*0.96), 300, 37);
		ULAButton.setBackground(Color.BLACK);
		ULAButton.setForeground(Color.white);
		ULAButton.setFont(myBigFont);
		ULAButton.setFocusable(false);
		ULAButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(ULAButton);
		
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		currentChampName = new JButton();
		currentChampName.setBounds((int)(frameWidth*0.9), (int) (frameHeight*0.96), 220, 37);
		currentChampName.setBackground(Color.black);
		currentChampName.setFont(new Font("palatino",Font.BOLD,20));
		currentChampName.setForeground(Color.white);
		this.add(currentChampName);

		turnOrderInfo.setBounds( (int)(frameWidth*0.43),(int)(frameHeight*0.82),(int) (frameWidth*0.075),(int)(frameHeight*0.19));
		turnOrderInfo.setBorder(border);
		turnOrderInfo.setFont(myMedFont);
		turnOrderInfo.setEditable(false);
		this.add(turnOrderInfo);

		castHEL = new JComboBox();
		castHEL.setBounds( (int)(frameWidth*0.19),(int)(frameHeight*0.82),(int) (frameWidth*0.075),(int)(frameHeight*0.04));
		castHEL.setBackground(Color.GRAY);
		this.add(castHEL);
		
		castDMG = new JComboBox();
		castDMG.setBounds( (int)(frameWidth*0.268),(int)(frameHeight*0.82),(int) (frameWidth*0.075),(int)(frameHeight*0.04));
		castDMG.setBackground(Color.GRAY);
		this.add(castDMG);
		
		castCC = new JComboBox();
		castCC.setBounds( (int)(frameWidth*0.346),(int)(frameHeight*0.82),(int) (frameWidth*0.075),(int)(frameHeight*0.04));
		castCC.setBackground(Color.GRAY);
		this.add(castCC);
		
		directionTitle = new JTextArea("DIRECTION :");
		directionTitle.setEditable(false);
		directionTitle.setBounds((int)(frameWidth*0.19), (int)(frameHeight*0.92),(int) (frameWidth*0.065),(int) (frameHeight*0.03));
		directionTitle.setFont(myUpMedFont);
		directionTitle.setBackground(null);
		this.add(directionTitle);
		
		directionArea = new JTextArea();
		directionArea.setBounds((int)(frameWidth*0.26), (int)(frameHeight*0.92),(int) (frameWidth*0.05),(int) (frameHeight*0.03));
		directionArea.setFont(myUpMedFont);
		directionArea.setBorder(myBorder);
		this.add(directionArea);
		
		singletargetArea = new JTextArea("SINGLETARGET :");
		singletargetArea.setBounds((int)(frameWidth*0.19), (int)(frameHeight*0.97),(int) (frameWidth*0.091),(int) (frameHeight*0.03));
		singletargetArea.setEditable(false);
		singletargetArea.setFont(myUpMedFont);
		singletargetArea.setBackground(null);
		this.add(singletargetArea);
		
		targetX = new JTextArea();
		targetX.setBounds((int)(frameWidth*0.285), (int)(frameHeight*0.97),(int) (frameWidth*0.012),(int) (frameHeight*0.03));
		targetX.setFont(myUpMedFont);
		targetX.setBorder(myBorder);
		this.add(targetX);
		
		targetY = new JTextArea();
		targetY.setBounds((int)(frameWidth*0.302), (int)(frameHeight*0.97),(int) (frameWidth*0.012),(int) (frameHeight*0.03));
		targetY.setFont(myUpMedFont);
		targetY.setBorder(myBorder);
		this.add(targetY);
		

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//this.setAlwaysOnTop (true);
		this.setVisible(true);
		this.revalidate();
		this.repaint();
		
	}
	
	
	public JButton getCurrNameButton() {
		return currentChampName;
	}
	
	public JButton getMoveRight() {
		return moveRight;
	}

	public JButton getMoveLeft() {
		return moveLeft;
	}

	public JButton getMoveUp() {
		return moveUp;
	}

	public JButton getMoveDown() {
		return moveDown;
	}
	
	public JButton getAttackRight() {
		return attackRight;
	}

	public JButton getAttackLeft() {
		return attackLeft;
	}

	public JButton getAttackUp() {
		return attackUp;
	}

	public JButton getAttackDown() {
		return attackDown;
	}

	public JButton getEndTurn() {
		return endTurn;
	}
	
	public JButton getULA() {
		return ULAButton;
	}
	
	public JComboBox<String> getHELBox(){
		return castHEL;
	}
	
	public JComboBox<String> getDMGBox(){
		return castDMG;
	}
	
	public JComboBox<String> getCCBox(){
		return castCC;
	}
	
	

	public JTextArea getDirectionArea() {
		return directionArea;
	}

	public JTextArea getTargetX() {
		return targetX;
	}

	public JTextArea getTargetY() {
		return targetY;
	}

	public void drawChampion(Champion c) throws IOException {
		JLabel label = boardLocations[(int) c.getLocation().getX()][(int) c.getLocation().getY()];
		BufferedImage img = null;
		img = ImageIO.read(new File("images/"+c.getName()+".png"));
		Image dimg = img.getScaledInstance(120, 100,Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(dimg);
		label.setIcon(icon);
		this.revalidate();
		this.repaint();
	}
	
	public void drawCover(Cover cover ) throws IOException {
		JLabel label = boardLocations[(int) cover.getLocation().getX()][(int) cover.getLocation().getY()];
		label.setLayout(new FlowLayout(0,5, 5));
		BufferedImage img = null;
		img = ImageIO.read(new File("images/cover.png"));
		Image dimg = img.getScaledInstance(120, 90,Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(dimg);
		JTextArea hp = new JTextArea("HP : "+String.valueOf(cover.getCurrentHP()));
		hp.setEditable(false);
		hp.setBackground(null);
		hp.setFont(myMedFont);
		label.add(hp,BorderLayout.NORTH);
		label.setIcon(icon);
		allCoverHP.add(hp);
		allCovers.add(cover);
	    //allCovers.add(cover);
		this.revalidate();
		this.repaint();
	}
	public void clearLabel(Damageable d,Point location) throws IOException {
		JLabel label = boardLocations[(int) location.getX()][(int) location.getY()];
		label.setIcon(null);
		label.setBackground(getForeground());
		if(d instanceof Cover) {
			Cover c = (Cover) d;
			int i = allCovers.indexOf(c);
			allCoverHP.get(i).setText(null);
		}
		if( d instanceof Champion && d.getCurrentHP()==0)
			playSound("sounds/finish-him.wav");
		label.setBackground(Color.white);
		label.setOpaque(true);
		this.revalidate();
		this.repaint();
	}

	public void updateCoverHp(Cover c) {
		int i = allCovers.indexOf(c);
		allCoverHP.get(i).setText("HP : "+c.getCurrentHP());
	}
	
	public void updateChampionInfo(Champion c) {
		if(firstPlayerTeamBeforeStart.contains(c)) {
			int i = firstPlayerTeamBeforeStart.indexOf(c);
			if(c.getCurrentHP() == 0) {
				firstPlayerTeamInfo.get(i).setText("\n"+"\n"+"\n"+"\n"+"     KO");
				firstPlayerTeamInfo.get(i).setForeground(Color.white);
				firstPlayerTeamInfo.get(i).setBackground(Color.BLACK);
				firstPlayerTeamInfo.get(i).setFont(myBigFont);
			}
			else {
				firstPlayerTeamInfo.get(i).setText(c.printChampionInfo());
				if(firstPlayer.getLeader()==c)
					firstPlayerTeamInfo.get(i).setText(firstPlayerTeamInfo.get(i).getText()+"\n"+"TEAM LEADER");
			}
		}
		else {
			int i = secondPlayerTeamBeforeStart.indexOf(c);
			if(c.getCurrentHP()==0) {
				secondPlayerTeamInfo.get(i).setText("\n"+"\n"+"\n"+"\n"+"     KO");
				secondPlayerTeamInfo.get(i).setForeground(Color.white);
				secondPlayerTeamInfo.get(i).setBackground(Color.BLACK);
				secondPlayerTeamInfo.get(i).setFont(myBigFont);
			}
			else {
				secondPlayerTeamInfo.get(i).setText(c.printChampionInfo());
				if(secondPlayer.getLeader()==c)
					secondPlayerTeamInfo.get(i).setText(secondPlayerTeamInfo.get(i).getText()+"\n"+"TEAM LEADER");
			}
			
		}
			
	}

	
	
	@Override
	public void generateCoverDamageSound() {
		playSound("sounds/hit-cover1.wav");
	}
	
	public void generateAttackedSound() {
		playSound("sounds/attack1.wav");
	}
	
	public void updateLeaderAbilityUsed(Champion c) {
		if(c==firstPlayer.getLeader()) { 
			firstPlayerUSA.setText(firstPlayer.getName()+"'s Leader Ability Has Been Used");
			firstPlayerUSA.setForeground(Color.red);
		}
		else {
			secondPlayerUSA.setText(secondPlayer.getName()+"'s Leader Ability Has Been Used");
			secondPlayerUSA.setForeground(Color.red);
		}
	}
	
	public void updateCurrentName(Champion c) {
		currentChampName.setText(c.getName());
	}
	
	public void playSound(String filePath) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void putCurrentHighlight(Point location) {
		JLabel label = boardLocations[(int) location.getX()][(int) location.getY()];
		Border border = BorderFactory.createLineBorder(Color.green, 4);
		label.setBorder(border);
	}
	public void removeCurrentHighlight(Point location) {
		JLabel label = boardLocations[(int) location.getX()][(int) location.getY()];
		Border border = BorderFactory.createLineBorder(Color.black, 1);
		label.setBorder(border);
	}

	public void updateTurnOrderInfo(PriorityQueue q) {
		String s = "   Turn Order"+"\n"+"\n";
		PriorityQueue temp = new PriorityQueue(6);
		while(!q.isEmpty()) {
			Champion c = (Champion) q.remove();
			s+="   "+c.getName()+"\n";
			temp.insert(c);
		}
		while(! temp.isEmpty()) {
			q.insert(temp.remove());
		}
		turnOrderInfo.setText(s);
	}
	
	@Override
	public void updateAbilityInfo(Champion c) {
		int i;
		if(firstPlayerTeamBeforeStart.contains(c)) {
			i = firstPlayerTeamBeforeStart.indexOf(c);
			if(c.getCurrentHP()==0)
				firstPlayerTeamAbility.get(i).setText("");
			else
				firstPlayerTeamAbility.get(i).setText(c.printAbilityInfo());
		}
		else {
			i = secondPlayerTeamBeforeStart.indexOf(c);
			if(c.getCurrentHP()==0)
				secondPlayerTeamAbility.get(i).setText("");
			else
				secondPlayerTeamAbility.get(i).setText(c.printAbilityInfo());
		}
	}
	
	public void updateAbilityComboBox(ArrayList<Ability> abilities) {
		try {
			castHEL.removeAllItems();
		} catch (Exception e) {
			System.out.println("cast HEL");
		}
		try {
			castDMG.removeAllItems();
		} catch (Exception e) {
			System.out.println("cast DMG");
		}
		try {
			castCC.removeAllItems();
		} catch (Exception e) {
			System.out.println("cast CC");
		}

		castHEL.addItem("CAST HEL");		
		castDMG.addItem("CAST DMG");		
		castCC.addItem("CAST CC");		
		
		for(Ability a : abilities) {
			if(a instanceof HealingAbility) 
				castHEL.addItem(a.getCastArea()+"/"+a.getName());
			else if(a instanceof DamagingAbility)
				castDMG.addItem(a.getCastArea()+"/"+a.getName());
			else
				castCC.addItem(a.getCastArea()+"/"+a.getName());
		}
		
	}

	
//	public static void main(String[] args) throws IOException {
//		new GamePlayView();
//	}
}
