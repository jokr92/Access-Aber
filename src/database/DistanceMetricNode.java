package database;

public interface DistanceMetricNode extends Node {

	public double getDistanceTravelled();

	public void setDistanceTravelled(double distanceTravelled);
	
	public double getDistanceToGoal();
	
	public void setGoalDistance(double goalDistance);
}
