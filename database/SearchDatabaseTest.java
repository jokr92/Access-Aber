package database;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class SearchDatabaseTest {
	
	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes().isEmpty()||BuildDatabase.getWays().isEmpty()){
			BuildDatabase.readConfig("map.osm");
		}
	}
	
	/**
	 * The method searchForNode in the class SearchDatabase only searches for nodes at the moment.
	 * TODO Should the method be able to search for more than just nodes?
	 */
	@Test
	public void fileShouldLoad(){
		assertNull(SearchDatabase.searchForNode("262041360"));//this is a Way
		assertTrue(SearchDatabase.searchForNode("3274334109")!=null);
		assertTrue(SearchDatabase.searchForNode("3274334109")!=null);
		assertNull(SearchDatabase.searchForNode("<node id=\"2997275611\""));
		assertNull(SearchDatabase.searchForNode("osm"));
		assertNull(SearchDatabase.searchForNode("ThisIsNotANode"));
		assertNull(SearchDatabase.searchForNode("1"));//To see if it finds exact or just similar matches
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
	public void ShouldExpandRelationsBetweenNodesUntilASpecificNodeIsFound(){
		Node startNode=SearchDatabase.searchForNode("2947828308");//"1078822336";
		Node goalNode=SearchDatabase.searchForNode("2947828315");
		Node currentNode=null;
		boolean found=false;
		
		List<Node> queueList = new ArrayList<Node>();//For organising the order of the nodes to be expanded
		List<String> visitedNodes = new ArrayList<String>();//For preventing revisiting of nodes, potentially ending in a loop
		List<Node> importList = new ArrayList<Node>();//For handling imported nodes before they are passed into the queueArray
		
		queueList=(SearchDatabase.filterAccessibleWays(SearchDatabase.getWaysContainingNode(startNode.getId())));
		visitedNodes.add(startNode.getId());
		Iterator<Node> i = queueList.iterator();
		while(i.hasNext()){
			Node node = i.next();
			if(visitedNodes.contains(node.getId())){
				i.remove();//So that we do not search for it again
			}
		}
		
			if(queueList.contains(goalNode)){//In case the goal can be reached directly from the current position
				found=true;
				visitedNodes.add(goalNode.getId());
			}
			else{
			while(found==false && queueList.isEmpty()==false){
				currentNode=queueList.get(0);
				queueList.remove(0);
				visitedNodes.add(currentNode.getId());
				
						for(Way way:SearchDatabase.getWaysContainingNode(currentNode.getId())){
							if(way.getNodeRelations().contains(goalNode)){//TODO Does this also find very similar nodes? ie is 1 the same as 11, 21 etc?
								found=true;
								visitedNodes.add(goalNode.getId());
								break;
							}
							importList.addAll(way.getNodeRelations());
						}
				
				if(found==false){
					Iterator<Node> j = importList.iterator();
					while(j.hasNext()){
						Node node = j.next();
						if(visitedNodes.contains(node.getId())){
							j.remove();//Faster to do it here than in the queueArray, as that array is most likely much larger

						}
					}
					queueList.addAll(importList);
					importList.clear();//This data has been passed on to the queueArray, so it is no longer needed
				}
			}
		}
			assertFalse(visitedNodes.isEmpty());
			assertTrue(found);
	}
	
	@Test
	public void FilterShouldRemoveAnyWayThatIsNotMeantForNavigation(){
		long nodesInAllWays=0;
		long nodesInFilteredWays=0;
		for(Way way:BuildDatabase.getWays()){
			nodesInAllWays=nodesInAllWays+way.getNodeRelations().size();
		}
		nodesInFilteredWays=SearchDatabase.filterAccessibleWays(BuildDatabase.getWays()).size();
		
		assertTrue(nodesInFilteredWays!=0);
		assertTrue(nodesInFilteredWays<nodesInAllWays);
	}
	
	@Test
	public void ShouldFindNodeSpecifiedByExactCoordinates(){
		Node testNode = BuildDatabase.getNodes().get(BuildDatabase.getNodes().size()/2);
		double lat=testNode.getLatitude();
		double lon=testNode.getLongitude();
		
		assertEquals(SearchDatabase.findClosestNode(lat,lon),(testNode));
	}
	
	@Test
	public void ShouldFindNodeClosestToCoordinates(){
		assertFalse(SearchDatabase.findClosestNode(90, 180).equals(null));
		assertFalse(SearchDatabase.findClosestNode(-90, -180).equals(null));
		assertFalse(SearchDatabase.findClosestNode(90, 180).equals(SearchDatabase.findClosestNode(-90, -180)));
	}
}
