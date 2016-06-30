package database;

import static org.junit.Assert.*;

import org.junit.Test;

public class WayTest {
	
	Way testWay = new Way();

	@Test
	public void GetAndSetNodeRelationsShouldReturnAResult() {
		Node node = new Node();
		assertTrue(testWay.getNodeRelations().isEmpty());
		testWay.setNodeRelation(node);
		assertFalse(testWay.getNodeRelations().isEmpty());
	}
	@Test
	public void GetAndSetKeyShouldReturnAResult() {
		assertNull(testWay.getKey());
		testWay.setKey("key");
		assertTrue(testWay.getKey().equals("key"));
	}

	@Test
	public void GetAndSetValueShouldReturnAResult() {
		assertNull(testWay.getValue());
		testWay.setValue("value");
		assertTrue(testWay.getValue().equals("value"));
	}

	@Test
	public void GetAndSetIdShouldReturnAResult() {
		assertNull(testWay.getId());
		testWay.setId("id");
		assertTrue(testWay.getId().equals("id"));
	}

	@Test
	public void GetAndSetVisibleShouldReturnAResult() {
		assertNull(testWay.getVisible());
		testWay.setVisible(true);
		assertTrue(testWay.getVisible());
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
		testWay.setUser("jok13");
		assertTrue(testWay.getUser().equals("jok13"));
	}

	@Test
	public void GetAndSetUidShouldReturnAResult() {
		assertNull(testWay.getUid());
		testWay.setUid("uid");
		assertTrue(testWay.getUid().equals("uid"));
	}
	
	@Test
	public void testToString() {
		assertFalse(testWay.toString().isEmpty());
	}

}
