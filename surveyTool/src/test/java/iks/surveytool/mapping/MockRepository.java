package iks.surveytool.mapping;

import iks.surveytool.entities.AbstractEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MockRepository<T, ID> implements JpaRepository<T, ID> {
    public static class MockAbstractEntityRepository<T extends AbstractEntity> extends MockRepository<T, Long> {
        public MockAbstractEntityRepository(Supplier<T> constructor) {
            super(id -> {
                T entity = constructor.get();
                entity.setId(id);
                return entity;
            });
        }
    }
    private final Function<ID, T> mocker;

    public MockRepository(Function<ID, T> mocker) {
        this.mocker = mocker;
    }

    @Override
    @NonNull
    public List<T> findAll() {
        return List.of();
    }

    @Override
    @NonNull
    public List<T> findAll(@NonNull Sort sort) {
        return List.of();
    }

    @Override
    @NonNull
    public List<T> findAllById(Iterable<ID> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
                .map(mocker)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public void flush() {

    }

    @Override
    @NonNull
    public <S extends T> S saveAndFlush(@NonNull S entity) {
        return entity;
    }

    @Override
    @NonNull
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllInBatch(@NonNull Iterable<T> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(@NonNull Iterable<ID> ids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    @NonNull
    public T getOne(@NonNull ID id) {
        return mocker.apply(id);
    }

    @Override
    @NonNull
    public T getById(@NonNull ID id) {
        return mocker.apply(id);
    }

    @Override
    @NonNull
    public <S extends T> List<S> findAll(@NonNull Example<S> example) {
        return List.of();
    }

    @Override
    @NonNull
    public <S extends T> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
        return List.of();
    }

    @Override
    @NonNull
    public Page<T> findAll(@NonNull Pageable pageable) {
        return Page.empty(pageable);
    }

    @Override
    @NonNull
    public <S extends T> S save(@NonNull S entity) {
        return entity;
    }

    @Override
    @NonNull
    public Optional<T> findById(@NonNull ID id) {
        return Optional.of(mocker.apply(id));
    }

    @Override
    public boolean existsById(@NonNull ID id) {
        return true;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(@NonNull ID id) {

    }

    @Override
    public void delete(@NonNull T entity) {

    }

    @Override
    public void deleteAllById(@NonNull Iterable<? extends ID> ids) {

    }

    @Override
    public void deleteAll(@NonNull Iterable<? extends T> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    @NonNull
    public <S extends T> Optional<S> findOne(@NonNull Example<S> example) {
        return Optional.empty();
    }

    @Override
    @NonNull
    public <S extends T> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
        return Page.empty(pageable);
    }

    @Override
    public <S extends T> long count(@NonNull Example<S> example) {
        return 0;
    }

    @Override
    public <S extends T> boolean exists(@NonNull Example<S> example) {
        return false;
    }
}
