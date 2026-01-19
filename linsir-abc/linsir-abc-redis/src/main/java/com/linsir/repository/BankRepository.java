package com.linsir.repository;

import com.linsir.entity.Bank;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface BankRepository extends JpaRepository<Bank,Integer> {

    Bank findByGoodsId(int goodsId);


    @Modifying
    @Query(nativeQuery = true, value = "UPDATE bank b set b.goods_num = b.goods_num - 1 where b.goods_id = ?1")
    void reduce(int goodsId);
}
