package com.db.awmd.challenge.web;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.exception.AmountInsufficientException;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.exception.OperationFailedException;
import com.db.awmd.challenge.service.TransferService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v2/transfer")
@Slf4j
public class TransferController {

	private static final Logger log = LoggerFactory.getLogger(TransferController.class);
	private final TransferService transferService;
	
	@Autowired
	public TransferController(TransferService transferService) {
		super();
		this.transferService = transferService;
	}

	@PostMapping(path = "/{fromAccountId}/{toAccountId}/{amount}")
	public ResponseEntity<Object> transferAmount(@PathVariable String fromAccountId, 
			@PathVariable String toAccountId, @PathVariable BigDecimal amount) {
		log.info("Amount receieved - "+amount);
		try {
			transferService.transferAmount(fromAccountId, toAccountId, amount);
		} catch (InvalidAccountException iae){
			return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (AmountInsufficientException aie){
			return new ResponseEntity<>(aie.getMessage(), HttpStatus.BAD_REQUEST);
		} catch(InvalidAmountException iae){
			return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);
		} catch(OperationFailedException ofe){
			return new ResponseEntity<>(ofe.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	
}
