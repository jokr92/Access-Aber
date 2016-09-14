package route;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import database.BuildDatabase;
import database.Node;
import database.SearchDatabase;
import route.ComplexityAnalysisSearch;;

public class ComplexityAnalysisSearchTest {

	static Node startNode;
	static Node goalNode;
	static AStar aStar;
	static GreedyBestFirst gBFS;
	static BreadthFirstSearch bfs;
	static DepthFirstSearch dfs;

	static final int WARMUPROUNDS = 1000;
	static final int BENCHMARKROUNDS = 10000;
	static final int UPPERTIMELIMIT = 30/*seconds*/;
	static final int LOWERTIMELIMIT = 10/*seconds*/;

	static long avgMemoryAStar, avgMemoryGBFS, avgMemoryBFS, avgMemoryDFS;
	static Duration avgTimeAStar=Duration.ZERO;
	static Duration  avgTimeGBFS=Duration.ZERO;
	static Duration  avgTimeBFS=Duration.ZERO;
	static Duration  avgTimeDFS=Duration.ZERO;


	@BeforeClass
	public static void Initialize(){
		BuildDatabase.readConfig("map.osm");

		startNode = SearchDatabase.findClosestNode(-180, -180);
		goalNode = SearchDatabase.findClosestNode(180, 180);
	}

	@Test
	public void ShouldBenchmarkAStar(){
		int rounds;
		Instant start=Instant.now();

		aStar=new AStar();
		aStar.setStartNode(startNode);aStar.setGoalNode(goalNode);
		List<Node> path=aStar.findPath();
		System.out.println("A*\nPath found: "+(path.contains(startNode)&&path.contains(goalNode)));

		for(rounds=0;(rounds<WARMUPROUNDS)&&(Duration.between(start, Instant.now()).getSeconds()<UPPERTIMELIMIT)||(Duration.between(start, Instant.now()).getSeconds()<LOWERTIMELIMIT);rounds++){
			aStar=new AStar();
			aStar.setStartNode(startNode);aStar.setGoalNode(goalNode);
			aStar.findPath();

			avgMemoryAStar+=aStar.getMaxNodesStored();
			avgTimeAStar=avgTimeAStar.plus(aStar.getTimeElapsed());

			aStar=null;//Is this necessary? Does the garbage collector remove this for me?
		}
		//Added do keep the JVM from optimising the above for-loop away
		if(avgMemoryAStar>=0){
			avgMemoryAStar=0;
			System.out.println("A* Warmup: "+rounds+"/"+WARMUPROUNDS+" rounds in "+avgTimeAStar.getSeconds()+" Seconds"+" + "+avgTimeAStar.getNano()+" nanoseconds");
			avgTimeAStar=Duration.ZERO;
		}

		start=Instant.now();
		for(rounds=0;(rounds<BENCHMARKROUNDS)&&(Duration.between(start, Instant.now()).getSeconds()<UPPERTIMELIMIT)||(Duration.between(start, Instant.now()).getSeconds()<LOWERTIMELIMIT);rounds++){
			aStar=new AStar();
			aStar.setStartNode(startNode);aStar.setGoalNode(goalNode);
			aStar.findPath();

			avgMemoryAStar+=aStar.getMaxNodesStored();
			avgTimeAStar=avgTimeAStar.plus(aStar.getTimeElapsed());

			aStar=null;//Is this necessary? Does the garbage collector remove this for me?
		}

		avgMemoryAStar=avgMemoryAStar/rounds;
		System.out.println("A* Benchmark: "+rounds+"/"+BENCHMARKROUNDS+" rounds in "+avgTimeAStar.getSeconds()+" Seconds"+" + "+avgTimeAStar.getNano()+" nanoseconds");
		avgTimeAStar=avgTimeAStar.dividedBy(rounds);

		System.out.println("avg max memory requirements A*: "+avgMemoryAStar);
		System.out.println("avg runtime A*: "+avgTimeAStar.getSeconds()+" Seconds"+" + "+avgTimeAStar.getNano()+" nanoseconds\n");
	}

	@Test
	public void ShouldBenchmarkGreedyBestFirstSearch(){
		int rounds;
		Instant start=Instant.now();
		
		gBFS=new GreedyBestFirst();
		gBFS.setStartNode(startNode);gBFS.setGoalNode(goalNode);
		List<Node> path=gBFS.findPath();
		System.out.println("GBFS\nPath found: "+(path.contains(startNode)&&path.contains(goalNode)));

		for(rounds=0;(rounds<WARMUPROUNDS)&&(Duration.between(start, Instant.now()).getSeconds()<UPPERTIMELIMIT)||(Duration.between(start, Instant.now()).getSeconds()<LOWERTIMELIMIT);rounds++){
			gBFS=new GreedyBestFirst();
			gBFS.setStartNode(startNode);gBFS.setGoalNode(goalNode);
			gBFS.findPath();

			avgMemoryGBFS+=gBFS.getMaxNodesStored();
			avgTimeGBFS=avgTimeGBFS.plus(gBFS.getTimeElapsed());

			gBFS=null;//Is this necessary? Does the garbage collector remove this for me?
		}
		//Added do keep the JVM from optimising the above for-loop away
		if(avgMemoryGBFS>=0){
			avgMemoryGBFS=0;
			System.out.println("GBFS Warmup: "+rounds+"/"+WARMUPROUNDS+" rounds in "+avgTimeGBFS.getSeconds()+" Seconds"+" + "+avgTimeGBFS.getNano()+" nanoseconds");
			avgTimeGBFS=Duration.ZERO;
		}

		start=Instant.now();
		for(rounds=0;(rounds<BENCHMARKROUNDS)&&(Duration.between(start, Instant.now()).getSeconds()<UPPERTIMELIMIT)||(Duration.between(start, Instant.now()).getSeconds()<LOWERTIMELIMIT);rounds++){
			gBFS=new GreedyBestFirst();
			gBFS.setStartNode(startNode);gBFS.setGoalNode(goalNode);
			gBFS.findPath();

			avgMemoryGBFS+=gBFS.getMaxNodesStored();
			avgTimeGBFS=avgTimeGBFS.plus(gBFS.getTimeElapsed());

			gBFS=null;//Is this necessary? Does the garbage collector remove this for me?
		}

		avgMemoryGBFS=avgMemoryGBFS/rounds;
		System.out.println("GBFS Benchmark: "+rounds+"/"+BENCHMARKROUNDS+" rounds in "+avgTimeGBFS.getSeconds()+" Seconds"+" + "+avgTimeGBFS.getNano()+" nanoseconds");
		avgTimeGBFS=avgTimeGBFS.dividedBy(rounds);

		System.out.println("avg max memory requirements GBFS: "+avgMemoryGBFS);
		System.out.println("avg runtime GBFS: "+avgTimeGBFS.getSeconds()+" Seconds"+" + "+avgTimeGBFS.getNano()+" nanoseconds\n");
	}

	@Test
	public void ShouldBenchmarkBreadthFirstSearch(){
		int rounds;
		Instant start=Instant.now();

		bfs=new BreadthFirstSearch();
		bfs.setStartNode(startNode);bfs.setGoalNode(goalNode);
		List<Node> path=bfs.findPath();
		System.out.println("BFS\nPath found: "+(path.contains(startNode)&&path.contains(goalNode)));
		
		for(rounds=0;(rounds<WARMUPROUNDS)&&(Duration.between(start, Instant.now()).getSeconds()<UPPERTIMELIMIT)||(Duration.between(start, Instant.now()).getSeconds()<LOWERTIMELIMIT);rounds++){
			bfs=new BreadthFirstSearch();
			bfs.setStartNode(startNode);bfs.setGoalNode(goalNode);
			bfs.findPath();

			avgMemoryBFS+=bfs.getMaxNodesStored();
			avgTimeBFS=avgTimeBFS.plus(bfs.getTimeElapsed());

			bfs=null;//Is this necessary? Does the garbage collector remove this for me?
		}
		//Added do keep the JVM from optimising the above for-loop away
		if(avgMemoryBFS>=0){
			avgMemoryBFS=0;
			System.out.println("BFS Warmup: "+rounds+"/"+WARMUPROUNDS+" rounds in "+avgTimeBFS.getSeconds()+" Seconds"+" + "+avgTimeBFS.getNano()+" nanoseconds");
			avgTimeBFS=Duration.ZERO;
		}

		start=Instant.now();
		for(rounds=0;(rounds<BENCHMARKROUNDS)&&(Duration.between(start, Instant.now()).getSeconds()<UPPERTIMELIMIT)||(Duration.between(start, Instant.now()).getSeconds()<LOWERTIMELIMIT);rounds++){
			bfs=new BreadthFirstSearch();
			bfs.setStartNode(startNode);bfs.setGoalNode(goalNode);
			bfs.findPath();

			avgMemoryBFS+=bfs.getMaxNodesStored();
			avgTimeBFS=avgTimeBFS.plus(bfs.getTimeElapsed());

			bfs=null;//Is this necessary? Does the garbage collector remove this for me?
		}

		avgMemoryBFS=avgMemoryBFS/rounds;
		System.out.println("BFS Benchmark: "+rounds+"/"+BENCHMARKROUNDS+" rounds in "+avgTimeBFS.getSeconds()+" Seconds"+" + "+avgTimeBFS.getNano()+" nanoseconds");
		avgTimeBFS=avgTimeBFS.dividedBy(rounds);

		System.out.println("avg max memory requirements BFS: "+avgMemoryBFS);
		System.out.println("avg runtime BFS: "+avgTimeBFS.getSeconds()+" Seconds"+" + "+avgTimeBFS.getNano()+" nanoseconds\n");
	}

	@Test
	public void ShouldBenchmarkDepthFirstSearch(){
		int rounds;
		Instant start=Instant.now();

		dfs=new DepthFirstSearch();
		dfs.setStartNode(startNode);dfs.setGoalNode(goalNode);
		List<Node> path=dfs.findPath();
		System.out.println("DFS\nPath found: "+(path.contains(startNode)&&path.contains(goalNode)));
		
		for(rounds=0;(rounds<WARMUPROUNDS)&&(Duration.between(start, Instant.now()).getSeconds()<UPPERTIMELIMIT)||(Duration.between(start, Instant.now()).getSeconds()<LOWERTIMELIMIT);rounds++){
			dfs=new DepthFirstSearch();
			dfs.setStartNode(startNode);dfs.setGoalNode(goalNode);
			dfs.findPath();

			avgMemoryDFS+=dfs.getMaxNodesStored();
			avgTimeDFS=avgTimeDFS.plus(dfs.getTimeElapsed());

			dfs=null;//Is this necessary? Does the garbage collector remove this for me?
		}
		//Added do keep the JVM from optimising the above for-loop away
		if(avgMemoryDFS>=0){
			avgMemoryDFS=0;
			System.out.println("DFS Warmup: "+rounds+"/"+WARMUPROUNDS+" rounds in "+avgTimeDFS.getSeconds()+" Seconds"+" + "+avgTimeDFS.getNano()+" nanoseconds");
			avgTimeDFS=Duration.ZERO;
		}

		start=Instant.now();
		for(rounds=0;(rounds<BENCHMARKROUNDS)&&(Duration.between(start, Instant.now()).getSeconds()<UPPERTIMELIMIT)||(Duration.between(start, Instant.now()).getSeconds()<LOWERTIMELIMIT);rounds++){
			dfs=new DepthFirstSearch();
			dfs.setStartNode(startNode);dfs.setGoalNode(goalNode);
			dfs.findPath();

			avgMemoryDFS+=dfs.getMaxNodesStored();
			avgTimeDFS=avgTimeDFS.plus(dfs.getTimeElapsed());

			dfs=null;//Is this necessary? Does the garbage collector remove this for me?
		}

		avgMemoryDFS=avgMemoryDFS/rounds;
		System.out.println("DFS Benchmark: "+rounds+"/"+BENCHMARKROUNDS+" rounds in "+avgTimeDFS.getSeconds()+" Seconds"+" + "+avgTimeDFS.getNano()+" nanoseconds");
		avgTimeDFS=avgTimeDFS.dividedBy(rounds);

		System.out.println("avg max memory requirements DFS: "+avgMemoryDFS);
		System.out.println("avg runtime DFS: "+avgTimeDFS.getSeconds()+" Seconds"+" + "+avgTimeDFS.getNano()+" nanoseconds\n");
	}
}