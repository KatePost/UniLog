package com.AK.unilog.service;

import com.AK.unilog.entity.CartItem;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import com.AK.unilog.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository){
        this.cartItemRepository = cartItemRepository;
    }

    public CartItem verifyCartItem(Section section, User user) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findBySectionAndStudent(section, user);
        if(cartItemOptional.isPresent()){
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setSection(section);
        cartItem.setStudent(user);
        return cartItemRepository.save(cartItem);
    }
}
