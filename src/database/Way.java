package database;

//import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Holds all the required fields used to build Ways containing Nodes
 * @author Jostein Kristiansen(jok13)
 *@see database.OSMNode
 *@see database.OSMWay
 */

public /*abstract*//*Can't instantiate an abstract class*/ interface Way extends Comparator<Way>{
	
	/**
	 * Represents this Way's position in the list of Ways
	 * Think of it as this Way's unique local key
	 * @return this Way's key
	 */
	int getlocalId();
	
	/**
	 * Changes this Way's local key
	 * Should move any Way already occupying this index in the list of Ways
	 * @param localID the key to assign to this Way. Should move any conflicting Way to a different index.
	 */
	void setLocalId(int localID);
	
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
	List<Node> getNodeRelations();
	
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
	
	void setKeyValuePairs(Map<String, Object> keyValuePairs);
	void addKeyValuePair(String key, Object value);
	Set<Entry<String, Object>> getKeyValuePairs();
	
	@Override
	public default int compare(Way w1, Way w2) {
		return w1.getId().compareTo(w2.getId());
	}
}
