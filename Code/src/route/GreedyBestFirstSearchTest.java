package route;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
 *@see route.GreedyBestFirstSearch
 */
public class GreedyBestFirstSearchTest {

	static Node startNode;
	static Node goalNode;
	GreedyBestFirstSearch gBFS;

	@BeforeClass
	public static void PopulateLists(){

		BuildDatabase.readConfig("map.osm");

		startNode=SearchDatabase.searchForNode(1);
		goalNode=SearchDatabase.searchForNode(20);
	}

	@Before
	public void initialiseGreedyBestFirst(){
		gBFS = new GreedyBestFirstSearch();
		gBFS.setStartNode(startNode);
		gBFS.setGoalNode(goalNode);
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
	//	/**
	//	 * Greedy Best First Search is not complete, so it cannot guarantee that a path will be found.
	//	 */
	//	public void ShouldFindRouteFromOneNodeToAnother(){
	//		List<Node> path = new ArrayList<Node>();
	//
	//		path=gBFS.findPath();
	//
	//		assertFalse(path.isEmpty());
	//		assertTrue(path.size()>2);//i.e contains more Nodes than just the start and goal nodes
	//		assertTrue(path.get(0).equals(goalNode));
	//		assertTrue(path.get(path.size()-1).equals(startNode));
	//	}

	@Test
	/**
	 * Will still finish successfully if the path is empty
	 * Greedy Best First Search is not complete, so it cannot guarantee that a path will be found
	 */
	public void ShouldOnlyAddNodesToRouteOnce(){
		List<Node> path = new ArrayList<Node>();

		path=gBFS.findPath();

		for(int i=0;i<path.size();i++){
			for(int j=i+1;j<path.size();j++){
				assertFalse(path.get(i).equals(path.get(j)));
			}
		}
	}

	@Test
	public void ShouldNotFindPathToUnconnectedNode(){
		List<Node> path = new ArrayList<Node>();

		OSMNode gNode=new OSMNode();
		gBFS.setGoalNode(gNode);

		path=gBFS.findPath();

		assertTrue(path.isEmpty());
	}

	@Test
	public void ShouldNotFindPathFromUnconnectedNode(){
		List<Node> path = new ArrayList<Node>();

		OSMNode sNode=new OSMNode();
		gBFS.setStartNode(sNode);

		path=gBFS.findPath();

		assertTrue(path.isEmpty());
	}

	//	@Test
	//	/**
	//	 * Greedy Best First Search is not optimal, so it cannot guarantee that it finds the best possible route
	//	 * This means that the path going in one direction is not guaranteed to be the same going in the other direction
	//	 */
	//	public void pathsShouldBeTheSameLengthGoingInEitherDirection(){
	//		List<Node> path1 = new ArrayList<Node>();
	//		List<Node> path2 = new ArrayList<Node>();
	//
	//		path1=gBFS.findPath();
	//
	//		//This makes path2 the reverse of path1
	//		GreedyBestFirstSearch otherGreedyBestFirst = new GreedyBestFirstSearch();
	//		otherGreedyBestFirst.setStartNode(gBFS.getGoalNode());
	//		otherGreedyBestFirst.setGoalNode(gBFS.getStartNode());
	//		path2=otherGreedyBestFirst.findPath();
	//
	//		assertTrue(path1!=null);
	//		assertTrue(path2!=null);
	//		assertTrue(path1.size()==path2.size());
	//		assertTrue(gBFS.getPathCost(gBFS.getGoalNode())>0 && gBFS.getPathCost(gBFS.getGoalNode())<Double.MAX_VALUE);
	//		//Makes sure that both path-costs are the same
	//		assertEquals(gBFS.getPathCost(gBFS.getGoalNode()),otherGreedyBestFirst.getPathCost(otherGreedyBestFirst.getGoalNode()),0);
	//		assertEquals(gBFS.getPathCost(gBFS.getGoalNode()),otherGreedyBestFirst.getPathCost(otherGreedyBestFirst.getGoalNode()),0);
	//		//Makes sure that path2 is path1 backwards (or in reverse if you will)
	//		for(int i=0;i<path1.size();i++){
	//			assertTrue(path1.get(i).equals(path2.get((path2.size()-1)-i)));
	//		}
	//	}
}
