package aurora.ide.prototype.consultant.product.fsd;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.Body;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Text;

public class ContextPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;

	public ContextPage(FSDDocumentPackage doc) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		
		mdp.addStyledParagraphOfText("tocheading", Messages.ContextPage_1); //$NON-NLS-1$
//		tocheading
		
		
		ObjectFactory factory = Context.getWmlObjectFactory();
		P paragraphForTOC = factory.createP();
		R r = factory.createR();

		FldChar fldchar = factory.createFldChar();
		fldchar.setFldCharType(STFldCharType.BEGIN);
		fldchar.setDirty(true);
		r.getContent().add(getWrappedFldChar(fldchar));
		paragraphForTOC.getContent().add(r);

		R r1 = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve"); //$NON-NLS-1$
		txt.setValue(" TOC \\o \"1-3\" \\h \\z \\u "); //$NON-NLS-1$
		r.getContent().add(factory.createRInstrText(txt));
		paragraphForTOC.getContent().add(r1);
		

//		R rx = factory.createR();
//		Text txtx = new Text();
//		txt.setSpace("preserve");
//		txt.setValue("HYPERLINK");
//		r.getContent().add(factory.createRInstrText(txtx));
//		paragraphForTOC.getContent().add(rx);

//		<w:instrText xml:space="preserve"> HYPERLINK \l _Toc21048 </w:instrText>
		
		
		FldChar fldcharend = factory.createFldChar();
		fldcharend.setFldCharType(STFldCharType.END);
		R r2 = factory.createR();
		r2.getContent().add(getWrappedFldChar(fldcharend));
		paragraphForTOC.getContent().add(r2);
		
		org.docx4j.wml.Document wmlDocumentEl = (org.docx4j.wml.Document)mdp.getJaxbElement();
		Body body =  wmlDocumentEl.getBody();
		body.getContent().add(paragraphForTOC);

	}

	public static JAXBElement getWrappedFldChar(FldChar fldchar) {

		return new JAXBElement(new QName(Namespaces.NS_WORD12, "fldChar"), //$NON-NLS-1$
				FldChar.class, fldchar);

	}
}
