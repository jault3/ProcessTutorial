package edu.msoe.tutorial.process.testing.google;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.testng.Assert;

import edu.msoe.tutorial.process.testing.google.RelationshipTester.RelationshipAssertion;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tester for equals() and hashCode() methods of a class.
 * <p/>
 * <p>To use, create a new EqualsTester and add equality groups where each group contains objects
 * that are supposed to be equal to each other, and objects of different groups are expected to be
 * unequal. For example:
 * <pre>
 * new EqualsTester()
 *     .addEqualityGroup("hello", "h" + "ello")
 *     .addEqualityGroup("world", "wor" + "ld")
 *     .addEqualityGroup(2, 1 + 1)
 *     .testEquals();
 * </pre>
 * This tests: <ul> <li>comparing each object against itself returns true <li>comparing each object
 * against null returns false <li>comparing each object against an instance of an incompatible class
 * returns false <li>comparing each pair of objects within the same equality group returns true
 * <li>comparing each pair of objects from different equality groups returns false <li>the hash
 * codes of any two equal objects are equal </ul>
 * <p/>
 * <p>When a test fails, the error message labels the objects involved in the failed comparison as
 * follows: <ul> <li>"{@code [group }<i>i</i>{@code , item }<i>j</i>{@code ]}" refers to the
 * <i>j</i><sup>th</sup> item in the <i>i</i><sup>th</sup> equality group, where both equality
 * groups and the items within equality groups are numbered starting from 1.  When either a
 * constructor argument or an equal object is provided, that becomes group 1. </ul>
 *
 * @author Jim McMaster
 * @author Jige Yu
 * @since 10.0
 */
@Beta
@GwtCompatible
public final class EqualsTester {
    private static final int REPETITIONS = 3;
    private final List<List<Object>> equalityGroups = Lists.newArrayList();
    private RelationshipTester.ItemReporter itemReporter = new RelationshipTester.ItemReporter();

    /**
     * Constructs an empty EqualsTester instance.
     */
    public EqualsTester() {
    }

    /**
     * Adds {@code equalityGroup} with objects that are supposed to be equal to each other and not
     * equal to any other equality groups added to this tester.
     *
     * @param equalityGroup the group of equal objects
     * @return this EqualsTester
     */
    public EqualsTester addEqualityGroup(Object... equalityGroup) {
        checkNotNull(equalityGroup);
        equalityGroups.add(ImmutableList.copyOf(equalityGroup));
        return this;
    }

    /**
     * Run tests on equals method, throwing a failure on an invalid test.
     *
     * @return this EqualsTester
     */
    public EqualsTester testEquals() {
        RelationshipTester<Object> delegate =
                new RelationshipTester<Object>(new RelationshipAssertion<Object>() {
                    @Override
                    public void assertRelated(Object item, Object related) {
                        Assert.assertEquals(related, item, "$ITEM must be equal to $RELATED");
                        int itemHash = item.hashCode();
                        int relatedHash = related.hashCode();
                        Assert.assertEquals(relatedHash, itemHash,
                                "the hash (" + itemHash + ") of $ITEM must be equal to the hash ("
                                        + relatedHash + ") of $RELATED");
                    }

                    @Override
                    public void assertUnrelated(Object item, Object unrelated) {
                        // TODO(cpovirk): should this implementation (and
                        // RelationshipAssertions in general) accept null inputs?
                        Assert.assertTrue(!Objects.equal(item, unrelated),
                                "$ITEM must be unequal to $UNRELATED");
                    }
                }, itemReporter);
        for (List<Object> group : equalityGroups) {
            delegate.addRelatedGroup(group);
        }
        for (int run = 0; run < REPETITIONS; run++) {
            testItems();
            delegate.test();
        }
        return this;
    }

    EqualsTester setItemReporter(RelationshipTester.ItemReporter reporter) {
        this.itemReporter = checkNotNull(reporter);
        return this;
    }

    private void testItems() {
        for (Object item : Iterables.concat(equalityGroups)) {
            Assert.assertTrue(item != null, item + " must be unequal to null");
            Assert.assertTrue(!item.equals(NotAnInstance.EQUAL_TO_NOTHING),
                    item + " must be unequal to an arbitrary object of another class");
            Assert.assertEquals(item, item, item + " must be equal to itself");
            Assert.assertEquals(item.hashCode(), item.hashCode(),
                    "the hash of " + item + " must be "
                            + "consistent");
        }
    }

    /**
     * Class used to test whether equals() correctly handles an instance of an incompatible class.
     * Since it is a private inner class, the invoker can never pass in an instance to the tester
     */
    private enum NotAnInstance {
        EQUAL_TO_NOTHING;
    }
}
