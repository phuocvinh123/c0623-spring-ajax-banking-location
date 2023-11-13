package com.cg.service.customer;

import com.cg.model.*;
import com.cg.model.dto.request.TransferReqDTO;
import com.cg.model.dto.response.CustomerResDTO;
import com.cg.model.dto.request.CustomerUpReqDTO;
import com.cg.model.dto.response.RecipientWithOutSenderDTO;
import com.cg.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LocationRegionRepository locationRegionRepository;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private WithdrawRepository withdrawRepository;

    @Autowired
    private TransferRepository transferRepository;


    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public List<Customer> findAllByIdNot(Long id) {
        return customerRepository.findAllByIdNot(id);
    }

    @Override
    public List<CustomerResDTO> findAllCustomerResDTO() {
        return customerRepository.findAllCustomerResDTO();
    }

    @Override
    public List<RecipientWithOutSenderDTO> findAllRecipientWithOutSenderDTO(Long customerId) {
        return customerRepository.findAllRecipientWithOutSenderDTO(customerId);
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public void create(Customer customer) {
        LocationRegion locationRegion = customer.getLocationRegion();
        locationRegionRepository.save(locationRegion);

        customer.setLocationRegion(locationRegion);
        customerRepository.save(customer);
    }


    @Override
    public void update(Long customerId, Long locationRegionId, CustomerUpReqDTO customerUpReqDTO) {
        Customer customer = customerUpReqDTO.toCustomer(customerId);

        LocationRegion locationRegion = customer.getLocationRegion();
        locationRegion.setId(locationRegionId);
        locationRegionRepository.save(locationRegion);

        customer.setId(customerId);
        customerRepository.save(customer);
    }

    @Override
    public void deposit(Deposit deposit) {
        depositRepository.save(deposit);
        customerRepository.incrementBalance(deposit.getCustomer().getId(), deposit.getTransactionAmount());
    }

    @Override
    public void withdraw(Withdraw withdraw) {
        withdrawRepository.save(withdraw);
        customerRepository.decrementBalance(withdraw.getCustomer().getId(),withdraw.getTransactionAmount());


    }

    @Override
    public void transfer(TransferReqDTO transferReqDTO) {
        Long senderId = Long.valueOf(transferReqDTO.getSenderId());
        Long recipientId = Long.valueOf(transferReqDTO.getRecipientId());
        String transferAmountStr = transferReqDTO.getTransferAmount();
        BigDecimal transferAmount = BigDecimal.valueOf(Long.parseLong(transferAmountStr));
        Long fee = 10L;

        BigDecimal feeAmount = transferAmount.multiply(BigDecimal.valueOf(fee)).divide(BigDecimal.valueOf(100));
        BigDecimal transactionAmount = transferAmount.add(feeAmount);

        customerRepository.decrementBalance(senderId, transactionAmount);
        customerRepository.incrementBalance(recipientId, transferAmount);

        Optional<Customer> sender = customerRepository.findById(senderId);
        Optional<Customer> recipient = customerRepository.findById(recipientId);

        Transfer transfer = new Transfer(sender.get(),recipient.get(),transferAmount,fee,feeAmount,transactionAmount);

        transferRepository.save(transfer);

    }



    @Override
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }
}
