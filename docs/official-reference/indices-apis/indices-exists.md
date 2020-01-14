source：https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-exists.html

## Indices Exists
用于检查索引是否存在。例如：
```yaml
HEAD twitter
```
http状态码表明索引是否存在。`404`，索引不存在；`200`，索引已存在。  
> NOTE：该请求不区分index和index_alias，即如果具有该名称的index_alias存在，也会返回200状态码。