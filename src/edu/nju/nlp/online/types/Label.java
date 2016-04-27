package edu.nju.nlp.online.types;

//��ȷ��ǩ�ӿ�
public interface Label {

	public FeatureVector getFeatureVectorRepresentation();//�����������

	// evaluates a prediction against this label
	public double loss(Label pred);//��� ��Ԥ��֮�� margin ���

	// public int hammingDistance(Label pred);

	/** 1 - hammingDistance */
	// public int correct(Label pred);

}
