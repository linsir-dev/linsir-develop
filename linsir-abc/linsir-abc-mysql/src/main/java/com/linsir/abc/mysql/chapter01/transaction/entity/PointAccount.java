package com.linsir.abc.mysql.chapter01.transaction.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 积分账户实体类
 * 
 * <p>用于演示事务日志和持久性，记录用户的积分信息</p>
 * 
 * <p>对应数据库表：point_accounts</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Data
public class PointAccount {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     * <p>关联的用户唯一标识</p>
     */
    private Long userId;
    
    /**
     * 可用积分
     * <p>当前可以使用的积分数量</p>
     */
    private Long availablePoints;
    
    /**
     * 冻结积分
     * <p>被冻结的积分，如兑换处理中的积分</p>
     */
    private Long frozenPoints;
    
    /**
     * 累计获得积分
     * <p>历史累计获得的总积分</p>
     */
    private Long totalEarned;
    
    /**
     * 累计消费积分
     * <p>历史累计消费的积分</p>
     */
    private Long totalConsumed;
    
    /**
     * 乐观锁版本号
     * <p>用于并发控制</p>
     */
    private Integer version;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 增加积分
     * <p>同时更新可用积分和累计获得积分</p>
     * 
     * @param points 要增加的积分数量
     * @throws IllegalArgumentException 如果积分为负数
     */
    public void addPoints(Long points) {
        if (points == null || points < 0) {
            throw new IllegalArgumentException("增加的积分必须为正数");
        }
        if (this.availablePoints == null) {
            this.availablePoints = 0L;
        }
        if (this.totalEarned == null) {
            this.totalEarned = 0L;
        }
        this.availablePoints += points;
        this.totalEarned += points;
    }
    
    /**
     * 扣减积分
     * <p>同时更新可用积分和累计消费积分</p>
     * 
     * @param points 要扣减的积分数量
     * @throws IllegalStateException 如果积分不足
     * @throws IllegalArgumentException 如果积分为负数
     */
    public void deductPoints(Long points) {
        if (points == null || points < 0) {
            throw new IllegalArgumentException("扣减的积分必须为正数");
        }
        if (this.availablePoints == null) {
            this.availablePoints = 0L;
        }
        if (this.availablePoints < points) {
            throw new IllegalStateException("积分不足，当前可用积分：" + this.availablePoints);
        }
        if (this.totalConsumed == null) {
            this.totalConsumed = 0L;
        }
        this.availablePoints -= points;
        this.totalConsumed += points;
    }
    
    /**
     * 冻结积分
     * 
     * @param points 要冻结的积分数量
     * @throws IllegalStateException 如果积分不足
     */
    public void freezePoints(Long points) {
        if (points == null || points < 0) {
            throw new IllegalArgumentException("冻结的积分必须为正数");
        }
        if (this.availablePoints == null || this.availablePoints < points) {
            throw new IllegalStateException("积分不足，无法冻结");
        }
        if (this.frozenPoints == null) {
            this.frozenPoints = 0L;
        }
        this.availablePoints -= points;
        this.frozenPoints += points;
    }
    
    /**
     * 解冻积分
     * 
     * @param points 要解冻的积分数量
     */
    public void unfreezePoints(Long points) {
        if (points == null || points < 0) {
            throw new IllegalArgumentException("解冻的积分必须为正数");
        }
        if (this.frozenPoints == null || this.frozenPoints < points) {
            throw new IllegalStateException("冻结积分不足，无法解冻");
        }
        this.frozenPoints -= points;
        this.availablePoints += points;
    }
    
    /**
     * 检查积分是否充足
     * 
     * @param points 需要检查的积分数量
     * @return true-积分充足，false-积分不足
     */
    public boolean hasSufficientPoints(Long points) {
        if (points == null || points < 0) {
            return false;
        }
        return this.availablePoints != null && this.availablePoints >= points;
    }
    
    /**
     * 获取总积分
     * <p>总积分 = 可用积分 + 冻结积分</p>
     * 
     * @return 总积分
     */
    public Long getTotalPoints() {
        long available = this.availablePoints != null ? this.availablePoints : 0L;
        long frozen = this.frozenPoints != null ? this.frozenPoints : 0L;
        return available + frozen;
    }
}
