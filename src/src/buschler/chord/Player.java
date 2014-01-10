package buschler.chord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import buschler.chord.Fleet.radar;
import de.uniba.wiai.lspi.chord.com.Broadcast;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Player implements NotifyCallback {

	private ChordImpl node;
	private List<Fleet> ocean;
	private Fleet mightyArmada;
	
	public Player() {

	}
	
	@Override
	public void retrieved(ID target) {
		System.out.println("Die Schweine!!! Sie haben Kenny getötet!!!!!!");
		System.out.println(target);
		System.out.println(node);
		node.broadcast(target, handleAttack(target));
		fire();
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		// TODO Auto-generated method stub
		//TEEST
	}

	public ChordImpl getNode() {
		return node;
	}

	public void setNode(ChordImpl node) {
		this.node = node;
	}

	public void initFleets(){
		mightyArmada = new Fleet(node.getID(), node.getPredecessorID());
		ocean = new ArrayList<Fleet>();
		int i = 0;
		while (i < mightyArmada.getS()) {
			int rnd = (int)Math.random()*mightyArmada.getI();
			if (mightyArmada.getFleetDeployment(rnd) == radar.UNKNOWN) {
				mightyArmada.setFleetDeployment(rnd, radar.HIT);
				i++;
			}
		}
		
		Node[] fingerTable = node.getFingerTable().toArray(new Node[node.getFingerTable().size()]);
		fingerTable = FingerTableSort.sort(fingerTable, node.getID());
		ID startNode = node.getID();
		ID endNode;
		for (i = 0; i < fingerTable.length; i++) {
			endNode = fingerTable[i].getNodeID();
			ocean.add(new Fleet(endNode, startNode));
			startNode = endNode;
		}
		if (!checkParticipant(node.getPredecessorID())) {
			ocean.add(new Fleet(node.getPredecessorID(), startNode));
		}
	}
	
	private boolean checkParticipant(ID id){
		boolean result = false;
		for (int i = 0; i < ocean.size() && result != true; i++) {
			result = (ocean.get(i).getIdEnd().compareTo(id.toBigInteger()) == 0);
		}
		return result;
	}
	
	private void newEnemyFleetDetected(){
		//NOT YET		
	}
	
	private boolean handleAttack(ID target){
		return mightyArmada.handleAttackOnMightyAmarda(target);
	}
	
	private void handleAttackOnEnemie(){
		//NOT YET		
	}
	
	private void fire(){
		Fleet target = null;
		for (Fleet rabbitFleet : ocean) {
			if (target == null || target.getNumberOfHits() < rabbitFleet.getNumberOfHits()) {
				target = rabbitFleet;
			}
		}
		for (int i = 1; i <= target.getI(); i++) {
			if(target.getFleetDeployment(i) == radar.UNKNOWN){
				node.retrieve(target.calculateIDFromField(i));
			}
		}
	}
	
	private void victory(){
		
	}
}
