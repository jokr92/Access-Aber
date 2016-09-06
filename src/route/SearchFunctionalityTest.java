package route;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import database.BuildDatabase;
import database.Node;

public class SearchFunctionalityTest {

	static Node startNode;
	static Node goalNode;
	static AStar aStar;
	static GreedyBestFirst gBFS;
	static BreadthFirstSearch bfs;
	static DepthFirstSearch dfs;
	
	static List<Node> aStarPath;
	static List<Node> gBFSPath;
	static List<Node> bfsPath;
	static List<Node> dfsPath;
	static List<Node> optimalPath;
	
	@BeforeClass
	public static void Initialize(){
		BuildDatabase.readConfig("InformedSearchPathTest.osm");

		startNode = BuildDatabase.getNodes()[0];
		goalNode = BuildDatabase.getNodes()[5];
		
		optimalPath = new ArrayList<Node>();
		for(int i=0;i<=5;i++){//This assumes that the optimal path is the sequence of Nodes 5-0
			optimalPath.add(BuildDatabase.getNodes()[i]);
		}

		aStar = new AStar();
		gBFS = new GreedyBestFirst();
		bfs = new BreadthFirstSearch();
		dfs = new DepthFirstSearch();

		aStar.setStartNode(startNode);aStar.setGoalNode(goalNode);
		gBFS.setStartNode(startNode);gBFS.setGoalNode(goalNode);
		bfs.setStartNode(startNode);bfs.setGoalNode(goalNode);
		dfs.setStartNode(startNode);dfs.setGoalNode(goalNode);
		
		aStarPath = aStar.findPath();
		gBFSPath = gBFS.findPath();
		bfsPath = bfs.findPath();
		dfsPath = dfs.findPath();
	}

	@Test
	public void shouldEnsureCompletenessForApplicableAlgorithms(){
		assertFalse(aStarPath.isEmpty());
		assertTrue(gBFSPath.isEmpty());//Not complete
		assertFalse(bfsPath.isEmpty());
		assertFalse(dfsPath.isEmpty());
	}
	
	@Test
	public void shouldEnsureOptimalityForApplicableAlgorithms(){
		assertTrue(aStarPath.equals(optimalPath));
		assertFalse(gBFSPath.equals(optimalPath));//Not optimal
		assertFalse(bfs.equals(optimalPath));//Not optimal
		assertFalse(dfs.equals(optimalPath));//Not optimal
	}
	@Test
	/**
	 * non-optimal algorithms might get lucky and find the optimal path,
	 * but my dataset is designed to avoid this, which is why these tests are here.
	 */
	public void optimalAlgorithmsShouldNotFindTheSamePathAsNonOptimalAlgorithms(){
		assertFalse(aStarPath.equals(gBFSPath));
		assertFalse(aStarPath.equals(bfsPath));
		assertFalse(aStarPath.equals(dfsPath));
	}
}
