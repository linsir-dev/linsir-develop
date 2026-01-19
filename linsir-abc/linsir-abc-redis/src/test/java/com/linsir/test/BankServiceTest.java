package com.linsir.test;


import com.linsir.entity.Bank;
import com.linsir.service.BankService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BankServiceTest {

    @Autowired
    private BankService bankService;


    @Test
    public void findByGoodsIdTest() {
      Bank bank = bankService.findByGoodsId(1);
      System.out.println(bank);
    }

    @Test
    public void reduceTest()
    {
        bankService.reduce(1);
    }

}
