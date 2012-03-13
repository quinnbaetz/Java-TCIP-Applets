package edu.uiuc.power.dataobjects.lp;

import edu.uiuc.power.dataobjects.GeneratorData;
import edu.utoronto.power.lpsolver.LPException;
import edu.utoronto.power.lpsolver.LPVariable;

public class LPVariableGen extends LPVariable {

	public GeneratorData gen;
	
	public LPVariableGen(String VariableName, double LeftBoundary,
			double RightBoundary, GeneratorData gen) {
		super(VariableName, LeftBoundary, RightBoundary);
		this.gen = gen;
	}

	@Override
	public void SetValue(double Value, boolean BasisVariable)
			throws LPException {
		// TODO Auto-generated method stub
		super.SetValue(Value, BasisVariable);
	}

}
