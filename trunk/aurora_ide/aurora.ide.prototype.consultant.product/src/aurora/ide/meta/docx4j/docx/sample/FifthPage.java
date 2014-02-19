package aurora.ide.meta.docx4j.docx.sample;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class FifthPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;

	public FifthPage(FSDDocumentPackage doc) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		mdp.addStyledParagraphOfText("2", Messages.FifthPage_1); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.FifthPage_3); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.FifthPage_6); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.FifthPage_9); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.FifthPage_12); //$NON-NLS-1$
		mdp.addParagraphOfText(""); //$NON-NLS-1$
		
	}

}
