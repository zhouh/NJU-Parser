package edu.nju.nlp.online.types;

import java.io.IOException;

public interface DataManager {

    public void createAlphabets(String file) throws IOException;

    public void closeAlphabets();

    public Instance[] readData(String file) throws IOException;

    public Instance[] readData(String file, boolean createFeatureFile) throws IOException;

    public MultiHashAlphabet getDataAlphabet();

}
