# Elasticsearch: Full text queries
see: [full-text-queries][full-text-queries]
+ [match query]: The standard query for performing full text queries, including fuzzy(模糊) matching and phrase(短语) or proximity(邻近) queries.
+ [match_phrase query]: Like the match query but used for matching exact phrases or word proximity matches.
+ [match_phrase_prefix query]: The poor man’s search-as-you-type. Like the match_phrase query, but does a wildcard search on the final word. 
+ [multi_match query]: The multi-field version of the match query.
+ [common_terms query]: A more specialized query which gives more preference to uncommon words.
+ [query_string query]: Supports the compact Lucene [query string syntax], allowing you to specify AND|OR|NOT conditions and multi-field search within a single query string. For expert users only.
+ [simple_query_string]: A simpler, more robust version of the `query_string` syntax suitable for exposing directly to users.

[full-text-queries]: https://www.elastic.co/guide/en/elasticsearch/reference/current/full-text-queries.html
[match query]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html
[match_phrase query]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query-phrase.html
[match_phrase_prefix query]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query-phrase-prefix.html
[multi_match query]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html
[common_terms query]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-common-terms-query.html
[query_string query]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html
[query string syntax]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#query-string-syntax
[simple_query_string]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-simple-query-string-query.html

## 1. match query
see: [match query]
+ [minimum_should_match]: The minimum number of optional should clauses to match can be set using the `minimum_should_match` parameter.
+ [lenient]: Defaults to false. `lenient = true`, 忽略数据类型不匹配导致的异常. 例如尝试使用文本查询字符串查询一个数字字段.

[minimum_should_match]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html

```json
{ "query": {
    "match" : {
        "field_name": {
           "query": "搜索字符串",
           "operator": "and | or", // 默认or,
           "minimum_should_match": "...", 
           "analyzer": "ik_smart", 
           "lenient": "true | false(default)", 
        }
    }
}}
```