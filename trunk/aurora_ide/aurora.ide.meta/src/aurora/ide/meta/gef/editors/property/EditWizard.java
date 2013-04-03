package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.wizard.Wizard;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;

import aurora.ide.api.javascript.JavascriptRhino;

public abstract class EditWizard extends Wizard {

	public abstract void setDialogEdiableObject(IDialogEditableObject obj);

	protected String validateFunction(String jsCode) {
		try {
			JavascriptRhino js = new JavascriptRhino(jsCode);
			AstRoot ar = js.createAST();
			Node node = ar.getFirstChild();
			if (node instanceof FunctionNode) {
				FunctionNode fNode = (FunctionNode) node;
				Name fName = fNode.getFunctionName();
				if (fName == null)
					throw new Exception("function dose not has a name");
				String fn = fName.getString();
				if (fn != null && fn.length() > 0)
					return "";
			}
			return "please write a correct function";
		} catch (Exception e1) {
			String eMsg = e1.getMessage();
			if (eMsg == null || eMsg.length() == 0)
				return e1.toString();
			return eMsg;
		}
	}
}
