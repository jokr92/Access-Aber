package database;

import java.util.Comparator;

/**
 * Holds all the required fields used to build Nodes
 * @author Jostein Kristiansen(jok13)
 *@see database.OSMNode
 *@see database.OSMWay
 */

public /*abstract*//*Can't instantiate an abstract class*/ interface Node extends Comparator<Node>{

	/**
	 * TODO Is this ever needed, or do I always use the external (OSM) ID?
	 * Represents this Node's position in the list of Nodes
	 * Think of it as this Node's unique local key
	 * @return this Node's key
	 */
	long getId();
	
	/**
	 * TODO Is this ever needed, or do I always use the external (OSM) ID?
	 * Changes this Node's local key
	 * Should move any Node already occupying this index in the list of Nodes
	 * @param localID the key to assign to this Node. Should move any conflicting Node to a different index.
	 */
	void setId(long localID);
	
	/**
	 * 
	 * @return This Node's external id, as it appears in the data read by {@link database.BuildDatabase}
	 */
	String getExternalId();

	/**
	 * Changes this Node's external id, as it appears in the data read by {@link database.BuildDatabase}
	 * @param id The ID to assign to this Node; Should be unique.
	 */
	void setExternalId(String id);

	double getLatitude();

	void setLatitude(double lat);

	double getLongitude();

	void setLongitude(double lon);
	
	/**
	 * Indicates whether a Node is part of more than one Way, i.e that it is a junction-Node
	 * @return true if the Node is part of more than one Way; false otherwise
	 */
	boolean isTowerNode();
	/**
	 * Controls the Node's level of importance. Tower Nodes act as connections between Ways, and are therefore more relevant when planning routes
	 * @param isTower true if the Node is part of more than one Way; false otherwise
	 */
	void setTowerNode(boolean isTower);

	@Override
	public default int compare(Node n1, Node n2) {
		return (n1.getExternalId().toUpperCase()).compareTo((n2.getExternalId().toUpperCase()));
	}
}
