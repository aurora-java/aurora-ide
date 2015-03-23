WHENEVER SQLERROR EXIT FAILURE ROLLBACK;
WHENEVER OSERROR  EXIT FAILURE ROLLBACK;

spool INSTALL_PROMPTS_REGISTER.log

set feedback off
set define off
begin

--PROMPTS注册

<#if (prompts)??>
   <#list prompts as p>
	sys_prompt_pkg.delete_prompt('${p.code}');
	sys_prompt_pkg.sys_prompts_load('${p.code}', 'ZHS', '${p.zhs}');
	sys_prompt_pkg.sys_prompts_load('${p.code}', 'US', '${p.us}');
   </#list>
</#if>

end;
/

commit;
set feedback on
set define on

spool off

exit
