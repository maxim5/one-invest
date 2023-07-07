package io.oneinvest.util;

import com.google.common.flogger.FluentLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Level;

public class Http {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();
    private static final String USER_AGENT = "Mozilla/5.0 (X11; CrOS x86_64 14541.0.0) AppleWebKit/537.36 " +
                                             "(KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";

    public static @NotNull String httpCall(@NotNull String url) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
            .get()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "text/html")
            .build();

        try (Response response = client.newCall(request).execute()) {
            log.at(Level.INFO).log("%s %s", response.request().url(), response.code());
            return response.body().string();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
