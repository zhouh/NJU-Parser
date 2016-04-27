package edu.nju.nlp.cw_library.types;

/**
 * A helper class for parameter optimization.
 * @author Mark Dredze
 *
 */
public class Parameters {
	public double Phi = 1;
	public boolean phi_opt = false;
	public double C = Double.POSITIVE_INFINITY;
	public boolean c_opt = false;
	public double phi_baseline;
	public double c_baseline;
	public int iters = 10;
	public boolean iters_opt = false;
	
	public boolean noOpt() {
		return !(phi_opt || c_opt || iters_opt); 
	}

	public String getParams() {
		return "Phi: " + Phi + ", C: " + C + ", iterations: " + iters;
	}

	public void clearCurrentOpt() {
		this.phi_opt = false;
		this.c_opt = false;
		this.iters_opt = false;
		
	}

	public void setCurrentOptToBaseline() {
		if (this.c_opt)
			this.C = this.c_baseline;
		if (this.phi_opt)
			this.Phi = this.phi_baseline;
	}

	public void setCurrentOptToValue(double value) {
		if (this.c_opt)
			this.C = value;
		if (this.phi_opt)
			this.Phi = value;
		
	}
}