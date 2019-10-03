package com.ezgroceries.shoppinglist.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(ShoppingListController.class)
public class ShoppingListControllerTests {

    @Autowired
    private MockMvc mvc;

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

        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists/{listId}/cocktails" , listId)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(cocktails))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().equals("{ cocktailId\": \"23b3d85a-3928-41c0-a533-6538a71e17c4\" }"));
    }

    @Test
    public void testAddCocktailsToList_BadRequestNoShoppingList() throws Exception
    {
        final String listId = "27912cbc-165e-4182-8491-23afb0e29919";
        final String cocktailId = "23b3d85a-3928-41c0-a533-6538a71e17c4";
        Map<String, String> cocktail = new HashMap<>();
        cocktail.put("cocktailId", cocktailId);
        List<Map> cocktails = new ArrayList<>();
        cocktails.add(cocktail);
        ObjectMapper objectMapper = new ObjectMapper();

        mvc.perform( MockMvcRequestBuilders
                .post("/shopping-lists/{listId}/cocktails" , listId)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(cocktails))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}

