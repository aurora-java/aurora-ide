package aurora.excel.model;

public class IDXLine {
	// （1）头文件。
	// I00006|A3411|c302|1201070|1|CNY0001|5|1|1|C5003912000016

	// 关键字代码 |表单代码|机构类代码|地区代码 |数据属性|币种 | 单位|业务数据标志|数值型类型|标准化机构编码
	// I00006 A3411 c302 1201070 1 CNY0001 5 1 1 C5003912000016

	// 字段1 关键字代码 6位
	private String key_word_code = "";
	// 字段2 表单代码 5位
	private String xls_code = "";
	// 字段3 机构类代码 4位
	private String org_code = "c302";
	// 字段4 地区代码 7位
	private String area_code = "1201070";
	// 字段5 数据属性 1位〔AB类表单为数据属性，对CD类表单，此处是列号〕
	private String data_type = "1";
	// 字段6 币种 7位
	private String currency_type = "CNY0001";
	// 字段7 单位 1位
	private String unit = "5";
	// 字段8 业务数据标志 1位
	private String b_data_flag = "1";
	// 字段9 数值型类型 1位（值为“1”）
	private String num_type = "1";
	// 字段10 标准化机构编码 14位
	private String std_org_code = "C5003912000016";

	public IDXLine(String key_word_code, String xls_code) {
		super();
		this.key_word_code = key_word_code;
		this.xls_code = xls_code;
	}

	public String toHeadString() {
		String[] ss = { key_word_code, xls_code, org_code, area_code,
				data_type, currency_type, unit, b_data_flag, num_type };
		StringBuilder sb = new StringBuilder();
		for (String s : ss) {
			sb.append(s);
			sb.append("|");
		}
		sb.append(std_org_code);
		return sb.toString();
	}

	public String getKey_word_code() {
		return key_word_code;
	}

	public void setKey_word_code(String key_word_code) {
		this.key_word_code = key_word_code;
	}

	public String getXls_code() {
		return xls_code;
	}

	public void setXls_code(String xls_code) {
		this.xls_code = xls_code;
	}

	public String getOrg_code() {
		return org_code;
	}

	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public String getCurrency_type() {
		return currency_type;
	}

	public void setCurrency_type(String currency_type) {
		this.currency_type = currency_type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getB_data_flag() {
		return b_data_flag;
	}

	public void setB_data_flag(String b_data_flag) {
		this.b_data_flag = b_data_flag;
	}

	public String getNum_type() {
		return num_type;
	}

	public void setNum_type(String num_type) {
		this.num_type = num_type;
	}

	public String getStd_org_code() {
		return std_org_code;
	}

	public void setStd_org_code(String std_org_code) {
		this.std_org_code = std_org_code;
	}

}
