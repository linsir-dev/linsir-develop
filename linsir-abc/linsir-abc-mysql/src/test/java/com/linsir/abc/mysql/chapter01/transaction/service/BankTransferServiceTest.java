package com.linsir.abc.mysql.chapter01.transaction.service;

import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransferRequest;
import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import com.linsir.abc.mysql.chapter01.transaction.entity.TransferRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 银行转账服务单元测试
 */
@SpringBootTest
@Transactional
class BankTransferServiceTest {

    @Autowired
    private BankTransferService bankTransferService;

    /**
     * 测试正常转账
     */
    @Test
    void testTransferSuccess() {
        // 准备测试数据
        TransferRequest request = new TransferRequest();
        request.setFromAccountNo("ACC001");
        request.setToAccountNo("ACC002");
        request.setAmount(new BigDecimal("100.00"));
        request.setRemark("单元测试转账");

        // 执行转账前查询余额
        BankAccount fromAccountBefore = bankTransferService.getAccount("ACC001");
        BigDecimal fromBalanceBefore = fromAccountBefore.getBalance();

        // 执行转账
        TransactionResult result = bankTransferService.transfer(request);

        // 验证结果
        assertTrue(result.isSuccess(), "转账应该成功");
        assertNotNull(result.getBusinessNo(), "应该生成转账单号");

        // 验证余额变化
        BankAccount fromAccountAfter = bankTransferService.getAccount("ACC001");
        assertEquals(fromBalanceBefore.subtract(new BigDecimal("100.00")),
                fromAccountAfter.getBalance(), "转出账户余额应该减少");

        // 验证转账记录
        TransferRecord record = bankTransferService.getTransferRecord(result.getBusinessNo());
        assertNotNull(record, "应该存在转账记录");
        assertEquals(TransferRecord.STATUS_SUCCESS, record.getStatus(), "转账状态应该为成功");
    }

    /**
     * 测试余额不足
     */
    @Test
    void testTransferInsufficientBalance() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNo("ACC001");
        request.setToAccountNo("ACC002");
        request.setAmount(new BigDecimal("999999.00")); // 超大金额

        TransactionResult result = bankTransferService.transfer(request);

        assertFalse(result.isSuccess(), "余额不足时转账应该失败");
        assertTrue(result.getMessage().contains("余额不足"), "错误消息应该提示余额不足");
    }

    /**
     * 测试账户不存在
     */
    @Test
    void testTransferAccountNotFound() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNo("NOT_EXIST");
        request.setToAccountNo("ACC002");
        request.setAmount(new BigDecimal("100.00"));

        TransactionResult result = bankTransferService.transfer(request);

        assertFalse(result.isSuccess(), "账户不存在时转账应该失败");
    }

    /**
     * 测试同一账户转账
     */
    @Test
    void testTransferSameAccount() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNo("ACC001");
        request.setToAccountNo("ACC001"); // 相同账户
        request.setAmount(new BigDecimal("100.00"));

        TransactionResult result = bankTransferService.transfer(request);

        assertFalse(result.isSuccess(), "同一账户转账应该失败");
    }

    /**
     * 测试批量转账
     */
    @Test
    void testBatchTransfer() {
        List<TransferRequest> requests = List.of(
            createTransferRequest("ACC001", "ACC002", new BigDecimal("50.00")),
            createTransferRequest("ACC002", "ACC003", new BigDecimal("50.00")),
            createTransferRequest("ACC003", "ACC001", new BigDecimal("50.00"))
        );

        List<TransactionResult> results = bankTransferService.batchTransfer(requests);

        assertEquals(3, results.size(), "应该返回3个结果");
        assertTrue(results.stream().allMatch(TransactionResult::isSuccess), "所有转账应该成功");
    }

    private TransferRequest createTransferRequest(String from, String to, BigDecimal amount) {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNo(from);
        request.setToAccountNo(to);
        request.setAmount(amount);
        return request;
    }
}
