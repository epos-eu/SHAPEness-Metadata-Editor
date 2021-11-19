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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.widgets.geomap.OsmTileServer;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.nebula.widgets.geomap.TileServer;
import org.eclipse.nebula.widgets.geomap.jface.GeoMapViewer;
import org.eclipse.nebula.widgets.geomap.jface.GoogleMapsSearchServer;
import org.eclipse.nebula.widgets.geomap.jface.IToolTipProvider;
import org.eclipse.nebula.widgets.geomap.jface.LabelImageProvider;
import org.eclipse.nebula.widgets.geomap.jface.Located;
import org.eclipse.nebula.widgets.geomap.jface.LocationProvider;
import org.eclipse.nebula.widgets.geomap.jface.OsmSearchServer;
import org.eclipse.nebula.widgets.geomap.jface.SearchServer;
import org.eclipse.nebula.widgets.geomap.jface.SearchServer.Result;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GeoMapDialog extends TitleAreaDialog {

	

    private GeoMapViewer geoMapViewer;
    private ListViewer contentViewer;
    private String[] searchServerNames = { "OSM Nomination", "Google Maps" };
    private SearchServer[] searchServers = { new OsmSearchServer(), new GoogleMapsSearchServer() };

	public GeoMapDialog(Shell parentShell) {
        super(parentShell);
        
    }
    
    @Override
	public void create() {
		super.create();
		setTitle("GeoMap");
		//setTitleImage(Icons.INFO_DIALOG.image());

	}


    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        /*
        Composite composite = new Composite(area, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		*/
        
        ScrolledComposite sc = new ScrolledComposite(area, SWT.BORDER | SWT.V_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        Composite composite = new Composite(sc, SWT.NULL);
        sc.setContent(composite);
        GridLayout layout = new GridLayout();
        
        composite.setLayout(layout);
        sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		
		createParameters(composite);
		geoMapViewer = new GeoMapViewer(composite, SWT.NONE);
		configureMapViewer();
		
		 
        return composite;
    }

    private void configureMapViewer() {
		geoMapViewer.getGeoMap().setTileServer(OsmTileServer.TILESERVERS[0]);
		// geoMapViewer.getGeoMap().setTileServer(GoogleTileServer.TILESERVERS[0]);

		geoMapViewer.setLabelProvider(labelProvider);
		geoMapViewer.setLocationProvider(new LocationProvider() {
			@Override
			public PointD getLonLat(Object element) {
				if (element instanceof Located) {
					return ((Located) element).getLonLat();
				}
				return null;
			}

			@Override
			public boolean setLonLat(Object element, double lon, double lat) {
				if (element instanceof Located) {
					((Located) element).setLonLat(lon, lat);
					return true;
				}
				return false;
			}
		});
		geoMapViewer.setContentProvider(new ArrayContentProvider());
		geoMapViewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				setViewerInputs(contributorLocations);
			}
		});
	}
    
    public void createParameters(Composite parent) {
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).applyTo(parent);

		Group group = new Group(parent, SWT.NONE);
		group.setText("Interaction");
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		group.setLayout(new GridLayout(2, false));

		Label tileServerLabel = new Label(group, SWT.NONE);
		tileServerLabel.setText("Tile server: ");
		tileServerLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		final Combo tileServerControl = new Combo(group, SWT.NONE);
		tileServerControl.setItems(new String[] { "http://tile.openstreetmap.org/{0}/{1}/{2}.png",
		"http://mt1.google.com/vt/lyrs=m@129&hl=en&s=Galileo&z={0}&x={1}&y={2}" });
		tileServerControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		tileServerControl.select(0);
		tileServerControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selection = tileServerControl.getItem(tileServerControl.getSelectionIndex());
				geoMapViewer.getGeoMap().setTileServer(new TileServer(selection, 15));
			}
		});

		Label moveSelectionModeLabel = new Label(group, SWT.NONE);
		moveSelectionModeLabel.setText("Move selection mode: ");
		moveSelectionModeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		final Combo moveSelectionModeControl = new Combo(group, SWT.READ_ONLY);
		moveSelectionModeControl.setItems(new String[] { "Cannot move selection", "Allow, check readonly on mouse down",
		"Allow, just try to set new location" });
		moveSelectionModeControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		moveSelectionModeControl.select(1);
		moveSelectionModeControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				geoMapViewer.setMoveSelectionMode(moveSelectionModeControl.getSelectionIndex());
			}
		});

		Label clipRuleLabel = new Label(group, SWT.NONE);
		clipRuleLabel.setText("Clip rule: ");
		clipRuleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		final Combo clipRuleControl = new Combo(group, SWT.READ_ONLY);
		clipRuleControl.setItems(new String[] { "Don't clip", "Clip on element position", "Clip in image bounds" });
		clipRuleControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		clipRuleControl.select(1);
		clipRuleControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				geoMapViewer.setClipRule(clipRuleControl.getSelectionIndex());
			}
		});

		addLabel("Search: ", group);
		final Text searchText = new Text(group, SWT.SEARCH | SWT.CANCEL);
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		addLabel("With: ", group);
		final Combo searchServerControl = new Combo(group, SWT.READ_ONLY);
		searchServerControl.setItems(searchServerNames);
		searchServerControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		searchServerControl.select(0);
		searchText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					setViewerInputs(contributorLocations);
				} else {
					doSearch(searchText.getText(), searchServerControl.getSelectionIndex());
				}
			}
		});

		addLabel("Contents: ", group).setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		contentViewer = new ListViewer(group, SWT.NONE);
		contentViewer.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		contentViewer.setContentProvider(new ArrayContentProvider());
		contentViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				String text = labelProvider.getText(element);
				if (labelProvider instanceof IToolTipProvider) {
					text += " (" + ((IToolTipProvider) labelProvider).getToolTip(element) + ")";
				}
				return text;
			}
		});
		contentViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				geoMapViewer.setSelection(event.getSelection(), true);
			}
		});
		setViewerInputs(contributorLocations);
	}
    
    private void doSearch(String search, int selectionIndex) {
		SearchServer server = searchServers[selectionIndex];
		Object[] results = server.doSearch(search);
		if (results != null) {
			setViewerInputs(results);
		}
	}

    
    private Label addLabel(String text, Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		return label;
	}

    
    private void setViewerInputs(Object[] results) {
		if (geoMapViewer != null && geoMapViewer.getContentProvider() != null) {
			geoMapViewer.setInput(results);
			geoMapViewer.revealAll();
		}
		if (contentViewer != null && contentViewer.getContentProvider() != null) {
			contentViewer.setInput(results);
		}
	}
    
    private static class ContributorLocation extends Located.Static {
		public final String name;
		public PointD location;
		public final String locationText;
		public final boolean committer;

		public ContributorLocation(String name, PointD location, String locationText, boolean committer) {
			super();
			this.name = name;
			this.location = location;
			this.locationText = locationText;
			this.committer = committer;
		}

		@Override
		public String toString() {
			return name + ", " + locationText + " @ " + location.x + ", " + location.y;
		}

		@Override
		public PointD getLonLat() {
			return location;
		}
	}

    
    private ContributorLocation[] contributorLocations = {
			new ContributorLocation("Hallvard Traetteberg", new PointD(10.4234, 63.4242), "Trondheim, Norway", true),
			new ContributorLocation("Stepan Rutz", new PointD(6.8222, 50.9178), "Frechen, Germany", false),
			new ContributorLocation("Wim Jongman", new PointD(4.6410, 52.3894), "Haarlem, Netherlands", true),
			new ContributorLocation("Dirk Fauth", new PointD(9.1858, 48.7775), "Stuttgart, Germany", true),
			new ContributorLocation("Tom Schindl", new PointD(11.4000, 47.2671), "Innsbruck, Austria", true),
			new ContributorLocation("Matthew Hall", new PointD(-111.97, 40.54), "Riverton, Utah, USA", true),
			new ContributorLocation("Justin Dolezy", new PointD(-0.34, 51.48), "Richmond, Surrey, UK", true),
			new ContributorLocation("Edwin Park", new PointD(-74.07, 40.76), "Hoboken, New Jersey, USA", true),
			new ContributorLocation("Mickael Istria", new PointD(5.7349, 45.1872), "Grenoble, France", true), };

    
    private LabelImageProvider labelProvider = new LabelImageProvider() {

		private RGB contributorColor = new RGB(255, 250, 200);
		private RGB committerColor = new RGB(200, 255, 200);

		@Override
		public Image getImage(Object element) {
			if (element instanceof SearchServer.Result) {
				setFillColor(committerColor);
			} else if (element instanceof ContributorLocation) {
				setFillColor(((ContributorLocation) element).committer ? committerColor : contributorColor);
			}
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof SearchServer.Result) {
				return ((SearchServer.Result) element).getName();
			} else if (element instanceof ContributorLocation) {
				ContributorLocation contributorLocation = (ContributorLocation) element;
				return contributorLocation.name;
			}
			return null;
		}

		@Override
		public Object getToolTip(Object element) {
			if (element instanceof SearchServer.Result) {
				Result result = (SearchServer.Result) element;
				PointD lonLat = result.getLonLat();
				return result.getText() + " @ " + lonLat.x + "," + lonLat.y;
			} else if (element instanceof ContributorLocation) {
				return ((ContributorLocation) element).toString();
			}
			return null;
		}
	};

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Spatial selection");
        //shell.setMessage(term); 
    }

    @Override
    public void okPressed() {
        close();
    }

	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

    
}
