package aurora.excel.model.files;

import java.util.Date;

import aurora.excel.model.format.runner.Messages;

public class TXT {
	// [1200000 天津港财务有限公司]
	// [1201000 天津港财务天津市市辖]
	// [1201070 天津港财务天津市塘沽区]

	static public String content() {
		StringBuilder sb = new StringBuilder();
		String date = new java.text.SimpleDateFormat(Messages.Runner_8)
				.format(new Date());
		sb.append(date);
		sb.append(Messages.Runner_9);
		sb.append("\r\n"); //$NON-NLS-1$
		sb.append(Messages.TXT_1);
		sb.append("\r\n"); //$NON-NLS-1$
		sb.append(Messages.TXT_3);
		sb.append("\r\n"); //$NON-NLS-1$
		sb.append(Messages.TXT_5);
		sb.append("\r\n"); //$NON-NLS-1$
		return sb.toString();
	}

}
