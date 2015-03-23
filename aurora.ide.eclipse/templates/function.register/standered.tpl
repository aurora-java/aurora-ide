WHENEVER SQLERROR EXIT FAILURE ROLLBACK;
WHENEVER OSERROR  EXIT FAILURE ROLLBACK;

spool INSTALL_SERVICE_REGISTER.log

set feedback off
set define off
begin

--页面注册

<#if (pages)??>
   <#list pages as s>
	sys_service_pkg.sys_service_load('${s.page_path}','${s.page_name}',1,1,0);
   </#list>
</#if>

--模块定义

<#if (module)??>
	sys_function_pkg.sys_function_load('${module.module_code}','${module.module_name}','','G','','','','ZHS');
</#if>

--功能定义

<#if (function)??>
	sys_function_pkg.sys_function_load('${function.function_code}','${function.function_name}','${module.module_code}','F','${function.host_path}','${function.function_order}','','ZHS');
</#if>


--分配页面
<#if (pages)??>
	<#list pages as p>
	sys_function_service_pkg.load_service('${function.host_path}','${p.page_path}');
	</#list>
</#if>

--分配BM

<#if (models)??>
	<#list models as m>
	sys_register_bm_pkg.register_bm('${function.host_path}','${m.bm_path}');
	</#list>
</#if>


end;
/

commit;
set feedback on
set define on

spool off

exit
