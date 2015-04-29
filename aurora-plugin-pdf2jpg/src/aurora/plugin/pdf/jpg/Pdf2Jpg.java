package aurora.plugin.pdf.jpg;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class Pdf2Jpg {
	public static List<String> setup(String pdfPath, String savePath,
			String jpgName) throws IOException {

		List<String> jpgFiles = new ArrayList<String>();
		// load a pdf from a byte buffer
		// File file = new
		// File("/Users/shiliyan/Desktop/pdf2jpg/Aurora Quick UI.pdf");
		File file = new File(pdfPath);
		// String name = file.getName();
		@SuppressWarnings("resource")
		FileChannel channel = new RandomAccessFile(file, "r").getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
				channel.size());
		PDFFile pdffile = new PDFFile(buf);

		for (int i = 1; i <= pdffile.getNumPages(); i++) {
			// draw the first page to an image
			PDFPage page = pdffile.getPage(i);

			// get the width and height for the doc at the default zoom
			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
					.getWidth(), (int) page.getBBox().getHeight());

			// generate the image
			Image img = page.getImage(rect.width, rect.height, // width &
																// height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true // block until drawing is done
					);

			BufferedImage tag = new BufferedImage(rect.width, rect.height,
					BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(img, 0, 0, rect.width, rect.height,
					null);
			String path = savePath + "/" + jpgName + "_" + i + ".jpg";

			FileOutputStream out = new FileOutputStream(path); // 输出到文件流
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(tag); // JPEG编码
			out.close();
			jpgFiles.add(path);

		}
		return jpgFiles;

		// show the image in a frame
		// JFrame frame = new JFrame("PDF Test");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.add(new JLabel(new ImageIcon(img)));
		// frame.pack();
		// frame.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
