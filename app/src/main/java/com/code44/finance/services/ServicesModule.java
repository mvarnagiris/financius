package com.code44.finance.services;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {StartupService.class, SyncService.class, LogoutService.class})
public class ServicesModule {
}
