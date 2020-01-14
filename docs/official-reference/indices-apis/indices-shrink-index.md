source：https://www.elastic.co/guide/en/elasticsearch/reference/6.2/indices-shrink-index.html

## Shrink Index（压缩索引，或缩小索引）
shrink-index APIs允许将现有索引缩小为具有较少primary-shards的新索引。  
target-index中所请求的primary-shards数量必须是source-index的因子（即必须整除）。  
例如，一个`primary-shards = 8`的index可以被缩小为`primary-shards = 4 | 2 |1 `，或者将`primary-shards = 15`的缩小为`5 | 3 | 1`。
如果索引中的shards数量是一个质数，则只能将其缩小（压缩）为单个primary-shard。  
  
缩小前，索引中的每个shard（primary/replica）的副本必须存在于同一个节点上。  
  
缩小的原理：
- 首先，创建一个新的target-index，其定义与source-target的定义相同，但primary-shards数量较少。
- 然后，将source-index中的所有片段（segment）硬连接（hard-link）到target-index。
（如果file-system不支持hard-link，那么所有的segment都被复制到target-index中，这是一个更耗时的过程。）
- 最后，恢复target-index，就好像它是一个刚刚re-open-index的close-index。

## Preparing an index for shrinking
为了shrink-index，index必须被标记为readonly，并且必须将index中的每个shard（primary/replica）的副本重新定位到同一节点，并使其[health]为`green`。
这两个条件可以通过以下请求来实现：
```yaml
PUT /my_source_index/_settings
{
  "settings": {
    "index.routing.allocation.require._name": "shrink_node_name", 
    "index.blocks.write": true
  }
}
``` 
1. 强制将每个shard的副本迁移到名为`shrink_node_name`的节点。更多参考：[Shard Allocation Filtering]
2. 阻止对此index的write操作，但同时允许对metadata的更改，例如删除索引。
  
可能需要一段时间才能迁移整个source-index。进度可以被追踪通过[_cat recovery API]，或者可以使用[cluster health API]的`wait_for_no_relocating_shards`参数来等待所有的source-shards被迁移完成。

[health]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cluster-health.html
[Shard Allocation Filtering]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/shard-allocation-filtering.html
[_cat recovery API]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cat-recovery.html
[cluster health API]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cluster-health.html

## Shrinking an index
将`my_source_index`缩小到名为`my_target_index`的新索引，可以发出以下请求：
```yaml
POST my_source_index/_shrink/my_target_index
```
上述请求会立即返回，当target-index被添加到cluster-state时。它（请求）不会等到压缩操作开始才返回。

> IMPORTANT：只有当index满足以下条件，才可以被压缩
> - target-index不存在。
> - source-index的primary-shards必须比target-index多。
> - target-index中的primary-shards数量必须是source-index的primary-shards的一个因子。
> - source-index中all-shards中的document总和不能超过`2,147,483,519 = (2^31 - 1) - 128`，因为这些document将被缩小到target-index中的其中single-shard中。
`2,147,483,519`是single-shard中可以放入的最大document数量。
> - 处理缩小进程的node必须有足够可用的磁盘空间来容纳现有index的第二个副本。

`_shrink` API与[`create-index` API][create-index API]类似，可接收target-index的`settings`和`aliases`参数：

[create-index API]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-create-index.html

```yaml
POST my_source_index/_shrink/my_target_index
{
  "settings": {
    "index.number_of_replicas": 1,
    "index.number_of_shards": 1, 
    "index.codec": "best_compression" 
  },
  "aliases": {
    "my_search_indices": {}
  }
}
```
1. `"index.number_of_shards": 1`，target-index的shards数量，值必须是source-index的shards数量的因子。
2. `"index.codec": "best_compression"`，`best_compression`只会影响新写入到index的数据，例如将shard[强制合并（force-merging）][force-merging]到single-segment时。

[force-merging]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-forcemerge.html

> NOTE：`_shrink`请求中不应该指定mappings，因为所有的`index.analysis.*`和`index.similarity.*`的settings设置都将被source-index中的settings覆盖。

## Monitoring the shrink process
shrink-index的过程可以被[_cat recovery API]或者[cluster health API]监控。通过设置参数`wait_for_status = yellow`，可以等到所有primary-shards都被分配完成。

`_shrink`API会在target-index被添加到cluster-state时立即返回，这发生在任何shards被分配前。此时，所有shards都处于`unassigned`状态。  
如果，由于某种原因target-index无法分配到执行缩小的node上时，则其primary-shard将保持在`unassigned`状态，直到被分配到shrink-node上。  

一旦primary-shard分配完成，状态会转换为`initializing`，并且开始缩小过程。当缩小操作完成时，shard会变成`active`。
此时，Elasticsearch将尝试分配replicas，并且可能决定将primary-shard迁移到另一个node。

[_cat recovery API]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cat-recovery.html
[cluster health API]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cluster-health.html

## Wait For Active Shards
因为缩小操作会创建一个新的索引来缩小shard，因此创建索引时的[wait for active shards] settings也同样适用于缩小索引的操作。

[wait for active shards]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-create-index.html#create-index-wait-for-active-shards
