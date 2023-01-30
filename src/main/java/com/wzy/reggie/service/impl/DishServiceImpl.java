package com.wzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzy.reggie.common.CustomException;
import com.wzy.reggie.dto.DishDto;
import com.wzy.reggie.mapper.DishMapper;
import com.wzy.reggie.pojo.Dish;
import com.wzy.reggie.pojo.DishFlavor;
import com.wzy.reggie.service.DishFlavorService;
import com.wzy.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;
    /**
     * 新增菜品、同时保存对应的口味数据
     * @param dishDto
     */

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表Dish
        this.save(dishDto);
        Long id = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) ->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到dish_Flavor表中
        dishFlavorService.saveBatch(flavors);
    }

    //根据id查询菜品信息、和口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息、从dish表查
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息、从dishflovor表查
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 更新菜品信息、同时更新菜品对应的口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据-----dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据------dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
    /**
     * 批量删除菜品
     */
    @Override
    public void bulkDeletion(List<Long> ids) {
        //统计符合删除条件的对象、确定是停售状态下才能删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        //统计符合删除的菜品个数
        int count = dishService.count(queryWrapper);
        //不能删除、抛出异常
        if (count > 0 ){
            throw new CustomException("不符合删除条件");
        }
       dishService.remove(queryWrapper);
    }
}
