package database;

import java.util.ArrayList;
//import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Builds Ways.
 * A Way contains the fields: id, visible, version, changeset, timestamp, user, uid, nodeRelations, key, value
 * @author Jostein Kristiansen(jok13)
 *@see database.Node
 */

public class OSMWay implements Way{

	/**Labels for identifying what this Way is*/
	private HashMap<String, Object> keyValuePairs=new HashMap<String,Object>();

	/**
	 * List of nodes, referring to nodes in the Array of Nodes in {@link database.BuildDatabase#getNodes()}
	 */
	protected List<OSMNode> nodeRelations=new ArrayList<OSMNode>();

	protected String id;
	private int localId;
	private boolean visible;
	private String changeset;
	private String timestamp;
	private String version;
	private String user;
	private String uid;

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}
	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	/**
	 * @return the changeset
	 */
	public String getChangeset() {
		return changeset;
	}
	/**
	 * @param changeset the changeset to set
	 */
	public void setChangeset(String changeset) {
		this.changeset = changeset;
	}
	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * @param keyValuePairs the keyValuePairs to set
	 */
	public void setKeyValuePairs(HashMap<String, Object> keyValuePairs) {
		this.keyValuePairs = keyValuePairs;
	}
	//TODO Write unit tests for this
	public void addKeyValuePair(String key, Object value){
		keyValuePairs.put(key, value);
	}
	@Override
	//TODO Write unit test for this
	public Set<Entry<String, Object>> getKeyValuePairs() {
		return keyValuePairs.entrySet();
	}

	@Override
	public String toString() {
		return "Way [id=" + id + ", visible=" + visible + ", version="
				+ version + ", changeset=" + changeset + ", timestamp=" + timestamp
				+ ", user=" + user +", uid=" + uid + "]";
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id=id;

	}
	@Override
	public void setNodeRelations(List<Node> nodeRelations) {
		this.nodeRelations.clear();
		for(Node n:nodeRelations){
			this.addNodeRelation(n);
		}

	}
	@Override
	public List<OSMNode> getNodeRelations() {
		return nodeRelations;
	}
	@Override
	/**
	 * TODO Is this an unsafe cast?
	 */
	public void addNodeRelation(Node node) {
		this.nodeRelations.add((OSMNode) node);
	}
	@Override
	public void removeNodeRelation(Node node) {
		Iterator<OSMNode> j = this.nodeRelations.iterator();
		while(j.hasNext()){
			OSMNode relation = j.next();

			if(relation.compare(relation, node)==0){
				j.remove();
			}
		}
	}
	@Override
	public int getlocalId() {
		return localId;
	}
	@Override
	public void setLocalId(int localID) {
		this.localId=localID;
	}
}
