<#include "body.ftl"/>
<?xml version="1.0" encoding="${config.encoding}"?>
<!-- 
  $Author: ${config.author}
  $Date: ${config.date}
  $Revision: ${config.revision}
  $${config.copyright}
-->
<a:screen xmlns:a="http://www.aurora-framework.org/application">
	<#if (screen[initProcedure])??>
	<a:init-procedure>
		<#if (screen[initProcedure][modelQuerys])??>
			<#list (screen[initProcedure][modelQuerys]) as m>
		        <a:model-query ${properties(m.u_id)}/>
			</#list>
		</#if>
    </a:init-procedure>
	</#if>
    <a:view>
		<style><![CDATA[.item-label {HEIGHT: 22px; line-height: 22px; vertical-align: middle; text-decoration:underline; }]]></style>
        <#if (screen.view.links)??>
    		<#list screen.view.links as l>
    			<a:link url="${l.url}"/>
    		</#list>
    	</#if>
        <#if (screen.view.script)??>
        	<script><![CDATA[${screen.view.script.cdata}]]></script>
        </#if>
		<#if (screen.view.datasets)??>
        <a:dataSets>
        	<#list screen.view.datasets as d>
        	<#if d.haschild == true>
            	<a:dataSet  ${properties(d.u_id)}>
					<#if (d.fields)??>
                	 <a:fields>
                		<#list d.fields as f>
                			<#if f.haschild == true>
    						<a:field  ${properties(f.u_id)}>
								<#if (f.mapping)??>
    								<a:mapping>
                        				<#list f.mapping as m>
	                            			<a:map from="${m.from}" to="${m.to}"/>
                            			</#list>
                        			</a:mapping>
                        		</#if>
    						</a:field>
    						<#else>
    						<a:field  ${properties(f.u_id)}/>
    						</#if>
    					</#list>
               		 </a:fields>
					</#if>
            	</a:dataSet>
            <#else>
				<a:dataSet  ${properties(d.u_id)}/>
        	</#if>
    		</#list>
        </a:dataSets>
        </#if>
        <a:screenBody>
        	<@container body=screen.view.screenbody/>
        </a:screenBody>
    </a:view>
</a:screen>