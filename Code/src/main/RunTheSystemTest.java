package main;

import org.junit.Test;

public class RunTheSystemTest {

	@Test
	public void shouldInitialiseTheMainMethodWithAppropriateInput(){
		String startGoalNode[]=new String[5];

		startGoalNode[0]="ASTAR";//BFS, DFS, GBFS, ASTAR are the accepted inputs

		startGoalNode[1]="52.41646778575976";//start latitude
		startGoalNode[2]="-4.063264770347585";//start longitude

		startGoalNode[3]="52.416123608655525";//goal latitude
		startGoalNode[4]="-4.065722757548523";//goal longitude

		RunTheSystem.main(startGoalNode);
	}

	@Test
	public void shouldInitialiseTheMainMethodWithoutInput(){
		String startGoalNode[]=new String[]{"","","","",""};

		RunTheSystem.main(startGoalNode);
	}
	
	@Test
	public void shouldInitialiseTheMainMethodWithInappropriateInput(){
		String startGoalNode[]=new String[5];

		startGoalNode[0]="Not an algorithm";//BFS, DFS, GBFS, ASTAR are the accepted inputs

		startGoalNode[1]="0.0";//start latitude
		startGoalNode[2]="That was a coordinate";//start longitude

		startGoalNode[3]="Coordinate incoming";//goal latitude
		startGoalNode[4]="1.0";//goal longitude

		RunTheSystem.main(startGoalNode);
	}

	@Test
	public void shouldFindPathThroughBuildingsAndOpenSpaces(){
		String startGoalNode[]=new String[5];

		startGoalNode[0]="ASTAR";//BFS, DFS, GBFS, ASTAR are the accepted inputs

		startGoalNode[1]="52.416236051824384";//start latitude
		startGoalNode[2]="-4.065608824135579";//start longitude

		startGoalNode[3]="52.416668728282914";//goal latitude
		startGoalNode[4]="-4.0660398058197185";//goal longitude

		RunTheSystem.main(startGoalNode);
	}
}
