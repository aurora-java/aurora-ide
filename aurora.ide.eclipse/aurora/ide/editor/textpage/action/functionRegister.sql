WHENEVER SQLERROR EXIT FAILURE ROLLBACK;
WHENEVER OSERROR  EXIT FAILURE ROLLBACK;

spool INSTALL_SERVICE_REGISTER.log

set feedback off
set define off
begin

--页面注册

#PAGE_REGISTER#

--功能定义

#MODULE_REGISTER#


--分配页面

#LOAD_SERVICE#

--分配BM

#REGISTER_BM#


end;
/

commit;
set feedback on
set define on

spool off

exit
