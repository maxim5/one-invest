package io.oneinvest.util;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rocksdb.FlushOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class HttpCache implements AutoCloseable {
    private static final String CACHE_FILE = DevPaths.STORAGE_HOME + "http-cache-%s.rocksdb";

    private final RocksDB db;

    public HttpCache(@NotNull String name) {
        File cacheFile = Paths.get(CACHE_FILE.formatted(name)).toFile();
        if (!cacheFile.getParentFile().exists()) {
            boolean mkdir = cacheFile.getParentFile().mkdir();
            assert mkdir;
        }
        try {
            Options rocksOptions = new Options().setCreateIfMissing(true).setParanoidChecks(false);
            db = RocksDB.open(rocksOptions, cacheFile.getPath());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(@NotNull String key, @NotNull HttpEntry value) {
        try {
            db.put(toBytes(key), toBytes(value));
            db.flush(new FlushOptions());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable HttpEntry get(@NotNull String key) {
        try {
            byte[] value = db.get(toBytes(key));
            return value != null ? fromBytes(value) : null;
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        db.close();
    }

    public record HttpEntry(byte @NotNull[] content,
                            @NotNull String charset,
                            long timestamp) {
        public static @NotNull HttpEntry utf8(@NotNull String content) {
            return new HttpEntry(content.getBytes(StandardCharsets.UTF_8), "", System.currentTimeMillis());
        }

        public static @NotNull HttpEntry utf8(byte @NotNull[] content) {
            return new HttpEntry(content, "", System.currentTimeMillis());
        }

        public @NotNull String contentAsString() {
            try {
                return charset.isEmpty() ? new String(content) : new String(content, charset);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean isFresh(long expireMillis) {
            return System.currentTimeMillis() - timestamp < expireMillis;
        }

        @Override
        public String toString() {
            return "HttpEntry{content=%s, charset=%s, timestamp=%tF %tT}"
                .formatted(contentAsString(), charset, timestamp, timestamp);
        }

        int byteSize() {
            return 4 + content.length + 4 + charset.length() + 8;
        }
    }

    // FIX[later]: publish "happy" utils and reuse below

    private byte @NotNull [] toBytes(@NotNull String key) {
        return key.getBytes();
    }

    private byte @NotNull[] toBytes(@NotNull HttpEntry httpEntry) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(httpEntry.byteSize())) {
            writeByteArray(httpEntry.content, output);
            writeString(httpEntry.charset, StandardCharsets.UTF_8, output);
            writeLong64(httpEntry.timestamp, output);
            return output.toByteArray();
        } catch (IOException impossible) {
            throw new UncheckedIOException(impossible);
        }
    }

    private @NotNull HttpEntry fromBytes(byte @NotNull[] bytes) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            byte[] content = readByteArray(input);
            String charset = readString(input, StandardCharsets.UTF_8);
            long timestamp = readLong64(input);
            return new HttpEntry(content, charset, timestamp);
        } catch (IOException impossible) {
            throw new UncheckedIOException(impossible);
        }
    }

    public static int writeInt32(int value, @NotNull OutputStream output) throws IOException {
        output.write(Ints.toByteArray(value));
        return Integer.BYTES;
    }

    public static int readInt32(@NotNull InputStream input) throws IOException {
        return Ints.fromByteArray(input.readNBytes(Integer.BYTES));
    }

    public static int writeLong64(long value, @NotNull OutputStream output) throws IOException {
        output.write(Longs.toByteArray(value));
        return Long.BYTES;
    }

    public static long readLong64(@NotNull InputStream input) throws IOException {
        return Longs.fromByteArray(input.readNBytes(Long.BYTES));
    }

    public static int writeByteArray(byte @NotNull [] value, @NotNull OutputStream output) throws IOException {
        int size = writeInt32(value.length, output);
        output.write(value);
        return size + value.length;
    }

    public static byte @NotNull [] readByteArray(@NotNull InputStream input) throws IOException {
        int length = readInt32(input);
        return input.readNBytes(length);
    }

    public static int writeString(@NotNull String value, @NotNull Charset charset, @NotNull OutputStream output) throws IOException {
        return writeByteArray(value.getBytes(charset), output);
    }

    public static @NotNull String readString(@NotNull InputStream input, @NotNull Charset charset) throws IOException {
        byte[] bytes = readByteArray(input);
        return new String(bytes, charset);
    }
}
