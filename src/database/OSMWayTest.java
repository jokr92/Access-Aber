package database;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.junit.Test;

public class OSMWayTest {
	
	OSMWay testWay = new OSMWay();

	@Test
	public void GetAndSetNodeRelationShouldReturnAResult() {
		OSMNode node = new OSMNode();
		assertTrue(testWay.getNodeRelations().isEmpty());
		testWay.addNodeRelation(node);
		assertFalse(testWay.getNodeRelations().isEmpty());
	}
	
	@Test
	public void ShouldReplaceNodeRelationsWithNewList(){
		List<Node> nodeRelations1=new ArrayList<Node>();
		List<Node> nodeRelations2=new ArrayList<Node>();
		OSMNode node1 = new OSMNode();
		OSMNode node2 = new OSMNode();
		
		nodeRelations1.add(node1);
		nodeRelations2.add(node1);
		nodeRelations1.add(node2);
		
		assertTrue(testWay.getNodeRelations().isEmpty());
		
		testWay.setNodeRelations(nodeRelations1);
		assertTrue(testWay.getNodeRelations().equals(nodeRelations1));
		
		testWay.setNodeRelations(nodeRelations2);
		assertFalse(testWay.getNodeRelations().equals(nodeRelations1));
		assertTrue(testWay.getNodeRelations().equals(nodeRelations2));
	}
	
	@Test
	public void ShouldCreateAndReturnNewKeyValuePairs() {
		assertTrue(testWay.getKeyValuePairs().isEmpty());
		
		testWay.addKeyValuePair("key", "value");
		testWay.addKeyValuePair("key2", "value2");
		
		assertFalse(testWay.getKeyValuePairs().isEmpty());
		
		boolean foundFirstEntry=false, foundSecondEntry=false;
		
		for(Entry<String, Object> s:testWay.getKeyValuePairs()){
			if(s.getKey().equals("key")&&s.getValue().equals("value")){
				foundFirstEntry=true;
			}else if(s.getKey().equals("key2")&&s.getValue().equals("value2")){
				foundSecondEntry=true;
			}else{
				fail("Key-Value Pair not Found");
			}
		}
		assertTrue(foundFirstEntry);
		assertTrue(foundSecondEntry);
	}

	@Test
	public void GetAndSetIdShouldReturnAResult() {
		assertNull(testWay.getId());
		testWay.setId("123");
		assertTrue(testWay.getId().equals("123"));
	}

	@Test
	/**
	 * TODO Why is this test so unnecessarily slow when it is run via 'AllTests' ?
	 * It takes ~50% (0.9sec) of the total runtime(~2sec)
	 * TODO This test is also slightly slower than the rest of the tests in this class
	 */
	public void GetAndSetVisibleShouldReturnAResult() {
		assertFalse(testWay.isVisible());
		testWay.setVisible(true);
		assertTrue(testWay.isVisible());
	}
	
	@Test
	public void GetAndSetVersionShouldReturnAResult() {
		assertNull(testWay.getVersion());
		testWay.setVersion("ver");
		assertTrue(testWay.getVersion().equals("ver"));
	}
	
	@Test
	public void GetAndSetChangesetShouldReturnAResult() {
		assertNull(testWay.getChangeset());
		testWay.setChangeset("Chng");
		assertTrue(testWay.getChangeset().equals("Chng"));
	}
	
	@Test
	public void GetAndSetTimestampShouldReturnAResult() {
		assertNull(testWay.getTimestamp());
		testWay.setTimestamp("time");
		assertTrue(testWay.getTimestamp().equals("time"));
	}

	@Test
	public void GetAndSetUserShouldReturnAResult() {
		assertNull(testWay.getUser());
		testWay.setUser("Jostein");
		assertTrue(testWay.getUser().equals("Jostein"));
	}

	@Test
	public void GetAndSetUidShouldReturnAResult() {
		assertNull(testWay.getUid());
		testWay.setUid("jok13");
		assertTrue(testWay.getUid().equals("jok13"));
	}
	
	@Test
	public void testToString() {
		assertFalse(testWay.toString().isEmpty());
	}

}
