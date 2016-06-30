package main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import database.BuildDatabaseTest;
import database.FieldTest;
import database.NodeTest;
import database.SearchDatabaseTest;
import database.WayTest;
import route.AStarTest;

/**
 * Runs every test in the system. The order in which tests are run can be specified here
 * @author Jostein Kristiansen(jok13)
 *
 */
@RunWith(Suite.class)
@SuiteClasses({FieldTest.class, NodeTest.class, WayTest.class, AStarTest.class, SearchDatabaseTest.class, BuildDatabaseTest.class, RunTheSystemTest.class})
public class AllTests {

}