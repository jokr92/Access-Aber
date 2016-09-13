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
import route.GreedyBestFirst;
import route.Search;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public final class RunTheSystem {
	private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;
	private static final boolean SHOW_DEBUG_LAYERS = false;

	private static final String MESSAGE = "Are you sure you want to exit the application?";
	private static final String TITLE = "Confirm close";

	private static final MapView mapView = createMapView();



	//List containing the path(s) found by the system
	private static List<Node> nodePath;

	private static Node startNode, goalNode;

	/**
	 * Starts the {@code Samples}.
	 *
	 * @param args Assumes args[0] is startLatitude, args[1] is startLongitude, args[2] is goalLatitude, and args[3] is goalLongitude
	 */
	public static void main(String args[]) {
		/***********************************************Initialize the system***********************************************/
		BuildDatabase.readConfig("map.osm");
		Search algorithm = new AStar();

		try{
			//TODO Is this a safe cast? Does this try-catch statement deal with casting-errors?
			startNode = SearchDatabase.findClosestNode(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
			goalNode = SearchDatabase.findClosestNode(Double.parseDouble(args[2]),Double.parseDouble(args[3]));

			algorithm.setStartNode(startNode);
			algorithm.setGoalNode(goalNode);

			nodePath=algorithm.findPath();

			System.out.println("This is your path:");
			for(Node step:nodePath){
				System.out.println(step);
			}

			/*********************************Handle errors*********************************/

		}catch(NumberFormatException e){//Would never be reached if args[] is a double. As any missing input would be 0 by default
			System.out.println("One or more elements in the input were not of type: double");

			try{
				System.out.println("start Node:" + SearchDatabase.findClosestNode(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
			}catch(NumberFormatException s){
				System.out.println("No start Node could be found. Please check input");
			}
			try{
				System.out.println("goal Node:" + SearchDatabase.findClosestNode(Double.parseDouble(args[2]),Double.parseDouble(args[3])));
			}catch(NumberFormatException g){
				System.out.println("No goal Node could be found. please check input");
			}
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("4 coordinates expected as input.\n" + args.length + " coordinate(s) received.\nPlease try again.");
		}

		/***********************************************\Initialize the system***********************************************/
		/**************************************************Create the Map**************************************************/

		// Increase read buffer limit
		ReadBuffer.setMaximumBufferSize(6500000);

		//TODO getMapFiles() is apparently able to load multiple .map-files. Is this useful in my application?
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
				LatLong center = new LatLong((startNode.getLatitude()+goalNode.getLatitude())/2.0,(startNode.getLongitude()+goalNode.getLongitude())/2.0);
				model.mapViewPosition.setMapPosition(new MapPosition(center, zoomLevelStart));
				model.mapViewPosition.setZoomLevelMin(zoomLevelMin);//Restricts how far out you're allowed to zoom
				model.mapViewPosition.setZoomLevelMax(zoomLevelMax);//Restricts how far in you're allowed to zoom
			}
		});
		if(nodePath!=null)
			drawRoute(nodePath);
		frame.setVisible(true); 

		/**************************************************\Create the Map**************************************************/
	}

	public static void drawRoute(List<Node> steps){

		// instantiating the start and end markers
		Circle startPosMarker = new Circle(null, 6, getStartMarkerPaint(true), getStartMarkerPaint(true), true);
		Circle endPosMarker = new Circle(null, 6, getStartMarkerPaint(false), getStartMarkerPaint(false), true);

		//TODO is this necessary?
		//		mapView.getLayerManager().getLayers().remove(startPosMarker, false);
		//		mapView.getLayerManager().getLayers().remove(endPosMarker, false);
		//		mapView.getLayerManager().getLayers().remove(polyline.hashCode(), false);


		if(startNode!=null&&goalNode!=null){
			LatLong start = new LatLong(startNode.getLatitude(), startNode.getLongitude());
			LatLong goal = new LatLong(goalNode.getLatitude(), goalNode.getLongitude());
			startPosMarker.setLatLong(start);
			endPosMarker.setLatLong(goal);
		}

		//creates a new polyline whenever an area (e.g building, parking lot) is entered or exited
		List<Polyline> polylines = new ArrayList<Polyline>();
		boolean change=true;//Indicates whether an area (e.g building, parking lot) has been entered/exited
		boolean area=false;
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
							
							if(area==false){
								change=true;//Indicates entrance into an area (building, parking lot, etc.)
								area=true;
							}else{
								change=false;
							}
							break;
						}
					}
				}
			}
			

			if(nodePartOfArea^area){//indicates a mismatch between the area-tag and the nodePartOfArea-tag
				area=!area;
				change=true;
			}
		}
			
			if(change&&area==false){
				
				if(polyline!=null){
					coordinateList.add(latlon);//Makes sure the lines intersect
					polylines.add(polyline);
				}
				
				polyline = new Polyline(getStripPaint(), AwtGraphicFactory.INSTANCE);
				change=false;
				
				// set lat long for the polyline
				coordinateList = polyline.getLatLongs();
				coordinateList.clear();
			}
			else if(change&&area==true){
				if(polyline!=null){
					coordinateList.add(latlon);//Makes sure the lines intersect
				polylines.add(polyline);
				}
				
				polyline = new Polyline(getAreaPaint(), AwtGraphicFactory.INSTANCE);
				change=false;
				
				// set lat long for the polyline
				coordinateList = polyline.getLatLongs();
				coordinateList.clear();
			}
			
			coordinateList.add(latlon);
		}
		
		if(polyline!=null){
			polylines.add(polyline);
			}


		// adding the layer to the mapview
		mapView.getLayerManager().getLayers().remove(startPosMarker,false);//false refers to whether to repaint the frame or not
		mapView.getLayerManager().getLayers().remove(endPosMarker,false);

		mapView.getLayerManager().getLayers().add(startPosMarker, false);
		mapView.getLayerManager().getLayers().add(endPosMarker, false);

		for(Polyline line:polylines){
			mapView.getLayerManager().getLayers().remove(line,false);//false refers to whether to repaint the frame or not
			mapView.getLayerManager().getLayers().add(line, false);
		}

		mapView.repaint();//updates the frame with all the new markers and lines
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
				System.out.println("Tap on: " + tapLatLong);
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
				System.out.println("Tap on: " + tapLatLong);
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