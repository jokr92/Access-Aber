package database;

import java.util.List;

public class ComplexityAnalysisDB {

	private static long numNodes;
	private static long numWays;
	private static long numConnections;

	public static long getNumNodes(final List<Node> nodeDBComplete){

		numNodes=nodeDBComplete.size();

		return numNodes;
	}
	

	public static long getNumWays(final List<Way> ways){

		numWays=ways.size();

		return numWays;
	}

	public static long getNumConnections(final List<Way> ways){
		long totNumNodes=0;

		for(Way w:ways){
			totNumNodes+=w.getNodeRelations().size();
		}
		numConnections=2*(totNumNodes-getNumWays(ways));

		return numConnections;
	}

	public static void reset(){
		numNodes=0;
		numWays=0;
		numConnections=0;
	}

}
