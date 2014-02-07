package aurora.ide.prototype.consultant.product.demonstrate;

import org.eclipse.core.runtime.IPath;


public class UIPInput {


	private final UIPInput fPrevious;
	private UIPInput fNext;

	private IPath path;
	/**
	 * Create a new Browser input.
	 *
	 * @param previous the input previous to this or <code>null</code> if this is the first
	 */
	public UIPInput(UIPInput previous,IPath path) {
		fPrevious= previous;
		this.path = path;
		if (previous != null)
			previous.fNext= this;
	}

	/**
	 * The previous input or <code>null</code> if this
	 * is the first.
	 *
	 * @return the previous input or <code>null</code>
	 */
	public UIPInput getPrevious() {
		return fPrevious;
	}

	/**
	 * The next input or <code>null</code> if this
	 * is the last.
	 *
	 * @return the next input or <code>null</code>
	 */
	public UIPInput getNext() {
		return fNext;
	}

	/**
	 * The element to use to set the browsers input.
	 *
	 * @return the input element
	 */
	public  IPath getPath(){
		return path;
	}

}
