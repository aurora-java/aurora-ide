/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ext.org.eclipse.jdt.internal.ui.text.correction;

import java.util.Collection;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IProblemLocation;



import ext.org.eclipse.jdt.internal.corext.dom.ASTNodes;
import ext.org.eclipse.jdt.internal.ui.JavaPluginImages;
import ext.org.eclipse.jdt.internal.ui.text.correction.proposals.ASTRewriteCorrectionProposal;


public class TypeArgumentMismatchSubProcessor {

//	public static void getTypeParameterMismatchProposals(IInvocationContext context, IProblemLocation problem, Collection proposals) {
//	CompilationUnit astRoot= context.getASTRoot();
//	ASTNode selectedNode= problem.getCoveredNode(astRoot);
//	if (!(selectedNode instanceof SimpleName)) {
//	return;
//	}

//	ASTNode normalizedNode= ASTNodes.getNormalizedNode(selectedNode);
//	if (!(normalizedNode instanceof ParameterizedType)) {
//	return;
//	}
//	// waiting for result of https://bugs.eclipse.org/bugs/show_bug.cgi?id=81544


//	}

	public static void removeMismatchedArguments(IInvocationContext context, IProblemLocation problem, Collection<ICommandAccess> proposals){
		ICompilationUnit cu= context.getCompilationUnit();
		ASTNode selectedNode= problem.getCoveredNode(context.getASTRoot());
		if (!(selectedNode instanceof SimpleName)) {
			return;
		}

		ASTNode normalizedNode=ASTNodes.getNormalizedNode(selectedNode);
		if (normalizedNode instanceof ParameterizedType) {
			ASTRewrite rewrite = ASTRewrite.create(normalizedNode.getAST());
			ParameterizedType pt = (ParameterizedType) normalizedNode;
			ASTNode mt = rewrite.createMoveTarget(pt.getType());
			rewrite.replace(pt, mt, null);
			String label= CorrectionMessages.TypeArgumentMismatchSubProcessor_removeTypeArguments;
			Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
			ASTRewriteCorrectionProposal proposal= new ASTRewriteCorrectionProposal(label, cu, rewrite, 6, image);
			proposals.add(proposal);
		}
	}

}
