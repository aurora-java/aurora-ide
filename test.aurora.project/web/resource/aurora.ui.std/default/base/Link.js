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