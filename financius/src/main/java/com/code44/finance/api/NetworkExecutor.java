package com.code44.finance.api;

public interface NetworkExecutor {
    public boolean isWorking(Request request);

    public void execute(Request request);
}
