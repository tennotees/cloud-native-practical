package com.ezgroceries.shoppinglist.controller;

import com.ezgroceries.shoppinglist.resource.ShoppingListResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/shopping-lists", produces = "application/json")
public class ShoppingListController {

    private List<ShoppingListResource> shoppingLists = new ArrayList<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListResource createShoppingList(@RequestParam String name) {

        ShoppingListResource shoppingListResource = new ShoppingListResource(name);
        shoppingLists.add(shoppingListResource);
        return shoppingListResource;
    }

    @PostMapping(path = "/{listId}/cocktails")
    public ResponseEntity<List<Map<String, String>>> addCocktailsToShoppingList(@PathVariable String listId, @RequestBody List<Map<String, String>> cocktailIds) {

        ShoppingListResource shoppingList = shoppingLists.stream()
                .filter(item -> item.getShoppingListId().toString().equals(listId))
                .findFirst()
                .orElse(null);

        if (shoppingList != null) {
            cocktailIds.forEach(item -> shoppingList.addCocktail(item.get("cocktailId")));
            return ResponseEntity.ok().body(cocktailIds);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public HttpEntity<List<ShoppingListResource>> getAllShoppingLists() {
        return ResponseEntity.ok().body(shoppingLists);
    }

}
