package database;

/**
 * Holds key-value pairs (or tags) for Ways whose Nodes represent the outline of an area rather than an actual path.
 * This outline could contain the walls and doors of a building, borders of a parking lot (usually including entrances and exits), etc.
 * This class is important because the path-cost going from one side of the area to another does not include the intermediate Nodes in the Way (unlike Ways like footways and stairs).
 * This class is also important because the map should not draw a route as following the exterior walls of a building when the path goes from one entrance in the building to another.
 * @author Jostein Kristiansen(jok13)
 *
 */
public enum AreaAndBuildingTags {

	BUILDING("building","yes"),
	UNIVERSITY_BUILDING("building","university"),
	PUBLIC_BUILDING("building","public"),
	CIVIC_BUILDING("building","civic"),

	AREA("area","yes"),
	SQUARE("place","square"),//Is this the same as the Piazza in Aber? Way-ID:108350246

	PARKING("amenity","parking"),
	PARKING_AISLE("service","parking_aisle");

	String key, value;

	AreaAndBuildingTags(String key, String value){
		this.key=key;
		this.value=value;
	}

	public String getKey(){
		return key;
	}
	public String getValue(){
		return value;
	}
}
