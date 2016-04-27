package edu.nju.nlp.online.types;

import java.io.IOException;
import java.io.ObjectInputStream;

/*ʵ��ӿ� 
 * ������ ������� 
*/
public interface Instance {

    public Input getInput();/*�������*/
    
    public Label getLabel();

    public Features getFeatures();  //�������

    public Features getFeatures(ObjectInputStream in) throws IOException;  //���������������

}
