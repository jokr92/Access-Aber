package database;

import static org.junit.Assert.*;

import org.junit.Test;

public class FieldTest {

	Field testField = new Field();
	
	@Test
	public void GetAndSetIdShouldReturnAResult() {
		assertNull(testField.getId());
		testField.setId("id");
		assertTrue(testField.getId().equals("id"));
	}

	@Test
	public void GetAndSetVisibleShouldReturnAResult() {
		assertNull(testField.getVisible());
		testField.setVisible(true);
		assertTrue(testField.getVisible());
	}
	
	@Test
	public void GetAndSetVersionShouldReturnAResult() {
		assertNull(testField.getVersion());
		testField.setVersion("ver");
		assertTrue(testField.getVersion().equals("ver"));
	}
	
	@Test
	public void GetAndSetChangesetShouldReturnAResult() {
		assertNull(testField.getChangeset());
		testField.setChangeset("Chng");
		assertTrue(testField.getChangeset().equals("Chng"));
	}
	
	@Test
	public void GetAndSetTimestampShouldReturnAResult() {
		assertNull(testField.getTimestamp());
		testField.setTimestamp("time");
		assertTrue(testField.getTimestamp().equals("time"));
	}

	@Test
	public void GetAndSetUserShouldReturnAResult() {
		assertNull(testField.getUser());
		testField.setUser("jok13");
		assertTrue(testField.getUser().equals("jok13"));
	}

	@Test
	public void GetAndSetUidShouldReturnAResult() {
		assertNull(testField.getUid());
		testField.setUid("uid");
		assertTrue(testField.getUid().equals("uid"));
	}
}
