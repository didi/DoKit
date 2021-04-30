import { pushScopeId, popScopeId, openBlock, createBlock, withDirectives, createVNode, vShow, toDisplayString, createCommentVNode, withScopeId, resolveComponent, KeepAlive, resolveDynamicComponent, reactive, resolveDirective, Fragment, renderList, shallowRef, unref, createApp } from 'vue';

/**
 * 拖拽指令 v-dragable
 * 减少外部依赖
*/
const INIT_VALUE = 9999;
const DEFAULT_OPACITY = 0.5;
const SAFE_BOTTOM = 50; // 底部防误触
// TODO 拖拽事件兼容 Pc处理
// TODO 默认初始位置为右下角
var dragable = {
  mounted (el) {
    // 初始化变量
    el.dokitEntryLastX = INIT_VALUE;
    el.dokitEntryLastY = INIT_VALUE;
    // 初始化样式
    el.style.position = 'fixed';
    el.style.opacity = DEFAULT_OPACITY;
    el.dokitPositionLeft = getDefaultX();
    el.dokitPositionTop = getDefaultY();
    el.style.top = `${el.dokitPositionTop}px`;
    el.style.left = `${el.dokitPositionLeft}px`;

    // 触摸事件监听
    el.ontouchstart = () => {
      el.style.opacity = 1;
    };

    el.ontouchmove = (e) => {
      e.preventDefault();
      
      if (el.dokitEntryLastX === INIT_VALUE) {
        el.dokitEntryLastX = e.touches[0].clientX;
        el.dokitEntryLastY = e.touches[0].clientY;
        return
      }

      el.dokitPositionTop += (e.touches[0].clientY - el.dokitEntryLastY);
      el.dokitPositionLeft += (e.touches[0].clientX - el.dokitEntryLastX);
      el.dokitEntryLastX = e.touches[0].clientX;
      el.dokitEntryLastY = e.touches[0].clientY;

      el.style.top = `${getAvailableTop(el)}px`;
      el.style.left = `${getAvailableLeft(el)}px`;
    };

    el.ontouchend = (e) => {
      setTimeout(() => {
        if (el.dokitPositionLeft < 0) {
          el.dokitPositionLeft = 0;
          el.style.left = `${el.dokitPositionLeft}px`;
        } else if (el.dokitPositionLeft + e.target.clientWidth > window.screen.availWidth) {
          el.dokitPositionLeft = window.screen.availWidth - e.target.clientWidth;
          el.style.left = `${el.dokitPositionLeft}px`;

        }
        
        if (el.dokitPositionTop < 0) {
          el.dokitPositionTop = 0;
          el.style.top = `${el.dokitPositionTop}px`;

        } else if (el.dokitPositionTop + e.target.clientHeight + SAFE_BOTTOM > window.screen.availHeight) {
          el.dokitPositionTop = window.screen.availHeight - e.target.clientHeight - SAFE_BOTTOM;
          el.style.top = `${el.dokitPositionTop}px`;

        }
        localStorage.setItem('dokitPositionTop', el.dokitPositionTop);
        localStorage.setItem('dokitPositionLeft', el.dokitPositionLeft);
      }, 100);
      el.dokitEntryLastX = INIT_VALUE;
      el.dokitEntryLastY = INIT_VALUE;
      el.style.opacity = 0.5;
    };
  }
};

function getDefaultX(el){
  let defaultX = Math.round(window.outerWidth/2);
  return localStorage.getItem('dokitPositionLeft') ? parseInt(localStorage.getItem('dokitPositionLeft')) : defaultX
}
function getDefaultY(el){
  let defaultY = Math.round(window.outerHeight/2);
  return localStorage.getItem('dokitPositionTop') ? parseInt(localStorage.getItem('dokitPositionTop')) : defaultY
}
function getAvailableLeft(el){
  return standardNumber(el.dokitPositionLeft, window.outerWidth - el.clientWidth)
}
function getAvailableTop(el){
  return standardNumber(el.dokitPositionTop, window.outerHeight - el.clientHeight)
}
function standardNumber(number, max){
  if(number < 0){
    return 0
  }
  if(number >= max){
    return max
  }
  return number
}

const IconBack = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAzCAMAAADIDVqJAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAXFQTFRFAAAANX7GNHzFM3zFM3zFNn3ENYDHNHzFM3zENIDHNH7INH3ENX3GNn7GM33ENYDFNYDGM4DINH7HM3zENH3HNHzENYDGM37FM33FNH3FNn3FNX7HN4DINHzEM33HNH7FNIDGNn7GM33ENX3FNXzHNoDENH7FNH3EM3zEN33INHzFNH3FM4DHNX3HNH3GNX7ENHzENHzFNYDFNH3FM33ENn3JM33GN4DINX7EM3zEM33GNH7FM33FM33FNIDFNH3GNX3KM37HNHzFN4DINX7FNH3EOHzHNH3GM3zFM4DGM33EM3zENHzENH3EM33FOoTFgID/NHzFOIDHM3zEM3zFNoPJM33FM33ENH3FNHzENYDL////NHzENH3FM4PFM3zFOYDGNH3FM3zFN3zINHzEM33FNoDJM3zFM33FNH3ENHzFM37FNH3ENH3FM33ENH7GNX7FM33ENH3FNH7FM37ENH7FM3zFNH3EM4jMM3zE////TdHL6gAAAHl0Uk5TAEPy+vA9RPb0QEX3P0fzPkg8Sfg7SjpL+fE5TThON082Ue81UjRT++4zVO0yVjFX/OwwWOsvWi5b6i1d/eksXitf6Cph5yli/ihk5ieh3x8CoiCg4CGf4Z3iIgGc4yOaJJnkJZjlJpaVk5KQj42LioiHhYSCgHOyD3AmCqsAAAABYktHRFt0vJU0AAAACXBIWXMAAABIAAAASABGyWs+AAABPUlEQVQ4y43UxVoCUBQE4Ctgd3cXKAYoKqioINiBrdjdXby9C+6c3ZzPWf+rE2MMS4bDmY4rk5qs7FQ6ObnU5OXDFFBTCFNUTE2JmFJqysqtcVRQUymmiprqGmtq66ipF9NATSOMq4maZidMCzWtMG3t1HSI6aSmq9sat4eaHpheLzV9YvqpGRi0xuenZghmOEDNiJhRasZggiFqxiesmQxTMyVmmpoZmEiUmlmYWJyauXmYBWoWYZaWqTEr1qRW1zhaT4jaUNQm1Na2onagdjW1B7V/wFXyEOroWFEnok4VdQZ1fqGoS6ira0XdpP4x+uStKL5EY+6gYpq6h4rEFfUgKqqoRyjlkI15ggqGFfUsKqSoFyjlTY15hfIFFPUmyq+odyilhIz5gHJ7FfUpyqOoLyilYo35/rFJ/Jo/DZ3bT7fEcIgAAAAldEVYdGRhdGU6Y3JlYXRlADIwMjEtMDQtMjFUMTc6MzI6MjgrMDg6MDBBnT5hAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDIxLTA0LTIxVDE3OjMyOjI4KzA4OjAwMMCG3QAAAABJRU5ErkJggg==';

const DefaultItemIcon = 'https://pt-starimg.didistatic.com/static/starimg/img/FHqpI3InaS1618997548865.png';

const dokitIcon = 'https://pt-starimg.didistatic.com/static/starimg/img/eM7MJKDqVG1618998466986.png';

var script$6 = {
  props: {
    title: {
      default: 'DoKit'
    },
    canBack:{
      default: true
    }
  },
  data(){
    return {
      icon: IconBack
    }
  },
  methods: {
    handleBackRoute(){
      if(this.$emit('back'));else {
        this.$router.back();
      }
    }
  }
};

const _withId$6 = /*#__PURE__*/withScopeId("data-v-29d70086");

pushScopeId("data-v-29d70086");
const _hoisted_1$6 = { class: "bar" };
const _hoisted_2$4 = /*#__PURE__*/createVNode("span", { class: "bar-back-btn" }, "返回", -1 /* HOISTED */);
const _hoisted_3$2 = { class: "bar-title" };
const _hoisted_4$1 = { class: "bar-title-text" };
popScopeId();

const render$6 = /*#__PURE__*/_withId$6((_ctx, _cache, $props, $setup, $data, $options) => {
  return (openBlock(), createBlock("div", _hoisted_1$6, [
    withDirectives(createVNode("div", {
      class: "bar-back",
      onClick: _cache[1] || (_cache[1] = (...args) => ($options.handleBackRoute && $options.handleBackRoute(...args)))
    }, [
      createVNode("img", {
        class: "bar-back-icon",
        src: $data.icon
      }, null, 8 /* PROPS */, ["src"]),
      _hoisted_2$4
    ], 512 /* NEED_PATCH */), [
      [vShow, $props.canBack]
    ]),
    createVNode("div", _hoisted_3$2, [
      createVNode("span", _hoisted_4$1, toDisplayString($props.title), 1 /* TEXT */)
    ]),
    createCommentVNode(" TODO 支持切换模式 "),
    createCommentVNode(" <div class=\"bar-other\">\n      <span class=\"bar-other-text\">更多</span>\n    </div> ")
  ]))
});

function styleInject(css, ref) {
  if ( ref === void 0 ) ref = {};
  var insertAt = ref.insertAt;

  if (!css || typeof document === 'undefined') { return; }

  var head = document.head || document.getElementsByTagName('head')[0];
  var style = document.createElement('style');
  style.type = 'text/css';

  if (insertAt === 'top') {
    if (head.firstChild) {
      head.insertBefore(style, head.firstChild);
    } else {
      head.appendChild(style);
    }
  } else {
    head.appendChild(style);
  }

  if (style.styleSheet) {
    style.styleSheet.cssText = css;
  } else {
    style.appendChild(document.createTextNode(css));
  }
}

var css_248z$6 = ".bar[data-v-29d70086] {\n  background-color: white;\n  height: 50px;\n  width: 100%;\n  display: flex;\n  justify-content: center;\n  align-items: center;\n  padding: 0 10px;\n  box-sizing: border-box;\n  position: relative;\n  border-radius: 10px 10px 0 0;\n}\n.bar-back[data-v-29d70086] {\n  position: absolute;\n  left: 10px;\n  display: flex;\n  flex-direction: row;\n  align-items: center;\n}\n.bar-back-icon[data-v-29d70086] {\n  display: inline-block;\n  height: 18px;\n}\n.bar-back-btn[data-v-29d70086] {\n  color: #337CC4;\n  font-size: 16px;\n  margin-left: 5px;\n}\n.bar-title-text[data-v-29d70086] {\n  color: #333333;\n  font-size: 20px;\n}\n.bar-other-text[data-v-29d70086] {\n  color: #666666;\n  font-size: 16px;\n}\n";
styleInject(css_248z$6);

script$6.render = render$6;
script$6.__scopeId = "data-v-29d70086";
script$6.__file = "src/common/components/top-bar.vue";

var script$5 = {
  components: {
   TopBar: script$6 
  },
  data(){
    return {}
  },
  computed:{
    component(){
      return this.$route.component
    },
    title(){
      return this.$route.title || 'Dokit'
    },
    canBack(){
      return this.$route.name !== 'home'
    }
  },
  created(){
    console.log(this.$router);
  }
};

const _withId$5 = /*#__PURE__*/withScopeId("data-v-8dfbe6e6");

pushScopeId("data-v-8dfbe6e6");
const _hoisted_1$5 = { class: "container" };
const _hoisted_2$3 = { class: "router-container" };
popScopeId();

const render$5 = /*#__PURE__*/_withId$5((_ctx, _cache, $props, $setup, $data, $options) => {
  const _component_top_bar = resolveComponent("top-bar");

  return (openBlock(), createBlock("div", _hoisted_1$5, [
    createVNode(_component_top_bar, {
      title: $options.title,
      canBack: $options.canBack
    }, null, 8 /* PROPS */, ["title", "canBack"]),
    createVNode("div", _hoisted_2$3, [
      (openBlock(), createBlock(KeepAlive, null, [
        (openBlock(), createBlock(resolveDynamicComponent($options.component)))
      ], 1024 /* DYNAMIC_SLOTS */))
    ])
  ]))
});

var css_248z$5 = ".container[data-v-8dfbe6e6] {\n  position: absolute;\n  left: 0;\n  right: 0;\n  top: 100px;\n  bottom: 0;\n  background-color: #f5f6f7;\n  display: flex;\n  flex-direction: column;\n  z-index: 99;\n  border-radius: 10px 10px 0 0;\n}\n.router-container[data-v-8dfbe6e6] {\n  margin-top: 5px;\n  background-color: white;\n  flex: 1;\n  overflow-y: scroll;\n}\n";
styleInject(css_248z$5);

script$5.render = render$5;
script$5.__scopeId = "data-v-8dfbe6e6";
script$5.__file = "src/components/router-container.vue";

var script$4 = {
  components: {},
  data(){
    return {}
  },
  computed:{
    singlePlugins(){
      return this.$store.state.singlePlugins
    }
  },
  created(){
    console.log(this.$router);
  }
};

const _withId$4 = /*#__PURE__*/withScopeId("data-v-1e3e6168");

pushScopeId("data-v-1e3e6168");
const _hoisted_1$4 = {
  class: "container",
  style: {"z-index":"999"}
};
const _hoisted_2$2 = /*#__PURE__*/createVNode("div", { class: "plugin-container" }, [
  /*#__PURE__*/createVNode("div", null, " I Am Single Container! ")
], -1 /* HOISTED */);
popScopeId();

const render$4 = /*#__PURE__*/_withId$4((_ctx, _cache, $props, $setup, $data, $options) => {
  return (openBlock(), createBlock("div", _hoisted_1$4, [
    _hoisted_2$2
  ]))
});

var css_248z$4 = ".container[data-v-1e3e6168] {\n  position: absolute;\n  left: 0;\n  right: 0;\n  bottom: 0;\n  top: 0;\n}\n.plugin-container[data-v-1e3e6168] {\n  background-color: #ffffff;\n  box-shadow: 1px 1px 2px #333333;\n}\n";
styleInject(css_248z$4);

script$4.render = render$4;
script$4.__scopeId = "data-v-1e3e6168";
script$4.__file = "src/components/single-container.vue";

const storeKey = 'store';
/**
 * 简易版 Store 实现
 * 支持直接修改 Store 数据
 */
class Store{
  constructor(options){
    let {state} = options;
    this.initData(state);
  }

  initData(data = {}){
    this._state = reactive({
      data: data
    });
  }

  get state(){
    return this._state.data
  }

  install(app){
    app.provide(storeKey, this);
    app.config.globalProperties.$store = this;
  }

}

const store = new Store({
  state: {
    showContainer: false,
    singlePlugins: [],
    features: []
  }
});

// 更新全局 Store 数据
function updateGlobalData(key, value){
  store.state[key] = value;
}

// 获取当前 Store 数据的状态
function getGlobalData(){
  return store.state
}

function toggleContainer(flag){
  if(flag){
    store.state.showContainer = flag;
  }else {
    store.state.showContainer = !store.state.showContainer;
  }
}

function pushContainer(plugin){
  // Unique Container
  store.state.singlePlugins.push(plugin);
  console.log(store.state.singlePlugins);
}
function popContainer(){
  store.state.singlePlugins.pop();
}

var script$3 = {
  components: {
    RouterContainer: script$5,
    SingleContainer: script$4
  },
  directives: {
    dragable,
  },
  data() {
    return {};
  },
  computed: {
    state(){
      return this.$store.state
    },
    showContainer(){
      return this.state.showContainer
    }
  },
  methods: {
    toggleShowContainer() {
      toggleContainer();
    },
  },
  created(){
    console.log("CurState: ",this.$store.state);
  }
};

const _withId$3 = /*#__PURE__*/withScopeId("data-v-6c0a0fc1");

pushScopeId("data-v-6c0a0fc1");
const _hoisted_1$3 = { class: "dokit-app" };
popScopeId();

const render$3 = /*#__PURE__*/_withId$3((_ctx, _cache, $props, $setup, $data, $options) => {
  const _component_router_container = resolveComponent("router-container");
  const _directive_dragable = resolveDirective("dragable");

  return (openBlock(), createBlock("div", _hoisted_1$3, [
    withDirectives(createVNode("div", {
      class: "dokit-entry-btn",
      style: {"z-index":"10000"},
      onClick: _cache[1] || (_cache[1] = (...args) => ($options.toggleShowContainer && $options.toggleShowContainer(...args)))
    }, null, 512 /* NEED_PATCH */), [
      [_directive_dragable]
    ]),
    withDirectives(createVNode("div", {
      class: "mask",
      onClick: _cache[2] || (_cache[2] = (...args) => (_ctx.toggleContainer && _ctx.toggleContainer(...args)))
    }, null, 512 /* NEED_PATCH */), [
      [vShow, $options.showContainer]
    ]),
    withDirectives(createVNode(_component_router_container, null, null, 512 /* NEED_PATCH */), [
      [vShow, $options.showContainer]
    ]),
    createCommentVNode(" <single-container></single-container> ")
  ]))
});

var css_248z$3 = ".dokit-app[data-v-6c0a0fc1] {\n  font-family: Helvetica Neue, Helvetica, Arial, sans-serif;\n}\n.dokit-entry-btn[data-v-6c0a0fc1] {\n  width: 50px;\n  height: 50px;\n  padding: 10px;\n  box-sizing: border-box;\n  background-image: url(//pt-starimg.didistatic.com/static/starimg/img/OzaetKDzHr1618905183992.png);\n  background-size: 50px;\n  background-position: center;\n  background-repeat: no-repeat;\n}\n.mask[data-v-6c0a0fc1] {\n  position: absolute;\n  top: 0;\n  left: 0;\n  right: 0;\n  bottom: 0;\n  z-index: 3;\n  background-color: #333333;\n  opacity: 0.3;\n}\n";
styleInject(css_248z$3);

script$3.render = render$3;
script$3.__scopeId = "data-v-6c0a0fc1";
script$3.__file = "src/components/app.vue";

const LifecycleHooks = {
  LOAD: 'load',
  UNLOAD: 'unload'
};

const applyLifecyle = function(features, lifecycle){
  features.forEach(feature => {
    let {list} = feature;
    list.forEach(item => {
      if(isFunction(item[lifecycle])){
        item[lifecycle]();
      }
    });
  });
};

const isFunction = function(ob){
  return typeof ob === 'function'
};

var script$2 = {
  props: {
    title: {
      default: '专区'
    },
    list: {
      default: []
    }
  },
  data(){
    return {
      defaultIcon: DefaultItemIcon
    }
  },
  methods: {
    handleClickItem(item){
      this.$emit('handleClickItem', item);
    }
  }
};

const _withId$2 = /*#__PURE__*/withScopeId("data-v-00020244");

pushScopeId("data-v-00020244");
const _hoisted_1$2 = { class: "card" };
const _hoisted_2$1 = { class: "card-title" };
const _hoisted_3$1 = { class: "card-title-text" };
const _hoisted_4 = { class: "item-list" };
const _hoisted_5 = { class: "item-icon" };
const _hoisted_6 = { class: "item-title" };
popScopeId();

const render$2 = /*#__PURE__*/_withId$2((_ctx, _cache, $props, $setup, $data, $options) => {
  return (openBlock(), createBlock("div", _hoisted_1$2, [
    createVNode("div", _hoisted_2$1, [
      createVNode("span", _hoisted_3$1, toDisplayString($props.title), 1 /* TEXT */)
    ]),
    createVNode("div", _hoisted_4, [
      (openBlock(true), createBlock(Fragment, null, renderList($props.list, (item, index) => {
        return (openBlock(), createBlock("div", {
          class: "item",
          key: index,
          onClick: $event => ($options.handleClickItem(item))
        }, [
          createVNode("div", _hoisted_5, [
            createVNode("img", {
              class: "item-icon-image",
              src: item.icon || $data.defaultIcon
            }, null, 8 /* PROPS */, ["src"])
          ]),
          createVNode("div", _hoisted_6, toDisplayString(item.nameZh || '默认功能'), 1 /* TEXT */)
        ], 8 /* PROPS */, ["onClick"]))
      }), 128 /* KEYED_FRAGMENT */))
    ])
  ]))
});

var css_248z$2 = ".card[data-v-00020244] {\n  margin-bottom: 10px;\n  padding: 10px;\n  background-color: white;\n}\n.card-title-text[data-v-00020244] {\n  font-size: 16px;\n  color: #333333;\n}\n.item-list[data-v-00020244] {\n  display: flex;\n  flex-wrap: wrap;\n  margin-top: 5px;\n}\n.item[data-v-00020244] {\n  display: flex;\n  flex-direction: column;\n  align-items: center;\n  width: 25%;\n  margin-top: 5px;\n}\n.item .item-icon-image[data-v-00020244] {\n  width: 30px;\n}\n.item .item-title[data-v-00020244] {\n  font-size: 14px;\n  margin-top: 5px;\n}\n";
styleInject(css_248z$2);

script$2.render = render$2;
script$2.__scopeId = "data-v-00020244";
script$2.__file = "src/common/components/card.vue";

var script$1 = {
  props: {
    version: String
  },
  data(){
    return {
      dokitIcon: dokitIcon
    }
  }
};

const _withId$1 = /*#__PURE__*/withScopeId("data-v-b7dc930c");

pushScopeId("data-v-b7dc930c");
const _hoisted_1$1 = { class: "card version" };
const _hoisted_2 = { class: "version-text" };
const _hoisted_3 = { class: "version-image" };
popScopeId();

const render$1 = /*#__PURE__*/_withId$1((_ctx, _cache, $props, $setup, $data, $options) => {
  return (openBlock(), createBlock("div", _hoisted_1$1, [
    createVNode("div", null, [
      createVNode("span", _hoisted_2, "当前版本：V" + toDisplayString($props.version), 1 /* TEXT */)
    ]),
    createVNode("div", _hoisted_3, [
      createVNode("img", {
        class: "dokit-icon",
        src: $data.dokitIcon
      }, null, 8 /* PROPS */, ["src"])
    ])
  ]))
});

var css_248z$1 = ".card[data-v-b7dc930c] {\n  padding: 10px;\n  background-color: white;\n}\n.version[data-v-b7dc930c] {\n  padding: 20px 0;\n  text-align: center;\n}\n.version .version-text[data-v-b7dc930c] {\n  font-size: 16px;\n  color: #999999;\n}\n.version .version-image[data-v-b7dc930c] {\n  margin-top: 20px;\n}\n.version .dokit-icon[data-v-b7dc930c] {\n  width: 150px;\n}\n";
styleInject(css_248z$1);

script$1.render = render$1;
script$1.__scopeId = "data-v-b7dc930c";
script$1.__file = "src/common/components/version.vue";

var script = {
  components: {
    TopBar: script$6,
    Card: script$2,
    VersionCard: script$1
  },
  data(){
    return {
      version: '1.0.0'
    }
  },
  mounted(){
  },
  computed: {
    features(){
      return this.$store.state.features
    }
  },
  methods: {
    handleClickItem(item){
      switch(item.type){
        case "RouterPlugin":
          this.$router.push({
            name: item.name
          });
          break;
        case "SinglePlugin":
          pushContainer(item);
          break;
      }
    }
  }
};

const _withId = /*#__PURE__*/withScopeId("data-v-957c9522");

pushScopeId("data-v-957c9522");
const _hoisted_1 = { class: "index-container" };
popScopeId();

const render = /*#__PURE__*/_withId((_ctx, _cache, $props, $setup, $data, $options) => {
  const _component_card = resolveComponent("card");
  const _component_version_card = resolveComponent("version-card");

  return (openBlock(), createBlock("div", _hoisted_1, [
    (openBlock(true), createBlock(Fragment, null, renderList($options.features, (item, index) => {
      return (openBlock(), createBlock(_component_card, {
        key: index,
        title: item.title,
        list: item.list,
        onHandleClickItem: $options.handleClickItem
      }, null, 8 /* PROPS */, ["title", "list", "onHandleClickItem"]))
    }), 128 /* KEYED_FRAGMENT */)),
    createVNode(_component_version_card, { version: $data.version }, null, 8 /* PROPS */, ["version"])
  ]))
});

var css_248z = ".index-container[data-v-957c9522] {\n  background-color: #f5f6f7;\n}\n";
styleInject(css_248z);

script.render = render;
script.__scopeId = "data-v-957c9522";
script.__file = "src/components/home.vue";

const defaultRoute = [{
  name: 'home',
  component: script
}];

function getRoutes(features){
  let routes = [];
  features.forEach(feature => {
    let {list, title:zoneTitle} = feature;
    list.forEach(item => {
      let {name, nameZh, component} = item;
      routes.push({
        name: name,
        component: component,
        meta: {
          title: nameZh,
          zone: zoneTitle
        }
      });
    });
  });
  return [...defaultRoute, ...routes]
}

/**
 * DoKit 专用 Router
 * 1. 处理普通的 Router Container
 */
const createRouter = function({routes:mainRoutes}){
  const routes = mainRoutes;
  const history = [];
  const defaultRoute = 'home';
  const homeRoute = matchRoute(defaultRoute);
  const currentRoute = shallowRef(homeRoute);
  /* Route Operation Start */
  function addRoute(route){
    routes.push(route);
  }
  function removeRoute(name){
    let index = routes.findIndex((item)=> item.name === name);
    if(index != -1){
      return routes.splice(index, 1)
    }else {
      return null
    }
  }
  function hasRoute(name){
    let index = routes.findIndex((item)=> item.name === name);
    return index !== -1
  }
  function getRoutes(){
    return routes
  }
  /* Route Operation End */
  /* Router Operation Start */
  function push({name}){
    history.push(name);
    updateCurrentRoute({name});
  }

  function replace(name){
    history.pop();
    history.push(name);
    updateCurrentRoute({name});
  }

  function back(){
    let index = history[history.length - 2];
    updateCurrentRoute({
      name: history[index]
    });
  }

  /* Router Operation End */

  function install(app){
    // Install To Vue
    const router = this;
    app.config.globalProperties.$router = router;
    Object.defineProperty(app.config.globalProperties, '$route', {
      get: () => unref(currentRoute)
    });
  }

  function matchRoute(name){
    let route = routes.find((item) => {
      return item.name === name
    });
    return route
  }
  
  function updateCurrentRoute({name}){
    let route = matchRoute(name) || homeRoute;
    currentRoute.value = route;
  }
  return {
    currentRoute,
    addRoute,
    removeRoute,
    hasRoute,
    getRoutes,
    push,
    replace,
    back,
    install
  }
};

function getRouter(features){
  return createRouter({
    routes: [...getRoutes(features)],
  })
}

const noop = () => {};

class BasePlugin{
  type = ''
  name = ''
  nameZh = ''
  icon = ''
  component = null
  _onLoad = noop
  _onUnload = noop
  constructor(options){
    let {name, nameZh, icon, component, onLoad, onUnload} = options;
    this.name = name;
    this.nameZh = nameZh;
    this.icon = icon;
    this.component = component;
    this._onLoad = onLoad || noop;
    this._onUnload = onUnload || noop;
  }
  load(){
    this._onLoad.call(this);
  }
  unload(){
    this._onUnload.call(this);
  }
}

/**
 * 基于路由容器的插件
 */
class RouterPlugin extends BasePlugin{
  type = "RouterPlugin"
  constructor(options){
    super(options);
  }
}

/**
 * 独立容器的插件
 */
class SinglePlugin extends BasePlugin{
  type = "SinglePlugin"
  constructor(options){
    super(options);
  }
}

const isRouterPlugin =  function(plugin){
  return plugin instanceof RouterPlugin
};

const isSinglePlugin =  function(plugin){
  return plugin instanceof SinglePlugin
};

class Dokit{
  options = null
  constructor(options){
    this.options = options;
    let app = createApp(script$3);
    let {features} = options; 
    app.use(getRouter(features));
    app.use(store);
    store.state.features = features;
    this.app = app;
    this.init();
    this.onLoad();
  }

  onLoad(){
    // Lifecycle Load
    applyLifecyle(this.options.features, LifecycleHooks.LOAD);
  }

  onUnload(){
    // Lifecycle UnLoad
    applyLifecyle(this.options.features, LifecycleHooks.UNLOAD);
  }

  init(){
    let dokitRoot = document.createElement('div');
    dokitRoot.id = "dokit-root";
    document.documentElement.appendChild(dokitRoot);
    // dokit 容器
    let el = document.createElement('div');
    el.id = "dokit-container";
    this.app.mount(el);
    dokitRoot.appendChild(el);
  }
}

var index = {
  Dokit
};

export default index;
export { BasePlugin, Dokit, RouterPlugin, SinglePlugin, getGlobalData, isRouterPlugin, isSinglePlugin, noop, popContainer, pushContainer, toggleContainer, updateGlobalData };
