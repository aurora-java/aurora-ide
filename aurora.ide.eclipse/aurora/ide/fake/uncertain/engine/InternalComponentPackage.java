package aurora.ide.fake.uncertain.engine;

import java.io.IOException;

import aurora.ide.helpers.DialogUtil;

import uncertain.pkg.ComponentPackage;

public class InternalComponentPackage extends ComponentPackage {

	@Override
	protected void initPackage() throws IOException {
		try {
			super.initPackage();
		} catch (IOException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		}
	}

}
