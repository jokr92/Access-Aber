package route;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import database.BuildDatabase;
import database.DistanceMetricNode;
import database.Node;
import database.OSMNode;
import database.SearchDatabase;
/**
 * 
 * @author Jostein Kristiansen(jok13)
 *@see route.AStar
 */
public class AStarTest {

	static DistanceMetricNode startNode;
	static DistanceMetricNode goalNode;
	AStar aStar;

	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes()==null||BuildDatabase.getWays()==null){
			BuildDatabase.readConfig("map.osm");
		}

		startNode=(DistanceMetricNode) SearchDatabase.searchForNode(1);
		goalNode=(DistanceMetricNode) SearchDatabase.searchForNode(20);
	}
	
	@Before
	public void initialiseAstar(){
		aStar = new AStar();
		AStar.setStartNode(startNode);
		AStar.setGoalNode(goalNode);
	}

	@Test
	public void shouldGetDistanceBetweenTwoPoints(){
		assertTrue(Search.distanceBetweenPoints(3.2, 2, 1, 4.1)!=4.3);
		assertTrue(Search.distanceBetweenPoints(3.2, 2, 1, 4.1)!=0);
		assertTrue(Search.distanceBetweenPoints(1, 4.1, 3.2, 2)!=4.3);
		assertTrue(Search.distanceBetweenPoints(1, 4.1, 3.2, 2)!=0);

		/*Makes sure that all distances are positive(CRUCIAL FOR PATH-COST CALCULATIONS)*/
		assertTrue(Search.distanceBetweenPoints(1, 1, 1, 1)>0);
		assertTrue(Search.distanceBetweenPoints(-4, -3, -2, -1)>0);
	}

	@Test
	public void ShouldExpandTheStartNodeAndSortTheResultingChildrenByTheirProximityToTheGoalNode(){
		List<DistanceMetricNode> unsortedTestNodes = new ArrayList<DistanceMetricNode>();
		List<DistanceMetricNode> sortedTestNodes = new ArrayList<DistanceMetricNode>();

		for(Node n:SearchDatabase.getNavigatableConnectedNodes(startNode)){
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
	public void ShouldFindRouteFromOneNodeToAnother(){
		List<Node> path = new ArrayList<Node>();

		path=aStar.findPath(startNode, goalNode);

		assertFalse(path.isEmpty());
		assertTrue(path.size()>2);//i.e contains more Nodes than just the start and goal nodes
		assertTrue(path.get(0).equals(goalNode));
		assertTrue(path.get(path.size()-1).equals(startNode));
	}

	@Test
	public void ShouldOnlyAddNodesToRouteOnce(){
		List<Node> path = new ArrayList<Node>();

		path=aStar.findPath(startNode, goalNode);

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
	
	@Test
	/**
	 * This test expands every node connected to the start node in the search-space.
	 * Run-time may rise significantly the more nodes there are in the search-space.
	 */
	public void ShouldNotFindPathToUnconnectedNode(){
		List<Node> path = new ArrayList<Node>();
		
		OSMNode gNode=new OSMNode();

		path=aStar.findPath(startNode, gNode);
		
		assertTrue(path.isEmpty());
	}
	
	@Test
	public void ShouldNotFindPathFromUnconnectedNode(){
		List<Node> path = new ArrayList<Node>();
		
		OSMNode sNode=new OSMNode();

		path=aStar.findPath(sNode, goalNode);
		
		assertTrue(path.isEmpty());
	}
}
