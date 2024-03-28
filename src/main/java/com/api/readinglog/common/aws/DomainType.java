package com.api.readinglog.common.aws;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DomainType {

    MEMBERS("members"), BOOK("books");

    private final String type;
}
