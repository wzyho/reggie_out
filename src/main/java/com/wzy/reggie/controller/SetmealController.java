package com.wzy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzy.reggie.common.R;
import com.wzy.reggie.dto.SetmealDto;
import com.wzy.reggie.pojo.Category;
import com.wzy.reggie.pojo.Setmeal;
import com.wzy.reggie.service.CategoryService;
import com.wzy.reggie.service.SetmealDishService;
import com.wzy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理页面
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     *新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<Setmeal> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success(setmealDto);
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> pages = new Page<>(page,pageSize);
        //套餐分类展示不出来、在setmealDto里面
        Page<SetmealDto> dtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.like(name != null,Setmeal::getName,name);
        //根据时间进行排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pages,queryWrapper);
        //做一个对象的cp
        BeanUtils.copyProperties(pages,dtoPage,"records");
        List<Setmeal> records = pages.getRecords();

        List<SetmealDto> list = records.stream().map((item) ->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category byId = categoryService.getById(categoryId);
            if (byId != null){
                //分类名称
                String name1 = byId.getName();
                setmealDto.setCategoryName(name1);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐功能
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 修改套餐信息、和对应的菜品信息
     * @param id
     */

    @GetMapping("/{id}")
    public R<SetmealDto> getByIdWithDish(@PathVariable("id") Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> updateWithDish(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 更新起售
     * @param ids
     * @return
     */

    @PostMapping("/status/1")
    public R<String> qiShou(Long ids){
        Setmeal setmeal = setmealService.getById(ids);
        setmeal.setStatus(1);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getId,ids);
        setmealService.update(setmeal,queryWrapper);
        return R.success("更新成功");

    }
    /**
     * 更新停售
     * @param ids
     * @return
     */

    @PostMapping("/status/0")
    public R<String> tingShou(Long ids){
        Setmeal setmeal = setmealService.getById(ids);
        setmeal.setStatus(0);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getId,ids);
        setmealService.update(setmeal,queryWrapper);
        return R.success("更新成功");

    }
    @GetMapping("/list")
    public R<List<Setmeal>> list(@RequestBody Setmeal setmeal) {
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, 1);
        //排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }
}
