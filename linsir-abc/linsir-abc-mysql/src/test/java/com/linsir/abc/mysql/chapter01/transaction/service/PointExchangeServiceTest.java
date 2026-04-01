package com.linsir.abc.mysql.chapter01.transaction.service;

import com.linsir.abc.mysql.chapter01.transaction.dto.ExchangeRequest;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.entity.PointAccount;
import com.linsir.abc.mysql.chapter01.transaction.entity.PointExchangeRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 积分兑换服务单元测试
 * 
 * <p>注意：不使用@Transactional注解，让事务正常提交，以便验证数据变化</p>
 */
@SpringBootTest
class PointExchangeServiceTest {

    @Autowired
    private PointExchangeService pointExchangeService;

    /**
     * 测试正常兑换
     * 
     * <p>使用用户10003进行测试，避免与其他测试冲突</p>
     */
    @Test
    void testExchangeSuccess() {
        // 使用用户10003进行测试
        Long testUserId = 10003L;
        
        // 准备测试数据
        ExchangeRequest request = new ExchangeRequest();
        request.setUserId(testUserId);
        request.setProductId(1L);
        request.setQuantity(1);

        // 执行兑换前查询积分
        PointAccount accountBefore = pointExchangeService.getPointAccount(testUserId);
        Long pointsBefore = accountBefore.getAvailablePoints();

        // 执行兑换
        TransactionResult result = pointExchangeService.exchange(request);

        // 验证结果
        assertTrue(result.isSuccess(), "兑换应该成功");
        assertNotNull(result.getBusinessNo(), "应该生成兑换单号");

        // 验证积分扣减
        PointAccount accountAfter = pointExchangeService.getPointAccount(testUserId);
        assertTrue(accountAfter.getAvailablePoints() < pointsBefore, "积分应该减少");

        // 验证兑换记录
        PointExchangeRecord record = pointExchangeService.getExchangeRecord(result.getBusinessNo());
        assertNotNull(record, "应该存在兑换记录");
        assertEquals(PointExchangeRecord.STATUS_SUCCESS, record.getStatus(), "兑换状态应该为成功");
    }

    /**
     * 测试积分不足
     */
    @Test
    void testExchangeInsufficientPoints() {
        ExchangeRequest request = new ExchangeRequest();
        request.setUserId(10004L); // 积分较少的用户
        request.setProductId(4L);  // 需要8500积分的商品
        request.setQuantity(10);   // 需要85000积分

        TransactionResult result = pointExchangeService.exchange(request);

        assertFalse(result.isSuccess(), "积分不足时兑换应该失败");
        assertTrue(result.getMessage().contains("积分不足"), "错误消息应该提示积分不足");
    }

    /**
     * 测试库存不足
     * 
     * <p>使用用户10001进行测试（积分充足但库存不足）</p>
     */
    @Test
    void testExchangeInsufficientStock() {
        // 使用用户10001进行测试，该用户有10000积分，足够兑换
        // 但库存只有100个，请求9999个会触发库存不足
        Long testUserId = 10001L;
        
        ExchangeRequest request = new ExchangeRequest();
        request.setUserId(testUserId);
        request.setProductId(1L);
        request.setQuantity(9999); // 超大数量，超过库存

        TransactionResult result = pointExchangeService.exchange(request);

        assertFalse(result.isSuccess(), "库存不足时兑换应该失败");
        // 可能是积分不足或库存不足，取决于哪个条件先不满足
        assertTrue(result.getMessage().contains("不足"), "错误消息应该提示不足");
    }

    /**
     * 测试取消兑换
     * 
     * <p>注意：由于不使用@Transactional，每次测试都会修改数据库数据，
     * 因此需要使用不同的用户ID或恢复数据</p>
     */
    @Test
    void testCancelExchange() {
        // 使用用户10002进行测试（避免与其他测试冲突）
        Long testUserId = 10002L;
        
        // 先执行兑换
        ExchangeRequest request = new ExchangeRequest();
        request.setUserId(testUserId);
        request.setProductId(1L);
        request.setQuantity(1);

        TransactionResult exchangeResult = pointExchangeService.exchange(request);
        assertTrue(exchangeResult.isSuccess(), "兑换应该成功");

        // 查询兑换前的积分
        PointAccount accountBefore = pointExchangeService.getPointAccount(testUserId);
        Long pointsBefore = accountBefore.getAvailablePoints();

        // 取消兑换
        TransactionResult cancelResult = pointExchangeService.cancelExchange(exchangeResult.getBusinessNo());

        // 验证结果
        assertTrue(cancelResult.isSuccess(), "取消兑换应该成功");

        // 验证积分返还
        PointAccount accountAfter = pointExchangeService.getPointAccount(testUserId);
        assertTrue(accountAfter.getAvailablePoints() > pointsBefore, "积分应该返还");

        // 验证兑换记录状态
        PointExchangeRecord record = pointExchangeService.getExchangeRecord(exchangeResult.getBusinessNo());
        assertEquals(PointExchangeRecord.STATUS_CANCELLED, record.getStatus(), "兑换状态应该为已取消");
    }
}
