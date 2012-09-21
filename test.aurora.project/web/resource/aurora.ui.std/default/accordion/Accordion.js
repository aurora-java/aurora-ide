/**
 * @class Aurora.Accordion
 * @extends Aurora.Component
 * <p>Accordion组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Accordion = Ext.extend($A.Component,{
	constructor: function(config){
		$A.Accordion.superclass.constructor.call(this,config);
	},
	initComponent:function(config){
		$A.Accordion.superclass.initComponent.call(this, config);
		this.selectedItems=[];
		this.frequency=this.frequency||20;
		this.actionTimes=this.actionTimes||2;
		this.currentTime=1;
		this.accordions=this.wrap.query('div.item-accordion');
		this.strips = this.wrap.query('div.strip');
		this.bodys = this.wrap.query('div.item-accordion-body');
		this.stripheight=this.stripheight||25;
		for(var i=0;i<this.accordions.length;i++){
			var accordion=Ext.get(this.accordions[i]);
			if(accordion.hasClass('selected')){
				this.selectedItems.add(accordion);
				this.load.call(this,i);
			}
		}
	},
	processListener: function(ou){
    	$A.Accordion.superclass.processListener.call(this,ou);
    	this.wrap[ou]('click',this.onClick, this);
    },
	initEvents:function(){
		$A.Accordion.superclass.initEvents.call(this);   
		this.addEvents(
		/**
         * @event select
         * 选择事件.
         * @param {Aurora.Accordion} tab Accordion对象.
         * @param {Number} index 序号.
         */
		'select');
		
	},
	/**
	 * 选中某个Accordion页
	 * @param {Number} index AccordionItem序号
	 */
	selectAccordionIndex:function(index){
		if(!this.accordions[index])return;
		if(this.singlemode)this.selectedItems=[];
		this.selectedItems.add(Ext.get(this.accordions[index]));
		this.intervalId=setInterval(this.go.createDelegate(this,[index]),this.frequency);
		this.go.call(this,index);
	},
	go:function(index){
		for(var i=0;i<this.accordions.length;i++){
			var accordion=Ext.get(this.accordions[i]);
			if(accordion.hasClass('selected')){
				if(this.singlemode||i==index)accordion.setHeight(this.stripheight+Ext.fly(this.bodys[i]).getHeight()*(1-this.currentTime/this.actionTimes));
			}else if(i==index) accordion.setHeight(this.stripheight+Ext.fly(this.bodys[i]).getHeight()*(this.currentTime/this.actionTimes));
		}
		if(this.actionTimes==this.currentTime){
			for(var i=0;i<this.accordions.length;i++){
				var accordion=Ext.get(this.accordions[i]);
				if(accordion.hasClass('selected')){
					if(this.singlemode||i==index){
						accordion.removeClass('selected');
						Ext.fly(this.bodys[i]).hide();
						this.selectedItems.remove(accordion);
					}
				}else if(i==index){
					accordion.addClass('selected');
					Ext.fly(this.bodys[i]).show();
					this.load(index);
				}
			}
			clearInterval(this.intervalId);
			this.currentTime=1;
		}else this.currentTime++;
	},
	onClick:function(e,t){
		t = Ext.fly(t);
		if(t.hasClass('item-accordion-btn'))t = t.parent();
		if(t.hasClass('strip')){
			for(var i=0,l=this.strips.length;i<l;i++){
				if(t.dom==this.strips[i]){
					this.selectAccordionIndex(i);
					break;
				}
			}
		}
	},
	onMouseOver : function(e,t){
		t = Ext.fly(t);
		if(t.hasClass('item-accordion-btn'))t.parent().addClass('strip-over')
    	$A.Accordion.superclass.onMouseOver.call(this,e,t);   
    },
    onMouseOut : function(e,t){
    	t = Ext.fly(t);
		if(t.hasClass('item-accordion-btn'))t.parent().removeClass('strip-over')
    	$A.Accordion.superclass.onMouseOut.call(this,e,t);   
    },
	showLoading : function(dom){
    	dom.update(_lang['accordion.loading']);
    	dom.setStyle('text-align','center');
    	dom.setStyle('line-height',5);
    },
    clearLoading : function(dom){
    	dom.update('');
    	dom.setStyle('text-align','');
    	dom.setStyle('line-height','');
    },
	load : function(index){
		var url=this.items[index].ref, dom=Ext.get(this.bodys[index]);
		if(!url||dom.isLoaded){
			this.fireEvent('select', this, index);
			return;
		}
		dom.isLoaded=true;
		dom.cmps={};
       	url=url+(url.indexOf('?') !=-1?'&':'?')+'_vw='+this.width+'&_vh='+(this.height-Ext.fly(this.strips[0]).getHeight());
		this.showLoading(dom);
		var sf = this;
    	Ext.Ajax.request({
			url: url,
		   	success: function(response, options){
		    	sf.clearLoading(dom);
		    	var html = response.responseText;		    	
		    	dom.update(html,true,function(){
                    sf.fireEvent('select', sf, index);
		    	},dom);
		    }
		});		
    },
    setHeight : function(h){
    	if(this.height == h) return;
    	var l=this.accordions.length,h = Math.max(h,this.stripheight*l);
    	this.height=h;
    	for(var i=0;i<l;i++){
    		Ext.fly(this.bodys[i]).setHeight((h-this.stripheight*l)/(this.singlemode?1:l));
    	}
    	for(var i=0;i<this.selectedItems.length;i++){
    		this.selectedItems[i].setHeight(this.singlemode?(h-this.stripheight*(l-1)):(h/l));
    	}
    },
    destroy : function(){
        var bodys = this.bodys;
    	for(var i=0;i<bodys.length;i++){
    		var body = Ext.get(bodys[i]),
    		cmps=body.cmps;
    		if(cmps){
	    		for(var key in this.cmps){
		            var cmp = this.cmps[key];
	        		if(cmp.destroy){
	        			try{
	        				cmp.destroy();
	        			}catch(e){
	        				alert('销毁Accordion出错: ' + e)
	        			}
	        		}
	        	}
    		}
    	}
		$A.Accordion.superclass.destroy.call(this); 
	}
});