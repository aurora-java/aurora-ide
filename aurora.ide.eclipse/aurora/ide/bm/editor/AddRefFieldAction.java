package aurora.ide.bm.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.BMUtil;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.ActionListener;

public class AddRefFieldAction extends ActionListener {

	private GridViewer viewer;
	private CompositeMap model;
	private static final String relations = "relations";
	private static final String refModel = "refModel";
	private static final String ref_fields = "ref-fields";
	private static final String refFieldElement = "ref-field";
	private static final String specialSeparator = "\"";
	private static final String[] gridProperties = new String[] { "name",
			"relationName", "ref_model" };
	private static final String[] refFieldProperties = new String[] {
			"sourceField", "name", "relationName" };
	private CompositeMap gridInput;

	public AddRefFieldAction(GridViewer viewer, CompositeMap model,
			int actionStyle) {
		this.viewer = viewer;
		this.model = model;
		setActionStyle(actionStyle);
	}

	public void run() {
		if (model == null) {
			DialogUtil.showErrorMessageBox("This model is null !");
			return;
		}
		gridInput = new CommentCompositeMap("gridInput");
		QualifiedName modelQN = new QualifiedName(model.getNamespaceURI(),
				model.getName());
		Assert.isTrue(
				AuroraConstant.ModelQN.getLocalName().equals(
						modelQN.getLocalName()),
				"This CompositeMap is not a model element!");
		CompositeMap relationsCM = model.getChild(relations);
		if (relationsCM == null) {
			DialogUtil.showErrorMessageBox("relations is null !");
			return;
		}
		List childList = relationsCM.getChildsNotNull();
		if (childList.size() == 0) {
			DialogUtil.showErrorMessageBox("relations have no child !");
			return;
		}
		CompositeMap refFields_array = viewer.getInput();
		List existRefFields = getExistRefFields(refFields_array);
		if (existRefFields == null)
			existRefFields = new ArrayList();
		for (Iterator it = childList.iterator(); it.hasNext();) {
			CompositeMap relation = (CompositeMap) it.next();
			CompositeMap fields = makeInput(relation, existRefFields);
			if (fields == null)
				continue;
			else {
				gridInput.addChilds(fields.getChildsNotNull());
			}
		}
		CompositeMap selectFileds = null;
		try {
			selectFileds = selectFileds();
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		CompositeMap refFields = createRefFields(selectFileds);
		if (refFields == null) {
			return;
		}
		List<CommentCompositeMap> fields = refFields.getChildsNotNull();
		if (fields.size() == 0)
			return;
		if (model.getChild(ref_fields) == null) {
			model.addChild(refFields_array);
		}

		// refFields_array.addChilds(fields);
		for (CommentCompositeMap m : fields) {
			refFields_array.addChild(m);
		}
		viewer.refresh(true);
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage
				.getString("add.icon"));
	}

	private CompositeMap makeInput(CompositeMap relation, List existRefFields) {
		final String fieldName = "name";
		CompositeMap input = new CommentCompositeMap("input");
		String ref_model = relation.getString(refModel);

		if (ref_model == null)
			return null;
		CompositeMap fields = null;
		try {
			IResource bmFile = BMUtil.getBMResourceFromClassPath(ref_model);
			CompositeMap bmData = AuroraResourceUtil.loadFromResource(bmFile);
			fields = bmData.getChild("fields");
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return null;
		}
		if (fields == null)
			return null;
		List fieldList = fields.getChildsNotNull();
		if (fieldList.size() == 0)
			return null;
		for (Iterator it = fieldList.iterator(); it.hasNext();) {
			CompositeMap field = (CompositeMap) it.next();
			String field_name = field.getString(fieldName);
			if (field_name == null) {
				DialogUtil.showErrorMessageBox(ref_model + "'s "
						+ field.toXML() + " has no 'name' property");
				continue;
			}
			String relationName = relation.getString("name");
			String fieldKey = field_name + specialSeparator + relationName;
			if (existRefFields.contains(fieldKey))
				continue;
			CompositeMap record = new CommentCompositeMap(model.getPrefix(),
					model.getNamespaceURI(), refFieldElement);
			record.put(gridProperties[0], field_name);
			record.put(gridProperties[1], relationName);
			record.put(gridProperties[2], ref_model);
			input.addChild(record);
		}
		return input;
	}

	private CompositeMap selectFileds() throws ApplicationException {
		CompositeMap selectResult = null;
		GridViewer grid = new GridViewer(IGridViewer.isMulti);
		grid.setData(gridInput);
		grid.setColumnNames(gridProperties);
		GridDialog dialog = new GridDialog(new Shell(), grid);
		if (dialog.open() == Window.OK) {
			selectResult = dialog.getSelected();
		}
		return selectResult;
	}

	private CompositeMap createRefFields(CompositeMap selectResult) {
		CompositeMap refFields = new CommentCompositeMap("refFileds");
		if (selectResult == null) {
			return null;
		}
		for (Iterator it = selectResult.getChildsNotNull().iterator(); it
				.hasNext();) {
			CompositeMap record = new CommentCompositeMap(
					(CompositeMap) it.next());
			record.setName(refFieldElement);
			record.setPrefix(model.getPrefix());
			record.setNameSpaceURI(model.getNamespaceURI());
			record.put(refFieldProperties[0], record.getString("name"));
			record.remove(gridProperties[2]);
			refFields.addChild(record);
		}
		return refFields;

	}

	private List getExistRefFields(CompositeMap refFields) {
		if (refFields == null) {
			return null;
		}
		List existRefFields = new ArrayList();
		List childs = refFields.getChildsNotNull();
		if (childs.size() == 0) {
			return null;
		}
		for (Iterator it = childs.iterator(); it.hasNext();) {
			CompositeMap child = (CompositeMap) it.next();
			String fieldKey = getFieldKey(child);
			if (fieldKey != null)
				existRefFields.add(fieldKey);
		}
		return existRefFields;

	}

	private String getFieldKey(CompositeMap field) {
		if (field == null) {
			return null;
		}
		String sourceField = field.getString(refFieldProperties[0]);
		String relationName = field.getString(refFieldProperties[2]);
		if (sourceField == null || relationName == null)
			return null;
		String fieldKey = sourceField + specialSeparator + relationName;
		return fieldKey;
	}

	@Override
	public Image getDefaultImage() {
		return ImagesUtils.getImage("add.gif");
	}
}
