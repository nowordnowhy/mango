# common-open

#### 介绍
redisson-spring-boot-starter

#### 使用说明

1.  pom引用

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>

        <dependency>
            <groupId>com.lushwe</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>1.0</version>
        </dependency>

        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>${redisson.version}</version>
        </dependency>

2.  创建 application.properties 文件，配置 redisson.config-location ，详细如下
    > # Redisson 配置 
    > redisson.config-location=classpath:redisson-redis.yaml
3.  创建 redisson-redis.yaml 或 redisson-redis.json


    redisson-redis.json 文件
        {
            "singleServerConfig":{
                "dnsMonitoringInterval": -1,
                "idleConnectionTimeout":10000,
                "pingTimeout":1000,
                "connectTimeout":10000,
                "timeout":3000,
                "retryAttempts":3,
                "retryInterval":1500,
                "reconnectionTimeout":3000,
                "failedAttempts":3,
                "password":"WN6oLpXAKAJC9Lxx",
                "subscriptionsPerConnection":2147483647,
                "clientName":null,
                "address": "redis://redis-bxjag6g7b534-proxy-nlb.jvessel-open-sh.jdcloud.com:6379",
                "subscriptionConnectionMinimumIdleSize":1,
                "subscriptionConnectionPoolSize":150,
                "connectionMinimumIdleSize":32,
                "connectionPoolSize":64,
                "database":6
            },
            "threads":0,
            "nettyThreads":0,
            "codec":{
                "class":"org.redisson.codec.JsonJacksonCodec"
            },
            "transportMode":"NIO"
        }


    redisson-redis.yaml 文件
            masterSlaveServersConfig:
                slaveAddresses:
                    - "redis://127.0.0.1:6379"
                masterAddress: "redis://127.0.0.1:6379"
                password: xxx
                idleConnectionTimeout: 10000
                pingTimeout: 1000
                connectTimeout: 10000
                timeout: 3000
                retryAttempts: 3
                retryInterval: 1500
                reconnectionTimeout: 3000
                failedAttempts: 3
                subscriptionsPerConnection: 5
                clientName: null
                loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> {}
                slaveSubscriptionConnectionMinimumIdleSize: 1
                slaveSubscriptionConnectionPoolSize: 50
                slaveConnectionMinimumIdleSize: 10
                slaveConnectionPoolSize: 64
                masterConnectionMinimumIdleSize: 10
                masterConnectionPoolSize: 64
                readMode: "SLAVE"
            threads: 0
            nettyThreads: 0
            codec: !<org.redisson.codec.JsonJacksonCodec> {}    
            useLinuxNativeEpoll: false
