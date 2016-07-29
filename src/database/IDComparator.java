package database;

import java.util.Comparator;

class IDComparator implements Comparator<Node> {
public int compare(Node n1, Node n2) {
return (n1.getId().toUpperCase()).compareTo((n2.getId().toUpperCase()));
}
}