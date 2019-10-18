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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(CocktailController.class)
@RunWith(SpringRunner.class)
@ComponentScan("com.ezgroceries.shoppinglist.cocktailapi")
public class CocktailServiceTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
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
    public void testgetCocktailsViaDirectServiceCall_ExpectedCocktailsReturned() throws Exception
    {
        Set<CocktailResource> result = cocktailDBClientService.searchCocktailsNameContaining("Blue");

        Assert.assertNotNull(result.stream().filter(c -> c.getName().equals("Blue Margerita servicetest")).findAny());
        Assert.assertEquals(1, result.stream().filter(c -> c.getName().equals("Blue Margerita servicetest")).count());
    }

    @Test
    public void testgetCocktailsViaControllerApiCall_ExpectedCocktailsReturned() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .get("/cocktails")
                .param("search", "Blue")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Blue Margerita servicetest"));

    }
}
