package com.atguigu.gmall.ums.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.alisms.api.GmallAlismsAip;
import com.atguigu.gmall.ums.feign.GmallAliSmsClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class AliSmsListner {

    @Autowired
    private GmallAliSmsClient gmallAliSmsClient;

    @RabbitListener(bindings =@QueueBinding(
            value = @Queue(value = "GMALL-ALISMS_QUEUE",durable = "true"),
            exchange = @Exchange(value = "GMALL-ALISMS-EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"sms.*"}

    ))
    public void listen(Map<String,Object> map){
        if(!CollectionUtils.isEmpty(map)){
            String phoneNum = map.get("phoneNum").toString();
            if(StringUtils.isNotBlank(phoneNum)){
                //用于监听，用户注册成功发送注册成功的短信
                //Resp<Object> objectResp = this.gmallAliSmsClient.sendRegisterMess(phoneNum);
                //String s = objectResp.getData().toString();
                //System.out.println(s);
            }
        }
    }


}
