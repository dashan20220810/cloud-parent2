package com.baisha.modulespringcacheredis.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author yh
 * @className RedissonConfig
 * @description 使得原本作为协调单机多线程并发程序的工具包获得了协调分布式多机多线程并发系统的能力，大大降低了设计和研发大规模分布式系统的难度。
 **/

@Slf4j
@Configuration
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;


    /**
     * 单机模式
     *
     * @return
     */
    @SuppressWarnings("all")
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        String redisUrl = String.format("redis://%s:%s", redisProperties.getHost(), redisProperties.getPort());
        serverConfig.setAddress(redisUrl);
        String password = redisProperties.getPassword();
        if (!StringUtils.isEmpty(password)) {
            log.info("redis password : [{}]", password);
            serverConfig.setPassword(password);
        }
        //使用json序列化方式
        Codec codec = new JsonJacksonCodec();
        config.setCodec(codec);
        RedissonClient redissonClient = Redisson.create(config);
        log.info(" redisson success ");
        return redissonClient;
    }

    /**
     * 哨兵模式
     *
     * @return
     */
    /*@SuppressWarnings("all")
    @Bean
    public RedissonClient redissonClient() {
        List<String> nodes = redisProperties.getSentinel().getNodes();
        if (CollectionUtils.isEmpty(nodes)) {
            log.error("nodes IS NULL , Sentinel地址为空,系统退出");
            System.exit(0);
        }
        System.out.println(nodes);
        Config config = new Config();
        SentinelServersConfig serversConfig = config.useSentinelServers();
        serversConfig.setMasterName(redisProperties.getSentinel().getMaster());
        serversConfig.setDatabase(redisProperties.getDatabase());
        for (String node : nodes) {
            log.info("redis sentinel addr : [{}]", node);
            if (StringUtils.isNotEmpty(node)) {
                serversConfig.addSentinelAddress("redis://" + node.trim());
            }
        }
        String password = redisProperties.getPassword();
        if (StringUtils.isNotEmpty(password)) {
            log.info("redis password : [{}]", password);
            serversConfig.setPassword(password);
        }
        serversConfig.setCheckSentinelsList(false);

        RedissonClient redissonClient = Redisson.create(config);
        log.info(" redisson success ");
        return redissonClient;
    }*/


    /**
     * 集群模式
     */
    /*@SuppressWarnings("all")
    @Bean
    public RedissonClient redissonClient() {
        List<String> nodes = redisProperties.getCluster().getNodes();
        if (CollectionUtils.isEmpty(nodes)) {
            log.error("nodes IS NULL , REDIS集群地址为空,系统退出");
            System.exit(0);
        }

        Config config = new Config();
        ClusterServersConfig serverConfig = config.useClusterServers();
        serverConfig.setScanInterval(2000); // 集群状态扫描间隔时间，单位是毫秒
        for (String node : nodes) {
            log.info("redis addr : [{}]", node);
            if (StringUtils.isNotEmpty(node)) {
                serverConfig.addNodeAddress("redis://" + node.trim());//// use "rediss://" for SSL connection
            }
        }

        // 设置密码
        String password = redisProperties.getPassword();
        if (StringUtils.isNotEmpty(password)) {
            log.info("redis password : [{}]", password);
            serverConfig.setPassword(password);
        }

        RedissonClient redissonClient = Redisson.create(config);
        log.info(" redisson success ");
        return redissonClient;
    }*/

}





