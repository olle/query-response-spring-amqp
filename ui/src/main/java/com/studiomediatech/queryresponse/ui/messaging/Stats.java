package com.studiomediatech.queryresponse.ui.messaging;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Stats(Collection<Stat> elements) {
}
