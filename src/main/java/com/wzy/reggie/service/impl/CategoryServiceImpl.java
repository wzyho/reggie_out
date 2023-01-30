package com.wzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzy.reggie.common.CustomException;
import com.wzy.reggie.mapper.CategoryMapper;
import com.wzy.reggie.pojo.Category;
import com.wzy.reggie.pojo.Dish;
import com.wzy.reggie.pojo.Setmeal;
import com.wzy.reggie.service.CategoryService;
import com.wzy.reggie.service.DishService;
import com.wzy.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category>implements CategoryService {

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    /**
     * 根据id删除分类、删除之前需要进行判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
        //添加查询条件、根据id查询
        queryWrapper1.eq(Dish::getCategoryId,ids);
        int count1 = dishService.count(queryWrapper1);
        //查询当前分类是否关联了菜品、如果已经关联、抛出一个业务异常
        if (count1 > 0){
            throw new CustomException("当前分类关联了菜品、不能删除");
        }
        //查询当前分类是否关联了套餐、如果已经关联、抛出一个业务异常
        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealService.count(queryWrapper2);
        if (count2 > 0){
            throw new CustomException("当前分类关联了套餐、不能删除");
        }
        //正常删除分类
        super.removeById(ids);
    }
}
