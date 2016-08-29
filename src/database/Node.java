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
	
	String getExternalId();

	/**
	 * Changes this Node's external id
	 * @param id The ID to assign to this Node; Should be unique.
	 */
	void setExternalId(String id);

	double getLatitude();

	void setLatitude(double lat);

	double getLongitude();

	void setLongitude(double lon);

	@Override
	public default int compare(Node n1, Node n2) {
		return (n1.getExternalId().toUpperCase()).compareTo((n2.getExternalId().toUpperCase()));
	}
}
