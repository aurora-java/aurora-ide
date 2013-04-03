package aurora.plugin.source.gen.screen.model.properties;

public interface ComponentProperties {
	// ==DEFAURLT COMPONENT==

	// document="组件的ID,在同一个screen中ID不能重复"/>
	String id = "id";
	// document="组件的标签" type="bm:Prompts"/>
	String prompt = "prompt";
	// document="组件是否隐藏" type="ss:string"/>
	String hidden = "hidden";
	// document="组件的宽度,单位是像素(px)" type="ss:int"/>
	String width = "width";
	// document="组件的高度,单位是像素(px)" type="ss:int"/>
	String height = "height";
	// document="组件的边缘宽度,单位是像素(px)" type="ss:int"/>
	String marginWidth = "marginWidth";
	// document="组件的边缘高度,单位是像素(px)" type="ss:int"/>
	String marginHeight = "marginHeight";
	// document="组件name,对应dataset中field的name"/>
	String name = "name";
	// document="组件的style"/>
	String style = "style";
	// document="组件的样式"/>
	String className = "className";
	// document="需要绑定的DataSet的ID" type="a:DataSetReference"/>
	String bindTarget = "bindTarget";
	// document="是否可编辑" type="a:boolean"/>
	String editable = "editable";

	String COMPONENT_PROPERTIES[] = { id, prompt, hidden, width, height,
			marginWidth, marginHeight, name, style, className, bindTarget,
			editable };

	// ==a:textField==

	String emptyText = "emptyText";
	// document="描述字段"/>
	String typeCase = "typeCase";
	// document="大小写类型" type="a:caseType"/>
	String readOnly = "readOnly";
	// document="是否只读" type="ss:boolean"/>
	String maxLength = "maxLength";
	// document="最大允许输入长度" type="ss:int"/>
	String restrict = "restrict";
	// document="输入限制(正则表达式)"/>
	String restrictInfo = "restrictInfo";
	// document="输入限制提示信息"/>

	String TEXTFIELD_PROPERTIES[] = { emptyText, typeCase, readOnly, maxLength,
			restrict, restrictInfo, id, prompt, hidden, width, height,
			marginWidth, marginHeight, name, style, className, bindTarget,
			editable };

	// ==BUTTON==
	// document="按钮的文本" type="bm:Prompts" />
	String text = "text";
	// document="按钮的点击事件" type="ss:string"/>
	String click = "click";
	// document="按钮的图片"/>
	String icon = "icon";
	// document="按钮的提示信息" type="bm:Prompts"/>
	String title = "title";
	// default="true" document="按钮的可用状态" type="ss:boolean"/>
	String disabled = "disabled";
	// document="预定义功能类型\nadd|save|delete|clear"/>
	String type = "type";
	// document="button样式对应的class" type="ss:String"/>
	String btnClass = "btnClass";
	// document="button的样式" type="ss:String"/>
	String btnStyle = "btnStyle";
	String BUTTON_PROPERTIES[] = { text, click, icon, title, disabled, type,
			btnClass, btnStyle, id, prompt, hidden, width, height, marginWidth,
			marginHeight, name, style, className, bindTarget, editable };

	// <ss:element name="a:comboBox">

	String valueField = "valueField";
	// document="ComboBox的valueField"/>
	String displayField = "displayField";
	// document="ComboBox的displayfield"/>
	String options = "options";
	// document="ComboBox的数据源"/>
	// String emptyText = "emptyText";
	// document="描述字段"/>
	// String readOnly = "readOnly";
	// document="是否只读" type="ss:boolean"/>
	String COMBOBOX_PROPERTIES[] = { valueField, displayField, options,
			emptyText, readOnly, id, prompt, hidden, width, height,
			marginWidth, marginHeight, name, style, className, bindTarget,
			editable };

	// <ss:element name="a:numberField" document="数字输入框">

	// String emptyText = "emptyText";
	// document="描述字段"/>
	String allowDecimals = "allowDecimals";
	// document="是否允许有小数位" type="ss:boolean"/>
	String decimalPrecision = "decimalPrecision";
	// document="小数位精度" type="ss:int"/>
	String allowNegative = "allowNegative";
	// document="是否允许为负数" type="ss:boolean"/>
	String allowFormat = "allowFormat";
	// document="是否按照千分位显示" type="ss:boolean"/>
	// String readOnly = "readOnly";
	// document="是否只读" type="ss:boolean"/>
	String NUMBERFIELD_PROPERTIES[] = { emptyText, allowDecimals,
			decimalPrecision, allowNegative, allowFormat, readOnly, id, prompt,
			hidden, width, height, marginWidth, marginHeight, name, style,
			className, bindTarget, editable };

	// <ss:element name="a:label" document="显示标签">

	String renderer = "renderer";
	// document="渲染\n回调函数function(value,record,name){return value}\n返回值value是html字符串"/>

	String LABEL_PROPERTIES[] = { renderer, id, prompt, hidden, width, height,
			marginWidth, marginHeight, name, style, className, bindTarget,
			editable };
	// <ss:element name="a:datePicker" document="日期选择框">
	String dayRenderer = "dayRenderer";
	// document="日期渲染\n回调函数function(cell,date,text){return text}\n当cell.disabled=true时，该日期无法被选择\n返回值text是html字符串"/>
	// String emptyText="emptyText" document="提示信息"/>
	// String readOnly="readOnly" document="是否只读" type="ss:boolean"/>
	String enableBesideDays = "enableBesideDays";
	// document="在本月的始末补齐前后月份的日期" type="a:showType"/>
	String enableMonthBtn = "enableMonthBtn";
	// document="月份选择按钮显示方式" type="a:showType"/>
	String viewSize = "viewSize";
	// document="日历显示个数,4为最大值" type="ss:int"/>
	// String renderer="renderer" document="渲染\n回调函数"/>
	String DATEPICKER_PROPERTIES[] = { dayRenderer, emptyText, readOnly,
			enableBesideDays, enableMonthBtn, viewSize, renderer, id, prompt,
			hidden, width, height, marginWidth, marginHeight, name, style,
			className, bindTarget, editable };

	// <ss:element name="a:dateTimePicker" document="日期时间选择控件,带有时分秒输入框">
	String hour = "hour";
	// document="默认显示的小时" default="0" type="ss:int"/>
	String minute = "minute";
	// document="默认显示的分钟" default="0" type="ss:int"/>
	String second = "second";
	// document="默认显示的秒钟" default="0" type="ss:int"/>

	String DATEPTIMEICKER_PROPERTIES[] = { hour, minute, second, dayRenderer,
			emptyText, readOnly, enableBesideDays, enableMonthBtn, viewSize,
			renderer, id, prompt, hidden, width, height, marginWidth,
			marginHeight, name, style, className, bindTarget, editable };

	// <ss:element name="a:checkBox">
	String checkedValue = "checkedValue";// document="checkbox选中的值"/>
	String uncheckedValue = "uncheckedValue";// document="checkbox未选中的值"/>
	String label = "label";
	// document="checkBox的描述"/>

	String CHECKBOX_PROPERTIES[] = { checkedValue, uncheckedValue, label, id,
			prompt, hidden, width, height, marginWidth, marginHeight, name,
			style, className, bindTarget, editable };

	// <ss:element name="a:lov" document="lov组件">

	// String emptyText="emptyText" document="描述字段"/>
	String lovService = "lovService";// document="Lov对应的model"/>
	String lovWidth = "lovWidth";// document="lov弹出窗口的宽度" type="ss:int"/>
	String lovHeight = "lovHeight";// document="lov弹出窗口的高度" type="ss:int"/>
	String lovGridHeight = "lovGridHeight";// document="Lov窗口中grid的高度"
											// type="ss:int"/>
	String lovLabelWidth = "lovLabelWidth";// document="lov弹出窗口查询条件字段描述的宽度"
											// type="ss:int"/>
	String lovAutoQuery = "lovAutoQuery";// default="true"
											// document="Lov窗口是否自动查询"
											// type="ss:boolean"/>
	String lovUrl = "lovUrl";// document="自定义URL"/>
	// String title="title" document="Lov弹出窗口的title" type="bm:Prompts"/>
	String autoCompleteRenderer = "autoCompleteRenderer";// document="autoComplete的渲染函数"/>
	String LOV_PROPERTIES[] = { emptyText, lovService, lovWidth, lovHeight,
			lovGridHeight, lovLabelWidth, lovAutoQuery, lovUrl, title,
			autoCompleteRenderer, id, prompt, hidden, width, height,
			marginWidth, marginHeight, name, style, className, bindTarget,
			editable };

	// <ss:element name="a:grid">
	String navBar = "navBar";// document="是否具有导航条" type="ss:boolean"/>
	String navBarType = "navBarType";// default="complex" document="导航条类型"
										// type="a:navBarType"/>
	String maxPageCount = "maxPageCount";// default="10"
											// document="导航条的类型simple时，最大可显示的页数，如果总页数超过该值，会以省略号显示。"
											// type="ss:int"/>
	String autoFocus = "autoFocus";// default="false" document="自动对焦"
									// type="ss:boolean"/>
	String autoAppend = "autoAppend";// default="true" document="自动新增行"
										// type="ss:boolean"/>
	String showRowNumber = "showRowNumber";// document="是否显示行号"
											// type="ss:boolean"/>
	String canWheel = "canWheel";// default="true" document="能否通过鼠标滚动来换行"
									// type="ss:boolean"/>
	String canPaste = "canPaste";// default="false"
									// document="是否允許使用粘贴功能来增加整行，该功能只有在IE下才有效。"
									// type="ss:boolean"/>
	String rowRenderer = "rowRenderer";// document="通过回调函数返回的样式表渲染指定行\n回调函数function(record,rowIndex){return css}\n返回值css值可以是class或者style字符串,也可以是class和style字符串数组"/>

	String GRID_PROPERTIES[] = { rowRenderer, canPaste, canWheel,
			showRowNumber, autoAppend, autoFocus, maxPageCount, navBarType,
			navBar, id, prompt, hidden, width, height, marginWidth,
			marginHeight, name, style, className, bindTarget, editable };

	// <ss:element name="a:column">
	// String name="id" document="列ID,主要用于个性化中"/>
	// St//ring name="name" document="列的field的name"/>
	// Stri//ng name="prompt" document="列名" type="bm:Prompts"/>
	// String name="hidden" default="false" document="隐藏列" type="ss:boolean"/>
	// String name="width" document="列的宽度" type="ss:int"/>
	String sortable = "sortable";// default="false" document="是否可按照次字段排序"
									// type="ss:boolean"/>
	String lock = "lock";// default="false" document="是否锁定" type="ss:boolean"/>
	String align = "align";// document="排列方式" type="a:alignType"/>
	String resizable = "resizable";// default="true" document="是否可调整宽度"
									// type="ss:boolean"/>
	// String renderer="renderer"
	// document="列渲染\n回调函数function(value,record,name){return value}\n返回值value是html字符串"/>
	String editor = "editor";// document="列的编辑器,对应editors中的id"/>
	String editorFunction = "editorFunction";// document="列编辑器函数,可动态改变编辑器\n回调函数function(record,name){return editorid}\n返回值editorid是编辑器的id"
												// editor=""/>
	String footerRenderer = "footerRenderer";// document="列脚注渲染\n回调函数function(data,name){return value}\n参数data是所绑定的dataset中的数据,返回值value是html字符串"/>
	String percentWidth = "percentWidth";// document="table节点专用。列的百分比宽度,单位是(%)"
											// type="ss:int"/>
	String forExport = "forExport";// default="true" document="是否对该列进行导出"
									// type="ss:boolean"/>
	String autoAdjust = "autoAdjust";// default="true" document="是否自动调整列宽"
										// type="ss:boolean"/>
	String maxAdjustWidth = "maxAdjustWidth";// default="300"
												// document="列宽自动调整的最大值，最大值不会超过grid的宽度"
												// type="ss:int"/>

	String COLUMN_PROPERTIES[] = { id, prompt, name, hidden, width, sortable,
			lock, align, resizable, renderer, editor, editorFunction,
			footerRenderer, percentWidth, forExport, autoAdjust, maxAdjustWidth };

	// <ss:element name="a:toolBar">
	// <ss:elements>
	// <ss:element ref="a:BaseViewComponent"/>
	// </ss:elements>
	String TOOLBAR_PROPERTIES[] = { name };
	// <ss:element name="a:tabPanel" document="TabPanel组件">
	// <ss:extensions>
	// <ss:extension base="a:Component"/>
	// </ss:extensions>
	String TABPANEL_PROPERTIES[] = { id, prompt, hidden, width, height,
			marginWidth, marginHeight, name, style, className, bindTarget,
			editable };

	// <ss:element name="a:tab" document="TabPanel的tab页">
	// String id="id";// document="组件的ID,在同一个screen中ID不能重复"/>
	// String bindTarget="bindTarget" ;//document="需要绑定的DataSet的ID"/>
	// String prompt="prompt";// document="tab的prompt" use="required"
	// type="bm:Prompts"/>
	// String width="width";// document="组件的宽度,单位是像素(px)" type="ss:int"/>
	// String height="height";// document="组件的高度,单位是像素(px)" type="ss:int"/>
	// String marginWidth="marginWidth" ;//document="组件的边缘宽度,单位是像素(px)"
	// type="ss:int"/>
	// String marginHeight="marginHeight" ;//document="组件的边缘高度,单位是像素(px)"
	// type="ss:int"/>
	String ref = "ref";// document="tab的引用screen" type="a:URLReference"/>
	String tabClassName = "tabClassName";// document="tab的className"/>
	String tabStyle = "tabStyle";// document="tab的style"/>
	String selected = "selected";// document="是否选中(默认选中第一个tab)"
									// type="ss:boolean"/>
	String bodyClassName = "bodyClassName";// document="tab的body的className"/>
	String bodyStyle = "bodyStyle";// document="tab的body的style"/>
	String closeable = "closeable";// default="false" document="标签是否可关闭"
									// type="ss:boolean"/>
	// String disabled="disabled" ;//default="false" document="标签是否不可用"
	// type="ss:boolean"/>
	String TAB_PROPERTIES[] = { id, bindTarget, prompt, width, height,
			marginWidth, marginHeight, ref, tabClassName, tabStyle, selected,
			bodyClassName, bodyStyle, closeable, disabled, name };

	// <ss:element name="a:dataSet" wizard="a:dataSetWizard">
	// String name="id" document="DataSet的ID" type="a:DataSetReference"
	// use="required"/>
	String model = "model";// document="DataSet关联的BM" type="bm:ModelReference"/>
	String loadData = "loadData";// default="false" document="是否服务端加载数据"
									// type="ss:boolean"/>
	String autoCreate = "autoCreate";// default="false" document="是否创建一条数据"
										// type="ss:boolean"/>
	String autoQuery = "autoQuery";// default="false" document="是否客户端自动发起ajax查询"
									// type="ss:boolean"/>
	String autoCount = "autoCount";// default="false" document="查询是否进行记录数统计"
									// type="ss:boolean"/>
	String fetchAll = "fetchAll";// default="false" document="是否查询所有数据"
									// editor="" type="ss:boolean"/>
	String canQuery = "canQuery";// default="true" document="是否可查询" editor=""
									// type="ss:boolean"/>
	String canSubmit = "canSubmit";// default="true" document="是否可以提交"
									// type="ss:boolean"/>
	String lookupCode = "lookupCode";// document="值列表的CODE"/>
	String autoPageSize = "autoPageSize";// default="false"
											// document="根据Grid的高度，自适应分页大小。注：只在grid初次加载时生效，如果Grid设定了marginHeigt，行数不会根据窗口大小的调整而调整。"
											// type="ss:boolean"/>
	String maxPageSize = "maxPageSize";// default="1000"
										// document="查询的分页大小的最大值，用于限制navBar中的分页输入框的输入上限"
										// type="ss:int"/>
	String pageSize = "pageSize";// default="10" document="查询的分页大小"
									// type="ss:int"/>
	String queryUrl = "queryUrl";// document="查询的URL地址" type="a:URLReference"/>
	String submitUrl = "submitUrl";// document="提交的url地址"
									// type="a:URLReference"/>
	String selectable = "selectable";// default="false" document="是否可选择"
										// type="ss:boolean"/>
	String selectionModel = "selectionModel";// document="选择模式"
												// type="a:selectType"/>
	String queryDataSet = "queryDataSet";// document="查询的DataSet的id"
											// type="a:DataSetReference"/>
	// String bindTarget="bindTarget";// document="该Dataset所级联的父级DataSet"
	// type="a:DataSetReference"/>
	String bindName = "bindName";// document="绑定父级DataSet中的field名" type=""/>
	String selectFunction = "selectFunction";// document="用回调函数判断行是否可选择\n 回调函数function(record){return true|false}\n 当返回值为false时，该record无法被选择"
												// type=""/>
	String notification = "notification";// document="未保存提示信息,dataset未保存离开页面给予提示"/>
	String DATASET_PROPERTIES[] = { id, model, loadData, autoCreate, autoQuery,
			autoCount, fetchAll, canQuery, canSubmit, lookupCode, autoPageSize,
			maxPageSize, pageSize, queryUrl, submitUrl, selectable,
			selectionModel, queryDataSet, bindTarget, bindName, selectFunction,
			notification };
	// <ss:element name="a:link">
	// String name="id" document="唯一标志" type=""/>
	// String model = "model";// document="bm文件的pkg路径"
	// type="bm:ModelReference"/>
	String modelaction = "modelaction";// document="对应bm的action" type=""/>
	String url = "url";// document="screen文件的路径" type="a:URLReference"/>
	String LINK_PROPERTIES[] = { id, model, modelaction, url };

	// <ss:element name="a:field" document="DataSet的field">
	// String name="name" document="field的name" type=""/>
	String required = "required";// default="false" document="是否必输"
									// type="ss:boolean"/>
	String requiredMessage = "requiredMessage";// document="必输提示信息"
												// type="ss:String"/>
	// String readOnly="readOnly";// default="false" document="是否只读"
	// type="ss:boolean"/>
	String returnField = "returnField";// document="ComboBox选中值的返回name"/>
	// String options="options" ;//document="ComboBox的options,对应DataSet的id"
	// type="a:DataSetReference"/>
	// String displayField="displayField";// document="ComboBox的displayField"/>
	// String valueField="valueField";// document="ComboBox的valueField"/>
	// String prompt="prompt";// document="field的prompt" type="bm:Prompts"/>
	// String title="title";// document="Lov弹出窗口的title" type="bm:Prompts"/>
	// String lovService="lovService" document="Lov对应的model"
	// type="bm:ModelReference"/>
	// String lovWidth="lovWidth" document="lov弹出窗口的宽度" type="ss:int"/>
	// String lovLabelWidth="lovLabelWidth" document="lov弹出窗口查询条件字段描述的宽度"
	// type="ss:int"/>
	// String lovHeight="lovHeight" document="lov弹出窗口的高度" type="ss:int"/>
	// String lovGridHeight="lovGridHeight" document="Lov窗口中grid的高度"
	// type="ss:int"/>
	// String lovAutoQuery="lovAutoQuery" default="true" document="Lov窗口是否自动查询"
	// type="ss:boolean"/>
	String defaultValue = "defaultValue";// document="默认值"/>
	// String checkedValue="checkedValue" document="checkbox选中的值"/>
	// String uncheckedValue="uncheckedValue" document="checkbox未选中的值"/>
	// String lovUrl="lovUrl" document="自定义URL" type="a:URLReference"/>
	String autoComplete = "autoComplete";// default="false"
											// document="是否开启lov的autoComplete功能"
											// type="ss:boolean"/>
	String autoCompleteField = "autoCompleteField";// document="autoComplete的查询字段，如果没有设定该值则默认取通过mapping映射本组件绑定字段的lov查询字段。\n例如：&lt;a:field name=&apos;value&apos;&gt;\n&lt;a:mapping&gt;\n&lt;a:map from=&apos;code&apos; to=&apos;value&apos;&gt;&lt;/a:map&gt;\n&lt;/a:mapping&gt;&lt;/a:field&gt;\n中code就是默认autoCompleteField。"/>
	String autoCompleteSize = "autoCompleteSize";// document="autoComplete触发最小字符数"
													// default="2"
													// type="ss:int"/>
	String autoCompletePageSize = "autoCompletePageSize";// document="autoComplete分页大小"
															// default="10"
															// type="ss:int"/>
	String validator = "validator";// document="自定义校验函数\n函数参数为 function(record,name,value)\n返回值:\n(1)校验成功返回true\n(2)校验失败返回错误的描述信息(文本格式)"/>
	String datatype = "datatype";// document="field的数据类型" type="ss:String"/>
	// String typeCase="typeCase" document="textField中输入大小写类型"
	// type="ss:String"/>
	String fetchRemote = "fetchRemote";// document="lov是否通过输入内容自动查询返回给文本框"
										// type="ss:boolean"/>
	// String type="type" document="chart的类型,line或者pie" type="a:chartType"/>
	String tooltip = "tooltip";// document="该field对应的提示信息"/>

	String DS_FIELD_PROPERTIES[] = { name, required, requiredMessage, readOnly,
			returnField, options, displayField, valueField, prompt, title,
			lovService, lovWidth, lovLabelWidth, lovGridHeight, lovHeight,
			lovAutoQuery, defaultValue, checkedValue, uncheckedValue, lovUrl,
			autoComplete, autoCompleteField, autoCompleteSize,
			autoCompletePageSize, validator, datatype, typeCase, fetchRemote,
			type, };
	// <ss:element name="a:map" document="lov或者combobox和当前dataSet的映射关系">
	String from = "from";// document="映射关系从,一般对应lov或者combobox的dataset的field"
							// type="bm:ForeignFieldReference"/>
	String to = "to";// document="映射关系到,对应当前dataset的field"/>

	// <ss:element name="a:box">
	String row = "row";// document="行数" type="ss:int"/>
	String column = "column";// document="列数" type="ss:int"/>
	String cellPadding = "cellPadding";// document="table的cellPadding值"
										// type="ss:int"/>
	String cellSpacing = "cellSpacing";// document="table的cellspacing值"
										// type="ss:int"/>
	String padding = "padding";// document="单元格的padding值" type="ss:int"/>
	// String name="prompt" document="box的prompt" type="bm:Prompts"/>
	String labelWidth = "labelWidth";// document="prompt的宽度" type="ss:int"/>
	String labelSeparator = "labelSeparator";// default=":"
												// document="prompt后面的符号"
												// type="ss:String"/>
	String showBorder = "showBorder";// document="是否显示边框" type="ss:boolean"/>
	String wrapperAdjust = "wrapperAdjust";// default="true"
											// document="当控件设置了width时，cell宽度自适应控件"
											// type="ss:boolean"/>

	String BOX_PROPERTIES[] = { wrapperAdjust, showBorder, labelSeparator,
			labelWidth, padding, cellSpacing, cellPadding, column, row, id,
			prompt, hidden, width, height, marginWidth, marginHeight, name,
			style, className, bindTarget, editable };

	// <ss:element name="a:vBox" document="竖向box">
	String VBOX_PROPERTIES[] = { wrapperAdjust, showBorder, labelSeparator,
			labelWidth, padding, cellSpacing, cellPadding, column, row, id,
			prompt, hidden, width, height, marginWidth, marginHeight, name,
			style, className, bindTarget, editable };

	// <ss:element name="a:hBox" document="横向box">
	String HBOX_PROPERTIES[] = { wrapperAdjust, showBorder, labelSeparator,
			labelWidth, padding, cellSpacing, cellPadding, column, row, id,
			prompt, hidden, width, height, marginWidth, marginHeight, name,
			style, className, bindTarget, editable };

	// <ss:element name="a:form" document="form布局">
	// String title="title" ;//document="form的标题" type="bm:Prompts"/>
	String showmargin = "showmargin";// default="false" document="是否显示form的上下间隙"
										// type="ss:boolean"/>
	String FORM_PROPERTIES[] = { title, showmargin, wrapperAdjust, showBorder,
			labelSeparator, labelWidth, padding, cellSpacing, cellPadding,
			column, row, id, prompt, hidden, width, height, marginWidth,
			marginHeight, name, style, className, bindTarget, editable };
	// <ss:element name="a:fieldSet">
	String FIELDSET_PROPERTIES[] = { title, showmargin, wrapperAdjust,
			showBorder, labelSeparator, labelWidth, padding, cellSpacing,
			cellPadding, column, row, id, prompt, hidden, width, height,
			marginWidth, marginHeight, name, style, className, bindTarget,
			editable };
}
