package com.linsir.abc.mysql.chapter01.transaction.mapper;

import com.linsir.abc.mysql.chapter01.transaction.entity.ExchangeProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 兑换商品Mapper接口
 * 
 * <p>提供兑换商品的CRUD操作</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface ExchangeProductMapper {
    
    /**
     * 根据ID查询兑换商品
     * 
     * @param id 商品ID
     * @return 兑换商品信息
     */
    @Select("SELECT * FROM exchange_products WHERE id = #{id}")
    ExchangeProduct selectById(Long id);
    
    /**
     * 根据商品编码查询
     * 
     * @param productCode 商品编码
     * @return 兑换商品信息
     */
    @Select("SELECT * FROM exchange_products WHERE product_code = #{productCode}")
    ExchangeProduct selectByProductCode(String productCode);
    
    /**
     * 查询所有上架的商品
     * 
     * @return 兑换商品列表
     */
    @Select("SELECT * FROM exchange_products WHERE status = 1 ORDER BY required_points")
    List<ExchangeProduct> selectAllOnline();
    
    /**
     * 根据状态查询商品
     * 
     * @param status 状态
     * @return 兑换商品列表
     */
    @Select("SELECT * FROM exchange_products WHERE status = #{status}")
    List<ExchangeProduct> selectByStatus(Integer status);
    
    /**
     * 插入兑换商品
     * 
     * @param product 兑换商品信息
     * @return 影响行数
     */
    @Insert("INSERT INTO exchange_products (product_code, product_name, description, " +
            "required_points, price, status) " +
            "VALUES (#{productCode}, #{productName}, #{description}, " +
            "#{requiredPoints}, #{price}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ExchangeProduct product);
    
    /**
     * 更新兑换商品
     * 
     * @param product 兑换商品信息
     * @return 影响行数
     */
    @Update("UPDATE exchange_products SET " +
            "product_name = #{productName}, " +
            "description = #{description}, " +
            "required_points = #{requiredPoints}, " +
            "price = #{price}, " +
            "status = #{status}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(ExchangeProduct product);
    
    /**
     * 更新商品状态
     * 
     * @param id 商品ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE exchange_products SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除兑换商品
     * 
     * @param id 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM exchange_products WHERE id = #{id}")
    int deleteById(Long id);
}
