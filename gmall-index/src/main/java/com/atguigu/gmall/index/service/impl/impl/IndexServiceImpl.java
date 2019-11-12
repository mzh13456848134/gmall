package com.atguigu.gmall.index.service.impl.impl;

import ch.qos.logback.core.util.TimeUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategorySonVo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private  StringRedisTemplate redisTemplate;

    private static final String SUB_CATEGORY_PREFIXX = "index:category:";

    @Autowired
    private JedisPool jedisPool;


    @Autowired
    private RedissonClient redissonClient;




    @Override
    public List<CategoryEntity> queryCvategory1() {
        Resp<List<CategoryEntity>> listResp = this.gmallPmsClient.queryCategory(1, null);
        List<CategoryEntity> categoryEntities = listResp.getData();
        return categoryEntities;
    }

    @Override
    @GmallCache(prefix = SUB_CATEGORY_PREFIXX,timeout = 500000L,random = 300000L)
    public List<CategorySonVo> queryCategorySonByPid(Integer pid) {



        //有直接取
        Resp<List<CategorySonVo>> listResp = this.gmallPmsClient.queryCategoryByPid(pid);
        List<CategorySonVo> categorySonVos = listResp.getData();

        return categorySonVos;
    }





    @Override
    public String test() {

        RLock lock = this.redissonClient.getLock("lock");
        lock.lock();

        String numString =  this.redisTemplate.opsForValue().get("num");
        if(StringUtils.isEmpty(numString)){
            return null;
        }

        Integer num = Integer.valueOf(numString);
        this.redisTemplate.opsForValue().set("num",String.valueOf(++num));

        lock.unlock();

        return "已经增加成功";

    }

    @Override
    public String testRead() {
        RReadWriteLock rwLock = this.redissonClient.getReadWriteLock("rwLock");
        rwLock.readLock().lock(10L,TimeUnit.SECONDS);
        String msg = this.redisTemplate.opsForValue().get("msg");
        //rwLock.readLock().unlock();
        return msg;
    }

    @Override
    public String testWrite() {
        RReadWriteLock rwLock = this.redissonClient.getReadWriteLock("rwLock");
        rwLock.writeLock().lock(10L,TimeUnit.SECONDS);
        String msg = UUID.randomUUID().toString();
        this.redisTemplate.opsForValue().set("msg",msg);
       // rwLock.writeLock().unlock();
        return "写入数据成功" + msg;
    }

    @Override
    public String latch() throws InterruptedException {
        RCountDownLatch lock = redissonClient.getCountDownLatch("latchDown");
        String msString = this.redisTemplate.opsForValue().get("ms");
        lock.trySetCount(Integer.parseInt(msString));
        lock.await();

        return "订单生成成功!";
    }

    @Override
    public String out() {
        RCountDownLatch lock = redissonClient.getCountDownLatch("latchDown");
        String msString = this.redisTemplate.opsForValue().get("ms");
        int count = Integer.parseInt(msString);
        this.redisTemplate.opsForValue().set("ms",String.valueOf(--count));
        lock.countDown();
        return "处理完流程"+count;
    }

   /* public synchronized String test1() {

        String uuid = UUID.randomUUID().toString();
        //所有请求，竞争锁
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 10,TimeUnit.SECONDS);

        //获取到锁执行业务逻辑
        if(lock){
            String numString =  this.redisTemplate.opsForValue().get("num");
            if(StringUtils.isEmpty(numString)){
                return null;
            }

            Integer num = Integer.valueOf(numString);
            this.redisTemplate.opsForValue().set("num",String.valueOf(++num));







            Jedis jedis = null;
            try {
                jedis = this.jedisPool.getResource();
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // this.redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList("lock"),uuid);
                jedis.eval(script,Arrays.asList("lock"),Arrays.asList(uuid));
            *//*if(StringUtils.equals(uuid,redisTemplate.opsForValue().get("lock"))){
                //释放锁
                this.redisTemplate.delete("lock");
            }*//*
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(jedis!=null){
                    jedis.close();
                }
            }




        }else{
            try {
                TimeUnit.SECONDS.sleep(1);
                test();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "已经增加成功";

    }*/
}
