	<#if context.ds_type=="combodataset">
		<#if context.lookupCode!="">
			<a:dataSet  id="${context.ds_id}"  ${properties("lookupCode")}/>
		</#if>
		<#if context.model!="">
			<a:dataSet id="${context.ds_id}" loadData="true" fetchAll='true' ${properties("model")}/>
		</#if>
    </#if>
	<#if context.ds_type=="querydataset">
		<a:dataSet autoCreate="true"  id="${context.ds_id}" >
			<a:fields>
				${action("datasetfields")}
			</a:fields>
		</a:dataSet>
    </#if>
    <#if context.ds_type=="resultdataset">
    
    
    
    ${action("build_head_ds")}
    
    <#if context.is_head_ds == 'true'>
			<a:dataSet id="${context.ds_id}"  ${properties("model","autoCreate","selectable","queryDataSet","bindName","bindTarget","selectionModel")}
			<#if context.need_master_detail_submit_url == 'true'>
			 <#if context.need_auto_query_url == 'false'>
			 submitUrl='${r"$"}{/request/@context_path}/master_detail_auto_save.svc?head_model=${context.model}&amp;line_model=${context.line_model}&amp;bind_name=${context.binded_name}'
			 </#if>
			 <#if context.need_auto_query_url == 'true'>
			 submitUrl='${r"$"}{/request/@context_path}/master_detail_auto_save.svc?head_model=${context.model}&amp;line_model=${context.line_model}&amp;bind_name=${context.binded_name}&amp;para_name=${config.para_name}&amp;para_value=${r"$"}{/parameter/@${config.para_value}}'
			 </#if>
			</#if>
			<#if context.need_auto_query_url == 'true'>
			 autoQuery="true" 
			 queryUrl='${r"$"}{/request/@context_path}/autocrud/${context.model}/query?${config.para_name}=${r"$"}{/parameter/@${config.para_value}}'
			</#if>   
			  >
		</#if>
	<#if context.is_head_ds != 'true'>
		<#if context.bindTarget != ''>
			<a:dataSet id="${context.ds_id}" fetchAll="true"   ${properties("model","selectable","bindName","queryDataSet","queryUrl","bindTarget","selectionModel")} >
     	<#else>
			<a:dataSet id="${context.ds_id}" ${properties("model","queryDataSet","selectable","selectionModel")} >
     	</#if>
	</#if>
    	<a:fields>
     		${action("datasetfields")}
     	</a:fields>
     	  </a:dataSet>
    </#if> 
