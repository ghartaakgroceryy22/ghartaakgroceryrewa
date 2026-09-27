import DefaultTheme from 'vitepress/theme'
import GharTakApp from './components/GharTakApp.vue'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('GharTakApp', GharTakApp)
  }
}
