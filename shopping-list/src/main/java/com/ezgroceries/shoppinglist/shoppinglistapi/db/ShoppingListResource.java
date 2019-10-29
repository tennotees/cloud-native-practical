package com.ezgroceries.shoppinglist.shoppinglistapi.db;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Resource;

@Resource
public class ShoppingListResource {

    private UUID shoppingListId;
    private String name;
    private Set<UUID> cocktailIds;

    public ShoppingListResource() {}

    public ShoppingListResource(String name) {
        this.shoppingListId = UUID.randomUUID();
        this.name = name;
        this.cocktailIds = new HashSet<>();
    }

    public ShoppingListResource(UUID id, String name, Set<UUID> cocktailIds) {
        this.shoppingListId = id;
        this.name = name;
        this.cocktailIds = cocktailIds;
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

    public Set<UUID> getCocktails() {
        return cocktailIds;
    }

    public void setCocktails(Set<UUID> cocktails) {
        this.cocktailIds = cocktails;
    }

    public void addCocktail(String cocktailId) {
        this.cocktailIds.add(UUID.fromString(cocktailId));
    }

}
