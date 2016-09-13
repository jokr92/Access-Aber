package database;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;


public class ComplexityAnalysisDBTest {

	static List<Node> nodeDBComplete = new ArrayList<Node>();
	static List<Node> nodeDBFiltered = new ArrayList<Node>();

	static List<Way> wayDBComplete = new ArrayList<Way>();
	static List<Way> wayDBFiltered = new ArrayList<Way>();

	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes()==null||BuildDatabase.getWays()==null){
			BuildDatabase.readConfig("map.osm");
		}
		ComplexityAnalysisDB.reset();

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
		//System.out.println("AllNodes: "+ComplexityAnalysisDB.getNumNodes(nodeDBComplete));
		assertTrue(ComplexityAnalysisDB.getNumNodes(nodeDBComplete)>0);
	}

	@Test
	public void shouldCountAllNodesInTheFilteredDatabase(){
		//System.out.println("FilteredNodes: "+ComplexityAnalysisDB.getNumNodes(nodeDBFiltered));
		assertTrue(ComplexityAnalysisDB.getNumNodes(nodeDBFiltered)>0);
	}

	@Test
	public void shouldCountAllWaysInTheCompleteDatabase(){
		//System.out.println("AllWays: "+ComplexityAnalysisDB.getNumWays(wayDBComplete));
		assertTrue(ComplexityAnalysisDB.getNumWays(wayDBComplete)>0);
	}

	@Test
	public void shouldCountAllWaysInTheFilteredDatabase(){
		//System.out.println("FilteredWays: "+ComplexityAnalysisDB.getNumWays(wayDBFiltered));
		assertTrue(ComplexityAnalysisDB.getNumWays(wayDBFiltered)>0);
	}

	@Test
	public void shouldCountAllConnectionsInTheCompleteDatabase(){
		//System.out.println("AllConnections: "+ComplexityAnalysisDB.getNumConnections(wayDBComplete));
		assertTrue(ComplexityAnalysisDB.getNumConnections(wayDBComplete)>0);
	}

	@Test
	public void shouldCountAllConnectionsInTheFilteredDatabase(){
		//System.out.println("FilteredConnections: "+ComplexityAnalysisDB.getNumConnections(wayDBFiltered));
		assertTrue(ComplexityAnalysisDB.getNumConnections(wayDBFiltered)>0);
	}

	@Test
	public void shouldPredictTheDifferenceInTheNumberOfNodesWaysAndConnections(){
		long numNodesComplete=ComplexityAnalysisDB.getNumNodes(nodeDBComplete);
		long numNodesFiltered=ComplexityAnalysisDB.getNumNodes(nodeDBFiltered);

		long numWaysComplete=ComplexityAnalysisDB.getNumWays(wayDBComplete);
		long numWaysFiltered=ComplexityAnalysisDB.getNumWays(wayDBFiltered);

		long numConnectionsComplete=ComplexityAnalysisDB.getNumConnections(wayDBComplete);
		long numConnectionsFiltered=ComplexityAnalysisDB.getNumConnections(wayDBFiltered);

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
