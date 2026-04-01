package com.linsir.abc.mysql.chapter01.transaction.integration;

import com.linsir.abc.mysql.chapter01.transaction.dto.ExchangeRequest;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransferRequest;
import com.linsir.abc.mysql.chapter01.transaction.entity.*;
import com.linsir.abc.mysql.chapter01.transaction.service.BankTransferService;
import com.linsir.abc.mysql.chapter01.transaction.service.PointExchangeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 事务集成测试
 *
 * <p>测试完整的业务流程</p>
 */
@SpringBootTest
class TransactionIntegrationTest {

    @Autowired
    private BankTransferService bankTransferService;

    @Autowired
    private PointExchangeService pointExchangeService;

    /**
     * 测试完整业务流程：转账 + 积分兑换
     *
     * <p>模拟用户先转账，然后用积分兑换商品</p>
     */
    @Test
    void testCompleteBusinessFlow() {
        // 步骤1：执行转账
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountNo("ACC001");
        transferRequest.setToAccountNo("ACC002");
        transferRequest.setAmount(new BigDecimal("200.00"));
        transferRequest.setRemark("转账测试");

        TransactionResult transferResult = bankTransferService.transfer(transferRequest);
        assertTrue(transferResult.isSuccess(), "转账应该成功");

        // 验证转账记录
        TransferRecord transferRecord = bankTransferService.getTransferRecord(transferResult.getBusinessNo());
        assertNotNull(transferRecord);
        assertEquals(TransferRecord.STATUS_SUCCESS, transferRecord.getStatus());

        // 步骤2：执行积分兑换
        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setUserId(10001L);
        exchangeRequest.setProductId(1L);
        exchangeRequest.setQuantity(2);

        TransactionResult exchangeResult = pointExchangeService.exchange(exchangeRequest);
        assertTrue(exchangeResult.isSuccess(), "兑换应该成功");

        // 验证兑换记录
        PointExchangeRecord exchangeRecord = pointExchangeService.getExchangeRecord(exchangeResult.getBusinessNo());
        assertNotNull(exchangeRecord);
        assertEquals(PointExchangeRecord.STATUS_SUCCESS, exchangeRecord.getStatus());

        // 验证数据一致性
        PointAccount pointAccount = pointExchangeService.getPointAccount(10001L);
        assertTrue(pointAccount.getTotalEarned() >= pointAccount.getTotalConsumed(),
                "累计获得积分应该大于等于累计消费");
    }

    /**
     * 测试事务回滚
     *
     * <p>验证事务失败时数据回滚</p>
     */
    @Test
    void testTransactionRollback() {
        // 获取转账前余额
        BankAccount fromAccountBefore = bankTransferService.getAccount("ACC001");
        BigDecimal balanceBefore = fromAccountBefore.getBalance();

        // 执行一个会失败的转账（余额不足）
        TransferRequest request = new TransferRequest();
        request.setFromAccountNo("ACC001");
        request.setToAccountNo("ACC002");
        request.setAmount(new BigDecimal("999999.00"));

        TransactionResult result = bankTransferService.transfer(request);
        assertFalse(result.isSuccess(), "转账应该失败");

        // 验证余额未变化
        BankAccount fromAccountAfter = bankTransferService.getAccount("ACC001");
        assertEquals(balanceBefore, fromAccountAfter.getBalance(),
                "转账失败后余额应该不变");
    }
}
