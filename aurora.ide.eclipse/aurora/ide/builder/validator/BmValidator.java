package aurora.ide.builder.validator;

import org.eclipse.core.resources.IFile;

import aurora.ide.builder.processor.AbstractProcessor;
import aurora.ide.builder.processor.BmProcessor;
import aurora.ide.builder.processor.ForeignFieldProcessor;
import aurora.ide.builder.processor.LocalFieldProcessor;
import aurora.ide.builder.processor.NamespaceProcessor;
import aurora.ide.builder.processor.SxsdProcessor;

public class BmValidator extends AbstractValidator {
	private AbstractProcessor[] aps = new AbstractProcessor[] {
			new BmProcessor(), new ForeignFieldProcessor(),
			new LocalFieldProcessor(), new NamespaceProcessor(),
			new SxsdProcessor() };

	public BmValidator(IFile file) {
		super(file);
	}

	public BmValidator() {
	}

	@Override
	public AbstractProcessor[] getMapProcessor() {
		return aps;
	}

}
