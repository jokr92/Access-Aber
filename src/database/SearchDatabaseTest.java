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
	
	@Test
	public void fileShouldLoadNodes(){
		assertNull(SearchDatabase.searchForNode("262041360"));//this is a Way
		assertNotNull(SearchDatabase.searchForNode("3274334109"));
		assertNotNull(SearchDatabase.searchForNode("3274334109"));
		assertNull(SearchDatabase.searchForNode("<node id=\"2997275611\""));
		assertNull(SearchDatabase.searchForNode("osm"));
		assertNull(SearchDatabase.searchForNode("ThisIsNotANode"));
		assertNull(SearchDatabase.searchForNode("1"));//To see whether it finds exact or just similar matches
	}
	
	@Test
	public void fileShouldLoadWays(){
		assertNotNull(SearchDatabase.searchForWay("262041360"));//this is a Way
		assertNull(SearchDatabase.searchForWay("3274334109"));//This is a Node
		assertNull(SearchDatabase.searchForWay("3274334109"));
		assertNull(SearchDatabase.searchForWay("<way id=\"217282207\""));
		assertNull(SearchDatabase.searchForWay("osm"));
		assertNull(SearchDatabase.searchForWay("ThisIsNotAWay"));
		assertNull(SearchDatabase.searchForWay("1"));//To see whether it finds exact or just similar matches
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
	
	//TODO This test doesn't really test any methods - it looks like it only tests itself...
	@Test
	public void ShouldExpandRelationsBetweenNodesUntilASpecificNodeIsFound(){
		Node startNode=(Node) SearchDatabase.searchForNode("2947828308");//"1078822336";
		Node goalNode=(Node) SearchDatabase.searchForNode("2947828315");
		Node currentNode=null;
		boolean found=false;
		
		List<Node> queueList = new ArrayList<Node>();//For organising the order of the nodes to be expanded
		List<String> visitedNodes = new ArrayList<String>();//For preventing revisiting of nodes, potentially ending in a loop
		List<Node> importList = new ArrayList<Node>();//For handling imported nodes before they are passed into the queueArray
		
		queueList=(SearchDatabase.filterAccessibleNodes(SearchDatabase.getWaysContainingNode(startNode.getId())));
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
