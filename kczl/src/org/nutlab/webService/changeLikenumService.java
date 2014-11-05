package org.nutlab.webService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.nutlab.kczl.kczlApplication;

import com.njut.data.CookieElement;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class changeLikenumService  {
	private String TAG = "ChangeLikenum_SERVICE";

	public String upload(String edid ,String from,String to,String tag) {
		if(kczlApplication.IsOffLine==1){
			loginService ls = new loginService();
			String msg = ls.login(kczlApplication.UserName,
					kczlApplication.PassWord);
			if(!msg.contains("birthday")){
				kczlApplication.IsLogined=0;
				return "Error";
			}	
			kczlApplication.IsOffLine=0;
		}
		HttpClient httpclient = new DefaultHttpClient();
		// 你的URL
		HttpPost httppost = new HttpPost("http://" + kczlApplication.ServerUri
				+ "/timetable/like.action");
		String strResult = "";
		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					2);
			// Your DATA
			nameValuePairs.add(new BasicNameValuePair("edid", edid));
			nameValuePairs.add(new BasicNameValuePair("from", from));
			nameValuePairs.add(new BasicNameValuePair("to", to));
			nameValuePairs.add(new BasicNameValuePair("tag", tag));
			((AbstractHttpClient) httpclient).setCookieStore(kczlApplication.Cookies);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
			HttpResponse response = httpclient.execute(httppost);
			/* 若状态码为200，Post成功 */
			if (response.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				strResult = EntityUtils.toString(response.getEntity());
				List<Cookie> cookies =  ((AbstractHttpClient) httpclient).getCookieStore().getCookies(); 
				if (cookies.isEmpty()) {
					Log.i(TAG, "-------Cookie NONE---------");
				} else {
					CookieElement cookie = new CookieElement();
				}
			} else {
				strResult = "Error Response:"
						+ response.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		strResult = strResult.trim();
		return strResult;
	}



}
