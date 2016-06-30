package database;

/**
 * Holds all values related to keys in a Way; describing what kind of Way it is
 * @author Jostein Kristiansen(jok13)
 *
 */
public enum Keys {

	KEY_HIGHWAY("highway"),
	//VALUE_FOOTWAY("footway"),
	//VALUE_PATH("path"),
	VALUE_STEPS("steps"),//TODO should these be here? Should this Enum hold Values as well as Keys? It might be cleaner to separate them
	
	KEY_AMENITY("amenity"),
	//VALUE_PARKING("parking"),
	
	KEY_BUILDING("building"),//value: yes/no
	KEY_NAME("name"),//value: name of building
	KEY_NAME_CY("name:cy"),//value: Welsh name
	KEY_NAME_EN("name:en"),//value: English name
	
	KEY_NATURAL("natural"),
	
	KEY_BARRIER("barrier"),
	
	KEY_LANDUSE("landuse"),
	
	KEY_DISUSED("disused"),
	
	KEY_WHEELCHAIR("wheelchair");//value: yes/no
	
	
	String s;

	Keys(String s){
		this.s = s;
	}

	public String getKey(){
		return s;
	}
}
