package database;

//import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Holds all the required fields used to build Ways containing Nodes
 * @author Jostein Kristiansen(jok13)
 *@see database.OSMNode
 *@see database.OSMWay
 */

/*abstract*//*Can't instantiate an abstract class*/ interface Way extends Comparator<Way>{
	
	/**
	 * @return the id
	 */
	String getId();
	/**
	 * @param id the id to set
	 */
	void setId(String id);
	
	/**
	 * Replaces the current list of Node-Relations with the list it receives
	 * @param nodeRelations the nodeRelations to set
	 */
	void setNodeRelations(List<Node> nodeRelations);

	/**
	 * 
	 * @return The complete list of Node-Relations in this Way
	 */
	List<OSMNode> getNodeRelations();
	
	/**
	 * Adds a single Node to the list of Node-Relations inside this Way
	 * @param node The Node to add
	 */
	void addNodeRelation(Node node);
	
	/**
	 * Removes the Node specified by the input
	 * TODO Every implementation of this method should remove all occurrences of the Node in question
	 * @param node The Node to remove
	 */
	void removeNodeRelation(Node node);
	
	@Override
	public default int compare(Way w1, Way w2) {
		return w1.getId().compareTo(w2.getId());
	}
}
