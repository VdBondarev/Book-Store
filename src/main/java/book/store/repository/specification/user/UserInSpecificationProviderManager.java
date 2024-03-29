package book.store.repository.specification.user;

import book.store.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInSpecificationProviderManager
        implements InSpecificationProviderManager<User, List<String>> {
    private final List<InSpecificationProvider<User, List<String>>> specificationProviders;

    @Override
    public InSpecificationProvider<User, List<String>> getSpecificationProvider(String key) {
        return specificationProviders
                .stream()
                .filter(provider -> provider.getKey().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find specification for key " + key));
    }
}
