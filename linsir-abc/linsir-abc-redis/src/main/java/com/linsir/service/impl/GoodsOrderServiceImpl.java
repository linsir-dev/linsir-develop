package com.linsir.service.impl;

import com.linsir.entity.Bank;
import com.linsir.entity.GoodsOrder;
import com.linsir.repository.GoodsOrderRepository;
import com.linsir.service.BankService;
import com.linsir.service.GoodsOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsOrderServiceImpl implements GoodsOrderService {

    @Autowired
    private GoodsOrderRepository goodsOrderRepository;


    @Autowired
    private BankService bankService;


    @Override
    public GoodsOrder save(GoodsOrder order) {
        return goodsOrderRepository.save(order);
    }

    @Transactional
    @Override
    public GoodsOrder orderGoods(GoodsOrder order) {
        GoodsOrder savedOrder = null;
        try {
           Bank bank = bankService.findByGoodsId(order.getGoodsId());
           // 有库存
           if (bank.getGoodsNum() > 0) {
              savedOrder = goodsOrderRepository.save(order);
               // 减库存
               bankService.reduce(order.getGoodsId());
           }
        }catch (Exception e){
            System.out.println("---- 订单失败");
            e.printStackTrace();
        }
        return savedOrder;
    }
}
