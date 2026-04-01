package com.linsir.abc.mysql.chapter01.transaction.controller;

import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import com.linsir.abc.mysql.chapter01.transaction.service.IsolationDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 隔离级别演示Controller
 *
 * <p>用于演示不同事务隔离级别下的并发问题</p>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/transaction/isolation")
@RequiredArgsConstructor
public class IsolationDemoController {

    private final IsolationDemoService isolationDemoService;

    /**
     * 演示脏读
     *
     * <p>GET /api/transaction/isolation/dirty-read/{accountNo}</p>
     *
     * @param accountNo 账户编号
     * @return 读取结果
     */
    @GetMapping("/dirty-read/{accountNo}")
    public ResponseEntity<Map<String, Object>> demonstrateDirtyRead(@PathVariable String accountNo) {
        BigDecimal balance = isolationDemoService.demonstrateDirtyRead(accountNo);
        Map<String, Object> result = new HashMap<>();
        result.put("isolation", "READ_UNCOMMITTED");
        result.put("accountNo", accountNo);
        result.put("balance", balance);
        result.put("description", "在READ_UNCOMMITTED级别下，可能读取到其他事务未提交的数据（脏读）");
        return ResponseEntity.ok(result);
    }

    /**
     * 演示不可重复读
     *
     * <p>GET /api/transaction/isolation/non-repeatable-read/{accountNo}</p>
     *
     * @param accountNo 账户编号
     * @return 两次读取结果
     */
    @GetMapping("/non-repeatable-read/{accountNo}")
    public ResponseEntity<Map<String, Object>> demonstrateNonRepeatableRead(@PathVariable String accountNo) {
        try {
            BigDecimal[] balances = isolationDemoService.demonstrateNonRepeatableRead(accountNo);
            Map<String, Object> result = new HashMap<>();
            result.put("isolation", "READ_COMMITTED");
            result.put("accountNo", accountNo);
            result.put("firstRead", balances[0]);
            result.put("secondRead", balances[1]);
            result.put("isConsistent", balances[0].equals(balances[1]));
            result.put("description", "在READ_COMMITTED级别下，同一事务内两次读取可能不一致（不可重复读）");
            return ResponseEntity.ok(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 演示可重复读
     *
     * <p>GET /api/transaction/isolation/repeatable-read/{accountNo}</p>
     *
     * @param accountNo 账户编号
     * @return 两次读取结果
     */
    @GetMapping("/repeatable-read/{accountNo}")
    public ResponseEntity<Map<String, Object>> demonstrateRepeatableRead(@PathVariable String accountNo) {
        try {
            BigDecimal[] balances = isolationDemoService.demonstrateRepeatableRead(accountNo);
            Map<String, Object> result = new HashMap<>();
            result.put("isolation", "REPEATABLE_READ");
            result.put("accountNo", accountNo);
            result.put("firstRead", balances[0]);
            result.put("secondRead", balances[1]);
            result.put("isConsistent", balances[0].equals(balances[1]));
            result.put("description", "在REPEATABLE_READ级别下，同一事务内多次读取结果一致");
            return ResponseEntity.ok(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 演示幻读
     *
     * <p>GET /api/transaction/isolation/phantom-read</p>
     *
     * @return 两次查询结果
     */
    @GetMapping("/phantom-read")
    public ResponseEntity<Map<String, Object>> demonstratePhantomRead() {
        try {
            int[] counts = isolationDemoService.demonstratePhantomRead();
            Map<String, Object> result = new HashMap<>();
            result.put("isolation", "REPEATABLE_READ");
            result.put("firstQueryCount", counts[0]);
            result.put("secondQueryCount", counts[1]);
            result.put("isConsistent", counts[0] == counts[1]);
            result.put("description", "在MySQL的REPEATABLE_READ级别下，通过Next-Key Lock避免幻读");
            return ResponseEntity.ok(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 演示串行化
     *
     * <p>GET /api/transaction/isolation/serializable/{accountNo}</p>
     *
     * @param accountNo 账户编号
     * @return 账户信息
     */
    @GetMapping("/serializable/{accountNo}")
    public ResponseEntity<Map<String, Object>> demonstrateSerializable(@PathVariable String accountNo) {
        BankAccount account = isolationDemoService.demonstrateSerializable(accountNo);
        Map<String, Object> result = new HashMap<>();
        result.put("isolation", "SERIALIZABLE");
        result.put("accountNo", accountNo);
        result.put("balance", account != null ? account.getBalance() : null);
        result.put("description", "在SERIALIZABLE级别下，所有操作串行执行，完全避免并发问题");
        return ResponseEntity.ok(result);
    }

    /**
     * 模拟并发修改
     *
     * <p>POST /api/transaction/isolation/concurrent-update</p>
     *
     * @param accountNo 账户编号
     * @param amount    修改金额
     * @return 操作结果
     */
    @PostMapping("/concurrent-update")
    public ResponseEntity<String> simulateConcurrentUpdate(
            @RequestParam String accountNo,
            @RequestParam BigDecimal amount) {
        isolationDemoService.simulateConcurrentUpdate(accountNo, amount);
        return ResponseEntity.ok("并发修改任务已提交");
    }
}
