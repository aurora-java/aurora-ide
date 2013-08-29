package aurora.ide.prototype.consultant.product.fsd;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

public class SecondPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;

	public SecondPage(FSDDocumentPackage doc) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		mdp.addStyledParagraphOfText("2", Messages.SecondPage_1); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.addStyledParagraphOfText("3", Messages.SecondPage_5); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.getContent()
				.add(createTbl("aurora/ide/meta/docx4j/docx/sample/record_table.xml")); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.SecondPage_10); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.getContent()
				.add(createTbl("aurora/ide/meta/docx4j/docx/sample/reviewer_table.xml")); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("3", Messages.SecondPage_15); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$
		mdp.getContent()
				.add(createTbl("aurora/ide/meta/docx4j/docx/sample/dispatch_table.xml")); //$NON-NLS-1$
		mdp.addStyledParagraphOfText("Docinfo", ""); //$NON-NLS-1$ //$NON-NLS-2$
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

	protected void createTc(Tbl tbl, int row, int cols, String content) {
		Tc tc = (Tc) (((Tr) (tbl.getContent().get(row))).getContent()
				.get((cols)));
		tc.getContent().clear();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("DocInfoTable", content)); //$NON-NLS-1$
	}
}
