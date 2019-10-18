package com.ezgroceries.shoppinglist.cocktailapi.entity;

import com.ezgroceries.shoppinglist.cocktailapi.entity.utils.StringSetConverter;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "COCKTAIL")
public class CocktailEntity {

    @Id private UUID id;

    private String id_drink;
    private String name;

    @Convert(converter = StringSetConverter.class)
    private Set<String> ingredients;

}
