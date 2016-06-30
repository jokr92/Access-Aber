package database;
/**
 * Builds Nodes.
 * A node contains the fields: id, visible, version, changeset, timestamp, user, uid, lat, long
 * @author Vogella
 * Source: www.vogella.com/tutorials/JavaXML/article.html
 * License: www.eclipse.org/legal/epl-v10.html
 * @see database.Field
 */
public class Node extends Field{
	  private double lat;
	  private double lon;
	  
	  /**
	   * TODO This should probably be kept elsewhere, but I am in a rush to get A* to work, so I'll keep it here for now
	   */
	  private double distanceTravelled;

	public double getLatitude() {
		return lat;
	}

	public void setLatitude(double lat) {
		this.lat = lat;
	}

	public double getLongitude() {
		return lon;
	}

	public void setLongitude(double lon) {
		this.lon = lon;
	}
	
	/**
	 * TODO This method should probably be deleted
	 * @see {@link #distanceTravelled}
	 */
	public double getDistanceTravelled(){
		return distanceTravelled;
	}
	
	/**
	 * TODO This method should probably be deleted
	 * @see {@link #distanceTravelled}
	 */
	public void setDistanceTravelled(double distanceTravelled){
		this.distanceTravelled = distanceTravelled;
	}
	
	@Override
	  public String toString() {
	    return "Node [id=" + id + ", visible=" + visible + ", version="
	        + version + ", changeset=" + changeset + ", timestamp=" + timestamp
	        + ", user=" + user +", uid=" + uid + ", lat=" + lat + ", lon=" + lon + "]";
	  }
	} 
