(function (vue) {
  'use strict';

  /**
   * 拖拽指令 v-dragable
   * 减少外部依赖
  */
  const INIT_VALUE = 9999;
  const DEFAULT_ZINDEX = 99;
  const DEFAULT_OPACITY = 0.5;
  const SAFE_BOTTOM = 50; // 底部防误触
  // TODO 拖拽事件兼容 Pc处理
  // TODO 默认初始位置为右下角
  var dragable = {
    mounted (el) {
      // 初始化变量
      el.dokitEntryLastX = INIT_VALUE;
      el.dokitEntryLastY = INIT_VALUE;
      el.dokitPositionTop = localStorage.getItem('dokitPositionTop') ? parseInt(localStorage.getItem('dokitPositionTop')) : 0;
      el.dokitPositionLeft = localStorage.getItem('dokitPositionLeft') ? parseInt(localStorage.getItem('dokitPositionLeft')) : 0;
      // 初始化样式
      el.style.position = 'fixed';
      el.style.opacity = DEFAULT_OPACITY;
      el.style.top = `${el.dokitPositionTop}px`;
      el.style.left = `${el.dokitPositionLeft}px`;
      el.style.zIndex = DEFAULT_ZINDEX;

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

        el.style.top = `${el.dokitPositionTop}px`;
        el.style.left = `${el.dokitPositionLeft}px`;
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

  const IconBack = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAzCAMAAADIDVqJAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAXFQTFRFAAAANX7GNHzFM3zFM3zFNn3ENYDHNHzFM3zENIDHNH7INH3ENX3GNn7GM33ENYDFNYDGM4DINH7HM3zENH3HNHzENYDGM37FM33FNH3FNn3FNX7HN4DINHzEM33HNH7FNIDGNn7GM33ENX3FNXzHNoDENH7FNH3EM3zEN33INHzFNH3FM4DHNX3HNH3GNX7ENHzENHzFNYDFNH3FM33ENn3JM33GN4DINX7EM3zEM33GNH7FM33FM33FNIDFNH3GNX3KM37HNHzFN4DINX7FNH3EOHzHNH3GM3zFM4DGM33EM3zENHzENH3EM33FOoTFgID/NHzFOIDHM3zEM3zFNoPJM33FM33ENH3FNHzENYDL////NHzENH3FM4PFM3zFOYDGNH3FM3zFN3zINHzEM33FNoDJM3zFM33FNH3ENHzFM37FNH3ENH3FM33ENH7GNX7FM33ENH3FNH7FM37ENH7FM3zFNH3EM4jMM3zE////TdHL6gAAAHl0Uk5TAEPy+vA9RPb0QEX3P0fzPkg8Sfg7SjpL+fE5TThON082Ue81UjRT++4zVO0yVjFX/OwwWOsvWi5b6i1d/eksXitf6Cph5yli/ihk5ieh3x8CoiCg4CGf4Z3iIgGc4yOaJJnkJZjlJpaVk5KQj42LioiHhYSCgHOyD3AmCqsAAAABYktHRFt0vJU0AAAACXBIWXMAAABIAAAASABGyWs+AAABPUlEQVQ4y43UxVoCUBQE4Ctgd3cXKAYoKqioINiBrdjdXby9C+6c3ZzPWf+rE2MMS4bDmY4rk5qs7FQ6ObnU5OXDFFBTCFNUTE2JmFJqysqtcVRQUymmiprqGmtq66ipF9NATSOMq4maZidMCzWtMG3t1HSI6aSmq9sat4eaHpheLzV9YvqpGRi0xuenZghmOEDNiJhRasZggiFqxiesmQxTMyVmmpoZmEiUmlmYWJyauXmYBWoWYZaWqTEr1qRW1zhaT4jaUNQm1Na2onagdjW1B7V/wFXyEOroWFEnok4VdQZ1fqGoS6ira0XdpP4x+uStKL5EY+6gYpq6h4rEFfUgKqqoRyjlkI15ggqGFfUsKqSoFyjlTY15hfIFFPUmyq+odyilhIz5gHJ7FfUpyqOoLyilYo35/rFJ/Jo/DZ3bT7fEcIgAAAAldEVYdGRhdGU6Y3JlYXRlADIwMjEtMDQtMjFUMTc6MzI6MjgrMDg6MDBBnT5hAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDIxLTA0LTIxVDE3OjMyOjI4KzA4OjAwMMCG3QAAAABJRU5ErkJggg==';

  const DefaultItemIcon = 'https://pt-starimg.didistatic.com/static/starimg/img/FHqpI3InaS1618997548865.png';

  const dokitIcon = 'https://pt-starimg.didistatic.com/static/starimg/img/eM7MJKDqVG1618998466986.png';

  var script$5$1 = {
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

  const _withId$5$1 = /*#__PURE__*/vue.withScopeId("data-v-29d70086");

  vue.pushScopeId("data-v-29d70086");
  const _hoisted_1$5$1 = { class: "bar" };
  const _hoisted_2$3$1 = /*#__PURE__*/vue.createVNode("span", { class: "bar-back-btn" }, "返回", -1 /* HOISTED */);
  const _hoisted_3$2$1 = { class: "bar-title" };
  const _hoisted_4$1$1 = { class: "bar-title-text" };
  vue.popScopeId();

  const render$5$1 = /*#__PURE__*/_withId$5$1((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$5$1, [
      vue.withDirectives(vue.createVNode("div", {
        class: "bar-back",
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.handleBackRoute && $options.handleBackRoute(...args)))
      }, [
        vue.createVNode("img", {
          class: "bar-back-icon",
          src: $data.icon
        }, null, 8 /* PROPS */, ["src"]),
        _hoisted_2$3$1
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

  var css_248z$5$1 = ".bar[data-v-29d70086] {\n  background-color: white;\n  height: 50px;\n  width: 100%;\n  display: flex;\n  justify-content: center;\n  align-items: center;\n  padding: 0 10px;\n  box-sizing: border-box;\n  position: relative;\n  border-radius: 10px 10px 0 0;\n}\n.bar-back[data-v-29d70086] {\n  position: absolute;\n  left: 10px;\n  display: flex;\n  flex-direction: row;\n  align-items: center;\n}\n.bar-back-icon[data-v-29d70086] {\n  display: inline-block;\n  height: 18px;\n}\n.bar-back-btn[data-v-29d70086] {\n  color: #337CC4;\n  font-size: 16px;\n  margin-left: 5px;\n}\n.bar-title-text[data-v-29d70086] {\n  color: #333333;\n  font-size: 20px;\n}\n.bar-other-text[data-v-29d70086] {\n  color: #666666;\n  font-size: 16px;\n}\n";
  styleInject$1(css_248z$5$1);

  script$5$1.render = render$5$1;
  script$5$1.__scopeId = "data-v-29d70086";
  script$5$1.__file = "src/common/components/top-bar.vue";

  var script$4$1 = {
    components: {
     TopBar: script$5$1 
    },
    data(){
      return {}
    },
    computed:{
      curRoute(){
        return this.$router.currentRoute.value
      },
      title(){
        return this.curRoute.meta.title || 'Dokit'
      },
      canBack(){
        return this.curRoute.name !== 'index'
      }
    },
    created(){
    }
  };

  const _withId$4$1 = /*#__PURE__*/vue.withScopeId("data-v-19d8ec21");

  vue.pushScopeId("data-v-19d8ec21");
  const _hoisted_1$4$1 = { class: "container" };
  const _hoisted_2$2$1 = { class: "router-container" };
  vue.popScopeId();

  const render$4$1 = /*#__PURE__*/_withId$4$1((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_top_bar = vue.resolveComponent("top-bar");
    const _component_router_view = vue.resolveComponent("router-view");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$4$1, [
      vue.createVNode(_component_top_bar, {
        title: $options.title,
        canBack: $options.canBack
      }, null, 8 /* PROPS */, ["title", "canBack"]),
      vue.createVNode("div", _hoisted_2$2$1, [
        vue.createVNode(_component_router_view, null, {
          default: _withId$4$1(({ Component }) => [
            (vue.openBlock(), vue.createBlock(vue.KeepAlive, null, [
              (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(Component)))
            ], 1024 /* DYNAMIC_SLOTS */))
          ]),
          _: 1 /* STABLE */
        })
      ])
    ]))
  });

  var css_248z$4$1 = ".container[data-v-19d8ec21] {\n  position: absolute;\n  left: 0;\n  right: 0;\n  top: 100px;\n  bottom: 0;\n  background-color: #f5f6f7;\n  display: flex;\n  flex-direction: column;\n  z-index: 99;\n  border-radius: 10px 10px 0 0;\n}\n.router-container[data-v-19d8ec21] {\n  margin-top: 5px;\n  background-color: white;\n  flex: 1;\n  overflow-y: scroll;\n}\n";
  styleInject$1(css_248z$4$1);

  script$4$1.render = render$4$1;
  script$4$1.__scopeId = "data-v-19d8ec21";
  script$4$1.__file = "src/components/container.vue";

  var script$3$1 = {
    components: {
      RouterContainer: script$4$1
    },
    directives: {
      dragable,
    },
    data() {
      return {
        showContainer: false,
      };
    },
    methods: {
      toggleContainer() {
        this.showContainer = !this.showContainer;
      },
    },
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
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.toggleContainer && $options.toggleContainer(...args)))
      }, null, 512 /* NEED_PATCH */), [
        [_directive_dragable]
      ]),
      vue.withDirectives(vue.createVNode("div", {
        class: "mask",
        onClick: _cache[2] || (_cache[2] = (...args) => ($options.toggleContainer && $options.toggleContainer(...args)))
      }, null, 512 /* NEED_PATCH */), [
        [vue.vShow, $data.showContainer]
      ]),
      vue.withDirectives(vue.createVNode(_component_router_container, null, null, 512 /* NEED_PATCH */), [
        [vue.vShow, $data.showContainer]
      ])
    ]))
  });

  var css_248z$3$1 = ".dokit-app[data-v-6c0a0fc1] {\n  font-family: Helvetica Neue, Helvetica, Arial, sans-serif;\n}\n.dokit-entry-btn[data-v-6c0a0fc1] {\n  width: 50px;\n  height: 50px;\n  padding: 10px;\n  box-sizing: border-box;\n  background-image: url(//pt-starimg.didistatic.com/static/starimg/img/OzaetKDzHr1618905183992.png);\n  background-size: 50px;\n  background-position: center;\n  background-repeat: no-repeat;\n}\n.mask[data-v-6c0a0fc1] {\n  position: absolute;\n  top: 0;\n  left: 0;\n  right: 0;\n  bottom: 0;\n  z-index: 3;\n  background-color: #333333;\n  opacity: 0.3;\n}\n";
  styleInject$1(css_248z$3$1);

  script$3$1.render = render$3$1;
  script$3$1.__scopeId = "data-v-6c0a0fc1";
  script$3$1.__file = "src/components/app.vue";

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
      features: []
    }
  });

  // 获取当前 Store 数据的状态
  function getGlobalData(){
    return store.state
  }

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

  /*!
    * vue-router v4.0.6
    * (c) 2021 Eduardo San Martin Morote
    * @license MIT
    */

  const hasSymbol = typeof Symbol === 'function' && typeof Symbol.toStringTag === 'symbol';
  const PolySymbol = (name) => 
  // vr = vue router
  hasSymbol
      ? Symbol('[vue-router]: ' + name )
      : ('[vue-router]: ' ) + name;
  // rvlm = Router View Location Matched
  /**
   * RouteRecord being rendered by the closest ancestor Router View. Used for
   * `onBeforeRouteUpdate` and `onBeforeRouteLeave`. rvlm stands for Router View
   * Location Matched
   *
   * @internal
   */
  const matchedRouteKey = /*#__PURE__*/ PolySymbol('router view location matched' );
  /**
   * Allows overriding the router view depth to control which component in
   * `matched` is rendered. rvd stands for Router View Depth
   *
   * @internal
   */
  const viewDepthKey = /*#__PURE__*/ PolySymbol('router view depth' );
  /**
   * Allows overriding the router instance returned by `useRouter` in tests. r
   * stands for router
   *
   * @internal
   */
  const routerKey = /*#__PURE__*/ PolySymbol('router' );
  /**
   * Allows overriding the current route returned by `useRoute` in tests. rl
   * stands for route location
   *
   * @internal
   */
  const routeLocationKey = /*#__PURE__*/ PolySymbol('route location' );
  /**
   * Allows overriding the current route used by router-view. Internally this is
   * used when the `route` prop is passed.
   *
   * @internal
   */
  const routerViewLocationKey = /*#__PURE__*/ PolySymbol('router view location' );

  const isBrowser = typeof window !== 'undefined';

  function isESModule(obj) {
      return obj.__esModule || (hasSymbol && obj[Symbol.toStringTag] === 'Module');
  }
  const assign = Object.assign;
  function applyToParams(fn, params) {
      const newParams = {};
      for (const key in params) {
          const value = params[key];
          newParams[key] = Array.isArray(value) ? value.map(fn) : fn(value);
      }
      return newParams;
  }
  let noop$1 = () => { };

  function warn(msg) {
      // avoid using ...args as it breaks in older Edge builds
      const args = Array.from(arguments).slice(1);
      console.warn.apply(console, ['[Vue Router warn]: ' + msg].concat(args));
  }
  /**
   * Transforms an URI into a normalized history location
   *
   * @param parseQuery
   * @param location - URI to normalize
   * @param currentLocation - current absolute location. Allows resolving relative
   * paths. Must start with `/`. Defaults to `/`
   * @returns a normalized history location
   */
  function parseURL(parseQuery, location, currentLocation = '/') {
      let path, query = {}, searchString = '', hash = '';
      // Could use URL and URLSearchParams but IE 11 doesn't support it
      const searchPos = location.indexOf('?');
      const hashPos = location.indexOf('#', searchPos > -1 ? searchPos : 0);
      if (searchPos > -1) {
          path = location.slice(0, searchPos);
          searchString = location.slice(searchPos + 1, hashPos > -1 ? hashPos : location.length);
          query = parseQuery(searchString);
      }
      if (hashPos > -1) {
          path = path || location.slice(0, hashPos);
          // keep the # character
          hash = location.slice(hashPos, location.length);
      }
      // no search and no query
      path = resolveRelativePath(path != null ? path : location, currentLocation);
      // empty path means a relative query or hash `?foo=f`, `#thing`
      return {
          fullPath: path + (searchString && '?') + searchString + hash,
          path,
          query,
          hash,
      };
  }
  /**
   * Stringifies a URL object
   *
   * @param stringifyQuery
   * @param location
   */
  function stringifyURL(stringifyQuery, location) {
      let query = location.query ? stringifyQuery(location.query) : '';
      return location.path + (query && '?') + query + (location.hash || '');
  }
  /**
   * Checks if two RouteLocation are equal. This means that both locations are
   * pointing towards the same {@link RouteRecord} and that all `params`, `query`
   * parameters and `hash` are the same
   *
   * @param a - first {@link RouteLocation}
   * @param b - second {@link RouteLocation}
   */
  function isSameRouteLocation(stringifyQuery, a, b) {
      let aLastIndex = a.matched.length - 1;
      let bLastIndex = b.matched.length - 1;
      return (aLastIndex > -1 &&
          aLastIndex === bLastIndex &&
          isSameRouteRecord(a.matched[aLastIndex], b.matched[bLastIndex]) &&
          isSameRouteLocationParams(a.params, b.params) &&
          stringifyQuery(a.query) === stringifyQuery(b.query) &&
          a.hash === b.hash);
  }
  /**
   * Check if two `RouteRecords` are equal. Takes into account aliases: they are
   * considered equal to the `RouteRecord` they are aliasing.
   *
   * @param a - first {@link RouteRecord}
   * @param b - second {@link RouteRecord}
   */
  function isSameRouteRecord(a, b) {
      // since the original record has an undefined value for aliasOf
      // but all aliases point to the original record, this will always compare
      // the original record
      return (a.aliasOf || a) === (b.aliasOf || b);
  }
  function isSameRouteLocationParams(a, b) {
      if (Object.keys(a).length !== Object.keys(b).length)
          return false;
      for (let key in a) {
          if (!isSameRouteLocationParamsValue(a[key], b[key]))
              return false;
      }
      return true;
  }
  function isSameRouteLocationParamsValue(a, b) {
      return Array.isArray(a)
          ? isEquivalentArray(a, b)
          : Array.isArray(b)
              ? isEquivalentArray(b, a)
              : a === b;
  }
  /**
   * Check if two arrays are the same or if an array with one single entry is the
   * same as another primitive value. Used to check query and parameters
   *
   * @param a - array of values
   * @param b - array of values or a single value
   */
  function isEquivalentArray(a, b) {
      return Array.isArray(b)
          ? a.length === b.length && a.every((value, i) => value === b[i])
          : a.length === 1 && a[0] === b;
  }
  /**
   * Resolves a relative path that starts with `.`.
   *
   * @param to - path location we are resolving
   * @param from - currentLocation.path, should start with `/`
   */
  function resolveRelativePath(to, from) {
      if (to.startsWith('/'))
          return to;
      if (!from.startsWith('/')) {
          warn(`Cannot resolve a relative location without an absolute path. Trying to resolve "${to}" from "${from}". It should look like "/${from}".`);
          return to;
      }
      if (!to)
          return from;
      const fromSegments = from.split('/');
      const toSegments = to.split('/');
      let position = fromSegments.length - 1;
      let toPosition;
      let segment;
      for (toPosition = 0; toPosition < toSegments.length; toPosition++) {
          segment = toSegments[toPosition];
          // can't go below zero
          if (position === 1 || segment === '.')
              continue;
          if (segment === '..')
              position--;
          // found something that is not relative path
          else
              break;
      }
      return (fromSegments.slice(0, position).join('/') +
          '/' +
          toSegments
              .slice(toPosition - (toPosition === toSegments.length ? 1 : 0))
              .join('/'));
  }

  var NavigationType;
  (function (NavigationType) {
      NavigationType["pop"] = "pop";
      NavigationType["push"] = "push";
  })(NavigationType || (NavigationType = {}));
  var NavigationDirection;
  (function (NavigationDirection) {
      NavigationDirection["back"] = "back";
      NavigationDirection["forward"] = "forward";
      NavigationDirection["unknown"] = "";
  })(NavigationDirection || (NavigationDirection = {}));
  /**
   * Starting location for Histories
   */
  const START = '';
  // remove any character before the hash
  const BEFORE_HASH_RE = /^[^#]+#/;
  function createHref(base, location) {
      return base.replace(BEFORE_HASH_RE, '#') + location;
  }

  function getElementPosition(el, offset) {
      const docRect = document.documentElement.getBoundingClientRect();
      const elRect = el.getBoundingClientRect();
      return {
          behavior: offset.behavior,
          left: elRect.left - docRect.left - (offset.left || 0),
          top: elRect.top - docRect.top - (offset.top || 0),
      };
  }
  const computeScrollPosition = () => ({
      left: window.pageXOffset,
      top: window.pageYOffset,
  });
  function scrollToPosition(position) {
      let scrollToOptions;
      if ('el' in position) {
          let positionEl = position.el;
          const isIdSelector = typeof positionEl === 'string' && positionEl.startsWith('#');
          /**
           * `id`s can accept pretty much any characters, including CSS combinators
           * like `>` or `~`. It's still possible to retrieve elements using
           * `document.getElementById('~')` but it needs to be escaped when using
           * `document.querySelector('#\\~')` for it to be valid. The only
           * requirements for `id`s are them to be unique on the page and to not be
           * empty (`id=""`). Because of that, when passing an id selector, it should
           * be properly escaped for it to work with `querySelector`. We could check
           * for the id selector to be simple (no CSS combinators `+ >~`) but that
           * would make things inconsistent since they are valid characters for an
           * `id` but would need to be escaped when using `querySelector`, breaking
           * their usage and ending up in no selector returned. Selectors need to be
           * escaped:
           *
           * - `#1-thing` becomes `#\31 -thing`
           * - `#with~symbols` becomes `#with\\~symbols`
           *
           * - More information about  the topic can be found at
           *   https://mathiasbynens.be/notes/html5-id-class.
           * - Practical example: https://mathiasbynens.be/demo/html5-id
           */
          if (typeof position.el === 'string') {
              if (!isIdSelector || !document.getElementById(position.el.slice(1))) {
                  try {
                      let foundEl = document.querySelector(position.el);
                      if (isIdSelector && foundEl) {
                          warn(`The selector "${position.el}" should be passed as "el: document.querySelector('${position.el}')" because it starts with "#".`);
                          // return to avoid other warnings
                          return;
                      }
                  }
                  catch (err) {
                      warn(`The selector "${position.el}" is invalid. If you are using an id selector, make sure to escape it. You can find more information about escaping characters in selectors at https://mathiasbynens.be/notes/css-escapes or use CSS.escape (https://developer.mozilla.org/en-US/docs/Web/API/CSS/escape).`);
                      // return to avoid other warnings
                      return;
                  }
              }
          }
          const el = typeof positionEl === 'string'
              ? isIdSelector
                  ? document.getElementById(positionEl.slice(1))
                  : document.querySelector(positionEl)
              : positionEl;
          if (!el) {
              warn(`Couldn't find element using selector "${position.el}" returned by scrollBehavior.`);
              return;
          }
          scrollToOptions = getElementPosition(el, position);
      }
      else {
          scrollToOptions = position;
      }
      if ('scrollBehavior' in document.documentElement.style)
          window.scrollTo(scrollToOptions);
      else {
          window.scrollTo(scrollToOptions.left != null ? scrollToOptions.left : window.pageXOffset, scrollToOptions.top != null ? scrollToOptions.top : window.pageYOffset);
      }
  }
  function getScrollKey(path, delta) {
      const position = history.state ? history.state.position - delta : -1;
      return position + path;
  }
  const scrollPositions = new Map();
  function saveScrollPosition(key, scrollPosition) {
      scrollPositions.set(key, scrollPosition);
  }
  function getSavedScrollPosition(key) {
      const scroll = scrollPositions.get(key);
      // consume it so it's not used again
      scrollPositions.delete(key);
      return scroll;
  }

  /**
   * Creates a in-memory based history. The main purpose of this history is to handle SSR. It starts in a special location that is nowhere.
   * It's up to the user to replace that location with the starter location by either calling `router.push` or `router.replace`.
   *
   * @param base - Base applied to all urls, defaults to '/'
   * @returns a history object that can be passed to the router constructor
   */
  function createMemoryHistory(base = '') {
      let listeners = [];
      let queue = [START];
      let position = 0;
      function setLocation(location) {
          position++;
          if (position === queue.length) {
              // we are at the end, we can simply append a new entry
              queue.push(location);
          }
          else {
              // we are in the middle, we remove everything from here in the queue
              queue.splice(position);
              queue.push(location);
          }
      }
      function triggerListeners(to, from, { direction, delta }) {
          const info = {
              direction,
              delta,
              type: NavigationType.pop,
          };
          for (let callback of listeners) {
              callback(to, from, info);
          }
      }
      const routerHistory = {
          // rewritten by Object.defineProperty
          location: START,
          state: {},
          base,
          createHref: createHref.bind(null, base),
          replace(to) {
              // remove current entry and decrement position
              queue.splice(position--, 1);
              setLocation(to);
          },
          push(to, data) {
              setLocation(to);
          },
          listen(callback) {
              listeners.push(callback);
              return () => {
                  const index = listeners.indexOf(callback);
                  if (index > -1)
                      listeners.splice(index, 1);
              };
          },
          destroy() {
              listeners = [];
          },
          go(delta, shouldTrigger = true) {
              const from = this.location;
              const direction = 
              // we are considering delta === 0 going forward, but in abstract mode
              // using 0 for the delta doesn't make sense like it does in html5 where
              // it reloads the page
              delta < 0 ? NavigationDirection.back : NavigationDirection.forward;
              position = Math.max(0, Math.min(position + delta, queue.length - 1));
              if (shouldTrigger) {
                  triggerListeners(this.location, from, {
                      direction,
                      delta,
                  });
              }
          },
      };
      Object.defineProperty(routerHistory, 'location', {
          get: () => queue[position],
      });
      return routerHistory;
  }

  function isRouteLocation(route) {
      return typeof route === 'string' || (route && typeof route === 'object');
  }
  function isRouteName(name) {
      return typeof name === 'string' || typeof name === 'symbol';
  }

  /**
   * Initial route location where the router is. Can be used in navigation guards
   * to differentiate the initial navigation.
   *
   * @example
   * ```js
   * import { START_LOCATION } from 'vue-router'
   *
   * router.beforeEach((to, from) => {
   *   if (from === START_LOCATION) {
   *     // initial navigation
   *   }
   * })
   * ```
   */
  const START_LOCATION_NORMALIZED = {
      path: '/',
      name: undefined,
      params: {},
      query: {},
      hash: '',
      fullPath: '/',
      matched: [],
      meta: {},
      redirectedFrom: undefined,
  };

  const NavigationFailureSymbol = /*#__PURE__*/ PolySymbol('navigation failure' );
  /**
   * Enumeration with all possible types for navigation failures. Can be passed to
   * {@link isNavigationFailure} to check for specific failures.
   */
  var NavigationFailureType;
  (function (NavigationFailureType) {
      /**
       * An aborted navigation is a navigation that failed because a navigation
       * guard returned `false` or called `next(false)`
       */
      NavigationFailureType[NavigationFailureType["aborted"] = 4] = "aborted";
      /**
       * A cancelled navigation is a navigation that failed because a more recent
       * navigation finished started (not necessarily finished).
       */
      NavigationFailureType[NavigationFailureType["cancelled"] = 8] = "cancelled";
      /**
       * A duplicated navigation is a navigation that failed because it was
       * initiated while already being at the exact same location.
       */
      NavigationFailureType[NavigationFailureType["duplicated"] = 16] = "duplicated";
  })(NavigationFailureType || (NavigationFailureType = {}));
  // DEV only debug messages
  const ErrorTypeMessages = {
      [1 /* MATCHER_NOT_FOUND */]({ location, currentLocation }) {
          return `No match for\n ${JSON.stringify(location)}${currentLocation
            ? '\nwhile being at\n' + JSON.stringify(currentLocation)
            : ''}`;
      },
      [2 /* NAVIGATION_GUARD_REDIRECT */]({ from, to, }) {
          return `Redirected from "${from.fullPath}" to "${stringifyRoute(to)}" via a navigation guard.`;
      },
      [4 /* NAVIGATION_ABORTED */]({ from, to }) {
          return `Navigation aborted from "${from.fullPath}" to "${to.fullPath}" via a navigation guard.`;
      },
      [8 /* NAVIGATION_CANCELLED */]({ from, to }) {
          return `Navigation cancelled from "${from.fullPath}" to "${to.fullPath}" with a new navigation.`;
      },
      [16 /* NAVIGATION_DUPLICATED */]({ from, to }) {
          return `Avoided redundant navigation to current location: "${from.fullPath}".`;
      },
  };
  function createRouterError(type, params) {
      {
          return assign(new Error(ErrorTypeMessages[type](params)), {
              type,
              [NavigationFailureSymbol]: true,
          }, params);
      }
  }
  function isNavigationFailure(error, type) {
      return (error instanceof Error &&
          NavigationFailureSymbol in error &&
          (type == null || !!(error.type & type)));
  }
  const propertiesToLog = ['params', 'query', 'hash'];
  function stringifyRoute(to) {
      if (typeof to === 'string')
          return to;
      if ('path' in to)
          return to.path;
      const location = {};
      for (const key of propertiesToLog) {
          if (key in to)
              location[key] = to[key];
      }
      return JSON.stringify(location, null, 2);
  }

  // default pattern for a param: non greedy everything but /
  const BASE_PARAM_PATTERN = '[^/]+?';
  const BASE_PATH_PARSER_OPTIONS = {
      sensitive: false,
      strict: false,
      start: true,
      end: true,
  };
  // Special Regex characters that must be escaped in static tokens
  const REGEX_CHARS_RE = /[.+*?^${}()[\]/\\]/g;
  /**
   * Creates a path parser from an array of Segments (a segment is an array of Tokens)
   *
   * @param segments - array of segments returned by tokenizePath
   * @param extraOptions - optional options for the regexp
   * @returns a PathParser
   */
  function tokensToParser(segments, extraOptions) {
      const options = assign({}, BASE_PATH_PARSER_OPTIONS, extraOptions);
      // the amount of scores is the same as the length of segments except for the root segment "/"
      let score = [];
      // the regexp as a string
      let pattern = options.start ? '^' : '';
      // extracted keys
      const keys = [];
      for (const segment of segments) {
          // the root segment needs special treatment
          const segmentScores = segment.length ? [] : [90 /* Root */];
          // allow trailing slash
          if (options.strict && !segment.length)
              pattern += '/';
          for (let tokenIndex = 0; tokenIndex < segment.length; tokenIndex++) {
              const token = segment[tokenIndex];
              // resets the score if we are inside a sub segment /:a-other-:b
              let subSegmentScore = 40 /* Segment */ +
                  (options.sensitive ? 0.25 /* BonusCaseSensitive */ : 0);
              if (token.type === 0 /* Static */) {
                  // prepend the slash if we are starting a new segment
                  if (!tokenIndex)
                      pattern += '/';
                  pattern += token.value.replace(REGEX_CHARS_RE, '\\$&');
                  subSegmentScore += 40 /* Static */;
              }
              else if (token.type === 1 /* Param */) {
                  const { value, repeatable, optional, regexp } = token;
                  keys.push({
                      name: value,
                      repeatable,
                      optional,
                  });
                  const re = regexp ? regexp : BASE_PARAM_PATTERN;
                  // the user provided a custom regexp /:id(\\d+)
                  if (re !== BASE_PARAM_PATTERN) {
                      subSegmentScore += 10 /* BonusCustomRegExp */;
                      // make sure the regexp is valid before using it
                      try {
                          new RegExp(`(${re})`);
                      }
                      catch (err) {
                          throw new Error(`Invalid custom RegExp for param "${value}" (${re}): ` +
                              err.message);
                      }
                  }
                  // when we repeat we must take care of the repeating leading slash
                  let subPattern = repeatable ? `((?:${re})(?:/(?:${re}))*)` : `(${re})`;
                  // prepend the slash if we are starting a new segment
                  if (!tokenIndex)
                      subPattern =
                          // avoid an optional / if there are more segments e.g. /:p?-static
                          // or /:p?-:p2
                          optional && segment.length < 2
                              ? `(?:/${subPattern})`
                              : '/' + subPattern;
                  if (optional)
                      subPattern += '?';
                  pattern += subPattern;
                  subSegmentScore += 20 /* Dynamic */;
                  if (optional)
                      subSegmentScore += -8 /* BonusOptional */;
                  if (repeatable)
                      subSegmentScore += -20 /* BonusRepeatable */;
                  if (re === '.*')
                      subSegmentScore += -50 /* BonusWildcard */;
              }
              segmentScores.push(subSegmentScore);
          }
          // an empty array like /home/ -> [[{home}], []]
          // if (!segment.length) pattern += '/'
          score.push(segmentScores);
      }
      // only apply the strict bonus to the last score
      if (options.strict && options.end) {
          const i = score.length - 1;
          score[i][score[i].length - 1] += 0.7000000000000001 /* BonusStrict */;
      }
      // TODO: dev only warn double trailing slash
      if (!options.strict)
          pattern += '/?';
      if (options.end)
          pattern += '$';
      // allow paths like /dynamic to only match dynamic or dynamic/... but not dynamic_something_else
      else if (options.strict)
          pattern += '(?:/|$)';
      const re = new RegExp(pattern, options.sensitive ? '' : 'i');
      function parse(path) {
          const match = path.match(re);
          const params = {};
          if (!match)
              return null;
          for (let i = 1; i < match.length; i++) {
              const value = match[i] || '';
              const key = keys[i - 1];
              params[key.name] = value && key.repeatable ? value.split('/') : value;
          }
          return params;
      }
      function stringify(params) {
          let path = '';
          // for optional parameters to allow to be empty
          let avoidDuplicatedSlash = false;
          for (const segment of segments) {
              if (!avoidDuplicatedSlash || !path.endsWith('/'))
                  path += '/';
              avoidDuplicatedSlash = false;
              for (const token of segment) {
                  if (token.type === 0 /* Static */) {
                      path += token.value;
                  }
                  else if (token.type === 1 /* Param */) {
                      const { value, repeatable, optional } = token;
                      const param = value in params ? params[value] : '';
                      if (Array.isArray(param) && !repeatable)
                          throw new Error(`Provided param "${value}" is an array but it is not repeatable (* or + modifiers)`);
                      const text = Array.isArray(param) ? param.join('/') : param;
                      if (!text) {
                          if (optional) {
                              // if we have more than one optional param like /:a?-static we
                              // don't need to care about the optional param
                              if (segment.length < 2) {
                                  // remove the last slash as we could be at the end
                                  if (path.endsWith('/'))
                                      path = path.slice(0, -1);
                                  // do not append a slash on the next iteration
                                  else
                                      avoidDuplicatedSlash = true;
                              }
                          }
                          else
                              throw new Error(`Missing required param "${value}"`);
                      }
                      path += text;
                  }
              }
          }
          return path;
      }
      return {
          re,
          score,
          keys,
          parse,
          stringify,
      };
  }
  /**
   * Compares an array of numbers as used in PathParser.score and returns a
   * number. This function can be used to `sort` an array
   * @param a - first array of numbers
   * @param b - second array of numbers
   * @returns 0 if both are equal, < 0 if a should be sorted first, > 0 if b
   * should be sorted first
   */
  function compareScoreArray(a, b) {
      let i = 0;
      while (i < a.length && i < b.length) {
          const diff = b[i] - a[i];
          // only keep going if diff === 0
          if (diff)
              return diff;
          i++;
      }
      // if the last subsegment was Static, the shorter segments should be sorted first
      // otherwise sort the longest segment first
      if (a.length < b.length) {
          return a.length === 1 && a[0] === 40 /* Static */ + 40 /* Segment */
              ? -1
              : 1;
      }
      else if (a.length > b.length) {
          return b.length === 1 && b[0] === 40 /* Static */ + 40 /* Segment */
              ? 1
              : -1;
      }
      return 0;
  }
  /**
   * Compare function that can be used with `sort` to sort an array of PathParser
   * @param a - first PathParser
   * @param b - second PathParser
   * @returns 0 if both are equal, < 0 if a should be sorted first, > 0 if b
   */
  function comparePathParserScore(a, b) {
      let i = 0;
      const aScore = a.score;
      const bScore = b.score;
      while (i < aScore.length && i < bScore.length) {
          const comp = compareScoreArray(aScore[i], bScore[i]);
          // do not return if both are equal
          if (comp)
              return comp;
          i++;
      }
      // if a and b share the same score entries but b has more, sort b first
      return bScore.length - aScore.length;
      // this is the ternary version
      // return aScore.length < bScore.length
      //   ? 1
      //   : aScore.length > bScore.length
      //   ? -1
      //   : 0
  }

  const ROOT_TOKEN = {
      type: 0 /* Static */,
      value: '',
  };
  const VALID_PARAM_RE = /[a-zA-Z0-9_]/;
  // After some profiling, the cache seems to be unnecessary because tokenizePath
  // (the slowest part of adding a route) is very fast
  // const tokenCache = new Map<string, Token[][]>()
  function tokenizePath(path) {
      if (!path)
          return [[]];
      if (path === '/')
          return [[ROOT_TOKEN]];
      if (!path.startsWith('/')) {
          throw new Error(`Route paths should start with a "/": "${path}" should be "/${path}".`
              );
      }
      // if (tokenCache.has(path)) return tokenCache.get(path)!
      function crash(message) {
          throw new Error(`ERR (${state})/"${buffer}": ${message}`);
      }
      let state = 0 /* Static */;
      let previousState = state;
      const tokens = [];
      // the segment will always be valid because we get into the initial state
      // with the leading /
      let segment;
      function finalizeSegment() {
          if (segment)
              tokens.push(segment);
          segment = [];
      }
      // index on the path
      let i = 0;
      // char at index
      let char;
      // buffer of the value read
      let buffer = '';
      // custom regexp for a param
      let customRe = '';
      function consumeBuffer() {
          if (!buffer)
              return;
          if (state === 0 /* Static */) {
              segment.push({
                  type: 0 /* Static */,
                  value: buffer,
              });
          }
          else if (state === 1 /* Param */ ||
              state === 2 /* ParamRegExp */ ||
              state === 3 /* ParamRegExpEnd */) {
              if (segment.length > 1 && (char === '*' || char === '+'))
                  crash(`A repeatable param (${buffer}) must be alone in its segment. eg: '/:ids+.`);
              segment.push({
                  type: 1 /* Param */,
                  value: buffer,
                  regexp: customRe,
                  repeatable: char === '*' || char === '+',
                  optional: char === '*' || char === '?',
              });
          }
          else {
              crash('Invalid state to consume buffer');
          }
          buffer = '';
      }
      function addCharToBuffer() {
          buffer += char;
      }
      while (i < path.length) {
          char = path[i++];
          if (char === '\\' && state !== 2 /* ParamRegExp */) {
              previousState = state;
              state = 4 /* EscapeNext */;
              continue;
          }
          switch (state) {
              case 0 /* Static */:
                  if (char === '/') {
                      if (buffer) {
                          consumeBuffer();
                      }
                      finalizeSegment();
                  }
                  else if (char === ':') {
                      consumeBuffer();
                      state = 1 /* Param */;
                  }
                  else {
                      addCharToBuffer();
                  }
                  break;
              case 4 /* EscapeNext */:
                  addCharToBuffer();
                  state = previousState;
                  break;
              case 1 /* Param */:
                  if (char === '(') {
                      state = 2 /* ParamRegExp */;
                  }
                  else if (VALID_PARAM_RE.test(char)) {
                      addCharToBuffer();
                  }
                  else {
                      consumeBuffer();
                      state = 0 /* Static */;
                      // go back one character if we were not modifying
                      if (char !== '*' && char !== '?' && char !== '+')
                          i--;
                  }
                  break;
              case 2 /* ParamRegExp */:
                  // TODO: is it worth handling nested regexp? like :p(?:prefix_([^/]+)_suffix)
                  // it already works by escaping the closing )
                  // https://paths.esm.dev/?p=AAMeJbiAwQEcDKbAoAAkP60PG2R6QAvgNaA6AFACM2ABuQBB#
                  // is this really something people need since you can also write
                  // /prefix_:p()_suffix
                  if (char === ')') {
                      // handle the escaped )
                      if (customRe[customRe.length - 1] == '\\')
                          customRe = customRe.slice(0, -1) + char;
                      else
                          state = 3 /* ParamRegExpEnd */;
                  }
                  else {
                      customRe += char;
                  }
                  break;
              case 3 /* ParamRegExpEnd */:
                  // same as finalizing a param
                  consumeBuffer();
                  state = 0 /* Static */;
                  // go back one character if we were not modifying
                  if (char !== '*' && char !== '?' && char !== '+')
                      i--;
                  customRe = '';
                  break;
              default:
                  crash('Unknown state');
                  break;
          }
      }
      if (state === 2 /* ParamRegExp */)
          crash(`Unfinished custom RegExp for param "${buffer}"`);
      consumeBuffer();
      finalizeSegment();
      // tokenCache.set(path, tokens)
      return tokens;
  }

  function createRouteRecordMatcher(record, parent, options) {
      const parser = tokensToParser(tokenizePath(record.path), options);
      // warn against params with the same name
      {
          const existingKeys = new Set();
          for (const key of parser.keys) {
              if (existingKeys.has(key.name))
                  warn(`Found duplicated params with name "${key.name}" for path "${record.path}". Only the last one will be available on "$route.params".`);
              existingKeys.add(key.name);
          }
      }
      const matcher = assign(parser, {
          record,
          parent,
          // these needs to be populated by the parent
          children: [],
          alias: [],
      });
      if (parent) {
          // both are aliases or both are not aliases
          // we don't want to mix them because the order is used when
          // passing originalRecord in Matcher.addRoute
          if (!matcher.record.aliasOf === !parent.record.aliasOf)
              parent.children.push(matcher);
      }
      return matcher;
  }

  /**
   * Creates a Router Matcher.
   *
   * @internal
   * @param routes - array of initial routes
   * @param globalOptions - global route options
   */
  function createRouterMatcher(routes, globalOptions) {
      // normalized ordered array of matchers
      const matchers = [];
      const matcherMap = new Map();
      globalOptions = mergeOptions({ strict: false, end: true, sensitive: false }, globalOptions);
      function getRecordMatcher(name) {
          return matcherMap.get(name);
      }
      function addRoute(record, parent, originalRecord) {
          // used later on to remove by name
          let isRootAdd = !originalRecord;
          let mainNormalizedRecord = normalizeRouteRecord(record);
          // we might be the child of an alias
          mainNormalizedRecord.aliasOf = originalRecord && originalRecord.record;
          const options = mergeOptions(globalOptions, record);
          // generate an array of records to correctly handle aliases
          const normalizedRecords = [
              mainNormalizedRecord,
          ];
          if ('alias' in record) {
              const aliases = typeof record.alias === 'string' ? [record.alias] : record.alias;
              for (const alias of aliases) {
                  normalizedRecords.push(assign({}, mainNormalizedRecord, {
                      // this allows us to hold a copy of the `components` option
                      // so that async components cache is hold on the original record
                      components: originalRecord
                          ? originalRecord.record.components
                          : mainNormalizedRecord.components,
                      path: alias,
                      // we might be the child of an alias
                      aliasOf: originalRecord
                          ? originalRecord.record
                          : mainNormalizedRecord,
                      // the aliases are always of the same kind as the original since they
                      // are defined on the same record
                  }));
              }
          }
          let matcher;
          let originalMatcher;
          for (const normalizedRecord of normalizedRecords) {
              let { path } = normalizedRecord;
              // Build up the path for nested routes if the child isn't an absolute
              // route. Only add the / delimiter if the child path isn't empty and if the
              // parent path doesn't have a trailing slash
              if (parent && path[0] !== '/') {
                  let parentPath = parent.record.path;
                  let connectingSlash = parentPath[parentPath.length - 1] === '/' ? '' : '/';
                  normalizedRecord.path =
                      parent.record.path + (path && connectingSlash + path);
              }
              if (normalizedRecord.path === '*') {
                  throw new Error('Catch all routes ("*") must now be defined using a param with a custom regexp.\n' +
                      'See more at https://next.router.vuejs.org/guide/migration/#removed-star-or-catch-all-routes.');
              }
              // create the object before hand so it can be passed to children
              matcher = createRouteRecordMatcher(normalizedRecord, parent, options);
              if (parent && path[0] === '/')
                  checkMissingParamsInAbsolutePath(matcher, parent);
              // if we are an alias we must tell the original record that we exist
              // so we can be removed
              if (originalRecord) {
                  originalRecord.alias.push(matcher);
                  {
                      checkSameParams(originalRecord, matcher);
                  }
              }
              else {
                  // otherwise, the first record is the original and others are aliases
                  originalMatcher = originalMatcher || matcher;
                  if (originalMatcher !== matcher)
                      originalMatcher.alias.push(matcher);
                  // remove the route if named and only for the top record (avoid in nested calls)
                  // this works because the original record is the first one
                  if (isRootAdd && record.name && !isAliasRecord(matcher))
                      removeRoute(record.name);
              }
              if ('children' in mainNormalizedRecord) {
                  let children = mainNormalizedRecord.children;
                  for (let i = 0; i < children.length; i++) {
                      addRoute(children[i], matcher, originalRecord && originalRecord.children[i]);
                  }
              }
              // if there was no original record, then the first one was not an alias and all
              // other alias (if any) need to reference this record when adding children
              originalRecord = originalRecord || matcher;
              // TODO: add normalized records for more flexibility
              // if (parent && isAliasRecord(originalRecord)) {
              //   parent.children.push(originalRecord)
              // }
              insertMatcher(matcher);
          }
          return originalMatcher
              ? () => {
                  // since other matchers are aliases, they should be removed by the original matcher
                  removeRoute(originalMatcher);
              }
              : noop$1;
      }
      function removeRoute(matcherRef) {
          if (isRouteName(matcherRef)) {
              const matcher = matcherMap.get(matcherRef);
              if (matcher) {
                  matcherMap.delete(matcherRef);
                  matchers.splice(matchers.indexOf(matcher), 1);
                  matcher.children.forEach(removeRoute);
                  matcher.alias.forEach(removeRoute);
              }
          }
          else {
              let index = matchers.indexOf(matcherRef);
              if (index > -1) {
                  matchers.splice(index, 1);
                  if (matcherRef.record.name)
                      matcherMap.delete(matcherRef.record.name);
                  matcherRef.children.forEach(removeRoute);
                  matcherRef.alias.forEach(removeRoute);
              }
          }
      }
      function getRoutes() {
          return matchers;
      }
      function insertMatcher(matcher) {
          let i = 0;
          // console.log('i is', { i })
          while (i < matchers.length &&
              comparePathParserScore(matcher, matchers[i]) >= 0)
              i++;
          // console.log('END i is', { i })
          // while (i < matchers.length && matcher.score <= matchers[i].score) i++
          matchers.splice(i, 0, matcher);
          // only add the original record to the name map
          if (matcher.record.name && !isAliasRecord(matcher))
              matcherMap.set(matcher.record.name, matcher);
      }
      function resolve(location, currentLocation) {
          let matcher;
          let params = {};
          let path;
          let name;
          if ('name' in location && location.name) {
              matcher = matcherMap.get(location.name);
              if (!matcher)
                  throw createRouterError(1 /* MATCHER_NOT_FOUND */, {
                      location,
                  });
              name = matcher.record.name;
              params = assign(
              // paramsFromLocation is a new object
              paramsFromLocation(currentLocation.params, 
              // only keep params that exist in the resolved location
              // TODO: only keep optional params coming from a parent record
              matcher.keys.filter(k => !k.optional).map(k => k.name)), location.params);
              // throws if cannot be stringified
              path = matcher.stringify(params);
          }
          else if ('path' in location) {
              // no need to resolve the path with the matcher as it was provided
              // this also allows the user to control the encoding
              path = location.path;
              if (!path.startsWith('/')) {
                  warn(`The Matcher cannot resolve relative paths but received "${path}". Unless you directly called \`matcher.resolve("${path}")\`, this is probably a bug in vue-router. Please open an issue at https://new-issue.vuejs.org/?repo=vuejs/vue-router-next.`);
              }
              matcher = matchers.find(m => m.re.test(path));
              // matcher should have a value after the loop
              if (matcher) {
                  // TODO: dev warning of unused params if provided
                  // we know the matcher works because we tested the regexp
                  params = matcher.parse(path);
                  name = matcher.record.name;
              }
              // location is a relative path
          }
          else {
              // match by name or path of current route
              matcher = currentLocation.name
                  ? matcherMap.get(currentLocation.name)
                  : matchers.find(m => m.re.test(currentLocation.path));
              if (!matcher)
                  throw createRouterError(1 /* MATCHER_NOT_FOUND */, {
                      location,
                      currentLocation,
                  });
              name = matcher.record.name;
              // since we are navigating to the same location, we don't need to pick the
              // params like when `name` is provided
              params = assign({}, currentLocation.params, location.params);
              path = matcher.stringify(params);
          }
          const matched = [];
          let parentMatcher = matcher;
          while (parentMatcher) {
              // reversed order so parents are at the beginning
              matched.unshift(parentMatcher.record);
              parentMatcher = parentMatcher.parent;
          }
          return {
              name,
              path,
              params,
              matched,
              meta: mergeMetaFields(matched),
          };
      }
      // add initial routes
      routes.forEach(route => addRoute(route));
      return { addRoute, resolve, removeRoute, getRoutes, getRecordMatcher };
  }
  function paramsFromLocation(params, keys) {
      let newParams = {};
      for (let key of keys) {
          if (key in params)
              newParams[key] = params[key];
      }
      return newParams;
  }
  /**
   * Normalizes a RouteRecordRaw. Creates a copy
   *
   * @param record
   * @returns the normalized version
   */
  function normalizeRouteRecord(record) {
      return {
          path: record.path,
          redirect: record.redirect,
          name: record.name,
          meta: record.meta || {},
          aliasOf: undefined,
          beforeEnter: record.beforeEnter,
          props: normalizeRecordProps(record),
          children: record.children || [],
          instances: {},
          leaveGuards: new Set(),
          updateGuards: new Set(),
          enterCallbacks: {},
          components: 'components' in record
              ? record.components || {}
              : { default: record.component },
      };
  }
  /**
   * Normalize the optional `props` in a record to always be an object similar to
   * components. Also accept a boolean for components.
   * @param record
   */
  function normalizeRecordProps(record) {
      const propsObject = {};
      // props does not exist on redirect records but we can set false directly
      const props = record.props || false;
      if ('component' in record) {
          propsObject.default = props;
      }
      else {
          // NOTE: we could also allow a function to be applied to every component.
          // Would need user feedback for use cases
          for (let name in record.components)
              propsObject[name] = typeof props === 'boolean' ? props : props[name];
      }
      return propsObject;
  }
  /**
   * Checks if a record or any of its parent is an alias
   * @param record
   */
  function isAliasRecord(record) {
      while (record) {
          if (record.record.aliasOf)
              return true;
          record = record.parent;
      }
      return false;
  }
  /**
   * Merge meta fields of an array of records
   *
   * @param matched - array of matched records
   */
  function mergeMetaFields(matched) {
      return matched.reduce((meta, record) => assign(meta, record.meta), {});
  }
  function mergeOptions(defaults, partialOptions) {
      let options = {};
      for (let key in defaults) {
          options[key] =
              key in partialOptions ? partialOptions[key] : defaults[key];
      }
      return options;
  }
  function isSameParam(a, b) {
      return (a.name === b.name &&
          a.optional === b.optional &&
          a.repeatable === b.repeatable);
  }
  /**
   * Check if a path and its alias have the same required params
   *
   * @param a - original record
   * @param b - alias record
   */
  function checkSameParams(a, b) {
      for (let key of a.keys) {
          if (!key.optional && !b.keys.find(isSameParam.bind(null, key)))
              return warn(`Alias "${b.record.path}" and the original record: "${a.record.path}" should have the exact same param named "${key.name}"`);
      }
      for (let key of b.keys) {
          if (!key.optional && !a.keys.find(isSameParam.bind(null, key)))
              return warn(`Alias "${b.record.path}" and the original record: "${a.record.path}" should have the exact same param named "${key.name}"`);
      }
  }
  function checkMissingParamsInAbsolutePath(record, parent) {
      for (let key of parent.keys) {
          if (!record.keys.find(isSameParam.bind(null, key)))
              return warn(`Absolute path "${record.record.path}" should have the exact same param named "${key.name}" as its parent "${parent.record.path}".`);
      }
  }

  /**
   * Encoding Rules ␣ = Space Path: ␣ " < > # ? { } Query: ␣ " < > # & = Hash: ␣ "
   * < > `
   *
   * On top of that, the RFC3986 (https://tools.ietf.org/html/rfc3986#section-2.2)
   * defines some extra characters to be encoded. Most browsers do not encode them
   * in encodeURI https://github.com/whatwg/url/issues/369, so it may be safer to
   * also encode `!'()*`. Leaving unencoded only ASCII alphanumeric(`a-zA-Z0-9`)
   * plus `-._~`. This extra safety should be applied to query by patching the
   * string returned by encodeURIComponent encodeURI also encodes `[\]^`. `\`
   * should be encoded to avoid ambiguity. Browsers (IE, FF, C) transform a `\`
   * into a `/` if directly typed in. The _backtick_ (`````) should also be
   * encoded everywhere because some browsers like FF encode it when directly
   * written while others don't. Safari and IE don't encode ``"<>{}``` in hash.
   */
  // const EXTRA_RESERVED_RE = /[!'()*]/g
  // const encodeReservedReplacer = (c: string) => '%' + c.charCodeAt(0).toString(16)
  const HASH_RE = /#/g; // %23
  const AMPERSAND_RE = /&/g; // %26
  const SLASH_RE = /\//g; // %2F
  const EQUAL_RE = /=/g; // %3D
  const IM_RE = /\?/g; // %3F
  const PLUS_RE = /\+/g; // %2B
  /**
   * NOTE: It's not clear to me if we should encode the + symbol in queries, it
   * seems to be less flexible than not doing so and I can't find out the legacy
   * systems requiring this for regular requests like text/html. In the standard,
   * the encoding of the plus character is only mentioned for
   * application/x-www-form-urlencoded
   * (https://url.spec.whatwg.org/#urlencoded-parsing) and most browsers seems lo
   * leave the plus character as is in queries. To be more flexible, we allow the
   * plus character on the query but it can also be manually encoded by the user.
   *
   * Resources:
   * - https://url.spec.whatwg.org/#urlencoded-parsing
   * - https://stackoverflow.com/questions/1634271/url-encoding-the-space-character-or-20
   */
  const ENC_BRACKET_OPEN_RE = /%5B/g; // [
  const ENC_BRACKET_CLOSE_RE = /%5D/g; // ]
  const ENC_CARET_RE = /%5E/g; // ^
  const ENC_BACKTICK_RE = /%60/g; // `
  const ENC_CURLY_OPEN_RE = /%7B/g; // {
  const ENC_PIPE_RE = /%7C/g; // |
  const ENC_CURLY_CLOSE_RE = /%7D/g; // }
  const ENC_SPACE_RE = /%20/g; // }
  /**
   * Encode characters that need to be encoded on the path, search and hash
   * sections of the URL.
   *
   * @internal
   * @param text - string to encode
   * @returns encoded string
   */
  function commonEncode(text) {
      return encodeURI('' + text)
          .replace(ENC_PIPE_RE, '|')
          .replace(ENC_BRACKET_OPEN_RE, '[')
          .replace(ENC_BRACKET_CLOSE_RE, ']');
  }
  /**
   * Encode characters that need to be encoded on the hash section of the URL.
   *
   * @param text - string to encode
   * @returns encoded string
   */
  function encodeHash(text) {
      return commonEncode(text)
          .replace(ENC_CURLY_OPEN_RE, '{')
          .replace(ENC_CURLY_CLOSE_RE, '}')
          .replace(ENC_CARET_RE, '^');
  }
  /**
   * Encode characters that need to be encoded query values on the query
   * section of the URL.
   *
   * @param text - string to encode
   * @returns encoded string
   */
  function encodeQueryValue(text) {
      return (commonEncode(text)
          // Encode the space as +, encode the + to differentiate it from the space
          .replace(PLUS_RE, '%2B')
          .replace(ENC_SPACE_RE, '+')
          .replace(HASH_RE, '%23')
          .replace(AMPERSAND_RE, '%26')
          .replace(ENC_BACKTICK_RE, '`')
          .replace(ENC_CURLY_OPEN_RE, '{')
          .replace(ENC_CURLY_CLOSE_RE, '}')
          .replace(ENC_CARET_RE, '^'));
  }
  /**
   * Like `encodeQueryValue` but also encodes the `=` character.
   *
   * @param text - string to encode
   */
  function encodeQueryKey(text) {
      return encodeQueryValue(text).replace(EQUAL_RE, '%3D');
  }
  /**
   * Encode characters that need to be encoded on the path section of the URL.
   *
   * @param text - string to encode
   * @returns encoded string
   */
  function encodePath(text) {
      return commonEncode(text).replace(HASH_RE, '%23').replace(IM_RE, '%3F');
  }
  /**
   * Encode characters that need to be encoded on the path section of the URL as a
   * param. This function encodes everything {@link encodePath} does plus the
   * slash (`/`) character.
   *
   * @param text - string to encode
   * @returns encoded string
   */
  function encodeParam(text) {
      return encodePath(text).replace(SLASH_RE, '%2F');
  }
  /**
   * Decode text using `decodeURIComponent`. Returns the original text if it
   * fails.
   *
   * @param text - string to decode
   * @returns decoded string
   */
  function decode(text) {
      try {
          return decodeURIComponent('' + text);
      }
      catch (err) {
          warn(`Error decoding "${text}". Using original value`);
      }
      return '' + text;
  }

  /**
   * Transforms a queryString into a {@link LocationQuery} object. Accept both, a
   * version with the leading `?` and without Should work as URLSearchParams

   * @internal
   *
   * @param search - search string to parse
   * @returns a query object
   */
  function parseQuery(search) {
      const query = {};
      // avoid creating an object with an empty key and empty value
      // because of split('&')
      if (search === '' || search === '?')
          return query;
      const hasLeadingIM = search[0] === '?';
      const searchParams = (hasLeadingIM ? search.slice(1) : search).split('&');
      for (let i = 0; i < searchParams.length; ++i) {
          // pre decode the + into space
          const searchParam = searchParams[i].replace(PLUS_RE, ' ');
          // allow the = character
          let eqPos = searchParam.indexOf('=');
          let key = decode(eqPos < 0 ? searchParam : searchParam.slice(0, eqPos));
          let value = eqPos < 0 ? null : decode(searchParam.slice(eqPos + 1));
          if (key in query) {
              // an extra variable for ts types
              let currentValue = query[key];
              if (!Array.isArray(currentValue)) {
                  currentValue = query[key] = [currentValue];
              }
              currentValue.push(value);
          }
          else {
              query[key] = value;
          }
      }
      return query;
  }
  /**
   * Stringifies a {@link LocationQueryRaw} object. Like `URLSearchParams`, it
   * doesn't prepend a `?`
   *
   * @internal
   *
   * @param query - query object to stringify
   * @returns string version of the query without the leading `?`
   */
  function stringifyQuery(query) {
      let search = '';
      for (let key in query) {
          if (search.length)
              search += '&';
          const value = query[key];
          key = encodeQueryKey(key);
          if (value == null) {
              // only null adds the value
              if (value !== undefined)
                  search += key;
              continue;
          }
          // keep null values
          let values = Array.isArray(value)
              ? value.map(v => v && encodeQueryValue(v))
              : [value && encodeQueryValue(value)];
          for (let i = 0; i < values.length; i++) {
              // only append & with i > 0
              search += (i ? '&' : '') + key;
              if (values[i] != null)
                  search += ('=' + values[i]);
          }
      }
      return search;
  }
  /**
   * Transforms a {@link LocationQueryRaw} into a {@link LocationQuery} by casting
   * numbers into strings, removing keys with an undefined value and replacing
   * undefined with null in arrays
   *
   * @param query - query object to normalize
   * @returns a normalized query object
   */
  function normalizeQuery(query) {
      const normalizedQuery = {};
      for (let key in query) {
          let value = query[key];
          if (value !== undefined) {
              normalizedQuery[key] = Array.isArray(value)
                  ? value.map(v => (v == null ? null : '' + v))
                  : value == null
                      ? value
                      : '' + value;
          }
      }
      return normalizedQuery;
  }

  /**
   * Create a list of callbacks that can be reset. Used to create before and after navigation guards list
   */
  function useCallbacks() {
      let handlers = [];
      function add(handler) {
          handlers.push(handler);
          return () => {
              const i = handlers.indexOf(handler);
              if (i > -1)
                  handlers.splice(i, 1);
          };
      }
      function reset() {
          handlers = [];
      }
      return {
          add,
          list: () => handlers,
          reset,
      };
  }
  function guardToPromiseFn(guard, to, from, record, name) {
      // keep a reference to the enterCallbackArray to prevent pushing callbacks if a new navigation took place
      const enterCallbackArray = record &&
          // name is defined if record is because of the function overload
          (record.enterCallbacks[name] = record.enterCallbacks[name] || []);
      return () => new Promise((resolve, reject) => {
          const next = (valid) => {
              if (valid === false)
                  reject(createRouterError(4 /* NAVIGATION_ABORTED */, {
                      from,
                      to,
                  }));
              else if (valid instanceof Error) {
                  reject(valid);
              }
              else if (isRouteLocation(valid)) {
                  reject(createRouterError(2 /* NAVIGATION_GUARD_REDIRECT */, {
                      from: to,
                      to: valid,
                  }));
              }
              else {
                  if (enterCallbackArray &&
                      // since enterCallbackArray is truthy, both record and name also are
                      record.enterCallbacks[name] === enterCallbackArray &&
                      typeof valid === 'function')
                      enterCallbackArray.push(valid);
                  resolve();
              }
          };
          // wrapping with Promise.resolve allows it to work with both async and sync guards
          const guardReturn = guard.call(record && record.instances[name], to, from, canOnlyBeCalledOnce(next, to, from) );
          let guardCall = Promise.resolve(guardReturn);
          if (guard.length < 3)
              guardCall = guardCall.then(next);
          if (guard.length > 2) {
              const message = `The "next" callback was never called inside of ${guard.name ? '"' + guard.name + '"' : ''}:\n${guard.toString()}\n. If you are returning a value instead of calling "next", make sure to remove the "next" parameter from your function.`;
              if (typeof guardReturn === 'object' && 'then' in guardReturn) {
                  guardCall = guardCall.then(resolvedValue => {
                      // @ts-ignore: _called is added at canOnlyBeCalledOnce
                      if (!next._called) {
                          warn(message);
                          return Promise.reject(new Error('Invalid navigation guard'));
                      }
                      return resolvedValue;
                  });
                  // TODO: test me!
              }
              else if (guardReturn !== undefined) {
                  // @ts-ignore: _called is added at canOnlyBeCalledOnce
                  if (!next._called) {
                      warn(message);
                      reject(new Error('Invalid navigation guard'));
                      return;
                  }
              }
          }
          guardCall.catch(err => reject(err));
      });
  }
  function canOnlyBeCalledOnce(next, to, from) {
      let called = 0;
      return function () {
          if (called++ === 1)
              warn(`The "next" callback was called more than once in one navigation guard when going from "${from.fullPath}" to "${to.fullPath}". It should be called exactly one time in each navigation guard. This will fail in production.`);
          // @ts-ignore: we put it in the original one because it's easier to check
          next._called = true;
          if (called === 1)
              next.apply(null, arguments);
      };
  }
  function extractComponentsGuards(matched, guardType, to, from) {
      const guards = [];
      for (const record of matched) {
          for (const name in record.components) {
              let rawComponent = record.components[name];
              {
                  if (!rawComponent ||
                      (typeof rawComponent !== 'object' &&
                          typeof rawComponent !== 'function')) {
                      warn(`Component "${name}" in record with path "${record.path}" is not` +
                          ` a valid component. Received "${String(rawComponent)}".`);
                      // throw to ensure we stop here but warn to ensure the message isn't
                      // missed by the user
                      throw new Error('Invalid route component');
                  }
                  else if ('then' in rawComponent) {
                      // warn if user wrote import('/component.vue') instead of () =>
                      // import('./component.vue')
                      warn(`Component "${name}" in record with path "${record.path}" is a ` +
                          `Promise instead of a function that returns a Promise. Did you ` +
                          `write "import('./MyPage.vue')" instead of ` +
                          `"() => import('./MyPage.vue')" ? This will break in ` +
                          `production if not fixed.`);
                      let promise = rawComponent;
                      rawComponent = () => promise;
                  }
                  else if (rawComponent.__asyncLoader &&
                      // warn only once per component
                      !rawComponent.__warnedDefineAsync) {
                      rawComponent.__warnedDefineAsync = true;
                      warn(`Component "${name}" in record with path "${record.path}" is defined ` +
                          `using "defineAsyncComponent()". ` +
                          `Write "() => import('./MyPage.vue')" instead of ` +
                          `"defineAsyncComponent(() => import('./MyPage.vue'))".`);
                  }
              }
              // skip update and leave guards if the route component is not mounted
              if (guardType !== 'beforeRouteEnter' && !record.instances[name])
                  continue;
              if (isRouteComponent(rawComponent)) {
                  // __vccOpts is added by vue-class-component and contain the regular options
                  let options = rawComponent.__vccOpts || rawComponent;
                  const guard = options[guardType];
                  guard && guards.push(guardToPromiseFn(guard, to, from, record, name));
              }
              else {
                  // start requesting the chunk already
                  let componentPromise = rawComponent();
                  if (!('catch' in componentPromise)) {
                      warn(`Component "${name}" in record with path "${record.path}" is a function that does not return a Promise. If you were passing a functional component, make sure to add a "displayName" to the component. This will break in production if not fixed.`);
                      componentPromise = Promise.resolve(componentPromise);
                  }
                  else {
                      // display the error if any
                      componentPromise = componentPromise.catch(console.error);
                  }
                  guards.push(() => componentPromise.then(resolved => {
                      if (!resolved)
                          return Promise.reject(new Error(`Couldn't resolve component "${name}" at "${record.path}"`));
                      const resolvedComponent = isESModule(resolved)
                          ? resolved.default
                          : resolved;
                      // replace the function with the resolved component
                      record.components[name] = resolvedComponent;
                      // __vccOpts is added by vue-class-component and contain the regular options
                      let options = resolvedComponent.__vccOpts || resolvedComponent;
                      const guard = options[guardType];
                      return guard && guardToPromiseFn(guard, to, from, record, name)();
                  }));
              }
          }
      }
      return guards;
  }
  /**
   * Allows differentiating lazy components from functional components and vue-class-component
   * @param component
   */
  function isRouteComponent(component) {
      return (typeof component === 'object' ||
          'displayName' in component ||
          'props' in component ||
          '__vccOpts' in component);
  }

  // TODO: we could allow currentRoute as a prop to expose `isActive` and
  // `isExactActive` behavior should go through an RFC
  function useLink(props) {
      const router = vue.inject(routerKey);
      const currentRoute = vue.inject(routeLocationKey);
      const route = vue.computed(() => router.resolve(vue.unref(props.to)));
      const activeRecordIndex = vue.computed(() => {
          let { matched } = route.value;
          let { length } = matched;
          const routeMatched = matched[length - 1];
          let currentMatched = currentRoute.matched;
          if (!routeMatched || !currentMatched.length)
              return -1;
          let index = currentMatched.findIndex(isSameRouteRecord.bind(null, routeMatched));
          if (index > -1)
              return index;
          // possible parent record
          let parentRecordPath = getOriginalPath(matched[length - 2]);
          return (
          // we are dealing with nested routes
          length > 1 &&
              // if the parent and matched route have the same path, this link is
              // referring to the empty child. Or we currently are on a different
              // child of the same parent
              getOriginalPath(routeMatched) === parentRecordPath &&
              // avoid comparing the child with its parent
              currentMatched[currentMatched.length - 1].path !== parentRecordPath
              ? currentMatched.findIndex(isSameRouteRecord.bind(null, matched[length - 2]))
              : index);
      });
      const isActive = vue.computed(() => activeRecordIndex.value > -1 &&
          includesParams(currentRoute.params, route.value.params));
      const isExactActive = vue.computed(() => activeRecordIndex.value > -1 &&
          activeRecordIndex.value === currentRoute.matched.length - 1 &&
          isSameRouteLocationParams(currentRoute.params, route.value.params));
      function navigate(e = {}) {
          if (guardEvent(e))
              return router[vue.unref(props.replace) ? 'replace' : 'push'](vue.unref(props.to));
          return Promise.resolve();
      }
      return {
          route,
          href: vue.computed(() => route.value.href),
          isActive,
          isExactActive,
          navigate,
      };
  }
  const RouterLinkImpl = /*#__PURE__*/ vue.defineComponent({
      name: 'RouterLink',
      props: {
          to: {
              type: [String, Object],
              required: true,
          },
          replace: Boolean,
          activeClass: String,
          // inactiveClass: String,
          exactActiveClass: String,
          custom: Boolean,
          ariaCurrentValue: {
              type: String,
              default: 'page',
          },
      },
      setup(props, { slots }) {
          const link = vue.reactive(useLink(props));
          const { options } = vue.inject(routerKey);
          const elClass = vue.computed(() => ({
              [getLinkClass(props.activeClass, options.linkActiveClass, 'router-link-active')]: link.isActive,
              // [getLinkClass(
              //   props.inactiveClass,
              //   options.linkInactiveClass,
              //   'router-link-inactive'
              // )]: !link.isExactActive,
              [getLinkClass(props.exactActiveClass, options.linkExactActiveClass, 'router-link-exact-active')]: link.isExactActive,
          }));
          {
              const instance = vue.getCurrentInstance();
              vue.watchEffect(() => {
                  if (!instance)
                      return;
                  instance.__vrl_route = link.route;
              }, { flush: 'post' });
              vue.watchEffect(() => {
                  if (!instance)
                      return;
                  instance.__vrl_active = link.isActive;
                  instance.__vrl_exactActive = link.isExactActive;
              }, { flush: 'post' });
          }
          return () => {
              const children = slots.default && slots.default(link);
              return props.custom
                  ? children
                  : vue.h('a', {
                      'aria-current': link.isExactActive
                          ? props.ariaCurrentValue
                          : null,
                      href: link.href,
                      // this would override user added attrs but Vue will still add
                      // the listener so we end up triggering both
                      onClick: link.navigate,
                      class: elClass.value,
                  }, children);
          };
      },
  });
  // export the public type for h/tsx inference
  // also to avoid inline import() in generated d.ts files
  /**
   * Component to render a link that triggers a navigation on click.
   */
  const RouterLink = RouterLinkImpl;
  function guardEvent(e) {
      // don't redirect with control keys
      if (e.metaKey || e.altKey || e.ctrlKey || e.shiftKey)
          return;
      // don't redirect when preventDefault called
      if (e.defaultPrevented)
          return;
      // don't redirect on right click
      if (e.button !== undefined && e.button !== 0)
          return;
      // don't redirect if `target="_blank"`
      // @ts-ignore getAttribute does exist
      if (e.currentTarget && e.currentTarget.getAttribute) {
          // @ts-ignore getAttribute exists
          const target = e.currentTarget.getAttribute('target');
          if (/\b_blank\b/i.test(target))
              return;
      }
      // this may be a Weex event which doesn't have this method
      if (e.preventDefault)
          e.preventDefault();
      return true;
  }
  function includesParams(outer, inner) {
      for (let key in inner) {
          let innerValue = inner[key];
          let outerValue = outer[key];
          if (typeof innerValue === 'string') {
              if (innerValue !== outerValue)
                  return false;
          }
          else {
              if (!Array.isArray(outerValue) ||
                  outerValue.length !== innerValue.length ||
                  innerValue.some((value, i) => value !== outerValue[i]))
                  return false;
          }
      }
      return true;
  }
  /**
   * Get the original path value of a record by following its aliasOf
   * @param record
   */
  function getOriginalPath(record) {
      return record ? (record.aliasOf ? record.aliasOf.path : record.path) : '';
  }
  /**
   * Utility class to get the active class based on defaults.
   * @param propClass
   * @param globalClass
   * @param defaultClass
   */
  const getLinkClass = (propClass, globalClass, defaultClass) => propClass != null
      ? propClass
      : globalClass != null
          ? globalClass
          : defaultClass;

  const RouterViewImpl = /*#__PURE__*/ vue.defineComponent({
      name: 'RouterView',
      // #674 we manually inherit them
      inheritAttrs: false,
      props: {
          name: {
              type: String,
              default: 'default',
          },
          route: Object,
      },
      setup(props, { attrs, slots }) {
          warnDeprecatedUsage();
          const injectedRoute = vue.inject(routerViewLocationKey);
          const routeToDisplay = vue.computed(() => props.route || injectedRoute.value);
          const depth = vue.inject(viewDepthKey, 0);
          const matchedRouteRef = vue.computed(() => routeToDisplay.value.matched[depth]);
          vue.provide(viewDepthKey, depth + 1);
          vue.provide(matchedRouteKey, matchedRouteRef);
          vue.provide(routerViewLocationKey, routeToDisplay);
          const viewRef = vue.ref();
          // watch at the same time the component instance, the route record we are
          // rendering, and the name
          vue.watch(() => [viewRef.value, matchedRouteRef.value, props.name], ([instance, to, name], [oldInstance, from, oldName]) => {
              // copy reused instances
              if (to) {
                  // this will update the instance for new instances as well as reused
                  // instances when navigating to a new route
                  to.instances[name] = instance;
                  // the component instance is reused for a different route or name so
                  // we copy any saved update or leave guards. With async setup, the
                  // mounting component will mount before the matchedRoute changes,
                  // making instance === oldInstance, so we check if guards have been
                  // added before. This works because we remove guards when
                  // unmounting/deactivating components
                  if (from && from !== to && instance && instance === oldInstance) {
                      if (!to.leaveGuards.size) {
                          to.leaveGuards = from.leaveGuards;
                      }
                      if (!to.updateGuards.size) {
                          to.updateGuards = from.updateGuards;
                      }
                  }
              }
              // trigger beforeRouteEnter next callbacks
              if (instance &&
                  to &&
                  // if there is no instance but to and from are the same this might be
                  // the first visit
                  (!from || !isSameRouteRecord(to, from) || !oldInstance)) {
                  (to.enterCallbacks[name] || []).forEach(callback => callback(instance));
              }
          }, { flush: 'post' });
          return () => {
              const route = routeToDisplay.value;
              const matchedRoute = matchedRouteRef.value;
              const ViewComponent = matchedRoute && matchedRoute.components[props.name];
              // we need the value at the time we render because when we unmount, we
              // navigated to a different location so the value is different
              const currentName = props.name;
              if (!ViewComponent) {
                  return normalizeSlot(slots.default, { Component: ViewComponent, route });
              }
              // props from route configuration
              const routePropsOption = matchedRoute.props[props.name];
              const routeProps = routePropsOption
                  ? routePropsOption === true
                      ? route.params
                      : typeof routePropsOption === 'function'
                          ? routePropsOption(route)
                          : routePropsOption
                  : null;
              const onVnodeUnmounted = vnode => {
                  // remove the instance reference to prevent leak
                  if (vnode.component.isUnmounted) {
                      matchedRoute.instances[currentName] = null;
                  }
              };
              const component = vue.h(ViewComponent, assign({}, routeProps, attrs, {
                  onVnodeUnmounted,
                  ref: viewRef,
              }));
              return (
              // pass the vnode to the slot as a prop.
              // h and <component :is="..."> both accept vnodes
              normalizeSlot(slots.default, { Component: component, route }) ||
                  component);
          };
      },
  });
  function normalizeSlot(slot, data) {
      if (!slot)
          return null;
      const slotContent = slot(data);
      return slotContent.length === 1 ? slotContent[0] : slotContent;
  }
  // export the public type for h/tsx inference
  // also to avoid inline import() in generated d.ts files
  /**
   * Component to display the current route the user is at.
   */
  const RouterView = RouterViewImpl;
  // warn against deprecated usage with <transition> & <keep-alive>
  // due to functional component being no longer eager in Vue 3
  function warnDeprecatedUsage() {
      const instance = vue.getCurrentInstance();
      const parentName = instance.parent && instance.parent.type.name;
      if (parentName &&
          (parentName === 'KeepAlive' || parentName.includes('Transition'))) {
          const comp = parentName === 'KeepAlive' ? 'keep-alive' : 'transition';
          warn(`<router-view> can no longer be used directly inside <transition> or <keep-alive>.\n` +
              `Use slot props instead:\n\n` +
              `<router-view v-slot="{ Component }">\n` +
              `  <${comp}>\n` +
              `    <component :is="Component" />\n` +
              `  </${comp}>\n` +
              `</router-view>`);
      }
  }

  function getDevtoolsGlobalHook() {
      return getTarget().__VUE_DEVTOOLS_GLOBAL_HOOK__;
  }
  function getTarget() {
      // @ts-ignore
      return typeof navigator !== 'undefined'
          ? window
          : typeof global !== 'undefined'
              ? global
              : {};
  }

  const HOOK_SETUP = 'devtools-plugin:setup';

  function setupDevtoolsPlugin(pluginDescriptor, setupFn) {
      const hook = getDevtoolsGlobalHook();
      if (hook) {
          hook.emit(HOOK_SETUP, pluginDescriptor, setupFn);
      }
      else {
          const target = getTarget();
          const list = target.__VUE_DEVTOOLS_PLUGINS__ = target.__VUE_DEVTOOLS_PLUGINS__ || [];
          list.push({
              pluginDescriptor,
              setupFn
          });
      }
  }

  function formatRouteLocation(routeLocation, tooltip) {
      const copy = assign({}, routeLocation, {
          // remove variables that can contain vue instances
          matched: routeLocation.matched.map(matched => omit(matched, ['instances', 'children', 'aliasOf'])),
      });
      return {
          _custom: {
              type: null,
              readOnly: true,
              display: routeLocation.fullPath,
              tooltip,
              value: copy,
          },
      };
  }
  function formatDisplay(display) {
      return {
          _custom: {
              display,
          },
      };
  }
  // to support multiple router instances
  let routerId = 0;
  function addDevtools(app, router, matcher) {
      // Take over router.beforeEach and afterEach
      // make sure we are not registering the devtool twice
      if (router.__hasDevtools)
          return;
      router.__hasDevtools = true;
      // increment to support multiple router instances
      const id = routerId++;
      setupDevtoolsPlugin({
          id: 'org.vuejs.router' + (id ? '.' + id : ''),
          label: 'Vue Router',
          packageName: 'vue-router',
          homepage: 'https://next.router.vuejs.org/',
          logo: 'https://vuejs.org/images/icons/favicon-96x96.png',
          componentStateTypes: ['Routing'],
          app,
      }, api => {
          // display state added by the router
          api.on.inspectComponent((payload, ctx) => {
              if (payload.instanceData) {
                  payload.instanceData.state.push({
                      type: 'Routing',
                      key: '$route',
                      editable: false,
                      value: formatRouteLocation(router.currentRoute.value, 'Current Route'),
                  });
              }
          });
          // mark router-link as active
          api.on.visitComponentTree(({ treeNode: node, componentInstance }) => {
              if (node.name === 'RouterLink') {
                  if (componentInstance.__vrl_route) {
                      node.tags.push({
                          label: componentInstance.__vrl_route.path,
                          textColor: 0,
                          backgroundColor: ORANGE_400,
                      });
                  }
                  if (componentInstance.__vrl_exactActive) {
                      node.tags.push({
                          label: 'exact',
                          textColor: 0,
                          backgroundColor: LIME_500,
                      });
                  }
                  if (componentInstance.__vrl_active) {
                      node.tags.push({
                          label: 'active',
                          textColor: 0,
                          backgroundColor: BLUE_600,
                      });
                  }
              }
          });
          vue.watch(router.currentRoute, () => {
              // refresh active state
              refreshRoutesView();
              api.notifyComponentUpdate();
              api.sendInspectorTree(routerInspectorId);
          });
          const navigationsLayerId = 'router:navigations:' + id;
          api.addTimelineLayer({
              id: navigationsLayerId,
              label: `Router${id ? ' ' + id : ''} Navigations`,
              color: 0x40a8c4,
          });
          // const errorsLayerId = 'router:errors'
          // api.addTimelineLayer({
          //   id: errorsLayerId,
          //   label: 'Router Errors',
          //   color: 0xea5455,
          // })
          router.onError(error => {
              api.addTimelineEvent({
                  layerId: navigationsLayerId,
                  event: {
                      title: 'Error',
                      subtitle: 'An uncaught error happened during navigation',
                      logType: 'error',
                      time: Date.now(),
                      data: { error },
                  },
              });
          });
          // attached to `meta` and used to group events
          let navigationId = 0;
          router.beforeEach((to, from) => {
              const data = {
                  guard: formatDisplay('beforeEach'),
                  from: formatRouteLocation(from, 'Current Location during this navigation'),
                  to: formatRouteLocation(to, 'Target location'),
              };
              // Used to group navigations together, hide from devtools
              Object.defineProperty(to.meta, '__navigationId', {
                  value: navigationId++,
              });
              api.addTimelineEvent({
                  layerId: navigationsLayerId,
                  event: {
                      time: Date.now(),
                      title: 'Start of navigation',
                      data,
                      groupId: to.meta.__navigationId,
                  },
              });
          });
          router.afterEach((to, from, failure) => {
              const data = {
                  guard: formatDisplay('afterEach'),
              };
              if (failure) {
                  data.failure = {
                      _custom: {
                          type: Error,
                          readOnly: true,
                          display: failure ? failure.message : '',
                          tooltip: 'Navigation Failure',
                          value: failure,
                      },
                  };
                  data.status = formatDisplay('❌');
              }
              else {
                  data.status = formatDisplay('✅');
              }
              // we set here to have the right order
              data.from = formatRouteLocation(from, 'Current Location during this navigation');
              data.to = formatRouteLocation(to, 'Target location');
              api.addTimelineEvent({
                  layerId: navigationsLayerId,
                  event: {
                      title: 'End of navigation',
                      time: Date.now(),
                      data,
                      logType: failure ? 'warning' : 'default',
                      groupId: to.meta.__navigationId,
                  },
              });
          });
          /**
           * Inspector of Existing routes
           */
          const routerInspectorId = 'router-inspector:' + id;
          api.addInspector({
              id: routerInspectorId,
              label: 'Routes' + (id ? ' ' + id : ''),
              icon: 'book',
              treeFilterPlaceholder: 'Search routes',
          });
          function refreshRoutesView() {
              // the routes view isn't active
              if (!activeRoutesPayload)
                  return;
              const payload = activeRoutesPayload;
              // children routes will appear as nested
              let routes = matcher.getRoutes().filter(route => !route.parent);
              // reset match state to false
              routes.forEach(resetMatchStateOnRouteRecord);
              // apply a match state if there is a payload
              if (payload.filter) {
                  routes = routes.filter(route => 
                  // save matches state based on the payload
                  isRouteMatching(route, payload.filter.toLowerCase()));
              }
              // mark active routes
              routes.forEach(route => markRouteRecordActive(route, router.currentRoute.value));
              payload.rootNodes = routes.map(formatRouteRecordForInspector);
          }
          let activeRoutesPayload;
          api.on.getInspectorTree(payload => {
              activeRoutesPayload = payload;
              if (payload.app === app && payload.inspectorId === routerInspectorId) {
                  refreshRoutesView();
              }
          });
          /**
           * Display information about the currently selected route record
           */
          api.on.getInspectorState(payload => {
              if (payload.app === app && payload.inspectorId === routerInspectorId) {
                  const routes = matcher.getRoutes();
                  const route = routes.find(route => route.record.__vd_id === payload.nodeId);
                  if (route) {
                      payload.state = {
                          options: formatRouteRecordMatcherForStateInspector(route),
                      };
                  }
              }
          });
          api.sendInspectorTree(routerInspectorId);
          api.sendInspectorState(routerInspectorId);
      });
  }
  function modifierForKey(key) {
      if (key.optional) {
          return key.repeatable ? '*' : '?';
      }
      else {
          return key.repeatable ? '+' : '';
      }
  }
  function formatRouteRecordMatcherForStateInspector(route) {
      const { record } = route;
      const fields = [
          { editable: false, key: 'path', value: record.path },
      ];
      if (record.name != null) {
          fields.push({
              editable: false,
              key: 'name',
              value: record.name,
          });
      }
      fields.push({ editable: false, key: 'regexp', value: route.re });
      if (route.keys.length) {
          fields.push({
              editable: false,
              key: 'keys',
              value: {
                  _custom: {
                      type: null,
                      readOnly: true,
                      display: route.keys
                          .map(key => `${key.name}${modifierForKey(key)}`)
                          .join(' '),
                      tooltip: 'Param keys',
                      value: route.keys,
                  },
              },
          });
      }
      if (record.redirect != null) {
          fields.push({
              editable: false,
              key: 'redirect',
              value: record.redirect,
          });
      }
      if (route.alias.length) {
          fields.push({
              editable: false,
              key: 'aliases',
              value: route.alias.map(alias => alias.record.path),
          });
      }
      fields.push({
          key: 'score',
          editable: false,
          value: {
              _custom: {
                  type: null,
                  readOnly: true,
                  display: route.score.map(score => score.join(', ')).join(' | '),
                  tooltip: 'Score used to sort routes',
                  value: route.score,
              },
          },
      });
      return fields;
  }
  /**
   * Extracted from tailwind palette
   */
  const PINK_500 = 0xec4899;
  const BLUE_600 = 0x2563eb;
  const LIME_500 = 0x84cc16;
  const CYAN_400 = 0x22d3ee;
  const ORANGE_400 = 0xfb923c;
  // const GRAY_100 = 0xf4f4f5
  const DARK = 0x666666;
  function formatRouteRecordForInspector(route) {
      const tags = [];
      const { record } = route;
      if (record.name != null) {
          tags.push({
              label: String(record.name),
              textColor: 0,
              backgroundColor: CYAN_400,
          });
      }
      if (record.aliasOf) {
          tags.push({
              label: 'alias',
              textColor: 0,
              backgroundColor: ORANGE_400,
          });
      }
      if (route.__vd_match) {
          tags.push({
              label: 'matches',
              textColor: 0,
              backgroundColor: PINK_500,
          });
      }
      if (route.__vd_exactActive) {
          tags.push({
              label: 'exact',
              textColor: 0,
              backgroundColor: LIME_500,
          });
      }
      if (route.__vd_active) {
          tags.push({
              label: 'active',
              textColor: 0,
              backgroundColor: BLUE_600,
          });
      }
      if (record.redirect) {
          tags.push({
              label: 'redirect: ' +
                  (typeof record.redirect === 'string' ? record.redirect : 'Object'),
              textColor: 0xffffff,
              backgroundColor: DARK,
          });
      }
      // add an id to be able to select it. Using the `path` is not possible because
      // empty path children would collide with their parents
      let id = String(routeRecordId++);
      record.__vd_id = id;
      return {
          id,
          label: record.path,
          tags,
          children: route.children.map(formatRouteRecordForInspector),
      };
  }
  //  incremental id for route records and inspector state
  let routeRecordId = 0;
  const EXTRACT_REGEXP_RE = /^\/(.*)\/([a-z]*)$/;
  function markRouteRecordActive(route, currentRoute) {
      // no route will be active if matched is empty
      // reset the matching state
      const isExactActive = currentRoute.matched.length &&
          isSameRouteRecord(currentRoute.matched[currentRoute.matched.length - 1], route.record);
      route.__vd_exactActive = route.__vd_active = isExactActive;
      if (!isExactActive) {
          route.__vd_active = currentRoute.matched.some(match => isSameRouteRecord(match, route.record));
      }
      route.children.forEach(childRoute => markRouteRecordActive(childRoute, currentRoute));
  }
  function resetMatchStateOnRouteRecord(route) {
      route.__vd_match = false;
      route.children.forEach(resetMatchStateOnRouteRecord);
  }
  function isRouteMatching(route, filter) {
      const found = String(route.re).match(EXTRACT_REGEXP_RE);
      route.__vd_match = false;
      if (!found || found.length < 3) {
          return false;
      }
      // use a regexp without $ at the end to match nested routes better
      const nonEndingRE = new RegExp(found[1].replace(/\$$/, ''), found[2]);
      if (nonEndingRE.test(filter)) {
          // mark children as matches
          route.children.forEach(child => isRouteMatching(child, filter));
          // exception case: `/`
          if (route.record.path !== '/' || filter === '/') {
              route.__vd_match = route.re.test(filter);
              return true;
          }
          // hide the / route
          return false;
      }
      const path = route.record.path.toLowerCase();
      const decodedPath = decode(path);
      // also allow partial matching on the path
      if (!filter.startsWith('/') &&
          (decodedPath.includes(filter) || path.includes(filter)))
          return true;
      if (decodedPath.startsWith(filter) || path.startsWith(filter))
          return true;
      if (route.record.name && String(route.record.name).includes(filter))
          return true;
      return route.children.some(child => isRouteMatching(child, filter));
  }
  function omit(obj, keys) {
      const ret = {};
      for (let key in obj) {
          if (!keys.includes(key)) {
              // @ts-ignore
              ret[key] = obj[key];
          }
      }
      return ret;
  }

  /**
   * Creates a Router instance that can be used by a Vue app.
   *
   * @param options - {@link RouterOptions}
   */
  function createRouter(options) {
      const matcher = createRouterMatcher(options.routes, options);
      let parseQuery$1 = options.parseQuery || parseQuery;
      let stringifyQuery$1 = options.stringifyQuery || stringifyQuery;
      let routerHistory = options.history;
      if (!routerHistory)
          throw new Error('Provide the "history" option when calling "createRouter()":' +
              ' https://next.router.vuejs.org/api/#history.');
      const beforeGuards = useCallbacks();
      const beforeResolveGuards = useCallbacks();
      const afterGuards = useCallbacks();
      const currentRoute = vue.shallowRef(START_LOCATION_NORMALIZED);
      let pendingLocation = START_LOCATION_NORMALIZED;
      // leave the scrollRestoration if no scrollBehavior is provided
      if (isBrowser && options.scrollBehavior && 'scrollRestoration' in history) {
          history.scrollRestoration = 'manual';
      }
      const normalizeParams = applyToParams.bind(null, paramValue => '' + paramValue);
      const encodeParams = applyToParams.bind(null, encodeParam);
      const decodeParams = applyToParams.bind(null, decode);
      function addRoute(parentOrRoute, route) {
          let parent;
          let record;
          if (isRouteName(parentOrRoute)) {
              parent = matcher.getRecordMatcher(parentOrRoute);
              record = route;
          }
          else {
              record = parentOrRoute;
          }
          return matcher.addRoute(record, parent);
      }
      function removeRoute(name) {
          let recordMatcher = matcher.getRecordMatcher(name);
          if (recordMatcher) {
              matcher.removeRoute(recordMatcher);
          }
          else {
              warn(`Cannot remove non-existent route "${String(name)}"`);
          }
      }
      function getRoutes() {
          return matcher.getRoutes().map(routeMatcher => routeMatcher.record);
      }
      function hasRoute(name) {
          return !!matcher.getRecordMatcher(name);
      }
      function resolve(rawLocation, currentLocation) {
          // const objectLocation = routerLocationAsObject(rawLocation)
          // we create a copy to modify it later
          currentLocation = assign({}, currentLocation || currentRoute.value);
          if (typeof rawLocation === 'string') {
              let locationNormalized = parseURL(parseQuery$1, rawLocation, currentLocation.path);
              let matchedRoute = matcher.resolve({ path: locationNormalized.path }, currentLocation);
              let href = routerHistory.createHref(locationNormalized.fullPath);
              {
                  if (href.startsWith('//'))
                      warn(`Location "${rawLocation}" resolved to "${href}". A resolved location cannot start with multiple slashes.`);
                  else if (!matchedRoute.matched.length) {
                      warn(`No match found for location with path "${rawLocation}"`);
                  }
              }
              // locationNormalized is always a new object
              return assign(locationNormalized, matchedRoute, {
                  params: decodeParams(matchedRoute.params),
                  hash: decode(locationNormalized.hash),
                  redirectedFrom: undefined,
                  href,
              });
          }
          let matcherLocation;
          // path could be relative in object as well
          if ('path' in rawLocation) {
              if ('params' in rawLocation &&
                  !('name' in rawLocation) &&
                  Object.keys(rawLocation.params).length) {
                  warn(`Path "${rawLocation.path}" was passed with params but they will be ignored. Use a named route alongside params instead.`);
              }
              matcherLocation = assign({}, rawLocation, {
                  path: parseURL(parseQuery$1, rawLocation.path, currentLocation.path).path,
              });
          }
          else {
              // pass encoded values to the matcher so it can produce encoded path and fullPath
              matcherLocation = assign({}, rawLocation, {
                  params: encodeParams(rawLocation.params),
              });
              // current location params are decoded, we need to encode them in case the
              // matcher merges the params
              currentLocation.params = encodeParams(currentLocation.params);
          }
          let matchedRoute = matcher.resolve(matcherLocation, currentLocation);
          const hash = rawLocation.hash || '';
          if (hash && !hash.startsWith('#')) {
              warn(`A \`hash\` should always start with the character "#". Replace "${hash}" with "#${hash}".`);
          }
          // decoding them) the matcher might have merged current location params so
          // we need to run the decoding again
          matchedRoute.params = normalizeParams(decodeParams(matchedRoute.params));
          const fullPath = stringifyURL(stringifyQuery$1, assign({}, rawLocation, {
              hash: encodeHash(hash),
              path: matchedRoute.path,
          }));
          let href = routerHistory.createHref(fullPath);
          {
              if (href.startsWith('//')) {
                  warn(`Location "${rawLocation}" resolved to "${href}". A resolved location cannot start with multiple slashes.`);
              }
              else if (!matchedRoute.matched.length) {
                  warn(`No match found for location with path "${'path' in rawLocation ? rawLocation.path : rawLocation}"`);
              }
          }
          return assign({
              fullPath,
              // keep the hash encoded so fullPath is effectively path + encodedQuery +
              // hash
              hash,
              query: 
              // if the user is using a custom query lib like qs, we might have
              // nested objects, so we keep the query as is, meaning it can contain
              // numbers at `$route.query`, but at the point, the user will have to
              // use their own type anyway.
              // https://github.com/vuejs/vue-router-next/issues/328#issuecomment-649481567
              stringifyQuery$1 === stringifyQuery
                  ? normalizeQuery(rawLocation.query)
                  : rawLocation.query,
          }, matchedRoute, {
              redirectedFrom: undefined,
              href,
          });
      }
      function locationAsObject(to) {
          return typeof to === 'string'
              ? parseURL(parseQuery$1, to, currentRoute.value.path)
              : assign({}, to);
      }
      function checkCanceledNavigation(to, from) {
          if (pendingLocation !== to) {
              return createRouterError(8 /* NAVIGATION_CANCELLED */, {
                  from,
                  to,
              });
          }
      }
      function push(to) {
          return pushWithRedirect(to);
      }
      function replace(to) {
          return push(assign(locationAsObject(to), { replace: true }));
      }
      function handleRedirectRecord(to) {
          const lastMatched = to.matched[to.matched.length - 1];
          if (lastMatched && lastMatched.redirect) {
              const { redirect } = lastMatched;
              let newTargetLocation = typeof redirect === 'function' ? redirect(to) : redirect;
              if (typeof newTargetLocation === 'string') {
                  newTargetLocation =
                      newTargetLocation.indexOf('?') > -1 ||
                          newTargetLocation.indexOf('#') > -1
                          ? (newTargetLocation = locationAsObject(newTargetLocation))
                          : { path: newTargetLocation };
              }
              if (!('path' in newTargetLocation) &&
                  !('name' in newTargetLocation)) {
                  warn(`Invalid redirect found:\n${JSON.stringify(newTargetLocation, null, 2)}\n when navigating to "${to.fullPath}". A redirect must contain a name or path. This will break in production.`);
                  throw new Error('Invalid redirect');
              }
              return assign({
                  query: to.query,
                  hash: to.hash,
                  params: to.params,
              }, newTargetLocation);
          }
      }
      function pushWithRedirect(to, redirectedFrom) {
          const targetLocation = (pendingLocation = resolve(to));
          const from = currentRoute.value;
          const data = to.state;
          const force = to.force;
          // to could be a string where `replace` is a function
          const replace = to.replace === true;
          const shouldRedirect = handleRedirectRecord(targetLocation);
          if (shouldRedirect)
              return pushWithRedirect(assign(locationAsObject(shouldRedirect), {
                  state: data,
                  force,
                  replace,
              }), 
              // keep original redirectedFrom if it exists
              redirectedFrom || targetLocation);
          // if it was a redirect we already called `pushWithRedirect` above
          const toLocation = targetLocation;
          toLocation.redirectedFrom = redirectedFrom;
          let failure;
          if (!force && isSameRouteLocation(stringifyQuery$1, from, targetLocation)) {
              failure = createRouterError(16 /* NAVIGATION_DUPLICATED */, { to: toLocation, from });
              // trigger scroll to allow scrolling to the same anchor
              handleScroll(from, from, 
              // this is a push, the only way for it to be triggered from a
              // history.listen is with a redirect, which makes it become a push
              true, 
              // This cannot be the first navigation because the initial location
              // cannot be manually navigated to
              false);
          }
          return (failure ? Promise.resolve(failure) : navigate(toLocation, from))
              .catch((error) => isNavigationFailure(error)
              ? error
              : // reject any unknown error
                  triggerError(error))
              .then((failure) => {
              if (failure) {
                  if (isNavigationFailure(failure, 2 /* NAVIGATION_GUARD_REDIRECT */)) {
                      if (// we are redirecting to the same location we were already at
                          isSameRouteLocation(stringifyQuery$1, resolve(failure.to), toLocation) &&
                          // and we have done it a couple of times
                          redirectedFrom &&
                          // @ts-ignore
                          (redirectedFrom._count = redirectedFrom._count
                              ? // @ts-ignore
                                  redirectedFrom._count + 1
                              : 1) > 10) {
                          warn(`Detected an infinite redirection in a navigation guard when going from "${from.fullPath}" to "${toLocation.fullPath}". Aborting to avoid a Stack Overflow. This will break in production if not fixed.`);
                          return Promise.reject(new Error('Infinite redirect in navigation guard'));
                      }
                      return pushWithRedirect(
                      // keep options
                      assign(locationAsObject(failure.to), {
                          state: data,
                          force,
                          replace,
                      }), 
                      // preserve the original redirectedFrom if any
                      redirectedFrom || toLocation);
                  }
              }
              else {
                  // if we fail we don't finalize the navigation
                  failure = finalizeNavigation(toLocation, from, true, replace, data);
              }
              triggerAfterEach(toLocation, from, failure);
              return failure;
          });
      }
      /**
       * Helper to reject and skip all navigation guards if a new navigation happened
       * @param to
       * @param from
       */
      function checkCanceledNavigationAndReject(to, from) {
          const error = checkCanceledNavigation(to, from);
          return error ? Promise.reject(error) : Promise.resolve();
      }
      // TODO: refactor the whole before guards by internally using router.beforeEach
      function navigate(to, from) {
          let guards;
          const [leavingRecords, updatingRecords, enteringRecords,] = extractChangingRecords(to, from);
          // all components here have been resolved once because we are leaving
          guards = extractComponentsGuards(leavingRecords.reverse(), 'beforeRouteLeave', to, from);
          // leavingRecords is already reversed
          for (const record of leavingRecords) {
              record.leaveGuards.forEach(guard => {
                  guards.push(guardToPromiseFn(guard, to, from));
              });
          }
          const canceledNavigationCheck = checkCanceledNavigationAndReject.bind(null, to, from);
          guards.push(canceledNavigationCheck);
          // run the queue of per route beforeRouteLeave guards
          return (runGuardQueue(guards)
              .then(() => {
              // check global guards beforeEach
              guards = [];
              for (const guard of beforeGuards.list()) {
                  guards.push(guardToPromiseFn(guard, to, from));
              }
              guards.push(canceledNavigationCheck);
              return runGuardQueue(guards);
          })
              .then(() => {
              // check in components beforeRouteUpdate
              guards = extractComponentsGuards(updatingRecords, 'beforeRouteUpdate', to, from);
              for (const record of updatingRecords) {
                  record.updateGuards.forEach(guard => {
                      guards.push(guardToPromiseFn(guard, to, from));
                  });
              }
              guards.push(canceledNavigationCheck);
              // run the queue of per route beforeEnter guards
              return runGuardQueue(guards);
          })
              .then(() => {
              // check the route beforeEnter
              guards = [];
              for (const record of to.matched) {
                  // do not trigger beforeEnter on reused views
                  if (record.beforeEnter && from.matched.indexOf(record) < 0) {
                      if (Array.isArray(record.beforeEnter)) {
                          for (const beforeEnter of record.beforeEnter)
                              guards.push(guardToPromiseFn(beforeEnter, to, from));
                      }
                      else {
                          guards.push(guardToPromiseFn(record.beforeEnter, to, from));
                      }
                  }
              }
              guards.push(canceledNavigationCheck);
              // run the queue of per route beforeEnter guards
              return runGuardQueue(guards);
          })
              .then(() => {
              // NOTE: at this point to.matched is normalized and does not contain any () => Promise<Component>
              // clear existing enterCallbacks, these are added by extractComponentsGuards
              to.matched.forEach(record => (record.enterCallbacks = {}));
              // check in-component beforeRouteEnter
              guards = extractComponentsGuards(enteringRecords, 'beforeRouteEnter', to, from);
              guards.push(canceledNavigationCheck);
              // run the queue of per route beforeEnter guards
              return runGuardQueue(guards);
          })
              .then(() => {
              // check global guards beforeResolve
              guards = [];
              for (const guard of beforeResolveGuards.list()) {
                  guards.push(guardToPromiseFn(guard, to, from));
              }
              guards.push(canceledNavigationCheck);
              return runGuardQueue(guards);
          })
              // catch any navigation canceled
              .catch(err => isNavigationFailure(err, 8 /* NAVIGATION_CANCELLED */)
              ? err
              : Promise.reject(err)));
      }
      function triggerAfterEach(to, from, failure) {
          // navigation is confirmed, call afterGuards
          // TODO: wrap with error handlers
          for (const guard of afterGuards.list())
              guard(to, from, failure);
      }
      /**
       * - Cleans up any navigation guards
       * - Changes the url if necessary
       * - Calls the scrollBehavior
       */
      function finalizeNavigation(toLocation, from, isPush, replace, data) {
          // a more recent navigation took place
          const error = checkCanceledNavigation(toLocation, from);
          if (error)
              return error;
          // only consider as push if it's not the first navigation
          const isFirstNavigation = from === START_LOCATION_NORMALIZED;
          const state = !isBrowser ? {} : history.state;
          // change URL only if the user did a push/replace and if it's not the initial navigation because
          // it's just reflecting the url
          if (isPush) {
              // on the initial navigation, we want to reuse the scroll position from
              // history state if it exists
              if (replace || isFirstNavigation)
                  routerHistory.replace(toLocation.fullPath, assign({
                      scroll: isFirstNavigation && state && state.scroll,
                  }, data));
              else
                  routerHistory.push(toLocation.fullPath, data);
          }
          // accept current navigation
          currentRoute.value = toLocation;
          handleScroll(toLocation, from, isPush, isFirstNavigation);
          markAsReady();
      }
      let removeHistoryListener;
      // attach listener to history to trigger navigations
      function setupListeners() {
          removeHistoryListener = routerHistory.listen((to, _from, info) => {
              // cannot be a redirect route because it was in history
              let toLocation = resolve(to);
              // due to dynamic routing, and to hash history with manual navigation
              // (manually changing the url or calling history.hash = '#/somewhere'),
              // there could be a redirect record in history
              const shouldRedirect = handleRedirectRecord(toLocation);
              if (shouldRedirect) {
                  pushWithRedirect(assign(shouldRedirect, { replace: true }), toLocation).catch(noop$1);
                  return;
              }
              pendingLocation = toLocation;
              const from = currentRoute.value;
              // TODO: should be moved to web history?
              if (isBrowser) {
                  saveScrollPosition(getScrollKey(from.fullPath, info.delta), computeScrollPosition());
              }
              navigate(toLocation, from)
                  .catch((error) => {
                  if (isNavigationFailure(error, 4 /* NAVIGATION_ABORTED */ | 8 /* NAVIGATION_CANCELLED */)) {
                      return error;
                  }
                  if (isNavigationFailure(error, 2 /* NAVIGATION_GUARD_REDIRECT */)) {
                      // Here we could call if (info.delta) routerHistory.go(-info.delta,
                      // false) but this is bug prone as we have no way to wait the
                      // navigation to be finished before calling pushWithRedirect. Using
                      // a setTimeout of 16ms seems to work but there is not guarantee for
                      // it to work on every browser. So Instead we do not restore the
                      // history entry and trigger a new navigation as requested by the
                      // navigation guard.
                      // the error is already handled by router.push we just want to avoid
                      // logging the error
                      pushWithRedirect(error.to, toLocation
                      // avoid an uncaught rejection, let push call triggerError
                      ).catch(noop$1);
                      // avoid the then branch
                      return Promise.reject();
                  }
                  // do not restore history on unknown direction
                  if (info.delta)
                      routerHistory.go(-info.delta, false);
                  // unrecognized error, transfer to the global handler
                  return triggerError(error);
              })
                  .then((failure) => {
                  failure =
                      failure ||
                          finalizeNavigation(
                          // after navigation, all matched components are resolved
                          toLocation, from, false);
                  // revert the navigation
                  if (failure && info.delta)
                      routerHistory.go(-info.delta, false);
                  triggerAfterEach(toLocation, from, failure);
              })
                  .catch(noop$1);
          });
      }
      // Initialization and Errors
      let readyHandlers = useCallbacks();
      let errorHandlers = useCallbacks();
      let ready;
      /**
       * Trigger errorHandlers added via onError and throws the error as well
       * @param error - error to throw
       * @returns the error as a rejected promise
       */
      function triggerError(error) {
          markAsReady(error);
          errorHandlers.list().forEach(handler => handler(error));
          return Promise.reject(error);
      }
      function isReady() {
          if (ready && currentRoute.value !== START_LOCATION_NORMALIZED)
              return Promise.resolve();
          return new Promise((resolve, reject) => {
              readyHandlers.add([resolve, reject]);
          });
      }
      /**
       * Mark the router as ready, resolving the promised returned by isReady(). Can
       * only be called once, otherwise does nothing.
       * @param err - optional error
       */
      function markAsReady(err) {
          if (ready)
              return;
          ready = true;
          setupListeners();
          readyHandlers
              .list()
              .forEach(([resolve, reject]) => (err ? reject(err) : resolve()));
          readyHandlers.reset();
      }
      // Scroll behavior
      function handleScroll(to, from, isPush, isFirstNavigation) {
          const { scrollBehavior } = options;
          if (!isBrowser || !scrollBehavior)
              return Promise.resolve();
          let scrollPosition = (!isPush && getSavedScrollPosition(getScrollKey(to.fullPath, 0))) ||
              ((isFirstNavigation || !isPush) &&
                  history.state &&
                  history.state.scroll) ||
              null;
          return vue.nextTick()
              .then(() => scrollBehavior(to, from, scrollPosition))
              .then(position => position && scrollToPosition(position))
              .catch(triggerError);
      }
      const go = (delta) => routerHistory.go(delta);
      let started;
      const installedApps = new Set();
      const router = {
          currentRoute,
          addRoute,
          removeRoute,
          hasRoute,
          getRoutes,
          resolve,
          options,
          push,
          replace,
          go,
          back: () => go(-1),
          forward: () => go(1),
          beforeEach: beforeGuards.add,
          beforeResolve: beforeResolveGuards.add,
          afterEach: afterGuards.add,
          onError: errorHandlers.add,
          isReady,
          install(app) {
              const router = this;
              app.component('RouterLink', RouterLink);
              app.component('RouterView', RouterView);
              app.config.globalProperties.$router = router;
              Object.defineProperty(app.config.globalProperties, '$route', {
                  get: () => vue.unref(currentRoute),
              });
              // this initial navigation is only necessary on client, on server it doesn't
              // make sense because it will create an extra unnecessary navigation and could
              // lead to problems
              if (isBrowser &&
                  // used for the initial navigation client side to avoid pushing
                  // multiple times when the router is used in multiple apps
                  !started &&
                  currentRoute.value === START_LOCATION_NORMALIZED) {
                  // see above
                  started = true;
                  push(routerHistory.location).catch(err => {
                      warn('Unexpected error when starting the router:', err);
                  });
              }
              const reactiveRoute = {};
              for (let key in START_LOCATION_NORMALIZED) {
                  // @ts-ignore: the key matches
                  reactiveRoute[key] = vue.computed(() => currentRoute.value[key]);
              }
              app.provide(routerKey, router);
              app.provide(routeLocationKey, vue.reactive(reactiveRoute));
              app.provide(routerViewLocationKey, currentRoute);
              let unmountApp = app.unmount;
              installedApps.add(app);
              app.unmount = function () {
                  installedApps.delete(app);
                  if (installedApps.size < 1) {
                      removeHistoryListener();
                      currentRoute.value = START_LOCATION_NORMALIZED;
                      started = false;
                      ready = false;
                  }
                  unmountApp();
              };
              {
                  addDevtools(app, router, matcher);
              }
          },
      };
      return router;
  }
  function runGuardQueue(guards) {
      return guards.reduce((promise, guard) => promise.then(() => guard()), Promise.resolve());
  }
  function extractChangingRecords(to, from) {
      const leavingRecords = [];
      const updatingRecords = [];
      const enteringRecords = [];
      const len = Math.max(from.matched.length, to.matched.length);
      for (let i = 0; i < len; i++) {
          const recordFrom = from.matched[i];
          if (recordFrom) {
              if (to.matched.find(record => isSameRouteRecord(record, recordFrom)))
                  updatingRecords.push(recordFrom);
              else
                  leavingRecords.push(recordFrom);
          }
          const recordTo = to.matched[i];
          if (recordTo) {
              // the type doesn't matter because we are comparing per reference
              if (!from.matched.find(record => isSameRouteRecord(record, recordTo))) {
                  enteringRecords.push(recordTo);
              }
          }
      }
      return [leavingRecords, updatingRecords, enteringRecords];
  }

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
        this.$router.push({
          name: item.name
        });
      }
    }
  };

  const _withId$2$1 = /*#__PURE__*/vue.withScopeId("data-v-00020244");

  vue.pushScopeId("data-v-00020244");
  const _hoisted_1$2$1 = { class: "card" };
  const _hoisted_2$1$1 = { class: "card-title" };
  const _hoisted_3$1$1 = { class: "card-title-text" };
  const _hoisted_4$2 = { class: "item-list" };
  const _hoisted_5$1 = { class: "item-icon" };
  const _hoisted_6$1 = { class: "item-title" };
  vue.popScopeId();

  const render$2$1 = /*#__PURE__*/_withId$2$1((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$2$1, [
      vue.createVNode("div", _hoisted_2$1$1, [
        vue.createVNode("span", _hoisted_3$1$1, vue.toDisplayString($props.title), 1 /* TEXT */)
      ]),
      vue.createVNode("div", _hoisted_4$2, [
        (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.list, (item, index) => {
          return (vue.openBlock(), vue.createBlock("div", {
            class: "item",
            key: index,
            onClick: $event => ($options.handleClickItem(item))
          }, [
            vue.createVNode("div", _hoisted_5$1, [
              vue.createVNode("img", {
                class: "item-icon-image",
                src: item.icon || $data.defaultIcon
              }, null, 8 /* PROPS */, ["src"])
            ]),
            vue.createVNode("div", _hoisted_6$1, vue.toDisplayString(item.nameZh || '默认功能'), 1 /* TEXT */)
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
  const _hoisted_2$8 = { class: "version-text" };
  const _hoisted_3$7 = { class: "version-image" };
  vue.popScopeId();

  const render$1$1 = /*#__PURE__*/_withId$1$1((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$1$1, [
      vue.createVNode("div", null, [
        vue.createVNode("span", _hoisted_2$8, "当前版本：V" + vue.toDisplayString($props.version), 1 /* TEXT */)
      ]),
      vue.createVNode("div", _hoisted_3$7, [
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

  var script$a = {
    components: {
      TopBar: script$5$1,
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
    }
  };

  const _withId$6 = /*#__PURE__*/vue.withScopeId("data-v-47323bf2");

  vue.pushScopeId("data-v-47323bf2");
  const _hoisted_1$9 = { class: "index-container" };
  vue.popScopeId();

  const render$a = /*#__PURE__*/_withId$6((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_card = vue.resolveComponent("card");
    const _component_version_card = vue.resolveComponent("version-card");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$9, [
      (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($options.features, (item, index) => {
        return (vue.openBlock(), vue.createBlock(_component_card, {
          key: index,
          title: item.title,
          list: item.list
        }, null, 8 /* PROPS */, ["title", "list"]))
      }), 128 /* KEYED_FRAGMENT */)),
      vue.createVNode(_component_version_card, { version: $data.version }, null, 8 /* PROPS */, ["version"])
    ]))
  });

  var css_248z$a = ".index-container[data-v-47323bf2] {\n  background-color: #f5f6f7;\n}\n";
  styleInject$1(css_248z$a);

  script$a.render = render$a;
  script$a.__scopeId = "data-v-47323bf2";
  script$a.__file = "src/components/index.vue";

  const routes = [{
    path: '/',
    name: 'index',
    component: script$a
  }];

  function getRoutes(features){
    let routes = [];
    features.forEach(feature => {
      let {list, title:featureTitle} = feature;
      list.forEach(item => {
        // TODO 暂时只支持路由方式的插件
        let {name, title, component} = item;
        routes.push({
          path: `/${name}`,
          name: name,
          component: component.component || component,
          meta: {
            title: title,
            feature: featureTitle
          }
        });
      });
    });
    return routes
  }

  function getRouter(features){
    return createRouter({
      routes: [...routes, ...getRoutes(features)],
      history: createMemoryHistory()
    })
  }

  const noop$2 = () => {};

  class BasePlugin{
    type = ''
    name = ''
    nameZh = ''
    icon = ''
    component = null
    _onLoad = noop$2
    _onUnload = noop$2
    constructor(options){
      let {name, nameZh, icon, component, onLoad, onUnload} = options;
      this.name = name;
      this.nameZh = nameZh;
      this.icon = icon;
      this.component = component;
      this._onLoad = onLoad || noop$2;
      this._onUnload = onUnload || noop$2;
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

  var script$9 = {
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

  const _withId$5 = /*#__PURE__*/vue.withScopeId("data-v-9d220f86");

  vue.pushScopeId("data-v-9d220f86");
  const _hoisted_1$8 = { class: "tab-container" };
  const _hoisted_2$7 = { class: "tab-list" };
  const _hoisted_3$6 = { class: "tab-item-text" };
  vue.popScopeId();

  const render$9 = /*#__PURE__*/_withId$5((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$8, [
      vue.createVNode("div", _hoisted_2$7, [
        (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.tabs, (item, index) => {
          return (vue.openBlock(), vue.createBlock("div", {
            class: ["tab-item", $data.curIndex === index? 'tab-active': 'tab-default'],
            key: index,
            onClick: $event => ($options.handleClickTab(item, index))
          }, [
            vue.createVNode("span", _hoisted_3$6, vue.toDisplayString(item.name), 1 /* TEXT */)
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

  var css_248z$9 = ".tab-container[data-v-9d220f86] .tab-list[data-v-9d220f86] {\n  display: flex;\n  height: 38px;\n  justify-content: space-between;\n  align-items: center;\n  border: 1px solid #f5f6f7;\n}\n.tab-container[data-v-9d220f86] .tab-item[data-v-9d220f86] {\n  flex: 1;\n  height: 38px;\n  line-height: 38px;\n  text-align: center;\n}\n.tab-container[data-v-9d220f86] .tab-item-text[data-v-9d220f86] {\n  font-size: 16px;\n  color: #333333;\n}\n.tab-container[data-v-9d220f86] .tab-active[data-v-9d220f86] {\n  border-bottom: 1px solid #1485ee;\n}\n.tab-container[data-v-9d220f86] .tab-default[data-v-9d220f86] {\n  border: none;\n}\n";
  styleInject(css_248z$9);

  script$9.render = render$9;
  script$9.__scopeId = "data-v-9d220f86";
  script$9.__file = "src/plugins/console/console-tap.vue";

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
  var script$8 = {
    name: "Detail",
    components: {
      Detail: script$8
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

  const _withId$4 = /*#__PURE__*/vue.withScopeId("data-v-151438cc");

  const render$8 = /*#__PURE__*/_withId$4((_ctx, _cache, $props, $setup, $data, $options) => {
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

  var css_248z$8 = ".detail-container[data-v-151438cc] {\n  font-size: 12px;\n  margin-left: 24px;\n  position: relative;\n}\n.can-unfold[data-v-151438cc][data-v-151438cc]::before {\n  content: \"\";\n  width: 0;\n  height: 0;\n  border: 4px solid transparent;\n  position: absolute;\n  border-left-color: #333;\n  left: -12px;\n  top: 3px;\n}\n.unfolded[data-v-151438cc][data-v-151438cc]::before {\n  border: 4px solid transparent;\n  border-top-color: #333;\n  top: 6px;\n}\n";
  styleInject(css_248z$8);

  script$8.render = render$8;
  script$8.__scopeId = "data-v-151438cc";
  script$8.__file = "src/plugins/console/log-detail.vue";

  const DATATYPE_NOT_DISPLAY = ['Number', 'String', 'Boolean'];

  var script$7 = {
    components: {
      Detail: script$8
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

  const _hoisted_1$7 = { class: "log-ltem" };
  const _hoisted_2$6 = { key: 0 };

  function render$7(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_Detail = vue.resolveComponent("Detail");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$7, [
      vue.createVNode("div", {
        class: "log-preview",
        innerHTML: $options.logPreview,
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.toggleDetail && $options.toggleDetail(...args)))
      }, null, 8 /* PROPS */, ["innerHTML"]),
      ($data.showDetail && typeof $props.value === 'object')
        ? (vue.openBlock(), vue.createBlock("div", _hoisted_2$6, [
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

  var css_248z$7 = ".log-ltem {\n  padding: 5px;\n  border-top: 1px solid #eee;\n  text-align: left;\n  font-size: 12px;\n}\n.log-ltem:first-child {\n  border: none;\n}\n.log-preview .data-type {\n  margin-left: 5px;\n  margin-right: 5px;\n  font-style: italic;\n  font-weight: bold;\n  color: #aaa;\n}\n.log-preview .data-structure {\n  font-style: italic;\n}\n";
  styleInject(css_248z$7);

  script$7.render = render$7;
  script$7.__file = "src/plugins/console/log-item.vue";

  var script$6 = {
    components: {
      LogItem: script$7
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

  const _withId$3 = /*#__PURE__*/vue.withScopeId("data-v-722c5870");

  vue.pushScopeId("data-v-722c5870");
  const _hoisted_1$6 = { class: "log-container" };
  vue.popScopeId();

  const render$6 = /*#__PURE__*/_withId$3((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_log_item = vue.resolveComponent("log-item");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$6, [
      (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.logList, (log, index) => {
        return (vue.openBlock(), vue.createBlock(_component_log_item, {
          key: index,
          value: log.value,
          type: log.type
        }, null, 8 /* PROPS */, ["value", "type"]))
      }), 128 /* KEYED_FRAGMENT */))
    ]))
  });

  var css_248z$6 = ".tab-container[data-v-722c5870] .tab-list[data-v-722c5870] {\n  display: flex;\n  height: 38px;\n  justify-content: space-between;\n  align-items: center;\n  border: 1px solid #f5f6f7;\n}\n.tab-container[data-v-722c5870] .tab-item[data-v-722c5870] {\n  flex: 1;\n  height: 38px;\n  line-height: 38px;\n  text-align: center;\n}\n.tab-container[data-v-722c5870] .tab-item-text[data-v-722c5870] {\n  font-size: 16px;\n  color: #333333;\n}\n.tab-container[data-v-722c5870] .tab-active[data-v-722c5870] {\n  border-bottom: 1px solid #1485ee;\n}\n.tab-container[data-v-722c5870] .tab-default[data-v-722c5870] {\n  border: none;\n}\n";
  styleInject(css_248z$6);

  script$6.render = render$6;
  script$6.__scopeId = "data-v-722c5870";
  script$6.__file = "src/plugins/console/log-container.vue";

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

  var script$5 = {
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

  const _withId$2 = /*#__PURE__*/vue.withScopeId("data-v-f469c502");

  vue.pushScopeId("data-v-f469c502");
  const _hoisted_1$5 = { class: "operation" };
  const _hoisted_2$5 = { class: "input-wrapper" };
  const _hoisted_3$5 = /*#__PURE__*/vue.createVNode("span", null, "Excute", -1 /* HOISTED */);
  vue.popScopeId();

  const render$5 = /*#__PURE__*/_withId$2((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$5, [
      vue.createVNode("div", _hoisted_2$5, [
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
        _hoisted_3$5
      ])
    ]))
  });

  var css_248z$5 = ".operation[data-v-f469c502] {\n  height: 50px;\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n}\n.input-wrapper[data-v-f469c502] {\n  flex: 1;\n  height: 100%;\n}\n.input-wrapper[data-v-f469c502] .input[data-v-f469c502] {\n  height: 100%;\n  width: 100%;\n  outline: none;\n  border: none;\n  line-height: 100%;\n  padding: 0 10px;\n  font-size: 18px;\n}\n.button-wrapper[data-v-f469c502] {\n  height: 100%;\n  line-height: 100%;\n  margin-left: 10px;\n  padding: 0 10px;\n  border-left: 1px solid #f5f6f7;\n  display: flex;\n  align-items: center;\n}\n";
  styleInject(css_248z$5);

  script$5.render = render$5;
  script$5.__scopeId = "data-v-f469c502";
  script$5.__file = "src/plugins/console/op-command.vue";

  var script$4 = {
    components: {
      ConsoleTap: script$9,
      LogContainer: script$6,
      OperationCommand: script$5
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

  const _withId$1 = /*#__PURE__*/vue.withScopeId("data-v-35ae264e");

  vue.pushScopeId("data-v-35ae264e");
  const _hoisted_1$4 = { class: "console-container" };
  const _hoisted_2$4 = { class: "log-container" };
  const _hoisted_3$4 = { class: "info-container" };
  const _hoisted_4$1 = { class: "operation-container" };
  vue.popScopeId();

  const render$4 = /*#__PURE__*/_withId$1((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_console_tap = vue.resolveComponent("console-tap");
    const _component_log_container = vue.resolveComponent("log-container");
    const _component_operation_command = vue.resolveComponent("operation-command");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$4, [
      vue.createVNode(_component_console_tap, {
        tabs: $data.logTabs,
        onChangeTap: $options.handleChangeTab
      }, null, 8 /* PROPS */, ["tabs", "onChangeTap"]),
      vue.createVNode("div", _hoisted_2$4, [
        vue.createVNode("div", _hoisted_3$4, [
          vue.createVNode(_component_log_container, { logList: $options.curLogList }, null, 8 /* PROPS */, ["logList"])
        ]),
        vue.createVNode("div", _hoisted_4$1, [
          vue.createVNode(_component_operation_command)
        ])
      ])
    ]))
  });

  var css_248z$4 = ".console-container[data-v-35ae264e] {\n  display: flex;\n  flex-direction: column;\n  height: 100%;\n}\n.log-container[data-v-35ae264e] {\n  flex: 1;\n  display: flex;\n  flex-direction: column;\n  overflow: hidden;\n}\n.log-container[data-v-35ae264e] .info-container[data-v-35ae264e] {\n  flex: 1;\n  background-color: #ffffff;\n  border-bottom: 1px solid #f5f6f7;\n  overflow-y: scroll;\n}\n";
  styleInject(css_248z$4);

  script$4.render = render$4;
  script$4.__scopeId = "data-v-35ae264e";
  script$4.__file = "src/plugins/console/main.vue";

  var Console = new RouterPlugin({
    name: 'console',
    nameZh: '日志',
    component: script$4,
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/PbNXVyzTbq1618997544543.png',
    onLoad(){
      console.log('Load');
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

  var script$3 = {
    props: {
      title: String
    }
  };

  const _hoisted_1$3 = { class: "dokit-card" };
  const _hoisted_2$3 = { class: "dokit-card__header" };
  const _hoisted_3$3 = { class: "dokit-card__body" };

  function render$3(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$3, [
      vue.createVNode("div", _hoisted_2$3, vue.toDisplayString($props.title), 1 /* TEXT */),
      vue.createVNode("div", _hoisted_3$3, [
        vue.renderSlot(_ctx.$slots, "default")
      ])
    ]))
  }

  var css_248z$3 = ".dokit-card {\n  border-radius: 10px;\n  box-shadow: 0 8px 12px #ebedf0;\n  overflow: hidden;\n}\n.dokit-card .dokit-card__header {\n  background-color: #337cc4;\n  padding: 10px;\n  color: #fff;\n}\n.dokit-card .dokit-card__body {\n  padding: 10px;\n}\n";
  styleInject(css_248z$3);

  script$3.render = render$3;
  script$3.__file = "src/common/Card.vue";

  var script$2 = {
    components: {
      Card: script$3
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

  const _withId = /*#__PURE__*/vue.withScopeId("data-v-756cbe6c");

  vue.pushScopeId("data-v-756cbe6c");
  const _hoisted_1$2 = { class: "app-info-container" };
  const _hoisted_2$2 = { class: "info-wrapper" };
  const _hoisted_3$2 = { border: "1" };
  const _hoisted_4 = /*#__PURE__*/vue.createVNode("td", null, "UA", -1 /* HOISTED */);
  const _hoisted_5 = /*#__PURE__*/vue.createVNode("td", null, "URL", -1 /* HOISTED */);
  const _hoisted_6 = { class: "info-wrapper" };
  const _hoisted_7 = { border: "1" };
  const _hoisted_8 = /*#__PURE__*/vue.createVNode("td", null, "设备缩放比", -1 /* HOISTED */);
  const _hoisted_9 = /*#__PURE__*/vue.createVNode("td", null, "screen", -1 /* HOISTED */);
  const _hoisted_10 = /*#__PURE__*/vue.createVNode("td", null, "viewport", -1 /* HOISTED */);
  vue.popScopeId();

  const render$2 = /*#__PURE__*/_withId((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_Card = vue.resolveComponent("Card");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$2, [
      vue.createVNode("div", _hoisted_2$2, [
        vue.createVNode(_component_Card, { title: "Page Info" }, {
          default: _withId(() => [
            vue.createVNode("table", _hoisted_3$2, [
              vue.createVNode("tr", null, [
                _hoisted_4,
                vue.createVNode("td", null, vue.toDisplayString($data.ua), 1 /* TEXT */)
              ]),
              vue.createVNode("tr", null, [
                _hoisted_5,
                vue.createVNode("td", null, vue.toDisplayString($data.url), 1 /* TEXT */)
              ])
            ])
          ]),
          _: 1 /* STABLE */
        })
      ]),
      vue.createVNode("div", _hoisted_6, [
        vue.createVNode(_component_Card, { title: "Device Info" }, {
          default: _withId(() => [
            vue.createVNode("table", _hoisted_7, [
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

  var css_248z$2 = ".app-info-container[data-v-756cbe6c] {\n  font-size: 14px;\n  height: 100%;\n  overflow: hidden;\n}\n.info-wrapper[data-v-756cbe6c] {\n  margin: 20px 20px 0 20px;\n}\ntable[data-v-756cbe6c] {\n  border-color: #eee;\n  width: 100%;\n  border-collapse: collapse;\n  border-spacing: 0;\n}\ntr[data-v-756cbe6c] {\n  width: 100%;\n}\ntd[data-v-756cbe6c],\nth[data-v-756cbe6c] {\n  padding: 5px;\n}\n";
  styleInject(css_248z$2);

  script$2.render = render$2;
  script$2.__scopeId = "data-v-756cbe6c";
  script$2.__file = "src/plugins/app-info/ToolAppInfo.vue";

  var AppInfo = new RouterPlugin({
    nameZh: '应用信息',
    name: 'app-info',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: script$2
  });

  var script$1 = {
    
  };

  const _hoisted_1$1 = { class: "hello-world" };
  const _hoisted_2$1 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, "Hello Dokit", -1 /* HOISTED */);
  const _hoisted_3$1 = /*#__PURE__*/vue.createVNode("div", null, "Demo Plugin", -1 /* HOISTED */);

  function render$1(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$1, [
      _hoisted_2$1,
      _hoisted_3$1
    ]))
  }

  var css_248z$1 = "\n.hello-world{\n  padding:10px;\n  text-align: center;\n}\n";
  styleInject(css_248z$1);

  script$1.render = render$1;
  script$1.__file = "src/plugins/demo-plugin/ToolHelloWorld.vue";

  var DemoPlugin = new RouterPlugin({
    nameZh: '测试',
    name: 'test',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: script$1
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
    list: [Console, AppInfo, DemoPlugin, {
      nameZh: 'H5任意门',
      name: 'h5-door',
      icon: 'https://pt-starimg.didistatic.com/static/starimg/img/FHqpI3InaS1618997548865.png',
      component: AppInfo
    }]
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
