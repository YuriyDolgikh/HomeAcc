package biz.itehnika.model;

public enum CustomerRole {
    ADMIN, USER;

    @Override
    public String toString() {
        return "ROLE_" + name();
    }
}
