import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Linsir Spring Cloud',
  description: 'Linsir Spring Cloud 微服务开发文档',
  base: '/linsir-springcloud/',
  
  head: [
    ['link', { rel: 'icon', href: '/favicon.ico' }]
  ],

  themeConfig: {
    logo: '/images/logo.png',
    
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/' },
      { text: '组件', link: '/components/' },
      { text: '实战', link: '/practice/' }
    ],

    sidebar: {
      '/guide/': [
        {
          text: '开始',
          items: [
            { text: '简介', link: '/guide/' },
            { text: '快速开始', link: '/guide/getting-started' },
            { text: '项目结构', link: '/guide/project-structure' }
          ]
        },
        {
          text: '核心概念',
          items: [
            { text: '微服务架构', link: '/guide/microservices-architecture' },
            { text: '服务注册与发现', link: '/guide/service-discovery' },
            { text: '配置中心', link: '/guide/config-center' },
            { text: '服务网关', link: '/guide/api-gateway' },
            { text: '负载均衡', link: '/guide/load-balancer' }
          ]
        }
      ],
      '/components/': [
        {
          text: '核心组件',
          items: [
            { text: 'Eureka', link: '/components/eureka' },
            { text: 'Nacos', link: '/components/nacos' },
            { text: 'Gateway', link: '/components/gateway' },
            { text: 'Feign', link: '/components/feign' },
            { text: 'Ribbon', link: '/components/ribbon' },
            { text: 'Hystrix', link: '/components/hystrix' },
            { text: 'Sentinel', link: '/components/sentinel' }
          ]
        },
        {
          text: '监控与追踪',
          items: [
            { text: 'Sleuth', link: '/components/sleuth' },
            { text: 'Zipkin', link: '/components/zipkin' },
            { text: 'Actuator', link: '/components/actuator' }
          ]
        }
      ],
      '/practice/': [
        {
          text: '实战案例',
          items: [
            { text: '项目搭建', link: '/practice/' },
            { text: '用户服务', link: '/practice/user-service' },
            { text: '订单服务', link: '/practice/order-service' },
            { text: '支付服务', link: '/practice/payment-service' }
          ]
        },
        {
          text: '最佳实践',
          items: [
            { text: '服务拆分', link: '/practice/service-splitting' },
            { text: '分布式事务', link: '/practice/distributed-transaction' },
            { text: '限流降级', link: '/practice/rate-limiting' },
            { text: '灰度发布', link: '/practice/canary-deployment' }
          ]
        }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/linsir-dev/linsir-develop' }
    ],

    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2024-present Linsir'
    },

    search: {
      provider: 'local'
    }
  }
})
