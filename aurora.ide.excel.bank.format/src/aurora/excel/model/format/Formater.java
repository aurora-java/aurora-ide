package aurora.excel.model.format;

public class Formater {
	// （1）头文件。
	// I00006|A3411|c302|1201070|1|CNY0001|5|1|1|C5003912000016
	
//	关键字代码	|表单代码|机构类代码|地区代码	|数据属性|币种   | 单位|业务数据标志|数值型类型|标准化机构编码
	//I00006 A3411   c302    1201070    1   CNY0001 5       1         1     C5003912000016

	
	
	// 字段1 关键字代码 6位
	private String key_word_code;
	// 字段2 表单代码 5位
	private String xls_code;
	// 字段3 机构类代码 4位
	private String org_code;
	// 字段4 地区代码 7位
	private String area_code;
	// 字段5 数据属性 1位〔AB类表单为数据属性，对CD类表单，此处是列号〕
	private String data_type;
	// 字段6 币种 7位
	private String currency_type;
	// 字段7 单位 1位
	private String unit;
	// 字段8 业务数据标志 1位
	private String b_data_flag;
	// 字段9 数值型类型 1位（值为“1”）
	private String num_type;
	// 字段10 标准化机构编码 14位
	private String std_org_code;

	// （2）数据文件。
	// I00002|12D26|494056.36
	// 关键字代码|指标代码|数据值
	// 对于不同业务类数据的报送，使用相同的文件格式。
	// 字段1 关键字代码 6位，与头文件关键字代码对应
	private String key_word_code_;
	// 字段2 指标代码 5位〔对CD类表单，此处是行号〕
	private String target_code;
	// 字段3 数据值 可识别小数点和负号
	private String data;

}
