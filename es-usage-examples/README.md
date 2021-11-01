# es-usage-examples


## Motianshi demo-search
- [\[github\] Motianshi demo-search](https://github.com/Motianshi/alldemo/tree/master/demo-search)
- [十分钟学会使用 Elasticsearch 优雅搭建自己的搜索系统（附源码）](https://mp.weixin.qq.com/s/C9fWBqiE6TzDXfiZmg8l9Q)

demo 很简单，但有 100,000 条数据~~~~

`elasticsearch-item-data.json` download: <https://share.weiyun.com/OOMltPbX>

## 搜索
Query DSL 优化:  
- 个人总结： (new)<https://docs.qq.com/doc/DWFhmWlpaa1NGamp2>, (old)<https://docs.qq.com/doc/DUklmUklaVWZieEhP>
- medcl 大佬文章：<https://elastic-search-in-action.medcl.com/>

分词器选择：  
现在是简单的使用过：[ik-analyzer](https://github.com/medcl/elasticsearch-analysis-ik) 和 [HanLP](https://www.hanlp.com)。

hanlp-standard 的分词语义相对比 ik_smart&ik_max_word更符合。**（都不扩充词库的情况下）** 
相应需要注意，hanlp-standard 更需要组合DSL提升召回率。

## Query DSL 参考
```
PUT test_analyzer
{
  "aliases": {
    "test_analyzer_alias": {}
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "fields": {
          "keyword": {"type": "keyword"},
          "wildcard": {"type": "wildcard"},
          "ik_smart":{"type": "text","analyzer": "ik_smart"},
          "ik_max_word":{"type": "text","analyzer": "ik_max_word"},
          "hanlp_standard":{"type": "text","analyzer": "hanlp_standard"
          }
}}}}}
```


```
# 提升召回率的boost 低于 提升精准率的boost。
#   match, base-score
#   prefix, boost < match,   
#   fuzzy, boost < prefix
#   match_phrase_prefix 或 match_phrase，`slop`设置多少合适？
#   wilcard，实现例如`content LIKT '%XX%'`。（示例  复制标题搜索，那么标题完全匹配的期望排在最前面）
GET test_analyzer/_search
{
  "query": {
    "bool": {
      "must": [],
      "should": [
        {"multi_match": {
          "query": "重庆",
          "fields": ["content.hanlp_standard"],
          "boost": 1.0
        }},
        {"prefix": {
          "content.hanlp_standard": {"value": "重庆", "boost": 0.8}
        }},
        {"fuzzy": {
            "content.hanlp_standard": {"boost": 0.4, "prefix_length": 2, "value": "重庆"}
        }},
        
        {"match_phrase_prefix": {
          "content.hanlp_standard": {"query": "重庆", "slop": 3,"boost": 3.0 }
        }},
        {"wildcard": {
          "content.wildcard": {"value": "*重庆*", "boost": 3.0 }
        }}
      ]
    }
  }
}
```