package com.linsir.test;

import com.linsir.entity.GoodsOrder;
import com.linsir.service.GoodsOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class GoodsOrderServiceTest {



    @Autowired
    private GoodsOrderService orderService;

    @Test
    public void saveTest() {
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsId(1);
        Date date = new Date();//获得系统时间.
        goodsOrder.setGetTime(date);
        orderService.save(goodsOrder);
    }

    @Test
    public void orderGoodsTest(){
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsId(1);
        Date date = new Date();//获得系统时间.
        goodsOrder.setGetTime(date);
        orderService.orderGoods(goodsOrder);
    }

}
