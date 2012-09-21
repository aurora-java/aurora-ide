/**
 * @class Aurora.Tab
 * @extends Aurora.Component
 * Tab组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Tab = Ext.extend($A.Component,{
    sd:'scroll-disabled',
    tslo:'tab-scroll-left-over',
    tsro:'tab-scroll-right-over',
    tsl:'tab-scroll-left',
    tsr:'tab-scroll-right',
    tc:'tab-close',
    tbo:'tab-btn-over',
    tbd:'tab-btn-down',
    ts:'tab-scroll',
	constructor: function(config){
		this.intervalIds=[];
		$A.Tab.superclass.constructor.call(this,config);
	},
	initComponent:function(config){
		$A.Tab.superclass.initComponent.call(this, config);
		this.scriptwidth = config.scriptwidth||60;
		this.head = this.wrap.child('div[atype=tab.strips]'); 
		this.body = this.wrap.child('div.item-tab-body');
		this.scrollLeft = this.wrap.child('div[atype=scroll-left]');
		this.scrollRight = this.wrap.child('div[atype=scroll-right]');
		this.script = this.head.parent();
		this.sp = this.script.parent();
		this.selectTab(config.selected||0)
	},
	processListener: function(ou){
    	$A.Tab.superclass.processListener.call(this,ou);
    	this.sp[ou]('mousedown',this.onMouseDown, this);
    	this.sp[ou]('mouseup',this.onMouseUp, this);
    	this.sp[ou]('mouseover',this.onMouseOver, this);
    	this.sp[ou]('mouseout',this.onMouseOut, this);
    	this.script[ou]('click',this.onClick, this);
    	this.script[ou]('mousewheel',this.onMouseWheel, this);
    },
	initEvents:function(){
		$A.Tab.superclass.initEvents.call(this);   
		this.addEvents(
		/**
         * @event select
         * 选择事件.
         * @param {Aurora.Tab} tab Tab对象.
         * @param {Number} index 序号.
         */
		'select',
		/**
         * @event beforeopen
         * 选择事件.
         * @param {Aurora.Tab} tab Tab对象.
         * @param {Number} index 序号. 
         */
        'beforeopen'
		);
		
	},
	/**
	 * 选中某个Tab页
	 * @param {Number} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
	 */
	selectTab:function(index){		
		var tab=this.getTab(index);
		if(!tab)return;
		if(tab.strip.hasClass(this.sd)){
			this.selectTab(tab.index+1);
			return;
		}
		var activeStrip = tab.strip,activeBody = tab.body;
		index=tab.index;		
		this.selectedIndex=index;			
		if(activeStrip){
			if(this.activeTab)this.activeTab.replaceClass('active','unactive');
			this.activeTab = activeStrip;
			activeStrip.replaceClass('unactive','active');
			var l=activeStrip.dom.offsetLeft,w=activeStrip.getWidth(),
				sl=this.script.getScroll().left,sw=this.script.getWidth(),hw=this.head.getWidth();
				tr=l+w-sl-sw,tl=sl-l;
			if(tr>0){
				this.scrollRight.removeClass(this.sd);
				this.scrollLeft.removeClass(this.sd);
				this.script.scrollTo('left',sl+tr);
			}else if(tl>0){
				this.scrollLeft.removeClass(this.sd);
				this.script.scrollTo('left',sl-tl);
				this.scrollRight.removeClass(this.sd);
			}
			if(sw+this.script.getScroll().left>=hw){
				this.script.scrollTo('left',hw-sw);
				this.scrollRight.addClass(this.sd);
			}else if(index==0){
				this.script.scrollTo('left',0);
				this.scrollLeft.addClass(this.sd);
			}
		}
		if(activeBody){
			if(this.activeBody){
				this.activeBody.setLeft('-10000px');
				this.activeBody.setTop('-10000px');
			}
			this.activeBody = activeBody;
			activeBody.setLeft('0px');
			activeBody.setTop('0px');
		}
		if(this.items[index].ref && activeBody.loaded!= true){
			this.load(this.items[index].ref,activeBody,index);
			activeBody.loaded = true;
		}else{
            this.fireEvent('select', this, index)
		}
	},	
	stripTpl:['<div class="strip unactive"  unselectable="on" onselectstart="return false;"><div style="height:26px;width:{stripwidth2}px">'
				,'<div class="strip-left"></div>',
				'<div style="width:{stripwidth}px;" class="strip-center"><div class="tab-close"></div>{prompt}</div>',
				'<div class="strip-right"></div>',
			'</div></div>'],
	bodyTpl:'<div style="width:{bodywidth}px;height:{bodyheight}px;left:-10000px;top:-10000px;" class="tab"></div>',
	/**
	 * 打开一个指定引用地址的Tab页，如果该指定的引用地址的页面已经被打开，则选中该Tab页
	 * @param {String} ref Tab页面的引用地址
	 * @param {String} prompt Tab的标题
	 */
	openTab : function(ref,prompt){
		var i=0;
		for(;i<this.items.length;i++){
			if(this.items[i].ref&&this.items[i].ref==ref){
				this.selectTab(i);return;
			}
		}
		var returnValue=this.fireEvent('beforeopen',this,i);
		if(returnValue!=false){
			this.items.push({'ref':ref});
			var stripwidth=$A.TextMetrics.measure(document.body,prompt).width+20;
			stripwidth=stripwidth<this.scriptwidth?this.scriptwidth:stripwidth;
			var width=this.head.getWidth()+stripwidth+6;
			this.head.setWidth(width);
			if(width>this.script.getWidth()){
				this.scrollLeft.setStyle({'display':'block'});
				this.scrollRight.setStyle({'display':'block'});
				this.script.setStyle('padding-left','1px');
			}
			new Ext.Template(this.stripTpl).append(this.head.dom,{'prompt':prompt,'stripwidth':stripwidth,'stripwidth2':stripwidth+6});
			new Ext.Template(this.bodyTpl).append(this.body.dom,{'bodywidth':this.body.getWidth(),'bodyheight':this.body.getHeight()});
			this.selectTab(i);
		}
	},
	/**
	 * 关闭某个Tab页
	 * @param {Integer} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
	 */
	closeTab : function(o){
		var tab=this.getTab(o);
        
		if(!tab)return;
		var strip=tab.strip,body=tab.body,index=tab.index;
		if(!strip.child('div.'+this.tc)){
			$A.showWarningMessage('警告','该Tab页无法被关闭!')
			return;
		}
        if(this.activeBody == tab.body){
            this.activeBody=null;
            this.activeTab=null;
        }
		this.items.splice(index,1);
		var width=this.head.getWidth()-strip.getWidth(),cmps=body.cmps;
		this.head.setWidth(width);
		if(width <= this.script.getWidth()){
			this.scrollLeft.setStyle({'display':'none'});
			this.scrollRight.setStyle({'display':'none'});
			this.script.setStyle('padding-left','0');
		}
		strip.remove();
		body.remove();
        
		delete body.loaded;
		setTimeout(function(){
        	for(var key in cmps){
        		var cmp = cmps[key];
        		if(cmp.destroy){
        			try{
        				cmp.destroy();
        			}catch(e){
        				alert('销毁Tab出错: ' + e)
        			}
        		}
        	}
        },10)
		this.selectTab(index);
	},
	destroy : function(){
        var bodys = this.body.dom.children;
//		var bodys = Ext.DomQuery.select('div.tab',this.body.dom);
    	for(var i=0;i<bodys.length;i++){
    		var body = Ext.get(bodys[i]),
    		cmps=body.cmps;
    		if(cmps){
	    		for(var key in cmps){
	        		var cmp = cmps[key];
	        		if(cmp.destroy){
	        			try{
	        				cmp.destroy();
	        			}catch(e){
	        				alert('销毁Tab出错: ' + e)
	        			}
	        		}
	        	}
    		}
    	}
		$A.Tab.superclass.destroy.call(this); 
	},
	/**
	 * 将某个Tab页设为不可用。当TabItem有且仅有1个时，该方法无效果。
	 * @param {Integer} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
	 */
	setDisabled : function(index){
		var tab = this.getTab(index);
		if(!tab)return;
		if(this.items.length > 1){
			if(this.activeTab==tab.strip){
				this.selectTab(tab.index+(this.getTab(tab.index+1)?1:-1))
			}
			tab.strip.addClass(this.sd);
		}
	},
	/**
	 * 将某个Tab页设为可用
	 * @param {Integer} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
	 */
	setEnabled : function(index){
		var tab = this.getTab(index);
		if(!tab)return;
		tab.strip.removeClass(this.sd);
	},
	getTab : function(o){
		var bodys = this.body.dom.children,//Ext.DomQuery.select('div.tab',this.body.dom),
        strips = this.head.dom.children;//Ext.DomQuery.select('div.strip',this.head.dom),strip,body;
		if(Ext.isNumber(o)){
			if(o<0)o+=strips.length;
			o=Math.round(o);
			if(strips[o]){
				strip=Ext.get(strips[o]);
				body=Ext.get(bodys[o]);
			}
		}else {
			o=Ext.get(o);
			for(var i=0,l=strips.length;i<l;i++){
				if(Ext.get(strips[i]) == o){
					strip=o;
					body=Ext.get(bodys[i]);
					o=i;
					break;
				}
			}
		}
		return strip?{'strip':strip,'body':body,'index':o}:null;
	},
	scrollTo : function(lr){
		if(lr=='left'){
			this.script.scrollTo('left',this.script.getScroll().left-this.scriptwidth);
			this.scrollRight.removeClass(this.sd);
			if(this.script.getScroll().left<=0){
				this.scrollLeft.addClass(this.sd);
				this.scrollLeft.replaceClass(this.tslo,this.tsl);
				this.stopScroll();
			}
		}else if(lr=='right'){
			this.script.scrollTo('left',this.script.getScroll().left+this.scriptwidth);
			this.scrollLeft.removeClass(this.sd);
			if(this.script.getScroll().left+this.script.getWidth()>=this.head.getWidth()){
				this.scrollRight.addClass(this.sd);
				this.scrollRight.replaceClass(this.tsro,this.tsr);
				this.stopScroll();
			}
		}
	},
	stopScroll : function(){
		if(this.scrollInterval){
			clearInterval(this.scrollInterval);
			delete this.scrollInterval;
		}
	},
	onClick : function(e){
		var el=Ext.get(e.target);
		if(el.hasClass(this.tc))this.closeTab(el.parent('.strip'));
	},
	onMouseWheel : function(e){
		var delta = e.getWheelDelta();
        if(delta > 0){
            this.scrollTo('left');
            e.stopEvent();
        }else{
            this.scrollTo('right');
            e.stopEvent();
        }
	},
	onMouseDown : function(e){
		var el=Ext.get(e.target),strip = el.parent('.strip'),sf=this;
		if(el.hasClass(sf.tc)){
			el.removeClass(sf.tbo);
			el.addClass(sf.tbd);
		}else if(el.hasClass(sf.ts) && !el.hasClass(sf.sd)){
			if(el.hasClass(sf.tslo))sf.scrollTo('left');
			else sf.scrollTo('right');
			sf.scrollInterval=setInterval(function(){
				if(el.hasClass(sf.ts)&&!el.hasClass(sf.sd)){
					if(el.hasClass(sf.tslo))sf.scrollTo('left');
					else sf.scrollTo('right');
					if(el.hasClass(sf.sd))clearInterval(sf.scrollInterval);
				}
			},100);
		}else if(strip && strip.hasClass('strip') && !strip.hasClass('active') && !strip.hasClass(sf.sd)){
			sf.selectTab(strip);
		}
	},
	onMouseUp : function(e){
		this.stopScroll();
	},
	onMouseOver : function(e){
		var el=Ext.get(e.target),strip = el.parent('.strip');
        if(el.hasClass(this.ts)&&!el.hasClass(this.sd)){
            if(el.hasClass(this.tsl))el.replaceClass(this.tsl,this.tslo);
            else if(el.hasClass(this.tsr))el.replaceClass(this.tsr,this.tsro);
        } else if(el.hasClass(this.tc)){
            el.addClass(this.tbo);
        }
        if(strip){
            var el = strip.child('div.'+this.tc);
            if(el){
                if(this.currentBtn)this.currentBtn.hide();
                this.currentBtn=el;
                el.show();
            }            
        }
	},
	onMouseOut : function(e){
		var el=Ext.get(e.target),strip = el.parent('.strip');
        if(el.hasClass(this.ts)&&!el.hasClass(this.sd)){
            this.stopScroll();
            if(el.hasClass(this.tslo))el.replaceClass(this.tslo,this.tsl);
            else if((el.hasClass(this.tsro)))el.replaceClass(this.tsro,this.tsr);
        }else if(el.hasClass(this.tc)){
            el.removeClass(this.tbo);
            el.removeClass(this.tbd);
        }
        if(strip){
            el = strip.child('div.'+this.tc);
            if(el){
                el.hide();
            }            
        }
	},
	showLoading : function(dom){
    	dom.update(_lang['tab.loading']);
    	dom.setStyle('text-align','center');
    	dom.setStyle('line-height',5);
    },
    clearLoading : function(dom){
    	dom.update('');
    	dom.setStyle('text-align','');
    	dom.setStyle('line-height','');
    },
	load : function(url,dom,index){
        url=url+(url.indexOf('?') !=-1?'&':'?')+'_vw='+this.width+'&_vh='+(this.height-Ext.fly(this.head).getHeight());
		var sf = this,body = Ext.get(dom);
		body.cmps={};
		sf.showLoading(body);
		//TODO:错误信息
    	Ext.Ajax.request({
			url: url,
		   	success: function(response, options){
                var res;
                try {
                    res = Ext.decode(response.responseText);
                }catch(e){}            
                if(res && res.success == false){
                    if(res.error){
                        if(res.error.code  && res.error.code == 'session_expired'){
                            $A.showErrorMessage(_lang['ajax.error'],  _lang['session.expired']);
                        }else{
                            $A.manager.fireEvent('ajaxfailed', $A.manager, options.url,options.para,res);
                            var st = res.error.stackTrace;
                            st = (st) ? st.replaceAll('\r\n','</br>') : '';
                            if(res.error.message) {
                                var h = (st=='') ? 150 : 250;
                                $A.showErrorMessage(_lang['window.error'], res.error.message+'</br>'+st,null,400,h);
                            }else{
                                $A.showErrorMessage(_lang['window.error'], st,null,400,250);
                            } 
                        }
                    }
                    return;
                }
		    	var html = response.responseText;
//	    		sf.intervalIds[index]=setInterval(function(){
//	    			if(!$A.focusTab){
//				    	clearInterval(sf.intervalIds[index]);
				    	sf.clearLoading(body);
//						$A.focusTab=body;
//						try{
					    	body.update(html,true,function(){
//					    		$A.focusTab=null;
			                    sf.fireEvent('select', sf, index)
					    	},body);
//						}catch(e){
//							$A.focusTab=null;
//						}
//	    			}
//		    	},10)
		    }
		});		
    },
    setWidth : function(w){
    	w = Math.max(w,2);
    	if(this.width==w)return;
    	$A.Tab.superclass.setWidth.call(this, w);
    	this.body.setWidth(w-2);
    	this.script.setWidth(w-38);
    	if(w-38<this.head.getWidth()){
			this.scrollLeft.setStyle({'display':'block'});
			this.scrollRight.setStyle({'display':'block'});
			this.script.setStyle('padding-left','1px');
			var sl=this.script.getScroll().left,sw=this.script.getWidth(),hw=this.head.getWidth();
			if(sl<=0)this.scrollLeft.addClass(this.sd);
			else this.scrollLeft.removeClass(this.sd);
			if(sl+sw>=hw){
				if(!this.scrollRight.hasClass(this.sd))this.scrollRight.addClass(this.sd);
				else this.script.scrollTo('left',hw-sw);
			}else this.scrollRight.removeClass(this.sd);
    	}else{
			this.scrollLeft.setStyle({'display':'none'});
			this.scrollRight.setStyle({'display':'none'});
			this.script.setStyle('padding-left','0');
			this.script.scrollTo('left',0);
    	}
        var bodys = this.body.dom.children;
//    	var bodys = Ext.DomQuery.select('div.tab',this.body.dom);
    	for(var i=0;i<bodys.length;i++){
    		var body = bodys[i];
    		Ext.fly(body).setWidth(w-4);
    	}
    },
    setHeight : function(h){
    	h = Math.max(h,25);
    	if(this.height==h)return;
    	$A.Tab.superclass.setHeight.call(this, h);
    	this.body.setHeight(h-26);
        var bodys = this.body.dom.children;
//    	var bodys = Ext.DomQuery.select('div.tab',this.body.dom);
    	for(var i=0;i<bodys.length;i++){
    		var body = bodys[i];
    		Ext.fly(body).setHeight(h-28);
    	}
    }
});