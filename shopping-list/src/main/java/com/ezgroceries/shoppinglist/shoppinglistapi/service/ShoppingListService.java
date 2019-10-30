package com.ezgroceries.shoppinglist.shoppinglistapi.service;

import com.ezgroceries.shoppinglist.cocktailapi.repository.CocktailRepository;
import com.ezgroceries.shoppinglist.shoppinglistapi.db.ShoppingListResource;
import com.ezgroceries.shoppinglist.shoppinglistapi.entity.CocktailShoppingListEntity;
import com.ezgroceries.shoppinglist.shoppinglistapi.entity.ShoppingListEntity;
import com.ezgroceries.shoppinglist.shoppinglistapi.repository.CocktailShoppingListRepository;
import com.ezgroceries.shoppinglist.shoppinglistapi.repository.ShoppingListRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingListService {

    private ShoppingListRepository shoppingListRepository;
    private CocktailShoppingListRepository cocktailShoppingListRepository;
    private CocktailRepository cocktailRepository;

    @Autowired
    public ShoppingListService(ShoppingListRepository shoppingListRepository, CocktailShoppingListRepository cocktailShoppingListRepository,
            CocktailRepository cocktailRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.cocktailShoppingListRepository = cocktailShoppingListRepository;
        this.cocktailRepository = cocktailRepository;
    }

    public ShoppingListResource createNewShoppingList(String name) {
        ShoppingListResource shoppingListResource = new ShoppingListResource(name);
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.id = shoppingListResource.getShoppingListId();
        shoppingListEntity.name = shoppingListResource.getName();
        shoppingListRepository.save(shoppingListEntity);

        return shoppingListResource;
    }

    public boolean doesShoppingListExist(String listId) {
        return shoppingListRepository.findById(UUID.fromString(listId)).isPresent();
    }

    public ShoppingListResource addCocktailToShoppingList(String listId, String cocktailId) {
        return addCocktailToShoppingList(UUID.fromString(listId), UUID.fromString(cocktailId));
    }

    public ShoppingListResource addCocktailToShoppingList(UUID listId, UUID cocktailId) {
        return addCocktailsToShoppingList(listId, Stream.of(cocktailId).collect(Collectors.toSet()));
    }

    public ShoppingListResource addCocktailsToShoppingList(String listId, Set<String> cocktailIds) {
        return addCocktailsToShoppingList(UUID.fromString(listId),
                cocktailIds.stream().map(c -> UUID.fromString(c)).collect(Collectors.toSet()));
    }

    public ShoppingListResource addCocktailsToShoppingList(UUID listId, Set<UUID> cocktailIds) {
        cocktailIds.stream().map(cocktailId -> {
            CocktailShoppingListEntity cocktailShoppingListEntity = new CocktailShoppingListEntity();
            cocktailShoppingListEntity.shoppingListId = listId;
            cocktailShoppingListEntity.cocktailId = cocktailId;
             cocktailShoppingListRepository.save(cocktailShoppingListEntity);
            return cocktailShoppingListEntity;
        }).collect(Collectors.toSet());

        return convertToShoppingListResource(listId);

    }

    private ShoppingListResource convertToShoppingListResource(UUID listId) {
        ShoppingListEntity listEntity = shoppingListRepository.findById(listId).get();

        List<CocktailShoppingListEntity> cocktailsInListEntity =
                cocktailShoppingListRepository.findCocktailShoppingListEntitiesByShoppingListId(listEntity.id);

        Set<UUID> cocktailIds = cocktailsInListEntity.stream().map(c -> c.cocktailId).collect(Collectors.toSet());

        return new ShoppingListResource(listEntity.id, listEntity.name, cocktailIds);
    }

    public UUID getShoppingListId(String name) {
        List<ShoppingListEntity> shoppingListEntities = shoppingListRepository.findByName(name);
        return shoppingListEntities.get(0).id;
    }

    public Set<UUID> getAllShoppingLists() {
        List<ShoppingListEntity> shoppingListEntities = shoppingListRepository.findAll();
        return shoppingListEntities.stream()
                .map(shoppingListEntity -> shoppingListEntity.id).collect(Collectors.toSet());
    }

    public ShoppingListResource getShoppingListCocktails(UUID listUuid) {

        List<CocktailShoppingListEntity> cocktailShoppingListEntityList =
                cocktailShoppingListRepository.findCocktailShoppingListEntitiesByShoppingListId(listUuid);

        ShoppingListResource shoppingListWithCocktails = new ShoppingListResource();
        shoppingListWithCocktails.setShoppingListId(listUuid);
        ShoppingListEntity shoppingListEntity = shoppingListRepository.findById(listUuid).get();
        shoppingListWithCocktails.setName(shoppingListEntity.name);

        shoppingListWithCocktails.setCocktails(
                cocktailShoppingListEntityList.stream().map(csl -> csl.cocktailId).collect(Collectors.toSet()));

        return shoppingListWithCocktails;
    }

    public List<String> getCocktailIdsFromShoppingList(String listId) {
        List<CocktailShoppingListEntity> csl = cocktailShoppingListRepository.findCocktailShoppingListEntitiesByShoppingListId(UUID.fromString(listId));
        List<String> cocktailIds = csl.stream().map(entity -> entity.cocktailId.toString()).collect(Collectors.toList());
        return cocktailIds;
    }

    public void emptyShoppingListTables() {
        shoppingListRepository.deleteAll();
        cocktailShoppingListRepository.deleteAll();
    }
}
