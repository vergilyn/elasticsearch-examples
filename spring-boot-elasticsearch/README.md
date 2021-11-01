# spring-boot-elastic-search

### 2021-10-27
`spring-boot-autoconfigure-2.2.11.RELEASE.jar` 已经可以支持 `RestHighLevelClient`的 auto-configure。
- `org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration`
- `org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientConfigurations`

### 2019-06-05
spring-data-elasticsearch升级到`3.2.0.SNAPSHOT`，对应elasticsearch版本`6.5.0`。
其中的`Function Score Query`查询API有重大变化，参考: [Elasticsearch 6.1 TransportClient实现多条件重排序搜索查询](https://blog.csdn.net/xiaoll880214/article/details/86716393)

examples 中涉及需要更改的地方:
- com.vergilyn.examples.elasticsearch.service.impl.SingerServiceImpl#search(...)
- com.vergilyn.examples.elasticsearch.service.impl.CityServiceImpl#getCitySearchQuery(...)

暂时未了解，但参考csdn的blog感觉这写法太复杂。

### 说明
因为本地装的elasticsearch-6.4.2（可能需要elasticsearch-2.4.6），所以这些代码并没完整测试，只是提供参考。
（运行测试时，并不会使用本地的elasticsearch-6.4.2，而是使用springboot嵌入的内存服务器作为elasticsearch-client）
springboot提供了starter来支持spring-data-elasticsearch。但是，springboot的更新速度赶不上spring-data-elasticsearch的版本。
而且，spring-data-elasticsearch的更新跟不上elasticsearch，所以还是建议使用ES官方推荐的`Rest-High-Java-Client`。

此问题参考：[springboot使用elasticsearch踩得坑（Windows版）](https://blog.csdn.net/wangh92/article/details/79204163)

>   https://docs.spring.io/spring-boot/docs/1.5.14.RELEASE/reference/htmlsingle/#boot-features-connecting-to-elasticsearch-spring-data
>   You can inject an auto-configured `ElasticsearchTemplate` or Elasticsearch `Client` instance as you would any other Spring Bean.
>   By default the instance will embed a local in-memory server (a `Node` in Elasticsearch terms) and use the current working directory as the home directory for the server.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```
- https://github.com/spring-projects/spring-data-elasticsearch
- https://spring.io/projects/spring-boot#learn

#### 2018-07-05
| spring data elasticsearch | elasticsearch |
|:-------------------------:|:-------------:|
| 3.1.x                     | 6.2.2         |
| 3.0.x                     | 5.5.0         |
| 2.1.x                     | 2.4.0         |
| 2.0.x                     | 2.2.0         |
| 1.3.x                     | 1.5.2         |

elasticsearch当前GA：6.3.0，但spring-data-elasticsearch的3.1.x还是snapshot并且才支持到elasticsearch-6.2.2。  
spring-boot当前GA：1.5.14、2.0.3，不打算用2.x，而1.5.14中的spring-data-elasticsearch：2.1.13，elasticsearch：2.4.6。    
如果引入spring-data-elasticsearch的当前GA-3.0.7，会导致spring-boot的autoconfigure错误（可以通过手动configure解决）。  


### 问题
1. spring-data-elasticsearch的`@Setting`、`@Mapping`

```java
/**
 * 当设置了@Mapping、@Setting时, 只会用其中的json信息. 根本不会去读取@Field的设置; <br/>
 *  (但, @Document 中部分设置有效, 比如indexName、type. 因为这两个值貌似不能在Setting/Mapping中指定)<br/>
 * 虽然不确定, 但最好只在同一地方做Setting/Mapping的配置.
 */
@Document(indexName = "test_settings_index"
        , type = "test_settings_type"
        , shards = 3
        , replicas = 0
        , refreshInterval = "10s"
        , indexStoreType = "fs"
)
@Setting(settingPath = "test_settings.json")
@Mapping(mappingPath = "test_mappings.json")
public class TestSettingsDoc {

    private Long id;

    @Field(type = FieldType.String, index = FieldIndex.analyzed, analyzer = "ik_smart")
    private String name;

    // FIXME vergilyn_analyzer定义在test_settings.json中，exception：
    @Field(type = FieldType.String, index = FieldIndex.analyzed, analyzer = "vergilyn_analyzer")
    private String desc;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, analyzer = "ik_max_word")
    private String newField;

    // 省略getter/setter
}
```

```json
// test_settings.json
{
  "number_of_shards": 2,
  "number_of_replicas": 0,

  "analysis": {
    "analyzer": {
      "vergilyn_analyzer": {
        "tokenizer": "ik_max_word",
        "char_filter": [
          "html_strip"
        ],
        "type": "custom"
      }
    }
  }
}
```

```json
// test_mappings.json
{
  "test_settings_type": {
    "_all": {
      "analyzer": "vergilyn_analyzer"
    },
    "dynamic": "strict",

    "properties": {
      "name": {
        "type": "string",
        "index": "no",
        "analyzer": "standard"
      },
      "desc": {
        "type": "string",
        "index": "analyzed",
        "analyzer": "ik_smart"
      }
    }
  }
}
```

在之前用es-2.4.6时，没有问题。但升级到es-6.4.1时却会抛异常：`Custom Analyzer [vergilyn_analyzer] failed to find tokenizer under name [ik_max_word]`。
特别注意：之前的es存在analyzer、analyzer_not、no，新版本的es只有true/false。

### 参考
1. github：https://github.com/JeffLi1993/springboot-learning-example   
其中的：[springboot-elasticsearch]、[spring-data-elasticsearch-crud]、[spring-data-elasticsearch-query]  

[springboot-elasticsearch]: https://github.com/JeffLi1993/springboot-learning-example/tree/master/springboot-elasticsearch
[spring-data-elasticsearch-crud]: https://github.com/JeffLi1993/springboot-learning-example/tree/master/spring-data-elasticsearch-crud
[spring-data-elasticsearch-query]: https://github.com/JeffLi1993/springboot-learning-example/tree/master/spring-data-elasticsearch-query
