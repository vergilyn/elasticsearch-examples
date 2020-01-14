source：https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-get-mapping.html

## Get Mapping
get-mapping API允许检索一个index或index/type的mapping定义。
```yaml
GET /twitter/_mapping/_doc
```

## Multiple Indices and Types
get-mapping API可用于通过单个调用获取多个index或type的mapping。  
API的一般用法遵循以下语法：`host:port/{index}/_mapping/{type}`，其中`{index}`和`{type}`都可以接受逗号分割的集合名称。  
为了得到所有索引的mapping，可以使用`_all`代替`{index}`。示例如下：
```yaml
GET /_mapping/_doc

GET /_all/_mapping/_doc

get /index-1,index-2/_mapping/_doc

```

如果想获得所有索引和类型的mapping，那么以下两个示例是等价的:
```yaml
GET /_all/_mapping

GET /_mapping
```