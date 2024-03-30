package book.store.telegram.strategy.response.impl;

import book.store.telegram.strategy.response.AdminResponseService;
import org.springframework.stereotype.Service;

@Service
public class HelpCommandResponseService implements AdminResponseService {
    private static final String HELP = "/help";

    @Override
    public String getMessage(String text) {
        return """
                ***
                This bot is for admins of this book store application only.
                
                It will send you notifications about main operations happening in the application.
                
                To get info about:
                1). A user
                2). A book
                3) A category
                
                Send me a message with the following format:
                
                Get info about a user with id: ...
                Get info about a book with id: ...
                Get info about a category with id: ...
                
                Instead of ... paste id.
                ***
                """;
    }

    @Override
    public boolean isApplicable(String text) {
        return text.equalsIgnoreCase(HELP);
    }
}
