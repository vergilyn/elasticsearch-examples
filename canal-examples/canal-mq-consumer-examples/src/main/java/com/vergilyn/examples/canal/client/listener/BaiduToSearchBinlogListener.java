package com.vergilyn.examples.canal.client.listener;

import com.alibaba.fastjson.JSON;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@RocketMQMessageListener(
		consumerGroup = "${spring.application.name}",
		topic = BaiduToSearchBinlogListener.TOPIC,
		selectorExpression = "canal_server_tag"
)
@Component
public class BaiduToSearchBinlogListener implements RocketMQListener<MessageExt> {
	protected static final String TOPIC = "CANAL_BINLOG_canal_listener_binlog_t_baidu_top_search";

	@Override
	public void onMessage(MessageExt message) {

		System.out.printf("body >>>> %s \n", new String(message.getBody()));

		System.out.printf("message >>>> %s \n", JSON.toJSONString(message, true));
	}
}
