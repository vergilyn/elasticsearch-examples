source：https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-get-index.html

## Get Index
get-index API允许重新获得一个或多个索引的信息。  
```yaml
GET /twitter
```
以上示例获取了名为`twitter`的索引的信息。  
get-index API必须提供索引名称(允许英文逗号分割集合)，或别名，或通配符表达式。  
```yaml
GET /index_1,index_2
GET /index_alias
GET /index_*
```
get-index API允许一次获得多个索引信息，通过使用逗号分割列表，或者用`_all or *`作为索引。
