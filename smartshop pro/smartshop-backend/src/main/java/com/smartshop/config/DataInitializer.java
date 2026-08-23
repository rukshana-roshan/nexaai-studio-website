package com.smartshop.config;

import com.smartshop.enums.PaymentMethod;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.enums.Role;
import com.smartshop.enums.UserStatus;
import com.smartshop.models.*;
import com.smartshop.repository.*;
import com.smartshop.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final CustomerRepository customerRepository;
    private final ShopSettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           SupplierRepository supplierRepository,
                           ExpenseCategoryRepository expenseCategoryRepository,
                           ExpenseRepository expenseRepository,
                           CustomerRepository customerRepository,
                           ShopSettingsRepository settingsRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseRepository = expenseRepository;
        this.customerRepository = customerRepository;
        this.settingsRepository = settingsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            logger.info("Initializing SmartShop Pro seed data...");

            // 1. Initialize Users
            User owner = new User("admin", "admin@smartshoppro.com", passwordEncoder.encode("admin123"), "Marcus Vance (Owner)", "+1 (555) 100-2000", Role.ROLE_OWNER, UserStatus.ACTIVE);
            userRepository.save(owner);

            User cashier1 = new User("cashier1", "cashier1@smartshoppro.com", passwordEncoder.encode("cashier123"), "Alex Miller (Cashier)", "+1 (555) 100-2001", Role.ROLE_CASHIER, UserStatus.ACTIVE);
            userRepository.save(cashier1);

            User cashier2 = new User("sarah", "sarah@smartshoppro.com", passwordEncoder.encode("sarah123"), "Sarah Jenkins (Cashier)", "+1 (555) 100-2002", Role.ROLE_CASHIER, UserStatus.ACTIVE);
            userRepository.save(cashier2);

            // 2. Initialize Shop Settings
            ShopSettings settings = new ShopSettings();
            settings.setShopName("SmartShop Pro Retailers");
            settings.setTagline("Appliances • Electronics • Plastics • Furniture");
            settings.setAddress("742 Evergreen Avenue, Commercial Complex Suite #104");
            settings.setPhone("+1 (555) 890-7654");
            settings.setEmail("support@smartshoppro.com");
            settings.setWebsite("www.smartshoppro.com");
            settings.setTaxNumber("TX-998822-US");
            settings.setCurrencySymbol("$");
            settings.setCurrencyCode("USD");
            settings.setDefaultTaxRate(new BigDecimal("5.00"));
            settings.setEnableTax(true);
            settings.setDefaultLowStockAlert(5);
            settingsRepository.save(settings);

            // 3. Initialize Categories
            Category catAppliances = categoryRepository.save(new Category("Home Appliances", "Refrigerators, Microwaves, Washing Machines, Air Conditioners", "Tv"));
            Category catElectronics = categoryRepository.save(new Category("Consumer Electronics", "Smart TVs, Soundbars, Induction Cookers, Kettles", "Cpu"));
            Category catPlastics = categoryRepository.save(new Category("Plastics & Storage", "Storage Bins, Racks, Buckets, Containers & Household Plastics", "Box"));
            Category catFurniture = categoryRepository.save(new Category("Furniture & Decor", "Ergonomic Chairs, Coffee Tables, Steel Almirahs, Dining Sets", "Armchair"));
            Category catKitchen = categoryRepository.save(new Category("Kitchen & Cookware", "Non-stick sets, Pressure Cookers, Stainless Steel sets", "Utensils"));

            // 4. Initialize Suppliers
            Supplier supApex = supplierRepository.save(new Supplier("Apex Appliances Wholesalers", "Apex Global Trading Ltd", "David Clark", "+1 (555) 345-6789", "orders@apexappliances.com", "Industrial Zone Warehouse 4B, Sector 7", "APX-88219", new BigDecimal("1200.00")));
            Supplier supPlastech = supplierRepository.save(new Supplier("Plastech Polymers & Moulding", "Plastech Industries Inc.", "Rachel Green", "+1 (555) 456-7890", "sales@plastech.com", "Polymer Park Bldg 12, West Hub", "PLT-55104", new BigDecimal("450.00")));
            Supplier supComfort = supplierRepository.save(new Supplier("Comfort Living Furniture", "Comfort Home Furnishings Corp", "Michael Scott", "+1 (555) 567-8901", "m.scott@comfortliving.com", "Timber Yard Rd, North Port", "CMF-11928", new BigDecimal("0.00")));

            // 5. Initialize Products
            productRepository.save(new Product("Double Door Frost-Free Refrigerator 260L", "APP-REF-001", "890123450001", catAppliances, "Samsung", "pcs", new BigDecimal("320.00"), new BigDecimal("449.99"), 12, 3, "Aisle 1 - Bay A", "Energy efficient inverter technology with 10-year warranty"));
            productRepository.save(new Product("Microwave Oven with Grill 23L", "APP-MIC-002", "890123450002", catAppliances, "LG Electronics", "pcs", new BigDecimal("75.00"), new BigDecimal("119.50"), 18, 4, "Aisle 1 - Bay B", "Multi-stage cooking with defrost and touch keypad"));
            productRepository.save(new Product("Fully Automatic Top Load Washer 7kg", "APP-WSH-003", "890123450003", catAppliances, "Whirlpool", "pcs", new BigDecimal("210.00"), new BigDecimal("299.00"), 8, 2, "Aisle 1 - Bay C", "Smart scrub technology with hard water wash"));
            productRepository.save(new Product("High-Power Nutri-Blender 1000W", "APP-BLN-004", "890123450004", catAppliances, "NutriBullet", "pcs", new BigDecimal("32.00"), new BigDecimal("54.99"), 25, 5, "Aisle 2 - Shelf 1", "Stainless steel cross blade with bullet extraction"));

            productRepository.save(new Product("43-inch 4K Ultra HD Smart LED TV", "ELE-TV-001", "890123450005", catElectronics, "Sony", "pcs", new BigDecimal("280.00"), new BigDecimal("399.00"), 15, 3, "Electronics Wall A", "HDR10 with Google TV OS and Dolby Audio"));
            productRepository.save(new Product("120W Bluetooth Soundbar with Subwoofer", "ELE-SND-002", "890123450006", catElectronics, "JBL", "pcs", new BigDecimal("55.00"), new BigDecimal("89.99"), 14, 4, "Electronics Wall B", "Wireless deep bass with optical and HDMI ARC input"));
            productRepository.save(new Product("Digital Induction Cooktop 2100W", "ELE-IND-003", "890123450007", catElectronics, "Philips", "pcs", new BigDecimal("26.00"), new BigDecimal("42.50"), 20, 5, "Aisle 2 - Shelf 2", "Feather touch sensor with timer and auto shutoff"));
            productRepository.save(new Product("Cordless Stainless Steel Kettle 1.8L", "ELE-KET-004", "890123450008", catElectronics, "Prestige", "pcs", new BigDecimal("12.50"), new BigDecimal("22.00"), 4, 6, "Aisle 2 - Shelf 3", "Concealed heating element with dry boil protection (Low Stock Alert)"));

            productRepository.save(new Product("Heavy Duty 4-Tier Modular Plastic Rack", "PLS-RCK-001", "890123450009", catPlastics, "Cello", "set", new BigDecimal("14.00"), new BigDecimal("26.00"), 35, 8, "Plastics Zone A", "Multipurpose kitchen and bathroom storage organizer"));
            productRepository.save(new Product("Transparent Storage Bin Set (6 Pcs - 10L)", "PLS-BIN-002", "890123450010", catPlastics, "Tupperware", "set", new BigDecimal("18.00"), new BigDecimal("34.50"), 28, 5, "Plastics Zone B", "BPA-free airtight containers with easy lock lids"));
            productRepository.save(new Product("Stackable Premium Armless Chair (Set of 4)", "PLS-CHR-003", "890123450011", catPlastics, "Nilkamal", "set", new BigDecimal("28.00"), new BigDecimal("49.99"), 16, 4, "Plastics Zone C", "Weatherproof virgin polypropylene plastic"));
            productRepository.save(new Product("Heavy Duty Pedal Dustbin 30L", "PLS-WST-004", "890123450012", catPlastics, "Kuber", "pcs", new BigDecimal("8.50"), new BigDecimal("16.00"), 3, 5, "Plastics Zone D", "Hands-free foot pedal operation (Low Stock Alert)"));

            productRepository.save(new Product("Executive High-Back Ergonomic Mesh Chair", "FUR-CHR-001", "890123450013", catFurniture, "GreenSoul", "pcs", new BigDecimal("95.00"), new BigDecimal("159.00"), 10, 3, "Furniture Display 1", "3D adjustable armrests, lumbar support and tilt lock"));
            productRepository.save(new Product("Solid Engineered Wood Coffee Table", "FUR-TBL-002", "890123450014", catFurniture, "UrbanWood", "pcs", new BigDecimal("45.00"), new BigDecimal("79.00"), 7, 2, "Furniture Display 2", "Scratch-resistant walnut finish with lower shelf"));
            productRepository.save(new Product("Heavy Gauge Steel 2-Door Wardrobe Almirah", "FUR-ALM-003", "890123450015", catFurniture, "Godrej", "pcs", new BigDecimal("160.00"), new BigDecimal("249.00"), 5, 2, "Furniture Display 3", "Corrosion resistant CRCA steel with internal locker"));

            productRepository.save(new Product("Hard Anodized Non-Stick Cookware Set (3 Pcs)", "KIT-SET-001", "890123450016", catKitchen, "Prestige", "set", new BigDecimal("28.00"), new BigDecimal("48.00"), 22, 5, "Cookware Section A", "Includes Kadai with lid, Fry Pan, and Flat Dosa Tawa"));
            productRepository.save(new Product("Stainless Steel Pressure Cooker 5L Induction Base", "KIT-PCK-002", "890123450017", catKitchen, "Hawkins", "pcs", new BigDecimal("24.00"), new BigDecimal("39.99"), 19, 4, "Cookware Section B", "Food grade SS 304 with inner lid safety pressure release"));

            // 6. Initialize Customers
            customerRepository.save(new Customer("Robert Johnson", "+1 (555) 912-3456", "robert.j@example.com", "12 Maple Street", "Springfield"));
            customerRepository.save(new Customer("Emily Davis", "+1 (555) 823-4567", "emily.d@example.com", "88 Oakwood Drive", "Springfield"));
            customerRepository.save(new Customer("James Anderson", "+1 (555) 734-5678", "james.a@example.com", "45 Pine Hill Rd", "Metropolis"));

            // 7. Initialize Expense Categories & Sample Expenses
            ExpenseCategory expRent = expenseCategoryRepository.save(new ExpenseCategory("Shop Rent & Lease", "Monthly rental for store premises"));
            ExpenseCategory expUtilities = expenseCategoryRepository.save(new ExpenseCategory("Electricity & Water", "Power bills, cooling, water utilities"));
            ExpenseCategory expSalaries = expenseCategoryRepository.save(new ExpenseCategory("Staff Salaries", "Staff payroll and wages"));
            ExpenseCategory expTransport = expenseCategoryRepository.save(new ExpenseCategory("Logistics & Freight", "Inward stock shipping and delivery costs"));
            ExpenseCategory expMaintenance = expenseCategoryRepository.save(new ExpenseCategory("Store Maintenance", "Repairs, cleaning supplies, lighting"));

            Expense e1 = new Expense();
            e1.setTitle("Store Rent - Current Month");
            e1.setCategory(expRent);
            e1.setAmount(new BigDecimal("1200.00"));
            e1.setExpenseDate(LocalDateTime.now().minusDays(5));
            e1.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
            e1.setReferenceNumber("TXN-RNT-082026");
            e1.setCreatedBy(owner);
            expenseRepository.save(e1);

            Expense e2 = new Expense();
            e2.setTitle("Commercial Electric Power Bill");
            e2.setCategory(expUtilities);
            e2.setAmount(new BigDecimal("340.00"));
            e2.setExpenseDate(LocalDateTime.now().minusDays(2));
            e2.setPaymentMethod(PaymentMethod.UPI);
            e2.setReferenceNumber("PWR-889912");
            e2.setCreatedBy(owner);
            expenseRepository.save(e2);

            logger.info("Seed data successfully initialized for SmartShop Pro!");
        }
    }
}
