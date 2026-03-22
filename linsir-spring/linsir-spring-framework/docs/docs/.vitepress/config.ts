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
          { text: '类型系统', link: '/spring-core/type-system/01-resolvable-type' },
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
                { text: '1. ResolvableType', link: '/spring-core/type-system/01-resolvable-type' },
                { text: '2. TypeDescriptor', link: '/spring-core/type-system/02-type-descriptor' },
                { text: '3. ConversionService', link: '/spring-core/type-system/03-conversion-service' },
                { text: '4. 测试代码说明', link: '/spring-core/type-system/04-test-documentation' },
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
