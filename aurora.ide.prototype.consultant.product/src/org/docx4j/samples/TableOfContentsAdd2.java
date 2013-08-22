package org.docx4j.samples;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.Body;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTSettings;
import org.docx4j.wml.CTView;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STView;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Text;

public class TableOfContentsAdd2 {

	public static void main(String[] args) throws Exception {

		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
				.createPackage();
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
		CTSettings ct = new CTSettings();
		DocumentSettingsPart dsp = documentPart.getDocumentSettingsPart();
		if (dsp == null) {
			dsp = new DocumentSettingsPart();
			CTView ctView = Context.getWmlObjectFactory().createCTView();
			ctView.setVal(STView.PRINT);
			ct.setView(ctView);
			BooleanDefaultTrue b = new BooleanDefaultTrue();
			b.setVal(true);
			ct.setUpdateFields(b);
			dsp.setJaxbElement(ct);
			documentPart.addTargetPart(dsp);
		}

		org.docx4j.wml.Document wmlDocumentEl = (org.docx4j.wml.Document) documentPart
				.getJaxbElement();
		Body body = wmlDocumentEl.getBody();

		ObjectFactory factory = Context.getWmlObjectFactory();

		/*
		 * Create the following:
		 * 
		 * <w:p> <w:r> <w:fldChar w:dirty="true" w:fldCharType="begin"/>
		 * <w:instrText xml:space="preserve">TOC \o &quot;1-3&quot; \h \z \ u
		 * \h</w:instrText> </w:r> <w:r/> <w:r> <w:fldChar w:fldCharType="end"/>
		 * </w:r> </w:p>
		 */
		P paragraphForTOC = factory.createP();
		R r = factory.createR();

		FldChar fldchar = factory.createFldChar();
		fldchar.setFldCharType(STFldCharType.BEGIN);
		fldchar.setDirty(true);
		r.getContent().add(getWrappedFldChar(fldchar));
		paragraphForTOC.getContent().add(r);

		R r1 = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue("TOC \\o \"1-3\" \\h \\z \\u ");
		r.getContent().add(factory.createRInstrText(txt));
		paragraphForTOC.getContent().add(r1);

		FldChar fldcharend = factory.createFldChar();
		fldcharend.setFldCharType(STFldCharType.END);
		R r2 = factory.createR();
		r2.getContent().add(getWrappedFldChar(fldcharend));
		paragraphForTOC.getContent().add(r2);

		body.getContent().add(paragraphForTOC);

		documentPart.addStyledParagraphOfText("Heading1", "Hello 1");
		documentPart.addStyledParagraphOfText("Heading2", "Hello 2");
		documentPart.addStyledParagraphOfText("Heading3", "Hello 3");
		documentPart.addStyledParagraphOfText("Heading1", "Hello 1");

		wordMLPackage.save(new java.io.File(System.getProperty("user.dir")
				+ "/OUT_TableOfContentsAdd.docx"));

	}

	public static JAXBElement getWrappedFldChar(FldChar fldchar) {

		return new JAXBElement(new QName(Namespaces.NS_WORD12, "fldChar"),
				FldChar.class, fldchar);

	}

}
