package com.seatunnel.orchestrator.enums;

public enum SourcePlugin {

  AMAZONDYNAMODB("AmazonDynamoDB"),
  AMAZONSQS("AmazonSqs"),
  CASSANDRA("Cassandra"),
  CLICKHOUSE("Clickhouse"),
  CLOUDBERRY("Cloudberry"),
  COSFILE("CosFile"),
  DB2("DB2"),
  DORIS("Doris"),
  EASYSEARCH("Easysearch"),
  ELASTICSEARCH("Elasticsearch"),
  FAKESOURCE("FakeSource"),
  FTPFILE("FtpFile"),
  GITHUB("Github"),
  GITLAB("Gitlab"),
  GOOGLESHEETS("GoogleSheets"),
  GRAPHQL("GraphQL"),
  GREENPLUM("Greenplum"),
  HBASE("Hbase"),
  HDFSFILE("HdfsFile"),
  HIVE("Hive"),
  HIVEJDBC("HiveJdbc"),
  HTTP("Http"),
  ICEBERG("Iceberg"),
  INFLUXDB("InfluxDB"),
  IOTDB("IoTDB"),
  JDBC("Jdbc"),
  JIRA("Jira"),
  KAFKA("Kafka"),
  KINGBASE("Kingbase"),
  KLAVIYO("Klaviyo"),
  KUDU("Kudu"),
  LEMLIST("Lemlist"),
  LOCALFILE("LocalFile"),
  MAXCOMPUTE("Maxcompute"),
  MILVUS("Milvus"),
  MONGODB("MongoDB"),
  MYHOURS("MyHours"),
  MYSQL("Mysql"),
  NEO4J("Neo4j"),
  NOTION("Notion"),
  OBSFILE("ObsFile"),
  OCEANBASE("OceanBase"),
  ONESIGNAL("OneSignal"),
  OPENMLDB("OpenMldb"),
  ORACLE("Oracle"),
  OSSFILE("OssFile"),
  OSSJINDOFILE("OssJindoFile"),
  PAIMON("Paimon"),
  PERSISTIQ("Persistiq"),
  PHOENIX("Phoenix"),
  POSTGRESQL("PostgreSQL"),
  PROMETHEUS("Prometheus"),
  PULSAR("Pulsar"),
  QDRANT("Qdrant"),
  RABBITMQ("Rabbitmq"),
  REDIS("Redis"),
  REDSHIFT("Redshift"),
  ROCKETMQ("RocketMQ"),
  S3FILE("S3File"),
  SFTPFILE("SftpFile"),
  SLS("Sls"),
  SNOWFLAKE("Snowflake"),
  SOCKET("Socket"),
  SQLSERVER("SqlServer"),
  STARROCKS("StarRocks"),
  TDENGINE("TDengine"),
  TABLESTORE("Tablestore"),
  TYPESENSE("Typesense"),
  VERTICA("Vertica"),
  WEB3J("Web3j"),

  TIDB_CDC("TiDB-CDC"),
  SQLSERVER_CDC("SqlServer-CDC"),
  POSTGRESQL_CDC("PostgreSQL-CDC"),
  OPENGAUSS_CDC("Opengauss-CDC"),
  ORACLE_CDC("Oracle-CDC"),
  MONGODB_CDC("MongoDB-CDC"),
  MYSQL_CDC("MySQL-CDC");


  private final String value;

  SourcePlugin(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
