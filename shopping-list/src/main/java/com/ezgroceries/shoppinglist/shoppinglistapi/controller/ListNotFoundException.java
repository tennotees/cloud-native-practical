package com.ezgroceries.shoppinglist.shoppinglistapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "List not found")
public class ListNotFoundException extends RuntimeException {
}
