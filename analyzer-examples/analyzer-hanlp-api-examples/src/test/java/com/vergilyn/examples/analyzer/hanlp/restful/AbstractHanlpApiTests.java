package com.vergilyn.examples.analyzer.hanlp.restful;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * <a href="https://www.hanlp.com/HanLPfile/admin.html">HanLP.com 接口使用指南</a> <br/>
 * 1) token获取：登录自己的账号后，点击生成新接口，获取到自己独享的token。 <br/>
 * 2) `hanlp-restful.jar` 不是最新的，请求会报错
 *
 * @author vergilyn
 * @since 2021-11-01
 */
public abstract class AbstractHanlpApiTests {

	public static String doHanlpApi(String token, String url, Map<String, Object> params) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			//添加header请求头，token请放在header里
			httpPost.setHeader("token", token);
			// 创建参数列表
			List<NameValuePair> paramList = Lists.newArrayList();
			if (params != null) {
				for (String key : params.keySet()) {
					//所有参数依次放在paramList中
					paramList.add(new BasicNameValuePair(key, (String) params.get(key)));
				}
				//模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			return resultString;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
