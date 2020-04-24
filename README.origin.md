# ElasticSearch Examples

source-github: https://github.com/SpringDataElasticsearchDevs/spring-data-elasticsearch-sample-application  


## 1. ElasticSearch 基础

### 1.1 理论参考  
  + [全文搜索引擎 Elasticsearch 入门教程 - 阮一峰的网络日志](http://www.ruanyifeng.com/blog/2017/08/elasticsearch.html)  
  + Elasticsearch 权威指南（中文版）: [GitHub](https://github.com/looly/elasticsearch-definitive-guide-cn)、[HTML][elasticsearch-guide]
  + spring-data-elasticsearch: [中文版](https://es.yemengying.com/), [english](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)  
  + [Elasticsearch系列基础教程](http://www.sojson.com/blog/85.html)

### 1.2 代码参考:  
  + [入门整合案例(SpringBoot+Spring-data-elasticsearch)](http://www.tianshouzhi.com/api/tutorials/elasticsearch/159): 很在意其中说的一句话, 对@Field注解默认值的说明
  + [JeffLi1993/springboot-learning-example](https://github.com/JeffLi1993/springboot-learning-example)  
  + [spring-data-elasticsearch-sample-application](https://github.com/SpringDataElasticsearchDevs/spring-data-elasticsearch-sample-application)  
       

## 3. ElasticSearch SpringBoot
### 3.1 spring-boot
见[spring boot](https://github.com/spring-projects/spring-boot).
### 3.2 spring-boot-starter-data-elasticsearch (spring-data-elasticsearch)
具体的Reference Documentation、API Documentation等参考[spring-data-elasticsearch][spring-data-elasticsearch]  
备注: 版本默认依赖  
> spring-boot-starter-data-elasticsearch(1.5.9.RELEASE)  
> spring-data-elasticsearch(2.1.9.RELEASE)  
> org-elasticsearch-elasticsearch(2.4.6)  
> lucene(5.5.4)  
> jackson(2.8.10)  
> netty(3.10.6)  
 
### 3.3 application.properties
> 
> ELASTICSEARCH ([ElasticsearchProperties](https://github.com/spring-projects/spring-boot/blob/v1.5.9.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchProperties.java))  
> spring.data.elasticsearch.cluster-name=elasticsearch # Elasticsearch cluster name.  
> spring.data.elasticsearch.cluster-nodes= # Comma-separated list of cluster node addresses. If not specified, starts a client node.  
> spring.data.elasticsearch.properties.*= # Additional properties used to configure the client.  
> spring.data.elasticsearch.repositories.enabled=true # Enable Elasticsearch repositories.  
 
> 
> JEST (Elasticsearch HTTP client) ([JestProperties](https://github.com/spring-projects/spring-boot/blob/v1.5.9.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/elasticsearch/jest/JestProperties.java))  
> spring.elasticsearch.jest.connection-timeout=3000 # Connection timeout in milliseconds.  
> spring.elasticsearch.jest.multi-threaded=true # Enable connection requests from multiple execution threads.  
> spring.elasticsearch.jest.password= # Login password.  
> spring.elasticsearch.jest.proxy.host= # Proxy host the HTTP client should use.  
> spring.elasticsearch.jest.proxy.port= # Proxy port the HTTP client should use.  
> spring.elasticsearch.jest.read-timeout=3000 # Read timeout in milliseconds.  
> spring.elasticsearch.jest.uris=http://localhost:9200 # Comma-separated list of the Elasticsearch instances to use.  
> spring.elasticsearch.jest.username= # Login user.  

### 3.4 配置注意事项
1. `spring.data.elasticsearch.cluster-name`要与elasticsearch.yml(elasticsearch/bin/config)中`cluster.name: my-application`一致.  
2. elasticsearch.yml中:  
  "network.host: 127.0.0.1": 表示允许请求的ip, 实际应用中最好设置.  
  
elasticsearch.yml配置介绍:  
  + [Elasticsearch Reference](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)  
  + [Elasticsearch Network Settings](https://www.cnblogs.com/xiaoheike/p/5750222.html)  
  + [ELK学习系列文章第二章：elasticsearch常见错误与配置简介](http://blog.csdn.net/qq_21387171/article/details/53577115)  
  
  
## 4. ElasticSearch与DataBase数据同步
使用官方推荐的[logstash][logstash-download], 及其扩展插件[logstash-input-jdbc][logstash-inputs-jdbc guide].  
同步实现思路: 待定.  
参考:  
  + [elasticsearch-jdbc实现MySQL同步到ElasticSearch深入详解](http://blog.csdn.net/laoyang360/article/details/51694519)  
  + [http://blog.csdn.net/qq_38046109/article/details/70224133](http://blog.csdn.net/qq_38046109/article/details/70224133)
  + [elasticsearch-jdbc填坑记](https://www.jianshu.com/p/cc7fd8bcea07?from=timeline)  
  + [ElasticSearch学习笔记（三）logstash安装和logstash-input-jdbc插件](http://blog.csdn.net/q15150676766/article/details/75949679)  
  

## 5. ElasticSearch具体实现"搜索"思路
比如[博客园的搜索文章](http://zzk.cnblogs.com/s/blogpost?Keywords=lucene), 假定搜索的Field: 博客标题articleTitle、博客内容articleContent、博客标签articleTag.  
我现在的想法是:  
  1. 我只会第一次接入elastic时同步一次数据库数据到 elastic-server.  
  
  2. 以后新增or更新博客, 通过新增代码逻辑, 每次发送请求给elastic-server,  
   大致"_source":{"articleId":"...", "articleTitle":"...","articleContent":"...","articleTag":"..."};  
  在elastic-server返回success才保存博客到database, 否则回滚事物. （不知道这有没有更好的实现）  
  
  3. 搜索时: 搜索出符合的结果, 然后用articleId去走正常的"查看博客逻辑", 即elastic的目的只是得到"_source"中的"articleId".  
    如果是如app中的列表展示, 比如每页10条, 我会用这10个articleId去组装列表数据, 即直接用这10个id去查数据库来获得列表数据.  
    
  4. 删除: 我会根据articleId去删除 elastic-server中的数据(document?).

(暂时思路是这样的, 具体不知道别人是怎么用的, 待优化吧.)

## 6 映射Mapping
参考: [Elasticsearch: 权威指南 » 基础入门 » 映射和分析 » 映射](https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping-intro.html)  
查看映射: localhost:9200/{indexName}/_mapping  
> mapping信息可以自动创建，但是不能自动更新，也就是说，如果需要重新进行mapping映射的话，需要将原来的删除，再进行mapping映射。  

### 6.1 修改映射
如果是新增mapping的field, 还比较好做. 但如果想把已有的String分词改成"IK", 则很麻烦.  
一般的做法(见下面参考链接), 会新创建一个index, 指定新的mapping, 把原有数据迁移过来.  
参考:  
  + [elasticsearch更改mapping，不停服务重建索引（转）](https://www.cnblogs.com/ajianbeyourself/p/5548497.html)  
  + [elasticsearch-不停服务修改mapping](http://blog.csdn.net/jingkyks/article/details/41513063)  
  + [Elasticsearch 的坑爹事——记录一次mapping field修改过程](https://www.cnblogs.com/Creator/p/3722408.html)  
  
### 6.2 `@Document`与`@Field`(`Singer.java`)
> 加上了@Document注解之后，默认情况下这个实体中所有的属性都会被建立索引、并且分词。  
> 我们通过@Field注解来进行详细的指定，如果没有特殊需求，那么只需要添加@Document即可。  

> 需要注意的是，这些默认值指的是我们没有在我们没有在属性上添加@Filed注解的默认处理。  
> 一旦添加了@Filed注解，所有的默认值都不再生效。此外，如果添加了@Filed注解，那么type字段必须指定。  

## 7 index与type的区别与抉择
详见: [Index 和 Type 的区别](https://www.cnblogs.com/nicebaby/p/7828057.html)  
### 7.1 index
> Index 存储在多个分片(shards)中，其中每一个分片都是一个独立的 Lucene Index。  
> 这就应该能提醒你，添加新 index 应该有个限度：每个 Lucene Index 都需要消耗一些磁盘，内存和文件描述符。  
> 因此，一个大的 index 比多个小 index 效率更高：Lucene Index 的固定开销被摊分到更多文档上了。  
> 
> 另一个重要因素是你准备怎么搜索你的数据。  
> 在搜索时，每个分片都需要搜索一次， 然后 ES 会合并来自所有分片的结果。  
> 例如，你要搜索 10 个 index，每个 index 有 5 个分片，那么协调这次搜索的节点就需要合并 5x10=50 个分片的结果。  
> 这也是一个你需要注意的地方：如果有太多分片的结果需要合并，或者你发起了一个结果巨大的搜索请求，合并任务会需要大量 CPU 和内存资源。这是第二个让 index 少一些的理由。  

### 7.2 type
> 使用 type 的一个好处是，搜索一个 index 下的多个 type，和只搜索一个 type 相比没有额外的开销 —— 需要合并结果的分片数量是一样的。  
> 不同 type 里的字段需要保持一致。例如，一个 index 下的不同 type 里有两个名字相同的字段，他们的类型（string, date 等等）和配置也必须相同。  
> 只在某个 type 里存在的字段，在其他没有该字段的 type 中也会消耗资源。  
> 有同一个 index 的中的 type 都有类似的映射 (mapping) 时，才应该使用 type。  

### 7.3 Type Takeaways (type结论)
　　see: [中文][cn-mapping], [English][en-mapping]  

[en-mapping]: https://www.elastic.co/guide/en/elasticsearch/guide/current/mapping.html
[cn-mapping]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping.html

　　那么, 这个讨论的结论是什么? 
　　技术上讲, 多个`type`可以在相同的`index`中存在, 只要它们的`field`不冲突(要么因为`field`是相互独占, 要么因为它们共享相同的`field`).  

　　**重要**:  
  　　`type`可以很好的区分同一个集合中的不同细分. 在不同的细分中数据的整体模式是相同的(或相似的).  
  　　如果两个`type`的`fields`是互不相同的, 意味着`index`中将有一半的数据是空的(一样会占用资源), 最终将导致性能问题。在这种情况下, 最好是使用两个单独的`index`.

　　(抉择? 按我现在的需求, 我只需要1个`index`和几个`type`. 所以, 现在的选择是冗余多个`type`到1个`index`)

## 8 数据结构
参考: [数据结构](https://www.elastic.co/guide/cn/elasticsearch/guide/current/modeling-your-data.html)  
### 8.1 期望
数据结构示例:
```
 // ColumnInfo: 栏目信息
 {
    "columnId": "Long, 栏目id",
    "columnTitle": "String, 栏目列表文章标题",
    "columnSummary": "String, 栏目列表文章摘要",
    "columnOnlineTime": "Date, 当前时间大于或等于此时间才为有效数据",
    "articleId": "Long, 关联文章",
    "columnOther": "栏目其他需要索引的field"
 }
``` 
```
 // Article: 文章信息
 {
    "articleId": "Long, 文章id",
    "articleTitle": "String, 文章标题",
    "articleSummary": "String, 文章摘要",
    "articleContent": "String, 文章内容",
    "articleOnlineTime": "Date, 当前时间大于或等于此时间才才为有效数据",
    "articleOther": "文章其他需要索引的field"
 }
```
`ColumnInfo` -> `Article`: ManyToOne, 即一篇文章可以在不同栏目下.  
案例情况:  
  1. 先新增`Article`, 在根据情况推送到不同的`ColumnInfo`;  
  2. `Article`、`ColumnInfo`更新频率很小.  
  3. `Article`、`ColumnInfo`的关联关系数不会太多, 90%情况是1对1, 基本在\[1,10];  
期望效果:  
  1. 搜索Field: `columnTitle`、`columnSummary`、`articleTitle`、`articleSummary`、`articleContent`.  
  2. 分页获取`columnId`, 且当前时间 ≥ `columnOnlineTime and articleOnlineTime`.  
  3. 数据必须实时性.  
(新增`Article`可以不创建elasticsearch, 等推送到某个栏目时才创建`Article`与`ColumnInfo`.)  

### 8.2 应用层联接Application-side Joins
详见: [中文](https://www.elastic.co/guide/cn/elasticsearch/guide/current/application-joins.html)、[English](https://www.elastic.co/guide/en/elasticsearch/guide/current/application-joins.html)  
本案例个人不推荐采用此数据模型(也想不到什么情形下会选择此模型).  

### 8.3 非规范化你的数据Denormalizing Your Dataedit
详见: [中文](https://www.elastic.co/guide/cn/elasticsearch/guide/current/denormalization.html)、[English](https://www.elastic.co/guide/en/elasticsearch/guide/current/denormalization.html#denormalization)  
即数据库的反范式设计, 把需要的字段冗余到需要的表.  
```
 // ColumnArticle: ColumnInfo冗余需要的Article字段
 {
    "columnId": "Long, 栏目id",
    "columnTitle": "String, 栏目列表文章标题",
    "columnSummary": "String, 栏目列表文章摘要",
    "columnOnlineTime": "Date, 当前时间大于或等于此时间才为有效数据",

    "articleId": "Long, 文章id",
    "articleTitle": "String, 文章标题",
    "articleSummary": "String, 文章摘要",
    "articleContent": "String, 文章内容",
    "articleOnlineTime": "Date, 当前时间大于或等于此时间才才为有效数据",
 }
```
优点:  
  1. 避免了关联(或联接)查询, 查询速度快.  
  2. 分页, 条件查询都很简单.  
  
缺点:  
  1. 更新`ColumnInfo`时, 只需更新对应的单个`ColumnArticle`; 但若更新`Article`, 则所有符合的`ColumnArticle`都要更新.  
  
结论:  
  可用此方案, 简单易懂. 
  流程逻辑:  
    1. 新增or更新`ColumnInfo`, 根据`columnId`&`articleId`判断是否存在. 不存在, 创建; 存在, 更新(或删除已有, 再创建);
    2. 更新`Article`, 根据`articleId`找到所有的`ColumnArticle`, 更新article-field(或, 全部删除再创建).
    3. 删除`ColumnInfo`, 根据`columnId`&`articleId`删除. 删除`Article`, 根据`articleId`找到所有的`ColumnArticle`执行删除.


### 8.4 嵌套对象Nested Objects
详见: [中文](https://www.elastic.co/guide/cn/elasticsearch/guide/current/nested-objects.html)、[English](https://www.elastic.co/guide/en/elasticsearch/guide/current/nested-objects.html)  
```
 // NestedObjects: 
 {
    "articleId": "Long, 文章id",
    "articleTitle": "String, 文章标题",
    "articleSummary": "String, 文章摘要",
    "articleContent": "String, 文章内容",
    "articleOnlineTime": "Date, 当前时间大于或等于此时间才才为有效数据",
    
    "columns":[
        {
            "columnId": "Long, 栏目id",
            "columnTitle": "String, 栏目列表文章标题",
            "columnSummary": "String, 栏目列表文章摘要",
            "columnOnlineTime": "Date, 当前时间大于或等于此时间才为有效数据"
        },
        {...}
    ]
 }
```
优点:  
  1. 结构清晰, 符合正常逻辑.
  2. 跟`Denormalizing Your Dataedit`差不多, 所有信息都在一个`document`中, 查询效率很高.  
  
备注:  
  1. > 嵌套文档是隐藏存储的,我们不能直接获取或查询。
  2. > 在独立索引每一个嵌套对象后,对象中每个字段的相关性得以保留。我们查询时,也仅仅返回那些真正符合条件的文档。  
  3. > 由于嵌套文档直接存储在文档内部,查询时嵌套文档和根文档联合成本很低,速度和单独存储几乎一样。
  4. > 如果要增删改一个嵌套对象,我们必须把整个文档重新索引才可以。
  5. > 重要, 查询的时候返回的是整个文档,而不是嵌套文档本身。  
  
缺点:  
  1. 要增删改一个嵌套对象,我们必须把整个文档重新索引才可以。  
  2. 查询的时候返回的是整个文档,而不是嵌套文档本身。 (以下理解不一定对) 造成的结果是: 返回的文档中的嵌套对象不一定满足条件.  
  
结论: 
  无法满足需求, 无法分页, 效果还不如`Denormalizing Your Dataedit`. 
  
### 8.5 父-子关系文档Parent-Child Relationship
详见: [中文](https://www.elastic.co/guide/cn/elasticsearch/guide/current/parent-child.html)、[English](https://www.elastic.co/guide/en/elasticsearch/guide/current/parent-child.html)  
数据结构类似`Nested Objects`, 区别:  
  + `Nested Objects`: 对象都是在同一个文档中.
  + `Parent-Child`: 父对象和子对象都是完全独立的文档.  
  
备注:  
  1. > 更新父文档时，不会重新索引子文档.   
  2. > 创建，修改或删除子文档时，不会影响父文档或其他子文档。这一点在这种场景下尤其有用：子文档数量较多，并且子文档创建和修改的频率高时.  
  3. > 子文档可以作为搜索结果独立返回.  
  4. > Elasticsearch 维护了一个父文档和子文档的映射关系，得益于这个映射，父-子文档关联查询操作非常快.  
         导致的问题:  
           a) 父文档和其所有子文档，都必须要存储在同一个分片(shared)中;  
           b) 若想要改变一个子文档的 parent 值，仅通过更新这个子文档是不够的. 因为新的父文档有可能在另外一个分片上。因此，必须要先把子文档删除，然后再重新索引这个子文档。  
  5. > 父子关系更适合于父文档少、子文档多的情况。(且不要父子层级过多, 出现过多的祖辈)  
  
优点:  
  1. 父对象和子对象都是完全独立的文档, 互不影响.
  2. 查询速度足够优秀.  
  
缺点:  
  理解不够, 暂时不清楚. 个人猜测, 可能创建速度相对较慢.    
  
结论:  
  在本案例中只有一个问题: 因为一部分搜索Filed在父文档, 一部分在子文档. 要如何查询?  
  `articleOnlineTime`可以冗余到子文档便于查询判断, 但要注意更新维护.  
  但其余Field要怎么做"聚合查询", 然后返回`columnId`的分页结果?  
  
  
## 9 `Field`中的`index`、`analyzer`、[`searchAnalyzer`][search-analyzer], 及`store` 和 `_source`、`_all`
强烈推荐仔细理解: [图解Elasticsearch中的_source、_all、store和index属性][csdn-62233031]  
备注: 以下很多是摘自blog, 其中的"文档"并不是指一个文件, 而是es中的`document`.  

[csdn-62233031]: http://blog.csdn.net/napoay/article/details/62233031
[search-analyzer]: https://www.elastic.co/guide/en/elasticsearch/reference/current/search-analyzer.html

### 9.1 `Field.index`
　　elasticsearch中的Field.index默认只有3种:  
　　　　1. (default) analyzed: 根据指定的`analyzer`解析字符串, 并创建倒排索引, 对应的field能被搜索.  
　　　　2. not_analyzed: 以完整的值创建倒排索引, 对应的field能被搜索.  
　　　　3. no: 不创建倒排索引, field不能被搜索.  

#### 9.1.1 Field.index.analyzed (default)
　　仅当`analyzed`时, 才会以`analyzer`分词器创建`倒排索引`, 其中:  
　　　　`analyzer`: 表示创建`倒排索引`时指定的分词器.  
　　　　`searchAnalyzer`: (个人理解) 要明确`倒排索引`始终只被`analyzer`分词器创建, 此`searchAnalyzer`只是用于重新指定`query_string`字符串的分词器(当search_analyzer缺省, 则默认即analyzer), 然后再去匹配已创建好的`倒排索引`.    
  
　　eg. "美利坚合众国"  
　　　　"ik_smart": "美利坚合众国" ;  
　　　　"ik_max_word": "美利坚合众国"、"美利坚合"、"美利坚"、"坚"、"合众国"、"合众"、"国";
　　　　1) 当`analyzer = ik_smart`、`searchAnalyzer = ik_max_word`时, 只有`query_string = 美利坚合众国`, 才能搜索到结果;  
　　　　2) 当`analyzer = ik_max_word`、`searchAnalyzer = ik_smart`时:  
　　　　　　a) 若`query_string = 美利|美|利`, 无法得到结果. 原因, "美利"被ik_smart分成"美"、"利", 而这2个term在"ik_max_word"创建的倒排索引中均匹配不到.  
　　　　　　b) 若`query_string = 美坚|坚烦`, 却可以得到结果. 原因, "美坚" -> "美|坚", "坚烦" -> "坚|烦", 因为"坚"在倒排索引存在.  
　　　　　　所以, 我的理解, `search_analyzer`并不是重新分词`_source.field`, 而只是用`search_analyzer`去分词`query_string`, 在用此分词结果去搜索`analyzer`已创建好的倒排索引.  
　　　　3) 同上, 即使`analyzer = searchAnalyzer = ik_max_word`, "美利"依旧无法匹配到结果.              

#### 9.1.2 Field.index.not_analyzed
　　会创建`倒排索引`, 即表示能被搜索.  
　　只是此搜索值必须精确匹配(完整匹配), 即使设置`analyzer = ik_max_word`, 也不会以`analyzer`进行分词创建`倒排索引`.  
　　但是, 设置了`analyzer|searchAnalyzer`却会影响到搜索结果.  
  
　　eg. "坂井泉水"  
　　　　"ik_smart": "坂"、"井"、"泉水";  
　　　　"ik_max_word": "坂"、"井"、"泉水、"泉"、"水";  
　　　　"standard"(默认分词器): "坂"、"井"、"泉"、"水";  
　　　　"whitespace"(elasticsearch提供的空白分词器): "坂井泉水";  

　　　　1) 设置`analyzer, searchAnalyzer = ik_max_word|ik_smart`(如果在_plugin/HEAD查看mapping, 并不会显示这2个属性配置, 但我下面又验证这2个属性不同会造成不同结果),  
　　　　　　a) 以`term`搜索"坂井泉水", 能匹配到结果. 但, 如果是"坂井|泉水", 则无法匹配到. **证明, `倒排索引`中的`term = 坂井泉水`**  
　　　　　　b) 以`query_string`搜索"坂井泉水", 无法匹配到结果. 因为`ik_max_word|ik_smart`都不会有`term = 坂井泉水`. **证明, `analyzer|searchAnalyzer`还是有用**  
　　　　2) 设置`analyzer, searchAnalyzer = standard`, 依旧无法匹配结果. (在_plugin/HEAD查看mapping无法看到这配置)   
　　　　3) 设置`analyzer = xx, searchAnalyzer = whitespace`, 搜索"坂井泉水"可以正常匹配结果. (在_plugin/HEAD查看mapping无法看到这配置)   

#### 9.1.3 Field.index.no
　　不会创建`倒排索引`, 该field的值无法被用于搜索.  `analyzer|searchAnalyzer`设置后没任何影响.  

### 9.2 Field.store true(yes)|false(no), 默认false(no)
　　推荐先浏览: [详解ElasticSearch的store属性](http://blog.csdn.net/liyantianmin/article/details/52531310)  
　　1) `_source`: 默认是存储的; 2) `Field.store`: 默认是不存储的(false).  
　　若`Field.store = true`是什么情况?  
>　"es中默认的设置_source是enable的,存储整个文档的值. 这意味着在执行search操作的时候可以返回整个文档的信息.  
>　　如果不想返回这个文档的完整信息, 也可以指定要求返回的field, es会自动从_source中抽取出指定field的值返回(比如说highlighting的需求)."  

>  若"field_A, store = true", 这意味着"field_A"的数据将会被单独存储. 此时若果想获取"field_A", es会分辨出"field_A"不是存储在"_source"而是被单独存储.  
>　　因此es不会从"_source"中加载"field_A", 而是从"field_A"的存储块中加载.  
>　　影响, 从"_source"获取只产生1次I/O操作, 但从"field_A的存储块中加载"会多产生1次I/O. 此时就产生了抉择.  

　　因为, 从"_source"获取值是快速而且高效, 所以基本不会设置"field_X, store = true". 但下列情况可能(上面blog中总结的, 自身还没体验和理解):  
　　　　1) "如果你的文档(数据)长度很长, 存储_source或者从_source中获取field的代价很大, 你可以显式的将某些field的store属性设置为yes."  
　　　　2) "还有一种情形: reindex from some field, 对某些字段重建索引的时候. 从_source中读取数据然后reindex, 和从某些field中读取数据相比, 显然后者代价更低. 这些字段store设置为yes比较合适."
　　
### 9.3 _source 与 Field.store
　　前面2篇blog都有说道, 结合着说下自己的理解.  
　　1) [详解ElasticSearch的store属性](http://blog.csdn.net/liyantianmin/article/details/52531310) 最后的总结很有用:  
　　　　a) 如果对某个field做了索引, 则可以查询. 如果store = true(yes), 则可以展示该field的值. (mark: 我没理解该博主说的"可以展示该field的值", "展示"指的什么?)  
　　　　b) 但是如果你存储了这个document的数据(_source = enable), 即使store = false(no), 仍然可以获得field的值(client去解析). (个人理解, 上面说的"展示", 应该就是获取field的值吧)  
　　　　c) 所以一个store = false(no)的field, 如果_source被disable, 则只能检索不能展示.  
  
　　`c)`对于优化很有意义, 正如另一篇blog中写的:  
　　　　_source字段默认是存储的, 什么情况下不用保留_source字段?  
　　　　如果某个字段内容非常多, **业务里面只需要能对该字段进行搜索, 最后返回文档(数据)id, 查看文档内容会再次到DB中取数据.**   
　　　　此时把大字段的内容存在elasticsearch中只会增大索引, 这一点文档数量越大结果越明显, 如果一条文档节省几KB, 放大到亿万级的量结果也是非常可观的.   
　　另外, "_source"可以指定需要存储的field(includes), 或排除不需要存储的field(excludes). 也可, 只返回指定的field(fields). (具体教程查阅文档)

### 9.4 _all
　　在这篇blog中有提到: [图解Elasticsearch中的_source、_all、store和index属性][csdn-62233031].  
　　1) `_all`字段默认是关闭的, 如果要开启_all(enable)字段`{"index_type":{"_all":{"enabled":true},"properties":{...}}}`, 索引增大是不言而喻的. `_all`即`_source`所有`field`的并集.  
　　2) `_all`字段开启适用于不指定搜索某一个字段, 根据关键词, 搜索整个`document`内容.  
　　3) 可以在`properties.field`中指定某个字段是否包含在_all中(前提`_all = enable`): `{"index_type":{"properties":{"field1":{"type":"string","include_in_all":false}}}}`
        
        
         
        
        