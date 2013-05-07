<a:grid  ${properties("bindTarget","prompt","width","height","navBar","navBarType")}>
 <#if (context.toolbar)??>

	 <#if (context.toolbar.component_children)??>
    	 <a:toolBar id="toolbar">
        	 <#list context.toolbar.component_children as b>
        	 	<#if b.type??>
    				<a:button type="${b.type}"/>
        	    </#if>
    		 </#list>
         </a:toolBar>	
      </#if>
      </#if>
     	<a:columns>
				${action('columns')}
        </a:columns>
     <#if (context.editors.components)??>
     	<a:editors>
     	<#list context.editors.components as e>
        	 	<#if e.id??>
    				<a:${e.editor_type} id="${e.id}"/>
        	    </#if>
    	</#list>
    </a:editors>
    </#if>  
	
</a:grid>