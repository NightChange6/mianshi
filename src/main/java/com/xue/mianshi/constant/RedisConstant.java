package com.xue.mianshi.constant;

/**
 * redis中key常量
 */
public interface RedisConstant {
    /**
     * 用户签到前缀
     */
    String user_sign_redis_key_prefix = "user:sign";

    /**
     * 返回当前年中用户签到信息的key
     * @param year
     * @param userId
     * @return
     */
    static String getUserSignPrefix(int year,long userId){
        return String.format("%s:%s:%s",user_sign_redis_key_prefix,year,userId);
    }


}
