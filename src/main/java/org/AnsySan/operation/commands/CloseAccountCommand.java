package org.AnsySan.operation.commands;

import java.util.Comparator;
import org.AnsySan.account.Account;
import org.AnsySan.account.AccountService;
import org.AnsySan.console.ConsoleInput;
import org.AnsySan.operation.ConsoleOperationType;
import org.AnsySan.operation.OperationCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CloseAccountCommand implements OperationCommand {
    private final ConsoleInput consoleInput;
    private final AccountService accountService;

    @Autowired
    public CloseAccountCommand(ConsoleInput consoleInput, AccountService accountService) {
        this.consoleInput = consoleInput;
        this.accountService = accountService;
    }

    public void execute() {
        int accountId = this.consoleInput.readRequiredInt("Enter account id to close:");
        Account result = this.accountService.close(accountId);
        int targetAccountId = ((Account)this.accountService.getUserAccounts(result.getUserId()).stream().min(Comparator.comparingInt(Account::getId)).orElseThrow(() -> new IllegalStateException("No target account found"))).getId();
        int id = result.getId();
        System.out.println("Account " + id + " closed. Remaining balance " + result.getMoneyAmount() + " transferred to account " + targetAccountId);
    }

    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSED;
    }
}