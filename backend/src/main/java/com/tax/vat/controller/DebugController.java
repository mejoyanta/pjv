package com.tax.vat.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    private final JdbcTemplate jdbcTemplate;

    public DebugController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/sync-sequences")
    public Map<String, Object> syncSequences() {
        Map<String, Object> res = new LinkedHashMap<>();
        String[] tables = {
            "document_registers", "dvcs", "company_reports", "audit_from_vats",
            "analyze_reports", "legal_management_cases", "task_management", "noc_certificates",
            "currencies", "unit_of_supplies", "ports", "cpc_item_nos", "designations", "departments",
            "additional_prices", "materials", "products", "suppliers", "priority_suppliers",
            "customers", "priority_customers"
        };
        for (String table : tables) {
            try {
                Long maxId = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 1) FROM " + table, Long.class);
                String seq = jdbcTemplate.queryForObject("SELECT pg_get_serial_sequence('" + table + "', 'id')", String.class);
                if (seq != null) {
                    jdbcTemplate.execute("SELECT setval('" + seq + "', " + maxId + ")");
                    res.put(table, "Synced to " + maxId + " (" + seq + ")");
                } else {
                    res.put(table, "No sequence found, maxId=" + maxId);
                }
            } catch (Exception e) {
                res.put(table, "Error: " + e.getMessage());
            }
        }
        return res;
    }

    @GetMapping("/init-products")
    public Map<String, Object> initProducts() {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            java.io.File sqlFile = new java.io.File("C:/Users/BARABD/.gemini/antigravity/brain/bdc240bc-c768-44b6-b809-842eaf49824f/scratch/products.sql");
            if (sqlFile.exists()) {
                String content = java.nio.file.Files.readString(sqlFile.toPath());
                String[] statements = content.split(";\\r?\\n");
                int count = 0;
                for (String stmt : statements) {
                    if (stmt.trim().length() > 5) {
                        jdbcTemplate.execute(stmt.trim());
                        count++;
                    }
                }
                // sync sequence
                try {
                    Long maxId = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 1) FROM products", Long.class);
                    String seq = jdbcTemplate.queryForObject("SELECT pg_get_serial_sequence('products', 'id')", String.class);
                    if (seq != null) {
                        jdbcTemplate.execute("SELECT setval('" + seq + "', " + maxId + ")");
                        res.put("sequenceSyncedTo", maxId);
                    }
                } catch (Exception se) {
                    res.put("sequenceError", se.getMessage());
                }
                res.put("status", "success");
                res.put("statementsExecuted", count);
            } else {
                res.put("status", "error");
                res.put("message", "File products.sql not found");
            }
        } catch (Exception e) {
            res.put("status", "error");
            res.put("error", e.getMessage());
        }
        return res;
    }

    @GetMapping("/execute-query")
    public Object executeQuery(@RequestParam String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("error", e.getMessage());
            return err;
        }
    }

    @GetMapping("/table-counts")
    public Map<String, Object> getTableCounts() {
        Map<String, Object> res = new LinkedHashMap<>();
        String[] tables = {
            "companies",
            "document_registers", "dvcs", "company_reports", "audit_from_vats",
            "analyze_reports", "legal_management_cases", "task_management", "noc_certificates",
            "currencies", "unit_of_supplies", "ports", "cpc_item_nos",
            "designations", "departments", "additional_prices", "materials",
            "products", "suppliers", "priority_suppliers", "customers", "priority_customers"
        };
        for (String table : tables) {
            try {
                Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
                Map<String, Object> tableInfo = new LinkedHashMap<>();
                tableInfo.put("total", total);
                try {
                    Long active = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE deleted_at IS NULL", Long.class);
                    tableInfo.put("active", active);
                } catch (Exception e) {
                    tableInfo.put("active", total);
                }
                res.put(table, tableInfo);
            } catch (Exception e) {
                res.put(table, "Table error: " + e.getMessage());
            }
        }
        return res;
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

    @GetMapping("/dashboard/overview")
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            // 1. Paid companies
            List<Map<String, Object>> paid = jdbcTemplate.queryForList(
                    "SELECT id, name, email FROM companies WHERE deleted_at IS NULL AND status = 'active' AND id IN " +
                    "(SELECT DISTINCT company_id FROM payments WHERE (status = 'pending' OR status = 'paid') AND (service_charge > 0 OR package_amount > 0)) ORDER BY name LIMIT 100"
            );
            // 2. Unpaid companies
            List<Map<String, Object>> unpaid = jdbcTemplate.queryForList(
                    "SELECT id, name, email, owner_name FROM companies WHERE deleted_at IS NULL AND status = 'active' AND id NOT IN " +
                    "(SELECT DISTINCT company_id FROM payments WHERE (status = 'pending' OR status = 'paid') AND (service_charge > 0 OR package_amount > 0)) ORDER BY name LIMIT 100"
            );

            long paidCount = paid.size();
            long unpaidCount = unpaid.size();
            long totalActive = paidCount + unpaidCount;
            int paidPct = totalActive > 0 ? (int) Math.round(((double) paidCount / totalActive) * 100) : 0;
            int unpaidPct = totalActive > 0 ? (int) Math.round(((double) unpaidCount / totalActive) * 100) : 0;

            res.put("paidCount", paidCount);
            res.put("unpaidCount", unpaidCount);
            res.put("paidPct", paidPct);
            res.put("unpaidPct", unpaidPct);
            res.put("paymentMonth", java.time.YearMonth.now().minusMonths(1).toString());
            res.put("paidList", paid);
            res.put("unpaidList", unpaid);

            // 3. Mushok 4.3 Amendment
            Long m43AmendCount = 0L;
            List<Map<String, Object>> m43AmendList = new ArrayList<>();
            try {
                m43AmendCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM musak_4_3s WHERE deleted_at IS NULL AND (parent_4_3_id IS NOT NULL OR (amendment_comment IS NOT NULL AND amendment_comment != ''))",
                        Long.class
                );
                m43AmendList = jdbcTemplate.queryForList(
                        "SELECT m.id, c.name as company, m.product_service_details as description, to_char(m.date, 'DD-Mon-YYYY') as date " +
                        "FROM musak_4_3s m LEFT JOIN companies c ON m.company_id = c.id " +
                        "WHERE m.deleted_at IS NULL AND (m.parent_4_3_id IS NOT NULL OR (m.amendment_comment IS NOT NULL AND m.amendment_comment != '')) " +
                        "ORDER BY m.id DESC LIMIT 25"
                );
            } catch (Exception e) {
                // table or column fallback
            }
            res.put("m43AmendmentCount", m43AmendCount != null ? m43AmendCount : 0L);
            res.put("m43AmendmentList", m43AmendList);

            // 4. VAT Online 9.1 submitted vs missing
            Long submittedCount = 0L;
            List<Map<String, Object>> submittedList = new ArrayList<>();
            List<Map<String, Object>> missingList = new ArrayList<>();
            try {
                submittedCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(DISTINCT company_id) FROM musak_9_1_onlines WHERE company_id IS NOT NULL",
                        Long.class
                );
                submittedList = jdbcTemplate.queryForList(
                        "SELECT DISTINCT c.id, c.name FROM companies c JOIN musak_9_1_onlines m ON c.id = m.company_id WHERE c.deleted_at IS NULL AND c.status = 'active' ORDER BY c.name LIMIT 50"
                );
                missingList = jdbcTemplate.queryForList(
                        "SELECT c.id, c.name, c.email, c.owner_name FROM companies c WHERE c.deleted_at IS NULL AND c.status = 'active' AND c.id NOT IN (SELECT DISTINCT company_id FROM musak_9_1_onlines WHERE company_id IS NOT NULL) ORDER BY c.name LIMIT 50"
                );
            } catch (Exception e) {
                // fallback
            }
            long subCount = submittedCount != null ? submittedCount : 0L;
            long misCount = Math.max(0, totalActive - subCount);
            int subPct = totalActive > 0 ? (int) Math.round(((double) subCount / totalActive) * 100) : 0;
            int misPct = totalActive > 0 ? (int) Math.round(((double) misCount / totalActive) * 100) : 0;
            java.time.YearMonth prevMonth = java.time.YearMonth.now().minusMonths(1);
            String windowLabel = prevMonth.getMonth().name().charAt(0) + prevMonth.getMonth().name().substring(1).toLowerCase() + " " + prevMonth.getYear();

            res.put("returnsSubmittedCount", subCount);
            res.put("returnsMissingCount", misCount);
            res.put("returnsSubmittedPct", subPct);
            res.put("returnsMissingPct", misPct);
            res.put("returnsWindowLabel", windowLabel);
            res.put("returnsSubmittedList", submittedList);
            res.put("returnsMissingList", missingList);

            // 5. Purchases count and sample
            Long purchaseTotal = 0L;
            List<Map<String, Object>> purchaseList = new ArrayList<>();
            try {
                purchaseTotal = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM purchases WHERE deleted_at IS NULL",
                        Long.class
                );
                purchaseList = jdbcTemplate.queryForList(
                        "SELECT p.id, c.name as company, p.purchase_type as type, p.total_amount as amount, to_char(p.date, 'DD-Mon-YYYY') as date, p.bill_of_entry as invoice " +
                        "FROM purchases p LEFT JOIN companies c ON p.company_id = c.id WHERE p.deleted_at IS NULL ORDER BY p.id DESC LIMIT 25"
                );
            } catch (Exception e) {
                // fallback
            }
            res.put("purchaseTotal", purchaseTotal != null ? purchaseTotal : 0L);
            res.put("purchaseList", purchaseList);

            // 6. Mushok 4.3 count and sample
            Long m43Total = 0L;
            List<Map<String, Object>> m43List = new ArrayList<>();
            try {
                m43Total = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM musak_4_3s WHERE deleted_at IS NULL",
                        Long.class
                );
                m43List = jdbcTemplate.queryForList(
                        "SELECT m.id, c.name as company, m.product_service_details as product, m.hs_code, m.sell_price as price, to_char(m.date, 'DD-Mon-YYYY') as date " +
                        "FROM musak_4_3s m LEFT JOIN companies c ON m.company_id = c.id WHERE m.deleted_at IS NULL ORDER BY m.id DESC LIMIT 25"
                );
            } catch (Exception e) {
                // fallback
            }
            res.put("m43Total", m43Total != null ? m43Total : 0L);
            res.put("m43List", m43List);

            // 7. Scroll Notices
            List<String> notices = new ArrayList<>();
            try {
                List<Map<String, Object>> noticeRows = jdbcTemplate.queryForList(
                        "SELECT message FROM scroll_notices WHERE status = 1 ORDER BY id DESC LIMIT 10"
                );
                for (Map<String, Object> nr : noticeRows) {
                    if (nr.get("message") != null) {
                        notices.add(nr.get("message").toString());
                    }
                }
            } catch (Exception e) {
                // fallback
            }
            if (notices.isEmpty()) {
                notices.add("তর জন্য জানানো যাচ্ছে যে, আপনাদের প্রজেক্টির ডকুমেন্ট এবং অন্যান্য তথ্যাদি পরিষ্কার স্থান করি আপনাদের info@barabdonline.xyz মেইলে প্রদান করার জন্য অনুরোধ করা হলো");
            }
            res.put("scrollNotices", notices);

        } catch (Exception e) {
            res.put("error", e.getMessage());
            e.printStackTrace();
        }
        return res;
    }
}
