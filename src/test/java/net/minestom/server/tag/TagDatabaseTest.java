package net.minestom.server.tag;

import org.jglrxavpok.hephaistos.nbt.NBT;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TagDatabaseTest {

    @Test
    public void empty() {
        TagDatabase db = createDB();
        var query = TagDatabase.Query.builder()
                .filter(TagDatabase.Filter.eq(Tag.String("key"), "value")).build();
        var result = db.find(query);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findFilterEq() {
        TagDatabase db = createDB();
        var tag = Tag.String("key");
        var compound1 = NBT.Compound(Map.of("key", NBT.String("value"),
                "other", NBT.String("otherValue")));
        var compound2 = NBT.Compound(Map.of("key", NBT.String("value2"),
                "other", NBT.String("otherValue")));

        db.updateSingle(tag, "value", TagHandler.fromCompound(compound1));
        db.updateSingle(tag, "value2", TagHandler.fromCompound(compound2));

        var query = TagDatabase.Query.builder()
                .filter(TagDatabase.Filter.eq(tag, "value")).build();
        assertEquals(List.of(compound1), db.find(query));
    }

    @Test
    public void findMultiEq() {
        TagDatabase db = createDB();
        var tag = Tag.String("key");
        var compound1 = NBT.Compound(Map.of("key", NBT.String("value"),
                "other", NBT.String("otherValue")));
        var compound2 = NBT.Compound(Map.of("key", NBT.String("value2"),
                "other", NBT.String("otherValue")));

        db.updateSingle(tag, "value", TagHandler.fromCompound(compound1));
        db.updateSingle(tag, "value2", TagHandler.fromCompound(compound2));

        var query = TagDatabase.Query.builder()
                .filter(TagDatabase.Filter.eq(Tag.String("other"), "otherValue")).build();
        assertEquals(List.of(compound1, compound2), db.find(query));
    }

    @Test
    public void findMultiTreeEq() {
        TagDatabase db = createDB();
        var tag = Tag.String("key");
        var compound1 = NBT.Compound(Map.of("key", NBT.String("value"),
                "path", NBT.Compound(Map.of("other", NBT.String("otherValue")))));
        var compound2 = NBT.Compound(Map.of("key", NBT.String("value2"),
                "path", NBT.Compound(Map.of("other", NBT.String("otherValue")))));

        db.updateSingle(tag, "value", TagHandler.fromCompound(compound1));
        db.updateSingle(tag, "value2", TagHandler.fromCompound(compound2));

        var query = TagDatabase.Query.builder()
                .filter(TagDatabase.Filter.eq(Tag.String("other").path("path"), "otherValue")).build();
        assertEquals(List.of(compound1, compound2), db.find(query));
    }

    @Test
    public void findFirst() {
        TagDatabase db = createDB();
        var tag = Tag.String("key");
        var compound = NBT.Compound(Map.of("key", NBT.String("value"),
                "key2", NBT.String("value2")));

        db.updateSingle(tag, "value", TagHandler.fromCompound(compound));

        var result = db.findFirst(tag, "value");
        assertEquals(compound, result.get());
    }

    private TagDatabase createDB() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
