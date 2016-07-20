package route;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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

	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes().isEmpty()||BuildDatabase.getWays().isEmpty()){
			BuildDatabase.readConfig("map.osm");
		}
	}

	@Test
	public void shouldGetDistanceBetweenTwoPoints(){
		assertTrue(AStar.distanceBetweenPoints(3.2, 2, 1, 4.1)==4.3);
		assertTrue(AStar.distanceBetweenPoints(1, 4.1, 3.2, 2)==4.3);

		/*Makes sure that all distances are positive(CRUCIAL FOR PATH-COST CALCULATIONS)*/
		assertTrue(AStar.distanceBetweenPoints(1, 1, 1, 1)>0);
	}

	@Test
	public void ShouldExpandTheStartNodeAndSortTheResultingChildrenByTheirProximityToTheGoalNode(){
		List<DistanceMetricNode> unsortedTestNodes = new ArrayList<DistanceMetricNode>();
		List<DistanceMetricNode> sortedTestNodes = new ArrayList<DistanceMetricNode>();

		AStar.setStartNode((DistanceMetricNode) SearchDatabase.searchForNode("2641099872"));
		AStar.setGoalNode((DistanceMetricNode) SearchDatabase.searchForNode("3274334109"));

		OSMNode startNode=(OSMNode) AStar.getStartNode();

		for(Node n:AStar.getNavigatableConnectedNodes(startNode.getId())){
			//TODO Is it safe to cast like this?
			unsortedTestNodes.add((DistanceMetricNode) n);
		}

		sortedTestNodes=AStar.sortByDistance(sortedTestNodes,unsortedTestNodes);

		for(int i=sortedTestNodes.size()-1;i>0;i--){
			assertTrue(AStar.distanceBetweenPoints(sortedTestNodes.get(i).getLatitude(), sortedTestNodes.get(i).getLongitude(), AStar.getGoalNode().getLatitude(), AStar.getGoalNode().getLongitude())
					<=(AStar.distanceBetweenPoints(sortedTestNodes.get(i-1).getLatitude(), sortedTestNodes.get(i-1).getLongitude(), AStar.getGoalNode().getLatitude(), AStar.getGoalNode().getLongitude())));
			//First Node//System.out.println(AStar.distanceBetweenPoints(sortedTestNodes.get(i).getLatitude(), sortedTestNodes.get(i).getLongitude(), AStar.getGoalNode().getLatitude(), AStar.getGoalNode().getLongitude()));
			//Next Node//System.out.println(AStar.distanceBetweenPoints(sortedTestNodes.get(i-1).getLatitude(), sortedTestNodes.get(i-1).getLongitude(), AStar.getGoalNode().getLatitude(), AStar.getGoalNode().getLongitude()));

		}
	}

	@Test
	public void ShouldFindShortestRouteFromOneNodeToAnother(){
		List<DistanceMetricNode> path = new ArrayList<DistanceMetricNode>();
		AStar.setStartNode((DistanceMetricNode) SearchDatabase.searchForNode("2947828308"));
		AStar.setGoalNode((DistanceMetricNode) SearchDatabase.searchForNode("2947828315"));
		OSMNode startNode=(OSMNode) AStar.getStartNode();
		OSMNode goalNode=(OSMNode) AStar.getGoalNode();

		path=AStar.search(startNode, goalNode);

		assertFalse(path.isEmpty());

		System.out.println("start");
		for(int i=path.size()-1;i>=0;i--){
			System.out.println(path.get(i));
		}
		System.out.println("finish");
	}
}
