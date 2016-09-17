package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import static database.FieldTags.*;
/**
 * TODO Only works with data from OSM. This doesn't seem correct
 * Populates two lists of Objects.
 * One list contains all Nodes in the provided database.
 * The other list contains all Ways in the provided database.
 * @author Vogella
 * Source: www.vogella.com/tutorials/JavaXML/article.html
 * License: www.eclipse.org/legal/epl-v10.html
 *@see database.Node
 *@see database.Way
 *@see database.OSMNode
 *@see database.OSMWay
 */
public class BuildDatabase{

	private static Node[] nodes;
	private static Way[] ways;

	private static List<Node> accessibleDoors = new ArrayList<Node>();//Keeps track of which Nodes are wheelchair-accessible doors in buildings

	/**
	 * 
	 * @return The complete list of Nodes in {@link #nodes}
	 */
	public static Node[] getNodes() {
		return nodes;
	}

	/**
	 * Changes the contents of {@link #nodes} to the contents of the input
	 * @param nodes The list of Nodes to mirror the list of {@link #nodes} from
	 */
	public static void setNodes(List<Node> tempNodes) {
		BuildDatabase.nodes = new Node[tempNodes.size()];
		for(int i=tempNodes.size()-1;i>=0;i--){
			nodes[i]=tempNodes.get(i);
			nodes[i].setId(i);
		}
	}

	/**
	 * 
	 * @return The complete list of Nodes in {@link #ways}
	 */
	public static Way[] getWays() {
		return ways;
	}

	/**
	 * Changes the contents of {@link #ways} to the contents of the input
	 * @param tempWays The list of Ways to mirror the list of {@link #ways} from
	 */
	public static void setWays(List<Way> tempWays) {
		ways=new Way[tempWays.size()];
		for(int i=tempWays.size()-1;i>=0;i--){
			ways[i]=tempWays.get(i);
			ways[i].setId(i);
		}
	}

	/**
	 * Deletes all Nodes in buildings or open areas that are not directly connected to another Way (i.e borders like walls).
	 * Also removes Nodes with no associated Way, and Ways with only one or no node-connections, as these are not useful for route-planning.
	 * This method only deals with Ways that have at least one tag found in {@link AreaAndBuildingTags} - other Ways are left as they are.
	 * @see AreaAndBuildingTags
	 */
	private static void removeObsoleteNodesAndWays(){
		//Removes inaccessible Nodes and Ways
		List<Way>tempWays= new ArrayList<Way>();
		tempWays.addAll(Arrays.asList(getWays()));
		
		List<Node>tempNodes=new ArrayList<Node>();
		tempNodes.addAll(Arrays.asList(getNodes()));
		

		for(Way w:getWays()){
			boolean area=false;

			for(Entry<String, Object> entry:w.getKeyValuePairs()){
				if(area==true){break;}

				for(AreaAndBuildingTags areaTag:AreaAndBuildingTags.values()){
					if(area==true){break;}

					if(entry.getKey().equals(areaTag.getKey())&&entry.getValue().equals(areaTag.getValue())){
						area=true;

						//Area AND Building
						if(entry.getKey().equals(AreaAndBuildingTags.BUILDING.getKey())){
							Iterator<Node> j = w.getNodeRelations().iterator();
							while(j.hasNext()){
								Node relation = j.next();
								//Removes all inaccessible doors in this building, but retains the walls and accessible doors
								if(accessibleDoors.contains(relation)==false){

									if(SearchDatabase.getWaysContainingNode(relation.getExternalId()).size()>1){//ignores intermediate Nodes - e.g the walls of this building
								j.remove();//Removes inaccessible doors
									}
								}else{relation.setTowerNode(true);}
							}
						}
						break;
					}
				}
			}
			if(w.getNodeRelations().size()<=1){//<=1 because a Way like this could never provide a useful link between other Ways
				tempWays.remove(w);
			}
			

			Iterator<Node> j = w.getNodeRelations().iterator();
			while(j.hasNext()){
				Node relation = j.next();
				//Delete relations that point to a non-existing Node
				if(!Arrays.asList(getNodes()).contains(relation)){
					j.remove();
				}
			}
		}
		
		for(Node n:getNodes()){
			int numConnections=SearchDatabase.getWaysContainingNode(n.getExternalId()).size();

			if(numConnections==1){
				n.setTowerNode(false);//This Node does not serve as a connector between Ways
			}else if(numConnections>1){
				n.setTowerNode(true);//This Node connects at least two Ways together
			}else{tempNodes.remove(n);} //This Node is an 'island', and is therefore not useful for anything
		}

		setWays(tempWays);
		setNodes(tempNodes);
	}

	@SuppressWarnings({ "unchecked" })
	public static void readConfig(String filename) {
		List<Node> tempNodes = new ArrayList<Node>();
		List<Way> tempWays = new ArrayList<Way>();

		StartElement startElement;
		String name;
		Attribute attribute;
		Iterator<Attribute> attributes;
		int nodeCounter=0;
		int wayCounter=0;
		try {
			// First, create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new FileInputStream(filename);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			Node node;
			Way way;

			// read the XML document
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					startElement = event.asStartElement();
					// If we have a Node element, we create a new Node
					if (startElement.getName().getLocalPart().equals(NODE_START.getString())) {
						node = new OSMNode();//TODO This should probably be more generic (i.e. Node instead of OSMNode)
						// We read the attributes from this tag and add them to our object
						attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							attribute = attributes.next();
							name=attribute.getName().toString();
							if (name.equals(ID.getString())) {
								node.setExternalId(attribute.getValue());
							}else if (name.equals(NODE_LATITUDE.getString())){
								node.setLatitude(Double.parseDouble(attribute.getValue()));
							}else if (name.equals(NODE_LONGITUDE.getString())){
								node.setLongitude(Double.parseDouble(attribute.getValue()));
							}else if (node.getExternalId()!=null&&node.getLatitude()!=0&&node.getLongitude()!=0){
								break;
							}
						}
						node.setId(nodeCounter++);
						tempNodes.add(node);
					}
					else if(startElement.getName().getLocalPart().equals(TAG_START.getString())){
						attributes = startElement.getAttributes();
						String key=null;
						String value=null;

						//Start adding a new entry (label)
						while (attributes.hasNext()) {
							attribute = attributes.next();
							name=attribute.getName().toString();
							String attributeValue=attribute.getValue();

							if (name.equals(TAG_KEY.getString())) {
								key=attributeValue;
							}else if (name.equals(TAG_VALUE.getString())){
								value=attributeValue;
							}
						}
						if(key!=null&&key.equals("wheelchair")&&value!=null&&(value.equals("yes")||value.equals("designated")||value.equals("limited"))){
							accessibleDoors.add(tempNodes.get(tempNodes.size()-1));
						}
						continue;
						//Finish adding a new entry (label)
					}
					else if(startElement.getName().getLocalPart().equals(WAY_START.getString())) {//TODO Very similar to the above. Can they be refactored and merged?
						way = new OSMWay();//TODO I should probably make the Ways I add more generic (i.e. Way instead of OSMWay)
						// We read the attributes from this tag and add them to our object
						attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							attribute = attributes.next();
							name = attribute.getName().toString();
							if (name.equals(ID.getString())) {
								way.setExternalId(attribute.getValue());
								break;
							}
						}
						while(eventReader.hasNext()){
							event = eventReader.nextEvent();

							//Starts adding nodes to this way
							if (event.isStartElement()) {
								startElement = event.asStartElement();
								if (startElement.getName().getLocalPart().equals(WAY_ND_START.getString())) {
									attributes = startElement.getAttributes();
									while (attributes.hasNext()) {
										attribute = attributes.next();
										name=attribute.getName().toString();
										//Adds a Node to this Way by creating a pointer to it in the nodes List
										if (name.equals(WAY_ND_REF.getString())) {
											String attributeValue=attribute.getValue();
											for(Node nodeI:tempNodes){
												if(nodeI.getExternalId().equals(attributeValue)){
													//TODO POSSIBLE MASSIVE BOTTLENECK HERE
													way.addNodeRelation(nodeI);
													break;
												}
											}
										}
									}
									continue;
									//adds the type(key) and identifier(value) to this way
								}else if (startElement.getName().getLocalPart().equals(TAG_START.getString())){
									attributes = startElement.getAttributes();
									String key=null;
									String value=null;

									//Start adding a new entry (label)
									while (attributes.hasNext()) {
										attribute = attributes.next();
										name=attribute.getName().toString();
										String attributeValue=attribute.getValue();

										if (name.equals(TAG_KEY.getString())) {
											key=attributeValue;
										}else if (name.equals(TAG_VALUE.getString())){
											value=attributeValue;
										}
									}
									if(key!=null){way.addKeyValuePair(key, value);}
									continue;
									//Finish adding a new entry (label)
								}
							}
							// If we reach the end of a way, we add it to the list
							else if (event.isEndElement()) {
								EndElement endElement = event.asEndElement();
								if (endElement.getName().getLocalPart().equals(WAY_START.getString())) {
									way.setId(wayCounter++);
									tempWays.add(way);
									//System.out.println(way.getlocalId());
									break;
								}
							}
						}    
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		setNodes(tempNodes);
		setWays(tempWays);
		removeObsoleteNodesAndWays();
	}
}