package com.smartshop.service;

import com.smartshop.dto.*;
import com.smartshop.enums.AuditAction;
import com.smartshop.enums.LedgerTransactionType;
import com.smartshop.enums.PaymentMethod;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.models.*;
import com.smartshop.repository.*;
import com.smartshop.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierPurchaseRepository purchaseRepository;
    private final SupplierPaymentRepository paymentRepository;
    private final SupplierLedgerEntryRepository ledgerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository,
                           SupplierPurchaseRepository purchaseRepository,
                           SupplierPaymentRepository paymentRepository,
                           SupplierLedgerEntryRepository ledgerRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           AuditLogService auditLogService) {
        this.supplierRepository = supplierRepository;
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
        this.ledgerRepository = ledgerRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream().map(SupplierDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplierDto> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue().stream().map(SupplierDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierEntity(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public SupplierDto getSupplierById(Long id) {
        return new SupplierDto(getSupplierEntity(id));
    }

    @Transactional
    public SupplierDto createSupplier(SupplierRequest req, UserPrincipal principal, HttpServletRequest request) {
        Supplier supplier = new Supplier(
                req.getName().trim(),
                req.getCompanyName(),
                req.getContactPerson(),
                req.getPhone().trim(),
                req.getEmail(),
                req.getAddress(),
                req.getTaxNumber(),
                req.getOpeningBalance() != null ? req.getOpeningBalance() : BigDecimal.ZERO
        );

        Supplier saved = supplierRepository.save(supplier);

        // Record initial ledger entry if opening balance > 0
        if (saved.getOpeningBalance().compareTo(BigDecimal.ZERO) > 0) {
            User user = principal != null ? userRepository.findById(principal.getId()).orElse(null) : null;
            SupplierLedgerEntry entry = new SupplierLedgerEntry(
                    saved,
                    LocalDateTime.now(),
                    LedgerTransactionType.OPENING_BALANCE,
                    "OPN-" + saved.getId(),
                    "Opening Balance Recorded",
                    BigDecimal.ZERO,
                    saved.getOpeningBalance(),
                    saved.getOpeningBalance(),
                    user
            );
            ledgerRepository.save(entry);
        }

        auditLogService.log(
                AuditAction.CREATE_SUPPLIER,
                "Created supplier: " + saved.getName() + " with opening balance " + saved.getOpeningBalance(),
                request
        );

        return new SupplierDto(saved);
    }

    @Transactional
    public SupplierDto updateSupplier(Long id, SupplierRequest req, HttpServletRequest request) {
        Supplier supplier = getSupplierEntity(id);

        supplier.setName(req.getName().trim());
        supplier.setCompanyName(req.getCompanyName());
        supplier.setContactPerson(req.getContactPerson());
        supplier.setPhone(req.getPhone().trim());
        supplier.setEmail(req.getEmail());
        supplier.setAddress(req.getAddress());
        supplier.setTaxNumber(req.getTaxNumber());

        Supplier updated = supplierRepository.save(supplier);
        auditLogService.log(AuditAction.UPDATE_SUPPLIER, "Updated supplier: " + updated.getName(), request);

        return new SupplierDto(updated);
    }

    @Transactional
    public void deleteSupplier(Long id, HttpServletRequest request) {
        Supplier supplier = getSupplierEntity(id);
        supplier.setActive(false);
        supplierRepository.save(supplier);
        auditLogService.log(AuditAction.UPDATE_SUPPLIER, "Deactivated supplier: " + supplier.getName(), request);
    }

    @Transactional
    public PurchaseResponseDto createPurchase(PurchaseRequest req, UserPrincipal principal, HttpServletRequest request) {
        Supplier supplier = getSupplierEntity(req.getSupplierId());
        User user = principal != null ? userRepository.findById(principal.getId()).orElse(null) : null;

        String invoiceNo = req.getInvoiceNumber();
        if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
            invoiceNo = "PO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        }

        SupplierPurchase purchase = new SupplierPurchase();
        purchase.setInvoiceNumber(invoiceNo);
        purchase.setSupplier(supplier);
        purchase.setPurchaseDate(req.getPurchaseDate() != null ? req.getPurchaseDate() : LocalDateTime.now());
        purchase.setPaymentMethod(req.getPaymentMethod());
        purchase.setNotes(req.getNotes());
        purchase.setCreatedBy(user);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseItemRequest itemReq : req.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemReq.getProductId()));

            BigDecimal itemTotal = itemReq.getUnitCost().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            PurchaseItem item = new PurchaseItem(product, itemReq.getQuantity(), itemReq.getUnitCost(), itemTotal);
            purchase.addItem(item);

            // Increment product inventory stock & update latest cost price
            product.setCurrentStock(product.getCurrentStock() + itemReq.getQuantity());
            product.setCostPrice(itemReq.getUnitCost());
            productRepository.save(product);
        }

        BigDecimal paidAmount = req.getPaidAmount() != null ? req.getPaidAmount() : BigDecimal.ZERO;
        if (paidAmount.compareTo(totalAmount) > 0) {
            paidAmount = totalAmount;
        }
        BigDecimal dueAmount = totalAmount.subtract(paidAmount);

        purchase.setTotalAmount(totalAmount);
        purchase.setPaidAmount(paidAmount);
        purchase.setDueAmount(dueAmount);

        if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
            purchase.setPaymentStatus(PaymentStatus.PAID);
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            purchase.setPaymentStatus(PaymentStatus.PARTIAL);
        } else {
            purchase.setPaymentStatus(PaymentStatus.UNPAID);
        }

        SupplierPurchase savedPurchase = purchaseRepository.save(purchase);

        // 1. Ledger Entry: Credit supplier for the entire purchase bill
        BigDecimal balanceAfterPurchase = supplier.getCurrentBalance().add(totalAmount);
        SupplierLedgerEntry purchaseLedger = new SupplierLedgerEntry(
                supplier,
                purchase.getPurchaseDate(),
                LedgerTransactionType.PURCHASE,
                invoiceNo,
                "Purchase Bill #" + invoiceNo + " (" + req.getItems().size() + " items)",
                BigDecimal.ZERO,
                totalAmount,
                balanceAfterPurchase,
                user
        );
        ledgerRepository.save(purchaseLedger);
        supplier.setCurrentBalance(balanceAfterPurchase);

        // 2. If payment made during purchase checkout, record payment and debit ledger
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal balanceAfterPayment = balanceAfterPurchase.subtract(paidAmount);

            SupplierPayment payment = new SupplierPayment();
            payment.setSupplier(supplier);
            payment.setPurchase(savedPurchase);
            payment.setAmount(paidAmount);
            payment.setPaymentDate(purchase.getPurchaseDate());
            payment.setPaymentMethod(req.getPaymentMethod() != null ? req.getPaymentMethod() : PaymentMethod.CASH);
            payment.setReferenceNumber(invoiceNo + "-PAY");
            payment.setNotes("Payment at purchase time for bill #" + invoiceNo);
            payment.setBalanceBeforePayment(balanceAfterPurchase);
            payment.setBalanceAfterPayment(balanceAfterPayment);
            payment.setCreatedBy(user);
            paymentRepository.save(payment);

            SupplierLedgerEntry paymentLedger = new SupplierLedgerEntry(
                    supplier,
                    purchase.getPurchaseDate(),
                    LedgerTransactionType.PAYMENT,
                    invoiceNo + "-PAY",
                    "Payment for Bill #" + invoiceNo + " via " + payment.getPaymentMethod().name(),
                    paidAmount,
                    BigDecimal.ZERO,
                    balanceAfterPayment,
                    user
            );
            ledgerRepository.save(paymentLedger);
            supplier.setCurrentBalance(balanceAfterPayment);
        }

        supplierRepository.save(supplier);

        auditLogService.log(
                AuditAction.CREATE_PURCHASE,
                "Recorded purchase bill #" + invoiceNo + " from supplier " + supplier.getName() + " for total " + totalAmount,
                request
        );

        return new PurchaseResponseDto(savedPurchase);
    }

    @Transactional
    public SupplierPayment recordPayment(SupplierPaymentRequest req, UserPrincipal principal, HttpServletRequest request) {
        Supplier supplier = getSupplierEntity(req.getSupplierId());
        User user = principal != null ? userRepository.findById(principal.getId()).orElse(null) : null;

        BigDecimal balanceBefore = supplier.getCurrentBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(req.getAmount());

        SupplierPayment payment = new SupplierPayment();
        payment.setSupplier(supplier);
        if (req.getPurchaseId() != null) {
            purchaseRepository.findById(req.getPurchaseId()).ifPresent(payment::setPurchase);
        }
        payment.setAmount(req.getAmount());
        payment.setPaymentDate(req.getPaymentDate() != null ? req.getPaymentDate() : LocalDateTime.now());
        payment.setPaymentMethod(req.getPaymentMethod() != null ? req.getPaymentMethod() : PaymentMethod.CASH);
        payment.setReferenceNumber(req.getReferenceNumber() != null ? req.getReferenceNumber() : "PAY-" + System.currentTimeMillis() % 1000000);
        payment.setNotes(req.getNotes());
        payment.setBalanceBeforePayment(balanceBefore);
        payment.setBalanceAfterPayment(balanceAfter);
        payment.setCreatedBy(user);

        SupplierPayment savedPayment = paymentRepository.save(payment);

        // Update supplier balance
        supplier.setCurrentBalance(balanceAfter);
        supplierRepository.save(supplier);

        // Add to supplier ledger (Debit)
        SupplierLedgerEntry ledgerEntry = new SupplierLedgerEntry(
                supplier,
                payment.getPaymentDate(),
                LedgerTransactionType.PAYMENT,
                payment.getReferenceNumber(),
                "Payment to supplier via " + payment.getPaymentMethod().name() + (req.getNotes() != null ? " - " + req.getNotes() : ""),
                req.getAmount(),
                BigDecimal.ZERO,
                balanceAfter,
                user
        );
        ledgerRepository.save(ledgerEntry);

        auditLogService.log(
                AuditAction.RECORD_SUPPLIER_PAYMENT,
                "Paid " + req.getAmount() + " to supplier " + supplier.getName() + " (Ref: " + payment.getReferenceNumber() + "). New balance: " + balanceAfter,
                request
        );

        return savedPayment;
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(PurchaseResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PurchaseResponseDto getPurchaseById(Long id) {
        SupplierPurchase purchase = purchaseRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + id));
        return new PurchaseResponseDto(purchase);
    }

    @Transactional(readOnly = true)
    public List<SupplierPayment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SupplierPayment> getSupplierPayments(Long supplierId) {
        return paymentRepository.findBySupplierIdOrderByPaymentDateDesc(supplierId);
    }

    @Transactional(readOnly = true)
    public List<LedgerResponseDto> getSupplierLedger(Long supplierId) {
        return ledgerRepository.findBySupplierIdOrderByTransactionDateAscIdAsc(supplierId).stream()
                .map(LedgerResponseDto::new)
                .collect(Collectors.toList());
    }
}
