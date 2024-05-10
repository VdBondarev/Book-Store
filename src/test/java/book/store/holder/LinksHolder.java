package book.store.holder;

public interface LinksHolder {
    String INSERT_BOOKS_FILE_PATH = "classpath:database/insert-books.sql";
    String DELETE_ALL_BOOKS_FILE_PATH = "classpath:database/delete-all-books.sql";
    String INSERT_CATEGORY_FILE_PATH = "classpath:database/insert-category.sql";
    String DELETE_ALL_CATEGORIES_FILE_PATH = "classpath:database/delete-all-categories.sql";
    String DELETE_ALL_USERS_FILE_PATH = "classpath:database/delete-all-users.sql";
    String INSERT_USER_FILE_PATH = "classpath:database/insert-user-to-database.sql";
    String DELETE_ALL_USER_ROLES_FILE_PATH =
            "classpath:database/delete-all-users_roles-from-database.sql";
    String INSERT_ADMIN_TO_USERS_ROLES_FILE_PATH =
            "classpath:database/insert-admin-to-user_roles.sql";
}
