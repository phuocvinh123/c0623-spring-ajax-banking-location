package com.cg.service.transfer;

import com.cg.model.Transfer;
import com.cg.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class TransferServiceImpl implements ITransferService{
    @Autowired
    TransferRepository transferRepository;
    @Override
    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }

    @Override
    public Optional<Transfer> findById(Long id) {
        return null;
    }

    @Override
    public void save(Transfer transfer) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
