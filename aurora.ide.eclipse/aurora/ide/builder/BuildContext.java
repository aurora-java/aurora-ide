package aurora.ide.builder;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.preferencepages.BuildLevelPage;

public class BuildContext {
	public static int LEVEL_CONFIG_PROBLEM;
	public static int LEVEL_UNDEFINED_BM;
	public static int LEVEL_UNDEFINED_ATTRIBUTE;
	public static int LEVEL_UNDEFINED_DATASET;
	public static int LEVEL_UNDEFINED_FOREIGNFIELD_SCREEN;
	public static int LEVEL_UNDEFINED_FOREIGNFIELD_BM;
	public static int LEVEL_UNDEFINED_LOCALFIELD_SCREEN;
	public static int LEVEL_UNDEFINED_LOCALFIELD_BM;
	public static int LEVEL_UNDEFINED_SCREEN;
	public static int LEVEL_UNDEFINED_TAG;
	public static int LEVEL_NONOENAMESPACE;

	public IFile file;
	public IDocument doc;
	public CompositeMap map;
	public CompositeMapInfo info;
	public List<Attribute> list;
	public String nullListMsg;

	static {
		initBuildLevel();
	}

	public BuildContext() {

	}

	public static void initBuildLevel() {
		LEVEL_CONFIG_PROBLEM = BuildLevelPage
				.getBuildLevel(AuroraBuilder.CONFIG_PROBLEM);
		LEVEL_NONOENAMESPACE = BuildLevelPage
				.getBuildLevel(AuroraBuilder.NONENAMESPACE);
		LEVEL_UNDEFINED_ATTRIBUTE = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_ATTRIBUTE);
		LEVEL_UNDEFINED_BM = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_BM);
		LEVEL_UNDEFINED_DATASET = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_DATASET);
		LEVEL_UNDEFINED_FOREIGNFIELD_BM = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_FOREIGNFIELD + "_BM");
		LEVEL_UNDEFINED_FOREIGNFIELD_SCREEN = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_FOREIGNFIELD + "_SCREEN");
		LEVEL_UNDEFINED_LOCALFIELD_BM = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_LOCALFIELD + "_BM");
		LEVEL_UNDEFINED_LOCALFIELD_SCREEN = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_LOCALFIELD + "_SCREEN");
		LEVEL_UNDEFINED_SCREEN = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_SCREEN);
		LEVEL_UNDEFINED_TAG = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_TAG);
	}

}
