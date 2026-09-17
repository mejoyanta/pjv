package com.tax.vat.config;

import com.tax.vat.entity.CompanyCategory;
import com.tax.vat.entity.Group;
import com.tax.vat.entity.User;
import com.tax.vat.enums.CategoryType;
import com.tax.vat.enums.UserStatus;
import com.tax.vat.repository.CompanyCategoryRepository;
import com.tax.vat.repository.GroupRepository;
import com.tax.vat.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CompanyCategoryRepository categoryRepository;
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DatabaseSeeder(UserRepository userRepository,
                          GroupRepository groupRepository,
                          CompanyCategoryRepository categoryRepository,
                          JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.categoryRepository = categoryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            // 0. Clean up any invalid foreign key constraints and legacy 0 values
            try {
                jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS fks8f6h77iurd4c5oyahuogw1gk");
                jdbcTemplate.execute("UPDATE users SET company_branch_id = NULL WHERE company_branch_id = 0");
                jdbcTemplate.execute("UPDATE users SET company_id = NULL WHERE company_id = 0");
                jdbcTemplate.execute("UPDATE users SET designation_id = NULL WHERE designation_id = 0");
                jdbcTemplate.execute("UPDATE users SET department_id = NULL WHERE department_id = 0");
                jdbcTemplate.execute("UPDATE users SET group_id = NULL WHERE group_id = 0");

                // Resync PostgreSQL sequences to match max existing IDs
                String[] tables = {"companies", "users", "company_branches", "groups", "company_categories"};
                for (String tbl : tables) {
                    try {
                        jdbcTemplate.execute("SELECT setval(pg_get_serial_sequence('" + tbl + "', 'id'), COALESCE((SELECT MAX(id) FROM " + tbl + "), 1))");
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) {
                System.out.println("Constraint/Sequence cleanup notice: " + e.getMessage());
            }

            // 1. Ensure default Admin Group exists
            Group adminGroup = groupRepository.findAll().stream().findFirst().orElseGet(() -> {
                Group g = new Group();
                g.setName("Super Admin");
                g.setSlug("super-admin");
                g.setIsAdmin(true);
                return groupRepository.save(g);
            });

            // 2. Ensure default Company Category exists
            if (categoryRepository.count() == 0) {
                categoryRepository.save(new CompanyCategory("Manufacturer", "manufacturer", CategoryType.MANUFACTURER, "Manufacturing Sector"));
                categoryRepository.save(new CompanyCategory("Traders", "traders", CategoryType.TRADERS, "Trading & Wholesale"));
                categoryRepository.save(new CompanyCategory("Service", "service", CategoryType.SERVICE, "Service Providers"));
                categoryRepository.save(new CompanyCategory("General Trading", "general-trading", CategoryType.TRADERS, "General Trading"));
            }

            // 3. Ensure joyanta319@gmail.com exists with password Bk123456789#
            User user = userRepository.findByEmail("joyanta319@gmail.com")
                    .or(() -> userRepository.findByUsername("joyanta319@gmail.com"))
                    .orElse(null);

            if (user == null) {
                user = new User();
                user.setName("Joyanta Admin");
                user.setEmail("joyanta319@gmail.com");
                user.setUsername("joyanta319@gmail.com");
                user.setPassword(encoder.encode("Bk123456789#"));
                user.setNid("1990123456789");
                user.setStatus(UserStatus.ACTIVE);
                user.setIsAdmin(true);
                user.setGroup(adminGroup);
                userRepository.save(user);
                System.out.println(">>> [DATABASE SEEDER] Created admin user: joyanta319@gmail.com / Bk123456789#");
            } else {
                user.setPassword(encoder.encode("Bk123456789#"));
                user.setStatus(UserStatus.ACTIVE);
                userRepository.save(user);
                System.out.println(">>> [DATABASE SEEDER] Verified admin credentials for: joyanta319@gmail.com");
            }
        } catch (Exception e) {
            System.err.println("DatabaseSeeder error (non-fatal): " + e.getMessage());
        }
    }
}
