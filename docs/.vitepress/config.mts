import { defineConfig } from 'vitepress'

export default defineConfig({
  title: "Ghar Tak Grocery",
  description: "Fast 15-Minute Grocery Delivery in Rewa, Madhya Pradesh",
  head: [
    ['link', { rel: 'icon', href: '/favicon.ico' }]
  ],
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Features', link: '/#features' },
      { text: 'Delivery Hubs', link: '/#delivery-areas' },
      { text: 'Privacy Policy', link: '/privacy' }
    ],
    footer: {
      message: 'Delivering fresh groceries across Rewa, MP.',
      copyright: 'Copyright © 2026 Ghar Tak Grocery'
    }
  }
})
