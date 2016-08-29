package database;

/**
 * Builds Nodes.
 * An OSM-Node contains the fields: id, visible, version, changeset, timestamp, user, uid, lat, long
 * @author Vogella
 * Source: www.vogella.com/tutorials/JavaXML/article.html
 * License: www.eclipse.org/legal/epl-v10.html
 * @see database.Node
 */
public class OSMNode implements Node{

	private String externalId;
	private long localId;
//	private boolean visible;
//	private String version;
//	private String timestamp;
//	private String changeset;
//	private String user;
//	private String uid;
	private double lat;
	private double lon;


	public final long getId() {
		return localId;
	}

	public void setId(long localID) {
		this.localId=localID;
		
	}
	
	public final String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId=externalId;

	}

//	/**
//	 * @return the visible
//	 */
//	public boolean isVisible() {
//		return visible;
//	}

//	/**
//	 * @param visible the visible to set
//	 */
//	public void setVisible(boolean visible) {
//		this.visible = visible;
//	}

//	/**
//	 * @return the version
//	 */
//	public String getVersion() {
//		return version;
//	}

//	/**
//	 * @param version the version to set
//	 */
//	public void setVersion(String version) {
//		this.version = version;
//	}

//	/**
//	 * @return the timestamp
//	 */
//	public String getTimestamp() {
//		return timestamp;
//	}

//	/**
//	 * @param timestamp the timestamp to set
//	 */
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

//	/**
//	 * @return the changeset
//	 */
//	public String getChangeset() {
//		return changeset;
//	}

//	/**
//	 * @param changeset the changeset to set
//	 */
//	public void setChangeset(String changeset) {
//		this.changeset = changeset;
//	}

//	/**
//	 * @return the user
//	 */
//	public String getUser() {
//		return user;
//	}

//	/**
//	 * @param user the user to set
//	 */
//	public void setUser(String user) {
//		this.user = user;
//	}

//	/**
//	 * @return the uid
//	 */
//	public String getUid() {
//		return uid;
//	}

//	/**
//	 * @param uid the uid to set
//	 */
//	public void setUid(String uid) {
//		this.uid = uid;
//	}


	public double getLatitude() {
		return lat;
	}

	public void setLatitude(double lat) {
		this.lat=lat;
	}

	public double getLongitude() {
		return lon;
	}

	public void setLongitude(double lon) {
		this.lon=lon;
	}

	public String toString() {
		return "Node [id=" + getExternalId() + ", lat=" + getLatitude() + ", lon=" + getLongitude() + "]";
	}

} 
