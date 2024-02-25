<template>
  <div id="app">
    <Mains />
  </div>
</template>

<script>
import Mains from './components/Mains.vue'

export default {
  name: 'App',
  components: {
    Mains
  }
}
</script>

<style>

* {
  padding: 0;
  margin: 0;
}

* {
  overflow: hidden;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  background-color: rgb(66, 66, 66);
  height: 100vh;
}
</style>
