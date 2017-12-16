package com.db.awmd.challenge.util;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.db.awmd.challenge.exception.AmountInsufficientException;
import com.db.awmd.challenge.exception.InvalidAmountException;

@Component
public class AppUtil {
	
	public BigDecimal convertAndGetAmount(String amount) {
		if(amount == null || "".equals(amount.trim())){
			throw new InvalidAmountException("Amount must be in numbers.");
		}
		return BigDecimal.valueOf(Double.valueOf(amount));
	}
	
	public void validateAmount(BigDecimal amount) {
		if(amount.doubleValue() < 0.0){
			throw new InvalidAmountException("Amount must be positive number.");
		}
	}
	
	public BigDecimal substractAmount(BigDecimal fromAmount, BigDecimal toAmount, String accountId){
		validateAmount(fromAmount);
		validateAmount(toAmount);
		Double finalAmount = fromAmount.doubleValue() - toAmount.doubleValue();
		if(finalAmount < 0.0){
			throw new AmountInsufficientException("Insufficient amount in the account "+accountId+".");
		}
		return BigDecimal.valueOf(finalAmount);
	}
	
	public BigDecimal addAmount(BigDecimal toAmount, BigDecimal amount){
		validateAmount(amount);
		Double finalAmount = toAmount.doubleValue() + amount.doubleValue();
		return BigDecimal.valueOf(finalAmount);
	}
	
}
