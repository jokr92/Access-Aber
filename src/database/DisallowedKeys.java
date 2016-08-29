package database;

/**
 * Holds the Key-Value pairs associated with Ways that are inaccessible to Persons with reduced mobility or the general public.
 * The tags here should only be those that might appear in a Way together with the tags in {@link database.PermittedKeys}}.
 * So there should not be necessary to add "highway,freeway", "access,boat", etc. here.
 * @author Jostein
 *@see database.PermittedKeys
 */
public enum DisallowedKeys {

	WHEELCHAIR_NO("wheelchair","no"),
	FOOT_NO("foot","no"),
	PRIVATE_ACCESS("access","private"),
	SIDEWALK_NONE("sidewalk","none"),//Is this one needed? The path may still be accessible
	SURFACE_EARTH("surface","ground"),
	SURFACE_GRASS("surface","grass"),
	SURFACE_SAND("surface","sand"),
	BAD_SMOOTHNESS("smoothness","bad");

	
	String key, value;

	DisallowedKeys(String key, String value){
		this.key=key;
		this.value=value;
	}

	String getKey(){
		return key;
	}
	String getValue(){
		return value;
	}
}
