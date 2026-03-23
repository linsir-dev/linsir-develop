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
