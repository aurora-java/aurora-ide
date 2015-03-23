package aurora.ide.api.composite.map;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.FileUtil;

public class CommentReader {

	private String comment;

	public CommentReader(String comment) {
		this.comment = comment;
	}

	public Comment read() {
		Comment c = new Comment();
		if (comment == null) {
			return c;
		}
		try {
			List<String> sl = FileUtil
					.readStringFileToList(new ByteArrayInputStream(comment
							.getBytes("utf-8")));
			for (String s : sl) {
				parse(s, c);
			}
		} catch (UnsupportedEncodingException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		}
		return c;
	}

	private void parse(String c, Comment cc) {
		c = c.trim();
		int begin = c.indexOf("$");
		if (begin == 0) {
			int center = c.indexOf(":");
			if (center != -1) {
				String key = c.substring(begin + 1, center);
				String value = c.substring(center + 1);
				if (!isBlank(key) && !isBlank(value)) {
					cc.put(key.trim().toLowerCase(), value.trim());
				}
			}
		}
	}

	private boolean isBlank(String s) {
		return s == null || "".equals(s.trim());
	}

}
