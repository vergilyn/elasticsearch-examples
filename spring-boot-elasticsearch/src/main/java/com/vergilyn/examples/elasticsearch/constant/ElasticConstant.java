package com.vergilyn.examples.elasticsearch.constant;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/14
 */
public interface ElasticConstant {
    public enum ElasticIndex{
        DEFAULT("vergilyn_index", "个人索引")
        ,SINGER("singers_index", "歌手")
        ,NEWS_INFO("news_info_index", "新闻信息");

        private String name;
        private String desc;

        ElasticIndex(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum ElasticType{
        DEFAULT("person", "")
        ,SINGER("singers", "歌手")
        ,NEWS_INFO("news_info_type", "新闻信息");

        private String name;
        private String desc;

        ElasticType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }
}
