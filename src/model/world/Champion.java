package model.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import model.abilities.Ability;
import model.effects.Effect;

@SuppressWarnings("rawtypes")
public abstract class Champion implements Damageable,Comparable {
	private String name;
	private int maxHP;
	private int currentHP;
	private int mana;
	private int maxActionPointsPerTurn;
	private int currentActionPoints;
	private int attackRange;
	private int attackDamage;
	private int speed;
	private ArrayList<Ability> abilities;
	private ArrayList<Effect> appliedEffects;
	private Condition condition;
	private Point location;
	

	public Champion(String name, int maxHP, int mana, int actions, int speed, int attackRange, int attackDamage) {
		this.name = name;
		this.maxHP = maxHP;
		this.mana = mana;
		this.currentHP = this.maxHP;
		this.maxActionPointsPerTurn = actions;
		this.speed = speed;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.condition = Condition.ACTIVE;
		this.abilities = new ArrayList<Ability>();
		this.appliedEffects = new ArrayList<Effect>();
		this.currentActionPoints=maxActionPointsPerTurn;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public String getName() {
		return name;
	}

	public void setCurrentHP(int hp) {

		if (hp <= 0) {
			currentHP = 0;
			condition=Condition.KNOCKEDOUT;
			
		} 
		else if (hp > maxHP)
			currentHP = maxHP;
		else
			currentHP = hp;

	}

	
	public int getCurrentHP() {

		return currentHP;
	}

	public ArrayList<Effect> getAppliedEffects() {
		return appliedEffects;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getAttackDamage() {
		return attackDamage;
	}

	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int currentSpeed) {
		if (currentSpeed < 0)
			this.speed = 0;
		else
			this.speed = currentSpeed;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point currentLocation) {
		this.location = currentLocation;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public ArrayList<Ability> getAbilities() {
		return abilities;
	}

	public int getCurrentActionPoints() {
		return currentActionPoints;
	}

	public void setCurrentActionPoints(int currentActionPoints) {
		if(currentActionPoints>maxActionPointsPerTurn)
			currentActionPoints=maxActionPointsPerTurn;
		else 
			if(currentActionPoints<0)
			currentActionPoints=0;
		this.currentActionPoints = currentActionPoints;
	}

	public int getMaxActionPointsPerTurn() {
		return maxActionPointsPerTurn;
	}

	public void setMaxActionPointsPerTurn(int maxActionPointsPerTurn) {
		this.maxActionPointsPerTurn = maxActionPointsPerTurn;
	}

	public int compareTo(Object o)
	{
		Champion c = (Champion)o;
		if(speed==c.speed)
			return name.compareTo(c.name);
		return -1 * (speed-c.speed);
	}
	
	public abstract void useLeaderAbility(ArrayList<Champion> targets);
	
	public String getType() {
		if( name.equals("Captain America") ||
			name.equals("Dr Strange")||
			name.equals("Hulk")||
			name.equals("Iceman")||
			name.equals("Ironman")||
			name.equals("Spiderman")||
			name.equals("Thor") )	
				return "Hero";
		if( name.equals("Electro")||
			name.equals("Hela")||	
			name.equals("Loki")||
			name.equals("Quicksilver")||
			name.equals("Yellow Jacket") )
				return "Villain";
		
		return "Anti-Hero";
	}
	public String toString() {
		String total = "Name : "+name+"\n";
		total+="Type : "+getType()+"\n";
		total+="Max HP : "+maxHP+"\n";
		total+="Current HP : "+currentHP+"\n";
		total+="Mana : "+mana+"\n";
		total+="Max Points : "+maxActionPointsPerTurn+"\n";
		total+="Current Points : "+currentActionPoints+"\n";
		total+="Attack Range : "+attackRange+"\n";
		total+="Attack Damage : "+attackDamage+"\n";
		total+="Speed : "+speed+"\n";
		return total;
	}
	
	public String printChampionInfo() {
		String s = "";
		s+= this.toString()+"Condition: "+condition+"\n"+"Applied Effects: ";
		for(Effect e : appliedEffects)
			s+="\n"+e.toString();
		return s;
	}
	
	public String printAbilityInfo() {
		String s = "";
		for(Ability a : abilities)
			s+=a.toString()+"\n";
		return s;
	}
	
	public Effect hasEffect(String name) {
		for(Effect ef : this.appliedEffects) { 
			if(ef.getName().equals(name)) 
				return ef;
		}
		return null;					
	}
	
	
	// Method to update the cool down of the abilities per turn
	// this method is called whenever a new turn takes action
	public void updateAbilitiesTimer(){ 
		for(Ability a : this.getAbilities()) 
			a.setCurrentCooldown(a.getCurrentCooldown()-1);
	}
	
	public void removeExpiredEffects() { // method to remove the expired effects from a champion
		Iterator<Effect> itr = this.getAppliedEffects().iterator();
		while(itr.hasNext()) {
			Effect ef = itr.next();
			ef.setDuration(ef.getDuration()-1);
			if(ef.getDuration()==0) {
				itr.remove();
				ef.remove(this);
			}
		}
	}
}
