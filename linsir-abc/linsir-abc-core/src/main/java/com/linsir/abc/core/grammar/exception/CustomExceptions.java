package com.linsir.abc.core.grammar.exception;

/**
 * 自定义异常示例
 *
 * 本类演示如何创建和使用自定义异常
 * 对应 JDK: 自定义异常类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomExceptions {

    /**
     * 演示自定义检查异常
     */
    public void demonstrateCheckedException() {
        System.out.println("=== 自定义检查异常 ===");

        BankAccount account = new BankAccount(1000);

        try {
            account.deposit(500);
            account.withdraw(200);
            account.withdraw(2000);  // 会抛出异常
        } catch (InsufficientFundsException e) {
            System.out.println("余额不足: " + e.getMessage());
            System.out.println("当前余额: " + e.getBalance());
            System.out.println("取款金额: " + e.getAmount());
        } catch (InvalidAmountException e) {
            System.out.println("金额无效: " + e.getMessage());
        }
    }

    /**
     * 演示自定义运行时异常
     */
    public void demonstrateRuntimeException() {
        System.out.println("\n=== 自定义运行时异常 ===");

        UserService service = new UserService();

        // 正常注册
        try {
            service.register("张三", "zhangsan@example.com", "password123");
            System.out.println("注册成功");
        } catch (ValidationException e) {
            System.out.println("注册失败: " + e.getMessage());
        }

        // 无效邮箱
        try {
            service.register("李四", "invalid-email", "password123");
        } catch (ValidationException e) {
            System.out.println("注册失败: " + e.getMessage());
            System.out.println("错误字段: " + e.getField());
        }

        // 密码太短
        try {
            service.register("王五", "wangwu@example.com", "123");
        } catch (ValidationException e) {
            System.out.println("注册失败: " + e.getMessage());
        }
    }

    /**
     * 银行账户类
     */
    static class BankAccount {
        private double balance;

        public BankAccount(double initialBalance) {
            this.balance = initialBalance;
        }

        public void deposit(double amount) throws InvalidAmountException {
            if (amount <= 0) {
                throw new InvalidAmountException("存款金额必须大于0: " + amount);
            }
            balance += amount;
            System.out.println("存入 " + amount + "，当前余额: " + balance);
        }

        public void withdraw(double amount) throws InsufficientFundsException {
            if (amount > balance) {
                throw new InsufficientFundsException("余额不足", balance, amount);
            }
            balance -= amount;
            System.out.println("取出 " + amount + "，当前余额: " + balance);
        }

        public double getBalance() {
            return balance;
        }
    }

    /**
     * 用户服务类
     */
    static class UserService {
        public void register(String name, String email, String password) {
            // 验证用户名
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("用户名不能为空", "name");
            }

            // 验证邮箱
            if (email == null || !email.contains("@")) {
                throw new ValidationException("邮箱格式无效", "email");
            }

            // 验证密码
            if (password == null || password.length() < 6) {
                throw new ValidationException("密码长度至少6位", "password");
            }

            // 注册逻辑...
        }
    }

    /**
     * 余额不足异常（检查异常）
     */
    static class InsufficientFundsException extends Exception {
        private final double balance;
        private final double amount;

        public InsufficientFundsException(String message, double balance, double amount) {
            super(message);
            this.balance = balance;
            this.amount = amount;
        }

        public double getBalance() {
            return balance;
        }

        public double getAmount() {
            return amount;
        }
    }

    /**
     * 无效金额异常（检查异常）
     */
    static class InvalidAmountException extends Exception {
        public InvalidAmountException(String message) {
            super(message);
        }
    }

    /**
     * 验证异常（运行时异常）
     */
    static class ValidationException extends RuntimeException {
        private final String field;

        public ValidationException(String message, String field) {
            super(message);
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 自定义异常演示                        ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        CustomExceptions demo = new CustomExceptions();
        demo.demonstrateCheckedException();
        demo.demonstrateRuntimeException();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
