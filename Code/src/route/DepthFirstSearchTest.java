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
 *@see route.BreadthFirstSearch
 */
public class DepthFirstSearchTest {

	static Node startNode;
	static Node goalNode;
	DepthFirstSearch dfs;

	@BeforeClass
	public static void PopulateLists(){

		BuildDatabase.readConfig("map.osm");

		startNode=SearchDatabase.searchForNode(1);
		goalNode=SearchDatabase.searchForNode(20);
	}

	@Before
	public void initialiseDFS(){
		dfs = new DepthFirstSearch();
		dfs.setStartNode(startNode);
		dfs.setGoalNode(goalNode);
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

	@Test
	public void ShouldFindRouteFromOneNodeToAnother(){
		List<Node> path = new ArrayList<Node>();

		path=dfs.findPath();

		assertFalse(path.isEmpty());
		assertTrue(path.size()>2);//i.e contains more Nodes than just the start and goal nodes
		assertTrue(path.get(0).equals(startNode));
		assertTrue(path.get(path.size()-1).equals(goalNode));
	}

	@Test
	public void ShouldOnlyAddNodesToRouteOnce(){
		List<Node> path = new ArrayList<Node>();

		path=dfs.findPath();

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
	public void ShouldNotFindPathToUnconnectedNode(){
		List<Node> path = new ArrayList<Node>();

		OSMNode gNode=new OSMNode();
		dfs.setGoalNode(gNode);

		path=dfs.findPath();

		assertTrue(path.isEmpty());
	}

	@Test
	public void ShouldNotFindPathFromUnconnectedNode(){
		List<Node> path = new ArrayList<Node>();

		OSMNode sNode=new OSMNode();
		dfs.setStartNode(sNode);

		path=dfs.findPath();

		assertTrue(path.isEmpty());
	}
}
