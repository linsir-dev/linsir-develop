import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'linsir-spring-framework',
  description: '轻量级 Java 开发框架',
  lang: 'zh-CN',
  lastUpdated: true,
  markdown: {
    config: (md) => {
      const originalFence = md.renderer.rules.fence
      md.renderer.rules.fence = (tokens, idx, options, env, self) => {
        const token = tokens[idx]
        if (token.info === 'mermaid') {
          return `<pre class="mermaid">${token.content}</pre>`
        }
        return originalFence(tokens, idx, options, env, self)
      }
    }
  },
  head: [
    ['script', { src: 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js' }],
    ['script', {}, `
      (function() {
        function initMermaid() {
          if (typeof mermaid !== 'undefined') {
            mermaid.initialize({ 
              startOnLoad: false,
              theme: 'default',
              securityLevel: 'loose'
            });
            mermaid.run({
              querySelector: '.mermaid'
            });
          }
        }
        
        if (document.readyState === 'loading') {
          document.addEventListener('DOMContentLoaded', initMermaid);
        } else {
          initMermaid();
        }
        
        // 监听页面切换
        const observer = new MutationObserver(function(mutations) {
          initMermaid();
        });
        
        if (document.body) {
          observer.observe(document.body, { childList: true, subtree: true });
        } else {
          document.addEventListener('DOMContentLoaded', function() {
            observer.observe(document.body, { childList: true, subtree: true });
          });
        }
      })();
    `]
  ],
  themeConfig: {
    logo: '/images/logo.png',
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/' },
      { text: 'spring-core', link: '/spring-core/' },
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
      { icon: 'github', link: 'https://github.com/linsir/linsir-spring' }
    ],
    footer: {
      message: '基于 MIT 许可发布',
      copyright: 'Copyright © 2024 Linsir'
    },
    editLink: {
      pattern: 'https://github.com/linsir/linsir-spring/edit/main/docs/docs/:path',
      text: '在 GitHub 上编辑此页'
    },
    lastUpdated: {
      text: '最后更新于'
    },
    docFooter: {
      prev: '上一页',
      next: '下一页'
    },
    outline: {
      label: '页面导航'
    }
  }
})
