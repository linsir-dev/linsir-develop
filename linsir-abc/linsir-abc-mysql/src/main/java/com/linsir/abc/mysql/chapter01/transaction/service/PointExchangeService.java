package com.linsir.abc.mysql.chapter01.transaction.service;

import com.linsir.abc.mysql.chapter01.transaction.dto.ExchangeRequest;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.entity.ExchangeProduct;
import com.linsir.abc.mysql.chapter01.transaction.entity.PointAccount;
import com.linsir.abc.mysql.chapter01.transaction.entity.PointExchangeRecord;

import java.util.List;

/**
 * 积分兑换服务接口
 *
 * <p>用于演示事务日志和持久性</p>
 *
 * @author linsir
 * @since 1.0.0
 */
public interface PointExchangeService {

    /**
     * 执行积分兑换
     * <p>演示事务的原子性和持久性</p>
     *
     * @param request 兑换请求
     * @return 事务结果
     */
    TransactionResult exchange(ExchangeRequest request);

    /**
     * 取消兑换
     * <p>回滚积分和库存</p>
     *
     * @param exchangeNo 兑换单号
     * @return 事务结果
     */
    TransactionResult cancelExchange(String exchangeNo);

    /**
     * 查询积分账户
     *
     * @param userId 用户ID
     * @return 积分账户
     */
    PointAccount getPointAccount(Long userId);

    /**
     * 查询兑换商品
     *
     * @param productId 商品ID
     * @return 兑换商品
     */
    ExchangeProduct getProduct(Long productId);

    /**
     * 查询所有上架商品
     *
     * @return 商品列表
     */
    List<ExchangeProduct> getAllOnlineProducts();

    /**
     * 查询用户的兑换记录
     *
     * @param userId 用户ID
     * @return 兑换记录列表
     */
    List<PointExchangeRecord> getExchangeRecordsByUser(Long userId);

    /**
     * 查询兑换记录
     *
     * @param exchangeNo 兑换单号
     * @return 兑换记录
     */
    PointExchangeRecord getExchangeRecord(String exchangeNo);
}
