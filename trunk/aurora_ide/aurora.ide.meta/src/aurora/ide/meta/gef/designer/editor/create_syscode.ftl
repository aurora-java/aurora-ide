begin
  sys_code_pkg.delete_sys_code('${code}');
  sys_code_pkg.insert_sys_code('${code}','${codename!"code_name"}','${codeprompt!"code_prompt"}','${codenameprompt!"code_name_prompt"}','ZHS');
  sys_code_pkg.update_sys_code('${code}','${codename!"code_name"}','${codeprompt!"code_prompt"}','${codenameprompt!"code_name_prompt"}','US');
<#list values as v>
  sys_code_pkg.insert_sys_code_value('${code}','${v.value}','${v.zhs}','ZHS','');
  sys_code_pkg.update_sys_code_value('${code}','${v.value}','${v.us}','US','');
</#list>
end;
