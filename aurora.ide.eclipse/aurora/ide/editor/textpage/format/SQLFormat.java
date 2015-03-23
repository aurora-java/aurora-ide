package aurora.ide.editor.textpage.format;

import SQLinForm_200.SQLForm;

public class SQLFormat {

	public String format(String sql) {
		SQLForm sf = new SQLForm();
		sf.setAlignmentAs(false);// <alignAs>true</alignAs>
		sf.setAlignmentComma(false);// <alignComma>false</alignComma>
		sf.setAlignmentComment(true);// <alignComment>true</alignComment>
		sf.setAlignmentConcat(true);// <alignConcat>true</alignConcat>
		sf.setAlignmentDeclaration(true);// <alignDecl>true</alignDecl>
		sf.setAlignmentEqual(true);// <alignEqual>true</alignEqual>
		sf.setAlignmentKeyword(false);// <alignKeyword>false</alignKeyword>
		sf.setAlignmentOperator(false);// <alignOperator>true</alignOperator>
		// sf.setBracketSpaces("0");// <bracketSpacing>0</bracketSpacing>
		// <bracketSpacingAndOrWhen>false</bracketSpacingAndOrWhen>
		// <breakAfterAnd>false</breakAfterAnd>
		// <breakAfterComma>true</breakAfterComma>
		sf.setLinebreakAfterConcat(false);// <breakAfterConcat>false</breakAfterConcat>
		// <breakBeforeAnd>true</breakBeforeAnd>
		// <breakBeforeComma>false</breakBeforeComma>
		// <breakBeforeComment>false</breakBeforeComment>
		sf.setLinebreakBeforeConcat(true);// <breakBeforeConcat>true</breakBeforeConcat>
		sf.setLinebreakCase(true);// <breakCase>true</breakCase>
		sf.setLinebreakCaseAndOr(true);// <breakCaseAndOr>true</breakCaseAndOr>
		sf.setLinebreakCaseElse(true);// <breakCaseElse>true</breakCaseElse>
		sf.setLinebreakCaseThen(true);// <breakCaseThen>true</breakCaseThen>
		sf.setLinebreakCaseWhen(true);// <breakCaseWhen>true</breakCaseWhen>
		sf.setLinebreakJoin(true);// <breakJoin>true</breakJoin>
		sf.setLinebreakKeyword(true);// <breakKeyword>false</breakKeyword>
		// <breakSchema>0</breakSchema>
		// <breakSelectBracket>false</breakSelectBracket>
		sf.setColor(false);// <colored>false</colored>
		// <commaSpacing>0</commaSpacing>
		// <dblIndent>false</dblIndent>
		// <forceDifference>true</forceDifference>
		// <indentAnd>false</indentAnd>
		// <lineNum>999</lineNum>
		// <lineWidth>999</lineWidth>

		// <moreNewlines>false</moreNewlines>
		
		sf.setNumCommas(1);// <numCommas>1</numCommas>
		sf.setIndention(4, false);// <numSpaces>2</numSpaces>
		// <operatorSpacing>0</operatorSpacing>
		// <quoteChar>0</quoteChar>
		// <replaceComment>false</replaceComment>
		// <smallSql>80</smallSql>
		// <sourceSql>1</sourceSql>
		// <sqlSourceEnclosed>0</sqlSourceEnclosed>
		// <sqlsourceCopied>false</sqlsourceCopied>
//		sf.setSuppressComment(true);// <suppressComment>false</suppressComment>
		// <targetSql>0</targetSql>
		// <uppercase>0</uppercase>
		// <useTab>false</useTab>
		sf.setSuppressEmptyLine(false);
		sf.setAndOrIndention(true);
		sf.setdoubleIndentionMasterKeyword(false);
		//sf.setSmallSQLWidth(80);
		sf.setLineBreak(false, true, false, true, false, true);
		return sf.formatSQLAsString(sql.trim());
	}
}
