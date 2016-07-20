package database;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;


public class ComplexityAnalysisTest {

	static List<Node> nodeDBComplete = new ArrayList<Node>();
	static List<Node> nodeDBFiltered = new ArrayList<Node>();

	static List<Way> wayDBComplete = new ArrayList<Way>();
	static List<Way> wayDBFiltered = new ArrayList<Way>();

	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes().isEmpty()||BuildDatabase.getWays().isEmpty()){
			BuildDatabase.readConfig("map.osm");
		}
		ComplexityAnalysis.reset();

		wayDBComplete.addAll(BuildDatabase.getWays());
		wayDBFiltered.addAll(SearchDatabase.filterAccessibleWays(wayDBComplete));

		nodeDBComplete.addAll(BuildDatabase.getNodes());
		nodeDBFiltered.addAll(SearchDatabase.filterAccessibleNodes(wayDBComplete));
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
