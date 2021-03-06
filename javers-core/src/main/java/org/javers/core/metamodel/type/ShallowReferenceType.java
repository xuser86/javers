package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.property.Property;

/**
 * @see org.javers.core.metamodel.annotation.ShallowReference
 * @author bartosz.walacik
 */
public class ShallowReferenceType extends EntityType {
    ShallowReferenceType(ManagedClass entity, Property idProperty, Optional<String> typeName) {
        super(entity.createShallowReference(), idProperty, typeName);
    }

    @Override
    EntityType spawn(ManagedClass managedClass, Optional<String> typeName) {
        return new ShallowReferenceType(managedClass, getIdProperty(), typeName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ShallowReferenceType)) {return false;}

        ShallowReferenceType that = (ShallowReferenceType) o;
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
