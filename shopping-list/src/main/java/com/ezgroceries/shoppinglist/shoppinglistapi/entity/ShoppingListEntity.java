package com.ezgroceries.shoppinglist.shoppinglistapi.entity;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SHOPPING_LIST")
public class ShoppingListEntity {

    @Id private UUID id;
    private String name;

}
