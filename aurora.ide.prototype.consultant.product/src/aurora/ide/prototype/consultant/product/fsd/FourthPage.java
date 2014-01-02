package aurora.ide.prototype.consultant.product.fsd;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class FourthPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;
	private FunctionDesc function;

	public FourthPage(FunctionDesc function, FSDDocumentPackage doc) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
		this.function = function;
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		mdp.addStyledParagraphOfText(
				"2", function.getFunName() + Messages.FourthPage_1); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
	}

}
