package com.baeldung.common.persistence.service;

import com.baeldung.common.persistence.exception.MyEntityNotFoundException;
import com.baeldung.common.persistence.model.IEntity;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Transactional
public abstract class AbstractRawService<T extends IEntity> implements IRawService<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    public AbstractRawService() {
        super();
    }

    // API

    // find - one

    @Override
    @Transactional(readOnly = true)
    public T findOne(final long id) {
        return getDao().findById(id)
            .orElse(null);
    }

    // find - all

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return Lists.newArrayList(getDao().findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAllPaginatedAndSortedRaw(final int page, final int size, final String sortBy, final String sortOrder) {
        final Sort sortInfo = constructSort(sortBy, sortOrder);
        return getDao().findAll(PageRequest.of(page, size, sortInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllPaginatedAndSorted(final int page, final int size, final String sortBy, final String sortOrder) {
        final Sort sortInfo = constructSort(sortBy, sortOrder);
        final List<T> content = getDao().findAll(PageRequest.of(page, size, sortInfo))
            .getContent();
        if (content == null) {
            return Lists.newArrayList();
        }
        return content;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAllPaginatedRaw(final int page, final int size) {
        return getDao().findAll(PageRequest.of(page, size));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllPaginated(final int page, final int size) {
        final List<T> content = getDao().findAll(PageRequest.of(page, size, null))
            .getContent();
        if (content == null) {
            return Lists.newArrayList();
        }
        return content;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllSorted(final String sortBy, final String sortOrder) {
        final Sort sortInfo = constructSort(sortBy, sortOrder);
        return Lists.newArrayList(getDao().findAll(sortInfo));
    }

    // save/create/persist

    @Override
    public T create(final T entity) {
        Objects.requireNonNull(entity);

        final T persistedEntity = getDao().save(entity);

        return persistedEntity;
    }

    // update/merge

    @Override
    public void update(final T entity) {
        Objects.requireNonNull(entity);

        getDao().save(entity);
    }

    // delete

    @Override
    public void deleteAll() {
        getDao().deleteAll();
    }

    @Override
    public void delete(final long id) {
        final T entity = getDao().findById(id).orElseThrow(MyEntityNotFoundException::new);

        getDao().delete(entity);
    }

    // count

    @Override
    public long count() {
        return getDao().count();
    }

    // template method

    protected abstract PagingAndSortingRepository<T, Long> getDao();

    protected abstract JpaSpecificationExecutor<T> getSpecificationExecutor();

    // template

    protected final Sort constructSort(final String sortBy, final String sortOrder) {
        Sort sortInfo = Sort.unsorted();
        if (sortBy != null) {
            sortInfo = Sort.by(Direction.fromString(sortOrder), sortBy);
        }
        return sortInfo;
    }

}
