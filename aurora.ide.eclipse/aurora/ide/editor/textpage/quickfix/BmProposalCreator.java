package aurora.ide.editor.textpage.quickfix;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.bm.wizard.sql.BMFromSQLWizard;
import aurora.ide.bm.wizard.sql.BMFromSQLWizardPage;
import aurora.ide.builder.ResourceUtil;

public class BmProposalCreator extends AbstractProposalCreator {

	public BmProposalCreator(IDocument doc, CompositeMap rootMap, IMarker marker) {
		super(doc, rootMap, marker);
	}

	@Override
	protected boolean isFixable() {
		if (super.isFixable()) {
			String word = getMarkerWord();
			if (word.equals("\"\"") || word.equals("''")) //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			return isValidWord(word);
		} else
			return false;
	}

	@Override
	protected void create(ArrayList<ICompletionProposal> result) {
		IProject project = AuroraPlugin.getActiveIFile().getProject();
		String bmHomeStr = ResourceUtil.getBMHome(project);
		if (bmHomeStr == null || bmHomeStr.length() == 0)
			return;
		String bmPath = getMarkerWord();
		String bmName = bmPath;
		int idx = bmPath.lastIndexOf('.');
		String prefix = ""; //$NON-NLS-1$
		if (idx != -1) {
			prefix = bmPath.substring(0, idx).replace('.', Path.SEPARATOR);
			bmName = bmPath.substring(idx + 1);
		}
		final IFolder folder = project.getParent().getFolder(
				new Path(bmHomeStr + Path.SEPARATOR + prefix));
		ArrayList<SortElement> list = getAvailableBm(bmName, folder);
		if (prefix.length() > 0)
			prefix = prefix.replace(Path.SEPARATOR, '.') + '.';
		IRegion region = getMarkerRegion();
		for (SortElement se : list) {
			String str = prefix + se.name;
			ICompletionProposal cp = new CompletionProposal(str,
					region.getOffset(), region.getLength(), str.length(),
					img_rename, NLS.bind(Messages.Change_to, str), null,
					NLS.bind(Messages.Suggest_change_to, str));
			result.add(cp);
		}
		result.add(getCreateNewBmProposal(bmPath, bmName, region, folder));
	}

	private ICompletionProposal getCreateNewBmProposal(String bmPath,
			final String bmName, IRegion region, final IFolder folder) {
		CompletionProposalAction cpa = new CompletionProposalAction("", //$NON-NLS-1$
				region.getOffset(), 0, 0, img_new, NLS.bind(Messages.Create_bm,
						bmPath), null, Messages.BmProposalCreator_5);
		cpa.setAction(new EmptyAction() {

			@Override
			public void run() {
				BMFromSQLWizard sqlWizard = new BMFromSQLWizard();
				WizardDialog wd = new WizardDialog(new Shell(Display
						.getCurrent()), sqlWizard);
				wd.setBlockOnOpen(false);
				wd.open();
				BMFromSQLWizardPage wp = (BMFromSQLWizardPage) wd
						.getCurrentPage();
				wp.setFolder(folder.getFullPath().toString());
				wp.setFileName(bmName);
				wp.getSQLTextField().forceFocus();
			}
		});
		cpa.setIgnoreReplace(true);
		return cpa;
	}

	/**
	 * get available bm name for {@code bmName} in folder
	 * 
	 * @param bmName
	 *            name of bm
	 * @param folder
	 *            folder of bm
	 * @return
	 */
	private ArrayList<SortElement> getAvailableBm(String bmName, IFolder folder) {
		ArrayList<SortElement> comp = new ArrayList<SortElement>();
		try {
			for (IResource res : folder.members()) {
				if (res instanceof IFile) {
					if ("bm".equalsIgnoreCase(res.getFileExtension())) { //$NON-NLS-1$
						String fn = res.getName();
						fn = fn.substring(0, fn.length() - 3);
						int ed = QuickAssistUtil.getApproiateEditDistance(
								bmName, fn);
						if (ed > 0)
							comp.add(new SortElement(fn, ed));
					}
				}
			}
		} catch (CoreException e) {
		}
		Collections.sort(comp);
		return comp;
	}
}
