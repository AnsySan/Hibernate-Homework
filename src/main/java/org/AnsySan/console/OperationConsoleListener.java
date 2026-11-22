package org.AnsySan.console;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.AnsySan.operation.ConsoleOperationType;
import org.AnsySan.operation.OperationCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperationConsoleListener {
    private final Map<ConsoleOperationType, OperationCommand> commandMap;
    private final ConsoleInput consoleInput;

    private static final String EXIT_COMMAND = "EXIT";

    @Autowired
    public OperationConsoleListener(ConsoleInput consoleInput, List<OperationCommand> commands) {
        this.consoleInput = consoleInput;
        this.commandMap = new EnumMap<>(ConsoleOperationType.class);

        for (OperationCommand command : commands) {
            this.commandMap.put(command.getOperationType(), command);
        }

    }

    public void start() {
        System.out.println("MiniBank started. Type" + EXIT_COMMAND + " to stop.");
        String availableCommands = consoleInput.getAvailableCommands();

        System.out.println("Available commands: " + availableCommands);

        while (true) {
            try {
                String rawCommand = this.consoleInput.readRequiredString("Enter command:").trim();
                if (rawCommand.isEmpty()) {
                    System.out.println("Error: empty command. Available commands: " + availableCommands);
                    continue;
                } else if (EXIT_COMMAND.equalsIgnoreCase(rawCommand)) {
                        System.out.println("MiniBank stopped.");
                        return;
                    }

                ConsoleOperationType operationType;
                try {
                    operationType = ConsoleOperationType.valueOf(rawCommand.toUpperCase());
                } catch (IllegalArgumentException var5) {
                    System.out.println("Error: unknown command '" + rawCommand + "'");
                    continue;
                }

                OperationCommand command = (OperationCommand) this.commandMap.get(operationType);
                if (command == null) {
                    System.out.println("Error: no handler registered for command " + String.valueOf(operationType));
                } else {
                    command.execute();
                }

            } catch (Exception e) {
                System.out.println("Error: unexpected error - " + e.getMessage());
            }
        }
    }
}
