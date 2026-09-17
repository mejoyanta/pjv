package com.tax.vat.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    private final JdbcTemplate jdbcTemplate;

    public DebugController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db-check")
    public Map<String, Object> checkDb() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            // 1. Current Database & User
            String currentDb = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
            String currentUser = jdbcTemplate.queryForObject("SELECT current_user", String.class);
            result.put("database", currentDb);
            result.put("user", currentUser);

            // 2. All tables in current schema
            List<String> tables = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name",
                    String.class
            );
            result.put("tables_in_public", tables);

            // 3. Check companies table
            if (tables.contains("companies")) {
                Integer totalCompanies = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM companies", Integer.class);
                Integer activeCompanies = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM companies WHERE deleted_at IS NULL", Integer.class);
                Integer deletedCompanies = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM companies WHERE deleted_at IS NOT NULL", Integer.class);
                result.put("companies_total_count", totalCompanies);
                result.put("companies_active_count", activeCompanies);
                result.put("companies_deleted_count", deletedCompanies);

                List<Map<String, Object>> sample = jdbcTemplate.queryForList(
                        "SELECT id, name, slug, username, bin, email, status, deleted_at FROM companies LIMIT 5"
                );
                result.put("companies_sample", sample);
            } else {
                result.put("companies_error", "Table 'companies' does not exist in public schema!");
            }

            // 4. Check users table
            if (tables.contains("users")) {
                Integer totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
                result.put("users_total_count", totalUsers);
            }

            // 5. Check other schemas if any
            List<String> schemas = jdbcTemplate.queryForList(
                    "SELECT schema_name FROM information_schema.schemata",
                    String.class
            );
            result.put("available_schemas", schemas);

        } catch (Exception e) {
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/columns")
    public Map<String, Object> checkColumns() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> compCols = jdbcTemplate.queryForList(
                    "SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_name = 'companies' ORDER BY ordinal_position"
            );
            result.put("companies_columns", compCols);

            List<Map<String, Object>> userCols = jdbcTemplate.queryForList(
                    "SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_name = 'users' ORDER BY ordinal_position"
            );
            result.put("users_columns", userCols);
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }
}
