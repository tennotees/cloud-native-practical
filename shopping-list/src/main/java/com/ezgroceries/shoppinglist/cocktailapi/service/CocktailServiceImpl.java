package com.ezgroceries.shoppinglist.cocktailapi.service;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBClient;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.entity.CocktailEntity;
import com.ezgroceries.shoppinglist.cocktailapi.repository.CocktailRepository;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CocktailServiceImpl implements CocktailService {

    private CocktailRepository cocktailRepository;
    private CocktailDBClient cocktailDBClient;

    @Autowired
    public CocktailServiceImpl(CocktailRepository cocktailRepository, CocktailDBClient cocktailDBClient) {
        this.cocktailRepository = cocktailRepository;
        this.cocktailDBClient = cocktailDBClient;
    }

    @Override
    public Set<CocktailResource> searchCocktailsNameContaining(String search) {

        //fetching cocktails from external cocktail DB
        CocktailDBResponse cocktailDBResult = cocktailDBClient.searchCocktails(search);

        //fetching cocktails from internal DB
        Map<String, CocktailEntity>  cocktailEntityMap = cocktailRepository.findByNameContaining(search).stream()
                .collect(Collectors.toMap(c -> c.iddrink, c -> c));

        //saving non-existing external cocktails in internal DB
        Set<CocktailEntity> cocktailNewEntities = cocktailDBResult.getDrinks().stream()
                .filter(drink -> !cocktailEntityMap.containsKey(drink.getIdDrink()))
                .map(drink -> {
                    Set<String> ingredients = new HashSet<>(Arrays.asList(drink.getStrIngredient1(), drink.getStrIngredient2(),
                            drink.getStrIngredient3(),drink.getStrIngredient4(), drink.getStrIngredient5(),
                            drink.getStrIngredient6(),drink.getStrIngredient7()));
                    ingredients.removeIf(Strings::isNullOrEmpty);
                    CocktailEntity cocktailEntity = new CocktailEntity(drink.getIdDrink(), drink.getStrDrink(), drink.getStrGlass(), drink.getStrInstructions(), drink.getStrDrinkThumb(), ingredients);
                    cocktailRepository.save(cocktailEntity);
                    return cocktailEntity;
                }).collect(HashSet::new, HashSet::add, HashSet::addAll);


        //merging & converting cocktails for presentation
        return mergingCocktails(cocktailEntityMap, cocktailNewEntities);
    }

    public Set<CocktailResource> mergingCocktails(Map<String, CocktailEntity> cocktailEntityMap, Set<CocktailEntity> cocktailNewEntities) {

        //merging cocktail of internal & external DB for presentation
        Set<CocktailEntity> allCocktailsSet = (cocktailEntityMap.values() == null ? new HashSet<>() :
                cocktailEntityMap.values().stream().collect(Collectors.toSet()));
        allCocktailsSet.addAll(cocktailNewEntities);

        //converting merged cocktails for presentation
        return allCocktailsSet.stream()
                .map(c -> new CocktailResource(c.id, c.name, c.ingredients))
                .collect(Collectors.toSet());
    }

    public CocktailResource searchCocktailById(String cocktailId) {
        return searchCocktailByName(cocktailRepository.findById(UUID.fromString(cocktailId)).get().name);
    }

    public CocktailResource searchCocktailByName(String name) {
        CocktailEntity cocktailEntity = cocktailRepository.findByName(name);
        return new CocktailResource(cocktailEntity.id, cocktailEntity.name, cocktailEntity.ingredients);
    }

    public boolean doesCocktailExist(String cocktailId) {
        return cocktailRepository.existsById(UUID.fromString(cocktailId));
    }

    public void emptyTable() {
        cocktailRepository.deleteAll();
    }
}
