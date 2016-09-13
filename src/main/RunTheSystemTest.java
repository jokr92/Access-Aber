package main;

import org.junit.Test;

public class RunTheSystemTest {
	
	@Test
	public void shouldInitialiseTheMainMethod(){
		String startGoalNode[]=new String[4];
		
		startGoalNode[0]="52.41646778575976";//start latitude
		startGoalNode[1]="-4.063264770347585";//start longitude
		
		startGoalNode[2]="52.416123608655525";//goal latitude
		startGoalNode[3]="-4.065722757548523";//goal longitude
		
	RunTheSystem.main(startGoalNode);
	}
	
	@Test
	public void shouldFindPathThroughBuildingsAndOpenSpaces(){
		String startGoalNode[]=new String[4];
		
		startGoalNode[0]="52.41584449354819";//start latitude
		startGoalNode[1]="-4.066314441997518";//start longitude
		
		startGoalNode[2]="52.41680058142066";//goal latitude
		startGoalNode[3]="-4.0657466373649465";//goal longitude
		
		RunTheSystem.main(startGoalNode);
	}
}
