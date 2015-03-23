package aurora.ide.statistics.wizard.dialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.api.statistics.Statistician;
import aurora.ide.api.statistics.map.ObjectStatisticsResult;
import aurora.ide.api.statistics.map.StatisticsResult;

public class FirstSaveDataWizardPage extends WizardPage {

	private Text txtEclipseProjectName;
	private Text txtProjectName;
	private Text txtStorer;
	private Text dtStorerDate;
	private Combo txtRepositoryType;
	private Text txtRepositoryRevesion;
	private Text txtRepositoryPath;
	private Text txtDetailed;

	public FirstSaveDataWizardPage(String pageName) {
		super(pageName);
	}

	public FirstSaveDataWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public void init(Statistician statistician) {
		txtEclipseProjectName.setText(statistician.getProject().getEclipseProjectName());
		txtProjectName.setText(statistician.getProject().getProjectName());
		txtStorer.setText(statistician.getProject().getStorer());
		txtRepositoryType.setText(statistician.getProject().getRepositoryType().trim().toUpperCase());
		txtRepositoryRevesion.setText(statistician.getProject().getRepositoryRevision());
		txtRepositoryPath.setText(statistician.getProject().getRepositoryPath());
		StringBuffer detailed = new StringBuffer();
		StatisticsResult result = statistician.getResult();
		ObjectStatisticsResult objectResult = result.getBMStatisticsResult();
		if (null != objectResult) {
			detailed.append("bm：");
			detailed = fillDetailed(detailed, objectResult);
		}
		objectResult = result.getSreenStatisticsResult();
		if (null != objectResult) {
			detailed.append("\nscreen：");
			detailed = fillDetailed(detailed, objectResult);
		}
		objectResult = result.getSVCStatisticsResult();
		if (null != objectResult) {
			detailed.append("\nsvc：");
			detailed = fillDetailed(detailed, objectResult);
		}
		txtDetailed.setText(detailed.toString());
	}

	private StringBuffer fillDetailed(StringBuffer detailed, ObjectStatisticsResult objectResult) {
		detailed.append("\n文件大小：" + conversion(objectResult.getTotalFileSize()));
		detailed.append("\n文件最大值：" + conversion(objectResult.getMaxFileSize()));
		detailed.append("\n文件最小值：" + conversion(objectResult.getMinFileSize()));
		detailed.append("\n文件平均值：" + conversion(objectResult.getAverageFileSize()));
		detailed.append("\n脚本大小：" + conversion(objectResult.getTotalScriptSize()));
		detailed.append("\n脚本最大值：" + conversion(objectResult.getMaxScriptSize()));
		detailed.append("\n脚本最小值：" + conversion(objectResult.getMinScriptSize()));
		detailed.append("\n脚本平均值：" + conversion(objectResult.getAverageScriptSize()));
		detailed.append("\n标签数量：" + objectResult.getTotalTagCount());
		detailed.append("\n标签数量最大值：" + objectResult.getMaxTagCount());
		detailed.append("\n标签数量最小值：" + objectResult.getMinTagCount());
		detailed.append("\n标签数量平均值：" + objectResult.getAverageTagCount());
		return detailed;
	}

	public void createControl(Composite parent) {
		setTitle("保存数据");
		setDescription("将统计信息保存到数据库");
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// ---------------------eclipse project name--------------------------
		Label lblEclipseProjectName = new Label(container, SWT.NULL);
		lblEclipseProjectName.setText("eclipse工程名：");

		GridData gbEclipseProjectName = new GridData(GridData.FILL_HORIZONTAL);
		txtEclipseProjectName = new Text(container, SWT.NULL | SWT.READ_ONLY);
		txtEclipseProjectName.setLayoutData(gbEclipseProjectName);
		// ---------------------project name----------------------------------
		Label lblProjectName = new Label(container, SWT.NULL);
		lblProjectName.setText("项目名：");

		GridData gbProjectName = new GridData(GridData.FILL_HORIZONTAL);
		txtProjectName = new Text(container, SWT.BORDER);
		txtProjectName.setLayoutData(gbProjectName);
		txtProjectName.setFocus();
		// ---------------------repository type------------------------------
		Label lblRepositoryType = new Label(container, SWT.NULL);
		lblRepositoryType.setText("资源库类型：");

		GridData gdRepositoryType = new GridData(GridData.FILL_HORIZONTAL);
		txtRepositoryType = new Combo(container, SWT.BORDER);
		txtRepositoryType.setLayoutData(gdRepositoryType);
		txtRepositoryType.add("CVS");
		txtRepositoryType.add("SVN");
		// ---------------------repository revesion--------------------------
		Label lblRepositoryRevesion = new Label(container, SWT.NULL);
		lblRepositoryRevesion.setText("资源库版本：");

		GridData gdRepositoryRevesion = new GridData(GridData.FILL_HORIZONTAL);
		txtRepositoryRevesion = new Text(container, SWT.BORDER);
		txtRepositoryRevesion.setLayoutData(gdRepositoryRevesion);
		// ---------------------repository path------------------------------
		Label lblRepositoryPath = new Label(container, SWT.NULL);
		lblRepositoryPath.setText("资源库路径：");

		GridData gdRepositoryPath = new GridData(GridData.FILL_HORIZONTAL);
		txtRepositoryPath = new Text(container, SWT.BORDER);
		txtRepositoryPath.setLayoutData(gdRepositoryPath);
		// ---------------------storer---------------------------------------
		Label lblStorer = new Label(container, SWT.NULL);
		lblStorer.setText("保存人：");

		GridData gdStorer = new GridData(GridData.FILL_HORIZONTAL);
		txtStorer = new Text(container, SWT.BORDER);
		txtStorer.setLayoutData(gdStorer);
		// ---------------------store date-----------------------------------
		Label lblStorerDate = new Label(container, SWT.NULL);
		lblStorerDate.setText("保存时间：");

		GridData gdCalendar = new GridData(GridData.FILL_HORIZONTAL);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
		dtStorerDate = new Text(container, SWT.NULL | SWT.READ_ONLY);
		dtStorerDate.setText(sd.format(new Date()));
		dtStorerDate.setLayoutData(gdCalendar);
		// ----------------------------Detailed--------------------------------
		GridData gdLabelDetailed = new GridData();
		gdLabelDetailed.verticalAlignment = SWT.TOP;
		Label lblDetailed = new Label(container, SWT.NULL);
		lblDetailed.setText("详细信息：");
		lblDetailed.setLayoutData(gdLabelDetailed);

		GridData gdDetailed = new GridData(GridData.FILL_BOTH);
		gdDetailed.heightHint = 200;
		txtDetailed = new Text(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);
		txtDetailed.setLayoutData(gdDetailed);

		setControl(container);
	}

	private String conversion(int num) {
		String value = Integer.toString(num);
		DecimalFormat df = new DecimalFormat("0.00");
		double v = Double.parseDouble(value);
		if (value.length() > 3 && value.length() <= 6) {
			v /= 1024.0;
			return df.format(v) + " KB";
		} else if (value.length() > 6) {
			v /= (1024.0 * 1024.0);
			return df.format(v) + " MB";
		} else {
			return (int) v + " Byte";
		}
	}

	public String getProjectName() {
		return txtProjectName.getText().trim();
	}

	public String getStorer() {
		return txtStorer.getText().trim();
	}

	public String getRepositoryType() {
		return txtRepositoryType.getText().trim();
	}

	public String getRepositoryRevesion() {
		return txtRepositoryRevesion.getText().trim();
	}

	public String getRepositoryPath() {
		return txtRepositoryPath.getText().trim();
	}
}