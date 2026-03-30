package com.linsir.abc.mysql.chapter01.architecture.mapper;

import com.linsir.abc.mysql.chapter01.architecture.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品数据访问接口
 * 对应数据库表：products
 *
 * 职责：
 * 1. 商品数据的CRUD操作
 * 2. 库存管理
 * 3. 商品查询
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface ProductMapper {

    /**
     * 根据ID查询商品
     *
     * @param id 商品ID
     * @return 商品对象
     */
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(Long id);

    /**
     * 根据商品编码查询
     *
     * @param productCode 商品编码
     * @return 商品对象
     */
    @Select("SELECT * FROM products WHERE product_code = #{productCode}")
    Product findByProductCode(String productCode);

    /**
     * 查询所有商品
     *
     * @return 商品列表
     */
    @Select("SELECT * FROM products ORDER BY id")
    List<Product> findAll();

    /**
     * 根据状态查询商品
     *
     * @param status 状态
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE status = #{status} ORDER BY id")
    List<Product> findByStatus(Integer status);

    /**
     * 根据分类查询商品
     *
     * @param categoryId 分类ID
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE category_id = #{categoryId} ORDER BY id")
    List<Product> findByCategoryId(Integer categoryId);

    /**
     * 插入商品
     *
     * @param product 商品对象
     * @return 影响行数
     */
    @Insert("INSERT INTO products (product_code, product_name, category_id, price, cost_price, stock, " +
            "status, description, created_at, updated_at) " +
            "VALUES (#{productCode}, #{productName}, #{categoryId}, #{price}, #{costPrice}, #{stock}, " +
            "#{status}, #{description}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    /**
     * 更新商品
     *
     * @param product 商品对象
     * @return 影响行数
     */
    @Update("UPDATE products SET product_code = #{productCode}, product_name = #{productName}, " +
            "category_id = #{categoryId}, price = #{price}, cost_price = #{costPrice}, stock = #{stock}, " +
            "status = #{status}, description = #{description}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Product product);

    /**
     * 更新库存
     *
     * @param productId 商品ID
     * @param stock     新库存
     * @return 影响行数
     */
    @Update("UPDATE products SET stock = #{stock} WHERE id = #{productId}")
    int updateStock(@Param("productId") Long productId, @Param("stock") Integer stock);

    /**
     * 扣减库存
     *
     * @param productId 商品ID
     * @param quantity  扣减数量
     * @return 影响行数
     */
    @Update("UPDATE products SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计商品数量
     *
     * @return 商品数量
     */
    @Select("SELECT COUNT(*) FROM products")
    long count();
}
