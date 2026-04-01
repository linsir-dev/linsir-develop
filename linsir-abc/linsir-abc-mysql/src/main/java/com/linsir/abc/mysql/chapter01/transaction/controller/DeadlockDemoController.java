package com.linsir.abc.mysql.chapter01.transaction.controller;

import com.linsir.abc.mysql.chapter01.transaction.service.DeadlockDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 死锁演示Controller
 *
 * <p>用于演示死锁的产生和解决方案</p>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/transaction/deadlock")
@RequiredArgsConstructor
public class DeadlockDemoController {

    private final DeadlockDemoService deadlockDemoService;

    /**
     * 演示死锁
     *
     * <p>POST /api/transaction/deadlock/demonstrate</p>
     *
     * @param accountNo1 账户1编号
     * @param accountNo2 账户2编号
     * @return 演示结果
     */
    @PostMapping("/demonstrate")
    public ResponseEntity<Map<String, Object>> demonstrateDeadlock(
            @RequestParam String accountNo1,
            @RequestParam String accountNo2) {
        Map<String, Object> result = new HashMap<>();
        try {
            deadlockDemoService.demonstrateDeadlock(accountNo1, accountNo2);
            result.put("success", true);
            result.put("message", "死锁演示完成，请查看日志");
            result.put("description", "事务A锁定账户1后请求账户2，事务B锁定账户2后请求账户1，形成循环等待");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "死锁演示异常：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 安全转账（避免死锁）
     *
     * <p>POST /api/transaction/deadlock/safe-transfer</p>
     *
     * @param fromAccountNo 转出账户
     * @param toAccountNo   转入账户
     * @param amount        金额
     * @return 转账结果
     */
    @PostMapping("/safe-transfer")
    public ResponseEntity<Map<String, Object>> safeTransfer(
            @RequestParam String fromAccountNo,
            @RequestParam String toAccountNo,
            @RequestParam BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();
        try {
            deadlockDemoService.safeTransfer(fromAccountNo, toAccountNo, amount);
            result.put("success", true);
            result.put("message", "安全转账成功");
            result.put("description", "按固定顺序获取锁，避免死锁");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "转账失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 乐观锁转账（避免死锁）
     *
     * <p>POST /api/transaction/deadlock/optimistic-transfer</p>
     *
     * @param fromAccountNo 转出账户
     * @param toAccountNo   转入账户
     * @param amount        金额
     * @return 转账结果
     */
    @PostMapping("/optimistic-transfer")
    public ResponseEntity<Map<String, Object>> optimisticTransfer(
            @RequestParam String fromAccountNo,
            @RequestParam String toAccountNo,
            @RequestParam BigDecimal amount) {
        boolean success = deadlockDemoService.optimisticTransfer(fromAccountNo, toAccountNo, amount);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "乐观锁转账成功" : "乐观锁转账失败");
        result.put("description", "使用乐观锁（版本号）避免显式锁定，从而避免死锁");
        return ResponseEntity.ok(result);
    }
}
