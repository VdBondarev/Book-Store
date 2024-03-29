package book.store.repository.specification.user;

public interface InSpecificationProviderManager<T, P> {
    InSpecificationProvider<T, P> getSpecificationProvider(String key);
}
