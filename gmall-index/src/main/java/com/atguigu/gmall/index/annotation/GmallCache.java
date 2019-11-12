package com.atguigu.gmall.index.annotation;



import java.lang.annotation.*;
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
    /**
     * 缓存前缀
     * @return
     */
    String prefix() default  "cache";

    /**
     * 过期时间
     * @return
     */
    long timeout() default  300;

    /**
     * 防止缓存雪崩，随机时间
     * @return
     */
    long random() ;


}
