# vergilyn spring-data-elasticsearch extension

+ [github, spring-data-elasticsearch](https://github.com/spring-projects/spring-data-elasticsearch)
+ [spring-data-elasticsearch reference](https://spring.io/projects/spring-data-elasticsearch/)

2020-04-29 >>>>  
XXX: 
1. `spring-data-elasticsearch` v3.2.7.RELEASE(current) 才知道支持 elasticsearch v6.8.8。
（elasticsearch 已经 v7.6.2）

TODO:  
1. 根据`spring-boot-elasticsearch`扩展支持entity类似"index-name-{yyyyMMdd}"的操作(save/update/search)。


version: <https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#preface.versions>  

| Spring Data Release Train | Spring Data Elasticsearch | Elasticsearch | Spring Boot |
| --------- | ----- | ---- | ---- |
| Moore | 3.2.x | 6.8.8 | 2.2.x |
| Lovelace | 3.1.x | 6.2.2 | 2.1.x |
| Kay | 3.0.x | 5.5.0 | 2.0.x |
| Ingalls | 2.1.x | 2.4.0 | 1.5.x |



## FAQ

### 1. spring-data-elasticsearch client log
- <https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-custom-log-levels>
- <https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.logging>


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
TODO 2020-05-01 

### 2. annotation add index-alias?
+ [spring-data-elasticsearch, Define alias for document](https://jira.spring.io/browse/DATAES-192)
- [How to interact with elastic search Alias using Spring data](https://stackoverflow.com/questions/32015592/how-to-interact-with-elastic-search-alias-using-spring-data)

1. 不存在 `@Document(alias = "xxx")`
2. 不存在（类似 `@Setting`） `@Alias(path = """)` 

解析domain 生成index代码：  
- `org.springframework.data.elasticsearch.repository.support.AbstractElasticsearchRepository.AbstractElasticsearchRepository(org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation<T,ID>, org.springframework.data.elasticsearch.core.ElasticsearchOperations)`

底层并不存在添加`alias`的逻辑（只有 mapping & setting）！

### 3. dynamic index, ex "indexName-{yyyy-MM}"
- [How to give Index Alias in Domain class instead of index name in Spring-dat-elasticsearch](https://stackoverflow.com/questions/51648942/how-to-give-index-alias-in-domain-class-instead-of-index-name-in-spring-dat-elas)