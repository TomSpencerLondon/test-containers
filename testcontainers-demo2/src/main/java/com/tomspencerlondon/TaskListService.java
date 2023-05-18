package com.tomspencerlondon;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class TaskListService {

    private final DataSource dataSource;

    public TaskListService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public TaskListEntry insert(TaskListEntry entry) {
        final var jdbcTemplate = new JdbcTemplate(dataSource);
        final String id = UUID.randomUUID().toString();
        jdbcTemplate.update("INSERT INTO TASKLIST_ENTRIES values(?, ?)", id, entry.getText());

        return new TaskListEntry(id, entry.getText());
    }

}