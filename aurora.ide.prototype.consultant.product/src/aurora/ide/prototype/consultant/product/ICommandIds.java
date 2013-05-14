package aurora.ide.prototype.consultant.product;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

    public static final String CMD_OPEN = "aurora.ide.prototype.consultant.product.open";
    public static final String OPEN_COMMAND_ID = "aurora.ide.prototype.consultant.product.action.OpenFileAction";
	public static final String OPEN_ID = "aurora.ide.prototype.consultant.product.action.OpenFileAction";    
	public static final String EDITOR_ID = "aurora.ide.meta.gef.editors.ConsultantVScreenEditor";
}
