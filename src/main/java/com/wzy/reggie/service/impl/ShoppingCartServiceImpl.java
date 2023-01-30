package com.wzy.reggie.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzy.reggie.mapper.ShoppingCartMapper;
import com.wzy.reggie.pojo.ShoppingCart;
import com.wzy.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
