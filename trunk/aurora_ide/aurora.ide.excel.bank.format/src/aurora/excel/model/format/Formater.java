package aurora.excel.model.format;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import uncertain.composite.CompositeMap;

public class Formater {
	public static final String KEY = "I";
	// （1）头文件。
	// I00006|A3411|c302|1201070|1|CNY0001|5|1|1|D3002412000012

	// 关键字代码 |表单代码|机构类代码|地区代码 |数据属性|币种 | 单位|业务数据标志|数值型类型|标准化机构编码
	// I00006 A3411 c302 1201070 1 CNY0001 5 1 1 D3002412000012
	// static final public String key_word_code;
	// 字段2 表单代码 5位
	// static final public String xls_code;
	// 字段3 机构类代码 4位
	static final public String org_code = "g305";
	// 字段4 地区代码 7位
	static final public String area_code = "1200000,1201000,1201070";
	static final public String default_area_code = "1200000";

	// 字段5 数据属性 1位〔AB类表单为数据属性，对CD类表单，此处是列号〕
	static final public String data_type = "1";
	// 字段6 币种 7位
	static final public String currency_type = "CNY0001";
	// 字段7 单位 1位
	static final public String unit = "5";
	// 字段8 业务数据标志 1位
	static final public String b_data_flag = "1";
	// 字段9 数值型类型 1位（值为“1”）
	static final public String num_type = "1";
	// 字段10 标准化机构编码 14位
	static final public String std_org_code = "D3002412000012";

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

	public static String getXLS_code(String filePath) {

		try {
			ExcelReader reader = new ExcelReader();
			InputStream is2 = new FileInputStream(filePath);
			String readExcelFirstSheetName = reader
					.readExcelFirstSheetName(is2);
			return readExcelFirstSheetName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String[] getArea_codes(CompositeMap idxMap) {
		String area_code = idxMap.getChild("area_code").getString("value",
				Formater.area_code);
		String[] a_codes = area_code.split(",");
		return a_codes;
	}

}
