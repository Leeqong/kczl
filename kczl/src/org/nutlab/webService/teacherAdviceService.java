package org.nutlab.webService;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.nutlab.kczl.kczlApplication;

import com.njut.R.string;

public class teacherAdviceService {
	private String TAG = "TEACHER_ADVICE_SERVICE";

	public String get(String ctid,String lastUpdate){
		if(kczlApplication.IsOffLine==1){
			loginteacherService ls = new loginteacherService();
			String msg = ls.login(kczlApplication.UserName,
					kczlApplication.PassWord);
			if(!msg.contains("teachername")){
				kczlApplication.IsLogined=0;
				return "Error";
			}
			kczlApplication.IsOffLine=0;
		}
		DefaultHttpClient httpclient = new DefaultHttpClient();
		// 你的URL
		HttpPost httppost = new HttpPost("http://" + kczlApplication.ServerUri
				+ "/timetable/fetchadvice.action");
		String strResult = "";
		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					2);
			// Your DATA
			nameValuePairs.add(new BasicNameValuePair("ctid", ctid));
			nameValuePairs.add(new BasicNameValuePair("lastupdate", lastUpdate));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
			httpclient.setCookieStore(kczlApplication.Cookies);
			HttpResponse response = httpclient.execute(httppost);
			/* 若状态码为200，Post成功 */
			if (response.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				strResult = EntityUtils.toString(response.getEntity());				
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

