package aurora.ide.meta.gef.editors.components.command;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.ImageData;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.plugin.source.gen.screen.model.CustomICon;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class ChangeImageCommand extends Command {

	private ImageData loadImageData;
	private CustomICon model;

	private int oldHeight = -1;
	private int oldWidth = -1;
	private byte[] oldIconByteData;
	private int iconType;

	public ChangeImageCommand(CustomICon model, ImageData loadImageData,
			int iconType) {
		this.model = model;
		this.loadImageData = loadImageData;
		this.iconType = iconType;
		this.setLabel("Change Icon");
	}

	@Override
	public void execute() {
		oldWidth = model
				.getIntegerPropertyValue(ComponentInnerProperties.IMAGE_WIDTH);
		oldHeight = model
				.getIntegerPropertyValue(ComponentInnerProperties.IMAGE_HEIGHT);
		oldIconByteData = AuroraImagesUtils.toBytes(model.getIconByteData());
		model.setPropertyValue(ComponentInnerProperties.IMAGE_WIDTH,
				loadImageData.width);
		model.setPropertyValue(ComponentInnerProperties.IMAGE_HEIGHT,
				loadImageData.height);
		byte[] bytes = AuroraImagesUtils.toBytes(loadImageData, iconType);
		model.setIconByteData(AuroraImagesUtils.toString(bytes));
	}

	@Override
	public void undo() {
		model.setPropertyValue(ComponentInnerProperties.IMAGE_WIDTH, oldWidth);
		model.setPropertyValue(ComponentInnerProperties.IMAGE_HEIGHT, oldHeight);
		model.setIconByteData(AuroraImagesUtils.toString(oldIconByteData));
	}

}
