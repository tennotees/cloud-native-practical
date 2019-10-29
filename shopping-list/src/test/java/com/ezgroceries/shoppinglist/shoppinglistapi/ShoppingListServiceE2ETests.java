package com.ezgroceries.shoppinglist.shoppinglistapi;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailServiceImpl;
import com.ezgroceries.shoppinglist.shoppinglistapi.db.ShoppingListResource;
import com.ezgroceries.shoppinglist.shoppinglistapi.service.ShoppingListService;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShoppingListServiceE2ETests {

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private CocktailServiceImpl cocktailService;

    @Before
    public void init() {

        shoppingListService.emptyShoppingListTables();
        //initializing cocktails table with list of cocktails
        cocktailService.emptyTable();
        cocktailService.searchCocktailsNameContaining("1");
    }

    @Test
    public void testCreateShoppingList_Success() {

        ShoppingListResource result = shoppingListService.createNewShoppingList("testList");

        Assert.assertEquals("testList", result.getName());
    }

    @Test
    public void addCocktailToEmptyList() {

        testCreateShoppingList_Success();
        UUID shoppingListId = shoppingListService.getShoppingListId("testList");
        UUID cocktail501BlueId = cocktailService.searchCocktailsNameContaining("501 Blue").stream()
                .map(c -> c.getCocktailId()).findFirst().get();
        ShoppingListResource result = shoppingListService.addCocktailToShoppingList(shoppingListService.getShoppingListId("testList"), cocktail501BlueId);

        ShoppingListResource expectedResult = new ShoppingListResource(shoppingListId, "testList", Stream.of(cocktail501BlueId).collect(Collectors.toSet()));
        Assert.assertEquals(expectedResult.getShoppingListId(), result.getShoppingListId());
        Assert.assertEquals(expectedResult.getName(), result.getName());
        Assert.assertEquals(expectedResult.getCocktails(), result.getCocktails());
    }

    @Test
    public void addCocktailToExistingList_ReturningAllCocktails() {

        testCreateShoppingList_Success();
        UUID shoppingListId = shoppingListService.getShoppingListId("testList");
        Set<CocktailResource> cocktails = new HashSet<>();
        cocktails.addAll(cocktailService.searchCocktailsNameContaining("501 Blue"));
        cocktails.addAll(cocktailService.searchCocktailsNameContaining("151 Florida Bushwacker"));
        cocktails.addAll(cocktailService.searchCocktailsNameContaining("A1"));

        Set<UUID> cocktailsIds = cocktails.stream()
                .map(c -> c.getCocktailId()).collect(Collectors.toSet());

        ShoppingListResource result = shoppingListService.addCocktailsToShoppingList(shoppingListId, cocktailsIds);

        ShoppingListResource expectedResult = new ShoppingListResource(shoppingListId, "testList", cocktailsIds);
        Assert.assertEquals(expectedResult.getShoppingListId(), result.getShoppingListId());
        Assert.assertEquals(expectedResult.getName(), result.getName());
        Assert.assertEquals(expectedResult.getCocktails(), result.getCocktails());
    }

}
