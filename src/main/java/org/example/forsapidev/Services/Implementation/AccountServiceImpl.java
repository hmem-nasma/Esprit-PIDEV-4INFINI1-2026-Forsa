package org.example.forsapidev.Services.Implementation;

import org.example.forsapidev.Repositories.AccountRepository;
import org.example.forsapidev.Repositories.ActivityRepository;
import org.example.forsapidev.Repositories.TransactionRepository;
import org.example.forsapidev.Repositories.WalletRepository;
import org.example.forsapidev.Services.Interfaces.AccountService;
import org.example.forsapidev.entities.WalletManagement.Account;
import org.example.forsapidev.entities.WalletManagement.AccountStatus;
import org.example.forsapidev.entities.WalletManagement.AccountType;
import org.example.forsapidev.entities.WalletManagement.Activity;
import org.example.forsapidev.entities.WalletManagement.Transaction;
import org.example.forsapidev.entities.WalletManagement.TransactionType;
import org.example.forsapidev.entities.WalletManagement.Wallet;
import org.example.forsapidev.DTO.WalletStatisticsDTO;import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final WalletRepository walletRepo;
    private final TransactionRepository transactionRepo;
    private final ActivityRepository activityRepo;

    public AccountServiceImpl(AccountRepository accountRepo,
                              WalletRepository walletRepo,
                              TransactionRepository transactionRepo,
                              ActivityRepository activityRepo) {
        this.accountRepo = accountRepo;
        this.walletRepo = walletRepo;
        this.transactionRepo = transactionRepo;
        this.activityRepo = activityRepo;
    }

    // 1. Create Account
    @Override
    @Transactional
    public Account createAccount(Long ownerId, String type) {

        Wallet wallet = new Wallet();
        wallet.setOwnerId(ownerId);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepo.save(wallet);

        Account account = new Account();
        account.setWallet(wallet);

        if (type.equalsIgnoreCase("INVESTMENT")) {
            account.setType(AccountType.INVESTMENT);
            account.setStatus(AccountStatus.BLOCKED);
        } else {
            account.setType(AccountType.SAVINGS);
            account.setStatus(AccountStatus.ACTIVE);
        }

        Account savedAccount = accountRepo.save(account);
        logActivity(wallet, "Account created of type: " + type);
        return savedAccount;
    }

    // 2. Deposit
    @Override
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new RuntimeException("Account is blocked");
        }

        Wallet wallet = account.getWallet();
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepo.save(wallet);

        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setDate(LocalDateTime.now());
        tx.setType(TransactionType.DEPOSIT);
        tx.setWallet(wallet);
        transactionRepo.save(tx);

        logActivity(wallet, "Deposit of " + amount);
    }

    // 3. Withdraw
    @Override
    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }

        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new RuntimeException("Account is blocked");
        }

        Wallet wallet = account.getWallet();

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepo.save(wallet);

        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setDate(LocalDateTime.now());
        tx.setType(TransactionType.WITHDRAW);
        tx.setWallet(wallet);
        transactionRepo.save(tx);

        logActivity(wallet, "Withdrawal of " + amount);
    }

    // 4. Monthly Interest (0.1%)
    @Override
    @Transactional
    public void applyMonthlyInterest() {

        List<Account> accounts = accountRepo.findAll();

        for (Account account : accounts) {
            if (account.getType() == AccountType.INVESTMENT) {

                Wallet wallet = account.getWallet();
                BigDecimal interest = wallet.getBalance()
                        .multiply(new BigDecimal("0.001"));

                wallet.setBalance(wallet.getBalance().add(interest));
                walletRepo.save(wallet);

                Transaction tx = new Transaction();
                tx.setAmount(interest);
                tx.setDate(LocalDateTime.now());
                tx.setType(TransactionType.INTEREST);
                tx.setWallet(wallet);
                transactionRepo.save(tx);

                logActivity(wallet, "Monthly interest applied: " + interest);
            }
        }
    }

    // 5. Transfer
    @Override
    @Transactional
    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }

        if (fromWalletId.equals(toWalletId)) {
            throw new RuntimeException("Cannot transfer to the same wallet");
        }

        Wallet from = walletRepo.findById(fromWalletId)
                .orElseThrow(() -> new RuntimeException("Source wallet not found"));
        Wallet to = walletRepo.findById(toWalletId)
                .orElseThrow(() -> new RuntimeException("Destination wallet not found"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        walletRepo.save(from);
        walletRepo.save(to);

        Transaction txOut = new Transaction();
        txOut.setAmount(amount);
        txOut.setDate(LocalDateTime.now());
        txOut.setType(TransactionType.TRANSFER_OUT);
        txOut.setWallet(from);
        transactionRepo.save(txOut);

        Transaction txIn = new Transaction();
        txIn.setAmount(amount);
        txIn.setDate(LocalDateTime.now());
        txIn.setType(TransactionType.TRANSFER_IN);
        txIn.setWallet(to);
        transactionRepo.save(txIn);

        logActivity(from, "Transfer sent: " + amount + " to wallet " + toWalletId);
        logActivity(to, "Transfer received: " + amount + " from wallet " + fromWalletId);
    }

    // 6. Statistics
    @Override
    public WalletStatisticsDTO getStatistics(Long walletId) {

        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal deposits = transactionRepo.sumByWalletAndType(walletId, TransactionType.DEPOSIT);
        BigDecimal withdrawals = transactionRepo.sumByWalletAndType(walletId, TransactionType.WITHDRAW);
        int totalCount = wallet.getTransactions() != null ? wallet.getTransactions().size() : 0;

        return new WalletStatisticsDTO(
                wallet.getBalance(),
                deposits,
                withdrawals,
                totalCount
        );
    }

    // 7. Filter Transactions
    @Override
    public List<Transaction> filterTransactions(Long walletId, TransactionType type) {

        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return wallet.getTransactions()
                .stream()
                .filter(t -> t.getType() == type)
                .toList();
    }

    // 8. Activities
    @Override
    public List<Activity> getActivities(Long walletId) {
        return activityRepo.findByWallet_Id(walletId)
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .toList();
    }

    // Helper
    private void logActivity(Wallet wallet, String action) {
        Activity activity = new Activity();
        activity.setAction(action);
        activity.setTimestamp(LocalDateTime.now());
        activity.setWallet(wallet);
        activityRepo.save(activity);
    }
}