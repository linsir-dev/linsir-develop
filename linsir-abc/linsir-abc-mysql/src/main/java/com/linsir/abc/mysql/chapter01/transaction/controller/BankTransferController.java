package com.linsir.abc.mysql.chapter01.transaction.controller;

import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransferRequest;
import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import com.linsir.abc.mysql.chapter01.transaction.entity.TransferRecord;
import com.linsir.abc.mysql.chapter01.transaction.service.BankTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 银行转账Controller
 *
 * <p>提供转账相关的RESTful API接口</p>
 *
 * @author linsir
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/transaction/bank")
@RequiredArgsConstructor
public class BankTransferController {

    private final BankTransferService bankTransferService;

    /**
     * 执行转账
     *
     * <p>POST /api/transaction/bank/transfer</p>
     *
     * @param request 转账请求
     * @return 转账结果
     */
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResult> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResult result = bankTransferService.transfer(request);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 跨行转账
     *
     * <p>POST /api/transaction/bank/cross-transfer</p>
     *
     * @param fromAccountNo 转出账户
     * @param toAccountNo   转入账户
     * @param amount        金额
     * @return 转账结果
     */
    @PostMapping("/cross-transfer")
    public ResponseEntity<TransactionResult> crossBankTransfer(
            @RequestParam String fromAccountNo,
            @RequestParam String toAccountNo,
            @RequestParam BigDecimal amount) {
        TransactionResult result = bankTransferService.crossBankTransfer(fromAccountNo, toAccountNo, amount);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 批量转账
     *
     * <p>POST /api/transaction/bank/batch-transfer</p>
     *
     * @param requests 转账请求列表
     * @return 转账结果列表
     */
    @PostMapping("/batch-transfer")
    public ResponseEntity<List<TransactionResult>> batchTransfer(@Valid @RequestBody List<TransferRequest> requests) {
        List<TransactionResult> results = bankTransferService.batchTransfer(requests);
        return ResponseEntity.ok(results);
    }

    /**
     * 查询账户信息
     *
     * <p>GET /api/transaction/bank/account/{accountNo}</p>
     *
     * @param accountNo 账户编号
     * @return 账户信息
     */
    @GetMapping("/account/{accountNo}")
    public ResponseEntity<BankAccount> getAccount(@PathVariable String accountNo) {
        BankAccount account = bankTransferService.getAccount(accountNo);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查询转账记录
     *
     * <p>GET /api/transaction/bank/transfer/{transferNo}</p>
     *
     * @param transferNo 转账单号
     * @return 转账记录
     */
    @GetMapping("/transfer/{transferNo}")
    public ResponseEntity<TransferRecord> getTransferRecord(@PathVariable String transferNo) {
        TransferRecord record = bankTransferService.getTransferRecord(transferNo);
        if (record != null) {
            return ResponseEntity.ok(record);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查询账户的转账记录
     *
     * <p>GET /api/transaction/bank/account/{accountNo}/transfers</p>
     *
     * @param accountNo 账户编号
     * @return 转账记录列表
     */
    @GetMapping("/account/{accountNo}/transfers")
    public ResponseEntity<List<TransferRecord>> getTransferRecordsByAccount(@PathVariable String accountNo) {
        List<TransferRecord> records = bankTransferService.getTransferRecordsByAccount(accountNo);
        return ResponseEntity.ok(records);
    }
}
