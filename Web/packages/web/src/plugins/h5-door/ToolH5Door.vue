<template>
  <div class="h5-door">
    <p>H5传送门</p>
    <input v-model="url" @click="initial" />
    <button @click="jumpHtml(url)">Click to Jump</button>
    <div>
      <p>历史记录</p>
      <ul>
        <li
          style="align-content: center"
          v-for="his in view_his"
          :key="his.id"
          @click="initial"
        >
          {{ his.id }} {{ his.content }}
        </li>
      </ul>
      <button @click="clearHistory">清空搜索历史</button>
    </div>
  </div>
</template>
<style scoped>
p {
  font-family: verdana;
  font-size: 20px;
  margin-left: 150px;
  font-style: italic;
  font-weight: bold;
  margin-right: auto;
}
button {
  font-weight: bold;
  background-color: white;
  font-size: small;
  height: 20px;
  vertical-align: center;
  text-align: center;
  border-radius: 5px;
  margin-left: 100px;
  margin: auto;
}
div {
  width: 450px;
  height: 50px;
  margin: auto;
}
input {
  width: 300px;
  height: 20px;
  background-color: white;
  font-size: small;
  border-color: black;
  border-radius: 5px;
}
</style>
<script>
export default {
  data() {
    return {
      url: "输入网址，点击右侧跳转",
      id: 1,
      view_his: [],
    };
  },
  methods: {
    jumpHtml(url) {
      if (!url.startsWith("http://")) {
        url = "http://" + url;
      }
      this.addHis(url);
      window.open(url);
    },
    initial() {
      alert(555);
      if (this.url == "输入网址，点击右侧跳转") {
        this.url = "";
      }
    },
    addHis(url) {
      this.view_his.push({ id: this.id, content: url });
      this.id++;
    },
    clearHistory() {
      this.view_his = [];
      this.id = 1;
    },
  },
};
</script>
