package aurora.excel.model.format.runner;

public class OutputFileSetting {
	// 1 代号（金融统计监测管理信息系统-数值型统计指标数据－B）
	// 2 标志位（头文件：I；数据文件：J；数据说明文件：D）
	// 3—6 机构类代码
	// 7—13 地区代码
	// 14—21 年（4位），月（2位，01、02…11、12月），日（2位）
	// 22 频度
	// 23 批次
	// 24 顺序号（文件名的顺序码没有特别的含义，主要为区分多次报送而设置，也可以在数据修改阶段，用于对不同时间报送的数据进行区分）
	private String code;
	private String org_code;
	private String area_code;
	private String date;
	private String rate;
	private String batch;
	private String order;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	
}
