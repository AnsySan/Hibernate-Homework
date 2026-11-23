package org.AnsySan;

import org.AnsySan.config.ApplicationConfiguration;
import org.AnsySan.console.OperationConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    static void main() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class)) {
            OperationConsoleListener listener = context.getBean(OperationConsoleListener.class);
            listener.start();
        }
    }
}