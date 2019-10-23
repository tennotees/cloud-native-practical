package com.ezgroceries.shoppinglist.cocktailapi;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBClient;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse.DrinkResource;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailDBClientServiceImpl;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;

//2 ways in configuring this :
// [1] loads a smaller context : MockitoJUnitRunner & @Mock
// [2] loads a full ApplicationContext : SpringRunner & @MockBean


//[1] :
@RunWith(MockitoJUnitRunner.class)
//[2] : @RunWith(SpringRunner.class)
@ComponentScan("com.ezgroceries.shoppinglist.cocktailapi")
public class CocktailDBClientServiceImplTests {

    //[1] :
    @Mock
    //[2] : @MockBean
    private CocktailDBClient cocktailDBClientMock;

    private CocktailDBClientServiceImpl cocktailDBClientService;

    @Before
    public void setUp() {
        cocktailDBClientService = new CocktailDBClientServiceImpl(cocktailDBClientMock);

        DrinkResource drink =  new DrinkResource();
        drink.setIdDrink("d615ec78-fe93-467b-8d26-5d26d8eab073");
        drink.setStrDrink("Blue Margerita servicetest");
        drink.setStrGlass("Cocktail glass");
        drink.setStrInstructions("Rub rim of cocktail glass with lime juice. Dip rim in coarse salt..");
        drink.setStrDrinkThumb("https://www.thecocktaildb.com/images/media/drink/qtvvyq1439905913.jpg");
        drink.setStrIngredient1("Tequila");
        drink.setStrIngredient2("Blue Curacao");
        drink.setStrIngredient3("Lime juice");
        drink.setStrIngredient4("Salt");
        CocktailDBResponse response = new CocktailDBResponse();
        response.addDrink(drink);
        Mockito.when(cocktailDBClientMock.searchCocktails("Blue")).thenReturn(response);
    }

    @Test
    public void testgetCocktails_ExpectedCocktailsReturned() throws Exception
    {
        Set<CocktailResource> result = cocktailDBClientService.searchCocktailsNameContaining("Blue");

        Assert.assertNotNull(result.stream().filter(c -> c.getName().equals("Blue Margerita servicetest")).findAny());
        Assert.assertEquals(1, result.stream().filter(c -> c.getName().equals("Blue Margerita servicetest")).count());
    }

}
