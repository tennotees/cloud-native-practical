package com.ezgroceries.shoppinglist.cocktailapi.db;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailDBResponse.DrinkResource;
import com.ezgroceries.shoppinglist.cocktailapi.repository.CocktailRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "cocktailDBClient", url = "https://www.thecocktaildb.com/api/json/v1/1", fallback = CocktailDBClient.CocktailDBFallback.class)
public interface CocktailDBClient {

    @GetMapping(value = "search.php")
    CocktailDBResponse searchCocktails(@RequestParam("s") String search);

    @Component
    class CocktailDBFallback implements CocktailDBClient {

        @Autowired
        private CocktailRepository cocktailRepository;

        @Override
        public CocktailDBResponse searchCocktails(String search) {

            CocktailDBResponse cocktails = new CocktailDBResponse();
            Set<DrinkResource> drinks = cocktailRepository.findByNameContaining(search).stream()
                    .map(entity -> {
                        DrinkResource drinkResource = new DrinkResource();
                        drinkResource.setIdDrink(entity.iddrink);
                        drinkResource.setStrDrink(entity.name);
                        drinkResource.setStrGlass((entity.glass));
                        drinkResource.setStrInstructions(entity.instructions);
                        drinkResource.setStrDrinkThumb(entity.image);

                        List<String> entityIngredients = entity.ingredients.stream().collect(Collectors.toList());
                        try {
                            for (int i = 1; i <= entityIngredients.size(); i++) {
                                Field ingredientField = drinkResource.getClass().getDeclaredField("strIngredient" + i);
                                ingredientField.setAccessible(true);
                                ingredientField.set(drinkResource, entityIngredients.get(i));
                            }
                        } catch (NoSuchFieldException e) {
                            System.out.println("Field with name 'strIngredient..' was not found");
                        } catch (IllegalAccessException e) {
                            System.out.println("Value for ingredient could not be set");
                        }
                        return drinkResource;
                    }).collect(Collectors.toSet());
            cocktails.setDrinks(drinks);

            return cocktails;
        }

    }
}
