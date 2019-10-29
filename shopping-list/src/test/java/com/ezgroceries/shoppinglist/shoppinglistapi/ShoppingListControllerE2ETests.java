package com.ezgroceries.shoppinglist.shoppinglistapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailServiceImpl;
import com.ezgroceries.shoppinglist.shoppinglistapi.service.ShoppingListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingListControllerE2ETests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CocktailServiceImpl cocktailService;

    @Autowired
    private ShoppingListService shoppingListService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        shoppingListService.emptyShoppingListTables();
        //initializing cocktails table with list of cocktails
        cocktailService.emptyTable();
        cocktailService.searchCocktailsNameContaining("Blue");
    }

    @Test
    public void testCreateList_ResponseStatusCreated() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists")
                .param("name", "Stephanie's birthday")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateList_ContentTypeJson() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists")
                .param("name", "Stephanie's birthday"))
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void testCreateList_ExpectedListReturned() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists")
                .param("name", "Stephanie's birthday")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Stephanie's birthday"));
    }

    @Test
    public void testAddCocktailsToList_ResponseOkAndIdReturned() throws Exception
    {
        //GIVEN
        String listId1 = createShoppingList("Stephanie's birthday");

        //WHEN
        MvcResult result = addCocktailsToList(listId1, Arrays.asList("Blue Hurricane"));

        //THEN
        String resultAsString = result.getResponse().getContentAsString();
        resultAsString = resultAsString.substring(1, resultAsString.length()-1);
        Map<String, String> resultMap = objectMapper.readValue(resultAsString, Map.class);

        assertEquals(resultMap.get("cocktailId"), shoppingListService.getCocktailIdsFromShoppingList(listId1).get(0));
    }

    @Test
    public void testGetList_ResponseOkAndListWithIngredientsReturned() throws Exception
    {
        //GIVEN
        String listId1 = createShoppingList("Stephanie's birthday");
        addCocktailsToList(listId1, Arrays.asList("Blue Hurricane"));
        createShoppingList("My Birthday");
        Set<String> expectedIngredients = Stream.of("Blue Curacao", "Passoa","Sweet and Sour","Dark Rum","Ice","Rum")
                .collect(Collectors.toSet());

        //WHEN
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/shopping-lists/{listId}", listId1))

        //THEN
                .andExpect(status().isOk())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        Map resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertEquals(resultMap.get("shoppingListId"), listId1);
        assertEquals(resultMap.get("name"), "Stephanie's birthday");
        assertEquals(expectedIngredients, ((ArrayList<String>) resultMap.get("ingredients")).stream().collect(Collectors.toSet()));
    }

    @Test
    public void testGetAllLists_ResponseOkAndListsReturned() throws Exception
    {
        //GIVEN
        String listId1 = createShoppingList("Stephanie's birthday");
        addCocktailsToList(listId1, Arrays.asList("Blue Hurricane"));
        createShoppingList("My Birthday");

        //WHEN
        mvc.perform(MockMvcRequestBuilders
                .get("/shopping-lists"))
         //THEN
                .andExpect(status().isOk())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(JUnitMatchers.containsString("Stephanie's birthday")))
                .andExpect(MockMvcResultMatchers.content().string(JUnitMatchers.containsString("My Birthday")));

    }

    @Test
    public void testAllLists_ResponseOkAndListsWithIngredientsReturned() throws Exception
    {
        //GIVEN
        String listId1 = createShoppingList("Stephanie's birthday");
        addCocktailsToList(listId1, Arrays.asList("Blue Hurricane", "Blue Lagoon", "Bluebird"));

        String listId2 = createShoppingList("My Birthday");
        addCocktailsToList(listId2, Arrays.asList("Belgian Blue", "Bluebird"));

        //WHEN
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/shopping-lists/"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        //THEN
        List resultLists = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertEquals(resultLists.size(), 2);

        List<List<String>> expectedIngredients = Stream.of(
                Stream.of("Lemon peel","Maraschino cherry","Gin","Triple sec","Bitters","Lemonade","Cherry","Vodka","Blue Curacao",
                        "Passoa","Sweet and Sour","Dark Rum","Ice","Rum").sorted().collect(Collectors.toList()),
                Stream.of("Blue Curacao","Sprite","Coconut liqueur","Vodka","Lemon peel","Maraschino cherry","Gin","Triple sec","Bitters")
                        .sorted().collect(Collectors.toList())
        ).collect(Collectors.toList());

        resultLists.stream().forEach(list -> {
            String listId = ((Map) list).get("shoppingListId").toString();
            List<String> ingredients = (List<String>) ((List) ((Map) list).get("ingredients")).stream().sorted().collect(Collectors.toList());
            if (!listId1.equals(listId)) {
                assertEquals(listId2, listId);
                assertTrue(expectedIngredients.get(1).equals(ingredients));
            } else {
                assertEquals(listId1, listId);
                assertTrue(expectedIngredients.get(0).equals(ingredients));
            }
        });
    }

    private String createShoppingList(String listName) throws Exception
    {
        MvcResult result =  mvc.perform(MockMvcRequestBuilders
                .post("/shopping-lists")
                .param("name", listName))
                .andExpect(status().isCreated())
                .andReturn();

       Map<String, String> convertedResult = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
       return convertedResult.get("shoppingListId");
    }

    private MvcResult addCocktailsToList(String listName, List<String> cocktailNames) throws JsonProcessingException, Exception {
        List<Map<String, String>> cocktails = cocktailNames.stream().map(cocktail -> {
            String cId = cocktailService.searchCocktailByName(cocktail).getCocktailId().toString();
            Map<String, String> map = new HashMap();
            map.put("cocktailId", cId);
            return map;
        }).collect(Collectors.toList());

        return mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists/{listId}/cocktails" , listName)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(cocktails))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}

