package hec.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;
import org.eclipse.wst.jsdt.core.dom.ASTVisitor;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.xml.sax.SAXException;

import test.RunScript;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.ocm.OCManager;
import a.d;
import aurora.bm.BusinessModel;
import aurora.bm.ModelFactory;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.javascript.Javascript4Rhino;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.reference.NamedMapFinder;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 1111111111111111111111111111111111
 * @see IWorkbenchWindowActionDelegate
 */
public class GemLinkUrlTag implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	List<CompositeMap> maps = new ArrayList<CompositeMap>();
	private Map compositeMap = new HashMap();

	/**
	 * The constructor.
	 */
	public GemLinkUrlTag() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {

		try {
			run6();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static private List<IFile> screenList = new ArrayList<IFile>();
	static private List<IFile> bmList = new ArrayList<IFile>();
	// static private List<IFile> svcList = new ArrayList<IFile>();

	static private Map<IFile, List<Javascript4Rhino>> fileScriptsMap = new HashMap<IFile, List<Javascript4Rhino>>();
	static private Map<List<Javascript4Rhino>, IFile> scriptsFileMap = new HashMap<List<Javascript4Rhino>, IFile>();
	static private List<org.mozilla.javascript.ast.StringLiteral> slns = new ArrayList<org.mozilla.javascript.ast.StringLiteral>();
	static private Map<IFile, List<org.mozilla.javascript.ast.StringLiteral>> fileSLS = new HashMap<IFile, List<org.mozilla.javascript.ast.StringLiteral>>();
	static private List<LinkUrl> linkUrls = new LinkedList<LinkUrl>();
	static private List<Result> results = new LinkedList<Result>();

	public class Result {
		IFile file;
		CompositeMap map;
		List<CompositeMap> scriptMaps = new ArrayList<CompositeMap>();
		Map<CompositeMap, List<LinkUrl>> scriptsMapURL = new HashMap<CompositeMap, List<LinkUrl>>();
	}

	public class BMLinkIterationHandler implements IterationHandle {
		BMLinkIterationHandler(String model, String modelAction) {
			this.model = model;
			this.modelAction = modelAction;
		}

		String model, modelAction;
		CompositeMap link;

		@Override
		public int process(CompositeMap map) {
			if ("link".equals(map.getName()) && model.equals(map.get("model"))
					&& modelAction.equals(map.get("modelaction"))) {
				link = map;
			}
			if (link != null)
				return IterationHandle.IT_BREAK;
			return IterationHandle.IT_CONTINUE;
		}

	}

	public class LinkUrl {

		org.mozilla.javascript.ast.StringLiteral sl;
		IFile screen;
		boolean isScreen;
		String model;
		String modelAction;
		String screenUrl;
		IFile linkFile;
		String urlLeftString;
		String comment;
		CompositeMap scriptMap;
	}

	private static int i = 0;

	static void run6() throws CoreException, ApplicationException {
		screenList.clear();
		fileScriptsMap.clear();
		scriptsFileMap.clear();
		bmList.clear();
		fileSLS.clear();
		slns.clear();
		linkUrls.clear();
		results.clear();
		ids.clear();
		i = 0;
		IProject webProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("web");
		collectScreenFile(webProject);
		collectJSMaps();
		collectMatchs();

		for (Result r : results) {
			if (r.scriptMaps.size() == 0) {
				continue;
			}
			r.map = r.scriptMaps.get(0).getRoot();

			for (CompositeMap sm : r.scriptMaps) {
				List<LinkUrl> list = r.scriptsMapURL.get(sm);
				String source = sm.getText();
				int diff = 0;
				for (LinkUrl linkUrl : list) {
					String rpText = linkUrl.comment + getURL2(linkUrl, r, 0)
							+ ("".equals(linkUrl.urlLeftString) ? "" : "+")
							+ linkUrl.urlLeftString;
					int position = diff + linkUrl.sl.getAbsolutePosition();
					diff += rpText.length() - linkUrl.sl.getLength();
					source = source.substring(0, position)
							+ rpText
							+ source.substring(position
									+ linkUrl.sl.getLength());
				}
				sm.setText(source);
			}

			// System.out.println(r.map.toXML());
			genFile(r.file, r.map);
		}
		// System.out.println("================================");
		// System.out.println();
		// System.out.println(i);
		// for (LinkUrl url : linkUrls) {
		// System.out.println("============================");
		// System.out.println(url.sl);
		// System.out.println(url.screen);
		// System.out.println(url.isScreen);
		// System.out.println(url.model);
		// System.out.println(url.modelAction);
		// System.out.println(url.screenUrl);
		// System.out.println(url.linkFile);
		// System.out.println(url.urlLeftString);
		// System.out.println(url.comment);
		// System.out.println(url.scriptMap);
		// System.out.println();
		// }
	}

	private static void genFile(IFile oldFile, CompositeMap cm) {
		IFile newFileHandle = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("result").getFile(oldFile.getProjectRelativePath());
		String xml_decl = "<?xml version=\"1.0\" encoding=\"" + "UTF-8"
				+ "\"?>\n";
		InputStream is = new ByteArrayInputStream(
				(xml_decl + cm.toXML()).getBytes());
		System.out.println("生成文件：" + newFileHandle.getProjectRelativePath());
		CreateFileOperation op = new CreateFileOperation(newFileHandle, null,
				is, "Create New File");
		try {
			PlatformUI
					.getWorkbench()
					.getOperationSupport()
					.getOperationHistory()
					.execute(op, null,
							WorkspaceUndoUtil.getUIInfoAdapter(new Shell()));
		} catch (final ExecutionException e) {
			// handle exceptions
			e.printStackTrace();
		}
	}

	static private List<String> ids = new ArrayList<String>();

	private static String getID(LinkUrl linkUrl, Result r, int i) {
		Path p = new Path(linkUrl.linkFile.getName());
		String id = p.removeFileExtension().toString();
		id += "_link";
		if (i > 0)
			id = id + "_" + i;
		if (ids.contains(id)) {
			i++;
			return getID(linkUrl, r, i);
		}
		System.out.println(id);
		ids.add(id);
		return id;
	}

	private static String getURL2(LinkUrl linkUrl, Result r, int i) {
		CompositeMap view = r.map.getChild("view");
		if (linkUrl.screen.getName().equals(
				"exp_report_maintain_read_only.screen")) {
			System.out.println();
		}
		if (linkUrl.screen.getName().equals("exp_report_maintain.screen")) {
			System.out.println();
		}
		// view.getc
		if (linkUrl.isScreen) {
			Path requestPath = new Path("${/request/@context_path}/");
			IPath filePath = linkUrl.linkFile.getProjectRelativePath();
			CompositeMap link = view.getChildByAttrib("link", "url",
					requestPath.append(filePath).toString());
			if (link != null) {
				return "$('" + link.get("id") + "')" + ".getUrl()";
			} else {
				String id = getID(linkUrl, r, 0);
				link = creatLinkUrl(linkUrl, view, id);
				return "$('" + id + "')" + ".getUrl()";
			}
		} else {
			BMLinkIterationHandler bih = new GemLinkUrlTag().new BMLinkIterationHandler(
					linkUrl.model, linkUrl.modelAction);
			view.iterate(bih, false);
			if (bih.link != null) {
				return "$('" + bih.link.get("id") + "')" + ".getUrl()";
			} else {
				String id = getID(linkUrl, r, 0);
				creatLinkUrl(linkUrl, view, id);
				return "$('" + id + "')" + ".getUrl()";
			}
		}
	}

	private static String getURL(LinkUrl linkUrl, Result r, int i) {
		CompositeMap view = r.map.getChild("view");
		Path p = new Path(linkUrl.linkFile.getName());
		String id = p.removeFileExtension().toString();
		id += "_link";
		if (i > 0)
			id = id + "_" + i;

		CompositeMap link = view.getChildByAttrib("link", "id", id);
		if (link == null) {
			link = creatLinkUrl(linkUrl, view, id);
			return "$('" + id + "')" + ".getUrl()";
		} else if (linkUrl.isScreen
				&& linkUrl.screenUrl.equals(link.get("url"))) {
			return "$('" + id + "')" + ".getUrl()";
		} else if (linkUrl.isScreen) {
			// bgt_period_assign_com_batch.svc,bgt_period_assign_com_batch.screen
			i++;
			return getURL(linkUrl, r, i);
		} else if (linkUrl.model.equals(link.get("model"))
				&& linkUrl.modelAction.equals(link.get("modelaction"))) {
			return "$('" + id + "')" + ".getUrl()";
		}
		i++;
		return getURL(linkUrl, r, i);
		// <a:link id="c" model="a.b" modelaction="batch_update"/>
		// <a:link id="d" url="link_test.screen"/>
		// var cUrl = $('c');
		// cUrl.set('name','周皓');
		// cUrl.set('number','3067');
		// alert(cUrl.getUrl());
		//
		//
		// var dUrl = $('d');
		// dUrl.set('good','yes');
		// alert(dUrl.getUrl());
	}

	public static CompositeMap creatLinkUrl(LinkUrl linkUrl, CompositeMap view,
			String id) {
		CompositeMap link = new CompositeMap("link");
		link.setPrefix(view.getPrefix());
		if (linkUrl.isScreen) {
			// 绝对路径。
			// link.put("url", linkUrl.screenUrl);
			Path requestPath = new Path("${/request/@context_path}/");
			IPath filePath = linkUrl.linkFile.getProjectRelativePath();
			link.put("url", requestPath.append(filePath).toString());

		} else {
			link.put("model", linkUrl.model);
			link.put("modelaction", linkUrl.modelAction);
		}
		link.put("id", id);
		view.addChild(0, link);
		return link;
	}

	public static void collectMatchs() {
		Set<List<Javascript4Rhino>> keySet = scriptsFileMap.keySet();
		for (List<Javascript4Rhino> list : keySet) {
			IFile iFile = scriptsFileMap.get(list);
			GemLinkUrlTag.Result result = new GemLinkUrlTag().new Result();
			result.file = iFile;
			List<CompositeMap> scriptMaps = new ArrayList<CompositeMap>();
			Map<CompositeMap, List<LinkUrl>> scriptsMapURL = new HashMap<CompositeMap, List<LinkUrl>>();
			result.scriptMaps = scriptMaps;
			result.scriptsMapURL = scriptsMapURL;

			List<org.mozilla.javascript.ast.StringLiteral> _slns = new ArrayList<org.mozilla.javascript.ast.StringLiteral>();
			for (Javascript4Rhino j4r : list) {
				List<org.mozilla.javascript.ast.StringLiteral> __slns = j4r
						.getStringLiteralNodes(null);
				List<LinkUrl> __linkUrls = new LinkedList<LinkUrl>();
				for (org.mozilla.javascript.ast.StringLiteral sl : __slns) {
					boolean match = false;
					String pkg = j4r.getLiteralValue(sl);
					GemLinkUrlTag.LinkUrl url = null;
					IFile findScreenFile = Util.findScreenFile(iFile, pkg);
					if (findScreenFile != null
							&& ("screen".equalsIgnoreCase(findScreenFile
									.getFileExtension()) || "svc"
									.equalsIgnoreCase(findScreenFile
											.getFileExtension()))) {
						match = true;
						url = new GemLinkUrlTag().new LinkUrl();
						url.sl = sl;
						url.screen = iFile;
						url.isScreen = true;
						url.linkFile = findScreenFile;
						url.screenUrl = Util.findScreenUrl(iFile, pkg);
						url.urlLeftString = Util.getUrlLeftString(
								url.screenUrl, pkg);
						url.comment = Util.getUrlComment(url.screenUrl, pkg);
						url.scriptMap = j4r.getMap();
					} else {
						for (IFile bm : bmList) {
							boolean bmRefMatch = Util.bmRefMatch(
									Util.toBMPKG(bm), pkg);
							if (bmRefMatch) {
								match = true;
								url = new GemLinkUrlTag().new LinkUrl();
								url.sl = sl;
								url.screen = iFile;
								url.isScreen = false;
								url.linkFile = bm;
								url.model = Util.toBMPKG(bm);
								url.modelAction = Util.getBmAction(url.model,
										pkg);
								url.urlLeftString = Util.getUrlLeftString(
										url.model, pkg);
								url.comment = Util
										.getUrlComment(url.model, pkg);
								url.scriptMap = j4r.getMap();

								if (null == url.modelAction
										|| "".equals(url.modelAction)) {
									url = null;
								}

								break;
							}
						}
					}
					if (match) {
						slns.add(sl);
						_slns.add(sl);
						if (url != null) {
							linkUrls.add(url);
							__linkUrls.add(url);
						}
						System.out.println(linkUrls.size());
					}
				}
				if (__linkUrls.size() > 0) {
					scriptMaps.add(j4r.getMap());
					scriptsMapURL.put(j4r.getMap(), __linkUrls);
				}
			}
			fileSLS.put(iFile, _slns);
			results.add(result);
		}
	}

	public static void collectJSMaps() throws CoreException,
			ApplicationException {
		for (IFile s : screenList) {
			CompositeMap screenMap = (CompositeMap) CacheManager
					.getCompositeMap(s).clone();
			NamedMapFinder namedMapFinder = new NamedMapFinder("script");
			screenMap.iterate(namedMapFinder, false);
			List<MapFinderResult> result = namedMapFinder.getResult();
			List<Javascript4Rhino> j4rs = new ArrayList<Javascript4Rhino>();
			for (MapFinderResult r : result) {
				Javascript4Rhino j4r = new Javascript4Rhino(s, r.getMap());
				j4rs.add(j4r);
				// j4r.file = s;
			}
			if (result.size() > 0) {
				fileScriptsMap.put(s, j4rs);
				scriptsFileMap.put(j4rs, s);
			}
		}
	}

	public static void collectScreenFile(IProject webProject) {
		try {
			webProject.accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws CoreException {
					if ("screen".equalsIgnoreCase(resource.getFileExtension())
							|| "svc".equalsIgnoreCase(resource
									.getFileExtension())) {
						screenList.add((IFile) resource);
					}
					if ("bm".equalsIgnoreCase(resource.getFileExtension())) {
						bmList.add((IFile) resource);
					}

					return true;
				}
			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// static void run5() {
	// ISchemaManager schemaManager = LoadSchemaManager.getSchemaManager();
	//
	// Collection allTypes = schemaManager.getAllTypes();
	// for (Iterator iterator = allTypes.iterator(); iterator.hasNext();) {
	// Object object = (Object) iterator.next();
	// System.out.println(object.getClass());
	// if (object instanceof Element) {
	// String name = ((Element) object).getName();
	// if("bm:model".equals(name)){
	// Element model = (Element)object;
	// List allArrays = model.getAllArrays();
	// List allElements = model.getAllElements();
	// List allAttachedClasses = model.getAllAttachedClasses();
	// List allAttributes = model.getAllAttributes();
	// List allExtendedTypes = model.getAllExtendedTypes();
	// Set childs = model.getChilds();
	// Array[] arrays = model.getArrays();
	// Attribute[] attributes = model.getAttributes();
	// String category = model.getCategory();
	// Category categoryInstance = model.getCategoryInstance();
	// FeatureClass[] classes = model.getClasses();
	// String default1 = model.getDefault();
	// IValidator[] validators = model.getValidators();
	// String type = model.getType();
	// String displayMask = model.getDisplayMask();
	// String document = model.getDocument();
	// Element[] elements = model.getElements();
	// IType elementType = model.getElementType();
	// Extension[] extensions = model.getExtensions();
	// String localName = model.getLocalName();
	// String maxOccurs = model.getMaxOccurs();
	// String minOccurs = model.getMinOccurs();
	// String name2 = model.getName();
	// ISchemaObject parent = model.getParent();
	// QualifiedName qName = model.getQName();
	// String ref = model.getRef();
	// ISchemaObject refObject = model.getRefObject();
	// QualifiedName refQName = model.getRefQName();
	// ComplexType refType = model.getRefType();
	// Schema schema = model.getSchema();
	// ISchemaManager schemaManager2 = model.getSchemaManager();
	// System.out.println();
	// }
	// System.out.println(name);
	// }
	// if (object instanceof ComplexType) {
	// System.out.println(((ComplexType) object).getName());
	// }
	// if (object instanceof SimpleType) {
	// System.out.println(((SimpleType) object).getName());
	// }
	//
	//
	// }
	// System.out.println();
	//
	// }

	private void aa(String source) {
		Parser p = new Parser();
		AstRoot parse = p.parse(source, "ss", 1);
		a.d.d().println(parse);
	}

	private void run3() {
		Document document;
		try {
			d d = a.d.d();
			document = new Document(RunScript.getTestJS("test/a.js"));
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(document.get().toCharArray());

			// parser.setCompilerOptions(options)
			JavaScriptCore.getOptions();
			parser.setBindingsRecovery(true);
			// javaScriptCore

			JavaScriptUnit cu = (JavaScriptUnit) parser.createAST(null);

			cu.accept(new ASTVisitor() {

				@Override
				public void preVisit(ASTNode node) {
					// StringLiteral
					if (node instanceof StringLiteral) {
						node.getBodyChild();
						node.getLength();
						node.properties();
						node.structuralPropertiesForType();
						((StringLiteral) node).getEscapedValue();

						node.getStartPosition();
						node.getLength();
						a.d.d().print(node.getClass() + "      : ");
						a.d.d().println(
								((StringLiteral) node).getEscapedValue());
						a.d.d().println(
								((StringLiteral) node).getStartPosition());
						a.d.d().println(((StringLiteral) node).getLength());
						// node
						a.d.d().print("");
					}

				}

			});

			// cu.findDeclaringNode();
			// ASTNode findDeclaringNode =
			// cu.findDeclaringNode("queryFunction");
			// d.println(1);
			// d.println(findDeclaringNode);
			// cu.getAST();
			// d.println(2);
			// d.println(cu.getAST());
			// cu.getBodyChild();
			// d.println(3);
			// d.println(cu.getBodyChild());
			// cu.getCommentList();
			// d.println(4);
			// d.println(cu.getCommentList());
			// cu.getJavaElement();
			// d.println(5);
			// d.println(cu.getJavaElement());
			// cu.getLength();
			// d.println(6);
			// d.println(cu.getLength());
			// cu.getLocationInParent();
			// d.println(7);
			// d.println(cu.getLocationInParent());
			// cu.getMessages();
			// d.println(8);
			// d.println(cu.getMessages());
			// cu.getPackage();
			// d.println(9);
			// d.println(cu.getPackage());
			// cu.getProblems();
			// d.println(10);
			// d.println(cu.getProblems());
			// cu.getProperty("");
			// d.println(11);
			// d.println(cu.getProperty(""));
			// cu.getRoot();
			// d.println(12);
			// d.println(cu.getRoot());
			// cu.getTypeRoot();
			// d.println(13);
			// d.println(cu.getTypeRoot());
			// cu.properties();
			// d.println(14);
			// d.println(cu.properties());
			// cu.imports();
			// d.println(15);
			// d.println(cu.imports());
			// cu.resolveBinding();
			// d.println(16);
			// d.println(cu.resolveBinding());
			// cu.statements();
			// d.println(17);
			// d.println(cu.statements());
			// cu.types();
			// d.println(18);
			// d.println(cu.types());
			//
			cu.recordModifications();
			AST ast = cu.getAST();

			// ast
			// System.out.println(document.get());
			FunctionDeclaration id = ast.newFunctionDeclaration();
			id.setName(ast.newSimpleName("X2"));
			cu.statements().add(id); // add declaration at end
			TextEdit edits = cu.rewrite(document, null);
			try {
				UndoEdit undo = edits.apply(document);
				// System.out.println(document.get());
			} catch (MalformedTreeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private class ScopeVisitor implements IResourceVisitor {
		private List result = new ArrayList();

		public boolean visit(IResource resource) throws CoreException {
			if (resource.getType() == IResource.FILE) {
				boolean checkExtension = checkExtension(resource);
				if (checkExtension) {
					result.add(resource);
				}
				return false;
			}
			return true;
		}

		public List getResult() {
			return result;
		}

		private boolean checkExtension(IResource resource) {
			IFile file = (IFile) resource;
			String fileExtension = file.getFileExtension();
			return "bm".equalsIgnoreCase(fileExtension)
					|| "screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension);
		}
	}

	private void run2() {

		IFile x = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("hr_aurora").getFile("/web/WEB-INF/classes/a.bm");
		IFile b = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("hr_aurora").getFile("b.bm");
		int rtimes = 0;
		String s = "";
		for (int t = 0; t < rtimes; t++) {
			a.d.count();

			compositeMap.put(s + t, x);
		}

		List mm = new ArrayList();

		// CompositeMap bm = loadFromResource(x);
		int times = 6;
		for (int z = 0; z < times; z++) {
			d d = a.d.d();
			d.b("第 " + z + " 次");
			for (CompositeMap map : maps) {
				if (map != null) {
					a.d.count();

					CompositeMap clone = (CompositeMap) map.clone();
					clone.iterate(new IterationHandle() {

						@Override
						public int process(CompositeMap map) {

							return IterationHandle.IT_CONTINUE;
						}

					}, true);
					System.out.println(clone.toString());
					System.out.println(compositeMap.size());
					mm.add(clone);

				}

			}

			d.e("第 " + z + " 次");
		}

	}

	private void run1() {
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("hr_aurora").getFile("/web/WEB-INF/classes/a.bm");

		CompositeMap bm = loadFromResource(file);
		// CompositeLoader.createInstanceForOCM().
		BusinessModel r = createResult(bm);
		r.getObjectContext().toXML();

		// r.get
		System.out.println(r);
	}

	public static CompositeMap loadFromResource(IResource file) {
		if (file == null || !file.exists()) {
			return null;
		}
		String fullLocationPath = file.getLocation().toOSString();
		CompositeLoader cl = CompositeLoader.createInstanceForOCM();

		cl.setSaveNamespaceMapping(true);
		CompositeMap bmData = null;
		try {
			bmData = cl.loadByFile(fullLocationPath);
		} catch (IOException e) {

		} catch (SAXException e) {

		}
		return bmData;
	}

	private static BusinessModel createResult(CompositeMap config) {

		ModelFactory factory = new ModelFactory(OCManager.getInstance());
		return factory.getModel(config);
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
		ScopeVisitor visitor = new ScopeVisitor();
		try {

			ResourcesPlugin.getWorkspace().getRoot().accept(visitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		List files = visitor.getResult();

		for (int i = 0; i < files.size(); i++) {
			// a.d.count();
			// maps.add(loadFromResource((IFile) files.get(i)));
		}
	}
}