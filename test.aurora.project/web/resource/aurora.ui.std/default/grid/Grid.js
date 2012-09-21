/**
 * @class Aurora.Grid
 * @extends Aurora.Component
 * <p>Grid 数据表格组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Grid = Ext.extend($A.Component,{
    bgc:'background-color',
    scor:'#dfeaf5',
    ocor:'#ffe3a8',
    cecls:'cell-editor',
    nbcls:'item-notBlank',
    constructor: function(config){
        this.overId = null;
        this.selectedId = null;
        this.lockWidth = 0;
        this.autofocus = config.autofocus||true;
        $A.Grid.superclass.constructor.call(this,config);
    },
    initComponent:function(config){
        $A.Grid.superclass.initComponent.call(this, config);
        var wp = this.wrap;
        this.wb = Ext.get(this.id+'_wrap');
        this.fb = this.wb.child('div[atype=grid.fb]');
        if(this.fb){
            this.uf = this.fb.child('div[atype=grid.uf]');
        }
        this.uc = wp.child('div[atype=grid.uc]');
        this.uh = wp.child('div[atype=grid.uh]');
        this.ub = wp.child('div[atype=grid.ub]'); 
        this.uht = wp.child('table[atype=grid.uht]'); 
        
        this.lc = wp.child('div[atype=grid.lc]'); 
        this.lh = wp.child('div[atype=grid.lh]');
        this.lb = wp.child('div[atype=grid.lb]');
        this.lht = wp.child('table[atype=grid.lht]'); 

        this.sp = wp.child('div[atype=grid.spliter]');
        Ext.getBody().insertFirst(this.sp)
        this.fs = wp.child('a[atype=grid.focus]');
        
        this.lockColumns =[],this.unlockColumns = [];
        this.lockWidth = 0,this.unlockWidth = 0;
        for(var i=0,l=this.columns.length;i<l;i++){
            var c = this.columns[i];
            if(c.lock === true){
                this.lockColumns.add(c);
                if(c.hidden !== true) this.lockWidth += c.width;
            }else{
                this.unlockColumns.add(c);
                if(c.hidden !== true) this.unlockWidth += c.width;
            }
        }
        this.columns = this.lockColumns.concat(this.unlockColumns);
        this.initTemplate();
    },
    processListener: function(ou){
    	$A.Grid.superclass.processListener.call(this, ou);
        this.wrap[ou]("mouseover", this.onMouseOver, this);
        this.wrap[ou]("mouseout", this.onMouseOut, this);
        this.wrap[ou]('click',this.focus,this);
        if(!(this.canwheel === false)){
        	this.wb[ou]('mousewheel',this.onMouseWheel,this);
        }
        this.fs[ou](Ext.isOpera ? "keypress" : "keydown", this.handleKeyDown,  this);
        this.ub[ou]('scroll',this.syncScroll, this);
        this.ub[ou]('click',this.onClick, this);
        this.ub[ou]('dblclick',this.onDblclick, this);
        this.uht[ou]('mousemove',this.onUnLockHeadMove, this);
        this.uh[ou]('mousedown', this.onHeadMouseDown,this);
        this.uh[ou]('click', this.onHeadClick,this);
        if(this.lb){
            this.lb[ou]('click',this.onClick, this);
            this.lb[ou]('dblclick',this.onDblclick, this);
        }
        if(this.lht) this.lht[ou]('mousemove',this.onLockHeadMove, this);
        if(this.lh) this.lh[ou]('mousedown', this.onHeadMouseDown,this);
        if(this.lh) this.lh[ou]('click', this.onHeadClick,this);
        this[ou]('cellclick',this.onCellClick,this);
    },
    initEvents:function(){
        $A.Grid.superclass.initEvents.call(this);
        this.addEvents(
        /**
         * @event render
         * grid渲染出数据后触发该事件
         * @param {Aurora.Grid} grid 当前Grid组件.
         */
        'render',
        /**
         * @event keydown
         * 键盘按下事件.
         * @param {Aurora.Grid} grid 当前Grid组件.
         * @param {EventObject} e 鼠标事件对象.
         */
        'keydown',
        /**
         * @event dblclick
         * 鼠标双击事件.
         * @param {Aurora.Grid} grid 当前Grid组件.
         * @param {Aurora.Record} record 鼠标双击所在行的Record对象.
         * @param {Number} row 行号.
         * @param {String} 当前name.
         */
        'dblclick',
        /**
         * @event cellclick
         * 单元格点击事件.
         * @param {Aurora.Grid} grid 当前Grid组件.
         * @param {Number} row 行号.
         * @param {String} 当前name.
         * @param {Aurora.Record} record 鼠标点击所在单元格的Record对象.
         */
        'cellclick',
        /**
         * @event rowclick
         * 行点击事件.
         * @param {Aurora.Grid} grid 当前Grid组件.
         * @param {Number} row 行号.
         * @param {Aurora.Record} record 鼠标点击所在行的Record对象.
         */
        'rowclick',
        /**
         * @event editorShow
         * 编辑器显示后触发的事件.
         * @param {Aurora.Grid} grid 当前Grid组件.
         * @param {Editor} grid 当前Editor组件.
         * @param {Number} row 行号.
         * @param {String} 当前name.
         * @param {Aurora.Record} record 鼠标点击所在行的Record对象.
         */
        'editorshow',
        /**
         * @event nexteditorshow
         * 切换下一个编辑器的事件.
         * @param {Aurora.Grid} grid 当前Grid组件.
         * @param {Number} row 行号.
         * @param {String} 当前name.
         */
        'nexteditorshow');
    },
    syncScroll : function(){
        this.hideEditor();
        this.uh.dom.scrollLeft = this.ub.dom.scrollLeft;
        if(this.lb) this.lb.dom.scrollTop = this.ub.dom.scrollTop;
        if(this.uf) this.uf.dom.scrollLeft = this.ub.dom.scrollLeft;
    },
    handleKeyDown : function(e){
        var key = e.getKey();
        if(e.ctrlKey&&e.keyCode == 86&&this.canpaste){
            var text = window.clipboardData.getData('text');
            if(text){
                var columns = this.columns;
                var rows = text.split('\n');
                for(var i=0,l=rows.length;i<l;i++){
                    var row = rows[i];
                    var values = row.split('\t');
                    if(values=='')continue;
                    var data = {}; 
                    for(var j=0,v=0,k=this.columns.length;j<k;j++){
                        var c = this.columns[j];
                        if(this.isFunctionCol(c)) continue;
                        if(c.hidden !== true) {
                            data[c.name] = values[v];
                            v++
                        }
                    }
                    this.dataset.create(data);
                }
            }
        }else{
            if(key == 38 || key == 40 || key == 33 || key == 34) {
                if(this.dataset.loading == true) return;
                var row;
                switch(e.getKey()){
                    case 33:
                        this.dataset.prePage();
                        break;
                    case 34:
                        this.dataset.nextPage();
                        break;
                    case 38:
                        this.dataset.pre();
                        break;
                    case 40:
                        this.dataset.next();
                        break;
                }
            }
        }
        this.fireEvent('keydown', this, e)
    },
    processDataSetLiestener: function(ou){
        var ds = this.dataset;
        if(ds){
        	ds[ou]('ajaxfailed', this.onAjaxFailed, this);
            ds[ou]('metachange', this.onRefresh, this);
            ds[ou]('update', this.onUpdate, this);
            ds[ou]('reject', this.onUpdate, this);
            ds[ou]('add', this.onAdd, this);
            ds[ou]('submit', this.onBeforSubmit, this);
            ds[ou]('submitfailed', this.onAfterSuccess, this);
            ds[ou]('submitsuccess', this.onAfterSuccess, this);
            ds[ou]('query', this.onBeforeLoad, this);
            ds[ou]('load', this.onLoad, this);
            ds[ou]('loadfailed', this.onAjaxFailed, this);
            ds[ou]('valid', this.onValid, this);
            ds[ou]('beforeremove', this.onBeforeRemove, this); 
            ds[ou]('remove', this.onRemove, this);
            ds[ou]('clear', this.onLoad, this);
            ds[ou]('refresh',this.onRefresh,this);
            ds[ou]('fieldchange', this.onFieldChange, this);
            ds[ou]('indexchange', this.onIndexChange, this);
            ds[ou]('select', this.onSelect, this);
            ds[ou]('unselect', this.onUnSelect, this);
            ds[ou]('selectall', this.onSelectAll, this);
            ds[ou]('unselectall', this.onUnSelectAll, this);
        }
    },
    bind : function(ds){
        if(typeof(ds)==='string'){
            ds = $(ds);
            if(!ds) return;
        }
        var sf = this;
        sf.dataset = ds;
        if(ds.autopagesize===true){
        	ds.pagesize=Math.round(((this.ub.getHeight()||parseFloat(this.ub.dom.style.height))-16)/25);
        }
        sf.processDataSetLiestener('on');
        this.onLoad();
//        Ext.onReady(function(){
//            sf.onLoad();
//        })
    },
    initTemplate : function(){
        this.rowTdTpl = new Ext.Template('<td atype="{atype}" class="grid-rowbox" recordid="{recordid}">');
        this.rowNumTdTpl = new Ext.Template('<td style="text-align:{align}" class="grid-rownumber" atype="grid-rownumber" recordid="{recordid}">');
        this.rowNumCellTpl = new Ext.Template('<div style="width:{width}px">{text}</div>');
        this.tdTpl = new Ext.Template('<td style="visibility:{visibility};text-align:{align}" dataindex="{name}" atype="grid-cell" recordid="{recordid}">');
        this.cellTpl = new Ext.Template('<div class="grid-cell {cellcls}" style="width:{width}px" id="'+this.id+'_{name}_{recordid}" title="{title}"><span>{text}</span></div>');        
        this.cbTpl = new Ext.Template('<center><div class="{cellcls}" id="'+this.id+'_{name}_{recordid}"></div></center>');
    },
    getCheckBoxStatus: function(record, name ,readonly) {
        var field = this.dataset.getField(name)
        var cv = field.getPropertity('checkedvalue');
        var uv = field.getPropertity('uncheckedvalue');
        var value = record.data[name];
        return 'item-ckb-'+(readonly?'readonly-':'')+((value && value == cv) ? 'c' : 'u');
    },
    createTemplateData : function(col,record){
    	return {
            width:col.width-2,
            recordid:record.id,
            visibility: col.hidden === true ? 'hidden' : 'visible',
            name:col.name
        }
    },
    createCell : function(col,record,includTd){
        var data = this.createTemplateData(col,record)
        var cellTpl;
        var tdTpl = this.tdTpl;
        var cls = ''; //col.editor ? this.cecls : 
        var xtype = col.type;
        var readonly;
        var editor = this.getEditor(col,record);
        if(editor!=''){
        	var edi = $A.CmpManager.get(editor);
        	//这里对于checkbox可能会存在问题
            if(edi && (edi instanceof $A.CheckBox)){
                xtype = 'cellcheck';
                cls = '';
            }else{
                cls = this.cecls;
            }
        }else if(col.name && Ext.isDefined(record.getField(col.name).get('checkedvalue'))){
    		xtype = 'cellcheck';
    		readonly=true;
        }
        if(xtype == 'rowcheck'||xtype == 'rowradio'){
        	if(!this.dataset.execSelectFunction(record))readonly="-readonly";
        	else readonly="";
            tdTpl = this.rowTdTpl;
            data = Ext.apply(data,{
                align:'center',
                atype:xtype == 'rowcheck'?'grid.rowcheck':'grid.rowradio',
                cellcls: xtype == 'rowcheck'?'grid-ckb item-ckb'+readonly+'-u':'grid-radio item-radio-img'+readonly+'-u'
            })
            cellTpl =  this.cbTpl;
        }else if(xtype == 'cellcheck'){
            data = Ext.apply(data,{
                align:'center',
                cellcls: 'grid-ckb ' + this.getCheckBoxStatus(record, col.name ,readonly) //+((cls=='') ? ' disabled ' : '' )
            })
            cellTpl =  this.cbTpl;
        }else{
            
            var field = record.getMeta().getField(col.name);
            if(field && Ext.isEmpty(record.data[col.name]) && record.isNew == true && field.get('required') == true){
                cls = cls + ' ' + this.nbcls
            }
            var sp = (cls.indexOf(this.cecls)!=-1) ? 5 : 2;
            var t = this.renderText(record,col,record.data[col.name])
            data = Ext.apply(data,{
                align:col.align||'left',
                cellcls: cls,
//                width:col.width-4,//-11
                width:data.width-sp,//-11
                text:t,
                title:String(t).replace(/<[^<>]*>/mg,'')
            })
            cellTpl =  this.cellTpl;
            if(xtype == 'rownumber') {
                tdTpl = this.rowNumTdTpl;
                cellTpl = this.rowNumCellTpl;
            }
        }
        var sb = [];
        if(includTd)sb.add(tdTpl.applyTemplate(data));
        sb.add(cellTpl.applyTemplate(data));
        if(includTd)sb.add('</td>')
        return sb.join('');
    },
    createRow : function(type, row, cols, item){
        var sb = [];
        var css=this.parseCss(this.renderRow(item,row));
        sb.add('<tr id="'+this.id+'$'+type+'-'+item.id+'" class="'+(row % 2==0 ? '' : 'row-alt')+css.cls+'"'+'style="'+css.style+'">');
        for(var i=0,l=cols.length;i<l;i++){
            var c = cols[i];
            sb.add(this.createCell(c,item, true))           
        }
        sb.add('</tr>');
        return sb.join('');
    },
    parseCss:function(css){
    	var style="",cls="";
    	if(Ext.isArray(css)){
    		for(var i=0;i<css.length;i++){
    			var _css=this.parseCss(css[i]);
    			style+=";"+_css.style;
    			cls+=" "+_css.cls;
    		}
    	}else if(typeof css=="string"){
    		isStyle=!!css.match(/^([^,:;]+:[^:;]+;)*[^,:;]+:[^:;]+;*$/);
    		cls=isStyle?"":css;
			style=isStyle?css:"";
    	}
    	return {style:style,cls:cls}
    	
    },
    renderText : function(record,col,value){        
    	var renderer = col.renderer;
        if(renderer){//&&!Ext.isEmpty(value)  去掉对value是否为空的判断
            var rder = $A.getRenderer(renderer);
            if(rder == null){
                alert("未找到"+renderer+"方法!")
                return value;
            }
            value = rder.call(window,value,record, col.name);
            return value == null ? '' : value;
        }
        return value == null ? '' : value;
    },
    renderRow : function(record,rowIndex){
    	var renderer = this.rowrenderer,css=null;
        if(renderer){
            var rder = $A.getRenderer(renderer);
            if(rder == null){
                alert("未找到"+renderer+"方法!")
                return css;
            }
            css = rder.call(window,record, rowIndex);
            return !css? '' : css;
        }
        return css ;
    },
    createTH : function(cols){
        var sb = [];
        sb.add('<tr class="grid-hl">');
        for(var i=0,l=cols.length;i<l;i++){
            var w = cols[i].width;
            if(cols[i].hidden === true) w = 0;
            sb.add('<th dataindex="'+cols[i].name+'" style="height:0px;width:'+w+'px"></th>');
        }
        sb.add('</tr>');
        return sb.join('');
    },
    onBeforeRemove : function(){
        $A.Masker.mask(this.wb,_lang['grid.mask.remove']);
    },
    onBeforeLoad : function(){
    	this.ub.scrollTo('left',0);
    	this.uh.scrollTo('left',0);
        $A.Masker.mask(this.wb,_lang['grid.mask.loading']);
    },
    onBeforSubmit : function(ds){
    	$A.Masker.mask(this.wb,_lang['grid.mask.submit']);
    },
    onAfterSuccess : function(){
        $A.Masker.unmask(this.wb);
    },
    preLoad : function(){},
    onLoad : function(){
    	this.wrap.removeClass('grid-select-all');
    	this.isSelectAll = false;
    	this.clearDomRef();
    	this.preLoad();
        var cb = Ext.fly(this.wrap).child('div[atype=grid.headcheck]');
        if(this.selectable && this.selectionmodel=='multiple')this.setCheckBoxStatus(cb,false);
        if(this.lb)
        this.renderLockArea();
        this.renderUnLockAread();
//        if(focus !== false) this.focus.defer(10,this);//获取数据后的获得焦点,会引起其他编辑器无法编辑
        this.drawFootBar();
        $A.Masker.unmask(this.wb);
        this.fireEvent('render',this)
    },
    clearDomRef : function(){
    	this.selectlockTr = null;
        this.selectUnlockTr = null;
    },
    onAjaxFailed : function(res,opt){
        $A.Masker.unmask(this.wb);
    },
    onMouseWheel : function(e){
        e.stopEvent();
        if(this.editing == true) return;
    	var delta = e.getWheelDelta(),sf=this;
        if(delta > 0){
            sf.dataset.pre();
        } else if(delta < 0){
            sf.dataset.next();
        }
    },
    focus: function(){    	
        this.fs.focus();
    },
    renderLockArea : function(){
        var sb = [];var cols = this.lockColumns;
//        var v = 0;
//        var columns = this.lockColumns;
//        for(var i=0,l=columns.length;i<l;i++){
//            cols.add(columns[i]);
//            if(columns[i].hidden !== true) v += columns[i].width;            
//        }
//        this.lockWidth = v;
        sb.add('<TABLE cellSpacing="0" atype="grid.lbt" cellPadding="0" border="0"  width="'+this.lockWidth+'"><TBODY>');
        sb.add(this.createTH(cols));
        for(var i=0,l=this.dataset.data.length;i<l;i++){
            sb.add(this.createRow('l', i, cols, this.dataset.getAt(i)));
        }
        sb.add('</TBODY></TABLE>');
        sb.add('<DIV style="height:17px"></DIV>');
        this.lb.update(sb.join(''));
        this.lbt = this.lb.child('table[atype=grid.lbt]'); 
    },
    renderUnLockAread : function(){
        var sb = [];var cols = this.unlockColumns;
//        var v = 0;
//        var columns = this.unlockColumns;
//        for(var i=0,l=columns.length;i<l;i++){
//            cols.add(columns[i]);
//            if(columns[i].hidden !== true) v += columns[i].width;            
//        }
        sb.add('<TABLE cellSpacing="0" atype="grid.ubt" cellPadding="0" border="0" width="'+this.unlockWidth+'"><TBODY>');
        sb.add(this.createTH(cols));
        for(var i=0,l=this.dataset.data.length;i<l;i++){
            sb.add(this.createRow('u', i, cols, this.dataset.getAt(i)));
        }
        sb.add('</TBODY></TABLE>');
        this.ub.update(sb.join(''));
        this.ubt = this.ub.child('table[atype=grid.ubt]'); 
    },
    isOverSplitLine : function(x){
        var v = 0;      
        var isOver = false;
        this.overColIndex = -1;
        var columns = this.columns;
        for(var i=0,l=columns.length;i<l;i++){
            var c = columns[i];
            if(c.hidden !== true) v += c.width;
            if(x < v+3 && x > v-3 && c.resizable != false){
                isOver = true;
                this.overColIndex = i;
                break;
            }
        }
        return isOver;
    },
    onRefresh : function(){
        this.onLoad(false);
        for(var i=0;i<this.dataset.selected.length;i++){
            this.onSelect(this.dataset, this.dataset.selected[i]);
        }
    },
    onIndexChange:function(ds, r){
        var index = this.getDataIndex(r.id);
        if(index == -1)return;
        //if(r != this.selectRecord){
            this.selectRow(index, false);
        //}
    },
    isFunctionCol: function(c){
        return c.type == 'rowcheck' || c.type == 'rowradio'
    },
    onAdd : function(ds,record,index){
        if(this.lb)
        var sb = [];var cols = [];
        var v = 0;
        var columns = this.columns;
        var row = this.dataset.data.length-1;
        var css = this.parseCss(this.renderRow(record,row));
        if(this.lbt){
            var ltr = document.createElement("tr");
            ltr.id=this.id+'$l'+'-'+record.id;
            ltr.className=(row % 2==0 ? '' : 'row-alt')+' '+css.cls;
            Ext.fly(ltr).set({'style':css.style});
            for(var i=0,l=columns.length;i<l;i++){
                var col = columns[i];
                if(col.lock === true){
                    var td = document.createElement("td");
                    if(col.type == 'rowcheck') {
                        Ext.fly(td).set({'recordid':record.id,'atype':'grid.rowcheck'})
                        td.className = 'grid-rowbox';
                        if(this.isSelectAll){
                        	td.className += ' item-ckb-self';
                        }
                    }else if(col.type == 'rowradio'){
                    	Ext.fly(td).set({'recordid':record.id,'atype':'grid.rowradio'})
                        td.className = 'grid-rowbox';
                    }else{
                        td.style.visibility=col.hidden === true ? 'hidden' : 'visible';
                        td.style.textAlign=col.align||'left';
                        if(!this.isFunctionCol(col)) td.dataindex=col.name;
                        Ext.fly(td).set({'dataindex':col.name,'recordid':record.id,'atype':'grid-cell'}); 
                        if(col.type == 'rownumber') {
                            td.className = 'grid-rownumber';
                        }
                    }
                    var cell = this.createCell(col,record, false);
                    td.innerHTML = cell;
                    ltr.appendChild(td);
                }
            }
            this.lbt.dom.tBodies[0].appendChild(ltr);
        }
        
        var utr = document.createElement("tr");
        utr.id=this.id+'$u'+'-'+record.id;
        utr.className=(row % 2==0 ? '' : 'row-alt')+' '+css.cls;
        Ext.fly(utr).set({'style':css.style});
        for(var i=0,l=columns.length;i<l;i++){
            var col = columns[i];
            if(col.lock !== true){
                var td = document.createElement("td");
                td.style.visibility=col.hidden === true ? 'hidden' : 'visible';
                td.style.textAlign=col.align||'left';
                Ext.fly(td).set({
                    'dataindex':col.name,
                    'recordid':record.id,
                    'atype':'grid-cell'
                })
                var cell = this.createCell(col,record,false);
                td.innerHTML = cell;
                utr.appendChild(td);
            }
        }
        this.ubt.dom.tBodies[0].appendChild(utr);
    	this.setSelectStatus(record);
    },
    renderEditor : function(div,record,c,editor){
    	var cell = this.createCell(c,record,false);
    	div.parent('td').update(cell);
    	
    	/*
    	if(editor == ''){    	
    		div.removeClass(this.cecls);
    	}else if(editor != '' || ($(editor) instanceof $A.CheckBox)){
    		var cell = this.createCell(c,record,false);
    		div.parent().update(cell)
    	}else{
            div.addClass(this.cecls);
    	}
        */
    },
    onUpdate : function(ds,record, name, value){
        this.setSelectStatus(record);
        var div = Ext.get(this.id+'_'+name+'_'+record.id);              
        if(div){
            var c = this.findColByName(name);
            var editor = this.getEditor(c,record);            
            if(editor!='' && ($(editor) instanceof $A.CheckBox)){
            	this.renderEditor(div,record,c,editor);
            }else{
            	//考虑当其他field的值发生变化的时候,动态执行其他带有renderer的
                var text =  this.renderText(record,c,value);
                div.update(text);
            }
        }
        var cls = this.columns;
        for(var i=0,l=cls.length;i<l;i++){
            var c = cls[i];
            if(c.name != name) {
            	var ediv = Ext.get(this.id+'_'+c.name+'_'+record.id);
            	if(ediv) {
            		if(c.editorfunction){
                		var editor = this.getEditor(c,record);
                        this.renderEditor(ediv,record, c, editor);
            		}
                    if(c.renderer){
                        var text =  this.renderText(record,c, record.get(c.name));
                        ediv.update(text);
                    }
                }
                
            }
        }
        this.drawFootBar(name);
    },
    onValid : function(ds, record, name, valid){
        var c = this.findColByName(name);
//      if(c&&c.editor){
        if(c){
            var div = Ext.get(this.id+'_'+name+'_'+record.id);
            if(div) {
                if(valid == false){
                    div.addClass('item-invalid');
                }else{
                    div.removeClass([this.nbcls,'item-invalid']);
                }
            }
        }
    },
    onRemove : function(ds,record,index){
        var lrow = Ext.get(this.id+'$l-'+record.id);
        if(lrow)lrow.remove();        
        var urow = Ext.get(this.id+'$u-'+record.id);
        if(urow)urow.remove();
        if(Ext.isIE||Ext.isIE9)this.syncScroll();
        this.clearDomRef();
        $A.Masker.unmask(this.wb);
        this.drawFootBar();
    },
    onClear : function(){
        
    },
    onFieldChange : function(ds, record, field, type, value){
        switch(type){
           case 'required':
               var div = Ext.get(this.id+'_'+field.name+'_'+record.id);
               if(div) {
                   (value==true) ? div.addClass(this.nbcls) : div.removeClass(this.nbcls);
               }
               break;
        }
    },
//  onRowMouseOver : function(e){
//      if(Ext.fly(e.target).hasClass('grid-cell')){
//          var rid = Ext.fly(e.target).getAttributeNS("","recordid");
//          var row = this.getDataIndex(rid);
//          if(row == -1)return;
//          if(rid != this.overId)
//          if(this.overlockTr) this.overlockTr.setStyle(this.bgc, this.selectedId ==this.overId ? '#ffe3a8' : '');
//          if(this.overUnlockTr)  this.overUnlockTr.setStyle(this.bgc,this.selectedId ==this.overId ? '#ffe3a8' : '');
//          this.overId = rid;
//          this.overlockTr = Ext.get(document.getElementById(this.id+'$l-'+rid));
//          if(this.overlockTr)this.overlockTr.setStyle(this.bgc,'#d9e7ed');
//          this.overUnlockTr = Ext.get(document.getElementById(this.id+'$u-'+rid));
//          this.overUnlockTr.setStyle(this.bgc,'#d9e7ed');
//      }
//  },
    getDataIndex : function(rid){
        var index = -1;
        for(var i=0,l=this.dataset.data.length;i<l;i++){
            var item = this.dataset.getAt(i);
            if(item.id == rid){
                index = i;
                break;
            }
        }
        return index;
    },
    onSelect : function(ds,record,isSelectAll){
        if(!record||isSelectAll)return;
        var cb = Ext.get(this.id+'__'+record.id);
        Ext.fly(cb.findParent('.grid-rowbox')).addClass('item-ckb-self');
        if(cb){
	        if(this.selectable && this.selectionmodel=='multiple') {
	            this.setCheckBoxStatus(cb, true);
	            this.setSelectStatus(record);
	        }else{
	            this.setRadioStatus(cb,true);
	            this.setSelectStatus(record);
	            this.dataset.locate((this.dataset.currentPage-1)*this.dataset.pagesize + this.dataset.indexOf(record) + 1)
	        }
        }
    },
    onUnSelect : function(ds,record,isSelectAll){
        if(!record||isSelectAll)return;
        var cb = Ext.get(this.id+'__'+record.id);
        Ext.fly(cb.findParent('.grid-rowbox')).addClass('item-ckb-self');
        if(cb){
	        if(this.selectable && this.selectionmodel=='multiple') {
	            this.setCheckBoxStatus(cb, false);
	            this.setSelectStatus(record);
	        }else{
	            this.setRadioStatus(cb,false);
	            this.setSelectStatus(record);
	        }
        }
    },
    onSelectAll : function(){
    	this.clearChecked();
    	this.isSelectAll = true;
    	this.isUnSelectAll = false;
    	this.wrap.addClass('grid-select-all');
    },
    onUnSelectAll : function(){
    	this.clearChecked();
    	this.isSelectAll = false;
    	this.isUnSelectAll = true;
    	this.wrap.removeClass('grid-select-all');
    },
    clearChecked : function(){
    	var ckbs = this.wrap.select('.item-ckb-self');
    	if(ckbs){
    		ckbs.removeClass('item-ckb-self');
    		for(var i = 0,els = ckbs.elements,l = els.length;i<l;i++){
    			var child = Ext.fly(els[i]).child('.item-ckb-c');
    			if(child)child.replaceClass('item-ckb-c','item-ckb-u');
    		}
    	}
    },
    onDblclick : function(e){
        var target = Ext.fly(e.target).findParent('td[atype=grid-cell]');
        if(target){
            var rid = Ext.fly(target).getAttributeNS("","recordid");
            var record = this.dataset.findById(rid);
            var row = this.dataset.indexOf(record);
            var name = Ext.fly(target).getAttributeNS("","dataindex");
            this.fireEvent('dblclick', this, record, row, name)
        }
    },
    onClick : function(e) {
        var target = Ext.fly(e.target).findParent('td');
        if(target){
            var atype = Ext.fly(target).getAttributeNS("","atype");
            var rid = Ext.fly(target).getAttributeNS("","recordid");
            if(atype=='grid-cell'){
                var record = this.dataset.findById(rid);
                var row = this.dataset.indexOf(record);
                var name = Ext.fly(target).getAttributeNS("","dataindex");
                this.fireEvent('cellclick', this, row, name, record);
                //this.adjustColumn(name);
                //this.showEditor(row,name);
                this.fireEvent('rowclick', this, row, record);
            }else if (atype == 'grid-rownumber'){
                var record = this.dataset.findById(rid);
                var row = this.dataset.indexOf(record);
                if(record.id != this.selectedId) this.selectRow(row);
            }else if(atype=='grid.rowcheck'){               
                var cb = Ext.get(this.id+'__'+rid);
                if(cb.hasClass('item-ckb-readonly-u')||cb.hasClass('item-ckb-readonly-c'))return;
                if(this.isSelectAll && !cb.parent('.item-ckb-self')){
                	cb.replaceClass('item-ckb-u','item-ckb-c');	
                }
				if(this.isUnselectAll && !cb.parent('.item-ckb-self')){
            		cb.replaceClass('item-ckb-c','item-ckb-u');	
                }
                var checked = cb.hasClass('item-ckb-c');
                (checked) ? this.dataset.unSelect(rid) : this.dataset.select(rid);
            }else if(atype=='grid.rowradio'){
            	var cb = Ext.get(this.id+'__'+rid);
                if(cb.hasClass('item-radio-img-readonly-u')||cb.hasClass('item-radio-img-readonly-c'))return;
                this.dataset.select(rid);
            }
        }
    },
    onCellClick : function(grid,row,name,record,callback){
    	this.adjustColumn(name);
    	this.showEditor(row,name,callback);
    },
    adjustColumn:function(name){
    	var th = this.wrap.select('tr.grid-hl th[dataindex='+name+']'),
    		w = max = Ext.fly(th.elements[0]).getWidth(),
    		margin = 12;
    	Ext.each(this.wrap.query('td[dataindex='+name+']'),function(td){
    		var t = Ext.fly(td),span = t.child('span');
    		if(span){
    			if(Ext.isIE || Ext.isIE9)span.parent().setStyle('text-overflow','clip');
	    		max = Math.max(span.getWidth()+margin,max);
	    		if(Ext.isIE || Ext.isIE9)span.parent().setStyle('text-overflow','');
    		}
    	});
    	if(max > w)this.setColumnSize(name,max);
    },
    /**
     * 设置指定name列的标题.
     * 
     * @param {String} name 列的name
     * @param {String} prompt 标题信息
     */
    setColumnPrompt: function(name,prompt){
        var tds = Ext.DomQuery.select('td.grid-hc',this.wrap.dom);
        for(var i=0,l=tds.length;i<l;i++){
            var td = tds[i];
            var dataindex = Ext.fly(td).getAttributeNS("","dataindex");
            if(dataindex==name){
            	var div = Ext.fly(td).child('div');
            	div.update(prompt)
                break;
            }
        }
    },
    /**
     * 设置当前行的编辑器.
     * 
     * @param {String} name 列的name.
     * @param {String} editor 编辑器的id. ''空表示没有编辑器.
     */
    setEditor: function(name,editor){
        var col = this.findColByName(name);
        col.editor = editor;
        var div = Ext.get(this.id+'_'+name+'_'+this.selectRecord.id)
        if(div){
            if(editor == ''){
            	div.removeClass(this.cecls)
            }else if(!$(editor) instanceof $A.CheckBox){
            	div.addClass(this.cecls)
            }
        }
    },
    getEditor : function(col,record){
        var ed = col.editor||'';
        if(col.editorfunction) {
            var ef = window[col.editorfunction];
            if(ef==null) {
                alert("未找到"+col.editorfunction+"方法!") ;
                return null;
            }
            ed = ef.call(window,record,col.name)
        }
        return ed;
    },
    /**
     * 显示编辑器.
     * @param {Number} row 行号
     * @param {String} name 当前列的name.
     */
    showEditor : function(row, name,callback){       
        if(row == -1)return;
        var col = this.findColByName(name);
        if(!col)return;
        var record = this.dataset.getAt(row);
        if(!record)return;
        if(record.id != this.selectedId) this.selectRow(row);
        this.focusColumn(name);
        var editor = this.getEditor(col,record);
        this.setEditor(name, editor);
        var sf = this;
        if(sf.currentEditor){
        	sf.currentEditor.editor.el.un('keydown', sf.onEditorKeyDown,sf);
    		var d = sf.currentEditor.focusCheckBox;
    		if(d){
    			d.setStyle('outline','none');
    			sf.currentEditor.focusCheckBox = null;
    		}
    	}
        if(editor!=''){
        	var ed = $(editor);
            setTimeout(function(){
            	var v = record.get(name);
                sf.currentEditor = {
                    record:record,
                    ov:v,
                    name:name,
                    editor:ed
                };
                var dom = Ext.get(sf.id+'_'+name+'_'+record.id);
                var xy = dom.getXY();
                ed.bind(sf.dataset, name);
                ed.render(record);
	        	if(ed instanceof $A.CheckBox){
	        		ed.move(-1000,xy[1]+5);
		        	ed.el.on('keydown', sf.onEditorKeyDown,sf);
		        	ed.onClick();
		        	sf.currentEditor.focusCheckBox = dom;
	        		dom.setStyle('outline','1px dotted blue');
//		            var field = sf.dataset.getField(name)
//		            var cv = field.getPropertity('checkedvalue');
//		            var uv = field.getPropertity('uncheckedvalue');
//		            var v = record.get(name);
//		            record.set(name, v == cv ? uv : cv);
	       		}else{
	       			ed.move(xy[0],xy[1]);
                    ed.setHeight(dom.parent().getHeight()-5)
                    ed.setWidth(dom.parent().getWidth()-7);
                    ed.isEditor = true;
                    ed.isFireEvent = true;
                    ed.isHidden = false;
                    ed.focus();
       				sf.editing = true;
                    ed.el.on('keydown', sf.onEditorKeyDown,sf);
                    ed.on('select',sf.onEditorSelect,sf);
//                    ed.on('blur',sf.onEditorBlur, sf);
                    Ext.get(document.documentElement).on("mousedown", sf.onEditorBlur, sf);
                    if(callback)callback.call(window,ed)
	                sf.fireEvent('editorshow', sf, ed, row, name, record);
       			}
            },10)
        }           
    },
    onEditorSelect : function(){
		var sf =this;
		setTimeout(function(){sf.hideEditor()},1);
    },
    onEditorKeyDown : function(e){
        var keyCode = e.keyCode;
        //esc
        if(keyCode == 27) {
            if(this.currentEditor && this.currentEditor.editor){
                var ed = this.currentEditor.editor
                ed.clearInvalid();
                ed.render(ed.binder.ds.getCurrentRecord());
            }
            this.hideEditor();
        }
        //enter
        if(keyCode == 13) {
        	if(!(this.currentEditor && this.currentEditor.editor && this.currentEditor.editor instanceof $A.TextArea)){
	            this.showNextEditor();
        	}
        }
        //tab
        if(keyCode == 9){
            e.stopEvent();
            this.showNextEditor();
        }
    },
    showNextEditor : function(){
        this.hideEditor();
        var sf = this;
        if(this.currentEditor && this.currentEditor.editor){
            var callback = function(ed){
                if(ed instanceof Aurora.Lov){
                    ed.showLovWindow();
                }
            }
            var ed = this.currentEditor.editor,ds = ed.binder.ds,
                fname = ed.binder.name,r = ed.record,
                row = ds.data.indexOf(r),name=null;
            if(row!=-1){
                var cls = this.columns;
                var start = 0;
                for(var i = 0,l = cls.length; i<l; i++){
                    if(cls[i].name == fname){
                        start = i+1;
                    }
                }
                var editor;
                for(var i = start,l = cls.length; i<l; i++){
                    var col = cls[i];
                    if(col.hidden !=true){
	                    editor = this.getEditor(col,r);
	                    if(editor!=''){
	                        name =  col.name;
	                        break;
	                    }
                    }
                }
                if(sf.currentEditor){
        			var d= sf.currentEditor.focusCheckBox;
        			if(d){
        				d.setStyle('outline','none');
        				sf.currentEditor.focusCheckBox = null;
        			}
        		}
                if(name){
                	var ed = $(editor);
                	if(ed instanceof $A.CheckBox){
                		sf.currentEditor = {
		                    record:r,
		                    ov:r.get(name),
		                    name:name,
		                    editor:ed
		                };
                		setTimeout(function(){
	                		ed.bind(sf.dataset,name);
	                		ed.render(r);
	                		var dom = Ext.get(sf.id+'_'+name+'_'+r.id);
	                		var xy = dom.getXY();
	                		ed.move(-1000,xy[1])
	                		ed.focus();
	                		ed.el.on('keydown', sf.onEditorKeyDown,sf);
	                		sf.currentEditor.focusCheckBox = dom;
	                		dom.setStyle('outline','1px dotted blue');
                		},10)
                	}else{
	                    this.fireEvent('cellclick', this, row, name, r ,callback);
	                    //this.showEditor(row,name,callback);
                	}
                }else{
                    var nr = ds.getAt(row+1);
                    if(nr){
                    	sf.selectRow(row+1);
                        for(var i = 0,l = cls.length; i<l; i++){
                            var col = cls[i];
                            var editor = this.getEditor(col,nr);
                            if(editor!=''){
                            	var ed = $(editor),name = col.name;
                            	if(ed instanceof $A.CheckBox){
			                		sf.currentEditor = {
					                    record:nr,
					                    ov:nr.get(name),
					                    name:name,
					                    editor:ed
					                };
			                		setTimeout(function(){
				                		ed.bind(sf.dataset,name);
				                		ed.render(nr);
				                		var dom = Ext.get(sf.id+'_'+name+'_'+nr.id);
				                		var xy = dom.getXY();
				                		ed.move(-1000,xy[1])
				                		ed.focus();
				                		ed.el.on('keydown', sf.onEditorKeyDown,sf);
				                		sf.currentEditor.focusCheckBox = dom;
				                		dom.setStyle('outline','1px dotted blue');
			                		},10)
			                	}else{
	                                this.fireEvent('cellclick', this, row+1, name, nr ,callback);
	                                //this.showEditor(row+1,name,callback);
			                	}
                                break;
                            }
                        }
                    }
                }
            }
            this.fireEvent('nexteditorshow',this, row, name);
        }
    },
    /**
     * 指定行获取焦点.
     * @param {Number} row
     */
    focusRow : function(row){
        var r = 25;
        var stop = this.ub.getScroll().top;
        if(row*r<stop){
            this.ub.scrollTo('top',row*r-1)
        }
        if((row+1)*r>(stop+this.ub.getHeight())){//this.ub.dom.scrollHeight
            var st = this.ub.dom.scrollWidth > this.ub.dom.clientWidth ? (row+1)*r-this.ub.getHeight() + 16 : (row+1)*r-this.ub.getHeight();
            this.ub.scrollTo('top',st)
        }
        if(this.autofocus)
        this.focus();
    },
    focusColumn: function(name){
        var r = 25;
        var sleft = this.ub.getScroll().left;
        var ll = lr = lw = tw = 0;
        for(var i=0,l=this.columns.length;i<l;i++){
            var c = this.columns[i];
            if(c.name && c.name == name) {
//          if(c.name && c.name.toLowerCase() == name.toLowerCase()) {
                tw = c.width;
            }
            if(c.hidden !== true){
                if(c.lock === true){
                    lw += c.width;
                }else{
                    if(tw==0) ll += c.width;
                }
            }
        }
        lr = ll + tw;
        if(ll<sleft){
            this.ub.scrollTo('left',ll)
        }
        if((lr-sleft)>(this.width-lw)){
            this.ub.scrollTo('left',lr  - this.width + lw)
        }
        if(this.autofocus)
        this.focus();
    },
    /**
     * 隐藏当前编辑器
     */
    hideEditor : function(){
        if(this.currentEditor && this.currentEditor.editor && this.editing){
            var ed = this.currentEditor.editor;
            //ed.un('blur',this.onEditorBlur, this);
            var needHide = true;
            if(ed.canHide){
                needHide = ed.canHide();
            }
            if(needHide) {
                ed.el.un('keydown', this.onEditorKeyDown,this);
                ed.un('select',this.onEditorSelect,this);
                Ext.get(document.documentElement).un("mousedown", this.onEditorBlur, this);
                var ed = this.currentEditor.editor;
                ed.move(-10000,-10000);
                ed.onBlur();
                ed.isFireEvent = false;
                ed.isHidden = true;
                this.editing = false;
            }
        }
    },
    onEditorBlur : function(e){
        if(this.currentEditor && !this.currentEditor.editor.isEventFromComponent(e.target)) {   
            this.hideEditor();
        }
    },
    onLockHeadMove : function(e){
//      if(this.draging)return;
        this.hmx = e.xy[0] - this.lht.getXY()[0];
        if(this.isOverSplitLine(this.hmx)){
            this.lh.setStyle('cursor',"w-resize");          
        }else{
            this.lh.setStyle('cursor',"default");           
        }
    },
    onUnLockHeadMove : function(e){
//      if(this.draging)return;
        var lw = 0;
        if(this.uht){
            lw = this.uht.getXY()[0] + this.uht.getScroll().left;
        }
        this.hmx = e.xy[0] - lw + this.lockWidth;
        if(this.isOverSplitLine(this.hmx)){
            this.uh.setStyle('cursor',"w-resize");          
        }else{
            this.uh.setStyle('cursor',"default");
        }       
    },
    onHeadMouseDown : function(e){
        this.dragWidth = -1;
        if(this.overColIndex == undefined || this.overColIndex == -1) return;
        this.dragIndex = this.overColIndex;
        this.dragStart = e.getXY()[0];
        this.sp.setHeight(this.wrap.getHeight());
        this.sp.setVisible(true);
        this.sp.setStyle("top", this.wrap.getXY()[1]+"px")
        this.sp.setStyle("left", e.xy[0]+"px")
        Ext.get(document.documentElement).on("mousemove", this.onHeadMouseMove, this);
        Ext.get(document.documentElement).on("mouseup", this.onHeadMouseUp, this);
    },
    onHeadClick : function(e){
        var target = Ext.fly(e.target).findParent('td');
        var atype;
        if(target) {
            target = Ext.fly(target)
            atype = target.getAttributeNS("","atype");
        }
        if(atype=='grid.head'){
            var index = target.getAttributeNS("","dataindex");
            var col = this.findColByName(index);
            if(col && col.sortable === true){
            	if(this.dataset.isModified()){
                    $A.showInfoMessage('提示', '有未保存数据!');
                    return;
            	}
                var d = target.child('div');
                var of = index;
                var ot = '';
//                this.dataset.setQueryParameter('ORDER_FIELD', index);
                if(this.currentSortTarget){
                    var cst = Ext.fly(this.currentSortTarget)
                    cst.removeClass(['grid-asc','grid-desc']);
                }
                this.currentSortTarget = d;
                if(Ext.isEmpty(col.sorttype)) {
                    col.sorttype = 'desc'
                    d.removeClass('grid-asc');
                    d.addClass('grid-desc');
                    ot = 'desc';
//                    this.dataset.setQueryParameter('ORDER_TYPE', 'desc');
                } else if(col.sorttype == 'desc'){
                    col.sorttype = 'asc';
                    d.removeClass('grid-desc');
                    d.addClass('grid-asc');
                    ot = 'asc';
//                    this.dataset.setQueryParameter('ORDER_TYPE', 'asc');
                }else {
                    col.sorttype = '';
                    d.removeClass(['grid-desc','grid-asc']);
//                    delete this.dataset.qpara['ORDER_TYPE'];
                }
//                if(this.dataset.getAll().length!=0)this.dataset.query();
                this.dataset.sort(of,ot);
            }
        }else if(atype=='grid.rowcheck'){
            var cb = target.child('div[atype=grid.headcheck]');
            if(cb){
                var checked = cb.hasClass('item-ckb-c');
                this.setCheckBoxStatus(cb,!checked);
                if(!checked){
                    this.dataset.selectAll();
                }else{
                    this.dataset.unSelectAll();
                }
            }
        }
    },
    setRadioStatus: function(el, checked){
        if(!checked){
            el.removeClass('item-radio-img-c');
            el.addClass('item-radio-img-u');
        }else{
            el.addClass('item-radio-img-c');
            el.removeClass('item-radio-img-u');
        }
    },
    setCheckBoxStatus: function(el, checked){
        if(el)
        if(!checked){
            el.removeClass('item-ckb-c');
            el.addClass('item-ckb-u');
        }else{
            el.addClass('item-ckb-c');
            el.removeClass('item-ckb-u');
        }
    },
    setSelectDisable:function(el,record){
    	if(this.selectable && this.selectionmodel=='multiple'){
    		el.removeClass('item-ckb-c');
    		el.removeClass('item-ckb-u');
            if(this.dataset.selected.indexOf(record) == -1){
                el.addClass('item-ckb-readonly-u');
            }else{
                el.addClass('item-ckb-readonly-c');
            }
    	}else{
    		el.removeClass(['item-radio-img-c','item-radio-img-u','item-radio-img-readonly-c','item-radio-img-readonly-u']);
            if(this.dataset.selected.indexOf(record) == -1){
                el.addClass('item-radio-img-readonly-u');
            }else{
                el.addClass('item-radio-img-readonly-c');
            }
    	}
    },
    setSelectEnable:function(el,record){
    	if(this.selectable && this.selectionmodel=='multiple'){
    		el.removeClass(['item-ckb-readonly-u','item-ckb-readonly-c']);
            if(this.dataset.selected.indexOf(record) == -1){
                el.addClass('item-ckb-u');
            }else{
                el.addClass('item-ckb-c');
            }
    	}else{
            el.removeClass(['item-radio-img-u','item-radio-img-c','item-radio-img-readonly-u','item-radio-img-readonly-c']);
    		if(this.dataset.selected.indexOf(record) == -1){
                el.addClass('item-radio-img-u');
            }else{
                el.addClass('item-radio-img-c');
            }
    	}	
    },
    setSelectStatus:function(record){
    	if(this.dataset.selectfunction){
	    	var cb = Ext.get(this.id+'__'+record.id);
	    	if(!this.dataset.execSelectFunction(record)){
	    		 this.setSelectDisable(cb,record)
	    	}else{
	    		 this.setSelectEnable(cb,record);
	    	}
    	}
    },
    onHeadMouseMove: function(e){
//      this.draging = true
        e.stopEvent();
        this.dragEnd = e.getXY()[0];
        var move = this.dragEnd - this.dragStart;
        var c = this.columns[this.dragIndex];
        var w = c.width + move;
        if(w > 30 && w < this.width) {
            this.dragWidth = w;
            this.sp.setStyle("left", e.xy[0]+"px")
        }
    },
    onHeadMouseUp: function(e){
//      this.draging = false;
        Ext.get(document.documentElement).un("mousemove", this.onHeadMouseMove, this);
        Ext.get(document.documentElement).un("mouseup", this.onHeadMouseUp, this);      
        this.sp.setVisible(false);
        if(this.dragWidth != -1)
        this.setColumnSize(this.columns[this.dragIndex].name, this.dragWidth);
        this.syncScroll();
    },
    /**
     * 根据列的name获取列配置.
     * 
     * @param {String} name 列的name
     * @return {Object} col 列配置对象.
     */
    findColByName : function(name){
        var col;
        for(var i=0,l=this.columns.length;i<l;i++){
            var c = this.columns[i];
            if(c.name && c.name.toLowerCase() === name.toLowerCase()){
                col = c;
                break;
            }
        }
        return col;
    }, 
    /**
     * 选中高亮某行.
     * @param {Number} row 行号
     */
    selectRow : function(row, locate){
        var record = this.dataset.getAt(row) 
        this.selectedId = record.id;
        if(this.selectlockTr) this.selectlockTr.setStyle(this.bgc,'');
        //if(this.selectUnlockTr) this.selectUnlockTr.setStyle(this.bgc,'');
        if(this.selectUnlockTr) this.selectUnlockTr.removeClass('row-selected');
        this.selectUnlockTr = Ext.get(this.id+'$u-'+record.id);
        if(this.selectUnlockTr)this.selectUnlockTr.addClass('row-selected');
        //if(this.selectUnlockTr)this.selectUnlockTr.setStyle(this.bgc,this.scor);
        
        this.selectlockTr = Ext.get(this.id+'$l-'+record.id);
        if(this.selectlockTr)this.selectlockTr.setStyle(this.bgc,this.scor);
        this.focusRow(row);
        
        var r = (this.dataset.currentPage-1)*this.dataset.pagesize + row+1;
        this.selectRecord = record
        if(locate!==false && r != null) {
//          this.dataset.locate(r);
            this.dataset.locate.defer(5, this.dataset,[r,false]);
        }
    },
    /**
     * 设置某列的宽度.
     * @param {String} name 列的name
     * @param {Number} size 列的宽度
     */
    setColumnSize : function(name, size){
        var columns = this.columns;
        var hth,bth,lw=0,uw=0;
        var wd = "width",px="px";
        for(var i=0,l=columns.length;i<l;i++){
            var c = columns[i];
            if(c.name && c.name === name){
                if(c.hidden === true) return;
                c.width = size;
                if(c.lock !== true){                    
                    hth = this.uh.child('th[dataindex='+name+']');
                    bth = this.ub.child('th[dataindex='+name+']');                  
                }else{                          
                    if(this.lh) hth = this.lh.child('th[dataindex='+name+']');
                    if(this.lb) bth = this.lb.child('th[dataindex='+name+']');
                    
                }
            }
            c.lock !== true ? (uw += c.width) : (lw += c.width);
        }
        var tds = Ext.DomQuery.select('td[dataindex='+name+']',this.wrap.dom);
        for(var i=0,l=tds.length;i<l;i++){
            var td = tds[i];
            var ce = Ext.fly(td).child('DIV.grid-cell');
//            if(ce)Ext.fly(ce).setStyle(wd, Math.max(size-4,0)+px);
            if(ce){
            	var sp = ce.hasClass(this.cecls) ? 7 : 4;
                Ext.fly(ce).setStyle(wd, Math.max(size-sp,0)+px);
            }
        }
        
        this.unlockWidth = uw;
        this.lockWidth = lw;
        if(hth) hth.setStyle(wd, size+px);
        if(bth) bth.setStyle(wd, size+px);
        if(this.fb){
            var ftd = this.fb.child('th[dataindex='+name+']');
            ftd.setStyle(wd, size+px);
            var uft = this.fb.child('table[atype=fb.ubt]');
            this.uf.setStyle(wd,Math.max(this.width - lw,0)+px);
            uft.setStyle(wd,uw+px);
            var lft = this.fb.child('table[atype=fb.lbt]');
            if(lft){
            var lf = this.fb.child('div[atype=grid.lf]');
                lf.setStyle(wd,(lw-1)+px);
                lft.setStyle(wd,lw+px);
            }
        }
        
        if(this.lc){
            var lcw = Math.max(lw-1,0);
            this.lc.setStyle(wd,lcw+px);
            this.lc.setStyle('display',lcw==0 ? 'none' : '');
        }
        if(this.lht)this.lht.setStyle(wd,lw+px);
        if(this.lbt)this.lbt.setStyle(wd,lw+px);
        this.uc.setStyle(wd, Math.max(this.width - lw,0)+px);
        this.uh.setStyle(wd,Math.max(this.width - lw,0)+px);
        this.ub.setStyle(wd,Math.max(this.width - lw,0)+px);
        this.uht.setStyle(wd,uw+px);
        if(this.ubt)this.ubt.setStyle(wd,uw+px);
        this.syncSize();
    },
    drawFootBar : function(objs){
    	if(!this.fb) return;
    	objs = [].concat((objs) ? objs : this.columns);
    	var sf = this;
    	Ext.each(objs, function(obj) {
    		var col = typeof(obj)==='string' ? sf.findColByName(obj) : obj;
            if(col&&col.footerrenderer){
                var name = col.name;
                var fder = $A.getRenderer(col.footerrenderer);
                if(fder == null){
                    alert("未找到"+col.footerrenderer+"方法!")
                    return;
                }
                var v = fder.call(window,sf.dataset.data, name);
                var t = sf.fb.child('td[dataindex='+name+']');
                t.update(v)
            }
    	});
    	
    },
    syncSize : function(){
        var lw = 0;
        for(var i=0,l=this.columns.length;i<l;i++){
            var c = this.columns[i];
            if(c.hidden !== true){
                if(c.lock === true){
                    lw += c.width;
                }
            }
        }
        if(lw !=0){
            var us = this.width -lw;
            this.uc.setWidth(us);
            this.uh.setWidth(us);
            this.ub.setWidth(us);
        }
    },
    /**
     * 显示某列.
     * 
     * @param {String} name 列的name
     */
    showColumn : function(name){
        var col = this.findColByName(name);
        if(col){
            if(col.hidden === true){
                delete col.hidden;
                this.setColumnSize(name, col.hiddenWidth);
                delete col.hiddenWidth;
//              if(!Ext.isIE){
                    var tds = Ext.DomQuery.select('td[dataindex='+name+']',this.wrap.dom);
                    for(var i=0,l=tds.length;i<l;i++){
                        var td = tds[i];
                        Ext.fly(td).show();
                    }
//              }
            }
        }   
    },
    /**
     * 隐藏某列.
     * 
     * @param {String} name 列的name;
     */
    hideColumn : function(name){
        var col = this.findColByName(name);
        if(col){
            if(col.hidden !== true){
                col.hiddenWidth = col.width;
                this.setColumnSize(name, 0, false);
//              if(!Ext.isIE){
                    var tds = Ext.DomQuery.select('td[dataindex='+name+']',this.wrap.dom);
                    for(var i=0,l=tds.length;i<l;i++){
                        var td = tds[i];
                        Ext.fly(td).hide();
                    }
//              }
                col.hidden = true;
            }
        }       
    },
    setWidth: function(w){
    	if(this.width == w) return;
        this.width = w;
        this.wrap.setWidth(w);
        var tb = $A.CmpManager.get(this.id+'_tb')
        if(tb)tb.setWidth(w);
        var nb = $A.CmpManager.get(this.id+'_navbar')
        if(nb)nb.setWidth(w);
        if(this.fb) this.fb.setWidth(w);
        var bw = w-this.lockWidth
        this.uc.setWidth(bw);
        this.uh.setWidth(bw);
        this.ub.setWidth(bw);
        if(this.uf) this.uf.setWidth(bw);
    },
    setHeight: function(h){
    	if(this.height == h) return;
        this.height = h;
        var tb = $A.CmpManager.get(this.id+'_tb');
        if(tb)h-=25;
        var nb = $A.CmpManager.get(this.id+'_navbar');
        if(nb)h-=25;
        if(this.fb)h-=25;
        this.wrap.setHeight(h);
        var bh = h - 25;
        if(this.lb)this.lb.setHeight(bh)
        this.ub.setHeight(bh)
    },
    deleteSelectRows: function(win){
        var selected = [].concat(this.dataset.getSelected());
        if(selected.length >0){
            this.dataset.remove(selected);
//          for(var i=0;i<selected.length;i++){
//              var r = selected[i];
//              this.dataset.remove(r);
//          }
        }
        win.close();
    },
    remove: function(){
        var selected = this.dataset.getSelected();
        if(selected.length >0) $A.showConfirm(_lang['grid.remove.confirm'],_lang['grid.remove.confirmMsg'],this.deleteSelectRows.createDelegate(this));     
    },
    clear: function(){
        var selected = this.dataset.getSelected();
    	while(selected[0]){
    		this.dataset.removeLocal(selected[0]);
    	}
    },
    _export : function(){
    	this.showExportConfirm();
    },
    showExportConfirm :function(){
    	this.initColumnPrompt();
    	var sf = this,id = this.id + '_export',
    		msg = ['<div class="item-export-wrap" style="margin:15px;width:270px" id="'+id+'">',
    				'<div class="grid-uh" atype="grid.uh" style="width: 270px; -moz-user-select: none; text-align: left; height: 25px; cursor: default;" onselectstart="return false;" unselectable="on">',
    				'<table cellSpacing="0" cellPadding="0" border="0"><tbody><tr height="25px">',
					'<td class="export-hc" style="width:22px;" atype="export.rowcheck"><center><div atype="export.headcheck" class="grid-ckb item-ckb-','u','"></div></center></td>',
					'<td class="export-hc" style="width:222px;" atype="grid-type">',_lang['grid.export.column'],'</td>',
					'</tr></tbody></table></div>',
					'<div style="overflow:auto;height:200px;"><table cellSpacing="0" cellPadding="0" border="0"><tbody>'],
					exportall = true;
			for(var i=0,l=this.columns.length;i<l;i++){
				var c = this.columns[i];
				if(exportall)exportall = c.forexport !==false;
				msg.push('<tr',i%2==0?'':' class="row-alt"','><td class="grid-rowbox" style="width:22px;" rowid="',i,'" atype="export.rowcheck"><center><div id="',this.id,'__',i,'" class="grid-ckb item-ckb-',c.forexport === false?'u':'c','"></div></center></td><td><div class="grid-cell" style="width:220px">',c.prompt,'</div></td></tr>');	
			}
			if(exportall)msg[4]='c';
			msg.push('</tbody></table></div></div>');
    	this.exportwindow = $A.showOkCancelWindow(_lang['grid.export.config'],msg.join(''),function(win2){
    		$A.showConfirm(_lang['grid.export.confirm'],_lang['grid.export.confirmMsg'],function(win){
		    	sf.doExport();
		       	win.close();
		       	win2.body.un('click',sf.onExportClick,sf);
		       	win2.close();
	    	});
	    	return false;
    	},null,null,300);
    	this.exportwindow.body.on('click',this.onExportClick,this);
    },
    initColumnPrompt : function(){
    	if(!this.isPromptInit){
    		for(var i=0,l=this.columns.length;i<l;i++){
    			var c = this.columns[i];
    			if(!c.type)c.prompt = c.name?this.wrap.child('td.grid-hc[dataindex='+c.name+'] div').dom.innerHTML : (c.prompt||this.dataset.getField(c.name).pro["prompt"]);
    		}
    		this.isPromptInit = true;
    	}
    },
    onExportClick : function(e,t){
    	var target =Ext.fly(Ext.fly(t).findParent('td'));
        if(target){
            var atype = target.getAttributeNS("","atype");
            var rid =target.getAttributeNS("","rowid");
            if(atype=='export.rowcheck'){               
	            var cb = target.child('div'),
                checked = cb.hasClass('item-ckb-c');
                this.setCheckBoxStatus(cb, !checked);
                var _atype = cb.getAttributeNS("","atype");
                if(_atype=='export.headcheck'){
                	var cbs = this.exportwindow.body.query('td[atype=export.rowcheck] div[atype!=export.headcheck]');
	            	for(var i = 0,l=cbs.length;i<l;i++){
	            		this.setCheckBoxStatus(Ext.fly(cbs[i]), !checked);
	            		this.columns[i].forexport = !checked;
	            	}
                }else
                	this.columns[rid].forexport = !checked;
            }
        }
    },
    doExport : function(){
    	this.initColumnPrompt();
    	var p={"parameter":{"_column_config_":{}}},columns=[],parentMap={},sf = this,
    	_parentColumn=function(pcl,cl){
    		if(!(Ext.isDefined(pcl.forexport)?pcl.forexport:true))return null;
    		var json=Ext.encode(pcl);
    		var c=parentMap[json];
    		if(!c)c={prompt:pcl.prompt};
    		parentMap[json]=c;
    		(c["column"]=c["column"]||[]).add(cl);
    		if(pcl._parent){
    			return _parentColumn(pcl._parent,c)
    		}
    		return c;
    	};
    	for(var i=0;i<sf.columns.length;i++){
    		var column=sf.columns[i],forExport=Ext.isDefined(column.forexport)?column.forexport:true;
    		if(!column.type&&forExport){
    			var c={prompt:column.prompt}
    			if(column.width)c.width=column.width;
    			if(column.name)c.name=column.exportfield||column.name;
    			c.align=column.align||"left";
    			var o=column._parent?_parentColumn(column._parent,c):c;
	    		if(o)columns.add(o);
    		}
    	}
    	p["parameter"]["_column_config_"]["column"]=columns;
    	p["_generate_state"]=true;
    	p["_format"]="xls";
    	var r,q = {};
    	if(sf.dataset.qds)r = sf.dataset.qds.getCurrentRecord();
    	if(r) Ext.apply(q, r.data);
    	Ext.apply(q, sf.dataset.qpara);
    	for(var k in q){
    	   var v = q[k];
    	   if(Ext.isEmpty(v,false)) delete q[k];
    	}
    	Ext.apply(p.parameter,q)
		var form = document.createElement("form");
		form.target = "_export_window";
		form.method="post";
		var url = sf.dataset.queryurl;
		if(url)form.action = url + (url.indexOf('?') == -1 ? '?' : '&')+'r='+Math.random();
		var iframe = Ext.get('_export_window')||new Ext.Template('<iframe id ="_export_window" name="_export_window" style="position:absolute;left:-1000px;top:-1000px;width:1px;height:1px;display:none"></iframe>').insertFirst(document.body,{},true)
		var s = document.createElement("input");
		s.id = "_request_data";
		s.type = 'hidden';
		s.name = '_request_data';
       	s.value = Ext.encode(p);
       	form.appendChild(s);
       	document.body.appendChild(form);
       	form.submit();
       	Ext.fly(form).remove();	
    },
    destroy: function(){
        $A.Grid.superclass.destroy.call(this);
        this.processDataSetLiestener('un');
        this.sp.remove();
        delete this.sp;
    }
});
$A.Grid.revision='$Rev: 5460 $';