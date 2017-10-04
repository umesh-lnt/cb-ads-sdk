package com.cloudbanter.adssdk.ad.mock;

import android.content.Context;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.model.CbAdvert;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;


public class MockDeviceConfig {
  private static Context mContext;
  private static CbDevice mDevice;
  private static CbSchedule mSchedule;

  public static void init(Context context) {
    mContext = context;
  }

  // mock entry maker based on (json) 
  final String json =
          "{'ackClickCount':0,'advertText':'Fastest pizza delivery in town'," +
                  "'advertTextClickCount':0,'advertTextViewCount':0," +
                  "'advertiser':'5617d5de6e484c77b9141ab8','bannerImage':'http://test" +
                  "./cloudbanter.com.s3-eu-west-1.amazonaws" +
                  ".com/advertisers/Advertiser1/advert-5602e2c8e0600ff4137d70f6/bannerImage.png'," +
                  "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                  "'createdAt':'2015-10-09T14:57:34.767Z','fullAdvertClickCount':0," +
                  "'fullAdvertViewCount':0,'fullImage':'http://test.images.cloudbanter.com" +
                  ".s3-eu-west-1.amazonaws" +
                  ".com/advertisers/Advertiser1/advert-5602e2c8e0600ff4137d70f6/fullImage.png'," +
                  "'keywords':['5617d5de6e484c77b9141aa9','5617d5de6e484c77b9141aaa'," +
                  "'5617d5de6e484c77b9141aab','5617d5de6e484c77b9141aac'," +
                  "'5617d5de6e484c77b9141aad','5617d5de6e484c77b9141aae'," +
                  "'5617d5de6e484c77b9141aaf','5617d5de6e484c77b9141ab0'," +
                  "'5617d5de6e484c77b9141ab1','5617d5de6e484c77b9141ab2']," +
                  "'keywordsValuation':'valuation','name':'Pizza delivery','state':'approved'," +
                  "'status':'active','updatedAt':'2015-10-09T14:57:34.767Z'," +
                  "'_id':'5617d5de6e484c77b9141ac5'}";
  CbScheduleEntry se = mockEntry(json, 1);

  public static CbScheduleEntry mockEntry(String json, int displayOrder) {
    CbScheduleEntry se = new CbScheduleEntry();
    se.advert = CbAdvert.fromJson(json);
    se._id = se.advert._id;
    se.advertiser = se.advert.advertiser;
    se.displayOrder = Integer.toString(displayOrder);
    se.isPreload = true;
    return se;
  }

  public static CbSchedule mockSchedule() {

    final String[] adverts3 = {
            "{'ackClickCount':0,'advertText':'Allianz',           'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://test.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/Advertiser1/advert-0001/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'001', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://" +
                    ".s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0001/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Allianz',            'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-0001'}",
            "{'ackClickCount':0,'advertText':'Banco Santander',   'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1','bannerImage':'http://" +
                    ".s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0002/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'002', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://" +
                    ".s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0002/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Banco Santander',    'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-0002'}",
            "{'ackClickCount':0,'advertText':'GEICO',             'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0003/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'003', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0," +
                    "'fullImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0003/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'GEICO',              'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-0003', " +
                    "'url':'http://www.geico.com/auto-insurance/'}",
            "{'ackClickCount':0,'advertText':'HSBC',              'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0004/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'004', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0," +
                    "'fullImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0004/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'HSBC',               'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-0004'}",
            "{\"ackClickCount\":0,\"advertText\":\"L'Or\u00e9al\", \"advertTextClickCount\":0," +
                    "\"advertTextViewCount\":0,\"advertiser\":\"Advertiser1\"," +
                    "\"bannerImage\":\"http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0005/bannerImage.png\"," +
                    "\"bannerTextClickCount\":0,\"bannerViewCount\":0,\"budget\":200.0," +
                    "\"createdAt\":\"2015-10-09T14:57:34.767Z\", \"displayOrder\":\"005\", " +
                    "\"fullAdvertClickCount\":0,\"fullAdvertViewCount\":0," +
                    "\"fullImage\":\"http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0005/fullImage.png\"," +
                    "\"keywords\":[\"5617d5de6e484c77b9141aa9\"]," +
                    "\"keywordsValuation\":\"valuation\",\"name\":\"L'Or\u00e9al\",  " +
                    "\"state\":\"approved\",\"status\":\"active\"," +
                    "\"updatedAt\":\"2015-10-09T14:57:34.767Z\",\"_id\":\"advert-0005\"}",
            "{'ackClickCount':0,'advertText':'Lavazza',           'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0006/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'006', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0," +
                    "'fullImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0006/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Lavazza',            'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-0006'}",
            "{'ackClickCount':0,'advertText':'Lexus',             'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0007/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'007', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0," +
                    "'fullImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0007/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Lexus',              'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-0007'}",
            "{'ackClickCount':0,'advertText':'Mercedes',          'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0008/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'008', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0," +
                    "'fullImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0008/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Mercedes',           'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-0008'}",
            "{'ackClickCount':0,'advertText':'Nestl\u00e9',       'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0009/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'009', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0," +
                    "'fullImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-0009/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Nestl\u00e9',        'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-0009'}",
            "{'ackClickCount':0,'advertText':'Unilever',          'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-000a/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'010', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0," +
                    "'fullImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-000a/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Unilever',           'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-000a'}",
            "{'ackClickCount':0,'advertText':'Wells Fargo',       'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'Advertiser1'," +
                    "'bannerImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-000b/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'011', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0," +
                    "'fullImage':'http://s3-eu-west-1.amazonaws.com.test.images.cloudbanter" +
                    ".com/advertisers/Advertiser1/advert-000b/fullImage.png'," +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Wells Fargo',        'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'advert-000b'}"
    };
    final String[] tigoPreload1 = {
            "{'ackClickCount':0,'advertText':'Samsung',      'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'advertiser'," +
                    "'bannerImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0001/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'001', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://pilot" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0001/fullImage.png', 'fullExt': 'png', " +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Samsung 7',     'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'0001'}",
            "{'ackClickCount':0,'advertText':'tigo-music',   'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'advertiser'," +
                    "'bannerImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0002/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'002', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://pilot" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0002/fullImage.png', 'fullExt': 'png', " +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'tigo music',    'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'0002'}",
            "{'ackClickCount':0,'advertText':'tigo-play',    'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'advertiser'," +
                    "'bannerImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0003/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'003', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://pilot" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0003/fullImage.png', 'fullExt': 'png', " +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'tigo play',     'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'0003'}"

            // FOR TESTING GIF, "{'ackClickCount':0,'advertText':'tigo-gif',
            // 'advertTextClickCount':0,'advertTextViewCount':0,'advertiser':'advertiser',
            // 'bannerImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws
            // .com/advertisers/advertiser/advert-0010/bannerImage.png','bannerTextClickCount':0,
            // 'bannerViewCount':0,'budget':200.0,'createdAt':'2015-10-09T14:57:34.767Z',
            // 'displayOrder':'004', 'fullAdvertClickCount':0,'fullAdvertViewCount':0,
            // 'fullImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws
            // .com/advertisers/advertiser/advert-0010/fullImage.gif', 'fullExt': 'gif',
            // 'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation',
            // 'name':'tigo gif',      'state':'approved','status':'active',
            // 'updatedAt':'2015-10-09T14:57:34.767Z','_id':'0010'}",
    };
    final String[] tigoPreload_wGIF = {
            "{'ackClickCount':0,'advertText':'Samsung',      'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'advertiser'," +
                    "'bannerImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0001/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'001', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://pilot" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0001/fullImage.png', 'fullExt': 'png', " +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'Samsung 7',     'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'0001'}",
            "{'ackClickCount':0,'advertText':'tigo-music',   'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'advertiser'," +
                    "'bannerImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0002/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'002', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://pilot" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0002/fullImage.png', 'fullExt': 'png', " +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'tigo music',    'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'0002'}",
            "{'ackClickCount':0,'advertText':'tigo-play',    'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'advertiser'," +
                    "'bannerImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0003/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'003', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://pilot" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0003/fullImage.png', 'fullExt': 'png', " +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'tigo play',     'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'0003'}"

            ,
            "{'ackClickCount':0,'advertText':'tigo-gif',     'advertTextClickCount':0," +
                    "'advertTextViewCount':0,'advertiser':'advertiser'," +
                    "'bannerImage':'http://pilot.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0010/bannerImage.png'," +
                    "'bannerTextClickCount':0,'bannerViewCount':0,'budget':200.0," +
                    "'createdAt':'2015-10-09T14:57:34.767Z', 'displayOrder':'004', " +
                    "'fullAdvertClickCount':0,'fullAdvertViewCount':0,'fullImage':'http://pilot" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/advertiser/advert-0010/fullImage.gif', 'fullExt': 'gif', " +
                    "'keywords':['5617d5de6e484c77b9141aa9'],'keywordsValuation':'valuation'," +
                    "'name':'tigo gif',      'state':'approved','status':'active'," +
                    "'updatedAt':'2015-10-09T14:57:34.767Z','_id':'0010'}",
    };


    String[] entrySet = null;
    if (CbAdsSdk.SERVER_NAME.equalsIgnoreCase("test")) {
      entrySet = adverts3;
    } else if (CbAdsSdk.SERVER_NAME.equalsIgnoreCase("pilot1")) {
      entrySet = tigoPreload1;
    } else if (CbAdsSdk.SERVER_NAME.equalsIgnoreCase("dev")) {
      entrySet = tigoPreload1;
    } else if (CbAdsSdk.SERVER_NAME.equalsIgnoreCase("local")) {
      entrySet = tigoPreload_wGIF;
    }


    if (null == entrySet) {
      throw new RuntimeException("PreloadAdverts schedule not set in MockDeviceConfig");
    }

    CbSchedule sched = new CbSchedule();
    int displayOrder = 100;
    CbScheduleEntry entries[] = new CbScheduleEntry[entrySet.length];
    int ent = 0;
    for (String ad : entrySet) {
      entries[ent++] = mockEntry(ad, displayOrder++);
    }
    
    /*
    for (String ad: adverts3) {
      String.format("{ \n  name: \'%s\',\n  advertText: \'%s\'\n ");
      name
      advertText
      fullImage
      bannerImage
      state
      status
      advertiser; 'preload'
    }
    */

    sched.entries = entries;
    sched.addAll(entries);
    return sched;
  }
}
