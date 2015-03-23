/**
 * 
 */
package aurora.ide.helpers;

import uncertain.composite.QualifiedName;

/**
 * @author linjinxiao
 * 
 */
public interface AuroraConstant {
	// uri
	public final static String UncertainUri = "http://www.uncertain-framework.org/schema/simple-schema";
	public final static String ApplicationUri = "http://www.aurora-framework.org/application";
	public final static String BMUri = "http://www.aurora-framework.org/schema/bm";
	// qn
	public final static QualifiedName ScreenQN = new QualifiedName(
			ApplicationUri, "screen");
	public final static QualifiedName ViewQN = new QualifiedName(
			ApplicationUri, "view");
	public final static QualifiedName DataSetQN = new QualifiedName(
			ApplicationUri, "dataSet");
	public final static QualifiedName FieldsQN = new QualifiedName(
			ApplicationUri, "fields");
	public final static QualifiedName FieldQN = new QualifiedName(
			ApplicationUri, "field");
	public final static QualifiedName DataSetSQN = new QualifiedName(
			ApplicationUri, "dataSets");
	public static QualifiedName ModelQN = new QualifiedName(BMUri, "model");

	public static String[] BuildinFileExtension = new String[] { "screen", "bm" };
	public static String ScreenFileExtension = "screen";
	public static String ServiceFileExtension = "service";
	public static String SvcFileExtension = "svc";
	public static String BMFileExtension = "bm";
	public static String DbConfigFileName = "datasource.config";
	public static String CoreConfigFileName = "uncertain.xml";
	public static String LogConfigFileName = "service-logging.config";

	public static String ENCODING = "UTF-8";

}
