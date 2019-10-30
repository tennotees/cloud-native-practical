package com.ezgroceries.shoppinglist.cocktailapi.entity;

import com.ezgroceries.shoppinglist.cocktailapi.entity.utils.StringSetConverter;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "COCKTAIL")
public class CocktailEntity {

    @Id public UUID id;
    @Column(name = "ID_DRINK")
    public String iddrink;
    public String name;
    public String glass;
    public String instructions;
    public String image;
    @Convert(converter = StringSetConverter.class)
    public Set<String> ingredients;

    public  CocktailEntity() {}

    public CocktailEntity(String id_drink, String name, String glass, String instructions, String image, Set<String> ingredients) {
        this.id = UUID.randomUUID();
        this.iddrink = id_drink;
        this.name = name;
        this.glass = glass;
        this.instructions = instructions;
        this.image = image;
        this.ingredients = ingredients;
    }

}
