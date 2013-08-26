package aurora.ide.meta.gef.editors.consultant.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.properties.ComponentFSDProperties;

public class FSDPropertyFactory implements ComponentFSDProperties {

	private static final IPropertyDescriptor PD_FSD_DESC = new TextAreaFSDPropertyDescriptor(
			FSD_DESC, "详细描述");

	private static final IPropertyDescriptor PD_FSD_PAGE_DESC = new TextAreaFSDPropertyDescriptor(
			FSD_PAGE_DESC, "页面描述");
	private static final IPropertyDescriptor PD_FSD_PAGE_NAME = new TextFSDPropertyDescriptor(
			FSD_PAGE_NAME, "页面名   ");
	private static final IPropertyDescriptor PD_FSD_LOGIC = new TextAreaFSDPropertyDescriptor(
			FSD_LOGIC, "逻辑      ");
	private static final IPropertyDescriptor PD_FSD_MEANING = new TextAreaFSDPropertyDescriptor(
			FSD_MEANING, "含义      ");
	private static final IPropertyDescriptor PD_FSD_DATA_FROM = new TextAreaFSDPropertyDescriptor(
			FSD_DATA_FROM, "数据来源");

	private static final IPropertyDescriptor[] input_pds = { PD_FSD_MEANING,
			PD_FSD_DATA_FROM, PD_FSD_LOGIC };
	private static final IPropertyDescriptor[] page_pds = { PD_FSD_PAGE_NAME,
			PD_FSD_PAGE_DESC };
	private static final IPropertyDescriptor[] container_pds = { PD_FSD_DESC };
	private static final IPropertyDescriptor[] button_pds = { PD_FSD_DESC };

	public IPropertyDescriptor[] createPropertyDescriptors(
			AuroraComponent component) {
		String componentType = component.getComponentType();
		if ("button".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return button_pds;
		}
		if ("checkbox".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("combobox".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}

		if ("datepicker".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("datetimepicker".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("fieldset".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return container_pds;
		}

		if ("form".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return container_pds;
		}
		if ("grid".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return container_pds;
		}
		if ("gridcolumn".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("hbox".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return container_pds;
		}
		if ("label".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("lov".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}

		if ("numberfield".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("tabPanel".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return NONE_PROPS;
		}
		if ("tab".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return NONE_PROPS;
		}
		if ("textfield".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("textarea".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("toolbar".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return NONE_PROPS;
		}
		if ("vbox".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return container_pds;
		}
		if ("screenbody".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return page_pds;
		}
		if ("formBody".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return NONE_PROPS;
		}
		if ("queryForm".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return container_pds;
		}
		if ("custom_icon".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		if ("radio_item".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return input_pds;
		}
		return NONE_PROPS;

	}

	private static final IPropertyDescriptor[] NONE_PROPS = new IPropertyDescriptor[0];

	public static IPropertyDescriptor[] mergePropertyDescriptor(
			IPropertyDescriptor[] pd1, IPropertyDescriptor[] pd2) {
		IPropertyDescriptor[] descs = new IPropertyDescriptor[pd1.length
				+ pd2.length];
		System.arraycopy(pd1, 0, descs, 0, pd1.length);
		System.arraycopy(pd2, 0, descs, pd1.length, pd2.length);
		return descs;
	}
}
