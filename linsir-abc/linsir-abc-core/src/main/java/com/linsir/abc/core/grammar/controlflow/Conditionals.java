package com.linsir.abc.core.grammar.controlflow;

/**
 * 条件语句示例
 *
 * 本类演示 Java 条件语句的使用，包括 if-else 和 switch
 * 对应 JDK: 条件分支控制
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class Conditionals {

    /**
     * 演示 if-else 条件语句
     */
    public void demonstrateIfElse() {
        System.out.println("=== if-else 条件语句 ===");

        int score = 85;

        // 单分支 if
        System.out.println("分数: " + score);
        if (score >= 60) {
            System.out.println("及格");
        }

        // 双分支 if-else
        System.out.println("\n双分支判断:");
        if (score >= 90) {
            System.out.println("优秀");
        } else {
            System.out.println("未达到优秀");
        }

        // 多分支 if-else if-else
        System.out.println("\n成绩等级判断:");
        char grade;
        if (score >= 90) {
            grade = 'A';
        } else if (score >= 80) {
            grade = 'B';
        } else if (score >= 70) {
            grade = 'C';
        } else if (score >= 60) {
            grade = 'D';
        } else {
            grade = 'F';
        }
        System.out.println("等级: " + grade);

        // 嵌套 if
        System.out.println("\n嵌套条件判断:");
        boolean hasTicket = true;
        int age = 20;

        if (hasTicket) {
            if (age >= 18) {
                System.out.println("可以入场（成人票）");
            } else {
                System.out.println("可以入场（儿童票）");
            }
        } else {
            System.out.println("请先购票");
        }
    }

    /**
     * 演示三元运算符
     */
    public void demonstrateTernaryOperator() {
        System.out.println("\n=== 三元运算符 ===");

        int a = 10, b = 20;

        // 基本用法
        int max = (a > b) ? a : b;
        System.out.println("a=" + a + ", b=" + b + ", max=" + max);

        // 嵌套三元运算符
        int score = 75;
        String result = (score >= 90) ? "优秀" :
                       (score >= 80) ? "良好" :
                       (score >= 60) ? "及格" : "不及格";
        System.out.println("分数 " + score + " -> " + result);

        // 三元运算符与 if-else 的对比
        int num = -5;
        int abs = (num >= 0) ? num : -num;
        System.out.println("|" + num + "| = " + abs);
    }

    /**
     * 演示传统 switch 语句
     */
    public void demonstrateTraditionalSwitch() {
        System.out.println("\n=== 传统 switch 语句 ===");

        int day = 3;
        String dayName;

        switch (day) {
            case 1:
                dayName = "星期一";
                break;
            case 2:
                dayName = "星期二";
                break;
            case 3:
                dayName = "星期三";
                break;
            case 4:
                dayName = "星期四";
                break;
            case 5:
                dayName = "星期五";
                break;
            case 6:
                dayName = "星期六";
                break;
            case 7:
                dayName = "星期日";
                break;
            default:
                dayName = "无效日期";
        }

        System.out.println("day=" + day + " -> " + dayName);

        // 多个 case 共享代码（穿透）
        System.out.println("\n多个 case 共享代码:");
        char grade = 'B';

        switch (grade) {
            case 'A':
            case 'B':
            case 'C':
                System.out.println("等级 " + grade + " -> 通过");
                break;
            case 'D':
            case 'F':
                System.out.println("等级 " + grade + " -> 未通过");
                break;
            default:
                System.out.println("无效等级");
        }
    }

    /**
     * 演示增强 switch 表达式（Java 14+）
     */
    public void demonstrateEnhancedSwitch() {
        System.out.println("\n=== 增强 switch 表达式 (Java 14+) ===");

        // 箭头语法，无需 break
        int day = 5;
        String dayName = switch (day) {
            case 1 -> "星期一";
            case 2 -> "星期二";
            case 3 -> "星期三";
            case 4 -> "星期四";
            case 5 -> "星期五";
            case 6 -> "星期六";
            case 7 -> "星期日";
            default -> "无效日期";
        };

        System.out.println("day=" + day + " -> " + dayName);

        // 多个 case 使用箭头语法
        System.out.println("\n多个 case:");
        String season = switch (day) {
            case 12, 1, 2 -> "冬季";
            case 3, 4, 5 -> "春季";
            case 6, 7, 8 -> "夏季";
            case 9, 10, 11 -> "秋季";
            default -> "无效月份";
        };
        System.out.println("月份 " + day + " -> " + season);

        // 使用代码块和 yield
        System.out.println("\n使用 yield 返回值:");
        int score = 85;
        String grade = switch (score / 10) {
            case 10, 9 -> {
                System.out.println("  优秀成绩");
                yield "A";
            }
            case 8 -> {
                System.out.println("  良好成绩");
                yield "B";
            }
            case 7 -> "C";
            case 6 -> "D";
            default -> {
                if (score < 0 || score > 100) {
                    yield "无效分数";
                }
                yield "F";
            }
        };
        System.out.println("分数 " + score + " -> 等级 " + grade);
    }

    /**
     * 演示 switch 与枚举
     */
    public void demonstrateSwitchWithEnum() {
        System.out.println("\n=== switch 与枚举 ===");

        Day today = Day.WEDNESDAY;

        // 传统方式
        System.out.println("传统 switch:");
        switch (today) {
            case MONDAY:
                System.out.println("  今天是星期一，工作开始");
                break;
            case FRIDAY:
                System.out.println("  今天是星期五，周末快到了");
                break;
            case SATURDAY:
            case SUNDAY:
                System.out.println("  今天是周末，休息");
                break;
            default:
                System.out.println("  今天是工作日");
        }

        // 增强 switch（Java 14+）
        System.out.println("\n增强 switch:");
        String activity = switch (today) {
            case MONDAY -> "工作开始";
            case TUESDAY, WEDNESDAY, THURSDAY -> "工作中";
            case FRIDAY -> "准备周末";
            case SATURDAY, SUNDAY -> "休息";
        };
        System.out.println("  今天活动: " + activity);
    }

    /**
     * 星期枚举
     */
    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    /**
     * 主方法，运行所有演示
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 条件语句演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        Conditionals demo = new Conditionals();

        demo.demonstrateIfElse();
        demo.demonstrateTernaryOperator();
        demo.demonstrateTraditionalSwitch();
        demo.demonstrateEnhancedSwitch();
        demo.demonstrateSwitchWithEnum();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
