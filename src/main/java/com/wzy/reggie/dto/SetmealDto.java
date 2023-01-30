package com.wzy.reggie.dto;
import com.wzy.reggie.pojo.Setmeal;
import com.wzy.reggie.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
