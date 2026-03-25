import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Linsir JDK',
  description: 'JDK 特性学习文档',
  
  themeConfig: {
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/' },
      { text: '深入学习', link: '/deep-dive/' }
    ],

    sidebar: {
      '/guide/': [
        {
          text: '指南',
          items: [
            { text: '指南首页', link: '/guide/' }
          ]
        }
      ],
      '/deep-dive/': [
        {
          text: '深入学习',
          items: [
            { text: 'JDK 8 源码架构全景分析', link: '/deep-dive/' }
          ]
        }
      ],

    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/linsir-dev/linsir-jdk' }
    ],

    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2024-present Linsir'
    }
  }
})
