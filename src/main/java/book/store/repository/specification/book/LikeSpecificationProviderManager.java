package book.store.repository.specification.book;

public interface LikeSpecificationProviderManager<T, P> {
    LikeSpecificationProvider<T, P> getSpecificationProvider(String key);
}
