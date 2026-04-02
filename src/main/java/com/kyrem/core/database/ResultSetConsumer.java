package com.kyrem.core.database;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetConsumer<T> {
    T handle(ResultSet rs) throws SQLException;
}
