package aurora.ide.prototype.consultant.product.fsd;

import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

public class FirstPage {

	private FSDDocumentPackage doc;
	private WordprocessingMLPackage wordMLPackage;
	private FunctionDesc function;

	public FirstPage(FSDDocumentPackage doc, FunctionDesc function) {
		this.doc = doc;
		this.wordMLPackage = doc.getWordMLPackage();
		this.function = function;
	}

	public void create() {
		MainDocumentPart mdp = doc.getMainDocumentPart();
		mdp.addStyledParagraphOfText("TitleBar", "");
		mdp.addStyledParagraphOfText("Title", "");
		mdp.addStyledParagraphOfText("Title", "HAND ENTERPRISE SOLUTIONS");
		mdp.addStyledParagraphOfText("DocTitle2", function.getFunCode());
		mdp.addStyledParagraphOfText("DocTitle3", function.getDocTitle());
		mdp.addStyledParagraphOfText("Docinfo", "");
		mdp.addStyledParagraphOfText("Docinfo", "");
		mdp.addStyledParagraphOfText("Docinfo", "");
		mdp.addStyledParagraphOfText("Docinfo", "");

		createFirstInfo(mdp,"作者:",function.getWriter());
		createFirstInfo(mdp,"建档日期:",function.getCreateDate());
		createFirstInfo(mdp,"上次更新:",function.getUpdateDate());
		createFirstInfo(mdp,"控制号:",function.getControlNo());
		createFirstInfo(mdp,"版本:",function.getVer());
		
		mdp.addStyledParagraphOfText("Docinfo", "");
		mdp.addStyledParagraphOfText("Docinfo", "");
		mdp.addStyledParagraphOfText("Docinfo", "");
		mdp.addStyledParagraphOfText("Docinfo", "");
		mdp.addStyledParagraphOfText("DocInfotabletitle", "审批:");
		
		createTbl();
	}
	
	private void createFirstInfo(MainDocumentPart mdp,String name,String value){
		P p =mdp.addStyledParagraphOfText("Docinfo", name);
		p.getContent().add(createTab());
		p.getContent().add(createText(value));
	}

	private R createTab() {
		org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		org.docx4j.wml.R run = factory.createR();
		run.getContent().add(factory.createRTab());
		return run;
	}

	private R createText(String text) {
		org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		org.docx4j.wml.R run = factory.createR();
		Text t = factory.createText();
		t.setValue(text);
		run.getContent().add(t);
		return run;
	}

	protected void createTbl() {
		WordprocessingMLPackage wordMLPackage = doc.getWordMLPackage();
		MainDocumentPart mdp = doc.getMainDocumentPart();
		int writableWidthTwips = wordMLPackage.getDocumentModel().getSections()
				.get(0).getPageDimensions().getWritableWidthTwips();
		int cols = 2;
		int cellWidthTwips = new Double(Math.floor((writableWidthTwips / cols)))
				.intValue();

		Tbl tbl = TblFactory.createTable(3, cols, cellWidthTwips);

		// w:tblPr
		String strTblPr = "<w:tblPr "
				+ Namespaces.W_NAMESPACE_DECLARATION
				+ ">"
				+ "<w:tblStyle w:val=\"TableGrid\"/>"
				+ "<w:tblW w:w=\"0\" w:type=\"auto\"/>"
				+ "<w:tblInd w:w=\"250\" w:type=\"dxa\" />"
				+ "<w:tblLook w:val=\"04A0\" w:firstRow=\"1\" w:lastRow=\"0\" w:firstColumn=\"1\" w:lastColumn=\"0\" w:noHBand=\"0\" w:noVBand=\"1\" />"
				+ "</w:tblPr>";
		TblPr tblPr = null;
		try {
			tblPr = (TblPr) XmlUtils.unmarshalString(strTblPr);
		} catch (JAXBException e) {
			// Shouldn't happen
			e.printStackTrace();
		}
		tbl.setTblPr(tblPr);

		createTc(tbl, 0, 0, "客户项目经理");
		createTc(tbl, 0, 1, function.getCustomerManager());
		createTc(tbl, 1, 0, "相关业务部门");
		createTc(tbl, 1, 1, function.getDept());
		createTc(tbl, 2, 0, "汉得项目经理");
		createTc(tbl, 2, 1, function.getHandManager());
		mdp.addObject(tbl);

		mdp.addStyledParagraphOfText("Docinfo", "");
	}

	protected void createTc(Tbl tbl, int row, int cols, String content) {
		Tc tc = (Tc) (((Tr) (tbl.getContent().get(row))).getContent()
				.get((cols)));
		tc.getContent().clear();
		tc.getContent().add(
				wordMLPackage.getMainDocumentPart()
						.createStyledParagraphOfText("DocInfoTable", content));
	}
}
