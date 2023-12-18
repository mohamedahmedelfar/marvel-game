package engine;

import java.awt.List;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.WebSocket.Listener;
import java.util.ArrayList;
import java.util.Random;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Disarm;
import model.effects.Dodge;
import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Embrace;
import model.effects.PowerUp;
import model.effects.Root;
import model.effects.Shield;
import model.effects.Shock;
import model.effects.Silence;
import model.effects.SpeedUp;
import model.effects.Stun;
import model.world.AntiHero;
import model.world.Champion;
import model.world.Condition;
import model.world.Cover;
import model.world.Damageable;
import model.world.Direction;
import model.world.Hero;
import model.world.Villain;

public class Game {
	private static ArrayList<Champion> availableChampions;
	private static ArrayList<Ability> availableAbilities;
	private Player firstPlayer;
	private Player secondPlayer;
	private Object[][] board;
	private PriorityQueue turnOrder;
	private boolean firstLeaderAbilityUsed;
	private boolean secondLeaderAbilityUsed;
	private final static int BOARDWIDTH = 5;
	private final static int BOARDHEIGHT = 5;
	private GameListener listener;
	

	public GameListener getListener() {
		return listener;
	}

	public void setListener(GameListener listener) {
		this.listener = listener;
	}

	public Game(Player first, Player second) throws IOException {
		firstPlayer = first;
		secondPlayer = second;
		availableChampions = new ArrayList<Champion>();
		availableAbilities = new ArrayList<Ability>();
		loadAbilities("Abilities.csv");
		loadChampions("Champions.csv");
		board = new Object[BOARDWIDTH][BOARDHEIGHT];
		turnOrder = new PriorityQueue(6);
		//placeChampions();
		//placeCovers();
		//prepareChampionTurns();
	}

	public static void loadAbilities(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Ability a = null;
			AreaOfEffect ar = null;
			switch (content[5]) {
			case "SINGLETARGET":
				ar = AreaOfEffect.SINGLETARGET;
				break;
			case "TEAMTARGET":
				ar = AreaOfEffect.TEAMTARGET;
				break;
			case "SURROUND":
				ar = AreaOfEffect.SURROUND;
				break;
			case "DIRECTIONAL":
				ar = AreaOfEffect.DIRECTIONAL;
				break;
			case "SELFTARGET":
				ar = AreaOfEffect.SELFTARGET;
				break;

			}
			Effect e = null;
			if (content[0].equals("CC")) {
				switch (content[7]) {
				case "Disarm":
					e = new Disarm(Integer.parseInt(content[8]));
					break;
				case "Dodge":
					e = new Dodge(Integer.parseInt(content[8]));
					break;
				case "Embrace":
					e = new Embrace(Integer.parseInt(content[8]));
					break;
				case "PowerUp":
					e = new PowerUp(Integer.parseInt(content[8]));
					break;
				case "Root":
					e = new Root(Integer.parseInt(content[8]));
					break;
				case "Shield":
					e = new Shield(Integer.parseInt(content[8]));
					break;
				case "Shock":
					e = new Shock(Integer.parseInt(content[8]));
					break;
				case "Silence":
					e = new Silence(Integer.parseInt(content[8]));
					break;
				case "SpeedUp":
					e = new SpeedUp(Integer.parseInt(content[8]));
					break;
				case "Stun":
					e = new Stun(Integer.parseInt(content[8]));
					break;
				}
			}
			switch (content[0]) {
			case "CC":
				a = new CrowdControlAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), e);
				break;
			case "DMG":
				a = new DamagingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			case "HEL":
				a = new HealingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			}
			availableAbilities.add(a);
			line = br.readLine();
		}
		br.close();
	}

	public static void loadChampions(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Champion c = null;
			switch (content[0]) {
			case "A":
				c = new AntiHero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;

			case "H":
				c = new Hero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			case "V":
				c = new Villain(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			}

			c.getAbilities().add(findAbilityByName(content[8]));
			c.getAbilities().add(findAbilityByName(content[9]));
			c.getAbilities().add(findAbilityByName(content[10]));
			availableChampions.add(c);
			line = br.readLine();
		}
		br.close();
	}

	public static Ability findAbilityByName(String name) {
		for (Ability a : availableAbilities) {
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}

	public void placeCovers() throws IOException {
		int i = 0;
		while (i < 5) {
			int x = ((int) (Math.random() * (BOARDWIDTH - 2))) + 1;
			int y = (int) (Math.random() * BOARDHEIGHT);

			if (board[x][y] == null) {
				Cover cover =  new Cover(x, y);
				board[x][y] = cover;
				listener.drawCover(cover);
				i++;
			}
		}

	}

	public void placeChampions() throws IOException {
		int i = 1;
		for (Champion c : firstPlayer.getTeam()) {
			board[0][i] = c;
			c.setLocation(new Point(0, i));
			listener.drawChampion(c);
			i++;
		}
		i = 1;
		for (Champion c : secondPlayer.getTeam()) {
			board[BOARDHEIGHT - 1][i] = c;
			c.setLocation(new Point(BOARDHEIGHT - 1, i));
			listener.drawChampion(c);
			i++;
		}
	
	}

	public static ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	public static ArrayList<Ability> getAvailableAbilities() {
		return availableAbilities;
	}

	public Player getFirstPlayer() {
		return firstPlayer;
	}

	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public Object[][] getBoard() {
		return board;
	}

	public PriorityQueue getTurnOrder() {
		return turnOrder;
	}

	public boolean isFirstLeaderAbilityUsed() {
		return firstLeaderAbilityUsed;
	}

	public boolean isSecondLeaderAbilityUsed() {
		return secondLeaderAbilityUsed;
	}

	public static int getBoardwidth() {
		return BOARDWIDTH;
	}

	public static int getBoardheight() {
		return BOARDHEIGHT;
	}
	
	public Champion getCurrentChampion() {
		return (Champion)(turnOrder.peekMin()); // the champion who's turn is taking action is the first champion in the queue
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public Player checkGameOver() {
		if(firstPlayer.getTeam().size()==0)     // if first player team all are dead then the winner is second player
			return secondPlayer;				   
		else if(secondPlayer.getTeam().size()==0) // same logic but the opposite 
			return firstPlayer;
		return null;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void move(Direction d) throws UnallowedMovementException,NotEnoughResourcesException{
		Champion current = getCurrentChampion();
		int oldX = (int) current.getLocation().getX();
		int oldY = (int) current.getLocation().getY();

		//Check for all possible exceptions
		
		if(current.getCurrentActionPoints()==0)
			throw new NotEnoughResourcesException("Champion does not have enough action points to perform the action!");
		if(current.getCondition()==Condition.ROOTED)
			throw new UnallowedMovementException("The champion cannot move due to being rooted!");
		
		//Point newPoint = moveTo(current.getLocation(), d);
		
		Point newPoint = null;
		switch(d) {
			case RIGHT : newPoint = new Point(oldX,oldY+1);break; //if right then the new point it the x and y+1
			case LEFT : newPoint = new Point(oldX,oldY-1);break;  //if left then the new point it the x and y-1	
			case UP : newPoint = new Point(oldX+1,oldY);break; 	  //if up then the new point it the x+1 and y
			case DOWN : newPoint = new Point(oldX-1,oldY);break;  //if down then the new point it the x-1 and y
	
		}
		
		if(newPoint.getX()<0 || newPoint.getX()>=BOARDHEIGHT || newPoint.getY()<0 || newPoint.getY()>=BOARDWIDTH)
			throw new UnallowedMovementException("Cannot move out of the board, The champion is standing on the edge!");
		
		if(board[(int)newPoint.getX()][(int)newPoint.getY()]!=null)
			throw new UnallowedMovementException("The champion cannot move to an occupied cell!");
		
		//If no exception was thrown then we place the champion on the new point
		board[(int)newPoint.getX()][(int)newPoint.getY()]=current;
		board[oldX][oldY]=null; //Private test case , make the old location empty
		current.setLocation(newPoint);
		current.setCurrentActionPoints(current.getCurrentActionPoints()-1);
		listener.updateChampionInfo(getCurrentChampion());
//		if(current.getCurrentActionPoints()==0)
//			endTurn();
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public static int manhatan(Point start,Point end) {
		return Math.abs((int)start.getX()-(int)end.getX()) + Math.abs((int)start.getY()-(int)end.getY());
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public ArrayList<Damageable> getAllCellsDirection(Point start, Direction d,int range) {
		int x = (int)start.getX();
		int y = (int)start.getY();
		ArrayList<Damageable> allInDirection = new ArrayList<Damageable>();
		// We need to get the first damageable cell in that direction which satisfies the range
		switch(d) {
			case RIGHT :	// if right then we loop until we hit the right edge of the board width
				while(y<BOARDWIDTH) {
					if( board[x][y] instanceof Damageable && y!=start.getY()) { // y!=start.getX() in order not to return the attacker himself
						int manhatanDistance = manhatan(start,new Point(x,y));
						if(manhatanDistance <= range ) 
							allInDirection.add((Damageable) board[x][y]); //add the object if it satisfies the range
					}										
					y++; // y++ in order to increase the y component on the board to check for the cell to the right of the current cell
				};break;
			case LEFT :
				while(y>=0) {	//loop until we hit the left edge of the board width
					if( board[x][y] instanceof Damageable && y!=start.getY()) { // same as above
						int manhatanDistance = manhatan(start,new Point(x,y));
						if(manhatanDistance <= range)
							allInDirection.add((Damageable) board[x][y]); //add the object if it satisfies the range
					}
					y--; // y-- in order to go left
				};break;
			case UP :
				while(x<BOARDHEIGHT) { //loop until we hit the upper edge of the board height
					if( board[x][y] instanceof Damageable && x!=start.getX()) {
						int manhatanDistance = manhatan(start,new Point(x,y));
						if(manhatanDistance <= range)
							allInDirection.add((Damageable) board[x][y]); //add the object if it satisfies the range
					}
					x++; // x++ to move up
				};break;
			case DOWN :
				while(x>=0) { //loop until we hit the lower edge of the board height
					if( board[x][y] instanceof Damageable && x!=start.getX()) {
						int manhatanDistance = manhatan(start,new Point(x,y));
						if(manhatanDistance <= range  )
							allInDirection.add((Damageable) board[x][y]); //add the object if it satisfies the range
					}
					x--; // x-- to move down
				};	
		}
		return allInDirection; // if no cell was found based on the direction and the range then a null will be returned
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public static boolean isNormalDamage(Champion c1 , Champion c2) { // returns true if no interaction condition was found
		return ((c1 instanceof Hero && c2 instanceof Hero)|| 		// HERO does not do extra damage to HERO
				(c1 instanceof Villain && c2 instanceof Villain)||	// VILLAIN does not do extra damage to VILLAIN
				(c1 instanceof AntiHero && c2 instanceof AntiHero));	// ANTIHERO does not do extra damage to ANTIHERO
	
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void attack(Direction d) throws ChampionDisarmedException,NotEnoughResourcesException,InvalidTargetException, IOException {
		Champion current = getCurrentChampion();
		
		if(current.hasEffect("Disarm")!=null)
			throw new ChampionDisarmedException("Cannot apply a normal attack, Champion is disarmed!");
		
		if(current.getCurrentActionPoints()<2)
			throw new NotEnoughResourcesException("Champion does not have enough action points to perform the action!");
		
		ArrayList<Damageable> allInDirection = getAllCellsDirection(current.getLocation(), d, current.getAttackRange());
		
		current.setCurrentActionPoints(current.getCurrentActionPoints()-2);
		listener.updateChampionInfo(current);
		
		if ( allInDirection.isEmpty() ) 
			return;
			
		Damageable attackedCell = allInDirection.get(0); // Get the attacked cell
		
		if(attackedCell instanceof Champion && isAlly(current,(Champion) attackedCell))
			throw new InvalidTargetException("Cannot attack an ally!");
		
		if(attackedCell instanceof Champion) { 							//If attacked cell is a champion
			Effect ef = ((Champion) attackedCell).hasEffect("Shield");	//And that champion has a shield
			if(ef!=null) {
				((Champion) attackedCell).getAppliedEffects().remove(ef);
				ef.remove((Champion)attackedCell);						//then remove the shield effect from the champion
				return;	//return in order not to execute the rest of the method, Because shield protects the champion
			}
		}
		
		//Not having a shield means that the champion or the cover will be damaged
		Boolean canDodge = false;

		if(attackedCell instanceof Cover) {	//if a cover then deduct it's HP
			Cover co = (Cover)(attackedCell);
			co.setCurrentHP(co.getCurrentHP()-current.getAttackDamage());
			listener.updateCoverHp((Cover) attackedCell);
			listener.generateCoverDamageSound();
		}

		else if(attackedCell instanceof Champion) {
			// if that champion has a dodge effect then he might avoid the attack by 50%
			if (((Champion)(attackedCell)).hasEffect("Dodge")!=null) {
				Random r = new Random();
				canDodge = r.nextBoolean();
			}
			if(!canDodge){// if the random is false then he will not avoid the attack so we go into the attack steps
				if(isNormalDamage(current, (Champion)attackedCell)) { //if there is no interaction then damage is calculated normally
					attackedCell.setCurrentHP(attackedCell.getCurrentHP()-current.getAttackDamage());
				}
				else { 												//else then interaction is met and do 50% extra damage
					int newDamage = (int)(current.getAttackDamage()*1.5); // the value of the damage + the extra
					attackedCell.setCurrentHP(attackedCell.getCurrentHP()-newDamage);				
				}
				listener.updateChampionInfo((Champion) attackedCell);
				listener.generateAttackedSound();
			}
		}
	
		eliminateIfDead(attackedCell);	//eliminate the attacked cell if it died after the attack

	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	// get the friendly team list that are in range
	public ArrayList<Champion> getAllFriendly(Champion ch){ // get the friendly team list without that champion
		//ArrayList<Damageable> list = new ArrayList<Damageable>();
		ArrayList<Champion> allFriendly = new ArrayList<Champion>();
		
		if(firstPlayer.getTeam().contains(ch)) // if that champion in the first player team then his friends are the first player team
			allFriendly = firstPlayer.getTeam();
		else
			allFriendly = secondPlayer.getTeam();//else then his friends are the second player team
		return allFriendly;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	// get the enemy team list that are in range 
	public ArrayList<Champion> getAllEnemies(Champion ch){ 
		ArrayList<Champion> allEnemies = new ArrayList<Champion>();		// We follow the same concept as in getFriendlyInRange		
		if(firstPlayer.getTeam().contains(ch)) 							// but we switch teams
			allEnemies = secondPlayer.getTeam();
		else
			allEnemies = firstPlayer.getTeam();
		
		return allEnemies;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	//Method to get all the damageables surrounding the input champion
	public ArrayList<Damageable> getSurroundings(Champion ch){ 
		Point p = ch.getLocation(); 
		int x = (int)p.getX();
		int y = (int)p.getY();
		int startX;
		int startY;
		if(x==0) 
			startX=0; // we do this to avoid being out of the board as a start 
		else
			startX = x-1; // we start searching by the point at south west the champion's location
		if(y==0)
			startY=0;
		else
			startY=y-1;
		
		ArrayList<Damageable> list = new ArrayList<Damageable>();
		for(int i=startX ; i<=x+1 && i<BOARDHEIGHT; i++) {
			for(int j = startY ; j<=y+1 && j<BOARDWIDTH;j++) {
				Object dm = board[i][j];
				if(dm instanceof Damageable && dm!=ch)
					list.add((Damageable)dm);
			}
		}
		return list;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isAlly(Champion c1 , Champion c2) { // A method to check whether the two champions are friends or not 
		if(firstPlayer.getTeam().contains(c1) && firstPlayer.getTeam().contains(c2))
			return true;
		if(secondPlayer.getTeam().contains(c1) && secondPlayer.getTeam().contains(c2))
			return true;
		return false;
	}
	public boolean isEnemy(Champion c1,Champion c2) {	// A method to check whether the two champions are enemies or not
		return !isAlly(c1, c2);
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void castAbility(Ability a) throws AbilityUseException,InvalidTargetException,NotEnoughResourcesException, CloneNotSupportedException, IOException {
		Champion current = getCurrentChampion();
		if(current.getCurrentActionPoints()<a.getRequiredActionPoints())
			throw new NotEnoughResourcesException("Champion does not have enough action points to cast the ability!");
		if(current.getMana()<a.getManaCost())
			throw new NotEnoughResourcesException("Champion does not have enough mana to cast the ability!");
		if(current.hasEffect("Silence")!=null)
			throw new AbilityUseException("Cannot cast the ability, Champion is silenced!");
		if(a.getCurrentCooldown()!=0)
			throw new AbilityUseException("Champion must wait for "+a.getBaseCooldown()+" turns in order to cast the ability");
		
		ArrayList<Damageable> targets = new ArrayList<Damageable>();
		//Damaging Ability/////////////////////////////
		if(a instanceof DamagingAbility) {	//DMG affects covers and enemies only
			if(a.getCastArea()==AreaOfEffect.TEAMTARGET) { //DMG TEAMTARGET affects enemies only
				ArrayList<Champion> allEnemies = getAllEnemies(current); // TEAMTARGET DMG affects only enemies
				for(Champion c : allEnemies)
					if(manhatan(current.getLocation(), c.getLocation()) <= a.getCastRange())
						targets.add(c);
			}
			else { // SURROUNDING DMG Affects enemies surrounding that champion
				ArrayList<Damageable> allSurroundings = getSurroundings(current); // A method to get all cells surrounding the input, It gets covers and champions
				for(Damageable dm : allSurroundings) {  
					if(dm instanceof Cover) // if a cover then simply add it to targets
						targets.add(dm);
					else if(isEnemy(current, (Champion)dm)){ // else it has to be a champion but we only add that champion if he is enemy								
							targets.add(dm);
					}
				}
			}
		}
		//HealingAbility////////////////////////////
		else if(a instanceof HealingAbility){
			if(a.getCastArea()==AreaOfEffect.TEAMTARGET) { // TEAMTARGET HEL is applied to the friendly team that are in range
				ArrayList<Champion> allFriends = getAllFriendly(current);
				for(Champion c : allFriends)
					if(manhatan(current.getLocation(), c.getLocation()) <= a.getCastRange())
						targets.add(c);
			}
			
			else if(a.getCastArea()==AreaOfEffect.SELFTARGET) { 
				targets.add(current); // We only pass the champion himself to the list because it is a SELFTARGET ability
			}
			
			else { // SURROUNDING HEL affects the friendly team surrounding that champion
				ArrayList<Damageable> allSurroundings = getSurroundings(current);	// Exactly like DMG but we don't include covers
				for(Damageable dm : allSurroundings) {
					if(dm instanceof Champion && isAlly(current, (Champion)dm)) {
						targets.add(dm);
					}
				}
			}
		}
		
		//CrowdControl//////////////////////////
		else { // CrowdControl
			if(a.getCastArea()==AreaOfEffect.TEAMTARGET){	
				if(((CrowdControlAbility)(a)).getEffect().getType()==EffectType.BUFF) {  // BUFF (Positive) CC affect the friendly team
					ArrayList<Champion> allFriends = getAllFriendly(current);
					for(Champion c : allFriends)
						if(manhatan(current.getLocation(), c.getLocation()) <= a.getCastRange())
							targets.add(c);
				}
				else { // TEAMTARGET DEBUFF (negative) CC affects the enemy team
					ArrayList<Champion> allEnemies = getAllEnemies(current);
					for(Champion c :allEnemies) {
						if(manhatan(current.getLocation(), c.getLocation()) <= a.getCastRange())
							targets.add(c);
					}
				}
			}
			
			else if(a.getCastArea()==AreaOfEffect.SELFTARGET) {
				if(((CrowdControlAbility)(a)).getEffect().getType()==EffectType.BUFF)// If BUFF then it affects the champion casting it
					targets.add(current); 
			}
			
			else { // SURROUNDING , don't consider range in the surrounding area of effect
				ArrayList<Damageable> allSurroundings = getSurroundings(current);	// Exactly like DMG but we don't include covers
				EffectType type = ((CrowdControlAbility)(a)).getEffect().getType();
				for(Damageable dm : allSurroundings) {
					if(dm instanceof Champion && type==EffectType.BUFF && isAlly(current,(Champion)dm))  // if BUFF then it affects friendlies 	
						targets.add(dm);
					else if(dm instanceof Champion && type==EffectType.DEBUFF && isEnemy(current,(Champion)dm))//It's not BUFF then it has to be DEBUFF which affects enemies if he is enemy then add him to targets 
						targets.add(dm);
				}
			}
			
		}
		a.execute(targets);
		
		a.setCurrentCooldown(a.getBaseCooldown());
		current.setCurrentActionPoints(current.getCurrentActionPoints()-a.getRequiredActionPoints());
		current.setMana(current.getMana()-a.getManaCost());
		
		// After executing the ability on the targets we need to check for the death of the members of the targets list
		// But only if it is a DMG or a DEBUFF CC , HEL ability does not cause death of anyone
		
		listener.updateChampionInfo(current);
		listener.updateAbilityInfo(current);
		
		for(Damageable d : targets) {
			if(d instanceof Cover)
				listener.updateCoverHp((Cover) d);
			else {
				listener.updateChampionInfo((Champion) d);
				listener.updateAbilityInfo((Champion) d);
			}
		}
		
		if(a instanceof DamagingAbility ) 
			eliminateAllDead(targets);
		

		
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void castAbility(Ability a, Direction d)throws NotEnoughResourcesException,AbilityUseException,InvalidTargetException, CloneNotSupportedException, IOException {
		Champion current = getCurrentChampion();
		
		if(current.getCurrentActionPoints()<a.getRequiredActionPoints())
			throw new NotEnoughResourcesException("Champion does not have enough action points to cast the ability!");
		if(current.getMana()<a.getManaCost())
			throw new NotEnoughResourcesException("Champion does not have enough mana to cast the ability!");
		if(current.hasEffect("Silence")!=null)
			throw new AbilityUseException("Cannot cast the ability, Champion is silenced!");
		if(a.getCurrentCooldown()!=0)
			throw new AbilityUseException("Champion must wait for:"+(a.getBaseCooldown()-a.getCurrentCooldown())+" turns in order to cast the ability");
		
		//If no exception was thrown then we deduct the resources needed for that action
		current.setCurrentActionPoints(current.getCurrentActionPoints()-a.getRequiredActionPoints());
		a.setCurrentCooldown(a.getBaseCooldown());
		current.setMana(current.getMana()-a.getManaCost());
		
		// getAllCellsDirection ---> is a method to get all the damageables in the given direction satisfying the range
		ArrayList<Damageable> allCellsInDirection = getAllCellsDirection(current.getLocation(), d, a.getCastRange());
		ArrayList<Damageable> targets = new ArrayList<Damageable>();
		
		// Take into consideration that the area of effect is DIRECTIONAL throughout that method
		if(a instanceof DamagingAbility) { // DMG Affects all covers and enemies in that direction within range
			for(Damageable dm : allCellsInDirection) { 
				if(dm instanceof Cover)	// if the damageable object is a cover then put it in targets
					targets.add(dm);
				else {
					if(isEnemy(current, (Champion) dm))	// Else it has to be a champion but we only put him if he is an enemy
						targets.add(dm);
				}
			}
		}
		else if(a instanceof HealingAbility) {	// HEL affects only the allies of that champion
			for (Damageable dm : allCellsInDirection) {
				if(dm instanceof Champion) { // first of all the damageable has to be a champion 
					if(isAlly(current, (Champion) dm))	//if he is ally then put him
						targets.add(dm);
				}
			}
		}
		else { // CC 
			EffectType type = ((CrowdControlAbility)(a)).getEffect().getType();
			for(Damageable dm:allCellsInDirection) {
				if(dm instanceof Champion) {
					if(type==EffectType.BUFF) { // if BUFF then we put the allies  
						if(isAlly(current,(Champion)dm)) // if he is ally then add him to targets
							targets.add(dm);
					}
					else { //else it has to be DEBUFF so we put him only if he is an enemy
						if(isEnemy(current,(Champion)dm)) //if he is enemy then add him to targets
							targets.add(dm);
					}
				}
			}
		}
		
		a.execute(targets); // after populating the targets ----> execute
		
		listener.updateAbilityInfo(current);
		listener.updateChampionInfo(current);
		for(Damageable affected : targets) {
			if(affected instanceof Cover)
				listener.updateCoverHp((Cover) affected);
			else {
				listener.updateChampionInfo((Champion) affected);
				listener.updateAbilityInfo((Champion) affected);
			}
		}
		
		//DMG Ability might cause death of some champions in the targets list after executing it on them so we eliminate dead ones if found
		if(a instanceof DamagingAbility) 
			eliminateAllDead(targets);
		
		
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	public void castAbility(Ability a, int x, int y)throws AbilityUseException,NotEnoughResourcesException,InvalidTargetException, CloneNotSupportedException, IOException {
		
		Champion current = getCurrentChampion();
		
		if(a.getCurrentCooldown()!=0)
			throw new AbilityUseException("Champion must wait for:"+(a.getBaseCooldown()-a.getCurrentCooldown())+" turns in order to cast the ability");

		if(current.getCurrentActionPoints()<a.getRequiredActionPoints()) // Check for all possible exceptions
			throw new NotEnoughResourcesException("Champion does not have enough action points to cast the ability!");
		
		if(current.getMana()<a.getManaCost()) 
			throw new NotEnoughResourcesException("Champion does not have enough mana to cast the ability!");
		
		if(current.hasEffect("Silence")!=null)
			throw new AbilityUseException("Cannot cast the ability, Champion is silenced!");
		
		if(board[x][y] == null)
			throw new InvalidTargetException("Target cell is empty!");
		
		if(manhatan(current.getLocation(), new Point(x,y)) > a.getCastRange())
			throw new AbilityUseException("Cannot cast the ability, Target is out of range!");
		
		Damageable targetObject = (Damageable)board[x][y]; // The attacked cell
				
		ArrayList<Damageable> targets = new ArrayList<Damageable>();
		
		if(a instanceof DamagingAbility){	//if the target is a friendly champion then we cannot cast a DMG ability on him, So we throw an exception
			if( (targetObject instanceof Champion && isAlly(current, (Champion) targetObject)))
				throw new InvalidTargetException("Cannot cast a Damaging Ability on the current champion or on an ally!");
			else { // else we put him in the targets and execute the Ability 
				targets.add(targetObject);
			}
		}
		else if(a instanceof HealingAbility){//If the target is a friend then we can cast the healing ability on him
			if(targetObject instanceof Champion && isAlly(current,(Champion) targetObject)) {
				targets.add(targetObject);
			}
			else 
				throw new InvalidTargetException("Cannot cast a Healing Ability on an enemy or a cover");
		}
		else {  
			// CC Abilities that are BUFF can only affect friendly champions
			if(((CrowdControlAbility)(a)).getEffect().getType()==EffectType.BUFF && targetObject instanceof Champion ) {
				if(isAlly(current, (Champion) targetObject)) {
					targets.add(targetObject);
				}
				else
					throw new InvalidTargetException("Cannot cast a positive CC ability on an enemy or a cover!");
			}
			else { // DEBUFF Can only affect enemy champions
				if((targetObject instanceof Champion && isEnemy(current, (Champion) targetObject))) {
					targets.add(targetObject);
				}
				else
					throw new InvalidTargetException("Cannot cast a negative CC ability on an ally or a cover!");
			}
		}
		a.execute(targets);
		
		//in SINGLETARGET ability we shouldn't deduct the resources if no valid cell was found
		//so if no valid cell was found then one of the exceptions will be thrown
		//thus the below lines that deduct the resources are not gonna be executed in this case so no deduction will happen
		
		a.setCurrentCooldown(a.getBaseCooldown());
		current.setMana(current.getMana()-a.getManaCost());
		current.setCurrentActionPoints(current.getCurrentActionPoints()-a.getRequiredActionPoints());
		
		listener.updateChampionInfo(current);
		listener.updateAbilityInfo(current);
		if(targetObject instanceof Cover)
			listener.updateCoverHp((Cover) targetObject);
		else {
			listener.updateChampionInfo((Champion) targetObject);
			listener.updateAbilityInfo((Champion) targetObject);
		}
		eliminateIfDead(targetObject);
		
		
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void useLeaderAbility() throws LeaderNotCurrentException,LeaderAbilityAlreadyUsedException, CloneNotSupportedException, IOException{
		Champion firstLeader = firstPlayer.getLeader();
		Champion secondLeader = secondPlayer.getLeader();
		Champion current = getCurrentChampion();	// champion who is taking the current turn
		
		if(current!=firstLeader && current!=secondLeader)
			throw new LeaderNotCurrentException("The turn is not for the leader!");
		if(current==firstLeader && firstLeaderAbilityUsed)
			throw new LeaderAbilityAlreadyUsedException(firstPlayer.getName()+"' has been already used!");
		if(current==secondLeader && secondLeaderAbilityUsed)
			throw new LeaderAbilityAlreadyUsedException(secondPlayer.getName()+"' has been already used!");
		
		ArrayList<Champion> targets = new ArrayList<Champion>(); // A list that will hold champions that the leader ability will affect 
		
		if(current instanceof Hero) { // Hero leader ability affects the whole friendly team
			if(current==firstLeader) { // if that hero is the leader of the first player
				for(Champion ch:firstPlayer.getTeam()) // then we add all the champions of the first player team
					targets.add(ch);
			}
			else {// else then that hero is the leader of the second player
				for(Champion ch: secondPlayer.getTeam()) // so we put all champions of the second player team
					targets.add(ch);
			}
		}
		
		else if(current instanceof Villain) { // villain leader ability eliminates all enemies who have less than 30% HP
			if(current==firstLeader) {//if that villain is the leader of the first player then his targets are second player's team
				for(Champion ch: secondPlayer.getTeam()) // so we put all second player's champions who have less than 30% HP
					if(ch.getCurrentHP()<ch.getMaxHP()*0.3)
						targets.add(ch);
			}
			else { //else then that villain is the leader of the second player so then his targets are first player's team
				for(Champion ch:firstPlayer.getTeam()) // same concept as above but we only switch the teams 
					if(ch.getCurrentHP()<ch.getMaxHP()*0.3) 
						targets.add(ch);
			}
		}
		
		else { // antihero's targets are all champions on the board except the leaders of the two teams
			for(Champion ch:firstPlayer.getTeam())
				if(ch!=firstLeader) // we put all first player's team except his leader 
					targets.add(ch);
			for(Champion ch: secondPlayer.getTeam()) // and we also put all the second player's team except his leader
				if(ch!=secondLeader)
					targets.add(ch);
		}
		
		current.useLeaderAbility(targets); // Apply the leader ability logic on the list after populating the targets
		
		if(current==firstLeader) { // if that current is the leader of first player then we set firstLeaderAbilityUsed to be true
			firstLeaderAbilityUsed=true;
			listener.updateLeaderAbilityUsed(current);
		}
		else {	
			secondLeaderAbilityUsed=true; // else then he must be the leader of the second player so we set the secondLeaderAbilityUsed
			listener.updateLeaderAbilityUsed(current);
		}
		if(current instanceof Villain) { //We need to eliminate any dead in case a villain leader ability is applied
			for(Champion c : targets)
				eliminateIfDead(c);
		}
		
		for(Champion c : targets)
			listener.updateChampionInfo(c);
		
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void endTurn() {
		Champion removed = (Champion) turnOrder.remove();
		listener.removeCurrentHighlight(removed.getLocation());	//Remove the champion who ended his turn from the turn order queue
		
		checkGameOver();
		
		if(turnOrder.isEmpty())				//Refill the turn queue after removing the champion only if it became empty
			prepareChampionTurns();
		
		listener.updateTurnOrderInfo(turnOrder);
		
		while( ! turnOrder.isEmpty() ) { 		//Loop until you hit the first ACTIVE champion in this case you hit break so that he can take turn
			Champion ch = ((Champion)(turnOrder.peekMin()));
			if( ch.getCondition()==Condition.INACTIVE ) { // skip inactive champions
				ch.removeExpiredEffects();	// But update his effects durations
				ch.updateAbilitiesTimer();  //And ability timers as well
				listener.updateTurnOrderInfo(turnOrder);
				listener.updateChampionInfo(ch);
				listener.updateAbilityInfo(ch);
				
				turnOrder.remove();			//then remove him from the queue						
				if(turnOrder.isEmpty())
					prepareChampionTurns();
			}
			else {
				break;
			}// When a break is hit , it means that we found the first ACTIVE champion
		}
											
		Champion newChamp = getCurrentChampion();
		
		newChamp.removeExpiredEffects();
		newChamp.updateAbilitiesTimer();
		newChamp.setCurrentActionPoints(newChamp.getMaxActionPointsPerTurn());
		listener.updateCurrentName(newChamp);
		listener.putCurrentHighlight(newChamp.getLocation());
		
		listener.updateAbilityComboBox(newChamp.getAbilities());
		listener.updateChampionInfo(newChamp);
		listener.updateAbilityInfo(newChamp);
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void eliminateAllDead(ArrayList<Damageable> targets) throws IOException { // Method to loop over a Damageable targets and eleminate the dead ones 
		for(Damageable dm : targets)							  // by calling the eleminateIfDead(Damageable damaged) on each one in the input targets
			eliminateIfDead(dm);
	}
	
	public void removeFromTeam(Champion c) {
		if(firstPlayer.getTeam().contains(c)) //remove the champion from his corresponding team arraylist depending on who's player the champion belongs to
			firstPlayer.getTeam().remove(c);
		else
			secondPlayer.getTeam().remove(c);	
	}
	
	public void removeFromQueue(Champion c) {
		PriorityQueue temp = new PriorityQueue(6);  //create a temp queue that will hold the turn order champions
													//but without the dead one
		while(!turnOrder.isEmpty()) {
			Champion ch = (Champion)turnOrder.remove();
			if(ch!=c)								//if that champion is not the one who died then insert him to the queue
				temp.insert(ch);
		}
		while(!temp.isEmpty())  					// put the champions back to the turn order queue
			turnOrder.insert(temp.remove());	
	}
	
	public void eliminateIfDead(Damageable damaged) throws IOException { 
		if(damaged.getCurrentHP()==0) { 				//If the champion's HP is zero then he should be eliminated
			if(damaged instanceof Champion) {				
				removeFromTeam((Champion) damaged);		//If he is a champion then he has to be removed from his corresponding team
				removeFromQueue((Champion) damaged);	//We also need to remove him from the turn order queue as well
				listener.updateTurnOrderInfo(turnOrder);
				listener.updateAbilityInfo((Champion) damaged);
			}
			// Nullify the dead object on the board (if the object is a cover then it will be nullified also)
			// Because when the cover's HP reaches 0 then it has to be destroyed
			int x = (int) damaged.getLocation().getX();
			int y = (int) damaged.getLocation().getY();
			board[x][y]=null;
			listener.clearLabel(damaged,damaged.getLocation());
		}
			
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void prepareChampionTurns() {
		
		for(Champion ch : firstPlayer.getTeam()) {  // put the champions of first player in the queue
			if(ch.getCondition() != Condition.KNOCKEDOUT) // if the champion is not eliminated then put him in the queue
				this.turnOrder.insert(ch);
		}
		
		for(Champion ch : secondPlayer.getTeam()) { // Do same steps for the second player
			if(ch.getCondition()!=Condition.KNOCKEDOUT)
				this.turnOrder.insert(ch);
		}
		
	}
	
	
	public ArrayList<Damageable> getFriendlyInRange(Champion c, int range) {
	   	 ArrayList<Champion> allFriendly = new ArrayList<Champion>();
	   	 ArrayList<Damageable> list = new ArrayList<Damageable>();
	   	 
	   	 if(firstPlayer.getTeam().contains(c))
	   		 allFriendly = firstPlayer.getTeam();
	   	 else 
	   		 allFriendly = secondPlayer.getTeam();
	   	 for (Champion champ : allFriendly) {
	   		 if(manhatan(champ.getLocation(),c.getLocation())<=range && c!=champ)
	   			 list.add((Damageable) champ);
	   	 }
	   	 return list;
   	}
    
    public ArrayList<Damageable> getEnemyInRange(Champion c, int range)
    {
	   	 ArrayList<Damageable> list = new ArrayList<Damageable>();
	   	 ArrayList<Champion> allEnemy = new ArrayList<Champion>();
	   	 
	   	 if(firstPlayer.getTeam().contains(c))
	   		 allEnemy = secondPlayer.getTeam();
	   	 else 
	   		 allEnemy = firstPlayer.getTeam();
	   	 for (Champion champ : allEnemy) {
	   		 if(manhatan(champ.getLocation(),c.getLocation())<=range)
	   			 list.add((Damageable) champ);
				
			}
			return list;
   	
    }

	
}
