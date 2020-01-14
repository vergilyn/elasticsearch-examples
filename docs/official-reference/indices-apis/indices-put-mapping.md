source：https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-put-mapping.html

## Put Mapping
put-mapping API允许将fields添加到exist-index中，或者更改exist-fields的search-setting。
```yaml
PUT twitter ①
{}

PUT twitter/_mapping/_doc ②
{
  "properties": {
    "email": {
      "type": "keyword"
    }
  }
}
```
①：[创建][indices-create-index]一个没有任何type-mapping，名为`twitter`的index。  
②：使用put-mapping API为`_doc`添加一个名为`email`的新字段，及其type-mapping。  

关于如何定义type-mapping的信息可以在[mapping]部分找到。

[indices-create-index]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-create-index.html
[mapping]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/mapping.html

## Multi-index
put-mapping API可以用单个请求应用到多个index。例如，我们可以同时更新`twitter-1`和`twitter-2`的mapping：
```yaml
# Create the two indices
PUT twitter-1
PUT twitter-2

# Update both mappings
PUT /twitter-1,twitter-2/_mapping/_doc ①
{
  "properties": {
    "user_name": {
      "type": "text"
    }
  }
}

```
①：注意，指定的index（`twitter-1,twitter-2`）允许[multi-index-name]和通配符表达式（wildcard）。

[multi-index]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/multi-index.html

> NOTE：
> 当使用put-mapping API更新`_default_`时，new-mapping不会与exist-mapping合并。相反的，新的`_default_`将替换exist-mapping。

## Updating field mappings
通常，无法更新exist-fields的mapping。但存在例外。例如：  

- new [properties] can be added to [Object datatype] fields.（可以向对象数据类型字段添加新属性。）
- new [multi-fields] can be added to existing fields.（可以向现有字段添加新的多字段。）
- the [ignore_above] parameter can be updated.（可以更新ignore_above参数。）

[properties]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/properties.html
[Object datatype]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/object.html
[multi-fields]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/multi-fields.html
[ignore_above]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/ignore-above.html

```yaml
PUT my_index ①
{
  "mappings": {
    "_doc": {
      "properties": {
        "name": {
          "properties": {
            "first": {
              "type": "text"
            }
          }
        },
        "user_id": {
          "type": "keyword"
        }
      }
    }
  }
}

PUT my_index/_mapping/_doc
{
  "properties": {
    "name": {
      "properties": {
        "last": { ②
          "type": "text"
        }
      }
    },
    "user_id": {
      "type": "keyword",
      "ignore_above": 100 ③
    }
  }
}

```
①：创建一个索引，`name`是[对象数据类型][Object datatype]，其下有一个名为`first`的filed。和一个`user_id`的filed。
②：在`name`对象字段下添加一个名为`last`的字段。
③：更新`ignore_above`的值（默认值为0）

每个[mapping parameter]指定其设置是否可以在exist-mapping上更新。

[mapping parameter]: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/mapping-params.html