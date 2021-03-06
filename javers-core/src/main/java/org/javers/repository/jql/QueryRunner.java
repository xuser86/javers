package org.javers.repository.jql;

import org.javers.common.collections.Function;
import org.javers.common.collections.Optional;
import org.javers.common.collections.Sets;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.List;
import java.util.Set;

/**
 * Adapter from a JqlQuery to JaversRepository API
 *
 * @author bartosz.walacik
 */
public class QueryRunner {
    private final JaversExtendedRepository repository;
    private final GlobalIdFactory globalIdFactory;
    private final TypeMapper typeMapper;

    public QueryRunner(JaversExtendedRepository repository, GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
        this.repository = repository;
        this.globalIdFactory = globalIdFactory;
        this.typeMapper = typeMapper;
    }

    public Optional<CdoSnapshot> runQueryForLatestSnapshot(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        return repository.getLatest(fromDto(globalId));
    }

    public List<CdoSnapshot> queryForSnapshots(JqlQuery query){
        Validate.argumentIsNotNull(query);

        if (query.isAnyDomainObjectQuery()) {
            return repository.getSnapshots(query.getQueryParams());
        }

        if (query.isIdQuery()){
            return repository.getStateHistory(fromDto(query.getIdFilter()), query.getQueryParams());
        }

        if (query.isInstanceQuery()){
            return repository.getStateHistory(fromInstance(query.getInstanceFilter()), query.getQueryParams());
        }

        if (query.isClassQuery()){
            Set<ManagedType> mTypes = getJaversManagedTypes(query.getClassFilter());
            return repository.getStateHistory(mTypes, query.getQueryParams());
        }

        if (query.isVoOwnerQuery()) {
            VoOwnerFilter filter = query.getVoOwnerFilter();
            EntityType ownerEntity = getOwnerEntity(filter);
            globalIdFactory.touchValueObjectFromPath(ownerEntity, filter.getPath());
            return repository.getValueObjectStateHistory(ownerEntity, filter.getPath(), query.getQueryParams());
        }

        throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "queryForSnapshots: " + query + " is not supported");
    }

    public List<Change> queryForChanges(JqlQuery query) {
        Validate.argumentIsNotNull(query);

        if (query.isAnyDomainObjectQuery()) {
            return repository.getChanges(query.isNewObjectChanges(), query.getQueryParams());
        }

        if (query.isIdQuery()){
            return repository.getChangeHistory(fromDto(query.getIdFilter()), query.getQueryParams());
        }

        if (query.isInstanceQuery()){
            return repository.getChangeHistory(fromInstance(query.getInstanceFilter()), query.getQueryParams());
        }

        if (query.isClassQuery()){
            Set<ManagedType> mTypes = getJaversManagedTypes(query.getClassFilter());
            return repository.getChangeHistory(mTypes, query.getQueryParams());
        }

        if (query.isVoOwnerQuery()) {
            VoOwnerFilter filter = query.getVoOwnerFilter();
            EntityType ownerEntity = getOwnerEntity(filter);
            globalIdFactory.touchValueObjectFromPath(ownerEntity, filter.getPath());
            return repository.getValueObjectChangeHistory(
                    ownerEntity, filter.getPath(), query.getQueryParams());
        }

        throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "queryForChanges: " + query + " is not supported");
    }

    private Set<ManagedType> getJaversManagedTypes(Set<Class> classes) {
        return Sets.transform(classes, new Function<Class, ManagedType>() {
            @Override
            public ManagedType apply(Class javaClass) {
                return typeMapper.getJaversManagedType(javaClass);
            }
        });
    }

    private GlobalId fromDto(GlobalIdDTO globalIdDTO) {
        return globalIdFactory.createFromDto(globalIdDTO);
    }

    private GlobalId fromInstance(Object instance) {
        return globalIdFactory.createInstanceId(instance);
    }

    private EntityType getOwnerEntity(VoOwnerFilter filter){
        ManagedType mType = typeMapper.getJaversManagedType(filter.getOwnerEntityClass());

        if (! (mType instanceof EntityType)) {
            throw new JaversException(
                    JaversExceptionCode.MALFORMED_JQL, "queryForChanges: ownerEntityClass {'"+filter.getOwnerEntityClass().getName()+"'} should be an Entity");
        }

        return  ((EntityType) mType);
    }
}
