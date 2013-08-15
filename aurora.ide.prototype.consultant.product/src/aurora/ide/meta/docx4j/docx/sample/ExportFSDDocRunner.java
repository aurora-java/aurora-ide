package aurora.ide.meta.docx4j.docx.sample;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.STBrType;

public class ExportFSDDocRunner {

	public static void main(String[] args) throws Exception {

		boolean save = true;
		FSDDocumentPackage pkg = new FSDDocumentPackage();
		pkg.create();

		FirstPage page1 = new FirstPage(pkg);
		page1.create();

		createNewPage(pkg.getMainDocumentPart());
		SecondPage page2 = new SecondPage(pkg);
		page2.create();
		
		createNewPage(pkg.getMainDocumentPart());
		FourthPage page4 = new FourthPage(pkg);
		page4.create();
		
		createNewPage(pkg.getMainDocumentPart());
		FifthPage page5 = new FifthPage(pkg);
		page5.create();
		
		createNewPage(pkg.getMainDocumentPart());
		ContentPage page6 = new ContentPage(pkg);
		page6.create();
		
		System.out.println(XmlUtils.marshaltoString(pkg.getMainDocumentPart()
				.getJaxbElement(), true, true));

		// Optionally save it'
		if (save) {
			String filename = "/Users/shiliyan/Desktop"
					+ "/OUT_CopyStyles.docx";
			pkg.getWordMLPackage().save(new java.io.File(filename));
			System.out.println("Saved " + filename);
		}
	}

	public static P createNewPage(MainDocumentPart mdp) {
		ObjectFactory objectFactory = new ObjectFactory();
		org.docx4j.wml.P p = objectFactory.createP();
		org.docx4j.wml.R run = objectFactory.createR();
		Br br = objectFactory.createBr();
		br.setType(STBrType.PAGE);
		run.getContent().add(br);
		p.getContent().add(run);
		mdp.getContent().add(p);
		return p;
	}

}
