package aurora.sql.java.editor.content.assist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionListenerExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension2;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import patch.org.eclipse.jdt.internal.ui.JavaPlugin;
import patch.org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import ext.org.eclipse.jdt.internal.corext.util.Messages;
import ext.org.eclipse.jdt.internal.ui.JavaUIMessages;
import ext.org.eclipse.jdt.internal.ui.dialogs.OptionalMessageDialog;
import ext.org.eclipse.jdt.internal.ui.text.java.CompletionProposalCategory;
import ext.org.eclipse.jdt.internal.ui.text.java.CompletionProposalComputerRegistry;
import ext.org.eclipse.jdt.internal.ui.text.java.JavaTextMessages;

public class SQLJContentAssistProcessor implements IContentAssistProcessor {

	/**
	 * The completion listener class for this processor.
	 * 
	 * @since 3.4
	 */
	private final class CompletionListener implements ICompletionListener,
			ICompletionListenerExtension {
		/*
		 * @see org.eclipse.jface.text.contentassist.ICompletionListener#
		 * assistSessionStarted
		 * (org.eclipse.jface.text.contentassist.ContentAssistEvent)
		 */
		public void assistSessionStarted(ContentAssistEvent event) {
			if (event.processor != SQLJContentAssistProcessor.this)
				return;

			fIterationGesture = getIterationGesture();
			KeySequence binding = getIterationBinding();

			// This may show the warning dialog if all categories are disabled
			setCategoryIteration();
			for (Iterator<CompletionProposalCategory> it = fCategories
					.iterator(); it.hasNext();) {
				CompletionProposalCategory cat = it.next();
				cat.sessionStarted();
			}

			fRepetition = 0;
			if (event.assistant instanceof IContentAssistantExtension2) {
				IContentAssistantExtension2 extension = (IContentAssistantExtension2) event.assistant;

				if (fCategoryIteration.size() == 1) {
					extension.setRepeatedInvocationMode(false);
					extension.setShowEmptyList(false);
				} else {
					extension.setRepeatedInvocationMode(true);
					extension.setStatusLineVisible(true);
					extension.setStatusMessage(createIterationMessage());
					extension.setShowEmptyList(true);
					if (extension instanceof IContentAssistantExtension3) {
						IContentAssistantExtension3 ext3 = (IContentAssistantExtension3) extension;
						((ContentAssistant) ext3)
								.setRepeatedInvocationTrigger(binding);
					}
				}

			}
		}

		/*
		 * @see org.eclipse.jface.text.contentassist.ICompletionListener#
		 * assistSessionEnded
		 * (org.eclipse.jface.text.contentassist.ContentAssistEvent)
		 */
		public void assistSessionEnded(ContentAssistEvent event) {
			if (event.processor != SQLJContentAssistProcessor.this)
				return;

			for (Iterator<CompletionProposalCategory> it = fCategories
					.iterator(); it.hasNext();) {
				CompletionProposalCategory cat = it.next();
				cat.sessionEnded();
			}

			fCategoryIteration = null;
			fRepetition = -1;
			fIterationGesture = null;
			if (event.assistant instanceof IContentAssistantExtension2) {
				IContentAssistantExtension2 extension = (IContentAssistantExtension2) event.assistant;
				extension.setShowEmptyList(false);
				extension.setRepeatedInvocationMode(false);
				extension.setStatusLineVisible(false);
				if (extension instanceof IContentAssistantExtension3) {
					IContentAssistantExtension3 ext3 = (IContentAssistantExtension3) extension;
					((ContentAssistant) ext3)
							.setRepeatedInvocationTrigger(null);
				}
			}
		}

		/*
		 * @see
		 * org.eclipse.jface.text.contentassist.ICompletionListener#selectionChanged
		 * (org.eclipse.jface.text.contentassist.ICompletionProposal, boolean)
		 */
		public void selectionChanged(ICompletionProposal proposal,
				boolean smartToggle) {
		}

		/*
		 * @see
		 * org.eclipse.jface.text.contentassist.ICompletionListenerExtension
		 * #assistSessionRestarted
		 * (org.eclipse.jface.text.contentassist.ContentAssistEvent)
		 * 
		 * @since 3.4
		 */
		public void assistSessionRestarted(ContentAssistEvent event) {
			fRepetition = 0;
		}
	}

	private static final boolean DEBUG = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jdt.ui/debug/ResultCollector")); //$NON-NLS-1$//$NON-NLS-2$

	/**
	 * Dialog settings key for the "all categories are disabled" warning dialog.
	 * See {@link OptionalMessageDialog}.
	 * 
	 * @since 3.3
	 */
	private static final String PREF_WARN_ABOUT_EMPTY_ASSIST_CATEGORY = "EmptyDefaultAssistCategory"; //$NON-NLS-1$

	private static final Comparator<CompletionProposalCategory> ORDER_COMPARATOR = new Comparator<CompletionProposalCategory>() {

		public int compare(CompletionProposalCategory d1,
				CompletionProposalCategory d2) {
			return d1.getSortOrder() - d2.getSortOrder();
		}

	};

	private final List<CompletionProposalCategory> fCategories;
	private final String fPartition;
	private final ContentAssistant fAssistant;

	private char[] fCompletionAutoActivationCharacters;

	/* cycling stuff */
	private int fRepetition = -1;
	private List<List<CompletionProposalCategory>> fCategoryIteration = null;
	private String fIterationGesture = null;
	private int fNumberOfComputedResults = 0;
	private String fErrorMessage;

	/**
	 * The completion proposal registry.
	 * 
	 * @since 3.4
	 */
	private CompletionProposalComputerRegistry fComputerRegistry;

	public SQLJContentAssistProcessor(ContentAssistant assistant,
			String partition) {
		// super(assistant)
		Assert.isNotNull(partition);
		Assert.isNotNull(assistant);
		fPartition = partition;
		fComputerRegistry = CompletionProposalComputerRegistry.getDefault();
		fCategories = fComputerRegistry.getProposalCategories();
		fAssistant = assistant;
		fAssistant.addCompletionListener(new CompletionListener());
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public final ICompletionProposal[] computeCompletionProposals(
			ITextViewer viewer, int offset) {

		// int replacementOffset, int replacementLength, int cursorPosition
		SqlContentAssistInvocationContext context = createContext(viewer,
				offset);
		SQLJContentAssistProvider pro = new SQLJContentAssistProvider(context);
		List<CompletionProposal> result = new ArrayList<CompletionProposal>();
		List<CompletionProposal> keyWordCompletionProposal = pro
				.getCompletionProposal();
		result.addAll(keyWordCompletionProposal);
		return result.toArray(new CompletionProposal[result.size()]);
		// long start= DEBUG ? System.currentTimeMillis() : 0;
		// // CompletionProposal
		// clearState();
		//
		// IProgressMonitor monitor= createProgressMonitor();
		// monitor.beginTask(JavaTextMessages.ContentAssistProcessor_computing_proposals,
		// fCategories.size() + 1);
		//
		// ContentAssistInvocationContext context= createContext(viewer,
		// offset);
		// long setup= DEBUG ? System.currentTimeMillis() : 0;
		//
		// monitor.subTask(JavaTextMessages.ContentAssistProcessor_collecting_proposals);
		// List<ICompletionProposal> proposals= collectProposals(viewer, offset,
		// monitor, context);
		// long collect= DEBUG ? System.currentTimeMillis() : 0;
		//
		// monitor.subTask(JavaTextMessages.ContentAssistProcessor_sorting_proposals);
		// List<ICompletionProposal> filtered= filterAndSortProposals(proposals,
		// monitor, context);
		// fNumberOfComputedResults= filtered.size();
		// long filter= DEBUG ? System.currentTimeMillis() : 0;
		//
		// ICompletionProposal[] result= filtered.toArray(new
		// ICompletionProposal[filtered.size()]);
		// monitor.done();
		//
		// if (DEBUG) {
		//			System.err.println("Code Assist Stats (" + result.length + " proposals)"); //$NON-NLS-1$ //$NON-NLS-2$
		//			System.err.println("Code Assist (setup):\t" + (setup - start) ); //$NON-NLS-1$
		//			System.err.println("Code Assist (collect):\t" + (collect - setup) ); //$NON-NLS-1$
		//			System.err.println("Code Assist (sort):\t" + (filter - collect) ); //$NON-NLS-1$
		// }
		//
		// return result;
	}

	private void clearState() {
		fErrorMessage = null;
		fNumberOfComputedResults = 0;
	}

	/**
	 * Collects the proposals.
	 * 
	 * @param viewer
	 *            the text viewer
	 * @param offset
	 *            the offset
	 * @param monitor
	 *            the progress monitor
	 * @param context
	 *            the code assist invocation context
	 * @return the list of proposals
	 */
	private List<ICompletionProposal> collectProposals(ITextViewer viewer,
			int offset, IProgressMonitor monitor,
			ContentAssistInvocationContext context) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		List<CompletionProposalCategory> providers = getCategories();
		for (Iterator<CompletionProposalCategory> it = providers.iterator(); it
				.hasNext();) {
			CompletionProposalCategory cat = it.next();
			List<ICompletionProposal> computed = cat
					.computeCompletionProposals(context, fPartition,
							new SubProgressMonitor(monitor, 1));
			proposals.addAll(computed);
			if (fErrorMessage == null)
				fErrorMessage = cat.getErrorMessage();
		}

		return proposals;
	}

	/**
	 * Filters and sorts the proposals. The passed list may be modified and
	 * returned, or a new list may be created and returned.
	 * 
	 * @param proposals
	 *            the list of collected proposals (element type:
	 *            {@link ICompletionProposal})
	 * @param monitor
	 *            a progress monitor
	 * @param context
	 *            TODO
	 * @return the list of filtered and sorted proposals, ready for display
	 *         (element type: {@link ICompletionProposal})
	 */
	protected List<ICompletionProposal> filterAndSortProposals(
			List<ICompletionProposal> proposals, IProgressMonitor monitor,
			ContentAssistInvocationContext context) {
		return proposals;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		clearState();

		IProgressMonitor monitor = createProgressMonitor();
		monitor.beginTask(
				JavaTextMessages.ContentAssistProcessor_computing_contexts,
				fCategories.size() + 1);

		monitor.subTask(JavaTextMessages.ContentAssistProcessor_collecting_contexts);
		List<IContextInformation> proposals = collectContextInformation(viewer,
				offset, monitor);

		monitor.subTask(JavaTextMessages.ContentAssistProcessor_sorting_contexts);
		List<IContextInformation> filtered = filterAndSortContextInformation(
				proposals, monitor);
		fNumberOfComputedResults = filtered.size();

		IContextInformation[] result = filtered
				.toArray(new IContextInformation[filtered.size()]);
		monitor.done();
		return result;
	}

	private List<IContextInformation> collectContextInformation(
			ITextViewer viewer, int offset, IProgressMonitor monitor) {
		List<IContextInformation> proposals = new ArrayList<IContextInformation>();
		ContentAssistInvocationContext context = createContext(viewer, offset);

		List<CompletionProposalCategory> providers = getCategories();
		for (Iterator<CompletionProposalCategory> it = providers.iterator(); it
				.hasNext();) {
			CompletionProposalCategory cat = it.next();
			List<IContextInformation> computed = cat.computeContextInformation(
					context, fPartition, new SubProgressMonitor(monitor, 1));
			proposals.addAll(computed);
			if (fErrorMessage == null)
				fErrorMessage = cat.getErrorMessage();
		}

		return proposals;
	}

	/**
	 * Filters and sorts the context information objects. The passed list may be
	 * modified and returned, or a new list may be created and returned.
	 * 
	 * @param contexts
	 *            the list of collected proposals (element type:
	 *            {@link IContextInformation})
	 * @param monitor
	 *            a progress monitor
	 * @return the list of filtered and sorted proposals, ready for display
	 *         (element type: {@link IContextInformation})
	 */
	protected List<IContextInformation> filterAndSortContextInformation(
			List<IContextInformation> contexts, IProgressMonitor monitor) {
		return contexts;
	}

	/**
	 * Sets this processor's set of characters triggering the activation of the
	 * completion proposal computation.
	 * 
	 * @param activationSet
	 *            the activation set
	 */
	public final void setCompletionProposalAutoActivationCharacters(
			char[] activationSet) {
		fCompletionAutoActivationCharacters = activationSet;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * getCompletionProposalAutoActivationCharacters()
	 */
	public final char[] getCompletionProposalAutoActivationCharacters() {
		return ".".toCharArray();
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage
	 * ()
	 */
	public String getErrorMessage() {
		if (fErrorMessage != null)
			return fErrorMessage;
		if (fNumberOfComputedResults > 0)
			return null;
		return JavaUIMessages.JavaEditor_codeassist_noCompletions;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	/**
	 * Creates a progress monitor.
	 * <p>
	 * The default implementation creates a <code>NullProgressMonitor</code>.
	 * </p>
	 * 
	 * @return a progress monitor
	 */
	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}

	/**
	 * Creates the context that is passed to the completion proposal computers.
	 * 
	 * @param viewer
	 *            the viewer that content assist is invoked on
	 * @param offset
	 *            the content assist offset
	 * @return the context to be passed to the computers
	 */
	protected SqlContentAssistInvocationContext createContext(
			ITextViewer viewer, int offset) {
		return new SqlContentAssistInvocationContext(viewer, offset);
	}

	private List<CompletionProposalCategory> getCategories() {
		if (fCategoryIteration == null)
			return fCategories;

		int iteration = fRepetition % fCategoryIteration.size();
		fAssistant.setStatusMessage(createIterationMessage());
		fAssistant.setEmptyMessage(createEmptyMessage());
		fRepetition++;

		// fAssistant.setShowMessage(fRepetition % 2 != 0);

		return fCategoryIteration.get(iteration);
	}

	// This may show the warning dialog if all categories are disabled
	private void setCategoryIteration() {
		fCategoryIteration = getCategoryIteration();
	}

	private List<List<CompletionProposalCategory>> getCategoryIteration() {
		List<List<CompletionProposalCategory>> sequence = new ArrayList<List<CompletionProposalCategory>>();
		sequence.add(getDefaultCategories());
		for (Iterator<CompletionProposalCategory> it = getSeparateCategories()
				.iterator(); it.hasNext();) {
			CompletionProposalCategory cat = it.next();
			sequence.add(Collections.singletonList(cat));
		}
		return sequence;
	}

	private List<CompletionProposalCategory> getDefaultCategories() {
		// default mix - enable all included computers
		List<CompletionProposalCategory> included = getDefaultCategoriesUnchecked();

		if (fComputerRegistry.hasUninstalledComputers(fPartition, included)) {
			if (informUserAboutEmptyDefaultCategory())
				// preferences were restored - recompute the default categories
				included = getDefaultCategoriesUnchecked();
			fComputerRegistry.resetUnistalledComputers();
		}

		return included;
	}

	private List<CompletionProposalCategory> getDefaultCategoriesUnchecked() {
		List<CompletionProposalCategory> included = new ArrayList<CompletionProposalCategory>();
		for (Iterator<CompletionProposalCategory> it = fCategories.iterator(); it
				.hasNext();) {
			CompletionProposalCategory category = it.next();
			if (category.isIncluded() && category.hasComputers(fPartition))
				included.add(category);
		}
		return included;
	}

	/**
	 * Informs the user about the fact that there are no enabled categories in
	 * the default content assist set and shows a link to the preferences.
	 * 
	 * @return <code>true</code> if the default should be restored
	 * @since 3.3
	 */
	private boolean informUserAboutEmptyDefaultCategory() {
		if (OptionalMessageDialog
				.isDialogEnabled(PREF_WARN_ABOUT_EMPTY_ASSIST_CATEGORY)) {
			final Shell shell = JavaPlugin.getActiveWorkbenchShell();
			String title = JavaTextMessages.ContentAssistProcessor_all_disabled_title;
			String message = JavaTextMessages.ContentAssistProcessor_all_disabled_message;
			// see PreferencePage#createControl for the 'defaults' label
			final String restoreButtonLabel = JFaceResources
					.getString("defaults"); //$NON-NLS-1$
			final String linkMessage = Messages
					.format(JavaTextMessages.ContentAssistProcessor_all_disabled_preference_link,
							LegacyActionTools
									.removeMnemonics(restoreButtonLabel));
			final int restoreId = IDialogConstants.CLIENT_ID + 10;
			final int settingsId = IDialogConstants.CLIENT_ID + 11;
			final OptionalMessageDialog dialog = new OptionalMessageDialog(
					PREF_WARN_ABOUT_EMPTY_ASSIST_CATEGORY, shell, title,
					null /* default image */, message, MessageDialog.WARNING,
					new String[] { restoreButtonLabel,
							IDialogConstants.CLOSE_LABEL }, 1) {
				/*
				 * @see
				 * org.eclipse.jdt.internal.ui.dialogs.OptionalMessageDialog
				 * #createCustomArea(org.eclipse.swt.widgets.Composite)
				 */
				@Override
				protected Control createCustomArea(Composite composite) {
					// wrap link and checkbox in one composite without space
					Composite parent = new Composite(composite, SWT.NONE);
					GridLayout layout = new GridLayout();
					layout.marginHeight = 0;
					layout.marginWidth = 0;
					layout.verticalSpacing = 0;
					parent.setLayout(layout);

					Composite linkComposite = new Composite(parent, SWT.NONE);
					layout = new GridLayout();
					layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
					layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
					layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
					linkComposite.setLayout(layout);

					Link link = new Link(linkComposite, SWT.NONE);
					link.setText(linkMessage);
					link.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							setReturnCode(settingsId);
							close();
						}
					});
					GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING,
							true, false);
					gridData.widthHint = this.getMinimumMessageWidth();
					link.setLayoutData(gridData);

					// create checkbox and "don't show this message" prompt
					super.createCustomArea(parent);

					return parent;
				}

				/*
				 * @see
				 * org.eclipse.jface.dialogs.MessageDialog#createButtonsForButtonBar
				 * (org.eclipse.swt.widgets.Composite)
				 */
				@Override
				protected void createButtonsForButtonBar(Composite parent) {
					Button[] buttons = new Button[2];
					buttons[0] = createButton(parent, restoreId,
							restoreButtonLabel, false);
					buttons[1] = createButton(parent,
							IDialogConstants.CLOSE_ID,
							IDialogConstants.CLOSE_LABEL, true);
					setButtons(buttons);
				}
			};
			int returnValue = dialog.open();
			if (restoreId == returnValue || settingsId == returnValue) {
				if (restoreId == returnValue) {
					IPreferenceStore store = JavaPlugin.getDefault()
							.getPreferenceStore();
					store.setToDefault(PreferenceConstants.CODEASSIST_CATEGORY_ORDER);
					store.setToDefault(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
				}
				if (settingsId == returnValue)
					PreferencesUtil
							.createPreferenceDialogOn(
									shell,
									"org.eclipse.jdt.ui.preferences.CodeAssistPreferenceAdvanced", null, null).open(); //$NON-NLS-1$
				fComputerRegistry.reload();
				return true;
			}
		}
		return false;
	}

	private List<CompletionProposalCategory> getSeparateCategories() {
		ArrayList<CompletionProposalCategory> sorted = new ArrayList<CompletionProposalCategory>();
		for (Iterator<CompletionProposalCategory> it = fCategories.iterator(); it
				.hasNext();) {
			CompletionProposalCategory category = it.next();
			if (category.isSeparateCommand()
					&& category.hasComputers(fPartition))
				sorted.add(category);
		}
		Collections.sort(sorted, ORDER_COMPARATOR);
		return sorted;
	}

	private String createEmptyMessage() {
		return Messages.format(
				JavaTextMessages.ContentAssistProcessor_empty_message,
				new String[] { getCategoryLabel(fRepetition) });
	}

	private String createIterationMessage() {
		return Messages
				.format(JavaTextMessages.ContentAssistProcessor_toggle_affordance_update_message,
						new String[] { getCategoryLabel(fRepetition),
								fIterationGesture,
								getCategoryLabel(fRepetition + 1) });
	}

	private String getCategoryLabel(int repetition) {
		int iteration = repetition % fCategoryIteration.size();
		if (iteration == 0)
			return JavaTextMessages.ContentAssistProcessor_defaultProposalCategory;
		return toString(fCategoryIteration.get(iteration).get(0));
	}

	private String toString(CompletionProposalCategory category) {
		return category.getDisplayName();
	}

	private String getIterationGesture() {
		TriggerSequence binding = getIterationBinding();
		return binding != null ? Messages
				.format(JavaTextMessages.ContentAssistProcessor_toggle_affordance_press_gesture,
						new Object[] { binding.format() })
				: JavaTextMessages.ContentAssistProcessor_toggle_affordance_click_gesture;
	}

	private KeySequence getIterationBinding() {
		final IBindingService bindingSvc = (IBindingService) PlatformUI
				.getWorkbench().getAdapter(IBindingService.class);
		TriggerSequence binding = bindingSvc
				.getBestActiveBindingFor(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		if (binding instanceof KeySequence)
			return (KeySequence) binding;
		return null;
	}
}
