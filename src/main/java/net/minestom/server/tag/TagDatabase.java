package net.minestom.server.tag;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.List;
import java.util.Optional;

@ApiStatus.Experimental
public interface TagDatabase {

    void update(@NotNull Query query, @NotNull TagHandler handler);

    @NotNull List<@NotNull NBTCompound> find(@NotNull Query query);

    default <T> void updateSingle(@NotNull Tag<T> tag, @NotNull T value, @NotNull TagHandler handler) {
        final Query query = Query.of(List.of(Filter.eq(tag, value)), List.of(), 1);
        update(query, handler);
    }

    default @NotNull List<@NotNull NBTCompound> find(@NotNull List<@NotNull Filter> queries) {
        final Query query = Query.of(queries, List.of(), Integer.MAX_VALUE);
        return find(query);
    }

    default Optional<NBTCompound> findFirst(@NotNull List<@NotNull Filter> queries) {
        final Query query = Query.of(queries, List.of(), 1);
        return find(query).stream().findFirst();
    }

    default <T> @NotNull Optional<@NotNull NBTCompound> findFirst(@NotNull Tag<T> tag, @NotNull T value) {
        return findFirst(List.of(Filter.eq(tag, value)));
    }

    sealed interface Query permits TagDatabaseImpl.Query {
        static Query of(@NotNull List<@NotNull Filter> filters, @NotNull List<@NotNull Sorter> sorters, int limit) {
            return new TagDatabaseImpl.Query(filters, sorters, limit);
        }

        @NotNull List<@NotNull Filter> filters();

        @NotNull List<@NotNull Sorter> sorters();

        int limit();
    }

    sealed interface Filter permits Filter.Eq {
        static <T> Filter eq(@NotNull Tag<T> tag, @NotNull T value) {
            return new TagDatabaseImpl.Filter.Eq<>(tag, value);
        }

        sealed interface Eq<T> extends Filter permits TagDatabaseImpl.Filter.Eq {
            @NotNull Tag<T> tag();

            @NotNull T value();
        }
    }

    interface Sorter {
        @NotNull Tag<?> tag();

        @NotNull SortOrder sortOrder();
    }

    enum SortOrder {
        ASCENDING,
        DESCENDING
    }
}
