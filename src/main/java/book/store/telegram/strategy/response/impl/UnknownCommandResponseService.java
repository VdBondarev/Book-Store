package book.store.telegram.strategy.response.impl;

import book.store.telegram.strategy.response.AdminResponseService;
import org.springframework.stereotype.Service;

@Service
public class UnknownCommandResponseService implements AdminResponseService {
    private static final String USER_REGEX =
            "^(?i)Get info about a user with id:\\s*\\d+$";
    private static final String START = "/start";
    private static final String HELP = "/help";

    @Override
    public String getMessage(String text) {
        return String.format("Unknown command: '%s'"
                + System.lineSeparator()
                + "Maybe you meant to type /start ?", text);
    }

    @Override
    public boolean isApplicable(String text) {
        return !text.equalsIgnoreCase(START)
                && !text.equalsIgnoreCase(HELP)
                && !text.matches(USER_REGEX);
    }
}
