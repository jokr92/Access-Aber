package route;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Node;
import database.OSMNode;
import database.SearchDatabase;
import database.Way;

abstract class InformedSearch extends Search {

	/**
	 * The total path-cost accumulated by this Node's parent nodes, starting at {@link #getStartNode()}.
	 * Format: Node , path-cost
	 */
	private Map<Node, Double> pathCost = new HashMap<Node, Double>();
	/**
	 * The straight-line distance from a Node to the goalNode.
	 * Format: <Node,straight-line distance>
	 */
	private Map<Node, Double> goalDistance = new HashMap<Node, Double>();


	/**
	 * 
	 * @param n The Node to update
	 * @param cost The path-cost of reaching this Node from the start Node, going through all of its parents
	 */
	void updatePathCost(Node n,double cost){
		pathCost.put(n, cost);
	}

	/**
	 * 
	 * @param n The Node to get the total path cost to
	 * @return The path cost required to reach this Node, or Positive Infinity if this value has not been calculated yet
	 */
	public double getPathCost(Node n){
		double cost=pathCost.getOrDefault(n,Double.POSITIVE_INFINITY);
		return cost;
	}

	/**
	 * 
	 * @param n The Node to update
	 * @param distance The new shortest possible distance to the goal from this Node
	 */
	void updateGoalDistance(Node n, double distance){
		goalDistance.put(n, distance);
	}

	/**
	 * 
	 * @param n The Node to get the distance from
	 * @return The distance to the goal Node, or Positive Infinity if this value has not been calculated yet
	 */
	double getGoalDistance(Node n){
		double dist=goalDistance.getOrDefault(n,Double.POSITIVE_INFINITY);
		return dist;
	}

	/**
	 * Estimates the total path-cost from the received Node to the goalNode.
	 * The estimate is based on the path-cost required to reach the Node from its parents and the straight-line distance to the goalNode
	 * @param n The Node to estimate the shortest possible total path-cost for
	 * @return the smallest possible path-cost from this Node to the goalNode, plus the path-cost required to reach this Node from its parents
	 * @see #getPathCost(Node)
	 * @see #getGoalDistance(Node)
	 */
	double getEstimatedMinimalCost(Node n){
		double cost=getPathCost(n);
		double dist=getGoalDistance(n);
		return (cost+dist);
	}

	public void setStartNode(Node startNode){
		if(this.getStartNode()!=startNode){
			this.startNode = startNode;

			this.path.clear();
			this.expansionList.clear();

			this.setTimeElapsed(null, null);
			this.updateMaxStoredNodes(0);

			pathCost.clear();
			goalDistance.clear();
			//TODO This does not reset any searches in progress though...
		}
	}

	public void setGoalNode(Node goalNode){
		if(this.getGoalNode()!=goalNode){
			this.goalNode = goalNode;

			this.path.clear();
			this.expansionList.clear();

			this.setTimeElapsed(null, null);
			this.updateMaxStoredNodes(0);

			pathCost.clear();
			goalDistance.clear();
			//TODO This does not reset any searches in progress though...
		}
	}

	/**
	 * Expands the Node it receives, and adds all of its children that are also tower Nodes to {@link route.Search#expansionList}.
	 * Also updates the total path-cost and goal-distance in all of the encountered tower Nodes if a shorter path to the tower Node is discovered.
	 * Additionally: This method makes sure that the distance between tower-Nodes takes into account the distance through intermediate Nodes as well, thus ensuring optimality
	 * @param currentNode The Node to expand
	 * @return A list of every tower Node in the same Way(s) as the received Node that has not been visited yet, or to which a shorter path has been found
	 */
	List<Node> expandNode(Node nodeToExpand){
		List<Node> towerNodes=new ArrayList<Node>();

		/***************Link this Node's children to it***************/
		if(nodeToExpand!=null){
			for(Way w:SearchDatabase.getWaysContainingNode(nodeToExpand.getExternalId())){
				List<Node> children=w.getNodeRelations();
				Node child;
				int currentNodeIndex=children.indexOf(nodeToExpand);
				Node prevTowerNode=nodeToExpand;
				Node intermediateParent=nodeToExpand;

				//Makes sure the distance between tower-Nodes takes into account the distance between the intermediate Nodes as well.
				//The path cannot be guaranteed to be optimal otherwise.
				double distanceCounter=0;

				//Add connections to Nodes that are listed after the currentNode
				for(int i=currentNodeIndex-1;i>=0;i--){
					child=children.get(i);
					if(child.equals(nodeToExpand)||child.equals(this.getStartNode())){//In case the currentNode appears more than once in a Way
						prevTowerNode=child;
						distanceCounter=0;
						intermediateParent=child;
						continue;
					}

					distanceCounter+=Search.distanceBetweenPoints(intermediateParent, child);
					if(child.isTowerNode()||child.equals(this.getGoalNode())){
						if(getEstimatedMinimalCost(child)>(getPathCost(prevTowerNode)+distanceCounter+Search.distanceBetweenPoints(child,this.getGoalNode()))){

							updatePathCost(child,getPathCost(prevTowerNode)+distanceCounter);
							updateGoalDistance(child,Search.distanceBetweenPoints(child,this.getGoalNode()));

							this.expansionList.put(child, prevTowerNode);
							towerNodes.remove(child);//To ensure that the Node only appears once in the queue
							towerNodes.add(child);
						}else{
							//If this Node has not been encountered yet
							if(this.expansionList.putIfAbsent(child, prevTowerNode)==null){
								towerNodes.add(child);
							}

						}
						distanceCounter=0;//reset because a new towerNode has been encountered and added to the queue
						prevTowerNode=child;
					}
					intermediateParent=child;
				}

				//Add connections to Nodes that are listed after the currentNode
				prevTowerNode=nodeToExpand;
				distanceCounter=0;
				intermediateParent=nodeToExpand;
				int mixIndex=1;//Lets us make sure the list 'towerNodes' is ordered by each Node's order in the Way - with respect to nodeToExpand's position in that list
				for(int i=currentNodeIndex+1;i<children.size();i++){
					child=children.get(i);
					if(child.equals(nodeToExpand)||child.equals(this.getStartNode())){//In case the currentNode appears more than once in a Way
						prevTowerNode=child;
						distanceCounter=0;
						intermediateParent=child;
						continue;
					}

					distanceCounter+=Search.distanceBetweenPoints(intermediateParent, child);
					if(child.isTowerNode()||child.equals(this.getGoalNode())){
						if(getEstimatedMinimalCost(child)>(getPathCost(prevTowerNode)+distanceCounter+Search.distanceBetweenPoints(child,this.getGoalNode()))){

							updatePathCost(child,getPathCost(prevTowerNode)+distanceCounter);
							updateGoalDistance(child,Search.distanceBetweenPoints(child,this.getGoalNode()));

							this.expansionList.put(child, prevTowerNode);
							towerNodes.remove(child);//To ensure that the Node only appears once in the queue
							if(!(mixIndex>towerNodes.size())){
								towerNodes.add(mixIndex,child);
								mixIndex+=3;
							}else{towerNodes.add(child);}
						}else{
							//If this Node has not been encountered yet
							if(this.expansionList.putIfAbsent(child, prevTowerNode)==null){
								towerNodes.remove(child);//To ensure that the Node only appears once in the queue
								if(!(mixIndex>towerNodes.size())){
									towerNodes.add(mixIndex,child);
									mixIndex+=2;
								}else{towerNodes.add(child);}
							}

						}
						distanceCounter=0;//reset because a new towerNode has been encountered and added to the queue
						prevTowerNode=child;
					}
					intermediateParent=child;
				}
			}
		}
		return towerNodes;
	}

}
