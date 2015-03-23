package aurora.ide.editor.textpage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import aurora.ide.editor.textpage.markers.XMLValidationErrorHandler;



/**
 * Performs DTD validation on supplied XML document
 */
public class XMLParser
{

	private ErrorHandler errorHandler;
	private ContentHandler contentHandler;

	public void setErrorHandler(ErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	public void setContentHandler(ContentHandler contentHandler)
	{
		this.contentHandler = contentHandler;
	}

	public static void main(String[] args)
	{
		try
		{
			XMLParser parser = new XMLParser();
			parser.setErrorHandler(new XMLValidationErrorHandler());
			parser.doParse(new File(args[0]));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";

	/**
	 * Does DTD-based validation on File
	 */
	public void doParse(File xmlFilePath) throws RuntimeException
	{

		InputSource inputSource = null;
		try
		{
			inputSource = new InputSource(new FileReader(xmlFilePath));
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		doParse(inputSource);

	}

	/**
	 * Does DTD-based validation on text
	 */
	public void doParse(String xmlText) throws RuntimeException
	{

		InputSource inputSource = new InputSource(new StringReader(xmlText));
		doParse(inputSource);

	}

	/**
	 * Does DTD-based validation on inputSource
	 */
	public void doParse(InputSource inputSource) throws RuntimeException
	{

		try
		{
//			XMLReader reader = SAXParser.class.
			SAXParserFactory spfactory =SAXParserFactory.newInstance();
			SAXParser saxParser = spfactory.newSAXParser();
			XMLReader reader = saxParser.getXMLReader();
			reader.setErrorHandler(errorHandler);
			reader.setContentHandler(contentHandler);
//			String dynamic = "http://apache.org/xml/features/validation/dynamic";
//			reader.setFeature(dynamic, true);
			reader.setFeature(VALIDATION_FEATURE, true);
			reader.parse(inputSource);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

