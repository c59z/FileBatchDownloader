import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// import './mock'


// Vue.component(Tree.name, Tree);

Vue.use(ElementUI)

new Vue({
  render: h => h(App),
}).$mount('#app')
