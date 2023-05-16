package com.yhy.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yhy.takeout.common.Result;
import com.yhy.takeout.entity.AddressBook;
import com.yhy.takeout.service.AddressBookService;
import com.yhy.takeout.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result<AddressBook> add(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(ThreadLocalUtil.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, ThreadLocalUtil.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return Result.success(addressBook);
        } else {
            return Result.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    public Result<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, ThreadLocalUtil.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return Result.error("没有找到该对象");
        } else {
            return Result.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(ThreadLocalUtil.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return Result.success(addressBookService.list(queryWrapper));
    }

    /**
     * 更新地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook){
        log.info("保存用户地址！");

        addressBookService.updateById(addressBook);

        return Result.success("更新信息成功！");
    }


    /**
     * 删除用户地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("删除用户地址中....");

        addressBookService.removeById(ids);

        return Result.success("成功删除用户地址！");
    }


}
