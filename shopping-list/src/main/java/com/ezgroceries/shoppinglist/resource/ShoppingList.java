package com.ezgroceries.shoppinglist.resource;

import java.util.UUID;
import javax.annotation.Resource;

@Resource
public class ShoppingList {

    private UUID shoppingListId;
    private String name;

    public ShoppingList(String name) {
        this.shoppingListId = UUID.randomUUID();
        this.name = name;
    }

    public UUID getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(UUID shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
