package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import static database.FieldTags.*;
/**
 * Populates two lists of Objects.
 * One list contains all Nodes in the provided database.
 * The other list contains all Ways in the provided database.
 * @author Vogella
 * Source: www.vogella.com/tutorials/JavaXML/article.html
 * License: www.eclipse.org/legal/epl-v10.html
 *@see database.Node
 *@see database.OSMNode
 *@see database.OSMWay
 */
public class BuildDatabase{
	
	private static List<OSMNode> nodes = new ArrayList<OSMNode>();
    private static List<OSMWay>  ways  = new ArrayList<OSMWay> ();

    /**
     * 
     * @return The complete list of Nodes in {@link #nodes}
     */
    public static List<OSMNode> getNodes() {
		return nodes;
	}

    /**
     * Changes the contents of {@link #nodes} to the contents of the input
     * @param nodes The list of Nodes to mirror the list of {@link #nodes} from
     */
	private static void setNodes(List<OSMNode> nodes) {
		BuildDatabase.nodes = nodes;
	}

	/**
	 * 
	 * @return The complete list of Nodes in {@link #ways}
	 */
	public static List<OSMWay> getWays() {
		return ways;
	}

	/**
	 * Changes the contents of {@link #ways} to the contents of the input
	 * @param ways The list of Ways to mirror the list of {@link #ways} from
	 */
	private static void setWays(List<OSMWay> ways) {
		BuildDatabase.ways = ways;
	}

@SuppressWarnings({ "unchecked" })
  public static void readConfig(String filename) {
	List<OSMNode> tempNodes = new ArrayList<OSMNode>();
	List<OSMWay> tempWays = new ArrayList<OSMWay>();
	StartElement startElement;
	String name;
	Attribute attribute;
	Iterator<Attribute> attributes;
    try {
      // First, create a new XMLInputFactory
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      // Setup a new eventReader
      InputStream in = new FileInputStream(filename);
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      OSMNode node = null;
      OSMWay way = null;
      
      // read the XML document
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();

        if (event.isStartElement()) {
          startElement = event.asStartElement();
          // If we have a Node element, we create a new Node
          if (startElement.getName().getLocalPart().equals(NODE_START.getString())) {
            node = new OSMNode();
            // We read the attributes from this tag and add them to our object
            attributes = startElement.getAttributes();
            while (attributes.hasNext()) {
              attribute = attributes.next();
              name=attribute.getName().toString();
              if (name.equals(ID.getString())) {
                node.setId(attribute.getValue());
              }else if (name.equals(VISIBLE.getString())){
            	  node.setVisible(Boolean.parseBoolean(attribute.getValue()));
              }else if (name.equals(VERSION.getString())){
            	  node.setVersion(attribute.getValue());
              }else if (name.equals(CHANGESET.getString())){
            	  node.setChangeset(attribute.getValue());
              }else if (name.equals(TIMESTAMP.getString())){
            	  node.setTimestamp(attribute.getValue());
              }else if (name.equals(USER.getString())){
            	  node.setUser(attribute.getValue());
              }else if (name.equals(UID.getString())){
            	  node.setUid(attribute.getValue());
              }else if (name.equals(NODE_LATITUDE.getString())){
            	  node.setLatitude(Double.parseDouble(attribute.getValue()));
              }else if (name.equals(NODE_LONGITUDE.getString())){
            	  node.setLongitude(Double.parseDouble(attribute.getValue()));
              }
            }
            tempNodes.add(node);
          }
          else if(startElement.getName().getLocalPart().equals(WAY_START.getString())) {//TODO Very similar to the above. Can they be refactored and merged?
            way = new OSMWay();
            // We read the attributes from this tag and add them to our object
            attributes = startElement.getAttributes();
            while (attributes.hasNext()) {
              attribute = attributes.next();
              name = attribute.getName().toString();
              if (name.equals(ID.getString())) {
                way.setId(attribute.getValue());
              }else if (name.equals(VISIBLE.getString())){
            	  way.setVisible(Boolean.parseBoolean(attribute.getValue()));
              }else if (name.equals(VERSION.getString())){
            	  way.setVersion(attribute.getValue());
              }else if (name.equals(CHANGESET.getString())){
            	  way.setChangeset(attribute.getValue());
              }else if (name.equals(TIMESTAMP.getString())){
            	  way.setTimestamp(attribute.getValue());
              }else if (name.equals(USER.getString())){
            	  way.setUser(attribute.getValue());
              }else if (name.equals(UID.getString())){
            	  way.setUid(attribute.getValue());
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
	                    	for(OSMNode nodeI:tempNodes){
	                    		if(nodeI.getId().equals(attributeValue)){
	                    			way.addNodeRelation(nodeI);
	                    			break;
	                    		}
	                    	}
	                    }
	            	}
	            	continue;
	            	//adds the type(key) and identifier(value) to this way
	            }else if (startElement.getName().getLocalPart().equals(WAY_TAG_START.getString())){
	            	attributes = startElement.getAttributes();
	            	String key=null;
	            	String value=null;
	            	
	            	//Start adding a new entry (label)
	            	while (attributes.hasNext()) {
	                    attribute = attributes.next();
	                    name=attribute.getName().toString();
	                    String attributeValue=attribute.getValue();
	                    
	                    if (name.equals(WAY_TAG_KEY.getString())) {
	                    	key=attributeValue;
	                    }else if (name.equals(WAY_TAG_VALUE.getString())){
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
                  tempWays.add(way);
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
    //return new BuildDatabase(getNodes(),getWays());
  }

}