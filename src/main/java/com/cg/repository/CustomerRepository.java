package com.cg.repository;

import com.cg.model.Customer;
import com.cg.model.dto.response.CustomerResDTO;
import com.cg.model.dto.response.RecipientWithOutSenderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findAllByIdNot(Long id);

    @Query("SELECT NEW com.cg.model.dto.response.CustomerResDTO ( " +
                "cus.id, " +
                "cus.fullName, " +
                "cus.email, " +
                "cus.phone, " +
                "cus.balance, " +
                "cus.locationRegion" +
            ") " +
            "FROM Customer AS cus"
    )
    List<CustomerResDTO> findAllCustomerResDTO();


    @Query("SELECT NEW com.cg.model.dto.response.RecipientWithOutSenderDTO (" +
                "cus.id, " +
                "cus.fullName" +
            ") " +
            "FROM Customer AS cus " +
            "WHERE cus.id <> :customerId"
    )
    List<RecipientWithOutSenderDTO> findAllRecipientWithOutSenderDTO(@Param("customerId") Long customerId);

    @Modifying
    @Query("UPDATE Customer AS cus " +
            "SET cus.balance = cus.balance + :transactionAmount " +
            "WHERE cus.id = :customerId"
    )
    void incrementBalance(@Param("customerId") Long customerId, @Param("transactionAmount")BigDecimal transactionAmount);

    @Modifying
    @Query("UPDATE Customer AS cus " +
            "SET cus.balance = cus.balance - :transactionAmount " +
            "WHERE cus.id = :customerId"
    )
    void decrementBalance(@Param("customerId") Long customerId, @Param("transactionAmount") BigDecimal transactionAmount);

}
