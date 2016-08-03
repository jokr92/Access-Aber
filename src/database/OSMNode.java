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

	private String id;
	private int localId;
//	private boolean visible;
//	private String version;
//	private String timestamp;
//	private String changeset;
//	private String user;
//	private String uid;
	private double lat;
	private double lon;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id=id;

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

	@Override
	public double getLatitude() {
		return lat;
	}

	@Override
	public void setLatitude(double lat) {
		this.lat=lat;
	}

	@Override
	public double getLongitude() {
		return lon;
	}

	@Override
	public void setLongitude(double lon) {
		this.lon=lon;
	}
	
	@Override
	public int getlocalId() {
		return localId;
	}

	@Override
	public void setLocalId(int localID) {
		this.localId=localID;
		
	}
	
	@Override
	public String toString() {
		return "Node [id=" + getId() + ", lat=" + getLatitude() + ", lon=" + getLongitude() + "]";
	}

} 
