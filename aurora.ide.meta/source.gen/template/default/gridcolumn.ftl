<#if context.hasChildren =="false">
<#if context.editor_type == "comboBox">
	<a:column name="${context.for_display_field}"  ${properties("prompt","width","renderer","editor","footerRenderer")} />
</#if>
<#if context.editor_type == "lov">
	<a:column name="${context.for_display_field}" ${properties("prompt","width","renderer","editor","footerRenderer")} />
</#if>
<#if context.editor_type == "checkBox" >
<a:column  ${properties("name","prompt","width","renderer","editor","footerRenderer")} />
</#if>
<#if context.editor_type == "datePicker">
<a:column  ${properties("name","prompt","width","renderer","editor","footerRenderer")} />
</#if>
<#if context.editor_type == "numberField">
<a:column  ${properties("name","prompt","width","renderer","editor","footerRenderer")} />
</#if>
<#if context.editor_type == "textField">
<a:column  ${properties("name","prompt","width","renderer","editor","footerRenderer")} />
</#if>
<#if context.editor_type == "dateTimePicker">
<a:column  ${properties("name","prompt","width","renderer","editor","footerRenderer")} />
</#if>
<#if context.editor_type == "label">
<a:column  ${properties("name","prompt","width","renderer","editor","footerRenderer")} />
</#if>

<#if context.editor_type == "">
<a:column  ${properties("name","prompt","width","renderer","editor","footerRenderer")} />
</#if> 	
</#if>

<#if context.hasChildren =="true">
<a:column ${properties("name","prompt","width","renderer")}>
	${action("children")}
</a:column>
</#if>
