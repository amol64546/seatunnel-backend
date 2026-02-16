package com.seatunnel.orchestrator.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SinkPlugin {

  ACTIVEMQ("Activemq"),
  AEROSPIKE("Aerospike"),
  AMAZONDYNAMODB("AmazonDynamoDB"),
  AMAZONSQS("AmazonSqs"),
  ASSERT("Assert"),
  CASSANDRA("Cassandra"),
  CLICKHOUSE("Clickhouse"),
  CLICKHOUSEFILE("ClickhouseFile"),
  CLOUDBERRY("Cloudberry"),
  CONSOLE("Console"),
  COSFILE("CosFile"),
  DB2("DB2"),
  DATAHUB("Datahub"),
  DINGTALK("DingTalk"),
  DORIS("Doris"),
  DRUID("Druid"),
  EASYSEARCH("Easysearch"),
  ELASTICSEARCH("Elasticsearch"),
  EMAIL("Email"),
  WECHAT("WeChat"),
  FEISHU("Feishu"),
  FTPFILE("FtpFile"),
  GOOGLEFIRESTORE("GoogleFirestore"),
  GRAPHQL("GraphQL"),
  GREENPLUM("Greenplum"),
  HBASE("Hbase"),
  HDFSFILE("HdfsFile"),
  HIVE("Hive"),
  HTTP("Http"),
  HUDI("Hudi"),
  ICEBERG("Iceberg"),
  INFLUXDB("InfluxDB"),
  IOTDB("IoTDB"),
  JDBC("Jdbc"),
  KAFKA("Kafka"),
  KINGBASE("Kingbase"),
  KUDU("Kudu"),
  LOCALFILE("LocalFile"),
  MAXCOMPUTE("Maxcompute"),
  MILVUS("Milvus"),
  MONGODB("MongoDB"),
  MYSQL("Mysql"),
  NEO4J("Neo4j"),
  OBSFILE("ObsFile"),
  OCEANBASE("OceanBase"),
  ORACLE("Oracle"),
  OSSFILE("OssFile"),
  OSSJINDOFILE("OssJindoFile"),
  PAIMON("Paimon"),
  PHOENIX("Phoenix"),
  POSTGRESQL("PostgreSql"),
  PROMETHEUS("Prometheus"),
  PULSAR("Pulsar"),
  QDRANT("Qdrant"),
  RABBITMQ("Rabbitmq"),
  REDIS("Redis"),
  REDSHIFT("Redshift"),
  ROCKETMQ("RocketMQ"),
  S3_REDSHIFT("S3-Redshift"),
  S3FILE("S3File"),
  SELECTDB_CLOUD("SelectDB-Cloud"),
  SENTRY("Sentry"),
  SFTPFILE("SftpFile"),
  SLACK("Slack"),
  SLS("Sls"),
  SNOWFLAKE("Snowflake"),
  SOCKET("Socket"),
  SQLSERVER("SqlServer"),
  STARROCKS("StarRocks"),
  TDENGINE("TDengine"),
  TABLESTORE("Tablestore"),
  TYPESENSE("Typesense"),
  VERTICA("Vertica");


  private final String value;

  SinkPlugin(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
