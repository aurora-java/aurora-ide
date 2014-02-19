package aurora.ide.meta.docx4j.docx.sample;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageDimensions;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsCorePart;
import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
import org.docx4j.openpackaging.parts.JaxbXmlPartXPathAware;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.utils.BufferUtil;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyStyles {
	private static ObjectFactory objectFactory = new ObjectFactory();
	private static ObjectFactory factory = new ObjectFactory();
	private static WordprocessingMLPackage wordMLPackage;

	public static void main(String[] args) throws Exception {

		boolean save = true;

		wordMLPackage = createPackage();
		MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();

		// 1. the Header part
		Relationship relationship = createHeaderPart(wordMLPackage);
		// 2. an entry in SectPr

		createHeaderReference(wordMLPackage, relationship);
		relationship = createFooterPart(wordMLPackage);

		createFooterReference(wordMLPackage, relationship);
		
		

		mdp.addStyledParagraphOfText("TitleBar", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.addStyledParagraphOfText("Title", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.addStyledParagraphOfText("Title", "HAND ENTERPRISE SOLUTIONS"); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.addStyledParagraphOfText("DocTitle2", "<ISP> < ISP_WACP5050>"); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.addStyledParagraphOfText("DocTitle3", Messages.CopyStyles_9); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.addStyledParagraphOfText("Docinfo", Messages.CopyStyles_15); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", Messages.CopyStyles_17); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", Messages.CopyStyles_19); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", Messages.CopyStyles_21); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", Messages.CopyStyles_23); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.addStyledParagraphOfText("DocInfotabletitle", Messages.CopyStyles_27); //$NON-NLS-1$

//		 Tbl table = createTableWithContent();
//		//
//		 addBorders(table);
//		//
//		 wordMLPackage.getMainDocumentPart().addObject(table);
		
		int writableWidthTwips = wordMLPackage.getDocumentModel().getSections()
				.get(0).getPageDimensions().getWritableWidthTwips();
		int cols = 2;
		int cellWidthTwips = new Double(Math.floor((writableWidthTwips / cols)))
				.intValue();

		Tbl tbl = TblFactory.createTable(3, cols, cellWidthTwips);

		// w:tblPr
		String strTblPr = "<w:tblPr " + Namespaces.W_NAMESPACE_DECLARATION //$NON-NLS-1$
				+ ">" + "<w:tblStyle w:val=\"TableGrid\"/>" //$NON-NLS-1$ //$NON-NLS-2$
				+ "<w:tblW w:w=\"0\" w:type=\"auto\"/>" //$NON-NLS-1$
				+ "<w:tblInd w:w=\"250\" w:type=\"dxa\" />"  //$NON-NLS-1$
				+ "<w:tblLook w:val=\"04A0\" w:firstRow=\"1\" w:lastRow=\"0\" w:firstColumn=\"1\" w:lastColumn=\"0\" w:noHBand=\"0\" w:noVBand=\"1\" />"+ //$NON-NLS-1$
				"</w:tblPr>"; //$NON-NLS-1$
		TblPr tblPr = null;
		try {
			tblPr = (TblPr) XmlUtils.unmarshalString(strTblPr);
		} catch (JAXBException e) {
			// Shouldn't happen
			e.printStackTrace();
		}
		tbl.setTblPr(tblPr);

		createTc(tbl, 0, 0, Messages.CopyStyles_35);
		createTc(tbl, 0, 1, ""); //$NON-NLS-1$
		createTc(tbl, 1, 0, Messages.CopyStyles_37);
		createTc(tbl, 1, 1, ""); //$NON-NLS-1$
		createTc(tbl, 2, 0, Messages.CopyStyles_39);
		createTc(tbl, 2, 1, ""); //$NON-NLS-1$
		mdp.addObject(tbl);

		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$

		createNewPage(mdp);

		System.out.println(XmlUtils.marshaltoString(mdp.getJaxbElement(), true,
				true));

		// Optionally save it'
		if (save) {
			String filename = "/Users/shiliyan/Desktop" //$NON-NLS-1$
					+ "/OUT_CopyStyles.docx"; //$NON-NLS-1$
			wordMLPackage.save(new java.io.File(filename));
			System.out.println("Saved " + filename); //$NON-NLS-1$
		}
	}

	static void createTc(Tbl tbl, int row, int cols, String content) {
		Tc tc = (Tc) (((Tr) (tbl.getContent().get(row))).getContent()
				.get((cols)));
		tc.getContent().clear();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("DocInfoTable", content)); //$NON-NLS-1$
	}

	private static void addBorders(Tbl table) {
		table.setTblPr(new TblPr());
		CTBorder border = new CTBorder();
		border.setColor("auto"); //$NON-NLS-1$
		border.setSz(new BigInteger("4")); //$NON-NLS-1$
		border.setSpace(new BigInteger("0")); //$NON-NLS-1$
		border.setVal(STBorder.SINGLE);

		TblBorders borders = new TblBorders();
		borders.setBottom(border);
		borders.setLeft(border);
		borders.setRight(border);
		borders.setTop(border);
		borders.setInsideH(border);
		borders.setInsideV(border);
		table.getTblPr().setTblBorders(borders);
	}

	private static Tbl createTableWithContent() {
		Tbl table = factory.createTbl();
		Tr tableRow = factory.createTr();
		addTableCell(tableRow, Messages.CopyStyles_50);
		addTableCell(tableRow, ""); //$NON-NLS-1$
		table.getContent().add(tableRow);

		tableRow = factory.createTr();
		addTableCell(tableRow, Messages.CopyStyles_52);
		addTableCell(tableRow, ""); //$NON-NLS-1$
		table.getContent().add(tableRow);

		tableRow = factory.createTr();
		addTableCell(tableRow, Messages.CopyStyles_54);
		addTableCell(tableRow, ""); //$NON-NLS-1$
		table.getContent().add(tableRow);

		return table;
	}

	private static void addTableCell(Tr tableRow, String content) {
		Tc tableCell = factory.createTc();
		tableCell.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("DocInfoTable", content)); //$NON-NLS-1$
		tableRow.getContent().add(tableCell);
	}

	public static P createNewPage(MainDocumentPart mdp) {
		org.docx4j.wml.P p = objectFactory.createP();

		org.docx4j.wml.R run = objectFactory.createR();

		// org.docx4j.wml.Text t = objectFactory.createText();
		// t.setValue("");
		// run.getContent().add(t);

		Br br = objectFactory.createBr();
		br.setType(STBrType.PAGE);
		run.getContent().add(br);

		// run.getContent().add(objectFactory.createRLastRenderedPageBreak());
		p.getContent().add(run);
		mdp.getContent().add(p);

		return p;
	}

	public static Relationship createFooterPart(
			WordprocessingMLPackage wordprocessingMLPackage) throws Exception {

		FooterPart headerPart = new FooterPart();
		Relationship rel = wordprocessingMLPackage.getMainDocumentPart()
				.addTargetPart(headerPart);

		// After addTargetPart, so image can be added properly
		headerPart.setJaxbElement(getFtr(wordprocessingMLPackage, headerPart));

		return rel;
	}

	private static Ftr getFtr(WordprocessingMLPackage wordPackage,
			Part sourcePart) throws Exception {
		org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
		Ftr ftr = factory.createFtr();
		// ftr.getContent().add(getPImage(wordPackage,sourcePart, bt,
		// "filename", "alttext", 1, 2));

		org.docx4j.wml.P p = factory.createP();

		org.docx4j.wml.Text t = factory.createText();
		t.setValue("Company Confidential - For internal use only"); //$NON-NLS-1$

		org.docx4j.wml.R run = factory.createR();
		run.getContent().add(t);

		p.getContent().add(run);

		PPr pPr = p.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new org.docx4j.wml.Jc();
		}
		// 页脚条形码所处位置
		jc.setVal(JcEnumeration.CENTER);
		pPr.setJc(jc);
		p.setPPr(pPr);

		ftr.getContent().add(p);

		return ftr;
	}

	public static void createFooterReference(
			WordprocessingMLPackage wordprocessingMLPackage,
			Relationship relationship) throws InvalidFormatException {

		List<SectionWrapper> sections = wordprocessingMLPackage
				.getDocumentModel().getSections();

		SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectPr == null) {
			sectPr = objectFactory.createSectPr();
			wordprocessingMLPackage.getMainDocumentPart().addObject(sectPr);
			sections.get(sections.size() - 1).setSectPr(sectPr);
		}

		FooterReference headerReference = objectFactory.createFooterReference();
		headerReference.setId(relationship.getId());
		headerReference.setType(HdrFtrRef.DEFAULT);
		sectPr.getEGHdrFtrReferences().add(headerReference);// add header or
		// footer references

	}

	public static Relationship createHeaderPart(
			WordprocessingMLPackage wordprocessingMLPackage) throws Exception {

		HeaderPart headerPart = new HeaderPart();
		Relationship rel = wordprocessingMLPackage.getMainDocumentPart()
				.addTargetPart(headerPart);

		// After addTargetPart, so image can be added properly
		headerPart.setJaxbElement(getHdr(wordprocessingMLPackage, headerPart));

		return rel;
	}

	public static void createHeaderReference(
			WordprocessingMLPackage wordprocessingMLPackage,
			Relationship relationship) throws InvalidFormatException {

		List<SectionWrapper> sections = wordprocessingMLPackage
				.getDocumentModel().getSections();

		SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectPr == null) {
			sectPr = objectFactory.createSectPr();
			wordprocessingMLPackage.getMainDocumentPart().addObject(sectPr);
			sections.get(sections.size() - 1).setSectPr(sectPr);
		}

		HeaderReference headerReference = objectFactory.createHeaderReference();
		headerReference.setId(relationship.getId());
		headerReference.setType(HdrFtrRef.DEFAULT);
		sectPr.getEGHdrFtrReferences().add(headerReference);// add header or
		// footer references

	}

	public static Hdr getHdr(WordprocessingMLPackage wordprocessingMLPackage,
			Part sourcePart) throws Exception {

		Hdr hdr = objectFactory.createHdr();

		File file = new File(System.getProperty("user.dir") //$NON-NLS-1$
				+ "/src/test/resources/images/hand-china.png"); //$NON-NLS-1$
		java.io.InputStream is = new java.io.FileInputStream(file);

		hdr.getContent().add(
				newImage(wordprocessingMLPackage, sourcePart,
						BufferUtil.getBytesFromInputStream(is), "hand-china", //$NON-NLS-1$
						"hand-china", 1, 2)); //$NON-NLS-1$
		return hdr;

	}

	// public static P getP() {
	// P headerP = objectFactory.createP();
	// R run1 = objectFactory.createR();
	// Text text = objectFactory.createText();
	// text.setValue("123head123");
	// run1.getRunContent().add(text);
	// headerP.getParagraphContent().add(run1);
	// return headerP;
	// }

	public static org.docx4j.wml.P newImage(
			WordprocessingMLPackage wordMLPackage, Part sourcePart,
			byte[] bytes, String filenameHint, String altText, int id1, int id2)
			throws Exception {

		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage
				.createImagePart(wordMLPackage, sourcePart, bytes);

		Inline inline = imagePart.createImageInline(filenameHint, altText, id1,
				id2, (long) 2000, false);

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
		// 页脚条形码所处位置
		jc.setVal(JcEnumeration.RIGHT);
		pPr.setJc(jc);
		p.setPPr(pPr);

		// p.setPPr(objectFactory.createPPr());
		// p.getPPr().setPStyle(objectFactory.createPPrBasePStyle());
		// p.getPPr().getPStyle().setVal("a5");

		return p;

	}

	protected static Logger log = LoggerFactory
			.getLogger(WordprocessingMLPackage.class);

	public static WordprocessingMLPackage createPackage()
			throws InvalidFormatException {

		String papersize = Docx4jProperties.getProperties().getProperty(
				"docx4j.PageSize", "A4"); //$NON-NLS-1$ //$NON-NLS-2$
		log.info("Using paper size: " + papersize); //$NON-NLS-1$

		String landscapeString = Docx4jProperties.getProperties().getProperty(
				"docx4j.PageOrientationLandscape", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		boolean landscape = Boolean.parseBoolean(landscapeString);
		log.info("Landscape orientation: " + landscape); //$NON-NLS-1$

		return createPackage(PageSizePaper.valueOf(papersize), landscape);
	}

	@SuppressWarnings("rawtypes")
	public static WordprocessingMLPackage createPackage(PageSizePaper sz,
			boolean landscape) throws InvalidFormatException {

		// Create a package
		WordprocessingMLPackage wmlPack = new WordprocessingMLPackage();

		// Create main document part
		MainDocumentPart wordDocumentPart = new MainDocumentPart();

		// Create main document part content
		org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		org.docx4j.wml.Body body = factory.createBody();
		org.docx4j.wml.Document wmlDocumentEl = factory.createDocument();

		wmlDocumentEl.setBody(body);

		// Create a basic sectPr using our Page model
		PageDimensions page = new PageDimensions();
		page.setPgSize(sz, landscape);

		SectPr sectPr = factory.createSectPr();
		body.setSectPr(sectPr);
		sectPr.setPgSz(page.getPgSz());
		sectPr.setPgMar(page.getPgMar());

		// Put the content in the part
		wordDocumentPart.setJaxbElement(wmlDocumentEl);

		// Add the main document part to the package relationships
		// (creating it if necessary)
		wmlPack.addTargetPart(wordDocumentPart);

		// Create a styles part
		Part stylesPart = new org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart();
		try {
			// ((org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart)
			// stylesPart)
			// .unmarshalDefaultStyles();
			unmarshalDefaultStyles((StyleDefinitionsPart) stylesPart,
					"aurora/ide/meta/docx4j/docx/sample/styles.xml"); //$NON-NLS-1$
			// Add the styles part to the main document part relationships
			// (creating it if necessary)
			wordDocumentPart.addTargetPart(stylesPart); // NB - add it to main

		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		try {
			Part styleEffectsPart = new JaxbXmlPartXPathAware(new PartName(
					"/word/stylesWithEffects.xml")) { //$NON-NLS-1$

			};
			styleEffectsPart
					.setContentType(new org.docx4j.openpackaging.contenttype.ContentType(
							"application/vnd.ms-word.stylesWithEffects+xml")); //$NON-NLS-1$

			// Used when this Part is added to a rels
			styleEffectsPart
					.setRelationshipType("http://schemas.microsoft.com/office/2007/relationships/stylesWithEffects"); //$NON-NLS-1$
			// ((org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart)
			// stylesPart)
			// .unmarshalDefaultStyles();
			unmarshalDefaultStyles((JaxbXmlPartXPathAware) styleEffectsPart,
					"aurora/ide/meta/docx4j/docx/sample/stylesWithEffects.xml"); //$NON-NLS-1$

			// Add the styles part to the main document part relationships
			// (creating it if necessary)
			wordDocumentPart.addTargetPart(styleEffectsPart); // NB - add it to
																// main

		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		// Metadata: docx4j 2.7.1 can populate some of this from
		// docx4j.properties
		// See SaveToZipFile
		DocPropsCorePart core = new DocPropsCorePart();
		org.docx4j.docProps.core.ObjectFactory coreFactory = new org.docx4j.docProps.core.ObjectFactory();
		core.setJaxbElement(coreFactory.createCoreProperties());
		wmlPack.addTargetPart(core);

		DocPropsExtendedPart app = new DocPropsExtendedPart();
		org.docx4j.docProps.extended.ObjectFactory extFactory = new org.docx4j.docProps.extended.ObjectFactory();
		app.setJaxbElement(extFactory.createProperties());
		wmlPack.addTargetPart(app);

		// Return the new package
		return wmlPack;

	}

	public static Object unmarshalDefaultStyles(
			StyleDefinitionsPart stylesPart, String path) throws JAXBException {

		// Throwable t = new Throwable();
		// t.printStackTrace();

		java.io.InputStream is = null;
		try {
			// Works in Eclipse if the resource is in source/main/java
			// is = getResource("styles.xml");

			// Works in Eclipse - not absence of leading '/'
			is = org.docx4j.utils.ResourceUtils.getResource(path);

			// styles.xml defines a small subset of common styles
			// (it is a much smaller set of styles than KnownStyles.xml)

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stylesPart.unmarshal(is); // side-effect is to set jaxbElement
	}

	public static Object unmarshalDefaultStyles(
			JaxbXmlPartXPathAware stylesPart, String path) throws JAXBException {

		// Throwable t = new Throwable();
		// t.printStackTrace();

		java.io.InputStream is = null;
		try {
			// Works in Eclipse if the resource is in source/main/java
			// is = getResource("styles.xml");

			// Works in Eclipse - not absence of leading '/'
			is = org.docx4j.utils.ResourceUtils.getResource(path);

			// styles.xml defines a small subset of common styles
			// (it is a much smaller set of styles than KnownStyles.xml)

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stylesPart.unmarshal(is); // side-effect is to set jaxbElement
	}
}
