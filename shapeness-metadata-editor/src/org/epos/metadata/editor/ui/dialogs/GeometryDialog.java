/*******************************************************************************
 * <one line to give the program's name and a brief idea of what it does.>
 *     
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.epos.metadata.editor.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Join;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.core.util.Parameters;
import org.mapsforge.map.awt.graphics.AwtBitmap;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.mapsforge.map.awt.util.AwtUtil;
import org.mapsforge.map.awt.util.JavaPreferences;
import org.mapsforge.map.awt.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.debug.TileCoordinatesLayer;
import org.mapsforge.map.layer.debug.TileGridLayer;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.download.tilesource.TileSource;
import org.mapsforge.map.layer.hills.DiffuseLightShadingAlgorithm;
import org.mapsforge.map.layer.hills.HillsRenderConfig;
import org.mapsforge.map.layer.hills.MemoryCachingHgtReaderTileSource;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.IMapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.util.MapViewProjection;
import org.osgi.framework.Bundle;

public class GeometryDialog extends TitleAreaDialog {


	private String geometry;
	private String oldValue;
	private Button pointType;
	private Button polygonType;
	private Composite area;

	private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;
	private static final boolean SHOW_DEBUG_LAYERS = false;
	private static final boolean SHOW_RASTER_MAP = false;

	private static final String MESSAGE = "Are you sure you want to exit the application?";
	private static final String TITLE = "Confirm close";

	private static int numberOfBaseLayers;

	private static ArrayList<String> pointsGenerated = new ArrayList<>();


	public GeometryDialog(Shell parentShell, String oldValue) {
		super(parentShell);
		this.oldValue = oldValue;
	}


	@Override
	public void create() {
		super.create();
		setTitle("Specify a point or polygon");
		setMessage("Map projection: WGS84 - Click on map to add points or write/update directly in the text area below \ne.g. POINT(10 10) or POLYGON((10 10, 20 20, 30 30, 10 10))");

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		area = (Composite) super.createDialogArea(parent);
		pointsGenerated.clear();
		//area.setSize(1024, 768)
		//container.setLayout(new FillLayout());

		Composite container = new Composite(area, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		/*createPointWidgets(container);
		createPolygonWidgets(container);*/

		createGeometryMap(container);

		container.setSize(1024, 768);

		return container;
	}

	public void createGeometryMap(Composite container) {

		String[] args = new String[1];
		args[0] = "resources/world.map";
		// Multithreaded map rendering
		Parameters.NUMBER_OF_THREADS = 2;

		// Square frame buffer
		Parameters.SQUARE_FRAME_BUFFER = false;

		HillsRenderConfig hillsCfg = null;
		File demFolder = getDemFolder(args);
		if (demFolder != null) {
			MemoryCachingHgtReaderTileSource tileSource = new MemoryCachingHgtReaderTileSource(demFolder, new DiffuseLightShadingAlgorithm(), AwtGraphicFactory.INSTANCE);
			tileSource.setEnableInterpolationOverlap(true);
			hillsCfg = new HillsRenderConfig(tileSource);
			hillsCfg.indexOnThread();
			args = Arrays.copyOfRange(args, 1, args.length);
		}

		List<File> mapFiles = SHOW_RASTER_MAP ? null : getMapFiles(args);
		final MapView mapView = createMapView();
		final BoundingBox boundingBox = addLayers(mapView, mapFiles, hillsCfg);

		final PreferencesFacade preferencesFacade = new JavaPreferences(Preferences.userNodeForPackage(GeometryDialog.class));

		Font fontBold = new Font("Courier", Font.BOLD,12);
		Font fontNormal = new Font("Courier", Font.PLAIN,12);

		final JTextArea geometryInput = new JTextArea();
		geometryInput.setLineWrap(true);
		geometryInput.setWrapStyleWord(true);
		geometryInput.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));


		final JScrollPane scrollPane = new JScrollPane(geometryInput);
		final JButton geometryInputButton = new JButton("Show on map");
		geometryInputButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showPolygon(mapView, geometryInput.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				geometry = geometryInput.getText();
			}
		});

		geometryInputButton.setFont(fontNormal);
		geometryInput.setFont(fontNormal);

		final JPanel geometryInputField = new JPanel();
		geometryInputField.setLayout(new BoxLayout(geometryInputField, BoxLayout.LINE_AXIS));
		geometryInputField.add(scrollPane);
		geometryInputField.add(geometryInputButton);
		geometryInputField.setPreferredSize(new Dimension(Frame.WIDTH, 100));
		geometryInputField.setBackground(Color.WHITE);

		//final JLabel description = new JLabel("Info map: WGS84 - Click on map to add points or write/update directly in the text area below");
		//final JLabel example = new JLabel("e.g. POINT(10 10) or POLYGON((10 10, 20 20, 30 30, 10 10))");
		final JLabel latitude = new JLabel("- Lat");  //LAT
		final JLabel longitude = new JLabel("- Lon"); //LON

		//lat + lon stessa riga

		//description.setFont(fontNormal);
		//example.setFont(fontNormal);
		latitude.setFont(fontBold);
		longitude.setFont(fontBold);


		final JPanel geometryOverlayField = new JPanel();
		geometryOverlayField.setLayout(new BoxLayout(geometryOverlayField, BoxLayout.PAGE_AXIS));
		//geometryOverlayField.add(description);
		//geometryOverlayField.add(example);
		geometryOverlayField.add(latitude);
		geometryOverlayField.add(longitude);
		latitude.setAlignmentX(Component.RIGHT_ALIGNMENT);
		longitude.setAlignmentX(Component.RIGHT_ALIGNMENT);

		geometryOverlayField.setPreferredSize(new Dimension(Frame.WIDTH, 50));
		geometryOverlayField.setBackground(Color.WHITE);


		final Frame frame = SWT_AWT.new_Frame(container);
		frame.setTitle("Geometry Input");
		frame.add(geometryOverlayField, BorderLayout.NORTH);
		frame.add(geometryInputField, BorderLayout.SOUTH);
		frame.add(mapView, BorderLayout.CENTER);
		frame.pack();
		frame.setSize(1024, 768);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				JLabel label = new JLabel(MESSAGE);
				label.setFont(new Font("Courier", Font.PLAIN,12));
				int result = JOptionPane.showConfirmDialog(frame, label, TITLE, JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					mapView.getModel().save(preferencesFacade);
					mapView.destroyAll();
					AwtGraphicFactory.clearResourceMemoryCache();
				}
			}

			@Override
			public void windowOpened(WindowEvent e) {
				final Model model = mapView.getModel();
				model.init(preferencesFacade);
				if (model.mapViewPosition.getZoomLevel() == 0 || !boundingBox.contains(model.mapViewPosition.getCenter())) {
					byte zoomLevel = LatLongUtils.zoomForBounds(model.mapViewDimension.getDimension(), boundingBox, model.displayModel.getTileSize());
					model.mapViewPosition.setMapPosition(new MapPosition(boundingBox.getCenterPoint(), zoomLevel));
				}
			}
		});

		mapView.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				Point tapXY = new Point(e.getX(), e.getY());
				LatLong tapLatLong = new MapViewProjection(mapView).fromPixels(tapXY.x, tapXY.y);
				if (tapLatLong != null) {
					for (int i = mapView.getLayerManager().getLayers().size() - 1; i >= 0; --i) {
						Layer layer = mapView.getLayerManager().getLayers().get(i);
						Point layerXY = new MapViewProjection(mapView).toPixels(layer.getPosition());
						if (layer.onTap(tapLatLong, layerXY, tapXY)) {
							break;
						}
					}
				}
				latitude.setText(tapLatLong.latitude+" - Lat");
				longitude.setText(tapLatLong.longitude+ "- Lon");
			}

			@Override
			public void mouseDragged(MouseEvent e) {}
		});

		mapView.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Point tapXY = new Point(e.getX(), e.getY());
				LatLong tapLatLong = new MapViewProjection(mapView).fromPixels(tapXY.x, tapXY.y);
				if (tapLatLong != null) {
					for (int i = mapView.getLayerManager().getLayers().size() - 1; i >= 0; --i) {
						Layer layer = mapView.getLayerManager().getLayers().get(i);
						Point layerXY = new MapViewProjection(mapView).toPixels(layer.getPosition());
						if (layer.onTap(tapLatLong, layerXY, tapXY)) {
							break;
						}
					}
				}

				JLabel label = new JLabel("<html>Do you want to add a new point on this location?<br><html>"+tapLatLong.toString());
				label.setFont(new Font("Courier", Font.PLAIN,12));
				int result = JOptionPane.showConfirmDialog(frame, label, "Add point", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					StringBuilder sb = new StringBuilder();
					if(pointsGenerated.size()>1 && pointsGenerated.get(0).equals(pointsGenerated.get(pointsGenerated.size()-1)))
						pointsGenerated.remove(pointsGenerated.size()-1);
					pointsGenerated.add(tapLatLong.getLongitude()+" "+tapLatLong.getLatitude());
					if(pointsGenerated.size()>1) {
						sb.append("POLYGON((");
						for(int i = 0; i<pointsGenerated.size();i++) {
							sb.append(pointsGenerated.get(i));
							if(i!=pointsGenerated.size()-1) sb.append(",");
						}
						if(pointsGenerated.size()>2){ 
							sb.append(",");
							sb.append(pointsGenerated.get(0));
						}
						sb.append("))");
					}
					if(pointsGenerated.size()==1) {
						sb.append("POINT(");
						sb.append(pointsGenerated.get(0));
						sb.append(")");
					}

					try {
						showPolygon(mapView, sb.toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					geometryInput.setText(sb.toString());
					geometry = sb.toString();
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});

		frame.setVisible(true);

		numberOfBaseLayers = mapView.getLayerManager().getLayers().size();
		
		if(oldValue != null && !oldValue.isEmpty()) {
			geometryInput.setText(oldValue);
			
			try {
				showPolygon(mapView, geometryInput.getText());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}

	private static void showPolygon(MapView mapView, String pointsString) throws IOException {
		Layers layers = mapView.getLayerManager().getLayers();
		String[] points = null;

		if(pointsString.isBlank()) {
			pointsGenerated.clear();
			while(layers.size()>numberOfBaseLayers) {
				layers.remove(mapView.getLayerManager().getLayers().size()-1);
			}
			return;
		}


		/**********************
		 * 
		 *  Parsing
		 * 
		 **********************/
		pointsString = pointsString.replaceAll("POLYGON", "").replaceAll("POINT", "").replaceAll("\\(", "").replaceAll("\\)","");
		//CHECK valori diversi da "," e lettere
		points = pointsString.split(",");
		pointsGenerated = new ArrayList<>(Arrays.asList(points));


		/**********************
		 * 
		 *  Polygon draw
		 * 
		 **********************/

		Paint paintStroke = AwtGraphicFactory.INSTANCE.createPaint();
		paintStroke.setStyle(Style.STROKE);
		paintStroke.setStrokeJoin(Join.BEVEL);
		paintStroke.setStrokeCap(Cap.SQUARE);
		paintStroke.setStrokeWidth(1);

		/**********************
		 * 
		 *  Point draw
		 * 
		 **********************/
		
		Bundle bundle = Platform.getBundle("org.epos.metadata.editor");
		URL fileURL = bundle.getEntry("icons/marker-2.png");
		File markerFile = null;
		try {
			markerFile = new File(FileLocator.toFileURL(fileURL).getPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStream is = new FileInputStream(markerFile);
		AwtBitmap markerimage = new AwtBitmap(is);

		/**********************
		 * 
		 *  Cleanup
		 * 
		 **********************/

		while(layers.size()>numberOfBaseLayers) {
			layers.remove(mapView.getLayerManager().getLayers().size()-1);
		}

		/**********************
		 * 
		 *  Run
		 * 
		 **********************/


		if(points.length > 1) {
			Paint stroke = GRAPHIC_FACTORY.createPaint();
			stroke.setStrokeWidth((float) 0.0000000001);
			Polyline p = new Polyline(paintStroke, AwtGraphicFactory.INSTANCE);
			for(String point : points) {
				point = point.trim();
				String[] singlePoint = point.split(" ");
				p.addPoint(new LatLong(Float.parseFloat(singlePoint[1]), Float.parseFloat(singlePoint[0])));
				Marker m = new Marker(new LatLong(Float.parseFloat(singlePoint[1]), Float.parseFloat(singlePoint[0])), markerimage,0,0);
				layers.add(m);
			}
			layers.add(p);
		}
		else {
			String[] singlePoint = points[0].trim().split(" ");
			if(singlePoint.length>1) {
				Marker p = new Marker(new LatLong(Float.parseFloat(singlePoint[1]), Float.parseFloat(singlePoint[0])), markerimage, 0, 0);
				layers.add(p);
			}
		}
	}

	private static BoundingBox addLayers(MapView mapView, List<File> mapFiles, HillsRenderConfig hillsRenderConfig) {
		Layers layers = mapView.getLayerManager().getLayers();

		int tileSize = SHOW_RASTER_MAP ? 256 : 512;

		// Tile cache
		TileCache tileCache = AwtUtil.createTileCache(
				tileSize,
				mapView.getModel().frameBufferModel.getOverdrawFactor(),
				1024,
				new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()));

		final BoundingBox boundingBox;
		if (SHOW_RASTER_MAP) {
			// Raster
			mapView.getModel().displayModel.setFixedTileSize(tileSize);
			OpenStreetMapMapnik tileSource = OpenStreetMapMapnik.INSTANCE;
			tileSource.setUserAgent("mapsforge-samples-awt");
			TileDownloadLayer tileDownloadLayer = createTileDownloadLayer(tileCache, mapView.getModel().mapViewPosition, tileSource);
			layers.add(tileDownloadLayer);
			tileDownloadLayer.start();
			mapView.setZoomLevelMin(tileSource.getZoomLevelMin());
			mapView.setZoomLevelMax(tileSource.getZoomLevelMax());
			boundingBox = new BoundingBox(LatLongUtils.LATITUDE_MIN, LatLongUtils.LONGITUDE_MIN, LatLongUtils.LATITUDE_MAX, LatLongUtils.LONGITUDE_MAX);
		} else {
			// Vector
			mapView.getModel().displayModel.setFixedTileSize(tileSize);
			MultiMapDataStore mapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL);
			for (File file : mapFiles) {
				mapDataStore.addMapDataStore(new MapFile(file), false, false);
			}
			TileRendererLayer tileRendererLayer = createTileRendererLayer(tileCache, mapDataStore, mapView.getModel().mapViewPosition, hillsRenderConfig);
			layers.add(tileRendererLayer);
			boundingBox = mapDataStore.boundingBox();
		}

		// Debug
		if (SHOW_DEBUG_LAYERS) {
			layers.add(new TileGridLayer(GRAPHIC_FACTORY, mapView.getModel().displayModel));
			layers.add(new TileCoordinatesLayer(GRAPHIC_FACTORY, mapView.getModel().displayModel));
		}

		return boundingBox;
	}

	private static MapView createMapView() {
		MapView mapView = new MapView();
		mapView.getMapScaleBar().setVisible(true);
		if (SHOW_DEBUG_LAYERS) {
			mapView.getFpsCounter().setVisible(true);
		}

		return mapView;
	}

	private static TileDownloadLayer createTileDownloadLayer(TileCache tileCache, IMapViewPosition mapViewPosition, TileSource tileSource) {
		return new TileDownloadLayer(tileCache, mapViewPosition, tileSource, GRAPHIC_FACTORY) {
			@Override
			public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
				return true;
			}
		};
	}

	private static TileRendererLayer createTileRendererLayer(TileCache tileCache, MapDataStore mapDataStore, IMapViewPosition mapViewPosition, HillsRenderConfig hillsRenderConfig) {
		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore, mapViewPosition, false, true, false, GRAPHIC_FACTORY, hillsRenderConfig) {
			@Override
			public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
				return true;
			}
		};
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
		return tileRendererLayer;
	}

	private static File getDemFolder(String[] args) {
		if (args.length == 0) {
			if (SHOW_RASTER_MAP) {
				return null;
			} else {
				throw new IllegalArgumentException("missing argument: <mapFile>");
			}
		}

		File demFolder = new File(args[0]);
		if (demFolder.exists() && demFolder.isDirectory() && demFolder.canRead()) {
			return demFolder;
		}
		return null;
	}

	private static List<File> getMapFiles(String[] args) {
		if (args.length == 0) {
			throw new IllegalArgumentException("missing argument: <mapFile>");
		}

		List<File> result = new ArrayList<>();
		for (String arg : args) {
			//*****

			Bundle bundle = Platform.getBundle("org.epos.metadata.editor");
			//URL fileURL = bundle.getEntry(arg);
			File mapFile = null;
			try {
				URL configURL = bundle.getResource(arg);
				mapFile = new File(FileLocator.toFileURL(configURL).getPath());
				//mapFile = new File(FileLocator.resolve(fileURL).toURI());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			//******
			//File mapFile = new File(arg);
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

	@Override
	protected boolean isResizable() {
		return true;
	}


	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected void okPressed() {

		super.okPressed();

	}

	@Override
    protected org.eclipse.swt.graphics.Point getInitialSize() {
        return new org.eclipse.swt.graphics.Point(1024, 768);
    }

	public String getGeometry() {
		return geometry;
	}


	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}


}