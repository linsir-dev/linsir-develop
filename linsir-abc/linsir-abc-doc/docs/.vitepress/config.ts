import { defineConfig } from 'vitepress'
import { withMermaid } from 'vitepress-plugin-mermaid'

// https://vitepress.vuejs.org/config/app-configs
export default withMermaid(defineConfig({
  title: 'Linsir ABC',
  description: 'Linsir ABC 文档',
  ignoreDeadLinks: true,

  head: [
    ['link', { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }]
  ],

  themeConfig: {
    logo: '/favicon.ico',
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/' },
      {
        text: '核心',
        items: [
          { text: '语法基础', link: '/core/grammar/' },
          { text: '基础模块', link: '/core/base/' },
          { text: 'JVM', link: '/core/jvm/' }
        ]
      },
      {
        text: 'MySQL',
        items: [
          { text: 'MySQL 首页', link: '/mysql/' },
          { text: '第一章：MySQL架构与历史', link: '/mysql/chapter-01-architecture/1.1-mysql-logical-architecture/01-mysql-logical-architecture' }
        ]
      },
      {
        text: 'Effective',
        items: [
          { text: 'Effective 首页', link: '/effective/' },
          { text: '设计模式', link: '/effective/designpattern/' }
        ]
      }
    ],

    sidebar: {
      '/guide/': [
        {
          text: '指南',
          items: [
            { text: '指南首页', link: '/guide/' },
            { text: '流程图示例', link: '/guide/mermaid-demo' }
          ]
        }
      ],
      '/core/grammar/': [
        {
          text: 'Grammar 语法基础',
          collapsed: false,
          items: [
            { text: 'Grammar 首页', link: '/core/grammar/' },
            { text: '详细设计文档', link: '/core/grammar/01-grammar-detailed-design' },
            { text: '代码指南', link: '/core/grammar/02-grammar-code-guide' },
            { text: '面试题汇总', link: '/core/grammar/03-grammar-interview-questions' }
          ]
        }
      ],
      '/core/jvm/': [
        {
          text: 'JVM 首页',
          link: '/core/jvm/'
        },
        {
          text: '第一部分：走近Java',
          collapsed: false,
          items: [
            { text: '第1章 走近Java', link: '/core/jvm/chapter-01-intro' },
            { text: '第1章 面试题总结', link: '/core/jvm/chapter-01-interview' }
          ]
        },
        {
          text: '第二部分：自动内存管理',
          collapsed: true,
          items: [
            { text: '第2章 Java内存区域与内存溢出异常', link: '/core/jvm/chapter-02-memory' },
            { text: '第2章 代码指南', link: '/core/jvm/chapter-02-oom-code-guide' },
            { text: '第2章 测试报告', link: '/core/jvm/chapter-02-oom-test-results' },
            { text: '第2章 面试题总结', link: '/core/jvm/chapter-02-interview' },
            { text: '第3章 垃圾收集器与内存分配策略', link: '/core/jvm/chapter-03-gc' },
            { text: '第3章 代码指南', link: '/core/jvm/chapter-03-gc-code-guide' },
            { text: '第3章 测试报告', link: '/core/jvm/chapter-03-gc-test-results' },
            { text: '第3章 面试题总结', link: '/core/jvm/chapter-03-gc-interview' },
            { text: '第4章 虚拟机性能监控与故障处理工具', link: '/core/jvm/chapter-04-tools' },
            { text: '第4章 面试题总结', link: '/core/jvm/chapter-04-interview' },
            { text: '第5章 调优案例分析与实战', link: '/core/jvm/chapter-05-tuning' },
            { text: '第5章 代码指南', link: '/core/jvm/chapter-05-tuning-code-guide' },
            { text: '第5章 测试报告', link: '/core/jvm/chapter-05-tuning-test-results' },
            { text: '第5章 面试题总结', link: '/core/jvm/chapter-05-interview' }
          ]
        },
        {
          text: '第三部分：虚拟机执行子系统',
          collapsed: true,
          items: [
            { text: '第6章 类文件结构', link: '/core/jvm/chapter-06-classfile' },
            { text: '第6章 面试题总结', link: '/core/jvm/chapter-06-interview' },
            { text: '第7章 虚拟机类加载机制', link: '/core/jvm/chapter-07-classloading' },
            { text: '第7章 代码指南', link: '/core/jvm/chapter-07-classloading-code-guide' },
            { text: '第7章 测试报告', link: '/core/jvm/chapter-07-classloading-test-results' },
            { text: '第7章 面试题总结', link: '/core/jvm/chapter-07-interview' },
            { text: '第8章 虚拟机字节码执行引擎', link: '/core/jvm/chapter-08-execution' },
            { text: '第8章 面试题总结', link: '/core/jvm/chapter-08-execution-interview-questions' },
            { text: '第9章 类加载及执行子系统的案例与实战', link: '/core/jvm/chapter-09-cases' },
            { text: '第9章 代码指南', link: '/core/jvm/chapter-09-cases-code-guide' },
            { text: '第9章 测试报告', link: '/core/jvm/chapter-09-cases-test-results' },
            { text: '第9章 面试题总结', link: '/core/jvm/chapter-09-cases-interview-questions' }
          ]
        },
        {
          text: '第四部分：程序编译与代码优化',
          collapsed: true,
          items: [
            { text: '第10章 早期（编译期）优化', link: '/core/jvm/chapter-10-compile-time' },
            { text: '第10章 代码指南', link: '/core/jvm/chapter-10-compile-time-code-guide' },
            { text: '第10章 测试报告', link: '/core/jvm/chapter-10-compile-time-test-results' },
            { text: '第10章 面试题总结', link: '/core/jvm/chapter-10-compile-time-interview' },
            { text: '第11章 晚期（运行期）优化', link: '/core/jvm/chapter-11-runtime' },
            { text: '第11章 代码指南', link: '/core/jvm/chapter-11-runtime-code-guide' },
            { text: '第11章 测试报告', link: '/core/jvm/chapter-11-runtime-test-results' },
            { text: '第11章 面试题总结', link: '/core/jvm/chapter-11-runtime-interview' }
          ]
        },
        {
          text: '第五部分：高效并发',
          collapsed: true,
          items: [
            { text: '第12章 Java内存模型与线程', link: '/core/jvm/chapter-12-jmm' },
            { text: '第12章 代码指南', link: '/core/jvm/chapter-12-jmm-code-guide' },
            { text: '第12章 测试报告', link: '/core/jvm/chapter-12-jmm-test-results' },
            { text: '第12章 面试题总结', link: '/core/jvm/chapter-12-jmm-interview' },
            { text: '第13章 线程安全与锁优化', link: '/core/jvm/chapter-13-thread-safety' },
            { text: '第13章 代码指南', link: '/core/jvm/chapter-13-thread-safety-code-guide' },
            { text: '第13章 测试报告', link: '/core/jvm/chapter-13-thread-safety-test-results' },
            { text: '第13章 面试题总结', link: '/core/jvm/chapter-13-thread-safety-interview' }
          ]
        },
        {
          text: '面试专题',
          collapsed: true,
          items: [
            { text: 'JVM面试题高频TOP榜单', link: '/core/jvm/jvm-interview-top-frequency' }
          ]
        }
      ],
      '/mysql/': [
        {
          text: '1.1 MySQL逻辑架构',
          collapsed: false,
          items: [
            { text: '逻辑架构概述', link: '/mysql/chapter-01-architecture/1.1-mysql-logical-architecture/01-mysql-logical-architecture' },
            { text: '代码设计文档', link: '/mysql/chapter-01-architecture/1.1-mysql-logical-architecture/01-mysql-logical-architecture-code-design' },
            { text: '代码指南', link: '/mysql/chapter-01-architecture/1.1-mysql-logical-architecture/01-mysql-logical-architecture-code-guide' },
            { text: '测试报告', link: '/mysql/chapter-01-architecture/1.1-mysql-logical-architecture/01-mysql-logical-architecture-test-results' },
            { text: '面试题总结', link: '/mysql/chapter-01-architecture/1.1-mysql-logical-architecture/01-mysql-logical-architecture-interview' }
          ]
        },
        {
          text: '1.2 并发控制',
          collapsed: false,
          items: [
            { text: '并发控制概述', link: '/mysql/chapter-01-architecture/1.2-concurrency-control/02-concurrency-control' },
            { text: '代码设计文档', link: '/mysql/chapter-01-architecture/1.2-concurrency-control/02-concurrency-control-code-design' },
            { text: '代码指南', link: '/mysql/chapter-01-architecture/1.2-concurrency-control/02-concurrency-control-code-guide' },
            { text: '测试报告', link: '/mysql/chapter-01-architecture/1.2-concurrency-control/02-concurrency-control-test-report' },
            { text: '面试题总结', link: '/mysql/chapter-01-architecture/1.2-concurrency-control/02-concurrency-control-interview' }
          ]
        },
        {
          text: '1.3 事务',
          collapsed: false,
          items: [
            { text: '事务概述', link: '/mysql/chapter-01-architecture/1.3-transaction/03-transaction' },
            { text: '详细设计文档', link: '/mysql/chapter-01-architecture/1.3-transaction/03-transaction-detailed-design' },
            { text: '代码指南', link: '/mysql/chapter-01-architecture/1.3-transaction/03-transaction-code-guide' },
            { text: '测试报告', link: '/mysql/chapter-01-architecture/1.3-transaction/03-transaction-test-report' },
            { text: '面试题总结', link: '/mysql/chapter-01-architecture/1.3-transaction/03-transaction-interview' }
          ]
        },
        {
          text: '1.4 MVCC',
          collapsed: false,
          items: [
            { text: 'MVCC概述', link: '/mysql/chapter-01-architecture/1.4-mvcc/04-mvcc' }
          ]
        },
        {
          text: '1.5 存储引擎',
          collapsed: false,
          items: [
            { text: '存储引擎概述', link: '/mysql/chapter-01-architecture/1.5-storage-engines/05-storage-engines' }
          ]
        },
        {
          text: '1.6 MySQL时间线',
          collapsed: false,
          items: [
            { text: 'MySQL时间线', link: '/mysql/chapter-01-architecture/1.6-mysql-timeline/06-mysql-timeline' }
          ]
        },
        {
          text: '1.7 MySQL开发模型',
          collapsed: false,
          items: [
            { text: 'MySQL开发模型', link: '/mysql/chapter-01-architecture/1.7-mysql-development-model/07-mysql-development-model' }
          ]
        }
      ],
      '/effective/designpattern/': [
        {
          text: '设计模式首页',
          link: '/effective/designpattern/'
        },
        {
          text: '创建型模式',
          collapsed: false,
          items: [
            { text: '单例模式', link: '/effective/designpattern/singleton/01-singleton-overview' },
            { text: '单例模式-代码指南', link: '/effective/designpattern/singleton/02-singleton-code-guide' },
            { text: '工厂模式', link: '/effective/designpattern/factory/01-factory-overview' },
            { text: '工厂模式-代码指南', link: '/effective/designpattern/factory/02-factory-code-guide' },
            { text: '抽象工厂模式', link: '/effective/designpattern/abstract-factory/01-abstract-factory-overview' },
            { text: '抽象工厂模式-代码指南', link: '/effective/designpattern/abstract-factory/02-abstract-factory-code-guide' },
            { text: '建造者模式', link: '/effective/designpattern/builder/01-builder-overview' },
            { text: '建造者模式-代码指南', link: '/effective/designpattern/builder/02-builder-code-guide' },
            { text: '原型模式', link: '/effective/designpattern/prototype/01-prototype-overview' },
            { text: '原型模式-代码指南', link: '/effective/designpattern/prototype/02-prototype-code-guide' }
          ]
        },
        {
          text: '结构型模式',
          collapsed: false,
          items: [
            { text: '代理模式', link: '/effective/designpattern/proxy/01-proxy-overview' },
            { text: '代理模式-代码指南', link: '/effective/designpattern/proxy/02-proxy-code-guide' },
            { text: '适配器模式', link: '/effective/designpattern/adapter/01-adapter-overview' },
            { text: '适配器模式-代码指南', link: '/effective/designpattern/adapter/02-adapter-code-guide' },
            { text: '桥接模式', link: '/effective/designpattern/bridge/01-bridge-overview' },
            { text: '桥接模式-代码指南', link: '/effective/designpattern/bridge/02-bridge-code-guide' },
            { text: '装饰器模式', link: '/effective/designpattern/decorator/01-decorator-overview' },
            { text: '装饰器模式-代码指南', link: '/effective/designpattern/decorator/02-decorator-code-guide' },
            { text: '外观模式', link: '/effective/designpattern/facade/01-facade-overview' },
            { text: '外观模式-代码指南', link: '/effective/designpattern/facade/02-facade-code-guide' },
            { text: '享元模式', link: '/effective/designpattern/flyweight/01-flyweight-overview' },
            { text: '享元模式-代码指南', link: '/effective/designpattern/flyweight/02-flyweight-code-guide' },
            { text: '组合模式', link: '/effective/designpattern/composite/01-composite-overview' },
            { text: '组合模式-代码指南', link: '/effective/designpattern/composite/02-composite-code-guide' }
          ]
        },
        {
          text: '行为型模式',
          collapsed: false,
          items: [
            { text: '观察者模式', link: '/effective/designpattern/observer/01-observer-overview' },
            { text: '观察者模式-代码指南', link: '/effective/designpattern/observer/02-observer-code-guide' },
            { text: '策略模式', link: '/effective/designpattern/strategy/01-strategy-overview' },
            { text: '策略模式-代码指南', link: '/effective/designpattern/strategy/02-strategy-code-guide' },
            { text: '模板方法模式', link: '/effective/designpattern/template-method/01-template-method-overview' },
            { text: '模板方法模式-代码指南', link: '/effective/designpattern/template-method/02-template-method-code-guide' },
            { text: '状态模式', link: '/effective/designpattern/state/01-state-overview' },
            { text: '状态模式-代码指南', link: '/effective/designpattern/state/02-state-code-guide' },
            { text: '命令模式', link: '/effective/designpattern/command/01-command-overview' },
            { text: '命令模式-代码指南', link: '/effective/designpattern/command/02-command-code-guide' },
            { text: '迭代器模式', link: '/effective/designpattern/iterator/01-iterator-overview' },
            { text: '迭代器模式-代码指南', link: '/effective/designpattern/iterator/02-iterator-code-guide' },
            { text: '中介者模式', link: '/effective/designpattern/mediator/01-mediator-overview' },
            { text: '中介者模式-代码指南', link: '/effective/designpattern/mediator/02-mediator-code-guide' },
            { text: '备忘录模式', link: '/effective/designpattern/memento/01-memento-overview' },
            { text: '备忘录模式-代码指南', link: '/effective/designpattern/memento/02-memento-code-guide' },
            { text: '访问者模式', link: '/effective/designpattern/visitor/01-visitor-overview' },
            { text: '访问者模式-代码指南', link: '/effective/designpattern/visitor/02-visitor-code-guide' },
            { text: '责任链模式', link: '/effective/designpattern/chain-of-responsibility/01-chain-of-responsibility-overview' },
            { text: '责任链模式-代码指南', link: '/effective/designpattern/chain-of-responsibility/02-chain-of-responsibility-code-guide' },
            { text: '解释器模式', link: '/effective/designpattern/interpreter/01-interpreter-overview' },
            { text: '解释器模式-代码指南', link: '/effective/designpattern/interpreter/02-interpreter-code-guide' }
          ]
        }
      ],
      '/core/base/': [
        {
          text: 'Base 基础模块',
          collapsed: false,
          items: [
            { text: 'Base 首页', link: '/core/base/' },
            { text: '详细设计文档', link: '/core/base/01-base-detailed-design' },
            { text: '代码检查报告', link: '/core/base/代码检查报告' },
            { text: '开发进度', link: '/core/base/PROGRESS' }
          ]
        },
        {
          text: 'java.lang',
          collapsed: true,
          items: [
            { text: '详细设计', link: '/core/base/lang/01-lang-detailed-design' },
            { text: '代码指南', link: '/core/base/lang/02-lang-code-guide' },
            { text: '面试题汇总', link: '/core/base/lang/03-lang-interview-questions' }
          ]
        },
        {
          text: 'java.util',
          collapsed: true,
          items: [
            { text: '详细设计', link: '/core/base/util/01-util-detailed-design' },
            { text: '代码指南', link: '/core/base/util/02-util-code-guide' },
            { text: '面试题汇总', link: '/core/base/util/03-util-interview-questions' }
          ]
        },
        {
          text: 'java.io',
          collapsed: true,
          items: [
            { text: '详细设计', link: '/core/base/io/01-io-detailed-design' },
            { text: '代码指南', link: '/core/base/io/02-io-code-guide' },
            { text: '面试题汇总', link: '/core/base/io/03-io-interview-questions' }
          ]
        },
        {
          text: 'java.nio',
          collapsed: true,
          items: [
            { text: '详细设计', link: '/core/base/nio/01-nio-detailed-design' },
            { text: '代码指南', link: '/core/base/nio/02-nio-code-guide' },
            { text: '面试题汇总', link: '/core/base/nio/03-nio-interview-questions' }
          ]
        },
        {
          text: 'java.net',
          collapsed: true,
          items: [
            { text: '详细设计', link: '/core/base/net/01-net-detailed-design' },
            { text: '代码指南', link: '/core/base/net/02-net-code-guide' },
            { text: '面试题汇总', link: '/core/base/net/03-net-interview-questions' }
          ]
        },
        {
          text: 'java.time',
          collapsed: true,
          items: [
            { text: '详细设计', link: '/core/base/time/01-time-detailed-design' },
            { text: '代码指南', link: '/core/base/time/02-time-code-guide' },
            { text: '面试题汇总', link: '/core/base/time/03-time-interview-questions' }
          ]
        }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/linsir-dev/linsir-abc' }
    ],

    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2024-present Linsir'
    }
  },

  // Mermaid 配置
  mermaid: {
    // 可选的 Mermaid 配置
    theme: 'default'
  },

  // 可选的 Mermaid 插件配置
  mermaidPlugin: {
    class: 'mermaid', // 可选的 CSS 类名
    // 其他选项...
  }
}))
