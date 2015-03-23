package aurora.ide.search.condition;

import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.LineElement;

public class AttributeCondition extends SearchCondition {

	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public SearchCondition read(IDialogSettings s) {
		this.setCaseSensitive(s.getBoolean("isCaseSensitive"));
		this.setName(s.get("name"));
		this.setValue(s.get("value"));
		return this;
	}

	@Override
	public void store(IDialogSettings s) {
		s.put("isCaseSensitive", this.isCaseSensitive());
		if (name != null)
			s.put("name", name);
		if (value != null)
			s.put("value", value);
	}

	@Override
	public boolean isEquals(SearchCondition condition) {
		if (condition instanceof AttributeCondition) {
			String _name = name == null ? "" : name;
			String _value = value == null ? "" : value;
			return _name.equals(((AttributeCondition) condition).name)
					&& _value.equals(((AttributeCondition) condition).value);
		}
		return super.isEquals(condition);
	}

	public MatchInfo match(CompositeMap map, IDocument document, LineElement l) {
		boolean nameMatch = !"".equals(name) && null != name;
		boolean valueMatch = !"".equals(value) && null != value;
		if (nameMatch && valueMatch) {
			return allMatch(map, document, l);
		} else if (nameMatch) {
			return nameMatch(map, document, l);
		} else if (valueMatch) {
			return valueMatch(map, document, l);
		}
		return MatchInfo.Not_Match;
	}

	private MatchInfo allMatch(CompositeMap map, IDocument document,
			LineElement l) {
		List mapAttributes = Util.getMapAttributes(map);
		MatchInfo info = new MatchInfo();
		info.setMap(map);
		if (mapAttributes != null) {
			FindReplaceDocumentAdapter dd = new FindReplaceDocumentAdapter(
					document);
			for (Iterator it = mapAttributes.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
//				String _value = map.getString(attrib.getName());
				String _value = CompositeMapUtil.getValueIgnoreCase(attrib, map);
				if (null == _value) {
					continue;
				}
				boolean attribMatch = Util.stringMatch(name, attrib.getName(),
						this.isCaseSensitive(), this.isRegularExpression());
				boolean valueMatch = Util.stringMatch(value, _value,
						this.isCaseSensitive(), this.isRegularExpression());
				if (attribMatch && valueMatch) {
					try {
						IRegion documentRegion = Util.getDocumentRegion(
								l.getOffset(), l.getLength(), attrib.getName(),
								document, IColorConstants.ATTRIBUTE);
						if (documentRegion == null) {
							continue;
						}
						IRegion valueRegion = Util.getValueRegion(
								documentRegion.getOffset(),
								l.getLength() - documentRegion.getOffset()
										+ l.getOffset(), _value, document,
								IColorConstants.STRING);
						// if (_value.contains("$")) {
						// valueRegion = dd.find(
						// documentRegion.getOffset(), _value, true,
						// true, true, false);
						// }
						// IRegion valueRegion = dd.find(
						// documentRegion.getOffset(), _value, true, true,
						// true, false);

						if (valueRegion != null) {
							info.addRegion(valueRegion);
							info.addRegion(documentRegion);
						}
					} catch (BadLocationException e) {
					}
				}
			}
		}
		if (info.getRegions().size() > 0) {
			return info;
		}
		return MatchInfo.Not_Match;
	}

	private MatchInfo nameMatch(CompositeMap map, IDocument document,
			LineElement l) {
		List mapAttributes = Util.getMapAttributes(map);
		MatchInfo info = new MatchInfo();
		info.setMap(map);
		if (mapAttributes != null) {
			FindReplaceDocumentAdapter dd = new FindReplaceDocumentAdapter(
					document);
			for (Iterator it = mapAttributes.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
//				String _value = map.getString(attrib.getName());
				String _value = CompositeMapUtil.getValueIgnoreCase(attrib, map);
				if (null == _value) {
					continue;
				}
				boolean attribMatch = Util.stringMatch(name, attrib.getName(),
						this.isCaseSensitive(), this.isRegularExpression());
				if (attribMatch) {
					try {
						IRegion documentRegion = Util.getDocumentRegion(
								l.getOffset(), l.getLength(), attrib.getName(),
								document, IColorConstants.ATTRIBUTE);
						if (documentRegion != null) {
							info.addRegion(documentRegion);
						}

					} catch (BadLocationException e) {
					}
				}
			}
		}
		if (info.getRegions().size() > 0) {
			return info;
		}
		return MatchInfo.Not_Match;
	}

	private MatchInfo valueMatch(CompositeMap map, IDocument document,
			LineElement l) {
		List mapAttributes = Util.getMapAttributes(map);
		MatchInfo info = new MatchInfo();
		info.setMap(map);
		if (mapAttributes != null) {
			FindReplaceDocumentAdapter dd = new FindReplaceDocumentAdapter(
					document);
			for (Iterator it = mapAttributes.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
//				String _value = map.getString(attrib.getName());
				String _value = CompositeMapUtil.getValueIgnoreCase(attrib, map);
				if (null == _value) {
					continue;
				}
				boolean valueMatch = Util.stringMatch(value, _value,
						this.isCaseSensitive(), this.isRegularExpression());
				if (valueMatch) {
					try {
						IRegion documentRegion = Util.getDocumentRegion(
								l.getOffset(), l.getLength(), attrib.getName(),
								document, IColorConstants.ATTRIBUTE);
						if (documentRegion == null) {
							continue;
						}
						IRegion valueRegion = Util.getValueRegion(
								documentRegion.getOffset(),
								l.getLength() - documentRegion.getOffset()
										+ l.getOffset(), _value, document,
								IColorConstants.STRING);
						// IRegion valueRegion = dd.find(
						// documentRegion.getOffset(), _value, true, true,
						// true, false);
						if (valueRegion != null) {
							info.addRegion(valueRegion);
						}
					} catch (BadLocationException e) {
					}
				}
			}
		}
		if (info.getRegions().size() > 0) {
			return info;
		}
		return MatchInfo.Not_Match;
	}

}
