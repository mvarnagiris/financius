package com.code44.finance.backend.endpoints;

import com.code44.finance.common.Constants;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

@Api(name = "financius",
        version = "v1",
        namespace = @ApiNamespace(ownerDomain = "backend.finance.code44.com", ownerName = "backend.finance.code44.com", packagePath = ""),
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.DEBUG_ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE})
public class Endpoint {
}
