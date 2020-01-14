source：https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-delete-index.html

## Delete Index
delete-index API允许删除现有的索引（index）。
```yaml
DELETE /twitter
```
上面的示例删除了一个叫`twitter`的索引。必须指定一个index或通配符表达式（wildcard-expression）。  
别名Aliases不能用于删除索引。  
通配符表达式仅解析为匹配的具体索引。  
  
delete-index API允许一次删除多个索引，通过使用逗号分割列表，或者用`_all or *`作为索引名删除所有索引。
```yaml
DELETE /index_1,index_2
DELETE /_all
DELETE /*
```
  
为了禁止允许通过使用通配符或_all删除索引，可以将`elasticsearch.yml`配置中的`action.destructive_requires_name`设置为`true`。  
此设置也可以通过集群更新设置api进行更改。
