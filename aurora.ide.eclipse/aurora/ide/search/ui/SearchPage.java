package aurora.ide.search.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkingSet;

import aurora.ide.search.condition.AttributeCondition;
import aurora.ide.search.condition.AuroraSearchPattern;
import aurora.ide.search.condition.ElementCondition;
import aurora.ide.search.condition.NameSpaceCondition;
import aurora.ide.search.condition.SearchCondition;
import aurora.ide.search.condition.SearchForCondition;
import aurora.ide.search.core.AuroraSearchQuery;

public class SearchPage extends DialogPage implements ISearchPage {

	public static final String ID = "aurora.search.SearchPage";

	private Button[] fSearchFor;

	private Combo namespacePattern;
	private Button namespaceCaseSensitive;

	private Combo elementPattern;
	private Button elementCaseSensitive;

	private Combo attributePattern;
	private Combo attValuePattern;
	private Button attributeCaseSensitive;

	private ISearchPageContainer fContainer;
	private SearchPageManager pageManager = new SearchPageManager();

	private boolean fSearchDerived = false;

	private boolean fFirstTime = true;

	private Button namespaceRegEx;

	private Button attributeRegEx;

	private Button elementRegEx;

	public SearchPage() {

	}

	public SearchPage(String title) {
		super(title);
	}

	public SearchPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void createControl(Composite parent) {

		initializeDialogUnits(parent);
		readConfiguration();

		Composite result = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, true);
		layout.horizontalSpacing = 10;
		result.setLayout(layout);

		Control searchFor = createSearchFor(result);
		searchFor.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, 2, 1));

		Control expressionComposite = createExpression(result);
		expressionComposite.setLayoutData(new GridData(GridData.FILL,
				GridData.CENTER, true, false, 2, 1));

		Label separator = new Label(result, SWT.NONE);
		separator.setVisible(false);
		GridData data = new GridData(GridData.FILL, GridData.FILL, false,
				false, 2, 1);
		data.heightHint = convertHeightInCharsToPixels(1) / 3;
		separator.setLayoutData(data);

		setControl(result);

		Dialog.applyDialogFont(result);
		// PlatformUI.getWorkbench().getHelpSystem()
		// .setHelp(result, IJavaHelpContextIds.JAVA_SEARCH_PAGE);
		handleChanged();
	}

	private Control createSearchFor(Composite parent) {
		Group result = new Group(parent, SWT.NONE);
		result.setText("Search for");
		result.setLayout(new GridLayout(3, true));

		fSearchFor = new Button[] {
				createButton(result, SWT.RADIO, "element", 1, true),
				createButton(result, SWT.RADIO, "child", 2, false),
				createButton(result, SWT.RADIO, "parent", 3, false) };
		return result;
	}

	private Button createButton(Composite parent, int style, String text,
			int data, boolean isSelected) {
		Button button = new Button(parent, style);
		button.setText(text);
		button.setData(new Integer(data));
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		button.setSelection(isSelected);
		return button;
	}

	private Control createExpression(Composite parent) {
		Group result = new Group(parent, SWT.NONE);
		result.setText("Search String(* = any string,? = any character)");
		result.setLayout(new GridLayout(6, true));

		// Pattern namespace
		Label label = new Label(result, SWT.LEFT);
		label.setText("Namespace");
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false,
				false, 1, 1));
		namespacePattern = new Combo(result, SWT.SINGLE | SWT.BORDER);
		namespacePattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleChanged();
			}
		});
		namespacePattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleChanged();
			}
		});
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, false,
				3, 1);
		data.widthHint = convertWidthInCharsToPixels(20);
		namespacePattern.setLayoutData(data);
		// Ignore case checkbox
		namespaceCaseSensitive = new Button(result, SWT.CHECK);
		namespaceCaseSensitive.setText("Case sensitive");
		namespaceCaseSensitive.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, false, false, 1, 1));

		// RegEx checkbox
		namespaceRegEx = new Button(result, SWT.CHECK);
		namespaceRegEx.setText("Regular e&xpression");
		namespaceRegEx.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		namespaceRegEx.setFont(result.getFont());

		// Pattern Element
		label = new Label(result, SWT.LEFT);
		label.setText("Element");
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false,
				false, 1, 1));
		elementPattern = new Combo(result, SWT.SINGLE | SWT.BORDER);
		elementPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleChanged();
			}
		});
		elementPattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleChanged();
			}
		});
		data = new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1);
		data.widthHint = convertWidthInCharsToPixels(20);
		elementPattern.setLayoutData(data);
		// Ignore case checkbox
		elementCaseSensitive = new Button(result, SWT.CHECK);
		elementCaseSensitive.setText("Case sensitive");
		elementCaseSensitive.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, false, false, 1, 1));

		elementRegEx = new Button(result, SWT.CHECK);
		elementRegEx.setText("Regular e&xpression");
		elementRegEx.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		elementRegEx.setFont(result.getFont());

		// Pattern Attribute
		label = new Label(result, SWT.LEFT);
		label.setText("Contains Attribute");
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false,
				false, 1, 1));
		attributePattern = new Combo(result, SWT.SINGLE | SWT.BORDER);
		attributePattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleChanged();
			}
		});
		attributePattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleChanged();
			}
		});
		data = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint = convertWidthInCharsToPixels(20);
		attributePattern.setLayoutData(data);

		label = new Label(result, SWT.LEFT);
		label.setText("value");
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false,
				false, 1, 1));
		attValuePattern = new Combo(result, SWT.SINGLE | SWT.BORDER);
		attValuePattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleChanged();
			}
		});
		attValuePattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleChanged();
			}
		});
		data = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint = convertWidthInCharsToPixels(20);
		attValuePattern.setLayoutData(data);
		// Ignore case checkbox
		attributeCaseSensitive = new Button(result, SWT.CHECK);
		attributeCaseSensitive.setText("Case sensitive");
		attributeCaseSensitive.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, false, false, 1, 1));
		attributeRegEx = new Button(result, SWT.CHECK);
		attributeRegEx.setText("Regular e&xpression");
		attributeRegEx.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		attributeRegEx.setFont(result.getFont());
		return result;
	}

	private void handleChanged() {
		AuroraSearchPattern createSearchPattern = createSearchPattern();
		List<SearchCondition> conditions = createSearchPattern.getConditions();
		this.getContainer().setPerformActionEnabled(conditions.size() > 0);
	}

	public boolean performAction() {
		addSearchHistory();
		return performNewSearch();
	}

	private void addSearchHistory() {
		NameSpaceCondition namespaceCondition = getNameSpaceCondition();
		this.pageManager.addPreviousSearchCondition(
				this.pageManager.getPreviousSearchNameSpace(),
				namespaceCondition);
		ElementCondition elementCondition = getElementCondition();
		this.pageManager.addPreviousSearchCondition(
				this.pageManager.getPreviousSearchElement(), elementCondition);

		AttributeCondition attCondition = getAttributeCondition();
		this.pageManager.addPreviousSearchCondition(
				this.pageManager.getPreviousSearchAttribute(), attCondition);

	}

	private AttributeCondition getAttributeCondition() {
		AttributeCondition attCondition = new AttributeCondition();
		attCondition.setName(this.attributePattern.getText());
		attCondition.setValue(this.attValuePattern.getText());
		attCondition.setCaseSensitive(this.attributeCaseSensitive
				.getSelection());
		attCondition.setRegularExpression(this.attributeRegEx.getSelection());
		return attCondition;
	}

	private ElementCondition getElementCondition() {
		ElementCondition elementCondition = new ElementCondition();
		elementCondition.setElementName(this.elementPattern.getText());
		elementCondition.setCaseSensitive(this.elementCaseSensitive
				.getSelection());
		elementCondition.setRegularExpression(this.elementRegEx.getSelection());
		return elementCondition;
	}

	private NameSpaceCondition getNameSpaceCondition() {
		NameSpaceCondition namespaceCondition = new NameSpaceCondition();
		namespaceCondition.setNameSpace(this.namespacePattern.getText());
		namespaceCondition.setCaseSensitive(this.namespaceCaseSensitive
				.getSelection());
		namespaceCondition.setRegularExpression(this.namespaceRegEx.getSelection());
		return namespaceCondition;
	}

	private boolean performNewSearch() {
		AuroraSearchPattern pattern = createSearchPattern();
		FileTextSearchScope createTextSearchScope = this
				.createTextSearchScope();
		AuroraSearchQuery textSearchJob = new AuroraSearchQuery(
				createTextSearchScope, pattern);
		NewSearchUI.runQueryInBackground(textSearchJob);
		return true;
	}

	private AuroraSearchPattern createSearchPattern() {
		String type = null;
		for (Button b : fSearchFor) {
			boolean selection = b.getSelection();
			if (selection) {
				type = b.getText();
				break;
			}
		}
		AuroraSearchPattern pattern = new AuroraSearchPattern(
				new SearchForCondition(type));
		NameSpaceCondition nameSpaceCondition = this.getNameSpaceCondition();
		if (!"".equals(nameSpaceCondition.getNameSpace()))
			pattern.addCondition(nameSpaceCondition);
		ElementCondition elementCondition = this.getElementCondition();
		if (!"".equals(elementCondition.getElementName()))
			pattern.addCondition(elementCondition);
		AttributeCondition attributeCondition = this.getAttributeCondition();
		if (!"".equals(attributeCondition.getName())
				|| !"".equals(attributeCondition.getValue()))
			pattern.addCondition(attributeCondition);
		return pattern;
	}

	public void setContainer(ISearchPageContainer container) {
		fContainer = container;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			if (fFirstTime) {
				fFirstTime = false;
				if (this.namespacePattern != null) {
					namespacePattern.setItems(this.pageManager
							.getPreviousNameSpace());

					namespacePattern.select(0);
				}
				if (this.elementPattern != null) {
					elementPattern.setItems(this.pageManager
							.getPreviousElement());
					elementPattern.select(0);
				}
				if (this.attributePattern != null) {
					attributePattern.setItems(this.pageManager
							.getPreviousAttributeNames());
					attributePattern.select(0);
				}
				if (this.attValuePattern != null) {
					attValuePattern.setItems(this.pageManager
							.getPreviousAttributeValues());
					attValuePattern.select(0);
				}
			}
			if (this.namespacePattern != null) {
				namespacePattern.setFocus();
			}
		}
		this.handleChanged();
		// IEditorInput editorInput= getContainer().getActiveEditorInput();
		// getContainer().setActiveEditorCanProvideScopeSelection(editorInput !=
		// null && editorInput.getAdapter(IFile.class) != null);
		super.setVisible(visible);
	}

	private void readConfiguration() {
		pageManager.readConfiguration(
				SearchPageManager.STORE_NAMESPACE_HISTORY_SIZE,
				pageManager.getPreviousSearchNameSpace());
		pageManager.readConfiguration(
				SearchPageManager.STORE_ELEMENT_HISTORY_SIZE,
				pageManager.getPreviousSearchElement());
		pageManager.readConfiguration(
				SearchPageManager.STORE_ATTRIBUTE_HISTORY_SIZE,
				pageManager.getPreviousSearchAttribute());

	}

	/**
	 * Stores it current configuration in the dialog store.
	 */
	private void writeConfiguration() {
		pageManager.writeConfiguration(
				SearchPageManager.STORE_NAMESPACE_HISTORY_SIZE,
				pageManager.getPreviousSearchNameSpace());
		pageManager.writeConfiguration(
				SearchPageManager.STORE_ELEMENT_HISTORY_SIZE,
				pageManager.getPreviousSearchElement());
		pageManager.writeConfiguration(
				SearchPageManager.STORE_ATTRIBUTE_HISTORY_SIZE,
				pageManager.getPreviousSearchAttribute());
	}

	public void dispose() {
		writeConfiguration();
		super.dispose();
	}

	public FileTextSearchScope createTextSearchScope() {
		// Setup search scope
		switch (getContainer().getSelectedScope()) {
		case ISearchPageContainer.WORKSPACE_SCOPE:
			return FileTextSearchScope.newWorkspaceScope(getExtensions(),
					fSearchDerived);
		case ISearchPageContainer.SELECTION_SCOPE:
			return getSelectedResourcesScope();
		case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:
			return getEnclosingProjectScope();
		case ISearchPageContainer.WORKING_SET_SCOPE:
			IWorkingSet[] workingSets = getContainer().getSelectedWorkingSets();
			return FileTextSearchScope.newSearchScope(workingSets,
					getExtensions(), fSearchDerived);
		default:
			// unknown scope
			return FileTextSearchScope.newWorkspaceScope(getExtensions(),
					fSearchDerived);
		}
	}

	private ISearchPageContainer getContainer() {
		return fContainer;
	}

	private FileTextSearchScope getSelectedResourcesScope() {
		HashSet resources = new HashSet();
		ISelection sel = getContainer().getSelection();
		if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
			Iterator iter = ((IStructuredSelection) sel).iterator();
			while (iter.hasNext()) {
				Object curr = iter.next();
				if (curr instanceof IWorkingSet) {
					IWorkingSet workingSet = (IWorkingSet) curr;
					if (workingSet.isAggregateWorkingSet()
							&& workingSet.isEmpty()) {
						return FileTextSearchScope.newWorkspaceScope(
								getExtensions(), fSearchDerived);
					}
					IAdaptable[] elements = workingSet.getElements();
					for (int i = 0; i < elements.length; i++) {
						IResource resource = (IResource) elements[i]
								.getAdapter(IResource.class);
						if (resource != null && resource.isAccessible()) {
							resources.add(resource);
						}
					}
				} else if (curr instanceof IAdaptable) {
					IResource resource = (IResource) ((IAdaptable) curr)
							.getAdapter(IResource.class);
					if (resource != null && resource.isAccessible()) {
						resources.add(resource);
					}
				}
			}
		}
		IResource[] arr = (IResource[]) resources
				.toArray(new IResource[resources.size()]);
		return FileTextSearchScope.newSearchScope(arr, getExtensions(),
				fSearchDerived);
	}

	private String[] getExtensions() {

		return new String[] { "*.bm", "*.svc", "*.screen" };
	}

	final void updateOKStatus() {
		// boolean regexStatus= validateRegex();
		// boolean hasFilePattern= fExtensions.getText().length() > 0;
		getContainer().setPerformActionEnabled(true);
	}

	private FileTextSearchScope getEnclosingProjectScope() {
		String[] enclosingProjectName = getContainer()
				.getSelectedProjectNames();
		if (enclosingProjectName == null) {
			return FileTextSearchScope.newWorkspaceScope(getExtensions(),
					fSearchDerived);
		}

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource[] res = new IResource[enclosingProjectName.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = root.getProject(enclosingProjectName[i]);
		}

		return FileTextSearchScope.newSearchScope(res, getExtensions(),
				fSearchDerived);
	}

}
