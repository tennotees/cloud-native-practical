package com.ezgroceries.shoppinglist.cocktailapi.service;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import java.util.Set;


public interface CocktailService {

    public Set<CocktailResource> searchCocktailsNameContaining(String search);

}
