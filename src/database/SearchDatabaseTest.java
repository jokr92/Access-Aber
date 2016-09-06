package database;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchDatabaseTest {

	static List<Node> DBNodes = new ArrayList<Node>();
	static List<Way> DBWays = new ArrayList<Way>();

	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes()==null||BuildDatabase.getWays()==null){
			BuildDatabase.readConfig("map.osm");
		}
	}

	@Before
	public void refreshNodesAndWays(){
		//Handle Nodes
		if(!(DBNodes.toArray().equals(BuildDatabase.getNodes()))){
			//TODO This if-statement is always !false... fix it
			DBNodes.clear();
			for(Node n:BuildDatabase.getNodes()){
				DBNodes.add(n);
			}
		}
		//Handle Ways
		if(!(DBWays.toArray().equals(BuildDatabase.getWays()))){
			DBWays.clear();
			for(Way w:BuildDatabase.getWays()){
				DBWays.add(w);
			}
		}
	}

	@Test
	public void fileShouldLoadNodes(){
		assertNull(SearchDatabase.searchForNode(Integer.MAX_VALUE));//this number is way too high
		assertNull(SearchDatabase.searchForNode(-1));//this number is too small
		assertNotNull(SearchDatabase.searchForNode(0));
		assertNotNull(SearchDatabase.searchForNode(BuildDatabase.getNodes().length-1));
	}

	@Test
	public void fileShouldLoadWays(){
		assertNull(SearchDatabase.searchForWay(Integer.MAX_VALUE));//this index is way too high
		assertNull(SearchDatabase.searchForWay(-1));//this number is too small
		assertNotNull(SearchDatabase.searchForWay(0));
		assertNotNull(SearchDatabase.searchForWay(BuildDatabase.getWays().length-1));
	}

	@Test
	public void ShouldFindRelationsBetweenNodes(){
		assertTrue(SearchDatabase.getWaysContainingNode("1078822336").size()==2);
		assertTrue(SearchDatabase.getWaysContainingNode("ThisIsNotANode").isEmpty());
	}

	@Test
	public void ShouldFindRelationsBetweenNodesAndExpandTheRelations(){
		List<Way> testList = new ArrayList<Way>(SearchDatabase.getWaysContainingNode("1078822336"));
		for(Way way:testList){
			assertFalse(way.getNodeRelations().isEmpty());
		}
	}

	@Test
	public void FilterShouldRemoveAnyWaysNotSuitableForNavigation(){
		long nodesInAllWays=0;
		long nodesInFilteredWays=0;
		for(Way way:DBWays){
			nodesInAllWays=nodesInAllWays+way.getNodeRelations().size();
		}
		nodesInFilteredWays=SearchDatabase.filterAccessibleWays(DBWays).size();

		assertTrue(nodesInFilteredWays!=0);
		assertTrue(nodesInFilteredWays<nodesInAllWays);
	}

	@Test
	public void FilterShouldRemoveAnyNodesNotSuitableForNavigation(){
		long numTotalNodes=0;
		long numFilteredNodes=0;
		for(Way way:DBWays){
			numTotalNodes=numTotalNodes+way.getNodeRelations().size();
		}
		numFilteredNodes=SearchDatabase.filterAccessibleNodes(DBWays).size();

		assertTrue(numFilteredNodes!=0);
		assertTrue(numFilteredNodes<numTotalNodes);
	}

	@Test
	/**
	 * This test assumes that any Node within a Way tagged with one of the tags found in @PermittedKeys can be found by the method it is testing.
	 * The exact coordinates it is supposed to find is actually the coordinates of a Node already stored in the internal database {@link BuildDatabase}
	 */
	public void ShouldFindNodeSpecifiedByExactCoordinates(){
		Node testNode=null;
		
		for(Way w:BuildDatabase.getWays()){
			if(testNode!=null){break;}

			for(Entry<String, Object> entry:w.getKeyValuePairs()){
				if(testNode!=null){break;}

				for(PermittedKeys permittedKey:PermittedKeys.values()){
					if(entry.getKey().equals(permittedKey.getKey())&&entry.getValue().equals(permittedKey.getValue())){
						testNode = BuildDatabase.getNodes()[(int) w.getNodeRelations().get(0).getId()];//TODO Might be a dangerous cast...
						break;
					}
				}
			}
		}
		if(testNode!=null){
			double lat=testNode.getLatitude();
			double lon=testNode.getLongitude();

			assertEquals(SearchDatabase.findClosestNode(lat,lon),(testNode));
		}else{fail("No navigatable node could be found. Issue likely in @BuildDatabase or @PermittedKeys");}
	}

	@Test
	public void ShouldFindNodeClosestToCoordinates(){
		System.out.println("Closest Node: "+SearchDatabase.findClosestNode(52.4164812821479, -4.065756207246293));
		assertFalse(SearchDatabase.findClosestNode(90, 180).equals(null));
		assertFalse(SearchDatabase.findClosestNode(-90, -180).equals(null));
		assertFalse(SearchDatabase.findClosestNode(90, 180).equals(SearchDatabase.findClosestNode(-90, -180)));
	}
}
