package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.service.customer.CustomerServiceImpl;
import com.cg.service.customer.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @GetMapping
    public String showListPage(Model model) {
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);

        return "customer/list";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("customer", new Customer());

        return "customer/create";
    }

    @GetMapping("/edit/{customerId}")
    public String showUpdatePage(@PathVariable Long customerId, Model model) {
        Optional<Customer> customer = customerService.findById(customerId);
        model.addAttribute("customer", customer.get());

        return "customer/edit";
    }

    @GetMapping("/delete/{customerId}")
    public String showCreatePage(@PathVariable Long customerId, Model model) {
//        Customer customer = customerService.findById(customerId);
//        model.addAttribute("customer", customer);

        return "customer/delete";
    }

    @GetMapping("/deposit/{customerId}")
    public String showDepositPage(@PathVariable Long customerId, Model model) {

        Optional<Customer> customer = customerService.findById(customerId);
        Deposit deposit = new Deposit();
        deposit.setCustomer(customer.get());

        model.addAttribute("deposit", deposit);

        return "customer/deposit";
    }

    @GetMapping("/withdraw/{customerId}")
    public String showWithdrawPage(@PathVariable Long customerId, Model model) {

        Optional<Customer> customer = customerService.findById(customerId);
        Withdraw withdraw = new Withdraw();
        withdraw.setCustomer(customer.get());

        model.addAttribute("withdraw", withdraw);

        return "customer/withdraw";
    }

    @GetMapping("/transfer/{senderId}")
    public String showTransferPage(@PathVariable Long senderId, Model model) {

        Optional<Customer> sender = customerService.findById(senderId);

        List<Customer> recipients = customerService.findAllByIdNot(senderId);
//
        Transfer transfer = new Transfer();
        transfer.setSender(sender.get());
//
        model.addAttribute("transfer", transfer);
        model.addAttribute("recipients", recipients);

        return "customer/transfer";
    }

    @PostMapping("/create")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {

        if (customer.getFullName().length() == 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Created unsuccessful");
        }
        else {
            customerService.save(customer);

            model.addAttribute("customer", new Customer());

            model.addAttribute("success", true);
            model.addAttribute("message", "Created successfully");
        }

        return "customer/create";
    }

    @PostMapping("/edit/{customerId}")
    public String updateCustomer(@PathVariable Long customerId, @ModelAttribute Customer customer, Model model) {

        customer.setId(customerId);

        customerService.save(customer);

        model.addAttribute("success", true);
        model.addAttribute("message", "Updated successful");

        model.addAttribute("customer", customer);

        return "customer/edit";
    }

    @PostMapping("/delete/{customerId}")
    public String deleteCustomer(@PathVariable Long customerId, RedirectAttributes redirectAttributes) {

        customerService.deleteById(customerId);

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "Deleted successfully");

        return "redirect:/customers";
    }

    @PostMapping("/deposit/{customerId}")
    public String deposit(@PathVariable Long customerId, @ModelAttribute Deposit deposit, Model model) {

        Optional<Customer> customer = customerService.findById(customerId);
//
        deposit.setCustomer(customer.get());
        customerService.deposit(deposit);

        customer = customerService.findById(customerId);

        Deposit newDeposit = new Deposit();
        newDeposit.setCustomer(customer.get());
//
//        deposit.setTransactionAmount(null);
//
        model.addAttribute("deposit", newDeposit);
//
        model.addAttribute("success", true);
        model.addAttribute("message", "Deposit successfully");

        return "customer/deposit";
    }

    @PostMapping("/withdraw/{customerId}")
    public String withdraw(@PathVariable Long customerId, @ModelAttribute Withdraw withdraw, Model model) {

        Optional<Customer> customer = customerService.findById(customerId);
//
        withdraw.setCustomer(customer.get());
        customerService.withdraw(withdraw);

        Withdraw newWithdraw = new Withdraw();
        newWithdraw.setCustomer(customer.get());
//
//        deposit.setTransactionAmount(null);
//
        model.addAttribute("withdraw", newWithdraw);
//
        model.addAttribute("success", true);
        model.addAttribute("message", "Withdraw successfully");

        return "customer/withdraw";
    }

    @PostMapping("/transfer/{senderId}")
    public String transfer(@PathVariable Long senderId, @ModelAttribute Transfer transfer, Model model) {

        Optional<Customer> sender = customerService.findById(senderId);

        Optional<Customer> recipient = customerService.findById(transfer.getRecipient().getId());

        transfer.setSender(sender.get());
        transfer.setRecipient(recipient.get());

        customerService.transfer(transfer);

        sender = customerService.findById(senderId);

        Transfer newTransfer = new Transfer();
        newTransfer.setSender(sender.get());

        List<Customer> recipients = customerService.findAllByIdNot(senderId);

        model.addAttribute("transfer", newTransfer);
        model.addAttribute("recipients", recipients);

        return "customer/transfer";
    }
}
