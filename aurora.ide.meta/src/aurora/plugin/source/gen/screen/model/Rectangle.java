package aurora.plugin.source.gen.screen.model;

public class Rectangle {
	public int x;

	public int y;
	public int width;
	public int height;

	// static final public Rectangle NONE = new Rectangle(-1, -1, -1, -1);

	public Rectangle(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Point getLocation() {
		return new Point(x, y);
	}

	public Point getSize() {
		return new Point(width, height);
	}

	public void setLocation(Point p) {
		x = p.x;
		y = p.y;
	}

	public void setSize(Point p) {
		width = p.x;
		height = p.y;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Rectangle getCopy() {
		return new Rectangle(x, y, width, height);
	}

	public static Rectangle NONE() {
		return new Rectangle(-1, -1, -1, -1);
	}
}
