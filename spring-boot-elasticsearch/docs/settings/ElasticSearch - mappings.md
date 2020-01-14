# settings - mapping(映射)
　　see: [reference: mapping][EN, reference: mapping]
   + [removal-of-types][EN, reference: removal-of-types]
   + [mapping-types][EN, reference: mapping-types]
   + [mapping-fields][EN, reference: mapping-fields]
   + [mapping-params][EN, reference: mapping-params]
   + [dynamic-mapping][EN, reference: dynamic-mapping]
    
[EN, reference: mapping]: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html
[EN, reference: removal-of-types]: https://www.elastic.co/guide/en/elasticsearch/reference/current/removal-of-types.html
[EN, reference: mapping-types]: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html
[EN, reference: mapping-fields]: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-fields.html
[EN, reference: mapping-params]: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-params.html
[EN, reference: dynamic-mapping]: https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic-mapping.html


　　_mapping API:  
　　　　1. localhost:9200/{index_name}/_mapping/{type_name}?pretty  
　　　　2. localhost:9200/{index_name}/_mapping?pretty  

```json
// 汇总mappings设置 (不一定完整)
{
  "mappings": {
      "_default_": {  // 为index的所有type设置默认配置. type设置中可以覆盖这些default.  
              "_all": { "enabled":  false },
              "_source": { "enabled":  false }
              // other...
      },
           
      "type_name": {
	     // 1) properties 节点, 列出了文档中可能包含的每个字段的映射;   
         "properties": {
            "field_name":{
               "type": "object | nested | string | [byte, short, integer, long] | boolean | [float, double] | date", 
               "index": "analyzed | not_analyzed | no",
               "format": "strict_date_optional_time||epoch_millis",  // 处理日期, 除开内置格式外, 还可以用熟悉的: yyyy-MM-dd
               "analyzer": "standard(default) | ik_smart | whitespace | simple | english | ...",  // 创建索引时的分析器
               "search_analyzer": "...",  // 搜索时的分析器(分析的是: 搜索时输入的字符串), 默认与"analyzer"相同.
               "store": false, // true | false(default), 是否单独存储此field的值; (true|false, 并不影响索引的创建和搜索)
               "include_in_all": true  // true(default) | false, 优先级高于"type_name.include_in_all" .
            },
            "field_object_or_nested":{  // 多层级对象映射
               "type": "object | nested", 
               "dynamic": "true(default) | false | strict", // 如果对象field_object_or_nested遇到新字段, 对新字段的处理策略
               "properties": {}
            }
         }, 
         
         // 2) 各种元数据字段, 它们都以一个"_"线开头, 例如: _type, _id, _source...;  
         "_source": {
            "enabled": true // true(default) | false, true: 存储代表文档体的JSON字符串
         },
         
         // metadata: Document Identity (非配置项: _id, _type, _index, _uid)
         "_id": "xx",  // document的 ID 字符串
         "_type": "",  // document的类型名, 被索引但是没有存储.
         "_index":"",  // document所在的索引
         "_uid":"",    // _type 和 _id 连接在一起构造成 type#id, 被存储（可取回）和索引（可搜索）的.
         
         // metadata: _all
         "_all": {
            "enabled": true,  // true | false, (不太确定默认值, 貌似是false) false: 不启用_all字段.
            "analyzer": "ik_smart"  // 指定_all的分析器
         },
         "include_in_all": false,  // true | false, 全局配置 include_in_all = false. 
         
         // 3) 设置项, 控制如何动态处理新的字段, 例如: analyzer, dynamic_date_formats, dynamic_templates...;  
         "analyzer": {}, 
         "numeric_detection": true,  // true | false(default), 默认是禁用字符串转数字, 应该自己显示的指定. 
         "date_detection": false,  // true(default) | false, false: 字符串将始终作为 string 类型. 如果你需要一个date字段, 你必须手动添加.
         "dynamic_date_formats": [ "strict_date_optional_time","yyyy/MM/dd HH:mm:ss Z||yyyy/MM/dd Z"],  // (即默认值) 判断字符串为日期的规则

         "dynamic": "true(default) | false | strict",  // 如果对象type_name遇到新字段, 对新字段的处理策略
         
         "dynamic_templates": [  // 动态模版
            { "my_template_name": {  // dynamic_template_name: my_template_name
                 "match": "*_es", 
                 "match_mapping_type": "string",
                 "mapping": {  
                    "type": "string",
                    "analyzer": "spanish"
                 }
            }}
         ],
      }
  }
}
```
　　备注:  
　　　　1) `format`用于处理日期, 除开[内置格式][EN, built-in date-formats]外, 还可以用熟悉的: yyyy-MM-dd; ([自定义日期格式语法][EN, date-format-syntax])  
　　　　2) `multi-fields`未理解: [multi-fields, multi-fields with multiple analyzers][EN, multi-fields], ;     

[EN, multi-fields]: https://www.elastic.co/guide/en/elasticsearch/reference/current/multi-fields.html

## 1. mapping-base(映射基础)
　　see: [映射][CN, mapping], [mapping][EN, mapping]  

[CN, mapping]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping.html
[EN, mapping]: https://www.elastic.co/guide/en/elasticsearch/guide/current/mapping.html

　　特别: 如果有两个不同的`type`, 每个`type`都有同名的`field`, 但映射(mapping)不同(eg: 一个是String, 一个是Integer), 将会出现什么情况?  
　　　　简单回答是, Elasticsearch 不会允许你定义这个映射. 当你配置这个映射时, 将会出现异常.  
　　(同一`index`的不同`type`, 如果存在同名的`field`, 则`field`的配置(field.type、field.index、field.analyzer等field.*)必须一致.)
　　
```json
{
   "data": {
      "mappings": {
         "people": {  // type: people
            "properties": {
               "name": {
                  "type": "string"
               },
               "address": {
                  "type": "string"
               }
            }
         },
         
         "transactions": { // type: transactions
            "properties": {
               "timestamp": {
                  "type": "date",
                  "format": "strict_date_optional_time"
               },
               "message": {
                  "type": "string"
               }
            }
         }
      }
   }
}

// 每个type定义两个字段 (people: ["name","address"], transactions: ["timestamp", "message"]). 它们看起来是相互独立的, 但在后台 Lucene 将创建一个映射:  
// 注: 这不是真实有效的映射语法, 只是用于演示 
{
   "data": {
      "mappings": {
        "_type": {
          "type": "string",
          "index": "not_analyzed"
        },
        "name": {
          "type": "string"
        },
        "address": {
          "type": "string"
        },
        "timestamp": {
          "type": "long"
        },
        "message": {
          "type": "string"
        }
      }
   }
}

```

## 2. mappings, root-object(根对象)
　　see: [根对象][CN, root-object], [root-object][EN, root-object]  

[CN, root-object]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/root-object.html
[EN, root-object]: https://www.elastic.co/guide/en/elasticsearch/guide/current/root-object.html

　　mapping的最高一层被称为 root-object. 它可能包含下面几项:  
```json
{
  "mappings": {
	  "type_name": {
	     // 1) 一个 properties 节点, 列出了文档中可能包含的每个字段的映射;   
         "properties": {}, 
         
         // 2) 各种元数据字段, 它们都以一个"_"线开头, 例如: _type, _id, _source...;  
         "_id": "xx",
         "_type": "",
         "_source": {},
         
         // 3) 设置项, 控制如何动态处理新的字段, 例如: analyzer, dynamic_date_formats, dynamic_templates...;  
         "analyzer": {}, 
         "dynamic_date_formats": "", 
         "dynamic_templates": {}
         
         // 4) 其他设置, 可以同时应用在root-object和其他 object 类型的字段上, 例如: enabled, dynamic, include_in_all...;  
	  },
	  
      "_default_": {  // 为index的所有type设置默认配置. type设置中可以覆盖这些default.  
          "_all": { "enabled":  false },
          "_source": { "enabled":  false }
          // other...
      },
  }
}
```

## 3. mapping-properties
　　什么是mapping-properties?　mapping-properties列出了document中可能包含的每个field的映射关系(field-mapping). 　

### 3.1. core-simple-field-types(核心简单域类型)
　　see: [映射, 核心简单域类型][CN, mapping-intro.html#core-fields], [mapping, core-simple-field-types][EN, mapping-intro.html#core-fields]

[CN, mapping-intro.html#core-fields]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping-intro.html#core-fields
[EN, mapping-intro.html#core-fields]: https://www.elastic.co/guide/en/elasticsearch/guide/current/mapping-intro.html#core-fields

```json
{"mappings": {
    "type_name": {
        "properties": {
            "field_name":{
                "index": "analyzed(default) | not_analyzed | no",
                "type": "object | nested | string | [byte, short, integer, long] | boolean | [float, double] | date", 
                "format": "strict_date_optional_time||epoch_millis", // 用于处理date转换
                "analyzer": "standard(default) | ik_smart | whitespace | simple | english | ...",  // 创建索引时的分析器
                "search_analyzer": "...",  // 搜索时的分析器(分析的是: 搜索时输入的字符串), 默认与"analyzer"相同.
                "store":false  // true | false(default), 是否单独存储此field的值; (true|false, 并不影响索引的创建和搜索) 
            }
        }
    }    
}}
/*
index 属性控制怎样索引字符串。它可以是下面三个值:
  analyzed: 首先分析字符串, 然后索引它。换句话说, 以全文索引这个域. 
  not_analyzed: 索引这个域, 所以它能够被搜索, 但索引的是精确值. 不会对它进行分析. 
  no: 不索引这个域. 这个域不会被搜索到.
*/
```
　　特别:  
　　　　1. 简单类型(例如long, double, date等) 也接受`index参数`, 但有意义的值只有`no | not_analyzed`, 因为它们永远不会被分析.  
　　　　2. 已存在的field-mapping不能被update, 原因: 该mapping可能已生成`倒排索引`, 如果update已存在的field-mapping, `倒排索引`的生成规则可能不一致, 导致不能被正常的搜索.  
　　　　　　但可以使用_mapping API为新类型(或者为存在的类型更新映射)增加filed-mapping.  
　　　　3. `object | nested`的搜索结果有差异. see: [嵌套对象][CN, nested-objects], [nested-objects][EN, nested-objects]   

[CN, nested-objects]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/nested-objects.html
[EN, nested-objects]: https://www.elastic.co/guide/en/elasticsearch/guide/current/nested-objects.html

### 3.2. complex-core-fields(复杂核心域类型)
　　see: [映射, 复杂核心域类型][CN, complex-core-fields], [mapping, complex-core-fields][EN, complex-core-fields]

[CN, complex-core-fields]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/complex-core-fields.html
[EN, complex-core-fields]: https://www.elastic.co/guide/en/elasticsearch/guide/current/complex-core-fields.html

```json
// 内部对象映射(多层级对象映射)
{"mappings": {
    "type_name": {
        "properties": {
            "field_object_or_nested":{
                "type": "object | nested", 
                "properties": {
                    "index": "analyzed(default) | not_analyzed | no",
                    "type": "object | nested | string | [byte, short, integer, long] | boolean | [float, double] | date", 
                  //"format": "strict_date_optional_time||epoch_millis", 
                    "analyzer": "standard(default) | ik_smart | whitespace | simple | english | ...",
                    "search_analyzer": "..."  
                }
            }
        }
     }
}}
```

　　特别: `object`与`nested`的区别.  see: [嵌套对象][cn-nested-objects], [nested-objects][en-nested-objects]   
```json
// object处理逻辑: 
// Lucene不理解内部对象, 文档会被扁平化处理, 例如:  
{
    "followers": [
        { "age": 35, "name": "Mary White"},
        { "age": 26, "name": "Alex Jones"},
        { "age": 19, "name": "Lisa Smith"}
    ]
}
// object, 扁平化处理结果: 
{
    "followers.age":    [19, 26, 35],
    "followers.name":   [alex, jones, lisa, smith, mary, white]
}

```
　　`{age: 35}`和`{name: Mary White}`之间的相关性已经丢失了, 因为每个多值域只是一包无序的值, 而不是有序数组.  
　　导致我们不能得到一个准确的答案: “是否有一个26岁, 名字叫Alex Jones的追随者?”.  
　　此时`object`无法满足, 但`nested`可以做到.  
```json
// nested, 处理结果:
{ // 第1个 nested-object
    "followers.age":    19,
    "followers.name":   [lisa, smith]
}
{ // 第2个 nested-object
    "followers.age":    26,
    "followers.name":   [alex, jones]
}
{ // 第3个 nested-object
    "followers.age":    26,
    "followers.name":   [mary, white]
}
{ // The root or parent document(根文档 或者也可称为父文档)
  "field_xx": "value_xx"
}
```

## 4. mapping-metadata(元数据)
　　各种元数据字段, 它们都以一个"_"开头, 例如: _type, _id, _source...

### 4.1. metadata: _source
　　1. `_source`字段存储代表文档体的JSON字符串. 和所有被存储的字段一样, `_source`字段在被写入磁盘之前先会被压缩.(`_source`字段要占用磁盘空间)     
　　2. 如果没有`_source`字段, 部分 update 请求不会生效.  
　　3. 当你的映射改变时, 你需要重新索引你的数据, 有了`_source`你可以直接从Elasticsearch这样做, 而不必从另一个(通常是速度更慢的)数据仓库取回你的所有文档.  
　　　　(see: [重新索引你的数据][CN, reindex], [reindex-your-data][EN, reindex]; [索引别名和零停机][CN, index-aliases], [Index Aliases and Zero Downtime][EN, index-aliases])  
　　4. **特别:** `_source`与`field.store`  
　　　　(可另外参考: [图解Elasticsearch中的_source、_all、store和index属性](http://blog.csdn.net/napoay/article/details/62233031), [详解ElasticSearch的store属性](http://blog.csdn.net/liyantianmin/article/details/52531310))  
　　　　从`_source`中获取需要的field.value总是更好的方式, 因为这样只会产生1次I/O操作.  
　　　　如果`field.store = true`, 意味着此field被单独存储, 当获取此field时会再执行1次I/O操作, 对性能影响较大.(但`field.store = true`有时会更优, 针对实际情况而定)  

[CN, reindex]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/reindex.html (重新索引你的数据)
[EN, reindex]: https://www.elastic.co/guide/en/elasticsearch/guide/current/reindex.html (Reindexing Your Data)
[CN, index-aliases]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/index-aliases.html (索引别名和零停机)
[EN, index-aliases]: https://www.elastic.co/guide/en/elasticsearch/guide/current/index-aliases.html (Index Aliases and Zero Downtime)

```json
{
  "mappings": {
	  "type_name": {
         "_source": {
            "enabled": true // true(default) | false, true: 存储代表文档体的JSON字符串
         },
	  }
  }
}

// search时可以指定需要or不需要的字段
// GET /_search
{
    "query":   { "match_all": {}},
    "_source": [ "title", "created" ]
}

// 或者
{
    "query":   { "match_all": {}},
    "_source": {
       "excludes": ["field_aa"],  // 指定不获取的field
       "includes": ["field_bb"]   // 获取特定的field
    }
}
```

### 4.2. metadata: _all
　　`_all`: 一个把其它字段值(fields), 当作一个大字符串来索引的特殊字段. (_all = field_1 + field_2 + ... + field_n)   
　　1) 不推荐使用`_all`, 因为会占用多余的空间.  
　　2) 指定查询字段比用`_all`更好.  
　　3) `_all`无法控制搜索结果的细粒度.
　　　　relevance algorithm 考虑的一个最重要的原则是字段的长度: 字段越短越重要.  
　　　　在较短的 title 字段中出现的短语可能比在较长的 content 字段中出现的短语更加重要.  
　　　　字段长度的区别在`_all`字段中无法体现.  

```json
/*
  通过 include_in_all 设置来逐个控制字段是否要包含在 _all 字段中, 默认值是 true.  
  在一个对象(或根对象)上设置 `include_in_all 可以修改这个对象中的所有字段的默认行为.
  
  注意 _all.enabled、_all.include_in_all、properties.include_in_all的逻辑关系
*/
{
  "mappings": {
	  "type_name": {
         "_all": {
            "enabled": true,  // true | false, (不太确定默认值, 貌似是false) false: 不启用_all字段.
            "analyzer": "ik_smart"  // 指定_all的分析器
         },
         
         "include_in_all": false,  // true | false, 全局配置 include_in_all = false. 

         "properties": {
            "title": {
               "type": "string",
               "include_in_all": true  // true(default) | false, 优先级高于"type_name.include_in_all" .
            },
         }
	  }
  }
}
```

### 4.3. metadata: Document Identity

```json
{
  "mappings": {
	  "type_name": {
	  
         // metadata: Document Identity
         "_id": "xx",  // document的 ID 字符串, 
         "_type": "",  // document的类型名
         "_index":"",  // document所在的索引
         "_uid":"",    // _type 和 _id 连接在一起构造成 type#id
         
	  }
  }
}
```
　　默认情况下, `_uid`字段是被存储（可取回）和索引（可搜索）的.  
　　`_type`字段被索引但是没有存储.  
　　`_id`和`_index`字段则既没有被索引也没有被存储, 这意味着它们并不是真实存在的.

　　尽管如此, 你仍然可以像真实字段一样查询`_id`字段. Elasticsearch使用`_uid`字段来派生出`_id`.  
　　虽然你可以修改这些字段的 index 和 store 设置, 但是基本上不需要这么做.  

## 5. dynamic-mapping(动态映射)
　　see: [动态映射][CN, dynamic-mapping], [dynamic-mapping][EN, dynamic-mapping];  [dynamic-mapping-reference][EN, dynamic-mapping-reference]  

[CN, dynamic-mapping]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/dynamic-mapping.html
[EN, dynamic-mapping]: https://www.elastic.co/guide/en/elasticsearch/guide/current/dynamic-mapping.html
[EN, dynamic-mapping-reference]: https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic-mapping.html

　　当 Elasticsearch 遇到文档中以前 未遇到的字段, 它用dynamic-mapping来确定字段的数据类型并自动把新的字段添加到类型映射;
　　新字段的处理行为, `dynamic`: 可以用在root-object或任何object类型的字段上,  含义:   
　　　　true: (默认)动态添加新的字段;  
　　　　false: 忽略新的字段;  
　　　　true: 如果遇到新字段抛出异常;  
　　把`dynamic`设置为`false`并不会改变`_source`内容. `_source`仍然包含被索引的整个JSON文档, 只是新的字段不会被加到映射中也不可搜索.  

```json
/*
  1. 如果遇到新字段, 对象 my_type 就会抛出异常;
  2. 而内部对象 stash 遇到新字段就会动态创建新字段. 
*/
{
  "mappings": {
	  "type_name": {
	  
         "dynamic": "true(default) | false | strict",   // 如果对象type_name遇到新字段, 对新字段的处理策略
         
         "properties": {
            "title": { 
               "type": "string"
            },
            "stash":  {
               "type": "object",
               "dynamic": true  // 如果对象stash遇到新字段, 对新字段的处理策略
            }
         }
	  }
  }
}

// 示例: 可以动态给 stash 对象添加新的可检索的字段, 以下请求正确执行.  
// PUT /my_index/my_type/1
{
    "title": "This doc adds a new field",
    "stash": {
       "new_field": "Success!"
    }
}

// 示例: 但是对根节点对象 my_type 进行同样的操作会失败, 以下请求会返回异常信息.
// PUT /my_index/my_type/1
{
    "title": "This throws a StrictDynamicMappingException",
    "new_field": "Fail!"
}
```

### 5.1. custom-dynamic-mapping(自定义动态映射)
　　see: [自定义动态映射][CN, custom-dynamic-mapping], [custom-dynamic-mapping][EN, custom-dynamic-mapping]  

[CN, custom-dynamic-mapping]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/custom-dynamic-mapping.html
[EN, custom-dynamic-mapping]: https://www.elastic.co/guide/en/elasticsearch/guide/current/custom-dynamic-mapping.html

```json
{
    "mappings": {
        "type_name": {
           "date_detection": false,  // true(default) | false, false: 字符串将始终作为 string 类型. 如果你需要一个date字段, 你必须手动添加.
           "dynamic_date_formats": [ "strict_date_optional_time","yyyy/MM/dd HH:mm:ss Z||yyyy/MM/dd Z"],  // (即默认值) 判断字符串为日期的规则
           
           "numeric_detection": true  // true | false(default), 默认是禁用字符串转数字, 应该自己显示的指定. 
        }
    }
}
```
　　see:[dynamic-field-mapping.html][EN, dynamic-field-mapping], [内置日期格式][EN, built-in date-formats], [自定义日期格式语法][EN, date-format-syntax]  

[EN, dynamic-field-mapping]: https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic-field-mapping.html  
[EN, built-in date-formats]: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format.html
[EN, date-format-syntax]: http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html

　　**dynamic_templates(动态模版):**  [动态模版][CN, dynamic_templates], [dynamic_templates][EN, dynamic_templates]
　　　　1. 使用dynamic_templates, 你可以完全控制 新检测生成字段的映射. 你甚至可以通过字段名称或数据类型来应用不同的映射.  
　　　　2. 模版按顺序处理 - 第一个匹配模版获胜. (Templates are processed in order — the first matching template wins. )  
　　　　3. 使用PUT mapping API可以将新模版添加到列表的末尾. 如果new-templates与existed-template具有相同的名称, 则将替换existed-template.  

[CN, dynamic_templates]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/custom-dynamic-mapping.html#dynamic_templates
[EN, dynamic_templates]: https://www.elastic.co/guide/en/elasticsearch/guide/current/custom-dynamic-mapping.html#dynamic_templates

```json
/*
  每个模板都有一个名称("es","en"), 你可以用来描述这个模板的用途;
  一个 mapping 来指定映射应该怎样使用, 以及至少一个参数 (如 match) 来定义这个模板适用于哪个字段.  
*/
// PUT /my_index
{"mappings": {
    "my_type": {
        "dynamic_templates": [
          {"longs_as_strings": {
              "match_mapping_type": "string",
              "match":   "long_*",
              "unmatch": "*_text",
              "path_match": "some_object.*.match_field",
              "path_unmatch": "some_object.*.unmatch_field",
              "mapping": {
                "type": "boolean | date | double | long | object | string | * ",
                "analyzer": "standard"
              }
          }}
        ]
}}}

// 正则
{"mappings": {
    "my_type": {
        "dynamic_templates": [
          { "xx_template_name": {
                "match_mapping_type": "string",
                    "match_pattern": "regex",
                    "match": "^profit_\d+$",
                    "mapping": {}
                }}
          ]
}}}

```
　　1) "match_mapping_type"允许你应用模板到特定类型的字段上, 就像有标准动态映射规则检测的一样, (例如 string 或 long);  
　　2) "match | unmatch", 匹配最终字段名称; ("unmatch"排出匹配的字段用此dynamic-templates的规则)
　　3) "path_match | patch_unmatch", 匹配字段在对象上的完整路径(operate on the full dotted path to the field, not just the final name), 例如: some_object.*.some_field;  
　　4) "match_pattern", 支持java正则.  
　　5) "{name} | {dynamic_type}", 支持占位符.  
　　see-more: [reference: dynamic-templates][EN, reference: dynamic-templates]
 
 
 
 
 
 
## 6. default-mapping(默认映射)
　　see: [默认映射][CN, default-mapping], [default-mapping][EN, default-mapping]  

[CN, default-mapping]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/default-mapping.html
[EN, default-mapping]: https://www.elastic.co/guide/en/elasticsearch/guide/current/default-mapping.html

　　1. 通常, 一个`index`中的所有`type`共享相同的字段和设置.  
　　2. `_default_`映射更加方便地指定通用设置, 而不是每次创建新类型时都要重复设置.  
　　3. `_default_`映射是新类型的模板.  
　　4. 在设置`_default_`映射之后, 创建的所有类型都将应用这些default-config, 除非类型在自己的映射中明确覆盖这些设置.  
```json
// PUT /my_index
{ "mappings": {
    "_default_": {
        "_all": { "enabled":  false },
        "_source": { 
            "enabled":  false 
        }
        // other...
    },
    "type_a": {
        "_all": { "enabled":  true  }
    },
    "type_b": {
        "_all": { "enabled":  true  }
      }
}}
```