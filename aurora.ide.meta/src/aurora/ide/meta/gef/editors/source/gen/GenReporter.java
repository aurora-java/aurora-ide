package aurora.ide.meta.gef.editors.source.gen;

public interface GenReporter {
	void genBegin();

	String getStatus();

	void genFinish();
}
