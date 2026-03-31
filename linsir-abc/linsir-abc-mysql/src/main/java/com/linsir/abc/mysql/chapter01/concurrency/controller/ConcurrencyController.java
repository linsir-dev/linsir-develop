package com.linsir.abc.mysql.chapter01.concurrency.controller;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Coupon;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Inventory;
import com.linsir.abc.mysql.chapter01.concurrency.entity.UserCoupon;
import com.linsir.abc.mysql.chapter01.concurrency.service.AccountService;
import com.linsir.abc.mysql.chapter01.concurrency.service.CouponService;
import com.linsir.abc.mysql.chapter01.concurrency.service.InventoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 并发控制演示控制器
 * 提供各种并发场景的REST API接口
 *
 * <p>演示场景：</p>
 * <ul>
 *   <li>转账：悲观锁 vs 乐观锁</li>
 *   <li>库存扣减：悲观锁 vs 乐观锁</li>
 *   <li>优惠券领取：悲观锁 vs 乐观锁</li>
 *   <li>资金冻结/解冻</li>
 *   <li>库存预占/释放</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/concurrency")
@RequiredArgsConstructor
public class ConcurrencyController {

    private final AccountService accountService;
    private final InventoryService inventoryService;
    private final CouponService couponService;

    // ==================== 账户相关接口 ====================

    /**
     * 创建账户
     *
     * @param request 账户信息
     * @return 创建的账户
     */
    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        try {
            Account account = Account.builder()
                    .accountNo(request.getAccountNo())
                    .accountName(request.getAccountName())
                    .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
                    .build();
            Account created = accountService.createAccount(account);
            return ResponseEntity.ok(Result.success(created));
        } catch (Exception e) {
            log.error("创建账户失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 查询账户
     *
     * @param id 账户ID
     * @return 账户信息
     */
    @GetMapping("/accounts/{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id") Long id) {
        try {
            Account account = accountService.getAccountById(id);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Result.success(account));
        } catch (Exception e) {
            log.error("查询账户失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 查询所有账户
     *
     * @return 账户列表
     */
    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts() {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            return ResponseEntity.ok(Result.success(accounts));
        } catch (Exception e) {
            log.error("查询账户列表失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 转账（悲观锁）
     *
     * @param request 转账请求
     * @return 转账结果
     */
    @PostMapping("/transfer/pessimistic")
    public ResponseEntity<?> transferWithPessimisticLock(@RequestBody TransferRequest request) {
        try {
            boolean success = accountService.transferWithPessimisticLock(
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    request.getAmount());
            return ResponseEntity.ok(Result.success("转账成功"));
        } catch (Exception e) {
            log.error("转账失败（悲观锁）", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 转账（乐观锁）
     *
     * @param request 转账请求
     * @return 转账结果
     */
    @PostMapping("/transfer/optimistic")
    public ResponseEntity<?> transferWithOptimisticLock(@RequestBody TransferRequest request) {
        try {
            boolean success = accountService.transferWithOptimisticLock(
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    request.getAmount());
            return ResponseEntity.ok(Result.success("转账成功"));
        } catch (Exception e) {
            log.error("转账失败（乐观锁）", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 充值（悲观锁）
     *
     * @param request 充值请求
     * @return 充值结果
     */
    @PostMapping("/recharge/pessimistic")
    public ResponseEntity<?> rechargeWithPessimisticLock(@RequestBody RechargeRequest request) {
        try {
            boolean success = accountService.rechargeWithPessimisticLock(
                    request.getAccountId(),
                    request.getAmount());
            return ResponseEntity.ok(Result.success("充值成功"));
        } catch (Exception e) {
            log.error("充值失败（悲观锁）", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 充值（乐观锁）
     *
     * @param request 充值请求
     * @return 充值结果
     */
    @PostMapping("/recharge/optimistic")
    public ResponseEntity<?> rechargeWithOptimisticLock(@RequestBody RechargeRequest request) {
        try {
            boolean success = accountService.rechargeWithOptimisticLock(
                    request.getAccountId(),
                    request.getAmount());
            return ResponseEntity.ok(Result.success("充值成功"));
        } catch (Exception e) {
            log.error("充值失败（乐观锁）", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 冻结金额
     *
     * @param request 冻结请求
     * @return 冻结结果
     */
    @PostMapping("/freeze")
    public ResponseEntity<?> freezeAmount(@RequestBody FreezeRequest request) {
        try {
            boolean success = accountService.freezeAmount(
                    request.getAccountId(),
                    request.getAmount());
            return ResponseEntity.ok(Result.success("冻结成功"));
        } catch (Exception e) {
            log.error("冻结失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 解冻金额
     *
     * @param request 解冻请求
     * @return 解冻结果
     */
    @PostMapping("/unfreeze")
    public ResponseEntity<?> unfreezeAmount(@RequestBody FreezeRequest request) {
        try {
            boolean success = accountService.unfreezeAmount(
                    request.getAccountId(),
                    request.getAmount());
            return ResponseEntity.ok(Result.success("解冻成功"));
        } catch (Exception e) {
            log.error("解冻失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    // ==================== 库存相关接口 ====================

    /**
     * 创建库存
     *
     * @param request 库存信息
     * @return 创建的库存
     */
    @PostMapping("/inventory")
    public ResponseEntity<?> createInventory(@RequestBody CreateInventoryRequest request) {
        try {
            Inventory inventory = Inventory.builder()
                    .productId(request.getProductId())
                    .warehouseId(request.getWarehouseId() != null ? request.getWarehouseId() : 1)
                    .availableStock(request.getAvailableStock())
                    .build();
            Inventory created = inventoryService.createInventory(inventory);
            return ResponseEntity.ok(Result.success(created));
        } catch (Exception e) {
            log.error("创建库存失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 查询库存
     *
     * @param id 库存ID
     * @return 库存信息
     */
    @GetMapping("/inventory/{id}")
    public ResponseEntity<?> getInventory(@PathVariable("id") Long id) {
        try {
            Inventory inventory = inventoryService.getInventoryById(id);
            if (inventory == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Result.success(inventory));
        } catch (Exception e) {
            log.error("查询库存失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 查询所有库存
     *
     * @return 库存列表
     */
    @GetMapping("/inventory")
    public ResponseEntity<?> getAllInventory() {
        try {
            List<Inventory> inventory = inventoryService.getAllInventory();
            return ResponseEntity.ok(Result.success(inventory));
        } catch (Exception e) {
            log.error("查询库存列表失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 扣减库存（悲观锁）
     *
     * @param request 扣减请求
     * @return 扣减结果
     */
    @PostMapping("/inventory/deduct/pessimistic")
    public ResponseEntity<?> deductStockWithPessimisticLock(@RequestBody DeductStockRequest request) {
        try {
            boolean success = inventoryService.deductStockWithPessimisticLock(
                    request.getInventoryId(),
                    request.getQuantity());
            return ResponseEntity.ok(Result.success("扣减库存成功"));
        } catch (Exception e) {
            log.error("扣减库存失败（悲观锁）", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 扣减库存（乐观锁）
     *
     * @param request 扣减请求
     * @return 扣减结果
     */
    @PostMapping("/inventory/deduct/optimistic")
    public ResponseEntity<?> deductStockWithOptimisticLock(@RequestBody DeductStockRequest request) {
        try {
            boolean success = inventoryService.deductStockWithOptimisticLock(
                    request.getInventoryId(),
                    request.getQuantity());
            return ResponseEntity.ok(Result.success("扣减库存成功"));
        } catch (Exception e) {
            log.error("扣减库存失败（乐观锁）", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 锁定库存
     *
     * @param request 锁定请求
     * @return 锁定结果
     */
    @PostMapping("/inventory/lock")
    public ResponseEntity<?> lockStock(@RequestBody DeductStockRequest request) {
        try {
            boolean success = inventoryService.lockStock(
                    request.getInventoryId(),
                    request.getQuantity());
            return ResponseEntity.ok(Result.success("锁定库存成功"));
        } catch (Exception e) {
            log.error("锁定库存失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 释放库存
     *
     * @param request 释放请求
     * @return 释放结果
     */
    @PostMapping("/inventory/unlock")
    public ResponseEntity<?> unlockStock(@RequestBody DeductStockRequest request) {
        try {
            boolean success = inventoryService.unlockStock(
                    request.getInventoryId(),
                    request.getQuantity());
            return ResponseEntity.ok(Result.success("释放库存成功"));
        } catch (Exception e) {
            log.error("释放库存失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 确认扣减库存
     *
     * @param request 确认请求
     * @return 确认结果
     */
    @PostMapping("/inventory/confirm")
    public ResponseEntity<?> confirmDeduct(@RequestBody DeductStockRequest request) {
        try {
            boolean success = inventoryService.confirmDeduct(
                    request.getInventoryId(),
                    request.getQuantity());
            return ResponseEntity.ok(Result.success("确认扣减成功"));
        } catch (Exception e) {
            log.error("确认扣减失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    // ==================== 优惠券相关接口 ====================

    /**
     * 创建优惠券
     *
     * @param request 优惠券信息
     * @return 创建的优惠券
     */
    @PostMapping("/coupons")
    public ResponseEntity<?> createCoupon(@RequestBody CreateCouponRequest request) {
        try {
            Coupon coupon = Coupon.builder()
                    .couponCode(request.getCouponCode())
                    .couponName(request.getCouponName())
                    .totalQuantity(request.getTotalQuantity())
                    .discountAmount(request.getDiscountAmount())
                    .validStartTime(request.getValidStartTime())
                    .validEndTime(request.getValidEndTime())
                    .build();
            Coupon created = couponService.createCoupon(coupon);
            return ResponseEntity.ok(Result.success(created));
        } catch (Exception e) {
            log.error("创建优惠券失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 查询优惠券
     *
     * @param id 优惠券ID
     * @return 优惠券信息
     */
    @GetMapping("/coupons/{id}")
    public ResponseEntity<?> getCoupon(@PathVariable("id") Long id) {
        try {
            Coupon coupon = couponService.getCouponById(id);
            if (coupon == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Result.success(coupon));
        } catch (Exception e) {
            log.error("查询优惠券失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 查询所有优惠券
     *
     * @return 优惠券列表
     */
    @GetMapping("/coupons")
    public ResponseEntity<?> getAllCoupons() {
        try {
            List<Coupon> coupons = couponService.getAllCoupons();
            return ResponseEntity.ok(Result.success(coupons));
        } catch (Exception e) {
            log.error("查询优惠券列表失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 领取优惠券（悲观锁）
     *
     * @param request 领取请求
     * @return 领取结果
     */
    @PostMapping("/coupons/grab/pessimistic")
    public ResponseEntity<?> grabCouponWithPessimisticLock(@RequestBody GrabCouponRequest request) {
        try {
            boolean success = couponService.grabCouponWithPessimisticLock(
                    request.getUserId(),
                    request.getCouponId());
            return ResponseEntity.ok(Result.success("领取优惠券成功"));
        } catch (Exception e) {
            log.error("领取优惠券失败（悲观锁）", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 领取优惠券（乐观锁）
     *
     * @param request 领取请求
     * @return 领取结果
     */
    @PostMapping("/coupons/grab/optimistic")
    public ResponseEntity<?> grabCouponWithOptimisticLock(@RequestBody GrabCouponRequest request) {
        try {
            boolean success = couponService.grabCouponWithOptimisticLock(
                    request.getUserId(),
                    request.getCouponId());
            return ResponseEntity.ok(Result.success("领取优惠券成功"));
        } catch (Exception e) {
            log.error("领取优惠券失败（乐观锁）", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 查询用户的优惠券
     *
     * @param userId 用户ID
     * @return 用户优惠券列表
     */
    @GetMapping("/coupons/user/{userId}")
    public ResponseEntity<?> getUserCoupons(@PathVariable("userId") Long userId) {
        try {
            List<UserCoupon> coupons = couponService.getUserCoupons(userId);
            return ResponseEntity.ok(Result.success(coupons));
        } catch (Exception e) {
            log.error("查询用户优惠券失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 使用优惠券
     *
     * @param request 使用请求
     * @return 使用结果
     */
    @PostMapping("/coupons/use")
    public ResponseEntity<?> useCoupon(@RequestBody UseCouponRequest request) {
        try {
            boolean success = couponService.useCoupon(
                    request.getUserCouponId(),
                    request.getOrderId());
            return ResponseEntity.ok(Result.success("使用优惠券成功"));
        } catch (Exception e) {
            log.error("使用优惠券失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    // ==================== 请求DTO ====================

    @Data
    public static class CreateAccountRequest {
        private String accountNo;
        private String accountName;
        private BigDecimal balance;
    }

    @Data
    public static class TransferRequest {
        private Long fromAccountId;
        private Long toAccountId;
        private BigDecimal amount;
    }

    @Data
    public static class RechargeRequest {
        private Long accountId;
        private BigDecimal amount;
    }

    @Data
    public static class FreezeRequest {
        private Long accountId;
        private BigDecimal amount;
    }

    @Data
    public static class CreateInventoryRequest {
        private Long productId;
        private Integer warehouseId;
        private Integer availableStock;
    }

    @Data
    public static class DeductStockRequest {
        private Long inventoryId;
        private Integer quantity;
    }

    @Data
    public static class CreateCouponRequest {
        private String couponCode;
        private String couponName;
        private Integer totalQuantity;
        private BigDecimal discountAmount;
        private java.time.LocalDateTime validStartTime;
        private java.time.LocalDateTime validEndTime;
    }

    @Data
    public static class GrabCouponRequest {
        private Long userId;
        private Long couponId;
    }

    @Data
    public static class UseCouponRequest {
        private Long userCouponId;
        private Long orderId;
    }

    // ==================== 响应封装 ====================

    @Data
    public static class Result<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> Result<T> success(T data) {
            Result<T> result = new Result<>();
            result.setSuccess(true);
            result.setData(data);
            return result;
        }

        public static <T> Result<T> error(String message) {
            Result<T> result = new Result<>();
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
    }
}
