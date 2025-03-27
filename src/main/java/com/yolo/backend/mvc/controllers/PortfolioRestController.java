package com.yolo.backend.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.backend.mvc.model.entity.Portfolio;
import com.yolo.backend.mvc.model.services.IPortfolioService;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioRestController {

    @Autowired
    private IPortfolioService portfolioService;

    @GetMapping("")
    public List<Portfolio> getUsers() {
        return portfolioService.findAll();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Portfolio createPortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.save(portfolio);
        return portfolio;
    }

    @GetMapping("/{userId}")
    public Portfolio getPortfolio(@PathVariable String userId) {
        return portfolioService.getPortfolioByUserId(userId);
    }
}
