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
		
		startGoalNode[0]="52.416236051824384";//start latitude
		startGoalNode[1]="-4.065608824135579";//start longitude
		
		startGoalNode[2]="52.416668728282914";//goal latitude
		startGoalNode[3]="-4.0660398058197185";//goal longitude
		
		RunTheSystem.main(startGoalNode);
	}
}
