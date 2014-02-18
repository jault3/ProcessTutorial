package edu.msoe.tutorial.process.testing.google;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tests a collection of objects according to the rules specified in a {@link
 * RelationshipAssertion}.
 *
 * @author Gregory Kick
 */
@GwtCompatible
final class RelationshipTester<T> {

    private final List<ImmutableList<T>> groups = Lists.newArrayList();
    private final RelationshipAssertion<T> assertion;
    private final ItemReporter itemReporter;

    RelationshipTester(RelationshipAssertion<T> assertion, ItemReporter itemReporter) {
        this.assertion = checkNotNull(assertion);
        this.itemReporter = checkNotNull(itemReporter);
    }

    RelationshipTester(RelationshipAssertion<T> assertion) {
        this(assertion, new ItemReporter());
    }

    public RelationshipTester<T> addRelatedGroup(Iterable<? extends T> group) {
        groups.add(ImmutableList.copyOf(group));
        return this;
    }

    public void test() {
        for (int groupNumber = 0; groupNumber < groups.size(); groupNumber++) {
            ImmutableList<T> group = groups.get(groupNumber);
            for (int itemNumber = 0; itemNumber < group.size(); itemNumber++) {
                // check related items in same group
                for (int relatedItemNumber =
                             0; relatedItemNumber < group.size(); relatedItemNumber++) {
                    if (itemNumber != relatedItemNumber) {
                        assertRelated(groupNumber, itemNumber, relatedItemNumber);
                    }
                }
                // check unrelated items in all other groups
                for (int unrelatedGroupNumber =
                             0; unrelatedGroupNumber < groups.size(); unrelatedGroupNumber++) {
                    if (groupNumber != unrelatedGroupNumber) {
                        ImmutableList<T> unrelatedGroup = groups.get(unrelatedGroupNumber);
                        for (int unrelatedItemNumber =
                                     0; unrelatedItemNumber < unrelatedGroup.size();
                             unrelatedItemNumber++) {
                            assertUnrelated(groupNumber, itemNumber, unrelatedGroupNumber,
                                    unrelatedItemNumber);
                        }
                    }
                }
            }
        }
    }

    private void assertRelated(int groupNumber, int itemNumber, int relatedItemNumber) {
        ImmutableList<T> group = groups.get(groupNumber);
        T item = group.get(itemNumber);
        T related = group.get(relatedItemNumber);
        try {
            assertion.assertRelated(item, related);
        } catch (AssertionError e) {
            // TODO(gak): special handling for ComparisonFailure?
            throw new AssertionError(e.getMessage().replace("$ITEM",
                    itemReporter.reportItem(new Item(item, groupNumber, itemNumber))).replace(
                    "$RELATED",
                    itemReporter.reportItem(new Item(related, groupNumber, relatedItemNumber))));
        }
    }

    private void assertUnrelated(int groupNumber, int itemNumber, int unrelatedGroupNumber,
            int unrelatedItemNumber) {
        T item = groups.get(groupNumber).get(itemNumber);
        T unrelated = groups.get(unrelatedGroupNumber).get(unrelatedItemNumber);
        try {
            assertion.assertUnrelated(item, unrelated);
        } catch (AssertionError e) {
            // TODO(gak): special handling for ComparisonFailure?
            throw new AssertionError(e.getMessage().replace("$ITEM",
                    itemReporter.reportItem(new Item(item, groupNumber, itemNumber))).replace(
                    "$UNRELATED", itemReporter.reportItem(
                    new Item(unrelated, unrelatedGroupNumber, unrelatedItemNumber))));
        }
    }

    static class ItemReporter {
        String reportItem(Item item) {
            return item.toString();
        }
    }

    static final class Item {
        final Object value;
        final int groupNumber;
        final int itemNumber;

        Item(Object value, int groupNumber, int itemNumber) {
            this.value = value;
            this.groupNumber = groupNumber;
            this.itemNumber = itemNumber;
        }

        @Override
        public String toString() {
            return new StringBuilder().append(value).append(" [group ").append(
                    groupNumber + 1).append(", item ").append(itemNumber + 1).append(
                    ']').toString();
        }
    }

    /**
     * A strategy for testing the relationship between objects.  Methods are expected to throw
     * AssertionFailedError whenever the relationship is violated.
     * <p/>
     * <p>As a convenience, any occurrence of {@code $ITEM}, {@code $RELATED} or {@code $UNRELATED}
     * in the error message will be replaced with a string that combines the {@link
     * Object#toString()}, item number and group number of the respective item.
     */
    abstract static class RelationshipAssertion<T> {
        abstract void assertRelated(T item, T related);

        abstract void assertUnrelated(T item, T unrelated);
    }
}
