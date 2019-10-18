package com.ezgroceries.shoppinglist.shoppinglistapi;

import com.ezgroceries.shoppinglist.shoppinglistapi.db.ShoppingListIngredientsResource;
import com.ezgroceries.shoppinglist.shoppinglistapi.db.ShoppingListResource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
    public HttpEntity<List<ShoppingListIngredientsResource>> getAllShoppingLists() {
        List<ShoppingListIngredientsResource> shoppingListIngredientsResources = new ArrayList<>();

        ShoppingListIngredientsResource s;

        for (ShoppingListResource shoppingList : shoppingLists) {
            s = new ShoppingListIngredientsResource();
            s.setShoppingListId(shoppingList.getShoppingListId());
            s.setName(shoppingList.getName());
            for (UUID cocktailsInList : shoppingList.getCocktails()) {
                if (cocktailsInList.equals(UUID.fromString("23b3d85a-3928-41c0-a533-6538a71e17c4"))) {
                    s.setIngredients(new HashSet<>(Arrays.asList("Tequila", "Triple sec", "Lime juice", "Salt")));
                } else {
                    s.setIngredients(new HashSet<>(Arrays.asList("Tequila", "Blue Curacao", "Lime juice", "Salt")));
                }
            }
            shoppingListIngredientsResources.add(s);
        }

        return ResponseEntity.ok().body(shoppingListIngredientsResources);
    }

    @GetMapping(path = "/{listId}")
    public HttpEntity<ShoppingListIngredientsResource> getShoppingList(@PathVariable UUID listId) throws Exception {

        ShoppingListIngredientsResource shoppingListIngredients = new ShoppingListIngredientsResource();

        Optional<ShoppingListResource> shoppingList = shoppingLists.stream().
                filter(resource -> resource.getShoppingListId().equals(listId)).findFirst();

        if (shoppingList.isPresent()) {
            shoppingListIngredients.setShoppingListId(shoppingList.get().getShoppingListId());
            shoppingListIngredients.setName(shoppingList.get().getName());
            for (UUID cocktailsInList : shoppingList.get().getCocktails()) {
                if (cocktailsInList.equals(UUID.fromString("23b3d85a-3928-41c0-a533-6538a71e17c4"))) {
                    shoppingListIngredients.setIngredients(new HashSet<>(Arrays.asList("Tequila", "Triple sec", "Lime juice", "Salt")));
                } else {
                    shoppingListIngredients.setIngredients(new HashSet<>(Arrays.asList("Tequila", "Blue Curacao", "Lime juice", "Salt")));
                }
            }
        }
        return ResponseEntity.ok().body(shoppingListIngredients);
    }
}
