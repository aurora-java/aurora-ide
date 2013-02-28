package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.link.Parameter;

public class ButtonHandler extends DefaultIOHandler {
	public static final String COMMENT_TARGET = "target";

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		Button b = (Button) ac;
		map.put(Button.BUTTON_TYPE, b.getButtonType());
		if (!b.isOnToolBar()) {
			map.put(Button.WIDTH, b.getSize().width);
			map.put(Button.HEIGHT, b.getSize().height);
		}
		if (b.getButtonType().equals(Button.DEFAULT)) {
			map.put(Button.BUTTON_TEXT, b.getText());
		}
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		Button b = (Button) ac;
		if (!b.isOnToolBar()) {
			ButtonClicker bc = b.getButtonClicker();
			if (bc == null)
				return;
			CompositeMap bcMap = new CommentCompositeMap(
					ButtonClicker.class.getSimpleName());
			String aid = bc.getActionID();
			bcMap.put("id", bc.getActionID());
			bcMap.put(MARKID, bc.markid);
			if (ButtonClicker.B_SEARCH.equals(aid)
					|| ButtonClicker.B_SAVE.equals(aid)
					|| ButtonClicker.B_RESET.equals(aid)) {
				AuroraComponent a = bc.getTargetComponent();
				if (a != null) {
					bcMap.put(COMMENT_TARGET, a.markid);
				}
			} else if (ButtonClicker.B_OPEN.equals(aid)) {
				bcMap.put("openPath", bc.getOpenPath());
			} else if (ButtonClicker.B_CLOSE.equals(aid)) {
				bcMap.put("closeWindowID", bc.getCloseWindowID());
			} else if (ButtonClicker.B_CUSTOM.equals(aid)) {
				CompositeMap fMap = new CommentCompositeMap("function");
				fMap.setText(bc.getFunction());
				bcMap.addChild(fMap);
			}
			map.addChild(bcMap);
			map.addChild(getParameterMap(bc, mic));
		}
	}

	private CompositeMap getParameterMap(ButtonClicker clicker,
			ModelIOContext mic) {
		CompositeMap pMap = new CommentCompositeMap(RendererHandler.PARAMETERS);
		ParameterHandler ph = new ParameterHandler();
		for (Parameter p : clicker.getParameters()) {
			pMap.addChild(ph.toCompositeMap(p, mic));
		}
		return pMap;
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		Button b = (Button) ac;
		b.setButtonType(map.getString(Button.BUTTON_TYPE));
		if (!map.getParent().getName()
				.equalsIgnoreCase(Toolbar.class.getSimpleName())) {
			b.setSize(new Dimension(map.getInt(Button.WIDTH), map
					.getInt(Button.HEIGHT)));
		}
		if (b.getButtonType().equals(Button.DEFAULT)) {
			String text = map.getString(Button.BUTTON_TEXT);
			if (text != null)
				b.setText(text);
		}
	}

	@Override
	public Button fromCompositeMap(CompositeMap map, ModelIOContext mic) {
		return (Button) super.fromCompositeMap(map, mic);
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		Button b = (Button) ac;
		if (!map.getParent().getName()
				.equalsIgnoreCase(Toolbar.class.getSimpleName())) {
			ButtonClicker bc = b.getButtonClicker();
			CompositeMap bcMap = map.getChild(ButtonClicker.class
					.getSimpleName());
			if (bc == null || bcMap == null)
				return;
			String aid = bcMap.getString("id");
			bc.setActionID(aid);
			String markid = bcMap.getString(MARKID);
			if (markid != null)
				bc.markid = markid;
			if (ButtonClicker.B_SEARCH.equals(aid)
					|| ButtonClicker.B_SAVE.equals(aid)
					|| ButtonClicker.B_RESET.equals(aid)) {
				markid = bcMap.getString(COMMENT_TARGET);
				if (markid != null) {
					AuroraComponent a = mic.markMap.get(markid);
					if (a != null) {
						bc.setTargetComponent(a);
					} else {
						ReferenceDecl rd = new ReferenceDecl();
						rd.markid = markid;
						rd.methodOwner = bc;
						rd.methodName = "setTargetComponent";
						rd.argType = AuroraComponent.class;
						mic.refDeclList.add(rd);
					}
				}
			} else if (ButtonClicker.B_OPEN.equals(aid)) {
				bc.setOpenPath(bcMap.getString("openPath"));
			} else if (ButtonClicker.B_CLOSE.equals(aid)) {
				bc.setCloseWindowID(bcMap.getString("closeWindowID"));
			} else if (ButtonClicker.B_CUSTOM.equals(aid)) {
				CompositeMap fMap = bcMap.getChild("function");
				if (fMap != null) {
					bc.setFunction(fMap.getText());
				}
			}
			restoreParameters(bc, map, mic);
		}
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new Button();
	}

	private void restoreParameters(ButtonClicker clicker, CompositeMap rMap,
			ModelIOContext mic) {
		CompositeMap psMap = rMap.getChild(RendererHandler.PARAMETERS);
		if (psMap == null)
			return;
		ParameterHandler ph = new ParameterHandler();
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = psMap.getChildsNotNull();
		for (CompositeMap m : list) {
			Parameter p = (Parameter) ph.fromCompositeMap(m, mic);
			clicker.addParameter(p);
		}
	}

}
