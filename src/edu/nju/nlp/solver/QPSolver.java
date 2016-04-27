package edu.nju.nlp.solver;

import edu.nju.nlp.online.types.FeatureVector;
import gnu.trove.map.hash.TIntIntHashMap;

public class QPSolver {

	static final int MAX_ITER = 10000;
	static final double EPS = 1e-8;
	static final double ZERO = 0.0000000000000001;

	private QPParameters qp_params;

	public QPSolver() {
		qp_params = new QPParameters();
	}

	public QPSolver(QPParameters pr) {
		qp_params = pr;
	}

	public double[] solve(QPConstraints constraints) {

		switch (qp_params.qpsolver_type) {
		case HILDRETH_SEQ_FAST:
			return hildreth_sequential_fast(constraints);
		case HILDRETH_SEQ_ALG_B0:
			return hildreth_sequential_alg_b0(constraints);
		case HILDRETH_SEQ_ALG_B00:
			return hildreth_sequential_alg_b00(constraints);
		case HILDRETH_SEQ_ALG_B00_BLAS:
			return hildreth_sequential_alg_b00_blas(constraints);
		case HILDRETH_SEQ_ALG_B:
			return hildreth_sequential_alg_b(constraints);
		case HILDRETH_SEQ_ALG_A:
			return hildreth_sequential_alg_a(constraints);
		default:
			System.out.println("No such qp type : " + qp_params.qpsolver_type);
		}

		return null;

	}

	public QPOutput solve_full(QPConstraints constraints) {

		switch (qp_params.qpsolver_type) {
		case HILDRETH_SEQ_ALG_B00_FULL:
			return hildreth_sequential_alg_b00_full(constraints);
		default:
			System.out.println("No such full qp type : "
					+ qp_params.qpsolver_type);
		}

		return null;

	}

	public double[] hildreth_sequential_fast(QPConstraints constraints) {

		int i;
		FeatureVector[] a = constraints.a;
		double b[] = constraints.b;
		int dim = constraints.dim;

		System.out.println("In hildreth sequential fast");

		double[] alpha = new double[b.length];
		double[] w = new double[dim];

		int K = a.length;

		double[] nrm = new double[K];
		for (i = 0; i < K; i++) {
			nrm[i] = FeatureVector.dotProduct(a[i], a[i]);
		}

		int[] timer = new int[K];
		int timer_adjust = qp_params.timer_adjust;

		int iter = 0;
		double diff_alpha;
		double try_alpha;
		double add_alpha;
		double kkt;
		int max_kkt_i = 0;
		double inner;
		int no_skip_constraints;

		double max_kkt = qp_params.eps + 1;
		boolean cont = true;
		boolean is_timed;
		while (cont) {

			is_timed = false;
			max_kkt = Double.NEGATIVE_INFINITY;
			no_skip_constraints = 0;
			for (int ind = 0; ind < K; ind++) {

				if (timer[ind] > 0) {
					timer[ind]--;
					is_timed = true;
					continue;
				}

				no_skip_constraints++;
				inner = a[ind].dotProdoct(w);
				kkt = b[ind] - inner;
				if (alpha[ind] > 0)
					kkt = Math.abs(kkt);

				if (kkt > max_kkt) {
					max_kkt = kkt;
					max_kkt_i = ind;
				}

				// update
				if (kkt > qp_params.zero) {
					diff_alpha = (b[ind] - inner) / nrm[ind];
					if (diff_alpha < -alpha[ind])
						diff_alpha = -alpha[ind];

					alpha[ind] += diff_alpha;
					if (Math.abs(diff_alpha) > qp_params.zero) {
						for (FeatureVector curr = a[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w[curr.index] += diff_alpha * curr.value;
						}
					}

				} else
					timer[ind] = qp_params.timer_adjust;
			}
			// System.out.println("max_kkt_i = " + max_kkt_i + " max_kkt = " +
			// max_kkt + " iter = " + iter + " no_skip_constraints = " +
			// no_skip_constraints);
			timer[max_kkt_i] = 1;
			iter++;

			if (iter >= qp_params.max_iter)
				cont = false;
			else if (max_kkt < qp_params.eps) {
				if (!is_timed)
					cont = false;
				else {
					System.out
							.println("QPSolver : readjusting timer_adjust from "
									+ timer_adjust);
					timer_adjust /= qp_params.timer_div;
					for (int p = 0; p < K; p++)
						timer[p] = 0;
					System.out.println("QPSolver : readjusted timer_adjust to "
							+ timer_adjust);

				}
			}

		} // while

		if (iter >= qp_params.max_iter)
			System.out.println("QPSolver : iter >= MAX_ITER");
		return alpha;
	}

	public double[] hildreth_sequential_alg_b0(QPConstraints cns) {

		int i;
		System.out.println("In hildreth sequential alg b0");
		System.out.println(cns.toString());

		int K = cns.b.length;
		double[] alpha = new double[cns.b.length];
		double[] w1 = new double[cns.dim1];
		double[] w2 = new double[cns.dim2];

		double[] nrm1 = new double[K];
		double[] nrm2 = new double[K];
		for (i = 0; i < K; i++) {
			nrm1[i] = FeatureVector.dotProduct(cns.a1[i], cns.a1[i]);
			nrm2[i] = FeatureVector.dotProduct(cns.a2[i], cns.a2[i]);
		}

		int[] timer = new int[K];
		int timer_adjust = qp_params.timer_adjust;

		int iter = 0;
		double diff_alpha;
		double try_alpha;
		double add_alpha;
		double kkt;
		int max_kkt_i = 0;
		double inner1, inner2;

		double max_kkt = qp_params.eps + 1;
		boolean cont = true;
		boolean is_timed;
		double invnu = 1 / qp_params.nu;
		int no_skip_constraints;

		while (cont) {

			is_timed = false;
			max_kkt = Double.NEGATIVE_INFINITY;
			no_skip_constraints = 0;
			for (int ind = 0; ind < K; ind++) {

				if (timer[ind] > 0) {
					timer[ind]--;
					is_timed = true;
					continue;
				}

				no_skip_constraints++;
				inner1 = cns.a1[ind].dotProdoct(w1);
				inner2 = cns.a2[ind].dotProdoct(w2);
				kkt = cns.b[ind] - inner1 - inner2;
				if (alpha[ind] == cns.C)
					kkt = -kkt;
				else if (alpha[ind] > 0)
					kkt = Math.abs(kkt);

				if (kkt > max_kkt) {
					max_kkt = kkt;
					max_kkt_i = ind;
				}

				// update
				if (kkt > qp_params.zero) {
					diff_alpha = (cns.b[ind] - inner1 - inner2)
							/ (nrm1[ind] + invnu * nrm2[ind]);
					if (diff_alpha < -alpha[ind])
						diff_alpha = -alpha[ind];

					alpha[ind] += diff_alpha;
					if (alpha[ind] > cns.C) {
						diff_alpha = -diff_alpha + cns.C - alpha[ind];
						alpha[ind] = cns.C;
					}

					if (Math.abs(diff_alpha) > qp_params.zero) {
						for (FeatureVector curr = cns.a1[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w1[curr.index] += diff_alpha * curr.value;
						}
						for (FeatureVector curr = cns.a2[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w2[curr.index] += diff_alpha * curr.value * invnu;
						}

					}

				} else
					timer[ind] = qp_params.timer_adjust;
			}
			// System.out.println("max_kkt_i = " + max_kkt_i + " max_kkt = "
			// + max_kkt + " iter = " + iter + " no_skip_constraints = "
			// + no_skip_constraints);
			timer[max_kkt_i] = 1;
			iter++;

			if (iter >= qp_params.max_iter)
				cont = false;
			else if (max_kkt < qp_params.eps) {
				if (!is_timed)
					cont = false;
				else {
					System.out
							.println("QPSolver : readjusting timer_adjust from "
									+ timer_adjust);
					timer_adjust /= qp_params.timer_div;
					for (int p = 0; p < K; p++)
						timer[p] = 0;
					System.out.println("QPSolver : readjusted timer_adjust to "
							+ timer_adjust);

				}
			}

		} // while

		if (iter >= qp_params.max_iter)
			System.out.println("QPSolver : iter >= MAX_ITER");
		return alpha;
	}

	public double[] hildreth_sequential_alg_b00(QPConstraints cns) {

		QPOutput qp_output = hildreth_sequential_alg_b00_full(cns);
		return qp_output.alpha;
	}

	public QPOutput hildreth_sequential_alg_b00_full(QPConstraints cns) {

		int i;
		// System.out.println("In hildreth sequential alg b00 full");
		System.out.println(cns.toString());

		int K = cns.b.length;

		double[] alpha = null;// = new double[cns.b.length];
		double[] w1 = null;// = new double[cns.dim1];
		double[] w2 = null;// = new double[cns.dim2];

		if (cns.alpha != null)
			alpha = cns.alpha;
		else
			alpha = new double[cns.b.length];

		if (cns.w1 != null)
			w1 = cns.w1;
		else {
			w1 = new double[cns.dim1];
		}

		if (cns.w2 != null)
			w2 = cns.w2;
		else
			w2 = new double[cns.dim2];

		double[] nrm1 = new double[K];
		double[] nrm2 = new double[K];
		double min_nrm1 = Double.POSITIVE_INFINITY;
		double min_nrm2 = Double.POSITIVE_INFINITY;

		for (i = 0; i < K; i++) {
			nrm1[i] = FeatureVector.dotProduct(cns.a1[i], cns.a1[i]);
			if ((nrm1[i] <= 0) || (nrm1[i] == Double.POSITIVE_INFINITY))
				System.out.println("ERROR: nrm1[" + i + "]=" + nrm1[i]);
			else {
				if (nrm1[i] < min_nrm1)
					min_nrm1 = nrm1[i];
			}

			nrm2[i] = FeatureVector.dotProduct(cns.a2[i], cns.a2[i]);
			if ((nrm2[i] <= 0) || (nrm2[i] == Double.POSITIVE_INFINITY))
				System.out.println("ERROR: nrm2[" + i + "]=" + nrm2[i]);
			else {
				if (nrm2[i] < min_nrm2)
					min_nrm2 = nrm2[i];
			}

		}

		System.out.println(" min_nrm1 = " + min_nrm1 + " min_nrm2 = "
				+ min_nrm2);

		int[] timer = new int[K];
		int timer_adjust = qp_params.timer_adjust;

		int iter = 0;
		double diff_alpha;
		double try_alpha;
		double add_alpha;
		double kkt;
		int max_kkt_i = 0;
		double inner1, inner2;

		double kkt_f;
		double max_kkt_f = Double.POSITIVE_INFINITY;
		;
		double max_kkt_f_i = -1;

		double max_kkt = Double.POSITIVE_INFINITY;
		boolean cont = true;
		boolean is_timed;
		double nu = qp_params.nu;
		double invnu = 1 / nu;
		int no_skip_constraints = -1;
		double eps = 1e-1;

		while (cont) {

			is_timed = false;
			max_kkt = Double.NEGATIVE_INFINITY;
			no_skip_constraints = 0;

			for (int ind = 0; ind < K; ind++) {

				if (timer[ind] > 0) {
					timer[ind]--;
					is_timed = true;
					continue;
				}

				no_skip_constraints++;
				inner1 = cns.a1[ind].dotProdoct(w1);
				inner2 = cns.a2[ind].dotProdoct(w2);

				kkt = cns.b[ind] - inner1 - inner2;
				if (alpha[ind] == cns.C)
					kkt = -kkt;
				else if (alpha[ind] > 0)
					kkt = Math.abs(kkt);

				if (kkt == Double.POSITIVE_INFINITY) {
					System.out.println(" ERROR: kkt= " + kkt + " ind= " + ind
							+ " timer[ind]= " + timer[ind] + " alpha[ind]= "
							+ alpha[ind] + " b[ind]= " + cns.b[ind]
							+ " inner1= " + inner1 + " inner2= " + inner2);

				}

				if (kkt > max_kkt) {
					max_kkt = kkt;
					max_kkt_i = ind;
				}

				// update
				if (kkt > qp_params.zero) {
					diff_alpha = (cns.b[ind] - inner1 - inner2)
							/ (nrm1[ind] + invnu * nrm2[ind]);
					if (diff_alpha < -alpha[ind])
						diff_alpha = -alpha[ind];

					alpha[ind] += diff_alpha;
					if (alpha[ind] > cns.C) {
						diff_alpha = -diff_alpha + cns.C - alpha[ind];
						alpha[ind] = cns.C;
					}

					if (Math.abs(diff_alpha) == Double.POSITIVE_INFINITY)
						System.out.println("ERROR: diff_alpha= " + diff_alpha
								+ " ind= " + ind + " alpha[ind]=" + alpha[ind]);

					if (Math.abs(diff_alpha) > qp_params.zero) {
						for (FeatureVector curr = cns.a1[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w1[curr.index] += diff_alpha * curr.value;
							if (w1[curr.index] == Double.POSITIVE_INFINITY)
								System.out.println("ERROR: w1[" + curr.index
										+ "]=" + w1[curr.index] + " ind= "
										+ ind);
						}
						for (FeatureVector curr = cns.a2[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w2[curr.index] += diff_alpha * curr.value * invnu;
							if (w2[curr.index] == Double.POSITIVE_INFINITY)
								System.out.println("ERROR: w2[" + curr.index
										+ "]=" + w2[curr.index] + " ind= "
										+ ind);
						}

					}

				} else
					timer[ind] = qp_params.timer_adjust;
			}

			timer[max_kkt_i] = 1;

			// other kkt condition
			max_kkt_f = Double.NEGATIVE_INFINITY;
			max_kkt_f_i = -1;
			for (i = 0; i < cns.dim2; i++) {
				kkt_f = Math.abs(w2[i] * nu);

				if (kkt_f == Double.POSITIVE_INFINITY) {
					System.out.println(" ERROR: kkt_f= " + kkt_f + " i= " + i);
				}
				if (kkt_f > max_kkt_f) {
					max_kkt_f = kkt_f;
					max_kkt_f_i = i;
				}
			}

			// System.out.println("max_kkt_i = " + max_kkt_i +
			// " max_kkt = " + max_kkt +
			// " iter = " + iter +
			// " no_skip_constraints = " + no_skip_constraints +
			// " max_kkt_f_i = " + max_kkt_f_i +
			// " max_kkt_f = " + max_kkt_f +
			// " iter = " + iter +
			// " nu= " + nu +
			// " invnu= " + invnu +
			// " eps = " + eps);

			iter++;

			if (iter >= qp_params.max_iter)
				cont = false;
			else if (max_kkt < eps) {
				if (!is_timed) {
					if (eps <= qp_params.eps)
						cont = false;
					else {
						eps = eps * qp_params.eps_mult;
						if (eps < qp_params.eps)
							eps = qp_params.eps;
						// System.out.println("QPSolver : readjusting timer_adjust from "
						// + timer_adjust);
						timer_adjust = qp_params.timer_adjust;
						// System.out.println("QPSolver : readjusted timer_adjust to "
						// + timer_adjust);
						nu *= qp_params.nu_mult;
						if (nu < qp_params.nu_min)
							nu = qp_params.nu_min;
						invnu = 1 / nu;
					}
				} else {
					// System.out.println("QPSolver : readjusting timer_adjust from "
					// + timer_adjust);
					timer_adjust /= qp_params.timer_div;
					for (int p = 0; p < K; p++)
						timer[p] = 0;
					// System.out.println("QPSolver : readjusted timer_adjust to "
					// + timer_adjust);

				}
			}

		} // while
		System.out.println("max_kkt_i = " + max_kkt_i + " max_kkt = " + max_kkt
				+ " iter = " + iter + " no_skip_constraints = "
				+ no_skip_constraints + " max_kkt_f_i = " + max_kkt_f_i
				+ " max_kkt_f = " + max_kkt_f + " iter = " + iter + " nu= "
				+ nu + " invnu= " + invnu + " eps = " + eps);

		if (iter >= qp_params.max_iter)
			System.out.println("QPSolver : iter >= MAX_ITER");

		QPOutput qp_output = new QPOutput();
		qp_output.alpha = alpha;
		qp_output.w1 = w1;
		qp_output.w2 = w2;

		return qp_output;
	}

	public double[] hildreth_sequential_alg_b00_blas(QPConstraints cns) {

		int i;
		System.out.println("In hildreth sequential alg b00 blas");
		System.out.println(cns.toString());

		int K = cns.b.length;
		double[] alpha = new double[cns.b.length];
		double[] w1 = new double[cns.dim1];
		double[] w2 = new double[cns.dim2];

		double[] nrm1 = new double[K];
		double[] nrm2 = new double[K];
		for (i = 0; i < K; i++) {
			nrm1[i] = FeatureVector.dotProduct(cns.a1[i], cns.a1[i]);
			nrm2[i] = FeatureVector.dotProduct(cns.a2[i], cns.a2[i]);
		}

		int[] timer = new int[K];
		int timer_adjust = qp_params.timer_adjust;

		int iter = 0;
		double diff_alpha;
		double try_alpha;
		double add_alpha;
		double kkt;
		int max_kkt_i = 0;
		double inner1, inner2;

		double kkt_f;
		double max_kkt_f;
		double max_kkt_f_i;

		double max_kkt = Double.POSITIVE_INFINITY;
		boolean cont = true;
		boolean is_timed;
		double nu = qp_params.nu;
		double invnu = 1 / nu;
		int no_skip_constraints;
		double eps = 1e-1;

		while (cont) {

			is_timed = false;
			max_kkt = Double.NEGATIVE_INFINITY;
			no_skip_constraints = 0;
			for (int ind = 0; ind < K; ind++) {

				if (timer[ind] > 0) {
					timer[ind]--;
					is_timed = true;
					continue;
				}

				no_skip_constraints++;
				inner1 = blas_mult(cns.a1[ind], w1);
				inner2 = cns.a2[ind].dotProdoct(w2);
				kkt = cns.b[ind] - inner1 - inner2;
				if (alpha[ind] == cns.C)
					kkt = -kkt;
				else if (alpha[ind] > 0)
					kkt = Math.abs(kkt);

				if (kkt > max_kkt) {
					max_kkt = kkt;
					max_kkt_i = ind;
				}

				// update
				if (kkt > qp_params.zero) {
					diff_alpha = (cns.b[ind] - inner1 - inner2)
							/ (nrm1[ind] + invnu * nrm2[ind]);
					if (diff_alpha < -alpha[ind])
						diff_alpha = -alpha[ind];

					alpha[ind] += diff_alpha;
					if (alpha[ind] > cns.C) {
						diff_alpha = -diff_alpha + cns.C - alpha[ind];
						alpha[ind] = cns.C;
					}

					if (Math.abs(diff_alpha) > qp_params.zero) {
						for (FeatureVector curr = cns.a1[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w1[curr.index] += diff_alpha * curr.value;
						}
						for (FeatureVector curr = cns.a2[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w2[curr.index] += diff_alpha * curr.value * invnu;
						}

					}

				} else
					timer[ind] = qp_params.timer_adjust;
			}

			timer[max_kkt_i] = 1;

			// other kkt condition
			max_kkt_f = Double.NEGATIVE_INFINITY;
			max_kkt_f_i = -1;
			for (i = 0; i < cns.dim2; i++) {
				kkt_f = Math.abs(w2[i] * nu);
				if (kkt_f > max_kkt_f) {
					max_kkt_f = kkt_f;
					max_kkt_f_i = i;
				}
			}
			System.out.println("max_kkt_i = " + max_kkt_i + " max_kkt = "
					+ max_kkt + " iter = " + iter + " no_skip_constraints = "
					+ no_skip_constraints + " max_kkt_f_i = " + max_kkt_f_i
					+ " max_kkt_f = " + max_kkt_f + " iter = " + iter + " nu= "
					+ nu + " eps = " + eps);

			iter++;

			if (iter >= qp_params.max_iter)
				cont = false;
			else if (max_kkt < eps) {
				if (!is_timed) {
					if (eps <= qp_params.eps)
						cont = false;
					else {
						eps = eps * qp_params.eps_mult;
						if (eps < qp_params.eps)
							eps = qp_params.eps;
						System.out
								.println("QPSolver : readjusting timer_adjust from "
										+ timer_adjust);
						timer_adjust = qp_params.timer_adjust;
						System.out
								.println("QPSolver : readjusted timer_adjust to "
										+ timer_adjust);
						nu *= qp_params.nu_mult;
						if (nu < qp_params.nu_min)
							nu = qp_params.nu_min;
						invnu = 1 / nu;
					}
				} else {
					System.out
							.println("QPSolver : readjusting timer_adjust from "
									+ timer_adjust);
					timer_adjust /= qp_params.timer_div;
					for (int p = 0; p < K; p++)
						timer[p] = 0;
					System.out.println("QPSolver : readjusted timer_adjust to "
							+ timer_adjust);

				}
			}

		} // while

		if (iter >= qp_params.max_iter)
			System.out.println("QPSolver : iter >= MAX_ITER");
		return alpha;
	}

	public double[] hildreth_sequential_alg_b(QPConstraints cns) {

		int i;
		System.out.println("In hildreth sequential alg b");
		System.out.println(cns.toString());

		int K = cns.b.length;
		double[] alpha = new double[cns.b.length];
		double[] diff_alpha = new double[cns.b.length];
		double[] w1 = new double[cns.dim1];
		double[] w2 = new double[cns.dim2];

		double[] nrm1 = new double[K];
		double[] nrm2 = new double[K];
		for (i = 0; i < K; i++) {
			nrm1[i] = FeatureVector.dotProduct(cns.a1[i], cns.a1[i]);
			nrm2[i] = FeatureVector.dotProduct(cns.a2[i], cns.a2[i]);
		}

		double kkt;
		int max_kkt_i = 0;
		double inner1, inner2;
		double max_kkt = Double.POSITIVE_INFINITY;

		int iter = 1;

		boolean cont = true;

		double beta = 1.0 / K;
		double nu = qp_params.nu;
		double invnu = 1 / nu;
		double next_nu = nu;

		while (cont) {

			nu = next_nu;
			invnu = 1 / nu;

			max_kkt = Double.NEGATIVE_INFINITY;

			for (int ind = 0; ind < K; ind++) {

				inner1 = cns.a1[ind].dotProdoct(w1);
				inner2 = cns.a2[ind].dotProdoct(w2);

				kkt = cns.b[ind] - inner1 - inner2;
				if (alpha[ind] > 0)
					kkt = Math.abs(kkt);

				if (kkt > max_kkt) {
					max_kkt = kkt;
					max_kkt_i = ind;
				}

				diff_alpha[ind] = (-cns.b[ind] + inner1 + inner2)
						/ (nrm1[ind] + invnu * nrm2[ind]);
				if (diff_alpha[ind] > alpha[ind] / beta)
					diff_alpha[ind] = alpha[ind] / beta;

			} // for

			for (int ind = 0; ind < K; ind++) {

				alpha[ind] -= beta * diff_alpha[ind];

				if (Math.abs(diff_alpha[ind]) > qp_params.zero) {

					for (FeatureVector curr = cns.a1[ind]; curr != null; curr = curr.next) {
						if (curr.index < 0)
							continue;
						w1[curr.index] -= beta * diff_alpha[ind] * curr.value;

					}
					for (FeatureVector curr = cns.a2[ind]; curr != null; curr = curr.next) {
						if (curr.index < 0)
							continue;
						w2[curr.index] -= beta * diff_alpha[ind] * curr.value
								* invnu;

					}

				}
			} // for

			iter++;
			next_nu = 1.0 / Math.sqrt(iter);
			for (i = 0; i < w2.length; i++) {
				w2[i] *= nu / next_nu;
			}

			System.out.println("max_kkt_i = " + max_kkt_i + " max_kkt = "
					+ max_kkt + " iter = " + iter + " nu = " + nu);

			if (iter >= qp_params.max_iter)
				cont = false;
		}

		if (iter >= qp_params.max_iter)
			System.out.println("QPSolver : iter >= MAX_ITER");
		return alpha;
	}

	public double[] hildreth_sequential_alg_a(QPConstraints cns) {

		int i;
		System.out.println("In hildreth sequential alg a");
		System.out.println(cns.toString());

		int K = cns.b.length;
		double[] alpha = new double[cns.b.length];
		double[] w1 = new double[cns.dim1];
		double[] w2 = new double[cns.dim2];

		double[] nrm1 = new double[K];
		double[] nrm2 = new double[K];
		for (i = 0; i < K; i++) {
			nrm1[i] = FeatureVector.dotProduct(cns.a1[i], cns.a1[i]);
			nrm2[i] = FeatureVector.dotProduct(cns.a2[i], cns.a2[i]);
		}

		int[] timer = new int[K];
		int timer_adjust = qp_params.timer_adjust;
		int timer_maxkkt = qp_params.timer_maxkkt;

		int iter = 0;
		double diff_alpha;
		double try_alpha;
		double add_alpha;
		double kkt;
		int max_kkt_i = 0;
		double inner1, inner2;

		double max_kkt = qp_params.eps + 1;
		boolean cont = true;
		boolean is_timed;
		double invnu = 1 / qp_params.nu;
		int no_skip_constraints;

		while (cont) {

			is_timed = false;
			max_kkt = Double.NEGATIVE_INFINITY;
			no_skip_constraints = 0;
			w1 = new double[cns.dim1];
			for (int ind = 0; ind < K; ind++) {

				if (timer[ind] > 0) {
					timer[ind]--;
					is_timed = true;
					continue;
				}

				no_skip_constraints++;
				inner1 = cns.a1[ind].dotProdoct(w1);
				inner2 = cns.a2[ind].dotProdoct(w2);
				kkt = cns.b[ind] - inner1 - inner2;
				if (alpha[ind] > 0)
					kkt = Math.abs(kkt);
				if (alpha[ind] == cns.C)
					kkt = -kkt;

				if (kkt > max_kkt) {
					max_kkt = kkt;
					max_kkt_i = ind;
				}

				// update
				if (kkt > qp_params.zero) {
					diff_alpha = (cns.b[ind] - inner1 - inner2)
							/ (nrm1[ind] + invnu * nrm2[ind]);
					if (diff_alpha < -alpha[ind])
						diff_alpha = -alpha[ind];

					alpha[ind] += diff_alpha;
					if (alpha[ind] > cns.C) {
						diff_alpha = -diff_alpha + cns.C - alpha[ind];
						alpha[ind] = cns.C;
					}

					System.out.println("alpha[ind]=" + alpha[ind]
							+ " diff_alpha=" + diff_alpha);
					if (Math.abs(diff_alpha) > qp_params.zero) {
						for (FeatureVector curr = cns.a1[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w1[curr.index] += diff_alpha * curr.value;
						}
						for (FeatureVector curr = cns.a2[ind]; curr != null; curr = curr.next) {
							if (curr.index < 0)
								continue;
							w2[curr.index] += diff_alpha * curr.value * invnu;
						}

					}

				} else
					timer[ind] = qp_params.timer_adjust;
			}
			System.out.println("max_kkt_i = " + max_kkt_i + " max_kkt = "
					+ max_kkt + " iter = " + iter + " no_skip_constraints = "
					+ no_skip_constraints);
			timer[max_kkt_i] = timer_maxkkt;
			iter++;

			if (iter >= qp_params.max_iter)
				cont = false;
			else if (max_kkt < qp_params.eps) {
				if (!is_timed)
					cont = false;
				else {
					System.out
							.println("QPSolver : readjusting timer_adjust from "
									+ timer_adjust);
					timer_adjust /= qp_params.timer_div;
					if (timer_adjust == 0)
						timer_maxkkt = 0;
					for (int p = 0; p < K; p++)
						timer[p] = 0;
					System.out.println("QPSolver : readjusted timer_adjust to "
							+ timer_adjust);

				}
			}

		} // while

		if (iter >= qp_params.max_iter)
			System.out.println("QPSolver : iter >= MAX_ITER");
		return alpha;
	}

	public static double[] hildreth(FeatureVector[] a, double[] b) {

		int i;

		// System.out.println("In hildreth");

		double[] alpha = new double[b.length];

		double[] F = new double[b.length];
		double[] kkt = new double[b.length];
		double max_kkt = Double.NEGATIVE_INFINITY;

		int K = a.length;

		double[][] A = new double[K][K];
		boolean[] is_computed = new boolean[K];
		for (i = 0; i < K; i++) {
			A[i][i] = FeatureVector.dotProduct(a[i], a[i]);
			is_computed[i] = false;
		}

		int max_kkt_i = -1;

		for (i = 0; i < F.length; i++) {
			F[i] = b[i];
			kkt[i] = F[i];
			if (kkt[i] > max_kkt) {
				max_kkt = kkt[i];
				max_kkt_i = i;
			}
		}

		int iter = 0;
		double diff_alpha;
		double try_alpha;
		double add_alpha;

		while (max_kkt >= EPS && iter < MAX_ITER) {
			// System.out.println("max_kkt_i = " + max_kkt_i + " max_kkt = "
			// + max_kkt + " iter = " + iter);

			diff_alpha = A[max_kkt_i][max_kkt_i] <= ZERO ? 0.0 : F[max_kkt_i]
					/ A[max_kkt_i][max_kkt_i];
			try_alpha = alpha[max_kkt_i] + diff_alpha;
			add_alpha = 0.0;

			if (try_alpha < 0.0)
				add_alpha = -1.0 * alpha[max_kkt_i];
			else
				add_alpha = diff_alpha;

			alpha[max_kkt_i] = alpha[max_kkt_i] + add_alpha;

			if (!is_computed[max_kkt_i]) {
				for (i = 0; i < K; i++) {
					A[i][max_kkt_i] = FeatureVector.dotProduct(a[i],
							a[max_kkt_i]); // for version 1
					is_computed[max_kkt_i] = true;
				}
			}

			for (i = 0; i < F.length; i++) {
				F[i] -= add_alpha * A[i][max_kkt_i];
				kkt[i] = F[i];
				if (alpha[i] > ZERO)
					kkt[i] = Math.abs(F[i]);
			}

			max_kkt = Double.NEGATIVE_INFINITY;
			max_kkt_i = -1;
			for (i = 0; i < F.length; i++)
				if (kkt[i] > max_kkt) {
					max_kkt = kkt[i];
					max_kkt_i = i;
				}

			iter++;
		}

		if (iter >= MAX_ITER)
			System.out.println("QPSolver : iter >= MAX_ITER");
		return alpha;
	}

	public static double[] hildreth(FeatureVector[] a, double[] b, double C) {

		int i;

		double[] alpha = new double[b.length];

		double[] F = new double[b.length];
		double[] kkt = new double[b.length];
		double max_kkt = Double.NEGATIVE_INFINITY;

		int K = a.length;

		double[][] A = new double[K][K];
		boolean[] is_computed = new boolean[K];
		for (i = 0; i < K; i++) {
			A[i][i] = FeatureVector.dotProduct(a[i], a[i]);
			is_computed[i] = false;
		}

		int max_kkt_i = -1;

		for (i = 0; i < F.length; i++) {
			F[i] = b[i];
			kkt[i] = F[i];
			if (kkt[i] > max_kkt) {
				max_kkt = kkt[i];
				max_kkt_i = i;
			}
		}

		int iter = 0;
		double diff_alpha;
		double try_alpha;
		double add_alpha;

		while (max_kkt >= EPS && iter < MAX_ITER) {

			diff_alpha = A[max_kkt_i][max_kkt_i] <= ZERO ? 0.0 : F[max_kkt_i]
					/ A[max_kkt_i][max_kkt_i];
			try_alpha = alpha[max_kkt_i] + diff_alpha;
			add_alpha = 0.0;

			if (try_alpha < 0.0)
				add_alpha = -1.0 * alpha[max_kkt_i];
			else if (try_alpha > C)
				add_alpha = C - alpha[max_kkt_i];
			else
				add_alpha = diff_alpha;

			alpha[max_kkt_i] = alpha[max_kkt_i] + add_alpha;

			if (!is_computed[max_kkt_i]) {
				for (i = 0; i < K; i++) {
					A[i][max_kkt_i] = FeatureVector.dotProduct(a[i],
							a[max_kkt_i]); // for version 1
					is_computed[max_kkt_i] = true;
				}
			}

			for (i = 0; i < F.length; i++) {
				F[i] -= add_alpha * A[i][max_kkt_i];
				kkt[i] = F[i];
				if (alpha[i] > C - ZERO)
					kkt[i] = -kkt[i];
				else if (alpha[i] > ZERO)
					kkt[i] = Math.abs(F[i]);
			}

			max_kkt = Double.NEGATIVE_INFINITY;
			max_kkt_i = -1;
			for (i = 0; i < F.length; i++)
				if (kkt[i] > max_kkt) {
					max_kkt = kkt[i];
					max_kkt_i = i;
				}

			iter++;
		}
		// System.out.println("max_kkt_i = " + max_kkt_i + " max_kkt = " +
		// max_kkt + " iter = " + iter);
		if (iter >= MAX_ITER)
			System.out.println("QPSolver : iter >= MAX_ITER");
		return alpha;
	}

	public static double blas_mult(FeatureVector fv, double[] w) {

		TIntIntHashMap hm = new TIntIntHashMap();
		int run_indx = 1;
		for (FeatureVector curr = fv; curr.next != null; curr = curr.next) {
			if (curr.index < 0)
				continue;
			if (hm.get(curr.index) == 0) {
				hm.put(curr.index, run_indx);
				run_indx++;
			}
		}

		int my_dim = run_indx;
		double[] vec_a = new double[my_dim];
		double[] vec_p = new double[my_dim];
		for (FeatureVector curr = fv; curr.next != null; curr = curr.next) {
			if (curr.index >= 0) {
				vec_a[hm.get(curr.index) - 1] += curr.value;
			}
		}

		int[] keys = hm.keys();

		for (int l = 0; l < keys.length; l++) {
			vec_p[hm.get(keys[l]) - 1] = w[keys[l]];
		}

		// double dot = BLAS.dot(my_dim, vec_a, 1, vec_p, 1);
		double dot = 0;
		if (dot < 100.0)
			throw new RuntimeException("Avihai handle this...");

		return (dot);

	}

}
