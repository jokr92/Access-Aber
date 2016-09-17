package route;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import database.BuildDatabase;
import database.Node;
import database.SearchDatabase;

public class SearchFunctionalityTest {

	static Node startNode;
	static Node goalNode;
	static AStar aStar;
	static GreedyBestFirstSearch gBFS;
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
		BuildDatabase.setWays(SearchDatabase.filterAccessibleWays(Arrays.asList(BuildDatabase.getWays())));
		BuildDatabase.setNodes(SearchDatabase.filterAccessibleNodes(Arrays.asList(BuildDatabase.getWays())));

		for(Node n:BuildDatabase.getNodes())
		if(n.getExternalId().equals("1")){
			startNode=n;
		}else if(n.getExternalId().equals("6")){
		goalNode=n;
		}else if(startNode!=null&&goalNode!=null){break;}
		
		optimalPath = new ArrayList<Node>();
		for(int i=1;i<=6;i++){//This assumes that the optimal path is the sequence of Nodes 1-6
			for(Node n:Arrays.asList(BuildDatabase.getNodes())){
				if(n.getExternalId().equals(Integer.toString(i))){
					optimalPath.add(n);
				}
			}
		}

		aStar = new AStar();
		gBFS = new GreedyBestFirstSearch();
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
