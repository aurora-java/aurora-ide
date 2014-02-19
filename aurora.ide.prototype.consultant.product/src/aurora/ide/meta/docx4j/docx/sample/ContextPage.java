package aurora.ide.meta.docx4j.docx.sample;

import java.io.File;
import java.io.IOException;

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
import org.docx4j.wml.PPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;

public class ContextPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;

	public ContextPage(FSDDocumentPackage doc) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		mdp.addStyledParagraphOfText("3", Messages.ContextPage_1); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$

		mdp.addParagraphOfText(Messages.ContextPage_3);

		try {
			File file = new File("/Users/shiliyan/Desktop/Uip_pages/Page1.png"); //$NON-NLS-1$
			java.io.InputStream is = new java.io.FileInputStream(file);
			mdp.getContent().add(
					newImage(wordMLPackage, mdp,
							BufferUtil.getBytesFromInputStream(is),
							"hand-china", "hand-china", 1, 2)); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Tbl createTbl = createTbl("aurora/ide/meta/docx4j/docx/sample/record_table.xml"); //$NON-NLS-1$
		createTbl.getContent().add(createTr());
		mdp.getContent().add(createTbl);
	}

	private Tr createTr() {

		
		return null;
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
