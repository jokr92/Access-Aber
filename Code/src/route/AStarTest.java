package route;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import database.BuildDatabase;
import database.Node;
import database.OSMNode;
import database.SearchDatabase;
/**
 * 
 * @author Jostein Kristiansen(jok13)
 *@see route.AStar
 */
public class AStarTest {

	static Node startNode;
	static Node goalNode;
	AStar aStar;

	@BeforeClass
	public static void PopulateLists(){
		BuildDatabase.readConfig("map.osm");
		
		BuildDatabase.setWays(SearchDatabase.filterAccessibleWays(Arrays.asList(BuildDatabase.getWays())));
		BuildDatabase.setNodes(SearchDatabase.filterAccessibleNodes(Arrays.asList(BuildDatabase.getWays())));

		startNode=SearchDatabase.searchForNode(1);
		goalNode=SearchDatabase.searchForNode(20);
	}

	@Before
	public void initialiseAstar(){
		aStar = new AStar();
		aStar.setStartNode(startNode);
		aStar.setGoalNode(goalNode);
	}

	@Test
	public void shouldGetDistanceBetweenTwoPoints(){
		Node n1 = new OSMNode(),n2 = new OSMNode();
		n1.setLatitude(3.2); n1.setLongitude(1);
		n2.setLatitude(2); n2.setLongitude(4.1);
		assertTrue(Search.distanceBetweenPoints(n1,n2)!=
				(n1.getLatitude()-n2.getLatitude())+
				(n1.getLongitude()-n2.getLongitude()));
		assertTrue(Search.distanceBetweenPoints(n1,n2)!=0);
		assertEquals(Search.distanceBetweenPoints(n1,n2),Search.distanceBetweenPoints(n2, n1),0);

		/*Makes sure that all distances are positive(CRUCIAL FOR PATH-COST CALCULATIONS)*/
		n1.setLatitude(1); n1.setLongitude(1);
		n2.setLatitude(1); n2.setLongitude(1);
		assertTrue(Search.distanceBetweenPoints(n1,n2)>0);
		n1.setLatitude(-4); n1.setLongitude(-3);
		n2.setLatitude(-2); n2.setLongitude(-1);
		assertTrue(Search.distanceBetweenPoints(n1,n2)>0);
	}

	//	@Test
	//	public void ShouldExpandTheStartNodeAndSortTheResultingChildrenByTheirProximityToTheGoalNode(){
	//		List<DistanceMetricNode> unsortedTestNodes = new ArrayList<DistanceMetricNode>();
	//		List<DistanceMetricNode> sortedTestNodes = new ArrayList<DistanceMetricNode>();
	//
	//		for(Node n:SearchDatabase.getNavigatableConnectedNodes(startNode)){
	//			//TODO Is it safe to cast like this?
	//			unsortedTestNodes.add((DistanceMetricNode) n);
	//		}
	//
	//		sortedTestNodes=AStar.sortByDistance(sortedTestNodes,unsortedTestNodes);
	//		for(int i=sortedTestNodes.size()-1;i>0;i--){
	//			assertTrue((sortedTestNodes.get(i).getDistanceTravelled()+sortedTestNodes.get(i).getDistanceToGoal())
	//					<=(sortedTestNodes.get(i).getDistanceTravelled()+sortedTestNodes.get(i).getDistanceToGoal()));
	//			/*First Node*///System.out.println("\n"+i+": "+AStar.distanceBetweenPoints(sortedTestNodes.get(i).getLatitude(), sortedTestNodes.get(i).getLongitude(), AStar.getGoalNode().getLatitude(), AStar.getGoalNode().getLongitude()));
	//			/*Next Node*///System.out.println(i+": "+AStar.distanceBetweenPoints(sortedTestNodes.get(i-1).getLatitude(), sortedTestNodes.get(i-1).getLongitude(), AStar.getGoalNode().getLatitude(), AStar.getGoalNode().getLongitude()));
	//
	//		}
	//	}

	@Test
	public void ShouldFindRouteFromOneNodeToAnother(){
		List<Node> path = new ArrayList<Node>();

		path=aStar.findPath();

		assertFalse(path.isEmpty());
		assertTrue(path.size()>2);//i.e contains more Nodes than just the start and goal nodes
		assertTrue(path.get(0).equals(startNode));
		assertTrue(path.get(path.size()-1).equals(goalNode));
	}

	@Test
	public void ShouldOnlyAddNodesToRouteOnce(){
		List<Node> path = new ArrayList<Node>();

		path=aStar.findPath();

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
		aStar.setGoalNode(gNode);

		path=aStar.findPath();

		assertTrue(path.isEmpty());
	}

	@Test
	public void ShouldNotFindPathFromUnconnectedNode(){
		List<Node> path = new ArrayList<Node>();

		OSMNode sNode=new OSMNode();
		aStar.setStartNode(sNode);

		path=aStar.findPath();

		assertTrue(path.isEmpty());
	}

	@Test
	public void pathsShouldBeTheSameLengthGoingInEitherDirection(){
		List<Node> path1 = new ArrayList<Node>();
		List<Node> path2 = new ArrayList<Node>();

		path1=aStar.findPath();

		//This makes path2 the reverse of path1
		AStar otherAStar = new AStar();
		otherAStar.setStartNode(aStar.getGoalNode());
		otherAStar.setGoalNode(aStar.getStartNode());
		path2=otherAStar.findPath();

		assertFalse(path1.isEmpty());
		assertFalse(path2.isEmpty());
		assertEquals(path1.size(),path2.size());

		//Makes sure that path2 is path1 backwards (i.e in reverse)
		for(int i=0;i<path1.size();i++){
			assertTrue(path1.get(i).equals(path2.get((path2.size()-1)-i)));
		}

		assertTrue(aStar.getPathCost(aStar.getGoalNode())>0 && aStar.getPathCost(aStar.getGoalNode())<Double.MAX_VALUE);
		//Makes sure that both path-costs are the same
		assertEquals(aStar.getPathCost(aStar.getGoalNode()),otherAStar.getPathCost(otherAStar.getGoalNode()),0.000000000000000001);


	}
}
