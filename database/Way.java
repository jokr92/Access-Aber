package database;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds Ways.
 * A Way contains the fields: id, visible, version, changeset, timestamp, user, uid, nodeRelations, key, value
 * @author Jostein Kristiansen(jok13)
 *@see database.Field
 */

public class Way extends Field{

	/**
	 * List of nodes, referring to nodes in the Array of Nodes in {@link database.BuildDatabase#getNodes()}
	 */
	private List<Node> nodeRelations=new ArrayList<Node>();
	private String key;
	private String value;

	public List<Node> getNodeRelations() {
		return nodeRelations;
	}
	public void setNodeRelation(Node node) {
		this.nodeRelations.add(node);
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	  public String toString() {
	    return "Way [id=" + id + ", visible=" + visible + ", version="
	        + version + ", changeset=" + changeset + ", timestamp=" + timestamp
	        + ", user=" + user +", uid=" + uid + ", key=" + key + "]";
	  }
}
