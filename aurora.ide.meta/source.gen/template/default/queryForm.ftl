 <a:queryForm ${properties("bindTarget","defaultQueryField","defaultQueryHint","resultTarget")}  style="width:100%;">
   <a:formToolBar>
             ${action("form_toolbar_children")}
   </a:formToolBar>
   <#if context.hasBody=="true">
    <a:formBody cellpadding="10" ${properties("column","labelWidth")} style="width:100%">
		 ${action("form_body_children")}
   </a:formBody>
    </#if>
</a:queryForm>

