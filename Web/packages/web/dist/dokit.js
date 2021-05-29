(function (vue) {
  'use strict';

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

  var script$6$1 = {
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

  const _withId$6$1 = /*#__PURE__*/vue.withScopeId("data-v-29d70086");

  vue.pushScopeId("data-v-29d70086");
  const _hoisted_1$6$1 = { class: "bar" };
  const _hoisted_2$4$1 = /*#__PURE__*/vue.createVNode("span", { class: "bar-back-btn" }, "返回", -1 /* HOISTED */);
  const _hoisted_3$2$1 = { class: "bar-title" };
  const _hoisted_4$1$1 = { class: "bar-title-text" };
  vue.popScopeId();

  const render$6$1 = /*#__PURE__*/_withId$6$1((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$6$1, [
      vue.withDirectives(vue.createVNode("div", {
        class: "bar-back",
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.handleBackRoute && $options.handleBackRoute(...args)))
      }, [
        vue.createVNode("img", {
          class: "bar-back-icon",
          src: $data.icon
        }, null, 8 /* PROPS */, ["src"]),
        _hoisted_2$4$1
      ], 512 /* NEED_PATCH */), [
        [vue.vShow, $props.canBack]
      ]),
      vue.createVNode("div", _hoisted_3$2$1, [
        vue.createVNode("span", _hoisted_4$1$1, vue.toDisplayString($props.title), 1 /* TEXT */)
      ]),
      vue.createCommentVNode(" TODO 支持切换模式 "),
      vue.createCommentVNode(" <div class=\"bar-other\">\n      <span class=\"bar-other-text\">更多</span>\n    </div> ")
    ]))
  });

  function styleInject$1(css, ref) {
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

  var css_248z$6$1 = ".bar[data-v-29d70086] {\n  background-color: white;\n  height: 50px;\n  width: 100%;\n  display: flex;\n  justify-content: center;\n  align-items: center;\n  padding: 0 10px;\n  box-sizing: border-box;\n  position: relative;\n  border-radius: 10px 10px 0 0;\n}\n.bar-back[data-v-29d70086] {\n  position: absolute;\n  left: 10px;\n  display: flex;\n  flex-direction: row;\n  align-items: center;\n}\n.bar-back-icon[data-v-29d70086] {\n  display: inline-block;\n  height: 18px;\n}\n.bar-back-btn[data-v-29d70086] {\n  color: #337CC4;\n  font-size: 16px;\n  margin-left: 5px;\n}\n.bar-title-text[data-v-29d70086] {\n  color: #333333;\n  font-size: 20px;\n}\n.bar-other-text[data-v-29d70086] {\n  color: #666666;\n  font-size: 16px;\n}\n";
  styleInject$1(css_248z$6$1);

  script$6$1.render = render$6$1;
  script$6$1.__scopeId = "data-v-29d70086";
  script$6$1.__file = "src/common/components/top-bar.vue";

  var script$5$1 = {
    components: {
     TopBar: script$6$1 
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

  const _withId$5$1 = /*#__PURE__*/vue.withScopeId("data-v-8dfbe6e6");

  vue.pushScopeId("data-v-8dfbe6e6");
  const _hoisted_1$5$1 = { class: "container" };
  const _hoisted_2$3$1 = { class: "router-container" };
  vue.popScopeId();

  const render$5$1 = /*#__PURE__*/_withId$5$1((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_top_bar = vue.resolveComponent("top-bar");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$5$1, [
      vue.createVNode(_component_top_bar, {
        title: $options.title,
        canBack: $options.canBack
      }, null, 8 /* PROPS */, ["title", "canBack"]),
      vue.createVNode("div", _hoisted_2$3$1, [
        (vue.openBlock(), vue.createBlock(vue.KeepAlive, null, [
          (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent($options.component)))
        ], 1024 /* DYNAMIC_SLOTS */))
      ])
    ]))
  });

  var css_248z$5$1 = ".container[data-v-8dfbe6e6] {\n  position: absolute;\n  left: 0;\n  right: 0;\n  top: 100px;\n  bottom: 0;\n  background-color: #f5f6f7;\n  display: flex;\n  flex-direction: column;\n  z-index: 99;\n  border-radius: 10px 10px 0 0;\n}\n.router-container[data-v-8dfbe6e6] {\n  margin-top: 5px;\n  background-color: white;\n  flex: 1;\n  overflow-y: scroll;\n}\n";
  styleInject$1(css_248z$5$1);

  script$5$1.render = render$5$1;
  script$5$1.__scopeId = "data-v-8dfbe6e6";
  script$5$1.__file = "src/components/router-container.vue";

  var script$4$1 = {
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

  const _withId$4$1 = /*#__PURE__*/vue.withScopeId("data-v-1e3e6168");

  vue.pushScopeId("data-v-1e3e6168");
  const _hoisted_1$4$1 = {
    class: "container",
    style: {"z-index":"999"}
  };
  const _hoisted_2$2$1 = /*#__PURE__*/vue.createVNode("div", { class: "plugin-container" }, [
    /*#__PURE__*/vue.createVNode("div", null, " I Am Single Container! ")
  ], -1 /* HOISTED */);
  vue.popScopeId();

  const render$4$1 = /*#__PURE__*/_withId$4$1((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$4$1, [
      _hoisted_2$2$1
    ]))
  });

  var css_248z$4$1 = ".container[data-v-1e3e6168] {\n  position: absolute;\n  left: 0;\n  right: 0;\n  bottom: 0;\n  top: 0;\n}\n.plugin-container[data-v-1e3e6168] {\n  background-color: #ffffff;\n  box-shadow: 1px 1px 2px #333333;\n}\n";
  styleInject$1(css_248z$4$1);

  script$4$1.render = render$4$1;
  script$4$1.__scopeId = "data-v-1e3e6168";
  script$4$1.__file = "src/components/single-container.vue";

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
      this._state = vue.reactive({
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

  var script$3$1 = {
    components: {
      RouterContainer: script$5$1,
      SingleContainer: script$4$1
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

  const _withId$3$1 = /*#__PURE__*/vue.withScopeId("data-v-6c0a0fc1");

  vue.pushScopeId("data-v-6c0a0fc1");
  const _hoisted_1$3$1 = { class: "dokit-app" };
  vue.popScopeId();

  const render$3$1 = /*#__PURE__*/_withId$3$1((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_router_container = vue.resolveComponent("router-container");
    const _directive_dragable = vue.resolveDirective("dragable");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$3$1, [
      vue.withDirectives(vue.createVNode("div", {
        class: "dokit-entry-btn",
        style: {"z-index":"10000"},
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.toggleShowContainer && $options.toggleShowContainer(...args)))
      }, null, 512 /* NEED_PATCH */), [
        [_directive_dragable]
      ]),
      vue.withDirectives(vue.createVNode("div", {
        class: "mask",
        onClick: _cache[2] || (_cache[2] = (...args) => (_ctx.toggleContainer && _ctx.toggleContainer(...args)))
      }, null, 512 /* NEED_PATCH */), [
        [vue.vShow, $options.showContainer]
      ]),
      vue.withDirectives(vue.createVNode(_component_router_container, null, null, 512 /* NEED_PATCH */), [
        [vue.vShow, $options.showContainer]
      ]),
      vue.createCommentVNode(" <single-container></single-container> ")
    ]))
  });

  var css_248z$3$1 = ".dokit-app[data-v-6c0a0fc1] {\n  font-family: Helvetica Neue, Helvetica, Arial, sans-serif;\n}\n.dokit-entry-btn[data-v-6c0a0fc1] {\n  width: 50px;\n  height: 50px;\n  padding: 10px;\n  box-sizing: border-box;\n  background-image: url(//pt-starimg.didistatic.com/static/starimg/img/OzaetKDzHr1618905183992.png);\n  background-size: 50px;\n  background-position: center;\n  background-repeat: no-repeat;\n}\n.mask[data-v-6c0a0fc1] {\n  position: absolute;\n  top: 0;\n  left: 0;\n  right: 0;\n  bottom: 0;\n  z-index: 3;\n  background-color: #333333;\n  opacity: 0.3;\n}\n";
  styleInject$1(css_248z$3$1);

  script$3$1.render = render$3$1;
  script$3$1.__scopeId = "data-v-6c0a0fc1";
  script$3$1.__file = "src/components/app.vue";

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

  var script$2$1 = {
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

  const _withId$2$1 = /*#__PURE__*/vue.withScopeId("data-v-00020244");

  vue.pushScopeId("data-v-00020244");
  const _hoisted_1$2$1 = { class: "card" };
  const _hoisted_2$1$1 = { class: "card-title" };
  const _hoisted_3$1$1 = { class: "card-title-text" };
  const _hoisted_4$4 = { class: "item-list" };
  const _hoisted_5$3 = { class: "item-icon" };
  const _hoisted_6$2 = { class: "item-title" };
  vue.popScopeId();

  const render$2$1 = /*#__PURE__*/_withId$2$1((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$2$1, [
      vue.createVNode("div", _hoisted_2$1$1, [
        vue.createVNode("span", _hoisted_3$1$1, vue.toDisplayString($props.title), 1 /* TEXT */)
      ]),
      vue.createVNode("div", _hoisted_4$4, [
        (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.list, (item, index) => {
          return (vue.openBlock(), vue.createBlock("div", {
            class: "item",
            key: index,
            onClick: $event => ($options.handleClickItem(item))
          }, [
            vue.createVNode("div", _hoisted_5$3, [
              vue.createVNode("img", {
                class: "item-icon-image",
                src: item.icon || $data.defaultIcon
              }, null, 8 /* PROPS */, ["src"])
            ]),
            vue.createVNode("div", _hoisted_6$2, vue.toDisplayString(item.nameZh || '默认功能'), 1 /* TEXT */)
          ], 8 /* PROPS */, ["onClick"]))
        }), 128 /* KEYED_FRAGMENT */))
      ])
    ]))
  });

  var css_248z$2$1 = ".card[data-v-00020244] {\n  margin-bottom: 10px;\n  padding: 10px;\n  background-color: white;\n}\n.card-title-text[data-v-00020244] {\n  font-size: 16px;\n  color: #333333;\n}\n.item-list[data-v-00020244] {\n  display: flex;\n  flex-wrap: wrap;\n  margin-top: 5px;\n}\n.item[data-v-00020244] {\n  display: flex;\n  flex-direction: column;\n  align-items: center;\n  width: 25%;\n  margin-top: 5px;\n}\n.item .item-icon-image[data-v-00020244] {\n  width: 30px;\n}\n.item .item-title[data-v-00020244] {\n  font-size: 14px;\n  margin-top: 5px;\n}\n";
  styleInject$1(css_248z$2$1);

  script$2$1.render = render$2$1;
  script$2$1.__scopeId = "data-v-00020244";
  script$2$1.__file = "src/common/components/card.vue";

  var script$1$1 = {
    props: {
      version: String
    },
    data(){
      return {
        dokitIcon: dokitIcon
      }
    }
  };

  const _withId$1$1 = /*#__PURE__*/vue.withScopeId("data-v-b7dc930c");

  vue.pushScopeId("data-v-b7dc930c");
  const _hoisted_1$1$1 = { class: "card version" };
  const _hoisted_2$d = { class: "version-text" };
  const _hoisted_3$c = { class: "version-image" };
  vue.popScopeId();

  const render$1$1 = /*#__PURE__*/_withId$1$1((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$1$1, [
      vue.createVNode("div", null, [
        vue.createVNode("span", _hoisted_2$d, "当前版本：V" + vue.toDisplayString($props.version), 1 /* TEXT */)
      ]),
      vue.createVNode("div", _hoisted_3$c, [
        vue.createVNode("img", {
          class: "dokit-icon",
          src: $data.dokitIcon
        }, null, 8 /* PROPS */, ["src"])
      ])
    ]))
  });

  var css_248z$1$1 = ".card[data-v-b7dc930c] {\n  padding: 10px;\n  background-color: white;\n}\n.version[data-v-b7dc930c] {\n  padding: 20px 0;\n  text-align: center;\n}\n.version .version-text[data-v-b7dc930c] {\n  font-size: 16px;\n  color: #999999;\n}\n.version .version-image[data-v-b7dc930c] {\n  margin-top: 20px;\n}\n.version .dokit-icon[data-v-b7dc930c] {\n  width: 150px;\n}\n";
  styleInject$1(css_248z$1$1);

  script$1$1.render = render$1$1;
  script$1$1.__scopeId = "data-v-b7dc930c";
  script$1$1.__file = "src/common/components/version.vue";

  var script$g = {
    components: {
      TopBar: script$6$1,
      Card: script$2$1,
      VersionCard: script$1$1
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

  const _withId$9 = /*#__PURE__*/vue.withScopeId("data-v-957c9522");

  vue.pushScopeId("data-v-957c9522");
  const _hoisted_1$f = { class: "index-container" };
  vue.popScopeId();

  const render$g = /*#__PURE__*/_withId$9((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_card = vue.resolveComponent("card");
    const _component_version_card = vue.resolveComponent("version-card");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$f, [
      (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($options.features, (item, index) => {
        return (vue.openBlock(), vue.createBlock(_component_card, {
          key: index,
          title: item.title,
          list: item.list,
          onHandleClickItem: $options.handleClickItem
        }, null, 8 /* PROPS */, ["title", "list", "onHandleClickItem"]))
      }), 128 /* KEYED_FRAGMENT */)),
      vue.createVNode(_component_version_card, { version: $data.version }, null, 8 /* PROPS */, ["version"])
    ]))
  });

  var css_248z$g = ".index-container[data-v-957c9522] {\n  background-color: #f5f6f7;\n}\n";
  styleInject$1(css_248z$g);

  script$g.render = render$g;
  script$g.__scopeId = "data-v-957c9522";
  script$g.__file = "src/components/home.vue";

  const defaultRoute = [{
    name: 'home',
    component: script$g
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
    const currentRoute = vue.shallowRef(homeRoute);
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
        get: () => vue.unref(currentRoute)
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

  const noop$1 = () => {};

  class BasePlugin{
    type = ''
    name = ''
    nameZh = ''
    icon = ''
    component = null
    _onLoad = noop$1
    _onUnload = noop$1
    constructor(options){
      let {name, nameZh, icon, component, onLoad, onUnload} = options;
      this.name = name;
      this.nameZh = nameZh;
      this.icon = icon;
      this.component = component;
      this._onLoad = onLoad || noop$1;
      this._onUnload = onUnload || noop$1;
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

  class Dokit{
    options = null
    constructor(options){
      this.options = options;
      let app = vue.createApp(script$3$1);
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

  var script$f = {
    props: {
      tabs: {
        type: Array
      },
    },
    data() {
      return {
        curIndex: 0
      };
    },
    methods: {
      handleClickTab(item, index) {
        let { type } = item;
        this.curIndex = index;
        this.$emit("changeTap", type);
      },
    },
  };

  const _withId$8 = /*#__PURE__*/vue.withScopeId("data-v-9d220f86");

  vue.pushScopeId("data-v-9d220f86");
  const _hoisted_1$e = { class: "tab-container" };
  const _hoisted_2$c = { class: "tab-list" };
  const _hoisted_3$b = { class: "tab-item-text" };
  vue.popScopeId();

  const render$f = /*#__PURE__*/_withId$8((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$e, [
      vue.createVNode("div", _hoisted_2$c, [
        (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.tabs, (item, index) => {
          return (vue.openBlock(), vue.createBlock("div", {
            class: ["tab-item", $data.curIndex === index? 'tab-active': 'tab-default'],
            key: index,
            onClick: $event => ($options.handleClickTab(item, index))
          }, [
            vue.createVNode("span", _hoisted_3$b, vue.toDisplayString(item.name), 1 /* TEXT */)
          ], 10 /* CLASS, PROPS */, ["onClick"]))
        }), 128 /* KEYED_FRAGMENT */))
      ])
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

  var css_248z$f = ".tab-container[data-v-9d220f86] .tab-list[data-v-9d220f86] {\n  display: flex;\n  height: 38px;\n  justify-content: space-between;\n  align-items: center;\n  border: 1px solid #f5f6f7;\n}\n.tab-container[data-v-9d220f86] .tab-item[data-v-9d220f86] {\n  flex: 1;\n  height: 38px;\n  line-height: 38px;\n  text-align: center;\n}\n.tab-container[data-v-9d220f86] .tab-item-text[data-v-9d220f86] {\n  font-size: 16px;\n  color: #333333;\n}\n.tab-container[data-v-9d220f86] .tab-active[data-v-9d220f86] {\n  border-bottom: 1px solid #1485ee;\n}\n.tab-container[data-v-9d220f86] .tab-default[data-v-9d220f86] {\n  border: none;\n}\n";
  styleInject(css_248z$f);

  script$f.render = render$f;
  script$f.__scopeId = "data-v-9d220f86";
  script$f.__file = "src/plugins/console/console-tap.vue";

  const getDataType = function (arg) {
    if (arg === null ) {
      return 'Null'
    }
    if (arg === undefined) {
      return 'Undefined'
    }
    return arg.constructor && arg.constructor.name || 'Object'
  };

  const MAX_DISPLAY_PROPERTY_NUM = 5;

  const getDataStructureStr = function (arg, isFirstLevel) { 
    let dataType = getDataType(arg);
    let str = '';
    switch (dataType) {
      case 'Number':
      case 'String':
      case 'Boolean':
      case 'RegExp':
      case 'Symbol':
      case 'Function':
        str = arg.toString();
        break;

      case 'Null':
      case 'Undefined':
        str = arg + '';
        break;
      case 'Array':
        break;
      case 'Object':
        str += '{';
        if (isFirstLevel) {
          let propertyNames = Object.getOwnPropertyNames(arg);
          let propertyNameStrs =  propertyNames.map(key =>
            str += `${key}: ${getDataStructureStr(arg[key], false)}`
          );
          propertyNameStrs.join(',');
          if(propertyNameStrs.length > MAX_DISPLAY_PROPERTY_NUM) {
            str+= ',...';
          }
        } else {
          str += '...';
        }
        
        str += '}';
        
        break;
    }
    return str
  };

  const TYPE_CAN_FOLD = ['Object', 'Array'];
  var script$e = {
    name: "Detail",
    components: {
      Detail: script$e
    },
    props: {
      detailValue: [String, Number, Object],
      detailIndex: [String, Number]
    },
    data () {
      return {
        unfold: false
      }
    },
    computed: {
      dataType () {
       return getDataType(this.detailValue)
      },
      canFold () {
        if (TYPE_CAN_FOLD.indexOf(this.dataType) > -1) {
          return true
        }
        return false
      },
      displayDetailValue () {
        let value = '';
        if (this.canFold) {
          if (this.dataType === 'Object') {
            value = 'Object';
          }
          if (this.dataType === 'Array') {
            value = `Array(${this.detailValue.length})`;
          }
        } else {
          value = `<span style="color:#1802C7;">${this.detailValue}</span>`;
        }
        return `<span style="color:#7D208C;">${this.detailIndex}</span>: ${value}`
      }
    },
    methods: {
      unfoldDetail() {
        this.unfold = !this.unfold;
      }
    }
  };

  const _withId$7 = /*#__PURE__*/vue.withScopeId("data-v-151438cc");

  const render$e = /*#__PURE__*/_withId$7((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_Detail = vue.resolveComponent("Detail");

    return (vue.openBlock(), vue.createBlock("div", {
      class: ["detail-container", [$options.canFold ? 'can-unfold':'', $data.unfold ? 'unfolded' : '']]
    }, [
      vue.createVNode("div", {
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.unfoldDetail && $options.unfoldDetail(...args))),
        innerHTML: $options.displayDetailValue
      }, null, 8 /* PROPS */, ["innerHTML"]),
      ($options.canFold)
        ? (vue.openBlock(true), vue.createBlock(vue.Fragment, { key: 0 }, vue.renderList($props.detailValue, (key, index) => {
            return vue.withDirectives((vue.openBlock(), vue.createBlock("div", { key: index }, [
              vue.createVNode(_component_Detail, {
                detailValue: key,
                detailIndex: index
              }, null, 8 /* PROPS */, ["detailValue", "detailIndex"])
            ], 512 /* NEED_PATCH */)), [
              [vue.vShow, $data.unfold]
            ])
          }), 128 /* KEYED_FRAGMENT */))
        : vue.createCommentVNode("v-if", true)
    ], 2 /* CLASS */))
  });

  var css_248z$e = ".detail-container[data-v-151438cc] {\n  font-size: 12px;\n  margin-left: 24px;\n  position: relative;\n}\n.can-unfold[data-v-151438cc][data-v-151438cc]::before {\n  content: \"\";\n  width: 0;\n  height: 0;\n  border: 4px solid transparent;\n  position: absolute;\n  border-left-color: #333;\n  left: -12px;\n  top: 3px;\n}\n.unfolded[data-v-151438cc][data-v-151438cc]::before {\n  border: 4px solid transparent;\n  border-top-color: #333;\n  top: 6px;\n}\n";
  styleInject(css_248z$e);

  script$e.render = render$e;
  script$e.__scopeId = "data-v-151438cc";
  script$e.__file = "src/plugins/console/log-detail.vue";

  const DATATYPE_NOT_DISPLAY = ['Number', 'String', 'Boolean'];

  var script$d = {
    components: {
      Detail: script$e
    },
    props: {
      type: [Number],
      value: [String, Number, Object]
    },
    data () {
      return {
        showDetail: false
      }
    },
    computed: {
      logPreview () {
        let dataType = '';
        let html = `<div>`;
        this.value.forEach(arg => {
          dataType = getDataType(arg);
          if (DATATYPE_NOT_DISPLAY.indexOf(dataType) === -1) {
            html += `<span class="data-type">${dataType}</span>`;
          }
          html += `<span class="data-structure">${getDataStructureStr(arg, true)}</span>`;
        });
        html += `</div>`;
        return html
      }
    },
    methods: {
      toggleDetail () {
        this.showDetail = !this.showDetail;
      }
    }
  };

  const _hoisted_1$d = { class: "log-ltem" };
  const _hoisted_2$b = { key: 0 };

  function render$d(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_Detail = vue.resolveComponent("Detail");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$d, [
      vue.createVNode("div", {
        class: "log-preview",
        innerHTML: $options.logPreview,
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.toggleDetail && $options.toggleDetail(...args)))
      }, null, 8 /* PROPS */, ["innerHTML"]),
      ($data.showDetail && typeof $props.value === 'object')
        ? (vue.openBlock(), vue.createBlock("div", _hoisted_2$b, [
            (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.value, (key, index) => {
              return (vue.openBlock(), vue.createBlock("div", {
                class: "list-item",
                key: index
              }, [
                vue.createVNode(_component_Detail, {
                  detailValue: key,
                  detailIndex: index
                }, null, 8 /* PROPS */, ["detailValue", "detailIndex"])
              ]))
            }), 128 /* KEYED_FRAGMENT */))
          ]))
        : vue.createCommentVNode("v-if", true)
    ]))
  }

  var css_248z$d = ".log-ltem {\n  padding: 5px;\n  border-top: 1px solid #eee;\n  text-align: left;\n  font-size: 12px;\n}\n.log-ltem:first-child {\n  border: none;\n}\n.log-preview .data-type {\n  margin-left: 5px;\n  margin-right: 5px;\n  font-style: italic;\n  font-weight: bold;\n  color: #aaa;\n}\n.log-preview .data-structure {\n  font-style: italic;\n}\n";
  styleInject(css_248z$d);

  script$d.render = render$d;
  script$d.__file = "src/plugins/console/log-item.vue";

  var script$c = {
    components: {
      LogItem: script$d
    },
    props: {
      logList: {
        type: Array,
        default: [],
      },
    },
    data() {
      return {};
    }
  };

  const _withId$6 = /*#__PURE__*/vue.withScopeId("data-v-722c5870");

  vue.pushScopeId("data-v-722c5870");
  const _hoisted_1$c = { class: "log-container" };
  vue.popScopeId();

  const render$c = /*#__PURE__*/_withId$6((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_log_item = vue.resolveComponent("log-item");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$c, [
      (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.logList, (log, index) => {
        return (vue.openBlock(), vue.createBlock(_component_log_item, {
          key: index,
          value: log.value,
          type: log.type
        }, null, 8 /* PROPS */, ["value", "type"]))
      }), 128 /* KEYED_FRAGMENT */))
    ]))
  });

  var css_248z$c = ".tab-container[data-v-722c5870] .tab-list[data-v-722c5870] {\n  display: flex;\n  height: 38px;\n  justify-content: space-between;\n  align-items: center;\n  border: 1px solid #f5f6f7;\n}\n.tab-container[data-v-722c5870] .tab-item[data-v-722c5870] {\n  flex: 1;\n  height: 38px;\n  line-height: 38px;\n  text-align: center;\n}\n.tab-container[data-v-722c5870] .tab-item-text[data-v-722c5870] {\n  font-size: 16px;\n  color: #333333;\n}\n.tab-container[data-v-722c5870] .tab-active[data-v-722c5870] {\n  border-bottom: 1px solid #1485ee;\n}\n.tab-container[data-v-722c5870] .tab-default[data-v-722c5870] {\n  border: none;\n}\n";
  styleInject(css_248z$c);

  script$c.render = render$c;
  script$c.__scopeId = "data-v-722c5870";
  script$c.__file = "src/plugins/console/log-container.vue";

  const LogMap = {
    0: 'All',
    1: 'Log',
    2: 'Info',
    3: 'Warn',
    4: 'Error'
  };
  const LogEnum = {
    ALL: 0,
    LOG: 1,
    INFO: 2,
    WARN: 3,
    ERROR: 4
  };

  const ConsoleLogMap = {
    'log': LogEnum.LOG,
    'info': LogEnum.INFO,
    'warn': LogEnum.WARN,
    'error': LogEnum.ERROR
  };

  const CONSOLE_METHODS = ["log", "info", 'warn', 'error'];

  const LogTabs = Object.keys(LogMap).map(key => {
    return {
      type: parseInt(key),
      name: LogMap[key]
    }
  });

  const excuteScript = function(command){
    let ret; 
    try{
      ret = eval.call(window, `(${command})`);
    }catch(e){
      ret = eval.call(window, command);
    }
    return ret
  };

  const origConsole = {};
  const noop = () => {};
  const overrideConsole = function(callback) {
    const winConsole = window.console;
    CONSOLE_METHODS.forEach((name) => {
      let origin = (origConsole[name] = noop);
      if (winConsole[name]) {
        origin = origConsole[name] = winConsole[name].bind(winConsole);
      }

      winConsole[name] = (...args) => {
        callback({
          name: name,
          type: ConsoleLogMap[name],
          value: args
        });
        origin(...args);
      };
    });
  };

  const restoreConsole = function(){
    const winConsole = window.console;
    CONSOLE_METHODS.forEach((name) => {
      winConsole[name] = origConsole[name];
    });
  };

  var script$b = {
    data(){
      return {
        command: ""
      }
    },
    methods: {
      excuteCommand(){
        if(!this.command){
          return
        }
        let ret = excuteScript(this.command);
        console.log(ret);
      }
    }
  };

  const _withId$5 = /*#__PURE__*/vue.withScopeId("data-v-f469c502");

  vue.pushScopeId("data-v-f469c502");
  const _hoisted_1$b = { class: "operation" };
  const _hoisted_2$a = { class: "input-wrapper" };
  const _hoisted_3$a = /*#__PURE__*/vue.createVNode("span", null, "Excute", -1 /* HOISTED */);
  vue.popScopeId();

  const render$b = /*#__PURE__*/_withId$5((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$b, [
      vue.createVNode("div", _hoisted_2$a, [
        vue.withDirectives(vue.createVNode("input", {
          class: "input",
          placeholder: "Command……",
          "onUpdate:modelValue": _cache[1] || (_cache[1] = $event => ($data.command = $event))
        }, null, 512 /* NEED_PATCH */), [
          [vue.vModelText, $data.command]
        ])
      ]),
      vue.createVNode("div", {
        class: "button-wrapper",
        onClick: _cache[2] || (_cache[2] = (...args) => ($options.excuteCommand && $options.excuteCommand(...args)))
      }, [
        _hoisted_3$a
      ])
    ]))
  });

  var css_248z$b = ".operation[data-v-f469c502] {\n  height: 50px;\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n}\n.input-wrapper[data-v-f469c502] {\n  flex: 1;\n  height: 100%;\n}\n.input-wrapper[data-v-f469c502] .input[data-v-f469c502] {\n  height: 100%;\n  width: 100%;\n  outline: none;\n  border: none;\n  line-height: 100%;\n  padding: 0 10px;\n  font-size: 18px;\n}\n.button-wrapper[data-v-f469c502] {\n  height: 100%;\n  line-height: 100%;\n  margin-left: 10px;\n  padding: 0 10px;\n  border-left: 1px solid #f5f6f7;\n  display: flex;\n  align-items: center;\n}\n";
  styleInject(css_248z$b);

  script$b.render = render$b;
  script$b.__scopeId = "data-v-f469c502";
  script$b.__file = "src/plugins/console/op-command.vue";

  var script$a = {
    components: {
      ConsoleTap: script$f,
      LogContainer: script$c,
      OperationCommand: script$b
    },
    data() {
      return {
        logTabs: LogTabs,
        curTab: LogEnum.ALL
      }
    },
    computed:{
      logList(){
        return this.$store.state.logList || []
      },
      curLogList(){
        if(this.curTab == LogEnum.ALL){
          return this.logList
        }
        return this.logList.filter(log => {
          return log.type == this.curTab
        })
      }
    },
    created () {},
    methods: {
      handleChangeTab(type){
        this.curTab = type;
      }
    }
  };

  const _withId$4 = /*#__PURE__*/vue.withScopeId("data-v-35ae264e");

  vue.pushScopeId("data-v-35ae264e");
  const _hoisted_1$a = { class: "console-container" };
  const _hoisted_2$9 = { class: "log-container" };
  const _hoisted_3$9 = { class: "info-container" };
  const _hoisted_4$3 = { class: "operation-container" };
  vue.popScopeId();

  const render$a = /*#__PURE__*/_withId$4((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_console_tap = vue.resolveComponent("console-tap");
    const _component_log_container = vue.resolveComponent("log-container");
    const _component_operation_command = vue.resolveComponent("operation-command");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$a, [
      vue.createVNode(_component_console_tap, {
        tabs: $data.logTabs,
        onChangeTap: $options.handleChangeTab
      }, null, 8 /* PROPS */, ["tabs", "onChangeTap"]),
      vue.createVNode("div", _hoisted_2$9, [
        vue.createVNode("div", _hoisted_3$9, [
          vue.createVNode(_component_log_container, { logList: $options.curLogList }, null, 8 /* PROPS */, ["logList"])
        ]),
        vue.createVNode("div", _hoisted_4$3, [
          vue.createVNode(_component_operation_command)
        ])
      ])
    ]))
  });

  var css_248z$a = ".console-container[data-v-35ae264e] {\n  display: flex;\n  flex-direction: column;\n  height: 100%;\n}\n.log-container[data-v-35ae264e] {\n  flex: 1;\n  display: flex;\n  flex-direction: column;\n  overflow: hidden;\n}\n.log-container[data-v-35ae264e] .info-container[data-v-35ae264e] {\n  flex: 1;\n  background-color: #ffffff;\n  border-bottom: 1px solid #f5f6f7;\n  overflow-y: scroll;\n}\n";
  styleInject(css_248z$a);

  script$a.render = render$a;
  script$a.__scopeId = "data-v-35ae264e";
  script$a.__file = "src/plugins/console/main.vue";

  var Console = new RouterPlugin({
    name: 'console',
    nameZh: '日志',
    component: script$a,
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/PbNXVyzTbq1618997544543.png',
    onLoad(){
      overrideConsole(({name, type, value}) => {
        let state = getGlobalData();
        state.logList = state.logList || [];
        state.logList.push({
          type: type,
          name: name,
          value: value
        });
      });
    },
    onUnload(){
      restoreConsole();
    }
  });

  var script$9 = {
    props: {
      title: String
    }
  };

  const _hoisted_1$9 = { class: "dokit-card" };
  const _hoisted_2$8 = { class: "dokit-card__header" };
  const _hoisted_3$8 = { class: "dokit-card__body" };

  function render$9(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$9, [
      vue.createVNode("div", _hoisted_2$8, vue.toDisplayString($props.title), 1 /* TEXT */),
      vue.createVNode("div", _hoisted_3$8, [
        vue.renderSlot(_ctx.$slots, "default")
      ])
    ]))
  }

  var css_248z$9 = ".dokit-card {\n  border-radius: 10px;\n  box-shadow: 0 8px 12px #ebedf0;\n  overflow: hidden;\n}\n.dokit-card .dokit-card__header {\n  background-color: #337cc4;\n  padding: 10px;\n  color: #fff;\n}\n.dokit-card .dokit-card__body {\n  padding: 10px;\n}\n";
  styleInject(css_248z$9);

  script$9.render = render$9;
  script$9.__file = "src/common/Card.vue";

  var script$8 = {
    components: {
      Card: script$9
    },
    data() {
      return {
        ua: window.navigator.userAgent,
        url: window.location.href,
        ratio: window.devicePixelRatio,
        screen: window.screen,
        viewport: {
          width: document.documentElement.clientWidth,
          height: document.documentElement.clientHeight
        }
      }
    },
  };

  const _withId$3 = /*#__PURE__*/vue.withScopeId("data-v-756cbe6c");

  vue.pushScopeId("data-v-756cbe6c");
  const _hoisted_1$8 = { class: "app-info-container" };
  const _hoisted_2$7 = { class: "info-wrapper" };
  const _hoisted_3$7 = { border: "1" };
  const _hoisted_4$2 = /*#__PURE__*/vue.createVNode("td", null, "UA", -1 /* HOISTED */);
  const _hoisted_5$2 = /*#__PURE__*/vue.createVNode("td", null, "URL", -1 /* HOISTED */);
  const _hoisted_6$1 = { class: "info-wrapper" };
  const _hoisted_7$1 = { border: "1" };
  const _hoisted_8 = /*#__PURE__*/vue.createVNode("td", null, "设备缩放比", -1 /* HOISTED */);
  const _hoisted_9 = /*#__PURE__*/vue.createVNode("td", null, "screen", -1 /* HOISTED */);
  const _hoisted_10 = /*#__PURE__*/vue.createVNode("td", null, "viewport", -1 /* HOISTED */);
  vue.popScopeId();

  const render$8 = /*#__PURE__*/_withId$3((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_Card = vue.resolveComponent("Card");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$8, [
      vue.createVNode("div", _hoisted_2$7, [
        vue.createVNode(_component_Card, { title: "Page Info" }, {
          default: _withId$3(() => [
            vue.createVNode("table", _hoisted_3$7, [
              vue.createVNode("tr", null, [
                _hoisted_4$2,
                vue.createVNode("td", null, vue.toDisplayString($data.ua), 1 /* TEXT */)
              ]),
              vue.createVNode("tr", null, [
                _hoisted_5$2,
                vue.createVNode("td", null, vue.toDisplayString($data.url), 1 /* TEXT */)
              ])
            ])
          ]),
          _: 1 /* STABLE */
        })
      ]),
      vue.createVNode("div", _hoisted_6$1, [
        vue.createVNode(_component_Card, { title: "Device Info" }, {
          default: _withId$3(() => [
            vue.createVNode("table", _hoisted_7$1, [
              vue.createVNode("tr", null, [
                _hoisted_8,
                vue.createVNode("td", null, vue.toDisplayString($data.ratio), 1 /* TEXT */)
              ]),
              vue.createVNode("tr", null, [
                _hoisted_9,
                vue.createVNode("td", null, vue.toDisplayString($data.screen.width) + "X" + vue.toDisplayString($data.screen.height), 1 /* TEXT */)
              ]),
              vue.createVNode("tr", null, [
                _hoisted_10,
                vue.createVNode("td", null, vue.toDisplayString($data.viewport.width) + "X" + vue.toDisplayString($data.viewport.height), 1 /* TEXT */)
              ])
            ])
          ]),
          _: 1 /* STABLE */
        })
      ])
    ]))
  });

  var css_248z$8 = ".app-info-container[data-v-756cbe6c] {\n  font-size: 14px;\n  height: 100%;\n  overflow: hidden;\n}\n.info-wrapper[data-v-756cbe6c] {\n  margin: 20px 20px 0 20px;\n}\ntable[data-v-756cbe6c] {\n  border-color: #eee;\n  width: 100%;\n  border-collapse: collapse;\n  border-spacing: 0;\n}\ntr[data-v-756cbe6c] {\n  width: 100%;\n}\ntd[data-v-756cbe6c],\nth[data-v-756cbe6c] {\n  padding: 5px;\n}\n";
  styleInject(css_248z$8);

  script$8.render = render$8;
  script$8.__scopeId = "data-v-756cbe6c";
  script$8.__file = "src/plugins/app-info/ToolAppInfo.vue";

  var AppInfo = new RouterPlugin({
    nameZh: '应用信息',
    name: 'app-info',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: script$8
  });

  var script$7 = {
    
  };

  const _hoisted_1$7 = { class: "hello-world" };
  const _hoisted_2$6 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, "Hello Dokit", -1 /* HOISTED */);
  const _hoisted_3$6 = /*#__PURE__*/vue.createVNode("div", null, "Demo Plugin", -1 /* HOISTED */);

  function render$7(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$7, [
      _hoisted_2$6,
      _hoisted_3$6
    ]))
  }

  var css_248z$7 = "\n.hello-world{\n  padding:10px;\n  text-align: center;\n}\n";
  styleInject(css_248z$7);

  script$7.render = render$7;
  script$7.__file = "src/plugins/demo-plugin/ToolHelloWorld.vue";

  var DemoPlugin = new RouterPlugin({
    nameZh: '测试',
    name: 'test',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: script$7
  });

  var script$6 = {
    
  };

  const _hoisted_1$6 = { class: "hello-world" };
  const _hoisted_2$5 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, "Hello Dokit", -1 /* HOISTED */);
  const _hoisted_3$5 = /*#__PURE__*/vue.createVNode("div", null, "Demo Plugin", -1 /* HOISTED */);

  function render$6(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$6, [
      _hoisted_2$5,
      _hoisted_3$5
    ]))
  }

  var css_248z$6 = "\n.hello-world{\n  padding:10px;\n  text-align: center;\n}\n";
  styleInject(css_248z$6);

  script$6.render = render$6;
  script$6.__file = "src/plugins/demo-single-plugin/ToolHelloWorld.vue";

  var DemoSinglePlugin = new SinglePlugin({
    nameZh: '独立 Single',
    name: 'test',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: script$6
  });

  var script$5 = {
    
  };

  const _hoisted_1$5 = { class: "hello-world" };
  const _hoisted_2$4 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, "Hello Dokit", -1 /* HOISTED */);
  const _hoisted_3$4 = /*#__PURE__*/vue.createVNode("div", null, "Demo Plugin", -1 /* HOISTED */);

  function render$5(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$5, [
      _hoisted_2$4,
      _hoisted_3$4
    ]))
  }

  var css_248z$5 = "\n.hello-world{\n  padding:10px;\n  text-align: center;\n}\n";
  styleInject(css_248z$5);

  script$5.render = render$5;
  script$5.__file = "src/plugins/h5-door/ToolHelloWorld.vue";

  var H5DoorPlugin = new RouterPlugin({
    nameZh: '任意门',
    name: 'h5-door',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/FHqpI3InaS1618997548865.png',
    component: script$5
  });

  var script$4 = {
    components: {

    },
    props: {
      reqDetail: {
        type: Object,
        default: function () {
          return {}
        }
      }
    },
    data () {
      return {
        request: this.reqDetail,
      }
    },
    computed: {
      displayRequestHeader () {
        let reqHeaders = this.reqDetail.reqHeaders;
        let value = '<tbody>';
        if(!reqHeaders){
          value = '<tbody><tr><td>Empty</td></tr></tbody>';
          return value
        }
        for(var key in reqHeaders){
          value += '<tr>';
          value += '<td class="header-key">'+key+'</td>';
          value += '<td>'+reqHeaders[key]+'</td>';
          value += '</tr>';
        }
        value += '</tbody>';
        return value
      },
      displayResponseHeader () {
        let resHeaders = this.reqDetail.resHeaders;
        let value = '<tbody>';
        for(var key in resHeaders){
          value += '<tr>';
          value += '<td class="header-key">'+key+'</td>';
          value += '<td>'+resHeaders[key]+'</td>';
          value += '</tr>';
        }
        value += '</tbody>';
        return value
      }
    },
    methods: {
      hideDetail(){
        this.$parent.hideDetail();
      }
    }
  };

  const _withId$2 = /*#__PURE__*/vue.withScopeId("data-v-5de00d90");

  vue.pushScopeId("data-v-5de00d90");
  const _hoisted_1$4 = { class: "request-detail" };
  const _hoisted_2$3 = { class: "request-url" };
  const _hoisted_3$3 = { class: "request-data" };
  const _hoisted_4$1 = { class: "request-headers-section" };
  const _hoisted_5$1 = /*#__PURE__*/vue.createVNode("h2", null, "Request Headers", -1 /* HOISTED */);
  const _hoisted_6 = /*#__PURE__*/vue.createVNode("h2", null, "Response Headers", -1 /* HOISTED */);
  const _hoisted_7 = { class: "request-data" };
  vue.popScopeId();

  const render$4 = /*#__PURE__*/_withId$2((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$4, [
      vue.createVNode("div", null, [
        vue.createVNode("div", _hoisted_2$3, vue.toDisplayString($data.request.url), 1 /* TEXT */),
        vue.createVNode("pre", _hoisted_3$3, vue.toDisplayString($data.request.data), 1 /* TEXT */),
        vue.createVNode("div", _hoisted_4$1, [
          _hoisted_5$1,
          vue.createVNode("table", {
            class: "request-headers",
            innerHTML: $options.displayRequestHeader
          }, null, 8 /* PROPS */, ["innerHTML"]),
          _hoisted_6,
          vue.createVNode("table", {
            class: "request-headers",
            innerHTML: $options.displayResponseHeader
          }, null, 8 /* PROPS */, ["innerHTML"])
        ]),
        vue.createVNode("pre", _hoisted_7, vue.toDisplayString($data.request.resTxt), 1 /* TEXT */)
      ]),
      vue.createVNode("div", {
        class: "detail-back",
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.hideDetail && $options.hideDetail(...args)))
      }, "返回列表")
    ]))
  });

  var css_248z$4 = ".request-detail[data-v-5de00d90] {\n  font-size: 12px;\n  position: relative;\n  width: 100%;\n  height: 100%;\n  left: 0;\n  top: 0;\n  z-index: 10;\n  padding-bottom: 40px;\n  background: #fff;\n}\n.request-url[data-v-5de00d90] {\n  background: #f3f3f3;\n  color: #333;\n  -webkit-user-select: text;\n  -moz-user-select: text;\n  -ms-user-select: text;\n  user-select: text;\n  margin-bottom: 10px;\n  word-break: break-all;\n  padding: 10px;\n  font-size: 16px;\n  min-height: 40px;\n  border-bottom: 1px solid #ccc;\n}\n.request-headers-section[data-v-5de00d90] {\n  border-top: 1px solid #ccc;\n  border-bottom: 1px solid #ccc;\n  margin-bottom: 10px;\n}\n.request-headers-section h2[data-v-5de00d90] {\n  margin: 2px 0;\n  background: #f3f3f3;\n  color: #333;\n  padding: 10px;\n  font-size: 14px;\n}\npre.request-data[data-v-5de00d90] {\n  -webkit-user-select: text;\n  -moz-user-select: text;\n  -ms-user-select: text;\n  user-select: text;\n  overflow-x: auto;\n  -webkit-overflow-scrolling: touch;\n  padding: 10px;\n  font-size: 12px;\n  margin-bottom: 10px;\n  white-space: pre-wrap;\n  border-top: 1px solid #ccc;\n  color: #333;\n  border-bottom: 1px solid #ccc;\n}\n.detail-back[data-v-5de00d90] {\n  position: absolute;\n  left: 0;\n  bottom: 0;\n  color: #333;\n  width: 100%;\n  border-top: 1px solid #ccc;\n  background: #f3f3f3;\n  display: block;\n  height: 40px;\n  line-height: 40px;\n  text-decoration: none;\n  text-align: center;\n  margin-top: 10px;\n  transition: background 0.3s;\n  cursor: pointer;\n}\ntable.request-headers[data-v-5de00d90] {\n  border-collapse: collapse;\n  border-spacing: 0;\n  color: #333;\n}\ntable.request-headers[data-v-5de00d90] tbody,\ntable.request-headers[data-v-5de00d90] tr {\n  margin: 0;\n  padding: 0;\n  border: 0;\n  font-size: 100%;\n  font: inherit;\n  vertical-align: baseline;\n}\ntable.request-headers[data-v-5de00d90] td {\n  font-size: 12px;\n  padding: 5px 10px;\n  word-break: break-all;\n}\ntable.request-headers[data-v-5de00d90] td.header-key {\n  white-space: nowrap;\n  font-weight: 700;\n  color: #1a73e8;\n}\n";
  styleInject(css_248z$4);

  script$4.render = render$4;
  script$4.__scopeId = "data-v-5de00d90";
  script$4.__file = "src/plugins/network/request-detail.vue";

  var script$3 = {
    components: {
      
    },
    props: {
      requestItemData: {
        type: Object,
        default: function () {
          return {}
        }
      }
    },
    data () {
      return {
        request: this.requestItemData
      }
    },
    computed: {
      
    },
    methods: {
      showDetail(id){
        this.$parent.$parent.showDetail(id);
      }
    }
  };

  const _hoisted_1$3 = { class: "request-url" };
  const _hoisted_2$2 = { class: "request-status" };
  const _hoisted_3$2 = { class: "request-time" };

  function render$3(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", {
      class: "request-item",
      onClick: _cache[1] || (_cache[1] = $event => ($options.showDetail($data.request.id)))
    }, [
      vue.createVNode("span", _hoisted_1$3, vue.toDisplayString($data.request.url), 1 /* TEXT */),
      vue.createVNode("span", _hoisted_2$2, vue.toDisplayString($data.request.status), 1 /* TEXT */),
      vue.createCommentVNode(" <span class=\"request-method\">{{request.method}}</span>\n    <span class=\"request-type\">{{request.type}}</span>\n    <span class=\"request-size\">{{request.size}}</span> "),
      vue.createVNode("span", _hoisted_3$2, vue.toDisplayString($data.request.time) + "ms", 1 /* TEXT */)
    ]))
  }

  var css_248z$3 = ".request-item {\n  text-align: left;\n  font-size: 13px;\n  display: flex;\n  width: 100%;\n  cursor: pointer;\n  border-bottom: 1px solid #ccc;\n  height: 41px;\n  color: #333;\n  white-space: nowrap;\n}\n.request-item:first-child {\n  border-top: none;\n}\n.request-url {\n  flex: 1;\n}\n.request-status {\n  width: 40px;\n}\n.request-method {\n  width: 50px;\n}\n.request-type {\n  width: 50px;\n}\n.request-size {\n  width: 70px;\n}\n.request-time {\n  width: 60px;\n  padding-right: 10px;\n}\n.request-item span {\n  display: block;\n  line-height: 40px;\n  height: 40px;\n  padding: 0 5px;\n  font-size: 12px;\n  text-align: center;\n  text-overflow: ellipsis;\n  overflow: hidden;\n}\n";
  styleInject(css_248z$3);

  script$3.render = render$3;
  script$3.__file = "src/plugins/network/request-item.vue";

  var script$2 = {
    components: {
      RequestItem: script$3
    },
    props: {
      reqList: {
        type: Array,
        default: [],
      },
    },
    data() {
      return {};
    }
  };

  const _withId$1 = /*#__PURE__*/vue.withScopeId("data-v-16e3716a");

  vue.pushScopeId("data-v-16e3716a");
  const _hoisted_1$2 = { class: "request-container" };
  vue.popScopeId();

  const render$2 = /*#__PURE__*/_withId$1((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_request_item = vue.resolveComponent("request-item");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$2, [
      (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.reqList, (request, index) => {
        return (vue.openBlock(), vue.createBlock(_component_request_item, {
          key: index,
          requestItemData: request
        }, null, 8 /* PROPS */, ["requestItemData"]))
      }), 128 /* KEYED_FRAGMENT */))
    ]))
  });

  var css_248z$2 = ".request-container[data-v-16e3716a] {\n  overflow-y: auto;\n  -webkit-overflow-scrolling: touch;\n  height: 100%;\n  margin-bottom: 10px;\n}\n";
  styleInject(css_248z$2);

  script$2.render = render$2;
  script$2.__scopeId = "data-v-16e3716a";
  script$2.__file = "src/plugins/network/request-container.vue";

  var script$1 = {
    components: {
      RequestContainer: script$2,
      RequestDetail: script$4
    },
    props: {
      
    },
    computed: {
     
    },
    data() {
      return {
        detailShowed: false,
        reqList: this.$store.state.reqList || [],
        curReq: {}
        // reqMap: this.$store.state.reqMap,
        // curReq: this.$store.state.reqMap.get(this.$store.state.reqList[0].id)
      };
    },
    methods: {
      showDetail(id){
        this.curReq = this.reqList[id];
        this.detailShowed = true;
        console.log("现在的reqid: "+id);
      },
      hideDetail(){
        this.detailShowed = false;
      }
    }
  };

  const _withId = /*#__PURE__*/vue.withScopeId("data-v-1a06d26e");

  vue.pushScopeId("data-v-1a06d26e");
  const _hoisted_1$1 = { class: "network-container" };
  const _hoisted_2$1 = /*#__PURE__*/vue.createVNode("div", { class: "network-title" }, [
    /*#__PURE__*/vue.createTextVNode(" 请求捕获 "),
    /*#__PURE__*/vue.createCommentVNode(" <div {{{class 'btn clear-request'}}}>\n        <span {{{class 'icon-clear'}}}></span>\n      </div> "),
    /*#__PURE__*/vue.createCommentVNode(" <img class=\"item-icon-image\" :src=\"defaultIcon\"/> ")
  ], -1 /* HOISTED */);
  const _hoisted_3$1 = { class: "network-info" };
  const _hoisted_4 = { key: 0 };
  const _hoisted_5 = { key: 1 };
  vue.popScopeId();

  const render$1 = /*#__PURE__*/_withId((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_request_container = vue.resolveComponent("request-container");
    const _component_request_detail = vue.resolveComponent("request-detail");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$1, [
      _hoisted_2$1,
      vue.createVNode("div", _hoisted_3$1, [
        (!$data.detailShowed)
          ? (vue.openBlock(), vue.createBlock("div", _hoisted_4, [
              vue.createVNode(_component_request_container, { reqList: $data.reqList }, null, 8 /* PROPS */, ["reqList"])
            ]))
          : vue.createCommentVNode("v-if", true),
        ($data.detailShowed)
          ? (vue.openBlock(), vue.createBlock("div", _hoisted_5, [
              vue.createVNode(_component_request_detail, { reqDetail: $data.curReq }, null, 8 /* PROPS */, ["reqDetail"])
            ]))
          : vue.createCommentVNode("v-if", true)
      ])
    ]))
  });

  var css_248z$1 = ".network-container[data-v-1a06d26e] {\n  display: flex;\n  flex-direction: column;\n  height: 100%;\n}\n.network-title[data-v-1a06d26e] {\n  font-size: 14px;\n  position: relative;\n  width: 100%;\n  left: 0;\n  top: 0;\n  background: #f3f3f3;\n  padding: 10px;\n  color: #333;\n  box-sizing: border-box;\n  pointer-events: all;\n  -webkit-user-select: none;\n  -moz-user-select: none;\n  -ms-user-select: none;\n  user-select: none;\n  display: block;\n  -webkit-tap-highlight-color: transparent;\n  -webkit-text-size-adjust: none;\n  border-bottom: 1px solid #ccc;\n}\n.network-info[data-v-1a06d26e] {\n  display: flex;\n  flex-direction: column;\n  overflow: hidden;\n  flex: 1;\n  background-color: #ffffff;\n  overflow-y: scroll;\n}\n";
  styleInject(css_248z$1);

  script$1.render = render$1;
  script$1.__scopeId = "data-v-1a06d26e";
  script$1.__file = "src/plugins/network/network-container.vue";

  // export const XHR_NETWORK_METHODS = ["open", "send", 'setRequestHeader']
  const mockData = function(callback) {
    const request = {
      name: "qqq",
      url: "https://www.modeben.com/free/week?appid=68852321&appsecret=BgGLDVc7",
      status: '200',
      type: 'plain',
      subType: '',
      size: 2,
      data: {},
      method: 'GET',
      startTime: 0,
      time: 139,
      resTxt: {
          "cityid": "101010100",
          "city": "\u5317\u4eac",
          "update_time": "2021-05-28 20:29:16",
          "data": [{
              "date": "2021-05-28",
              "wea": "\u9634\u8f6c\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "28",
              "tem_night": "16",
              "win": "\u5317\u98ce",
              "win_speed": "3-4\u7ea7"
          }, {
              "date": "2021-05-29",
              "wea": "\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "27",
              "tem_night": "15",
              "win": "\u4e1c\u5317\u98ce",
              "win_speed": "3-4\u7ea7\u8f6c<3\u7ea7"
          }, {
              "date": "2021-05-30",
              "wea": "\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "27",
              "tem_night": "14",
              "win": "\u4e1c\u5357\u98ce",
              "win_speed": "<3\u7ea7"
          }, {
              "date": "2021-05-31",
              "wea": "\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "26",
              "tem_night": "16",
              "win": "\u4e1c\u5357\u98ce",
              "win_speed": "<3\u7ea7"
          }, {
              "date": "2021-06-01",
              "wea": "\u9634\u8f6c\u96f7\u9635\u96e8",
              "wea_img": "yu",
              "tem_day": "29",
              "tem_night": "17",
              "win": "\u4e1c\u5357\u98ce",
              "win_speed": "3-4\u7ea7"
          }, {
              "date": "2021-06-02",
              "wea": "\u6674\u8f6c\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "28",
              "tem_night": "18",
              "win": "\u897f\u5357\u98ce",
              "win_speed": "3-4\u7ea7"
          }, {
              "date": "2021-06-03",
              "wea": "\u591a\u4e91\u8f6c\u6674",
              "wea_img": "yun",
              "tem_day": "29",
              "tem_night": "19",
              "win": "\u5317\u98ce",
              "win_speed": "4-5\u7ea7\u8f6c3-4\u7ea7"
          }]
      },
      done: false,
      reqHeaders: {
          
      },
      resHeaders: {
          "Content-Type": "application/json",
          "Content-Type1": "application/json",
          "Content-Type2": "application/json",
      },
      hasErr: false,
      response: {},
      headers: {},
      displayTime: 1,
    };

    callback({
      value: request
    });
  };

  /*
   * @Author: yanghui 
   * @Date: 2021-05-28 20:53:35 
   * @Last Modified by: yanghui
   * @Last Modified time: 2021-05-29 17:17:10
   */

  var Network = new RouterPlugin({
    nameZh: '请求捕获',
    name: 'network',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: script$1,
    onLoad(){
      //获取拦截到的请求列表，存到state中
      mockData(({value}) => {
        let state = getGlobalData();
        state.reqList = state.reqList || [];
        value.id = state.reqList.length;
        state.reqList.push(value);
        let value2 = {
          name: "collect",
          url: "https://www.tianqiapi.com/free/week?appid=68852321&appsecret=BgGLDVc7",
          status: '200',
          type: 'plain',
          subType: '',
          size: 2,
          data: {},
          method: 'GET',
          startTime: 0,
          time: 139,
          resTxt: '',
          done: false,
          reqHeaders: {},
          resHeaders: {},
          hasErr: false,
          response: {},
          headers: {},
          displayTime: 1,
        };
        value2.id = state.reqList.length;
        state.reqList.push(value2);
      });
    },
    onUnload(){
      // restoreNetwork()
    }
  });

  var script = {
    
  };

  const _hoisted_1 = { class: "hello-world" };
  const _hoisted_2 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, "Hello Dokit", -1 /* HOISTED */);
  const _hoisted_3 = /*#__PURE__*/vue.createVNode("div", null, "Demo Plugin", -1 /* HOISTED */);

  function render(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1, [
      _hoisted_2,
      _hoisted_3
    ]))
  }

  var css_248z = "\n.hello-world{\n  padding:10px;\n  text-align: center;\n}\n";
  styleInject(css_248z);

  script.render = render;
  script.__file = "src/components/ToolHelloWorld.vue";

  const BasicFeatures = {
    title: '常用工具',
    list: [Console, AppInfo, DemoPlugin, DemoSinglePlugin, H5DoorPlugin, Network]
  };

  const DokitFeatures = {
    title: '平台功能',
    list: [{
      nameZh: 'Mock数据',
      name: 'mock',
      icon: 'https://pt-starimg.didistatic.com/static/starimg/img/aDn77poRDB1618997545078.png',
      component: script
    }]
  };

  const UIFeatures = {
    title: '视觉功能',
    list: [{
      nameZh: '取色器',
      name: 'color-selector',
      icon: 'https://pt-starimg.didistatic.com/static/starimg/img/QYUvEE8FnN1618997536890.png',
      component: script
    }, {
      nameZh: '对齐标尺',
      name: 'align-ruler',
      icon: 'https://pt-starimg.didistatic.com/static/starimg/img/a5UTjMn6lO1618997535798.png',
      component: script
    }, {
      nameZh: 'UI结构',
      name: 'view-selector',
      icon: 'https://pt-starimg.didistatic.com/static/starimg/img/XNViIWzG7N1618997548483.png',
      component: script
    }]
  };
  const Features = [BasicFeatures, DokitFeatures, UIFeatures];

  /*
  * TODO 全局注册 Dokit
  */
  window.Dokit = new Dokit({
    features: Features,
  });

}(Vue));
