package com.atguigu.gmall.wms.service.impl;

import com.atguigu.gmall.wms.entity.vo.SkuLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private WareSkuDao wareSkuDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<WareSkuEntity> queryWareSkuBySkuId(Long skuId) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        List<WareSkuEntity> wareSkuEntities = this.list(wrapper);
        return wareSkuEntities;
    }

    @Override
    public String checkAndLock(List<SkuLock> skuLocks) {

        //遍历
        skuLocks.forEach(skuLock -> {
            lockSku(skuLock);
        });

        //查看有没有失败的记录
        List<SkuLock> success = skuLocks.stream().filter(skuLock -> skuLock.getLock()).collect(Collectors.toList());
        List<SkuLock> error = skuLocks.stream().filter(skuLock -> !skuLock.getLock()).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(error)){
            success.forEach(skuLock -> {
                this.wareSkuDao.unlock(skuLock.getSkuWareId(),skuLock.getCount());
            });
            return "锁定失败:" + error.stream().map(skuLock -> skuLock.getSkuId()).collect(Collectors.toList()).toString();
        }

        //有失败的记录，则回滚成功的记录


        return null;
    }

    private void lockSku(SkuLock skuLock){

        RLock lock = redissonClient.getLock("cart:lock:" + skuLock.getSkuId());
        lock.lock();
        //验库存
        List<WareSkuEntity> wareSkuEntities = this.wareSkuDao.queryWareSkuBySkuId(skuLock.getSkuId(),skuLock.getCount());
        //没有库存数锁定失败
        skuLock.setLock(false);
        if(!CollectionUtils.isEmpty(wareSkuEntities)){
            Integer count = this.wareSkuDao.LockStore(wareSkuEntities.get(0).getId() ,skuLock.getCount());
            if(count==1){
                skuLock.setLock(true);
                skuLock.setSkuWareId(wareSkuEntities.get(0).getId());
            }
        }

        //锁库存
        lock.unlock();

    }
}