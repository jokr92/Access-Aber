package database;

import static org.junit.Assert.*;

import org.junit.Test;

public class OSMNodeTest {

	OSMNode testNode = new OSMNode();
	
	@Test
	public void GetAndSetLocalIdShouldReturnAResult() {
		testNode.setId(123);
		assertTrue(testNode.getId()==123);
	}
	
	@Test
	public void ShouldAssignLargeValueToID(){
		long t=Integer.MAX_VALUE;
		testNode.setId(t+1);
		assertTrue(testNode.getId()>Integer.MAX_VALUE);
	}
	
	@Test
	public void GetAndSetExternalIdShouldReturnAResult() {
		assertNull(testNode.getExternalId());
		testNode.setExternalId("123");
		assertTrue(testNode.getExternalId().equals("123"));
	}
	
	@Test
	public void GetAndSetLatitudeShouldReturnAResult() {
		assertTrue(testNode.getLatitude()==0);
		testNode.setLatitude(12.34);
		assertTrue(testNode.getLatitude()==12.34);
	}
	@Test
	public void GetAndSetLongitudeShouldReturnAResult() {
		assertTrue(testNode.getLongitude()==0);
		testNode.setLongitude(43.21);
		assertTrue(testNode.getLongitude()==43.21);
	}
	
	@Test
	public void ShouldMarkAsTowerNode(){
		assertFalse(testNode.isTowerNode());
		testNode.setTowerNode(true);
		assertTrue(testNode.isTowerNode());
	}

//	@Test
//	public void GetAndSetVisibleShouldReturnAResult() {
//		assertFalse(testNode.isVisible());
//		testNode.setVisible(true);
//		assertTrue(testNode.isVisible());
//	}
	
//	@Test
//	public void GetAndSetVersionShouldReturnAResult() {
//		assertNull(testNode.getVersion());
//		testNode.setVersion("ver");
//		assertTrue(testNode.getVersion().equals("ver"));
//	}
	
//	@Test
//	public void GetAndSetChangesetShouldReturnAResult() {
//		assertNull(testNode.getChangeset());
//		testNode.setChangeset("Chng");
//		assertTrue(testNode.getChangeset().equals("Chng"));
//	}
	
//	@Test
//	public void GetAndSetTimestampShouldReturnAResult() {
//		assertNull(testNode.getTimestamp());
//		testNode.setTimestamp("time");
//		assertTrue(testNode.getTimestamp().equals("time"));
//	}

//	@Test
//	public void GetAndSetUserShouldReturnAResult() {
//		assertNull(testNode.getUser());
//		testNode.setUser("Jostein");
//		assertTrue(testNode.getUser().equals("Jostein"));
//	}

//	@Test
//	public void GetAndSetUidShouldReturnAResult() {
//		assertNull(testNode.getUid());
//		testNode.setUid("jok13");
//		assertTrue(testNode.getUid().equals("jok13"));
//	}
	
	@Test
	public void testToString() {
		assertFalse(testNode.toString().isEmpty());
	}
}
