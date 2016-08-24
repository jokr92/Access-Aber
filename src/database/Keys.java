package database;

/**
 * Holds all values related to keys in a Way; describing what kind of Way it is
 * TODO Look here for more tags related to accessibility: http://wiki.openstreetmap.org/wiki/Wheelchair_routing
 * @author Jostein Kristiansen(jok13)
 *
 */
public enum Keys {

	WHEELCHAIR("wheelchair","yes"),
	WHEELCHAIR_LIM("wheelchair","limited"),
	WHEELCHAIR_DES("wheelchair","designated"),
	
	CROSSING("highway","crossing"),
	/*
	 * Some footways seem to span multiple layers ("ramp"?); Example: k="layer" v="-1"
	 */
	FOOTWAY("highway","footway"),
	PEDESTRIAN("highway","pedestrian"),
	/*
	 * Generally for access to a building, service station, beach, campsite, industrial estate, business park, etc.
	 * also commonly used for access to parking, driveways, and alleys.
	 */
	SERVICE_ROAD("highway","service"),
	//PATH("highway","path"),//Are these always accessible?
	PARKING("amenity","parking"),
	//PARKING_DISABLED("capacity:disabled",null),//Is this actually disabled-parking? Does the above: "PARKING" cover this?
	//DOCTORS("amenity","doctors"),//Should this be included? The building itself might not be accessible
	//BRIDGE("building","bridge"),//Are the bridges always accessible?
	PARKING_AISLE("service","parking_aisle"),
	SQUARE("place","square");//Is this the same as the Plaza in Aber?
	
	String key, value;
	
	Keys(String key, String value){
		this.key=key;
		this.value=value;
	}
	
	String getKey(){
		return key;
	}
	String getValue(){
		return value;
	}
	
	/*
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
	*/
}
