package org.javers.core.diff.changetype.map;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Predicate;
import org.javers.common.validation.Validate;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.javers.common.string.ToStringBuilder.addEnumField;

/**
 * @author bartosz walacik
 */
public class MapChange extends PropertyChange {
    private final List<EntryChange> changes;

    public MapChange(GlobalId affectedCdoId, String propertyName, List<EntryChange> changes) {
        super(affectedCdoId, propertyName);
        Validate.argumentIsNotNull(changes);
        Validate.argumentCheck(!changes.isEmpty(),"changes list should not be empty");
        this.changes = Collections.unmodifiableList(new ArrayList<>(changes));
    }

    public List<EntryChange> getEntryChanges() {
        return changes;
    }

    public List<EntryAdded> getEntryAddedChanges() {
        return filterChanges(EntryAdded.class);
    }

    public List<EntryRemoved> getEntryRemovedChanges() {
        return filterChanges(EntryRemoved.class);
    }

    public List<EntryValueChange> getEntryValueChanges() {
        return filterChanges(EntryValueChange.class);
    }

    private <T extends EntryChange> List<T> filterChanges(final Class<T> ofType) {
        return (List) Lists.positiveFilter(changes, new Predicate<EntryChange>() {
            public boolean apply(EntryChange input) {
                return ofType.isAssignableFrom(input.getClass());
            }
        });
    }

    @Override
    protected String fieldsToString() {
        StringBuilder changesAsString = new StringBuilder();

        for (EntryChange c : changes){
            if (changesAsString.length() > 0) { changesAsString.append(", "); }
            changesAsString.append(c);
        }
        return super.fieldsToString() + addEnumField("entryChanges", changesAsString);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MapChange) {
            MapChange that = (MapChange) obj;
            return super.equals(that)
                    && Objects.equals(this.changes, that.changes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.changes);
    }
}
