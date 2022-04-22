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

    @Test
    public void replaceConstant() {
        TagDatabase db = createDB();
        var tag = Tag.Integer("number");
        var compound = NBT.Compound(Map.of("number", NBT.Int(5)));

        db.insert(TagHandler.fromCompound(compound));
        db.replaceConstant(TagDatabase.Query.ALL, tag, 10);

        var result = db.find(TagDatabase.Query.ALL);
        assertEquals(1, result.size());
        assertEquals(NBT.Compound(Map.of("number", NBT.Int(10))), result.get(0));
    }

    @Test
    public void replaceNull() {
        TagDatabase db = createDB();
        var tag = Tag.Integer("number");
        var compound = NBT.Compound(Map.of("number", NBT.Int(5)));

        db.insert(TagHandler.fromCompound(compound));
        db.replaceConstant(TagDatabase.Query.ALL, tag, null);
        // Empty handlers must be removed
        var result = db.find(TagDatabase.Query.ALL);
        assertTrue(result.isEmpty());
    }

    @Test
    public void replaceOperator() {
        TagDatabase db = createDB();
        var tag = Tag.Integer("number");
        var compound = NBT.Compound(Map.of("number", NBT.Int(5)));

        db.insert(TagHandler.fromCompound(compound));
        db.replace(TagDatabase.Query.ALL, tag, integer -> integer * 2);

        var result = db.find(TagDatabase.Query.ALL);
        assertEquals(1, result.size());
        assertEquals(NBT.Compound(Map.of("number", NBT.Int(10))), result.get(0));
    }

    @Test
    public void delete() {
        TagDatabase db = createDB();
        var tag = Tag.Integer("number");
        var compound = NBT.Compound(Map.of("number", NBT.Int(5)));
        var query = TagDatabase.Query.builder().filter(TagDatabase.Filter.eq(tag, 5)).build();

        db.insert(TagHandler.fromCompound(compound));
        db.delete(query);

        var result = db.find(query);
        assertTrue(result.isEmpty());
    }

    private TagDatabase createDB() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
