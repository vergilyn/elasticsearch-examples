package com.vergilyn.examples.analyzer.hanlp;

import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import org.junit.jupiter.api.Test;

public class PlaceTests {

	/**
	 * <a href="https://github.com/hankcs/HanLP/blob/v1.8.2/src/test/java/com/hankcs/demo/DemoPlaceRecognition.java">DemoPlaceRecognition.java</a>
	 */
	@Test
	public void test(){
		String text = "重庆市渝北区";

		System.out.println("disabled place >>>> " + StandardTokenizer.segment(text));

		StandardTokenizer.SEGMENT.enablePlaceRecognize(true);

		System.out.println("enabled place >>>> " + StandardTokenizer.segment(text));
	}
}
