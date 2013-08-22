package aurora.ide.prototype.consultant.product.fsd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.utils.BufferUtil;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.PPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.palette.DefaultPaletteViewerPreferences;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.parts.ExtAuroraPartFactory;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.ide.meta.gef.util.MessageUtil;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.IDatasetFieldDelegate;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.TabFolder;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;
import aurora.plugin.source.gen.screen.model.properties.ComponentFSDProperties;

public class ContentPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;
	private ObjectFactory objectFactory = new ObjectFactory();
	private List<String> files;

	public ContentPage(FSDDocumentPackage doc, List<String> files) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
		this.files = files;
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		mdp.addStyledParagraphOfText("3", "界面设计");
		mdp.addParagraphOfText("");

		for (String file : files) {
			createContent(file);
		}
		mdp.addStyledParagraphOfText("3", "特殊逻辑");
		mdp.addParagraphOfText("");
		mdp.addParagraphOfText("");
		mdp.addStyledParagraphOfText("3", "系统Message");
		mdp.addParagraphOfText("");
		mdp.addParagraphOfText("");
		mdp.getContent()
				.add(createTbl("aurora/ide/meta/docx4j/docx/sample/sys_msg_table.xml"));

		mdp.addStyledParagraphOfText("3", "附件");
		mdp.addParagraphOfText("");
		mdp.addParagraphOfText("");
	}

	private void createContent(String file) {

		ScreenBody screenBody = loadFile(file);

		if (hasTab(screenBody))
			return;

		MainDocumentPart mdp = doc.getMainDocumentPart();

		mdp.addStyledParagraphOfText("contentPageTitle", screenBody
				.getStringPropertyValue(ComponentFSDProperties.FSD_PAGE_NAME));
		mdp.addParagraphOfText("");

		try {
			mdp.getContent()
					.add(newImage(
							wordMLPackage,
							mdp,
							BufferUtil
									.getBytesFromInputStream(createImageInputStream(screenBody)),
							"hand-china", "hand-china", 1, 2));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mdp.addParagraphOfText(screenBody
				.getStringPropertyValue(ComponentFSDProperties.FSD_PAGE_DESC));

		List<AuroraComponent> children = screenBody.getChildren();

		List<AuroraComponent> childs = getNoContainerChildren(children);

		createContentTblInfo(childs);

		List<Container> containers = getContainerChildren(children);

		for (Container container : containers) {
			List<AuroraComponent> cs = getAllChildren(container);
			if (cs.size() == 0)
				continue;
			mdp.addStyledParagraphOfText("3", container
					.getStringPropertyValue(ComponentFSDProperties.FSD_DESC));
			createContentTblInfo(cs);
		}
		List<Button> buttons = getButtons(screenBody);
		if (buttons.size() > 0)
			mdp.addStyledParagraphOfText("3", "操作说明:");
		for (int i = 0; i < buttons.size(); i++) {
			Button b = buttons.get(i);
			mdp.addParagraphOfText("" + (i + 1) + ".  "
					+ MessageUtil.getButtonText(b) + " :  "
					+ b.getStringPropertyValue(ComponentFSDProperties.FSD_DESC));
		}
	}

	private List<Button> getButtons(Container container) {
		List<AuroraComponent> children = container.getChildren();
		List<Button> r = new ArrayList<Button>();
		for (AuroraComponent ac : children) {
			if (ac instanceof Button) {
				r.add((Button) ac);
			}
			if (ac instanceof Container) {
				r.addAll(getButtons((Container) ac));
			}
		}

		return r;

	}

	private boolean hasTab(Container container) {
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			if (TabFolder.TAB_PANEL.equals(ac.getComponentType())) {
				return true;
			}
			if (ac instanceof Container && hasTab((Container) ac)) {
				return true;
			}
		}
		return false;
	}

	private List<AuroraComponent> getAllChildren(Container container) {
		List<AuroraComponent> r = new ArrayList<AuroraComponent>();
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container) {
				if (GridColumn.GRIDCOLUMN.equals(ac.getComponentType())
						&& ((Container) ac).getChildren().size() == 0) {
					r.add(ac);
				} else {
					r.addAll(getAllChildren((Container) ac));
				}
			} else if (ac instanceof Input) {
				r.add(ac);
			}
		}

		return r;
	}

	private List<Container> getContainerChildren(List<AuroraComponent> children) {
		List<Container> r = new ArrayList<Container>();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container == true) {
				r.add((Container) ac);
			}
		}
		return r;
	}

	private void createContentTblInfo(List<AuroraComponent> childs) {
		if (childs.size() == 0)
			return;
		MainDocumentPart mdp = doc.getMainDocumentPart();
		Tbl createTbl = createTbl("aurora/ide/meta/docx4j/docx/sample/content_table.xml");
		for (int i = 0; i < childs.size(); i++) {
			createTbl.getContent().add(createTr(childs.get(i), i + 1));
		}
		mdp.getContent().add(createTbl);

		for (int i = 0; i < childs.size(); i++) {
			AuroraComponent auroraComponent = childs.get(i);
			mdp.addStyledParagraphOfText("contentInfoHead", "Note" + (i + 1)
					+ ":" + auroraComponent.getPrompt());
			mdp.addStyledParagraphOfText(
					"contentInfo",
					"含义: "
							+ auroraComponent
									.getStringPropertyValue(ComponentFSDProperties.FSD_MEANING));
			mdp.addStyledParagraphOfText(
					"contentInfo",
					"数据来源: "
							+ auroraComponent
									.getStringPropertyValue(ComponentFSDProperties.FSD_DATA_FROM));
			mdp.addStyledParagraphOfText(
					"contentInfo",
					"逻辑: "
							+ auroraComponent
									.getStringPropertyValue(ComponentFSDProperties.FSD_LOGIC));
		}
	}

	private List<AuroraComponent> getNoContainerChildren(
			List<AuroraComponent> children) {
		List<AuroraComponent> r = new ArrayList<AuroraComponent>();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container == false) {
				if (ac instanceof Input
						|| GridColumn.GRIDCOLUMN.equals(ac.getComponentType()))
					r.add(ac);
			}
		}
		return r;
	}

	private ScreenBody loadFile(String file) {
		ScreenBody diagram = null;
		CompositeMap loadFile = CompositeMapUtil.loadFile(new File(file));
		if (loadFile != null) {
			CompositeMap2Object c2o = new CompositeMap2Object();
			diagram = c2o.createScreenBody(loadFile);
		} else {
			diagram = new ScreenBody();
		}
		return diagram;
	}

	private Tr createTr(AuroraComponent ac, int i) {
		Tr tr = objectFactory.createTr();
		Tc tc = objectFactory.createTc();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("contentTable", "" + i));
		tr.getContent().add(tc);
		tc = objectFactory.createTc();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("contentTable",
								ac.getPrompt()));
		tr.getContent().add(tc);
		tc = objectFactory.createTc();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("contentTable",
								getType(ac)));
		tr.getContent().add(tc);
		tc = objectFactory.createTc();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("contentTable", "30"));

		tr.getContent().add(tc);
		tc = objectFactory.createTc();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("contentTable",
								this.getFormat(ac)));

		tr.getContent().add(tc);
		tc = objectFactory.createTc();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("contentTable",
								this.getMustInput(ac)));

		tr.getContent().add(tc);

		tc = objectFactory.createTc();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("contentTable",
								this.getDefaultValue(ac)));
		tr.getContent().add(tc);
		tc = objectFactory.createTc();
		tc.getContent()
				.add(wordMLPackage
						.getMainDocumentPart()
						.createStyledParagraphOfText("contentTable", "Note" + i));

		tr.getContent().add(tc);
		return tr;
	}

	private String getDefaultValue(AuroraComponent ac) {
		if (ac instanceof IDatasetFieldDelegate) {
			String v = ((IDatasetFieldDelegate) ac).getDatasetField()
					.getDefaultValue();
			if (v == null || "".equals(v))
				return "N/A";
			return ""
					+ ((IDatasetFieldDelegate) ac).getDatasetField()
							.isRequired();
		}
		return "N/A";
	}

	// N/A
	private String getMustInput(AuroraComponent ac) {
		if (ac instanceof IDatasetFieldDelegate) {
			return ("" + ((IDatasetFieldDelegate) ac).getDatasetField()
					.isRequired()).toUpperCase();
		}
		return "false".toUpperCase();
	}

	private String getFormat(AuroraComponent ac) {
		if (ac instanceof Input) {
			return ac.getComponentType().toUpperCase();
		}
		if (GridColumn.GRIDCOLUMN.equals(ac.getComponentType())) {
			GridColumn gc = (GridColumn) ac;
			return gc.getEditor().toUpperCase();
		}
		return "TEXT";
	}

	private String getType(AuroraComponent ac) {
		String type = ac.getComponentType();
		if (Input.DATE_PICKER.equals(type) || Input.DATETIMEPICKER.equals(type)) {
			return "DATE";
		}
		if (Input.NUMBER.equals(type)) {
			return "NUM";
		}
		if (GridColumn.GRIDCOLUMN.equals(type)) {
			GridColumn gc = (GridColumn) ac;
			type = gc.getEditor();
			if (Input.DATE_PICKER.equals(type)
					|| Input.DATETIMEPICKER.equals(type)) {
				return "DATE";
			}
			if (Input.NUMBER.equals(type)) {
				return "NUM";
			}
		}
		return "VC";
	}

	protected void createTc(Tbl tbl, int row, int cols, String content) {
		Tc tc = (Tc) (((Tr) (tbl.getContent().get(row))).getContent()
				.get((cols)));
		tc.getContent().clear();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("DocInfoTable", content));
	}

	protected Tbl createTbl(String path) {
		java.io.InputStream is = null;
		try {
			is = org.docx4j.utils.ResourceUtils.getResource(path);
			Tbl tbl = (Tbl) XmlUtils.unmarshal(is);
			return tbl;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream createImageInputStream(ScreenBody diagram) {

		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		ScalableRootEditPart root = new ScalableRootEditPart();
		viewer.setRootEditPart(root);
		viewer.setEditPartFactory(new ExtAuroraPartFactory(new EditorMode() {
			public String getMode() {
				return None;
			}

			public boolean isForDisplay() {
				return false;
			}

			public boolean isForCreate() {
				return true;
			}

			public boolean isForUpdate() {
				return true;
			}

			public boolean isForSearch() {
				return false;
			}
		}));
		viewer.setContents(diagram);
		Font font = new Font(Display.getCurrent(),
				new DefaultPaletteViewerPreferences().getFontData());
		IFigure figure = root.getFigure();
		figure.setFont(font);
		figure.validate();

		InputStream is = this
				.createImageInputStream((ScalableRootEditPart) viewer
						.getRootEditPart());
		font.dispose();
		return is;
	}

	public InputStream createImageInputStream(ScalableRootEditPart rootEditPart) {

		double zoom = rootEditPart.getZoomManager().getZoom();

		try {
			IFigure figure = getRootFigure(rootEditPart);
			Rectangle rectangle = calBounds(figure);

			Image image = new Image(Display.getDefault(), rectangle.width,
					rectangle.height);
			GC gc = new GC(image);
			SWTGraphics graphics = new SWTGraphics(gc);
			figure.paint(graphics);
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData() };

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			loader.save(baos, SWT.IMAGE_PNG);

			ByteArrayInputStream is = new ByteArrayInputStream(
					baos.toByteArray());
			image.dispose();
			gc.dispose();
			return is;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			rootEditPart.getZoomManager().setZoom(zoom);
		}
		return null;
	}

	public IFigure getRootFigure(ScalableRootEditPart rootEditPart) {
		List children = rootEditPart.getChildren();
		if (children.size() > 0) {
			Object object = children.get(0);
			if (object instanceof ViewDiagramPart) {
				return ((ViewDiagramPart) object).getFigure();
			}
		}
		return rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS);
	}

	public Rectangle calBounds(IFigure figure) {
		Rectangle r = new Rectangle(0, 0, 0, 0);
		List children = figure.getChildren();
		for (Object object : children) {
			if (object instanceof IFigure) {
				Rectangle b = ((IFigure) object).getBounds();
				r.union(b);
			}
		}
		return r.expand(10, 10);
	}

	protected org.docx4j.wml.P newImage(WordprocessingMLPackage wordMLPackage,
			Part sourcePart, byte[] bytes, String filenameHint, String altText,
			int id1, int id2) throws Exception {

		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage
				.createImagePart(wordMLPackage, sourcePart, bytes);

		Inline inline = imagePart.createImageInline(filenameHint, altText, id1,
				id2, false);

		// inline.getCNvGraphicFramePr().getGraphicFrameLocks().setNoChangeAspect(false);
		// Pic pic = inline.getGraphic().getGraphicData().getPic();
		org.docx4j.dml.ObjectFactory dmlFactory = new org.docx4j.dml.ObjectFactory();
		org.pptx4j.pml.ObjectFactory pmlFactory = new org.pptx4j.pml.ObjectFactory();
		//
		// pic.getBlipFill();
		//
		// CTPictureLocking picLocks = dmlFactory.createCTPictureLocking();
		// picLocks.setNoChangeArrowheads(false);
		// picLocks.setNoChangeAspect(false);
		// pic.getNvPicPr().getCNvPicPr().setPicLocks(picLocks);
		//
		// pic.getSpPr().setBwMode(STBlackWhiteMode.AUTO);
		// Now add the inline in w:p/w:r/w:drawing
		org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		org.docx4j.wml.P p = factory.createP();
		org.docx4j.wml.R run = factory.createR();
		p.getContent().add(run);
		org.docx4j.wml.Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);

		PPr pPr = p.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new org.docx4j.wml.Jc();
		}
		jc.setVal(JcEnumeration.RIGHT);
		pPr.setJc(jc);
		p.setPPr(pPr);

		// p.setPPr(objectFactory.createPPr());
		// p.getPPr().setPStyle(objectFactory.createPPrBasePStyle());
		// p.getPPr().getPStyle().setVal("a5");

		return p;
	}
}
