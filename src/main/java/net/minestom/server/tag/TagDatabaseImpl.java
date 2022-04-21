package net.minestom.server.tag;

import java.util.List;

final class TagDatabaseImpl {
    record Query(List<TagDatabase.Filter> filters,
                 List<TagDatabase.Sorter> sorters,
                 int limit)
            implements TagDatabase.Query {
        Query {
            filters = List.copyOf(filters);
            sorters = List.copyOf(sorters);
        }
    }

    static final class Filter {
        record Eq<T>(Tag<T> tag, T value) implements TagDatabase.Filter.Eq<T> {
        }
    }
}
