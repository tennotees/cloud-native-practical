package com.ezgroceries.shoppinglist.shoppinglistapi.repository;

import com.ezgroceries.shoppinglist.shoppinglistapi.entity.CocktailShoppingListEntity;
import com.ezgroceries.shoppinglist.shoppinglistapi.entity.CocktailShoppingListId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CocktailShoppingListRepository extends CrudRepository<CocktailShoppingListEntity, CocktailShoppingListId> {

    List<CocktailShoppingListEntity> findCocktailShoppingListEntitiesByShoppingListId(UUID shoppingListId);
}
