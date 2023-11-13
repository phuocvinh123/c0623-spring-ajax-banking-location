package com.cg.service.customer;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.model.dto.request.TransferReqDTO;
import com.cg.model.dto.response.CustomerResDTO;
import com.cg.model.dto.request.CustomerUpReqDTO;
import com.cg.model.dto.response.RecipientWithOutSenderDTO;
import com.cg.service.IGeneralService;

import java.util.List;

public interface ICustomerService extends IGeneralService<Customer, Long> {

    List<Customer> findAllByIdNot(Long id);

    List<CustomerResDTO> findAllCustomerResDTO();

    List<RecipientWithOutSenderDTO> findAllRecipientWithOutSenderDTO(Long customerId);

    void create(Customer customer);

    void update(Long customerId, Long locationRegionId, CustomerUpReqDTO customer);

    void deposit(Deposit deposit);

    void withdraw(Withdraw withdraw);

    void transfer(TransferReqDTO transferReqDTO);

}
