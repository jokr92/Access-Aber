package main;
/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2014 Christian Pesch
 * Copyright 2014 Ludwig M Brinckmann
 * Copyright 2014-2016 devemux86
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.mapsforge.map.awt.util.JavaPreferences;
import org.mapsforge.map.awt.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.debug.TileCoordinatesLayer;
import org.mapsforge.map.layer.debug.TileGridLayer;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.download.tilesource.TileSource;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.reader.ReadBuffer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import database.AreaAndBuildingTags;
import database.BuildDatabase;
import database.Node;
import database.SearchDatabase;
import database.Way;
import route.AStar;
import route.BreadthFirstSearch;
import route.DepthFirstSearch;
import route.GreedyBestFirstSearch;
import route.Search;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public final class RunTheSystem {
	/***********************DETERMINES WHICH ALGORITHM IS USED FOR ROUTE PLANNING***********************/
	
	private static Search algorithm = new AStar();//Just to ensure a default algorithm
	
	/***********************DETERMINES WHICH ALGORITHM IS USED FOR ROUTE PLANNING***********************/
	
	private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;
	private static final boolean SHOW_DEBUG_LAYERS = false;

	private static final String MESSAGE = "Are you sure you want to exit the application?";
	private static final String TITLE = "Confirm close";

	private static final MapView mapView = createMapView();



	//List containing the path(s) found by the system
	private static List<Node> path;

	private static Node startNode, goalNode;
	
	/**
	 * Used to lock the thread used to plan routes. I'm sure there's a better way to lock the threads though
	 */
	private static boolean routePlanningInProgress;



	/**
	 * Starts the {@code Samples}.
	 *
	 * @param args Assumes args[0] is startLatitude, args[1] is startLongitude, args[2] is goalLatitude, and args[3] is goalLongitude
	 */
	public static void main(String args[]) {
		/***********************************************Initialize the system***********************************************/
		BuildDatabase.readConfig("map.osm");
		BuildDatabase.setWays(SearchDatabase.filterAccessibleWays(Arrays.asList(BuildDatabase.getWays())));
		BuildDatabase.setNodes(SearchDatabase.filterAccessibleNodes(Arrays.asList(BuildDatabase.getWays())));

		try{
			if(args[0].equalsIgnoreCase("BFS")){
				algorithm = new BreadthFirstSearch();
			}else if(args[0].equalsIgnoreCase("DFS")){
				algorithm = new DepthFirstSearch();
			}else if(args[0].equalsIgnoreCase("GBFS")){
				algorithm = new GreedyBestFirstSearch();
			}else{
				algorithm = new AStar();
			}
			
			startNode = SearchDatabase.findClosestNode(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
			goalNode = SearchDatabase.findClosestNode(Double.parseDouble(args[3]),Double.parseDouble(args[4]));

			algorithm.setStartNode(startNode);
			algorithm.setGoalNode(goalNode);

			path=algorithm.findPath();

			System.out.println("This is your path:");
			for(Node step:path){
				System.out.println(step);
			}

			/*********************************Handle errors*********************************/

		}catch(NumberFormatException e){//Would never be reached if args[] is a double. As any missing input would be 0 by default
			System.err.println("One or more elements in the input were not of type: double");

			try{
				System.out.println("start Node:" + SearchDatabase.findClosestNode(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
			}catch(NumberFormatException s){
				System.err.println("No start Node could be found. Please check input");
			}
			try{
				System.out.println("goal Node:" + SearchDatabase.findClosestNode(Double.parseDouble(args[3]),Double.parseDouble(args[4])));
			}catch(NumberFormatException g){
				System.err.println("No goal Node could be found. please check input");
			}
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("1 String and 4 coordinates expected as input.\n" + args.length + " elements received.\nPlease try again.");
		}

		/***********************************************\Initialize the system***********************************************/
		/**************************************************Create the Map**************************************************/

		// Increase read buffer limit
		ReadBuffer.setMaximumBufferSize(6500000);

		//TODO getMapFiles() is apparently able to load multiple '.map'-files. Is this useful in my application?
		String[] map={"./maps/europe_great-britain_wales-gh/wales.map"};
		List<File> mapFiles = getMapFiles(map);
		final BoundingBox boundingBox = addLayers(mapView, mapFiles);

		final PreferencesFacade preferencesFacade = new JavaPreferences(Preferences.userNodeForPackage(RunTheSystem.class));

		final JFrame frame = new JFrame();
		frame.setTitle("Access Aber");
		frame.add(mapView);
		frame.pack();
		frame.setSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(frame, MESSAGE, TITLE, JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					mapView.getModel().save(preferencesFacade);
					mapView.destroyAll();
					AwtGraphicFactory.clearResourceMemoryCache();
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				}
			}

			@Override
			public void windowOpened(WindowEvent e) {
				final Model model = mapView.getModel();
				// model.init(preferencesFacade);
				byte zoomLevelMin = LatLongUtils.zoomForBounds(model.mapViewDimension.getDimension(), boundingBox, model.displayModel.getTileSize());
				byte zoomLevelStart = 17;// zoom level chosen based on experimentation. Accepted range is: 1(7)-25
				byte zoomLevelMax = 24;// zoom level chosen based on experimentation. Accepted range is: 1(7)-25
				if(startNode!=null&&goalNode!=null){
					LatLong center = new LatLong((startNode.getLatitude()+goalNode.getLatitude())/2.0,(startNode.getLongitude()+goalNode.getLongitude())/2.0);
					model.mapViewPosition.setMapPosition(new MapPosition(center, zoomLevelStart));
				}else{
					model.mapViewPosition.setMapPosition(new MapPosition(boundingBox.getCenterPoint(), zoomLevelMin));
				}

				model.mapViewPosition.setZoomLevelMin(zoomLevelMin);//Restricts how far out you're allowed to zoom
				model.mapViewPosition.setZoomLevelMax(zoomLevelMax);//Restricts how far in you're allowed to zoom
			}
		});
		if(path!=null)
			drawRoute(path);
		frame.setVisible(true); 

		/**************************************************\Create the Map**************************************************/
	}

	public static void drawRoute(List<Node> steps){
		
		/*********Remove old markers and polylines*********/
		
		List<Integer> layersToRemove = new ArrayList<Integer>();
		Layers layer = mapView.getLayerManager().getLayers();
		for(int i=0;i<layer.size();i++){
			if(layer.get(i).getClass().equals(Circle.class)||
					layer.get(i).getClass().equals(Polyline.class)){
				layersToRemove.add(i);
			}
		}
		for(int i=0;i<layersToRemove.size();i++){
			int num=layersToRemove.get(i);
			mapView.getLayerManager().getLayers().remove(num-i);
		}

		// instantiating the start and end markers
		Circle startPosMarker = new Circle(null, 6, getStartMarkerPaint(true), getStartMarkerPaint(true), true);
		Circle endPosMarker = new Circle(null, 6, getStartMarkerPaint(false), getStartMarkerPaint(false), true);

		/****************Adding the start and goal markers****************/
		if(startNode!=null){
			LatLong start = new LatLong(startNode.getLatitude(), startNode.getLongitude());
			startPosMarker.setLatLong(start);

			mapView.getLayerManager().getLayers().add(startPosMarker, false);//false refers to whether to repaint the frame or not
		}else{System.err.println("Start node not found");}

		if(goalNode!=null){
			LatLong goal = new LatLong(goalNode.getLatitude(), goalNode.getLongitude());
			endPosMarker.setLatLong(goal);

			mapView.getLayerManager().getLayers().add(endPosMarker, false);//false refers to whether to repaint the frame or not
		}else{System.err.println("Goal node not found");}

		/****************Adding the polylines****************/
		//creates a new polyline whenever an area (e.g building, parking lot) is entered or exited
		List<Polyline> polylines = new ArrayList<Polyline>();
		boolean changeArea=true;//Indicates whether an area (e.g building, parking lot) has been entered/exited
		boolean isArea=false;
		List<LatLong> coordinateList = new ArrayList<LatLong>();
		Polyline polyline = null;

		for(int i=0;i<steps.size();i++){

			LatLong latlon = new LatLong(steps.get(i).getLatitude(),steps.get(i).getLongitude());

			if(i+1<steps.size()){
				List<String> parentChild = new ArrayList<String>();
				parentChild.add(steps.get(i).getExternalId());
				parentChild.add(steps.get(i+1).getExternalId());

				boolean nodePartOfArea=false;
				for(Way w:SearchDatabase.getWaysContainingNode(parentChild)){

					for(Entry<String, Object> entry:w.getKeyValuePairs()){

						for(AreaAndBuildingTags areaTag:AreaAndBuildingTags.values()){
							if(entry.getKey().equals(areaTag.getKey())&&entry.getValue().equals(areaTag.getValue())){
								nodePartOfArea=true;

								if(isArea==false){
									changeArea=true;//Indicates entrance into an area (building, parking lot, etc.)
									isArea=true;
								}else{
									changeArea=false;
								}
								break;
							}
						}
					}
				}


				if(nodePartOfArea^isArea){//indicates a mismatch between the iaArea-tag and the nodePartOfArea-tag
					isArea=!isArea;
					changeArea=true;
				}
			}

			if(changeArea&&isArea==false){

				if(polyline!=null){
					coordinateList.add(latlon);//Makes sure the lines intersect
					polylines.add(polyline);
				}

				polyline = new Polyline(getStripPaint(), AwtGraphicFactory.INSTANCE);
				changeArea=false;

				// set lat long for the polyline
				coordinateList = polyline.getLatLongs();
				coordinateList.clear();
			}
			else if(changeArea&&isArea==true){
				if(polyline!=null){
					coordinateList.add(latlon);//Makes sure the lines intersect
					polylines.add(polyline);
				}

				polyline = new Polyline(getAreaPaint(), AwtGraphicFactory.INSTANCE);
				changeArea=false;

				// set lat long for the polyline
				coordinateList = polyline.getLatLongs();
				coordinateList.clear();
			}

			coordinateList.add(latlon);//add another coordinate to a polyline
		}

		/****************Adds the final polyline****************/
		if(polyline!=null){
			polylines.add(polyline);
		}


		/****************adding every polyline to the mapview****************/
		for(Polyline line:polylines){
			mapView.getLayerManager().getLayers().add(line, false);//false refers to whether to repaint the frame or not
		}


		/****************update the frame with all the new markers and lines****************/
		mapView.getLayerManager().redrawLayers();
	}

	private static Paint getStripPaint(){
		// instantiating the paint object
		Paint routePaint = AwtGraphicFactory.INSTANCE.createPaint();

		routePaint.setColor(Color.BLUE);
		routePaint.setStrokeWidth(5);
		routePaint.setStyle(Style.STROKE);
		return routePaint;
	}

	private static Paint getAreaPaint(){
		// instantiating the paint object
		Paint routePaint = AwtGraphicFactory.INSTANCE.createPaint();

		routePaint.setColor(Color.RED);
		//routePaint.setDashPathEffect(new float[] { 25, 15 });
		routePaint.setStrokeWidth(3);
		routePaint.setStyle(Style.STROKE);
		return routePaint;
	}

	private static Paint getStartMarkerPaint(boolean startMarker){
		Paint markerPaint = AwtGraphicFactory.INSTANCE.createPaint();

		if(startMarker){
			markerPaint.setColor(Color.GREEN);
		}else{
			markerPaint.setColor(Color.RED);
		}

		markerPaint.setStrokeWidth(6);
		markerPaint.setStyle(Style.STROKE);

		return markerPaint;
	}

	private static BoundingBox addLayers(MapView mapView, List<File> mapFiles) {
		Layers layers = mapView.getLayerManager().getLayers();

		// Raster
		/*TileDownloadLayer tileDownloadLayer = createTileDownloadLayer(createTileCache(0), mapView.getModel().mapViewPosition);
        layers.add(tileDownloadLayer);
        tileDownloadLayer.start();
        BoundingBox result = new BoundingBox(LatLongUtils.LATITUDE_MIN, LatLongUtils.LONGITUDE_MIN, LatLongUtils.LATITUDE_MAX, LatLongUtils.LONGITUDE_MAX);
        mapView.setZoomLevelMin(OpenStreetMapMapnik.INSTANCE.getZoomLevelMin());
        mapView.setZoomLevelMax(OpenStreetMapMapnik.INSTANCE.getZoomLevelMax());*/

		// Vector
		BoundingBox result = null;
		for (int i = 0; i < mapFiles.size(); i++) {
			File mapFile = mapFiles.get(i);
			TileRendererLayer tileRendererLayer = createTileRendererLayer(createTileCache(i),
					mapView.getModel().mapViewPosition, true, true, false, mapFile);
			BoundingBox boundingBox = tileRendererLayer.getMapDataStore().boundingBox();
			result = result == null ? boundingBox : result.extendBoundingBox(boundingBox);
			layers.add(tileRendererLayer);
		}

		// Debug
		if (SHOW_DEBUG_LAYERS) {
			layers.add(new TileGridLayer(GRAPHIC_FACTORY, mapView.getModel().displayModel));
			layers.add(new TileCoordinatesLayer(GRAPHIC_FACTORY, mapView.getModel().displayModel));
		}

		return result;
	}

	private static MapView createMapView() {
		MapView mapView = new MapView();
		mapView.getMapScaleBar().setVisible(true);
		if (SHOW_DEBUG_LAYERS) {
			mapView.getFpsCounter().setVisible(true);
		}

		return mapView;
	}

	private static TileCache createTileCache(int index) {
		TileCache firstLevelTileCache = new InMemoryTileCache(256);
		File cacheDirectory = new File(System.getProperty("java.io.tmpdir"), "mapsforge" + index);
		TileCache secondLevelTileCache = new FileSystemTileCache(1024, cacheDirectory, GRAPHIC_FACTORY);
		return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
	}

	@SuppressWarnings("unused")
	private static TileDownloadLayer createTileDownloadLayer(TileCache tileCache, MapViewPosition mapViewPosition) {
		TileSource tileSource = OpenStreetMapMapnik.INSTANCE;
		return new TileDownloadLayer(tileCache, mapViewPosition, tileSource, GRAPHIC_FACTORY) {
			@Override
			public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
				
				if(routePlanningInProgress==true){
					System.err.println("Already planning other route");
					return true;
				}

				/**********In case the start/goal Node has not been set yet**********/
				if(startNode==null){
					
					startNode = SearchDatabase.findClosestNode(tapLatLong.getLatitude(), tapLatLong.getLongitude());
					algorithm.setStartNode(startNode);
				}else if(goalNode==null){
					
					goalNode = SearchDatabase.findClosestNode(tapLatLong.getLatitude(), tapLatLong.getLongitude());
					algorithm.setGoalNode(goalNode);

					if(startNode!=null&&goalNode!=null){
						routePlanningInProgress=true;
						drawRoute(algorithm.findPath());
						routePlanningInProgress=false;
						
						return true;
					}
				}

				/**********Change the start/goal Node position, and recalculate the route**********/
				if(startNode!=null&&goalNode!=null){
					if(Search.distanceBetweenPoints(tapLatLong.getLatitude(), tapLatLong.getLongitude(), startNode.getLatitude(), startNode.getLongitude())<
							Search.distanceBetweenPoints(tapLatLong.getLatitude(), tapLatLong.getLongitude(), goalNode.getLatitude(), goalNode.getLongitude())){
						
						startNode = SearchDatabase.findClosestNode(tapLatLong.getLatitude(), tapLatLong.getLongitude());
						algorithm.setStartNode(startNode);
					}else{
						
						goalNode = SearchDatabase.findClosestNode(tapLatLong.getLatitude(), tapLatLong.getLongitude());
						algorithm.setGoalNode(goalNode);
					}
					
					routePlanningInProgress=true;
					drawRoute(algorithm.findPath());
					routePlanningInProgress=false;
				}

				return true;
			}
		};
	}

	private static TileRendererLayer createTileRendererLayer(TileCache tileCache, MapViewPosition mapViewPosition,
			boolean isTransparent, boolean renderLabels, boolean cacheLabels, File mapFile) {
		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, new MapFile(mapFile),
				mapViewPosition, isTransparent, renderLabels, cacheLabels, GRAPHIC_FACTORY) {
			@Override
			public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
				
				if(routePlanningInProgress==true){
					System.err.println("Already planning other route");
					return true;
				}

				/**********In case the start/goal Node has not been set yet**********/
				if(startNode==null){
					startNode = SearchDatabase.findClosestNode(tapLatLong.getLatitude(), tapLatLong.getLongitude());
					algorithm.setStartNode(startNode);
				}else if(goalNode==null){
					goalNode = SearchDatabase.findClosestNode(tapLatLong.getLatitude(), tapLatLong.getLongitude());
					algorithm.setGoalNode(goalNode);

					if(startNode!=null&&goalNode!=null){
						routePlanningInProgress=true;
						drawRoute(algorithm.findPath());
						routePlanningInProgress=false;
						return true;
					}
				}

				/**********Change the start/goal Node position, and recalculate the route**********/
				if(startNode!=null&&goalNode!=null){
					Node closestNode = SearchDatabase.findClosestNode(tapLatLong.getLatitude(), tapLatLong.getLongitude());
					if(Search.distanceBetweenPoints(tapLatLong.getLatitude(), tapLatLong.getLongitude(), startNode.getLatitude(), startNode.getLongitude())<
							Search.distanceBetweenPoints(tapLatLong.getLatitude(), tapLatLong.getLongitude(), goalNode.getLatitude(), goalNode.getLongitude())){

						if(!startNode.equals(closestNode)){
							startNode=closestNode;
							algorithm.setStartNode(startNode);
						}else{return true;}//No need to plan the route again if it is the same as before
						
					}else{
						if(!goalNode.equals(closestNode)){
							goalNode=closestNode;
							algorithm.setGoalNode(goalNode);
						}else{return true;}//No need to plan the route again if it is the same as before
						
					}
					routePlanningInProgress=true;
					drawRoute(algorithm.findPath());
					routePlanningInProgress=false;
				}

				return true;
			}
		};
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
		return tileRendererLayer;
	}

	private static List<File> getMapFiles(String[] args) {
		if (args.length == 0) {
			throw new IllegalArgumentException("missing argument: <mapFile>");
		}

		List<File> result = new ArrayList<>();
		for (String arg : args) {
			File mapFile = new File(arg);
			if (!mapFile.exists()) {
				throw new IllegalArgumentException("file does not exist: " + mapFile);
			} else if (!mapFile.isFile()) {
				throw new IllegalArgumentException("not a file: " + mapFile);
			} else if (!mapFile.canRead()) {
				throw new IllegalArgumentException("cannot read file: " + mapFile);
			}
			result.add(mapFile);
		}
		return result;
	}

	private RunTheSystem() {
		throw new IllegalStateException();
	}
}