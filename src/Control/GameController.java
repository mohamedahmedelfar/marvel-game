package Control;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.http.WebSocket.Listener;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import engine.Game;
import engine.Player;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.world.Champion;
import model.world.Direction;
import views.GamePlayView;
import views.PopUp;
import views.SelectChampionsView;
import views.StartView;

public class GameController implements ActionListener ,KeyListener{
	
	private StartView startView;
	private SelectChampionsView selectChampionsView;
	private GamePlayView gamePlayView;
	private Game game;
	private PopUp pop;
	
	public GameController() throws IOException {
		startView = new StartView();
		playSound("sounds/start-view.wav");
		startView.getStartGame().addActionListener(this);
	}
	
	public static void main(String[] args) throws IOException {
		new GameController();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object action = e.getSource();
		try {
			if(action ==startView.getStartGame()) {
				Player p1 = new Player(startView.getFirstPlayerName().getText());
				Player p2 = new Player(startView.getSecondPlayerName().getText());	
				
				game = new Game(p1, p2);
				selectChampionsView = new SelectChampionsView(p1, p2, Game.getAvailableChampions());
				selectChampionsView.getStartFight().addActionListener(this);

				startView.setVisible(false);
			}
			else if(action == selectChampionsView.getStartFight()){
				if(game.getFirstPlayer().getLeader()!=null && game.getSecondPlayer().getLeader()!=null) {
					playSound("sounds/start-fight.wav");
					selectChampionsView.setVisible(false);
					
					gamePlayView = new GamePlayView(game.getFirstPlayer(), game.getSecondPlayer());
					game.prepareChampionTurns();
					game.setListener(gamePlayView);
					gamePlayView.updateTurnOrderInfo(game.getTurnOrder());

					gamePlayView.getMoveRight().addActionListener(this);
					gamePlayView.getMoveLeft().addActionListener(this);
					gamePlayView.getMoveUp().addActionListener(this);
					gamePlayView.getMoveDown().addActionListener(this);
					
					gamePlayView.getAttackRight().addActionListener(this);
					gamePlayView.getAttackLeft().addActionListener(this);
					gamePlayView.getAttackUp().addActionListener(this);
					gamePlayView.getAttackDown().addActionListener(this);

					gamePlayView.getEndTurn().addActionListener(this);
					gamePlayView.getULA().addActionListener(this);
					gamePlayView.getCurrNameButton().addActionListener(this);
					gamePlayView.getHELBox().addActionListener(this);
					gamePlayView.getDMGBox().addActionListener(this);
					gamePlayView.getCCBox().addActionListener(this);

					gamePlayView.getCurrNameButton().setText( ((Champion)(game.getTurnOrder().peekMin())).getName() );
					gamePlayView.updateAbilityComboBox( ((Champion)(game.getTurnOrder().peekMin())).getAbilities() );

					game.placeCovers();
					game.placeChampions();
					gamePlayView.putCurrentHighlight(game.getCurrentChampion().getLocation());

				}
			}
			
			else if(action == gamePlayView.getMoveRight() || action == gamePlayView.getMoveLeft() || action == gamePlayView.getMoveUp() ||action == gamePlayView.getMoveDown() ) {
				Direction d;
				if(action==gamePlayView.getMoveRight())
					d=Direction.RIGHT;
				else if(action==gamePlayView.getMoveLeft())
					d=Direction.LEFT;
				else if(action==gamePlayView.getMoveUp())
					d=Direction.UP;
				else
					d=Direction.DOWN;
				
				Point oldPoint = game.getCurrentChampion().getLocation();
				game.move(d);
				playSound("sounds/move1.wav");
				gamePlayView.clearLabel(game.getCurrentChampion(),oldPoint);
				gamePlayView.removeCurrentHighlight(oldPoint);
				gamePlayView.drawChampion(game.getCurrentChampion());
				gamePlayView.putCurrentHighlight(game.getCurrentChampion().getLocation());	
			}
			
			else if(action == gamePlayView.getAttackRight() || action == gamePlayView.getAttackLeft() || action == gamePlayView.getAttackUp() ||action == gamePlayView.getAttackDown()) {
				Direction d;
				if(action==gamePlayView.getAttackRight())
					d=Direction.RIGHT;
				else if(action==gamePlayView.getAttackLeft())
					d=Direction.LEFT;
				else if(action==gamePlayView.getAttackUp())
					d=Direction.UP;
				else
					d=Direction.DOWN;
				game.attack(d);
			}
			
			else if(action==gamePlayView.getEndTurn()) {
				game.endTurn();
				playSound("sounds/excellent.wav");
			}
			
			else if(action == gamePlayView.getULA()) {
				game.useLeaderAbility();
				playSound("sounds/ula.wav");

			}
			
			else if(action == gamePlayView.getHELBox() || action == gamePlayView.getDMGBox() || action == gamePlayView.getCCBox()) {
				JComboBox<String> box ;
				if(action == gamePlayView.getHELBox())
					box = gamePlayView.getHELBox();
				else if(action == gamePlayView.getDMGBox())
					box = gamePlayView.getDMGBox();
				else
					box = gamePlayView.getCCBox();
				
				String str = (String) box.getSelectedItem();
				if(str.indexOf('/')<0)
					return;
				Ability a = Game.findAbilityByName(str.substring(str.indexOf('/')+1));
				String areaOfEffect = str.substring( 0, str.indexOf('/') );
				
				if( !(areaOfEffect.equals("SINGLETARGET")) && !(areaOfEffect.equals("DIRECTIONAL") )  ) {
					game.castAbility(a);
					playSound("sounds/impressive.wav");
				}
				else if(areaOfEffect.equals("DIRECTIONAL")) {
					String direction = gamePlayView.getDirectionArea().getText();
					Direction d = null;
					switch(direction) {
						case "RIGHT" : d=Direction.RIGHT;break;
						case "LEFT" : d=Direction.LEFT;break;
						case "UP" : d=Direction.UP;break;
						case "DOWN" : d=Direction.DOWN;break;
					}
					if(d != null) {
						game.castAbility(a, d);
						playSound("sounds/impressive.wav");
					}
				}
				else if(areaOfEffect.equals("SINGLETARGET")) {
					String xValue = gamePlayView.getTargetX().getText();
					String yValue = gamePlayView.getTargetY().getText();
					int x ;
					int y ;
					if(xValue.equals("") || yValue.equals(""))
						return;
					else {
						x = Integer.parseInt(xValue);
						y = Integer.parseInt(yValue);
					}
					game.castAbility(a, x, y);
					playSound("sounds/impressive.wav");
				}
				
			
			}

			
		}
		catch (InvalidTargetException | ChampionDisarmedException |  AbilityUseException | LeaderAbilityAlreadyUsedException | LeaderNotCurrentException | NotEnoughResourcesException | UnallowedMovementException | CloneNotSupportedException | IOException exception) {
			pop = new PopUp(exception.getMessage());
			
		}
		finally {
			Player p = game.checkGameOver();
			if(p != null && action!=startView.getStartGame()) {
				pop = new PopUp(p.getName()+" Wins!");
				pop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				playSound("sounds/game-over.wav");
			}
		}
		
		
	}
	
	public void keyPressed(KeyEvent e) {
		
	}
	
	public void keyTyped(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		
	}
	
	public void playSound(String filePath) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
