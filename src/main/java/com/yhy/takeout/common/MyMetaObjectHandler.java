package com.yhy.takeout.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.yhy.takeout.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 元数据填充处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insert auto fill");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", ThreadLocalUtil.getCurrentId());
        metaObject.setValue("updateUser", ThreadLocalUtil.getCurrentId());
    }

    /**
     * 更新操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", ThreadLocalUtil.getCurrentId());
    }
}
