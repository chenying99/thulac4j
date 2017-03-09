package io.github.yizhiru.thulac4j;

import io.github.yizhiru.thulac4j.base.CwsModel;
import io.github.yizhiru.thulac4j.base.NGramFeature;
import io.github.yizhiru.thulac4j.base.POCS;
import io.github.yizhiru.thulac4j.base.Util;
import io.github.yizhiru.thulac4j.process.Cementer;
import io.github.yizhiru.thulac4j.process.Decoder;
import io.github.yizhiru.thulac4j.process.Ruler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jyzheng
 */
public abstract class Segmenter<T> {
  protected CwsModel model;
  protected int[][] labelTrans;
  protected Cementer ns;
  protected Cementer idiom;
  protected Cementer uw;



  protected void setUp() {
    labelTrans = Util.labelPreTransitions(model.labelValues);
    ns = new Cementer(this.getClass().getResourceAsStream(Util.nsDat), "ns");
    idiom = new Cementer(this.getClass().getResourceAsStream(Util.idiomDat), "i");
  }


  /**
   * 处理序列标注得到分词结果
   *
   * @param cleaned 清洗后的字符串
   * @return 词与词性的数组
   */
  abstract List<T> getResult(String cleaned, int[] labels);


  public List<T> segment(String sentence) {
    if (sentence.length() == 0) return getResult(sentence, null);
    Ruler ruler = new Ruler(sentence.toCharArray());
    String cleaned = ruler.rulePoc();
    NGramFeature nGram = new NGramFeature(model.featureDat);
    int[][] values = nGram.putValues(model, cleaned.toCharArray());
    int[] labels = Decoder.viterbi(model, cleaned.length(), ruler.pocss, values, labelTrans);
    return getResult(cleaned, labels);
  }


  public void setUserWordsPath(String path) throws FileNotFoundException {
    uw = new Cementer(path, "uw");
  }
}
