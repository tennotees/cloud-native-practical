package com.ezgroceries.shoppinglist.shoppinglistapi.db;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;

@Resource
public class ShoppingListResource {

    private UUID shoppingListId;
    private String name;
    private List<UUID> cocktailIds;

    public ShoppingListResource(String name) {
        this.shoppingListId = UUID.fromString("27912cbc-165e-4182-8491-23afb0e29919");
        this.name = name;
        this.cocktailIds = new ArrayList<>();
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

    public List<UUID> getCocktails() {
        return cocktailIds;
    }

    public void setCocktails(List<UUID> cocktails) {
        this.cocktailIds = cocktails;
    }

    public void addCocktail(String cocktailId) {
        this.cocktailIds.add(UUID.fromString(cocktailId));
    }

}
