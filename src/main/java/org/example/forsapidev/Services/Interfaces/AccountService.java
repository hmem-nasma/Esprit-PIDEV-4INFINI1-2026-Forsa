package org.example.forsapidev.Services.Interfaces;

import org.example.forsapidev.DTO.WalletStatisticsDTO;
import org.example.forsapidev.entities.WalletManagement.Account;
import org.example.forsapidev.entities.WalletManagement.Activity;
import org.example.forsapidev.entities.WalletManagement.Transaction;
import org.example.forsapidev.entities.WalletManagement.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account createAccount(Long ownerId, String type);

    void deposit(Long accountId, BigDecimal amount);

    void withdraw(Long accountId, BigDecimal amount);

    void applyMonthlyInterest();

    void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount);

    WalletStatisticsDTO getStatistics(Long walletId);

    List<Transaction> filterTransactions(Long walletId, TransactionType type);

    List<Activity> getActivities(Long walletId);
}