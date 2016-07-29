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
	 * Represents this Node's position in the list of Nodes
	 * Think of it as this Node's unique local key
	 * @return this Node's key
	 */
	int getlocalId();
	
	/**
	 * Changes this Node's local key
	 * Should move any Node already occupying this index in the list of Nodes
	 * @param localID the key to assign to this Node. Should move any conflicting Node to a different index.
	 */
	void setLocalId(int localID);
	
	String getId();

	void setId(String id);

	double getLatitude();

	void setLatitude(double lat);

	double getLongitude();

	void setLongitude(double lon);

	@Override
	public default int compare(Node n1, Node n2) {
		System.out.println((n1.getId().toUpperCase()).compareTo((n2.getId().toUpperCase())));
		return (n1.getId().toUpperCase()).compareTo((n2.getId().toUpperCase()));
	}
}
