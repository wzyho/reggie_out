package com.wzy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzy.reggie.common.R;
import com.wzy.reggie.dto.DishDto;
import com.wzy.reggie.mapper.CategoryMapper;
import com.wzy.reggie.pojo.Category;
import com.wzy.reggie.pojo.Dish;
import com.wzy.reggie.pojo.DishFlavor;
import com.wzy.reggie.pojo.Setmeal;
import com.wzy.reggie.service.CategoryService;
import com.wzy.reggie.service.DishFlavorService;
import com.wzy.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
    分类管理页面
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
     private DishService dishServicel;

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增分类
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        Long createUser = category.getCreateUser();
        Long updateUser = category.getUpdateUser();
        category.setCreateUser(createUser);
        category.setUpdateUser(updateUser);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page pages = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加一个排序条件、sort排序、使用升序orderbye
        queryWrapper.orderByDesc(Category::getSort);
        //进行分页查询
        categoryService.page(pages,queryWrapper);
        return R.success(pages);
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete( Long ids){
        categoryService.remove(ids);
        return R.success("根据id删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        String name = category.getName();
        Integer sort = category.getSort();
        Long id = category.getId();
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(id);
        categoryMapper.updateStatus(name,sort,id);

        return R.success("修改分类成功");
    }

    /**
     * 根据条件来查询分类
     * @param
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> get(Dish dish) {
        //条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据传进来的categoryId查询
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //只查询状态为1的菜品（在售菜品）
        queryWrapper.eq(Dish::getStatus, 1);
        //简单排下序，其实也没啥太大作用
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //获取查询到的结果作为返回值
        List<Dish> list = dishServicel.list(queryWrapper);
        //item就是list中的每一条数据，相当于遍历了
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            //创建一个dishDto对象
            DishDto dishDto = new DishDto();
            //将item的属性全都copy到dishDto里
            BeanUtils.copyProperties(item, dishDto);
            //由于dish表中没有categoryName属性，只存了categoryId
            Long categoryId = item.getCategoryId();
            //所以我们要根据categoryId查询对应的category
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //然后取出categoryName，赋值给dishDto
                dishDto.setCategoryName(category.getName());
            }
            //然后获取一下菜品id，根据菜品id去dishFlavor表中查询对应的口味，并赋值给dishDto
            Long itemId = item.getId();
            //条件构造器
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            //条件就是菜品id
            lambdaQueryWrapper.eq(itemId != null, DishFlavor::getDishId, itemId);
            //根据菜品id，查询到菜品口味
            List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
            //赋给dishDto的对应属性
            dishDto.setFlavors(flavors);
            //并将dishDto作为结果返回
            return dishDto;
            //将所有返回结果收集起来，封装成List
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }


}

