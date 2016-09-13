package database;

import java.util.Comparator;

class IDComparator implements Comparator<Node> {
public int compare(Node n1, Node n2) {
return (n1.getExternalId().toUpperCase()).compareTo((n2.getExternalId().toUpperCase()));
}
}