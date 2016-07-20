package database;

import java.util.Comparator;

/**
 * Holds all the required fields used to build Nodes
 * @author Jostein Kristiansen(jok13)
 *@see database.OSMNode
 *@see database.OSMWay
 */

public /*abstract*//*Can't instantiate an abstract class*/ interface Node extends Comparator<Node>{

	String getId();

	void setId(String id);

	double getLatitude();

	void setLatitude(double lat);

	double getLongitude();

	void setLongitude(double lon);

	@Override
	public default int compare(Node n1, Node n2) {
		return n1.getId().compareTo(n2.getId());
	}
}
