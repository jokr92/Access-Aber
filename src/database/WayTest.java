package database;

import static org.junit.Assert.*;

//import java.util.ArrayList;
//import java.util.List;

import org.junit.Test;

public class WayTest {

	@Test
	public void ClassShouldBeAnInterface(){
			assertTrue(Way.class.isInterface());
		}
	/*
	Way testWay = new Way();
	
	@Test
	public void GetAndSetIdShouldReturnAResult() {
		assertNull(testWay.getId());
		testWay.setId("id");
		assertTrue(testWay.getId().equals("id"));
	}
	
	@Test
	public void GetAndSetNodeRelationShouldReturnAResult() {
		OSMNode node = new OSMNode();
		assertTrue(testWay.getNodeRelations().isEmpty());
		testWay.setNodeRelation(node);
		assertFalse(testWay.getNodeRelations().isEmpty());
	}
	
	@Test
	public void ShouldReplaceNodeRelationsWithNewList(){
		List<OSMNode> nodeRelations1=new ArrayList<OSMNode>();
		List<OSMNode> nodeRelations2=new ArrayList<OSMNode>();
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
*/
}
