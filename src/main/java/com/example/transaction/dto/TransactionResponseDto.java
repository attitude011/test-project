package com.example.transaction.dto;

import java.util.List;

public class TransactionResponseDto {
    private int amount;
    private String store;
    private String currency;
    private List<UserDto> users;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(int amount, String store, String currency, List<UserDto> users) {
        this.amount = amount;
        this.store = store;
        this.currency = currency;
        this.users = users;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
}
