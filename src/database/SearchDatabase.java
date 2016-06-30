package database;

import java.util.ArrayList;
import java.util.List;

import route.AStar;

/**
 * Makes it possible to search through the databases in {@link database.BuildDatabase} for specific entries, and filter out the entries that are unfit for navigation before they are used for pathfinding
 * @author Jostein Kristiansen(jok13)
 *
 */

public class SearchDatabase{
	
	/**
	 * Searches for a specified node in {@link database.BuildDatabase#getNodes()}.
	 * @param nodeID The ID of the node to search for
	 * @return The node if found, null otherwise.
	 */
	public static Node searchForNode(final String nodeID){
		for(Node node:BuildDatabase.getNodes()){
			if (node.getId().equals(nodeID)){
				return node;
			}
		}
		return null;
	}

	/**
	 * Searches through the list {@link database.BuildDatabase#getWays()} for a specific node(the parent), and returns the ways that are related to it(the children).
	 * @param nodeToFind The ID of the node to search for
	 * @return An unordered list of every Way that contains this Node
	 */
	public static List<Way> getWaysContainingNode(final String nodeToFind){
		List<Way> matches = new ArrayList<Way>();
		
		for(Way way:BuildDatabase.getWays()){
			for(Node node:way.getNodeRelations()){
				if(node.getId().equals(nodeToFind)){
					matches.add(way);
					//matches.addAll(way.getNodeRelations());//returns all nodes in this way
					break;
				}
			}
		}
		return matches;
		}
	
	/**
	 *TODO Currently, the filter is: Key=highway && Value!=steps. Is it possible to create a better filter? Maybe it should be possible to cross open spaces like k=amenity v=parking?
	 * Filters out the Ways that are not marked with a highway key (ie. any Way that is not a road, path, stair, etc.).
	 * This is meant to aid in restricting the nodes used for pathfinding, as not all nodes are actually meant for navigation.
	 * @param wayList List of Ways to run the filter on.
	 * @return A list of nodes that can be used for navigation.
	 */
	public static List<Node> filterAccessibleWays(final List<Way> wayList){
		List<Node> navigatableNodes = new ArrayList<Node>();
		for(Way way:wayList){
			try{
				//Only adds a Way to the list if it is a highway(eg. a road, path, etc.), and it is not stairs
			if(way.getKey().equals(Keys.KEY_HIGHWAY.getKey())&&(!way.getValue().equals(Keys.VALUE_STEPS.getKey()))){
			navigatableNodes.addAll(way.getNodeRelations());
			}//else{System.out.println(way.getKey() +", "+way.getValue() + " is not suitable for navigation");}
			}catch(NullPointerException e){
				//in case a Key or Value is null; which can happen in the OSM database.
			}
		}
		return navigatableNodes;
	}
	
	/**
	 * Searches through the database of Nodes {@link database.BuildDatabase #getNodes()} for the Node closest to the input coordinates
	 * @param lat degrees min -90, max 90
	 * @param lon degrees min -180, max 180
	 * @return The Node closest to the coordinates specified in the input
	 */
	public static Node findClosestNode(double lat, double lon){
		Node closestNode=null;
		double distance=Double.POSITIVE_INFINITY;
		for(Node node:BuildDatabase.getNodes()){
			if(AStar.distanceBetweenPoints(node.getLatitude(), node.getLongitude(), lat, lon)<distance){
				closestNode=node;
				distance=AStar.distanceBetweenPoints(node.getLatitude(), node.getLongitude(), lat, lon);
			}
		}
		return closestNode;
	}
}
