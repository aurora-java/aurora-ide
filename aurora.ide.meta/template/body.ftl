<#macro container body>
			<#list body.components as c>
        		<#if c.isLayout==true>
        			<#if c.isBox==true>
            			<a:${c.name} ${properties(c.u_id)}>
            				<@container body=c/>
            			</a:${c.name}>	
        			</#if>
        			
        			<#if c.name=="grid">
            			<a:${c.name} ${properties(c.u_id)} >
	                		<#if (c.toolbar.components)??>
    	           			    <a:toolBar id="toolbar">
        	        				<#list c.toolbar.components as b>
    									<a:button type="${b.type}"/>
    								</#list>
                				</a:toolBar>	
                			</#if>
            				<#if c.columns??>
            				<a:columns>
                    			${columns(c.columns.u_id)}
                			</a:columns>
            				</#if>
                			<#if c.editors??>
	                			<a:editors>
    	              			 	<#list c.editors as e>
    									<a:${e.name} id="${e.id}"/>
    								</#list>
        	        			</a:editors>
                			</#if>
            			</a:${c.name}>	
        			</#if>
        			<#if c.name=="tabPanel">
        				<a:tabPanel ${properties(c.u_id)}>
                			<a:tabs>
                				<#list c.tabs as t>
                				   <#if t.ref??>
                					 <a:tab ${properties(t.u_id)}/>
                				   <#else>
		                  			   <a:tab ${properties(t.u_id)}>
				            				<@container body=t/>
    								   </a:tab>
                				   </#if>
    							</#list>
        					</a:tabs>
        				</a:tabPanel>
        			</#if>
        		</#if>
        		<#if c.isLayout==false>
        			<a:${c.name} ${properties(c.u_id)}/>
        		</#if>
        	</#list>	
</#macro>