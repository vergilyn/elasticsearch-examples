# analyzer-examples

- ik-analyzer: <https://github.com/medcl/elasticsearch-analysis-ik>
- HanLP: <https://www.hanlp.com/>

## HanLP
- <https://www.hanlp.com/>
- <https://github.com/hankcs/HanLP>
- <https://github.com/KennFalcon/elasticsearch-analysis-hanlp>
- HanLP Document: <https://hanlp.hankcs.com/docs/index.html>
- 语言学标注规范：<https://hanlp.hankcs.com/docs/annotations/index.html>
- 格式规范：<https://hanlp.hankcs.com/docs/data_format.html>

**支持的分词方式：**  
- hanlp: hanlp默认分词
- hanlp_standard: 标准分词，使用HanLP标准分词器分词速度快，资源占用小
- hanlp_index: 索引分词，事先建立文档索引库，按照索引进行快速分词，分词效率更高
- hanlp_nlp: NLP分词，能够完成词性标注的一种常用分词方式
- hanlp_crf: CRF分词，目前非深度学习方法中最佳的分词效果，対歧义词和未知词的识别效果更优
- hanlp_n_short: N-最短路分词，利用最短路径形成有向无环图，通过权值来优化分词结果。（不是n-gram）
- hanlp_dijkstra: 最短路分词，？？？
- hanlp_speed: 极速词典分词，分词效率最高的一种方法，分词速率可达2000万字/秒

