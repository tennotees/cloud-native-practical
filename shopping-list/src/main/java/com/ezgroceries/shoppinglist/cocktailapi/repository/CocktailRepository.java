package com.ezgroceries.shoppinglist.cocktailapi.repository;

import com.ezgroceries.shoppinglist.cocktailapi.entity.CocktailEntity;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CocktailRepository extends CrudRepository<CocktailEntity, UUID> {

    Set<CocktailEntity> findByNameLike(String name);


}
