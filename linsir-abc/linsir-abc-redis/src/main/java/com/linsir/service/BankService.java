package com.linsir.service;

import com.linsir.entity.Bank;

public interface BankService {

   Bank findByGoodsId(int goodsId);

   void reduce(int goodsId);
}
