package database;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;


public class ComplexityAnalysisTest {

	static List<Node> nodeDBComplete;
	static List<Node> nodeDBFiltered;

	static List<Way> wayDBComplete;
	static List<Way> wayDBFiltered;

	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes().isEmpty()||BuildDatabase.getWays().isEmpty()){
			BuildDatabase.readConfig("map.osm");
		}
		ComplexityAnalysis.reset();

		nodeDBComplete=BuildDatabase.getNodes();
		nodeDBFiltered=SearchDatabase.filterAccessibleNodes(BuildDatabase.getWays());

		wayDBComplete=BuildDatabase.getWays();
		wayDBFiltered=SearchDatabase.filterAccessibleWays(BuildDatabase.getWays());
	}

	@Test
	public void shouldCountAllNodesInTheCompleteDatabase(){
		assertTrue(ComplexityAnalysis.getNumNodes(nodeDBComplete)>0);
		System.out.println("AllNodes: "+ComplexityAnalysis.getNumNodes(nodeDBComplete));
	}

	@Test
	public void shouldCountAllNodesInTheFilteredDatabase(){
		assertTrue(ComplexityAnalysis.getNumNodes(nodeDBFiltered)>0);
		System.out.println("FilteredNodes: "+ComplexityAnalysis.getNumNodes(nodeDBFiltered));
	}

	@Test
	public void shouldCountAllWaysInTheCompleteDatabase(){
		assertTrue(ComplexityAnalysis.getNumWays(wayDBComplete)>0);
		System.out.println("AllWays: "+ComplexityAnalysis.getNumWays(wayDBComplete));
	}

	@Test
	public void shouldCountAllWaysInTheFilteredDatabase(){
		assertTrue(ComplexityAnalysis.getNumWays(wayDBFiltered)>0);
		System.out.println("FilteredWays: "+ComplexityAnalysis.getNumWays(wayDBFiltered));
	}

	@Test
	public void shouldCountAllConnectionsInTheCompleteDatabase(){
		assertTrue(ComplexityAnalysis.getNumConnections(wayDBComplete)>0);
		System.out.println("AllConnections: "+ComplexityAnalysis.getNumConnections(wayDBComplete));
	}

	@Test
	public void shouldCountAllConnectionsInTheFilteredDatabase(){
		assertTrue(ComplexityAnalysis.getNumConnections(wayDBFiltered)>0);
		System.out.println("FilteredConnections: "+ComplexityAnalysis.getNumConnections(wayDBFiltered));
	}

	@Test
	public void shouldPredictTheDifferenceInTheNumberOfNodesWaysAndConnections(){
		long numNodesComplete=ComplexityAnalysis.getNumNodes(nodeDBComplete);
		long numNodesFiltered=ComplexityAnalysis.getNumNodes(nodeDBFiltered);

		long numWaysComplete=ComplexityAnalysis.getNumWays(wayDBComplete);
		long numWaysFiltered=ComplexityAnalysis.getNumWays(wayDBFiltered);

		long numConnectionsComplete=ComplexityAnalysis.getNumConnections(wayDBComplete);
		long numConnectionsFiltered=ComplexityAnalysis.getNumConnections(wayDBFiltered);

		//compare the filtered sets to the complete sets 
		assertTrue(numWaysComplete>numWaysFiltered);
		assertTrue(numNodesComplete>numNodesFiltered);
		assertTrue(numConnectionsComplete>numConnectionsFiltered);

		//compare the complete sets to each other
		assertTrue(numWaysComplete<numNodesComplete);
		assertTrue(numNodesComplete<numConnectionsComplete);

		//compare the filtered sets to each other
		assertTrue(numWaysFiltered<numNodesFiltered);
		assertTrue(numNodesFiltered<numConnectionsFiltered);
	}

}
