package aurora.ide.editor.textpage.xml.validate;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

abstract public class XMLErrorHandler implements ErrorHandler {

	public XMLErrorHandler() {

	}

	public void warning(SAXParseException exception) throws SAXException {
	}

	public void error(SAXParseException exception) throws SAXException {
	}

	public void fatalError(SAXParseException exception) throws SAXException {
	}

	// 配置过程中出现的异常，可能为
	// @see ParserConfigurationException 配置异常
	// @see SAXException 创建xmlreader异常
	// @see IOException 内容读取异常
	public void configurationError(Exception e) {

	}

}
