package org.nutlab.webService;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.nutlab.kczl.kczlApplication;

public class curriculumteacherService {
	
	private String TAG = "CURRICULUM_TAG_SERVICE";

	public String get(){
		if(kczlApplication.IsOffLine==1){
			loginteacherService lts = new loginteacherService();
			String msg = lts.login(kczlApplication.UserName,
					kczlApplication.PassWord);
			if(!msg.contains("teachername")){
				kczlApplication.IsLogined=0;
				return "Error";
			}	
			kczlApplication.IsOffLine=0;
		}
		DefaultHttpClient httpclient = new DefaultHttpClient();
		// ���URL
		HttpPost httppost = new HttpPost("http://" + kczlApplication.ServerUri
				+ "/timetable/fetchcurriculum.action");
		String strResult = "";
		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					2);
			// Your DATA
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
			httpclient.setCookieStore(kczlApplication.Cookies);
			HttpResponse response = httpclient.execute(httppost);
			/* ��״̬��Ϊ200��Post�ɹ� */
			if (response.getStatusLine().getStatusCode() == 200) {
				/* ���������� */
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
