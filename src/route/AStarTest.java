package route;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import database.BuildDatabase;
import database.DistanceMetricNode;
import database.Node;
import database.SearchDatabase;
/**
 * 
 * @author Jostein Kristiansen(jok13)
 *@see route.AStar
 */
public class AStarTest {
	
	static DistanceMetricNode startNode;
	static DistanceMetricNode goalNode;

	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes()==null||BuildDatabase.getWays()==null){
			BuildDatabase.readConfig("map.osm");
		}

		startNode=(DistanceMetricNode) SearchDatabase.searchForNode(1);
		goalNode=(DistanceMetricNode) SearchDatabase.searchForNode(20);
		
		AStar.setStartNode(startNode);
		AStar.setGoalNode(goalNode);
	}

	@Test
	public void shouldGetDistanceBetweenTwoPoints(){
		assertTrue(AStar.distanceBetweenPoints(3.2, 2, 1, 4.1)!=4.3);
		assertTrue(AStar.distanceBetweenPoints(3.2, 2, 1, 4.1)!=0);
		assertTrue(AStar.distanceBetweenPoints(1, 4.1, 3.2, 2)!=4.3);
		assertTrue(AStar.distanceBetweenPoints(1, 4.1, 3.2, 2)!=0);

		/*Makes sure that all distances are positive(CRUCIAL FOR PATH-COST CALCULATIONS)*/
		assertTrue(AStar.distanceBetweenPoints(1, 1, 1, 1)>0);
		assertTrue(AStar.distanceBetweenPoints(-4, -3, -2, -1)>0);
	}

	@Test
	public void ShouldExpandTheStartNodeAndSortTheResultingChildrenByTheirProximityToTheGoalNode(){
		List<DistanceMetricNode> unsortedTestNodes = new ArrayList<DistanceMetricNode>();
		List<DistanceMetricNode> sortedTestNodes = new ArrayList<DistanceMetricNode>();

		for(Node n:AStar.getNavigatableConnectedNodes(startNode.getId())){
			//TODO Is it safe to cast like this?
			unsortedTestNodes.add((DistanceMetricNode) n);
		}

		sortedTestNodes=AStar.sortByDistance(sortedTestNodes,unsortedTestNodes);
		for(int i=sortedTestNodes.size()-1;i>0;i--){
			assertTrue((sortedTestNodes.get(i).getDistanceTravelled()+sortedTestNodes.get(i).getDistanceToGoal())
					<=(sortedTestNodes.get(i).getDistanceTravelled()+sortedTestNodes.get(i).getDistanceToGoal()));
			/*First Node*///System.out.println("\n"+i+": "+AStar.distanceBetweenPoints(sortedTestNodes.get(i).getLatitude(), sortedTestNodes.get(i).getLongitude(), AStar.getGoalNode().getLatitude(), AStar.getGoalNode().getLongitude()));
			/*Next Node*///System.out.println(i+": "+AStar.distanceBetweenPoints(sortedTestNodes.get(i-1).getLatitude(), sortedTestNodes.get(i-1).getLongitude(), AStar.getGoalNode().getLatitude(), AStar.getGoalNode().getLongitude()));

		}
	}

	@Test
	/**
	 * TODO Skips a step when printing the route in AStar.getPath(). Why?
	 * It is the exact same as "ShouldOnlyAddNodesToRouteOnce()", but the issue is only here... WHY?
	 * start:1 goal:20
	 * 305025164-305024276-/304928999/-295134062
	 */
	public void ShouldFindRouteFromOneNodeToAnother(){
		List<Node> path = new ArrayList<Node>();
		
		path=AStar.search(startNode, goalNode);
		
		assertFalse(path.isEmpty());
		assertTrue(path.size()>2);//i.e contains more Nodes than just the start and goal nodes
		assertTrue(path.get(0).equals(goalNode));
		assertTrue(path.get(path.size()-1).equals(startNode));
	}

	@Test
	public void ShouldOnlyAddNodesToRouteOnce(){
		List<Node> path = new ArrayList<Node>();
		
		path=AStar.search(startNode, goalNode);

		if(path.size()>1){
			for(int i=0;i<path.size();i++){
				for(int j=i+1;j<path.size();j++){
					assertFalse(path.get(i).equals(path.get(j)));
				}
			}
		}else if(path.size()==1){
			fail("Only one Node in this route; unable to make comparisons");
		}
		else{
			fail("No Nodes in this route");
		}
	}
}
