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
		mdp.addStyledParagraphOfText("2", "寄销网上发票内部查询");
		mdp.addStyledParagraphOfText("3", "前提（Prerequisite）");
		mdp.addParagraphOfText("");
		mdp.addStyledParagraphOfText("3", "术语定义（Terminology Definition）");
		mdp.addParagraphOfText("");
		mdp.addStyledParagraphOfText("3", "用户权限");
		mdp.addParagraphOfText("");
		mdp.addStyledParagraphOfText("3", "操作流程（Procedure）");
		mdp.addParagraphOfText("");
		
	}

}
