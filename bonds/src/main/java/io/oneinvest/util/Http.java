package io.oneinvest.util;

import com.google.common.flogger.FluentLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Level;

public class Http {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();
    private static final String USER_AGENT = "Mozilla/5.0 (X11; CrOS x86_64 14541.0.0) AppleWebKit/537.36 " +
                                             "(KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";

    public static @NotNull String httpCall(@NotNull String url) {
        return httpCall(url, ResponseBody::string);
    }

    public static @NotNull String httpCall(@NotNull String url, @NotNull HttpCache cache, @NotNull HttpOptions options) {
        if (!options.cacheEnabled()) {
            return httpCall(url);
        }
        HttpCache.HttpEntry httpEntry = cache.get(url);
        if (httpEntry != null && httpEntry.isFresh(options.expireMillis())) {
            return httpEntry.contentAsString();
        }
        return httpCall(url, responseBody -> {
            HttpCache.HttpEntry value = HttpCache.HttpEntry.utf8(responseBody.bytes());
            cache.put(url, value);
            return value.contentAsString();
        });
    }

    private static <T> @NotNull T httpCall(@NotNull String url,
                                           @NotNull ThrowFunction<ResponseBody, T, IOException> onSuccess) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
            .get()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "text/html")
            .build();

        try (Response response = client.newCall(request).execute()) {
            log.at(Level.INFO).log("%s %s", response.request().url(), response.code());
            return onSuccess.apply(response.body());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public record HttpOptions(boolean cacheEnabled, long expireMillis) {
        public HttpOptions(long expireMillis) {
            this(true, expireMillis);
        }
    }
}
