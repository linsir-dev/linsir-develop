package com.linsir.service.impl;

import com.linsir.entity.Bank;
import com.linsir.repository.BankRepository;
import com.linsir.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    @Override
    public Bank findByGoodsId(int goodsId) {
        return bankRepository.findByGoodsId(goodsId);
    }

    @Override
    public void reduce(int goodsId) {
        bankRepository.reduce(goodsId);
    }
}
