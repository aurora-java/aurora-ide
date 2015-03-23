package aurora.ide.editor.textpage.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.editor.BaseCompositeMapEditor;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;

public class QuickFixInformationControl extends AbstractInformationControl
		implements DisposeListener {

	class ProposalHyperlinkAdapter extends HyperlinkAdapter {
		private ICompletionProposal cp;

		public ProposalHyperlinkAdapter(ICompletionProposal cp) {
			this.cp = cp;
		}

		@Override
		public void linkActivated(HyperlinkEvent e) {
			if (cp instanceof CompletionProposalAction) {
				CompletionProposalAction cpa = (CompletionProposalAction) cp;
				if (cpa.isIgnoreReplace()) {
					IAction action = cpa.getAction();
					action.run();
					getShell().close();
					return;
				}
			}
			cp.apply(curDoc);
			Point selection = cp.getSelection(curDoc);
			if (selection != null)
				textPage.getSelectionProvider().setSelection(
						new TextSelection(selection.x, selection.y));
			getShell().close();
		}
	}

	private Composite com;
	private MarkerAnnotation ma;
	private IMarker marker;
	private int displayWidth = 0;
	private int displayHeight = 0;
	private IDocument curDoc;
	private CompositeMap curMap;

	private TextPage textPage;

	private static Color gray_color = new Color(null, 128, 128, 128);
	private static Color link_color = new Color(null, 0, 102, 204);

	public QuickFixInformationControl(Shell parent, MarkerAnnotation ma,
			boolean isResizeable) {
		super(parent, isResizeable);
		init(ma);
	}

	public QuickFixInformationControl(Shell parent, MarkerAnnotation ma,
			String statusText) {
		super(parent, statusText);
		init(ma);
	}

	@Override
	public Point computeSizeHint() {
		return new Point(displayWidth, displayHeight + 20);
	}

	@Override
	public Rectangle computeTrim() {
		return Geometry.add(super.computeTrim(), com.computeTrim(0, 0, 0, 0));
	}

	@Override
	protected void createContent(final Composite parent) {
		com = new Composite(parent, 0);
		com.setForeground(parent.getForeground());
		com.setBackground(parent.getBackground());
		com.setFont(JFaceResources.getDialogFont());
		com.setLayout(null);
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new QuickFixInformationControl(parent, ma, true);
			}
		};
	}

	private int guessWidth(Control ctr, String msg) {
		GC gc = new GC(ctr);
		Point pt = gc.textExtent(msg);
		gc.dispose();
		return pt.x;
	}

	public boolean hasContents() {
		return marker != null;
	}

	private void init(MarkerAnnotation ma) {
		this.ma = ma;
		marker = ma.getMarker();
		textPage = (TextPage) ((BaseCompositeMapEditor) AuroraPlugin
				.getActivePage().getActiveEditor()).getActiveEditor();
		curDoc = textPage.getInputDocument();
		if (curDoc == null)
			return;
		try {
			curMap = CompositeMapUtil.loaderFromString(curDoc.get());
		} catch (ApplicationException e1) {
			e1.printStackTrace();
		}
		create();
	}

	@Override
	public void setBackgroundColor(Color background) {
		super.setBackgroundColor(background);
		com.setBackground(background);
	}

	@Override
	public void setForegroundColor(Color foreground) {
		super.setForegroundColor(foreground);
		com.setForeground(foreground);
	}

	@Override
	public void setInformation(String content) {
		if (com == null)
			return;
		final Text text = new Text(com, SWT.SINGLE);
		text.setMenu(new Menu(getShell(), SWT.NONE));
		text.setBackground(com.getBackground());
		text.setEditable(false);
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);
			text.setText(msg);
			displayWidth = guessWidth(text, msg) + 12;
		} catch (CoreException e) {
			e.printStackTrace();
			text.setText(e.getMessage());
		}
		text.setBounds(2, 2, displayWidth, 20);

		displayHeight = 30;
		final ICompletionProposal[] cps = new CompletionProposalCreator(curDoc,
				curMap, marker).getCompletionProposal();
		if (cps == null || cps.length == 0)
			return;
		com.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				e.gc.setForeground(gray_color);
				Rectangle r = text.getBounds();
				int y = r.y + r.height + 1;
				e.gc.drawLine(0, y, com.getShell().getSize().x, y);
			}
		});
		for (int i = 0; i < cps.length; i++) {
			Label l = new Label(com, 0);
			l.setBackground(com.getBackground());
			l.setBounds(3, displayHeight, 16, 20);
			l.setImage(cps[i].getImage());
			final Hyperlink hl = new Hyperlink(com, SWT.NONE);
			hl.setBounds(20, displayHeight,
					guessWidth(hl, cps[i].getDisplayString()) + 10, 20);
			hl.setText(cps[i].getDisplayString());
			hl.setBackground(com.getBackground());
			hl.setForeground(link_color);
			// hl.setUnderlined(true);
			hl.addHyperlinkListener(new ProposalHyperlinkAdapter(cps[i]));
			hl.addMouseTrackListener(new MouseTrackListener() {

				public void mouseEnter(MouseEvent e) {
					hl.setUnderlined(true);
				}

				public void mouseExit(MouseEvent e) {
					hl.setUnderlined(false);
				}

				public void mouseHover(MouseEvent e) {

				}
			});
			displayHeight += 24;
		}
		displayHeight += 24;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	public void widgetDisposed(DisposeEvent event) {
	}
}
