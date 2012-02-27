
x='aa';d='aa';
var ssssss;
vvvvvvvv = 'aa';
openAssignPage();
a.queryFunction();
//dajiahao
/*wo shi zhu shi*/
            function queryFunction(){
                $('sys_function_result_ds').query()
            }
            function resetFunction(){
            	$('sys_function_query_ds').reset()
            }
            function assignPageRenderer(value,record, name){
                var sal = record.get('function_type');
                if(sal == 'F'){
                   return '<a href="javascript:openAssignPage('+record.get('function_id')+')">${l:ACP_SYS_ACP_REQ_TYPES.ASSIGN_PAGE}</a>';
                }else{
                    return '';
                }
            }
            
            function openAssignPage(id){
                new Aurora.Window({id:'sys_function_service_assign_window', url:'sys_function_service.screen?function_id='+ id, title:'${l:ACP_SYS_ACP_REQ_TYPES.ASSIGN_PAGE}', height:435,width:620});
            }
            
            function assignBmRenderer(value,record, name){
                var sal = record.get('function_type');
                if(sal == 'F'){
                   return '<a href="javascript:openAssignBM('+record.get('function_id')+')">${l:HEC_ASSIGN}BM</a>';
                }else{
                    return '';
                }
            }
            
            function openAssignBM(id){
                new Aurora.Window({id:'sys_function_bm_window', url:'sys_function_bm.screen?function_id='+ id, title:'${l:HEC_ASSIGN}BM', height:435,width:620});
            }
            
            function parentFunctionRenderer(value,record, name){
                if(value){
                    return value;
                }else{
                    return '<font color="red">${l:ROOT}</font>';
                }
            }
            
            function commandLineEditor(record){
                var sal = record.get('function_type');
                if(sal == 'F'){
                    return "sys_function_result_grid_lv";
                }else{
                    return "";
                }
            }
            
            function onUpdate(ds,record,name,value){
                if(name=='function_type'){
                    if(value == 'G'){
                        record.set('command_line',''); 
                    }
                }
            } 
            
            function viewFunctionTree(){
                new Aurora.Window({id:'sys_function_tree_window', url:'sys_function_tree.screen', title:'${l:FUNCTION_TREE}', height:400,width:300});
            }
            function setrequired(grid,row,name,record){
            	
                if (record.get('function_type')=='F'){
                	//fff
                	record.getMeta().getField('service_name').setRequired(true);
                }
                
            }
           function canedit(record,name){
                if (!record.isNew){
                    return '';
                }else{
                    return 'sys_function_result_grid_tf';
                }
           }
           function doChange(com,value,oldValue){
           		var record=com.record;
           		if(value=='功能'){record.getMeta().getField('service_name').setRequired(true); 
           		}
           			 
           		else{record.getMeta().getField('service_name').setRequired(false);    
           		}
           			       		
           }