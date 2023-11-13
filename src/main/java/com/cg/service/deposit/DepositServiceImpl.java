package com.cg.service.deposit;

import com.cg.model.Deposit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepositServiceImpl implements IDepositService {

    private final static List<Deposit> deposits = new ArrayList<>();

    private static Long id;

    @Override
    public List<Deposit> findAll() {
        return null;
    }

    @Override
    public Optional<Deposit> findById(Long id) {
        return null;
    }

    @Override
    public void save(Deposit deposit) {
        deposit.setId(id++);
        deposit.setDeleted(false);
        deposits.add(deposit);
    }

    @Override
    public void deleteById(Long id) {

    }
}
