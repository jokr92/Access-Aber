package database;

/**
 * Holds all values related to keys in a Way; describing what kind of Way it is
 * TODO Look here for more tags related to accessibility: http://wiki.openstreetmap.org/wiki/Wheelchair_routing
 * @author Jostein Kristiansen(jok13)
 *
 */
public enum PermittedKeys {

	WHEELCHAIR("wheelchair","yes"),
	WHEELCHAIR_LIM("wheelchair","limited"),
	WHEELCHAIR_DES("wheelchair","designated"),
	
	FOOT_ACCESS("foot","yes"),
	FOOT_PERMISSIVE("foot","permissive"),
	BIKE_ACCESS("bicycle","yes"),
	BIKE_PERMISSIVE("bicycle","permissive"),
	
	/*
	 * Generally for access to a building, service station, beach, campsite, industrial estate, business park, etc.
	 * also commonly used for access to parking, driveways, and alleys.
	 */
	SERVICE_ROAD("highway","service"),
	/*
	 * Some footways seem to span multiple layers ("ramp"?); Example: k="layer" v="-1"
	 */
	FOOTWAY("highway","footway"),
	PEDESTRIAN("highway","pedestrian"),
	CYCLEWAY("highway","cycleway"),
	LIVING_STREET("highway","living_street"),
	RESIDENTIAL("highway","residential"),
	//PATH("highway","path"),//Are these always accessible?
	//EQUESTRIAN("highway","bridleway"),//Intended for use by horse riders, but also pedestrians; surface may be unsuitable for wheelchairs
	
	BUILDING("building","yes"),
	UNIVERSITY_BUILDING("building","university"),
	PUBLIC_BUILDING("building","public"),
	CIVIC_BUILDING("building","civic"),
	//BRIDGE("building","bridge"),//Are bridges always accessible?
	
	AREA("area","yes"),
	SQUARE("place","square"),//Is this the same as the Piazza in Aber? OSM-ID:108350246
	CROSSING("highway","crossing"),
	SIDEWALK_LEFT("sidewalk","left"),
	SIDEWALK_RIGHT("sidewalk","right"),
	SIDEWALK_BOTH("sidewalk","both"),
	
	PARKING("amenity","parking"),
	//PARKING_DISABLED("capacity:disabled",null),//Is this actually disabled-parking? Does the above: "PARKING" cover this?
	//DOCTORS("amenity","doctors"),//Should this be included? The building itself might not be accessible. A tag like wheelchair=yes on an entrance should cover this
	PARKING_AISLE("service","parking_aisle"),
	ENTRANCE("entrance","yes");
	
	String key, value;
	
	PermittedKeys(String key, String value){
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
