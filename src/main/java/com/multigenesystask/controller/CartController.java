package com.multigenesystask.controller;

import java.security.Principal;


import com.multigenesystask.exception.CartItemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.requests.AddItemRequest;
import com.multigenesystask.service.CartService;
import com.multigenesystask.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
@Slf4j
public class CartController {


    private CartService cartService;


    private UserService userService;

    @GetMapping
    public ResponseEntity<Cart> findUserCartHandler(@AuthenticationPrincipal UserDetails userDetails) throws UserException{
        log.info("GET cart request for user: {}", userDetails.getUsername());
        User user=userService.findUserByEmail(userDetails.getUsername());
        Cart cart=cartService.findUserCart(user.getId());

        return new ResponseEntity<>(cart,HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody AddItemRequest req,
                                                  @AuthenticationPrincipal UserDetails userDetails) throws UserException, ProductException, CartItemException {

        log.info("Add to cart request for user: {}", userDetails.getUsername());

        User user=userService.findUserByEmail(userDetails.getUsername());

        CartItem item = cartService.addCartItem(user.getId(), req);


        log.info("Item Added To Cart Successfully");

        return new ResponseEntity<>(item,HttpStatus.ACCEPTED);

    }

}
