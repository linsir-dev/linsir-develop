package com.linsir.abc.mysql.chapter01.transaction.controller;

import com.linsir.abc.mysql.chapter01.transaction.dto.ExchangeRequest;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.entity.ExchangeProduct;
import com.linsir.abc.mysql.chapter01.transaction.entity.PointAccount;
import com.linsir.abc.mysql.chapter01.transaction.entity.PointExchangeRecord;
import com.linsir.abc.mysql.chapter01.transaction.service.PointExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 积分兑换Controller
 *
 * <p>提供积分兑换相关的RESTful API接口</p>
 *
 * @author linsir
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/transaction/point")
@RequiredArgsConstructor
public class PointExchangeController {

    private final PointExchangeService pointExchangeService;

    /**
     * 执行积分兑换
     *
     * <p>POST /api/transaction/point/exchange</p>
     *
     * @param request 兑换请求
     * @return 兑换结果
     */
    @PostMapping("/exchange")
    public ResponseEntity<TransactionResult> exchange(@Valid @RequestBody ExchangeRequest request) {
        TransactionResult result = pointExchangeService.exchange(request);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 取消兑换
     *
     * <p>POST /api/transaction/point/cancel/{exchangeNo}</p>
     *
     * @param exchangeNo 兑换单号
     * @return 取消结果
     */
    @PostMapping("/cancel/{exchangeNo}")
    public ResponseEntity<TransactionResult> cancelExchange(@PathVariable String exchangeNo) {
        TransactionResult result = pointExchangeService.cancelExchange(exchangeNo);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 查询积分账户
     *
     * <p>GET /api/transaction/point/account/{userId}</p>
     *
     * @param userId 用户ID
     * @return 积分账户
     */
    @GetMapping("/account/{userId}")
    public ResponseEntity<PointAccount> getPointAccount(@PathVariable Long userId) {
        PointAccount account = pointExchangeService.getPointAccount(userId);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查询商品详情
     *
     * <p>GET /api/transaction/point/product/{productId}</p>
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ExchangeProduct> getProduct(@PathVariable Long productId) {
        ExchangeProduct product = pointExchangeService.getProduct(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查询所有上架商品
     *
     * <p>GET /api/transaction/point/products</p>
     *
     * @return 商品列表
     */
    @GetMapping("/products")
    public ResponseEntity<List<ExchangeProduct>> getAllOnlineProducts() {
        List<ExchangeProduct> products = pointExchangeService.getAllOnlineProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 查询用户的兑换记录
     *
     * <p>GET /api/transaction/point/exchanges/{userId}</p>
     *
     * @param userId 用户ID
     * @return 兑换记录列表
     */
    @GetMapping("/exchanges/{userId}")
    public ResponseEntity<List<PointExchangeRecord>> getExchangeRecordsByUser(@PathVariable Long userId) {
        List<PointExchangeRecord> records = pointExchangeService.getExchangeRecordsByUser(userId);
        return ResponseEntity.ok(records);
    }

    /**
     * 查询兑换记录详情
     *
     * <p>GET /api/transaction/point/exchange/{exchangeNo}</p>
     *
     * @param exchangeNo 兑换单号
     * @return 兑换记录
     */
    @GetMapping("/exchange/{exchangeNo}")
    public ResponseEntity<PointExchangeRecord> getExchangeRecord(@PathVariable String exchangeNo) {
        PointExchangeRecord record = pointExchangeService.getExchangeRecord(exchangeNo);
        if (record != null) {
            return ResponseEntity.ok(record);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
