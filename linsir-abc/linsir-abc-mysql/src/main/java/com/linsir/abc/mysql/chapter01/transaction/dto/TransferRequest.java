package com.linsir.abc.mysql.chapter01.transaction.dto;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 转账请求DTO
 *
 * <p>用于接收转账请求的参数</p>
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
public class TransferRequest {

    /**
     * 转出账户编号
     * <p>不能为空</p>
     */
    @NotBlank(message = "转出账户不能为空")
    @Size(max = 32, message = "转出账户编号长度不能超过32位")
    private String fromAccountNo;

    /**
     * 转入账户编号
     * <p>不能为空</p>
     */
    @NotBlank(message = "转入账户不能为空")
    @Size(max = 32, message = "转入账户编号长度不能超过32位")
    private String toAccountNo;

    /**
     * 转账金额
     * <p>必须大于0</p>
     */
    @NotNull(message = "转账金额不能为空")
    @DecimalMin(value = "0.01", message = "转账金额必须大于0")
    private BigDecimal amount;

    /**
     * 备注
     * <p>可选，用于记录转账说明</p>
     */
    @Size(max = 256, message = "备注长度不能超过256位")
    private String remark;

    /**
     * 验证转出账户和转入账户是否相同
     *
     * @return true-有效，false-无效（相同账户）
     */
    public boolean isValid() {
        if (fromAccountNo == null || toAccountNo == null) {
            return false;
        }
        return !fromAccountNo.equals(toAccountNo);
    }

    /**
     * 获取验证错误信息
     *
     * @return 错误信息，如果验证通过返回null
     */
    public String getValidationError() {
        if (fromAccountNo == null || fromAccountNo.isBlank()) {
            return "转出账户不能为空";
        }
        if (toAccountNo == null || toAccountNo.isBlank()) {
            return "转入账户不能为空";
        }
        if (fromAccountNo.equals(toAccountNo)) {
            return "转出账户和转入账户不能相同";
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "转账金额必须大于0";
        }
        return null;
    }
}
