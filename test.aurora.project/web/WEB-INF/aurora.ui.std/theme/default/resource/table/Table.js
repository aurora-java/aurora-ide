/**
 * @class Aurora.Table
 * @extends Aurora.Component
 * <p>Table 数据表格布局.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Table = Ext.extend($A.Component,{
	bgc:'background-color',
    scor:'#dfeaf5',
    ocor:'#ffe3a8',
    cecls:'table-cell-editor',
    nbcls:'item-notBlank',
	initComponent:function(config){
		$A.Table.superclass.initComponent.call(this,config);
		var wrap=this.wrap;
		this.cb = wrap.child('div[atype=table.headcheck]');
		this.tbody=wrap.child('tbody');
		this.fb=wrap.child('tfoot');
		this.initTemplate();
	},
	processListener:function(ou){
		$A.Table.superclass.processListener.call(this,ou);
		this.tbody[ou]('click',this.onClick, this);
		if(this.canwheel){
			this.tbody[ou]('mousewheel',this.onMouseWheel,this);
		}
		if(this.cb)this.cb[ou]('click',this.onHeadClick,this);
	},
	processDataSetLiestener: function(ou){
        var ds = this.dataset;
        if(ds){
	       	ds[ou]('ajaxfailed', this.onAjaxFailed, this);
            ds[ou]('metachange', this.onLoad, this);
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
            ds[ou]('refresh',this.onLoad,this);
            ds[ou]('fieldchange', this.onFieldChange, this);
            ds[ou]('indexchange', this.onIndexChange, this);
            ds[ou]('select', this.onSelect, this);
            ds[ou]('unselect', this.onUnSelect, this);
        }
    },
    initEvents:function(){
        $A.Table.superclass.initEvents.call(this);
        this.addEvents(
        /**
         * @event render
         * table渲染出数据后触发该事件
         * @param {Aurora.Table} table 当前Table组件.
         */
        'render',
        /**
         * @event cellclick
         * 单元格点击事件.
         * @param {Aurora.Table} table 当前Table组件.
         * @param {Number} row 行号.
         * @param {String} 当前name.
         * @param {Aurora.Record} record 鼠标点击所在单元格的Record对象.
         */
        'cellclick',
        /**
         * @event rowclick
         * 行点击事件.
         * @param {Aurora.Table} table 当前Table组件.
         * @param {Number} row 行号.
         * @param {Aurora.Record} record 鼠标点击所在行的Record对象.
         */
        'rowclick',
        /**
         * @event editorShow
         * 编辑器显示后触发的事件.
         * @param {Aurora.Table} table 当前Table组件.
         * @param {Editor} grid 当前Editor组件.
         * @param {Number} row 行号.
         * @param {String} 当前name.
         * @param {Aurora.Record} record 鼠标点击所在行的Record对象.
         */
        'editorshow',
        /**
         * @event nexteditorshow
         * 切换下一个编辑器的事件.
         * @param {Aurora.Table} table 当前table组件.
         * @param {Number} row 行号.
         * @param {String} 当前name.
         */
        'nexteditorshow');
    },
	bind:function(ds){
		if(typeof(ds)==='string'){
            ds = $(ds);
            if(!ds) return;
        }
        this.dataset = ds;
        this.processDataSetLiestener('on');
        this.onLoad();
	},
	initTemplate : function(){
        this.cellTpl = new Ext.Template('<div class="table-cell {cellcls}" id="'+this.id+'_{name}_{recordid}">{text}</div>');        
    	this.cbTpl = new Ext.Template('<center><div class="{cellcls}" id="'+this.id+'_{name}_{recordid}"></div></center>');
    },
	createRow:function(record,index){
		var tr=this.tbody.dom.insertRow(-1);
		var css=this.parseCss(this.renderRow(record,index));
		tr.id=this.id+'-'+record.id;
		tr.style.cssText=css.style;
		tr.className=(index%2==1?"table-row-alt ":"")+css.cls;
		for(var i=0,l=this.columns.length;i<l;i++){
			this.createCell(tr,this.columns[i],record);
		}
	},
	createEmptyRow:function(){
		this.emptyRow=this.tbody.dom.insertRow(-1);
		for(var i=0,l=this.columns.length;i<l;i++){
			var td=this.emptyRow.insertCell(-1),col = this.columns[i];
			td.innerHTML="&#160;";
			Ext.fly(td).set({'atype':'table-cell','dataindex':col.name,'style':'text-align:'+(col.align||'left')+(col.hidden?';display:none;':';')});
		}
	},
	removeEmptyRow:function(){
		if(this.emptyRow){
			this.tbody.dom.removeChild(this.emptyRow);
			this.emptyRow=null;
		}
	},
	getCheckBoxStatus: function(record, name ,readonly) {
        var field = this.dataset.getField(name)
        var cv = field.getPropertity('checkedvalue');
        var uv = field.getPropertity('uncheckedvalue');
        var value = record.data[name];
        return 'item-ckb-'+(readonly?'readonly-':'')+((value && value == cv) ? 'c' : 'u');
    },
	createCell:function(tr,col,record){
		var field = record.getMeta().getField(col.name);
        if(field && Ext.isEmpty(record.data[col.name]) && record.isNew == true && field.get('required') == true){
            cls = cls + ' ' + this.nbcls
        }
		var editor = this.getEditor(col,record),xtype = col.type,readonly,cls,td;
		if(tr.tagName.toLowerCase()=='tr')td=tr.insertCell(-1);
		else td=Ext.fly(tr).parent('td').dom
		if(editor!=''){
        	var edi = $A.CmpManager.get(editor);
            if(edi && (edi instanceof $A.CheckBox)){
                xtype = 'cellcheck';
                cls = '';
            }else{
                cls = 'table-cell-editor';
            }
        }else if(col.name && Ext.isDefined(record.getField(col.name).get('checkedvalue'))){
    		xtype = 'cellcheck';
    		readonly=true;
        }
		if(xtype == 'rowcheck'||xtype == 'rowradio'){
	    	if(!this.dataset.execSelectFunction(record))readonly="-readonly";
	    	else readonly="";
	    	Ext.fly(td).set({'atype':xtype == 'rowcheck'?'table.rowcheck':'table.rowradio','recordid':record.id,'class':'table-rowbox'});
	        td.innerHTML=this.cbTpl.applyTemplate({cellcls:xtype == 'rowcheck'?'table-ckb item-ckb'+readonly+'-u':'table-radio item-radio-img'+readonly+'-u',name:col.name,recordid:record.id});
	    }else{
			Ext.fly(td).set({'atype':'table-cell','recordid':record.id,'dataindex':col.name,'style':'text-align:'+(col.align||'left')+(col.hidden?';display:none;':';')});
			if(xtype == 'cellcheck'){
				td.innerHTML=this.cbTpl.applyTemplate({cellcls:'table-ckb ' + this.getCheckBoxStatus(record, col.name ,readonly),name:col.name,recordid:record.id});
			}else{
				td.innerHTML=this.cellTpl.applyTemplate({text:this.renderText(record,col,record.data[col.name]),cellcls:cls,name:col.name,recordid:record.id});
			}
		}
	},
	onSelect : function(ds,record){
        var cb = Ext.get(this.id+'__'+record.id);
        if(cb && this.selectable && this.selectionmodel=='multiple') {
            this.setCheckBoxStatus(cb, true);
            this.setSelectStatus(record);
        }else{
            this.setRadioStatus(cb,true);
            this.setSelectStatus(record);
        }
    },
    onUnSelect : function(ds,record){
        var cb = Ext.get(this.id+'__'+record.id);
        if(cb && this.selectable && this.selectionmodel=='multiple') {
            this.setCheckBoxStatus(cb, false);
            this.setSelectStatus(record);
        }else{
            this.setRadioStatus(cb,false);
            this.setSelectStatus(record);
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
            el.removeClass('item-ckb-readonly-u');
            el.removeClass('item-ckb-readonly-c');
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
     onHeadClick : function(e){
            var cb = this.cb;
            var checked = cb.hasClass('item-ckb-c');
            this.setCheckBoxStatus(cb,!checked);
            if(!checked){
                this.dataset.selectAll();
            }else{
                this.dataset.unSelectAll();
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
        this.focusdiv = Ext.get(this.id+'_'+name+'_'+this.selectRecord.id)
        if(this.focusdiv){
        	if(editor == ''){
            	this.focusdiv.removeClass(this.cecls)
            }else if(!$(editor) instanceof $A.CheckBox){
            	this.focusdiv.addClass(this.cecls)
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
        if(record.id != this.selectedId)
        //this.selectRecord = record;
        this.selectRow(row);
        //this.focusColumn(name);
        var editor = this.getEditor(col,record);
        this.setEditor(name, editor);
        var sf = this;
        if(sf.currentEditor){
        	sf.currentEditor.editor.el.un('keydown', sf.onEidtorKeyDown,sf);
    		var d = sf.currentEditor.focusCheckBox;
    		if(d){
    			d.setStyle('outline','none');
    			sf.currentEditor.focusCheckBox = null;
    		}
    	}
        if(editor!=''){
        	var ed = $(editor);
            setTimeout(function(){
                var v = record.get(name)
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
		        	ed.el.on('keydown', sf.onEidtorKeyDown,sf);
		        	ed.onClick();
		        	sf.currentEditor.focusCheckBox = dom;
	        		dom.setStyle('outline','1px dotted blue');
//		            var field = this.dataset.getField(name)
//		            var cv = field.getPropertity('checkedvalue');
//		            var uv = field.getPropertity('uncheckedvalue');
//		            var v = record.get(name);
//		            record.set(name, v == cv ? uv : cv);
		        } else if(editor){
           			sf.positionEditor();
                    ed.isEditor = true;
                    ed.isFireEvent = true;
                    ed.isHidden = false;
//                    ed.bind(sf.dataset, name);
//                    ed.render(record);
                    ed.focus();
                    sf.editing = true;
                    ed.el.on('keydown', sf.onEidtorKeyDown,sf);
//                    ed.on('blur',sf.onEditorBlur, sf);
                    Ext.fly(document.documentElement).on("mousedown", sf.onEditorBlur, sf);
                    Ext.fly(window).on("resize", sf.positionEditor, sf);
                    if(callback)callback.call(window,ed)
	                sf.fireEvent('editorshow', sf, ed, row, name, record);
		        }
            },10)
        }           
    },
    onEidtorKeyDown : function(e,editor){
        var keyCode = e.keyCode;
        //esc
        if(keyCode == 27) {
            editor.clearInvalid();
            editor.render(editor.binder.ds.getCurrentRecord());
            this.hideEditor();
        }
        //enter
        if(keyCode == 13) {
        	if(!editor instanceof $A.TextArea)
            this.showNextEditor();
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
	                		ed.el.on('keydown', sf.onEidtorKeyDown,sf);
	                		sf.currentEditor.focusCheckBox = dom;
	                		dom.setStyle('outline','1px dotted blue');
                		},10)
                	}else{
	                    this.fireEvent('cellclick', this, row, name, r);
	                    this.showEditor(row,name,callback);
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
				                		ed.el.on('keydown', sf.onEidtorKeyDown,sf);
				                		sf.currentEditor.focusCheckBox = dom;
				                		dom.setStyle('outline','1px dotted blue');
			                		},10)
			                	}else{
	                                this.fireEvent('cellclick', this, row+1, name, nr);
	                                this.showEditor(row+1,name,callback);
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
    positionEditor:function(){
    	var ed=this.currentEditor.editor,dom=this.focusdiv,xy = dom.getXY(),sf=this;
        ed.setHeight(dom.getHeight()-2);
        ed.setWidth(dom.getWidth()-5<22?22:(dom.getWidth()-5));
        ed.move(xy[0],xy[1]);
        if(ed.isExpanded&&ed.isExpanded()){
        	if(Ext.isIE){
        		if(this.t)clearTimeout(this.t);
	        	this.t=setTimeout(function(){
	        		ed.syncPopup();
	        	},1)
        	}else{
        		ed.syncPopup();	
        	}
        }
    },
    /**
     * 隐藏当前编辑器
     */
    hideEditor : function(){
        if(this.currentEditor && this.currentEditor.editor){
            var ed = this.currentEditor.editor;
//            ed.un('blur',this.onEditorBlur, this);
            var needHide = true;
            if(ed.canHide){
                needHide = ed.canHide();
            }
            if(needHide) {
            	ed.el.un('keydown', this.onEidtorKeyDown,this);
                Ext.fly(document.documentElement).un("mousedown", this.onEditorBlur, this);
                Ext.fly(window).un("resize", this.positionEditor, this);
                var ed = this.currentEditor.editor;
                ed.move(-10000,-10000);
                ed.onBlur();
                ed.isFireEvent = false;
                ed.isHidden = true;
            }
            this.editing = false;
        }
    },
        /**
     * 选中高亮某行.
     * @param {Number} row 行号
     */
    selectRow : function(row, locate){
        var record = this.dataset.getAt(row) 
        this.selectedId = record.id;
        if(this.selectTr)this.selectTr.setStyle(this.bgc,'');
        this.selectTr = Ext.get(this.id+'-'+record.id);
        if(this.selectTr)this.selectTr.setStyle(this.bgc,this.scor);
        //this.focusRow(row);
        var r = (this.dataset.currentPage-1)*this.dataset.pagesize + row+1;
        this.selectRecord = record
        if(locate!==false && r != null) {
//          this.dataset.locate(r);
            this.dataset.locate.defer(5, this.dataset,[r,false]);
        }
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
                if(!Ext.isEmpty(v)){
		    		var t = sf.fb.child('td[dataindex='+name+']');
	                t.update(v)
                }
            }
    	});
    },
    showColumn : function(name){
        var col = this.findColByName(name);
        if(col){
            if(col.hidden === true){
                delete col.hidden;
                //this.setColumnSize(name, col.hiddenWidth);
                //delete col.hiddenWidth;
//              if(!Ext.isIE){
                    var tds = Ext.DomQuery.select('td[dataindex='+name+']',this.wrap.dom);
                    for(var i=0,l=tds.length;i<l;i++){
                        var td = tds[i];
                        Ext.fly(td).setStyle('display','');
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
                //col.hiddenWidth = col.width;
                //this.setColumnSize(name, 0, false);
//              if(!Ext.isIE){
                    var tds = Ext.DomQuery.select('td[dataindex='+name+']',this.wrap.dom);
                    for(var i=0,l=tds.length;i<l;i++){
                        var td = tds[i];
                        Ext.fly(td).setStyle('display','none');
                    }
//              }
                col.hidden = true;
            }
        }       
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
    renderEditor : function(div,record,c,editor){
    	this.createCell(div.dom,c,record);
    	//div.parent().update(cell);
    },
    onClick : function(e) {
        var target = Ext.fly(e.target).findParent('td');
        if(target){
            var atype = Ext.fly(target).getAttributeNS("","atype");
            var rid = Ext.fly(target).getAttributeNS("","recordid");
            if(atype=='table-cell'){
                var record = this.dataset.findById(rid);
                var row = this.dataset.indexOf(record);
                var name = Ext.fly(target).getAttributeNS("","dataindex");
                this.showEditor(row,name);
                this.fireEvent('cellclick', this, row, name, record);
                this.fireEvent('rowclick', this, row, record);
            }else if(atype=='table.rowcheck'){               
                var cb = Ext.get(this.id+'__'+rid);
                if(cb.hasClass('item-ckb-readonly-u')||cb.hasClass('item-ckb-readonly-c'))return;
                var checked = cb.hasClass('item-ckb-c');
                (checked) ? this.dataset.unSelect(rid) : this.dataset.select(rid);
            }else if(atype=='table.rowradio'){
            	var cb = Ext.get(this.id+'__'+rid);
                if(cb.hasClass('item-radio-img-readonly-u')||cb.hasClass('item-radio-img-readonly-c'))return;
                this.dataset.select(rid);
            }
        }
    },
    onUpdate : function(ds,record, name, value){
        this.setSelectStatus(record);
    	var div=Ext.get(this.id+'_'+name+'_'+record.id);
        if(div){
            var c = this.findColByName(name);
            var editor = this.getEditor(c,record);            
            if(editor!='' && ($(editor) instanceof $A.CheckBox)){
            	this.renderEditor(div,record,c,editor);
            }else{
            	//考虑当其他field的值发生变化的时候,动态执行其他带有renderer的
                var text =  this.renderText(record,c, value);
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
    onLoad:function(){
    	this.clearBody();
    	var l=this.dataset.data.length;
    	if(l==0)this.createEmptyRow();
		for(var i=0;i<l;i++){
            this.createRow(this.dataset.getAt(i),i);
        }
        this.drawFootBar();
        $A.Masker.unmask(this.wrap);
        this.fireEvent('render',this)
	},
	onValid : function(ds, record, name, valid){
        var c = this.findColByName(name);
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
    onAdd : function(ds,record,index){
    	this.removeEmptyRow();
        this.createRow(record,this.dataset.data.length-1);
        this.selectRow(this.dataset.indexOf(record));
        this.setSelectStatus(record);
    },
    onRemove : function(ds,record,index){
        var row = Ext.get(this.id+'-'+record.id);
        if(row)row.remove();
        this.selectTr=null;
        $A.Masker.unmask(this.wrap);
        this.drawFootBar();
    },
	clearBody:function(){
		while(this.tbody.dom.childNodes.length){
			this.tbody.dom.removeChild(this.tbody.dom.firstChild);
		}
		this.emptyRow = null;
	},
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
    onEditorBlur : function(e){
        if(this.currentEditor && !this.currentEditor.editor.isEventFromComponent(e.target)) { 
            this.hideEditor();
        }
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
    onIndexChange:function(ds, r){
        var index = this.getDataIndex(r.id);
        if(index == -1)return;
        if(r != this.selectRecord){
            this.selectRow(index, false);
        }
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
    onBeforeRemove : function(){
        $A.Masker.mask(this.wrap,_lang['grid.mask.remove']);
    },
    onBeforeLoad : function(){
        $A.Masker.mask(this.wrap,_lang['grid.mask.loading']);
    },
    onAfterSuccess : function(){
        $A.Masker.unmask(this.wrap);
    },
    onBeforSubmit : function(ds){
    	$A.Masker.mask(this.wrap,_lang['grid.mask.submit']);
    },
    onAjaxFailed : function(res,opt){
        $A.Masker.unmask(this.wrap);
    }
})