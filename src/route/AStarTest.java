package route;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import database.BuildDatabase;
import database.Node;
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
	}
	
	@Test
	public void ShouldExpandTheStartNodeAndSortTheResultingChildrenByTheirProximityToTheGoalNode(){
		List<Node> unsortedTestNodes = new ArrayList<Node>();
		List<Node> sortedTestNodes = new ArrayList<Node>();

		AStar.setStartNode(SearchDatabase.searchForNode("2641099872"));
		AStar.setGoalNode(SearchDatabase.searchForNode("3274334109"));
		
		Node startNode=AStar.getStartNode();
		
		unsortedTestNodes=AStar.getNavigatableConnectedNodes(startNode.getId());
		
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
		List<Node> path = new ArrayList<Node>();
		AStar.setStartNode(SearchDatabase.searchForNode("2947828308"));
		AStar.setGoalNode(SearchDatabase.searchForNode("2947828315"));
		Node startNode=AStar.getStartNode();
		Node goalNode=AStar.getGoalNode();
		
		path=AStar.search(startNode, goalNode);
		
			assertFalse(path.isEmpty());
			
		System.out.println("start");
		for(int i=path.size()-1;i>=0;i--){
			System.out.println(path.get(i));
		}
		System.out.println("finish");
	}
}
