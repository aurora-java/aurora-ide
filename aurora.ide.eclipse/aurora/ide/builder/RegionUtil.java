package aurora.ide.builder;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class RegionUtil {
	/**
	 * check whether the givenn offset is in region
	 * 
	 * @param region
	 * @param offset
	 * @return if region is null ,returns false
	 */
	public static boolean isInRegion(IRegion region, int offset) {
		if (region == null)
			return false;
		return offset >= region.getOffset()
				&& offset <= region.getOffset() + region.getLength();
	}

	/**
	 * check whether region2 is a subregion of region1
	 * 
	 * @param region1
	 * @param region2
	 * @return if region1 or region2 is null , returns false
	 */
	public static boolean isSubRegion(IRegion region1, IRegion region2) {
		if (region1 == null || region2 == null)
			return false;
		return isInRegion(region1, region2.getOffset())
				&& isInRegion(region1,
						region2.getOffset() + region2.getLength());
	}

	/**
	 * compute the intersect of regino1 and region2
	 * 
	 * @param region1
	 * @param region2
	 * @return if region1 or region2 is null , returns null
	 */
	public static IRegion intersect(IRegion region1, IRegion region2) {
		if (region1 == null || region2 == null)
			return null;
		int offset = Math.max(region1.getOffset(), region2.getOffset());
		int offset2 = Math.min(region1.getOffset() + region1.getLength(),
				region2.getOffset() + region2.getLength());
		if (offset2 < offset)
			return null;
		IRegion region = new Region(offset, offset2 - offset);
		return region;
	}

	/**
	 * compute the union of region1 and region2
	 * 
	 * @param region1
	 * @param region2
	 * @return if region1 or region2 is null , returns null
	 */
	public static IRegion union(IRegion region1, IRegion region2) {
		if (region1 == null || region2 == null)
			return null;
		int offset = Math.min(region1.getOffset(), region2.getOffset());
		int offset2 = Math.max(region1.getOffset() + region1.getLength(),
				region2.getOffset() + region2.getLength());
		IRegion region = new Region(offset, offset2 - offset);
		return region;
	}
}
