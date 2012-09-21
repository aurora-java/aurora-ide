$A.MenuHelper={
	getMenuAt :function(index){
		return (!this.children||this.children.length==0)?null:this.children[index];
	},
	getMenus : function(){
		return this.getMenusByText();
	},
	getMenusByText : function(text){
		var results=[];
		if(!this.children)return results;
		if(this.children.length==0){
			this.addMenus(this.options);this.initMenus=true;
			if(this.children.length==0)return results;
		}
		for(var i=0;this.children[i];i++){
			if(typeof text=='undefined'||text==this.children[i].text)results.add(this.children[i]);
			if(this.children[i].children)results=results.concat(this.children[i].getMenusByText(text));
		}
		return results;
	},
	getMenuById : function(id){
		if(this.dataId==id)return this;
		if(!this.children||this.children.length==0)return null;
		for(var i=0;this.children[i];i++){
			var menu=this.children[i].getMenuById(id);
			if(menu)return menu;
		}
	},
	findAncestorMenus : function(){
		return this.findAncestorMenusByText();
	},
	findAncestorMenusByText : function(text){
		if(!this.parent)return [];
		var results=[];
		if((typeof text=='undefined'||text==this.parent.text)&&this.parent!=this.bar)results.add(this.parent);
		results=results.concat(this.parent.findAncestorMenusByText(text));
		return results;
	},
	parentMenu : function(){
		return this.parent;
	},
	perviewMenu : function(){
		return this.parent.children[(this.index-1)==-1?this.parent.children.length-1:this.index-1];
	},
	nextMenu : function(){
		return this.parent.children[(this.index+1)==this.parent.children.length?0:this.index+1];
	}
}
/**
 * @class Aurora.MenuBar
 * @extends Aurora.Component
 * <p>树形组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.MenuBar=Ext.extend($A.Component,Ext.apply({
	constructor: function(config) {
		this.isActive=false,this.needHide=false,this.children=[],this.selectIndex = null,this.altKeyAccess=false;
		$A.MenuBar.superclass.constructor.call(this, config);
		this.handlerfield=config.handlerfield||'handler';
		this.menutype=config.menutype||'type';
		this.checked=config.checked||'checked';
	},
	initComponent : function(config){
		$A.MenuBar.superclass.initComponent.call(this,config);
		if(config.focus){
			Ext.fly(config.focus).set({'tabIndex':'-1'});
			Ext.fly(config.focus).setStyle({'outline':'none'});
		}
	},
	processListener: function(ou){
    	$A.MenuBar.superclass.processListener.call(this,ou);
    	Ext.fly(document)[ou]('mousedown',this.onMouseDown,this);
    	Ext.fly(document)[ou]('mouseup',this.onMouseUp,this);
    	Ext.getBody()[ou]('selectstart',this.preventMenuAndSelect,this);
    	Ext.getBody()[ou]('contextmenu',this.preventMenuAndSelect,this);
    	Ext.fly(this.focus||document)[ou]('keyup',this.onKeyUp,this);
    	Ext.fly(this.focus||document)[ou]('keydown',this.onKeyDown,this);
    	if(ou=='on')Ext.onReady(function(ou){this.processIframeListener(ou);}.createDelegate(this,[ou]))
    	else this.processIframeListener(ou);
    },
    processIframeListener:function(ou){//解决iframe中子窗口无法响应父窗口的事件
    	var frames=document.getElementsByTagName('iframe');
    	for(var i=0;frames[i];i++){
			Ext.fly(frames[i])[ou]('load',function(frame){
	    		Ext.fly(frame.contentWindow.document)[ou]('mousedown',this.onMouseDown,this);
	    		Ext.fly(frame.contentWindow.document)[ou]('mouseup',this.onMouseUp,this);
	    		if(!this.focus||Ext.fly(this.focus).contains(frame)){
	    			Ext.fly(frame.contentWindow.document)[ou]('keyup',this.onKeyUp,this);
    				Ext.fly(frame.contentWindow.document)[ou]('keydown',this.onKeyDown,this);
	    		}
			}.createDelegate(this,[frames[i]]))
			if(this.urltarget&&this.urltarget==frames[i].name)this.targetFrame=frames[i];
    	}
    },
    processDataSetLiestener: function(ou){
		var ds = this.dataset;
		if(ds){
			ds[ou]('update', this.onUpdate, this);
			ds[ou]('load', this.onLoad, this);
		}
	},
    bind : function(ds){
    	if(typeof(ds)==='string'){
			ds = $(ds);
			if(!ds) return;
		}
		this.dataset = ds;
		this.processDataSetLiestener('on');
    	this.onLoad();
    },
    renderText : function(data){
    	if(!this.renderer)return data.get(this.displayfield);
    	return $A.getRenderer(this.renderer).call(window,data.get(this.displayfield),data,this.context);
    },
    onUpdate : function(ds,record,name,value){
    	if(name==this.displayfield)record.menu.setText(this.value);
    	else if(name==this.checked)record.menu.check(value);
    },
    onLoad : function(){
    	var options=[],map={},datas=this.dataset.data;
    	for(var i=0;datas[i];i++){
    		map[datas[i].get(this.idfield)]={record:datas[i],text:datas[i].get(this.displayfield),renderText:this.renderText(datas[i]),index:datas[i].get(this.sequence)||Number.MAX_VALUE,icon:datas[i].get(this.iconfield),dataId:datas[i].get(this.idfield)};
    		if(datas[i].get(this.menutype)){
    			var types=datas[i].get(this.menutype).match(/^([^\[\]]+)\[?([^\[\]]+)?\]?$/);
    			Ext.apply(map[datas[i].get(this.idfield)],{type:types[1],checked:datas[i].get(this.checked)=="true"||false});
    			if(types[2])Ext.apply(map[datas[i].get(this.idfield)],{groupName:types[2]});
    		}
    		if(datas[i].get(this.handlerfield))Ext.apply(map[datas[i].get(this.idfield)],{listeners:{'mouseup':function(handler,record){return function(){window[handler].apply(window,Ext.toArray(arguments).concat(record))}}(datas[i].get(this.handler),datas[i])}});
    		if(datas[i].get(this.urlfield))Ext.apply(map[datas[i].get(this.idfield)],{listeners:{'submit':this.directURL.createDelegate(this,[datas[i].get(this.urlfield),datas[i].get(this.displayfield)])}});
    	}
    	for(var i=0;datas[i];i++){
    		var pid=datas[i].get(this.parentfield);
    		if(!pid||pid==this.rootid||pid<0)options.add(map[datas[i].get(this.idfield)]);
    		else{
    			if(!map[pid].options)map[pid].options=[];
    			map[pid].options.add(map[datas[i].get(this.idfield)]);
    		}
    	}
    	this.addMenus(options.sort(this.sortOptions));
    },
    directURL : function(url,title){
    	if(this.targetFrame)this.targetFrame.setAttribute('src',url+(url.match(/\?/)?"&":"?")+"randomnumber="+Math.floor(Math.random()*100000));
    	else if(this.urltarget)window.open(url,this.urltarget);
    	else new $A.Window({title:title,url:url,width:Ext.fly(document).child('html').getWidth()-100,height:Ext.fly(document).child('html').getHeight()-100})
    },
    sortOptions : function(o1,o2){
    	if(o1.options)o1.options.sort($A.MenuBar.prototype.sortOptions);
    	if(o2.options)o2.options.sort($A.MenuBar.prototype.sortOptions);
    	return parseFloat(o1.index)-parseFloat(o2.index);
    },
    preventMenuAndSelect :function(e){
    	if(this.isAncestor(e.target)){e.stopEvent();return false;}
    },
    pressKeyDown : function(){
		if(!this.children[this.selectIndex].isActive){
			this.children[this.selectIndex].show();
			this.children[this.selectIndex].selectIndex=0;
			this.children[this.selectIndex].children[0].active();
		}else {
			if(this.children[this.selectIndex].selectIndex!=null)this.children[this.selectIndex].pressKeyDown();
			else{
				this.children[this.selectIndex].selectIndex=0;
				this.children[this.selectIndex].children[0].active();
			}
		}
    },
    pressKeyUp : function(){
		if(!this.children[this.selectIndex].isActive){
			this.children[this.selectIndex].show();
			this.children[this.selectIndex].selectIndex=0;
			this.children[this.selectIndex].children[0].active();
		}else {
			if(this.children[this.selectIndex].selectIndex!=null)this.children[this.selectIndex].pressKeyUp();
			else{
				this.children[this.selectIndex].selectIndex=this.children[this.selectIndex].children.length-1;
				this.children[this.selectIndex].children[this.children[this.selectIndex].selectIndex].active();
			}
		}
    },
    pressKeyLeft : function(){
		if(!this.children[this.selectIndex].isActive){
			this.children[this.selectIndex].inactive();
			this.selectIndex=(--this.selectIndex<0)?this.children.length+this.selectIndex:this.selectIndex;
			this.children[this.selectIndex].active();
		}else this.children[this.selectIndex].pressKeyLeft();
    },
    pressKeyRight : function(){
		if(!this.children[this.selectIndex].isActive){
			this.children[this.selectIndex].inactive();
			this.selectIndex=(++this.selectIndex==this.children.length)?0:this.selectIndex;
			this.children[this.selectIndex].active();
		}else{
			this.children[this.selectIndex].selectIndex=this.children[this.selectIndex].selectIndex||0;
			this.children[this.selectIndex].pressKeyRight();
		}
    },
    pressKeyEnter : function(){
    	if(!this.children[this.selectIndex].isActive) this.pressKeyDown();
    	else this.children[this.selectIndex].pressKeyEnter();
    },
    onKeyUp : function(e){
    	if(this.children.length==0)return;
    	if(e.keyCode==18){
	    	if(!this.isActive){
	    		this.isActive=true;
	    		this.children[0].active(e);
	    	}else this.isActive=false;
    	}
    	e.stopEvent();
    },
    onKeyDown : function(e){
    	if(this.children.length==0)return;
    	if(this.isActive){
    		if(e.altKey&&this.selectIndex!=null){
    			this.children[this.selectIndex].inactive(e);
    			this.children[this.selectIndex].hide();
				this.selectIndex=null;
    		}
    		switch(e.keyCode){
    			case 13:this.pressKeyEnter();break;
    			case 37:this.pressKeyLeft();break;
    			case 38:this.pressKeyUp();break;
    			case 39:this.pressKeyRight();break;
    			case 40:this.pressKeyDown();break;
    		}
    	}
    	e.stopEvent();
    },
	onMouseDown : function(e){
		if(this.selectIndex==null||this.children.length==0)return;
		if(e.button==0){
			if(this.wrap.contains(e.target))this.needHide=this.isActive;
			if(this.isAncestor(e.target)){
				this.isActive=true;
				this.children[this.selectIndex].show();
			}else{
				this.children[this.selectIndex].inactive();
				this.children[this.selectIndex].hide();
				this.isActive=false;
			}
		}
	},
	onMouseUp : function(e){
		if(this.selectIndex==null||this.children.length==0)return;
		if(e.button==0){
			if(this.needHide||!this.isAncestor(e.target)){
				this.isActive=false;
				this.children[this.selectIndex].hide();
			}else if(!this.isActive)this.children[this.selectIndex].hide();
		}
	},
	addMenus : function(options){
		for(var i=0,j=this.children.length;i<options.length;i++){
			var menu=null,_id=this.id+"-node"+j;
			new Ext.Template(this.childTpl).append(this.wrap.dom,{id:_id});
			menu=new Aurora[options[i].options?'Menu':'MenuItem'](Ext.apply(options[i],{id:_id,parent:this,bar:this,index:j}));
			options[i].record.menu=menu;
			delete options[i].record;
			this.children.push(menu);j++;
		}
	},
	clearMenus : function(){
		while(this.children.length){
			this.children.shift().destroy();
		}
		this.isActive=false,this.needHide=false,this.selectIndex = null,this.altKeyAccess=false;
	},
	destroy : function(){
		delete this.children;
		delete this.isActive;
		delete this.needHide;
		$A.Menu.superclass.destroy.call(this);
	},
	isAncestor : function(el){
		if(this.wrap.dom!=el&&this.wrap.contains(el))return true;
		for (var i=0;i<this.children.length;i++) {
			if (this.children[i].isAncestor&&this.children[i].isAncestor(el))return true;
		}
		return false;
	},
	childTpl : '<LI id="{id}" class="item-menu"></LI>'
},$A.MenuHelper));

/**
 * @class Aurora.MenuItem
 * @extends Aurora.Component
 * <p>树形组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.MenuItem=Ext.extend($A.Component,Ext.apply({
	constructor: function(config) {
		this.hasIcon=false;
		$A.MenuItem.superclass.constructor.call(this, config);
	},
	initComponent : function(config){
		$A.MenuItem.superclass.initComponent.call(this,config);
		this.el=new Ext.Template(this.parent===this.bar?this.menuBarTpl:this.menuTpl).append(this.wrap.dom,{text:this.renderText,width:'16px'},true);
		if(this.parent!==this.bar){
			this.setIcon();
			if(this.type&&!this.children)this.initMenuType();
		}
	},
	processListener: function(ou){
    	$A.MenuItem.superclass.processListener.call(this,ou);
    	this.wrap[ou]('mouseup',this.onMouseUp,this);
    },
    processMouseOverOut : function(ou){
        this.wrap[ou]('mouseover',this.onMouseOver,this);
    	this.wrap[ou]('mouseout',this.onMouseOut,this);
    },
	initEvents : function(){
		$A.MenuItem.superclass.initEvents.call(this);
		this.addEvents(
		/**
         * @event submit
         * menu的url定向.
         */
		'submit',
		/**
         * @event mouseup
         * menu点击事件.
         */
		'mouseup');
	},
	getWidth : function(){
		return this.wrap.child('td.item-menu-text').getWidth()+(this.parent==this.bar?0:72);
	},
	initMenuType : function(){
		if(this.type=='radio')this.wrap.child('td.item-menu-icon div').addClass("type-radio");
		else if(this.type=='checkbox')this.wrap.child('td.item-menu-icon div').addClass("type-checkbox");
		this.check();
	},
	check : function(value){
		this.checked=value;
		this.wrap.child('td.item-menu-icon div')[this.checked?'addClass':'removeClass']('check');
	},
	getBindingRecord : function(){
		return this.record;
	},
	setText : function(text){
		this.text=text;
		this.renderText=this.bar.renderText(this.record);
		this.el.update(this.renderText);
	},
	getText : function(){
		return this.text;
	},
	setIcon : function(icon){
		if(!(icon||(icon=this.icon))||this.type)return;
		var _icon=icon.match(/^([^\?]*)\??([^?]*)?$/);
		this.wrap.child('td.item-menu-icon div').setStyle({'background-image':'url('+(_icon[1].match(/^[\/]{1}/)?this.bar.context:'')+_icon[1]+')','background-position':_icon[2]||'0 0'})
		this.hasIcon=true;
	},
	getIcon : function(){
		return this.icon;
	},
	clearIcon : function(){
		if(!this.hasIcon)return;
		this.wrap.child('div.item-menu-icon').setStyle({'background-image':'none','background-position':'0 0'})
		this.parent.icons.remove(this);
		if(this.parent.icons.length==0){
			for(var list=this.parent.container.query('div.item-menu-icon');list.length;){
				Ext.fly(list.shift()).setWidth(0);
			}
		}
		this.hasIcon=false;
	},
	toggleIcon : function(){
		this[this.hasIcon?'clearIcon':'setIcon'].apply(this);
	},
	pressKeyRight : function(){
		this.bar.children[this.bar.selectIndex].hide();
		this.bar.pressKeyRight();
		this.bar.pressKeyDown();
	},
    pressKeyLeft : function(){
    	if(this.parent==this.bar){
    		this.bar.children[this.bar.selectIndex].hide();
			this.bar.pressKeyLeft();
			this.bar.pressKeyDown();
    	}else{
			this.parent.children[this.parent.selectIndex].hide();
    	}
    },
    pressKeyEnter : function(){
    	this.submit();
    },
    submit : function(){
    	if(!this.children){
			if(this.parent!=this.bar){
				this.bar.children[this.bar.selectIndex].inactive();
				this.bar.children[this.bar.selectIndex].hide();
				this.bar.isActive=false;
			}
			if(this.type=='checkbox'){
				this.record.set(this.bar.checked,!this.checked);
			}else if(this.type=='radio'){
				if(this.checked==true)return;
				this.record.set(this.bar.checked,true);
				if(this.groupName){
					for(var i=0,list=this.parent.groups[this.groupName];i<list.length;i++){
						if(list[i]!=this){
							list[i].record.set(this.bar.checked,false);
						}
					}
				}
			}
    	}
    	this.fireEvent('mouseup',this.bar,this);
    	this.fireEvent('submit',this.bar);
    },
	onMouseUp : function(e){
		if(e.button==0)this.submit();
	},
	onMouseOver : function(e){
		this.active();
	},
	onMouseOut : function(e){
		if(!this.isActive)this.inactive();
		if(this.showIntervalId)clearInterval(this.showIntervalId);
	},
	active : function(){
		this.wrap.addClass('item-menu-current');
		if(this.parent.selectIndex===this.index)return;
		if (this.parent.selectIndex!=null){
			this.parent.children[this.parent.selectIndex].inactive();
			if(this.parent==this.bar)this.parent.children[this.parent.selectIndex].hide();
			else setTimeout(this.parent.children[this.parent.selectIndex].hide.createDelegate(this.parent.children[this.parent.selectIndex]),299);
		}
		this.parent.selectIndex=this.index;
	},
	inactive : function(){
		this.wrap.removeClass('item-menu-current');
	},
	show : function(){
	},
	hide : function(){
	},
	destroy : function(){
		delete this.el;
		delete this.bar;
		$A.MenuItem.superclass.destroy.call(this);
	},
	menuTpl :['<TD class="item-menu-icon" align="center"><DIV></DIV></TD>',
				'<TD class="item-menu-text">{text}</TD>',
				'<TD></TD>'],
	menuBarTpl:'<SPAN class="item-menu-text">{text}</SPAN>'
},$A.MenuHelper));

/**
 * @class Aurora.Menu
 * @extends Aurora.MenuItem
 * <p>树形组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Menu=Ext.extend($A.MenuItem,{
	constructor: function(config) {
		this.children=[],this.selectIndex=null,this.groups={},this.isActive=false,this.initMenus=false;
		$A.Menu.superclass.constructor.call(this, config);
	},
	initComponent : function(config){
		$A.Menu.superclass.initComponent.call(this,config);
		this.shadow=new Ext.Template(this.shadowTpl).append(document.body, {zIndex:9999+this.index}, true);
		this.shadow.addClass('item-menu-hide');
		this.container=new Ext.Template(this.containerTpl).append(document.body,{zIndex:10000+this.index},true);
	},
	addMenus : function(options){
		if(!this.options)return;
		var width=0;
		for(var i=0,j=this.children.length;i<options.length;i++){
			var menu=null,_id=this.id+"-node"+j;
			new Ext.Template(this.childTpl).append(this.container.dom,{id:_id});
			menu=new Aurora[options[i].options?'Menu':'MenuItem'](Ext.apply(options[i],{id:_id,parent:this,bar:this.bar,index:j}));
			if(menu.groupName){
				if(!this.groups[menu.groupName])this.groups[menu.groupName]=[];
				this.groups[menu.groupName].add(menu);
			}
			this.children.push(menu);
			options[i].record.menu=menu;
			delete options[i].record;
			width=menu.getWidth()>width?menu.getWidth():width;
			j++;
		}
		this.container.setWidth(width);
	},
	pressKeyRight : function(eventName){
    	if(this.children[this.selectIndex]){
			if(!this.children[this.selectIndex].isActive){
				if(this.children[this.selectIndex].children){
					this.children[this.selectIndex].show();
					this.children[this.selectIndex].selectIndex=0;
					this.children[this.selectIndex].children[0].active();
				}else this.children[this.selectIndex][eventName||'pressKeyRight']();
			}else{
				if(this.children[this.selectIndex].selectIndex!=null)this.children[this.selectIndex][eventName||'pressKeyRight']();
				else{
					this.children[this.selectIndex].selectIndex=0;
					this.children[this.selectIndex].children[0].active();
				}
			}
		}
    },
    pressKeyLeft : function(){
    	if(this.parent==this.bar&&(this.selectIndex==null||!this.children[this.selectIndex].isActive)){
    		this.bar.children[this.bar.selectIndex].hide();
			this.bar.pressKeyLeft();
			this.bar.pressKeyDown();
    	}else{
    		if(!this.children[this.selectIndex].isActive)this.parent.children[this.parent.selectIndex].hide();
    		else{
    			if(this.children[this.selectIndex].selectIndex!=null)this.children[this.selectIndex].pressKeyLeft();
    			else this.children[this.selectIndex].hide();
    		}
    	}
    },
    pressKeyUp : function(){
    	if(this.children[this.selectIndex]){
			if(!this.children[this.selectIndex].isActive){
				this.children[this.selectIndex].inactive();
				this.selectIndex=(--this.selectIndex<0)?this.children.length+this.selectIndex:this.selectIndex;
				this.children[this.selectIndex].active();
			}else{
				if(this.children[this.selectIndex].selectIndex!=null)this.children[this.selectIndex].pressKeyUp();
				else{
					this.children[this.selectIndex].selectIndex=this.children[this.selectIndex].children.length-1;
					this.children[this.selectIndex].children[this.children[this.selectIndex].selectIndex].active();
				}
			}
		}
    },
    pressKeyDown : function(){
    	if(this.children[this.selectIndex]){
			if(!this.children[this.selectIndex].isActive){
				this.children[this.selectIndex].inactive();
				this.selectIndex=(++this.selectIndex==this.children.length)?0:this.selectIndex;
				this.children[this.selectIndex].active();
			}else{
				if(this.children[this.selectIndex].selectIndex!=null)this.children[this.selectIndex].pressKeyDown();
				else{
					this.children[this.selectIndex].selectIndex=0;
					this.children[this.selectIndex].children[0].active();
				}
			}
		}
    },
    pressKeyEnter : function(){
    	this.pressKeyRight('pressKeyEnter');
    },
    onMouseOver : function(e){
		$A.Menu.superclass.onMouseOver.call(this,e);
		if(this.parent===this.bar)this.show();
		else this.showIntervalId=setInterval(this.show.createDelegate(this),300);
	},
	show : function(){
		$A.Menu.superclass.show.call(this);
		if (!this.parent.isActive)return;
		if(!this.initMenus){this.addMenus(this.options);this.initMenus=true;}
		var xy=this.wrap.getXY(),x,y,
			W=this.container.getWidth(),H=this.container.getHeight(),
			PH=this.wrap.getHeight(),PW=this.wrap.getWidth(),
			BH=$A.getViewportHeight()-3,BW=$A.getViewportWidth()-3;
		if(this.parent===this.bar){
			x=(xy[0]+W)>BW?((BW-W)<0?xy[0]:(BW-W)):xy[0];
			y=(xy[1]+PH+H)>BH?((xy[1]-H)<0?(xy[1]+PH):(xy[1]-H)):(xy[1]+PH);
		}else{
			x=(xy[0]+PW+W)>BW?((xy[0]-W)<0?(xy[0]+PW):(xy[0]-W)):(xy[0]+PW);
			y=(xy[1]+PH+H)>BH?((BH-H)<0?xy[1]:(BH-H)):xy[1];
		}
		this.container.moveTo(x,y);
		this.shadow.moveTo(x+3,y+3);
		this.container.removeClass('item-menu-hide');
		this.shadow.removeClass('item-menu-hide');
		this.shadow.setHeight(this.container.getHeight());
		this.shadow.setWidth(this.container.getWidth());
		this.isActive=true;
		if(this.intervalId)clearInterval(this.intervalId);
	},
	hide : function(){
		$A.Menu.superclass.hide.call(this);
		this.container.addClass('item-menu-hide');
		this.shadow.addClass('item-menu-hide');
		this.shadow.setHeight(this.container.getHeight());
		this.shadow.setWidth(this.container.getWidth());
		this.container.moveTo(0,0);
		this.shadow.moveTo(0,0);
		if(this.selectIndex!=null){this.children[this.selectIndex].inactive();this.children[this.selectIndex].hide();}
		this.isActive=false;
		this.selectIndex=null;
	},
	destroy : function(){
		this.container.remove();
		this.shadow.remove();
		delete this.children;
		delete this.groups;
		$A.Menu.superclass.destroy.call(this);
	},
	isAncestor : function(el){
		if(this.container.dom!=el&&this.container.contains(el))return true;
		for (var i=0;i<this.children.length;i++) {
			if (this.children[i].isAncestor&&this.children[i].isAncestor(el))return true;
		}
		return false;
	},
	menuTpl :['<TD class="item-menu-icon" align="center"><DIV></DIV></TD>',
				'<TD class="item-menu-text">{text}</TD>',
				'<TD class="item-menu-arrow" align="center"><DIV></DIV></TD>'],
	containerTpl : '<TABLE cellspacing="0" class="item-menu-container item-menu-hide" style="z-index:{zIndex}"></TABLE>',
	shadowTpl : '<DIV class="item-shadow" style="z-index:{zIndex}"></DIV>',
	childTpl : '<TR id="{id}" class="item-menu"></TR>',
	hSplitTpl : '<TR class="item-menu-h-split"><TD>&nbsp;</TD></TR>'
});
delete $A.MenuHelper;