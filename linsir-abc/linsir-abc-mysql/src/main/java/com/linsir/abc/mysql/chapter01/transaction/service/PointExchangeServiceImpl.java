package com.linsir.abc.mysql.chapter01.transaction.service;

import com.linsir.abc.mysql.chapter01.transaction.dto.ExchangeRequest;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.entity.*;
import com.linsir.abc.mysql.chapter01.transaction.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

/**
 * 积分兑换服务实现类
 *
 * <p>实现积分兑换业务逻辑，演示事务的原子性和持久性</p>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointExchangeServiceImpl implements PointExchangeService {

    private final PointAccountMapper pointAccountMapper;
    private final PointTransactionLogMapper pointTransactionLogMapper;
    private final ExchangeProductMapper exchangeProductMapper;
    private final ProductInventoryMapper productInventoryMapper;
    private final PointExchangeRecordMapper pointExchangeRecordMapper;

    /**
     * 执行积分兑换
     *
     * <p>事务流程：</p>
     * <ol>
     *   <li>校验参数和商品信息</li>
     *   <li>锁定库存</li>
     *   <li>冻结积分</li>
     *   <li>创建兑换记录</li>
     *   <li>扣减库存（从锁定库存中扣减）</li>
     *   <li>扣减积分（从冻结积分中扣减）</li>
     *   <li>记录积分流水</li>
     *   <li>更新兑换记录状态</li>
     * </ol>
     *
     * @param request 兑换请求
     * @return 事务结果
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public TransactionResult exchange(ExchangeRequest request) {
        // 参数校验
        String validationError = request.getValidationError();
        if (validationError != null) {
            return TransactionResult.fail(validationError);
        }

        String exchangeNo = generateExchangeNo();
        log.info("开始积分兑换，单号：{}，用户：{}，商品：{}，数量：{}",
                exchangeNo, request.getUserId(), request.getProductId(), request.getQuantity());

        try {
            // 1. 查询积分账户
            PointAccount pointAccount = pointAccountMapper.selectByUserId(request.getUserId());
            if (pointAccount == null) {
                return TransactionResult.fail("积分账户不存在");
            }

            // 2. 查询商品信息
            ExchangeProduct product = exchangeProductMapper.selectById(request.getProductId());
            if (product == null) {
                return TransactionResult.fail("商品不存在");
            }
            if (!product.isAvailable()) {
                return TransactionResult.fail("商品已下架");
            }

            // 3. 计算所需积分
            long totalPoints = request.calculateTotalPoints(product.getRequiredPoints());

            // 4. 检查积分是否充足
            if (!pointAccount.hasSufficientPoints(totalPoints)) {
                return TransactionResult.fail("积分不足，当前可用积分：" + pointAccount.getAvailablePoints());
            }

            // 5. 查询并锁定库存
            ProductInventory inventory = productInventoryMapper.selectByProductIdForUpdate(request.getProductId());
            if (inventory == null) {
                return TransactionResult.fail("商品库存信息不存在");
            }
            if (!inventory.hasSufficientStock(request.getQuantity())) {
                return TransactionResult.fail("库存不足，当前可用库存：" + inventory.getAvailableStock());
            }

            // 6. 锁定库存
            int affected = productInventoryMapper.lockStock(
                    inventory.getId(),
                    request.getQuantity(),
                    inventory.getVersion()
            );
            if (affected == 0) {
                throw new ConcurrentModificationException("库存已被修改，请重试");
            }

            // 7. 冻结积分
            affected = pointAccountMapper.freezePoints(
                    pointAccount.getId(),
                    totalPoints,
                    pointAccount.getVersion()
            );
            if (affected == 0) {
                throw new ConcurrentModificationException("积分账户已被修改，请重试");
            }

            // 8. 创建兑换记录
            PointExchangeRecord record = new PointExchangeRecord();
            record.setExchangeNo(exchangeNo);
            record.setPointAccountId(pointAccount.getId());
            record.setProductId(product.getId());
            record.setQuantity(request.getQuantity());
            record.setTotalPoints(totalPoints);
            record.setStatus(PointExchangeRecord.STATUS_PROCESSING);
            record.setCreatedAt(LocalDateTime.now());
            pointExchangeRecordMapper.insert(record);

            // 9. 扣减锁定库存（实际出库）
            affected = productInventoryMapper.deductLockedStock(
                    inventory.getId(),
                    request.getQuantity(),
                    inventory.getVersion() + 1  // 版本号已增加
            );
            if (affected == 0) {
                throw new ConcurrentModificationException("锁定库存扣减失败");
            }

            // 10. 扣减冻结积分（实际消费）
            // 不解冻，直接扣减冻结积分，同时更新累计消费
            affected = pointAccountMapper.deductFrozenPoints(
                    pointAccount.getId(),
                    totalPoints,
                    pointAccount.getVersion() + 1  // 版本号已增加
            );
            if (affected == 0) {
                throw new ConcurrentModificationException("积分扣减失败");
            }

            // 11. 记录积分消费流水
            // 扣减冻结积分后，可用积分不变，冻结积分减少
            // 需要重新查询获取最新状态
            PointAccount finalAccount = pointAccountMapper.selectById(pointAccount.getId());
            PointTransactionLog transactionLog = createPointTransactionLog(
                    finalAccount.getId(),
                    PointTransactionLog.TYPE_CONSUME,
                    -totalPoints,
                    finalAccount.getAvailablePoints(),  // 兑换前可用积分
                    finalAccount.getAvailablePoints(),  // 兑换后可用积分（不变）
                    PointTransactionLog.SOURCE_EXCHANGE,
                    record.getId(),
                    "兑换商品：" + product.getProductName()
            );
            pointTransactionLogMapper.insert(transactionLog);

            // 12. 更新兑换记录状态为成功
            pointExchangeRecordMapper.updateStatus(exchangeNo, PointExchangeRecord.STATUS_SUCCESS);

            log.info("积分兑换成功，单号：{}，消耗积分：{}", exchangeNo, totalPoints);
            return TransactionResult.success(exchangeNo, "兑换成功，消耗积分：" + totalPoints);

        } catch (Exception e) {
            log.error("积分兑换失败，单号：{}，错误：{}", exchangeNo, e.getMessage());
            // 更新兑换记录状态为失败
            pointExchangeRecordMapper.updateStatus(exchangeNo, PointExchangeRecord.STATUS_FAILED);
            throw e; // 抛出异常触发事务回滚
        }
    }

    /**
     * 取消兑换
     *
     * <p>回滚积分和库存</p>
     *
     * @param exchangeNo 兑换单号
     * @return 事务结果
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public TransactionResult cancelExchange(String exchangeNo) {
        log.info("开始取消兑换，单号：{}", exchangeNo);

        // 1. 查询兑换记录
        PointExchangeRecord record = pointExchangeRecordMapper.selectByExchangeNo(exchangeNo);
        if (record == null) {
            return TransactionResult.fail("兑换记录不存在");
        }
        // 允许取消处理中或成功的兑换
        if (record.isFailed() || record.isCancelled()) {
            return TransactionResult.fail("已失败或已取消的兑换无法再次取消");
        }

        try {
            // 2. 查询积分账户
            PointAccount pointAccount = pointAccountMapper.selectByIdForUpdate(record.getPointAccountId());
            if (pointAccount == null) {
                return TransactionResult.fail("积分账户不存在");
            }

            // 3. 查询库存
            ProductInventory inventory = productInventoryMapper.selectByProductIdForUpdate(record.getProductId());
            if (inventory == null) {
                return TransactionResult.fail("商品库存不存在");
            }

            // 4. 回滚积分（返还可用积分，减少累计消费）
            // 兑换完成后，积分已被扣减，冻结积分为0
            // 取消时需要返还可用积分
            int affected = pointAccountMapper.returnPoints(
                    pointAccount.getId(),
                    record.getTotalPoints(),
                    pointAccount.getVersion()
            );
            if (affected == 0) {
                throw new ConcurrentModificationException("积分账户已被修改");
            }

            // 重新查询获取最新版本号
            pointAccount = pointAccountMapper.selectById(pointAccount.getId());

            // 5. 回滚库存（返还可用库存）
            // 兑换完成后，锁定库存已被扣减为0
            // 取消时需要增加可用库存
            affected = productInventoryMapper.returnStock(
                    inventory.getId(),
                    record.getQuantity(),
                    inventory.getVersion()
            );
            if (affected == 0) {
                throw new ConcurrentModificationException("库存已被修改");
            }

            // 6. 记录积分返还流水
            // 解冻后，可用积分已经增加，需要计算解冻前的余额
            PointTransactionLog transactionLog = createPointTransactionLog(
                    pointAccount.getId(),
                    PointTransactionLog.TYPE_EARN,
                    record.getTotalPoints(),
                    pointAccount.getAvailablePoints() - record.getTotalPoints(),
                    pointAccount.getAvailablePoints(),
                    PointTransactionLog.SOURCE_EXCHANGE,
                    record.getId(),
                    "取消兑换返还积分"
            );
            pointTransactionLogMapper.insert(transactionLog);

            // 7. 更新兑换记录状态为已取消
            pointExchangeRecordMapper.updateStatus(exchangeNo, PointExchangeRecord.STATUS_CANCELLED);

            log.info("取消兑换成功，单号：{}，返还积分：{}", exchangeNo, record.getTotalPoints());
            return TransactionResult.success(exchangeNo, "取消成功，返还积分：" + record.getTotalPoints());

        } catch (Exception e) {
            log.error("取消兑换失败，单号：{}，错误：{}", exchangeNo, e.getMessage());
            throw e;
        }
    }

    @Override
    public PointAccount getPointAccount(Long userId) {
        return pointAccountMapper.selectByUserId(userId);
    }

    @Override
    public ExchangeProduct getProduct(Long productId) {
        return exchangeProductMapper.selectById(productId);
    }

    @Override
    public List<ExchangeProduct> getAllOnlineProducts() {
        return exchangeProductMapper.selectAllOnline();
    }

    @Override
    public List<PointExchangeRecord> getExchangeRecordsByUser(Long userId) {
        PointAccount account = pointAccountMapper.selectByUserId(userId);
        if (account == null) {
            return List.of();
        }
        return pointExchangeRecordMapper.selectByPointAccountId(account.getId());
    }

    @Override
    public PointExchangeRecord getExchangeRecord(String exchangeNo) {
        return pointExchangeRecordMapper.selectByExchangeNo(exchangeNo);
    }

    /**
     * 生成兑换单号
     *
     * @return 兑换单号
     */
    private String generateExchangeNo() {
        return "EXC" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    /**
     * 创建积分交易流水
     *
     * @param pointAccountId 积分账户ID
     * @param type           交易类型
     * @param points         积分数量
     * @param before         交易前积分
     * @param after          交易后积分
     * @param sourceType     来源类型
     * @param sourceId       来源ID
     * @param remark         备注
     * @return 积分交易流水
     */
    private PointTransactionLog createPointTransactionLog(Long pointAccountId, Byte type,
                                                           Long points, Long before, Long after,
                                                           String sourceType, Long sourceId, String remark) {
        PointTransactionLog log = new PointTransactionLog();
        log.setTransactionNo("PTX" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase());
        log.setPointAccountId(pointAccountId);
        log.setTransactionType(type);
        log.setPoints(points);
        log.setBalanceBefore(before);
        log.setBalanceAfter(after);
        log.setSourceType(sourceType);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }
}
