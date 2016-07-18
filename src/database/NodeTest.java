package database;

import static org.junit.Assert.*;
import org.junit.Test;

public class NodeTest {
	
	@Test
	public void ClassShouldBeAnInterface(){
			assertTrue(Node.class.isInterface());
		}
/*
	Node testNode = new Node();
	
	@Test
	public void GetAndSetIdShouldReturnAResult() {
		assertNull(testNode.getId());
		testNode.setId("id");
		assertTrue(testNode.getId().equals("id"));
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
*/
}
