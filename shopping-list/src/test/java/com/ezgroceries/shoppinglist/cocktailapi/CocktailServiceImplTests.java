package com.ezgroceries.shoppinglist.cocktailapi;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBClient;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse.DrinkResource;
import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.entity.CocktailEntity;
import com.ezgroceries.shoppinglist.cocktailapi.repository.CocktailRepository;
import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan("com.ezgroceries.shoppinglist.cocktailapi")
public class CocktailServiceImplTests {

    @MockBean
    private CocktailRepository cocktailRepositoryMock;

    @MockBean
    private CocktailDBClient cocktailDBClientMock;

    private CocktailServiceImpl cocktailService;

    @Before
    public void setUp() {
        cocktailService  = new CocktailServiceImpl(cocktailRepositoryMock, cocktailDBClientMock);

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
        Mockito.when(cocktailRepositoryMock.findByNameContaining("Blue")).thenReturn(new ArrayList<>());
    }

    @Test
    public void testgetCocktails_LocalNonExistant_ExpectedCocktailsReturned() throws Exception
    {
        //no cocktails exist in local db
        Mockito.when(cocktailRepositoryMock.findByNameContaining("Blue")).thenReturn(new ArrayList<>());

        ArgumentCaptor<Map<String, CocktailEntity>> cocktailsInLocalDb = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Set<CocktailEntity>> newCocktailsToSave = ArgumentCaptor.forClass(Set.class);
        CocktailServiceImpl cocktailServiceSpy = Mockito.spy(cocktailService);

        Set<CocktailResource> result = cocktailServiceSpy.searchCocktailsNameContaining("Blue");

        Assert.assertNotNull(result.stream().filter(c -> c.getName().equals("Blue Margerita servicetest")).findAny());
        Assert.assertEquals(1, result.stream().filter(c -> c.getName().equals("Blue Margerita servicetest")).count());
        Mockito.verify(cocktailServiceSpy).mergingCocktails(cocktailsInLocalDb.capture(), newCocktailsToSave.capture());
        Set<CocktailEntity> cocktailSetToSave = newCocktailsToSave.getAllValues().get(0);
        Assert.assertEquals(1, cocktailSetToSave.size());
        Assert.assertEquals("Blue Margerita servicetest", ((CocktailEntity) cocktailSetToSave.toArray()[0]).name);
    }

    @Test
    public void testgetCocktails_LocalExistant_ExpectedCocktailsReturned() throws Exception
    {
        //cocktail exists in local db
        CocktailEntity cocktailEntity = new CocktailEntity();
        cocktailEntity.id = UUID.randomUUID();
        cocktailEntity.iddrink = "d615ec78-fe93-467b-8d26-5d26d8eab073";
        cocktailEntity.name = "Blue Margerita servicetest";
        cocktailEntity.ingredients = Stream.of("Tequila","Blue Curacao","Lime juice","Salt").collect(Collectors.toSet());
        List<CocktailEntity> localDBCocktails = new ArrayList<>();
        localDBCocktails.add(cocktailEntity);
        Mockito.when(cocktailRepositoryMock.findByNameContaining("Blue")).thenReturn(localDBCocktails);

        ArgumentCaptor<Map<String, CocktailEntity>> cocktailsInLocalDb = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Set<CocktailEntity>> newCocktailsToSave = ArgumentCaptor.forClass(Set.class);
        CocktailServiceImpl cocktailServiceSpy = Mockito.spy(cocktailService);

        Set<CocktailResource> result = cocktailServiceSpy.searchCocktailsNameContaining("Blue");

        Assert.assertNotNull(result.stream().filter(c -> c.getName().equals("Blue Margerita servicetest")).findAny());
        Assert.assertEquals(1, result.stream().filter(c -> c.getName().equals("Blue Margerita servicetest")).count());
        Mockito.verify(cocktailServiceSpy).mergingCocktails(cocktailsInLocalDb.capture(), newCocktailsToSave.capture());
        Assert.assertEquals(0, newCocktailsToSave.getAllValues().get(0).size());
    }
}
