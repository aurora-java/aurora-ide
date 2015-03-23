/**
 * 
 */
package aurora.ide.editor.widgets.core;


/**
 * @author linjinxiao
 *
 */
public interface IGridViewer {
	public final int NONE = 0;
	public final int isMulti = 1<<1;
	public final int isColumnPacked = 1<<2;
	public final int fullEditable = 1<<3;
	public final int filterBar = 1<<4;
	public final int NoToolBar = 1<<5;
	public final int isAllChecked = 1<<6;
	public final int NoSeqColumn = 1<<7;
	public final int isOnlyUpdate = 1<<8;
	
}
