
see: [reference: query-dsl][EN, reference: query-dsl]

[EN, reference: query-dsl]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html

## 1. `Field.index`: `analyzed`, `not_analyzed`, `no`
  + analyzed: 首先分析字符串, 然后索引它. 换句话说, 以全文索引这个域. (用分析后的terms去创建`倒排索引`)    
  + not_analyzed: 索引这个域, 所以它能够被搜索, 但索引的是精确值. 不会对它进行分析. (用完整的值去创建`倒排索引`)  
  + no: 不索引这个域. 这个域不会被搜索到. (不会创建`倒排索引`)   
　　see: [映射][CN, mapping-intro], [mapping][EN, mapping-intro]

[CN, mapping-intro]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping-intro.html
[EN, mapping-intro]: https://www.elastic.co/guide/en/elasticsearch/guide/current/mapping-intro.html

基于词条(Term-based)和全文(Full-text): http://blog.csdn.net/dm_vincent/article/details/41693125  

>  Low-level Queries(低级查询): 不含有解析阶段, 例如: term、fuzzy.
>  它们在单一词条上进行操作.  一个针对词条"Foo"的term查询会在`倒排索引`中寻找该词条的精确匹配(Exact term).

>  High-level Queries(高级查询): 含有解析阶段, 能够理解一个字段的映射. 例如: match、query_string. 
>    1. 如果你使用它们去查询一个date或者integer字段，它们会将查询字符串分别当做日期或者整型数。
>    2. 如果你查询一个精确值(not_analyzed)字符串字段，它们会将整个查询字符串当做一个单独的词条。  

　　`2.`感觉有问题(基于elasticsearch-2.4.6): 即使是去查询not_analyzed字段, 其实也会存在分析"查询字符串"的阶段, 并且query_string还有部分简单语法(whitespace的特殊性) , 测试过程:  
```json
// -- PUT /test_index
{ "mappings": {
    "test_type": {
        "properties": {
            "desc": {
                "type": "string",
                "index": "not_analyzed",
                "analyzer": "keyword",
                "search_analyzer": "keyword"
            }
        }
    }
}}

// -- PUT 127.0.0.1:9200/_bulk
{ "create" : { "_index" : "test_index", "_type" : "test_type", "_id" : "1" } }
{"desc":"Foo Bar"}

{ "create" : { "_index" : "test_index", "_type" : "test_type", "_id" : "2" } }
{"desc":"乱七八糟 描述"}

// match-query
// -- GET/POST 127.0.0.1:9200/test_index/test_type/_search
{ "query": {
    "match" : {
      "desc": {
        "query" : "Foo Bar"
      }
    }
}}
// response ->> 
//   只有"query" : "Foo Bar",  成功匹配"_id = 1" >>>  证明: not_analyzed 只会生成一个term["Foo Bar"].

// query_string 127.0.0.1:9200/test_index/test_type/_search
// -- GET/POST
{ "query": {
    "query_string": {
      "default_field": "desc",
        "query": "\"Foo Bar\""
      }
}}
// response ->> 
//   若 "query":"Foo Bar", 即使"analyzer":"keyword"也并不能匹配结果. 因为whitespace的在query_string的特殊性.  
//   若 "query": "\"Foo Bar\"", 则可以匹配到"_id = 1"

```
　　1) 如果是`term | fuzzy`等低级查询, 是不会分析"Foo Bar"这个字符串, 直接用"Foo Bar"去匹配已创建的`倒排索引`.  
　　2) 如果是`match | query_string`等高级查询, 首先会分析"Foo Bar", 再用分析后的这些terms去匹配已创建的`倒排索引`. 


>    3. 但是如果你查询了一个全文字段(analyzed), 它们会首先将查询字符串传入到合适的解析器(analyzer的查找顺序), 用来得到需要查询的词条列表. 

>    4. 在很少的情况下，你才需要直接使用基于词条的查询(Term-based Queries)。
         通常你需要查询的是全文, 而不是独立的词条， 而这个工作通过高级的全文查询来完成会更加容易(在内部它们最终还是使用的基于词条的低级查询).  
         如果你发现你确实需要在一个not_analyzed字段上查询一个精确值，那么考虑一下你是否真的需要使用match|query, 而不是使用filter. (即用filter更好)


[全文搜索 (四) - 控制分析及相关度](http://blog.csdn.net/dm_vincent/article/details/41773959)  
 （细看这个, 看 analyzer在 索引期间、搜索期间 查找解析器的顺序差异, 可能能想通上面的问题）
  


>   查询只能搜索到真实存在于倒排索引(Inverted Index)中的词条(Term)，
>   因此确保相同的分析过程会被适用于文档的`索引阶段`和`搜索阶段`的查询字符串是很重要的，
>   这样才能够让查询中的词条能够和倒排索引中的词条匹配。

  es-analyzers:  https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.

    


