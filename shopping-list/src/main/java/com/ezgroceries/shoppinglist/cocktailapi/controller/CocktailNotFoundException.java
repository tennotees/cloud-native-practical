package com.ezgroceries.shoppinglist.cocktailapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Cocktail doesn't exist")
public class CocktailNotFoundException extends RuntimeException {

}
