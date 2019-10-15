package com.ezgroceries.shoppinglist.cocktailapi;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBClient;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse.DrinkResource;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailDBClientServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


//@WebMvcTest(CocktailDBClientService.class)
@RunWith(SpringRunner.class)
public class CocktailServiceTests {

 //   @Autowired
 //   private MockMvc mvc;

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
        response.setDrinks(Arrays.asList(drink));
        Mockito.when(cocktailDBClientMock.searchCocktails("Blue")).thenReturn(response);
    }

    @Test
    public void testgetCocktails_ExpectedCocktailsReturned() throws Exception
    {
        List<CocktailResource> result = cocktailDBClientService.searchCocktailsNameContaining("Blue");

        //can't test on uuid since it's randomly generated in servicelayer
        Assert.assertEquals("Blue Margerita servicetest", result.get(0).getName());

        //CAN'T GET IT TO RUN WITH MVC CALL !!!! HOW TODO THIS ????
        //so a test of controller + service layer with repo mockeds
        /*mvc.perform( MockMvcRequestBuilders
                .get("/cocktails")
                .param("search", "Blue")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].uuid").value("23b3d85a-3928-41c0-a533-6538a71e17c4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].uuid").value("d615ec78-fe93-467b-8d26-5d26d8eab073"));

         */
    }

}
