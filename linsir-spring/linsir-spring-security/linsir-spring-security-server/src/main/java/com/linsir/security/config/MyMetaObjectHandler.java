package com.linsir.security.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 自动填充处理器
 *
 * @author linsir
 * @version 1.0.0
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时的填充策略
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 填充创建时间（使用 fillStrategy 强制填充）
        this.fillStrategy(metaObject, "createTime", LocalDateTime.now());

        // 填充更新时间
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());

        // 填充逻辑删除字段（默认为0，未删除）
        this.fillStrategy(metaObject, "deleted", 0);

        log.debug("插入填充完成");
    }

    /**
     * 更新时的填充策略
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 填充更新时间
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());

        log.debug("更新填充完成");
    }
}
