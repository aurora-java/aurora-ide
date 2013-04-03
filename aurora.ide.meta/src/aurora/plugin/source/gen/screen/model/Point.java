package aurora.plugin.source.gen.screen.model;

public class Point {
	public int x;
	public int y;

	public static final Point NONE = new Point(-1, -1);

	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		super();
		this.x = p.x;
		this.y = p.y;
	}

}
