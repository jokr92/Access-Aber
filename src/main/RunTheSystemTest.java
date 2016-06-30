package main;

import org.junit.Test;

public class RunTheSystemTest {
	
	@Test
	public void shouldInitialiseTheMainMethod(){
		double startGoalNode[]=new double[4];
		startGoalNode[0]=52.4162321;//start latitude
		startGoalNode[1]=-4.0665499;//start longitude
		
		startGoalNode[2]=52.4166716;//goal latitude
		startGoalNode[3]=-4.0660413;//goal longitude
		
	RunTheSystem.main(startGoalNode);
	}
}
