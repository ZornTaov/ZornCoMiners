package org.zornco.miners.common.multiblock.pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MultiBlockPatternBuilder {
    private static final Joiner COMMA_JOINED = Joiner.on(",");
    private final List<String[]> pattern = Lists.newArrayList();
    private final Map<Character, Predicate<MultiBlockInWorld>> lookup = Maps.newHashMap();
    private int height;
    private int width;

    private MultiBlockPatternBuilder() {
        this.where(' ', MultiBlockInWorld.hasState(MultiBlockInWorldType.NOT_INCLUDED, (state -> {return true;})));
    }

    /**
     * Adds a single aisle to this pattern, going in the z axis. (so multiple calls to this will increase the z-size by
     * 1)
     */
    public MultiBlockPatternBuilder aisle(String... pAisle) {
        if (!ArrayUtils.isEmpty((Object[])pAisle) && !StringUtils.isEmpty(pAisle[0])) {
            if (this.pattern.isEmpty()) {
                this.height = pAisle.length;
                this.width = pAisle[0].length();
            }

            if (pAisle.length != this.height) {
                throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + pAisle.length + ")");
            } else {
                for(String s : pAisle) {
                    if (s.length() != this.width) {
                        throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + s.length() + ")");
                    }

                    for(char c0 : s.toCharArray()) {
                        if (!this.lookup.containsKey(c0)) {
                            this.lookup.put(c0, (Predicate<MultiBlockInWorld>)null);
                        }
                    }
                }

                this.pattern.add(pAisle);
                return this;
            }
        } else {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
    }

    public static MultiBlockPatternBuilder start() {
        return new MultiBlockPatternBuilder();
    }

    public MultiBlockPatternBuilder where(char pSymbol, Predicate<MultiBlockInWorld> pBlockMatcher) {
        this.lookup.put(pSymbol, pBlockMatcher);
        return this;
    }

    public MultiBlockPattern build() {
        return new MultiBlockPattern(this.createPattern());
    }

    private Predicate<MultiBlockInWorld>[][][] createPattern() {
        this.ensureAllCharactersMatched();
        Predicate<MultiBlockInWorld>[][][] predicate = (Predicate[][][]) Array.newInstance(Predicate.class, this.pattern.size(), this.height, this.width);

        for(int i = 0; i < this.pattern.size(); ++i) {
            for(int j = 0; j < this.height; ++j) {
                for(int k = 0; k < this.width; ++k) {
                    predicate[i][j][k] = this.lookup.get((this.pattern.get(i))[j].charAt(k));
                }
            }
        }

        return predicate;
    }

    private void ensureAllCharactersMatched() {
        List<Character> list = Lists.newArrayList();

        for(Map.Entry<Character, Predicate<MultiBlockInWorld>> entry : this.lookup.entrySet()) {
            if (entry.getValue() == null) {
                list.add(entry.getKey());
            }
        }

        if (!list.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + COMMA_JOINED.join(list) + " are missing");
        }
    }
}
