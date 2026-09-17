package com.tax.vat;

import com.tax.vat.dto.request.CompanyBranchCreateRequest;
import com.tax.vat.dto.request.CompanyCreateRequest;
import com.tax.vat.dto.request.LoginRequest;
import com.tax.vat.dto.request.UserCreateRequest;
import com.tax.vat.dto.response.CompanyResponse;
import com.tax.vat.dto.response.LoginResponse;
import com.tax.vat.dto.response.UserResponse;
import com.tax.vat.entity.CompanyBranch;
import com.tax.vat.entity.CompanyCategory;
import com.tax.vat.entity.Group;
import com.tax.vat.entity.User;
import com.tax.vat.enums.CategoryType;
import com.tax.vat.enums.CompanyLevel;
import com.tax.vat.enums.CompanyStatus;
import com.tax.vat.enums.UserStatus;
import com.tax.vat.repository.CompanyCategoryRepository;
import com.tax.vat.repository.GroupRepository;
import com.tax.vat.repository.UserRepository;
import com.tax.vat.service.AuthService;
import com.tax.vat.service.CompanyBranchService;
import com.tax.vat.service.CompanyService;
import com.tax.vat.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserCompanyBranchIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyBranchService branchService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CompanyCategoryRepository categoryRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void testFullFlow_Login_Company_Branch_User_Login_Logout() {
        try {
            System.out.println(">>> CHECKING TABLES IN POSTGRES:");
            for (String t : new String[]{"companies", "payments", "purchases", "musak_4_3s", "musak_9_1_onlines", "scroll_notices"}) {
                try {
                    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + t, Integer.class);
                    System.out.println(">>> TABLE [" + t + "] COUNT = " + count);
                } catch (Exception e) {
                    System.out.println(">>> TABLE [" + t + "] NOT FOUND or ERR: " + e.getMessage());
                }
            }
            try {
                java.util.List<java.util.Map<String, Object>> notices = jdbcTemplate.queryForList("SELECT id, message, status FROM scroll_notices LIMIT 3");
                System.out.println(">>> SCROLL NOTICES SAMPLE: " + notices);
            } catch (Exception e) {
                System.out.println(">>> NOTICES ERR: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Debug query failed: " + e.getMessage());
        }
        System.out.println("====== [TEST STEP 1]: Setting up/Verifying joyanta319@gmail.com ======");
        // Ensure default Group and Category exist
        Group testGroup = groupRepository.findAll().stream().findFirst().orElseGet(() -> {
            Group g = new Group();
            g.setName("Super Admin");
            g.setSlug("super-admin");
            g.setIsAdmin(true);
            return groupRepository.save(g);
        });

        CompanyCategory testCategory = categoryRepository.findAll().stream().findFirst().orElseGet(() -> {
            CompanyCategory cat = new CompanyCategory("General Trading", "general-trading", CategoryType.TRADERS, "Test Category");
            return categoryRepository.save(cat);
        });

        // Ensure user joyanta319@gmail.com exists with password Bk123456789#
        User adminUser = userRepository.findByEmail("joyanta319@gmail.com")
                .or(() -> userRepository.findByUsername("joyanta319@gmail.com"))
                .orElse(null);

        if (adminUser == null) {
            adminUser = new User();
            adminUser.setName("Joyanta Admin");
            adminUser.setEmail("joyanta319@gmail.com");
            adminUser.setUsername("joyanta319@gmail.com");
            adminUser.setPassword(encoder.encode("Bk123456789#"));
            adminUser.setNid("1990123456789");
            adminUser.setGroup(testGroup);
            adminUser.setStatus(UserStatus.ACTIVE);
            adminUser = userRepository.save(adminUser);
            System.out.println("Created admin user joyanta319@gmail.com");
        } else {
            // Update password to match Bk123456789#
            adminUser.setPassword(encoder.encode("Bk123456789#"));
            adminUser.setStatus(UserStatus.ACTIVE);
            userRepository.save(adminUser);
        }

        // 1. TEST LOGIN with joyanta319@gmail.com
        LoginRequest adminLogin = new LoginRequest("joyanta319@gmail.com", "Bk123456789#");
        LoginResponse adminResponse = authService.login(adminLogin);
        assertNotNull(adminResponse.getToken(), "Admin token should not be null");
        System.out.println(">>> [SUCCESS] Logged in as: " + adminResponse.getName() + " (" + adminResponse.getEmail() + ")");

        // 2. CREATE A COMPANY
        System.out.println("====== [TEST STEP 2]: Creating Company ======");
        String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 100000);
        CompanyCreateRequest companyReq = new CompanyCreateRequest();
        companyReq.setName("Joyanta Logistics " + uniqueSuffix);
        companyReq.setUsername("BARA49-JLOG" + uniqueSuffix);
        companyReq.setEmail("logistics" + uniqueSuffix + "@joyanta.com");
        companyReq.setBin("009" + uniqueSuffix + "-0101");
        companyReq.setPhone("01700" + uniqueSuffix);
        companyReq.setCategoryId(testCategory.getId());
        companyReq.setCompanyLevel(CompanyLevel.MEDIUM);
        companyReq.setStatus(CompanyStatus.ACTIVE);
        companyReq.setAddress("Agrabad Commercial Area, Chittagong");
        companyReq.setOwnerName("Joyanta Roy");
        companyReq.setOwnerPhone("01811" + uniqueSuffix);

        CompanyResponse createdCompany = companyService.createCompany(companyReq);
        assertNotNull(createdCompany.getId(), "Company ID should not be null");
        System.out.println(">>> [SUCCESS] Created Company: " + createdCompany.getName() + " [ID: " + createdCompany.getId() + ", Slug: " + createdCompany.getSlug() + "]");

        // 3. CREATE A BRANCH FOR THE COMPANY
        System.out.println("====== [TEST STEP 3]: Creating Company Branch ======");
        CompanyBranchCreateRequest branchReq = new CompanyBranchCreateRequest();
        branchReq.setCompanyId(createdCompany.getId());
        branchReq.setName("Chittagong Port Terminal Branch");
        branchReq.setPhone("01911" + uniqueSuffix);
        branchReq.setEmail("port.branch" + uniqueSuffix + "@joyanta.com");
        branchReq.setAddress("Port Area Road 4, Chittagong");
        branchReq.setIsMain(false);

        CompanyBranch createdBranch = branchService.createBranch(branchReq);
        assertNotNull(createdBranch.getId(), "Branch ID should not be null");
        System.out.println(">>> [SUCCESS] Created Branch: " + createdBranch.getName() + " [Branch ID: " + createdBranch.getId() + "]");

        // 4. CREATE A USER ASSIGNED TO THIS COMPANY & BRANCH
        System.out.println("====== [TEST STEP 4]: Creating New User ======");
        String newUsername = "op_" + uniqueSuffix;
        String newEmail = "operator" + uniqueSuffix + "@joyanta.com";
        String newPassword = "Operator123#";

        UserCreateRequest userReq = new UserCreateRequest();
        userReq.setName("Mr. Branch Operator");
        userReq.setUsername(newUsername);
        userReq.setEmail(newEmail);
        userReq.setPassword(encoder.encode(newPassword));
        userReq.setNid("1995123456789");
        userReq.setContact("01712345678");
        userReq.setGroupId(testGroup.getId());
        userReq.setCompanyId(createdCompany.getId());
        userReq.setCompanyBranchId(createdBranch.getId());
        userReq.setStatus(UserStatus.ACTIVE);

        UserResponse createdUser = userService.createUser(userReq, createdCompany.getId());
        assertNotNull(createdUser.getId(), "Created User ID should not be null");
        System.out.println(">>> [SUCCESS] Created User: " + createdUser.getName() + " (" + createdUser.getUsername() + ")");

        // 5. TEST LOGIN WITH THE NEWLY CREATED USER
        System.out.println("====== [TEST STEP 5]: Testing Login with newly created user ======");
        LoginRequest newUserLogin = new LoginRequest(newUsername, newPassword);
        LoginResponse newUserResponse = authService.login(newUserLogin);
        assertNotNull(newUserResponse.getToken(), "New user token should not be null");
        assertEquals(newUsername, newUserResponse.getUsername());
        System.out.println(">>> [SUCCESS] Logged in successfully with newly created user: " + newUserResponse.getUsername());

        // 6. TEST LOGOUT WITH THE USER
        System.out.println("====== [TEST STEP 6]: Testing Logout ======");
        authService.logout(newUserResponse.getId());
        User loggedOutUser = userRepository.findById(newUserResponse.getId()).orElseThrow();
        assertFalse(loggedOutUser.getIsOnline(), "User should be marked offline after logout");
        System.out.println(">>> [SUCCESS] User logged out successfully. isOnline = " + loggedOutUser.getIsOnline());
        System.out.println("====== [ALL TESTS PASSED SUCCESSFULLY!] ======");
    }
}
