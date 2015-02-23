package ru.spbu.astro.commons;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * User: amosov-f
 * Date: 17.01.15
 * Time: 2:16
 */
public final class Catalogs {
    public static final Catalog HIPPARCOS_1997 = Catalog.read(Catalog.class.getResourceAsStream("/catalogue/hipparcos1997.txt"));
    public static final Catalog HIPPARCOS_2007 = innerJoin(Catalog.read(Catalog.class.getResourceAsStream("/catalogue/hipparcos2007.txt")), HIPPARCOS_1997);

    private Catalogs() {
    }

    @NotNull
    public static Catalog innerJoin(@NotNull final Catalog catalog1, @NotNull final Catalog catalog2) {
        final Catalog result = new Catalog();
        for (final int id : Sets.intersection(catalog1.id2row.keySet(), catalog2.id2row.keySet())) {
            final Map<Catalog.Parameter<?>, Object> values = new HashMap<>(catalog2.id2row.get(id).values);
            values.putAll(catalog1.id2row.get(id).values);
            result.add(new Catalog.Row(id, values));
        }
        return result;
    }

    @NotNull
    public static Catalog update(@NotNull final Catalog catalog, @NotNull final Function<Star, Star> processor) {
        final Catalog updatedCatalog = new Catalog();
        for (final Catalog.Row row : catalog.id2row.values()) {
            final Star star = row.toStar();
            if (star != null) {
                updatedCatalog.add(new Catalog.Row(processor.apply(star)));
            }
        }
        return updatedCatalog;
    }
}