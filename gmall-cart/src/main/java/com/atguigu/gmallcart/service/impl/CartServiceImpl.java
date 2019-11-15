package com.atguigu.gmallcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.vo.CartItemVo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmallcart.feign.GmallPmsClient;
import com.atguigu.gmallcart.feign.GmallSmsClient;
import com.atguigu.gmallcart.interceptor.LoginInterceptor;
import com.atguigu.gmallcart.service.CartService;
import com.atguigu.gmallcart.vo.Cart;
import com.atguigu.gmallcart.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vo.ItemSaleVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {


    private static final String KEY_PREFIX  = "cart:key:";


    private static  final String CURRENT_PRICE_PRIFIX = "cart:price:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallSmsClient gmallSmsClient;

    @Override
    public void addCart(Cart cart) {
        String key = getKey();

        Long skuId = cart.getSkuId();
        //如果是临时用户添加到临时用户的购物车内
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        Boolean b = hashOps.hasKey(skuId.toString());

        Integer count = cart.getCount();
        if(b){
            //有该记录需要修改其数量
            String cartStr = hashOps.get(skuId.toString()).toString();
            //JSON返序列化
            cart = JSON.parseObject(cartStr,Cart.class);
            cart.setCount(count + cart.getCount());
        }else{
            //如果没有该记录直接添加

            //根据skuid获取sku信息
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();

            if(skuInfoEntity != null){
                cart.setTitle(skuInfoEntity.getSkuTitle());
                cart.setCheck(true);
                cart.setPrice(skuInfoEntity.getPrice());
            }


            //根据skuid获取sku销售属性
            Resp<List<SkuSaleAttrValueEntity>> skuSaleResp = this.gmallPmsClient.querySaleAttrValuesBySkuId(skuId);
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = skuSaleResp.getData();
            cart.setSkuAttrValue(skuSaleAttrValueEntityList);
            cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());


            Resp<List<ItemSaleVo>> itemSaleResp = this.gmallSmsClient.queryItemSaleVos(skuId);
            List<ItemSaleVo> itemSaleVos = itemSaleResp.getData();
            cart.setSales(itemSaleVos);

            this.redisTemplate.opsForValue().set(CURRENT_PRICE_PRIFIX + skuId,skuInfoEntity.getPrice().toString());
            //hashOps.put(skuId.toString(),JSON.toJSONString(cart));
        }

        //将cart信息同步到redis中
        hashOps.put(skuId.toString(),JSON.toJSONString(cart));
    }

    public String getKey() {
        //判断登录状态
        String key = KEY_PREFIX;
        //获取到登录的信息
        UserInfoVO userInfoVO = LoginInterceptor.get();

        if(userInfoVO.getUserId() != null){
            //普通用户
            key += userInfoVO.getUserId();
        }else{
            //临时用户
            key += userInfoVO.getUserKey();

        }
        return key;
    }

    @Override
    public List<Cart> queryCarts() {

        //直接查询临时用户的购物车
        UserInfoVO userInfoVO = LoginInterceptor.get();
        String userkey = KEY_PREFIX + userInfoVO.getUserKey();
        BoundHashOperations<String, Object, Object> userKeyOps = this.redisTemplate.boundHashOps(userkey);
        List<Object> cartJsonStr = userKeyOps.values();


        //如果临时用的购物车有数据，直接解析
        List<Cart> carts = null;
        if(!CollectionUtils.isEmpty(cartJsonStr)){
            carts = cartJsonStr.stream().map(
            cartJson -> {
                Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                cart.setPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PRIFIX + cart.getSkuId())));
                return cart;
            }
            ).collect(Collectors.toList());
        }

        if(StringUtils.isEmpty(userInfoVO.getUserId())){
            return  carts;
        }
        //查看登录状态
        //如果登录,查询登陆用户的购物车

        String idkey = KEY_PREFIX + userInfoVO.getUserId();
        BoundHashOperations<String, Object, Object> uesrIdOps = this.redisTemplate.boundHashOps(idkey);
        //判断登陆的购物车是否为空
        if(!CollectionUtils.isEmpty(carts)){
            //不为空，合并两种购物车，并返回
            carts.forEach(car ->{
                if(uesrIdOps.hasKey(car.getSkuId().toString())){
                    //有该记录需要修改其数量
                    String cartStr = uesrIdOps.get(car.getSkuId().toString()).toString();
                    //JSON返序列化
                    Cart idcart = JSON.parseObject(cartStr,Cart.class);
                    idcart.setCount(car.getCount() + idcart.getCount());
                    uesrIdOps.put(car.getSkuId().toString(), JSON.toJSONString(idcart));

                }else{
                    //如果没有该记录直接添加
                    uesrIdOps.put(car.getSkuId().toString(), JSON.toJSONString(car));

                }
            });
           // this.redisTemplate.delete(userkey);
        }
            //为空，直接返回登陆的购物车
        List<Object> userIdCartJSON = uesrIdOps.values();
        if(CollectionUtils.isEmpty(userIdCartJSON)){
            return null;
        }
        return userIdCartJSON.stream().map(
            carJSON -> {
                Cart cart = JSON.parseObject(carJSON.toString(), Cart.class);
                cart.setPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PRIFIX+cart.getSkuId())));
                return cart;

            }
        ).collect(Collectors.toList());
        //直接返回
    }

    @Override
    public void updateCart(Cart cart) {
        String key = getKey();

        Integer count = cart.getCount();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        if(hashOps.hasKey(cart.getSkuId().toString())){
            //获取购物车中的数量的购物记录
            String cartStr = hashOps.get(cart.getSkuId().toString()).toString();
            cart = JSON.parseObject(cartStr, Cart.class);
            cart.setCount(count);
            hashOps.put(cart.getSkuId().toString(),JSON.toJSONString(cart));
        }
    }

    @Override
    public void deleteCart(Long skuId) {
        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        if(hashOps.hasKey(skuId.toString())){
           hashOps.delete(skuId.toString());
        }
    }

    @Override
    public void checkCart(List<Cart> cart) {
        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        cart.forEach(car ->{
            if(hashOps.hasKey(car.getSkuId().toString())){
                String cartJson = hashOps.get(car.getSkuId().toString()).toString();
                Cart cartObject = JSON.parseObject(cartJson, Cart.class);
                cartObject.setCheck(car.getCheck());
                cartJson = JSON.toJSONString(cartObject);
                hashOps.put(car.getSkuId().toString(),cartJson);
            }

        });
    }

    @Override
    public List<CartItemVo> queryCheckSkuInfo(Long userId) {
        //ThreadLocal 只能用过浏览器请求与拿到，不能用过远程调用拿到
        //UserInfoVO userInfoVO = LoginInterceptor.get();
        String idkey = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> uesrIdOps = this.redisTemplate.boundHashOps(idkey);

        //为空，直接返回登陆的购物车
        List<Object> userIdCartJSON = uesrIdOps.values();
        if(CollectionUtils.isEmpty(userIdCartJSON)){
            return null;
        }


        return userIdCartJSON.stream().map(
                carJSON -> {
                    Cart cart = JSON.parseObject(carJSON.toString(), Cart.class);
                    cart.setPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PRIFIX + cart.getSkuId())));
                    return cart;
                }
        ).filter(cart -> cart.getCheck()).map(cart -> {
            CartItemVo cartItemVo = new CartItemVo();
            cartItemVo.setCount(cart.getCount());
            cartItemVo.setSkuId(cart.getSkuId());
            return cartItemVo;
        }).collect(Collectors.toList());
        //直接返回
    }
}
