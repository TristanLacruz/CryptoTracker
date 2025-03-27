package com.yolo.backend.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.backend.mvc.model.entity.Transaction;
import com.yolo.backend.mvc.model.services.ITransactionService;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

	@Autowired
	private ITransactionService transactionService;
	
	@GetMapping
	public List<Transaction> getTransaction(){
		return transactionService.findAll();
	}
	
	@PostMapping("/transaction")
	@ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(@RequestBody Transaction transaction) {
		transactionService.save(transaction);
        return transaction;
    }
	
	@PostMapping("/buy")
    public Transaction buyCrypto(@RequestParam String userId,
                                 @RequestParam String symbol,
                                 @RequestParam double amountUSD) {
        return transactionService.buyCrypto(userId, symbol, amountUSD);
    }

    @PostMapping("/sell")
    public Transaction sellCrypto(@RequestParam String userId,
                                  @RequestParam String symbol,
                                  @RequestParam double amountUSD) {
        return transactionService.sellCrypto(userId, symbol, amountUSD);
    }

}
