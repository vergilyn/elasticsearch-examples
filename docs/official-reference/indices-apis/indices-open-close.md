source：https://www.elastic.co/guide/en/elasticsearch/reference/6.2/indices-open-close.html

## Open / Close Index API
open/close-index APIs允许close-index，并在稍后open。close-index在集群上几乎没有开销（除开维护元数据metadata），并且在read/write操作时被阻塞。  
通过正常的恢复方法可以将关闭的索引重新打开。(v：A closed index can be opened which will then go through the normal recovery process.)  
  
REST endpoint：`/{index}/_close`、`/{index}/_open`。示例：
```yaml
POST /my_index/_close

POST /my_index/_open
```  
APIs允许批量open/close-index。  
如果请求明确引用缺少的索引，则会引发错误（请求响应结果会标明错误信息）。这个行为可以被禁用，通过添加请求参数`ignore_unavailable=true`。

open/close所有索引可以使用`_all`，或者指定能识别全部索引的模式（e.g. *）。  

可以通过设置config中的`action.destructive_requires_name = true`来禁用通配符或_all模式。此设置也可以通过cluster-update APIs来更改。  
  
close-index会消耗大量的磁盘空间，这可能导致托管环境出现问题。  
通过设置cluster-settings APIs中的`cluster.indices.close.enable = false`来禁用close-index，默认为`true`。
  
## Wait For Active Shards
由于open-index会为其分配shard，因此创建索引时[`settings.index.write.wait_for_active_shards`][wait_for_active_shards]的设置也适用于open-index。  
open-index API中的[`wait_for_active_shards`][wait_for_active_shards]默认值为0，这意味着该命令不会等待分配shard。

[wait_for_active_shards]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-create-index.html#create-index-wait-for-active-shards