package com.vergilyn.examples.analyzer.hanlp;

import java.util.List;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.dictionary.stopword.Filter;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.BasicTokenizer;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import org.junit.jupiter.api.Test;

public class StopwordTests {

	@Test
	public void test() {
		// HanLP.Config.enableDebug();

		String text = "重庆市的渝北区";

		// StandardTokenizer 默认未实现 stopwords逻辑。
		// StandardTokenizer.SEGMENT.enableStop..() 不存在，感觉是源码问题，可以参考·NotionalTokenizer.segment(...)`中的停词逻辑
		System.out.println(StandardTokenizer.segment(text));

		// 实词分词器，自动移除停用词
		System.out.println(NotionalTokenizer.segment(text));
	}

	/**
	 * <a href="https://github.com/hankcs/HanLP/blob/v1.8.2/src/test/java/com/hankcs/demo/DemoStopWord.java">DemoStopWord.java</a>
	 */
	@Test
	public void official() {
		String text = "小区居民有的反对喂养流浪猫，而有的居民却赞成喂养这些小宝贝";
		// 可以动态修改停用词词典
		CoreStopWordDictionary.add("居民");
		System.out.println(NotionalTokenizer.segment(text));

		CoreStopWordDictionary.remove("居民");
		System.out.println(NotionalTokenizer.segment(text));

		// 可以对任意分词器的结果执行过滤
		List<Term> termList = BasicTokenizer.segment(text);
		System.out.println(termList);
		CoreStopWordDictionary.apply(termList);
		System.out.println(termList);
		// 还可以自定义过滤逻辑
		CoreStopWordDictionary.FILTER = new Filter() {
			@Override
			public boolean shouldInclude(Term term) {
				if (term.nature == Nature.nz) {
					return !CoreStopWordDictionary.contains(term.word);
				}
				return false;
			}
		};
		System.out.println(NotionalTokenizer.segment(text));
	}

}
