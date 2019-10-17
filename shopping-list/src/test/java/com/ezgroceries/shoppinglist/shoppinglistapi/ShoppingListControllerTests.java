package com.ezgroceries.shoppinglist.shoppinglistapi;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(ShoppingListController.class)
public class ShoppingListControllerTests {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper = new ObjectMapper();

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
        final String listId = "27912cbc-165e-4182-8491-23afb0e29919";
        final String cocktailId = "23b3d85a-3928-41c0-a533-6538a71e17c4";
        Map<String, String> cocktail = new HashMap<>();
        cocktail.put("cocktailId", cocktailId);
        List<Map> cocktails = new ArrayList<>();
        cocktails.add(cocktail);
        ObjectMapper objectMapper = new ObjectMapper();

        mvc.perform(MockMvcRequestBuilders
                .post("/shopping-lists")
                .param("name", "Stephanie's birthday"))
                .andExpect(status().isCreated());

        MvcResult result = mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists/{listId}/cocktails" , listId)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(cocktails))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        resultAsString = resultAsString.substring(1, resultAsString.length()-1);
        Map<String, String> resultMap = objectMapper.readValue(resultAsString, Map.class);

        assertEquals(resultMap.get("cocktailId"), cocktailId);
    }

    @Test
    public void testGetList_ResponseOkAndListWithIngredientsReturned() throws Exception
    {
        final String cocktailId = "23b3d85a-3928-41c0-a533-6538a71e17c4";
        Map<String, String> cocktail = new HashMap<>();
        cocktail.put("cocktailId", cocktailId);
        List<Map> cocktails = new ArrayList<>();
        cocktails.add(cocktail);

        String listId1 = createShoppingList("Stephanie's birthday");
        String listId2 = createShoppingList("My Birthday");

        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists/{listId}/cocktails" , listId1)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(cocktails))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/shopping-lists/{listId}", listId1))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        Map resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertEquals(resultMap.get("shoppingListId"), "27912cbc-165e-4182-8491-23afb0e29919");
        assertEquals(resultMap.get("name"), "Stephanie's birthday");
        assertEquals(resultMap.get("ingredients"), Arrays.asList("Tequila","Triple sec","Lime juice","Salt"));

    }

    @Test
    public void testGetAllLists_ResponseOkAndListsReturned() throws Exception
    {
        final String cocktailId = "23b3d85a-3928-41c0-a533-6538a71e17c4";
        Map<String, String> cocktail = new HashMap<>();
        cocktail.put("cocktailId", cocktailId);
        List<Map> cocktails = new ArrayList<>();
        cocktails.add(cocktail);

        String listId1 = createShoppingList("Stephanie's birthday");
        String listId2 = createShoppingList("My Birthday");

        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists/{listId}/cocktails" , listId1)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(cocktails))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                .get("/shopping-lists"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(JUnitMatchers.containsString("Stephanie's birthday")))
                .andExpect(MockMvcResultMatchers.content().string(JUnitMatchers.containsString("My Birthday")));

    }

    private String createShoppingList(String name) throws Exception
    {
        MvcResult result =  mvc.perform(MockMvcRequestBuilders
                .post("/shopping-lists")
                .param("name", name))
                .andExpect(status().isCreated())
                .andReturn();

       Map<String, String> convertedResult = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
       return convertedResult.get("shoppingListId");
    }

    private String getCocktailId(String name) throws Exception
    {
        MvcResult result =  mvc.perform(MockMvcRequestBuilders
                .get("/cocktails")
                .param("name", name))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, String> convertedResult = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        return convertedResult.get("cocktailId");
    }
}

