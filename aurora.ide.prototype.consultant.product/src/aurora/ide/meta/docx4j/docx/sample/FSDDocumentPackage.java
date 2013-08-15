package aurora.ide.meta.docx4j.docx.sample;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.docx4j.Docx4jProperties;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageDimensions;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.model.structure.SectionWrapper;
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
import org.docx4j.relationships.Relationship;
import org.docx4j.utils.BufferUtil;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.PPr;
import org.docx4j.wml.SectPr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSDDocumentPackage {
	private ObjectFactory objectFactory = new ObjectFactory();
	private WordprocessingMLPackage wordMLPackage;
	protected static Logger log = LoggerFactory
			.getLogger(WordprocessingMLPackage.class);

	public FSDDocumentPackage(){
	}
	
	public void create() throws Exception {
		wordMLPackage = createPackage();
		Relationship relationship = createHeaderPart(getWordMLPackage());
		createHeaderReference(getWordMLPackage(), relationship);
		relationship = createFooterPart(getWordMLPackage());
		createFooterReference(getWordMLPackage(), relationship);
	}



	protected WordprocessingMLPackage createPackage()
			throws InvalidFormatException {

		String papersize = Docx4jProperties.getProperties().getProperty(
				"docx4j.PageSize", "A4");
		log.info("Using paper size: " + papersize);

		String landscapeString = Docx4jProperties.getProperties().getProperty(
				"docx4j.PageOrientationLandscape", "false");
		boolean landscape = Boolean.parseBoolean(landscapeString);
		log.info("Landscape orientation: " + landscape);

		return createPackage(PageSizePaper.valueOf(papersize), landscape);
	}

	@SuppressWarnings("rawtypes")
	protected WordprocessingMLPackage createPackage(PageSizePaper sz,
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
					"aurora/ide/meta/docx4j/docx/sample/styles.xml");
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
					"/word/stylesWithEffects.xml")) {

			};
			styleEffectsPart
					.setContentType(new org.docx4j.openpackaging.contenttype.ContentType(
							"application/vnd.ms-word.stylesWithEffects+xml"));

			// Used when this Part is added to a rels
			styleEffectsPart
					.setRelationshipType("http://schemas.microsoft.com/office/2007/relationships/stylesWithEffects");
			// ((org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart)
			// stylesPart)
			// .unmarshalDefaultStyles();
			unmarshalDefaultStyles((JaxbXmlPartXPathAware) styleEffectsPart,
					"aurora/ide/meta/docx4j/docx/sample/stylesWithEffects.xml");

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

	protected Object unmarshalDefaultStyles(StyleDefinitionsPart stylesPart,
			String path) throws JAXBException {

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

	protected Object unmarshalDefaultStyles(JaxbXmlPartXPathAware stylesPart,
			String path) throws JAXBException {

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

	protected Relationship createFooterPart(
			WordprocessingMLPackage wordprocessingMLPackage) throws Exception {

		FooterPart headerPart = new FooterPart();
		Relationship rel = wordprocessingMLPackage.getMainDocumentPart()
				.addTargetPart(headerPart);

		// After addTargetPart, so image can be added properly
		headerPart.setJaxbElement(getFtr(wordprocessingMLPackage, headerPart));

		return rel;
	}

	private Ftr getFtr(WordprocessingMLPackage wordPackage, Part sourcePart)
			throws Exception {
		org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
		Ftr ftr = factory.createFtr();
		// ftr.getContent().add(getPImage(wordPackage,sourcePart, bt,
		// "filename", "alttext", 1, 2));

		org.docx4j.wml.P p = factory.createP();

		org.docx4j.wml.Text t = factory.createText();
		t.setValue("Company Confidential - For internal use only");

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

	protected void createFooterReference(
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

	protected Relationship createHeaderPart(
			WordprocessingMLPackage wordprocessingMLPackage) throws Exception {

		HeaderPart headerPart = new HeaderPart();
		Relationship rel = wordprocessingMLPackage.getMainDocumentPart()
				.addTargetPart(headerPart);

		// After addTargetPart, so image can be added properly
		headerPart.setJaxbElement(getHdr(wordprocessingMLPackage, headerPart));

		return rel;
	}

	protected void createHeaderReference(
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

	protected Hdr getHdr(WordprocessingMLPackage wordprocessingMLPackage,
			Part sourcePart) throws Exception {

		Hdr hdr = objectFactory.createHdr();

		File file = new File(System.getProperty("user.dir")
				+ "/src/test/resources/images/hand-china.png");
		java.io.InputStream is = new java.io.FileInputStream(file);

		hdr.getContent().add(
				newImage(wordprocessingMLPackage, sourcePart,
						BufferUtil.getBytesFromInputStream(is), "hand-china",
						"hand-china", 1, 2));
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

	protected org.docx4j.wml.P newImage(WordprocessingMLPackage wordMLPackage,
			Part sourcePart, byte[] bytes, String filenameHint, String altText,
			int id1, int id2) throws Exception {

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
		jc.setVal(JcEnumeration.RIGHT);
		pPr.setJc(jc);
		p.setPPr(pPr);

		// p.setPPr(objectFactory.createPPr());
		// p.getPPr().setPStyle(objectFactory.createPPrBasePStyle());
		// p.getPPr().getPStyle().setVal("a5");

		return p;

	}

	public WordprocessingMLPackage getWordMLPackage() {
		return wordMLPackage;
	}


	public MainDocumentPart getMainDocumentPart(){
		return wordMLPackage.getMainDocumentPart();
	}
}
