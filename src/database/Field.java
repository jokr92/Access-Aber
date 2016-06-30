package database;

/**
 * Holds all the fields used to build Nodes and Ways
 * @author Jostein Kristiansen(jok13)
 *@see database.Node
 *@see database.Way
 */

class Field {
	protected String id; 
	protected Boolean visible;
	protected String version;
	protected String changeset;
	protected String timestamp;
	protected String user;
	protected String uid;
	
	public String getId() {
	    return id;
	  }
	  
	  public void setId(String id) {
	    this.id = id;
	  }
	  public Boolean getVisible() {
	    return visible;
	  }
	  public void setVisible(Boolean visible) {
	    this.visible = visible;
	  }
	  public String getVersion() {
	    return version;
	  }
	  public void setVersion(String version) {
	    this.version = version;
	  }
	  public String getChangeset() {
	    return changeset;
	  }
	  public void setChangeset(String changeset) {
	    this.changeset = changeset;
	  }
	  public String getTimestamp() {
	    return timestamp;
	  }
	  public void setTimestamp(String timestamp) {
	    this.timestamp = timestamp;
	  }

	  public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
