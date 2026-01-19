package com.linsir.service;

import com.linsir.entity.GoodsOrder;

public interface GoodsOrderService {

   GoodsOrder save(GoodsOrder order);

   GoodsOrder orderGoods(GoodsOrder order);
}
