package aurora.ide.meta.gef.editors.source.gen.core;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.exception.TemplateNotBindedException;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.ILink;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.link.Parameter;

class DisplayScreenGenerator extends ScreenGenerator {

	

	private ILink link;

	public DisplayScreenGenerator(IProject project, ILink link, IFile file) {
		super(project, file);
		this.link = link;
	}

	@Override
	public String genFile(String header, ViewDiagram view)
			throws TemplateNotBindedException {
		String bindTemplate = view.getBindTemplate();

		if (bindTemplate == null || "".equals(bindTemplate))
			throw new TemplateNotBindedException();
		init(view);
		

		run(view);

		bindModelQueryPara();

		String xml = header + this.getScreenMap().toXML();
		return xml;
	}

	private void bindModelQueryPara() {
		ViewDiagram viewDiagram = this.getViewDiagram();
		List<Container> sectionContainers = viewDiagram.getSectionContainers(
				viewDiagram, new String[] { Container.SECTION_TYPE_QUERY,
						Container.SECTION_TYPE_RESULT });
		List<Parameter> parameters = link.getParameters();
		for (Container container : sectionContainers) {
			String findDatasetId = this.findDatasetId(container);
			CompositeMap datasetsMap = this.getDatasetsMap();
			CompositeMap childByAttrib = datasetsMap.getChildByAttrib("id",
					findDatasetId);
			if (childByAttrib == null)
				continue;
			String model = childByAttrib.getString("model", "");
			if (!"".equals(model)) {
				String queryUrl = this.getQueryUrl(model, parameters);
				if (isBindTarget(container) == false) {
					childByAttrib.put("queryUrl", queryUrl);
					childByAttrib.put("loadData", true);
				}
			}
		}
	}

	private boolean isBindTarget(Container c) {
		Dataset findDataset = this.findDataset(c);
		if (findDataset instanceof ResultDataSet) {
			Container target = ((ResultDataSet) findDataset)
					.getQueryContainer().getTarget();
			if (target != null) {
				return true;
			}
		}
		return false;
	}

	private String getQueryUrl(String model, List<Parameter> parameters) {
		String request = "${/request/@context_path}/autocrud/";
		StringBuilder sb = new StringBuilder(request);
		String query = "/query?";
		sb.append(model).append(query);
		String kv = "key=${/parameter/@key}";
		for (int i = 0; i < parameters.size(); i++) {
			String name = parameters.get(i).getName();
			kv = kv.replaceAll("key", name);
			sb.append(kv);
			if (i < parameters.size() - 1) {
				sb.append("&");
			}
		}
		return sb.toString();
	}
}
