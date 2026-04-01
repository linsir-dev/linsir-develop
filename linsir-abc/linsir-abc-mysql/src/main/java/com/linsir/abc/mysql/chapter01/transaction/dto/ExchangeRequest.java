package com.linsir.abc.mysql.chapter01.transaction.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 积分兑换请求DTO
 *
 * <p>用于接收积分兑换请求的参数</p>
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
public class ExchangeRequest {

    /**
     * 用户ID
     * <p>不能为空</p>
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 商品ID
     * <p>不能为空</p>
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 兑换数量
     * <p>必须大于等于1</p>
     */
    @NotNull(message = "兑换数量不能为空")
    @Min(value = 1, message = "兑换数量必须大于等于1")
    private Integer quantity;

    /**
     * 获取验证错误信息
     *
     * @return 错误信息，如果验证通过返回null
     */
    public String getValidationError() {
        if (userId == null) {
            return "用户ID不能为空";
        }
        if (productId == null) {
            return "商品ID不能为空";
        }
        if (quantity == null || quantity < 1) {
            return "兑换数量必须大于等于1";
        }
        return null;
    }

    /**
     * 计算总积分
     *
     * @param pointsPerItem 每件商品所需积分
     * @return 总积分
     */
    public long calculateTotalPoints(long pointsPerItem) {
        if (quantity == null) {
            return 0;
        }
        return pointsPerItem * quantity;
    }
}
