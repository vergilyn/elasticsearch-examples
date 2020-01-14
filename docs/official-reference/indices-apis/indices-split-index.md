source：https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-split-index.html

## Split Index
split-index API允许你将存在的索引拆分为一个新的索引，其中每个origin-primary-shard在new-index被拆分成2个或多个primary-shard。

> important：
> `_split`API需要使用特定的`number_of_routing_shards`来创建source-index，以便将来拆分。
> ES 7.0中已删除此要求。

index可以被拆分的次数（以及每个origin-shard可以被拆分的数量）由settings中的`index.number_of_routing_shards`决定。
number_of_routing_shards的数量指定了内部用于在具有一致hash的shard之间分发document的hash-space。
例如，拥有5个shard的index，将`number_of_routing_shards`设置成`30(5 * 2 * 3)`，可以按因子拆分成`2 or 3`。换句话说，它可以按如下拆分：
- 5 → 10 → 30 (split by 2, then by 3)
- 5 → 15 → 30 (split by 3, then by 2)
- 5 → 30 (split by 6)

## 2018-07-08 待续....