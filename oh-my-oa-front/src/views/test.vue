<template>
  <div>
    <div>账号切换</div>
    <button @click="admin()" type="default" size="mini">admin</button>
    <button @click="zhangsan()" type="default" size="mini">张三</button>
    <button @click="zzjl()" type="default" size="mini">张总经理</button>
    <button @click="lisi()" type="default" size="mini">李四</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created(){
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    admin() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_6tWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwNDA2NbA00FEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQA_TupQQgAAAA.W7YEIFVVnImHKLB173708pc7oWcih6NRCOriqtAr4Lg'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zhangsan() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_6tWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwNDA2NTQw0VEqLU4t8kxRsjKHMPMSc1OBWqoyEvPSixPzlGoBgTFa-0UAAAA.QLMuOasBYTIhVOdupff3499Hpws2_0_LcjE0l0cFewQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zzjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDM3NjMwNLYw11EqLU4t8kxRsjKFMPMSc1OBWqqqsnKUagGHebUNQQAAAA.FcCkD6QfaP8bd_3MYsRD6Tp9wr9JSRFWIUuIdG5O7XxhIeyWfzGLp40_Lesx_kWMMaaskh_ba3NrTlj8-nP6OA'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_6tWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwNDA2NTUy0FEqLU4t8kxRsjKBMPMSc1OBWnIyizOVagFP9zxfQQAAAA.1IftKvmxl0yxFUI3b5uWKGWoNZQx0vfL_niOWRAQjow'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
