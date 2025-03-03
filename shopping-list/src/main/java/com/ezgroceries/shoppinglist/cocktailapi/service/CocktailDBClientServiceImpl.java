package com.ezgroceries.shoppinglist.cocktailapi.service;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBClient;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CocktailDBClientServiceImpl implements CocktailService {

    public final CocktailDBClient cocktailDBClient;

    @Autowired
    public CocktailDBClientServiceImpl(CocktailDBClient cocktailDBClient) {
        this.cocktailDBClient = cocktailDBClient;
    }

    @Override
    public Set<CocktailResource> searchCocktailsNameContaining(String search) {

        CocktailDBResponse response = cocktailDBClient.searchCocktails(search);

        return response.getDrinks().stream()
                .map(drink -> {
                    Set<String> ingredients = new HashSet<>(Arrays.asList(drink.getStrIngredient1(), drink.getStrIngredient2(),
                            drink.getStrIngredient3(),drink.getStrIngredient4(), drink.getStrIngredient5(),
                            drink.getStrIngredient6(),drink.getStrIngredient7()));
                    ingredients.removeIf(Strings::isNullOrEmpty);
                    return new CocktailResource(UUID.randomUUID() ,drink.getStrDrink(), drink.getStrGlass(),
                            drink.getStrInstructions(), drink.getStrDrinkThumb(), ingredients);
                }).collect(HashSet::new, HashSet::add, HashSet::addAll);
    }
}
