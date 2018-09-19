package com.vendor.scrapy.vendorscrapy.webservice;

/*@Author: Ruhi Bhandari
* @Created Date: 6/8/2016
* @Interface used for services to return data in activity
* */

public interface OnTaskCompleted {

    void onTaskCompleted(String result, String TAG) throws Exception;

}