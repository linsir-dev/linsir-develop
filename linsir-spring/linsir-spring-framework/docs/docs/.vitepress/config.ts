import { defineConfig } from 'vitepress'
import { withMermaid } from 'vitepress-plugin-mermaid'

export default withMermaid({
  title: 'linsir-spring-framework',
  description: '轻量级 Java 开发框架',
  lang: 'zh-CN',
  lastUpdated: true,
  themeConfig: {
    logo: '/images/logo.png',
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/' },
      {
        text: 'spring-core',
        items: [
          { text: '概述', link: '/spring-core/' },
          { text: '类型系统', link: '/spring-core/type-system/00-type-system-overview' },
          { text: '反射工具', link: '/spring-core/reflection/00-reflection-overview' },
          { text: '资源抽象', link: '/spring-core/resource/00-resource-overview' },
          { text: '类型转换', link: '/spring-core/conversion/00-conversion-overview' },
          { text: '断言工具', link: '/spring-core/asserts/00-assert-overview' },
          { text: '环境抽象', link: '/spring-core/env/00-env-overview' },
        ]
      },
      { text: 'spring-beans', link: '/spring-beans/' },
      { text: 'spring-context', link: '/spring-context/' },
      { text: 'spring-aop', link: '/spring-aop/' },
      { text: 'spring-tx', link: '/spring-tx/' },
      { text: 'spring-web', link: '/spring-web/' },
      { text: 'spring-webmvc', link: '/spring-webmvc/' },
    ],
    sidebar: {
      '/guide/': [
        {
          text: '指南',
          items: [
            { text: 'Spring Framework 分析', link: '/guide/' },
          ]
        }
      ],
      '/spring-core/': [
        {
          text: 'spring-core',
          items: [
            { text: '概述', link: '/spring-core/' },
            {
              text: '类型系统',
              collapsed: false,
              items: [
                { text: '1. 类型系统概述', link: '/spring-core/type-system/00-type-system-overview' },
                { text: '2. 实现与测试文档', link: '/spring-core/type-system/01-type-system-implementation' },
              ]
            },
            {
              text: '反射工具',
              collapsed: false,
              items: [
                { text: '1. 反射工具概述', link: '/spring-core/reflection/00-reflection-overview' },
                { text: '2. 示例代码指南', link: '/spring-core/reflection/01-reflection-code-guide' },
                { text: '3. 测试代码指南', link: '/spring-core/reflection/02-reflection-test-guide' },
                { text: '4. 测试报告', link: '/spring-core/reflection/03-reflection-test-report' },
                { text: '5. 功能扩展设计', link: '/spring-core/reflection/04-reflection-extension-design' },
              ]
            },
            {
              text: '资源抽象',
              collapsed: false,
              items: [
                { text: '1. 资源抽象概述', link: '/spring-core/resource/00-resource-overview' },
                { text: '2. 示例代码指南', link: '/spring-core/resource/01-resource-code-guide' },
                { text: '3. 测试引导文档', link: '/spring-core/resource/02-resource-test-guide' },
                { text: '4. 测试报告', link: '/spring-core/resource/03-resource-test-report' },
                { text: '5. 功能扩展设计', link: '/spring-core/resource/04-resource-extension-design' },
              ]
            },
            {
              text: '类型转换',
              collapsed: false,
              items: [
                { text: '1. 类型转换概述', link: '/spring-core/conversion/00-conversion-overview' },
                { text: '2. 示例代码指南', link: '/spring-core/conversion/01-conversion-code-guide' },
                { text: '3. 测试引导文档', link: '/spring-core/conversion/02-conversion-test-guide' },
                { text: '4. 测试报告', link: '/spring-core/conversion/03-conversion-test-report' },
                { text: '5. 功能扩展设计', link: '/spring-core/conversion/04-conversion-extension-design' },
              ]
            },
            {
              text: '断言工具',
              collapsed: false,
              items: [
                { text: '1. 断言工具概述', link: '/spring-core/asserts/00-assert-overview' },
                { text: '2. 示例代码指南', link: '/spring-core/asserts/01-assert-code-guide' },
                { text: '3. 测试引导文档', link: '/spring-core/asserts/02-assert-test-guide' },
                { text: '4. 测试报告', link: '/spring-core/asserts/03-assert-test-report' },
                { text: '5. 功能扩展设计', link: '/spring-core/asserts/04-assert-extension-design' },
              ]
            },
            {
              text: '环境抽象',
              collapsed: false,
              items: [
                { text: '1. 环境抽象概述', link: '/spring-core/env/00-env-overview' },
                { text: '2. 示例代码指南', link: '/spring-core/env/01-env-code-guide' },
                { text: '3. 测试引导文档', link: '/spring-core/env/02-env-test-guide' },
                { text: '4. 测试报告', link: '/spring-core/env/03-env-test-report' },
                { text: '5. 功能扩展设计', link: '/spring-core/env/04-env-extension-design' },
              ]
            }
          ]
        }
      ],
      '/spring-beans/': [
        {
          text: 'spring-beans',
          items: [
            { text: '概述', link: '/spring-beans/' },
          ]
        }
      ],
      '/spring-context/': [
        {
          text: 'spring-context',
          items: [
            { text: '概述', link: '/spring-context/' },
          ]
        }
      ],
      '/spring-aop/': [
        {
          text: 'spring-aop',
          items: [
            { text: '概述', link: '/spring-aop/' },
          ]
        }
      ],
      '/spring-tx/': [
        {
          text: 'spring-tx',
          items: [
            { text: '概述', link: '/spring-tx/' },
          ]
        }
      ],
      '/spring-web/': [
        {
          text: 'spring-web',
          items: [
            { text: '概述', link: '/spring-web/' },
          ]
        }
      ],
      '/spring-webmvc/': [
        {
          text: 'spring-webmvc',
          items: [
            { text: '概述', link: '/spring-webmvc/' },
          ]
        }
      ],
    },
    socialLinks: [
      { icon: 'github', link: 'https://github.com/vuejs/vitepress' }
    ]
  },
  mermaid: {
    theme: 'default',
    securityLevel: 'loose',
  },
  mermaidPlugin: {
    class: 'mermaid',
  },
})
