source：https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-create-index.html
## Create Index
create-index API允许实例化一个索引（index）。ES为多个索引提供支持，包括跨多个索引执行操作。

### Index Settings
每个被创建的索引都可以指定与其相关联的特定设置（settings）。
```yaml
PUT twitter
{
    "settings" : {
        "index" : {
            "number_of_shards" : 3,
            "number_of_replicas" : 2
        }
    }
}
```
1. number_of_shards（分片数）：默认为5。
2. number_of_replicas（复制分片数）：默认为1（ie：每个主分片shard都有一个复制分片replica）。

上面的curl示例展示了如何使用YAML创建一个名为`twitter`的索引，并对其进行特定设置。
在以上curl实例中，创建了具有3个主分片的索引（shard），每个索引具有2个副本（replica）。索引设置也可以用JSON定义（v：yaml、json格式规范不同而已）：
```yaml
PUT twitter
{
    "settings" : {
        "index" : {
            "number_of_shards" : 3,
            "number_of_replicas" : 2
        }
    }
}
```
或者更简单的方式
```yaml
PUT twitter
{
    "settings" : {
        "number_of_shards" : 3,
        "number_of_replicas" : 2
    }
}
```
> NOTE：你不必在`settings`部分中显示地指定`index`。
    
更多信息：关于创建索引时可以设置的所有索引级别的`settings`，请参考[index-modules][index-modules]部分。
[index-modules]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/index-modules.html

### Mappings
create-index API允许提供一个类型映射（mappings）：
```yaml
PUT test
{
    "settings" : {
        "number_of_shards" : 1
    },
    "mappings" : {
        "type1" : {
            "properties" : {
                "field1" : { "type" : "text" }
            }
        }
    }
}
```

### Aliases
create-index API同时还允许提供一组别名[aliases]：

[aliases]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-aliases.html
```yaml
PUT test
{
    "aliases" : {
        "alias_1" : {},
        "alias_2" : {
            "filter" : {
                "term" : {"user" : "kimchy" }
            },
            "routing" : "kimchy"
        }
    }
}
```

### Wait For Active Shards
默认情况下，索引的创建只会在以下情况返回给客户端一个响应：当每个shard的主复制分片已经开始启动，或者请求超时。响应将表明发生了什么：
```yaml
{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "test"
}
```
`acknowledged`表明该index是否在集群（cluster）被成功创建，而`shards_recognition`表示在超时之前，是否为索引中的每个shard启动了所需的share-copies数。  
注意，`acknowledged`或`shards_recognition`有可能是`false`，但index还是会被成功创建。这些值仅表示操作是否是在超时之前完成。  
如果`acknowledged = false`，那么在使用最近创建的index去更新cluster-state之前便已经超时，但是index可能很快就被创建。  
如果`shards_acknowledged = false`，那么在必要数量的shards启动之前已经超时（by default just the primaries），即使cluster-state被成功更新以反映新创建的索引（i.e. `acknowledged = true`）。  
  
我们可以通过index-settings来改变只等待主分片开始的默认值`index.write.wait_for_active_shards`（注意：更改此设置也会影响所有后续写入操作的wait_for_active_shards值）：  
```yaml
PUT test
{
    "settings": {
        "index.write.wait_for_active_shards": "2"
    }
}
```
或者通过请求参数`wait_for_active_shards`：
```yaml
PUT test?wait_for_active_shards=2
```
关于`wait_for_active_shards`及其可能值的详细解释可以在[这里找到][index-wait-for-active-shards]。

[index-wait-for-active-shards]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-index_.html#index-wait-for-active-shards