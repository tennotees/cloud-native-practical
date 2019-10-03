package com.ezgroceries.shoppinglist.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@WebMvcTest(CocktailController.class)
public class CocktailControllerTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testgetCocktails_ResponseStatusOk() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .get("/cocktails")
                .param("search", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testgetCocktails_ContentTypeJson() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .get("/cocktails")
                .param("search", "1"))
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void testgetCocktails_ExpectedCocktailsReturned() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .get("/cocktails")
                .param("search", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].uuid").value("23b3d85a-3928-41c0-a533-6538a71e17c4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].uuid").value("d615ec78-fe93-467b-8d26-5d26d8eab073"));
    }

}
