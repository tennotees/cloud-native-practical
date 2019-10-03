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
@WebMvcTest(ShoppingListController.class)
public class ShoppingListControllerTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testCreateList_ResponseStatusOk() throws Exception
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

}

