(function (vue) {
  'use strict';

  let x=!1;const N={name:"",opacity:1,left:"",top:"",safeBottom:0},y={mounted(t,n){t.config={...N,...n.value},t.dokitEntryLastX=9999,t.dokitEntryLastY=9999,t.style.position="fixed",t.style.opacity=t.config.opacity,t.dokitPositionLeft=function(t){let n=t.config.left||Math.round(window.innerWidth/2);return localStorage.getItem(`dokitPositionLeft_${t.config.name}`)?parseInt(localStorage.getItem(`dokitPositionLeft_${t.config.name}`)):n}(t),t.dokitPositionTop=function(t){let n=t.config.top||Math.round(window.innerHeight/2);return localStorage.getItem(`dokitPositionTop_${t.config.name}`)?parseInt(localStorage.getItem(`dokitPositionTop_${t.config.name}`)):n}(t),t.style.top=`${t.dokitPositionTop}px`,t.style.left=`${t.dokitPositionLeft}px`,F(t),t.ontouchstart=()=>{E(t);},t.ontouchmove=n=>{n.preventDefault(),b(t,n);},t.ontouchend=n=>{H(t);},t.onmousedown=n=>{n.preventDefault(),E(t),x=!0;},window.addEventListener("mousemove",(n=>{x&&b(t,n);})),window.addEventListener("mouseup",(n=>{x&&(H(t),x=!1);})),window.addEventListener("resize",(()=>{F(t);}));}};function E(t){t.style.opacity=1;}function b(t,n){let e=n.touches?n.touches[0]:n;if(9999===t.dokitEntryLastX)return t.dokitEntryLastX=e.clientX,void(t.dokitEntryLastY=e.clientY);t.dokitPositionTop+=e.clientY-t.dokitEntryLastY,t.dokitPositionLeft+=e.clientX-t.dokitEntryLastX,t.dokitEntryLastX=e.clientX,t.dokitEntryLastY=e.clientY,t.style.top=`${t.dokitPositionTop}px`,t.style.left=`${t.dokitPositionLeft}px`;}function H(t,n){setTimeout((()=>{F(t),t.config.name&&localStorage.setItem(`dokitPositionTop_${t.config.name}`,t.dokitPositionTop),t.config.name&&localStorage.setItem(`dokitPositionLeft_${t.config.name}`,t.dokitPositionLeft);}),100),t.dokitEntryLastX=9999,t.dokitEntryLastY=9999,t.style.opacity=t.config.opacity;}function F(t){t.dokitPositionLeft<0?(t.dokitPositionLeft=0,t.style.left=`${t.dokitPositionLeft}px`):t.dokitPositionLeft+t.getBoundingClientRect().width>window.innerWidth&&(t.dokitPositionLeft=window.innerWidth-t.getBoundingClientRect().width,t.style.left=`${t.dokitPositionLeft}px`),t.dokitPositionTop<0?(t.dokitPositionTop=0,t.style.top=`${t.dokitPositionTop}px`):t.dokitPositionTop+t.getBoundingClientRect().height+t.config.safeBottom>window.innerHeight&&(t.dokitPositionTop=window.innerHeight-t.getBoundingClientRect().height-t.config.safeBottom,t.style.top=`${t.dokitPositionTop}px`);}var M={props:{title:{default:"DoKit"},canBack:{default:!0}},data:()=>({icon:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAzCAMAAADIDVqJAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAXFQTFRFAAAANX7GNHzFM3zFM3zFNn3ENYDHNHzFM3zENIDHNH7INH3ENX3GNn7GM33ENYDFNYDGM4DINH7HM3zENH3HNHzENYDGM37FM33FNH3FNn3FNX7HN4DINHzEM33HNH7FNIDGNn7GM33ENX3FNXzHNoDENH7FNH3EM3zEN33INHzFNH3FM4DHNX3HNH3GNX7ENHzENHzFNYDFNH3FM33ENn3JM33GN4DINX7EM3zEM33GNH7FM33FM33FNIDFNH3GNX3KM37HNHzFN4DINX7FNH3EOHzHNH3GM3zFM4DGM33EM3zENHzENH3EM33FOoTFgID/NHzFOIDHM3zEM3zFNoPJM33FM33ENH3FNHzENYDL////NHzENH3FM4PFM3zFOYDGNH3FM3zFN3zINHzEM33FNoDJM3zFM33FNH3ENHzFM37FNH3ENH3FM33ENH7GNX7FM33ENH3FNH7FM37ENH7FM3zFNH3EM4jMM3zE////TdHL6gAAAHl0Uk5TAEPy+vA9RPb0QEX3P0fzPkg8Sfg7SjpL+fE5TThON082Ue81UjRT++4zVO0yVjFX/OwwWOsvWi5b6i1d/eksXitf6Cph5yli/ihk5ieh3x8CoiCg4CGf4Z3iIgGc4yOaJJnkJZjlJpaVk5KQj42LioiHhYSCgHOyD3AmCqsAAAABYktHRFt0vJU0AAAACXBIWXMAAABIAAAASABGyWs+AAABPUlEQVQ4y43UxVoCUBQE4Ctgd3cXKAYoKqioINiBrdjdXby9C+6c3ZzPWf+rE2MMS4bDmY4rk5qs7FQ6ObnU5OXDFFBTCFNUTE2JmFJqysqtcVRQUymmiprqGmtq66ipF9NATSOMq4maZidMCzWtMG3t1HSI6aSmq9sat4eaHpheLzV9YvqpGRi0xuenZghmOEDNiJhRasZggiFqxiesmQxTMyVmmpoZmEiUmlmYWJyauXmYBWoWYZaWqTEr1qRW1zhaT4jaUNQm1Na2onagdjW1B7V/wFXyEOroWFEnok4VdQZ1fqGoS6ira0XdpP4x+uStKL5EY+6gYpq6h4rEFfUgKqqoRyjlkI15ggqGFfUsKqSoFyjlTY15hfIFFPUmyq+odyilhIz5gHJ7FfUpyqOoLyilYo35/rFJ/Jo/DZ3bT7fEcIgAAAAldEVYdGRhdGU6Y3JlYXRlADIwMjEtMDQtMjFUMTc6MzI6MjgrMDg6MDBBnT5hAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDIxLTA0LTIxVDE3OjMyOjI4KzA4OjAwMMCG3QAAAABJRU5ErkJggg=="}),methods:{handleBackRoute(){this.$emit("back")||this.$router.back();}}};const I=vue.withScopeId("data-v-12d2fe44");vue.pushScopeId("data-v-12d2fe44");const C={class:"bar"},w=vue.createVNode("span",{class:"bar-back-btn"},"返回",-1),z={class:"bar-title"},P={class:"bar-title-text"};vue.popScopeId();const L=I(((t,n,r,c,l,p)=>(vue.openBlock(),vue.createBlock("div",C,[vue.withDirectives(vue.createVNode("div",{class:"bar-back",onClick:n[1]||(n[1]=(...t)=>p.handleBackRoute&&p.handleBackRoute(...t))},[vue.createVNode("img",{class:"bar-back-icon",src:l.icon},null,8,["src"]),w],512),[[vue.vShow,r.canBack]]),vue.createVNode("div",z,[vue.createVNode("span",P,vue.toDisplayString(r.title),1)])]))));function D(t,n){void 0===n&&(n={});var e=n.insertAt;if(t&&"undefined"!=typeof document){var o=document.head||document.getElementsByTagName("head")[0],i=document.createElement("style");i.type="text/css","top"===e&&o.firstChild?o.insertBefore(i,o.firstChild):o.appendChild(i),i.styleSheet?i.styleSheet.cssText=t:i.appendChild(document.createTextNode(t));}}D(".bar[data-v-12d2fe44] {\n  background-color: white;\n  height: 50px;\n  width: 100%;\n  display: flex;\n  justify-content: center;\n  align-items: center;\n  padding: 0 10px;\n  box-sizing: border-box;\n  position: relative;\n  border-radius: 10px 10px 0 0;\n}\n.bar-back[data-v-12d2fe44] {\n  position: absolute;\n  left: 10px;\n  display: flex;\n  flex-direction: row;\n  align-items: center;\n}\n.bar-back-icon[data-v-12d2fe44] {\n  display: inline-block;\n  height: 18px;\n}\n.bar-back-btn[data-v-12d2fe44] {\n  color: #337CC4;\n  font-size: 16px;\n  margin-left: 5px;\n}\n.bar-title-text[data-v-12d2fe44] {\n  color: #333333;\n  font-size: 20px;\n}\n.bar-other-text[data-v-12d2fe44] {\n  color: #666666;\n  font-size: 16px;\n}\n"),M.render=L,M.__scopeId="data-v-12d2fe44";var T={components:{TopBar:M},data:()=>({}),computed:{component(){return this.$route.component},title(){return this.$route.title||"Dokit"},canBack(){return "home"!==this.$route.name}},created(){console.log(this.$router);}};const B=vue.withScopeId("data-v-60e326b6");vue.pushScopeId("data-v-60e326b6");const X={class:"container"},R={class:"router-container"};vue.popScopeId();const Y=B(((t,n,i,s,d,r)=>{const u=vue.resolveComponent("top-bar");return vue.openBlock(),vue.createBlock("div",X,[vue.createVNode(u,{title:r.title,canBack:r.canBack},null,8,["title","canBack"]),vue.createVNode("div",R,[(vue.openBlock(),vue.createBlock(vue.KeepAlive,null,[(vue.openBlock(),vue.createBlock(vue.resolveDynamicComponent(r.component)))],1024))])])}));D(".container[data-v-60e326b6] {\n  position: absolute;\n  left: 0;\n  right: 0;\n  top: 100px;\n  bottom: 0;\n  background-color: #f5f6f7;\n  display: flex;\n  flex-direction: column;\n  z-index: 999;\n  border-radius: 10px 10px 0 0;\n}\n.router-container[data-v-60e326b6] {\n  margin-top: 5px;\n  background-color: white;\n  flex: 1;\n  overflow-y: scroll;\n}\n"),T.render=Y,T.__scopeId="data-v-60e326b6";var $={components:{},data:()=>({}),computed:{independPlugins(){return this.$store.state.independPlugins}},created(){console.log(this.$router);},methods:{toRaw:vue.toRaw}};const G=vue.withScopeId("data-v-17e44a68");vue.pushScopeId("data-v-17e44a68");const U={class:"container",style:{"z-index":"998"}};vue.popScopeId();const _=G(((t,n,i,a,s,d)=>(vue.openBlock(),vue.createBlock("div",U,[(vue.openBlock(!0),vue.createBlock(vue.Fragment,null,vue.renderList(d.independPlugins,(t=>(vue.openBlock(),vue.createBlock(vue.resolveDynamicComponent(d.toRaw(t.component)),{key:t.name})))),128))]))));D(".container[data-v-17e44a68] {\n  position: absolute;\n  left: 0;\n  right: 0;\n  bottom: 0;\n  top: 0;\n}\n"),$.render=_,$.__scopeId="data-v-17e44a68";const O=new class{constructor(t){let{state:n}=t;this.initData(n);}initData(t={}){this._state=vue.reactive({data:t});}get state(){return this._state.data}install(t){t.provide("store",this),t.config.globalProperties.$store=this;}}({state:{showContainer:!1,independPlugins:[],features:[]}});function q(){return O.state}function J(t){O.state.showContainer=t||!O.state.showContainer;}function j(t){O.state.independPlugins.findIndex((n=>n.name===t.name))>-1||O.state.independPlugins.push(t);}function W(t){let n=O.state.independPlugins.findIndex((n=>n.name===t));-1!==n&&O.state.independPlugins.splice(n,1);}var Q={components:{RouterContainer:T,IndependContainer:$},directives:{dragable:y},data:()=>({btnConfig:{name:"dokit_entry",opacity:.5,left:window.innerWidth-50,top:window.innerHeight-100,safeBottom:50}}),computed:{state(){return this.$store.state},showContainer(){return this.state.showContainer},independPlugins(){return this.$store.state.independPlugins}},methods:{toggleShowContainer(){J();}},created(){console.log("CurState: ",this.$store.state);}};const V=vue.withScopeId("data-v-6f4c1c67");vue.pushScopeId("data-v-6f4c1c67");const Z={class:"dokit-app"};vue.popScopeId();const K=V(((t,n,d,r,l,p)=>{const u=vue.resolveComponent("router-container"),f=vue.resolveComponent("independ-container"),m=vue.resolveDirective("dragable");return vue.openBlock(),vue.createBlock("div",Z,[vue.withDirectives(vue.createVNode("div",{class:"dokit-entry-btn",style:{"z-index":"10000"},onClick:n[1]||(n[1]=(...t)=>p.toggleShowContainer&&p.toggleShowContainer(...t))},null,512),[[m,l.btnConfig]]),vue.withDirectives(vue.createVNode("div",{class:"mask",onClick:n[2]||(n[2]=(...n)=>t.toggleContainer&&t.toggleContainer(...n))},null,512),[[vue.vShow,p.showContainer]]),vue.withDirectives(vue.createVNode(u,null,null,512),[[vue.vShow,p.showContainer]]),vue.withDirectives(vue.createVNode(f,null,null,512),[[vue.vShow,p.independPlugins.length]])])}));D(".dokit-app[data-v-6f4c1c67] {\n  font-family: Helvetica Neue, Helvetica, Arial, sans-serif;\n}\n.dokit-entry-btn[data-v-6f4c1c67] {\n  width: 50px;\n  height: 50px;\n  padding: 10px;\n  box-sizing: border-box;\n  background-image: url(//pt-starimg.didistatic.com/static/starimg/img/OzaetKDzHr1618905183992.png);\n  background-size: 50px;\n  background-position: center;\n  background-repeat: no-repeat;\n}\n.mask[data-v-6f4c1c67] {\n  position: absolute;\n  top: 0;\n  left: 0;\n  right: 0;\n  bottom: 0;\n  z-index: 3;\n  background-color: #333333;\n  opacity: 0.3;\n}\n"),Q.render=K,Q.__scopeId="data-v-6f4c1c67";const tt="load",nt="unload",et=function(t,n){t.forEach((t=>{let{list:e}=t;e.forEach((t=>{ot(t[n])&&t[n]();}));}));},ot=function(t){return "function"==typeof t};var it={props:{title:{default:"专区"},list:{default:[]}},data:()=>({defaultIcon:"https://pt-starimg.didistatic.com/static/starimg/img/FHqpI3InaS1618997548865.png"}),methods:{handleClickItem(t){this.$emit("handleClickItem",t);}}};const at=vue.withScopeId("data-v-227eea1f");vue.pushScopeId("data-v-227eea1f");const st={class:"card"},dt={class:"card-title"},rt={class:"card-title-text"},ct={class:"item-list"},lt={class:"item-icon"},pt={class:"item-title"};vue.popScopeId();const ut=at(((t,n,i,s,r,c)=>(vue.openBlock(),vue.createBlock("div",st,[vue.createVNode("div",dt,[vue.createVNode("span",rt,vue.toDisplayString(i.title),1)]),vue.createVNode("div",ct,[(vue.openBlock(!0),vue.createBlock(vue.Fragment,null,vue.renderList(i.list,((t,n)=>(vue.openBlock(),vue.createBlock("div",{class:"item",key:n,onClick:n=>c.handleClickItem(t)},[vue.createVNode("div",lt,[vue.createVNode("img",{class:"item-icon-image",src:t.icon||r.defaultIcon},null,8,["src"])]),vue.createVNode("div",pt,vue.toDisplayString(t.nameZh||"默认功能"),1)],8,["onClick"])))),128))])]))));D(".card[data-v-227eea1f] {\n  margin-bottom: 10px;\n  padding: 10px;\n  background-color: white;\n}\n.card-title-text[data-v-227eea1f] {\n  font-size: 16px;\n  color: #333333;\n}\n.item-list[data-v-227eea1f] {\n  display: flex;\n  flex-wrap: wrap;\n  margin-top: 5px;\n}\n.item[data-v-227eea1f] {\n  display: flex;\n  flex-direction: column;\n  align-items: center;\n  width: 25%;\n  margin-top: 5px;\n}\n.item .item-icon-image[data-v-227eea1f] {\n  width: 30px;\n}\n.item .item-title[data-v-227eea1f] {\n  font-size: 14px;\n  margin-top: 5px;\n}\n"),it.render=ut,it.__scopeId="data-v-227eea1f";var ft={props:{version:String},data:()=>({dokitIcon:"https://pt-starimg.didistatic.com/static/starimg/img/eM7MJKDqVG1618998466986.png"})};const mt=vue.withScopeId("data-v-3aae3480");vue.pushScopeId("data-v-3aae3480");const gt={class:"card version"},ht={class:"version-text"},vt={class:"version-image"};vue.popScopeId();const kt=mt(((t,n,i,s,r,c)=>(vue.openBlock(),vue.createBlock("div",gt,[vue.createVNode("div",null,[vue.createVNode("span",ht,"当前版本：V"+vue.toDisplayString(i.version),1)]),vue.createVNode("div",vt,[vue.createVNode("img",{class:"dokit-icon",src:r.dokitIcon},null,8,["src"])])]))));D(".card[data-v-3aae3480] {\n  padding: 10px;\n  background-color: white;\n}\n.version[data-v-3aae3480] {\n  padding: 20px 0;\n  text-align: center;\n}\n.version .version-text[data-v-3aae3480] {\n  font-size: 16px;\n  color: #999999;\n}\n.version .version-image[data-v-3aae3480] {\n  margin-top: 20px;\n}\n.version .dokit-icon[data-v-3aae3480] {\n  width: 150px;\n}\n"),ft.render=kt,ft.__scopeId="data-v-3aae3480";var At={components:{TopBar:M,Card:it,VersionCard:ft},data:()=>({version:"1.0.0"}),mounted(){},computed:{features(){return this.$store.state.features}},methods:{handleClickItem(t){switch(t.type){case"RouterPlugin":this.$router.push({name:t.name});break;case"IndependPlugin":j(t),this.$store.state.showContainer=!1;}}}};const xt=vue.withScopeId("data-v-c51fce46");vue.pushScopeId("data-v-c51fce46");const Nt={class:"index-container"};vue.popScopeId();const yt=xt(((t,n,i,s,d,r)=>{const l=vue.resolveComponent("card"),p=vue.resolveComponent("version-card");return vue.openBlock(),vue.createBlock("div",Nt,[(vue.openBlock(!0),vue.createBlock(vue.Fragment,null,vue.renderList(r.features,((t,n)=>(vue.openBlock(),vue.createBlock(l,{key:n,title:t.title,list:t.list,onHandleClickItem:r.handleClickItem},null,8,["title","list","onHandleClickItem"])))),128)),vue.createVNode(p,{version:d.version},null,8,["version"])])}));D(".index-container[data-v-c51fce46] {\n  background-color: #f5f6f7;\n}\n"),At.render=yt,At.__scopeId="data-v-c51fce46";const Et=[{name:"home",component:At}];function bt(t){let n=[];return t.forEach((t=>{let{list:e,title:o}=t;e.forEach((t=>{let{name:e,nameZh:i,component:a}=t;n.push({name:e,component:a,meta:{title:i,zone:o}});}));})),[...Et,...n]}function Ht(t){return function({routes:t}){const n=t,e=[],o=a("home"),i=vue.shallowRef(o);function a(t){return n.find((n=>n.name===t))}function s({name:t}){let n=a(t)||o;i.value=n;}return {currentRoute:i,addRoute:function(t){n.push(t);},removeRoute:function(t){let e=n.findIndex((n=>n.name===t));return -1!=e?n.splice(e,1):null},hasRoute:function(t){return -1!==n.findIndex((n=>n.name===t))},getRoutes:function(){return n},push:function({name:t}){e.push(t),s({name:t});},replace:function(t){e.pop(),e.push(t),s({name:t});},back:function(){let t=e[e.length-2];s({name:e[t]});},install:function(t){t.config.globalProperties.$router=this,Object.defineProperty(t.config.globalProperties,"$route",{get:()=>vue.unref(i)});}}}({routes:[...bt(t)]})}const Ft=()=>{};class Mt{type="";name="";nameZh="";icon="";component=null;_onLoad=Ft;_onUnload=Ft;constructor(t){let{name:n,nameZh:e,icon:o,component:i,onLoad:a,onUnload:s}=t;this.name=n,this.nameZh=e,this.icon=o,this.component=i,this._onLoad=a||Ft,this._onUnload=s||Ft;}load(){this._onLoad.call(this);}unload(){this._onUnload.call(this);}}class It extends Mt{type="RouterPlugin";constructor(t){super(t);}}class Ct extends Mt{type="IndependPlugin";constructor(t){super(t);}}class Pt{options=null;constructor(t){this.options=t;let n=vue.createApp(Q),{features:e}=t;n.use(Ht(e)),n.use(O),O.state.features=e,this.app=n,this.init(),this.onLoad();}onLoad(){et(this.options.features,tt);}onUnload(){et(this.options.features,nt);}init(){let t=document.createElement("div");t.id="dokit-root",document.documentElement.appendChild(t);let n=document.createElement("div");n.id="dokit-container",this.app.mount(n),t.appendChild(n);}}

  var script$i = {
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
  const _hoisted_1$f = { class: "tab-container" };
  const _hoisted_2$9 = { class: "tab-list" };
  const _hoisted_3$9 = { class: "tab-item-text" };
  vue.popScopeId();

  const render$i = /*#__PURE__*/_withId$8((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$f, [
      vue.createVNode("div", _hoisted_2$9, [
        (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.tabs, (item, index) => {
          return (vue.openBlock(), vue.createBlock("div", {
            class: ["tab-item", $data.curIndex === index? 'tab-active': 'tab-default'],
            key: index,
            onClick: $event => ($options.handleClickTab(item, index))
          }, [
            vue.createVNode("span", _hoisted_3$9, vue.toDisplayString(item.name), 1 /* TEXT */)
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

  script$i.render = render$i;
  script$i.__scopeId = "data-v-9d220f86";
  script$i.__file = "src/plugins/console/console-tap.vue";

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
  var script$h = {
    name: "Detail",
    components: {
      Detail: script$h
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

  const render$h = /*#__PURE__*/_withId$7((_ctx, _cache, $props, $setup, $data, $options) => {
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

  script$h.render = render$h;
  script$h.__scopeId = "data-v-151438cc";
  script$h.__file = "src/plugins/console/log-detail.vue";

  const DATATYPE_NOT_DISPLAY = ['Number', 'String', 'Boolean', 'Undefined', 'Null'];
  var script$g = {
    components: {
      Detail: script$h
    },
    props: {
      type: [Number],
      value: [String, Number, Object],
      logType: [String]
    },
    data () {
      return {
        showDetail: false
      }
    },
    computed: {
      logPreview () {
        let dataType = '';
        let func = null;
        let html = `<div>`;
        if (this.logType === 'log' || this.logType === 'info') {
          func = arg => {
            dataType = getDataType(arg);
            if (DATATYPE_NOT_DISPLAY.indexOf(dataType) === -1) {
              html += `<span class="data-type">${dataType}</span>`;
            }
            html += `<span class="data-structure">${getDataStructureStr(arg, true)}</span>`;
          };
          // this.value.forEach(arg => {
          //   dataType = getDataType(arg)
          //   if (DATATYPE_NOT_DISPLAY.indexOf(dataType) === -1) {
          //     html += `<span class="data-type">${dataType}</span>`
          //   }
          //   html += `<span class="data-structure">${getDataStructureStr(arg, true)}</span>`
          // });
        } else if (this.logType === 'error' || this.logType === 'warn') {
          func = arg => {
            if (arg.stack) {
              html += `<span style="white-space: pre-wrap;">${arg.stack}</span>`;
            } else {
              dataType = getDataType(arg);
              if (DATATYPE_NOT_DISPLAY.indexOf(dataType) === -1) {
                html += `<span class="data-type">${dataType}</span>`;
              }
              html += `<span class="data-structure">${getDataStructureStr(arg, true)}</span>`;
            }
          };
        } else ;
        
        this.value.forEach(func);

        html += `</div>`;
        return html
      },
      canShowDetail () {
        return this.showDetail 
          && typeof this.value === 'object'
          && !this.value.stack
      }
    },
    methods: {
      toggleDetail () {
        this.showDetail = !this.showDetail;
      }
    }
  };

  const _hoisted_1$e = { key: 0 };

  function render$g(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_Detail = vue.resolveComponent("Detail");

    return (vue.openBlock(), vue.createBlock("div", {
      class: ["log-ltem", $props.logType]
    }, [
      vue.createVNode("div", {
        class: "log-preview",
        innerHTML: $options.logPreview,
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.toggleDetail && $options.toggleDetail(...args)))
      }, null, 8 /* PROPS */, ["innerHTML"]),
      ($options.canShowDetail)
        ? (vue.openBlock(), vue.createBlock("div", _hoisted_1$e, [
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
    ], 2 /* CLASS */))
  }

  var css_248z$d = ".log-ltem {\n  padding: 5px;\n  padding-left: 20px;\n  border-top: 1px solid #eee;\n  text-align: left;\n  font-size: 12px;\n}\n.info {\n  background-color: #ECF1F7;\n  position: relative;\n}\n.info::before {\n  content: \"\";\n  background: url(\"https://pt-starimg.didistatic.com/static/starimg/img/M3nz7HYPH21621412737959.png\") no-repeat;\n  background-size: 10px 10px;\n  width: 10px;\n  height: 10px;\n  position: absolute;\n  top: 7px;\n  left: 8px;\n}\n.warn {\n  background-color: #FFFBE4;\n  color: #5C3C01;\n  position: relative;\n}\n.warn::before {\n  content: \"\";\n  background: url(\"https://pt-starimg.didistatic.com/static/starimg/img/39hzJzObhZ1621411397522.png\") no-repeat;\n  background-size: 10px 10px;\n  width: 10px;\n  height: 10px;\n  position: absolute;\n  top: 7px;\n  left: 8px;\n}\n.error {\n  background-color: #FEF0F0;\n  color: #FF161A;\n  position: relative;\n}\n.error::before {\n  content: \"\";\n  background: url(\"https://pt-starimg.didistatic.com/static/starimg/img/z6EndYs29d1621411397532.png\") no-repeat;\n  background-size: 10px 10px;\n  width: 10px;\n  height: 10px;\n  position: absolute;\n  top: 7px;\n  left: 8px;\n}\n.log-ltem:first-child {\n  border: none;\n}\n.log-preview .data-type {\n  margin-left: 5px;\n  margin-right: 5px;\n  font-style: italic;\n  font-weight: bold;\n  color: #aaa;\n}\n.log-preview .data-structure {\n  font-style: italic;\n}\n";
  styleInject(css_248z$d);

  script$g.render = render$g;
  script$g.__file = "src/plugins/console/log-item.vue";

  var script$f = {
    components: {
      LogItem: script$g
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
  const _hoisted_1$d = { class: "log-container" };
  vue.popScopeId();

  const render$f = /*#__PURE__*/_withId$6((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_log_item = vue.resolveComponent("log-item");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$d, [
      (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($props.logList, (log, index) => {
        return (vue.openBlock(), vue.createBlock(_component_log_item, {
          key: index,
          value: log.value,
          type: log.type,
          logType: log.name
        }, null, 8 /* PROPS */, ["value", "type", "logType"]))
      }), 128 /* KEYED_FRAGMENT */))
    ]))
  });

  var css_248z$c = ".tab-container[data-v-722c5870] .tab-list[data-v-722c5870] {\n  display: flex;\n  height: 38px;\n  justify-content: space-between;\n  align-items: center;\n  border: 1px solid #f5f6f7;\n}\n.tab-container[data-v-722c5870] .tab-item[data-v-722c5870] {\n  flex: 1;\n  height: 38px;\n  line-height: 38px;\n  text-align: center;\n}\n.tab-container[data-v-722c5870] .tab-item-text[data-v-722c5870] {\n  font-size: 16px;\n  color: #333333;\n}\n.tab-container[data-v-722c5870] .tab-active[data-v-722c5870] {\n  border-bottom: 1px solid #1485ee;\n}\n.tab-container[data-v-722c5870] .tab-default[data-v-722c5870] {\n  border: none;\n}\n";
  styleInject(css_248z$c);

  script$f.render = render$f;
  script$f.__scopeId = "data-v-722c5870";
  script$f.__file = "src/plugins/console/log-container.vue";

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

  var script$e = {
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
        try {
          let ret = excuteScript(this.command);
          console.log(ret);
        } catch (error) {
          console.error(error);
        }
        this.command = '';
      }
    }
  };

  const _withId$5 = /*#__PURE__*/vue.withScopeId("data-v-f469c502");

  vue.pushScopeId("data-v-f469c502");
  const _hoisted_1$c = { class: "operation" };
  const _hoisted_2$8 = { class: "input-wrapper" };
  const _hoisted_3$8 = /*#__PURE__*/vue.createVNode("span", null, "Excute", -1 /* HOISTED */);
  vue.popScopeId();

  const render$e = /*#__PURE__*/_withId$5((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$c, [
      vue.createVNode("div", _hoisted_2$8, [
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
        _hoisted_3$8
      ])
    ]))
  });

  var css_248z$b = ".operation[data-v-f469c502] {\n  height: 50px;\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n}\n.input-wrapper[data-v-f469c502] {\n  flex: 1;\n  height: 100%;\n}\n.input-wrapper[data-v-f469c502] .input[data-v-f469c502] {\n  height: 100%;\n  width: 100%;\n  outline: none;\n  border: none;\n  line-height: 100%;\n  padding: 0 10px;\n  font-size: 18px;\n}\n.button-wrapper[data-v-f469c502] {\n  height: 100%;\n  line-height: 100%;\n  margin-left: 10px;\n  padding: 0 10px;\n  border-left: 1px solid #f5f6f7;\n  display: flex;\n  align-items: center;\n}\n";
  styleInject(css_248z$b);

  script$e.render = render$e;
  script$e.__scopeId = "data-v-f469c502";
  script$e.__file = "src/plugins/console/op-command.vue";

  var script$d = {
    components: {
      ConsoleTap: script$i,
      LogContainer: script$f,
      OperationCommand: script$e
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
  const _hoisted_1$b = { class: "console-container" };
  const _hoisted_2$7 = { class: "log-container" };
  const _hoisted_3$7 = { class: "info-container" };
  const _hoisted_4$2 = { class: "operation-container" };
  vue.popScopeId();

  const render$d = /*#__PURE__*/_withId$4((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_console_tap = vue.resolveComponent("console-tap");
    const _component_log_container = vue.resolveComponent("log-container");
    const _component_operation_command = vue.resolveComponent("operation-command");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$b, [
      vue.createVNode(_component_console_tap, {
        tabs: $data.logTabs,
        onChangeTap: $options.handleChangeTab
      }, null, 8 /* PROPS */, ["tabs", "onChangeTap"]),
      vue.createVNode("div", _hoisted_2$7, [
        vue.createVNode("div", _hoisted_3$7, [
          vue.createVNode(_component_log_container, { logList: $options.curLogList }, null, 8 /* PROPS */, ["logList"])
        ]),
        vue.createVNode("div", _hoisted_4$2, [
          vue.createVNode(_component_operation_command)
        ])
      ])
    ]))
  });

  var css_248z$a = ".console-container[data-v-35ae264e] {\n  display: flex;\n  flex-direction: column;\n  height: 100%;\n}\n.log-container[data-v-35ae264e] {\n  flex: 1;\n  display: flex;\n  flex-direction: column;\n  overflow: hidden;\n}\n.log-container[data-v-35ae264e] .info-container[data-v-35ae264e] {\n  flex: 1;\n  background-color: #ffffff;\n  border-bottom: 1px solid #f5f6f7;\n  overflow-y: scroll;\n}\n";
  styleInject(css_248z$a);

  script$d.render = render$d;
  script$d.__scopeId = "data-v-35ae264e";
  script$d.__file = "src/plugins/console/main.vue";

  var Console = new It({
    name: 'console',
    nameZh: '日志',
    component: script$d,
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/PbNXVyzTbq1618997544543.png',
    onLoad(){
      console.log('Load');
      overrideConsole(({name, type, value}) => {
        let state = q();
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

  var script$c = {
    props: {
      title: String
    }
  };

  const _hoisted_1$a = { class: "dokit-card" };
  const _hoisted_2$6 = { class: "dokit-card__header" };
  const _hoisted_3$6 = { class: "dokit-card__body" };

  function render$c(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$a, [
      vue.createVNode("div", _hoisted_2$6, vue.toDisplayString($props.title), 1 /* TEXT */),
      vue.createVNode("div", _hoisted_3$6, [
        vue.renderSlot(_ctx.$slots, "default")
      ])
    ]))
  }

  var css_248z$9 = ".dokit-card {\n  background-color: #d9e1e8;\n  border-radius: 5px;\n  box-shadow: 0 8px 12px #ebedf0;\n  overflow: hidden;\n}\n.dokit-card .dokit-card__header {\n  background-color: #2b90d9;\n  padding: 5px;\n  color: #fff;\n  font-weight: bold;\n  font-style: italic;\n}\n.dokit-card .dokit-card__body {\n  padding: 5px;\n}\n";
  styleInject(css_248z$9);

  script$c.render = render$c;
  script$c.__file = "src/common/Card.vue";

  var script$b = {
    components: {
      Card: script$c
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
  const _hoisted_1$9 = { class: "app-info-container" };
  const _hoisted_2$5 = { class: "info-wrapper" };
  const _hoisted_3$5 = /*#__PURE__*/vue.createVNode("td", { class: "key" }, "UA", -1 /* HOISTED */);
  const _hoisted_4$1 = { class: "value" };
  const _hoisted_5$1 = /*#__PURE__*/vue.createVNode("td", { class: "key" }, "URL", -1 /* HOISTED */);
  const _hoisted_6$1 = { class: "value" };
  const _hoisted_7$1 = {
    class: "info-wrapper",
    style: {"margin-top":"20px"}
  };
  const _hoisted_8$1 = /*#__PURE__*/vue.createVNode("td", { class: "key" }, "设备缩放比", -1 /* HOISTED */);
  const _hoisted_9$1 = { class: "value" };
  const _hoisted_10$1 = /*#__PURE__*/vue.createVNode("td", { class: "key" }, "screen", -1 /* HOISTED */);
  const _hoisted_11$1 = /*#__PURE__*/vue.createVNode("td", { class: "key" }, "viewport", -1 /* HOISTED */);
  vue.popScopeId();

  const render$b = /*#__PURE__*/_withId$3((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_Card = vue.resolveComponent("Card");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$9, [
      vue.createVNode("div", _hoisted_2$5, [
        vue.createVNode(_component_Card, { title: "Page Info" }, {
          default: _withId$3(() => [
            vue.createVNode("table", null, [
              vue.createVNode("tr", null, [
                _hoisted_3$5,
                vue.createVNode("td", _hoisted_4$1, vue.toDisplayString($data.ua), 1 /* TEXT */)
              ]),
              vue.createVNode("tr", null, [
                _hoisted_5$1,
                vue.createVNode("td", _hoisted_6$1, vue.toDisplayString($data.url), 1 /* TEXT */)
              ])
            ])
          ]),
          _: 1 /* STABLE */
        })
      ]),
      vue.createVNode("div", _hoisted_7$1, [
        vue.createVNode(_component_Card, { title: "Device Info" }, {
          default: _withId$3(() => [
            vue.createVNode("table", null, [
              vue.createVNode("tr", null, [
                _hoisted_8$1,
                vue.createVNode("td", _hoisted_9$1, vue.toDisplayString($data.ratio), 1 /* TEXT */)
              ]),
              vue.createVNode("tr", null, [
                _hoisted_10$1,
                vue.createVNode("td", null, vue.toDisplayString($data.screen.width) + " X " + vue.toDisplayString($data.screen.height), 1 /* TEXT */)
              ]),
              vue.createVNode("tr", null, [
                _hoisted_11$1,
                vue.createVNode("td", null, vue.toDisplayString($data.viewport.width) + " X " + vue.toDisplayString($data.viewport.height), 1 /* TEXT */)
              ])
            ])
          ]),
          _: 1 /* STABLE */
        })
      ])
    ]))
  });

  var css_248z$8 = ".app-info-container[data-v-756cbe6c] {\n  font-size: 14px;\n  height: 100%;\n  overflow: hidden;\n}\n.info-wrapper[data-v-756cbe6c] {\n  margin: 5px 5px 0 5px;\n}\n.info-wrapper[data-v-756cbe6c] .key[data-v-756cbe6c] {\n  font-weight: bold;\n}\ntable[data-v-756cbe6c] {\n  border-color: #eee;\n  width: 100%;\n  border-collapse: collapse;\n  border-spacing: 0;\n}\ntr[data-v-756cbe6c] {\n  width: 100%;\n}\ntd[data-v-756cbe6c],\nth[data-v-756cbe6c] {\n  padding: 5px;\n}\n";
  styleInject(css_248z$8);

  script$b.render = render$b;
  script$b.__scopeId = "data-v-756cbe6c";
  script$b.__file = "src/plugins/app-info/ToolAppInfo.vue";

  var AppInfo = new It({
    nameZh: '应用信息',
    name: 'app-info',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: script$b
  });

  var script$a = {
    props: {
      title: {
        default: "",
      },
      infoMap: {
        default: {},
      },
    },
    data() {
      return {
        keyword: "",
      };
    },
    computed: {
      filteredMap() {
        if (this.keyword) {
          let map = Object.create({});
          for (const key in this.infoMap) {
            if (Object.hasOwnProperty.call(this.infoMap, key)) {
              if(this.infoMap[key].indexOf(this.keyword) > -1 || key.indexOf(this.keyword) > -1) {
                map[key] = this.infoMap[key];
              }
            }
          }
          return map
        } else {
          return this.infoMap
        }
        
      }
    },
    methods: {
      removeItem(key) {
        this.$emit("removeItem", key);
      },
      openPrompt() {
        this.keyword = window.prompt(
          "请输入过滤关键词",
          this.keyword ? this.keyword : ""
        );
      },
      clearAll() {
        if (window.confirm(`将清空所有${this.title}数据，是否确认清空？`)) {
          this.$emit("clear");
        }
      },
      refresh() {
        console.log('refresh');
        this.$emit("refresh");
      },
    },
  };

  const _hoisted_1$8 = { class: "info-card" };
  const _hoisted_2$4 = { class: "info-card-header" };
  const _hoisted_3$4 = { class: "info-card-header__title" };
  const _hoisted_4 = { class: "info-card-header__opt" };
  const _hoisted_5 = { class: "filter-text" };
  const _hoisted_6 = { class: "info-card-body" };
  const _hoisted_7 = { class: "info-key" };
  const _hoisted_8 = { class: "info-value" };
  const _hoisted_9 = { class: "info-opt" };
  const _hoisted_10 = { class: "empty" };
  const _hoisted_11 = /*#__PURE__*/vue.createVNode("span", null, "empty", -1 /* HOISTED */);

  function render$a(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$8, [
      vue.createVNode("div", _hoisted_2$4, [
        vue.createVNode("div", _hoisted_3$4, vue.toDisplayString($props.title), 1 /* TEXT */),
        vue.createVNode("div", _hoisted_4, [
          vue.createVNode("div", {
            class: ["filter-box", $data.keyword ? 'filter-box-actived' : '']
          }, [
            vue.createVNode("span", _hoisted_5, vue.toDisplayString($data.keyword), 1 /* TEXT */),
            vue.createVNode("span", {
              class: "filter opt-icon",
              onClick: _cache[1] || (_cache[1] = (...args) => ($options.openPrompt && $options.openPrompt(...args)))
            })
          ], 2 /* CLASS */),
          vue.createVNode("span", {
            class: "clear-all opt-icon",
            onClick: _cache[2] || (_cache[2] = (...args) => ($options.clearAll && $options.clearAll(...args)))
          }),
          vue.createVNode("span", {
            class: "refresh opt-icon",
            onClick: _cache[3] || (_cache[3] = (...args) => ($options.refresh && $options.refresh(...args)))
          })
        ])
      ]),
      vue.createVNode("div", _hoisted_6, [
        vue.withDirectives(vue.createVNode("table", null, [
          vue.createVNode("tbody", null, [
            (vue.openBlock(true), vue.createBlock(vue.Fragment, null, vue.renderList($options.filteredMap, (value, key) => {
              return (vue.openBlock(), vue.createBlock("tr", {
                class: "",
                key: key
              }, [
                vue.createVNode("td", _hoisted_7, vue.toDisplayString(key), 1 /* TEXT */),
                vue.createVNode("td", _hoisted_8, vue.toDisplayString(value), 1 /* TEXT */),
                vue.createVNode("td", _hoisted_9, [
                  vue.createVNode("span", {
                    class: "info-delete",
                    onClick: $event => ($options.removeItem(key))
                  }, null, 8 /* PROPS */, ["onClick"])
                ])
              ]))
            }), 128 /* KEYED_FRAGMENT */))
          ])
        ], 512 /* NEED_PATCH */), [
          [vue.vShow, Object.keys($options.filteredMap).length]
        ]),
        vue.withDirectives(vue.createVNode("div", _hoisted_10, [
          _hoisted_11
        ], 512 /* NEED_PATCH */), [
          [vue.vShow, Object.keys($options.filteredMap).length === 0]
        ])
      ])
    ]))
  }

  var css_248z$7 = ".info-card {\n  border-radius: 5px;\n  background-color: #d9e1e8;\n  overflow: hidden;\n}\n.info-card-header {\n  border-bottom: 1px solid #eeeeee;\n  background-color: #2b90d9;\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n  color: white;\n}\n.info-card-header .info-card-header__title {\n  padding: 5px;\n  font-size: 14px;\n  font-weight: bold;\n  font-style: italic;\n}\n.info-card-header .info-card-header__opt {\n  display: flex;\n}\n.info-card-header .info-card-header__opt .opt-icon {\n  display: inline-block;\n  width: 15px;\n  height: 15px;\n  background-size: 15px;\n  background-repeat: no-repeat;\n  margin: 0 10px;\n}\n.info-card-header .info-card-header__opt .refresh {\n  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAr5QTFRFAAAA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////0dICkAAAAOl0Uk5TAAcuVXuiwcjQ2N/n7/b+8eXLv7KlmYx/USMgYJO54PXHaz4QndnhqghEjsqpWRR9zGUYZ7azVDbwxGY9oNZ4GajoiStMr/qS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');\n}\n.info-card-header .info-card-header__opt .clear-all {\n  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAvRQTFRFAAAA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////ScVxIwAAAPt0Uk5TAB41BcjfTK71YpV5fJCmE0m9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');\n}\n.info-card-header .info-card-header__opt .filter-box {\n  padding-left: 10px;\n  display: inline-flex;\n  justify-content: space-between;\n  align-items: center;\n}\n.info-card-header .info-card-header__opt .filter-box .filter-text {\n  font-size: 12px;\n}\n.info-card-header .info-card-header__opt .filter-box-actived {\n  border-radius: 10px;\n  border: 1px solid #d9e1e8;\n}\n.info-card-header .info-card-header__opt .filter {\n  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAspQTFRFAAAA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////17LOWQAAAO10Uk5TADFiiK2/n3ZVFBCE99pmUta5KkH11DDhKbWsWTPFjlHoC387avCa8sDVtqYv4JfQozrroEVUXZ1P6hsS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');\n}\n.info-card-body table {\n  width: 100%;\n}\n.info-card-body td {\n  padding: 5px;\n  font-size: 12px;\n}\n.info-card-body .empty {\n  padding: 10px;\n  text-align: center;\n  line-height: 20px;\n  color: #9baec8;\n  font-weight: bold;\n}\n.info-card-body .info-key {\n  font-weight: bolder;\n  max-width: 150px;\n  overflow-x: scroll;\n}\n.info-card-body .info-opt {\n  width: 15px;\n}\n.info-card-body .info-opt span {\n  display: inline-block;\n  width: 15px;\n  height: 15px;\n  background-image: url(\"data:image/png;base64,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\");\n  background-size: 15px;\n  background-repeat: no-repeat;\n}\n";
  styleInject(css_248z$7);

  script$a.render = render$a;
  script$a.__file = "src/common/info-card.vue";

  const overrideLocalStorage = function(callback) {
    const originSetItem = localStorage.setItem.bind(localStorage);
    localStorage.setItem = function (key, value) {
      // if (!isStr(key) || !isStr(value)) return;
      originSetItem(key, value);
      callback({type: 'setItem'});
    };

    const originRemoveItem = localStorage.removeItem.bind(localStorage);
    localStorage.removeItem = function (key) {
      originRemoveItem(key);
      callback({type: 'removeItem'});
    };

    const originClear = localStorage.clear.bind(localStorage);
    localStorage.clear = function (key) {
      originClear();
      callback({type: 'clear'});
    };
  };

  const overrideSessionStorage = function(callback) {
    const originSetItem = sessionStorage.setItem.bind(sessionStorage);
    sessionStorage.setItem = function (key, value) {
      // if (!isStr(key) || !isStr(value)) return;
      originSetItem(key, value);
      callback({type: 'setItem'});
    };

    const originRemoveItem = sessionStorage.removeItem.bind(sessionStorage);
    sessionStorage.removeItem = function (key) {
      originRemoveItem(key);
      callback({type: 'removeItem'});
    };

    const originClear = sessionStorage.clear.bind(sessionStorage);
    sessionStorage.clear = function (key) {
      originClear();
      callback({type: 'clear'});
    };
  };

  const clearCookie = function () {
    let cookieMap = getCookieMap();
    for (const key in cookieMap) {
      if (cookieMap.hasOwnProperty.call(cookieMap, key)) {
        removeCookieItem(key);
      }
    }
  };

  const removeCookieItem = function (key) {
    document.cookie = encodeURIComponent(key) + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT";
  };

  const getCookieMap = function (params) {
    const cookieMap = Object.create({});
    const cookie = document.cookie;
    if (cookie.trim() !== '') {
      cookie.split(';').forEach(ele => {
        ele = ele.split('=');
        const key = ele.shift().trim();
        ele = decodeURIComponent(ele.join('='));
        cookieMap[key] = ele;
      });
    }

    return cookieMap;
  };

  var script$9 = {
    components: {
      InfoCard: script$a
    },
    data () {
      return {
        storageMap: {}
      }
    },
    created() {
      overrideLocalStorage(() => {
        this.updateList();
      });
      this.updateList();
    },
    methods: {
      updateList() {
        let storageMap = {...window.localStorage};
        // 有一些属性不需要展示
        for (const key in storageMap) {
          if (Object.hasOwnProperty.call(storageMap, key)) {
            if (~key.indexOf('dokit') || typeof storageMap[key] !== 'string') delete storageMap[key];
          }
        }
        this.storageMap = storageMap;
      },
      removeItem(key) {
        window.localStorage.removeItem(key);
      },
      clear() {
        window.localStorage.clear();
      }
    },
  };

  function render$9(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_InfoCard = vue.resolveComponent("InfoCard");

    return (vue.openBlock(), vue.createBlock("div", null, [
      vue.createVNode(_component_InfoCard, {
        infoMap: $data.storageMap,
        title: "localStorage",
        onRefresh: $options.updateList,
        onClear: $options.clear,
        onRemoveItem: $options.removeItem
      }, null, 8 /* PROPS */, ["infoMap", "onRefresh", "onClear", "onRemoveItem"])
    ]))
  }

  script$9.render = render$9;
  script$9.__file = "src/plugins/storage/local-storage.vue";

  var script$8 = {
    components: {
      InfoCard: script$a
    },
    data () {
      return {
        storageMap: {}
      }
    },
    created() {
      overrideSessionStorage(() => {
        this.updateList();
      });
      this.updateList();
    },
    methods: {
      updateList() {
        let storageMap = {...window.sessionStorage};
        // 有一些属性不需要展示
        for (const key in storageMap) {
          if (Object.hasOwnProperty.call(storageMap, key)) {
            if (~key.indexOf('dokit') || typeof storageMap[key] !== 'string') delete storageMap[key];
          }
        }
        this.storageMap = storageMap;
      },
      removeItem(key) {
        window.sessionStorage.removeItem(key);
      },
      clear() {
        window.sessionStorage.clear();
      }
    },
  };

  const _hoisted_1$7 = { style: {"margin-top":"20px"} };

  function render$8(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_InfoCard = vue.resolveComponent("InfoCard");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$7, [
      vue.createVNode(_component_InfoCard, {
        infoMap: $data.storageMap,
        title: "sessionStorage",
        onRefresh: $options.updateList,
        onClear: $options.clear,
        onRemoveItem: $options.removeItem
      }, null, 8 /* PROPS */, ["infoMap", "onRefresh", "onClear", "onRemoveItem"])
    ]))
  }

  script$8.render = render$8;
  script$8.__file = "src/plugins/storage/session-storage.vue";

  var script$7 = {
    components: {
      InfoCard: script$a
    },
    data () {
      return {
        storageMap: {}
      }
    },
    created() {
      this.updateList();
    },
    methods: {
      updateList() {
        this.storageMap = getCookieMap();
      },
      removeItem(key) {
        removeCookieItem(key);
        this.updateList();
      },
      clear() {
        clearCookie();
        this.updateList();
      }
    },
  };

  const _hoisted_1$6 = { style: {"margin-top":"20px"} };

  function render$7(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_InfoCard = vue.resolveComponent("InfoCard");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$6, [
      vue.createVNode(_component_InfoCard, {
        infoMap: $data.storageMap,
        title: "Cookie",
        onRefresh: $options.updateList,
        onClear: $options.clear,
        onRemoveItem: $options.removeItem
      }, null, 8 /* PROPS */, ["infoMap", "onRefresh", "onClear", "onRemoveItem"])
    ]))
  }

  script$7.render = render$7;
  script$7.__file = "src/plugins/storage/cookie.vue";

  var script$6 = {
    components: {
      localStorage: script$9,
      sessionStorage: script$8,
      cookie: script$7
    }
  };

  const _hoisted_1$5 = { class: "storage-plugin" };

  function render$6(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_localStorage = vue.resolveComponent("localStorage");
    const _component_sessionStorage = vue.resolveComponent("sessionStorage");
    const _component_cookie = vue.resolveComponent("cookie");

    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$5, [
      vue.createVNode(_component_localStorage),
      vue.createVNode(_component_sessionStorage),
      vue.createVNode(_component_cookie)
    ]))
  }

  var css_248z$6 = "\n.storage-plugin{\n  padding: 5px;\n}\n";
  styleInject(css_248z$6);

  script$6.render = render$6;
  script$6.__file = "src/plugins/storage/main.vue";

  var Storage = new It({
    name: 'Storage',
    nameZh: '存储',
    component: script$6,
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/LM74BpA9bS1621926286444.png',
    onLoad(){},
    onUnload(){}
  });

  var script$5 = {
    
  };

  const _hoisted_1$4 = { class: "hello-world" };
  const _hoisted_2$3 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, "Hello Dokit", -1 /* HOISTED */);
  const _hoisted_3$3 = /*#__PURE__*/vue.createVNode("div", null, "Demo Plugin", -1 /* HOISTED */);

  function render$5(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$4, [
      _hoisted_2$3,
      _hoisted_3$3
    ]))
  }

  var css_248z$5 = "\n.hello-world{\n  padding:10px;\n  text-align: center;\n}\n";
  styleInject(css_248z$5);

  script$5.render = render$5;
  script$5.__file = "src/plugins/demo-plugin/ToolHelloWorld.vue";

  var DemoPlugin = new It({
    nameZh: '测试',
    name: 'test',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/6WONqJCVks1621926657356.png',
    component: script$5
  });

  /**
   * 拖拽指令 v-dragable
   * 减少外部依赖
   * 默认使用v-dragable
   * 也接受传入一个config对象 v-dragable="config"
   * config支持 name opacity left top safeBottom 等属性
  */
  const INIT_VALUE = 9999;
  // const SAFE_BOTTOM = 50 // 底部防误触

  let MOUSE_DOWN_FLAG = false;

  const DEFAULT_EL_CONF = {
    name: '',         // 名称 用于存储位置storage的标识，没有则不存储
    opacity: 1,       // 默认透明度
    left: '',         // 初始位置, 没有则居中
    top: '',          // 初始位置, 没有则居中
    safeBottom: 0
  };

  // TODO 拖拽事件兼容 Pc处理
  // TODO 默认初始位置为右下角
  const dragable = {
    mounted (el, binding) {
      el.config = {
        ...DEFAULT_EL_CONF,
        ...binding.value
      };
      // 初始化变量
      el.dokitEntryLastX = INIT_VALUE;
      el.dokitEntryLastY = INIT_VALUE;
      // 初始化样式
      el.style.position = 'fixed';
      el.style.opacity = el.config.opacity;
      el.dokitPositionLeft = getDefaultX(el);
      el.dokitPositionTop = getDefaultY(el);
      el.style.top = `${el.dokitPositionTop}px`;
      el.style.left = `${el.dokitPositionLeft}px`;

      adjustPosition(el);

      // 触摸事件监听
      el.ontouchstart = () => {
        moveStart(el);
      };
      el.ontouchmove = (e) => {
        e.preventDefault();
        moving(el, e);
      };
      el.ontouchend = (e) => {
        moveEnd(el);
      };
      // PC鼠标事件
      el.onmousedown = (e) => {
        e.preventDefault();
        moveStart(el);
        MOUSE_DOWN_FLAG = true;
      };

      window.addEventListener('mousemove', (e)=> {
        if (MOUSE_DOWN_FLAG) moving(el, e);
      });

      window.addEventListener('mouseup', (e)=> {
        if (MOUSE_DOWN_FLAG) {
          moveEnd(el);
          MOUSE_DOWN_FLAG = false;
        }
      });

      window.addEventListener('resize', ()=> {
        adjustPosition(el);
      });
    }
  };

  function moveStart(el) {
    el.style.opacity = 1;
  }

  function moving(el, e) {
    let target = e.touches ? e.touches[0] : e;
    if (el.dokitEntryLastX === INIT_VALUE) {
      el.dokitEntryLastX = target.clientX;
      el.dokitEntryLastY = target.clientY;
      return
    }

    el.dokitPositionTop += (target.clientY - el.dokitEntryLastY);
    el.dokitPositionLeft += (target.clientX - el.dokitEntryLastX);
    el.dokitEntryLastX = target.clientX;
    el.dokitEntryLastY = target.clientY;

    // el.style.top = `${getAvailableTop(el)}px`
    // el.style.left = `${getAvailableLeft(el)}px`
    el.style.top = `${el.dokitPositionTop}px`;
    el.style.left = `${el.dokitPositionLeft}px`;
  }

  function moveEnd(el, e) {
    setTimeout(() => {
      adjustPosition(el);
      el.config.name && localStorage.setItem(`dokitPositionTop_${el.config.name}`, el.dokitPositionTop);
      el.config.name && localStorage.setItem(`dokitPositionLeft_${el.config.name}`, el.dokitPositionLeft);
    }, 100);
    el.dokitEntryLastX = INIT_VALUE;
    el.dokitEntryLastY = INIT_VALUE;
    el.style.opacity = el.config.opacity;
  }

  function getDefaultX(el){
    let defaultX = el.config.left || Math.round(window.innerWidth/2);
    return localStorage.getItem(`dokitPositionLeft_${el.config.name}`) ? parseInt(localStorage.getItem(`dokitPositionLeft_${el.config.name}`)) : defaultX
  }
  function getDefaultY(el){
    let defaultY = el.config.top || Math.round(window.innerHeight/2);
    return localStorage.getItem(`dokitPositionTop_${el.config.name}`) ? parseInt(localStorage.getItem(`dokitPositionTop_${el.config.name}`)) : defaultY
  }

  // function getAvailableLeft(el){
  //   return standardNumber(el.dokitPositionLeft, window.innerWidth - el.clientWidth)
  // }
  // function getAvailableTop(el){
  //   return standardNumber(el.dokitPositionTop, window.innerHeight - el.clientHeight)
  // }
  // function standardNumber(number, max){
  //   if(number < 0){
  //     return 0
  //   }
  //   if(number >= max){
  //     return max
  //   }
  //   return number
  // }

  function adjustPosition(el) {
    if (el.dokitPositionLeft < 0) {
      el.dokitPositionLeft = 0;
      el.style.left = `${el.dokitPositionLeft}px`;
    } else if (el.dokitPositionLeft + el.getBoundingClientRect().width > window.innerWidth) {
      el.dokitPositionLeft = window.innerWidth - el.getBoundingClientRect().width;
      el.style.left = `${el.dokitPositionLeft}px`;
    }
    
    if (el.dokitPositionTop < 0) {
      el.dokitPositionTop = 0;
      el.style.top = `${el.dokitPositionTop}px`;

    } else if (el.dokitPositionTop + el.getBoundingClientRect().height + el.config.safeBottom > window.innerHeight) {
      el.dokitPositionTop = window.innerHeight - el.getBoundingClientRect().height - el.config.safeBottom;
      el.style.top = `${el.dokitPositionTop}px`;
    }
  }

  var script$4 = {
    directives: {
      dragable,
    },
    methods: {
      remove() {
        W("test");
      },
    },
  };

  const _withId$2 = /*#__PURE__*/vue.withScopeId("data-v-0274cd90");

  vue.pushScopeId("data-v-0274cd90");
  const _hoisted_1$3 = { class: "hello-independ" };
  const _hoisted_2$2 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, " Hello Dokit ", -1 /* HOISTED */);
  const _hoisted_3$2 = /*#__PURE__*/vue.createVNode("div", null, "Demo Independ Plugin", -1 /* HOISTED */);
  vue.popScopeId();

  const render$4 = /*#__PURE__*/_withId$2((_ctx, _cache, $props, $setup, $data, $options) => {
    const _directive_dragable = vue.resolveDirective("dragable");

    return vue.withDirectives((vue.openBlock(), vue.createBlock("div", _hoisted_1$3, [
      _hoisted_2$2,
      _hoisted_3$2,
      vue.createVNode("div", {
        onClick: _cache[1] || (_cache[1] = (...args) => ($options.remove && $options.remove(...args))),
        style: {"background-color":"red","color":"white","margin-top":"10px"}
      }, " 点击移除当前独立插件 ")
    ], 512 /* NEED_PATCH */)), [
      [_directive_dragable]
    ])
  });

  var css_248z$4 = "\n.hello-independ[data-v-0274cd90] {\n  display: inline-block;\n  width: 200px;\n  /* padding: 10px; */\n  text-align: center;\n  background-color: white;\n  border-radius: 20px;\n  box-shadow: 0 8px 12px #ebedf0;\n  overflow: hidden;\n  border: 1px solid red;\n}\n";
  styleInject(css_248z$4);

  script$4.render = render$4;
  script$4.__scopeId = "data-v-0274cd90";
  script$4.__file = "src/plugins/demo-single-plugin/IndependPluginDemo.vue";

  var DemoIndependPlugin = new Ct({
    nameZh: '独立插件',
    name: 'test',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: script$4
  });

  var script$3 = {
    
  };

  const _hoisted_1$2 = { class: "hello-world" };
  const _hoisted_2$1 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, "Hello Dokit", -1 /* HOISTED */);
  const _hoisted_3$1 = /*#__PURE__*/vue.createVNode("div", null, "Demo Plugin", -1 /* HOISTED */);

  function render$3(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$2, [
      _hoisted_2$1,
      _hoisted_3$1
    ]))
  }

  var css_248z$3 = "\n.hello-world{\n  padding:10px;\n  text-align: center;\n}\n";
  styleInject(css_248z$3);

  script$3.render = render$3;
  script$3.__file = "src/plugins/h5-door/ToolHelloWorld.vue";

  var H5DoorPlugin = new It({
    nameZh: '任意门',
    name: 'h5-door',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/FHqpI3InaS1618997548865.png',
    component: script$3
  });

  var script$2 = {
    
  };

  const _hoisted_1$1 = { class: "hello-world" };
  const _hoisted_2 = /*#__PURE__*/vue.createVNode("div", { style: {"font-weight":"bold","font-size":"30px","font-style":"italic"} }, "Hello Dokit", -1 /* HOISTED */);
  const _hoisted_3 = /*#__PURE__*/vue.createVNode("div", null, "Demo Plugin", -1 /* HOISTED */);

  function render$2(_ctx, _cache, $props, $setup, $data, $options) {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1$1, [
      _hoisted_2,
      _hoisted_3
    ]))
  }

  var css_248z$2 = "\n.hello-world{\n  padding:10px;\n  text-align: center;\n}\n";
  styleInject(css_248z$2);

  script$2.render = render$2;
  script$2.__file = "src/components/ToolHelloWorld.vue";

  var script$1 = {
    props: {
      position: {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0,
      },
    },
  };

  const _withId$1 = /*#__PURE__*/vue.withScopeId("data-v-1cd0f855");

  vue.pushScopeId("data-v-1cd0f855");
  const _hoisted_1 = { class: "position-infobox-container" };
  vue.popScopeId();

  const render$1 = /*#__PURE__*/_withId$1((_ctx, _cache, $props, $setup, $data, $options) => {
    return (vue.openBlock(), vue.createBlock("div", _hoisted_1, [
      vue.createTextVNode(" 位置: 左 " + vue.toDisplayString($props.position.left) + ", 右 " + vue.toDisplayString($props.position.right) + ", 上 " + vue.toDisplayString($props.position.top) + ", 下 " + vue.toDisplayString($props.position.bottom) + " ", 1 /* TEXT */),
      vue.createVNode("span", {
        class: "close-button",
        onClick: _cache[1] || (_cache[1] = $event => (_ctx.$emit('remove')))
      })
    ]))
  });

  var css_248z$1 = "\n.position-infobox-container[data-v-1cd0f855] {\n  position: fixed;\n  left: 0;\n  right: 0;\n  margin: auto;\n  bottom: 20px;\n\n  width: 250px;\n  height: 50px;\n  padding: 0px 20px;\n  line-height: 50px;\n  font-size: 12px;\n\n  border-radius: 5px;\n  box-shadow: 0px 0px 2px #cccccc;\n}\n.position-info[data-v-1cd0f855] {\n  font-size: 10px;\n}\n.close-button[data-v-1cd0f855] {\n  background: #324456;\n  color: #fff;\n\n  border-radius: 10px;\n  width: 20px;\n  height: 20px;\n\n  line-height: 18px;\n  font-size: 15px;\n  text-align: center;\n\n  top: 15px;\n  right: 10px;\n  position: absolute;\n}\n.close-button[data-v-1cd0f855]::before {\n  content: \"×\";\n}\n";
  styleInject(css_248z$1);

  script$1.render = render$1;
  script$1.__scopeId = "data-v-1cd0f855";
  script$1.__file = "src/plugins/align-ruler/info-box.vue";

  var script = {
    name: "align-ruler",
    components: {
      InfoBox: script$1,
    },
    data() {
      return {
        posX: 200,
        posY: 200,
        screenWidth: document.documentElement.clientWidth,
        screenHeight: document.documentElement.clientHeight,
      };
    },
    computed: {
      position() {
        return {
          top: this.posY,
          left: this.posX,
          right: this.screenWidth - this.posX,
          bottom: this.screenHeight - this.posY,
        };
      },

      centerStyle() {
        return {
          top: this.posY + "px",
          left: this.posX + "px",
        };
      },
      horizonLineStyle() {
        return {
          width: this.screenWidth + "px",
          top: this.posY + "px",
        };
      },
      verticalLineStyle() {
        return {
          height: this.screenHeight + "px",
          left: this.posX + "px",
        };
      },
      topInfoStyle() {
        return {
          top: this.posY / 2 + "px",
          left: this.posX + "px",
        };
      },
      rightInfoStyle() {
        return {
          top: this.posY + "px",
          left: (this.posX + this.screenWidth) / 2 + "px",
        };
      },
      bottomInfoStyle() {
        return {
          top: (this.screenHeight + this.posY) / 2 + "px",
          left: this.posX + "px",
        };
      },
      leftInfoStyle() {
        return {
          top: this.posY + "px",
          left: this.posX / 2 + "px",
        };
      },
    },

    // 监听浏览器缩放
    mounted() {
      window.addEventListener("resize", this.handleResize);
    },
    beforeUnmount() {
      window.removeEventListener("resize", this.handleResize);
    },

    methods: {
      remove() {
        W("align-ruler");
      },
      handleResize(event) {
        this.screenWidth = document.documentElement.clientWidth;
        this.screenHeight = document.documentElement.clientHeight;
        this.posX = this.screenWidth * 0.3;
        this.posY = this.screenHeight * 0.3;
      },
      drag(e) {
        e.preventDefault();
        e.stopPropagation();
        let el = e.target;

        // 算出鼠标相对元素的位置
        // 兼容触摸和鼠标
        let startX = e.clientX ? e.clientX : e.touches[0].clientX;
        let startY = e.clientY ? e.clientY : e.touches[0].clientY;
        let offsetX = startX - el.offsetLeft;
        let offsetY = startY - el.offsetTop;

        let update = (X, Y) => {
          let left = X - offsetX;
          let top = Y - offsetY;

          this.posX = Math.round(left);
          this.posY = Math.round(top);
        };

        let handleMousemove = (e) => {
          update(e.clientX, e.clientY);
        };

        let handleTouchmove = (e) => {
          update(e.touches[0].clientX, e.touches[0].clientY);
        };

        let removeDragHandle = (e) => {
          document.removeEventListener("mousemove", handleMousemove);
          document.removeEventListener("touchmove", handleTouchmove);
          document.removeEventListener("mouseup", removeDragHandle);
          document.removeEventListener("touchend", removeDragHandle);
        };

        document.addEventListener("mousemove", handleMousemove);
        document.addEventListener("touchmove", handleTouchmove);
        document.addEventListener("mouseup", removeDragHandle);
        document.addEventListener("touchend", removeDragHandle);
      },
    },
  };

  const _withId = /*#__PURE__*/vue.withScopeId("data-v-600632d6");

  const render = /*#__PURE__*/_withId((_ctx, _cache, $props, $setup, $data, $options) => {
    const _component_InfoBox = vue.resolveComponent("InfoBox");

    return (vue.openBlock(), vue.createBlock("div", null, [
      vue.createVNode("div", {
        class: "ruler-center-bg",
        style: $options.centerStyle
      }, null, 4 /* STYLE */),
      vue.createVNode("div", {
        class: "ruler-center-round",
        style: $options.centerStyle
      }, null, 4 /* STYLE */),
      vue.createVNode("div", {
        class: "ruler-center-dot",
        style: $options.centerStyle
      }, null, 4 /* STYLE */),
      vue.createVNode("div", {
        class: "ruler-line ruler-horizon-line",
        style: $options.horizonLineStyle
      }, null, 4 /* STYLE */),
      vue.createVNode("div", {
        class: "ruler-line ruler-vertical-line",
        style: $options.verticalLineStyle
      }, null, 4 /* STYLE */),
      vue.createVNode("div", {
        class: "ruler-info",
        style: $options.topInfoStyle
      }, vue.toDisplayString($options.position.top), 5 /* TEXT, STYLE */),
      vue.createVNode("div", {
        class: "ruler-info",
        style: $options.rightInfoStyle
      }, vue.toDisplayString($options.position.right), 5 /* TEXT, STYLE */),
      vue.createVNode("div", {
        class: "ruler-info",
        style: $options.bottomInfoStyle
      }, vue.toDisplayString($options.position.bottom), 5 /* TEXT, STYLE */),
      vue.createVNode("div", {
        class: "ruler-info",
        style: $options.leftInfoStyle
      }, vue.toDisplayString($options.position.left), 5 /* TEXT, STYLE */),
      vue.createVNode(_component_InfoBox, {
        position: $options.position,
        onRemove: $options.remove
      }, null, 8 /* PROPS */, ["position", "onRemove"]),
      vue.createVNode("div", {
        class: "ruler-drag-mask",
        style: $options.centerStyle,
        onMousedown: _cache[1] || (_cache[1] = (...args) => ($options.drag && $options.drag(...args))),
        onTouchstart: _cache[2] || (_cache[2] = (...args) => ($options.drag && $options.drag(...args)))
      }, null, 36 /* STYLE, HYDRATE_EVENTS */)
    ]))
  });

  var css_248z = "\n.color[data-v-600632d6] {\n  color: #ffffff;\n  color: #cc3a4b;\n  color: #337cc4;\n  color: #cc3a4b30;\n}\n.ruler-center-bg[data-v-600632d6] {\n  box-sizing: border-box;\n  background-color: rgba(255, 255, 255, 0.392156863);\n  position: fixed;\n  width: 60px;\n  height: 60px;\n  transform: translate(-30px, -30px);\n  border-radius: 30px;\n  border: solid 1px rgba(51, 124, 196, 0.392156863);\n}\n.ruler-center-round[data-v-600632d6] {\n  background-color: rgba(204, 58, 75, 0.196078431);\n  position: fixed;\n  width: 40px;\n  height: 40px;\n  transform: translate(-20px, -20px);\n  border-radius: 20px;\n}\n.ruler-center-dot[data-v-600632d6] {\n  background-color: #cc3a4b;\n  position: fixed;\n  width: 6px;\n  height: 6px;\n  transform: translate(-3px, -3px);\n  border-radius: 3px;\n}\n.ruler-line[data-v-600632d6] {\n  position: fixed;\n  background-color: #cc3a4b;\n}\n.ruler-horizon-line[data-v-600632d6] {\n  left: 0;\n  height: 1px;\n  transform: translate(0px, -0.5px);\n}\n.ruler-vertical-line[data-v-600632d6] {\n  top: 0;\n  width: 1px;\n  transform: translate(-0.5px, 0px);\n}\n.ruler-info[data-v-600632d6] {\n  position: fixed;\n}\n.ruler-drag-mask[data-v-600632d6] {\n  position: fixed;\n  width: 60px;\n  height: 60px;\n  transform: translate(-30px, -30px);\n  border-radius: 30px;\n}\n";
  styleInject(css_248z);

  script.render = render;
  script.__scopeId = "data-v-600632d6";
  script.__file = "src/plugins/align-ruler/align-ruler.vue";

  var AlignRuler = new Ct({
    nameZh: '对齐标尺',
    name: 'align-ruler',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/a5UTjMn6lO1618997535798.png',
    component: script
  });

  const BasicFeatures = {
    title: '常用工具',
    list: [Console, AppInfo, Storage, DemoPlugin, DemoIndependPlugin, H5DoorPlugin]
  };

  const DokitFeatures = {
    title: '平台功能',
    list: [new It({
      nameZh: 'Mock数据',
      name: 'mock',
      icon: 'https://pt-starimg.didistatic.com/static/starimg/img/aDn77poRDB1618997545078.png',
      component: script$2
    })]
  };

  const UIFeatures = {
    title: '视觉功能',
    list: [AlignRuler, 
      new It({
      nameZh: 'UI结构',
      name: 'view-selector',
      icon: 'https://pt-starimg.didistatic.com/static/starimg/img/XNViIWzG7N1618997548483.png',
      component: script$2
    })]
  };
  const Features = [BasicFeatures, DokitFeatures, UIFeatures];

  /*
  * TODO 全局注册 Dokit
  */
  window.Dokit = new Pt({
    features: Features,
  });

}(Vue));
