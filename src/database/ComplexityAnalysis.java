package database;

import java.util.List;

public class ComplexityAnalysis {

	private static long numNodes;
	private static long numWays;
	private static long numConnections;

	public static long getNumNodes(final List<OSMNode> nodes){

		numNodes=nodes.size();

		return numNodes;
	}
	

	public static long getNumWays(final List<OSMWay> ways){

		numWays=ways.size();

		return numWays;
	}

	public static long getNumConnections(final List<OSMWay> ways){
		long totNumNodes=0;

		for(OSMWay w:ways){
			for(int i=0;i<w.getNodeRelations().size();i++){
				totNumNodes++;
			}
		}
		numConnections=2*(totNumNodes-getNumWays(ways));
		//System.out.println("2*("+totNumNodes+"-"+getNumWays(ways)+")="+numConnections);

		return numConnections;
	}

	public static void reset(){
		numNodes=0;
		numWays=0;
		numConnections=0;
	}

}
