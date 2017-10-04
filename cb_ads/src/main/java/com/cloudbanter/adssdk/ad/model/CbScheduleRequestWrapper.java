package com.cloudbanter.adssdk.ad.model;

import java.util.List;

/**
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 30/1/17
 */
public class CbScheduleRequestWrapper extends AModel<CbScheduleRequestWrapper> {

  /** Expected keywords for ads **/
  private List<String> keywords;

  public CbScheduleRequestWrapper(List<String> keywords) {
    this.keywords = keywords;
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
  public CbScheduleRequestWrapper setKeywords(List<String> keywords) {
    this.keywords = keywords;
    return this;
  }

}
