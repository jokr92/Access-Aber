package database;

import static org.junit.Assert.*;

import org.junit.Test;

public class NodeTest {

	Node testNode = new Node();
	
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
	public void GetAndSetIdShouldReturnAResult() {
		assertNull(testNode.getId());
		testNode.setId("id");
		assertTrue(testNode.getId().equals("id"));
	}

	@Test
	public void GetAndSetVisibleShouldReturnAResult() {
		assertNull(testNode.getVisible());
		testNode.setVisible(true);
		assertTrue(testNode.getVisible());
	}
	
	@Test
	public void GetAndSetVersionShouldReturnAResult() {
		assertNull(testNode.getVersion());
		testNode.setVersion("ver");
		assertTrue(testNode.getVersion().equals("ver"));
	}
	
	@Test
	public void GetAndSetChangesetShouldReturnAResult() {
		assertNull(testNode.getChangeset());
		testNode.setChangeset("Chng");
		assertTrue(testNode.getChangeset().equals("Chng"));
	}
	
	@Test
	public void GetAndSetTimestampShouldReturnAResult() {
		assertNull(testNode.getTimestamp());
		testNode.setTimestamp("time");
		assertTrue(testNode.getTimestamp().equals("time"));
	}

	@Test
	public void GetAndSetUserShouldReturnAResult() {
		assertNull(testNode.getUser());
		testNode.setUser("jok13");
		assertTrue(testNode.getUser().equals("jok13"));
	}

	@Test
	public void GetAndSetUidShouldReturnAResult() {
		assertNull(testNode.getUid());
		testNode.setUid("uid");
		assertTrue(testNode.getUid().equals("uid"));
	}
	
	@Test
	public void testToString() {
		assertFalse(testNode.toString().isEmpty());
	}
}
