package com.cloudbanter.adssdk.ad.model;

import java.util.List;

/**
 * Element that contains the keywords
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 27/1/17
 */
public class CbKeywords extends AModel<CbKeywords> {

  /** Keywords list **/
  private List<String> keywords;

  /**
   * Converts a given json into {@link CbKeywords}
   *
   * @param json
   *         Json string
   *
   * @return {@link CbKeywords} instance
   */
  public static CbKeywords fromJson(String json) {
    return gson.fromJson(json, CbKeywords.class);
  }

  /**
   * @return the keywords
   */
  public List<String> getKeywords() {
    return keywords;
  }

  /**
   * @param keywords
   *         the keywords to set
   */
  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }
}
