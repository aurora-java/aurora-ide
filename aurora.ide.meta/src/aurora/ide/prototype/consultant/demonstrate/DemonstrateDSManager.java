package aurora.ide.prototype.consultant.demonstrate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.XMLOutputter;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.plugin.source.gen.screen.model.DemonstrateDS;

public class DemonstrateDSManager {
	private static final String dsLocation = System.getProperty("user.home")
			+ "/Aurora Quick UI/demonstrateDS.xml";
	private List<DemonstrateDS> demonstrateDS = new ArrayList<DemonstrateDS>();

	private static DemonstrateDSManager instance;

	static public DemonstrateDSManager getInstance() {
		return instance;
	}

	static public DemonstrateDSManager makeInstance() {
		return instance = new DemonstrateDSManager();
	}

	public void loadDemonstrateDS() {
		if (new File(dsLocation).exists() == false)
			return;
		CompositeMap loadFile = CompositeMapUtil.loadFile(new File(dsLocation));
		if (loadFile == null)
			return;
		loadFile.iterate(new IterationHandle() {

			public int process(CompositeMap map) {
				if ("DS".equals(map.getName())) {
					DemonstrateDS ds = new DemonstrateDS(map.getChild("Name")
							.getText(), map.getChild("Data").getText());
					demonstrateDS.add(ds);
				}
				return IterationHandle.IT_CONTINUE;
			}
		}, false);
	}

	public void saveDemonstrateDS() {

		File file = new File(dsLocation);
		try {
			if (file.getParentFile().exists() == false) {
				file.getParentFile().mkdir();
			}
			if (file.exists() == false)
				file.createNewFile();
			XMLOutputter.saveToFile(file, createCompositeMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private CompositeMap createCompositeMap() {
		CompositeMap map = new CompositeMap("List");
		for (DemonstrateDS ds : demonstrateDS) {
			CompositeMap dsMap = map.createChild("DS");
			dsMap.createChild("Name").setText(ds.getName());
			dsMap.createChild("Data").setText(ds.getData());
		}
		return map;
	}

	public DemonstrateDS[] getDemonstrateDS() {
		return demonstrateDS.toArray(new DemonstrateDS[demonstrateDS.size()]);
	}

	public DemonstrateDS getDemonstrateDS(String dsName) {
		for (DemonstrateDS ds : demonstrateDS) {
			if (dsName.equals(ds.getName())) {
				return ds;
			}
		}
		return null;
	}

	public void addDemonstrateDS(DemonstrateDS ds) {
		demonstrateDS.add(ds);
	}

	public void removeDemonstrateDS(DemonstrateDS ds) {
		demonstrateDS.remove(ds);
	}
}
