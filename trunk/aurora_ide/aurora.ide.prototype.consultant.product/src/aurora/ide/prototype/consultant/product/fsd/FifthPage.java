package aurora.ide.prototype.consultant.product.fsd;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class FifthPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;
	private FunctionDesc function;

	public FifthPage(FSDDocumentPackage doc, FunctionDesc function) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
		this.function = function;
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		mdp.addStyledParagraphOfText("2", function.getFunName()); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.FifthPage_2); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.FifthPage_5); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.FifthPage_8); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.FifthPage_11); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
		
	}

}
