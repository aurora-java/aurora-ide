/** Holds SAP function parameter config
 *  Created on 2006-6-14
 */
package aurora.plugin.sap;

import uncertain.composite.CompositeMap;

public class Parameter {
    public String   Name;
    public String   Source_field;
    public String   Value;
    public String   Return_field;
    public boolean  Nullable = true;
    
    public CompositeMap toCompositeMap(){
        CompositeMap param = new CompositeMap("jco","aurora.plugin.sap","parameter");
        param.put("name", Name);
        param.put("source_field", Source_field);
        param.put("value", Value);
        param.put("return_field", Return_field);
        param.put("nullable", new Boolean(Nullable));
        return param;
    }
    
    public String toString(){
        StringBuffer msg = new StringBuffer();
        msg.append(Name).append("=").append(Source_field==null?Value:Source_field);
        return msg.toString();
    }
    
    public Parameter(){
        
    }

    /**
     * @param name
     * @param value
     * @param return_field
     */
    public Parameter(String name, String value, String return_field) {
        super();
        Name = name;
        Value = value;
        Return_field = return_field;
    }
    

}
