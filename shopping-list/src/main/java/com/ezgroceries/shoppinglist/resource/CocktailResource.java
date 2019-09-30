package com.ezgroceries.shoppinglist.resource;

import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;

@Resource
public class CocktailResource {

    private UUID uuid;
    private String name;
    private String serving;
    private String instructions;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    private String pictureUrl;
    private List<String> ingredients;

    public CocktailResource(UUID uuid, String name, String serving, String instructions, String pictureUrl, List<String> ingredients) {
        this.uuid = uuid;
        this.name = name;
        this.serving = serving;
        this.instructions = instructions;
        this.pictureUrl = pictureUrl;
        this.ingredients = ingredients;
    }

}
