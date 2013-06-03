package aurora.ide.meta.gef.editors;

import java.util.HashMap;
import java.util.Map;

import oracle.sql.CHAR;

import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class TestKeyHandler extends KeyHandler {

	private Map actions;
	private KeyHandler parent;

	/**
	 * Processes a <i>key pressed</i> event. This method is called by the Tool
	 * whenever a key is pressed, and the Tool is in the proper state.
	 * 
	 * @param event
	 *            the KeyEvent
	 * @return <code>true</code> if KeyEvent was handled in some way
	 */
	public boolean keyPressed(KeyEvent event) {
		char character = event.character;
//		;
		
		
		
//		if(character == new Character()){
//			
//		}
		
		
		if (performStroke(KeyStroke.getPressed(character, event.keyCode,
				event.stateMask))) {
			event.doit = false;
			return true;
		}
		return parent != null && parent.keyPressed(event);
	}

	/**
	 * Processes a <i>key released</i> event. This method is called by the Tool
	 * whenever a key is released, and the Tool is in the proper state.
	 * 
	 * @param event
	 *            the KeyEvent
	 * @return <code>true</code> if KeyEvent was handled in some way
	 */
	public boolean keyReleased(KeyEvent event) {
		
		
		System.out.println(event.character);
		
		System.out.println(Character.codePointAt(new char[]{event.character}, 0));
//		Character.
//		CharSequence.
//		String.
		System.out.println(event.character);
//		Char a  = new Char();
//		int c = '0x01';
//		'\u0003'
		if(event.character == 3 ){
			System.out.println();
		}
		
		if(event.character == 22){
			System.out.println();
		}
		if(event.character == 25){
			System.out.println();
		}
		if(event.character == 26){
			System.out.println();
		}
		
		if (performStroke(KeyStroke.getReleased(event.character, event.keyCode,
				event.stateMask)))
			return true;
		return parent != null && parent.keyReleased(event);
	}

	private boolean performStroke(KeyStroke key) {
		
		boolean containsKey = actions.containsKey(key);
		if(containsKey == false){
			System.out.println();
		}
		 int hashCode = key.hashCode();
//		 25428097
		 int hashCode2 = KeyStroke.getReleased(99, SWT.CTRL).hashCode();
//		 26214532
		
		 if(hashCode!=hashCode2){
			 System.out.println();
		 }
		
		 if(key.equals(KeyStroke.getReleased(99, SWT.CTRL))){
			 System.out.println();
		 }
		
		if (actions == null)
			return false;
		IAction action = (IAction) actions.get(key);
		if (action == null)
			return false;
		if (action.isEnabled())
			action.run();
		return true;
	}

	/**
	 * Maps a specified <code>KeyStroke</code> to an <code>IAction</code>. When
	 * a KeyEvent occurs matching the given KeyStroke, the Action will be
	 * <code>run()</code> iff it is enabled.
	 * 
	 * @param keystroke
	 *            the KeyStroke
	 * @param action
	 *            the Action to run
	 */
	public void put(KeyStroke keystroke, IAction action) {
		if (actions == null)
			actions = new HashMap();
		actions.put(keystroke, action);
	}

	/**
	 * Removed a mapped <code>IAction</code> for the specified
	 * <code>KeyStroke</code>.
	 * 
	 * @param keystroke
	 *            the KeyStroke to be unmapped
	 */
	public void remove(KeyStroke keystroke) {
		if (actions != null)
			actions.remove(keystroke);
	}

	/**
	 * Sets a <i>parent</i> <code>KeyHandler</code> to which this KeyHandler
	 * will forward un-consumed KeyEvents. This KeyHandler will first attempt to
	 * handle KeyEvents. If it does not recognize a given KeyEvent, that event
	 * is passed to its <i>parent</i>
	 * 
	 * @param parent
	 *            the <i>parent</i> KeyHandler
	 * @return <code>this</code> for convenience
	 */
	public KeyHandler setParent(KeyHandler parent) {
		this.parent = parent;
		return this;
	}

}
