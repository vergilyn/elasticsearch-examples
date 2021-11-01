package com.vergilyn.examples.analyzer.hanlp.restful;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 *
 * @author vergilyn
 * @since 2021-11-01
 */
public class PlaceTests extends AbstractHanlpApiTests {

	/**
	 * <a href="https://www.hanlp.com/product-Place.html">HanLP 地名地址识别</a> <br/>
	 * （页面底部 API地址处 登录后生成新的token）
	 * <pre>
	 * {
	 * 	"code":0,
	 * 	"data":[
	 *        {"nature":"ns","word":"重庆市"},
	 *        {"nature":"ns","word":"渝北区"}
	 * 	]
	 * }
	 * </pre>
	 */
	@Test
	@SneakyThrows
	public void test() {
		String token = "07d7eb1592fb49e59b85036c725408bc1635744162509token";
		String url = "http://comdo.hanlp.com/hanlp/v1/ner/place";

		Map<String, Object> params = Maps.newHashMap();
		params.put("text", "重庆市渝北区");

		String result = doHanlpApi(token, url, params);
		System.out.println(result);

		// final List<List<String>> tokenize = hanLPClient.tokenize(text);

		// System.out.println("tokenize >>>> " + JSON.toJSONString(tokenize));
	}
}
