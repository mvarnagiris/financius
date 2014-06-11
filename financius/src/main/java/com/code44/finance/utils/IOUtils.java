package com.code44.finance.utils;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import retrofit.client.Response;

public class IOUtils {
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
        }
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static JsonObject readJsonObject(Response response) throws IOException {
        InputStream in = response.getBody().in();
        JsonObject json = IOUtils.toJsonObject(in);
        IOUtils.closeQuietly(in);

        return json;
    }

    public static JsonArray readJsonArray(Response response) throws IOException {
        InputStream in = response.getBody().in();
        JsonArray json = IOUtils.toJsonArray(in);
        IOUtils.closeQuietly(in);

        return json;
    }

    public static JsonObject toJsonObject(InputStream in) {
        InputStreamReader reader = new InputStreamReader(in);
        Gson gson = new Gson();
        return gson.fromJson(reader, JsonElement.class).getAsJsonObject();
    }

    public static JsonArray toJsonArray(InputStream in) {
        InputStreamReader reader = new InputStreamReader(in);
        Gson gson = new Gson();
        return gson.fromJson(reader, JsonElement.class).getAsJsonArray();
    }

    public static String toString(InputStream inputStream) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str;

        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }

        return buffer.toString();
    }
}
