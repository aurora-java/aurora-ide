package aurora.ide.prototype.consultant.product.fsd;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class FourthPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;

	public FourthPage(FSDDocumentPackage doc) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		mdp.addStyledParagraphOfText("2", "功能概述");
		mdp.addParagraphOfText("");
	}

}
