package com.cloudbanter.adssdk.ad.manager;

import android.content.Context;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.model.CbAdvert;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;


/**
 * Created by eric on 4/7/16.
 */
public class PreloadSchedule {
  public static final String TAG = PreloadSchedule.class.getSimpleName();

  private static Context mContext;
  private static CbDevice mDevice;
  private static CbSchedule mSchedule;

  public static void init(Context context) {
    mContext = context;
  }

/*
  public static CbSchedule get() {
    CbSchedule sched = getScheduleFromAssets();
    if (null == sched || sched.isEmpty()) {
      return sched;
    } else {
      sched = com.cloudbanter.mms.ad.mock.MockDeviceConfig.mockSchedule();
      return sched;
    }
  }

  private static CbSchedule getScheduleFromAssets() {
    Context context = MmsApp.getApplication().getApplicationContext();
    String json = AssetFile.getString(context, "PreloadSchedule.json");
    if (TextUtils.isEmpty(json))
      return null;
    else
      return CbSchedule.fromJson(json);
  }
*/


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
    se.advert.url = null;
    se._id = se.advert._id;
    se.advertiser = se.advert.advertiser;
    se.displayOrder = Integer.toString(displayOrder);
    se.isPreload = true;
    return se;
  }

  public static CbSchedule getSchedule() {

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
                    "'name':'Samsung 7', 'url': 'http://tgo.gt/cbpsamsung',    " +
                    "'state':'approved','status':'active','updatedAt':'2015-10-09T14:57:34.767Z'," +
                    "'_id':'0001'}",
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
                    "'name':'tigo music', 'url': 'http://www.tigomusic.gt',    " +
                    "'state':'approved','status':'active','updatedAt':'2015-10-09T14:57:34.767Z'," +
                    "'_id':'0002'}",
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
                    "'name':'tigo play', 'url': 'http://play.tigostar.com.gt', " +
                    "'state':'approved','status':'active','updatedAt':'2015-10-09T14:57:34.767Z'," +
                    "'_id':'0003'}"

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
                    "'name':'Samsung 7', 'url': 'http://tgo.gt/cbpsamsung',    " +
                    "'state':'approved','status':'active','updatedAt':'2015-10-09T14:57:34.767Z'," +
                    "'_id':'0001'}",
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
                    "'name':'tigo music', 'url': 'http://www.tigomusic.gt',    " +
                    "'state':'approved','status':'active','updatedAt':'2015-10-09T14:57:34.767Z'," +
                    "'_id':'0002'}",
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
                    "'name':'tigo play', 'url': 'http://play.tigostar.com.gt', " +
                    "'state':'approved','status':'active','updatedAt':'2015-10-09T14:57:34.767Z'," +
                    "'_id':'0003'}"

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
    final String[] demoPreload = {
            "{ 'advertText': 'Cloudbanter Mobile Messaging', 'advertiser': 'cb', 'bannerImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl001/bannerImage.png', 'createdAt': " +
                    "'2016-05-23T14:57:34.767Z', 'displayOrder': '1100', 'fullImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl001/fullImage.png', 'fullExt': 'gif', 'name': " +
                    "'Cloudbanter Mobile Messaging', 'url': 'http://www.cloudbanter.com', " +
                    "'state': 'reviewed', 'status': 'active', 'updatedAt': '2016-05-23T14:57:34" +
                    ".767Z', '_id': 'pl001', 'ackClickCount': '0', 'advertTextClickCount': '0', " +
                    "'advertTextViewCount': '0', 'bannerTextClickCount': '0', 'bannerViewCount': " +
                    "'0', 'budget': '0', 'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'BriTelco', 'advertiser': 'cb', 'bannerImage': 'http://demo.images" +
                    ".cloudbanter.com.s3-eu-west-1.amazonaws.com/advertisers/cb/pl002/bannerImage" +
                    ".png', 'createdAt': '2016-05-23T14:57:34.767Z', 'displayOrder': '1200', " +
                    "'fullImage': 'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl002/fullImage.png', 'fullExt': 'gif', 'name': " +
                    "'BriTelco', 'url': 'http://www.cloudbanter.com/britelco', 'state': " +
                    "'reviewed', 'status': 'active', 'updatedAt': '2016-05-23T14:57:34.767Z', " +
                    "'_id': 'pl002', 'ackClickCount': '0', 'advertTextClickCount': '0', " +
                    "'advertTextViewCount': '0', 'bannerTextClickCount': '0', 'bannerViewCount': " +
                    "'0', 'budget': '0', 'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'Cloudbanter Mobile Messaging', 'advertiser': 'cb', 'bannerImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl003/bannerImage.png', 'createdAt': " +
                    "'2016-05-23T14:57:34.767Z', 'displayOrder': '1300', 'fullImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl003/fullImage.png', 'fullExt': 'gif', 'name': " +
                    "'Cloudbanter Mobile Messaging', 'url': 'http://www.cloudbanter.com', " +
                    "'state': 'reviewed', 'status': 'active', 'updatedAt': '2016-05-23T14:57:34" +
                    ".767Z', '_id': 'pl003', 'ackClickCount': '0', 'advertTextClickCount': '0', " +
                    "'advertTextViewCount': '0', 'bannerTextClickCount': '0', 'bannerViewCount': " +
                    "'0', 'budget': '0', 'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'BriTelco WebTV', 'advertiser': 'cb', 'bannerImage': 'http://demo" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl004/bannerImage.png', 'createdAt': " +
                    "'2016-05-23T14:57:34.767Z', 'displayOrder': '1400', 'fullImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl004/fullImage.png', 'fullExt': 'gif', 'name': " +
                    "'BriTelco WebTV', 'url': 'http://www.cloudbanter.com/britelco', 'state': " +
                    "'reviewed', 'status': 'active', 'updatedAt': '2016-05-23T14:57:34.767Z', " +
                    "'_id': 'pl004', 'ackClickCount': '0', 'advertTextClickCount': '0', " +
                    "'advertTextViewCount': '0', 'bannerTextClickCount': '0', 'bannerViewCount': " +
                    "'0', 'budget': '0', 'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'Pizza April', 'advertiser': 'cb', 'bannerImage': 'http://demo" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl005/bannerImage.png', 'createdAt': " +
                    "'2016-05-23T14:57:34.767Z', 'displayOrder': '1500', 'fullImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl005/fullImage.png', 'fullExt': 'gif', 'name': 'Pizza " +
                    "April', 'state': 'reviewed', 'status': 'active', 'updatedAt': " +
                    "'2016-05-23T14:57:34.767Z', '_id': 'pl005', 'ackClickCount': '0', " +
                    "'advertTextClickCount': '0', 'advertTextViewCount': '0', " +
                    "'bannerTextClickCount': '0', 'bannerViewCount': '0', 'budget': '0', " +
                    "'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'Elite Motors', 'advertiser': 'cb', 'bannerImage': 'http://demo" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl006/bannerImage.png', 'createdAt': " +
                    "'2016-05-23T14:57:34.767Z', 'displayOrder': '1600', 'fullImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl006/fullImage.png', 'fullExt': 'png', 'name': 'Elite " +
                    "Motors', 'state': 'reviewed', 'status': 'active', 'updatedAt': " +
                    "'2016-05-23T14:57:34.767Z', '_id': 'pl006', 'ackClickCount': '0', " +
                    "'advertTextClickCount': '0', 'advertTextViewCount': '0', " +
                    "'bannerTextClickCount': '0', 'bannerViewCount': '0', 'budget': '0', " +
                    "'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'Pizza 241', 'advertiser': 'cb', 'bannerImage': 'http://demo.images" +
                    ".cloudbanter.com.s3-eu-west-1.amazonaws.com/advertisers/cb/pl007/bannerImage" +
                    ".png', 'createdAt': '2016-05-23T14:57:34.767Z', 'displayOrder': '1700', " +
                    "'fullImage': 'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl007/fullImage.png', 'fullExt': 'gif', 'name': 'Pizza " +
                    "241', 'state': 'reviewed', 'status': 'active', 'updatedAt': " +
                    "'2016-05-23T14:57:34.767Z', '_id': 'pl007', 'ackClickCount': '0', " +
                    "'advertTextClickCount': '0', 'advertTextViewCount': '0', " +
                    "'bannerTextClickCount': '0', 'bannerViewCount': '0', 'budget': '0', " +
                    "'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'JuiceBurst Explosion', 'advertiser': 'cb', 'bannerImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl008/bannerImage.png', 'createdAt': " +
                    "'2016-05-23T14:57:34.767Z', 'displayOrder': '1800', 'fullImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl008/fullImage.png', 'fullExt': 'gif', 'name': " +
                    "'JuiceBurst Explosion', 'url': 'http://www.juiceburst.com/our-drinks', " +
                    "'state': 'reviewed', 'status': 'active', 'updatedAt': '2016-05-23T14:57:34" +
                    ".767Z', '_id': 'pl008', 'ackClickCount': '0', 'advertTextClickCount': '0', " +
                    "'advertTextViewCount': '0', 'bannerTextClickCount': '0', 'bannerViewCount': " +
                    "'0', 'budget': '0', 'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'Really JuiceBurst', 'advertiser': 'cb', 'bannerImage': 'http://demo" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl009/bannerImage.png', 'createdAt': " +
                    "'2016-05-23T14:57:34.767Z', 'displayOrder': '1900', 'fullImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl009/fullImage.png', 'fullExt': 'gif', 'name': 'Really " +
                    "JuiceBurst', 'url': 'http://www.juiceburst.com/our-drinks', 'state': " +
                    "'reviewed', 'status': 'active', 'updatedAt': '2016-05-23T14:57:34.767Z', " +
                    "'_id': 'pl009', 'ackClickCount': '0', 'advertTextClickCount': '0', " +
                    "'advertTextViewCount': '0', 'bannerTextClickCount': '0', 'bannerViewCount': " +
                    "'0', 'budget': '0', 'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }",
            "{ 'advertText': 'Canine Partners', 'advertiser': 'cb', 'bannerImage': 'http://demo" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl010/bannerImage.png', 'createdAt': " +
                    "'2016-05-23T14:57:34.767Z', 'displayOrder': '2000', 'fullImage': " +
                    "'http://demo.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/cb/pl010/fullImage.png', 'fullExt': 'gif', 'name': 'Canine " +
                    "Partners', 'url': 'https://caninepartners.org" +
                    ".uk/blog/meet-martin-a-lecturer-from-west-sussex', 'state': 'reviewed', " +
                    "'status': 'active', 'updatedAt': '2016-05-23T14:57:34.767Z', '_id': 'pl010'," +
                    " 'ackClickCount': '0', 'advertTextClickCount': '0', 'advertTextViewCount': " +
                    "'0', 'bannerTextClickCount': '0', 'bannerViewCount': '0', 'budget': '0', " +
                    "'fullAdvertClickCount': '0', 'fullAdvertViewCount': '0' }"

    };
    final String[] videoconPilot = {
            "{ 'advertText':'Connect Bill online', 'advertiser':'videocon', " +
                    "'bannerImage':'http://videocon.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/videocon/ad-001/bannerImage.png', " +
                    "'createdAt':'2016-05-23T14:57:34.767Z', 'displayOrder':'001', " +
                    "'fullImage':'http://videocon.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/videocon/ad-001/fullImage.png', 'fullExt':'png', " +
                    "'name':'Connect Bill online', 'url':'http://www.connectzone.in/payonlinestd" +
                    ".php' , 'state':'approved', 'status':'active', " +
                    "'updatedAt':'2016-05-23T14:57:34.767Z', '_id':'ad-001', 'ackClickCount':0, " +
                    "'advertTextClickCount':0, 'advertTextViewCount':0, 'bannerTextClickCount':0," +
                    " 'bannerViewCount':0, 'budget':200.0, 'fullAdvertClickCount':0, " +
                    "'fullAdvertViewCount':0 }",
            "{ 'advertText':'FTTH', 'advertiser':'videocon', 'bannerImage':'http://videocon" +
                    ".images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/videocon/ad-002/bannerImage.png', " +
                    "'createdAt':'2016-05-23T14:57:34.767Z', 'displayOrder':'002', " +
                    "'fullImage':'http://videocon.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/videocon/ad-002/fullImage.png', 'fullExt':'png', " +
                    "'name':'FTTH'   , 'url':'http://www.connectzone.in/ftth-service.php' , " +
                    "'state':'approved', 'status':'active', 'updatedAt':'2016-05-23T14:57:34" +
                    ".767Z', '_id':'ad-002', 'ackClickCount':0, 'advertTextClickCount':0, " +
                    "'advertTextViewCount':0, 'bannerTextClickCount':0, 'bannerViewCount':0, " +
                    "'budget':200.0, 'fullAdvertClickCount':0, 'fullAdvertViewCount':0 }",
            "{ 'advertText':'Custom plans', 'advertiser':'videocon', " +
                    "'bannerImage':'http://videocon.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/videocon/ad-003/bannerImage.png', " +
                    "'createdAt':'2016-05-23T14:57:34.767Z', 'displayOrder':'003', " +
                    "'fullImage':'http://videocon.images.cloudbanter.com.s3-eu-west-1.amazonaws" +
                    ".com/advertisers/videocon/ad-003/fullImage.png', 'fullExt':'png', " +
                    "'name':'Custom plans' , 'url':'http://www.connectzone.in/best-fit-plan.php'," +
                    " 'state':'approved', 'status':'active', 'updatedAt':'2016-05-23T14:57:34" +
                    ".767Z', '_id':'ad-003', 'ackClickCount':0, 'advertTextClickCount':0, " +
                    "'advertTextViewCount':0, 'bannerTextClickCount':0, 'bannerViewCount':0, " +
                    "'budget':200.0, 'fullAdvertClickCount':0, 'fullAdvertViewCount':0 }"
    };

    final String[] devPreload = {
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


    String[] entrySet = null;
    if (CbAdsSdk.PRELOAD_IMAGE_SCHED.equalsIgnoreCase("test")) {
      Log.d(TAG, "Preload: test");
      entrySet = adverts3;
    } else if (CbAdsSdk.PRELOAD_IMAGE_SCHED.equalsIgnoreCase("pilot1")) {
      Log.d(TAG, "Preload: test");
      entrySet = tigoPreload1;
    } else if (CbAdsSdk.PRELOAD_IMAGE_SCHED.equalsIgnoreCase("dev")) {
      Log.d(TAG, "Preload: dev");
      entrySet = devPreload;
    } else if (CbAdsSdk.PRELOAD_IMAGE_SCHED.equalsIgnoreCase("local")) {
      Log.d(TAG, "Preload: local");
      entrySet = tigoPreload_wGIF;
    } else if (CbAdsSdk.PRELOAD_IMAGE_SCHED.equalsIgnoreCase("pilot2")) {
      Log.d(TAG, "Preload: pilot2");
      entrySet = videoconPilot;
    } else if (CbAdsSdk.PRELOAD_IMAGE_SCHED.equalsIgnoreCase("demo")) {
      Log.d(TAG, "Preload: demo");
      entrySet = demoPreload;
    }


    if (null == entrySet) {
      throw new RuntimeException("PreloadAdverts schedule not set in PreloadSchedule.java");
    }

    CbSchedule sched = new CbSchedule();
    sched.scheduleType = "PRELOAD";
    int displayOrder = 100;
    CbScheduleEntry entries[] = new CbScheduleEntry[entrySet.length];
    int ent = 0;
    for (String ad : entrySet) {
      entries[ent++] = mockEntry(ad, displayOrder++);
      sched.addItem(mockEntry(ad, displayOrder++));
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
