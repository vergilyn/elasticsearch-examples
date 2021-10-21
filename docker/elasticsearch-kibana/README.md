# elasticsearch-kibana

+ [Install Elasticsearch(7.11.x) with Docker](https://www.elastic.co/guide/en/elasticsearch/reference/7.11/docker.html)
+ [Install Kibana(7.11.x) with Docker](https://www.elastic.co/guide/en/kibana/7.11/docker.html)

| image         | tags                 | compress-size | size      |
|:--------------|:---------------------|:--------------|:----------|
| elasticsearch | elasticsearch:7.11.1 | 402.65 MB     | 819.27 MB |
| kibana        | kibana:7.11.1        | 371.44 MB     | 1.01 GB   |

## Q&A

1. 宿主机无法成功访问`https://127.0.0.1:9200`，容器内访问正常。  
`elasticsearch.yml` 中 `network.host: 127.0.0.1` 改为 `0.0.0.0` 后宿主机可以正常访问。

2. docker-container 之间互相访问？
+ [Networking in Compose](https://docs.docker.com/compose/networking/)
+ [docker network](https://docs.docker.com/engine/reference/commandline/network/)

3. 访问kibana
<http://127.0.0.1:5601/>