package aurora.ide.meta.gef.editors.request;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.DropRequest;

public class DropBMRequest extends org.eclipse.gef.Request implements
		DropRequest ,AuroraRequestConstants{



	private IFile bm;

	private Dimension size;
	private Point location;

	/**
	 * Creates a CreateRequest with the default type.
	 */
	public DropBMRequest() {
		setType(DROP_BM);
	}

	/**
	 * Returns the location of the object to be created.
	 * 
	 * @return the location
	 */
	public Point getLocation() {
		return location;
	}

	

	public IFile getBm() {
		return bm;
	}

	public void setBm(IFile bm) {
		this.bm = bm;
	}

	/**
	 * Returns the size of the object to be created.
	 * 
	 * @return the size
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * Sets the location where the new object will be placed.
	 * 
	 * @param location
	 *            the location
	 */
	public void setLocation(Point location) {
		this.location = location;
	}

	/**
	 * Sets the size of the new object.
	 * 
	 * @param size
	 *            the size
	 */
	public void setSize(Dimension size) {
		this.size = size;
	}

}