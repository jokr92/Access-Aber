package database;

/**
 * Holds all the tags used for reading the .osm document.
 * This makes sure that the right tag is used when reading the document, and that the same tag is used everywhere
 * @author Jostein Kristiansen(jok13)
 *
 */
public enum FieldTags {
	
	ID("id"),

	NODE_LATITUDE("lat"),
	NODE_LONGITUDE("lon"),
	
	NODE_START("node"),
	NODE_END("</node>"),//seems to only be used in a few cases. usually /> is used instead
	
	WAY_START("way"),
	WAY_END("</way>"),
	
	WAY_ND_START("nd"),
	WAY_ND_REF("ref"),
	
	TAG_START("tag"),
	TAG_KEY("k"),
	TAG_VALUE("v");
	
	String s;

	FieldTags(String s){
		this.s = s;
	}

	public String getString(){
		return s;
	}

}
