package main;

import org.junit.Test;

public class RunTheSystemTest {
	
	@Test
	public void shouldInitialiseTheMainMethod(){
		String startGoalNode[]=new String[4];
		startGoalNode[0]="52.41584449354819";//start latitude
		startGoalNode[1]="-4.066314441997518";//start longitude
		
		startGoalNode[2]="52.4166716";//goal latitude
		startGoalNode[3]="-4.0660413";//goal longitude
		
	RunTheSystem.main(startGoalNode);
	}
	
	@Test
	public void shouldFindPathThroughBuildingsAndOpenSpaces(){
		String startGoalNode[]=new String[4];
		
		startGoalNode[0]="52.4130564";//start latitude
		startGoalNode[1]="-4.0639836";//start longitude
		
		startGoalNode[2]="52.41646778575976";//goal latitude
		startGoalNode[3]="-4.063264770347585";//goal longitude
		
		RunTheSystem.main(startGoalNode);
	}
}
