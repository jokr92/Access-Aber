package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import route.Search;

/**
 * Makes it possible to search through the databases in {@link database.BuildDatabase} for specific entries, and filter out the entries that are unfit for navigation before they are used for pathfinding
 * @author Jostein Kristiansen(jok13)
 *
 */

public class SearchDatabase{

	/**
	 * Searches for a specified Node in {@link database.BuildDatabase#getNodes()}.
	 * @param nodeKey The key of the Node to search for
	 * @return The Node if found, null otherwise.
	 */
	public static Node searchForNode(final int nodeKey){
		try{
			return BuildDatabase.getNodes()[nodeKey];
		}catch(ArrayIndexOutOfBoundsException e){
			//TODO Is it a bit silly to catch a NPE instead of just avoiding it?
			return null;
		}
	}

	/**
	 * Searches for a specified Way in {@link database.BuildDatabase#getWays()}.
	 * @param wayKey The key of the Way to search for
	 * @return The Way if found, null otherwise.
	 */
	public static Way searchForWay(final int wayKey){
		try{
			return BuildDatabase.getWays()[wayKey];
		}catch(ArrayIndexOutOfBoundsException e){
			//TODO Is it a bit silly to catch a NPE instead of just avoiding it?
			return null;
		}
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
				if(node.getExternalId().equals(nodeToFind)){
					matches.add(way);
					//matches.addAll(way.getNodeRelations());//returns all nodes in this way
					break;
				}
			}
		}
		return matches;
	}

	/**
	 * Searches through the list {@link database.BuildDatabase#getWays()} for a specific set of nodes, and returns only the ways that contain all of them.
	 * @param nodeToFind The IDs of the nodes to search for
	 * @return An unordered list of every Way that contain the complete set of Nodes
	 */
	public static List<Way> getWaysContainingNode(final List<String> nodeToFind){
		List<Way> matches = new ArrayList<Way>();

		for(Way way:BuildDatabase.getWays()){
			int matchCounter = 0;
			for(Node node:way.getNodeRelations()){
				if(nodeToFind.contains(node.getExternalId())){
					matchCounter++;

					if(matchCounter>=nodeToFind.size()){
						matches.add(way);
						//matches.addAll(way.getNodeRelations());//returns all nodes in this way
						break;
					}
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
	public static List<Way> filterAccessibleWays(final List<Way> wayList){
		List<Way> navigatableWays = new ArrayList<Way>();
		for(Way way:wayList){
			try{
				//Only adds a Way to the list if it is a highway(eg. a road, path, etc.), and it is not stairs
				boolean accessible=false;
				boolean inaccessible=false;
				for(Entry<String, Object> dbPair:way.getKeyValuePairs()){
					for(DisallowedKeys disallowedTag:DisallowedKeys.values()){
						if(dbPair.getKey().equals(disallowedTag.getKey())&&dbPair.getValue().equals(disallowedTag.getValue())){
							accessible=false;
							inaccessible=true;
							break;
						}
					}
					
					if(inaccessible){
						accessible=false;
						System.out.println("Way "+way.getExternalId()+": "+dbPair.getKey() +"-"+ dbPair.getValue() + " is not suitable for navigation");
						break;
					}
					
					for(PermittedKeys permittedTag:PermittedKeys.values()){
						if(dbPair.getKey().equals(permittedTag.getKey())&&dbPair.getValue().equals(permittedTag.getValue())){
							accessible=true;
							break;
						}
					}
				}
				if(accessible){
					navigatableWays.add(way);
					System.out.println("Way "+way.getExternalId()+": IS navigable");
				}
			}catch(NullPointerException e){
				//in case a Key or Value is null; which can happen in the OSM database.
			}
		}
		System.out.print("\n");
		return navigatableWays;
	}

	/**
	 *TODO Currently, the filter is: Key=highway && Value!=steps. Is it possible to create a better filter? Maybe it should be possible to cross open spaces like k=amenity v=parking?
	 * Filters out the Ways that are not marked with a highway key (ie. any Way that is not a road, path, stair, etc.).
	 * This is meant to aid in restricting the nodes used for pathfinding, as not all nodes are actually meant for navigation.
	 * @param wayList List of Ways to run the filter on.
	 * @return A list of Nodes that can be used for navigation.
	 */
	public static List<Node> filterAccessibleNodes(final List<Way> wayList){
		List<Node> navigatableNodes = new ArrayList<Node>();
		for(Way way:filterAccessibleWays(wayList)){
			//Only adds a Node to the list if it is part of a highway(eg. a road, path, etc.), and it is not stairs
			navigatableNodes.addAll(way.getNodeRelations());			
		}
		return navigatableNodes;
	}

	/**
	 * Searches through the database of Nodes {@link database.BuildDatabase #getNodes()} for the Node closest to the input coordinates
	 * TODO The node found may not be accessible or connected to any other nodes; I should fix this
	 * @param lat degrees min -90, max 90
	 * @param lon degrees min -180, max 180
	 * @return The Node closest to the coordinates specified in the input
	 */
	public static Node findClosestNode(double lat, double lon){
		Node closestNode=null;
		double shortestDistance=Double.POSITIVE_INFINITY;
		double currentDistance=Double.POSITIVE_INFINITY;

		for(Node node:BuildDatabase.getNodes()){
			currentDistance=Search.distanceBetweenPoints(node.getLatitude(), node.getLongitude(), lat, lon);
			if(currentDistance<shortestDistance){
				closestNode=node;
				shortestDistance=currentDistance;
			}
		}
		return closestNode;
	}

	/**
	 * Searches for the Nodes connected to the input, and returns only the Nodes fit for navigation, and the Nodes connected to the goal(regardless) - if found
	 * @param parentNode Node to expand (find references to)
	 * @return The children of the parent. I.e every Node in a Way related to this Node
	 * @see database.SearchDatabase #FilterAccessibleWays(List)
	 */
	public static List<Node> getNavigatableConnectedNodes(Node parentNode){
		List<Way> wayList=getWaysContainingNode(parentNode.getExternalId());
		List<Node>nodeList= new ArrayList<Node>();//This can remove a Way containing the goal... Do I want this?

		for(Node n:filterAccessibleNodes(wayList)){
			if(n!=parentNode){
				nodeList.add(n);
			}
		}

		return nodeList;
	}
}
