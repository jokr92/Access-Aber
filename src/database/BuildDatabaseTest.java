package database;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO All these tests assume we are working with OSMNodes/Ways; This doesn't seem very robust
 * @author Jostein Kristiansen(jok13)
 *@see database.BuildDatabase
 */

public class BuildDatabaseTest {
	
	static List<Node> nodeConfig = new ArrayList<Node>();
	static List<Way> wayConfig = new ArrayList<Way>();
	
	static Node prevNode = new OSMNode();
	static Way prevWay = new OSMWay();
		
	@BeforeClass
	public static void PopulateLists(){
		if(BuildDatabase.getNodes()==null||BuildDatabase.getWays()==null){
			BuildDatabase.readConfig("map.osm");
		}
		for(Node n:BuildDatabase.getNodes()){
		nodeConfig.add(n);
		}
		for(Way w:BuildDatabase.getWays()){
		wayConfig.add(w);
		}
		prevNode = nodeConfig.get(0);
		prevWay = wayConfig.get(0);
	}
	
	@Test
	public void NodeAndWayListsShouldBePopulated(){
		assertFalse(nodeConfig.isEmpty());
		//System.out.println(nodeConfig.size());
		assertFalse(wayConfig.isEmpty());
		//System.out.println(wayConfig.size());
	}
	
	@Test
	public void NoEntryShouldBeNull(){
		for(Node n:BuildDatabase.getNodes()){
			if(n==null){
				fail("This Node-entry is null");
			}
		}
		
		for(Way w:BuildDatabase.getWays()){
			if(w==null){
				fail("This Way-entry is null");
			}
		}
	}
	
	@Test
	public void AllNodeFieldsShouldBePopulated(){
		assertTrue(nodeConfig.get(0).getId()!=null);
//		assertTrue(nodeConfig.get(0).isVisible());
//		assertTrue(nodeConfig.get(0).getVersion()!=null);
//		assertTrue(nodeConfig.get(0).getChangeset()!=null);
//		assertTrue(nodeConfig.get(0).getTimestamp()!=null);
//		assertTrue(nodeConfig.get(0).getUser()!=null);
//		assertTrue(nodeConfig.get(0).getUid()!=null);
		assertTrue(nodeConfig.get(0).getLatitude()>0||(nodeConfig.get(0).getLatitude()<0));
		assertTrue(nodeConfig.get(0).getLongitude()>0||(nodeConfig.get(0).getLongitude()<0));
		
		assertTrue(nodeConfig.get(nodeConfig.size()/2).getId()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()/2).isVisible());
//		assertTrue(nodeConfig.get(nodeConfig.size()/2).getVersion()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()/2).getChangeset()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()/2).getTimestamp()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()/2).getUser()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()/2).getUid()!=null);
		assertTrue(nodeConfig.get(nodeConfig.size()/2).getLatitude()>0||(nodeConfig.get(0).getLatitude()<0));
		assertTrue(nodeConfig.get(nodeConfig.size()/2).getLongitude()>0||(nodeConfig.get(0).getLongitude()<0));
		
		assertTrue(nodeConfig.get(nodeConfig.size()-1).getId()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()-1).isVisible());
//		assertTrue(nodeConfig.get(nodeConfig.size()-1).getVersion()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()-1).getChangeset()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()-1).getTimestamp()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()-1).getUser()!=null);
//		assertTrue(nodeConfig.get(nodeConfig.size()-1).getUid()!=null);
		assertTrue(nodeConfig.get(nodeConfig.size()-1).getLatitude()>0||(nodeConfig.get(0).getLatitude()<0));
		assertTrue(nodeConfig.get(nodeConfig.size()-1).getLongitude()>0||(nodeConfig.get(0).getLongitude()<0));
	}
	
	@Test
	public void AllWayFieldsShouldBePopulated(){
		assertTrue(wayConfig.get(0).getId()!=null);
//		assertTrue(wayConfig.get(0).isVisible());
//		assertTrue(wayConfig.get(0).getVersion()!=null);
//		assertTrue(wayConfig.get(0).getChangeset()!=null);
//		assertTrue(wayConfig.get(0).getTimestamp()!=null);
//		assertTrue(wayConfig.get(0).getUser()!=null);
//		assertTrue(wayConfig.get(0).getUid()!=null);
		assertFalse(wayConfig.get(0).getNodeRelations().isEmpty());
		assertFalse(wayConfig.get(0).getKeyValuePairs().isEmpty());
		
		assertTrue(wayConfig.get(wayConfig.size()/2).getId()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()/2).isVisible());
//		assertTrue(wayConfig.get(wayConfig.size()/2).getVersion()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()/2).getChangeset()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()/2).getTimestamp()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()/2).getUser()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()/2).getUid()!=null);
		assertFalse(wayConfig.get(wayConfig.size()/2).getNodeRelations().isEmpty());
		assertFalse(wayConfig.get(wayConfig.size()/2).getKeyValuePairs().isEmpty());
		
		assertTrue(wayConfig.get(wayConfig.size()-1).getId()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()-1).isVisible());
//		assertTrue(wayConfig.get(wayConfig.size()-1).getVersion()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()-1).getChangeset()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()-1).getTimestamp()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()-1).getUser()!=null);
//		assertTrue(wayConfig.get(wayConfig.size()-1).getUid()!=null);
		assertFalse(wayConfig.get(wayConfig.size()-1).getNodeRelations().isEmpty());
		assertFalse(wayConfig.get(wayConfig.size()-1).getKeyValuePairs().isEmpty());
	}
	
	@Test
	public void IdFieldsShouldNotBeTheSameInAllEntries(){
		Boolean different=false;
		for(Node testNode:nodeConfig){
			if(!testNode.getId().equals(prevNode.getId())){
				different=true;
				break;
			}
			
			prevNode=testNode;
		}
		assertTrue(different);
		different=false;
		for(Way testWay:wayConfig){
			if(!testWay.getId().equals(prevWay.getId())){
				different=true;
				break;
			}
			
			prevWay=testWay;
		}
		assertTrue(different);
	}
	
	
	/*It seems like all Visible fields are given the value: true, so this test is not really very useful
	 * @Test
	public void VisibleFieldsShouldNotBeTheSameInAllEntries(){
		Boolean different=false;
		for(Node testNode:nodeConfig){
			if(!testNode.getVisible().equals(prevNode.getVisible())){
				different=true;
				break;
			}
			
			prevNode=testNode;
		}
		assertTrue(different);
		different=false;
		for(Way testWay:wayConfig){
			if(!testWay.getVisible().equals(prevWay.getVisible())){
				different=true;
				break;
			}
			
			prevWay=testWay;
		}
		assertTrue(different);
	}*/
	
//	@Test
//	public void VersionFieldsShouldNotBeTheSameInAllEntries(){
//		Boolean different=false;
//		for(Node testNode:nodeConfig){
//			if(!testNode.getVersion().equals(prevNode.getVersion())){
//				different=true;
//				break;
//			}
//			
//			prevNode=testNode;
//		}
//		assertTrue(different);
//		different=false;
//		for(Way testWay:wayConfig){
//			if(!testWay.getVersion().equals(prevWay.getVersion())){
//				different=true;
//				break;
//			}
//			
//			prevWay=testWay;
//		}
//		assertTrue(different);	
//	}
	
//	@Test
//	public void ChangesetFieldsShouldNotBeTheSameInAllEntries(){
//		Boolean different=false;
//		for(Node testNode:nodeConfig){
//			if(!testNode.getChangeset().equals(prevNode.getChangeset())){
//				different=true;
//				break;
//			}
//			
//			prevNode=testNode;
//		}
//		assertTrue(different);
//		different=false;
//		for(Way testWay:wayConfig){
//			if(!testWay.getChangeset().equals(prevWay.getChangeset())){
//				different=true;
//				break;
//			}
//			
//			prevWay=testWay;
//		}
//		assertTrue(different);
//	}
	
//	@Test
//	public void TimestampFieldsShouldNotBeTheSameInAllEntries(){
//		Boolean different=false;
//		for(Node testNode:nodeConfig){
//			if(!testNode.getTimestamp().equals(prevNode.getTimestamp())){
//				different=true;
//				break;
//			}
//			
//			prevNode=testNode;
//		}
//		assertTrue(different);
//		different=false;
//		for(Way testWay:wayConfig){
//			if(!testWay.getTimestamp().equals(prevWay.getTimestamp())){
//				different=true;
//				break;
//			}
//			
//			prevWay=testWay;
//		}
//		assertTrue(different);
//	}
	
//	@Test
//	public void UserFieldsShouldNotBeTheSameInAllEntries(){
//		Boolean different=false;
//		for(Node testNode:nodeConfig){
//			if(!testNode.getUser().equals(prevNode.getUser())){
//				different=true;
//				break;
//			}
//			
//			prevNode=testNode;
//		}
//		assertTrue(different);
//		different=false;
//		for(Way testWay:wayConfig){
//			if(!testWay.getUser().equals(prevWay.getUser())){
//				different=true;
//				break;
//			}
//			
//			prevWay=testWay;
//		}
//		assertTrue(different);
//	}
	
//	@Test
//	public void UidFieldsShouldNotBeTheSameInAllEntries(){
//		Boolean different=false;
//		for(Node testNode:nodeConfig){
//			if(!testNode.getUid().equals(prevNode.getUid())){
//				different=true;
//				break;
//			}
//			
//			prevNode=testNode;
//		}
//		assertTrue(different);
//		different=false;
//		for(Way testWay:wayConfig){
//			if(!testWay.getUid().equals(prevWay.getUid())){
//				different=true;
//				break;
//			}
//			
//			prevWay=testWay;
//		}
//		assertTrue(different);
//	}
	
	@Test
	public void LatitudeFieldsShouldNotBeTheSameInAllEntries(){
		Boolean different=false;
		for(Node testNode:nodeConfig){
			if(testNode.getLatitude()!=(prevNode.getLatitude())){
				different=true;
				break;
			}
			
			prevNode=testNode;
		}
		assertTrue(different);
	}
	
	@Test
	public void LongitudeFieldsShouldNotBeTheSameInAllEntries(){
		Boolean different=false;
		for(Node testNode:nodeConfig){
			if(testNode.getLongitude()!=(prevNode.getLongitude())){
				different=true;
				break;
			}
			
			prevNode=testNode;
		}
		assertTrue(different);
	}
	
	@Test
	public void KeyAndValuePairsShouldNotBeTheSameInAllEntries(){
		Boolean different=false;
		for(Way testway:wayConfig){
			if(!testway.getKeyValuePairs().equals(prevWay.getKeyValuePairs())){
				different=true;
				break;
			}
			
			prevWay=testway;
		}
		assertTrue(different);
	}
}