package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
	private HashMap<String, String> keyValuePairs=new HashMap<String,String>();

	public List<Node> getNodeRelations() {
		return nodeRelations;
	}
	public void setNodeRelation(Node node) {
		this.nodeRelations.add(node);
	}
	//TODO Write unit tests for this
	public void addKeyValuePair(String key, String value){
		keyValuePairs.put(key, value);
	}
	//TODO Write unit test for this
	public Set<Entry<String, String>> getKeyValuePairs() {
		return keyValuePairs.entrySet();
	}

	@Override
	  public String toString() {
	    return "Way [id=" + id + ", visible=" + visible + ", version="
	        + version + ", changeset=" + changeset + ", timestamp=" + timestamp
	        + ", user=" + user +", uid=" + uid + "]";
	  }
}
