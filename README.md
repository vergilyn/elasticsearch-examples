# elasticsearch-examples

2020-04-23 >>>> 重新整理，基于es v7.6.2

+ [elastic, guide]
+ [ik-analyzer][plugins, ik-analyzer]

扩展:
+ [Aliyun OpenSearch][aliyun, open-search]


[elastic, guide]: https://www.elastic.co/guide/index.html
[aliyun, open-search]: https://www.aliyun.com/product/opensearch/
[plugins, ik-analyzer]: https://github.com/medcl/elasticsearch-analysis-ik


### Logging
+ [java-rest-low-usage-logging.html](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-low-usage-logging.html)
 
The Java REST client uses the same logging library that the Apache Async Http Client uses: [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/), 
which comes with support for a number of popular logging implementations. 
The java packages to enable logging for are `org.elasticsearch.client` for the client itself 
and `org.elasticsearch.client.sniffer` for the sniffer.

The request tracer logging can also be enabled to log every request and corresponding response in curl format. 
That comes handy when debugging, for instance in case a request needs to be manually executed to check whether it still yields the same response as it did. 
Enable trace logging for the `tracer` package to have such log lines printed out. 
Do note that this type of logging is expensive and should not be enabled at all times in production environments, 
but rather temporarily used only when needed.


+ [Enabling ElasticSearch RestClient response logging when using elastic4s and logback?](https://stackoverflow.com/questions/58544365/enabling-elasticsearch-restclient-response-logging-when-using-elastic4s-and-logb)
> elasticsearch-client 默认使用的是 `Jdk14Logger`(commons-logging.jar)


```xml
<!-- 1. exclude `commons-logging` -->
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-client</artifactId>
    <version>${elasticsearch.version}</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- 2. dependency `jcl-over-slf4j` -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>${jcl-over-slf4j.version}</version>
</dependency>
```

然后就可以在`logback.xml`中配置：
```xml
<logger name="org.elasticsearch.client" level="TRACE"/>
```

remark:
1. elasticsearch 打印DSL `org.elasticsearch.client.RequestLogger#logResponse(...)`

FEATURE:
1. logger打印的request/response 并未格式化  
see: 
- org.elasticsearch.client.RestClient#convertResponse(...)
- org.elasticsearch.client.RequestLogger#logRespone(...)
- org.elasticsearch.client.RequestLogger#buildTraceRequest(org.apache.http.client.methods.HttpUriRequest, org.apache.http.HttpHost)
- org.elasticsearch.client.RequestLogger#buildTraceResponse(org.apache.http.HttpResponse)