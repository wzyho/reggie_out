package com.wzy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzy.reggie.dto.DishDto;
import com.wzy.reggie.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品、同时插入菜品对应的口味数据、需要操作俩张表:dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息、和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息、同时更新菜品对应的口味信息
    public void updateWithFlor(DishDto dishDto);

    //批量删除菜品
    public void bulkDeletion(List<Long> ids);
}
