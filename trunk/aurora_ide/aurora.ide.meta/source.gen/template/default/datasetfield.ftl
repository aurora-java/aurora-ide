<#if context.field_type ??>
<#if context.field_type == "comboBox">
	<a:field name="${context.for_display_field}" returnField="${context.for_return_field}"  ${properties("required","readOnly","prompt")} ${properties("options","displayField","valueField")}>
	<#if context.mappings ??>
	<a:mapping>
	<#list context.mappings.components as m>
    	<a:map from="${m.mapping_from}" to="${m.mapping_to}"/>
    </#list>
    </a:mapping>
	</#if>
	</a:field>
	<a:field name="${context.for_return_field}"/>
</#if>
<#if context.field_type == "lov">
	<a:field  name="${context.for_display_field}"   ${properties("required","readOnly","prompt")}  ${properties("lovService","lovWidth","lovLabelWidth","lovHeight","lovGridHeight","lovAutoQuery")}>
    		<a:mapping>
    			<#if context.valueField??>
	            	<a:map from="${context.valueField}" to="${context.for_return_field}"/>
	            	</#if>
	            	<#if context.displayField??>
	            	<a:map from="${context.displayField}" to="${context.for_display_field}"/>
	            	</#if>
	            	<#if (context.mappings.components)??>
	            	<#list context.mappings.components as m>
    					<a:map from="${m.mapping_from}" to="${m.mapping_to}"/>
    				</#list>
	            	</#if>
            </a:mapping>
    </a:field>
    <a:field name="${context.for_return_field}"/>
</#if>
<#if context.field_type == "checkBox" >
	<a:field   name="${context.field_name}"  ${properties("required","readOnly","prompt","defaultValue","checkedValue","uncheckedValue")} />
</#if>
                		<#if context.field_type == "datePicker">
                			<a:field name="${context.field_name}"  ${properties("required","readOnly","prompt")} />
                		</#if>
                		<#if context.field_type == "numberField">
                			<a:field name="${context.field_name}"  ${properties("required","readOnly","prompt")} />
                		</#if>
                		<#if context.field_type == "textField">
                			<a:field name="${context.field_name}"  ${properties("required","readOnly","prompt")}  />
                		</#if>
                		<#if context.field_type == "textArea">
                			<a:field name="${context.field_name}"  ${properties("required","readOnly","prompt")}  />
                		</#if>
                		<#if context.field_type == "dateTimePicker">
                			<a:field name="${context.field_name}" ${properties("required","readOnly","prompt")}   />
                		</#if>
                		<#if context.field_type == "label">
                			<a:field name="${context.field_name}"  ${properties("required","readOnly","prompt")}   />
                		</#if>
</#if>