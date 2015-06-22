package edu.mit.cci.roma.pangaea.corenew.processors.outputs;

import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.core.VensimHelper;

/**
 * Processor that fetches values from stack and applies math operator to them (+, -, *, /)
 * @author janusz
 *
 */
public class ArithmeticOperationOutputProcessor implements OutputProcessor {
	private final static String OP_SUM = "+";
	private final static String OP_SUB = "-";
	private final static String OP_MUL = "*";
	private final static String OP_DIV = "/";
	
	private float constant;
	private boolean hasConstant;
	private boolean isSum;
	private boolean isSub;
	private boolean isMul;
	private boolean isDiv;
	private boolean isConstantLeft;
	

	@Override
	public void init(String configuration) {
		String opStr = configuration;
		if (configuration.contains(";")) {
			// there is a constant 
			String[] operatorAndConstant = configuration.split(";");

			try {
				opStr = operatorAndConstant[0];
				constant = Float.parseFloat(operatorAndConstant[1]);
			}
			catch (NumberFormatException e) {
				// constant on the left?
				try {
					opStr = operatorAndConstant[1];
					constant = Float.parseFloat(operatorAndConstant[0]);
					isConstantLeft = true;
				}
				catch (NumberFormatException ex2) {
					throw new RuntimeException("Unknown arithmetic operation configuration: " + configuration);
				}
			}
			
			hasConstant = true;
			
		}
		if (OP_SUM.equals(opStr)) {
			isSum = true;
		}
		else if (OP_SUB.equals(opStr)) {
			isSub = true;
		}
		else if (OP_MUL.equals(opStr)) {
			isMul = true;
		}
		else if (OP_DIV.equals(opStr)) {
			isDiv = true;
		}
		else {
			throw new RuntimeException("Unknown operator configuration: " + configuration);
		}

	}

	@Override
	public void processOutputs(VensimHelper vensimRun, VensimHelper baselineVensimRun, Stack<Pair<float[], float[]>> executionStack) 
			throws VensimException {
		Pair<float[], float[]> left = executionStack.pop();
		if (hasConstant) {
			
			for (int i=0; i < left.getRight().length; i++) {
				if (isSum) left.getRight()[i] += constant;
				if (isMul) left.getRight()[i] *= constant;
				if (isConstantLeft) {
					if (isSub) left.getRight()[i] = constant - left.getRight()[i];
					if (isDiv && left.getRight()[i] != 0) left.getRight()[i] = constant / left.getRight()[i];
				}
				else {
					if (isSub) left.getRight()[i] -= constant;
					if (isDiv) left.getRight()[i] /= constant;
				}
			}
		}
		else {
			Pair<float[], float[]> right = executionStack.pop();
			
			for (int i=0; i < left.getRight().length; i++) {
				if (left.getLeft()[i] != right.getLeft()[i]) {
					throw new VensimException("Indexes on variables don't match: " + left.getLeft()[i] + " != " + right.getLeft()[i]); 
				}

				if (isSum) left.getRight()[i] += right.getRight()[i];
				if (isSub) left.getRight()[i] -= right.getRight()[i];
				if (isMul) left.getRight()[i] *= right.getRight()[i];
				if (isDiv && right.getRight()[i] != 0) left.getRight()[i] /= right.getRight()[i];
			}
		}
		executionStack.push(left);
	}
	

}
