package com.code44.finance.backend.endpoints.body;

import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.repackaged.org.codehaus.jackson.annotate.JsonIgnore;

public interface Body {
    @JsonIgnore void verifyRequiredFields() throws BadRequestException;
}
