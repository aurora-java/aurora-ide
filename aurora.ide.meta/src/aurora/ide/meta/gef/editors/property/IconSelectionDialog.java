package aurora.ide.meta.gef.editors.property;

import java.io.FileNotFoundException;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.DialogEditableObject;
import aurora.plugin.source.gen.screen.model.IDialogEditableObject;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class IconSelectionDialog extends EditWizard {

	public IconSelectionDialog() {
		super();
		setWindowTitle("ICON"); //$NON-NLS-1$
	}

	private DialogEditableObject obj;

	public void addPages() {
		addPage(new InnerPage("LocalIconSelection")); //$NON-NLS-1$
	}

	@Override
	public void setDialogEdiableObject(IDialogEditableObject obj) {
		if (obj instanceof DialogEditableObject) {
			if (ComponentInnerProperties.ICON_BYTES_DATA.equals(((DialogEditableObject) obj)
					.getPropertyId()))
				this.obj = (DialogEditableObject) obj;
		}
	}

	private String getIconData() {
		if (obj instanceof DialogEditableObject) {
			if (ComponentInnerProperties.ICON_BYTES_DATA.equals(obj
					.getPropertyId())) {
				Object data = ((DialogEditableObject) obj).getData();
				return data == null ? "" : data.toString();
			}
		}
		return "";
	}

	@Override
	public boolean performFinish() {
//		if (obj instanceof DialogEditableObject) {
//			AuroraComponent contextInfo = (AuroraComponent) obj
//					.getContextInfo();
//			contextInfo.setPropertyValue(
//					ComponentInnerProperties.ICON_BYTES_DATA_DEO, obj);
//		}
		return true;
	}

	private class InnerPage extends WizardPage {

		private Text t;

		protected InnerPage(String pageName) {
			super(pageName);
			setTitle("ICON");
			this.setMessage("选择一个16*16大小的图片");
		}

		public void createControl(Composite parent) {
			Composite root = new Composite(parent, SWT.NONE);
			root.setLayout(new GridLayout(4, false));
			Label l = new Label(root, SWT.NONE);
			l.setText("ICON:");
			t = new Text(root, SWT.BORDER | SWT.READ_ONLY);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Button b1 = new Button(root, SWT.NONE);
			b1.setText("浏览");
			b1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					queryIcon();
				}
			});
			Button b2 = new Button(root, SWT.NONE);
			b2.setText("清除");
			b2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					clear();
				}
			});
			setControl(root);
			refreshText();
		}

		private void refreshText() {
			String iconData = getIconData();
			t.setText("".equals(iconData) == false ? "已设置" : "未设置");
		}

		private void queryIcon() {
			String path = AuroraImagesUtils.queryFile(this.getShell());
			if (path != null) {
				try {
					Path p = new Path(path);
					String fileExtension = p.getFileExtension();
					int iconType = AuroraImagesUtils.getIconType(fileExtension);
					if (iconType == -1)
						return;
					ImageData loadImageData = AuroraImagesUtils
							.loadImageData(p);
					byte[] bytes = AuroraImagesUtils.toBytes(loadImageData,
							iconType);
					if (obj instanceof DialogEditableObject) {
						obj.setData(AuroraImagesUtils.toString(bytes));
						obj.setDescripition("Y");
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			refreshText();
		}

		private void clear() {
			if (obj instanceof DialogEditableObject) {
				obj.setData("");
				obj.setDescripition("N");
			}
			refreshText();
		}
	}

}
