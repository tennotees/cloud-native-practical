package com.ezgroceries.shoppinglist.controller;

import com.ezgroceries.shoppinglist.resource.ShoppingList;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/shopping-lists", produces = "application/json")
public class ShoppingListController {

    @PostMapping
    public ShoppingList creteShoppingList(@RequestParam String name) {
        return new ShoppingList(name);
    }

}
