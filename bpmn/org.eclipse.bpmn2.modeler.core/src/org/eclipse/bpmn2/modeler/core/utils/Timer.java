/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.core.utils;

import java.util.Stack;

/**
 *
 */
public class Timer {

	class Interval {
		String msg;
		long t1, t2;
		
		Interval(String msg) {
			this.msg = msg;
			System.err.println("Start: "+msg);
			t1 = System.currentTimeMillis();
		}
		
		void stop() {
			t2 = System.currentTimeMillis();
			System.err.println("Stop:  "+msg+" " + (double)(t2-t1)/1000.0+" sec");
		}
	}
	
	private Stack<Interval> stack = new Stack<Interval>();
	
	public static Timer INSTANCE = new Timer();
	
	public void mark(String msg) {
		stop();
		stack.push(new Interval(msg));
	}
	
	public void start(String msg) {
		stack.push(new Interval(msg));
	}
	
	public void stop() {
		if (!stack.isEmpty()) {
			Interval i = stack.pop();
			i.stop();
		}
	}
	
	public void stopAll() {
		while (!stack.isEmpty()) {
			stop();
		}
	}
}
