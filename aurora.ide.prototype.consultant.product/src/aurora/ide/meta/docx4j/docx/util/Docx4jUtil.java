package aurora.ide.meta.docx4j.docx.util;

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.STBrType;

public class Docx4jUtil {
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
