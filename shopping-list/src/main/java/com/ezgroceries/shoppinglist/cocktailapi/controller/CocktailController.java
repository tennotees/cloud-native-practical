package com.ezgroceries.shoppinglist.cocktailapi.controller;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cocktails", produces = "application/json")
public class CocktailController {

    private Set<CocktailResource> cocktails = new HashSet<>();
    private final CocktailService CocktailService;

    @Autowired
    public CocktailController(@Qualifier("cocktailServiceImpl") CocktailService CocktailService) {
        this.CocktailService = CocktailService;
    }


    @GetMapping
    public Set<CocktailResource> get(@RequestParam String search) {
        return CocktailService.searchCocktailsNameContaining(search);
    }

    @GetMapping(path = "/init")
    public Set<CocktailResource> initDummies() {
        this.cocktails = getDummyResources();
        return cocktails;
    }

    private Set<CocktailResource> getDummyResources() {
        return Stream.of(
                new CocktailResource(
                        UUID.fromString("23b3d85a-3928-41c0-a533-6538a71e17c4"), "Margerita",
                        "Cocktail glass",
                        "Rub the rim of the glass with the lime slice to make the salt stick to it. Take care to moisten..",
                        "https://www.thecocktaildb.com/images/media/drink/wpxpvu1439905379.jpg",
                        new HashSet<>(Arrays.asList("Tequila", "Triple sec", "Lime juice", "Salt"))),
                new CocktailResource(
                        UUID.fromString("d615ec78-fe93-467b-8d26-5d26d8eab073"), "Blue Margerita",
                        "Cocktail glass",
                        "Rub rim of cocktail glass with lime juice. Dip rim in coarse salt..",
                        "https://www.thecocktaildb.com/images/media/drink/qtvvyq1439905913.jpg",
                        new HashSet<>(Arrays.asList("Tequila", "Blue Curacao", "Lime juice", "Salt")))
            ).collect(Collectors.toSet());
    }

}