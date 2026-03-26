import { defineConfig } from 'vitepress'

// https://vitepress.vuejs.org/config/app-configs
export default defineConfig({
  title: 'Linsir ABC',
  description: 'Linsir ABC 文档',

  themeConfig: {
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/' },
      {
        text: '核心',
        items: [
          { text: 'Base 基础模块', link: '/core/base/' }
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
      '/core/': [
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
  }
})
