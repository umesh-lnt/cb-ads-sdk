package com.cloudbanter.adssdk.ad.manager.adstates;


import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;

/**
 * Created by eric on 8/18/15.
 */
public class AdStateMachine {

  private static AdStateLike initialState = new StateBase();


  private AdStateLike curState;

  AdStateMachine() {
    setCurState(initialState);
  }

  void setCurState(final AdStateLike state) {
    curState = state;
  }

  void resetState() {
    curState = initialState;
  }

  public CbScheduleEntry getAcknowledgement() {
    return curState.getAcknowledgement();
  }

  public String getPromoDetail() {
    return curState.getPromoDetail();
  }

  public boolean hasAcknowledgement() {
    return curState.hasAcknowledgement();
  }

  public boolean hasPromoDetail() {
    return curState.hasPromoDetail();
  }

  public void onClick() {
    curState.onClick();
  }


  interface AdStateLike {
    void onView();

    AdStateLike onClick();

    CbScheduleEntry getAcknowledgement();

    String getPromoDetail();

    boolean hasAcknowledgement();

    boolean hasPromoDetail();
  }

  private static class StateBase implements AdStateLike {


    @Override
    public void onView() {

    }

    @Override
    public AdStateLike onClick() {
      return null;
    }

    @Override
    public CbScheduleEntry getAcknowledgement() {
      return null;
    }

    @Override
    public String getPromoDetail() {
      return null;
    }

    @Override
    public boolean hasAcknowledgement() {
      return false;
    }

    @Override
    public boolean hasPromoDetail() {
      return false;
    }
  }

  private static class StateText extends StateBase {

  }

  private static class StateBanner extends StateBase {

  }

  private static class StateFull extends StateBase {

  }

}


