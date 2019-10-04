package com.ezgroceries.shoppinglist.cocktailapi.service;

import com.ezgroceries.shoppinglist.cocktailapi.CocktailResource;
import java.util.List;

public interface CocktailDBClientService {

    public List<CocktailResource> searchCocktailsNameContaining(String search);

}
