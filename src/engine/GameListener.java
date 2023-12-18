package engine;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;

import model.abilities.Ability;
import model.world.Champion;
import model.world.Cover;
import model.world.Damageable;

public interface GameListener {
	
	public void clearLabel(Damageable d,Point location) throws IOException;
	public void drawCover(Cover cover ) throws IOException ;
	public void drawChampion(Champion c) throws IOException ;
	public void updateChampionInfo(Champion c);
	public void updateAbilityInfo(Champion c);
	public void updateCoverHp(Cover c);
	public void generateCoverDamageSound();
	public void generateAttackedSound();
	public void updateLeaderAbilityUsed(Champion c);
	public void updateCurrentName(Champion c);
	public void putCurrentHighlight(Point location);
	public void removeCurrentHighlight(Point location);
	public void updateTurnOrderInfo(PriorityQueue q);
	public void updateAbilityComboBox(ArrayList<Ability> abilities);

}
