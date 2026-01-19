package com.linsir.controller;

import com.linsir.components.Listener;
import com.linsir.entity.GoodsOrder;
import com.linsir.service.GoodsOrderService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/goods/")
public class GoodsOrderController {

    @Autowired
    private  GoodsOrderService goodsOrderService;

    @Autowired
    private RedissonClient redissonClient;

    private final static Logger logger = LoggerFactory.getLogger(GoodsOrderController.class);


    @GetMapping("order/{goodsId}")
    public String order(@PathVariable("goodsId") int goodsId) throws InterruptedException {

        String  flag = "业务失败";

        RLock lock = redissonClient.getLock("lock");
        //加锁，参数：获取锁的最大等待时间（期间会重试），锁自动释放时间，时间单位
        //注意：如果指定锁自动释放时间，不管业务有没有执行完，锁都不会自动延期，即没有 watch dog 机制。
        boolean isLock = lock.tryLock(10, 25, TimeUnit.SECONDS);
       try {
           SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           if (isLock)
           {
               logger.info("线程{}在{}获取分布式锁成功", Thread.currentThread().getId(), format.format(System.currentTimeMillis()));
               Thread.sleep(10000);
               /*业务内容*/
               GoodsOrder goodsOrder = new GoodsOrder();
               goodsOrder.setGetTime(new Date());
               goodsOrder.setGoodsId(goodsId);
               goodsOrderService.orderGoods(goodsOrder);
               flag = "业务成功";
               logger.info("线程{}在{}业务完成", Thread.currentThread().getId(), format.format(System.currentTimeMillis()));
           }else
           {
               logger.info("线程{}在{}获取分布式锁失败", Thread.currentThread().getId(), format.format(System.currentTimeMillis()));
           }
       }catch (Exception e){
           e.printStackTrace();
           throw new RuntimeException("业务异常");
       }finally {
           if (lock.isHeldByCurrentThread() && lock.isLocked()) {
               //释放锁
               logger.info("线程{}解锁", Thread.currentThread().getId());
               lock.unlock();
           }
       }
       return flag;
    }


    @RequestMapping("reentrant")
    public void reentrant1() throws InterruptedException {
        //获取锁
        RLock lock = redissonClient.getLock("reentrant");
        //加锁，参数：获取锁的最大等待时间（期间会重试），锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(10, 25, TimeUnit.SECONDS);
        try {
            if (isLock) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(format.format(System.currentTimeMillis()) + "获取分布式锁1成功");
                Thread.sleep(15000);
                //调用方法2
                reentrant2();
                System.out.println(format.format(System.currentTimeMillis()) + "业务1完成");
            }
        } catch (Exception e) {
            throw new RuntimeException("业务异常");
        } finally {
            //当前线程未解锁
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                //释放锁
                System.out.println("分布式锁1解锁");
                lock.unlock();
            }
        }
    }


    public void reentrant2() throws InterruptedException {
        //获取锁
        RLock lock = redissonClient.getLock("reentrant");
        //加锁，参数：获取锁的最大等待时间（期间会重试），锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(5, 25, TimeUnit.SECONDS);
        try {
            if (isLock) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(format.format(System.currentTimeMillis()) + "获取分布式锁2成功");
                Thread.sleep(10000);
                System.out.println(format.format(System.currentTimeMillis()) + "业务2完成");
            }
        } catch (Exception e) {
            throw new RuntimeException("业务异常");
        } finally {
            //当前线程未解锁
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                //释放锁
                System.out.println("分布式锁2解锁");
                lock.unlock();
            }
        }
    }
}
