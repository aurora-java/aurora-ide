package aurora.ide.editor.textpage.xml.validate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import aurora.ide.helpers.DialogUtil;

public class XMLValidator {
	private static SAXParserFactory parser_factory = SAXParserFactory
			.newInstance();
	static {
		parser_factory.setNamespaceAware(true);
		parser_factory.setValidating(false);
	}

	private XMLErrorHandler errorHandler;

	public XMLValidator(XMLErrorHandler errorHandler) {
		super();
		this.errorHandler = errorHandler;
	}

	private void validate(InputSource source) {
		try {
			SAXParser parser = parser_factory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(new DefaultHandler());
			if (errorHandler != null)
				xmlReader.setErrorHandler(errorHandler);
			xmlReader.parse(source);
		} catch (ParserConfigurationException e) {
			if (errorHandler != null)
				errorHandler.configurationError(e);
		} catch (SAXException e) {
			if (errorHandler != null)
				errorHandler.configurationError(e);
		} catch (IOException e) {
			if (errorHandler != null)
				errorHandler.configurationError(e);
		}
	}

	public void validate(InputStream stream) {
		validate(new InputSource(stream));
	}

	public void validate(String xml) {
		try {
			validate(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
		}
	}

	public void validate(IFile file) {
		try {
			validate(file.getContents());
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public void validate(File file) {
		try {
			validate(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	// public boolean validatexmlbyxsd(String xmlfilename, String xsdfilename,
	// boolean validatesuccess) {
	//
	// File xmlfile = new File(xmlfilename);
	// File xsdfile = new File(xsdfilename);
	// // 检测原文件和验证文件是否存在
	// if (!((xmlfile.exists()) && (xsdfile.exists())))
	// return false;
	// // final String SCHEMA_LANGUAGE =
	// // "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	// // final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	// // final String SCHEMA_SOURCE =
	// // "http://java.sun.com/xml/jaxp/properties/schemaSource";
	//
	// SAXParserFactory factory = SAXParserFactory.newInstance();
	// factory.setNamespaceAware(true);
	// factory.setValidating(false);
	//
	// try {
	// SAXParser parser = factory.newSAXParser();
	// // parser.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
	// // parser.setProperty(SCHEMA_SOURCE, xsdfile);
	//
	// XMLReader xmlReader = parser.getXMLReader();
	// xmlReader.setContentHandler(new DefaultHandler());
	// xmlReader.setErrorHandler(new MyErrorHandler(System.out));
	// // XML存放地址
	// xmlReader.parse(new InputSource(new FileInputStream(xmlfile)));
	//
	// validatesuccess = true;
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// validatesuccess = false;
	// }
	// return validatesuccess;
	// }
	//
	// public static void main(String[] args) {
	// boolean b = new XMLValidator(null)
	// .validatexmlbyxsd(
	// "/Users/shishiliyan/Desktop/work/aurora/workspace/runtime-aurora_protype/test.aurora.project/web/display/exp_report_maintain_travel.screen",
	// "/Users/shishiliyan/Desktop/work/aurora/workspace/aurora/aurora/aurora_builtin_package/aurora.base/config/aurora_screen.sxsd",
	// false);
	// System.out.println(b);
	// }
}
