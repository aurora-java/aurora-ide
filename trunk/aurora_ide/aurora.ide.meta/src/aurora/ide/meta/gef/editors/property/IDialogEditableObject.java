package aurora.ide.meta.gef.editors.property;


public interface IDialogEditableObject {
	String getDescripition();

	Object getContextInfo();

	IDialogEditableObject clone();

}
