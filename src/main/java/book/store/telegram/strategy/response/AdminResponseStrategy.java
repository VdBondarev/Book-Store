package book.store.telegram.strategy.response;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminResponseStrategy {
    private final List<AdminResponseService> responseServices;

    public AdminResponseService getResponseService(String text) {
        return responseServices
                .stream()
                .filter(service -> service.isApplicable(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Can't get a response for text " + text));
    }
}
