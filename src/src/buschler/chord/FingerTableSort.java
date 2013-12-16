package buschler.chord;

import java.util.Arrays;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;

public class FingerTableSort {

	public static Node[] sort(Node[] nodes, ID initNode) {
		int i;
		Node[] sortedNodes = new Node[nodes.length];
		
		Arrays.sort(nodes);
		
		for (i = 0; i < nodes.length; i++) {
			if (nodes[i].getNodeID().compareTo(initNode) == 1) {
				break;
			}
		}
		
		if (i < nodes.length) {
			for (int j = 0; j < sortedNodes.length; j++) {
				sortedNodes[j] = nodes[i];
				if (++i == nodes.length) {
					i = 0;
				}
			}			
		} else {
			sortedNodes = nodes;
		}
		
		return sortedNodes;
		
	}
	
}
