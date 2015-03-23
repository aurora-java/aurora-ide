package aurora.ide.search.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import uncertain.composite.QualifiedName;
import aurora.ide.search.reference.BMFieldReferenceQuery;
import aurora.ide.search.reference.FileReferenceQuery;
import aurora.ide.search.reference.ScreenDSReferenceQuery;

public class SearchQueryFactory {

	public static AbstractSearchQuery createSearchQuery(
			QualifiedName referenceTypeQName, IResource scope,
			IFile sourceFile, String fieldName) {

		if (AbstractSearchService.foreignFieldReference
				.equals(referenceTypeQName)) {
			return createBMFieldReferenceQuery(scope, sourceFile, fieldName);
		}
		if (AbstractSearchService.localFieldReference
				.equals(referenceTypeQName)) {
			return createBMFieldReferenceQuery(scope, sourceFile, fieldName);
		}
		if (AbstractSearchService.bmReference.equals(referenceTypeQName)) {
			return createBMReferenceQuery(scope, sourceFile, fieldName);
		}
		if (AbstractSearchService.screenReference.equals(referenceTypeQName)) {
			return createBMReferenceQuery(scope, sourceFile, fieldName);
		}
		if (AbstractSearchService.datasetReference.equals(referenceTypeQName)) {
			return createDSReferenceQuery(scope, sourceFile, fieldName);
		}
		return null;
	}

	private static AbstractSearchQuery createDSReferenceQuery(IResource scope,
			IFile sourceFile, String fieldName) {
		return new ScreenDSReferenceQuery(scope, sourceFile, fieldName);
	}

	private static AbstractSearchQuery createBMReferenceQuery(IResource scope,
			IFile sourceFile, String fieldName) {
		return new FileReferenceQuery(scope, sourceFile);
	}

	private static AbstractSearchQuery createBMFieldReferenceQuery(
			IResource scope, IFile sourceFile, String fieldName) {
		return new BMFieldReferenceQuery(scope, sourceFile, fieldName);
	}
}
