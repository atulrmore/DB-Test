package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.OperationFailedException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.util.AppUtil;

import lombok.Getter;
import lombok.Synchronized;

@Service
public class TransferService {
	
	private static final Logger log = LoggerFactory.getLogger(TransferService.class);
	@Autowired private AppUtil appUtil;
	@Autowired private NotificationService notificationService;

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public TransferService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}
	
	public void transferAmount(String fromAccountId, String toAccountId, BigDecimal amountStr){
		//Validate input data
		appUtil.validateAmount(amountStr);
		this.accountsRepository.getAccount(fromAccountId);
		this.accountsRepository.getAccount(toAccountId);
		doAmountTransfer(fromAccountId, toAccountId, amountStr);
	}
	
	@Synchronized
	private void doAmountTransfer(String fromAccountId, String toAccountId, BigDecimal amountStr){
		//Fetach the account data, create copy of account data in case of failure replace the new data with existing data
		Account fromAccount = this.accountsRepository.getAccount(fromAccountId); 
		Account fromAccountCopy = new Account(fromAccount);
		Account toAccount = this.accountsRepository.getAccount(toAccountId);
		Account toAccountCopy = new Account(toAccount);
		doCalculationAndSetAmount(fromAccount, toAccount, amountStr);
		try {
			accountsRepository.setAccount(fromAccountId, fromAccount);
			accountsRepository.setAccount(toAccountId, toAccount);
			sendNotifications(fromAccount, toAccount, amountStr);
		} catch (RuntimeException e) {
			doRollback(fromAccountId, toAccountId, fromAccountCopy, toAccountCopy);
			throw new OperationFailedException("Transfer operation failed for account "+fromAccountId+" and "+toAccountId+".");
		}
		log.info("Balance from account {}", fromAccount);
		log.info("Balance to account {}", toAccount);
	}
	
	private void doCalculationAndSetAmount(Account fromAccount, Account toAccount, BigDecimal amount){
		fromAccount.setBalance(appUtil.substractAmount(fromAccount.getBalance(), amount, fromAccount.getAccountId()));
		toAccount.setBalance(appUtil.addAmount(toAccount.getBalance(), amount));
	}

	private void doRollback(String fromAccountId, String toAccountId, Account fromAccount, Account toAccount){
		accountsRepository.setAccount(fromAccountId, fromAccount);
		accountsRepository.setAccount(toAccountId, toAccount);
	}
	
	private void sendNotifications(Account fromAccount, Account toAccount, BigDecimal amountStr){
		notificationService.notifyAboutTransfer(fromAccount, amountStr+" transfer from your account successfully.!");
		notificationService.notifyAboutTransfer(toAccount, amountStr+" transfer to your account successfully.!");
	}
	
}
