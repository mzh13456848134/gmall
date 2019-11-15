package com.atguigu.gmallorder.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.vo.CartItemVo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmallorder.feign.*;
import com.atguigu.gmallorder.service.OrderService;
import com.atguigu.gmallorder.vo.OrderConfirmVo;
import com.atguigu.gmallorder.vo.OrderItemVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vo.ItemSaleVo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private GmallUmsFeign gmallUmsFeign;

    @Autowired
    private GmallPmsFeign gmallPmsFeign;

    @Autowired
    private GmallWmsFeign gmallWmsFeign;

    @Autowired
    private GmallSmsFeign gmallSmsFeign;

    @Autowired
    private GmallCartFeign gmallCartFeign;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public OrderConfirmVo confirm(Long userId) {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();


        CompletableFuture<Void> memberAddressFuture = CompletableFuture.runAsync(() -> {
            //获取用户地址信息
            Resp<List<MemberReceiveAddressEntity>> memberAddressResp = gmallUmsFeign.queryAddressByUserId(userId);
            List<MemberReceiveAddressEntity> memberReceiveAddressEntities = memberAddressResp.getData();
            orderConfirmVo.setAddresses(memberReceiveAddressEntities);

        }, threadPoolExecutor);


        CompletableFuture<Void> memberFuture = CompletableFuture.runAsync(() -> {
            //获取用户的积分信息
            Resp<MemberEntity> memberEntityResp = gmallUmsFeign.queryUserById(userId);
            MemberEntity memberEntity = memberEntityResp.getData();
            orderConfirmVo.setBounds(memberEntity.getIntegration());
        }, threadPoolExecutor);


        CompletableFuture<Void> cartFuture = CompletableFuture.supplyAsync(() -> {
            //获取购物车中选中商品的信息
            Resp<List<CartItemVo>> cartResp = gmallCartFeign.queryCheckSkuInfo(userId);
            List<CartItemVo> cartItemVos = cartResp.getData();
            return cartItemVos;
        }, threadPoolExecutor).thenAcceptAsync(cartItemVos -> {
            if (CollectionUtils.isEmpty(cartItemVos)) {
                return;
            }
            List<OrderItemVo> orderItemVos = cartItemVos.stream().map(cartItemVo -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setCount(cartItemVo.getCount());
                orderItemVo.setSkuId(cartItemVo.getSkuId());


                //通过skuid查询skuinfo
                Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsFeign.querySkuById(cartItemVo.getSkuId());
                SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                orderItemVo.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                orderItemVo.setPrice(skuInfoEntity.getPrice());
                orderItemVo.setTitle(skuInfoEntity.getSkuTitle());
                orderItemVo.setWeight(skuInfoEntity.getWeight());


                //通过skuid查询销售属性信息
                Resp<List<SkuSaleAttrValueEntity>> skuSaleAttrResp = gmallPmsFeign.querySaleAttrValuesBySkuId(cartItemVo.getSkuId());
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuSaleAttrResp.getData();
                orderItemVo.setSkuSaleAttrValueEntities(skuSaleAttrValueEntities);


                //根据skuid查询营销信息
                Resp<List<ItemSaleVo>> itemSaleResp = gmallSmsFeign.queryItemSaleVos(cartItemVo.getSkuId());
                List<ItemSaleVo> itemSaleVos = itemSaleResp.getData();
                orderItemVo.setSales(itemSaleVos);


                //根据skuid查询库存信息
                Resp<List<WareSkuEntity>> wareSkuResp = gmallWmsFeign.queryWareSkuBySkuId(cartItemVo.getSkuId());
                List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
                boolean b = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
                orderItemVo.setStore(b);
                return orderItemVo;

            }).collect(Collectors.toList());
            orderConfirmVo.setOrderItems(orderItemVos);
        }, threadPoolExecutor);


        CompletableFuture<Void> tokenFuture = CompletableFuture.runAsync(() -> {
            //生成唯一字段，和订单信息
            String timeId = IdWorker.getTimeId();
            orderConfirmVo.setOrderToken(timeId);
        }, threadPoolExecutor);

        CompletableFuture.allOf(memberAddressFuture,memberFuture,cartFuture,tokenFuture).join();

        return orderConfirmVo;
    }
}
