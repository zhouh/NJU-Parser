package edu.nju.nlp.solver;

public class QPParameters {

	static public enum QPSolverType {
		HILDRETH, HILDRETH_SEQ, HILDRETH_SEQ_FAST, HILDRETH_SEQ_ALG_B0, HILDRETH_SEQ_ALG_B00, HILDRETH_SEQ_ALG_B00_FULL, HILDRETH_SEQ_ALG_B00_BLAS, HILDRETH_SEQ_ALG_A, HILDRETH_SEQ_ALG_B
	};

	public int max_iter;
	public double eps;
	public double zero;
	public int timer_adjust;
	public int timer_div;
	public int timer_maxkkt;
	public QPSolverType qpsolver_type;
	public double nu;
	public double nu_mult;
	public double eps_mult;
	public double nu_min;

	public QPParameters() {
		max_iter = 15000;
		eps = 5e-4;
		zero = 0;// 0.0000000000000001;
		timer_adjust = 4;// 0
		timer_div = 2;
		timer_maxkkt = 1;// 0;
		nu = 1;
		qpsolver_type = QPSolverType.HILDRETH_SEQ_FAST;
		nu_mult = 0.95;
		eps_mult = 0.95;
		nu_min = 5e-3;
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("qpsolver_type: " + qpsolver_type + "\n");
		str.append("max_iter      : " + max_iter + "\n");
		str.append("eps           : " + eps + "\n");
		str.append("zero          : " + zero + "\n");
		str.append("timer_adjust  : " + timer_adjust + "\n");
		str.append("timer_div     : " + timer_div + "\n");
		str.append("timer_maxkkt  : " + timer_maxkkt + "\n");
		str.append("nu            : " + nu + "\n");
		str.append("nu_mult       : " + nu_mult + "\n");
		str.append("eps_mult      : " + eps_mult + "\n");
		str.append("nu_min        : " + nu_min + "\n");
		return (str.toString());
	}
};
