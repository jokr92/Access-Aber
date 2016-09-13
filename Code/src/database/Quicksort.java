package database;

import java.util.ArrayList;
import java.util.List;
/**
 * TODO Delete this method, or refactor it so that it is no longer plagiarised!
 * @author PLAGIARISED FROM VOGELLA!
 *
 */
public class Quicksort {
	
	List<OSMNode> nodeList = new ArrayList<OSMNode>();
	List<OSMWay> wayList = new ArrayList<OSMWay>();
	
	public List<OSMNode> sortNodes(List<OSMNode> tempNodes) {
	    // check for empty or null array
	    if (tempNodes.isEmpty()){
	      return null;
	    }
	    this.nodeList = tempNodes;
	    quicksortNodes(0, nodeList.size()-1);
	    return tempNodes;
	  }
	
	public List<OSMWay> sortWays(List<OSMWay> tempWays) {
	    // check for empty or null array
	    if (tempWays.isEmpty()){
	      return null;
	    }
	    this.wayList = tempWays;
	    quicksortWays(0, wayList.size()-1);
	    return tempWays;
	  }

	private void quicksortNodes(int low, int high) {
	    int i = low, j = high;
	    // Get the pivot element from the middle of the list
	    Node pivot = nodeList.get(low + (high-low)/2);

	    // Divide into two lists
	    while (i <= j) {
	      // If the current value from the left list is smaller then the pivot
	      // element then get the next element from the left list
	      while (nodeList.get(i).compare(nodeList.get(i), pivot) ==-1) {
	        i++;
	      }
	      // If the current value from the right list is larger then the pivot
	      // element then get the next element from the right list
	      while (nodeList.get(j).compare(nodeList.get(j), pivot) ==1) {
	        j--;
	      }

	      // If we have found a values in the left list which is larger then
	      // the pivot element and if we have found a value in the right list
	      // which is smaller then the pivot element then we exchange the
	      // values.
	      // As we are done we can increase i and j
	      if (i <= j) {
	        exchangeNodes(i, j);
	        i++;
	        j--;
	      }
	    }
	    // Recursion
	    if (low < j)
	      quicksortNodes(low, j);
	    if (i < high)
	      quicksortNodes(i, high);
	  }
	
	private void quicksortWays(int low, int high) {
	    int i = low, j = high;
	    // Get the pivot element from the middle of the list
	    Way pivot = wayList.get(low + (high-low)/2);

	    // Divide into two lists
	    while (i <= j) {
	      // If the current value from the left list is smaller then the pivot
	      // element then get the next element from the left list
	      while (wayList.get(i).compare(wayList.get(i), pivot) ==-1) {
	        i++;
	      }
	      // If the current value from the right list is larger then the pivot
	      // element then get the next element from the right list
	      while (wayList.get(j).compare(wayList.get(j), pivot) ==1) {
	        j--;
	      }

	      // If we have found a values in the left list which is larger then
	      // the pivot element and if we have found a value in the right list
	      // which is smaller then the pivot element then we exchange the
	      // values.
	      // As we are done we can increase i and j
	      if (i <= j) {
	        exchangeWays(i, j);
	        i++;
	        j--;
	      }
	    }
	    // Recursion
	    if (low < j)
	      quicksortWays(low, j);
	    if (i < high)
	      quicksortWays(i, high);
	  }

	  private void exchangeNodes(int i, int j) {
	    OSMNode temp = nodeList.get(i);
	    nodeList.set(i, nodeList.get(j));
	    nodeList.set(j, temp);
	  }
	  
	  private void exchangeWays(int i, int j) {
		    OSMWay temp = wayList.get(i);
		    wayList.set(i, wayList.get(j));
		    wayList.set(j, temp);
		  }
	
}
