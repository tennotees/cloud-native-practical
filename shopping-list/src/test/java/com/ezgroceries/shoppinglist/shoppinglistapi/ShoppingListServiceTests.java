package com.ezgroceries.shoppinglist.shoppinglistapi;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.ezgroceries.shoppinglist.shoppinglistapi.db.ShoppingListResource;
import com.ezgroceries.shoppinglist.shoppinglistapi.entity.CocktailShoppingListEntity;
import com.ezgroceries.shoppinglist.shoppinglistapi.entity.ShoppingListEntity;
import com.ezgroceries.shoppinglist.shoppinglistapi.repository.CocktailShoppingListRepository;
import com.ezgroceries.shoppinglist.shoppinglistapi.repository.ShoppingListRepository;
import com.ezgroceries.shoppinglist.shoppinglistapi.service.ShoppingListService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShoppingListServiceTests {

    @Autowired
    private ShoppingListService shoppingListService;

    @MockBean
    private ShoppingListRepository shoppingListRepository;

    @MockBean
    private CocktailShoppingListRepository cocktailShoppingListRepository;

    @Before
    public void init() {

    }

    @Test
    public void testCreateShoppingList_Success() {

        //GIVEN
        Mockito.when(shoppingListRepository.save(Mockito.any())).thenReturn(new ShoppingListEntity());

        //WHEN
        ShoppingListResource result = shoppingListService.createNewShoppingList("testList");

        //THEN
        Assert.assertEquals("testList", result.getName());
    }

    @Test
    public void addCocktailToEmptyList() {

        //GIVEN
        when(shoppingListRepository.save(any())).thenReturn(new ShoppingListEntity());
        ShoppingListResource shoppingList = shoppingListService.createNewShoppingList("testList");
        UUID cocktail501BlueId = UUID.fromString("6970626f-a371-4f7c-a2f5-10324fe51be9");
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.id = shoppingList.getShoppingListId();
        shoppingListEntity.name = shoppingList.getName();

        when(cocktailShoppingListRepository.save(any())).thenReturn(new CocktailShoppingListEntity());
        when(shoppingListRepository.findById(anyObject())).thenReturn(Optional.of(shoppingListEntity));
        CocktailShoppingListEntity cocktailShoppingListEntity = new CocktailShoppingListEntity();
        cocktailShoppingListEntity.shoppingListId = shoppingList.getShoppingListId();
        cocktailShoppingListEntity.cocktailId = cocktail501BlueId;
        when(cocktailShoppingListRepository.findCocktailShoppingListEntitiesByShoppingListId(anyObject())).thenReturn(Lists.list(cocktailShoppingListEntity));

        //WHEN
        ShoppingListResource result = shoppingListService.addCocktailToShoppingList(shoppingList.getShoppingListId(), cocktail501BlueId);

        //THEN
        ShoppingListResource expectedResult = new ShoppingListResource(shoppingList.getShoppingListId(), "testList", Stream.of(cocktail501BlueId).collect(Collectors.toSet()));
        Assert.assertEquals(expectedResult.getShoppingListId(), result.getShoppingListId());
        Assert.assertEquals(expectedResult.getName(), result.getName());
        Assert.assertEquals(expectedResult.getCocktails(), result.getCocktails());
    }

    @Test
    public void addCocktailToExistingList_ReturningAllCocktails() {

        //GIVEN
        when(shoppingListRepository.save(any())).thenReturn(new ShoppingListEntity());
        ShoppingListResource shoppingList = shoppingListService.createNewShoppingList("testList");

        Set<String> cocktailIds = Sets.newSet("6970626f-a371-4f7c-a2f5-10324fe51be9", "d452a79a-3b8d-4a0e-9edc-4263a78b3ed6",
                "3587849d-bcd6-4a04-a70e-9ae57e356344");

        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.id = shoppingList.getShoppingListId();
        shoppingListEntity.name = shoppingList.getName();

        when(cocktailShoppingListRepository.save(any())).thenReturn(new CocktailShoppingListEntity());
        when(shoppingListRepository.findById(anyObject())).thenReturn(Optional.of(shoppingListEntity));

        List<CocktailShoppingListEntity> cocktailShoppingListEntitySet = cocktailIds.stream().map(cocktailId -> {
            CocktailShoppingListEntity cocktailShoppingListEntity = new CocktailShoppingListEntity();
            cocktailShoppingListEntity.shoppingListId = shoppingList.getShoppingListId();
            cocktailShoppingListEntity.cocktailId = UUID.fromString(cocktailId);
            return cocktailShoppingListEntity;
        }).collect(Collectors.toList());

        when(cocktailShoppingListRepository.findCocktailShoppingListEntitiesByShoppingListId(anyObject()))
                .thenReturn(cocktailShoppingListEntitySet);


        //WHEN
        ShoppingListResource result = shoppingListService.addCocktailsToShoppingList(shoppingList.getShoppingListId().toString(), cocktailIds);

        //THEN
        ShoppingListResource expectedResult = new ShoppingListResource(shoppingList.getShoppingListId(), "testList", cocktailIds.stream()
                .map(id -> UUID.fromString(id)).collect(Collectors.toSet()));
        Assert.assertEquals(expectedResult.getShoppingListId(), result.getShoppingListId());
        Assert.assertEquals(expectedResult.getName(), result.getName());
        Assert.assertEquals(expectedResult.getCocktails(), result.getCocktails());
    }

    @Test
    public void getShoppingListId_Ok() {

        //GIVEN
        when(shoppingListRepository.save(any())).thenReturn(new ShoppingListEntity());
        ShoppingListResource shoppingList = shoppingListService.createNewShoppingList("testList");

        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.id = shoppingList.getShoppingListId();
        shoppingListEntity.name = shoppingList.getName();
        when(shoppingListRepository.findByName(anyString())).thenReturn(Lists.list(shoppingListEntity));

        //WHEN
        UUID resultUuid = shoppingListService.getShoppingListId("testList");

        //THEN
        Assert.assertEquals(shoppingList.getShoppingListId(), resultUuid);
    }

    @Test
    public void getAllShoppingLists_Ok() {

        //GIVEN
        when(shoppingListRepository.save(any())).thenReturn(new ShoppingListEntity());
        List<String> shoppingLists = Lists.list("testList", "testList2", "testList3");

        List<ShoppingListEntity> shoppingListEntityList = shoppingLists.stream().map(list -> {
            ShoppingListResource shoppingList = shoppingListService.createNewShoppingList(list);

            ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
            shoppingListEntity.id = shoppingList.getShoppingListId();
            shoppingListEntity.name = shoppingList.getName();
            return shoppingListEntity;
        }).collect(Collectors.toList());
        when(shoppingListRepository.findAll()).thenReturn(shoppingListEntityList);

        //WHEN
        Set<UUID> resultUuids = shoppingListService.getAllShoppingLists();

        //THEN
        shoppingListEntityList.stream().forEach(sle ->
            Assert.assertTrue(resultUuids.contains(sle.id)));
    }

        @Test
    public void getShoppingListCocktails_ReturningAllCocktails() {

        //GIVEN
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.id = UUID.randomUUID();
        shoppingListEntity.name = "testList";

        Set<String> cocktailIds = Sets.newSet("6970626f-a371-4f7c-a2f5-10324fe51be9", "d452a79a-3b8d-4a0e-9edc-4263a78b3ed6",
                "3587849d-bcd6-4a04-a70e-9ae57e356344");

        when(shoppingListRepository.findById(anyObject())).thenReturn(Optional.of(shoppingListEntity));

        List<CocktailShoppingListEntity> cocktailShoppingListEntitySet = cocktailIds.stream().map(cocktailId -> {
            CocktailShoppingListEntity cocktailShoppingListEntity = new CocktailShoppingListEntity();
            cocktailShoppingListEntity.shoppingListId = shoppingListEntity.id;
            cocktailShoppingListEntity.cocktailId = UUID.fromString(cocktailId);
            return cocktailShoppingListEntity;
        }).collect(Collectors.toList());

        when(cocktailShoppingListRepository.findCocktailShoppingListEntitiesByShoppingListId(anyObject()))
                .thenReturn(cocktailShoppingListEntitySet);

        //WHEN
        ShoppingListResource resultResource = shoppingListService.getShoppingListCocktails(shoppingListEntity.id);

        //THEN
        ShoppingListResource expectedResult = new ShoppingListResource(shoppingListEntity.id, "testList", cocktailIds.stream()
                .map(id -> UUID.fromString(id)).collect(Collectors.toSet()));
        Assert.assertEquals(expectedResult.getShoppingListId(), resultResource.getShoppingListId());
        Assert.assertEquals(expectedResult.getName(), resultResource.getName());
        Assert.assertEquals(expectedResult.getCocktails(), resultResource.getCocktails());
    }



}
