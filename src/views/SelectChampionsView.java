package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import engine.Player;
import model.world.Champion;

public class SelectChampionsView extends JFrame implements ActionListener{
	
	private Player firstPlayer;
	private Player secondPlayer;
	private ArrayList<Champion> allChampions;
	private ArrayList<JButton> allSelectButtons;
	private ArrayList<JButton> allSetLeader;

	private ArrayList<Champion> alreadySelectedChampions;
	private JPanel allPanels;
	
	private JPanel firstPlayerTeamPanel;
	private JPanel secondPlayerTeamPanel;
	private JButton startFight;
	
	
	private Font myFont = new Font("Palatino",Font.ITALIC,10);
	private Font myBigFont = new Font("Palatino",Font.BOLD,30);
	private Font myMedFont = new Font("Palatino",Font.BOLD,20);

	public SelectChampionsView(Player p1, Player p2 , ArrayList<Champion> list) throws IOException {
	
		firstPlayer = p1;
		secondPlayer = p2;
		allChampions = list;
		allSelectButtons = new ArrayList<JButton>();
		allSetLeader = new ArrayList<JButton>();
		alreadySelectedChampions = new ArrayList<Champion>();
		
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = 1600;
		int screenHeight = 860;
		int frameWidth = (int) (screenWidth*0.9);
		int frameHeight = (int) (screenHeight*0.9);
		this.setBounds(screenWidth/2,screenHeight/2,screenWidth,screenHeight);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		
		allPanels = new JPanel();
		allPanels.setBounds((int)(frameWidth*0.02),(int)(frameHeight*0.05),(int) (frameWidth*0.7),(int) (frameHeight*0.9));
		allPanels.setBackground(Color.gray);
		allPanels.setOpaque(true);
		allPanels.setLayout(new GridLayout(3,5,5,5));
		
		for(Champion c:allChampions) {
			JPanel p = new JPanel();
			p.setBackground(Color.white);
			p.setOpaque(true);
			p.setLayout(new GridBagLayout());
			JTextArea area = new JTextArea(c.toString());
			area.setEditable(false);
			area.setFont(myFont);
			//area.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			p.add(area);
			
			GridBagConstraints gbc = new GridBagConstraints();
			JButton select = new JButton("Select");
			
			gbc.gridx = 0;
			gbc.gridy = 1;
			p.add(select,gbc);
			allSelectButtons.add(select);
			allPanels.add(p);
		}
		
		for(JButton btn : allSelectButtons) {
			btn.setBackground(Color.white);
			btn.addActionListener(this);
		}
		
		JTextArea firstName = new JTextArea(firstPlayer.getName()+"'s Team");
		firstName.setEditable(false);
		firstName.setFont(myBigFont);
		firstName.setBounds((int)(frameWidth*0.8), (int)(frameHeight*0.05),(int) (frameWidth*0.3),(int) (frameHeight*0.05));
		firstName.setBackground(null);
		firstName.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		JTextArea secondName = new JTextArea(secondPlayer.getName()+"'s Team");
		secondName.setEditable(false);
		secondName.setFont(myBigFont);
		secondName.setBounds((int)(frameWidth*0.8), (int)(frameHeight*0.45),(int) (frameWidth*0.3),(int) (frameHeight*0.05));
		secondName.setBackground(null);
		secondName.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		firstPlayerTeamPanel = new JPanel();
		firstPlayerTeamPanel.setBounds((int)(frameWidth*0.73), (int)(frameHeight*0.15),(int) (frameWidth*0.3),(int) (frameHeight*0.25));
		firstPlayerTeamPanel.setOpaque(true);
		firstPlayerTeamPanel.setLayout(new GridLayout(1,3,5,10));
		
		
		secondPlayerTeamPanel = new JPanel();
		secondPlayerTeamPanel.setBounds((int)(frameWidth*0.73), (int)(frameHeight*0.55),(int) (frameWidth*0.3),(int) (frameHeight*0.25));
		secondPlayerTeamPanel.setOpaque(true);
		secondPlayerTeamPanel.setLayout(new GridLayout(1,3,5,10));
		
		startFight = new JButton("Fight");
		startFight.setBounds((int)(frameWidth*0.83), (int)(frameHeight*0.9),(int) (frameWidth*0.1),(int) (frameHeight*0.05));
		startFight.addActionListener(this);
		startFight.setForeground(Color.white);
		startFight.setBackground(Color.black);
		startFight.setFocusable(false);
		startFight.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
		startFight.setFont(myMedFont);

		this.add(firstName);
		this.add(secondName);
		this.add(allPanels);
		this.add(firstPlayerTeamPanel);
		this.add(secondPlayerTeamPanel);
		this.add(startFight);
		this.setVisible(true);
		this.revalidate();
		this.repaint();
	}

	public void createCard(Champion c , JPanel p) throws IOException {
		JPanel panel = new JPanel();
		
		JLabel pic = new JLabel();
		BufferedImage img = null;
		img = ImageIO.read(new File("images/"+c.getName()+".png"));
		Image dimg = img.getScaledInstance(160, 150,Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(dimg);
		pic.setIcon(icon);
		pic.setBackground(null);
		//pic.setOpaque(true);
		JButton makeLeader = new JButton("Set Leader");
		makeLeader.setBackground(Color.white);
		makeLeader.addActionListener(this);
		allSetLeader.add(makeLeader);
		panel.add(pic,BorderLayout.NORTH);
		panel.add(makeLeader,BorderLayout.SOUTH);
		//p.add(pic,BorderLayout.NORTH);
		//p.add(makeLeader,BorderLayout.SOUTH);
		p.add(panel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Select")) {
			boolean flag = true;
			for(JButton selectBtn : allSelectButtons) {
				if(e.getSource()==selectBtn) {
					int i = allSelectButtons.indexOf(selectBtn);
					Champion c = allChampions.get(i);
					
					if((!alreadySelectedChampions.contains(c)) && (secondPlayer.getTeam().size()!=3)) {
						
						JPanel p = new JPanel();
						p.setLayout(new BorderLayout());
						try {
							createCard(c,p);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						if(firstPlayer.getTeam().size()<3) {
							firstPlayer.getTeam().add(c);
							firstPlayerTeamPanel.add(p);
						}
						else if(secondPlayer.getTeam().size()<3) {
							secondPlayer.getTeam().add(c);
							secondPlayerTeamPanel.add(p);
						}
						alreadySelectedChampions.add(c);
						
						if(flag) {
							selectBtn.setBackground(Color.red);
							playSound("sounds/select1.wav");
						}
						if( secondPlayer.getTeam().size() ==3 )
							flag=false;
						
						this.revalidate();
						this.repaint();
						

					}
					
				}
				
			}
		}
		else if(e.getActionCommand().equals("Set Leader")) {
			int i = allSetLeader.indexOf(e.getSource());
			Champion c = alreadySelectedChampions.get(i);
			if(i<3) {
				for(int j=0 ; j<firstPlayer.getTeam().size() ; j++)
					allSetLeader.get(j).setBackground(Color.white);
				firstPlayer.setLeader(c);
				//System.out.println(firstPlayer.getName()+" : "+firstPlayer.getLeader().getName());
			}
			else {
				for(int j=firstPlayer.getTeam().size() ; j<alreadySelectedChampions.size() ; j++)
					allSetLeader.get(j).setBackground(Color.white);
				secondPlayer.setLeader(c);
				//System.out.println(secondPlayer.getName()+" : "+secondPlayer.getLeader().getName());
			}
			((Component) e.getSource()).setBackground(Color.red);
			playSound("sounds/select-leader.wav");

		}
		
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

	public JButton getStartFight() {
		return startFight;
	}

	
}
