/*
 * Aurora UI Library.
 * Copyright(c) 2010, Hand China Co.,Ltd.
 * 
 * http://www.hand-china.com
 */

/**
 * @class Aurora
 * Aurora UI 核心工具类.
 * @author 牛佳庆
 * @singleton
 */
 
Ext.Ajax.timeout = 1800000;

$A = Aurora = {version: '1.0',revision:'$Rev: 5366 $'};
//$A.firstFire = false;
$A.fireWindowResize = function(){
	if($A.winWidth != $A.getViewportWidth() || $A.winHeight != $A.getViewportHeight()){
        $A.winHeight = $A.getViewportHeight();
        $A.winWidth = $A.getViewportWidth();
        $A.Cover.resizeCover();
	}
}
if(Ext.isIE6)Ext.EventManager.on(window, "resize", $A.fireWindowResize, this);



$A.cache = {};
$A.cmps = {};
$A.onReady = function(fn, scope, options){
	if(window['__host']){
		if(!$A.loadEvent)$A.loadEvent = new Ext.util.Event();
		$A.loadEvent.addListener(fn, scope, options);
	}else{
		Ext.onReady(fn, scope, options);
	}
}//Ext.onReady;

$A.get = Ext.get;
//$A.focusWindow;
//$A.focusTab;
$A.defaultDateFormat="isoDate";
$A.defaultDateTimeFormat="yyyy-mm-dd HH:MM:ss";
$A.defaultChineseLength = 2;

/**
 * 页面地址重定向
 * @param {String} url
 */
$A.go=function(url){
	if(!url)return;
	var r=Math.random();
	location.href=url+(url.indexOf('?')==-1?'?':'&')+'__r__='+r;
}

/**
 * 将对象居中
 * @param {Object/String} el Aurora组件对象或者是DOM对象或者是对象的ID字符串
 */
$A.center = function(el){
	var ele;
	if(typeof(el)=="string"){
        var cmp = $A.CmpManager.get(el)
        if(cmp){
            if(cmp.wrap){
                ele = cmp.wrap;
            }
        }else{
             ele = Ext.get(el);
        }             
    }else{
        ele = Ext.get(el);
    }
    var screenWidth = $A.getViewportWidth();
    var screenHeight = $A.getViewportHeight();
    var x = Math.max(0,(screenWidth - ele.getWidth())/2);
    var y = Math.max(0,(screenHeight - ele.getHeight())/2);
    ele.setStyle('position','absolute');
    ele.moveTo(x,y);
}

/**
 * 设置主题
 * @param {String} theme 主题名
 */
$A.setTheme = function(theme){
	if(theme) {
		var exp  = new Date();   
	    exp.setTime(exp.getTime() + 24*3600*1000);
	    document.cookie = "app_theme="+ escape (theme) + ";expires=" + exp.toGMTString(); 
	    window.location.reload();
	}
}
$A.CmpManager = function(){
    return {
        put : function(id, cmp){
        	if(!this.cache) this.cache = {};
        	if(this.cache[id] != null) {
	        	alert("错误: ID为' " + id +" '的组件已经存在!");
	        	return;
	        }
            if(window['__host']){
                    window['__host'].cmps[id] = cmp;
                    cmp['__host'] = window['__host'];
            }
//        	if($A.focusWindow) $A.focusWindow.cmps[id] = cmp;
//        	if($A.focusTab) $A.focusTab.cmps[id] = cmp;
        	this.cache[id]=cmp;
        	cmp.on('mouseover',$A.CmpManager.onCmpOver,$A.CmpManager);
        	cmp.on('mouseout',$A.CmpManager.onCmpOut,$A.CmpManager);
        },
        onCmpOver: function(cmp, e){
        	if($A.validInfoType != 'tip') return;
        	if(($A.Grid && cmp instanceof $A.Grid)||($A.Table && cmp instanceof $A.Table)){
        		var ds = cmp.dataset;
        		if(!ds||ds.isValid == true||!e.target)return;
        		var target = Ext.fly(e.target).findParent('td');
                if(target){
                    var atype = Ext.fly(target).getAttributeNS("","atype");
            		if(atype == 'grid-cell'||atype == 'table-cell'){
            			var rid = Ext.fly(target).getAttributeNS("","recordid");
            			var record = ds.findById(rid);
            			if(record){
                			var name = Ext.fly(target).getAttributeNS("","dataindex");        			
        					var msg = record.valid[name];
        	        		if(Ext.isEmpty(msg))return;
        	        		$A.ToolTip.show(target, msg);
            			}
                    }
        		}
        	}else{
	        	if(cmp.binder){
	        		var ds = cmp.binder.ds;
	        		if(!ds || ds.isValid == true)return;
	        		var record = cmp.record;
	        		if(!record)return;
	        		var msg = record.valid[cmp.binder.name];
	        		if(Ext.isEmpty(msg))return;
	        		$A.ToolTip.show(cmp.id, msg);
	        	}
        	}
        },
        onCmpOut: function(cmp,e){
        	if($A.validInfoType != 'tip') return;
        	$A.ToolTip.hide();
        },
        getAll : function(){
        	return this.cache;
        },
        remove : function(id){
        	var cmp = this.cache[id];
            if(cmp['__host'] && cmp['__host'].cmps){
                delete cmp['__host'].cmps[id];        
            }
        	cmp.un('mouseover',$A.CmpManager.onCmpOver,$A.CmpManager);
        	cmp.un('mouseout',$A.CmpManager.onCmpOut,$A.CmpManager);
        	delete this.cache[id];
        },
        get : function(id){
        	if(!this.cache) return null;
        	return this.cache[id];
        }
    };
}();
Ext.Ajax.on("requestexception", function(conn, response, options) {
	if($A.slideBarEnable)$A.SideBar.enable = $A.slideBarEnable;
	$A.manager.fireEvent('ajaxerror', $A.manager, response.status, response);
	if($A.logWindow){
		var record = $('HTTPWATCH_DATASET').getCurrentRecord();
		var st = $A['_startTime'];
		var ed = new Date();					
		record.set('spend',ed-st);
		record.set('status',response.status);
		record.set('result',response.statusText);
		record.set('response',response.statusText);
	}
	switch(response.status){
		case 404:
			$A.showErrorMessage(response.status + _lang['ajax.error'], _lang['ajax.error.404']+'"'+ response.statusText+'"',null,400,150);
			break;
		case 500:
            $A.showErrorMessage(response.status + _lang['ajax.error'], response.responseText,null,500,300);
            break;
        case 0:
            break;
		default:
			$A.showErrorMessage(_lang['ajax.error'], response.statusText);
			break;
	}	
}, this);

/**
 * 获取Aurora控件的对象，可以使用简写方式的$()方法
 * @param {String} id Aurora控件的id
 */
$ = $A.getCmp = function(id){
	var cmp = $A.CmpManager.get(id)
	if(cmp == null) {
		alert('未找到组件:' + id)
	}
	return cmp;
}

/**
 * 设置cookie
 * @param {String} name cookie名
 * @param {String} value cookie值
 * @param {Number} days 有效期(单位是天),默认是sessions
 */
$A.setCookie = function(name,value,days){
    var pathname = location.pathname;
    pathname = pathname.substring(0, pathname.lastIndexOf('/') + 1);
    var exp = null;
    if(days){
        exp  = new Date();
        exp.setTime(exp.getTime() + days*24*60*60*1000);
    }
    document.cookie = name + "="+ escape (value) +';path = ' + pathname + ((exp) ? (';expires=' + exp.toGMTString()) : '');
}

/**
 * 根据cookie名获取cookie值
 * @param {String} name cookie名
 */
$A.getCookie = function(name){
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
     if(arr != null) return unescape(arr[2]); return null;

}

/**
 * 获取页面可视高度
 * @return {Number} 页面可视高度
 */
$A.getViewportHeight = function(){
    if(Ext.isIE){
        return Ext.isStrict ? document.documentElement.clientHeight :
                 document.body.clientHeight;
    }else{
        return self.innerHeight;
    }
}
/**
 * 获取页面可视宽度
 * @return {Number} 页面可视宽度
 */
$A.getViewportWidth = function() {
    if(Ext.isIE){
        return Ext.isStrict ? document.documentElement.clientWidth :
                 document.body.clientWidth;
    }else{
        return self.innerWidth;
    }
}
//$A.recordSize = function(){
//    var w = $A.getViewportWidth();
//    var h = $A.getViewportHeight();
//    document.cookie = "vw="+w;
//    document.cookie = "vh="+h;
//}
//$A.recordSize();
/**
 * post的方式提交数据，同{@link Aurora.DataSet#post}
 * @param {String} action 提交的url地址
 * @param {Object} data 数据集合
 */
$A.post = function(action,data){
    var form = Ext.getBody().createChild({style:'display:none',tag:'form',method:'post',action:action});
    for(var key in data){
    	var v = data[key]
    	if(v) {
    		if(v instanceof Date) v = v.format('isoDate');//TODO:时分秒如何处理?
            form.createChild({tag:"input",type:"hidden",name:key,value:v});
    	}
    }
    form.dom.submit();
}
/**
 * POST方式的Ajax请求
 * <p>
 * opt对象的属性:
 * <div class="mdetail-params"><ul>
 * <li><code>url</code>
 * <div class="sub-desc">提交的url地址</div></li>
 * <li><code>para</code>
 * <div class="sub-desc">提交的参数</div></li>
 * <li><code>scope</code>
 * <div class="sub-desc">作用域</div></li>
 * <li><code>sync</code>
 * <div class="sub-desc">是否同步,默认false</div></li> 
 * <li><code>success</code>
 * <div class="sub-desc">成功的回调函数</div></li>
 * <li><code>error</code>
 * <div class="sub-desc">错误的回调函数</div></li>
 * <li><code>failure</code>
 * <div class="sub-desc">ajax调用失败的回调函数</div></li>
 * </ul></div></p>
 * @param {Object} opt 参数对象
 */
$A.request = function(opt){
	var url = opt.url,para = opt.para,successCall = opt.success,errorCall = opt.error,scope = opt.scope,failureCall = opt.failure;
	var opts = Ext.apply({},opt.opts);
	$A.manager.fireEvent('ajaxstart', url, para);
	if($A.logWindow){
		$A['_startTime'] = new Date();
		$('HTTPWATCH_DATASET').create({'url':url,'request':Ext.util.JSON.encode({parameter:para})})
	}
	var data = Ext.apply({parameter:para},opt.ext);
	return Ext.Ajax.request({
		url: url,
		method: 'POST',
		params:{_request_data:Ext.util.JSON.encode(data)},
		opts:opts,
        sync:opt.sync,
		success: function(response,options){
			if($A.logWindow){
				var st = $A['_startTime'];
				var ed = new Date();					
				var record = $('HTTPWATCH_DATASET').getCurrentRecord();
				record.set('spend',ed-st);
				record.set('result',response.statusText);
				record.set('status',response.status);
				record.set('response',response.responseText);
			}
			$A.manager.fireEvent('ajaxcomplete', url, para,response);
			if(response){
				var res = null;
				try {
					res = Ext.decode(response.responseText);
				}catch(e){
					$A.showErrorMessage(_lang['ajax.error'], _lang['ajax.error.format']);
					return;
				}
                
				if(res && !res.success){
					$A.manager.fireEvent('ajaxfailed', $A.manager, url,para,res);
					if(res.error){
                        if(res.error.code  && (res.error.code == 'session_expired' || res.error.code == 'login_required')){
                            if($A.manager.fireEvent('timeout', $A.manager))
                            $A.showErrorMessage(_lang['ajax.error'],  _lang['session.expired']);
                        }else{
    						var st = res.error.stackTrace;
    						st = (st) ? st.replaceAll('\r\n','</br>') : '';
    						if(res.error.message) {
    							var h = (st=='') ? 150 : 250;
    						    $A.showErrorMessage(_lang['ajax.error'], res.error.message+'</br>'+st,null,400,h);
    						}else{
    						    $A.showErrorMessage(_lang['ajax.error'], st,null,400,250);
    						}
                        }
						if(errorCall)
                        errorCall.call(scope, res, options);
					}								    						    
				} else {
					$A.manager.fireEvent('ajaxsuccess', $A.manager, url,para,res);
					if(successCall)successCall.call(scope,res, options);
				}
			}
		},
		failure : function(response, options){
            if(failureCall)failureCall.call(scope, response, options);
		},
		scope: scope
	});
}
Aurora.dateFormat = function () { 
	var masks = {  
        "default":      "ddd mmm dd yyyy HH:MM:ss",  
        shortDate:      "m/d/yy",  
        mediumDate:     "mmm d, yyyy",  
        longDate:       "mmmm d, yyyy",  
        fullDate:       "dddd, mmmm d, yyyy",  
        shortTime:      "h:MM TT",  
        mediumTime:     "h:MM:ss TT",  
        longTime:       "h:MM:ss TT Z",  
        isoDate:        "yyyy-mm-dd",  
        isoTime:        "HH:MM:ss",  
        isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",  
        isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"  
    };
    var token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,  
        timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,  
        timezoneClip = /[^-+\dA-Z]/g,  
        pad = function (val, len) {  
            val = String(val);  
            len = len || 2;  
            while (val.length < len) val = "0" + val;  
            return val;  
        },
        hasTimeStamp = function(mask,token){
	    	return !!String(masks[mask] || mask || masks["default"]).match(token);
        },
        _parseDate=function(string,mask,fun){
        	for(var i=0,arr=mask.match(token),numbers=string.match(/\d+/g),value,index=0;i<arr.length;i++){
        		if(numbers.length==arr.length)value=numbers[i];
        		else if(numbers.length == 1)value=parseInt(string.slice(index,index+=arr[i].length),10);
        		else value=parseInt(string.slice(index=mask.search(arr[i]),index+arr[i].length));
        		switch(arr[i]){
        			case "mm":;
        			case "m":value--;break;
        		}
        		fun(arr[i],value);
        	}
        }; 
    return {
    	pad:pad,
    	parseDate:function(string,mask,utc){
    		if(typeof string!="string"||string=="")return null;
    		mask = String(masks[mask] || mask || masks["default"]); 
    		if (mask.slice(0, 4) == "UTC:") {  
	            mask = mask.slice(4);  
	            utc = true;  
	        }
    		var date=new Date(1970,1,2,0,0,0),
    			_ = utc ? "setUTC" : "set",  
	            d = date[_ + "Date"],  
	            m = date[_ + "Month"],  
	            yy = date[_ + "FullYear"], 
	            y = date[_ + "Year"], 
	            H = date[_ + "Hours"],  
	            M = date[_ + "Minutes"],  
	            s = date[_ + "Seconds"],  
	            L = date[_ + "Milliseconds"],  
	            //o = utc ? 0 : date.getTimezoneOffset();
				flags = {  
	                d:    d,  
	                dd:   d,
	                m:    m,  
	                mm:   m,  
	                yy:   y,  
	                yyyy: yy,  
	                h:    H,  
	                hh:   H,  
	                H:    H,  
	                HH:   H,  
	                M:    M,  
	                MM:   M,  
	                s:    s,  
	                ss:   s,  
	                l:    L,  
	                L:    L
	            }; 
	            try{
					_parseDate(string,mask,function($0,value){
					   	flags[$0].call(date,value);
					});
	            }catch(e){throw new SyntaxError("invalid date");}
				if (isNaN(date)) throw new SyntaxError("invalid date"); 
				return date;
    	},
	    format:function (date, mask, utc) {    
	        if (arguments.length == 1 && (typeof date == "string" || date instanceof String) && !/\d/.test(date)) {  
	            mask = date;  
	            date = undefined;  
	        }   
	        date = date ? new Date(date) : new Date();  
	        if (isNaN(date)) throw new SyntaxError("invalid date");  
	  
	        mask = String(masks[mask] || mask || masks["default"]);  
	        if (mask.slice(0, 4) == "UTC:") {  
	            mask = mask.slice(4);  
	            utc = true;  
	        }  
	  
	        var _ = utc ? "getUTC" : "get",  
	            d = date[_ + "Date"](),  
	            D = date[_ + "Day"](),  
	            m = date[_ + "Month"](),  
	            y = date[_ + "FullYear"](),  
	            H = date[_ + "Hours"](),  
	            M = date[_ + "Minutes"](),  
	            s = date[_ + "Seconds"](),  
	            L = date[_ + "Milliseconds"](),  
	            o = utc ? 0 : date.getTimezoneOffset(),  
	            flags = {  
	                d:    d,  
	                dd:   pad(d),
	                m:    m + 1,  
	                mm:   pad(m + 1),  
	                yy:   String(y).slice(2),  
	                yyyy: y,  
	                h:    H % 12 || 12,  
	                hh:   pad(H % 12 || 12),  
	                H:    H,  
	                HH:   pad(H),  
	                M:    M,  
	                MM:   pad(M),  
	                s:    s,  
	                ss:   pad(s),  
	                l:    pad(L, 3),  
	                L:    pad(L > 99 ? Math.round(L / 10) : L),  
	                t:    H < 12 ? "a"  : "p",  
	                tt:   H < 12 ? "am" : "pm",  
	                T:    H < 12 ? "A"  : "P",  
	                TT:   H < 12 ? "AM" : "PM",  
	                Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),  
	                o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),  
	                S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]  
	            }; 
	        return mask.replace(token, function ($0) {  
	            return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);  
	        });  
	    },
	    isDateTime:function(mask){
	    	return hasTimeStamp(mask,/([HhMs])\1?/);
	    }
    };  
}();

Ext.applyIf(String.prototype, {
	trim : function(){
		return this.replace(/(^\s*)|(\s*$)/g, "");
	}
});
Ext.applyIf(Date.prototype, {
    format : function(mask, utc){
        return Aurora.dateFormat.format(this, mask, utc);  
    }
});
Ext.applyIf(Array.prototype, {
	add : function(o){
		if(this.indexOf(o) == -1)
		this[this.length] = o;
	}
});
Ext.applyIf(String.prototype, {
    replaceAll : function(s1,s2){
        return this.replace(new RegExp(s1,"gm"),s2);  
    }
}); 
Ext.applyIf(String.prototype, {
    parseDate : function(mask,utc){
        return Aurora.dateFormat.parseDate(this.toString(),mask,utc);  
    }
}); 
$A.TextMetrics = function(){
    //var shared;
    return {
        measure : function(el, text, fixedWidth){
            //if(!shared){
              var shared = $A.TextMetrics.Instance(el, fixedWidth);
            //}
            //shared.bind(el);
            //shared.setFixedWidth(fixedWidth || 'auto');
            return shared.getSize(text);
        }
    };
}();
$A.TextMetrics.Instance = function(bindTo, fixedWidth){
	var p = '<div style="left:-1000px;top:-1000px;position:absolute;visibility:hidden"></div>';
	var ml = Ext.get(Ext.DomHelper.append(Ext.get(bindTo),p));
//    var ml = new Ext.Element(document.createElement('div'));
//    document.body.appendChild(ml.dom);
//    ml.position('absolute');
//    ml.setLeft(-1000);
//    ml.setTop(-1000);    
//    ml.hide();
    if(fixedWidth){
        ml.setWidth(fixedWidth);
    }
    var instance = {      
        getSize : function(text){
            ml.update(text);            
            var s=new Object();
            s.width=ml.getWidth();
            s.height=ml.getHeight();
            ml.update('');
            return s;
        },       
        bind : function(el){
        	var a=new Array('font-size','font-style', 'font-weight', 'font-family','line-height', 'text-transform', 'letter-spacing');	
        	var len = a.length, r = {};
        	for(var i = 0; i < len; i++){
                r[a[i]] = Ext.fly(el).getStyle(a[i]);
            }
            ml.setStyle(r);           
        },       
        setFixedWidth : function(width){
            ml.setWidth(width);
        }       
    };
    instance.bind(bindTo);
    return instance;
};
$A.ToolTip = function(){
	q = {
		init: function(){
			var sf = this;
			Ext.onReady(function(){
				var qdom = Ext.DomHelper.insertFirst(
				    Ext.getBody(),
				    {
					    tag: 'div',
					    cls: 'tip-wrap',
					    children: [{tag: 'div', cls:'tip-body'}]
				    }
				);
				var sdom = Ext.DomHelper.insertFirst(Ext.getBody(),{tag:'div',cls: 'item-shadow'});
				sf.tip = Ext.get(qdom);
				sf.shadow = Ext.get(sdom);
				sf.body = sf.tip.first("div.tip-body");
			})
			
		},
		show: function(el, text){
			if(this.tip == null){
				this.init();
				//return;
			}
			this.tip.show();
			this.shadow.show();
			this.body.update(text)
			var ele;
			if(typeof(el)=="string"){
				if(this.sid==el) return;
				this.sid = el;
				var cmp = $A.CmpManager.get(el)
				if(cmp){
					if(cmp.wrap){
						ele = cmp.wrap;
					}
				}				
			}else{
				ele = Ext.get(el);
			}
			this.shadow.setWidth(this.tip.getWidth())
			this.shadow.setHeight(this.tip.getHeight())
			this.correctPosition(ele);
		},
		correctPosition: function(ele){
			var screenWidth = $A.getViewportWidth();
			var x = ele.getX()+ele.getWidth() + 5;
			var sx = ele.getX()+ele.getWidth() + 7;
			if(x+this.tip.getWidth() > screenWidth){
				x = ele.getX() - this.tip.getWidth() - 5;
				sx = ele.getX() - this.tip.getWidth() - 3;
			}
			this.tip.setX(x);
			this.tip.setY(ele.getY());
			this.shadow.setX(sx);
			this.shadow.setY(this.tip.getY()+ 2)
		},
		hide: function(){
			this.sid = null;
			if(this.tip != null) this.tip.hide();
			if(this.shadow != null) this.shadow.hide();
		}
	}
	return q
}();
$A.SideBar = function(){
    var m = {
    	enable:true,
        bar:null,
        show : function(msg){
        	if(!this.enable)return;
//            this.hide();
            var sf = this;
            if(parent.showSideBar){
                parent.showSideBar(msg)
            }else{
            	this.hide();
                var p = '<div class="item-slideBar">'+msg+'</div>';
                this.bar = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(),p));
                this.bar.setStyle('z-index', 999999);
                var screenWidth = $A.getViewportWidth();
                var x = Math.max(0,(screenWidth - this.bar.getWidth())/2);
                this.bar.setX(x);
                this.bar.fadeIn();
//                this.bar.animate({height: {to: 50, from: 0}},0.35,function(){
                    setTimeout(function(){
                       sf.hide();
                    }, 2000);            
//                },'easeOut','run');
            }
        },
        hide : function(){
        	if(parent.hideSideBar){
                parent.hideSideBar()
            }else{
                if(this.bar) {
                    Ext.fly(this.bar).fadeOut();
                    Ext.fly(this.bar).remove();
                    this.bar = null;
                }
            }
        }
    }
    return m;
}();
$A.Status = function(){
    var m = {
        bar:null,
        enable:true,
        show : function(msg){
        	if(!this.enable)return;
        	this.hide();
        	if(parent.showStatus) {
        	   parent.showStatus(msg);
        	}else{
                var p = '<div class="item-statusBar" unselectable="on">'+msg+'</div>';
                this.bar = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(),p));
                this.bar.setStyle('z-index', 999998);
        	}
        },
        hide : function(){
        	if(parent.hideStatus){
                parent.hideStatus();
        	}else{
                if(this.bar) {
                    Ext.fly(this.bar).remove();
                    this.bar = null;
                }
        	}
        }
    }
    return m;
}();
$A.Cover = function(){
	var m = {
		bodyOverflow:null,
		sw:null,
		sh:null,
		container: {},
		cover : function(el){
//			if(!$A.Cover.bodyOverflow)$A.Cover.bodyOverflow = Ext.getBody().getStyle('overflow');		
			var scrollWidth = Ext.isStrict ? document.documentElement.scrollWidth : document.body.scrollWidth;
    		var scrollHeight = Ext.isStrict ? document.documentElement.scrollHeight : document.body.scrollHeight;
    		var screenWidth = Math.max(scrollWidth,$A.getViewportWidth());
    		var screenHeight = Math.max(scrollHeight,$A.getViewportHeight());
			var p = '<DIV class="aurora-cover"'+(Ext.isIE6?' style="position:absolute;width:'+(screenWidth-1)+'px;height:'+(screenHeight-1)+'px;':'')+'" unselectable="on"></DIV>';
			var cover = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(),p));
	    	cover.setStyle('z-index', Ext.fly(el).getStyle('z-index') - 1);
//	    	Ext.getBody().setStyle('overflow','hidden');
	    	$A.Cover.container[el.id] = cover;
		},
		uncover : function(el){
			var cover = $A.Cover.container[el.id];
			if(cover) {
				Ext.fly(cover).remove();
				$A.Cover.container[el.id] = null;
				delete $A.Cover.container[el.id];
			}
			var reset = true;
			for(key in $A.Cover.container){
                if($A.Cover.container[key]) {
                    reset = false; 	
                    break;
                }
            }
//            if(reset&&$A.Cover.bodyOverflow)Ext.getBody().setStyle('overflow',$A.Cover.bodyOverflow);
		},
		resizeCover : function(){
			for(key in $A.Cover.container){
                var cover = $A.Cover.container[key];
                Ext.fly(cover).setStyle('display','none');
            }
            setTimeout(function(){
    			var scrollWidth = Ext.isStrict ? document.documentElement.scrollWidth : document.body.scrollWidth;
        		var scrollHeight = Ext.isStrict ? document.documentElement.scrollHeight : document.body.scrollHeight;
        		var screenWidth = Math.max(scrollWidth,$A.getViewportWidth()) -1;
        		var screenHeight = Math.max(scrollHeight,$A.getViewportHeight()) -1;
    			for(key in $A.Cover.container){
    				var cover = $A.Cover.container[key];
    				Ext.fly(cover).setWidth(screenWidth);
    				Ext.fly(cover).setHeight(screenHeight);
    				Ext.fly(cover).setStyle('display','');
    			}		
            },1)
		}
	}
	return m;
}();
$A.Masker = function(){
    var m = {
        container: {},
        mask : function(el,msg){
        	if($A.Masker.container[el.id]){
        	   return;
        	}
        	msg = msg||_lang['mask.loading'];
        	var el = Ext.get(el);
            var w = el.getWidth();
            var h = el.getHeight();//leftp:0px;top:0px; 是否引起resize?
            var p = '<div class="aurora-mask"  style="left:-1000px;top:-1000px;width:'+w+'px;height:'+h+'px;position: absolute;"><div unselectable="on"></div><span style="top:'+(h/2-11)+'px">'+msg+'</span></div>';
            var wrap = el.parent('body')?el.parent():el;
            var masker = Ext.get(Ext.DomHelper.append(wrap,p));
            var zi = el.getStyle('z-index') == 'auto' ? 0 : el.getStyle('z-index');
            masker.setStyle('z-index', zi + 1);
            masker.setXY(el.getXY());
            var sp = masker.child('span');
            //var size = $A.TextMetrics.measure(sp,msg);
            //sp.setLeft((w-size.width - 45)/2)
            sp.setLeft((w-sp.getWidth() - 45)/2)
            $A.Masker.container[el.id] = masker;
        },
        unmask : function(el){
            var masker = $A.Masker.container[el.id];
            if(masker) {
                Ext.fly(masker).remove();
                $A.Masker.container[el.id] = null;
                delete $A.Masker.container[el.id];
            }
        }
    }
    return m;
}();
Ext.util.JSON.encodeDate = function(o){
	var pad = function(n) {
        return n < 10 ? "0" + n : n;
    };
    var r = '"' + o.getFullYear() + "-" +
            pad(o.getMonth() + 1) + "-" +
            pad(o.getDate());
    if(o.xtype == 'timestamp') {
        r = r + " " +
            pad(o.getHours()) + ":" +
            pad(o.getMinutes()) + ":" +
            pad(o.getSeconds())    	
    }
    r += '"';
    return r
};
$A.evalList = [];
$A.evaling = false;
$A.doEvalScript = function(){
    $A.evaling = true;
    var list = $A.evalList;
    var o = list.shift();
    if(!o) {
        window['__host'] = null;
        $A.evaling = false;
        if($A.loadEvent){
        	$A.loadEvent.fire();
	        $A.loadEvent = null;
        }
        return;
    }
    var sf = o.sf, html=o.html, loadScripts=o.loadScripts, callback=o.callback, host=o.host;
    var dom = sf.dom;
    
    if(host) window['__host'] = host;
    var links = [];
    var scripts = [];
    var hd = document.getElementsByTagName("head")[0];
    for(var i=0;i<hd.childNodes.length;i++){
        var he = hd.childNodes[i];
        if(he.tagName == 'LINK') {
            links.push(he.href);
        }else if(he.tagName == 'SCRIPT'){
            scripts.push(he.src);
        }
    }
    var jsre = /(?:<script([^>]*)?>)((\n|\r|.)*?)(?:<\/script>)/ig;
    var jsSrcRe = /\ssrc=([\'\"])(.*?)\1/i;
    
    var cssre = /(?:<link([^>]*)?>)((\n|\r|.)*?)/ig;
    var cssHreRe = /\shref=([\'\"])(.*?)\1/i;
    
    var cssm;
    while(cssm = cssre.exec(html)){
        var attrs = cssm[1];
        var srcMatch = attrs ? attrs.match(cssHreRe) : false;
        if(srcMatch && srcMatch[2]){
            var included = false;
            for(var i=0;i<links.length;i++){
                var link = links[i];
                if(link.indexOf(srcMatch[2]) != -1){
                    included = true;
                    break;
                }
            }
            if(!included) {
                var s = document.createElement("link");
                s.type = 'text/css';
                s.rel = 'stylesheet';
                s.href = srcMatch[2];
                hd.appendChild(s);
            }
        }
    }
    var match;
    var jslink = [];
    var jsscript = [];
    while(match = jsre.exec(html)){
        var attrs = match[1];
        var srcMatch = attrs ? attrs.match(jsSrcRe) : false;
        if(srcMatch && srcMatch[2]){
            var included = false;
            for(var i=0;i<scripts.length;i++){
                var script = scripts[i];
                if(script.indexOf(srcMatch[2]) != -1){
                    included = true;
                    break;
                }
            }
            if(!included) {
                jslink[jslink.length] = {
                    src:srcMatch[2],
                    type:'text/javascript'
                }
            } 
        }else if(match[2] && match[2].length > 0){
            jsscript[jsscript.length] = match[2];
        }
    }
    var loaded = 0;
    
    var onReadOnLoad = function(){
        var isready = Ext.isIE ? (!this.readyState || this.readyState == "loaded" || this.readyState == "complete") : true;
        if(isready) {
            loaded ++;
            if(loaded==jslink.length) {
                for(j=0,k=jsscript.length;j<k;j++){
                    var jst = jsscript[j];
                    if(window.execScript) {
                        window.execScript(jst);
                    } else {
                        window.eval(jst);
                    }
                }
                var el = document.getElementById(id);
                if(el){Ext.removeNode(el);} 
                Ext.fly(dom).setStyle('display', 'block');
                if(typeof callback == "function"){
                    callback();
                }
                $A.doEvalScript();
            }else{
                var js = jslink[loaded];
                var s = document.createElement("script");
                s.src = js.src;
                s.type = js.type;
                s[Ext.isIE ? "onreadystatechange" : "onload"] = onReadOnLoad;
                hd.appendChild(s);
            }
        }
    }
    
    if(jslink.length > 0){
        var js = jslink[0];
        var s = document.createElement("script");
        s.src = js.src;
        s.type = js.type;
        s[Ext.isIE ? "onreadystatechange" : "onload"] = onReadOnLoad;
        hd.appendChild(s);
    } else if(jslink.length ==0) {
        for(j=0,k=jsscript.length;j<k;j++){
            var jst = jsscript[j];
            if(window.execScript) {
               window.execScript(jst);
            } else {
               window.eval(jst);
            }
        }
        var el = document.getElementById(id);
        if(el){Ext.removeNode(el);} 
        Ext.fly(dom).setStyle('display', 'block');
        if(typeof callback == "function"){
                callback();
        }
        $A.doEvalScript();
    } 
}
Ext.Element.prototype.update = function(html, loadScripts, callback,host){
    if(typeof html == "undefined"){
        html = "";
    }
    if(loadScripts !== true){
        this.dom.innerHTML = html;
        if(typeof callback == "function"){
            callback();
        }
        return this;
    }
    
    var id = Ext.id();
    var sf = this;
    var dom = this.dom;
    html += '<span id="' + id + '"></span>';
    Ext.lib.Event.onAvailable(id, function(){
        $A.evalList.push({
            html:html,
            loadScripts:loadScripts,
            callback:callback,
            host:host,
            sf:sf
        });
        if(!$A.evaling)
        $A.doEvalScript() 
    });
    
    Ext.fly(dom).setStyle('display', 'none');
    dom.innerHTML = html.replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/ig, "").replace(/(?:<link.*?>)((\n|\r|.)*?)/ig, "");
    return this;
}

Ext.EventObjectImpl.prototype['isSpecialKey'] = function(){
    var k = this.keyCode;
//    return (this.type == 'keypress' && this.ctrlKey) || k==8 || k== 46 || k == 9 || k == 13  || k == 40 || k == 27 || k == 44 ||
     return (this.type == 'keypress' && this.ctrlKey) || k == 9 || k == 13  || k == 40 || k == 27 ||
    (k == 16) || (k == 17) ||
    (k >= 18 && k <= 20) ||
    (k >= 33 && k <= 35) ||
    (k >= 36 && k <= 39) ||
    (k >= 44 && k <= 45);
}
Ext.removeNode = Ext.isIE && !Ext.isIE8 ? function(){
    var d;
    return function(n){
        if(n && n.tagName != 'BODY'){
            (Ext.enableNestedListenerRemoval) ? Ext.EventManager.purgeElement(n, true) : Ext.EventManager.removeAll(n);
            if(!d){
                d = document.createElement('div');
                d.id = '_removenode';
                d.style.cssText = 'position:absolute;display:none;left:-1000px;top:-1000px';
            }
//            d = d || document.createElement('<div id="_removenode" style="position:absolute;display:none;left:-1000px;top:-1000px">');
            if(!d.parentNode)document.body.appendChild(d);
            d.appendChild(n);
            d.innerHTML = '';
            delete Ext.elCache[n.id];
        }
    }
}() : function(n){
    if(n && n.parentNode && n.tagName != 'BODY'){
        (Ext.enableNestedListenerRemoval) ? Ext.EventManager.purgeElement(n, true) : Ext.EventManager.removeAll(n);
        n.parentNode.removeChild(n);
        delete Ext.elCache[n.id];
    }
}
$A.parseDate = function(str){
	if(typeof str == 'string'){  
		
		//TODO:临时, 需要服务端解决
//		if(str.indexOf('.0') !=-1) str = str.substr(0,str.length-2);
		
		var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);      
		if(results && results.length>3)      
	  		return new Date(parseInt(results[1]),parseInt(results[2],10) -1,parseInt(results[3],10));       
		results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);  
	    if(results && results.length>6)      
    	return new Date(parseInt(results[1]),parseInt(results[2],10) -1,parseInt(results[3],10),parseInt(results[4],10),parseInt(results[5],10),parseInt(results[6],10));       
	}      
  	return null;      
}
$A.getRenderer = function(renderer){
	if(!renderer) return null;
	var rder;
    if(renderer.indexOf('Aurora.') != -1){
        rder = $A[renderer.substr(7,renderer.length)]
    }else{
        rder = window[renderer];
    }
    return rder;
}
$A.RowNumberRenderer = function(value,record,name){
    if(record && record.ds){
        var ds = record.ds;
        return (ds.currentPage-1)*ds.pagesize + ds.indexOf(record) + 1;
    }
}
/**
 * 将日期转换成默认格式的字符串，默认格式是根据Aurora.defaultDateFormat来定义的.如果没有特殊指定,默认格式为yyyy-mm-dd
 * @param {Date} date 转换的日期
 * @return {String}
 */
$A.formatDate = function(date){
	if(!date)return '';
	if(date.format)return date.format($A.defaultDateFormat);
	return date;
}
/**
 * 将日期转换成yyyy-mm-dd HH:MM:ss格式的字符串
 * @param {Date} date 需要转换的日期
 * @return {String} 转换后的字符串
 */
$A.formatDateTime = function(date){
	if(!date)return '';
	if(date.format)return date.format($A.defaultDateTimeFormat);
	return date;
}
/**
 * 将数值根据精度转换成带有千分位的字符串
 * 
 * @param {Number} value 数值
 * @param {Number} decimalprecision 小数点位数
 * @return {String}
 */
$A.formatNumber = function(value,decimalprecision){
	if(Ext.isEmpty(value))return '';
	value = String(value).replace(/,/g,'');
	if(isNaN(value))return '';
	if(decimalprecision||decimalprecision===0) value=Number(value).toFixed(decimalprecision);
    var ps = value.split('.');
    var sub = (ps.length==2)?'.'+ps[1]:'';
    var whole = ps[0];
    var r = /(\d+)(\d{3})/;
    while (r.test(whole)) {
        whole = whole.replace(r, '$1' + ',' + '$2');
    }
    v = whole + sub;
    return v;   
}
/**
 * 将数值转换成带有千分位的字符串，并保留两位小数
 * 
 * @param {Number} value 数值
 * @return {String}
 */
$A.formatMoney = function(v){
    return $A.formatNumber(v,2)
}
/**
 * 将字符串的千分位去除
 * @param {Number} value 数值
 * @param {String} rv 带有千分位的数值字符串
 * @return {Number} 数值
 */
$A.removeNumberFormat = function(rv){
    rv = String(rv||'');
    while (rv.indexOf(',')!=-1) {
        rv = rv.replace(',', '');
    }
    return isNaN(rv) ? parseFloat(rv) : rv;
}

$A.EventManager = Ext.extend(Ext.util.Observable,{
	constructor: function() {
		$A.EventManager.superclass.constructor.call(this);
		this.initEvents();
	},
	initEvents : function(){
    	this.addEvents(
    		'ajaxerror',
    		'ajaxsuccess',
    		'ajaxfailed',
    		'ajaxstart',
    		'ajaxcomplete',
    		'valid',
	        'timeout'
		);    	
    }
});
$A.manager = new $A.EventManager();
$A.manager.on('ajaxstart',function(){
    $A.Status.show(_lang['eventmanager.start']);   
})
$A.manager.on('timeout',function(){
    $A.Status.hide();
})
$A.manager.on('ajaxerror',function(){
    $A.Status.hide();
})
$A.manager.on('ajaxcomplete',function(){
    $A.Status.hide();
})
$A.manager.on('ajaxsuccess',function(){
    $A.SideBar.show(_lang['eventmanager.success'])
})

$A.regEvent = function(name, hanlder){
	$A.manager.on(name, hanlder);
}

$A.validInfoType = 'area';
$A.validInfoTypeObj = '';
$A.setValidInfoType = function(type, obj){
	$A.validInfoType = type;
	$A.validInfoTypeObj = obj;
}

$A.invalidRecords = {};
$A.addInValidReocrd = function(id, record){
	var rs = $A.invalidRecords[id];
	if(!rs){
		$A.invalidRecords[id] = rs = [];
	}
	var has = false;
	for(var i=0;i<rs.length;i++){
		var r = rs[i];
		if(r.id == record.id){
			has = true;
			break;
		}
	}
	if(!has) {
		rs.add(record)
	}
}
$A.removeInvalidReocrd = function(id,record){
	var rs = $A.invalidRecords[id];
	if(!rs) return;
	for(var i=0;i<rs.length;i++){
		var r = rs[i];
		if(r.id == record.id){
			rs.remove(r)
			break;
		}
	}
}
$A.getInvalidRecords = function(pageid){
	var records = [];
	for(var key in $A.invalidRecords){
		var ds = $A.CmpManager.get(key)
		if(ds.pageid == pageid){
			var rs = $A.invalidRecords[key];
			records = records.concat(rs);
		}
	}
	return records;
}
$A.isInValidReocrdEmpty = function(pageid){
	var isEmpty = true;
	for(var key in $A.invalidRecords){
		var ds = $A.CmpManager.get(key)
		if(ds.pageid == pageid){
			var rs = $A.invalidRecords[key];
			if(rs.length != 0){
				isEmpty = false;
				break;
			}
		}
	}
	return isEmpty;
}
$A.manager.on('valid',function(manager, ds, valid){
	switch($A.validInfoType){
		case 'area':
			$A.showValidTopMsg(ds);
			break;
		case 'message':
			$A.showValidWindowMsg(ds);
			break;
	}
})
$A.showValidWindowMsg = function(ds) {
	var empty = $A.isInValidReocrdEmpty(ds.pageid);
	if(empty == true){
		if($A.validWindow)$A.validWindow.close();
	}
	if(!$A.validWindow && empty == false){
		$A.validWindow = $A.showWarningMessage(_lang['valid.fail'],'',400,200);
		$A.validWindow.on('close',function(){
			$A.validWindow = null;			
		})
	}
	var sb =[];
	var rs = $A.getInvalidRecords(ds.pageid);
	for(var i=0;i<rs.length;i++){
		var r = rs[i];
		var index = r.ds.data.indexOf(r)+1
		sb[sb.length] =_lang['valid.fail.note']+'<a href="#" onclick="$(\''+r.ds.id+'\').locate('+index+')">('+r.id+')</a>:';

		for(var k in r.valid){
			sb[sb.length] = r.valid[k]+';'
		}
		sb[sb.length]='<br/>';
	}
	if($A.validWindow)$A.validWindow.body.child('div').update(sb.join(''))
}
$A.pageids = [];
$A.showValidTopMsg = function(ds) {
	var empty = $A.isInValidReocrdEmpty(ds.pageid);
	if(empty == true){
		var d = Ext.get(ds.pageid+'_msg');
		if(d){
			d.hide();
			d.setStyle('display','none')
			d.update('');
		}
		return;
	}
	var rs = $A.getInvalidRecords(ds.pageid);
	var sb = [];
	for(var i=0;i<rs.length;i++){
		var r = rs[i];
		var index = r.ds.data.indexOf(r)+1
		sb[sb.length] =_lang['valid.fail.note']+'<a href="#" onclick="$(\''+r.ds.id+'\').locate('+index+')">('+r.id+')</a>:';

		for(var k in r.valid){
			sb[sb.length] = r.valid[k]+';'
		}
		sb[sb.length]='<br/>';		
	}
	var d = Ext.get(ds.pageid+'_msg');
	if(d){
		d.update(sb.join(''));
		d.show(true);
	}					
}
//Ext.get(document.documentElement).on('keydown',function(e){
//	if(e.altKey&&e.keyCode == 76){
//		if(!$A.logWindow) {
//			$A.logWindow = new $A.Window({modal:false, url:'log.screen',title:'AjaxWatch', height:550,width:530});	
//			$A.logWindow.on('close',function(){
//				delete 	$A.logWindow;		
//			})
//		}
//	}
//})
$A.startCustomization = function(){
    var cust = $A.CmpManager.get('_customization');
    if(cust==null){
        cust = new $A.Customization({id:'_customization'});    
    }
    cust.start();
}
$A.stopCustomization = function(){
    var cust = $A.CmpManager.get('_customization');
    if(cust!=null){
        cust.stop();
        $A.CmpManager.remove('_customization');
    }
}
/**
 * 将数字金额转换成大写金额.
 * 
 * @param {Number} amount 金额
 * @return {String} 大写金额
 */
$A.convertMoney = function(mnum){
	mnum = Math.abs(mnum);
	var unitArray = [["元", "万", "亿"], ["仟", "", "拾", "佰"],["零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"]];
	totalarray = new Array();

	totalarray = mnum.toString().split(".");
	if (totalarray.length == 1) {
		totalarray[1] = "00"
	} else if (totalarray[1].length == 1) {
		totalarray[1] += '0';
	}
	integerpart = new Array();
	decimalpart = new Array();
	var strout = "";
	for (var i = 0; i < totalarray[0].length; i++) {
		integerpart[i] = totalarray[0].charAt(i);
	}
	for (var i = 0; i < totalarray[1].length; i++) {
		decimalpart[i] = totalarray[1].charAt(i);
	}
	for (var i = 0; i < integerpart.length; i++) {
		var strTemp = (integerpart.length - i - 1) % 4 == 0
				? unitArray[0][parseInt((integerpart.length - i) / 4)]
				: (integerpart[i] == 0)
						? ""
						: unitArray[1][((integerpart.length - i) % 4)]
		strout = strout + unitArray[2][integerpart[i]] + strTemp;
	}
	strout = strout.replace(new RegExp(/零+/g), "零");
	strout = strout.replace("零万", "万");
	strout = strout.replace("零亿", "亿");
	strout = strout.replace("零元", "元");
	strout = strout.replace("亿万", "亿");
	var strdec = ""
	if (decimalpart[0] == 0 && decimalpart[1] == 0) {
		strdec = "整";
	} else {
		if (decimalpart[0] == 0) {
			strdec = "零"
		} else {
			strdec = unitArray[2][decimalpart[0]] + '角';
		}
		if (decimalpart[1] != 0) {
			strdec += unitArray[2][decimalpart[1]] + '分';
		}
	}
	strout += strdec;
	if (mnum < 0)
		strout = "负" + strout;
	return strout;
}
$A.setValidInfoType('tip'); 

$A.escapeHtml = function(str){
	if(Ext.isEmpty(str) || !Ext.isString(str))
		return str;
	return String(str).replace(/&/gm,'&amp;')
	.replace(/</gm,'&lt;').replace(/>/gm,'&gt;');
}
/**
 * @class Aurora.DataSet
 * @extends Ext.util.Observable
 * <p>DataSet是一个数据源，也是一个数据集合，它封装了所有数据的操作，校验，提交等操作.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.AUTO_ID = 1000;
$A.DataSet = Ext.extend(Ext.util.Observable,{
    constructor: function(config) {//datas,fields, type
        $A.DataSet.superclass.constructor.call(this);
        config = config || {};
        if(config.listeners){
            this.on(config.listeners);
        }
        this.pageid = config.pageid;
        this.spara = {};
        this.selected = [];
        this.sorttype = config.sorttype||'remote';
        this.maxpagesize = config.maxpagesize || 1000;
        this.pagesize = config.pagesize || 10;
        if(this.pagesize > this.maxpagesize) 
        	this.pagesize = this.maxpagesize;
        this.submiturl = config.submiturl || '';
        this.queryurl = config.queryurl || '';
        this.fetchall = config.fetchall||false;
        this.totalcountfield = config.totalcountfield || 'totalCount';
        this.selectable = config.selectable||false;
        this.selectionmodel = config.selectionmodel||'multiple';
        this.selectfunction = config.selectfunction;
        this.autocount = config.autocount;
        this.autopagesize = config.autopagesize;
        this.bindtarget = config.bindtarget;
        this.bindname = config.bindname;
        this.processfunction = config.processfunction;
        this.loading = false;
        this.qpara = {};
        this.fields = {};
        this.resetConfig();
        this.id = config.id || Ext.id();
        $A.CmpManager.put(this.id,this) 
        if(this.bindtarget&&this.bindname) this.bind($(this.bindtarget),this.bindname);//$(this.bindtarget).bind(this.bindname,this);
        this.qds = Ext.isEmpty(config.querydataset) ? null :$(config.querydataset);
        if(this.qds != null && this.qds.getCurrentRecord() == null) this.qds.create();
        this.initEvents();
        if(config.fields)this.initFields(config.fields)
        if(config.datas && config.datas.length != 0) {
            var datas=config.datahead?this.convertData(config.datahead,config.datas):config.datas;
            this.autocount = false;
            this.loadData(datas);
            //this.locate(this.currentIndex); //不确定有没有影响
        }
        if(config.autoquery === true) {
            var sf = this;
            $A.onReady(function(){
               sf.query(); 
            });
        }
        if(config.autocreate==true) {
            if(this.data.length == 0)
            this.create();
        }
    },
    convertData : function(head,datas){
        var nds=[];
        for(var i=0;i<datas.length;i++){
            var d=datas[i],nd={};
            for(var j=0;j<head.length;j++){
                if(!Ext.isEmpty(d[j], true))
                nd[head[j]]=d[j];
            }
            nds.push(nd);
        }
        return nds;
    },
    destroy : function(){
    	if(this.qtId){
			Ext.Ajax.abort(this.qtId);
		}
        if(this.bindtarget&&this.bindname){
            var bd = $A.CmpManager.get(this.bindtarget)
            if(bd)bd.clearBind();
        }
        $A.CmpManager.remove(this.id);
        delete $A.invalidRecords[this.id]
    },
    reConfig : function(config){
        this.resetConfig();
        Ext.apply(this, config);
    },
    /**
     * 取消绑定.
     */
    clearBind : function(){
        var name = this.bindname;
        var ds = this.fields[name].pro['dataset'];
        if(ds)
        ds.processBindDataSetListener(this,'un');
        delete this.fields[name];
    },
    processBindDataSetListener : function(ds,ou){
        var bdp = this.onDataSetModify;
//        this[ou]('beforecreate', this.beforeCreate, this);//TODO:有待测试
        this[ou]('add', bdp, this);
        this[ou]('remove', bdp, this);
        this[ou]('select', this.onDataSetSelect, this);
        this[ou]('update', bdp, this);
        this[ou]('indexchange', bdp, this);
        this[ou]('clear', bdp, this);
        this[ou]('load', this.onDataSetLoad, this);
        this[ou]('reject', bdp, this);
        ds[ou]('indexchange',this.onDataSetIndexChange, this);
        ds[ou]('load',this.onBindDataSetLoad, this);
        ds[ou]('remove',this.onBindDataSetLoad, this);
        ds[ou]('clear',this.removeAll, this);
    },
    /**
     * 将组件绑定到某个DataSet的某个Field上.
     * @param {Aurora.DataSet} dataSet 绑定的DataSet.
     * @param {String} name Field的name. 
     * 
     */
    bind : function(ds, name){
        if(ds.fields[name]) {
            alert('重复绑定 ' + name);
            return;
        }
        this.processBindDataSetListener(ds,'un');
        this.processBindDataSetListener(ds,'on');
        var field = new $A.Record.Field({
            name:name,
            type:'dataset',
            dataset:this
        });
        ds.fields[name] = field;
    },
    onBindDataSetLoad : function(ds,options){
        if(ds.getAll().length == 0) this.removeAll();
    },
    onDataSetIndexChange : function(ds, record){
        if(!record.get(this.bindname) && record.isNew != true){
            this.qpara = {};
            Ext.apply(this.qpara,record.data);
            this.query(1,{record:record});
        }   
    },
    onDataSetModify : function(){
        var bt = $A.CmpManager.get(this.bindtarget);
        if(bt){
            this.refreshBindDataSet(bt.getCurrentRecord(),this.getConfig())
        }
    },
    onDataSetSelect : function(ds,record){
        var bt = $A.CmpManager.get(this.bindtarget);
        if(bt){
            var datas = bt.data;
            var found = false;
            for(var i = 0;i<datas.length;i++){
                var dr = datas[i];
                var dc = dr.get(this.bindname);
                if(dc){
                    for(var j = 0;j<dc.data.length;j++){
                        var r = dc.data[j];
                        if(r.id == record.id){
                            dc.selected = this.selected;
                            found = true;
                            break;
                        }
                    }
                    if(found) break;
                }
            }
        }
    },
    onDataSetLoad : function(ds,options){
        var record;
        if(options && options.opts && options.opts.record){
            record = options.opts.record;
        }else{
            var bt = $A.CmpManager.get(this.bindtarget);
            if(bt) record = bt.getCurrentRecord();          
        }
        if(record)
        this.refreshBindDataSet(record,ds.getConfig())
    },
    refreshBindDataSet: function(record,config){
        if(!record)return;
        //record.set(this.bindname,config,true)//this.getConfig()
        record.data[this.bindname] = config;

//      for(var k in this.fields){
//          var field = this.fields[k];
//          if(field.type == 'dataset'){  
//              var ds = field.pro['dataset'];
////                if(ds && clear==true)ds.resetConfig();
//              record.set(field.name,ds.getConfig(),true)
//          }
//      }
    },
    beforeCreate: function(ds, record, index){
        if(this.data.length == 0){
            this.create({},false);
        }
    },
    resetConfig : function(){
        this.data = [];
        this.selected = [];
        this.gotoPage = 1;
        this.currentPage = 1;
        this.currentIndex = 1;
        this.totalCount = 0;
        this.totalPage = 0;
        this.isValid = true;
//      this.bindtarget = null;
//        this.bindname = null;
    },
    getConfig : function(){
        var c = {};
//      c.id = this.id;
        c.xtype = 'dataset';
        c.data = this.data;
        c.selected = this.selected;
        c.isValid = this.isValid;
//      c.bindtarget = this.bindtarget;
//        c.bindname = this.bindname;
        c.gotoPage = this.gotoPage;
        c.currentPage = this.currentPage;
        c.currentIndex = this.currentIndex;
        c.totalCount = this.totalCount;
        c.totalPage = this.totalPage;
        c.fields = this.fields;
        return c;
    },
    initEvents : function(){
        this.addEvents( 
            /**
             * @event ajaxfailed
             * ajax调用失败.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Object} res res.
             * @param {Object} opt opt.
             */
            'ajaxfailed',
            /**
             * @event beforecreate
             * 数据创建前事件.返回true则新增一条记录,false则不新增直接返回
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Object} object 新增的数据对象.
             */
            'beforecreate',
            /**
             * @event metachange
             * meta配置改变事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 当前的record.
             * @param {Aurora.Record.Meta} meta meta配置对象.
             * @param {String} type 类型.
             * @param {Object} value 值.
             */
            'metachange',
            /**
             * @event fieldchange
             * field配置改变事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 当前的record.
             * @param {Aurora.Record.Field} field Field配置对象.
             * @param {String} type 类型.
             * @param {Object} value 值.
             */
            'fieldchange',
            /**
             * @event add
             * 数据增加事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 增加的record.
             * @param {Number} index 指针.
             */
            'add',
            /**
             * @event remove
             * 数据删除事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 删除的record.
             * @param {Number} index 指针.
             */
            'remove',
            /**
             * @event beforeremove
             * 数据删除前.如果为true则删除一条记录,false则不删除直接返回
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Array} records 将要删除的数据集合
             */
            'beforeremove',
            /**
             * @event afterremove
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */
            'afterremove',
            /**
             * @event update
             * 数据更新事件.
             * "update", this, record, name, value
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 更新的record.
             * @param {String} name 更新的field.
             * @param {Object} value 更新的值.
             * @param {Object} oldvalue 更新前的值.
             */
            'update',
            /**
             * @event clear
             * 清除数据事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */
            'clear',
            /**
             * @event query
             * 查询事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */ 
            'query',
            /**
             * @event beforeload
             * 准备加载数据事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */ 
            'beforeload',
            /**
             * @event load
             * 加载数据事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */ 
            'load',
            /**
             * @event loadfailed
             * 加载数据失败.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Object} res res.
             * @param {Object} opt opt.
             */ 
            'loadfailed',
            /**
             * @event refresh
             * 刷新事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */ 
            'refresh',
            /**
             * @event valid
             * DataSet校验事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 校验的record.
             * @param {String} name 校验的field.
             * @param {Boolean} valid 校验结果. true 校验成功  false 校验失败
             */ 
            'valid',
            /**
             * @event indexchange
             * DataSet当前指针改变事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 当前record.
             */ 
            'indexchange',
            /**
             * @event beforeselect
             * 选择数据前事件. 返回true表示可以选中,false表示不能选中
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 选择的record.
             */ 
            'beforeselect',
            /**
             * @event select
             * 选择数据事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 选择的record.
             */ 
            'select',
            /**
             * @event unselect
             * 取消选择数据事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 取消选择的record.
             */
            'unselect',
            /**
             * @event selectall
             * 选择数据事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */ 
            'selectall',
            /**
             * @event unselectall
             * 取消选择数据事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */
            'unselectall',
            /**
             * @event reject
             * 数据重置事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Aurora.Record} record 取消选择的record.
             * @param {String} name 重置的field.
             * @param {Object} value 重置的值.
             */
            'reject',
            /**
             * @event beforesubmit
             * 数据提交前事件.如果为false则中断提交请求
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             */
            'beforesubmit',
            /**
             * @event submit
             * 数据提交事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {String} url 提交的url.
             * @param {Array} datas 提交的数据.
             */
            'submit',
            /**
             * @event submitsuccess
             * 数据提交成功事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Object} res 返回结果res.
             */
            'submitsuccess',
            /**
             * @event submitfailed
             * 数据提交失败事件.
             * @param {Aurora.DataSet} dataSet 当前DataSet.
             * @param {Object} res 返回结果res.
             */
            'submitfailed'
        );      
    },
    initFields : function(fields){
        for(var i = 0, len = fields.length; i < len; i++){
            var field = new $A.Record.Field(fields[i]);
            this.fields[field.name] = field;
        }
    },
    /**
     * 获取Field配置.
     * @param {String} name Field的name. 
     * @return {Aurora.Record.Field} field配置对象
     */
    getField : function(name){
        return this.fields[name];
    },
    beforeLoadData : function(datas){
        if(this.processfunction) {
            var fun = $A.getRenderer(this.processfunction);
            if(fun){
                return fun.call(window,datas);
            }
        }
        return datas;
    },
    loadData : function(datas, num, options){
        datas = this.beforeLoadData(datas);
        this.data = [];
        this.selected = [];
        if(num && this.fetchall == false) {
            this.totalCount = num;
            this.totalPage = Math.ceil(this.totalCount/this.pagesize);
        }else{
            this.totalCount = datas.length;
            this.totalPage = 1;
        }
        
        
        for(var i = 0, len = datas.length; i < len; i++){
            var data = datas[i].data||datas[i];
            for(var key in this.fields){
                var field = this.fields[key];
                if(field){
                    data[key] = this.processData(data,key,field)
                }
            }
            var record = new $A.Record(data,datas[i].field);
            record.setDataSet(this);
            this.data.push(record);
        }
//        if(this.sortInfo) this.sort();
        
        this.fireEvent("beforeload", this, this.data);
        
        var needFire = true;
        if(this.bindtarget && options){
           var cr = $A.CmpManager.get(this.bindtarget).getCurrentRecord();
           if(options.opts.record && cr!=options.opts.record){
               this.refreshBindDataSet(options.opts.record,this.getConfig());
               needFire = false;
           }
        }
        if(needFire)
        this.fireEvent("load", this, options);
    },
    sort : function(f, direction){
        if(this.getAll().length==0)return;
        if(this.sorttype == 'remote'){
            if(direction=='') {
                delete this.qpara['ORDER_FIELD'];
                delete this.qpara['ORDER_TYPE'];
            }else{
                this.setQueryParameter('ORDER_FIELD', f);
                this.setQueryParameter('ORDER_TYPE', direction);            
            }
            this.query();
        }else{
            this.data.sort(function(a, b){
                var rs = a.get(f) > b.get(f)
                return (direction == 'desc' ? (rs ? -1 : 1) : (rs ? 1 : -1));
            })
            this.fireEvent('refresh',this);
        }
    },
    /**
     * 创建一条记录
     * @param {Object} data 数据对象
     * @return {Aurora.Record} record 返回创建的record对象
     */
    create : function(data, valid){
        data = data||{}
        if(this.fireEvent("beforecreate", this, data)){
    //      if(valid !== false) if(!this.validCurrent())return;
            var dd = {};
            for(var k in this.fields){
                var field = this.fields[k];
                var dv = field.getPropertity('defaultvalue');
                if(dv && !data[field.name]){
                    dd[field.name] = dv;
                }else {
                    dd[field.name] = this.processValueListField(dd,data[field.name],field);
                }
            }
            var data = Ext.apply(data||{},dd);
            var record = new $A.Record(data);
            this.add(record); 
    //        var index = (this.currentPage-1)*this.pagesize + this.data.length;
    //        this.locate(index, true);
            return record;
        }
    },
    /**
     * 获取所有新创建的数据. 
     * @return {Array} 所有新创建的records
     */
    getNewRecrods: function(){
        var records = this.getAll();
        var news = [];
        for(var k = 0,l=records.length;k<l;k++){
            var record = records[k];
            if(record.isNew == true){
                news.push(record);
            }
        }
        return news;
    },
//    validCurrent : function(){
//      var c = this.getCurrentRecord();
//      if(c==null)return true;
//      return c.validateRecord();
//    },
    /**
     * 新增数据. 
     * @param {Aurora.Record} record 需要新增的Record对象. 
     */
    add : function(record){
        record.isNew = true;
        record.setDataSet(this);
        var index = this.data.length;
        this.data.add(record);
//        for(var k in this.fields){
//          var field = this.fields[k];
//          if(field.type == 'dataset'){                
//              var ds = field.pro['dataset'];
//              ds.resetConfig()            
//          }
//      }
        var index = (this.currentPage-1)*this.pagesize + this.data.length;
        this.currentIndex = index;
        this.fireEvent("add", this, record, index);
        this.locate(index, true);
    },
    /**
     * 获取当前Record的数据对象
     * @return {Object}
     */
    getCurrentObject : function(){
        return this.getCurrentRecord().getObject();
    },
    /**
     * 获取当前指针的Record. 
     * @return {Aurora.Record} 当前指针所处的Record
     */
    getCurrentRecord : function(){
        if(this.data.length ==0) return null;
        return this.data[this.currentIndex - (this.currentPage-1)*this.pagesize -1];
    },
    /**
     * 插入数据. 
     * @param {Number} index  指定位置. 
     * @param {Array} records 需要新增的Record对象集合.
     */
    insert : function(index, records){
        records = [].concat(records);
        var splice = this.data.splice(index,this.data.length);
        for(var i = 0, len = records.length; i < len; i++){
            records[i].setDataSet(this);
            this.data.add(records[i]);
        }
        this.data = this.data.concat(splice);
        this.fireEvent("add", this, records, index);
    },
    /**
     * 移除数据.  
     * @param {Aurora.Record} record 需要移除的Record.
     */
    remove : function(record){
        if(!record){
            record = this.getCurrentRecord();
        }
        if(!record)return;
        var rs = [].concat(record);
        if(this.fireEvent("beforeremove", this, rs)){
            var rrs = [];
            for(var i=0;i<rs.length;i++){
                var r = rs[i]
                if(r.isNew){
                    this.removeLocal(r);
                }else{          
                    rrs[rrs.length] = r;
                }
            }
            this.removeRemote(rrs);
        }
    },
    removeRemote: function(rs){     
        if(this.submiturl == '') return;
        var p = [];
        for(var k=0;k<rs.length;k++){
            var r = rs[k]
            for(var key in this.fields){
                var f = this.fields[key];
                if(f && f.type == 'dataset') delete r.data[key];
            }
            var d = Ext.apply({}, r.data);
            d['_id'] = r.id;
            d['_status'] = 'delete';
            p[k] = d
        }
//      var p = [d];
//      for(var i=0;i<p.length;i++){
//          p[i] = Ext.apply(p[i],this.spara)
//      }
        if(p.length > 0) {
            var opts;
            if(this.bindtarget){
                var bd = $A.CmpManager.get(this.bindtarget);
                opts = {record:bd.getCurrentRecord(),dataSet:this};
            }
            $A.request({url:this.submiturl, para:p, ext:this.spara,success:this.onRemoveSuccess, error:this.onSubmitError, scope:this, failure:this.onAjaxFailed,opts:opts});
        }
    
    },
    onRemoveSuccess: function(res,options){
        if(res.result.record){
            var datas = [].concat(res.result.record);
            if(this.bindtarget){
                var bd = $A.CmpManager.get(this.bindtarget);
                if(bd.getCurrentRecord()==options.opts.record){
                    for(var i=0;i<datas.length;i++){
                        var data = datas[i];
                        this.removeLocal(this.findById(data['_id']),true); 
                    }
                }else{
                    var config = options.opts.record.get(this.bindname);
                    var ds = new $A.DataSet({});
                    ds.reConfig(config);
                    for(var i=0;i<datas.length;i++){
                        var data = datas[i];
                        ds.removeLocal(ds.findById(data['_id']),true);
                    }
                    this.refreshBindDataSet(options.opts.record,ds.getConfig())
                    delete ds;
                }
            }else{
                for(var i=0;i<datas.length;i++){
                    var data = datas[i];
                    this.removeLocal(this.findById(data['_id']),true); 
                }
            }
            this.fireEvent('afterremove',this);
        }
    },
    removeLocal: function(record,count,notLocate){
        $A.removeInvalidReocrd(this.id, record)
        var index = this.data.indexOf(record);      
        if(index == -1)return;
        this.data.remove(record);
        if(count) this.totalCount --;
        this.selected.remove(record);
//        if(this.data.length == 0){
//          this.removeAll();
//          return;
//        }
        if(!notLocate)
        if(this.data.length != 0){
            var lindex = this.currentIndex - (this.currentPage-1)*this.pagesize;
            if(lindex<0)return;
            if(lindex<=this.data.length){
                this.locate(this.currentIndex,true);
            }else{
                this.pre();
            }
        }
        this.fireEvent("remove", this, record, index);      
    },
    /**
     * 获取当前数据集下的所有数据.  
     * @return {Array} records 当前数据集的所有Record.
     */
    getAll : function(){
        return this.data;       
    },
    /**
     * 查找数据.  
     * @param {String} property 查找的属性.
     * @param {Object} value 查找的属性的值.
     * @return {Aurora.Record} 符合查找条件的第一个record
     */
    find : function(property, value){
        var r = null;
        this.each(function(record){
            var v = record.get(property);
            if(v ==value){
                r = record;
                return false;               
            }
        }, this)
        return r;
    },
    /**
     * 根据id查找数据.  
     * @param {Number} id id.
     * @return {Aurora.Record} 查找的record
     */
    findById : function(id){
        var find = null;
        for(var i = 0,len = this.data.length; i < len; i++){
            if(this.data[i].id == id){
                find = this.data[i]
                break;
            }
        }
        return find;
    },
    /**
     * 删除所有数据.
     */
    removeAll : function(){
        this.currentIndex = 1;
        this.totalCount = 0;
        this.data = [];
        this.selected = [];
        this.fireEvent("clear", this);
    },
    /**
     * 返回指定record的位置
     * @param {Aurora.Record} record
     * @return {int}
     */
    indexOf : function(record){
        return this.data.indexOf(record);
    },
    /**
     * 获取指定位置的record
     * @param {Number} 位置
     */
    getAt : function(index){
        return this.data[index];
    },
    each : function(fn, scope){
        var items = [].concat(this.data); // each safe for removal
        for(var i = 0, len = items.length; i < len; i++){
            if(fn.call(scope || items[i], items[i], i, len) === false){
                break;
            }
        }
    },
    processCurrentRow : function(){
        var r = this.getCurrentRecord();
        for(var k in this.fields){
            var field = this.fields[k];
            if(field.type == 'dataset'){
                var ds = field.pro['dataset'];
                if(r && r.data[field.name]){
                    ds.reConfig(r.data[field.name]);
                }else{
                    ds.resetConfig();
                }
                ds.fireEvent('refresh',ds);
                ds.processCurrentRow();
            }
        }
        if(r) this.fireEvent("indexchange", this, r);
    },
    /**
     * 获取所有选择的数据.
     * @return {Array} 所有选择数据.
     */
    getSelected : function(){
        return this.selected;
    },
    /**
     * 选择所有数据.
     */
    selectAll : function(){
        for(var i=0,l=this.data.length;i<l;i++){
            if(!this.execSelectFunction(this.data[i]))continue;
            this.select(this.data[i],true);
        }
        this.fireEvent('selectall', this , this.selected);
    },
    /**
     * 取消所有选择.
     */
    unSelectAll : function(){
        for(var i=0,l=this.data.length;i<l;i++){
            if(!this.execSelectFunction(this.data[i]))continue;
            this.unSelect(this.data[i],true);
        }
        this.fireEvent('unselectall', this , this.selected);
    },
    /**
     * 选择某个record.
     * @param {Aurora.Record} record 需要选择的record.
     */
    select : function(r,isSelectAll){
        if(!this.selectable)return;
        if(typeof(r) == 'string'||typeof(r) == 'number') r = this.findById(r);
        if(!r) return;
        if(this.selected.indexOf(r) != -1)return;
//        if(!this.execSelectFunction(r))return;
        if(this.fireEvent("beforeselect",this,r)){
            if(this.selectionmodel == 'multiple'){
                this.selected.add(r);
                this.fireEvent('select', this, r , isSelectAll);
            }else{
                var or = this.selected[0];
                this.unSelect(or);
                this.selected = []
                this.selected.push(r);
                this.fireEvent('select', this, r);
            }
        }
    },
    /**
     * 取消选择某个record.
     * @param {Aurora.Record} record 需要取消选择的record.
     */
    unSelect : function(r,isSelectAll){
        if(!this.selectable)return;
        if(typeof(r) == 'string'||typeof(r) == 'number') r = this.findById(r);
        if(!r) return;
        if(this.selected.indexOf(r) == -1) return;
        this.selected.remove(r);
        this.fireEvent('unselect', this, r , isSelectAll);
    },
    execSelectFunction:function(r){
        if(this.selectfunction){
            var selfun = $A.getRenderer(this.selectfunction);
            if(selfun == null){
                alert("未找到"+this.selectfunction+"方法!")
            }else{
                var b=selfun.call(window,r);
                if(Ext.isDefined(b))return b;
            }
        }
        return true;
    },
    /**
     * 定位到某个指针位置.
     * @param {Number} index 指针位置.
     */
    locate : function(index, force){
        if(this.currentIndex === index && force !== true) return;
        if(this.fetchall == true && index > ((this.currentPage-1)*this.pagesize + this.data.length)) return;
        //对于没有autcount的,判断最后一页
        if(!this.autocount && index > ((this.currentPage-1)*this.pagesize + this.data.length) && this.data.length < this.pagesize) return;
//      if(valid !== false) if(!this.validCurrent())return;
        if(index<=0)return;
        if(index <=0 || (this.autocount && (index > this.totalCount + this.getNewRecrods().length)))return;
        var lindex = index - (this.currentPage-1)*this.pagesize;
        if(this.data[lindex - 1]){
            this.currentIndex = index;
        }else{
            if(this.isModified()){
                $A.showInfoMessage(_lang['dataset.info'], _lang['dataset.info.locate'])
            }else{
                this.currentIndex = index;
                this.currentPage =  Math.ceil(index/this.pagesize);
                this.query(this.currentPage);
                return;
            }
        }
        this.processCurrentRow();
        index = index - this.pagesize*(this.gotoPage-1);
        if(this.selectionmodel == 'single') this.select(this.getAt(index-1));
    },
    /**
     * 定位到某页.
     * @param {Number} page 页数.
     */
    goPage : function(page){
        if(page >0) {
            this.gotoPage = page;
            var go = (page-1)*this.pagesize + 1;
            var news = this.getAll().length-this.pagesize;
            if(this.currentPage < page && news > 0)go+=news;
//          var go = Math.max(0,page-2)*this.pagesize + this.data.length + 1;
            this.locate(go);
        }
    },
    /**
     * 定位到所有数据的第一条位置.
     */
    first : function(){
        this.locate(1);
    },
    /**
     * 向前移动一个指针位置.
     */
    pre : function(){
        this.locate(this.currentIndex-1);       
    },
    /**
     * 向后移动一个指针位置.
     */
    next : function(){
        this.locate(this.currentIndex+1);
    },
    /**
     * 定位到第一页.
     */
    firstPage : function(){
        this.goPage(1);
    },
    /**
     * 向前移动一页.
     */
    prePage : function(){
        this.goPage(this.currentPage -1);
    },
    /**
     * 向后移动一页.
     */
    nextPage : function(){        
        this.goPage(this.currentPage +1);
    },
    /**
     * 定位到最后一页.
     */
    lastPage : function(){
        this.goPage(this.totalPage);
    },
    /**
     * 仅对dataset本身进行校验,不校验绑定的子dataset.
     * @return {Boolean} valid 校验结果.
     */
    validateSelf : function(){
        return this.validate(true,false)
    },
    /**
     * 对当前数据集进行校验.
     * @return {Boolean} valid 校验结果.
     */
    validate : function(fire,vc){
        this.isValid = true;
        var current = this.getCurrentRecord();
        if(!current)return true;
        var records = this.getAll();
        var dmap = {};
        var hassub = false;
        var unvalidRecord = null;
        
        if(vc !== false)
        for(var k in this.fields){
            var field = this.fields[k];
            if(field.type == 'dataset'){
                hassub = true;
                var d = field.pro['dataset'];
                dmap[field.name] = d;
            }
        }
        for(var k = 0,l=records.length;k<l;k++){
            var record = records[k];
            //有些项目是虚拟的字段,例如密码修改
//          if(record.dirty == true || record.isNew == true) {
                if(!record.validateRecord()){
                    this.isValid = false;
                    unvalidRecord = record;
                    $A.addInValidReocrd(this.id, record);
                }else{
                    $A.removeInvalidReocrd(this.id, record);
                }
                if(this.isValid == false) {
                    if(hassub)break;
                } else {
                    for(var key in dmap){
                        var ds = dmap[key];
                        if(record.data[key]){
                            ds.reConfig(record.data[key]);
                            if(!ds.validate(false)) {
                                this.isValid = false;
                                unvalidRecord = record;
                            }else
                            	ds.reConfig(current.data[key]);//循环校验完毕后,重新定位到当前行
                        }
                    }
                    
                    if(this.isValid == false) {
                        break;
                    }
                                    
//              }
            }
        }
        
        if(unvalidRecord != null){
            var r = this.indexOf(unvalidRecord);
            if(r!=-1)this.locate(r+1);
        }
        if(fire !== false) {
            $A.manager.fireEvent('valid', $A.manager, this, this.isValid);
            if(!this.isValid) $A.showInfoMessage(_lang['dataset.info'], _lang['dataset.info.validate']);
        }
        return this.isValid;
    },
    /**
     * 设置查询的Url.
     * @param {String} url 查询的Url.
     */
    setQueryUrl : function(url){
        this.queryurl = url;
    },
    /**
     * 设置查询的参数.
     * @param {String} para 参数名.
     * @param {Object} value 参数值.
     */
    setQueryParameter : function(para, value){
        this.qpara[para] = value;
    },
    /**
     * 设置查询的DataSet.
     * @param {Aurora.DataSet} ds DataSet.
     */
    setQueryDataSet : function(ds){ 
        this.qds = ds;
        if(this.qds.getCurrentRecord() == null) this.qds.create();
    },
    /**
     * 设置提交的Url.
     * @param {String} url 提交的Url.
     */
    setSubmitUrl : function(url){
        this.submiturl = url;
    },
    /**
     * 设置提交的参数.
     * @param {String} para 参数名.
     * @param {Object} value 参数值.
     */
    setSubmitParameter : function(para, value){
        this.spara[para] = value;
    },
	/**
	 * 等待ds中的所有record都ready后执行回调函数
	 * @param {String} isAll 判断所有的record还是选中的record
	 * @param {Function} callback 回调函数
	 * @param {Object} scope 回调函数的作用域
	 */
    wait : function(isAll,callback,scope){
    	var records = isAll ? this.getAll() : this.getSelected(),
			intervalId = setInterval(function(){
		        for(var i = 0;i < records.length;i++){
		            if(!records[i].isReady)return;
		        }
		        clearInterval(intervalId);
		        if(callback)callback.call(scope||window);
		    },10);
    },
    /**
     * 查询数据.
     * @param {Number} page(可选) 查询的页数.
     */
    query : function(page,opts){
        $A.slideBarEnable = $A.SideBar.enable;
        $A.SideBar.enable = false;
        if(!this.queryurl) return;
        if(this.qds) {
            if(this.qds.getCurrentRecord() == null) this.qds.create();
            this.qds.wait(true,function(){
	    		if(!this.qds.validate()) return;
                this.doQuery(page,opts);
	    	},this);
        }else{
            this.doQuery(page,opts);
        }
    },
    doQuery:function(page,opts){
        var r;
        if(this.qds)r = this.qds.getCurrentRecord();
        if(!page) this.currentIndex = 1;
        this.currentPage = page || 1;
        var q = {};
        if(r != null) Ext.apply(q, r.data);
        Ext.apply(q, this.qpara);
        for(var k in q){
           var v = q[k];
           if(Ext.isEmpty(v,false)) delete q[k];
        }
        var para = 'pagesize='+this.pagesize + 
                      '&pagenum='+this.currentPage+
                      '&_fetchall='+this.fetchall+
                      '&_autocount='+this.autocount
//                    + '&_rootpath=list'
        var url = this.queryurl +(this.queryurl.indexOf('?') == -1?'?':'&') + para;
        this.loading = true;
        this.fireEvent("query", this);
//      this.fireBindDataSetEvent("beforeload", this);//主dataset无数据,子dataset一直loading
        if(this.qtId) Ext.Ajax.abort(this.qtId);
        this.qtId = $A.request({url:url, para:q, success:this.onLoadSuccess, error:this.onLoadError, scope:this,failure:this.onAjaxFailed,opts:opts,ext:opts?opts.ext:null});
    },
    /**
     * 判断当前数据集是否发生改变.
     * @return {Boolean} modified 是否发生改变.
     */
    isModified : function(){
        var modified = false;
        var records = this.getAll();
        for(var k = 0,l=records.length;k<l;k++){
            var record = records[k];
            if(record.dirty == true || record.isNew == true) {
                modified = true;
                break;
            }else{
                for(var key in this.fields){
                    var field = this.fields[key];
                    if(field.type == 'dataset'){                
                        var ds = field.pro['dataset'];
                        ds.reConfig(record.data[field.name]);
                        if(ds.isModified()){
                            modified = true;
                            break;
                        }
                    }
                }
            }
        }
        return modified;
    },
//    isDataModified : function(){
//      var modified = false;
//      for(var i=0,l=this.data.length;i<l;i++){
//          var r = this.data[i];           
//          if(r.dirty || r.isNew){
//              modified = true;
//              break;
//          }
//      }
//      return modified;
//    },
    /**
     * 以json格式返回当前数据集.
     * @return {Object} json 返回的json对象.
     */
    getJsonData : function(selected,fields){
        var datas = [];
        var items = this.data;
        if(selected) items = this.getSelected();
        for(var i=0,l=items.length;i<l;i++){
            var r = items[i];
            var isAdd = r.dirty || r.isNew
            var d = Ext.apply({}, r.data);
            d['_id'] = r.id;
            d['_status'] = r.isNew ? 'insert' : 'update';
            for(var k in r.data){
            	if(fields && fields.indexOf(k)==-1){
            		delete d[k];
            	}else{
	                var item = d[k];
	                if(item && item.xtype == 'dataset'){
	                	//if(item.data.length > 0){
		                    var ds = new $A.DataSet({});//$(item.id);
		                    //ds.fields = item.data[0].ds.fields;
	                    	ds.reConfig(item)
		                    isAdd = isAdd == false ? ds.isModified() :isAdd;
		                    d[k] = ds.getJsonData();
	                	//}
	                }
            	}
            }
            if(isAdd||selected){
                datas.push(d);              
            }
        }
        
        return datas;
    },
    doSubmit : function(url, items){
        if(!this.validate()){           
            return;
        }
        this.fireBindDataSetEvent("submit",url,items);
        this.submiturl = url||this.submiturl;
        if(this.submiturl == '') return;
        var p = items;//this.getJsonData();
        for(var i=0;i<p.length;i++){
            var data = p[i]
            for(var key in data){
                var f = this.fields[key];
                if(f && f.type != 'dataset' && data[key]==='')data[key]=null;
            }
//            p[i] = Ext.apply(p[i],this.spara)
        }
        
        //if(p.length > 0) {
//            this.fireEvent("submit", this);
            $A.request({url:this.submiturl, para:p, ext:this.spara,success:this.onSubmitSuccess, error:this.onSubmitError, scope:this,failure:this.onAjaxFailed});
        //}
    },
    /**
     * 提交选中数据.
     * @param {String} url(可选) 提交的url.
     * @param {Array} fields(可选) 根据选定的fields提交.
     */
    submitSelected : function(url,fields){
    	this.wait(false,function(){
    		if(this.fireEvent("beforesubmit",this)){
                var d = this.getJsonData(true,fields);
                this.doSubmit(url,d);
            }
    	},this);
    },
    /**
     * 提交数据.
     * @param {String} url(可选) 提交的url.
     * @param {Array} fields(可选) 根据选定的fields提交.
     */
    submit : function(url,fields){
    	this.wait(true,function(){
    		if(this.fireEvent("beforesubmit",this)){
                var d = this.getJsonData(false,fields);
                this.doSubmit(url,d);
            }
    	},this);
    },
    /**
     * post方式提交数据.
     * @param {String} url(可选) 提交的url.
     */
    post : function(url){
        var r=this.getCurrentRecord();
        if(!r)return;
        this.wait(true,function(){
    		if(this.validate())$A.post(url,r.data);
    	},this);
    },
    /**
     * 重置数据.
     */
    reset : function(){
        var record=this.getCurrentRecord();
        if(!record&&!record.fields)return;
        for(var c in record.fields){
            var v=record.fields[c].get('defaultvalue');
            if(v!=record.get(c))
                record.set(c,v==undefined||v==null?"":v);
        }
    },
    fireBindDataSetEvent : function(){//event
        var a = Ext.toArray(arguments);
        var event = a[0];
        a[0] = this;
        this.fireEvent.apply(this,[event].concat(a))
//      this.fireEvent(event,this);
        for(var k in this.fields){
            var field = this.fields[k];
            if(field.type == 'dataset'){  
                var ds = field.pro['dataset'];
                if(ds) {
                    ds.fireBindDataSetEvent(event)
                }
            }
        }
    },
    afterEdit : function(record, name, value,oldvalue) {
        this.fireEvent("update", this, record, name, value,oldvalue);
    },
    afterReject : function(record, name, value) {
        this.fireEvent("reject", this, record, name, value);
    },
    onSubmitSuccess : function(res){
        var datas = []
        if(res.result.record){
            datas = [].concat(res.result.record);
            this.commitRecords(datas,true)
        }
        this.fireBindDataSetEvent('submitsuccess',res);
    },
    commitRecords : function(datas,fire,record){
        //this.resetConfig();
        for(var i=0,l=datas.length;i<l;i++){
            var data = datas[i];
            var r = this.findById(data['_id']);
            if(r.isNew) this.totalCount ++;
            if(!r) return;
            r.commit();
            for(var k in data){
                var field = k;
                var f = this.fields[field];
                if(f && f.type == 'dataset'){
                    var ds = f.pro['dataset'];
                    ds.reConfig(r.data[f.name]);
                    if(data[k].record) {
                        ds.commitRecords([].concat(data[k].record),this.getCurrentRecord() === r && fire, r);                     
                    }
                }else{
                    var ov = r.get(field);
                    var nv = data[k]
                    if(field == '_id' || field == '_status'||field=='__parameter_parsed__') continue;
                    if(f){
                       nv = this.processData(data,k,f);
                    }
                    if(ov != nv) {
                        if(fire){
                            //由于commit放到上面,这个时候不改变状态,防止重复提交
                            r.set(field,nv, true);
                        }else{
                            r.data[field] = nv;
	                    	if(record)record.data[this.bindname]=this.getConfig();
                        }
                    }
                }
            }
//          r.commit();//挪到上面了,record.set的时候会触发update事件,重新渲染.有可能去判断isNew的状态
        }
    },
    processData: function(data,key,field){
        var v = data[key];
        if(v){
            var dt = field.getPropertity('datatype');
            dt = dt ? dt.toLowerCase() : '';
            switch(dt){
                case 'date':
                    v = $A.parseDate(v);
                    break;
                case 'java.util.date':
                    v = $A.parseDate(v);
                    break;
                case 'java.sql.date':
                    v = $A.parseDate(v);
                    break;
                case 'java.sql.timestamp':
                    v = $A.parseDate(v);
                    v.xtype = 'timestamp';
                    break;
                case 'int':
                    v = parseInt(v);
                    break;
                case 'float':
                    v = parseFloat(v);
                    break;
                case 'boolean':
                    v = v=="true";
                    break;
            }
        }
        //TODO:处理options的displayField
        return this.processValueListField(data,v,field);
    }, 
    processValueListField : function(data,v, field){
        var op = field.getPropertity('options');
        var df = field.getPropertity('displayfield');
        var vf = field.getPropertity('valuefield');
        var mp = field.getPropertity('mapping')
        if(df && vf && op && mp && !v){
            var rf;
            for(var i=0;i<mp.length;i++){
                var map = mp[i];
                if(vf == map.from){
                    rf = map.to;
                    break;
                }
            }
            var rv = data[rf];
            var options = $(op);
            if(options && !Ext.isEmpty(rv)){
                var r = options.find(vf,rv);
                if(r){
                    v = r.get(df);
                }
            }
        }
        return v;
    },
    onSubmitError : function(res){
//      $A.showErrorMessage('错误', res.error.message||res.error.stackTrace,null,400,200);
        this.fireBindDataSetEvent('submitfailed', res);
    },
    onLoadSuccess : function(res, options){
        if(res == null) return;
        if(!res.result.record) res.result.record = [];
        var records = [].concat(res.result.record);
        //var total = res.result.totalCount;
        var total = res.result[this.totalcountfield]
        var datas = [];
        if(records.length > 0){
            for(var i=0,l=records.length;i<l;i++){
                var item = {
                    data:records[i]             
                }
                datas.push(item);
            }
        }else if(records.length == 0){
            this.currentIndex  = 1
        }       
        this.loading = false;
        this.loadData(datas, total, options);
        if(datas.length != 0)
        this.locate(this.currentIndex,true);
        $A.SideBar.enable = $A.slideBarEnable;
        
    },
    onAjaxFailed : function(res,opt){
        this.fireBindDataSetEvent('ajaxfailed',res,opt);
    },
    onLoadError : function(res,opt){
        this.fireBindDataSetEvent('loadfailed', res,opt);
//      $A.showWarningMessage('错误', res.error.message||res.error.stackTrace,null,350,150);
        this.loading = false;
        $A.SideBar.enable = $A.slideBarEnable;
    },
    onFieldChange : function(record,field,type,value) {
        this.fireEvent('fieldchange', this, record, field, type, value)
    },
    onMetaChange : function(record,meta,type,value) {
        this.fireEvent('metachange', this, record, meta, type, value)
    },
    onRecordValid : function(record, name, valid){
        if(valid==false && this.isValid !== false) this.isValid = false;
        this.fireEvent('valid', this, record, name, valid)
    }
});

/**
 * @class Aurora.Record
 * <p>Record是一个数据对象.
 * @constructor
 * @param {Object} data 数据对象. 
 * @param {Array} fields 配置对象. 
 */
$A.Record = function(data, fields){
    /**
     * Record的id. (只读).
     * @type Number
     * @property
     */
    this.id = ++$A.AUTO_ID;
    /**
     * Record的数据 (只读).
     * @type Object
     * @property
     */
    this.data = data;
    /**
     * Record的Fields (只读).
     * @type Object
     * @property
     */
    this.fields = {};
    /**
     * Record的验证信息 (只读).
     * @type Object
     * @property
     */
    this.valid = {};
    /**
     * Record的验证结果 (只读).
     * @type Boolean
     * @property
     */
    this.isValid = true; 
    /**
     * 是否是新数据 (只读).
     * @type Boolean
     * @property
     */
    this.isNew = false;
    /**
     * 是否发生改变 (只读).
     * @type Boolean
     * @property
     */
    this.dirty = false; 
    /**
     * 编辑状态 (只读).
     * @type Boolean
     * @property
     */
    this.editing = false;
    /**
     * 编辑信息对象 (只读).
     * @type Object
     * @property
     */
    this.modified= null;
    /**
     * 是否是已就绪数据 (只读).
     * @type Boolean
     * @property
     */
    this.isReady=true;
    this.meta = new $A.Record.Meta(this);
    if(fields)this.initFields(fields);
};
$A.Record.prototype = {
    commit : function() {
        this.editing = false;
        this.valid = {};
        this.isValid = true;
        this.isNew = false;
        this.dirty = false;
        this.modified = null;
    },
    initFields : function(fields){
        for(var i=0,l=fields.length;i<l;i++){
            var f = new $A.Record.Field(fields[i]);
            f.record = this;
            this.fields[f.name] = f;
        }
    },
    validateRecord : function() {
        this.isValid = true;
        this.valid = {};
        var df = this.ds.fields;
        var rf = this.fields;
        var names = [];
        for(var k in df){
            if(df[k].type !='dataset')
            names.push(k);
        }
        
        for(var k in rf){
            if(names.indexOf(k) == -1){
                if(rf[k].type !='dataset')
                names.push(k);
            }
        }
        for(var i=0,l=names.length;i<l;i++){
            if(this.isValid == true) {
                this.isValid = this.validate(names[i]);
            } else {
                this.validate(names[i]);
            }
        }
        return this.isValid;
    },
    validate : function(name){
        var valid = true;
        var oldValid = this.valid[name];
        var v = this.get(name);
        var field = this.getMeta().getField(name)
        var validator = field.get('validator');
        var vv = v;
        if(v&&v.trim) vv = v.trim();
        if(Ext.isEmpty(vv) && field.get('required') == true){
            this.valid[name] = _lang['dataset.validate.required'];
            valid =  false;
        }
        if(valid == true){
            var isvalid = true;
            if(validator){
                validator = window[validator];
                isvalid = validator.call(window,this, name, v);
                if(isvalid !== true){
                    valid = false;  
                    this.valid[name] = isvalid;
                }
            }
        }
        if(valid==true)delete this.valid[name];
        if((oldValid||this.valid[name])&& oldValid != this.valid[name])this.ds.onRecordValid(this,name,valid);
        return valid;
    },
    setDataSet : function(ds){
        this.ds = ds;
    },
    /**
     * 获取field对象
     * @param {String} name
     * @return {Aurora.Record.Field}
     */
    getField : function(name){
        return this.getMeta().getField(name);
    },
    getMeta : function(){
        return this.meta;
    },
    copy : function(record){
        if(record == this){
            alert('不能copy自身!');
            return;
        }
        if(record.dirty){
            for(var n in record.modified){
                this.set(n,record.get(n))
            }
        }
    },
    /**
     * 设置值.
     * @param {String} name 设定值的名字.
     * @param {Object} value 设定的值.
     * @param {Boolean} notDirty true 不改变record的dirty状态.
     */
    set : function(name, value, notDirty){
        var old = this.data[name];
        if(!(old === value||(Ext.isEmpty(old)&&Ext.isEmpty(value))||(Ext.isDate(old)&&Ext.isDate(value)&&old.getTime()==value.getTime()))){
            if(!notDirty){
                this.dirty = true;
                if(!this.modified){
                    this.modified = {};
                }
                if(typeof this.modified[name] == 'undefined'){
                    this.modified[name] = old;
                }
            }
            this.data[name] = value;
            if(!this.editing && this.ds) {
                this.ds.afterEdit(this, name, value, old);
            }
        }
        this.validate(name)
    },
    /**
     * 设置值.
     * @param {String} name 名字.
     * @return {Object} value 值.
     */
    get : function(name){
        return this.data[name];
    },
    /**
     * 返回record的data对象.
     * 可以通过obj.xx的方式获取数据
     * @return {Object}
     */
    getObject : function(){
        return Ext.apply({},thi.data);
    },
    /**
     * 更新data数据.
     * @param {Object} o
     */
    setObject : function(o){
        for(var key in o){
            this.set(key,o[key]);
        }
    },
    reject : function(silent){
        var m = this.modified;
        for(var n in m){
            if(typeof m[n] != "function"){
                this.data[n] = m[n];
                this.ds.afterReject(this,n,m[n]);
            }
        }
        delete this.modified;
        this.editing = false;
        this.dirty = false;
    },
//    beginEdit : function(){
//        this.editing = true;
//        this.modified = {};
//    },
//    cancelEdit : function(){
//        this.editing = false;
//        delete this.modified;
//    },
//    endEdit : function(){
//        delete this.modified;
//        this.editing = false;
//        if(this.dirty && this.ds){
//            this.ds.afterEdit(this);//name,value怎么处理?
//        }        
//    },
    onFieldChange : function(name, type, value){
        var field = this.getMeta().getField(name);
        this.ds.onFieldChange(this, field, type, value);
    },
    onFieldClear : function(name){
        var field = this.getMeta().getField(name);
        this.ds.onFieldChange(this, field);
    },
    onMetaChange : function(meta, type, value){
        this.ds.onMetaChange(this,meta, type, value);
    },
    onMetaClear : function(meta){
        this.ds.onMetaChange(this,meta);
    }
}
$A.Record.Meta = function(r){
    this.record = r;
    this.pro = {};
}
$A.Record.Meta.prototype = {
    clear : function(){
        this.pro = {};
        this.record.onMetaClear(this);
    },
    getField : function(name){
        if(!name)return null;
        var f = this.record.fields[name];
        var df = this.record.ds.fields[name];
        var rf;
        if(!f){
            if(df){
                f = new $A.Record.Field({name:df.name,type:df.type||'string'});
            }else{
                f = new $A.Record.Field({name:name,type:'string'});//
            }
            f.record = this.record;
            this.record.fields[f.name]=f;
        }
        
        var pro = {};
        if(df) pro = Ext.apply(pro, df.pro);
        pro = Ext.apply(pro, this.pro);
        pro = Ext.apply(pro, f.pro);
        delete pro.name;
        delete pro.type;
        f.snap = pro;
        return f;
    },
    setRequired : function(r){
        var op = this.pro['required'];
        if(op !== r){
            this.pro['required'] = r;
            this.record.onMetaChange(this, 'required', r);
        }
    },
    setReadOnly : function(r){
        var op = this.pro['readonly'];
        if(op !== r){
            this.pro['readonly'] = r;
            this.record.onMetaChange(this,'readonly', r);
        }
    }
}
/**
 * @class Aurora.Record.Field
 * <p>Field是一个配置对象，主要配置指定列的一些附加属性，例如非空，只读，值列表等信息.
 * @constructor
 * @param {Object} data 数据对象. 
 */
$A.Record.Field = function(c){
    this.name = c.name;
    this.type = c.type;
    this.pro = c||{};
    this.record;
};
$A.Record.Field.prototype = {
    /**
     * 清除所有配置信息.
     */
    clear : function(){
        this.pro = {};
        this.record.onFieldClear(this.name);
    },
    setPropertity : function(type,value) {
        var op = this.pro[type];
        if(op !== value){
            this.pro[type] = value;
            this.record.onFieldChange(this.name, type, value);
        }
    },
    /**
     * 获取配置信息
     * @param {String} name 配置名
     * @return {Object} value 配置值
     */
    get : function(name){
        var v = null;
        if(this.snap){
            v = this.snap[name];
        }
        return v;
    },
    getPropertity : function(name){
        return this.pro[name]
    },
    /**
     * 设置当前Field是否必输
     * @param {Boolean} required  是否必输.
     */
    setRequired : function(r){
        this.setPropertity('required',r);
        if(!r)this.record.validate(this.name);
    },
    /**
     * 当前Field是否必输.
     * @return {Boolean} required  是否必输.
     */
    isRequired : function(){
        return this.getPropertity('required');
    },
    /**
     * 设置当前Field是否只读.
     * @param {Boolean} readonly 是否只读
     */
    setReadOnly : function(r){  
        if(r)delete this.record.valid[this.name];
        this.setPropertity('readonly',r);
    },
    /**
     * 当前Field是否只读.
     * @return {Boolean} readonly 是否只读
     */
    isReadOnly : function(){
        return this.getPropertity('readonly');
    },
    /**
     * 设置当前Field的数据集.
     * @param {Object} r 数据集
     */
    setOptions : function(r){
        this.setPropertity('options',r);
    },
    /**
     * 获取当前的数据集.
     * @return {Object} r 数据集
     */
    getOptions : function(){
        return this.getPropertity('options');
    },
    /**
     * 设置当前Field的映射.
     * 例如：<p>
       var mapping = [{from:'name', to: 'code'},{from:'service', to: 'name'}];</p>
       field.setMapping(mapping);
     * @return {Array} mapping 映射列表.
     * 
     */
    setMapping : function(m){
        this.setPropertity('mapping',m);
    },
    /**
     * 获取当前的映射.
     * @return {Array} array 映射集合
     */
    getMapping : function(){
        return this.getPropertity('mapping');
    },
    /**
     * 设置Lov弹出窗口的Title.
     * @param {String} title lov弹出窗口的Tile
     */
    setTitle : function(t){
        this.setPropertity('title',t);
    },
    /**
     * 设置Lov弹出窗口的宽度.
     * @param {Number} width lov弹出窗口的Width
     */
    setLovWidth : function(w){
        this.setPropertity('lovwidth',w);
    },
    /**
     * 设置Lov弹出窗口的高度.
     * @param {Number} height lov弹出窗口的Height
     */
    setLovHeight : function(h){
        this.setPropertity('lovheight',h);
    },
    /**
     * 设置Lov弹出窗口中grid的高度.
     * 配置这个主要是由于查询条件可能存在多个，导致查询的form过高.
     * @param {Number} height lov弹出窗口的grid组件的Height
     */
    setLovGridHeight : function(gh){
        this.setPropertity("lovgridheight",gh)
    },
    /**
     * 设置Lov的Model对象.
     * Lov的配置可以通过三种方式.(1)model (2)service (3)url.
     * @param {String} model lov配置的model.
     */
    setLovModel : function(m){
        this.setPropertity("lovmodel",m) 
    },
    /**
     * 设置Lov的Service对象.
     * Lov的配置可以通过三种方式.(1)model (2)service (3)url.
     * @param {String} service lov配置的service.
     */
    setLovService : function(m){
        this.setPropertity("lovservice",m) 
    },
    /**
     * 设置Lov的Url地址.
     * Lov的配置可以通过三种方式.(1)model (2)service (3)url.
     * 通过url打开的lov，可以不用调用setLovGridHeight
     * @param {String} url lov打开的url.
     */
    setLovUrl : function(m){
        this.setPropertity("lovurl",m) 
    },
    /**
     * 设置Lov的查询参数
     * @param {String} name
     * @param {Object} value
     */
    setLovPara : function(name,value){
        var p = this.getPropertity('lovpara');
        if(!p){
            p = {};
            this.setPropertity("lovpara",p) 
        }
        if(value==null){
            delete p[name]
        }else{
            p[name] = value;
        }
    }
    
}
/**
 * @class Aurora.Component
 * @extends Ext.util.Observable
 * <p>所有组件对象的父类.
 * <p>所有的子类将自动继承Component的所有属性和方法.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Component = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {
        $A.Component.superclass.constructor.call(this);
        this.id = config.id || Ext.id();
        $A.CmpManager.put(this.id,this)
		this.initConfig=config;
		this.isHidden = false;
		this.isFireEvent = false;
		this.initComponent(config);
        this.initEvents();
    },
    initComponent : function(config){ 
		config = config || {};
        Ext.apply(this, config);
        this.wrap = Ext.get(this.id);
        if(this.listeners){
            this.on(this.listeners);
        }
    },
    processListener: function(ou){
    	this.processMouseOverOut(ou)
        if(this.marginwidth||this.marginheight) {
//        	this.windowResizeListener();//TODO:以后修改服务端component,去掉自身尺寸的判断
            Ext.EventManager[ou](window, "resize", this.windowResizeListener,this);
        }
    },
    processMouseOverOut : function(ou){
        if(this.wrap){
            this.wrap[ou]("mouseover", this.onMouseOver, this);
            this.wrap[ou]("mouseout", this.onMouseOut, this);
        }
    },
    initEvents : function(){
    	this.addEvents(
        /**
         * @event focus
         * 获取焦点事件
         * @param {Component} this 当前组件.
         */
    	'focus',
        /**
         * @event blur
         * 失去焦点事件
         * @param {Component} this 当前组件.
         */
    	'blur',
    	/**
         * @event change
         * 组件值改变事件.
         * @param {Component} this 当前组件.
         * @param {Object} value 新的值.
         * @param {Object} oldValue 旧的值.
         */
    	'change',
    	/**
         * @event valid
         * 组件验证事件.
         * @param {Component} this 当前组件.
         * @param {Aurora.Record} record record对象.
         * @param {String} name 对象绑定的Name.
         * @param {Boolean} isValid 验证是否通过.
         */
    	'valid',
    	/**
         * @event mouseover
         * 鼠标经过组件事件.
         * @param {Component} this 当前组件.
         * @param {EventObject} e 鼠标事件对象.
         */
    	'mouseover',
    	/**
         * @event mouseout
         * 鼠标离开组件事件.
         * @param {Component} this 当前组件.
         * @param {EventObject} e 鼠标事件对象.
         */
    	'mouseout');
    	this.processListener('on');
    },
    windowResizeListener : function(){
    	var ht,wd;
        if(this.marginheight){
            ht = Aurora.getViewportHeight();
            this.setHeight(ht-this.marginheight);           
        }
        if(this.marginwidth){
            wd = Aurora.getViewportWidth();
            this.setWidth(wd-this.marginwidth);
        }
    },
    isEventFromComponent:function(el){
    	return this.wrap.contains(el)||this.wrap.dom === (el.dom?el.dom:el);
    },
    move: function(x,y){
		this.wrap.setX(x);
		this.wrap.setY(y);
	},
	getBindName: function(){
		return this.binder ? this.binder.name : null;
	},
	getBindDataSet: function(){
		return this.binder ? this.binder.ds : null;
	},
	/**
     * 将组件绑定到某个DataSet的某个Field上.
     * @param {String/Aurora.DataSet} dataSet 绑定的DataSet. 可以是具体某个DataSet对象，也可以是某个DataSet的id.
     * @param {String} name Field的name. 
     */
    bind : function(ds, name){
    	this.clearBind();
    	if(typeof(ds) == 'string'){
    		ds = $(ds);
    	}
    	if(!ds)return;
    	this.binder = {
    		ds: ds,
    		name:name
    	}
    	this.record = ds.getCurrentRecord();
    	var field =  ds.fields[this.binder.name];
    	if(field) {
			var config={};
			Ext.apply(config,this.initConfig);
			Ext.apply(config, field.pro);
			delete config.name;
			delete config.type;
			this.initComponent(config);
			
    	}
    	ds.on('metachange', this.onRefresh, this);
    	ds.on('valid', this.onValid, this);
    	ds.on('remove', this.onRemove, this);
    	ds.on('clear', this.onClear, this);
    	ds.on('update', this.onUpdate, this);
    	ds.on('reject', this.onUpdate, this);
    	ds.on('fieldchange', this.onFieldChange, this);
    	ds.on('indexchange', this.onRefresh, this);
    	this.onRefresh(ds)
    },
    /**
     * 清除组件的绑定信息.
     * <p>删除所有绑定的事件信息.
     */
    clearBind : function(){
    	if(this.binder) {
    		var bds = this.binder.ds;
    		bds.un('metachange', this.onRefresh, this);
	    	bds.un('valid', this.onValid, this);
	    	bds.un('remove', this.onRemove, this);
	    	bds.un('clear', this.onClear, this);
	    	bds.un('update', this.onUpdate, this);
	    	bds.un('reject', this.onUpdate, this);
	    	bds.un('fieldchange', this.onFieldChange, this);
	    	bds.un('indexchange', this.onRefresh, this);
    	} 
		this.binder = null; 
		this.record = null;
    },
    /**
     * <p>销毁组件对象.</p>
     * <p>1.删除所有绑定的事件.</p>
     * <p>2.从对象管理器中删除注册信息.</p>
     * <p>3.删除dom节点.</p>
     */
    destroy : function(){
    	this.processListener('un');
    	$A.CmpManager.remove(this.id);
    	this.clearBind();
    	delete this.wrap;
    },
    onMouseOver : function(e){
    	this.fireEvent('mouseover', this, e);
    },
    onMouseOut : function(e){
    	this.fireEvent('mouseout', this, e);
    },
    onRemove : function(ds, record){
    	if(this.binder.ds == ds && this.record == record){
    		this.clearValue();
    	}
    },
    onCreate : function(ds, record){
    	this.clearInvalid();
    	this.record = ds.getCurrentRecord();
		this.setValue('',true);	
    },
    onRefresh : function(ds){
    	if(this.isFireEvent == true || this.isHidden == true) return;
    	this.clearInvalid();
		this.render(ds.getCurrentRecord());
    },
    render : function(record){
    	this.record = record;
    	if(this.record) {
			var value = this.record.get(this.binder.name);
			var field = this.record.getMeta().getField(this.binder.name);		
			var config={};
			Ext.apply(config,this.initConfig);
			Ext.apply(config, field.snap);
			this.initComponent(config);
			if(this.record.valid[this.binder.name]){
				this.markInvalid();
			}
			//TODO:和lov的设值有问题
//			if(this.value == value) return;
			if(!Ext.isEmpty(value,true)) {
                this.setValue(value,true);
			}else{
                this.clearValue();
			}
		} else {
			this.setValue('',true);
		}
    },
    onValid : function(ds, record, name, valid){
    	if(this.binder.ds == ds && this.binder.name == name && this.record == record){
	    	if(valid){
	    		this.fireEvent('valid', this, this.record, this.binder.name, true)
    			this.clearInvalid();
	    	}else{
	    		this.fireEvent('valid', this, this.record, this.binder.name, false);
	    		this.markInvalid();
	    	}
    	}    	
    },
    onUpdate : function(ds, record, name, value){
    	if(this.binder.ds == ds && this.record == record && this.binder.name == name && this.getValue() !== value){
	    	this.setValue(value, true);
    	}
    },
    onFieldChange : function(ds, record, field){
    	if(this.binder.ds == ds && this.record == record && this.binder.name == field.name){
	    	this.onRefresh(ds);   	
    	}
    },
    onClear : function(ds){
    	this.clearValue();    
    },
    /**
     * 设置当前的值.
     * @param {Object} value 值对象
     * @param {Boolean} silent 是否更新到dataSet中
     */
    setValue : function(v, silent){
    	var ov = this.value;
    	this.value = v;
    	if(silent === true)return;
    	if(this.binder){
    		this.record = this.binder.ds.getCurrentRecord();
    		if(this.record == null){
                this.record = this.binder.ds.create({},false);                
            }
            this.record.set(this.binder.name,v);
            if(Ext.isEmpty(v,true)) delete this.record.data[this.binder.name];
    	}
    	//if(ov!=v){
    	if(!(ov === v||(Ext.isEmpty(ov)&&Ext.isEmpty(v)))){
            this.fireEvent('change', this, v, ov);
    	}
    },
    /**
     * 返回当前值
     * @return {Object} value 返回值.
     */
    getValue : function(){
        var v= this.value;
        v=(v === null || v === undefined ? '' : v);
        return v;
    },
    setWidth: function(w){
    	if(this.width == w) return;
    	this.width = w;
    	this.wrap.setWidth(w);
    },
    setHeight: function(h){
    	if(this.height == h) return;
    	this.height = h;
    	this.wrap.setHeight(h);
    },
    clearInvalid : function(){},
    markInvalid : function(){},
    clearValue : function(){},
    initMeta : function(){},
    setDefault : function(){},
    setRequired : function(){},
    onDataChange : function(){},
    setWidth : function(w){
    	this.wrap.setStyle('width',w+'px');
    },
    setHeight : function(h){
    	this.wrap.setStyle('height',h+'px');
    }
});
/**
 * @class Aurora.Field
 * @extends Aurora.Component
 * <p>带有input标记的输入类的组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Field = Ext.extend($A.Component,{	
	validators: [],
	requiredCss:'item-notBlank',
	focusCss:'item-focus',
	readOnlyCss:'item-readOnly',
	emptyTextCss:'item-emptyText',
	invalidCss:'item-invalid',
	constructor: function(config) {
		config.required = config.required || false;
		config.readonly = config.readonly || false;
        $A.Field.superclass.constructor.call(this, config);
    },
    initElements : function(){
    	this.el = this.wrap.child('input[atype=field.input]'); 
    },
    initComponent : function(config){
    	$A.Field.superclass.initComponent.call(this, config);
    	this.initElements();
    	this.originalValue = this.getValue();
    	this.applyEmptyText();
    	this.initStatus();
    	if(this.hidden == true){
    		this.setVisible(false)
    	}
    },
    processListener: function(ou){
    	$A.Field.superclass.processListener.call(this, ou);
//    	this.el[ou](Ext.isIE || Ext.isSafari3 ? "keydown" : "keypress", this.fireKey,  this);
    	this.el[ou]("focus", this.onFocus,  this);
    	this.el[ou]("blur", this.onBlur,  this);
    	this.el[ou]("change", this.onChange, this);
    	this.el[ou]("keyup", this.onKeyUp, this);
        this.el[ou]("keydown", this.onKeyDown, this);
        this.el[ou]("keypress", this.onKeyPress, this);
        this.el[ou]("mouseup", this.onMouseUp, this);
//        this.el[ou]("mouseover", this.onMouseOver, this);
//        this.el[ou]("mouseout", this.onMouseOut, this);
    },
    processMouseOverOut : function(ou){
        this.el[ou]("mouseover", this.onMouseOver, this);
        this.el[ou]("mouseout", this.onMouseOut, this);
    },
    initEvents : function(){
    	$A.Field.superclass.initEvents.call(this);
        this.addEvents(
        /**
         * @event keydown
         * 键盘按下事件.
         * @param {Aurora.Field} field field对象.
         * @param {EventObject} e 键盘事件对象.
         */
        'keydown',
        /**
         * @event keyup
         * 键盘抬起事件.
         * @param {Aurora.Field} field field对象.
         * @param {EventObject} e 键盘事件对象.
         */
        'keyup',
        /**
         * @event keypress
         * 键盘敲击事件.
         * @param {Aurora.Field} field field对象.
         * @param {EventObject} e 键盘事件对象.
         */
        'keypress',
        /**
         * @event enterdown
         * 回车键事件.
         * @param {Aurora.Field} field field对象.
         * @param {EventObject} e 键盘事件对象.
         */
        'enterdown');
    },
    destroy : function(){
    	$A.Field.superclass.destroy.call(this);
    	delete this.el;
    },
	setWidth: function(w){
		this.wrap.setStyle("width",(w+3)+"px");
		this.el.setStyle("width",w+"px");
	},
	setHeight: function(h){
		this.wrap.setStyle("height",h+"px");
		this.el.setStyle("height",(h-2)+"px");
	},
	setVisible: function(v){
		if(v==true)
			this.wrap.show();
		else
			this.wrap.hide();
	},
    initStatus : function(){
    	this.clearInvalid();
    	this.initRequired(this.required);
    	this.initReadOnly(this.readonly);
    	this.initEditable(this.editable);
    	this.initMaxLength(this.maxlength);
    },
//    onMouseOver : function(e){
//    	$A.ToolTip.show(this.id, "测试");
//    },
//    onMouseOut : function(e){
//    	$A.ToolTip.hide();
//    },
    onChange : function(e){},
    onKeyUp : function(e){
        this.fireEvent('keyup', this, e);
    },
    onKeyDown : function(e){
        this.fireEvent('keydown', this, e);        
        var keyCode = e.keyCode;
        if(this.isEditor==true && keyCode == 9) e.stopEvent();
        if(keyCode == 13 || keyCode == 27) {//13:enter  27:esc
        	this.blur();//为了获取到新的值
        	if(keyCode == 13) {
        		var sf = this;
        		setTimeout(function(){
        			sf.fireEvent('enterdown', sf, e)
        		},5);
        	}
        }
    },
    onKeyPress : function(e){
        this.fireEvent('keypress', this, e);
    },
//    fireKey : function(e){
//      this.fireEvent("keydown", this, e);
//    },
    onFocus : function(e){
        //(Ext.isGecko||Ext.isGecko2||Ext.isGecko3) ? this.select() : this.select.defer(10,this);
    	this.select();
    	if(this.readonly) return;
        if(!this.hasFocus){
            this.hasFocus = true;
            this.startValue = this.getValue();
            if(this.emptytext){
	            if(this.el.dom.value == this.emptytext){
	                this.setRawValue('');
	            }
	            this.wrap.removeClass(this.emptyTextCss);
	        }
	        this.wrap.addClass(this.focusCss);
            this.fireEvent("focus", this);
        }
    },
    onMouseUp : function(e){
    	if(this.isSelect)
    		e.stopEvent();
    	this.isSelect = false;
    },
    processValue : function(v){
    	return v;
    },
    onBlur : function(e){
    	if(this.readonly) return;
    	if(this.hasFocus){
	        this.hasFocus = false;
	        var rv = this.getRawValue();
           	rv = this.processMaxLength(rv);
	        rv = this.processValue(rv);
//	        if(String(rv) !== String(this.startValue)){
//	            this.fireEvent('change', this, rv, this.startValue);
//	        } 
            
	        this.setValue(rv);
	        this.wrap.removeClass(this.focusCss);
	        this.fireEvent("blur", this);
    	}
    },
    processMaxLength : function(rv){
    	var sb = [];
        if(this.isOverMaxLength(rv)){
            for (i = 0,k=0; i < rv.length;i++) {
                var cr = rv.charAt(i);
                var cl = cr.match(/[^\x00-\xff]/g);
                if (cl !=null && cl.length>0) {
                    k=k+$A.defaultChineseLength;
                } else {
                    k=k+1
                }
                if(k<=this.maxlength) {
                    sb[sb.length] = cr
                }else{
                    break;
                }
            }
            return sb.join('');
        }
        return rv;
    },
    setValue : function(v, silent){
    	if(this.emptytext && this.el && v !== undefined && v !== null && v !== ''){
            this.wrap.removeClass(this.emptyTextCss);
        }
        this.setRawValue(this.formatValue((v === null || v === undefined ? '' : v)));
        this.applyEmptyText();
    	$A.Field.superclass.setValue.call(this,v, silent);
    },
    formatValue : function(v){
        var rder = null;
        if(this.renderer) rder = $A.getRenderer(this.renderer);
        return (rder!=null) ? rder.call(window,v) : v;
    },
    getRawValue : function(){
        var v = this.el.getValue();
        if(v === this.emptytext || v === undefined){
            v = '';
        }
        return v;
    },   
//    getValue : function(){
//    	var v= this.value;
//		v=(v === null || v === undefined ? '' : v);
//		return v;
//    },
    initRequired : function(required){
    	if(this.crrentRequired == required)return;
		this.clearInvalid();    	
    	this.crrentRequired = required;
    	if(required){
    		this.wrap.addClass(this.requiredCss);
    	}else{
    		this.wrap.removeClass(this.requiredCss);
    	}
    },
    initEditable : function(editable){
    	this.el.dom.readOnly = this.readonly? true :(editable === false);
    },
    initReadOnly : function(readonly){
    	if(this.currentReadOnly == readonly)return;
    	this.currentReadOnly = readonly;
    	this.el.dom.readOnly = readonly;
    	if(readonly){
    		this.wrap.addClass(this.readOnlyCss);
    	}else{
    		this.wrap.removeClass(this.readOnlyCss);
    	}
    },
    isOverMaxLength : function(str){
        if(!this.maxlength) return false;
        var c = 0;
        for (i = 0; i < str.length; i++) {
            var cr = str.charAt(i);
            var cl = cr.match(/[^\x00-\xff]/g);
//            var st = escape(str.charAt(i));
            if (cl !=null &&cl.length >0) {
                c=c+$A.defaultChineseLength;
            } else {
                c=c+1;
            }
        }
        return c > this.maxlength;
    },
    initMaxLength : function(maxlength){
    	if(maxlength)
    	this.el.dom.maxLength=maxlength;
    },
    applyEmptyText : function(){
        if(this.emptytext && this.getRawValue().length < 1){
            this.setRawValue(this.emptytext);
            this.wrap.addClass(this.emptyTextCss);
        }
    },
//    validate : function(){
//        if(this.readonly || this.validateValue(this.getValue())){
//            this.clearInvalid();
//            return true;
//        }
//        return false;
//    },
    clearInvalid : function(){
    	this.invalidMsg = null;
    	this.wrap.removeClass(this.invalidCss);
//    	this.fireEvent('valid', this);
    },
    markInvalid : function(msg){
    	this.invalidMsg = msg;
    	this.wrap.addClass(this.invalidCss);
    },
//    validateValue : function(value){    
//    	if(value.length < 1 || value === this.emptyText){ // if it's blank
//        	if(!this.required){
//                this.clearInvalid();
//                return true;
//        	}else{
//                this.markInvalid('字段费控');//TODO:测试
//        		return false;
//        	}
//        }
//    	Ext.each(this.validators.each, function(validator){
//    		var vr = validator.validate(value)
//    		if(vr !== true){
//    			//TODO:
//    			return false;
//    		}    		
//    	})
//        return true;
//    },
    select : function(start, end){
    	var v = this.getRawValue();
        if(v.length > 0){
            start = start === undefined ? 0 : start;
            end = end === undefined ? v.length : end;
            var d = this.el.dom;
            if(d.setSelectionRange){  
                d.setSelectionRange(start, end);
            }else if(d.createTextRange){
                var range = d.createTextRange();
                range.moveStart("character", start);
                range.moveEnd("character", end-v.length);
                range.select();
            }
        }
        this.isSelect = true;
    },
    setRawValue : function(v){
        if(this.el.dom.value === (v === null || v === undefined ? '' : v)) return;
        return this.el.dom.value = (v === null || v === undefined ? '' : v);
    },
    reset : function(){
    	this.setValue(this.originalValue);
        this.clearInvalid();
        this.applyEmptyText();
    },
    focus : function(){
    	if(this.readonly) return;
    	this.el.dom.focus();
    	this.fireEvent('focus', this);
    },
    blur : function(){
    	if(this.readonly) return;
    	this.el.blur();
    	this.fireEvent('blur', this);
    },
    clearValue : function(){
    	this.setValue('', true);
    	this.clearInvalid();
        this.applyEmptyText();
    }
})
/**
 * @class Aurora.Box
 * @extends Aurora.Component
 * <p>Box组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Box = Ext.extend($A.Component,{
	constructor: function(config) {
        this.errors = [];
        $A.Box.superclass.constructor.call(this,config);
    },
//    initComponent : function(config){ 
//		config = config || {};
//        Ext.apply(this, config); 
        //TODO:所有的组件?
//        for(var i=0;i<this.cmps.length;i++){
//    		var cmp = $(this.cmps[i]);
//    		if(cmp){
//	    		cmp.on('valid', this.onValid, this)
//	    		cmp.on('invalid', this.onInvalid,this)
//    		}
//    	}
//    },
    initEvents : function(){
//    	this.addEvents('focus','blur','change','invalid','valid');    	
    },
    onValid : function(cmp, record, name, isvalid){
    	if(isvalid){
    	   this.clearError(cmp.id);
    	}else{
            var error = record.errors[name];
            if(error){
                this.showError(cmp.id,error.message)
            }    		
    	}
    },
    showError : function(id, msg){
    	Ext.fly(id+'_vmsg').update(msg)
    },
    clearError : function(id){
    	Ext.fly(id+'_vmsg').update('')
    },
    clearAllError : function(){
    	for(var i=0;i<this.errors.length;i++){
    		this.clearError(this.errors[i])
    	}
    }
});
/**
 * @class Aurora.ImageCode
 * @extends Aurora.Component
 * <p>图片验证码组件.
 * @author njq.niu@hand-china.com
 * @constructor 
 */
$A.ImageCode = Ext.extend($A.Component,{
    processListener: function(ou){
        $A.ImageCode.superclass.processListener.call(this,ou);
        this.wrap[ou]("click", this.onClick,  this);
    },
    onClick : function(){
        if(this.enable == true)
    	this.refresh();
    },
    setEnable : function(isEnable){
        if(isEnable == true){
            this.enable = true;
            this.refresh();
        }else{
            this.enable = false;
            this.wrap.dom.src = "";
        }
    },
    /**
     * 重新加载验证码
     * 
     */
    refresh : function(){
        this.wrap.dom.src = "imagecode?r="+Math.random();
    }
});
/**
 * @class Aurora.Label
 * @extends Aurora.Component
 * <p>Label组件.
 * @author njq.niu@hand-china.com
 * @constructor 
 */
$A.Label = Ext.extend($A.Component,{
    onUpdate : function(ds, record, name, value){
    	if(this.binder.ds == ds && this.binder.name == name){
	    	this.updateLabel(record,name,value);
    	}
    },
    /**
     * 绘制Label
     * @param {Aurora.Record} record record对象
     */
    render : function(record){
    	this.record = record;
    	if(this.record) {
			var value = this.record.get(this.binder.name);
			this.updateLabel(this.record,this.binder.name,value);
    	}
    },
    updateLabel: function(record,name,value){
        var rder = $A.getRenderer(this.renderer);
	    if(rder!=null){
    		value = rder.call(window,value,record, name);
	    }
	    this.wrap.update(value);
    }
});
/**
 * @class Aurora.Link
 * @extends Aurora.Component
 * <p>Link组件.
 * @author njq.niu@hand-china.com
 * @constructor 
 */
$A.Link = Ext.extend($A.Component,{
    params: {},
    constructor: function(config) {
        this.url = config.url || "";
        $A.Link.superclass.constructor.call(this, config);
    },
    processListener: function(ou){
    },
    reset : function(){
        this.params = {};
    },
    /**
     * 增加参数值
     * @param {String} name 参数名
     * @param {Object} value 参数值
     */
    set : function(name,value){
        this.params[name]=value;
    },
    /**
     * 返回参数值
     * 
     * @param {String} name 参数名
     * @return {Object} obj 返回值
     */
    get : function(name){
        return this.params[name];
    },
    /**
     * 返回生成的URL
     * 
     * @return {String} url  
     */
    getUrl : function(){
        var url;
        var pr = Ext.urlEncode(this.params);
        if(Ext.isEmpty(pr)){
            url = this.url;
        }else{
            url = this.url +(this.url.indexOf('?') == -1?'?':'&') + Ext.urlEncode(this.params);
        } 
        return url;
    }
});
/**
 * @class Aurora.Button
 * @extends Aurora.Component
 * <p>按钮组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Button = Ext.extend($A.Component,{
	disableCss:'item-btn-disabled',
	overCss:'item-btn-over',
	pressCss:'item-btn-pressed',
	disabled:false,
	constructor: function(config) {
        $A.Button.superclass.constructor.call(this, config);
    },
	initComponent : function(config){
    	$A.Button.superclass.initComponent.call(this, config);
    	this.el = this.wrap.child('button[atype=btn]');
    	if(this.hidden == true)this.setVisible(false)
    	if(this.disabled == true)this.disable();
    },
    processListener: function(ou){
    	$A.Button.superclass.processListener.call(this,ou);
    	this.wrap[ou]("click", this.onClick,  this);
        this.wrap[ou]("mousedown", this.onMouseDown,  this);
    },
    initEvents : function(){
    	$A.Button.superclass.initEvents.call(this);
    	this.addEvents(
    	/**
         * @event click
         * 鼠标点击事件.
         * @param {Aurora.Button} button 按钮对象.
         * @param {EventObject} e 键盘事件对象.
         */
    	'click');
    },    
    destroy : function(){
		$A.Button.superclass.destroy.call(this);
    	delete this.el;
    },
    /**
     * 设置按钮是否可见.
     * @param {Boolean} visiable  是否可见.
     */
    setVisible: function(v){
		if(v==true)
			this.wrap.show();
		else
			this.wrap.hide();
	},
//    destroy : function(){
//    	$A.Button.superclass.destroy.call(this);
//    	this.el.un("click", this.onClick,  this);
//    	delete this.el;
//    },
	/**
	 * 获取焦点
	 */
	focus: function(){
		if(this.disabled)return;
		this.el.dom.focus();
	},
	/**
	 * 失去焦点
	 */	
	blur : function(){
    	if(this.disabled) return;
    	this.el.dom.blur();
    },
    /**
     * 设置不可用状态
     */
    disable: function(){
    	this.disabled = true;
    	this.wrap.addClass(this.disableCss);
    	this.el.dom.disabled = true;
    },
    /**
     * 设置可用状态
     */
    enable: function(){
    	this.disabled = false;
    	this.wrap.removeClass(this.disableCss);
    	this.el.dom.disabled = false;
    },
    onMouseDown: function(e){
    	if(!this.disabled){
        	this.wrap.addClass(this.pressCss);
        	Ext.get(document.documentElement).on("mouseup", this.onMouseUp, this);
    	}
    },
    onMouseUp: function(e){
    	if(!this.disabled){
        	Ext.get(document.documentElement).un("mouseup", this.onMouseUp, this);
        	this.wrap.removeClass(this.pressCss);
    	}
    },
    onClick: function(e){
    	if(!this.disabled){
        	e.stopEvent();
        	this.fireEvent("click", this, e);
    	}
    },
    onMouseOver: function(e){
    	if(!this.disabled)
    	this.wrap.addClass(this.overCss);
        $A.Button.superclass.onMouseOver.call(this,e);
    },
    onMouseOut: function(e){
    	if(!this.disabled)
    	this.wrap.removeClass(this.overCss);
        $A.Button.superclass.onMouseOut.call(this,e);
    }
});
$A.Button.getTemplate = function(id,text,width){
    return '<TABLE class="item-btn " id="'+id+'" style="WIDTH: '+(width||60)+'px" cellSpacing="0"><TBODY><TR><TD class="item-btn-tl"><I></I></TD><TD class="item-btn-tc"></TD><TD class="item-btn-tr"><I></I></TD></TR><TR><TD class="item-btn-ml"><I></I></TD><TD class="item-btn-mc"><BUTTON hideFocus style="HEIGHT: 17px" atype="btn">'+text+'</BUTTON></TD><TD class="item-btn-mr"><I></I></TD></TR><TR><TD class="item-btn-bl"><I></I></TD><TD class="item-btn-bc"></TD><TD class="item-btn-br"><I></I></TD></TR></TBODY></TABLE><script>new Aurora.Button({"id":"'+id+'"});</script>';
}
/**
 * @class Aurora.CheckBox
 * @extends Aurora.Component
 * <p>可选组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.CheckBox = Ext.extend($A.Component,{
	checkedCss:'item-ckb-c',
	uncheckedCss:'item-ckb-u',
	readonyCheckedCss:'item-ckb-readonly-c',
	readonlyUncheckedCss:'item-ckb-readonly-u',
	constructor: function(config){
		config.checked = config.checked || false;
		config.readonly = config.readonly || false;
		$A.CheckBox.superclass.constructor.call(this,config);
	},
	initComponent:function(config){
		this.checkedvalue = 'Y';
		this.uncheckedvalue = 'N';
		$A.CheckBox.superclass.initComponent.call(this, config);
		this.wrap=Ext.get(this.id);
		this.el=this.wrap.child('div[atype=checkbox]');
	},
	processListener: function(ou){
    	this.wrap[ou]('click',this.onClick,this);
    	this.el[ou]('keydown',this.onKeyDown,this);
    	this.el[ou]('focus',this.onFocus,this)
    	this.el[ou]('blur',this.onBlur,this)
    },
	initEvents:function(){
		$A.CheckBox.superclass.initEvents.call(this);  	
		this.addEvents(
		/**
         * @event click
         * 鼠标点击事件.
         * @param {Aurora.CheckBox} checkBox 可选组件.
         * @param {Boolean} checked 选择状态.
         */
		'click');    
	},
	destroy : function(){
    	$A.CheckBox.superclass.destroy.call(this);
    	delete this.el;
    },
    onKeyDown : function(e){
    	var keyCode = e.keyCode;
    	if(keyCode == 32){
    		this.onClick.call(this,e)
    	}
    },
	onClick: function(event){
		if(!this.readonly){
			this.checked = this.checked ? false : true;	
			this.setValue(this.checked);
			this.fireEvent('click', this, this.checked);
			this.focus();
		}
	},
	focus : function(){
		this.el.focus();
	},
	onFocus : function(){
		this.el.setStyle('outline','1px dotted blue')
		this.fireEvent('focus',this);
	},
	onBlur : function(){
		this.el.setStyle('outline','none')
		this.fireEvent('blur',this);
	},
	setValue:function(v, silent){
		if(typeof(v)==='boolean'){
			this.checked=v?true:false;			
		}else{
			this.checked = (''+v == ''+this.checkedvalue)
//			this.checked = v === this.checkedvalue ? true : false;
		}
		this.initStatus();
		var value = this.checked==true ? this.checkedvalue : this.uncheckedvalue;		
		$A.CheckBox.superclass.setValue.call(this,value, silent);
	},
	getValue : function(){
    	var v= this.value;
		v=(v === null || v === undefined ? '' : v);
		return v;
    },
//	setReadOnly:function(b){
//		if(typeof(b)==='boolean'){
//			this.readonly=b?true:false;	
//			this.initStatus();		
//		}		
//	},
	initStatus:function(){
		this.el.removeClass(this.checkedCss);
		this.el.removeClass(this.uncheckedCss);
		this.el.removeClass(this.readonyCheckedCss);
		this.el.removeClass(this.readonlyUncheckedCss);
		if (this.readonly) {				
			this.el.addClass(this.checked ? this.readonyCheckedCss : this.readonlyUncheckedCss);			
		}else{
			this.el.addClass(this.checked ? this.checkedCss : this.uncheckedCss);
		}		
	}			
});
/**
 * @class Aurora.Radio
 * @extends Aurora.Component
 * <p>单选框组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Radio = Ext.extend($A.Component, {
	ccs:'item-radio-img-c',
	ucs:'item-radio-img-u',
	rcc:'item-radio-img-readonly-c',
	ruc:'item-radio-img-readonly-u',
//	optionCss:'item-radio-option',
	imgCss:'item-radio-img',
	valueField:'value',
	constructor: function(config){
		config.checked = config.checked || false;
		config.readonly = config.readonly || false;
		$A.Radio.superclass.constructor.call(this,config);		
	},
	initComponent:function(config){
		$A.Radio.superclass.initComponent.call(this, config);
		this.wrap=Ext.get(this.id);	
		this.nodes = Ext.DomQuery.select('.item-radio-option',this.wrap.dom);
		this.initStatus();
//		this.select(this.selectIndex);
	},	
	processListener: function(ou){
        $A.Radio.superclass.processListener.call(this, ou);
    	this.wrap[ou]('click',this.onClick,this);
    	this.wrap[ou]("keydown", this.onKeyDown, this);
    },
    focus : function(){
    	this.wrap.focus();
    },
    onKeyDown:function(e){
        this.fireEvent('keydown', this, e);
        var keyCode = e.keyCode;
        if(keyCode == 13)  {
            var sf = this;
            setTimeout(function(){
                sf.fireEvent('enterdown', sf, e)
            },5);
        }else if(keyCode==40){
            var vi = this.getValueItem();
            var i = this.options.indexOf(vi);
            if(i+1 < this.options.length){
                var v = this.options[i+1][this.valueField];
                this.setValue(v)
            }
        }else if(keyCode==38){
            var vi = this.getValueItem();
            var i = this.options.indexOf(vi);
            if(i-1 >=0){
                var v = this.options[i-1][this.valueField];
                this.setValue(v)
            }
        }
    },
	initEvents:function(){
		$A.Radio.superclass.initEvents.call(this); 	
		this.addEvents(
		/**
         * @event click
         * 点击事件.
         * @param {Aurora.Tree} Radio对象
         * @param {Object} 当前选中的值
         */
		'click',
		/**
         * @event keydown
         * 键盘事件.
         * @param {Aurora.Tree} Radio对象
         * @param {Event} 键盘事件对象
         */
		'keydown',
		/**
         * @event enterdown
         * 回车事件.
         * @param {Aurora.Tree} Radio对象
         * @param {Event} 键盘事件对象
         */
		'enterdown');    
	},
	setValue:function(value,silent){
		if(value=='')return;
		$A.Radio.superclass.setValue.call(this,value, silent);
		this.initStatus();
		this.focus();
	},
	getItem: function(){
		var item = this.getValueItem();
		if(item!=null){
            item = new $A.Record(item);
		}
		return item;
	},
	getValueItem: function(){
	   var v = this.getValue();
	   var l = this.options.length;
	   var r = null;
	   for(var i=0;i<l;i++){
	       var o = this.options[i];
	       if(o[this.valueField]==v){
	           r = o;
	           break;
	       }
	   }	   
	   return r;
	},
	select : function(i){
		var v = this.getItemValue(i);
		if(v){
			this.setValue(v);
		}
	},
	getValue : function(){
    	var v = this.value;
		v=(v === null || v === undefined ? '' : v);
		return v;
    },
//	setReadOnly:function(b){
//		if(typeof(b)==='boolean'){
//			this.readonly=b?true:false;	
//			this.initStatus();		
//		}
//	},
	initStatus:function(){
		var l=this.nodes.length;
		for (var i = 0; i < l; i++) {
			var node=Ext.fly(this.nodes[i]).child('.'+this.imgCss);		
			node.removeClass(this.ccs);
			node.removeClass(this.ucs);
			node.removeClass(this.rcc);
			node.removeClass(this.ruc);
			var value = Ext.fly(this.nodes[i]).getAttributeNS("","itemvalue");
			//if((i==0 && !this.value) || value === this.value){
			if(value === this.value){
				this.readonly?node.addClass(this.rcc):node.addClass(this.ccs);				
			}else{
				this.readonly?node.addClass(this.ruc):node.addClass(this.ucs);		
			}
		}
	},
	getItemValue:function(i){
	   var node = Ext.fly(this.nodes[i]);
	   if(!node)return null;
	   var v = node.getAttributeNS("","itemvalue");
	   return v;
	},
	onClick:function(e) {
		if(!this.readonly){
			var l=this.nodes.length;
			for (var i = 0; i < l; i++) {
				var node = Ext.fly(this.nodes[i]);
				if(node.contains(e.target)){
					var v = node.getAttributeNS("","itemvalue");
					this.setValue(v);
					this.fireEvent('click',this,v);
					break;
				}
			}
			
		}		
	}	
});
/**
 * @class Aurora.TextField
 * @extends Aurora.Field
 * <p>文本输入组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.TextField = Ext.extend($A.Field,{
	constructor: function(config) {
        $A.TextField.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	$A.TextField.superclass.initComponent.call(this, config);    	
    },
    initEvents : function(){
    	$A.TextField.superclass.initEvents.call(this);   
    },
    processListener : function(ou){
    	$A.TextField.superclass.processListener.call(this, ou);
    	if(this.typecase){
    		if(!window.clipboardData){
    			this.el[ou]("change", this.onChange, this);
    		}else if(this.typecase){
    			this.el[ou]("paste", this.onPaste, this);
    		}
    	}
    },
    onPaste : function(e){	
    	if(window.clipboardData){
            var t = window.clipboardData.getData('text');
            if(this.typecase == 'upper'){
                window.clipboardData.setData('text',t.toUpperCase());
            }else if(this.typecase == 'lower') {
            	window.clipboardData.setData('text',t.toLowerCase());
            }
            setTimeout(function(){window.clipboardData.setData('text',t);},1);
    	}
    },
    onChange : function(e){
        var str = this.getRawValue();
        if(this.isDbc(str)){
            str = this.dbc2sbc(str);
            this.setRawValue(str)
        }
    	if(this.typecase == 'upper'){
	    	this.setValue(this.getRawValue().toUpperCase());
        }else if(this.typecase == 'lower') {
        	this.setValue(this.getRawValue().toLowerCase());
        }
    },
    destroy : function(){
        $A.TextField.superclass.destroy.call(this);
    },
    isCapsLock: function(e){
        var keyCode  =  e.getKey();
        var isShift  =  e.shiftKey;
        if (((keyCode >= 65&&keyCode<=90)&&!isShift)||((keyCode>=97&&keyCode<=122)&&isShift)){
        	if(this.dcl!=true)
            $A.showWarningMessage(_lang['textfield.warn'], _lang['textfield.warn.capslock']);
        	this.dcl = true;
        }else{
            this.dcl = false;
        }
    }, 
    onKeyPress : function(e){
    	$A.TextField.superclass.onKeyPress.call(this,e);
    	if(this.detectCapsLock) this.isCapsLock(e);
		var keyCode = e.getKey();
		var code = keyCode;
		if(this.typecase&&!e.ctrlKey&&!this.readonly){
        	if(this.typecase == 'upper'){
                if(keyCode>=97 && keyCode<=122) code = keyCode - 32;
            }else if(this.typecase == 'lower') {
            	if(keyCode>=65 && keyCode<=90) code = keyCode + 32;
            }
            if(Ext.isIE) {
                e.browserEvent.keyCode = code;
            }else if((keyCode>=97 && keyCode<=122)||(keyCode>=65 && keyCode<=90)){
                var v = String.fromCharCode(code);
                e.stopEvent();
                var d = this.el.dom
                var rv = this.getRawValue();
                var s = d.selectionStart;
                var e = d.selectionEnd;
//                if(rv.length>=this.maxlength&&s==e)return;
//                if(this.isOverMaxLength(rv) && s==e) return;
                rv = rv.substring(0,s) + v + rv.substring(e,rv.length);
                this.setRawValue(rv)
                d.selectionStart=s+1;
                d.selectionEnd=d.selectionStart;
            }
    	}
    },
    isDbc : function(s){
        var dbc = false;
        for(var i=0;i<s.length;i++){
            var c = s.charCodeAt(i);
            if((c>65248)||(c==12288)) {
                dbc = true
                break;
            }
        }
        return dbc;
    },
    dbc2sbc : function(str){
        var result = '';
        for(var i=0;i<str.length;i++){
            code = str.charCodeAt(i);//获取当前字符的unicode编码
            if (code >= 65281 && code <= 65373) {//在这个unicode编码范围中的是所有的英文字母已及各种字符
                result += String.fromCharCode(str.charCodeAt(i) - 65248);//把全角字符的unicode编码转换为对应半角字符的unicode码                
            } else if (code == 12288){//空格
                result += String.fromCharCode(str.charCodeAt(i) - 12288 + 32);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }
})
/**
 * @class Aurora.NumberField
 * @extends Aurora.TextField
 * <p>数字输入组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.NumberField = Ext.extend($A.TextField,{
	allowdecimals : true,
    allownegative : true,
    allowformat : true,
	baseChars : "0123456789",
    decimalSeparator : ".",
    decimalprecision : 2,
	constructor: function(config) {
        $A.NumberField.superclass.constructor.call(this, config);
    },
    initComponent : function(config){
    	$A.NumberField.superclass.initComponent.call(this, config); 
    	this.allowed = this.baseChars+'';
        if(this.allowdecimals){
            this.allowed += this.decimalSeparator;
        }
        if(this.allownegative){
            this.allowed += "-";
        }
    },
    initEvents : function(){
    	$A.NumberField.superclass.initEvents.call(this);    	
    },
    onKeyPress : function(e){
        var k = e.keyCode;
        if((Ext.isGecko || Ext.isOpera) && (e.isSpecialKey() || k == 8 || k == 46)){//BACKSPACE or DELETE
            return;
        }
        var c = e.getCharCode();
        if(this.allowed.indexOf(String.fromCharCode(c)) === -1){
            e.stopEvent();
            return;
        }
        $A.NumberField.superclass.onKeyPress.call(this, e); 
    },
    formatValue : function(v){
    	var rv = this.fixPrecision(this.parseValue(v))        
        if(this.allowformat)rv = $A.formatNumber(rv);
        return $A.NumberField.superclass.formatValue.call(this,rv);
    },
    processMaxLength : function(rv){
    	var s=rv.split('.'),isNegative=false;
    	if(s[0].search(/-/)!=-1)isNegative=true;
    	return (isNegative?'-':'')+$A.NumberField.superclass.processMaxLength.call(this, s[0].replace(/[-,]/g,''))+(s[1]?'.'+s[1]:''); 
    },
    initMaxLength : function(maxlength){
    	if(maxlength && !this.allowdecimals)
    		this.el.dom.maxLength=maxlength;
    },
    processValue : function(v){
        return this.parseValue(v);
    },
    onFocus : function(e) {
    	if(this.readonly) return;
    	if(this.allowformat) {
            this.setRawValue($A.removeNumberFormat(this.getRawValue()));
        }
    	$A.NumberField.superclass.onFocus.call(this,e);
    },
    parseValue : function(value){
    	value = String(value);
		if(value.indexOf(",")!=-1)value=value.replace(/,/g,"");
    	if(!this.allownegative)value = value.replace('-','');
    	if(!this.allowdecimals)value = value.indexOf(".")==-1?value:value.substring(0,value.indexOf("."));
        value = parseFloat(this.fixPrecision(value.replace(this.decimalSeparator, ".")));
        return isNaN(value) ? '' : value;
    },
    fixPrecision : function(value){
        var nan = isNaN(value);
        if(!this.allowdecimals || this.decimalprecision == -1 || nan || !value){
           return nan ? '' : value;
        }
        return parseFloat(value).toFixed(this.decimalprecision);
    }
})
/**
 * @class Aurora.Spinner
 * @extends Aurora.TextField
 * <p>微调范围输入组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Spinner = Ext.extend($A.TextField,{
	constructor: function(config) {
        $A.Spinner.superclass.constructor.call(this, config);
    },
    initComponent : function(config){
    	$A.Spinner.superclass.initComponent.call(this, config);
    	this.step = Number(config.step||1);
		this.max = Ext.isEmpty(config.max)?Number.MAX_VALUE:Number(config.max);
		this.min = Ext.isEmpty(config.min)?-Number.MAX_VALUE:Number(config.min);
		var decimal = String(this.step).split('.')[1];
		this.decimalprecision = decimal?decimal.length:0;
    	this.btn = this.wrap.child('div.item-spinner-btn');
    },
    processListener: function(ou){
    	$A.Spinner.superclass.processListener.call(this, ou);
    	this.btn[ou]('mouseover',this.onBtnMouseOver,this);
    	this.btn[ou]('mouseout',this.onBtnMouseOut,this);
    	this.btn[ou]('mousedown',this.onBtnMouseDown,this);
    	this.btn[ou]('mouseup',this.onBtnMouseUp,this);
    },
    onBtnMouseOver:function(e,t){
    	if(this.readonly)return;
    	Ext.fly(t).addClass('spinner-over');
    },
    onBtnMouseOut:function(e,t){
    	if(this.readonly)return;
    	Ext.fly(t).removeClass('spinner-over');
    	this.onBtnMouseUp(e,t);
    },
    onBtnMouseDown:function(e,t){
    	if(this.readonly)return;
    	var target = Ext.fly(t);
    	target.addClass('spinner-select');
		var isPlus = !!target.parent('.item-spinner-plus');
		this.goStep(isPlus,function(){
			var sf = this;
	    	this.intervalId = setInterval(function(){
		    	clearInterval(sf.intervalId);
	    		sf.intervalId = setInterval(function(){
	    			sf.goStep(isPlus,null,function(){
	    				clearInterval(sf.intervalId);
	    			});
	    		},40);
	    	},500);			
		});
    },
    onBtnMouseUp : function(e,t){
    	if(this.readonly)return;
    	clearInterval(this.intervalId);
    	Ext.fly(t).removeClass('spinner-select');
    	this.setValue(this.tempValue);
    	delete this.intervalId;
    },
    /**
     * 递增
     */
    plus : function(){
    	this.goStep(true,function(n){
    		this.setValue(n);
    	});
    },
    /**
     * 递减
     */
    minus : function(){
    	this.goStep(false,function(n){
    		this.setValue(n);
    	});
    },
    goStep : function(isPlus,callback,callback2){
    	if(this.readonly)return;
    	var n = Ext.isEmpty(this.tempValue) ? Number(this.getValue()||0) : this.tempValue;
    	n += isPlus ? this.step : -this.step;
    	var mod = this.toFixed(this.toFixed(n - this.min)%this.step);
    	n = this.toFixed(n - (mod == this.step ? 0 : mod));
    	if(n <= this.max && n >= this.min){
    		this.setRawValue(n);
	    	this.tempValue = n;
    		if(callback)callback.call(this,n);
    	}else{
    		if(callback2)callback2.call(this,n)
    	}
    },
    toFixed : function(n){
    	return Number(n.toFixed(this.decimalprecision));
    },
    processValue : function(v){
    	if(Ext.isEmpty(v)||isNaN(v))return '';
    	if(v > this.max)v = this.max;
    	else if(v < this.min)v = this.min;
    	//else v -= v%this.step;
        return v;
    }
})
/**
 * @class Aurora.TriggerField
 * @extends Aurora.TextField
 * <p>触发类组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.TriggerField = Ext.extend($A.TextField,{
	constructor: function(config) {
        $A.TriggerField.superclass.constructor.call(this, config);
    },
    initComponent : function(config){
    	$A.TriggerField.superclass.initComponent.call(this, config);
    	this.trigger = this.wrap.child('div[atype=triggerfield.trigger]'); 
    	this.initPopup();
    },
    initPopup: function(){
    	if(this.initpopuped == true) return;
    	this.popup = this.wrap.child('div[atype=triggerfield.popup]');
    	this.shadow = this.wrap.child('div[atype=triggerfield.shadow]');
    	Ext.getBody().insertFirst(this.popup);
    	Ext.getBody().insertFirst(this.shadow);
    	this.initpopuped = true
    },
    processListener: function(ou){
    	$A.TriggerField.superclass.processListener.call(this, ou);
    	this.trigger[ou]('click',this.onTriggerClick, this, {preventDefault:true})
    },
    /**
     * 判断当时弹出面板是否展开
     * @return {Boolean} isexpanded 是否展开
     */
    isExpanded : function(){ 
    	var xy = this.popup.getXY();
    	return !(xy[0]<-500||xy[1]<-500)
    },
    setWidth: function(w){
		this.wrap.setStyle("width",(w+3)+"px");
		this.el.setStyle("width",(w-20)+"px");
	},
    onFocus : function(){
    	if(this.readonly) return;
        $A.TriggerField.superclass.onFocus.call(this);
        if(!this.isExpanded())this.expand();
    },
    onBlur : function(e){
//        if(this.isEventFromComponent(e.target)) return;
//    	if(!this.isExpanded()){
	    	this.hasFocus = false;
	        this.wrap.removeClass(this.focusCss);
	        this.fireEvent("blur", this);
//    	}
    },
    onKeyDown: function(e){
		switch(e.keyCode){
    		case 9:
    		case 13:
    		case 27:if(this.isExpanded())this.collapse();break;
    		case 40:if(!this.isExpanded() && !this.readonly)this.expand();
		}
    	$A.TriggerField.superclass.onKeyDown.call(this,e);
    },
    isEventFromComponent:function(el){
    	var isfrom = $A.TriggerField.superclass.isEventFromComponent.call(this,el);
    	return isfrom || this.popup.contains(el);
    },
	destroy : function(){
		if(this.isExpanded()){
    		this.collapse();
    	}
    	this.shadow.remove();
    	this.popup.remove();
    	delete this.popup;
    	delete this.shadow;
    	$A.TriggerField.superclass.destroy.call(this);
	},
    triggerBlur : function(e){
    	if(this.popup.dom != e.target && !this.popup.contains(e.target) && !this.wrap.contains(e.target)){    		
            if(this.isExpanded()){
	    		this.collapse();
	    	}	    	
        }
    },
    setVisible : function(v){
    	$A.TriggerField.superclass.setVisible.call(this,v);
    	if(v == false && this.isExpanded()){
    		this.collapse();
    	}
    },
    /**
     * 折叠弹出面板
     */
    collapse : function(){
    	Ext.get(document.documentElement).un("mousedown", this.triggerBlur, this);
    	this.popup.moveTo(-1000,-1000);
    	this.shadow.moveTo(-1000,-1000);
    },
    /**
     * 展开弹出面板
     */
    expand : function(){
//    	Ext.get(document.documentElement).on("mousedown", this.triggerBlur, this, {delay: 10});
        //对于某些行上的cb，如果是二级关联的情况下，会expand多次，导致多次绑定事件
        Ext.get(document.documentElement).un("mousedown", this.triggerBlur, this);
    	Ext.get(document.documentElement).on("mousedown", this.triggerBlur, this);
    	this.syncPopup();
    },
    syncPopup:function(){
    	var sl = document[Ext.isStrict&&!Ext.isWebKit?'documentElement':'body'].scrollLeft,
    		st = document[Ext.isStrict&&!Ext.isWebKit?'documentElement':'body'].scrollTop,
    		xy = this.wrap.getXY(),
    		_x = xy[0] - sl,
    		_y = xy[1] - st,
			W=this.popup.getWidth(),H=this.popup.getHeight(),
			PH=this.wrap.getHeight(),PW=this.wrap.getWidth(),
			BH=$A.getViewportHeight()-3,BW=$A.getViewportWidth()-3,
			x=((_x+W)>BW?((BW-W)<0?_x:(BW-W)):_x)+sl;
			y=((_y+PH+H)>BH?((_y-H)<0?(_y+PH):(_y-H)):(_y+PH))+st;
    	this.popup.moveTo(x,y);
    	this.shadow.moveTo(x+3,y+3);
    },
    onTriggerClick : function(){
    	if(this.readonly) return;
    	if(this.isExpanded()){
    		this.collapse();
    	}else{
    		this.expand();
	    	this.el.focus();
    	}
    }
});
/**
 * @class Aurora.ComboBox
 * @extends Aurora.TriggerField
 * <p>Combo组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.ComboBox = Ext.extend($A.TriggerField, {	
	maxHeight:200,
	blankOption:true,
	rendered:false,
	selectedClass:'item-comboBox-selected',	
	//currentNodeClass:'item-comboBox-current',
	constructor : function(config) {
		$A.ComboBox.superclass.constructor.call(this, config);		
	},
	initComponent:function(config){
		$A.ComboBox.superclass.initComponent.call(this, config);
		if(config.options) {
            this.setOptions(config.options);
		}else{
            this.clearOptions();
		}
	},
	initEvents:function(){
		$A.ComboBox.superclass.initEvents.call(this);
		this.addEvents(
		/**
         * @event select
         * 选择事件.
         * @param {Aurora.Combobox} combo combo对象.
         * @param {Object} value valueField的值.
         * @param {String} display displayField的值.
         * @param {Aurora.Record} record 选中的Record对象
         */
		'select');
	},
	onTriggerClick : function() {
		this.doQuery('',true);
		$A.ComboBox.superclass.onTriggerClick.call(this);		
	},
	onBlur : function(e){
        if(this.readonly)return;
        if(this.hasFocus){
			$A.ComboBox.superclass.onBlur.call(this,e);
			if(!this.isExpanded()) {
				var raw = this.getRawValue();
				if(this.fetchrecord===false){
					this.setValue(raw)
				}else{
					var record = this.getRecordByDisplay(raw);
					if(record != null){
						this.setValue(record.get(this.displayfield));				
					}else{
						this.setValue('');
					}
				}
			}
        }
    },
    getRecordByDisplay: function(name){
    	if(!this.optionDataSet)return null;
    	var datas = this.optionDataSet.getAll();
		var l=datas.length;
		var record = null;
		for(var i=0;i<l;i++){
			var r = datas[i];
			var d = r.get(this.displayfield);
			if(d == name){
				record = r;
				break;
			}
		}
		return record;
    },
    /**
     * 展开下拉菜单.
     */
	expand:function(){
		if(!this.optionDataSet)return;
		if(this.rendered===false)this.initQuery();
		this.correctViewSize();
		$A.ComboBox.superclass.expand.call(this);
		var v = this.getValue();
		this.currentIndex = this.getIndex(v);
//		if(!this.currentIndex) return;
		if (!Ext.isEmpty(v)) {				
			this.selectItem(this.currentIndex)
		}		
	},
    onKeyDown: function(e){
        if(this.readonly)return;
        var current = Ext.isEmpty(this.selectedIndex) ? -1 : this.selectedIndex;
        var keyCode = e.keyCode;
        if(keyCode == 40||keyCode == 38){
            this.inKeyMode = true;
            if(keyCode == 38){
                current --;
                if(current>=0){
                    this.selectItem(current)
                }            
            }
            if(keyCode == 40){
                current ++;
                if(current<this.view.dom.childNodes.length){
                    this.selectItem(current)
                }
            }
        }
        if(this.inKeyMode && keyCode == 13){
            this.inKeyMode = false;
            var doms = this.view.dom.childNodes;
            var se = null;
            for(var i=0;i<doms.length;i++){
                var t = doms[i];
                if(Ext.fly(t).hasClass(this.selectedClass)){
                    se = t;
                    break;
                }
            }
            if(se)this.onSelect(se);
            this.collapse();
        } else {
            $A.ComboBox.superclass.onKeyDown.call(this,e);
        }
    },
	/**
	 * 收起下拉菜单.
	 */
	collapse:function(){
		$A.ComboBox.superclass.collapse.call(this);
		if(this.currentIndex!==undefined)
		Ext.fly(this.getNode(this.currentIndex)).removeClass(this.selectedClass);
	},
	clearOptions : function(){
	   this.processDataSet('un');
	   this.optionDataSet = null;
	},
	setOptions : function(name){
		var ds = name
		if(typeof(name)==='string'){
			ds = $(name);
		}
		if(this.optionDataSet != ds){
			this.optionDataSet = ds;
			this.processDataSet('un');
			this.processDataSet('on');
			this.rendered = false;
			if(!Ext.isEmpty(this.value)) this.setValue(this.value, true)
		}
	},
	processDataSet: function(ou){
		if(this.optionDataSet){
            this.optionDataSet[ou]('load', this.onDataSetLoad, this);
            this.optionDataSet[ou]('add', this.onDataSetLoad, this);
            this.optionDataSet[ou]('update', this.onDataSetLoad, this);
            this.optionDataSet[ou]('remove', this.onDataSetLoad, this);
            this.optionDataSet[ou]('clear', this.onDataSetLoad, this);
            this.optionDataSet[ou]('reject', this.onDataSetLoad, this);
		}
	},	
	onDataSetLoad: function(){
		this.rendered=false;
		if(this.isExpanded()) {
            this.expand();
		}
	},
	onRender:function(){	
        if(!this.view){
        	this.popup.update('<ul></ul>');
			this.view=this.popup.child('ul');
			this.view.on('click', this.onViewClick,this);
			this.view.on('mousemove',this.onViewMove,this);
        }
        
        if(this.optionDataSet){
			this.initList();
			this.rendered = true;
		}       
	},
	correctViewSize: function(){
		var widthArray = [];
		var mw = this.wrap.getWidth();
		for(var i=0;i<this.view.dom.childNodes.length;i++){
			var li=this.view.dom.childNodes[i];
			var width=$A.TextMetrics.measure(li,li.innerHTML).width;
			mw = Math.max(mw,width)||mw;
		}
		this.popup.setWidth(mw);
		var lh = Math.min(this.popup.child('ul').getHeight()+4,this.maxHeight); 
		this.popup.setHeight(lh<20?20:lh);
    	this.shadow.setWidth(mw);
    	this.shadow.setHeight(lh<20?20:lh);
	},
	onViewClick:function(e,t){
		if(t.tagName!='LI'){
		    return;
		}		
		this.onSelect(t);
		this.collapse();		
	},	
//	onViewOver:function(e,t){
//		this.inKeyMode = false;
//	},
	onViewMove:function(e,t){
//		if(this.inKeyMode){ // prevent key nav and mouse over conflicts
//            return;
//        }
        var index = t.tabIndex;
        this.selectItem(index);        
	},
	onSelect:function(target){
		var index = target.tabIndex;
		if(index==-1)return;
		var record = this.optionDataSet.getAt(index);
		var value = record.get(this.valuefield);
		var display = this.getRenderText(record);//record.get(this.displayfield);
		this.setValue(display);
		this.fireEvent('select',this, value, display, record);
	},
	initQuery: function(){//事件定义中调用
		this.doQuery(this.getText());
	},
	doQuery : function(q,forceAll) {		
		if(q === undefined || q === null){
			q = '';
	    }		
//		if(forceAll){
//            this.store.clearFilter();
//        }else{
//            this.store.filter(this.displayField, q);
//        }
        
		//值过滤先不添加
		this.onRender();	
	},
	initList: function(){
		this.refresh();
//		this.litp=new Ext.Template('<li tabIndex="{index}">{'+this.displayfield+'}&#160;</li>');
		if(this.optionDataSet.loading == true){
			this.view.update('<li tabIndex="-1">'+_lang['combobox.loading']+'</li>');
		}else{
			var datas = this.optionDataSet.getAll();
			var l=datas.length;
			var sb = [];
			for(var i=0;i<l;i++){
//				var d = Ext.apply(datas[i].data, {index:i})
				var rder = $A.getRenderer(this.renderer);
				var text = this.getRenderText(datas[i]);
				sb.add('<li tabIndex="'+i+'">'+text+'</li>');	//this.litp.applyTemplate(d)等数据源明确以后再修改		
			}
			if(l!=0){
				this.view.update(sb.join(''));			
			}
		}
	},
	getRenderText : function(record){
        var rder = $A.getRenderer(this.renderer);
        var text = '&#160;';
        if(rder){
            text = rder.call(window,this,record);
        }else{
            text = record.get(this.displayfield);
        }
		return text;
	},
	refresh:function(){
		this.view.update('');
		this.selectedIndex = null;
	},
	selectItem:function(index){
		if(Ext.isEmpty(index)){
			return;
		}	
		var node = this.getNode(index);			
		if(node && node.tabIndex!=this.selectedIndex){
			if(!Ext.isEmpty(this.selectedIndex)){							
				Ext.fly(this.getNode(this.selectedIndex)).removeClass(this.selectedClass);
			}
			this.selectedIndex=node.tabIndex;			
			Ext.fly(node).addClass(this.selectedClass);					
		}			
	},
	getNode:function(index){		
		return this.view.dom.childNodes[index];
	},	
	destroy : function(){
		if(this.view){
			this.view.un('click', this.onViewClick,this);
//			this.view.un('mouseover',this.onViewOver,this);
			this.view.un('mousemove',this.onViewMove,this);
		}
		this.processDataSet('un');
    	$A.ComboBox.superclass.destroy.call(this);
		delete this.view;
	},
	getText : function() {		
		return this.text;
	},
//	processValue : function(rv){
//		var r = this.optionDataSet == null ? null : this.optionDataSet.find(this.displayfield, rv);
//		if(r != null){
//			return r.get(this.valuefield);
//		}else{
//			return this.value;
//		}
//	},
//	formatValue : function(){
//		var v = this.getValue();
//		var r = this.optionDataSet == null ? null : this.optionDataSet.find(this.valuefield, v);
//		this.text = '';
//		if(r != null){
//			this.text = r.get(this.displayfield);
//		}else{
////			this.text = v;
//		}
//		return this.text;
//	},
	setValue: function(v, silent){
        $A.ComboBox.superclass.setValue.call(this, v, silent);
        if(this.record){
			var field = this.record.getMeta().getField(this.binder.name);
			if(field){
				var mapping = field.get('mapping');
				var raw = this.getRawValue();
				var record = this.getRecordByDisplay(raw);
//				if(mapping&&record){
				if(mapping){//TODO: v是空的时候?
					for(var i=0;i<mapping.length;i++){
						var map = mapping[i];
    					var vl = record ? record.get(map.from) : (this.fetchrecord===false?raw:'');
//    					var vl = record ? (record.get(map.from)||'') : '';
//    					if(vl!=''){
    					if(!Ext.isEmpty(vl,true)){
    						//避免render的时候发生update事件
    						if(silent){
                                this.record.data[map.to] = vl;
    						}else{
    						    this.record.set(map.to,vl);						
    						}
    					}else{
    						delete this.record.data[map.to];
    					}
						
					}
				}
			}
		}
	},
	getIndex:function(v){
		var datas = this.optionDataSet.getAll();		
		var l=datas.length;
		for(var i=0;i<l;i++){
			if(datas[i].data[this.displayfield]==v){				
				return i;
			}
		}
	}
});
/**
 * @class Aurora.DateField
 * @extends Aurora.Component
 * <p>日期组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.DateField = Ext.extend($A.Component, {
	bodyTpl:['<TABLE cellspacing="0">',
				'<CAPTION class="item-dateField-caption">',
					'{preYearBtn}',
					'{nextYearBtn}',
					'{preMonthBtn}',
					'{nextMonthBtn}',
					'<SPAN>',
						'<SPAN atype="item-year-span" style="margin-right:5px;cursor:pointer"></SPAN>',
						'<SPAN atype="item-month-span" style="cursor:pointer"></SPAN>',
					'</SPAN>',
				'</CAPTION>',
				'<THEAD class="item-dateField-head">',
					'<TR>',
						'<TD>{sun}</TD>',
						'<TD>{mon}</TD>',
						'<TD>{tues}</TD>',
						'<TD>{wed}</TD>',
						'<TD>{thur}</TD>',
						'<TD>{fri}</TD>',
						'<TD>{sat}</TD>',
					'</TR>',
				'</THEAD>',
				'<TBODY>',
				'</TBODY>',
			'</TABLE>'],
	preMonthTpl:'<DIV class="item-dateField-pre" title="{preMonth}" onclick="$(\'{id}\').preMonth()"></DIV>',
	nextMonthTpl:'<DIV class="item-dateField-next" title="{nextMonth}" onclick="$(\'{id}\').nextMonth()"></DIV>',
	preYearTpl:'<DIV class="item-dateField-preYear" title="{preYear}" onclick="$(\'{id}\').preYear()"></DIV>',
	nextYearTpl:'<DIV class="item-dateField-nextYear" title="{nextYear}" onclick="$(\'{id}\').nextYear()"></DIV>',
	popupTpl:'<DIV class="item-popup" atype="date-popup" style="vertical-align: middle;background-color:#fff;visibility:hidden"></DIV>',
    initComponent : function(config){
    	$A.DateField.superclass.initComponent.call(this, config);
    	if(this.height)this.rowHeight=(this.height-18*(Ext.isIE?3:2))/6;
    	var data = {sun:_lang['datefield.sun'],mon:_lang['datefield.mon'],tues:_lang['datefield.tues'],wed:_lang['datefield.wed'],thur:_lang['datefield.thur'],fri:_lang['datefield.fri'],sat:_lang['datefield.sat']}
        if(this.enableyearbtn=="both"||this.enableyearbtn=="pre")
        	data.preYearBtn = new Ext.Template(this.preYearTpl).apply({preYear:_lang['datefield.preYear'],id:this.id});
    	if(this.enableyearbtn=="both"||this.enableyearbtn=="next")
    		data.nextYearBtn = new Ext.Template(this.nextYearTpl).apply({nextYear:_lang['datefield.nextYear'],id:this.id});
        if(this.enablemonthbtn=="both"||this.enablemonthbtn=="pre")
    		data.preMonthBtn = new Ext.Template(this.preMonthTpl).apply({preMonth:_lang['datefield.preMonth'],id:this.id});
    	if(this.enablemonthbtn=="both"||this.enablemonthbtn=="next")
    		data.nextMonthBtn = new Ext.Template(this.nextMonthTpl).apply({nextMonth:_lang['datefield.nextMonth'],id:this.id});
        this.body = new Ext.Template(this.bodyTpl).append(this.wrap.dom,data,true);
        this.yearSpan = this.body.child("span[atype=item-year-span]");
        this.monthSpan = this.body.child("span[atype=item-month-span]");
        this.popup = new Ext.Template(this.popupTpl).append(this.body.child('caption').dom,{},true);
        //this.popup = new Ext.Template(this.popupTpl).append(this.wrap.dom,true);
    },
    processListener: function(ou){
    	$A.DateField.superclass.processListener.call(this,ou);
    	this.body[ou]('mousewheel',this.onMouseWheel,this);	
    	this.body[ou]("mouseover", this.onMouseOver, this);
    	this.body[ou]("mouseout", this.onMouseOut, this);
    	this.body[ou]("mouseup",this.onSelect,this);
    	this.yearSpan[ou]("click",this.onViewShow,this);
    	this.monthSpan[ou]("click",this.onViewShow,this);
    	//this.body[ou]("keydown",this.onKeyDown,this);
    },
    initEvents : function(){
    	$A.DateField.superclass.initEvents.call(this);   	
    	this.addEvents(
    	/**
         * @event select
         * 选择事件.
         * @param {Aurora.DateField} dateField 日期组件.
         * @param {Date} date 选择的日期.
         */
    	'select',
    	/**
         * @event draw
         * 绘制事件.
         * @param {Aurora.DateField} dateField 日期组件.
         */
    	'draw');
    },
    destroy : function(){
    	$A.DateField.superclass.destroy.call(this);
		delete this.preMonthBtn;
    	delete this.nextMonthBtn;
    	delete this.body;
	},
	onMouseWheel:function(e){
        this[(e.getWheelDelta()>0?'pre':'next')+(e.ctrlKey?'Year':'Month')]();
        e.stopEvent();
	},
    onMouseOver: function(e,t){
    	this.out();
    	if(((t = Ext.fly(t)).hasClass('item-day')||(t = t.parent('.item-day'))) && t.getAttributeNS("",'_date') != '0'){
    		$A.DateField.superclass.onMouseOver.call(this,e);
			this.over(t);
    	}
    },
    onMouseOut: function(e){
    	$A.DateField.superclass.onMouseOut.call(this,e);
    	this.out();
    },
    over : function(t){
    	t = t||this.body.last().child('td.item-day')
    	this.overTd = t; 
		t.addClass('dateover');
    },
    out : function(){
    	if(this.overTd) {
    		this.overTd.removeClass('dateover');
    		this.overTd=null;
    	}
    },
    onSelect:function(e,t){
    	var td = Ext.get(t);
    	if(td.parent('div[atype="date-popup"]')){
    		this.onViewClick(e,td);
    	}else{
    		this.fireEvent("select",e,t);
    	}
    },
	onSelectDay: function(o){
		if(!o.hasClass('onSelect'))o.addClass('onSelect');
	},
	onViewShow : function(e,t){
		var span = Ext.get(t);
		this.focusSpan = span;
		var head = this.body.child('thead'),xy = head.getXY();
		this.popup.moveTo(xy[0],xy[1]);
		this.popup.setWidth(head.getWidth());
		this.popup.setHeight(head.getHeight()+head.next().getHeight());
		if(span.getAttributeNS("","atype")=="item-year-span")
			this.initView(this.year,100,true);
		else
			this.initView(7,60);
		Ext.get(document.documentElement).on("mousedown", this.viewBlur, this);
		this.popup.show();
	},
	onViewHide : function(){
		Ext.get(document.documentElement).un("mousedown", this.viewBlur, this);
		this.popup.hide();
	},
	viewBlur : function(e,t){
		if(!this.popup.contains(t) && !(this.focusSpan.contains(t)||this.focusSpan.dom==t)){    		
    		this.onViewHide();
        }
	},
	onViewClick : function(e,t){
		if(t.hasClass('item-day')){
			if(this.focusSpan.getAttributeNS("","atype")=="item-year-span")
				this.year = t.getAttributeNS("",'_data');
			else
				this.month = t.getAttributeNS("",'_data');
			this.year -- ;
			this.nextYear();
			this.onViewHide();
		}
	},
    /**
     * 当前月
     */
	nowMonth: function() {
		this.predraw(new Date());
	},
	/**
	 * 上一月
	 */
	preMonth: function() {
		this.predraw(new Date(this.year, this.month - 2, 1,this.hours,this.minutes,this.seconds));
	},
	/**
	 * 下一月
	 */
	nextMonth: function() {
		this.predraw(new Date(this.year, this.month, 1,this.hours,this.minutes,this.seconds));
	},
	/**
	 * 上一年
	 */
	preYear: function() {
		this.predraw(new Date(this.year - 1, this.month - 1, 1,this.hours,this.minutes,this.seconds));
	},
	/**
	 * 下一年
	 */
	nextYear: function() {
		this.predraw(new Date(this.year + 1, this.month - 1, 1,this.hours,this.minutes,this.seconds));
	},
  	/**
  	 * 根据日期画日历
  	 * @param {Date} date 当前日期
  	 */
  	predraw: function(date,notFire) {
  		if(!date || !date instanceof Date)date = new Date();
  		this.date=date;
  		this.hours=date.getHours();this.minutes=date.getMinutes();this.seconds=date.getSeconds();
		this.year = date.getFullYear(); this.month = date.getMonth() + 1;
		this.draw(new Date(this.year,this.month-1,1,this.hours,this.minutes,this.seconds));
		if(!notFire)this.fireEvent("draw",this);
  	},
  	/**
  	 * 渲染日历
  	 */
	draw: function(date) {
		//用来保存日期列表
		var arr = [],year=date.getFullYear(),month=date.getMonth()+1,hour=date.getHours(),minute=date.getMinutes(),second=date.getSeconds();
		this.yearSpan.update(year+_lang['datefield.year']);
		this.monthSpan.update(month+_lang['datefield.month']);
		//用当月第一天在一周中的日期值作为当月离第一天的天数,用上个月的最后天数补齐
		for(var i = 1, firstDay = new Date(year, month - 1, 1).getDay(),lastDay = new Date(year, month - 1, 0).getDate(); i <= firstDay; i++){ 
			arr.push((this.enablebesidedays=="both"||this.enablebesidedays=="pre")?new Date(year, month - 2, lastDay-firstDay+i,hour,minute,second):null);
		}
		//用当月最后一天在一个月中的日期值作为当月的天数
		for(var i = 1, monthDay = new Date(year, month, 0).getDate(); i <= monthDay; i++){ 
			arr.push(new Date(year, month - 1, i,hour,minute,second)); 
		}
		//用下个月的前几天补齐6行
		for(var i=1, monthDay = new Date(year, month, 0).getDay(),besideDays=43-arr.length;i<besideDays;i++){
			arr.push((this.enablebesidedays=="both"||this.enablebesidedays=="next")?new Date(year, month, i,hour,minute,second):null);
		}
		//先清空内容再插入(ie的table不能用innerHTML)
		var body = this.body.dom.tBodies[0];
		while(body.firstChild){
			Ext.fly(body.firstChild).remove();
		}
		//插入日期
		var k=0;
		while(arr.length){
			//每个星期插入一个tr
			var row = Ext.get(body.insertRow(-1));
			row.set({'r_index':k});
			if(k%2==0)row.addClass('week-alt');
			if(this.rowHeight)row.setHeight(this.rowHeight);
			k++;
			//每个星期有7天
			for(var i = 1; i <= 7; i++){
				var d = arr.shift();
				if(Ext.isDefined(d)){
					var cell = Ext.get(row.dom.insertCell(-1)); 
					if(d){
						cell.set({'c_index':i-1});
						cell.addClass(date.getMonth()==d.getMonth()?"item-day":"item-day item-day-besides");
						cell.update(this.renderCell(cell,d,d.getDate(),month)||d.getDate());
						if(cell.disabled){
							cell.set({'_date':'0'});
							cell.addClass("item-day-disabled");
						}else {
							cell.set({'_date':(''+d.getTime())});
							if(this.format)cell.set({'title':d.format(this.format)})
						}
						//判断是否今日
						if(this.isSame(d, new Date())) cell.addClass("onToday");
						//判断是否选择日期
						if(this.selectDay && this.isSame(d, this.selectDay))this.onSelectDay(cell);
					}else cell.update('&#160;');
				}
			}
		}
	},
	renderCell:function(cell,date,day,currentMonth){
		if(this.dayrenderer)
			return $A.getRenderer(this.dayrenderer).call(this,cell,date,day,currentMonth);
	},
	/**
	 * 判断是否同一日
	 * @param {Date} d1 日期1
	 * @param {Date} d2 日期2
	 * @return {Boolean} 是否同一天
	 */
	isSame: function(d1, d2) {
		if(!d2.getFullYear||!d1.getFullYear)return false;
		return (d1.getFullYear() == d2.getFullYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate());
	},
	initView : function(num,width,isYear){
		var html = ["<table cellspacing='0' cellpadding='0' width='100%'><tr><td width='45%'></td><td width='10%'></td><td width='45%'></td></tr>"];
		for(var i=0,rows = (isYear?5:6),year = num - rows,year2 = num;i<rows;i++){
			html.push("<tr><td class='item-day' _data='"+year+"'>"+year+"</td><td></td><td class='item-day' _data='"+year2+"'>"+year2+"</td></tr>");
			year += 1;year2 += 1;
		}
		html.push("");
		if(isYear){
			html.push("<tr><td><div class='item-dateField-pre' onclick='$(\""+this.id+"\").initView("+(num-10)+","+width+",true)'></div></td>");
			html.push("<td><div class='item-dateField-close' onclick='$(\""+this.id+"\").onViewHide()'></div></td>")
			html.push("<td><div class='item-dateField-next' onclick='$(\""+this.id+"\").initView("+(num+10)+","+width+",true)'></div></td></tr>");
		}else{
			html.push("<td colspan='3' align='center'><div class='item-dateField-close' onclick='$(\""+this.id+"\").onViewHide()'></div></td>")
		}
		html.push("</table>");
		this.popup.update(html.join(''));
	}
});
/**
 * @class Aurora.DatePicker
 * @extends Aurora.TriggerField
 * <p>DatePicker组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.DatePicker = Ext.extend($A.TriggerField,{
	nowTpl:['<DIV class="item-day" style="cursor:pointer" title="{title}">{now}</DIV>'],
	constructor: function(config) {
		this.dateFields = [];
		this.cmps = {};
        $A.DatePicker.superclass.constructor.call(this,config);
    },
	initComponent : function(config){
		$A.DatePicker.superclass.initComponent.call(this,config);
		this.initFormat();
	},
	initFormat : function(){
		this.format=this.format||$A.defaultDateFormat;
	},
    initDatePicker : function(){
        if(!this.inited){
            this.initDateField();
            this.initFooter();
            this.inited = true;
            this.processListener('un');
            this.processListener('on');
        }
    },
    initDateField:function(){
    	this.popup.setStyle({'width':150*this.viewsize+'px'})
    	if(this.dateFields.length==0){
    		window['__host']=this;
    		for(var i=0;i<this.viewsize;i++){
	    		var cfg = {
	    			id:this.id+'_df'+i,
	    			height:130,
	    			enablemonthbtn:'none',
	    			enablebesidedays:'none',
	    			dayrenderer:this.dayrenderer,
	    			listeners:{
	    				"select":this.onSelect.createDelegate(this),
	    				"draw":this.onDraw.createDelegate(this),
	    				"mouseover":this.mouseOver.createDelegate(this),
	    				"mouseout":this.mouseOut.createDelegate(this)
	    			}
	    		}
		    	if(i==0){
		    		if(this.enablebesidedays=="both"||this.enablebesidedays=="pre")
		    			cfg.enablebesidedays="pre";
		    		if(this.enablemonthbtn=="both"||this.enablemonthbtn=="pre")
		    			cfg.enablemonthbtn="pre";
		    		if(this.enableyearbtn=="both"||this.enableyearbtn=="pre")
		    			cfg.enableyearbtn="pre";
		    	}
		    	if(i==this.viewsize-1){
		    		if(this.enablebesidedays=="both"||this.enablebesidedays=="next")
		    			cfg.enablebesidedays=cfg.enablebesidedays=="pre"?"both":"next";
		    		if(this.enablemonthbtn=="both"||this.enablemonthbtn=="next")
		    			cfg.enablemonthbtn=cfg.enablemonthbtn=="pre"?"both":"next";
		    		if(this.enableyearbtn=="both"||this.enableyearbtn=="next")
		    			cfg.enableyearbtn=cfg.enableyearbtn=="pre"?"both":"next";
		    	}else Ext.fly(this.id+'_df'+i).dom.style.cssText="border-right:1px solid #BABABA";
		    	this.dateFields.add(new $A.DateField(cfg));
    		}
    		window['__host']=null;
    	}
    },
    initFooter : function(){
    	if(!this.now)this.now=new Ext.Template(this.nowTpl).append(this.popup.child("div.item-dateField-foot").dom,{now:_lang['datepicker.today'],title:new Date().format(this.format)},true);;
    	var now = new Date();
    	this.now.set({"_date":new Date(now.getFullYear(),now.getMonth(),now.getDate(),0,0,0).getTime()});
    },
    initEvents : function(){
    	$A.DatePicker.superclass.initEvents.call(this);
        this.addEvents(
        /**
         * @event select
         * 选择事件.
         * @param {Aurora.DatePicker} datePicker 日期选择组件.
         * @param {Date} date 选择的日期.
         */
        'select');
    },
    processListener : function(ou){
    	$A.DatePicker.superclass.processListener.call(this,ou);
    	this.el[ou]('click',this.mouseOut, this);
    	if(this.now)this.now[ou]("click", this.onSelect, this);
    },
    mouseOver: function(cmp,e){
    	if(this.focusField)this.focusField.out();
    	this.focusField = cmp
    },
    mouseOut: function(){
    	if(this.focusField)this.focusField.out();
    	this.focusField = null;
    },
    onKeyUp: function(e){
    	if(this.readonly)return;
    	$A.DatePicker.superclass.onKeyUp.call(this,e);
    	var c = e.keyCode;
    	if(!e.isSpecialKey()||c==8||c==46){
	    	try{
	    		this.selectDay=this.getRawValue().parseDate(this.format);
                this.wrapDate(this.selectDay);
	    		$A.Component.prototype.setValue.call(this,this.selectDay||"");
	    		this.predraw(this.selectDay);
	    	}catch(e){
	    	}
    	}
    },
    onKeyDown: function(e){
    	if(this.readonly)return;
    	if(this.focusField){
	    	switch(e.keyCode){
	    		case 37:this.goLeft(e);break;
	    		case 38:this.goUp(e);break;
	    		case 39:this.goRight(e);break;
	    		case 40:this.goDown(e);break;
	    		case 13:this.onSelect(e,this.focusField.overTd);
	    		default:{
					if(this.focusField)this.focusField.out();
					this.focusField = null;
	    		}
	    	}
   		}else {
   			$A.DatePicker.superclass.onKeyDown.call(this,e);
   			if(e.keyCode == 40){
				this.focusField = this.dateFields[0];
				this.focusField.over();
   			}
   		}
    },
    goLeft : function(e){
    	var field = this.focusField;
		var td = field.overTd,prev = td.prev('.item-day');
		field.out();
    	if(prev) {
    		field.over(prev);
    	}else{
			var f = this.dateFields[this.dateFields.indexOf(field)-1],
			index = td.parent().getAttributeNS('','r_index')
			if(f){
				this.focusField = f;
			}else{
				field.preMonth();
				this.focusField = this.dateFields[this.dateFields.length-1];
			}
			var l = this.focusField.body.child('tr[r_index='+index+']').select('td.item-day')
			this.focusField.over(l.item(l.getCount()-1));
		}
		e.stopEvent();
    },
    goUp : function(e){
    	var field = this.focusField;
		var td = field.overTd,prev = td.parent().prev(),index = td.getAttributeNS('','c_index'),t;
		field.out();
		if(prev)t = prev.child('td[c_index='+index+']');
		if(t)field.over(t);
		else {
			var f = this.dateFields[this.dateFields.indexOf(field)-1];
			if(f){
				this.focusField = f;
			}else{
				field.preMonth();
				this.focusField = this.dateFields[0];
			}
			var l = this.focusField.body.select('td[c_index='+index+']')
			this.focusField.over(l.item(l.getCount()-1));
		}
    },
    goRight : function(e){
    	var field = this.focusField;
		var td = field.overTd,next = td.next('.item-day'),parent = td.parent();
		field.out();
    	if(next) {
    		field.over(next);
    	}else{
			var f = this.dateFields[this.dateFields.indexOf(field)+1];
			if(f){
				this.focusField = f;
			}else{
				field.nextMonth();
				this.focusField = this.dateFields[0];
			}
			this.focusField.over(this.focusField.body.child('tr[r_index='+parent.getAttributeNS('','r_index')+']').child('td.item-day'));
		}
		e.stopEvent();
    },
    goDown : function(e){
    	var field = this.focusField;
		var td = field.overTd,next = td.parent().next(),t,index = td.getAttributeNS('','c_index');
		field.out();
		if(next)t = next.child('td[c_index='+index+']');
		if(t)field.over(t);
		else {
			var f = this.dateFields[this.dateFields.indexOf(field)+1];
			if(f){
				this.focusField = f;
			}else{
				field.nextMonth();
				this.focusField = this.dateFields[this.dateFields.length-1];
			}
			this.focusField.over(this.focusField.body.child('td[c_index='+index+']'));
		}
    },
    onDraw : function(field){
    	if(this.dateFields.length>1)this.sysnDateField(field);
    	this.shadow.setWidth(this.popup.getWidth());
    	this.shadow.setHeight(this.popup.getHeight());
    },
    onSelect: function(e,t){
		if((Ext.fly(t).hasClass('item-day'))&& Ext.fly(t).getAttributeNS("",'_date') != '0'){
    		var date=new Date(parseInt(Ext.fly(t).getAttributeNS("",'_date')));
	    	this.collapse();
            this.processDate(date);            
	    	this.setValue(date);
	    	this.fireEvent('select',this, date);
    	}
    },
    wrapDate : function(d){},
    processDate : function(d){},
    onBlur : function(e){
    	if(this.readonly)return;
    	if(this.hasFocus){
			$A.DatePicker.superclass.onBlur.call(this,e);
			if(!this.isExpanded()){
				try{
	                var d = this.getRawValue().parseDate(this.format)
	                this.wrapDate(d);
					this.setValue(d||"");
				}catch(e){
	                //alert(e.message);
					this.setValue("");
				}
			}
    	}
    },
    formatValue : function(date){
    	if(date instanceof Date)return date.format(this.format);
    	return date;
    },
    expand : function(){
        this.initDatePicker();
    	this.selectDay = this.getValue();
		this.predraw(this.selectDay);
    	$A.DatePicker.superclass.expand.call(this);
    },
    collapse : function(){
    	$A.DatePicker.superclass.collapse.call(this);
    	this.focusField = null;
    },
    destroy : function(){
    	$A.DatePicker.superclass.destroy.call(this);
    	delete this.format;
    	delete this.viewsize;
    	var sf = this;
        setTimeout(function(){
        	for(var key in sf.cmps){
        		var cmp = sf.cmps[key];
        		if(cmp.destroy){
        			try{
        				cmp.destroy();
        			}catch(e){
        				alert('销毁window出错: ' + e)
        			}
        		}
        	}
        },10)
	},
	predraw : function(date){
		if(date && date instanceof Date){
			this.selectDay=new Date(date);
		}else {
			date=new Date();
			date.setHours(this.hour||0);
			date.setMinutes(this.minute||0);
			date.setSeconds(this.second||0);
			date.setMilliseconds(0);
		}
		this.draw(date);
	},
	draw: function(date){
		this.dateFields[0].selectDay=this.selectDay;
		this.dateFields[0].format=this.format;
		this.dateFields[0].predraw(date);
	},
	sysnDateField : function(field){
		var date=new Date(field.date);
		for(var i=0;i<this.viewsize;i++){
			if(field==this.dateFields[i])date.setMonth(date.getMonth()-i);
		}
		for(var i=0;i<this.viewsize;i++){
			this.dateFields[i].selectDay=this.selectDay;
			if(i!=0)date.setMonth(date.getMonth()+1);
			this.dateFields[i].format=this.format;
			if(field!=this.dateFields[i])
			this.dateFields[i].predraw(date,true);
		}
	}
});
/**
 * @class Aurora.DateTimePicker
 * @extends Aurora.DatePicker
 * <p>DatePicker组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.DateTimePicker = Ext.extend($A.DatePicker,{
	initFormat : function(){
		this.format=this.format||$A.defaultDateTimeFormat;
	},
	initFooter : function(){
		this.hourSpan = this.popup.child("input[atype=field.hour]");
    	this.minuteSpan = this.popup.child("input[atype=field.minute]");
    	this.secondSpan = this.popup.child("input[atype=field.second]");
    },
    processListener : function(ou){
    	$A.DateTimePicker.superclass.processListener.call(this,ou);
    	if(this.hourSpan){
	    	this.hourSpan[ou]("focus", this.onDateFocus, this);
			this.hourSpan[ou]("blur", this.onDateBlur, this);
			this.minuteSpan[ou]("focus", this.onDateFocus, this);
			this.minuteSpan[ou]("blur", this.onDateBlur, this);
			this.secondSpan[ou]("focus", this.onDateFocus, this);
			this.secondSpan[ou]("blur", this.onDateBlur, this);
			this.hourSpan.parent()[ou]("keydown", this.onDateKeyDown, this);
			this.hourSpan.parent()[ou]("keyup", this.onDateKeyUp, this);
    	}
    },
    onDateKeyDown : function(e) {
		var c = e.keyCode, el = e.target;
		if (c == 13) {
			el.blur();
		} else if (c == 27) {
			el.value = el.oldValue || "";
			el.blur();
		} else if (c != 8 && c!=9 && c!=37 && c!=39 && c != 46 && (c < 48 || c > 57 || e.shiftKey)) {
			e.stopEvent();
			return;
		}
	},
	onDateKeyUp : function(e){
		var c = e.keyCode, el = e.target;
		if (c != 8 && c!=9 && c!=37 && c!=39 && c != 46 && (c < 48 || c > 57 || e.shiftKey)) {
			e.stopEvent();
			return;
		} else if(this.value&&this.value instanceof Date){
			var date=new Date(this.value.getTime());
			this.processDate(date);
	    	this.setValue(date);
	    	//this.fireEvent('select',this, date);
		}
	},
    onDateFocus : function(e) {
		Ext.fly(e.target.parentNode).addClass("item-dateField-input-focus");
		e.target.select();
	},
	onDateBlur : function(e) {
		var el=e.target;
		Ext.fly(el.parentNode).removeClass("item-dateField-input-focus");
		if(!el.value.match(/^[0-9]*$/))el.value=el.oldValue||"";
		else this.draw(new Date(this.dateFields[0].year,this.dateFields[0].month - 1, 1,this.hourSpan.dom.value,this.minuteSpan.dom.value,this.secondSpan.dom.value));
	},
	predraw : function(date,noSelect){
		$A.DateTimePicker.superclass.predraw.call(this,date,noSelect);
		this.hourSpan.dom.oldValue = this.hourSpan.dom.value = $A.dateFormat.pad(this.dateFields[0].hours);
		this.minuteSpan.dom.oldValue = this.minuteSpan.dom.value = $A.dateFormat.pad(this.dateFields[0].minutes);
		this.secondSpan.dom.oldValue = this.secondSpan.dom.value = $A.dateFormat.pad(this.dateFields[0].seconds);
	},
    processDate : function(d){
        if(d){
            d.setHours((el=this.hourSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
            d.setMinutes((el=this.minuteSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
            d.setSeconds((el=this.secondSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
            this.wrapDate(d)
        }
    },
    wrapDate : function(d){
        if(d)
        d.xtype = 'timestamp';
    }
//    ,collapse : function(){
//    	$A.DateTimePicker.superclass.collapse.call(this);
//    	if(this.getRawValue()){
//    		var d = this.selectDay;
//    		if(d){
//	    		d.setHours((el=this.hourSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
//	    		d.setMinutes((el=this.minuteSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
//	    		d.setSeconds((el=this.secondSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
//    		}
//    		d.xtype = 'timestamp';
//    		this.setValue(d);
//    	}
//    }
});
$A.ToolBar = Ext.extend($A.Component,{
	constructor: function(config) {
        $A.ToolBar.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	$A.ToolBar.superclass.initComponent.call(this, config);    	
    },
    initEvents : function(){
    	$A.ToolBar.superclass.initEvents.call(this); 
    }
})
$A.NavBar = Ext.extend($A.ToolBar,{
	constructor: function(config) {
        $A.NavBar.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	$A.NavBar.superclass.initComponent.call(this, config);
    	this.dataSet = $(this.dataSet);
    	this.navInfo = this.wrap.child('div[atype=displayInfo]');//Ext.get(this.infoId);
    	if(this.type != "simple" && this.type != "tiny"){
	    	this.pageInput = $(this.inputId);
	    	this.currentPage = this.wrap.child('div[atype=currentPage]');
	    	this.pageInfo = this.wrap.child('div[atype=pageInfo]');//Ext.get(this.pageId);
	
	    	if(this.comboBoxId){
	    		this.pageSizeInput = $(this.comboBoxId);
	    		this.pageSizeInfo = this.wrap.child('div[atype=pageSizeInfo]');
	    		this.pageSizeInfo2 = this.wrap.child('div[atype=pageSizeInfo2]');
	    		this.pageSizeInfo.update(_lang['toolbar.pageSize']);
	    		this.pageSizeInfo2.update(_lang['toolbar.pageSize2']);
	    	}
	    	this.pageInfo.update(_lang['toolbar.total'] + '&#160;&#160;' + _lang['toolbar.page']);
	    	this.currentPage.update(_lang['toolbar.ctPage']);
    	}
    },
    processListener: function(ou){
    	$A.NavBar.superclass.processListener.call(this,ou);
    	this.dataSet[ou]('load', this.onLoad,this);
    	if(this.type != "simple" && this.type != "tiny"){
    		this.pageInput[ou]('change', this.onPageChange, this);
    		if(this.pageSizeInput){
    			this.pageSizeInput[ou]('change', this.onPageSizeChange, this);
    		}
    	}
    },
    initEvents : function(){
    	$A.NavBar.superclass.initEvents.call(this);    	
    },
    onLoad : function(){
    	this.navInfo.update(this.creatNavInfo());
    	if(this.type != "simple" && this.type != "tiny"){
	    	this.pageInput.setValue(this.dataSet.currentPage);
	    	this.pageInfo.update(_lang['toolbar.total'] + this.dataSet.totalPage + _lang['toolbar.page']);
	    	if(this.pageSizeInput&&!this.pageSizeInput.optionDataSet){
	    		var pageSize=[10,20,50,100];
	    		if(pageSize.indexOf(this.dataSet.pagesize)==-1){
	    			pageSize.unshift(this.dataSet.pagesize);
	    			pageSize.sort(function(a,b){return a-b});
	    		}
	    		var datas=[];
	    		while(Ext.isDefined(pageSize[0])){
	    			var ps=pageSize.shift();
	    			datas.push({'code':ps,'name':ps});
	    		}
	    		var dataset=new $A.DataSet({'datas':datas});
		    	this.pageSizeInput.setOptions(dataset);
		    	this.pageSizeInput.setValue(this.dataSet.pagesize);
	    	}
    	}
    },
    creatNavInfo : function(){
    	if(this.type == "simple"){
    		var html=[],ds=this.dataSet,currentPage=ds.currentPage,totalPage=ds.totalPage;
    		if(totalPage){
    			html.push('<span>共'+totalPage+'页</span>');
    			html.push(currentPage == 1 ? '<span>首页</span>' : this.createAnchor('首页',1));
    			html.push(currentPage == 1 ? '<span>上一页</span>' : this.createAnchor('上一页',currentPage-1));
    			for(var i = 1 ; i < 4 && i <= totalPage ; i++){
    				html.push(i == currentPage ? '<b>' + currentPage + '</b>' : this.createAnchor(i,i));
    			}
    			if(totalPage > this.maxPageCount){
    				if(currentPage > 5)this.createSplit(html);
    				for(var i = currentPage - 1;i < currentPage + 2 ;i++){
    					if(i > 3 && i < totalPage - 2){
    						html.push(i == currentPage ? '<b>' + currentPage + '</b>' : this.createAnchor(i,i));
    					}
    				}
    				if(currentPage < totalPage - 4)this.createSplit(html);
    			}else if(totalPage > 6){
    				for(var i = 4; i < totalPage - 2 ;i++){
    					html.push(i == currentPage ? '<b>' + currentPage + '</b>' : this.createAnchor(i,i));
    				}
    			}
	    		for(var i = totalPage - 2 ; i < totalPage + 1; i++){
	    			if(i > 3){
    					html.push(i == currentPage ? '<b>' + currentPage + '</b>' : this.createAnchor(i,i));
	    			}
    			}
	    		html.push(currentPage == totalPage ? '<span>下一页</span>' : this.createAnchor('下一页',currentPage+1));
    			html.push(currentPage == totalPage ? '<span>尾页</span>' : this.createAnchor('尾页',totalPage));
    		}
    		return html.join('');
    	}else if(this.type == 'tiny'){
    		var html=[],ds=this.dataSet,currentPage=ds.currentPage;
    		html.push(currentPage == 1 ? '<span>首页</span>' : this.createAnchor('首页',1));
			html.push(currentPage == 1 ? '<span>上一页</span>' : this.createAnchor('上一页',currentPage-1));
    		html.push(this.createAnchor('下一页',currentPage+1));
    		html.push('<span>第'+currentPage+'页</span>');
    		return html.join('');
    	}else{
	    	var from = ((this.dataSet.currentPage-1)*this.dataSet.pagesize+1);
	    	var to = this.dataSet.currentPage*this.dataSet.pagesize;
	    	if(to>this.dataSet.totalCount) to = this.dataSet.totalCount;
	    	if(to==0) from =0;
	    	return _lang['toolbar.visible'] + from + ' - ' + to + ' '+ _lang['toolbar.total'] + this.dataSet.totalCount + _lang['toolbar.item'];
    	}
    },
    createAnchor : function(text,page){
    	return '<a href="javascript:$(\''+this.dataSet.id+'\').goPage('+page+')">'+text+'</a>';
    },
    createSplit : function(html){
    	html.push('<span>···</span>');
    },
    onPageChange : function(el,value,oldvalue){
    	if(this.dataSet.totalPage == 0){
    		el.setValue(1);
    	}else if(isNaN(value) || value<=0 || value>this.dataSet.totalPage){
    		el.setValue(oldvalue)
    	}else if(this.dataSet.currentPage!=value){
	    	this.dataSet.goPage(value);
    	}
    },
    onPageSizeChange : function(el,value,oldvalue){
    	var max = this.dataSet.maxpagesize;
    	if(isNaN(value) || value<=0){
    		el.setValue(oldvalue);
    	}else if(value > max){
			$A.showMessage(_lang['toolbar.errormsg'],_lang['toolbar.maxPageSize']+max+_lang['toolbar.item'],null,240);
			el.setValue(oldvalue);
		}else if(this.dataSet.pagesize!=value){
	    	this.dataSet.pagesize=Math.round(value);
	    	this.dataSet.query();
    	}
    }
})
$A.WindowManager = function(){
    return {
        put : function(win){
        	if(!this.cache) this.cache = [];
        	this.cache.add(win)
        },
        getAll : function(){
        	return this.cache;
        },
        remove : function(win){
        	this.cache.remove(win);
        },
        get : function(id){
        	if(!this.cache) return null;
        	var win = null;
        	for(var i = 0;i<this.cache.length;i++){
    			if(this.cache[i].id == id) {
	        		win = this.cache[i];
    				break;      			
        		}
        	}
        	return win;
        },
        getZindex: function(){
        	var zindex = 40;
        	var all = this.getAll();
        	for(var i = 0;i<all.length;i++){
        		var win = all[i];
        		var zd = win.wrap.getStyle('z-index');
        		if(zd =='auto') zd = 0;
        		if(zd > zindex) zindex = zd;       		
        	}
        	return Number(zindex);
        }
    };
}();
/**
 * @class Aurora.Window
 * @extends Aurora.Component
 * <p>窗口组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Window = Ext.extend($A.Component,{
	constructor: function(config) { 
		if($A.WindowManager.get(config.id))return;
        this.draggable = true;
        this.closeable = true;
        this.fullScreen = false;
        this.modal = config.modal||true;
        this.cmps = {};
//        $A.focusWindow = null;
        $A.Window.superclass.constructor.call(this,config);
    },
    initComponent : function(config){
    	$A.Window.superclass.initComponent.call(this, config);
    	var sf = this; 
    	$A.WindowManager.put(sf);
    	var windowTpl = new Ext.Template(sf.getTemplate());
    	var shadowTpl = new Ext.Template(sf.getShadowTemplate());
    	sf.width = 1*(sf.width||350);
    	sf.height= 1*(sf.height||400);
    	if(sf.fullScreen){
    		sf.width=$A.getViewportWidth()-(Ext.isIE||!sf.hasVScrollBar()?0:17)-(Ext.isIE8?1:0);
    		sf.height=$A.getViewportHeight()-(Ext.isIE||!sf.hasHScrollBar()?26:43);
    		sf.draggable = false;
    		sf.marginheight=1;
    		sf.marginwidth=1;
    	}
        var urlAtt = '';
        if(sf.url){
            urlAtt = 'url="'+sf.url+'"';
        }
        sf.wrap = windowTpl.insertFirst(document.body, {title:sf.title,width:sf.width,bodywidth:sf.width-2,height:sf.height,url:urlAtt}, true);
        sf.shadow = shadowTpl.insertFirst(document.body, {}, true);
        sf.shadow.setWidth(sf.wrap.getWidth());
        sf.shadow.setHeight(sf.wrap.getHeight());
        sf.focusEl = sf.wrap.child('a[atype=win.focus]')
    	sf.title = sf.wrap.child('div[atype=window.title]');
    	sf.head = sf.wrap.child('td[atype=window.head]');
    	sf.body = sf.wrap.child('div[atype=window.body]');
        sf.closeBtn = sf.wrap.child('div[atype=window.close]');
        if(sf.draggable) sf.initDraggable();
        if(!sf.closeable)sf.closeBtn.hide();
        if(Ext.isEmpty(config.x)||Ext.isEmpty(config.y)||sf.fullScreen){
            sf.center();
        }else{
            sf.move(config.x,config.y);
            this.toFront();
            this.focus.defer(10,this);
        }
        if(sf.url){
        	sf.showLoading();       
        	sf.load(sf.url,config.params)
        }
    },
    processListener: function(ou){
    	$A.Window.superclass.processListener.call(this,ou);
    	if(this.closeable) {
    	   this.closeBtn[ou]("click", this.onCloseClick,  this); 
    	   this.closeBtn[ou]("mouseover", this.onCloseOver,  this);
    	   this.closeBtn[ou]("mouseout", this.onCloseOut,  this);
    	   this.closeBtn[ou]("mousedown", this.onCloseDown,  this);
    	}
        if(!this.modal) this.wrap[ou]("click", this.toFront, this);
    	this.focusEl[ou]("keydown", this.handleKeyDown,  this);
    	if(this.draggable)this.head[ou]('mousedown', this.onMouseDown,this);
    },
    initEvents : function(){
    	$A.Window.superclass.initEvents.call(this);
    	this.addEvents(
    	/**
         * @event beforeclose
         * 窗口关闭前的事件.
         * <p>监听函数返回值为false时，不执行关闭</p>
         * @param {Window} this 当前窗口.         * 
         */
    	'beforeclose',
    	/**
         * @event close
         * 窗口关闭事件.
         * @param {Window} this 当前窗口.         * 
         */
    	'close',
    	/**
         * @event load
         * 窗口加载完毕.
         * @param {Window} this 当前窗口.
         */
    	'load');    	
    },
    handleKeyDown : function(e){
		e.stopEvent();
		var key = e.getKey();
		if(key == 27){
			this.close();
		}
    },
    initDraggable: function(){
    	this.head.addClass('item-draggable');
    },
    /**
     * 窗口获得焦点.
     * 
     */
    focus: function(){
		this.focusEl.focus();
	},
	/**
     * 窗口居中.
     * 
     */
    center: function(){
    	var screenWidth = $A.getViewportWidth();
    	var screenHeight = $A.getViewportHeight();
    	var sl = document[Ext.isStrict&&!Ext.isWebKit?'documentElement':'body'].scrollLeft;
    	var st = document[Ext.isStrict&&!Ext.isWebKit?'documentElement':'body'].scrollTop;
    	var x = sl+Math.max((screenWidth - this.width)/2,0);
    	var y = st+Math.max((screenHeight - this.height-(Ext.isIE?26:23))/2,0);
//        this.shadow.setWidth(this.wrap.getWidth());
//        this.shadow.setHeight(this.wrap.getHeight());
        if(this.fullScreen){
        	x=sl;y=st;
            this.move(x,y,true);
        	this.shadow.moveTo(x,y)
        }else {
            this.move(x,y)
        }
//        this.wrap.moveTo(x,y);
        this.toFront();
        this.focus.defer(10,this);
    },
    /**
     * 移动窗口到指定位置.
     * 
     */
    move: function(x,y,m){
        this.wrap.moveTo(x,y);
        if(!m)this.shadow.moveTo(x+3,y+3)
    },
    hasVScrollBar : function(){
    	var body=document[Ext.isStrict?'documentElement':'body'];
    	return body.scrollTop>0||body.scrollHeight>body.clientHeight;
    },
    hasHScrollBar : function(){
    	var body=document[Ext.isStrict?'documentElement':'body'];
    	return body.scrollLeft>0||body.scrollWidth>body.clientWidth;
    },
    getShadowTemplate: function(){
    	return ['<DIV class="item-shadow"></DIV>']
    },
    getTemplate : function() {
        return [
            '<TABLE class="win-wrap" style="left:-1000px;top:-1000px;width:{width}px;" cellSpacing="0" cellPadding="0" border="0" {url}>',
			'<TBODY>',
			'<TR style="height:23px;" >',
				'<TD class="win-caption">',
					'<TABLE cellSpacing="0" unselectable="on"  onselectstart="return false;" style="height:23px;-moz-user-select:none;"  cellPadding="0" width="100%" border="0" unselectable="on">',
						'<TBODY>',
						'<TR>',
							'<TD unselectable="on" class="win-caption-label" atype="window.head" width="99%">',
								'<A atype="win.focus" href="#" class="win-fs" tabIndex="-1"></A><DIV unselectable="on" atype="window.title" unselectable="on">{title}</DIV>',
							'</TD>',
							'<TD unselectable="on" class="win-caption-button" noWrap>',
								'<DIV class="win-close" atype="window.close" unselectable="on"></DIV>',
							'</TD>',
							'<TD><DIV style="width:5px;"/></TD>',
						'</TR>',
						'</TBODY>',
					'</TABLE>',
				'</TD>',
			'</TR>',
			'<TR style="height:{height}px">',
				'<TD class="win-body" vAlign="top" unselectable="on">',
					'<DIV class="win-content" atype="window.body" style="position:relatvie;width:{bodywidth}px;height:{height}px;" unselectable="on"></DIV>',
				'</TD>',
			'</TR>',
			'</TBODY>',
		'</TABLE>'
        ];
    },
    /**
     * 窗口定位到最上层.
     * 
     */
    toFront : function(){ 
    	var myzindex = this.wrap.getStyle('z-index');
    	var zindex = $A.WindowManager.getZindex();
    	if(myzindex =='auto') myzindex = 0;
    	if(myzindex < zindex) {
	    	this.wrap.setStyle('z-index', zindex+5);
	    	this.shadow.setStyle('z-index', zindex+4);
	    	if(this.modal) $A.Cover.cover(this.wrap);
    	}
//    	$A.focusWindow = this;    	
    },
    onMouseDown : function(e){
    	var sf = this; 
    	//e.stopEvent();
    	sf.toFront();
    	var xy = sf.wrap.getXY();
    	sf.relativeX=xy[0]-e.getPageX();
		sf.relativeY=xy[1]-e.getPageY();
		sf.screenWidth = $A.getViewportWidth();
        sf.screenHeight = $A.getViewportHeight();
        if(!this.proxy) this.initProxy();
        this.proxy.show();
    	Ext.get(document.documentElement).on("mousemove", sf.onMouseMove, sf);
    	Ext.get(document.documentElement).on("mouseup", sf.onMouseUp, sf);
        sf.focus();
    },
    onMouseUp : function(e){
    	var sf = this; 
    	Ext.get(document.documentElement).un("mousemove", sf.onMouseMove, sf);
    	Ext.get(document.documentElement).un("mouseup", sf.onMouseUp, sf);
    	if(sf.proxy){
    		sf.wrap.moveTo(sf.proxy.getX(),sf.proxy.getY());
    		sf.shadow.moveTo(sf.proxy.getX()+3,sf.proxy.getY()+3);
	    	sf.proxy.hide();
    	}
    },
    onMouseMove : function(e){
    	e.stopEvent();
    	var sl = document[Ext.isStrict&&!Ext.isWebKit?'documentElement':'body'].scrollLeft;
    	var st = document[Ext.isStrict&&!Ext.isWebKit?'documentElement':'body'].scrollTop;
    	var sw = sl + this.screenWidth;
    	var sh = st + this.screenHeight;
    	var tx = e.getPageX()+this.relativeX;
    	var ty = e.getPageY()+this.relativeY;
//    	if(tx<=sl) tx =sl;
//    	if((tx+this.width)>= (sw-3)) tx = sw - this.width - 3;
//    	if(ty<=st) ty =st;
//    	if((ty+this.height)>= (sh-30)) ty = Math.max(sh - this.height - 30,0);
    	this.proxy.moveTo(tx,ty);
    },
    showLoading : function(){
    	this.body.update(_lang['window.loading']);
    	this.body.setStyle('text-align','center');
    	this.body.setStyle('line-height',5);
    },
    clearLoading : function(){
    	this.body.update('');
    	this.body.setStyle('text-align','');
    	this.body.setStyle('line-height','');
    },
    initProxy : function(){
    	var sf = this; 
    	var p = '<DIV style="border:1px dashed black;Z-INDEX: 10000; LEFT: 0px; WIDTH: 100%; CURSOR: default; POSITION: absolute; TOP: 0px; HEIGHT: 621px;" unselectable="on"></DIV>'
    	sf.proxy = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(),p));
//    	sf.proxy.hide();
    	var xy = sf.wrap.getXY();
    	sf.proxy.setWidth(sf.wrap.getWidth());
    	sf.proxy.setHeight(sf.wrap.getHeight());
    	sf.proxy.setLocation(xy[0], xy[1]);
    },
    onCloseClick : function(e){
        e.stopEvent();
    	this.close(); 	
    },
    onCloseOver : function(e){
        this.closeBtn.addClass("win-btn-over");
    },
    onCloseOut : function(e){
    	this.closeBtn.removeClass("win-btn-over");
    },
    onCloseDown : function(e){
    	this.closeBtn.removeClass("win-btn-over");
    	this.closeBtn.addClass("win-btn-down");
        Ext.get(document.documentElement).on("mouseup", this.onCloseUp, this);
    },
    onCloseUp : function(e){
    	this.closeBtn.removeClass("win-btn-down");
    	Ext.get(document.documentElement).un("mouseup", this.onCloseUp, this);
    },
    close : function(){
    	if(this.fireEvent('beforeclose',this)){
	    	$A.WindowManager.remove(this);
	    	this.destroy(); 
	    	this.fireEvent('close', this);
    	}
    },
    destroy : function(){
//    	$A.focusWindow = null;
    	var wrap = this.wrap;
    	if(!wrap)return;
    	if(this.proxy) this.proxy.remove();
    	if(this.modal) $A.Cover.uncover(this.wrap);
        for(var key in this.cmps){
            var cmp = this.cmps[key];
            if(cmp.destroy){
                try{
                    cmp.destroy();
                }catch(e){
                    alert('销毁window出错: ' + e)
                }
            }
        }
    	$A.Window.superclass.destroy.call(this);
    	delete this.title;
    	delete this.head;
    	delete this.body;
        delete this.closeBtn;
        delete this.proxy;
        wrap.remove();
        this.shadow.remove();
//        var sf = this;
//        setTimeout(function(){
//        	for(var key in sf.cmps){
//        		var cmp = sf.cmps[key];
//        		if(cmp.destroy){
//        			try{
//        				cmp.destroy();
//        			}catch(e){
//        				alert('销毁window出错: ' + e)
//        			}
//        		}
//        	}
//        },10)
    },
    /**
     * 窗口加载.
     * 
     * @param {String} url  加载的url
     * @param {Object} params  加载的参数
     */
    load : function(url,params){
//    	var cmps = $A.CmpManager.getAll();
//    	for(var key in cmps){
//    		this.oldcmps[key] = cmps[key];
//    	}
        
    	Ext.Ajax.request({
			url: url,
			params:params||{},
		   	success: this.onLoad.createDelegate(this)
		});		
    },
    setChildzindex : function(z){
    	for(var key in this.cmps){
    		var c = this.cmps[key];
    		c.setZindex(z)
    	}
    },
    setWidth : function(w){
    	w=$A.getViewportWidth()-(Ext.isIE||!this.hasVScrollBar()?0:17)-(Ext.isIE8?1:0);
    	$A.Window.superclass.setWidth.call(this,w);
    	this.body.setWidth(w-2);
    	this.shadow.setWidth(this.wrap.getWidth());
    },
    setHeight : function(h){
    	h=$A.getViewportHeight()-(Ext.isIE||!this.hasHScrollBar()?26:43);
    	Ext.fly(this.body.dom.parentNode.parentNode).setHeight(h);
    	this.body.setHeight(h);
        this.shadow.setHeight(this.wrap.getHeight());
    	var sl = document[Ext.isStrict?'documentElement':'body'].scrollLeft;
    	var st = document[Ext.isStrict?'documentElement':'body'].scrollTop;
        this.shadow.moveTo(sl,st);
        this.wrap.moveTo(sl,st);
    },
    onLoad : function(response, options){
    	if(!this.body) return;
    	this.clearLoading();
    	var html = response.responseText;
    	var res
    	try {
            res = Ext.decode(response.responseText);
        }catch(e){}
        if(res && res.success == false){
        	if(res.error){
                if(res.error.code  && res.error.code == 'session_expired' || res.error.code == 'login_required'){
                    if($A.manager.fireEvent('timeout', $A.manager))
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
    	var sf = this
    	this.body.update(html,true,function(){
//	    	var cmps = $A.CmpManager.getAll();
//	    	for(var key in cmps){
//	    		if(sf.oldcmps[key]==null){	    			
//	    			sf.cmps[key] = cmps[key];
//	    		}
//	    	}
	    	sf.fireEvent('load',sf)
    	},this);
    }
});
/**
 * 
 * 显示提示信息窗口
 * 
 * @param {String} title 标题
 * @param {String} msg 内容
 * @param {Function} callback 回调函数
 * @param {int} width 宽度
 * @param {int} height 高度
 * @return {Window} 窗口对象
 */
$A.showMessage = function(title, msg,callback,width,height){
	return $A.showTypeMessage(title, msg, width||300, height||100,'win-info',callback);
}
/**
 * 显示带警告图标的窗口
 * 
 * @param {String} title 标题
 * @param {String} msg 内容
 * @param {Function} callback 回调函数
 * @param {int} width 宽度
 * @param {int} height 高度
 * @return {Window} 窗口对象
 */
$A.showWarningMessage = function(title, msg,callback,width,height){
	return $A.showTypeMessage(title, msg, width||300, height||100,'win-warning',callback);
}
/**
 * 显示带信息图标的窗口
 * 
 * @param {String} title 标题
 * @param {String} msg 内容
 * @param {Function} callback 回调函数
 * @param {int} width 宽度
 * @param {int} height 高度
 * @return {Window} 窗口对象
 */
$A.showInfoMessage = function(title, msg,callback,width,height){
	return $A.showTypeMessage(title, msg, width||300, height||100,'win-info',callback);
}
/**
 * 显示带错误图标的窗口
 * 
 * @param {String} title 标题
 * @param {String} msg 内容
 * @param {Function} callback 回调函数
 * @param {int} width 宽度
 * @param {int} height 高度
 * @return {Window} 窗口对象
 */
$A.showErrorMessage = function(title,msg,callback,width,height){
	return $A.showTypeMessage(title, msg, width||300, height||100,'win-error',callback);
}

$A.showTypeMessage = function(title, msg,width,height,css,callback){
	var msg = '<div class="win-icon '+css+'"><div class="win-type" style="width:'+(width-60)+'px;height:'+(height-62)+'px;">'+msg+'</div></div>';
	return $A.showOkWindow(title, msg, width, height,callback);	
} 
/**
 * 带图标的确定窗口.
 * 
 * @param {String} title 标题
 * @param {String} msg 内容
 * @param {Function} okfun 确定的callback
 * @param {Function} cancelfun 取消的callback
 * @param {int} width 宽度
 * @param {int} height 高度
 * @return {Window} 窗口对象
 */
$A.showConfirm = function(title, msg, okfun,cancelfun, width, height){
	width = width||300;
	height = height||100;
    var msg = '<div class="win-icon win-question"><div class="win-type" style="width:'+(width-60)+'px;height:'+(height-62)+'px;">'+msg+'</div></div>';
    return $A.showOkCancelWindow(title, msg, okfun,cancelfun, width, height);  	
}
//$A.hideWindow = function(){
//	var cmp = $A.CmpManager.get('aurora-msg')
//	if(cmp) cmp.close();
//}
//$A.showWindow = function(title, msg, width, height, cls){
//	cls = cls ||'';
//	var cmp = $A.CmpManager.get('aurora-msg')
//	if(cmp == null) {
//		cmp = new $A.Window({id:'aurora-msg',title:title, height:height,width:width});
//		if(msg){
//			cmp.body.update('<div class="'+cls+'" style="height:'+(height-68)+'px;">'+msg+'</div>');
//		}
//	}
//	return cmp;
//}
/**
 * 带确定取消按钮的窗口.
 * 
 * @param {String} title 标题
 * @param {String} msg 内容
 * @param {Function} okfun 确定的callback
 * @param {Function} cancelfun 取消的callback
 * @param {int} width 宽度
 * @param {int} height 高度
 * @return {Window} 窗口对象
 */
$A.showOkCancelWindow = function(title, msg, okfun,cancelfun,width, height){
    //var cmp = $A.CmpManager.get('aurora-msg-ok-cancel')
    //if(cmp == null) {
        var id = Ext.id(),okid = 'aurora-msg-ok'+id,cancelid = 'aurora-msg-cancel'+id,
        okbtnhtml = $A.Button.getTemplate(okid,_lang['window.button.ok']),
        cancelbtnhtml = $A.Button.getTemplate(cancelid,_lang['window.button.cancel']),
        cmp = new $A.Window({id:'aurora-msg-ok-cancel'+id,closeable:true,title:title, height:height||100,width:width||300});
        if(msg){
            cmp.body.update(msg+ '<center><table cellspacing="5"><tr><td>'+okbtnhtml+'</td><td>'+cancelbtnhtml+'</td><tr></table></center>',true,function(){
                var okbtn = $(okid);
                var cancelbtn = $(cancelid);
                cmp.cmps[okid] = okbtn;
                cmp.cmps[cancelid] = cancelbtn;
                okbtn.on('click',function(){
                	if(okfun && okfun.call(this,cmp) === false)return;
                	cmp.close();
                });
                cancelbtn.on('click',function(){
                	if(cancelfun && cancelfun.call(this,cmp) === false)return;
                	cmp.close();
                });
            });
        }
    //}
    return cmp;
}
/**
 * 带确定按钮的窗口.
 * 
 * @param {String} title 标题
 * @param {String} msg 内容
 * @param {Function} okfun 确定的callback
 * @param {Function} cancelfun 取消的callback
 * @param {int} width 宽度
 * @param {int} height 高度
 * @return {Window} 窗口对象
 */
$A.showOkWindow = function(title, msg, width, height,callback){
	//var cmp = $A.CmpManager.get('aurora-msg-ok');
	//if(cmp == null) {
		var id = Ext.id(),yesid = 'aurora-msg-yes'+id,
		btnhtml = $A.Button.getTemplate(yesid,_lang['window.button.ok']),
		cmp = new $A.Window({id:'aurora-msg-ok'+id,closeable:true,title:title, height:height,width:width});
		if(msg){
			cmp.body.update(msg+ '<center>'+btnhtml+'</center>',true,function(){
    			var btn = $(yesid);
                cmp.cmps[yesid] = btn;
                btn.on('click',function(){
                    if(callback && callback.call(this,cmp) === false)return;
                    cmp.close();
                });
                //btn.focus();
                btn.focus.defer(10,btn);
			});
		}
	//}
	return cmp;
}
/**
 * 上传附件窗口.
 * 
 * @param {String} path  当前的context路径
 * @param {String} title 上传窗口标题
 * @param {int} pkvalue  pkvalue
 * @param {String} source_type source_type
 * @param {int} max_size 最大上传大小(单位kb)  0表示不限制
 * @param {String} file_type 上传类型(*.doc,*.jpg)
 * @param {String} callback 回调函数的名字
 */
$A.showUploadWindow = function(path,title,source_type,pkvalue,max_size,file_type,callback){
    new Aurora.Window({id:'upload_window', url:path+'/upload.screen?callback='+callback+'&pkvalue='+pkvalue+'&source_type='+source_type+'&max_size='+(max_size||0)+'&file_type='+(file_type||'*.*'), title:title||_lang['window.upload.title'], height:330,width:595});
}
/**
 * @class Aurora.Lov
 * @extends Aurora.TextField
 * <p>Lov 值列表组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.Lov = Ext.extend($A.TextField,{
	selectedClass:'autocomplete-selected',
	viewClass:'item-comboBox-view',
    constructor: function(config) {
        this.isWinOpen = false;
        this.fetching = false;
        this.fetchremote = true;
        this.needFetch = true;
        this.autocompletesize = 2;
        this.autocompletedelay = 500;
        this.autocompletepagesize = 10;
        this.maxHeight = 240;
        this.context = config.context||'';
        $A.Lov.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
        $A.Lov.superclass.initComponent.call(this,config);
        this.para = {};
        if(!Ext.isEmpty(this.lovurl)){
            this.lovurl = this.processParmater(this.lovurl);
        }else if(!Ext.isEmpty(this.lovservice)){
            this.lovservice = this.processParmater(this.lovservice);           
        }else if(!Ext.isEmpty(this.lovmodel)){
            this.lovmodel = this.processParmater(this.lovmodel);
        }
        if(this.autocomplete = this.autocomplete == "true"){
        	if(!this.autocompletefield){
        		var maps = this.getMapping(),name = this.binder.name;
        		for(var i=0;i<maps.length;i++){
        			if(maps[i].to == name)this.autocompletefield = maps[i].from;
        		}
        	}
        	this.fetchremote = false;
        	this.autocompleteview = new $A.Popup({});
        	if(!this.optionDataSet)this.optionDataSet = new $A.DataSet({id:this.id+"_autocomplete_ds",autocount:false})
        }
        this.trigger = this.wrap.child('div[atype=triggerfield.trigger]');
    },
    processParmater:function(url){
        var li = url.indexOf('?')
        if(li!=-1){
            this.para = Ext.urlDecode(url.substring(li+1,url.length));
            return url.substring(0,li);
        } 
        return url;
    },
    processListener: function(ou){
        $A.Lov.superclass.processListener.call(this,ou);
        this.trigger[ou]('click',this.onTriggerClick, this, {preventDefault:true})
    },
    initEvents : function(){
        $A.Lov.superclass.initEvents.call(this);
        this.addEvents(
        /**
         * @event commit
         * commit事件.
         * @param {Aurora.Lov} lov 当前Lov组件.
         * @param {Aurora.Record} r1 当前lov绑定的Record
         * @param {Aurora.Record} r2 选中的Record. 
         */
        'commit',
        /**
         * @event beforetriggerclick
         * 点击弹出框按钮之前的事件。
         * @param {Aurora.Lov} lov 当前Lov组件.
         */
        'beforetriggerclick');
    },
    onTriggerClick : function(e){
    	e.stopEvent();
    	if(this.fireEvent('beforetriggerclick',this)){
    		this.showLovWindow();
    	}
    },
    destroy : function(){
    	if(this.qtId){
    		Ext.Ajax.abort(this.qtId);
    	}
    	if(this.optionDataSet){
    		this.optionDataSet.destroy();
    	}
        $A.Lov.superclass.destroy.call(this);
    },
    setWidth: function(w){
        this.wrap.setStyle("width",(w+3)+"px");
        this.el.setStyle("width",(w-20)+"px");
    },
    onChange : function(e){
    	if(this.fetchremote == true||(this.autocomplete&&this.needFetch))
			this.fetchRecord();
    },
    onKeyUp : function(e){
        this.fireEvent('keyup', this, e);
        if(this.autocomplete){
        	var v=this.getRawValue(),view=this.autocompleteview,code = e.keyCode;
        	//if((code > 47 && code < 58) || (code > 64 && code < 91) || code == 8 || code == 46 || code == 13 || code == 32 || code == 16 || code == 17){
	        if((code < 37 || code > 40)&&code != 13 && code !=27 && code != 9){
        		if(v.length >= this.autocompletesize){
	        		var sf=this;
	        		if(this.showCompleteId)clearTimeout(this.showCompleteId);
	        		this.showCompleteId=setTimeout(function(){
	        			var url;
                        var lp = Ext.urlEncode(sf.getLovPara())
			        	if(!Ext.isEmpty(sf.lovservice)){
//				            url = sf.context + 'sys_lov.svc?svc='+sf.lovservice +'&'+ Ext.urlEncode(sf.getLovPara());
                            url = sf.context + 'autocrud/'+sf.lovservice+'/query' + (!Ext.isEmpty(lp) ? '?' + lp : '');
				        }else if(!Ext.isEmpty(sf.lovmodel)){
				            url = sf.context + 'autocrud/'+sf.lovmodel+'/query' + (!Ext.isEmpty(lp) ? '?' + lp : '');
				        }
				        sf.optionDataSet.setQueryUrl(url);
				       	sf.pagesize=sf.autocompletepagesize;
	        			sf.optionDataSet.setQueryParameter(sf.autocompletefield,'%'+v.trim()+'%');
	        			view.show();
	        			sf.optionDataSet.query();
	        			delete sf.showCompleteId;
	        		},this.autocompletedelay);
	        	}else{
	        		if(this.showCompleteId){
	        			clearTimeout(this.showCompleteId);
	        			delete this.showCompleteId;
	        		}
	        		if(view.isShow){
	        			view.hide();
	        			view.on('show',this.autoCompleteShow,this);
	        		}
	        	}
        	}
        }
    },
    onKeyDown : function(e){
        if(this.isWinOpen)return;       
        var keyCode = e.keyCode;
        if(this.autocomplete && this.autocompleteview && this.autocompleteview.isShow){
            if(keyCode == 13 ) {
    	    	if(this.selectedIndex != null){
    	    		this.blur();
        			this.onSelect(this.selectedIndex);
    				this.autocompleteview.hide();
        			this.focus();
        		}else{
        			this.autocompleteview.hide();
    	    		var sf = this;
    	    		setTimeout(function(){
    	    			sf.fireEvent('enterdown', sf, e)
    	    		},5);
        		}
            }else if(keyCode == 27 || keyCode == 9){
            	this.autocompleteview.hide();
            	this.blur();
            }else if(this.optionDataSet.getAll().length > 0){
    	        if(keyCode == 38){
    	        	this.selectItem(this.selectedIndex == null ? -1 : this.selectedIndex - 1);
    	        }else if(keyCode == 40){
    	        	this.selectItem(this.selectedIndex == null ? 0 : this.selectedIndex + 1);
    	        }
            }
        }else{
            $A.Lov.superclass.onKeyDown.call(this,e);
        }
    },
    onFocus : function(e){
    	if(this.autocomplete){
    		this.autocompleteview.bind(this.optionDataSet,this);
    		//this.wrap.appendChild(this.autocompleteview.wrap);
    		this.autocompleteview.on('show',this.autoCompleteShow,this);
    	}
    	$A.Lov.superclass.onFocus.call(this,e);
    },
    onBlur : function(e){
    	if(this.autocomplete){
    		if(this.showCompleteId){
    			clearTimeout(this.showCompleteId);
    			delete this.showCompleteId;
    		}
    		(this.tempview||this.autocompleteview).un('show',this.autoCompleteShow,this);
    	}
    	$A.Lov.superclass.onBlur.call(this,e);
    },
    autoCompleteShow : function(){
    	this.autoCompletePosition();
    	var view = this.autocompleteview;
    	view.addClass(this.viewClass);
		view.update('');
		view.wrap.on('click', this.onViewClick,this);
    	view.on('beforerender',this.onQuery,this);
		view.on('render',this.onRender,this);
    	view.on('hide',this.autoCompleteHide,this);
    },
    autoCompleteHide : function(){
    	this.needFetch = true;
		Ext.Ajax.abort(this.optionDataSet.qtId);
    	var view = this.autocompleteview;
    	view.wrap.un('click', this.onViewClick,this);
		view.wrap.un('mousemove',this.onViewMove,this);
    	view.un('show',this.autoCompleteShow,this);
    	view.un('beforerender',this.onQuery,this);
    	view.un('render',this.onRender,this);
    	view.un('hide',this.autoCompleteHide,this);
    },
    autoCompletePosition:function(){
    	var xy = this.wrap.getXY(),
			W=this.autocompleteview.getWidth(),H=this.autocompleteview.getHeight(),
			PH=this.wrap.getHeight(),PW=this.wrap.getWidth(),
			BH=$A.getViewportHeight()-3,BW=$A.getViewportWidth()-3,
			x=(xy[0]+W)>BW?((BW-W)<0?xy[0]:(BW-W)):xy[0];
			y=(xy[1]+PH+H)>BH?((xy[1]-H)<0?(xy[1]+PH):(xy[1]-H)):(xy[1]+PH);
    	this.autocompleteview.moveTo(x,y);
    },
    onViewClick:function(e,t){
    	t = Ext.fly(t);
		t = (t.parent('TR')||t).dom;
		if(t.tagName!='TR'){
		    return;
		}		
		this.onSelect(t);
		this.autocompleteview.hide();
		this.focus();
	},	
	onViewMove:function(e,t){
		t = Ext.fly(t);
		t = t.parent('TR')||t;
        this.selectItem(t.dom.tabIndex);        
	},
	onSelect : function(target){
		var index = Ext.isNumber(target)?target:target.tabIndex;
		if(index<-1){
			if(!this.needFetch)this.fetchRecord();
			return;
		}
		var record = this.optionDataSet.getAt(index);
		this.commit(record);
	},
    onQuery : function(){
    	var view = this.autocompleteview;
    	view.update('<table cellspacing="0" cellpadding="2"><tr tabIndex="-2"><td>'+_lang['lov.query']+'</td></tr></table>');
    	view.wrap.un('mousemove',this.onViewMove,this);
    	this.correctViewSize();
    },
    onRender : function(){
    	var datas = this.optionDataSet.getAll();
		var l=datas.length,view = this.autocompleteview;
		var sb = ['<table class="autocomplete" cellspacing="0" cellpadding="2">'];
		this.selectedIndex = null;
		if(l==0){
			sb.add('<tr tabIndex="-2"><td>'+_lang['lov.notfound']+'</td></tr></table>');
			view.update(sb.join(''));
		}else{
			var displayFields = this.binder.ds.getField(this.binder.name).getPropertity('displayFields');
            if(displayFields && displayFields.length){
            	sb.add('<tr tabIndex="-2" class="autocomplete-head">');
            	for(var i = 0,ll = displayFields.length;i < ll;i++){
            		sb.add('<td>'+displayFields[i].prompt+'</td>');
            	}
				sb.add('</tr>');
            }
			for(var i=0;i<l;i++){
				var text = this.getRenderText(datas[i],displayFields);
				sb.add('<tr tabIndex="'+i+'"'+(i%2==1?' class="autocomplete-row-alt"':'')+'>'+text+'</tr>');	//this.litp.applyTemplate(d)等数据源明确以后再修改		
			}
			sb.add('</table>');
			view.update(sb.join(''));	
			view.wrap.on('mousemove',this.onViewMove,this);
			this.needFetch=false;
		}
		this.correctViewSize();
    },
    correctViewSize: function(){
		var widthArray = [],view = this.autocompleteview,table = view.wrap.child('table');
		if(table.getWidth() < 150)table.setWidth(150);
		var lh = Math.min(table.getHeight()+2,this.maxHeight); 
		view.setHeight(lh<20?20:lh);
		var mw = view.wrap.getWidth();
    	view.setWidth(mw);
		this.autoCompletePosition();
	},
    selectItem:function(index){
		if(Ext.isEmpty(index)||index < -1){
			return;
		}	
		var node = this.getNode(index);	
		if(node && node.tabIndex!=this.selectedIndex){
			if(!Ext.isEmpty(this.selectedIndex)){							
				Ext.fly(this.getNode(this.selectedIndex)).removeClass(this.selectedClass);
			}
			this.selectedIndex=node.tabIndex;			
			Ext.fly(node).addClass(this.selectedClass);					
		}			
	},
	getNode:function(index){
		var nodes = this.autocompleteview.wrap.select('tr[tabindex!=-2]').elements,l = nodes.length;
		if(index >= l) index =  index % l;
		else if (index < 0) index = l + index % l;
		return nodes[index];
	},
    getRenderText : function(record,displayFields){
        var rder = $A.getRenderer(this.autocompleterenderer);
        var text = '&#160;';
        if(rder){
            text = rder.call(window,this,record);
        }else{
        	if(displayFields){
            	text = '';
            	for(var i = 0,l = displayFields.length;i < l;i++){
            		var v = record.get(displayFields[i].name);
            		text += '<td>'+(Ext.isEmpty(v)?'&#160;':v)+'</td>';
            	}
            }else{
            	var v = record.get(this.autocompletefield);
            	text = '<td>'+(Ext.isEmpty(v)?'&#160;':v)+'</td>';
            }
        }
		return text;
	},
//  onKeyDown : function(e){
//        if(e.getKey() == 13) {
//          this.showLovWindow();
//        }else {
//          $A.TriggerField.superclass.onKeyDown.call(this,e);
//        }
//    },
    canHide : function(){
        return this.isWinOpen == false
    },
    commit:function(r,lr){
        if(this.win) this.win.close();
//        this.setRawValue('')
        var record = lr ? lr : this.record;
        if(record && r){
            var mapping = this.getMapping();
            for(var i=0;i<mapping.length;i++){
                var map = mapping[i], from = r.get(map.from);
                record.set(map.to,Ext.isEmpty(from)?'':from);
            }
        }
//        else{
//          this.setValue()
//        }
        
        this.fireEvent('commit', this, record, r)
    },
    getMapping: function(){
        var mapping
        if(this.record){
            var field = this.record.getMeta().getField(this.binder.name);
            if(field){
                mapping = field.get('mapping');
            }
        }
        return mapping ? mapping : [{from:this.binder.name,to:this.binder.name}];
    },
//  setValue: function(v, silent){
//      $A.Lov.superclass.setValue.call(this, v, silent);
//      if(this.record && this.dataRecord && silent !== true){
//          var mapping = this.getMapping();
//          for(var i=0;i<mapping.length;i++){
//              var map = mapping[i];
//              this.record.set(map.to,this.dataRecord.get(map.from));
//          }       
//      }
//  },
    onWinClose: function(){
        this.isWinOpen = false;
        this.win = null;
        if(!Ext.isIE6 && !Ext.isIE7){//TODO:不知什么地方会导致冲突,ie6 ie7 会死掉 
            this.focus();
        }else{
        	var sf = this;
        	setTimeout(function(){sf.focus()},10)	
        }
    },
    getLovPara : function(){
        var para = Ext.apply({},this.para);
        var field;
        if(this.record) field = this.record.getMeta().getField(this.binder.name);
        if(field){
            var lovpara = field.get('lovpara'); 
            if(lovpara)Ext.apply(para,lovpara);
        }
        return para;
    },
    fetchRecord : function(){
        if(this.readonly == true) return;
        this.fetching = true;
        var v = this.getRawValue(),url;
        
        if(!Ext.isEmpty(this.lovservice)){
//            url = this.context + 'sys_lov.svc?svc='+this.lovservice+'&pagesize=1&pagenum=1&_fetchall=false&_autocount=false&'+ Ext.urlEncode(this.getLovPara());
            url = this.context + 'autocrud/'+this.lovservice+'/query?pagenum=1&_fetchall=false&_autocount=false&'+ Ext.urlEncode(this.getLovPara());
        }else if(!Ext.isEmpty(this.lovmodel)){
            url = this.context + 'autocrud/'+this.lovmodel+'/query?pagenum=1&_fetchall=false&_autocount=false&'+ Ext.urlEncode(this.getLovPara());
        }
        var record = this.record;
        if(record == null && this.binder)
        	record = this.binder.ds.create({},false);
        record.isReady=false;
        var p = {};
        var mapping = this.getMapping();
        for(var i=0;i<mapping.length;i++){
            var map = mapping[i];           
            if(this.binder.name == map.to){
                p[map.from]=v;
            }
            record.set(map.to,'');          
        }
        $A.slideBarEnable = $A.SideBar.enable;
        $A.SideBar.enable = false;
        if(Ext.isEmpty(v) || !Ext.isEmpty(this.lovurl)) {
            this.fetching = false;
            record.isReady=true;
            $A.SideBar.enable = $A.slideBarEnable;
            return;
        }
        this.setRawValue(_lang['lov.query'])
        this.qtId = $A.request({url:url, para:p, success:function(res){
            var r = new $A.Record({});
            if(res.result.record){
                var datas = [].concat(res.result.record),l = datas.length;
                if(l>0){
                	if(this.fetchsingle && l>1){
                		var sb=['<table class="autocomplete" cellspacing="0" cellpadding="2">'],
                			displayFields = this.binder.ds.getField(this.binder.name).getPropertity('displayFields');
            			if(displayFields && displayFields.length){
            				sb.add('<tr tabIndex="-2" class="autocomplete-head">');
			            	for(var i = 0,ll = displayFields.length;i < ll;i++){
			            		sb.add('<td>'+displayFields[i].prompt+'</td>');
			            	}
							sb.add('</tr>');
            			}
            			for(var i=0;i<l;i++){
							var text = this.getRenderText(new $A.Record(datas[i]),displayFields);
							sb.add('<tr tabIndex="'+i+'"'+(i%2==1?' class="autocomplete-row-alt"':'')+'>'+text+'</tr>');	//this.litp.applyTemplate(d)等数据源明确以后再修改		
						}
						sb.add('</table>');
						var div = new Ext.Template('<div style="position:absolute;left:0;top:0">{sb}</div>').append(document.body,{'sb':sb.join('')},true),
                			cmp = new $A.Window({id:this.id+'_fetchmulti',closeable:true,title:'请选择', height:Math.max(div.getHeight(),300),width:Math.max(div.getWidth(),200)});
                		cmp.on('close',function(){
                			if(this.tempview){
	                			this.autocompleteview = this.tempview;
	                			delete this.tempview;
                			}else this.autocompleteview = null;
                		},this);
                		if(this.autocompleteview)this.tempview = this.autocompleteview;
            			this.autocompleteview = {};
                		(this.autocompleteview.wrap = cmp.body).update(sb.join(''));
                		div.remove();
                		cmp.body.child('table').setWidth('100%')
                		cmp.body.on('mousemove',this.onViewMove,this);
                		cmp.body.on('dblclick',function(e,t){
							t = Ext.fly(t).parent('TR');
							var index = t.dom.tabIndex;
							if(index<-1)return;
							var r2 = new $A.Record(datas[index]);
							this.commit(r2,record);
							cmp.close();
                		},this);
                	}else{
	                    var data = datas[0];
	                    r = new $A.Record(data);
                	}
                }
            }
            this.fetching = false;
            this.setRawValue('');
            this.commit(r,record);
            record.isReady=true;
            $A.SideBar.enable = $A.slideBarEnable;
        }, error:this.onFetchFailed, scope:this});
    },
    onFetchFailed: function(res){
        this.fetching = false;
        $A.SideBar.enable = $A.slideBarEnable;
    },    
//  onBlur : function(e){
////        if(this.isEventFromComponent(e.target)) return;
////        var sf = this;
////        setTimeout(function(){
////            if(!this.isWinOpen){
////            }
////        })
//      if(!this.fetching)
//        $A.Lov.superclass.onBlur.call(this,e);
//    },
    showLovWindow : function(){        
        if(this.fetching||this.isWinOpen||this.readonly) return;
        
        var v = this.getRawValue();
        this.blur();
        var url;
        var lp = Ext.urlEncode(this.getLovPara())
        if(!Ext.isEmpty(this.lovurl)){
            url = this.lovurl+'?' + Ext.urlEncode(this.getLovPara()) + '&';
        }else if(!Ext.isEmpty(this.lovservice)){
            
//            url = this.context + 'sys_lov.screen?url='+encodeURIComponent(this.context + 'sys_lov.svc?svc='+this.lovservice + '&'+ Ext.urlEncode(this.getLovPara()))+'&service='+this.lovservice+'&';
            url = this.context + 'sys_lov.screen?url='+encodeURIComponent(this.context + 'autocrud/'+this.lovservice+'/query'+ (!Ext.isEmpty(lp) ? '?' + lp : ''))+'&service='+this.lovservice+'&';
        }else if(!Ext.isEmpty(this.lovmodel)){
            url = this.context + 'sys_lov.screen?url='+encodeURIComponent(this.context + 'autocrud/'+this.lovmodel+'/query'+ (!Ext.isEmpty(lp) ? '?' + lp : ''))+'&service='+this.lovmodel+'&';
        }
        if(url) {
	        this.isWinOpen = true;
	        //alert(this.lovlabelwidth+' '+this.lovgridheight)
            this.win = new $A.Window({title:this.title||'Lov', url:url+"lovid="+this.id+"&key="+encodeURIComponent(v)+"&gridheight="+(this.lovgridheight||350)+"&innerwidth="+((this.lovwidth||400)-30)+"&lovautoquery="+this.lovautoquery+"&lovlabelwidth="+this.lovlabelwidth, height:this.lovheight||400,width:this.lovwidth||400});
            this.win.on('close',this.onWinClose,this);
        }
    }
});

$A.Popup = Ext.extend($A.Component,{
	constructor : function(config) {
		var id = 'aurora-item-popup',popup = $A.CmpManager.get(id);
		if(popup)return popup;
		config.id=id;
        $A.Popup.superclass.constructor.call(this, config);
    },
    initComponent : function(config){
        $A.Popup.superclass.initComponent.call(this,config);
    	this.wrap = new Ext.Template(this.tpl).insertFirst(document.body,{width:this.width,height:this.height},true);
    	this.shadow = new Ext.Template(this.shadowtpl).insertFirst(document.body,{width:this.width,height:this.height},true);
    },
    initEvents : function(){
        $A.Popup.superclass.initEvents.call(this);
        this.addEvents(
        	'show',
        	'hide',
        	'beforerender',
        	'render'
        );
    },
    processDataSet: function(ou){
		if(this.optionDataSet){
            this.optionDataSet[ou]('load', this.onDataSetLoad, this);
            this.optionDataSet[ou]('query', this.onDataSetQuery, this);
		}
	},
	
	onDataSetQuery : function(){
		this.fireEvent('beforerender',this)
	},
	onDataSetLoad : function(){
		this.fireEvent('render',this)
	},
	update : function(){
		this.wrap.update.apply(this.wrap,Ext.toArray(arguments));
	},
    show : function(){
    	if(!this.isShow){
    		this.isShow=true;
	    	this.fireEvent('show',this);
	    	this.wrap.show();
	    	this.shadow.show();
	    	Ext.get(document).on('mousedown',this.trigger,this);
    	}
    },
    trigger : function(e){
    	if(!this.wrap.contains(e.target) && !this.wrap.contains(e.target) &&(!this.owner||!this.owner.wrap.contains(e.target))){ 
    		this.hide();
    	}
    },
    hide : function(e){
    	if(this.isShow){
    		this.isShow=false;
	    	this.fireEvent('hide',this)
	    	Ext.get(document).un('mousedown',this.trigger,this)
	    	this.wrap.hide();
	    	this.shadow.hide();
    	}
    },
    moveTo : function(x,y){
    	this.wrap.moveTo(x,y);
    	this.shadow.moveTo(x+3,y+3);
    },
    setHeight : function(h){
    	this.wrap.setHeight(h);
    	this.shadow.setHeight(h);
    },
    setWidth : function(w){
    	//this.wrap.setWidth(w);
    	this.shadow.setWidth(w);
    },
    getHeight : function(){
    	return this.wrap.getHeight();
    },
    getWidth : function(){
    	return this.wrap.getWidth();
    },
    addClass : function(className){
		if(this.customClass == className)return;
    	if(this.customClass)this.wrap.removeClass(this.customClass);
    	this.customClass = className;
    	this.wrap.addClass(this.customClass);
    },
    bind : function(ds,cmp){
    	this.owner = cmp;
    	if(this.optionDataSet != ds){
    		this.processDataSet('un');
    		this.optionDataSet = ds;
			this.processDataSet('on');
    	}
    },
    destroy : function(){
    	$A.Popup.superclass.destroy.call(this);
    	this.processDataSet('un');
    	delete this.shadow;
    },
    tpl : ['<div tabIndex="-2" class="item-popup" style="visibility:hidden;background-color:#fff;">','</div>'],
    shadowtpl : ['<div class="item-shadow" style="visibility:hidden;">','</div>']
});
/**
 * @class Aurora.MultiLov
 * @extends Aurora.Lov
 * <p>MultiLov 值列表组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.MultiLov = Ext.extend($A.Lov,{
    constructor: function(config) {
    	this.quote="'";
    	this.localvalues=[];
        $A.MultiLov.superclass.constructor.call(this, config);        
    },
    processListener : function(ou){
        $A.MultiLov.superclass.processListener.call(this,ou);
        this.el[ou]('click',this.onClick, this)
    },
    initEvents : function(){
        $A.MultiLov.superclass.initEvents.call(this);
        /**
         * @event commit
         * commit事件.
         * @param {Aurora.Lov} lov 当前Lov组件.
         * @param {Aurora.Record} r 当前lov绑定的Record
         * @param {Aurora.Record} rs 选中的Record集合. 
         */
    },
    onChange : function(e){},
    onClick : function(e){
        var pos = this.getCursortPosition();
        var value = this.getRawValue();
        var strs = value.split(';')
        var start = 0;
        for(var i=0;i<strs.length;i++){
            var end = start + strs[i].length + 1;
            if(pos>start&&pos<end){
                if(this.start!=start||this.end!=end){
                	this.select(start,end);
                	if(Ext.isGecko||Ext.isOpera){
                		this.start=start;
                		this.end=end;
                	}
                }else this.start=this.end=0;
				break;
            }else{
                start +=strs[i].length + 1;
            }
        }
    },
    commit:function(ds,lr){
        if(this.win) this.win.close();
        var record = lr ? lr : this.record,
        	records=ds.getAll(),from="";
        if(record){
    		this.optionDataSet=ds;
	    	for(var j=0;j<records.length;j++){
	        	if(records[j].get(this.valuefield)){
	        		var v=records[j].get(this.valuefield);
	        		from+=this.quote+v+this.quote;
	        		if(j!=records.length-1)from+=","
	        	}
	        }
        	record.set(this.binder.name,from);
        }
        this.fireEvent('commit', this, record, records)
    },
    getCursortPosition : function() {
        var p = 0;
        if (document.selection) {
        	this.el.focus();
            var r = document.selection.createRange();
            r.moveStart('character', -this.el.dom.value.length);
            p = r.text.length;
        }else if (this.el.dom.selectionStart||this.el.dom.selectionStart=='0')
            p = this.el.dom.selectionStart;
        return p;
    },
    processValue : function(v){
    	this.localvalues=[];
    	if(!v)return '';
    	var values=v.split(';'),rv="",records=[];
    	for(var i=0;i<values.length;i++){
    		var vs=values[i].trim();
    		if(vs||vs=='0'){
    			var record=this.getRecordByDisplay(vs);
    			if(record){
    				vs=record.get(this.valuefield);
    				records.add(record);
    			}else{
    				this.localvalues.add(vs);
    			}
	    		rv+=this.quote+vs+this.quote+",";
    		}
    	}
    	if(this.optionDataSet){
	    	this.optionDataSet.removeAll();
    	}
    	for(var i=0;i<records.length;i++){
    		this.optionDataSet.add(records[i]);
    	}
    	return rv.match(/,$/)?rv.slice(0,rv.length-1):rv;
    },
    getRecordByDisplay: function(name){
    	if(!this.optionDataSet)return null;
    	var datas = this.optionDataSet.getAll();
		for(var i=0;i<datas.length;i++){
			var d = datas[i].get(this.displayfield);
			if(d == name){
				return datas[i];
			}
		}
		return null;
    },
    formatValue : function(v){
    	var rv="";
    	if(this.optionDataSet){
    		var datas=this.optionDataSet.getAll();
			for(var i=0;i<datas.length;i++){
				rv+=datas[i].get(this.displayfield)+";";
			}
    	}
		for(var i=0;i<this.localvalues.length;i++){
			rv+=this.localvalues[i]+";";
		}
    	return rv;
    },
    showLovWindow : function(){        
        if(this.fetching||this.isWinOpen||this.readonly) return;
        
        var v = this.getRawValue();
        this.blur();
        var url;
        if(!Ext.isEmpty(this.lovurl)){
            url = this.lovurl+'?' + Ext.urlEncode(this.getLovPara()) + '&';
        }else if(!Ext.isEmpty(this.lovservice)){
            url = this.context + 'sys_multiLov.screen?url='+encodeURIComponent(this.context + 'sys_lov.svc?svc='+this.lovservice + '&'+ Ext.urlEncode(this.getLovPara()))+'&service='+this.lovservice+'&';           
        }else if(!Ext.isEmpty(this.lovmodel)){
            url = this.context + 'sys_multiLov.screen?url='+encodeURIComponent(this.context + 'autocrud/'+this.lovmodel+'/query?'+ Ext.urlEncode(this.getLovPara()))+'&service='+this.lovmodel+'&';
        }
        if(url) {
	        this.isWinOpen = true;
            this.win = new $A.Window({title:this.title||'Lov', url:url+"lovid="+this.id+"&key="+encodeURIComponent(v)+"&gridheight="+(this.lovgridheight||350)+"&innerwidth="+((this.lovwidth||400)-30)+"&innergridwidth="+Math.round(((this.lovwidth||400)-90)/2)+"&lovautoquery="+this.lovautoquery+"&lovlabelwidth="+this.lovlabelwidth, height:this.lovheight||400,width:this.lovwidth||400});
            this.win.on('close',this.onWinClose,this);
        }
    },
    destroy : function(){
        $A.Lov.superclass.destroy.call(this);
    }
});
/**
 * @class Aurora.TextArea
 * @extends Aurora.Field
 * <p>TextArea组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.TextArea = Ext.extend($A.Field,{
	constructor: function(config) {
        $A.TextArea.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	$A.TextArea.superclass.initComponent.call(this, config); 		
    },
    initEvents : function(){
    	$A.TextArea.superclass.initEvents.call(this);    	
    },
    initElements : function(){
    	this.el= this.wrap;
    },
    onKeyDown : function(e){}
//    ,setRawValue : function(v){
//        this.el.update(v === null || v === undefined ? '' : v);
//    }
//    ,getRawValue : function(){
//        var v = this.el.dom.innerHTML;
//        if(v === this.emptytext || v === undefined){
//            v = '';
//        }
//        return v;
//    }
})
$A.Customization = Ext.extend(Ext.util.Observable,{
    constructor: function(config) {
        $A.Customization.superclass.constructor.call(this);
        this.id = config.id || Ext.id();
        $A.CmpManager.put(this.id,this)
        this.initConfig=config;
    },
    start : function(config){
        var sf = this;
        this.scanInterval = setInterval(function() {
            var cmps = $A.CmpManager.getAll();
            for(var key in cmps){
                var cmp = cmps[key];
                if(cmp.iscust == true){
                    cmp.on('mouseover',sf.onCmpOver,sf);
                }
            }
        }, 500);
    },
    mask : function(el){
        var w = el.getWidth();
        var h = el.getHeight();//leftp:0px;top:0px; 是否引起resize?
        var p = '<div title="点击设置个性化" style="border:2px solid #000;cursor:pointer;left:-1000px;top:-1000px;width:'+(w)+'px;height:'+(h)+'px;position: absolute;"><div style="width:100%;height:100%;filter: alpha(opacity=0);opacity: 0;mozopacity: 0;background-color:#ffffff;"> </div></div>';
        this.masker = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(),p));
        this.masker.setStyle('z-index', 10001);
        var xy = el.getXY();
        this.masker.setX(xy[0]-2);
        this.masker.setY(xy[1]-2);
        this.masker.on('click', this.onClick,this);
        this.cover.on('mouseover',this.onCmpOut,this);
    },
    onClick : function(){
        var path = window.location.pathname;
        var str = path.indexOf('modules');
        var screen_path = path.substring(str,path.length);
        var screen = screen_path.substring(screen_path.lastIndexOf('/')+1, screen_path.length);
        var parent = this.el.parent('.win-wrap')
        if(parent) {
            var url = parent.getAttributeNS("","url");
            if(url){
                url = url.split('?')[0];
                var li = url.lastIndexOf('/');
                if(li != -1){
                    url = url.substring(li+1,url.length);
                }
                screen_path = screen_path.replaceAll(screen, url);
            }
        }
        var context_path = path.substring(0,str);
        new Aurora.Window({id:'sys_customization_window', url:context_path + 'modules/sys/sys_customization_window.screen?screen_path='+screen_path + '&id='+ this.cmp.id, title:'个性化设置',height:170,width:400});
        this.onCmpOut();
    },
    hideMask : function(){
        if(this.masker){
            Ext.fly(this.masker).remove();   
            this.masker = null;
        }
    },
    showCover : function(){
        var scrollWidth = Ext.isStrict ? document.documentElement.scrollWidth : document.body.scrollWidth;
        var scrollHeight = Ext.isStrict ? document.documentElement.scrollHeight : document.body.scrollHeight;
        var screenWidth = Math.max(scrollWidth,Aurora.getViewportWidth());
        var screenHeight = Math.max(scrollHeight,Aurora.getViewportHeight());
        var st = (Ext.isIE6 ? 'position:absolute;width:'+(screenWidth-1)+'px;height:'+(screenHeight-1)+'px;':'')
//        var p = '<DIV class="aurora-cover" style="'+st+'" unselectable="on"></DIV>';
        var p = '<DIV class="aurora-cover" style="'+st+'filter: alpha(opacity=0);background-color: #fff;opacity: 0;mozopacity: 0;" unselectable="on"></DIV>';
        this.cover = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(),p));
        this.cover.setStyle('z-index', 9999);
    },
    hideCover : function(){
        if(this.cover){
            this.cover.un('mouseover',this.onCmpOut,this);
            Ext.fly(this.cover).remove();
            this.cover = null;
        }
    },
    getEl : function(cmp){
        var el;
        if(Aurora.Grid && cmp instanceof Aurora.Grid) {
            el = cmp.wb;       
        }else{
            el = cmp.wrap;
        }
        
        return el;
    },
    onCmpOver : function(cmp, e){
        if(this.isInSpotlight) return;
        this.isInSpotlight = true;
        this.showCover();
        this.cmp = cmp;
        this.el = this.getEl(cmp);
        if(this.el){
//            this.backgroundcolor = this.el.getStyle('background-color');
//            this.currentPosition = this.el.getStyle('position');
            this.currentZIndex = this.el.getStyle('z-index');
//            this.el.setStyle('background-color','#fff')
//            this.el.setStyle('position','relative');
            this.el.setStyle('z-index', 10000);
        }
        this.mask(this.el)
    },
    onCmpOut : function(e){
        this.isInSpotlight = false;
        if(this.el){
//            this.el.setStyle('position',this.currentPosition||'')
            this.el.setStyle('z-index', this.currentZIndex);
//            this.el.setStyle('background-color', this.backgroundcolor||'');
            this.el = null;
        }
        this.hideMask();
        this.hideCover();
        this.cmp = null;
    },
    stop : function(){
        if(this.scanInterval) clearInterval(this.scanInterval)
        this.onCmpOut();
        var cmps = $A.CmpManager.getAll();
        for(var key in cmps){
            var cmp = cmps[key];
            if(cmp.iscust == true){
                cmp.un('mouseover',this.onCmpOver,this);
            }
        }
    }
});
