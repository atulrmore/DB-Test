package com.db.awmd.challenge.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.OperationFailedException;

import lombok.Synchronized;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		Account account = accounts.get(accountId);
		if(account == null){
			throw new InvalidAccountException("Account id " + accountId + " is invalid!");
		}
		return account;
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public void setAccount(String accountId, Account oldAccountDetails, Account newAccountDetails) {
		/*boolean result = accounts.replace(accountId, oldAccountDetails, newAccountDetails);
		if(!result){
			oldAccountDetails = getAccount(accountId);
			result = accounts.replace(accountId, oldAccountDetails, newAccountDetails);
			if(!result){
				throw new OperationFailedException("Transfer operation failed for account "+accountId+".");
			}
		}*/
	}

	@Override
	public void setAccount(String accountId, Account account) {
		accounts.replace(accountId, account);
	}

	
}
