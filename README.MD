# Sample project for Elasticsearch
### Installation
* Install elasticsearch:
    * `docker pull docker.elastic.co/elasticsearch/elasticsearch:7.9.0`
    * `docker run --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.9.0`
* Install kibana:
    * `docker pull docker.elastic.co/kibana/kibana:7.9.0`
    * `docker run --name kibana --link elasticsearch:elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:7.9.0`
* Open kibana: _0.0.0.0:5601_
* Download simple data: _kibana_sample_data_ecommerce_
* Run elasticsearch container first if not started yet
* Then run kibana container
* Run project as Spring Boot project
### Sample

* `curl --request GET --url dockhttp://localhost:8080/elastic/fields`
* `curl --request GET --url 'http://localhost:8080/elastic/chart-data?fieldName=geoip.region_name'`

### Let explore elasticsearch
* _https://www.elastic.co/home_