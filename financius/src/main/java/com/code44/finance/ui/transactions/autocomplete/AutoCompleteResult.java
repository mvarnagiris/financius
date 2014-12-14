package com.code44.finance.ui.transactions.autocomplete;

import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;

import java.util.List;

public final class AutoCompleteResult {
    private Long amount;
    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private List<Tag> tags;
    private String note;
    private List<Long> otherAmounts;
    private List<Account> otherAccountsFrom;
    private List<Account> otherAccountsTo;
    private List<Category> otherCategories;
    private List<Tag> otherTags;
    private List<String> otherNotes;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Long> getOtherAmounts() {
        return otherAmounts;
    }

    public void setOtherAmounts(List<Long> otherAmounts) {
        this.otherAmounts = otherAmounts;
    }

    public List<Account> getOtherAccountsFrom() {
        return otherAccountsFrom;
    }

    public void setOtherAccountsFrom(List<Account> otherAccountsFrom) {
        this.otherAccountsFrom = otherAccountsFrom;
    }

    public List<Account> getOtherAccountsTo() {
        return otherAccountsTo;
    }

    public void setOtherAccountsTo(List<Account> otherAccountsTo) {
        this.otherAccountsTo = otherAccountsTo;
    }

    public List<Category> getOtherCategories() {
        return otherCategories;
    }

    public void setOtherCategories(List<Category> otherCategories) {
        this.otherCategories = otherCategories;
    }

    public List<Tag> getOtherTags() {
        return otherTags;
    }

    public void setOtherTags(List<Tag> otherTags) {
        this.otherTags = otherTags;
    }

    public List<String> getOtherNotes() {
        return otherNotes;
    }

    public void setOtherNotes(List<String> otherNotes) {
        this.otherNotes = otherNotes;
    }
}
