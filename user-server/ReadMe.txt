Camel Java Router Project
=========================

=== How to build

To build this project use

    mvn install

=== How to run

You can run this example using

    mvn camel:run

=== More information

You can find more information about Apache Camel at the website: http://camel.apache.org/


server:
  port: 9300

project:
  title: 用户中心
  swagger:
    enable: true

logging:
  level:
    org.hibernate.type.descriptor.sql.BasicBinder: trace

spring:
  security:
    user:
      name: baisha
      password: baisha
  jpa:
    database: mysql
    show-sql: true
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
  datasource:
    url: jdbc:mysql://192.168.26.26:3306/cloud_user?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
    username: cloud
    password: 12qw!@QW
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      auto-commit: true
      minimum-idle: 4
      idle-timeout: 60000
      connection-timeout: 30000
      max-lifetime: 1800000
      pool-name: DatebookHikariCP
      maximum-pool-size: 16
      connection-test-query: select 1
  cache:
    type: redis
  redis:
    host: 192.168.26.24
    port: 6379
    timeout: 2000
    database: 11
    password:
    lettuce:
      pool:
        # 连接池最大连接数（使用负值表示没有限制） 默认 8
        max-active: 32
        # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
        max-wait: -1
        # 连接池中的最大空闲连接 默认 8
        max-idle: 32
        # 连接池中的最小空闲连接 默认 0
        min-idle: 2
  rabbitmq:
    host: 192.168.26.26
    port: 5672
    username: admin
    password: 12qw!@QW


