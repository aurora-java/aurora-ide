package aurora.excel.model;

public class DATLine {
	// （2）数据文件。
	// I00002|12D26|494056.36
	// 关键字代码|指标代码|数据值
	// 对于不同业务类数据的报送，使用相同的文件格式。
	// 字段1 关键字代码 6位，与头文件关键字代码对应
	private String key_word_code = "";
	// 字段2 指标代码 5位〔对CD类表单，此处是行号〕
	private String target_code = "";
	// 字段3 数据值 可识别小数点和负号
	private String data = "";

	public String getKey_word_code() {
		return key_word_code;
	}

	public void setKey_word_code(String key_word_code) {
		this.key_word_code = key_word_code;
	}

	public String getTarget_code() {
		return target_code;
	}

	public void setTarget_code(String target_code) {
		this.target_code = target_code;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public DATLine(String key_word_code, String target_code, String data) {
		super();
		this.key_word_code = key_word_code;
		this.target_code = target_code;
		this.data = data;
	}

	public String toDATString() {
		return key_word_code + "|" + target_code + "|" + data;
	}

}
