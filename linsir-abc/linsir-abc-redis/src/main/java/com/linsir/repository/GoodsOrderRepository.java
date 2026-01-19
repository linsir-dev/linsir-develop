package com.linsir.repository;


import com.linsir.entity.GoodsOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsOrderRepository  extends CrudRepository<GoodsOrder, Integer> {
}
