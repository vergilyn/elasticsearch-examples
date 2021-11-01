package com.vergilyn.examples.analyzer.hanlp;

import java.io.IOException;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;

import org.junit.jupiter.api.Test;

public class AnalyzerTests {

	/**
	 * <a href="https://github.com/hankcs/HanLP/blob/v1.8.2/src/test/java/com/hankcs/demo/DemoCRFLexicalAnalyzer.java">DemoCRFLexicalAnalyzer.java</a>
	 *
	 * @see com.hankcs.hanlp.tokenizer.lexical.LexicalAnalyzer
	 * @see CRFLexicalAnalyzer#CRFLexicalAnalyzer(String cwsModelPath, String posModelPath, String nerModelPath)
	 */
	@Test
	public void analyzer() throws IOException {
		// 首次运行会自动建立模型缓存，为了避免你等得无聊，开启调试模式说点什么:-)
		HanLP.Config.enableDebug();

		// cwsModelPath, CRF分词器模型路径
		// posModelPath, CRF词性标注器模型路径
		// nerModelPath, CRF命名实体识别器模型路径
		CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer(HanLP.Config.CRFCWSModelPath,
		                                                     HanLP.Config.CRFPOSModelPath,
		                                                     HanLP.Config.CRFNERModelPath);

		String[] tests = new String[]{
				"应聘深圳事业单位交计划生育承诺书",
				"林俊杰在上海市开演唱会啦",
				"商品和服务",
				"上海华安工业（集团）公司董事长谭旭光和秘书胡花蕊来到美国纽约现代艺术博物馆参观",
				"微软公司於1975年由比爾·蓋茲和保羅·艾倫創立，18年啟動以智慧雲端、前端為導向的大改組。" // 支持繁体中文
		};
		for (String sentence : tests)
		{
			System.out.println(analyzer.analyze(sentence));
            // System.out.println(analyzer.seg(sentence));
		}
	}
}
