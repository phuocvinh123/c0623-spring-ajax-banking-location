package com.cg.controller.rest;


import com.cg.exception.DataInputException;
import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.model.dto.request.*;
import com.cg.model.dto.response.CustomerResDTO;
import com.cg.model.dto.response.RecipientWithOutSenderDTO;
import com.cg.model.dto.response.TransferResDTO;
import com.cg.service.customer.ICustomerService;
import com.cg.service.transfer.TransferServiceImpl;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerResController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private TransferServiceImpl transferService;

    @Autowired
    private AppUtils appUtils;

    @GetMapping
    public ResponseEntity<?> getALl() {
        List<CustomerResDTO> customerResDTOS = customerService.findAllCustomerResDTO();

//        List<Customer> customers = customerService.findAll();
//        List<CustomerResDTO> customerResDTOS = new ArrayList<>();
//
//        for (Customer customer : customers) {
//            CustomerResDTO customerResDTO = customer.toCustomerResDTO();
//            customerResDTOS.add(customerResDTO);
//        }

        return new ResponseEntity<>(customerResDTOS, HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getById(@PathVariable Long customerId) {

        Customer customer = customerService.findById(customerId).orElseThrow(() -> {
            throw new DataInputException("Customer not found");
        });

        CustomerResDTO customerResDTO = customer.toCustomerResDTO();


        return new ResponseEntity<>(customerResDTO, HttpStatus.OK);
    }

    @GetMapping("/get-all-recipient-with-out-id/{senderId}")
    public ResponseEntity<?> getAllRecipientsWithOutId(@PathVariable Long senderId) {

        List<RecipientWithOutSenderDTO> recipients = customerService.findAllRecipientWithOutSenderDTO(senderId);

        return new ResponseEntity<>(recipients, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody CustomerCreReqDTO customerCreReqDTO, BindingResult bindingResult) {

        new CustomerCreReqDTO().validate(customerCreReqDTO, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Customer customer = customerCreReqDTO.toCustomer();
        customer.setBalance(BigDecimal.ZERO);
        customer.setDeleted(false);

        customerService.create(customer);

        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<?> update(@PathVariable Long customerId, @Validated @RequestBody CustomerUpReqDTO customerUpReqDTO, BindingResult bindingResult) {

        Customer customer = customerService.findById(customerId).orElseThrow(() -> {
           throw new DataInputException("Customer not found");
        });

        new CustomerUpReqDTO().validate(customerUpReqDTO, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        customerService.update(customerId, customer.getLocationRegion().getId(), customerUpReqDTO);

        customer = customerService.findById(customerId).get();

        return new ResponseEntity<>(customer.toCustomerResDTO(), HttpStatus.OK);
    }

    @PostMapping("/deposit/{customerId}")
    public ResponseEntity<?> deposit(@RequestBody DepositCreReqDTO depositCreReqDTO) {
        Optional<Customer> customer = customerService.findById(Long.valueOf(depositCreReqDTO.getCustomerId()));
        BigDecimal transactionAmount = BigDecimal.valueOf(Long.parseLong(String.valueOf(depositCreReqDTO.getTransactionAmount())));

        Deposit deposit = new Deposit();
        deposit.setCustomer(customer.get());
        deposit.setTransactionAmount(transactionAmount);

        customerService.deposit(deposit);
        Optional<Customer> updateCustomer = customerService.findById(deposit.getCustomer().getId());

        return new ResponseEntity<>(updateCustomer.get().toCustomerResDTO(), HttpStatus.OK);
    }

    @PostMapping("/withdraw/{customerId}")
    public ResponseEntity<?> withdraw(@RequestBody WithdrawReqDTO withdrawReqDTO) {
        Optional<Customer> customer = customerService.findById(Long.valueOf(withdrawReqDTO.getCustomerId()));
        BigDecimal transactionAmount = BigDecimal.valueOf(Long.parseLong(withdrawReqDTO.getTransactionAmount()));

        Withdraw withdraw = new Withdraw();
        withdraw.setCustomer(customer.get());
        withdraw.setTransactionAmount(transactionAmount);

        customerService.withdraw(withdraw);
        Optional<Customer> updateCustomer = customerService.findById(withdraw.getCustomer().getId());

        return new ResponseEntity<>(updateCustomer.get().toCustomerResDTO(), HttpStatus.OK);
    }
    @PostMapping("/transfer/{customerId}")
    public ResponseEntity<?> transfer(@PathVariable Long customerId, @RequestBody TransferReqDTO transferReqDTO) {

        Optional<Customer> senderOptional = customerService.findById(customerId);
        customerService.transfer(transferReqDTO);

        TransferResDTO transferResDTO = new TransferResDTO();

        Optional<Customer> recipientOptional = customerService.findById(Long.parseLong(transferReqDTO.getRecipientId()));


        CustomerResDTO sender = senderOptional.get().toCustomerResDTO();
        CustomerResDTO recipient = recipientOptional.get().toCustomerResDTO();

        transferResDTO.setSender(sender);
        transferResDTO.setRecipient(recipient);

        return new ResponseEntity<>(transferResDTO, HttpStatus.OK);
    }
    @GetMapping("/histories")
    public ResponseEntity<?> getAllHistories() {
        List<Transfer> histories = transferService.findAll();
        return new ResponseEntity<>(histories, HttpStatus.OK);
    }
}
