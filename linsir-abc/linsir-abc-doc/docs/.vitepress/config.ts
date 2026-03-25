import { defineConfig } from 'vitepress'

// https://vitepress.vuejs.org/config/app-configs
export default defineConfig({
  title: 'Linsir ABC',
  description: 'Linsir ABC 文档',
  
  themeConfig: {
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/' }
    ],

    sidebar: {
      '/guide/': [
        {
          text: '指南',
          items: [
            { text: '指南首页', link: '/guide/' }
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
