package com.ezgroceries.shoppinglist.cocktailapi.service;

import com.ezgroceries.shoppinglist.cocktailapi.db.CocktailResource;
import com.ezgroceries.shoppinglist.cocktailapi.entity.CocktailEntity;
import com.ezgroceries.shoppinglist.cocktailapi.repository.CocktailRepository;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CocktailServiceImpl implements CocktailService {

    private CocktailRepository cocktailRepository;

    @Autowired
    public void CocktailServiceImpl(CocktailRepository cocktailRepository) {
        this.cocktailRepository = cocktailRepository;
    }

    @Override
    public Set<CocktailResource> searchCocktailsNameContaining(String search) {

        Set<CocktailEntity> set = cocktailRepository.findByNameLike(search);
        return null;
    }
}
