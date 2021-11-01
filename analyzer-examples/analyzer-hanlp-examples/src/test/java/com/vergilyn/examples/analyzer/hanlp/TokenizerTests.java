package com.vergilyn.examples.analyzer.hanlp;

import java.util.List;
import java.util.StringJoiner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.BasicTokenizer;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.tokenizer.TraditionalChineseTokenizer;
import com.hankcs.hanlp.tokenizer.URLTokenizer;

import org.junit.jupiter.api.Test;

public class TokenizerTests {

	@Test
	public void tokenizer(){
		String text = "太阳能热水器维修";
		text = "林俊杰在上海市开演唱会啦";
		text = "空调维修";
		text = "重庆市渝北区";

		// 基础分词器，只做基本NGram分词，不识别命名实体，不使用用户词典
		printTerms("basic", BasicTokenizer.segment(text));

		// 索引分词器
		printTerms("index", IndexTokenizer.segment(text));

		// 可供自然语言处理用的分词器，更重视准确率
		// java.io.IOException: data/model/perceptron/large/cws.bin 加载失败
		// printTerms("NLP", NLPTokenizer.segment(text));

		// 实词分词器，自动移除停用词
		printTerms("Notional", NotionalTokenizer.segment(text));

		// 极速分词，基于Double Array Trie实现的词典分词，适用于“高吞吐量”“精度一般”的场合
		printTerms("speed", SpeedTokenizer.segment(text));

		// 标准分词器
		printTerms("standard", StandardTokenizer.segment(text));

		// 繁体中文分词器
		printTerms("TraditionalChinese", TraditionalChineseTokenizer.segment(text));

		// 可以识别URL的分词器
		printTerms("URL", URLTokenizer.segment(text));
	}

	@Test
	public void standardTokenizer(){
		HanLP.Config.ShowTermNature = false;

		String text = "林俊杰在上海市开演唱会啦";
		// 核心是：林俊杰，上海[市]，[开]演唱会
		// standard（TODO, 上海 是否可以搜索出来） >>>> 0-林俊杰,1-在,2-上海市,3-开,4-演唱会,5-啦
		// ik_max_word() >>>> 0-林俊杰,1-俊杰,2-在上,3-上海市,4-上海,5-海市,6-开演,7-演唱会,8-演唱,9-会,10-啦
		// ik_smart(语义完全错了) >>>> 0-林俊杰,1-在上,2-海市,3-开,4-演唱会,5-啦

		printTerms("standard", StandardTokenizer.segment(text));
	}

	private static void printTerms(String tokenizer, List<Term> terms){
		int index = 0;
		StringJoiner joiner = new StringJoiner(",");
		for (Term term : terms) {
			joiner.add((index++) + "-" + term);
		}

		System.out.printf("%s >>>> %s\n", tokenizer, joiner.toString());
	}

	public static void main(String[] args) {
		String json = "[\n" + "    {\n" + "      \"token\" : \"林俊杰\",\n" + "      \"start_offset\" : 0,\n"
				+ "      \"end_offset\" : 3,\n" + "      \"type\" : \"CN_WORD\",\n" + "      \"position\" : 0\n"
				+ "    },\n" + "    {\n" + "      \"token\" : \"在上\",\n" + "      \"start_offset\" : 3,\n"
				+ "      \"end_offset\" : 5,\n" + "      \"type\" : \"CN_WORD\",\n" + "      \"position\" : 1\n"
				+ "    },\n" + "    {\n" + "      \"token\" : \"海市\",\n" + "      \"start_offset\" : 5,\n"
				+ "      \"end_offset\" : 7,\n" + "      \"type\" : \"CN_WORD\",\n" + "      \"position\" : 2\n"
				+ "    },\n" + "    {\n" + "      \"token\" : \"开\",\n" + "      \"start_offset\" : 7,\n"
				+ "      \"end_offset\" : 8,\n" + "      \"type\" : \"CN_CHAR\",\n" + "      \"position\" : 3\n"
				+ "    },\n" + "    {\n" + "      \"token\" : \"演唱会\",\n" + "      \"start_offset\" : 8,\n"
				+ "      \"end_offset\" : 11,\n" + "      \"type\" : \"CN_WORD\",\n" + "      \"position\" : 4\n"
				+ "    },\n" + "    {\n" + "      \"token\" : \"啦\",\n" + "      \"start_offset\" : 11,\n"
				+ "      \"end_offset\" : 12,\n" + "      \"type\" : \"CN_CHAR\",\n" + "      \"position\" : 5\n"
				+ "    }\n" + "  ]";

		final JSONArray jsonArray = JSON.parseArray(json);

		int index = 0;
		StringJoiner joiner = new StringJoiner(",");
		for (Object o : jsonArray) {
			final JSONObject object = (JSONObject) o;
			joiner.add((index++) + "-" + object.getString("token"));
		}
		System.out.printf(">>>> %s\n", joiner.toString());
	}
}
