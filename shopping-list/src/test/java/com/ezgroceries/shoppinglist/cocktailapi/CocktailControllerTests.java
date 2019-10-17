package com.ezgroceries.shoppinglist.cocktailapi;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.service.CocktailDBClientServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(CocktailController.class)
@RunWith(SpringRunner.class)
public class CocktailControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CocktailDBClientServiceImpl cocktailDBClientServiceMock;

    @Before
    public void setupMockServiceToReturn2HardcodedCocktails() {
        List<CocktailResource> cocktails = Arrays.asList(
                new CocktailResource(
                        UUID.fromString("23b3d85a-3928-41c0-a533-6538a71e17c4"), "Margerita",
                        "Cocktail glass",
                        "Rub the rim of the glass with the lime slice to make the salt stick to it. Take care to moisten..",
                        "https://www.thecocktaildb.com/images/media/drink/wpxpvu1439905379.jpg",
                        Arrays.asList("Tequila", "Triple sec", "Lime juice", "Salt")),
                new CocktailResource(
                        UUID.fromString("d615ec78-fe93-467b-8d26-5d26d8eab073"), "Blue Margerita",
                        "Cocktail glass",
                        "Rub rim of cocktail glass with lime juice. Dip rim in coarse salt..",
                        "https://www.thecocktaildb.com/images/media/drink/qtvvyq1439905913.jpg",
                        Arrays.asList("Tequila", "Blue Curacao", "Lime juice", "Salt"))
        );
        Mockito.when(cocktailDBClientServiceMock.searchCocktailsNameContaining(Mockito.anyString())).thenReturn(cocktails);
    }


    @Test
    public void testgetCocktails_ResponseStatusOk() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .get("/cocktails")
                .param("search", "Blue")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testgetCocktails_ContentTypeJson() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .get("/cocktails")
                .param("search", "Blue"))
                .andExpect(header().string("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void testgetCocktails_ExpectedCocktailsReturned() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .get("/cocktails")
                .param("search", "Margerita")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cocktailId").value("23b3d85a-3928-41c0-a533-6538a71e17c4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].cocktailId").value("d615ec78-fe93-467b-8d26-5d26d8eab073"))
                .andReturn();
    }

}
