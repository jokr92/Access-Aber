package database;
/**
 * TODO This interface is not needed. The distance-counters should not be stored in the Node itself
 * @author Jostein
 *
 */
public interface DistanceMetricNode extends Node {

	public double getDistanceTravelled();

	public void setDistanceTravelled(double distanceTravelled);
	
	public double getDistanceToGoal();
	
	public void setGoalDistance(double goalDistance);
}
