/**
 * @class Aurora.AccordionMenu
 * @extends Aurora.Component
 *          <p>
 *          导航
 * @author shengbo.huang@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$A.AccordionMenu = Ext.extend($A.Component, {
	constructor : function(config) {
		$A.AccordionMenu.superclass.constructor.call(this, config);
		this.context = config.context || '';
	},
	initComponent : function(config) {
		$A.AccordionMenu.superclass.initComponent.call(this, config);
		this.body = this.wrap.child('ul[atype=navigationbar.body]');
		this.navbar = [];		
	},
	processListener : function(ou) {
		//$A.NavigationBar.superclass.processListener.call(this,ou);
		this.wrap[ou]('click', this.onClick, this);
		if (Ext.isIE6) {
			this.wrap[ou]('mouseover', this.onMouseOver, this);
			this.wrap[ou]('mouseout', this.onMouseOut, this);
		}
	},
	initEvents : function(){
		$A.AccordionMenu.superclass.initEvents.call(this);
		this.addEvents(		
		/**
         * @event click
         * 点击事件.         
         */
		'click'
		);
	},
	bind : function(ds) {
		if (typeof (ds) === 'string') {
			ds = $(ds);
			if (!ds)
				return;
		}
		var sf = this;
		sf.dataset = ds;
		this.initNavBar();
		this.buildView();
		this.showCurrent();
		this.showSubCurrent();
		if (Ext.isIE6)
			this.correctForIE6();
		if (Ext.isIE7 || Ext.isIE8) {			
			Ext.fly('adminmenushadow').removeClass('adminmenushadow');
		}
	},
	showCurrent : function(){
		if(this.menu_id){
			Ext.fly(this.menu_id).addClass('wp-has-current-submenu').removeClass('wp-not-current-submenu');
		}
	},
	showSubCurrent : function(){
		if(this.submenu_id){
			Ext.fly(this.submenu_id).addClass('current');
		}
	},
	createNode : function(record) {
		return {
			record : record,
			children : []
		}
	},
	initNavBar : function() {
		if(!this.idfield || !this.parentfield)
			return;
		var map1 = {};
		var map2 = {};
		var datas = this.dataset.data;
		var l = datas.length;
		for ( var i = 0; i < l; i++) {
			var record = datas[i];
			var id = record.get(this.idfield);
			var node = this.createNode(record);
			map1[id] = node;
			map2[id] = node;
		}
		for ( var key in map2) {
			var node = map2[key];
			var record = node.record;
			var pid = record.get(this.parentfield);
			var parent = map2[pid];
			if (parent) {
				parent.children.add(node);
				delete map1[key];
			}
		}
		for ( var key in map1) {
			var node = map2[key];
			this.navbar.add(node);
		}
	},	
	sort : function(array, sequence){
		array.sort(function(a,b){
			var n1 = a.record.get(sequence)||Number.MAX_VALUE;
            var n2 = b.record.get(sequence)||Number.MAX_VALUE;
            return parseFloat(n1)-parseFloat(n2);
		});
		var len = array.length;
		for(var i = 0; i < len; i++){
       	    var n = array[i];
       	    this.sort(n.children,sequence)
        }
	},
	buildView : function() {	
		if(this.sequencefield && this.sequencefield != ''){
			this.sort(this.navbar, this.sequencefield);
		}
		var l = this.navbar.length;	
		for ( var i = 0; i < l; i++) {
			var record = this.navbar[i].record;	
			var children = this.navbar[i].children;
			var sub_length = children.length;
			var li = document.createElement('li');
			li.id = record.id;
			li.menu_id = record.id;
			li.className='wp-first-item wp-not-current-submenu wp-has-submenu wp-menu-open menu-top menu-top-first menu-icon-dashboard menu-top-last';
			var divImg = document.createElement('div');
			divImg.className='wp-menu-image';
			var div = document.createElement('div');
			divImg.appendChild(div);
			li.appendChild(divImg);
			if(!Ext.isIE6 && !Ext.isIE7 && !Ext.isIE8){
				var divArrow = document.createElement('div');
				divArrow.className='wp-menu-arrow';
				divArrow.id='wp-menu-arrow';
				divArrow.appendChild(div);
				li.appendChild(divArrow);
			}
			var a = document.createElement('a');
			a.className='mymenu menu-top wp-has-current-submenu';
			var text = document.createTextNode(record.get(this.displayfield));
			var wp_submenu = document.createElement('div');
			wp_submenu.className = 'wp-submenu';
			wp_submenu.id = 'hide-' + li.id;
			var submenu = document.createElement('div');
			submenu.className = 'wp-submenu-wrap';
			wp_submenu.appendChild(submenu);
			var ul = document.createElement('ul');
			submenu.appendChild(ul);
			this.body.dom.appendChild(li);									
			li.appendChild(a);
			li.appendChild(wp_submenu);
			a.appendChild(text);
			for(var j = 0; j < sub_length; j++){
				var sub_record = children[j].record;
				var sub_li = document.createElement('li');
				sub_li.id = sub_record.id;
				sub_li.menu_id = li.id;
				sub_li.submenu_id = sub_li.id;
				ul.appendChild(sub_li);
				var sub_a = document.createElement('a');
				sub_li.appendChild(sub_a);
				if(this.icon && this.icon != ''){
					var img = document.createElement('img');
					img.style.cssText='margin-left:8px;';
					img.src=this.icon;
					sub_a.appendChild(img);
				}
				var span = document.createElement('span');
				span.style.cssText='margin-left:8px;';
				sub_a.appendChild(span);
				var sub_text = document.createTextNode(sub_record.get(this.displayfield));
				span.appendChild(sub_text);
			}
		}
	},
	correctForIE6 : function() {			
		Ext.fly('adminmenushadow').removeClass('adminmenushadow');
		var subMenus = [];
		subMenus = Ext.query('.mymenu');
		for ( var i = 0; i < subMenus.length; i++) {
			var subMenu = new Ext.Element(subMenus[i]);
			subMenu.addClass('myMenu');
		}		
	},
	onClick : function(event){
		var elem = Ext.fly(event.target).findParent('li');
		if(!elem)return;		
		var record = this.dataset.findById(elem.id);
		this.fireEvent('click', this, record, elem.menu_id, elem.submenu_id);		
	},
	onMouseOver : function(event){
		var elem = Ext.fly(event.target).findParent('li');
		if(!elem)return;
		if(elem.menu_id==this.menu_id)return;					
		var menu = 'hide-' + elem.menu_id;
		Ext.fly(menu).setStyle('display', 'block').setStyle('margin-left','-29px');
	},
	onMouseOut : function(event){
		var elem = Ext.fly(event.target).findParent('li');
		if(!elem)return;
		if(elem.menu_id==this.menu_id)return;
		var menu = 'hide-' + elem.menu_id;
		Ext.fly(menu).setStyle('display', 'none');
	}
});