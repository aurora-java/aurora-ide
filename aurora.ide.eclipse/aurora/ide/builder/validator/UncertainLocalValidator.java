package aurora.ide.builder.validator;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.processor.AbstractProcessor;

public class UncertainLocalValidator extends AbstractValidator {

	public UncertainLocalValidator(IFile file) {
		super(file);
	}

	public UncertainLocalValidator() {
	}

	@Override
	public AbstractProcessor[] getMapProcessor() {
		return new AbstractProcessor[] { new AbstractProcessor() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void processMap(BuildContext bc) {
				if (BuildContext.LEVEL_CONFIG_PROBLEM == 0)
					return;
				if ("path-config".equalsIgnoreCase(bc.map.getName())) {
					for (Map.Entry entry : (Set<Map.Entry>) bc.map.entrySet()) {
						String key = (String) entry.getKey();
						String value = bc.map.getString(key);
						IRegion region = bc.info.getAttrValueRegion2(key);
						int line = bc.info.getLineOfRegion(region) + 1;
						File f = new File(value);
						if (f.exists()) {
							if (!f.isDirectory())
								AuroraBuilder.addMarker(bc.file, key + " : "
										+ value + " 存在 , 但不是一个目录", line,
										region,
										BuildContext.LEVEL_CONFIG_PROBLEM,
										AuroraBuilder.CONFIG_PROBLEM);
						} else
							AuroraBuilder.addMarker(bc.file, key + " : "
									+ value + " 不存在 ", line, region,
									BuildContext.LEVEL_CONFIG_PROBLEM,
									AuroraBuilder.CONFIG_PROBLEM);
					}
				}
			}

			@Override
			public void processComplete(IFile file, CompositeMap map,
					IDocument doc) {

			}
		} };
	}
}
