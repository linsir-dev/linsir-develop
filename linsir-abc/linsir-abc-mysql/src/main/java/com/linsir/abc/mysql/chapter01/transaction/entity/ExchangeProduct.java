package com.linsir.abc.mysql.chapter01.transaction.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 兑换商品实体类
 * 
 * <p>积分商城中的可兑换商品信息</p>
 * 
 * <p>对应数据库表：exchange_products</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Data
public class ExchangeProduct {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 商品编码
     * <p>唯一标识商品，格式如：PROD001</p>
     */
    private String productCode;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 所需积分
     * <p>兑换该商品需要的积分数量</p>
     */
    private Long requiredPoints;
    
    /**
     * 参考价格
     * <p>商品的市场参考价格</p>
     */
    private BigDecimal price;
    
    /**
     * 商品状态
     * <p>0-下架，1-上架</p>
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    // 状态常量
    
    /**
     * 状态：下架
     */
    public static final int STATUS_OFFLINE = 0;
    
    /**
     * 状态：上架
     */
    public static final int STATUS_ONLINE = 1;
    
    /**
     * 获取状态描述
     * 
     * @param status 状态代码
     * @return 状态描述
     */
    public static String getStatusDescription(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case STATUS_OFFLINE -> "已下架";
            case STATUS_ONLINE -> "上架中";
            default -> "未知";
        };
    }
    
    /**
     * 是否可兑换
     * 
     * @return true-可兑换
     */
    public boolean isAvailable() {
        return status != null && status == STATUS_ONLINE;
    }
    
    /**
     * 检查积分是否足够兑换
     * 
     * @param points 用户拥有的积分
     * @return true-积分足够
     */
    public boolean canExchange(Long points) {
        if (points == null || requiredPoints == null) {
            return false;
        }
        return isAvailable() && points >= requiredPoints;
    }
}
