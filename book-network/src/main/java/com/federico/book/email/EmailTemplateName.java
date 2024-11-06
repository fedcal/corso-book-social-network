package com.federico.book.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    //genera un bean con il template per la relativa mail
    ACTIVATE_ACCOUNT("activate_account");
    private final String name;

    private EmailTemplateName(String name) {
        this.name = name;
    }
}
