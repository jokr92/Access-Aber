package database;

//import java.util.ArrayList;
import java.util.Comparator;
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
	 * TODO Is this ever needed, or do I always use the external (OSM) ID?
	 * Represents this Way's position in the list of Ways
	 * Think of it as this Way's unique local key
	 * @return this Way's key
	 */
	long getId();
	
	/**
	 * TODO Is this ever needed, or do I always use the external (OSM) ID?
	 * Changes this Way's local key
	 * Should move any Way already occupying this index in the list of Ways
	 * @param localID the key to assign to this Way. Should move any conflicting Way to a different index.
	 */
	void setId(long localID);
	
	/**
	 * @return this Way's external id
	 */
	String getExternalId();
	/**
	 * @param id the external id to associate this Way with
	 */
	void setExternalId(String externalId);
	
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
	 * TODO Every implementation of this method should remove all occurrences of the Node in question, not just the first
	 * @param node The Node to remove
	 */
	void removeNodeRelation(Node node);
	
	void setKeyValuePairs(Map<String, Object> keyValuePairs);
	void addKeyValuePair(String key, Object value);
	Set<Entry<String, Object>> getKeyValuePairs();
	
	@Override
	public default int compare(Way w1, Way w2) {
		return w1.getExternalId().compareTo(w2.getExternalId());
	}
}
