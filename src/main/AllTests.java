package main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import database.BuildDatabaseTest;
import database.ComplexityAnalysisTest;
import database.NodeTest;
import database.OSMNodeTest;
import database.OSMWayTest;
import database.SearchDatabaseTest;
import database.WayTest;
import route.AStarTest;
import route.BreadthFirstSearchTest;
import route.DepthFirstSearchTest;
import route.GreedyBestFirstTest;
import route.SearchFunctionalityTest;

/**
 * Runs every test in the system. The order in which tests are run can be specified here
 * @author Jostein Kristiansen(jok13)
 *
 */
@RunWith(Suite.class)
@SuiteClasses({NodeTest.class, OSMNodeTest.class, WayTest.class, OSMWayTest.class, AStarTest.class, GreedyBestFirstTest.class, BreadthFirstSearchTest.class, DepthFirstSearchTest.class, SearchDatabaseTest.class, BuildDatabaseTest.class, ComplexityAnalysisTest.class, RunTheSystemTest.class, SearchFunctionalityTest.class})
public class AllTests {

}