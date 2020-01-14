elasticsearch guide, index settings: [索引设置][CN, index-settings], [index-settings][EN, index-settings].  
see more: **[index-settings-modules][EN, index-modules]**  

```json
// 汇总settings主要使用的配置 (不一定完整)
{ 
  // 1. settings
  "settings": {
    // shards: 每个索引的主分片数，默认值是 5 。这个配置在索引创建后不能修改。
    "number_of_shards": 5,
    // replicas: 每个主分片的副本数，默认值是 1 。对于活动的索引库，这个配置可以随时修改。
    "number_of_replicas": 1,
     
    // 2. settings-analysis
    "analysis": {
        "char_filter": {  // custom character filters
        "&_to_and": {
            "type": "mapping",
            "mappings": [ "&=> and "]
        }},
         
    //  "tokenizer":   { ...    custom tokenizers     ... },
     
        "filter": {  // custom token filters
            "my_stopwords": {
                "type": "stop",
                "stopwords": [ "the", "a" ]
        }},
         
        "analyzer": {  // custom analyzers
            "my_analyzer": {
                "type": "custom",
                "char_filter": [ "html_strip", "&_to_and" ],
                "tokenizer": "standard",
                "filter": [ "lowercase", "my_stopwords" ]
        }}
    }
  }, 
  
  // 2. mappings
  "mappings": {
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
        
          // metadata: Document Identity (非配置项)
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
        
          "_default_": {  // 为index的所有type设置默认配置. type设置中可以覆盖这些default.  
              "_all": { "enabled":  false },
              "_source": { "enabled":  false }
              // other...
          },
    }
  }
}
```

## 1. settings, base-config
　　see: [索引设置][CN, index-settings], [Index Settings][EN, index-settings]  
　　see-more: [index-modules][EN, index-modules]

[CN, index-settings]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/index-settings.html
[EN, index-settings]: https://www.elastic.co/guide/en/elasticsearch/guide/current/_index_settings.html
[EN, index-modules]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html

### 1.1. static index setting
　　static index settings: 创建后不能修改
```json
{"settings": {
    // shards: 每个索引的主分片数, 默认值是 5.
    "number_of_shards": 5,
    
    "shard.check_on_startup": "false | checksum | true | fix", 
    
    // 默认使用"LZ4"来压缩需要存储的数据
    "codec": "LZ4",
    
    // The number of shards a custom routing value can go to. 
    "routing_partition_size": 1  // default: 1, 且改值小于number_of_shards(除非number_of_shards = routing_partition_size = 1)
    
}}
```

### 1.2. dynamic index settings
　　dynamic index settings: 创建后可以通过_mapping / _setting API动态更新.  

```json
{"settings": {
    // replicas: 每个主分片的副本数, 默认值是 1.
    "number_of_replicas": 1,
    
    // Auto-expand the number of replicas based on the number of available nodes;
    "auto_expand_replicas": "0-[n | all]",  //  Defaults to false(disabled). eg. 0-5, 0-all
    
    // How often to perform a refresh operation, which makes recent changes to the index visible to search. 
    "refresh_interval":"1s",  // Defaults to 1s. Can be set to -1 to disable refresh.
    
    // The maximum value of "from + size" for searches to this index.  
    "max_result_window": 10000,  // Defaults to 10000
    
    // The maximum value of from + size for inner hits definition and top hits aggregations to this index. 
    "max_inner_result_window": 100,  // Defaults to 100.
    
    "max_rescore_window": 1000,
    "max_docvalue_fields_search": 100,
    "max_script_fields": 32,
    "max_ngram_diff": 1, 
    "max_shingle_diff": 3,
    
    // 可能是"blocks":{"read_only":true, "read_only_allow_delete":true ...}
    "blocks.read_only": true,
    "blocks.read_only_allow_delete": true,
    "blocks.read": true,
    "blocks.write": true,
    "blocks.metadata": true,
    
    // Maximum number of refresh listeners available on each shard of the index.
    "max_refresh_listeners": "不清楚默认值",
    
}}
```

### 1.3. store
　　Configure the type of filesystem used to access shard data.　see: [index-modules-store][EN, index-modules-store]  

[EN, index-modules-store]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-store.html

```json
{
  "settings": {
    "index.store.type": "fs | simplefs | niofs | mmapfs",  // 默认应该是"fs", 不确定
  }
}
```

## 2. settings, analysis(分析器)
　　Settings to define analyzers, tokenizers, token filters and character filters. see: [reference: analysis][EN, reference: analysis]  
　　see more:  
　　　　[配置分析器][CN, configuring-analyzers], [configuring-analyzers][EN, configuring-analyzers]  
　　　　[自定义分析器][CN, custom-analyzers], [custom-analyzers][EN, custom-analyzers]  
　　　　**[reference: analyzer][EN, reference: analyzer]**  
　　　　**[reference: search-analyzer][EN, reference: search-analyzer]**       

[CN, configuring-analyzers]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/configuring-analyzers.html
[EN, configuring-analyzers]: https://www.elastic.co/guide/en/elasticsearch/guide/current/configuring-analyzers.html
[CN, custom-analyzers]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/custom-analyzers.html
[EN, custom-analyzers]: https://www.elastic.co/guide/en/elasticsearch/guide/current/custom-analyzers.html
[EN, reference: analysis]: https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis.html
[EN, reference: analyzer]: https://www.elastic.co/guide/en/elasticsearch/reference/current/analyzer.html
[EN, reference: search-analyzer]: https://www.elastic.co/guide/en/elasticsearch/reference/current/search-analyzer.html

　　(部分)_analyze API:  
　　　　1) localhost:9200/_analyze?pretty&analyzer={ik_smart|standard}&text={字符串}  
　　　　　　用指定的`analyzer`把`text`分解成单个词条(term), 返回词条条目(terms).  

　　　　2) localhost:9200/{index_name}/_analyze?pretty&field={field_name}&text={字符串}  
　　　　　　用`index.field`定义的`analyzer`把`text`分解成单个词条(term), 返回词条条目(terms).  

　　`settings.analysis.analyzer` 执行流程:  
　　　　1) 先用"char_filter"整理尚未被分词的字符串.  
　　　　2) 再用"tokenizer" 把字符串分解成单个词条(term)或者词汇单元(tokens);  
　　　　　　备注: An analyzer must have a single tokenizer. (每个analyzer只能有唯一的tokenizer)  
　　　　3) 经过分词, 作为结果的 词单元流 会按照指定的顺序通过指定的词单元过滤器(token filters, 即此"filter") 。token filters可以修改、添加或者移除词单元。  
```json
/* settings.analysis含义:  
 1. 使用"html_strip"字符过滤器移除HTML部分。
 2. 使用一个自定义的"mapping"字符过滤器, 把"&"替换为"and": 
 3. 使用"standard"分词器分词.
 4. 小写词条, 使用"lowercase"词过滤器处理.
 5. 使用自定义:"my_stopwords", 词过滤器移除自定义的停止词列表中包含的词.
*/
{ "settings": {
      "analysis": {
          "char_filter": {  // custom character filters
              "&_to_and": {
              "type": "mapping",
              "mappings": [ "&=> and "]
          }},
            
      //  "tokenizer":   { ...    custom tokenizers     ... },
        
          "filter": {  // custom token filters
              "my_stopwords": {
                  "type": "stop",
                  "stopwords": [ "the", "a" ]
          }},
            
          "analyzer": {  // custom analyzers
              "my_analyzer": {
                  "type": "custom",
                  "char_filter": [ "html_strip", "&_to_and" ],
                  "tokenizer": "standard",
                  "filter": [ "lowercase", "my_stopwords" ]
              },
              "comma_analyzer":{  // 逗号分词
                  "type": "pattern",
                  "pattern": ","
              }
          }
      }
}}

// 使用示例: 
// PUT /my_index/_mapping/my_type
{
    "properties": {
        "title": {
            "type":      "string",
            "analyzer":  "my_analyzer"
        }
    }
}
```

## 3. settings, mappings(映射)
　　Enable or disable dynamic mapping for an index. see: [index-modules-mapper][[EN, index-modules-mapper]  
　　see more: [映射][CN, mapping], [mapping][EN, mapping]; [reference: mapping][EN, reference: mapping] ;参考文件: ElasticSearch - mappings.md;  

[CN, mapping]: https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping.html
[EN, mapping]: https://www.elastic.co/guide/en/elasticsearch/guide/current/mapping.html
[EN, index-modules-mapper]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-mapper.html
[EN, reference: mapping]: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html

## 4. settings, index-shard-allocation
　　Control over where, when, and how shards are allocated to nodes. see: [index-shard-allocation][EN, index-shard-allocation]  
   + [Shard allocation filtering][EN, shard-allocation-filtering]: Controlling which shards are allocated to which nodes.  
   + [Delayed allocation][EN, delayed-allocation]: Delaying allocation of unassigned shards caused by a node leaving.
   + [Total shards per node][EN, allocation-total-shards.html]: A hard limit on the number of shards from the same index per node.

[EN, index-shard-allocation]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-allocation.html
[EN, shard-allocation-filtering]: https://www.elastic.co/guide/en/elasticsearch/reference/current/shard-allocation-filtering.html
[EN, delayed-allocation]: https://www.elastic.co/guide/en/elasticsearch/reference/current/delayed-allocation.html
[EN, allocation-total-shards.html]: https://www.elastic.co/guide/en/elasticsearch/reference/current/allocation-total-shards.html

## 5. settings, index-modules-merge
　　Control over how shards are merged by the background merge process. see: [index-modules-merge][EN, index-modules-merge]

[EN, index-modules-merge]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-merge.html
```json
{"settings": {
    // Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() / 2)
   "merge.scheduler.max_thread_count": "Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() / 2)"
}}
```

## 6. settings, index-modules-similarity
　　Configure custom similarity settings to customize how search results are scored. see: [index-modules-similarity][EN, index-modules-similarity]

[EN, index-modules-similarity]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-similarity.html

## 7. settings, index-modules-slowlog
　　Control over how slow queries and fetch requests are logged. see: [index-modules-slowlog][EN, index-modules-slowlog]  

[EN, index-modules-slowlog]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-slowlog.html

## 8. settings, index-modules-translog
　　Control over the transaction log and background flush operations. see: [index-modules-similarity][EN, index-modules-translog]  

[EN, index-modules-translog]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-translog.html
