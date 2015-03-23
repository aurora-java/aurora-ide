package aurora.ide.builder.validator;

import org.eclipse.core.resources.IFile;

import aurora.ide.builder.processor.AbstractProcessor;
import aurora.ide.builder.processor.BmProcessor;
import aurora.ide.builder.processor.SxsdProcessor;
import aurora.ide.builder.processor.UncertainNsProcessor;

public class SvcValidator extends AbstractValidator {
	private AbstractProcessor[] aps = new AbstractProcessor[] {
			new BmProcessor(), new SxsdProcessor(), new UncertainNsProcessor() };

	public SvcValidator(IFile file) {
		super(file);
	}

	public SvcValidator() {
	}

	@Override
	public AbstractProcessor[] getMapProcessor() {
		return aps;
	}
}
