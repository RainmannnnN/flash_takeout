package com.yhy.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhy.takeout.entity.Orders;

public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);
}
