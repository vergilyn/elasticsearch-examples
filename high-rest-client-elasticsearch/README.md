# Java High Level REST Client
　　see: [elasticsearch guid: java-rest-high, 6.4][EN, java-rest-high: 6.4]  

[EN, java-rest-high: 6.4]: https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.4/java-rest-high.html
　　code example:  
　　　　[hainet, elasticsearch-rest-high-level-client-sample](https://github.com/hainet/elasticsearch-rest-high-level-client-sample)

测试代码想达到的目的：
  `_index = app_xx`，例如 app_csdn、app_cnblog。xx 表示文章的来源网站。
  每个`_index`没有细分type，统一都叫`source_type_all`。
  
  csdn 的栏目下搜索，只会搜索 `_index=app_csdn，_source=source_type_all`下的数据。
  
目的达到，但代码需要认真重构。