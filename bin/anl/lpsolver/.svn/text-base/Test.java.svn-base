package anl.lpsolver;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		revisedSimplex LP = new revisedSimplex(2, 2);
		float[] coefficients = new float[2];
		coefficients[0] = -200;
		coefficients[1] = -250;
		LP.specifyObjective(coefficients, true); // true means minimize
		LP.addConstraint(new float[]{2.0f,2.0f}, 10, revisedSimplex.LessThan);
		LP.addConstraint(new float[]{3.0f,5.0f}, 16, revisedSimplex.LessThan);
		LP.preprocess(2, 2);
		LP.solveLP();
		LP.showInfo();
	}
}
