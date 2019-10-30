package com.ezgroceries.shoppinglist.shoppinglistapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailServiceImpl;
import com.ezgroceries.shoppinglist.shoppinglistapi.db.ShoppingListResource;
import com.ezgroceries.shoppinglist.shoppinglistapi.service.ShoppingListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingListControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CocktailServiceImpl cocktailService;

    @MockBean
    private ShoppingListService shoppingListService;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testCreateList_ReturnsCreatedAndJson() throws Exception
    {
        //GIVEN
        String shoppingListName = "Stephanie's birthday";
        ShoppingListResource shoppingListResource = new ShoppingListResource();
        shoppingListResource.setShoppingListId(UUID.randomUUID());
        shoppingListResource.setName(shoppingListName);
        when(shoppingListService.createNewShoppingList(shoppingListName)).thenReturn(shoppingListResource);

        //WHEN
        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists")
                .param("name", shoppingListName)
                .accept(MediaType.APPLICATION_JSON))
        //THEN
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void testCreateList_ExpectedListReturned() throws Exception
    {
        //GIVEN
        String shoppingListName = "Stephanie's birthday";
        ShoppingListResource shoppingListResource = new ShoppingListResource();
        shoppingListResource.setShoppingListId(UUID.randomUUID());
        shoppingListResource.setName(shoppingListName);
        when(shoppingListService.createNewShoppingList(shoppingListName)).thenReturn(shoppingListResource);

        //WHEN
        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists")
                .param("name", shoppingListName)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(shoppingListName));
    }

    @Test
    public void testAddCocktailsToList_ResponseOkAndIdReturned() throws Exception
    {
        //GIVEN
        String shoppingListName = "Stephanie's birthday";
        String cocktailId = "6970626f-a371-4f7c-a2f5-10324fe51be9";
        CocktailResource cocktailResource = new CocktailResource(UUID.fromString(cocktailId), "Blue Forest", Sets.newHashSet("Lemon", "Gin", "7Up", "Bitters"));
        ShoppingListResource shoppingListResource = new ShoppingListResource();
        shoppingListResource.setShoppingListId(UUID.randomUUID());
        shoppingListResource.setName(shoppingListName);
        shoppingListResource.setCocktails(new HashSet<>());
        when(shoppingListService.createNewShoppingList(shoppingListName)).thenReturn(shoppingListResource);
        when(shoppingListService.doesShoppingListExist(anyString())).thenReturn(true);
        when(cocktailService.doesCocktailExist(anyString())).thenReturn(true);
        shoppingListResource.addCocktail(cocktailId);
        when(shoppingListService.addCocktailsToShoppingList(anyString(), anyObject())).thenReturn(shoppingListResource);
        when(cocktailService.searchCocktailByName(anyObject())).thenReturn(cocktailResource);

        //WHEN
        MvcResult result = addCocktailsToList(shoppingListName, shoppingListResource.getCocktails().stream().map(uuid -> uuid.toString())
                .collect(Collectors.toList()));

        //THEN
        String resultAsString = result.getResponse().getContentAsString();
        resultAsString = resultAsString.substring(1, resultAsString.length()-1);
        Map<String, String> resultMap = objectMapper.readValue(resultAsString, Map.class);

        assertEquals(cocktailId, resultMap.get("cocktailId"));
    }

    @Test
    public void testGetList_ResponseOkAndListWithIngredientsReturned() throws Exception
    {
        //GIVEN
        String shoppingListName = "Stephanie's birthday";
        UUID shoppingListId = UUID.randomUUID();
        String cocktailId = "6970626f-a371-4f7c-a2f5-10324fe51be9";
        CocktailResource cocktailResource = new CocktailResource(UUID.fromString(cocktailId), "Blue Forest", Sets.newHashSet("Lemon", "Gin", "7Up", "Bitters"));
        ShoppingListResource shoppingListResource = new ShoppingListResource();
        shoppingListResource.setShoppingListId(shoppingListId);
        shoppingListResource.setName(shoppingListName);
        shoppingListResource.setCocktails(new HashSet<>());
        when(shoppingListService.createNewShoppingList(shoppingListName)).thenReturn(shoppingListResource);
//        when(shoppingListService.doesShoppingListExist(anyString())).thenReturn(true);
//        when(cocktailService.doesCocktailExist(anyString())).thenReturn(true);
        shoppingListResource.addCocktail(cocktailId);
        when(shoppingListService.addCocktailsToShoppingList(anyString(), anyObject())).thenReturn(shoppingListResource);
//        when(cocktailService.searchCocktailByName(anyObject())).thenReturn(cocktailResource);
        when(shoppingListService.getShoppingListCocktails(anyObject())).thenReturn(shoppingListResource);
        when(cocktailService.searchCocktailById(cocktailId)).thenReturn(cocktailResource);

        //WHEN
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/shopping-lists/{listId}", shoppingListId))

        //THEN
                .andExpect(status().isOk())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        Map resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertEquals(resultMap.get("shoppingListId"), shoppingListId.toString());
        assertEquals(resultMap.get("name"), shoppingListName);
        assertEquals(cocktailResource.getIngredients(), ((ArrayList<String>) resultMap.get("ingredients")).stream().collect(Collectors.toSet()));
    }

    @Test
    public void testGetAllLists_ResponseOkAndListsReturned() throws Exception
    {
        //GIVEN
        String shoppingListName1 = "Stephanie's birthday";
        UUID shoppingListId1 = UUID.randomUUID();
        String shoppingListName2 = "My Birthday";
        UUID shoppingListId2 = UUID.randomUUID();
        String cocktailId = "6970626f-a371-4f7c-a2f5-10324fe51be9";
        CocktailResource cocktailResource = new CocktailResource(UUID.fromString(cocktailId), "Blue Forest", Sets.newHashSet("Lemon", "Gin", "7Up", "Bitters"));
        ShoppingListResource shoppingListResource = new ShoppingListResource();
        shoppingListResource.setShoppingListId(shoppingListId1);
        shoppingListResource.setName(shoppingListName1);
        shoppingListResource.setCocktails(new HashSet<>());
        shoppingListResource.addCocktail(cocktailId);
        when(shoppingListService.getShoppingListCocktails(shoppingListId1)).thenReturn(shoppingListResource);
        ShoppingListResource shoppingListResource2 = new ShoppingListResource();
        shoppingListResource2.setShoppingListId(shoppingListId2);
        shoppingListResource2.setName(shoppingListName2);
        shoppingListResource2.setCocktails(new HashSet<>());
        when(shoppingListService.getShoppingListCocktails(shoppingListId2)).thenReturn(shoppingListResource2);
        when(shoppingListService.getAllShoppingLists()).thenReturn(Sets.newHashSet(shoppingListId1, shoppingListId2));
        when(cocktailService.searchCocktailById(cocktailId)).thenReturn(cocktailResource);

        //WHEN
        mvc.perform(MockMvcRequestBuilders
                .get("/shopping-lists"))
         //THEN
                .andExpect(status().isOk())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(JUnitMatchers.containsString(shoppingListName1)))
                .andExpect(MockMvcResultMatchers.content().string(JUnitMatchers.containsString(shoppingListName2)));

    }

    @Test
    public void testAllLists_ResponseOkAndListsWithIngredientsReturned() throws Exception
    {
        //GIVEN
        String shoppingListName1 = "Stephanie's birthday";
        UUID shoppingListId1 = UUID.randomUUID();
        String shoppingListName2 = "My Birthday";
        UUID shoppingListId2 = UUID.randomUUID();
        String cocktailId = "6970626f-a371-4f7c-a2f5-10324fe51be9";
        List<List<String>> expectedIngredients = new ArrayList<>();
        expectedIngredients.add(Stream.of("Lemon", "Gin", "7Up", "Bitters").sorted().collect(Collectors.toList()));
        expectedIngredients.add(new ArrayList<>());
        CocktailResource cocktailResource = new CocktailResource(UUID.fromString(cocktailId), "Blue Forest", expectedIngredients.get(0).stream().collect(Collectors.toSet()));
        ShoppingListResource shoppingListResource = new ShoppingListResource();
        shoppingListResource.setShoppingListId(shoppingListId1);
        shoppingListResource.setName(shoppingListName1);
        shoppingListResource.setCocktails(new HashSet<>());
        shoppingListResource.addCocktail(cocktailId);
        when(shoppingListService.getShoppingListCocktails(shoppingListId1)).thenReturn(shoppingListResource);
        ShoppingListResource shoppingListResource2 = new ShoppingListResource();
        shoppingListResource2.setShoppingListId(shoppingListId2);
        shoppingListResource2.setName(shoppingListName2);
        shoppingListResource2.setCocktails(new HashSet<>());
        when(shoppingListService.getShoppingListCocktails(shoppingListId2)).thenReturn(shoppingListResource2);
        when(shoppingListService.getAllShoppingLists()).thenReturn(Sets.newHashSet(shoppingListId1, shoppingListId2));
        when(cocktailService.searchCocktailById(cocktailId)).thenReturn(cocktailResource);

        //WHEN
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/shopping-lists/"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        //THEN
        List resultLists = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertEquals(resultLists.size(), 2);


        resultLists.stream().forEach(list -> {
            UUID listId = UUID.fromString(((Map) list).get("shoppingListId").toString());
            List<String> ingredients = (List<String>) ((List) ((Map) list).get("ingredients")).stream().sorted().collect(Collectors.toList());
            if (!shoppingListId1.equals(listId)) {
                assertEquals(shoppingListId2, listId);
                assertTrue(expectedIngredients.get(1).equals(ingredients));
            } else {
                assertEquals(shoppingListId1, listId);
                assertTrue(expectedIngredients.get(0).equals(ingredients));
            }
        });
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

