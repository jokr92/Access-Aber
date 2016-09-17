package main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import database.BuildDatabaseTest;
import database.ComplexityAnalysisDBTest;
import database.NodeTest;
import database.OSMNodeTest;
import database.OSMWayTest;
import database.SearchDatabaseTest;
import database.WayTest;
import route.AStarTest;
import route.BreadthFirstSearchTest;
import route.ComplexityAnalysisSearchTest;
import route.DepthFirstSearchTest;
import route.GreedyBestFirstSearchTest;
import route.SearchFunctionalityTest;

/**
 * Runs every test in the system. The order in which tests are run can be specified here
 * @author Jostein Kristiansen(jok13)
 *
 */
@SuppressWarnings("unused")
@RunWith(Suite.class)
@SuiteClasses({NodeTest.class, OSMNodeTest.class, WayTest.class, OSMWayTest.class, AStarTest.class,
	GreedyBestFirstSearchTest.class, BreadthFirstSearchTest.class, DepthFirstSearchTest.class, SearchDatabaseTest.class,
	BuildDatabaseTest.class, ComplexityAnalysisDBTest.class, RunTheSystemTest.class,
	
	/* This test takes a long time to run, and has therefore been commented out
	 * Worst case: numSearchAlgorithms * (30+30seconds).
	 * Best case: numSearchAlgorithms * (10+10 Seconds).
	 */
	//ComplexityAnalysisSearchTest.class,
	
	/*This test should be run last, as it changes the contents of BuildDatabase (which all of the other tests rely on)*/
	SearchFunctionalityTest.class})
public class AllTests {

}