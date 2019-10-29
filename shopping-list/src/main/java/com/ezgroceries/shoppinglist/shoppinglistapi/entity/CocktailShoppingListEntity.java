package com.ezgroceries.shoppinglist.shoppinglistapi.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity @IdClass(CocktailShoppingListId.class)
@Table(name = "COCKTAIL_SHOPPING_LIST")
public class CocktailShoppingListEntity {

    @Id
    @Column(name = "COCKTAIL_ID")
    public UUID cocktailId;

    @Id
    @Column(name = "SHOPPING_LIST_ID")
    public UUID shoppingListId;

}
