package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.CashBookDTO;
import com.BookFlow.bookflow.dto.CashBookSummaryDTO;
import com.BookFlow.bookflow.model.CashBook;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.CashBookRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.utils.classes.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CashBookService {

    @Autowired
    private CashBookRepo cashBookRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserContextUtil userContextUtil;




    public Page<CashBookDTO> getAllTransactions(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );
        Page<CashBook> cashBooks = cashBookRepo.findByCompany(userContextUtil.getCurrentUserCompany(),pageable);
        return cashBooks.map(this::mapToDTO);
    }


    public boolean existsByVoucherNumber(String voucherNumber) {
        return cashBookRepo.existsByVoucherNumber(voucherNumber);
    }


    @Transactional
    public CashBookDTO addTransaction(CashBookDTO transactionDTO) {
        Company company = userContextUtil.getCurrentUserCompany();
        CashBook cashBook = mapToEntity(transactionDTO);

        BigDecimal previousBalance = getCurrentBalance(company);
        BigDecimal newBalance = previousBalance
                .add(cashBook.getReceiptAmount() != null ? cashBook.getReceiptAmount() : BigDecimal.ZERO)
                .subtract(cashBook.getPaymentAmount() != null ? cashBook.getPaymentAmount() : BigDecimal.ZERO);

        cashBook.setBalance(newBalance);
        cashBook.setCompany_id(company);

        CashBook savedTransaction = cashBookRepo.save(cashBook);
        return mapToDTO(savedTransaction);
    }

    public BigDecimal getCurrentBalance(Company company) {
        return cashBookRepo.findTopByCompanyOrderByIdDesc(company)
                .map(CashBook::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    public Optional<CashBookDTO> getTransactionById(Long id) {
        Company company = userContextUtil.getCurrentUserCompany();
        return cashBookRepo.findById(id)
                .filter(t -> t.getCompany_id().equals(company))
                .map(this::mapToDTO);
    }


    @Transactional
    public CashBookDTO updateTransaction(Long id, CashBookDTO transactionDTO) {
        Company company = userContextUtil.getCurrentUserCompany();

        try {
            CashBook existingTransaction = cashBookRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

            Company transactionCompany = existingTransaction.getCompany_id();

            if (!transactionCompany.getCompany_id().equals(company.getCompany_id())) {
                throw new RuntimeException("Transaction not found with id: " + id + " for current company");
            }

            BigDecimal oldReceiptAmount = existingTransaction.getReceiptAmount() != null ?
                    existingTransaction.getReceiptAmount() : BigDecimal.ZERO;
            BigDecimal oldPaymentAmount = existingTransaction.getPaymentAmount() != null ?
                    existingTransaction.getPaymentAmount() : BigDecimal.ZERO;

            // Update other fields
            existingTransaction.setDate(transactionDTO.getDate() != null ?
                    transactionDTO.getDate() : existingTransaction.getDate());
            existingTransaction.setVoucherNumber(transactionDTO.getVoucherNumber() != null ?
                    transactionDTO.getVoucherNumber() : existingTransaction.getVoucherNumber());
            existingTransaction.setDescription(transactionDTO.getDescription() != null ?
                    transactionDTO.getDescription() : existingTransaction.getDescription());
            existingTransaction.setCategory(transactionDTO.getCategory() != null ?
                    transactionDTO.getCategory() : existingTransaction.getCategory());

            if (transactionDTO.getReceiptAmount() != null) {
                existingTransaction.setReceiptAmount(transactionDTO.getReceiptAmount());
            }
            if (transactionDTO.getPaymentAmount() != null) {
                existingTransaction.setPaymentAmount(transactionDTO.getPaymentAmount());
            }

            existingTransaction.setReimbursementPending(transactionDTO.isReimbursementPending());

            recalculateBalances(existingTransaction, oldReceiptAmount, oldPaymentAmount);

            CashBook updatedTransaction = cashBookRepo.save(existingTransaction);
            return mapToDTO(updatedTransaction);
        } catch (Exception e) {
            System.out.println("Error in updateTransaction: " + e.getMessage());
            throw e;
        }
    }



    public Page<CashBookDTO> searchTransactions(String query, LocalDate fromDate, LocalDate toDate, int page, int size) {
        Company company = userContextUtil.getCurrentUserCompany();
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        boolean isQueryEmpty = (query == null || query.trim().isEmpty());
        boolean isFromDateProvided = (fromDate != null);
        boolean isToDateProvided = (toDate != null);

        Page<CashBook> transactions;

        if (!isQueryEmpty && isFromDateProvided && isToDateProvided) {
            transactions = cashBookRepo.findByCompanyAndSearchQueryAndDateRange(company, query, fromDate, toDate, pageable);
        } else if (!isQueryEmpty) {
            transactions = cashBookRepo.findByCompanyAndSearchQuery(company, query, pageable);
        } else if (isFromDateProvided && isToDateProvided) {
            transactions = cashBookRepo.findByCompanyAndDateRange(company, fromDate, toDate, pageable);
        } else {
            transactions = cashBookRepo.findByCompany(company, pageable);
        }

        return transactions.map(this::mapToDTO);
    }

    public byte[] exportTransactionsToCSV(String query, LocalDate fromDate, LocalDate toDate) throws IOException {
        Company company = userContextUtil.getCurrentUserCompany();
        List<CashBook> transactions;

        if (query != null && !query.trim().isEmpty() && fromDate != null && toDate != null) {
            transactions = cashBookRepo.findByCompanyAndSearchQueryAndDateRangeNoPage(company, query, fromDate, toDate);
        } else if (query != null && !query.trim().isEmpty()) {
            transactions = cashBookRepo.findByCompanyAndSearchQueryNoPage(company, query);
        } else if (fromDate != null && toDate != null) {
            transactions = cashBookRepo.findByCompanyAndDateRangeNoPage(company, fromDate, toDate);
        } else {
            transactions = cashBookRepo.findByCompanyNoPage(company);
        }

        StringWriter sw = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.withHeader(
                "Date", "Voucher Number", "Description", "Category",
                "Receipt Amount", "Payment Amount", "Balance", "Reimbursement Pending"
        ));

        for (CashBook transaction : transactions) {
            csvPrinter.printRecord(
                    transaction.getDate(),
                    transaction.getVoucherNumber(),
                    transaction.getDescription(),
                    transaction.getCategory(),
                    transaction.getReceiptAmount(),
                    transaction.getPaymentAmount(),
                    transaction.getBalance(),
                    transaction.isReimbursementPending()
            );
        }

        csvPrinter.flush();
        return sw.toString().getBytes(StandardCharsets.UTF_8);
    }


    public CashBookSummaryDTO getTransactionSummary() {
        Company company = userContextUtil.getCurrentUserCompany();
        CashBookSummaryDTO summary = new CashBookSummaryDTO();
        BigDecimal currentBalance = cashBookRepo.findLatestBalance(company)
                .orElse(BigDecimal.ZERO);
        summary.setCurrentBalance(currentBalance);

        LocalDate today = LocalDate.now();
        BigDecimal receiptsToday = cashBookRepo.findTotalReceiptsForToday(today, company);
        BigDecimal paymentsToday = cashBookRepo.findTotalPaymentsForToday(today, company);

        summary.setTotalReceiptsToday(receiptsToday);
        summary.setTotalPaymentsToday(paymentsToday);
        BigDecimal pendingReimbursements = cashBookRepo.findTotalPendingReimbursements(company);
        summary.setPendingReimbursements(pendingReimbursements);
        return summary;
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Company company = userContextUtil.getCurrentUserCompany();
        CashBook transaction = cashBookRepo.findById(id)
                .filter(t -> t.getCompany_id().equals(company))
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        BigDecimal deletedTransactionBalance = transaction.getBalance();
        cashBookRepo.deleteById(id);

        List<CashBook> subsequentTransactions = cashBookRepo.findByCompanyAndIdGreaterThan(company, id);

        if (subsequentTransactions.isEmpty()) {
            return;
        }

        BigDecimal previousBalance = cashBookRepo.findTopByCompanyAndIdLessThan(company, id)
                .map(CashBook::getBalance)
                .orElse(BigDecimal.ZERO);

        BigDecimal runningBalance = previousBalance;
        for (CashBook t : subsequentTransactions) {
            runningBalance = runningBalance
                    .add(t.getReceiptAmount() != null ? t.getReceiptAmount() : BigDecimal.ZERO)
                    .subtract(t.getPaymentAmount() != null ? t.getPaymentAmount() : BigDecimal.ZERO);
            t.setBalance(runningBalance);
        }

        cashBookRepo.saveAll(subsequentTransactions);
    }

    public void recalculateBalances(CashBook updatedTransaction,
                                     BigDecimal oldReceiptAmount,
                                     BigDecimal oldPaymentAmount) {
        Company company = userContextUtil.getCurrentUserCompany();
        BigDecimal netChange = updatedTransaction.getReceiptAmount()
                .subtract(oldReceiptAmount)
                .subtract(updatedTransaction.getPaymentAmount())
                .add(oldPaymentAmount);

        if (netChange.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        List<CashBook> subsequentTransactions = cashBookRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .filter(t -> t.getCompany_id().equals(company))
                .filter(t -> t.getId() >= updatedTransaction.getId())
                .toList();

        BigDecimal runningBalance = updatedTransaction.getBalance();

        for (CashBook t : subsequentTransactions) {
            if (t.getId().equals(updatedTransaction.getId())) {
                continue;
            }

            runningBalance = runningBalance
                    .add(t.getReceiptAmount())
                    .subtract(t.getPaymentAmount());

            t.setBalance(runningBalance);
            cashBookRepo.save(t);
        }
    }


    public void recalculateBalancesAfterDelete(Long deletedId,
                                                BigDecimal deletedReceiptAmount,
                                                BigDecimal deletedPaymentAmount,
                                                Company company) {
        List<CashBook> subsequentTransactions = cashBookRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .filter(t -> t.getCompany_id().equals(company))
                .filter(t -> t.getId() > deletedId)
                .toList();

        if (subsequentTransactions.isEmpty()) {
            return;
        }

        Optional<CashBook> previousTransaction = cashBookRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .filter(t -> t.getCompany_id().equals(company))
                .filter(t -> t.getId() < deletedId)
                .findFirst();

        BigDecimal previousBalance = previousTransaction.map(CashBook::getBalance).orElse(BigDecimal.ZERO);

        // Corrected adjustment calculation
        BigDecimal adjustment = deletedReceiptAmount.subtract(deletedPaymentAmount);
        BigDecimal runningBalance = previousBalance.subtract(adjustment);

        for (CashBook t : subsequentTransactions) {
            runningBalance = runningBalance
                    .add(t.getReceiptAmount())
                    .subtract(t.getPaymentAmount());

            t.setBalance(runningBalance);
            cashBookRepo.save(t);
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("M/d/yyyy"));
            } catch (DateTimeParseException ex) {
                log.error("Could not parse date: " + dateStr, ex);
                throw ex;
            }
        }
    }

    public List<CashBookDTO> importCsv(MultipartFile file) throws IOException {
        List<CashBookDTO> importedTransactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());

            for (CSVRecord record : csvParser) {
                CashBookDTO transaction = new CashBookDTO();

                try {
                    transaction.setDate(parseDate(record.get("Date")));

                    transaction.setVoucherNumber(record.get("Voucher Number"));
                    transaction.setDescription(record.get("Description"));
                    transaction.setCategory(record.get("Category"));

                    String receiptStr = record.get("Receipt Amount");
                    String paymentStr = record.get("Payment Amount");

                    transaction.setReceiptAmount(receiptStr.isEmpty() ? BigDecimal.ZERO :
                            new BigDecimal(receiptStr.replace("₹", "").trim()));
                    transaction.setPaymentAmount(paymentStr.isEmpty() ? BigDecimal.ZERO :
                            new BigDecimal(paymentStr.replace("₹", "").trim()));

                    transaction.setReimbursementPending(Boolean.parseBoolean(record.get("Reimbursement Pending")));

                    // Save to database
                    CashBookDTO savedTransaction = addTransaction(transaction);
                    importedTransactions.add(savedTransaction);
                } catch (Exception e) {
                    log.error("Error parsing CSV record: " + record, e);
                }
            }
        }

        return importedTransactions;
    }


    public void validateCsvFormat(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV file is empty");
            }

            Set<String> requiredHeaders = Set.of(
                    "Date", "Voucher Number", "Description", "Category",
                    "Receipt Amount", "Payment Amount", "Reimbursement Pending"
            );

            Set<String> actualHeaders = Arrays.stream(headerLine.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

            if (!actualHeaders.containsAll(requiredHeaders)) {
                throw new IllegalArgumentException("CSV file is missing required headers");
            }
        }
    }

    public Map<String, BigDecimal> getMonthlyTransactionSummary() {
        List<Object[]> results = cashBookRepo.getMonthlyTransactionSummary(userContextUtil.getCurrentUserCompany().getCompany_id());
        Map<String, BigDecimal> summary = new HashMap<>();

        for (Object[] row : results) {
            String month = (String) row[0];
            BigDecimal totalAmount = (BigDecimal) row[1];
            summary.put(month, totalAmount);
        }
        return summary;
    }

    public Map<String, BigDecimal> getDailyTransactionSummary(LocalDate month) {
        Company company = userContextUtil.getCurrentUserCompany();

        int extractedMonth = month.getMonthValue();
        int extractedYear = month.getYear();

        List<Object[]> results = cashBookRepo.getDailyTransactionSummary(extractedMonth, extractedYear, company.getCompany_id());

        Map<String, BigDecimal> summary = new HashMap<>();
        for (Object[] row : results) {
            String day = String.format("%02d", ((Number)row[0]).intValue());
            BigDecimal totalAmount = (BigDecimal) row[1];
            summary.put(day, totalAmount);
        }
        return summary;
    }








    private CashBookDTO mapToDTO(CashBook cashbook) {
        return new CashBookDTO(
                cashbook.getId(),
                cashbook.getDate(),
                cashbook.getVoucherNumber(),
                cashbook.getDescription(),
                cashbook.getCategory(),
                cashbook.getReceiptAmount(),
                cashbook.getPaymentAmount(),
                cashbook.getBalance(),
                cashbook.isReimbursementPending()
        );
    }

    private CashBook mapToEntity(CashBookDTO dto) {
        CashBook cashBook = new CashBook();
        cashBook.setId(dto.getId());
        cashBook.setDate(dto.getDate());
        cashBook.setVoucherNumber(dto.getVoucherNumber());
        cashBook.setDescription(dto.getDescription());
        cashBook.setCategory(dto.getCategory());
        cashBook.setReceiptAmount(dto.getReceiptAmount());
        cashBook.setPaymentAmount(dto.getPaymentAmount());
        cashBook.setBalance(dto.getBalance());
        cashBook.setReimbursementPending(dto.isReimbursementPending());
        return cashBook;
    }


}
