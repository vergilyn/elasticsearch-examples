# vergilyn spring-data-elasticsearch extension

+ [github, spring-data-elasticsearch](https://github.com/spring-projects/spring-data-elasticsearch)
+ [spring-data-elasticsearch reference](https://spring.io/projects/spring-data-elasticsearch/)

**WARNING:**   
`spring-data-elasticsearch` 使用的是 v3.2.3，其依赖 elasticsearch v6.8。  
但elasticsearch强制使用 v7.6.2，会导致 spring-data-elasticsearch 部分API不可用！

version: <https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#preface.versions>  

| Spring Data Release Train | Spring Data Elasticsearch | Elasticsearch | Spring Boot |
| --------- | ----- | ---- | ---- |
| Moore | 3.2.x | 6.8.8 | 2.2.x |
| Lovelace | 3.1.x | 6.2.2 | 2.1.x |
| Kay | 3.0.x | 5.5.0 | 2.0.x |
| Ingalls | 2.1.x | 2.4.0 | 1.5.x |


## TODO
1. spring-data-elasticsearch 扩展 `EntityMapper`，所有的document包含metadata
2. index alias
3. `copy_to` or `field.keyword`


## FAQ

### 1. spring-data-elasticsearch client log
- <https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-custom-log-levels>
- <https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.logging>

```yaml
# application.yaml
logging:
  level:
    tracer: trace
```

**1.1 方案一**  
- [关于Spring-JCL日志的坑](https://www.jianshu.com/p/e254c4783a5d)

`spring-core` 依赖 `spring-jcl`，试着排除`spring-jcl`。并指定用自定义的`logback.xml`：  
```yaml
logging:
  config: classpath:logback.xml
```

**缺点**：因为是 spring-boot 项目，不必要的log太多，需要自己定义。

**1.2 方案二**  
还是期望通过 spring-boot 修改。
2020-05-02 >>>>
```properties
logging.level.tracer = trace
``` 

好蠢！！！
通过`org.elasticsearch.client.RequestLogger` -> `LogFactory.getLog("tracer")`!  
前后花了几个小时，其实很简单...回过头去，才明白elasticsearch官网说的什么...

+ [java-rest-low-usage-logging.html](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-low-usage-logging.html)
 
The Java REST client uses the same logging library that the Apache Async Http Client uses: [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/), 
which comes with support for a number of popular logging implementations. 
The java packages to enable logging for are `org.elasticsearch.client` for the client itself 
and `org.elasticsearch.client.sniffer` for the sniffer.

The request tracer logging can also be enabled to log every request and corresponding response in curl format. 
That comes handy when debugging, for instance in case a request needs to be manually executed to check whether it still yields the same response as it did. 
Enable trace logging for the **`tracer`** package to have such log lines printed out. 
Do note that this type of logging is expensive and should not be enabled at all times in production environments, 
but rather temporarily used only when needed.

### 2. annotation add index-alias? (TODO)
+ [spring-data-elasticsearch, Define alias for document](https://jira.spring.io/browse/DATAES-192)
- [How to interact with elastic search Alias using Spring data](https://stackoverflow.com/questions/32015592/how-to-interact-with-elastic-search-alias-using-spring-data)

1. 不存在 `@Document(alias = "xxx")`
2. 不存在（类似 `@Setting`） `@Alias(path = """)` 

解析domain 生成index代码：  
- `org.springframework.data.elasticsearch.repository.support.AbstractElasticsearchRepository.AbstractElasticsearchRepository(org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation<T,ID>, org.springframework.data.elasticsearch.core.ElasticsearchOperations)`

底层并不存在添加`alias`的逻辑（只有 mapping & setting）！

### 3. dynamic index, ex "indexName-{yyyy-MM}"
- [How to give Index Alias in Domain class instead of index name in Spring-dat-elasticsearch](https://stackoverflow.com/questions/51648942/how-to-give-index-alias-in-domain-class-instead-of-index-name-in-spring-dat-elas)

结合SpEL实现。

### 4. spring-boot-2.x LocalDateTime
- [springboot2 LocalDateTime类型未生效](https://blog.csdn.net/jieyanqulaopo123/article/details/105547050)
- [如何使Spring Data Elasticsearch与java.time.LocalDat...](http://www.cocoachina.com/articles/40857)

```java
@Setting(settingPath = "spring-data-elasticsearch_settings.json")
@Document(indexName = ArticleDocument.ES_INDEX)
@Data
public class ArticleDocument {

    @Field(name = "publish_time", type = FieldType.Date,
            format = DateFormat.custom, pattern = DATETIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime publishTime;
}
```

### 5. `copy_to` or `field.keyword` (TODO)
ex. `ArticleDocument.class`  
```json
{
	"mapping": {
		"title": {
			"type": "text",
			"analyzer": "ik_max_word",
			"search_analyzer": "ik_smart_synonym",
			"fields": {
				"keyword": {
					"type": "keyword"
				}
			}
		},
		"list_title": {
			"type": "text",
			"analyzer": "ik_max_word",
			"search_analyzer": "ik_smart_synonym",
			"fields": {
				"keyword": {
					"type": "keyword"
				}
			}
		}
	}
}
```