package com.smartshop.service;

import com.smartshop.dto.SaleItemRequest;
import com.smartshop.dto.SaleRequest;
import com.smartshop.dto.SaleResponseDto;
import com.smartshop.enums.AuditAction;
import com.smartshop.enums.Role;
import com.smartshop.enums.SaleStatus;
import com.smartshop.models.Customer;
import com.smartshop.models.Product;
import com.smartshop.models.Sale;
import com.smartshop.models.SaleItem;
import com.smartshop.models.User;
import com.smartshop.repository.CustomerRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.SaleRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public SaleService(SaleRepository saleRepository,
                       ProductRepository productRepository,
                       CustomerRepository customerRepository,
                       UserRepository userRepository,
                       AuditLogService auditLogService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public SaleResponseDto createSale(SaleRequest req, UserPrincipal principal, HttpServletRequest request) {
        User cashier = userRepository.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("Authenticated cashier not found"));

        // Generate unique Invoice Number
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countToday = saleRepository.countSalesBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        ) + 1;
        String invoiceNumber = String.format("INV-%s-%04d", datePrefix, countToday);

        // Resolve Customer
        Customer customer = null;
        if (req.getCustomerId() != null) {
            customer = customerRepository.findById(req.getCustomerId()).orElse(null);
        } else if (req.getCustomerPhone() != null && !req.getCustomerPhone().trim().isEmpty()) {
            customer = customerRepository.findByPhone(req.getCustomerPhone().trim())
                    .orElseGet(() -> {
                        Customer newCust = new Customer(
                                req.getCustomerName() != null ? req.getCustomerName().trim() : "Customer",
                                req.getCustomerPhone().trim(),
                                null, null, null
                        );
                        return customerRepository.save(newCust);
                    });
        }

        Sale sale = new Sale();
        sale.setInvoiceNumber(invoiceNumber);
        sale.setCustomer(customer);
        sale.setCustomerName(customer != null ? customer.getName() : (req.getCustomerName() != null ? req.getCustomerName() : "Walk-in Customer"));
        sale.setCustomerPhone(customer != null ? customer.getPhone() : req.getCustomerPhone());
        sale.setCashier(cashier);
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaymentMethod(req.getPaymentMethod());
        sale.setStatus(SaleStatus.COMPLETED);
        sale.setNotes(req.getNotes());

        BigDecimal calculatedSubtotal = BigDecimal.ZERO;
        BigDecimal calculatedTotalCost = BigDecimal.ZERO;
        BigDecimal calculatedTotalProfit = BigDecimal.ZERO;

        for (SaleItemRequest itemReq : req.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemReq.getProductId()));

            if (!product.isActive()) {
                throw new RuntimeException("Product '" + product.getName() + "' is archived/inactive");
            }

            if (product.getCurrentStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product '" + product.getName() + "'. Available: " + product.getCurrentStock() + ", Requested: " + itemReq.getQuantity());
            }

            // Deduct inventory stock
            product.setCurrentStock(product.getCurrentStock() - itemReq.getQuantity());
            productRepository.save(product);

            BigDecimal itemDiscount = itemReq.getDiscount() != null ? itemReq.getDiscount() : BigDecimal.ZERO;
            BigDecimal itemSubtotal = itemReq.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                    .subtract(itemDiscount);

            BigDecimal itemCost = product.getCostPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            BigDecimal itemProfit = itemSubtotal.subtract(itemCost);

            calculatedSubtotal = calculatedSubtotal.add(itemSubtotal);
            calculatedTotalCost = calculatedTotalCost.add(itemCost);
            calculatedTotalProfit = calculatedTotalProfit.add(itemProfit);

            SaleItem saleItem = new SaleItem(
                    product,
                    product.getName(),
                    product.getSku(),
                    itemReq.getQuantity(),
                    itemReq.getUnitPrice(),
                    product.getCostPrice(),
                    itemDiscount,
                    itemSubtotal,
                    itemProfit
            );

            sale.addItem(saleItem);
        }

        BigDecimal discountAmount = req.getDiscountAmount() != null ? req.getDiscountAmount() : BigDecimal.ZERO;
        if (req.getDiscountPercentage() != null && req.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = calculatedSubtotal.multiply(req.getDiscountPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal discountedSubtotal = calculatedSubtotal.subtract(discountAmount);
        if (discountedSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            discountedSubtotal = BigDecimal.ZERO;
        }

        BigDecimal taxRate = req.getTaxRate() != null ? req.getTaxRate() : BigDecimal.ZERO;
        BigDecimal taxAmount = discountedSubtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = discountedSubtotal.add(taxAmount);

        BigDecimal paidAmount = req.getPaidAmount() != null ? req.getPaidAmount() : grandTotal;
        BigDecimal changeAmount = paidAmount.compareTo(grandTotal) > 0 ? paidAmount.subtract(grandTotal) : BigDecimal.ZERO;

        // Overall net profit after general bill discounts
        BigDecimal finalSaleProfit = calculatedTotalProfit.subtract(discountAmount);

        sale.setSubtotal(calculatedSubtotal);
        sale.setDiscountPercentage(req.getDiscountPercentage() != null ? req.getDiscountPercentage() : BigDecimal.ZERO);
        sale.setDiscountAmount(discountAmount);
        sale.setTaxRate(taxRate);
        sale.setTaxAmount(taxAmount);
        sale.setGrandTotal(grandTotal);
        sale.setPaidAmount(paidAmount);
        sale.setChangeAmount(changeAmount);
        sale.setTotalCost(calculatedTotalCost);
        sale.setTotalProfit(finalSaleProfit);

        Sale savedSale = saleRepository.save(sale);

        // Update Customer Lifetime Stats
        if (customer != null) {
            customer.setTotalPurchases(customer.getTotalPurchases() + 1);
            customer.setTotalSpent(customer.getTotalSpent().add(grandTotal));
            customerRepository.save(customer);
        }

        auditLogService.log(
                AuditAction.CREATE_SALE,
                "New sale completed: #" + savedSale.getInvoiceNumber() + " | Cashier: " + cashier.getUsername() + " | Amount: " + grandTotal + " | Pay: " + savedSale.getPaymentMethod().name(),
                request
        );

        boolean isOwner = principal.getRole() == Role.ROLE_OWNER || principal.getRole() == Role.ROLE_ADMIN;
        return new SaleResponseDto(savedSale, isOwner);
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDto> getAllSales(boolean isOwner) {
        return saleRepository.findAll().stream()
                .sorted((a, b) -> b.getSaleDate().compareTo(a.getSaleDate()))
                .map(s -> new SaleResponseDto(s, isOwner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDto> getMySales(Long cashierId, boolean isOwner) {
        return saleRepository.findByCashierIdOrderBySaleDateDesc(cashierId).stream()
                .map(s -> new SaleResponseDto(s, isOwner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDto> getTodaySalesForCashier(Long cashierId, boolean isOwner) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        return saleRepository.findByCashierIdAndSaleDateBetweenOrderBySaleDateDesc(cashierId, startOfDay, endOfDay).stream()
                .map(s -> new SaleResponseDto(s, isOwner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SaleResponseDto getSaleById(Long id, boolean isOwner) {
        Sale sale = saleRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Sale order not found with id: " + id));
        return new SaleResponseDto(sale, isOwner);
    }

    @Transactional(readOnly = true)
    public SaleResponseDto getSaleByInvoice(String invoiceNumber, boolean isOwner) {
        Sale sale = saleRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceNumber));
        return new SaleResponseDto(sale, isOwner);
    }

    @Transactional
    public void cancelSale(Long id, UserPrincipal principal, HttpServletRequest request) {
        Sale sale = saleRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Sale order not found with id: " + id));

        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new RuntimeException("Sale is already cancelled");
        }

        // Restore product stock
        if (sale.getItems() != null) {
            for (SaleItem item : sale.getItems()) {
                if (item.getProduct() != null) {
                    Product product = item.getProduct();
                    product.setCurrentStock(product.getCurrentStock() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        sale.setStatus(SaleStatus.CANCELLED);
        saleRepository.save(sale);

        auditLogService.log(
                AuditAction.CANCEL_SALE,
                "Cancelled sale invoice #" + sale.getInvoiceNumber() + " and restored product stock",
                request
        );
    }
}
