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
		if(BuildDatabase.getNodes()==null||BuildDatabase.getWays()==null){
			BuildDatabase.readConfig("map.osm");
		}
		ComplexityAnalysis.reset();

		for(Node n:BuildDatabase.getNodes()){
			nodeDBComplete.add(n);
		}

		for(Way w:BuildDatabase.getWays()){
			wayDBComplete.add(w);
		}
		
		wayDBFiltered.addAll(SearchDatabase.filterAccessibleWays(wayDBComplete));
		nodeDBFiltered.addAll(SearchDatabase.filterAccessibleNodes(wayDBComplete));
	}

	@Test
	public void shouldCountAllNodesInTheCompleteDatabase(){
		//System.out.println("AllNodes: "+ComplexityAnalysis.getNumNodes(nodeDBComplete));
		assertTrue(ComplexityAnalysis.getNumNodes(nodeDBComplete)>0);
	}

	@Test
	public void shouldCountAllNodesInTheFilteredDatabase(){
		//System.out.println("FilteredNodes: "+ComplexityAnalysis.getNumNodes(nodeDBFiltered));
		assertTrue(ComplexityAnalysis.getNumNodes(nodeDBFiltered)>0);
	}

	@Test
	public void shouldCountAllWaysInTheCompleteDatabase(){
		//System.out.println("AllWays: "+ComplexityAnalysis.getNumWays(wayDBComplete));
		assertTrue(ComplexityAnalysis.getNumWays(wayDBComplete)>0);
	}

	@Test
	public void shouldCountAllWaysInTheFilteredDatabase(){
		//System.out.println("FilteredWays: "+ComplexityAnalysis.getNumWays(wayDBFiltered));
		assertTrue(ComplexityAnalysis.getNumWays(wayDBFiltered)>0);
	}

	@Test
	public void shouldCountAllConnectionsInTheCompleteDatabase(){
		//System.out.println("AllConnections: "+ComplexityAnalysis.getNumConnections(wayDBComplete));
		assertTrue(ComplexityAnalysis.getNumConnections(wayDBComplete)>0);
	}

	@Test
	public void shouldCountAllConnectionsInTheFilteredDatabase(){
		//System.out.println("FilteredConnections: "+ComplexityAnalysis.getNumConnections(wayDBFiltered));
		assertTrue(ComplexityAnalysis.getNumConnections(wayDBFiltered)>0);
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
