package com.ezgroceries.shoppinglist.shoppinglistapi.controller;

import com.ezgroceries.shoppinglist.cocktailapi.controller.CocktailNotFoundException;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailServiceImpl;
import com.ezgroceries.shoppinglist.shoppinglistapi.db.ShoppingListIngredientsResource;
import com.ezgroceries.shoppinglist.shoppinglistapi.db.ShoppingListResource;
import com.ezgroceries.shoppinglist.shoppinglistapi.service.ShoppingListService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private CocktailServiceImpl cocktailService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListResource createShoppingList(@RequestParam String name) {
        return shoppingListService.createNewShoppingList(name);
    }

    @PostMapping(path = "/{listId}/cocktails")
    public ResponseEntity<List<Map<String, String>>> addCocktailsToShoppingList(@PathVariable String listId, @RequestBody List<Map<String, String>> cocktailIds) {

        if (!shoppingListService.doesShoppingListExist(listId)) {
            throw new ListNotFoundException();
        };

        ShoppingListResource resultingList = shoppingListService.addCocktailsToShoppingList(listId,
            cocktailIds.stream().map(cocktail -> {
                String cocktailId = cocktail.get("cocktailId");
                if (!cocktailService.doesCocktailExist(cocktailId)) {
                    throw new CocktailNotFoundException();
                }
                return cocktailId;
            }).collect(Collectors.toSet())
        );

        List<Map<String, String>> responseAddedCocktails = resultingList.getCocktails().stream()
                .map(cocktailId -> {
                    Map<String, String> cocktailMap = new HashMap<>();
                    cocktailMap.put("cocktailId", cocktailId.toString());
                    return cocktailMap;
                }).collect(Collectors.toList());

        return ResponseEntity.ok().body(responseAddedCocktails);
    }

    @GetMapping
    public HttpEntity<List<ShoppingListIngredientsResource>> getAllShoppingLists() {
        List<ShoppingListIngredientsResource> shoppingListIngredientsResources = new ArrayList<>();

        Set<UUID> allShoppingListIds = shoppingListService.getAllShoppingLists();
        shoppingListIngredientsResources = allShoppingListIds.stream().map(listId -> getShoppingList(listId).getBody())
            .collect(Collectors.toList());

        return ResponseEntity.ok().body(shoppingListIngredientsResources);
    }

    @GetMapping(path = "/{listId}")
    public HttpEntity<ShoppingListIngredientsResource> getShoppingList(@PathVariable UUID listId) {

        ShoppingListIngredientsResource shoppingListIngredientsResource = new ShoppingListIngredientsResource();

        ShoppingListResource shoppingListWithCocktails = shoppingListService.getShoppingListCocktails(listId);

        List<CocktailResource> list = shoppingListWithCocktails.getCocktails().stream().map(cocktailId -> {
            CocktailResource cocktail = cocktailService.searchCocktailById(cocktailId.toString());
            return cocktail;
        }).collect(Collectors.toList());

        Set<String> shoppingListIngredients = list.stream().map(cocktail -> cocktail.getIngredients()).collect(HashSet::new, HashSet::addAll, HashSet::addAll);

        shoppingListIngredientsResource.setShoppingListId(shoppingListWithCocktails.getShoppingListId());
        shoppingListIngredientsResource.setName(shoppingListWithCocktails.getName());
        shoppingListIngredientsResource.setIngredients(shoppingListIngredients.stream().collect(Collectors.toSet()));

        return ResponseEntity.ok().body(shoppingListIngredientsResource);
    }
}
