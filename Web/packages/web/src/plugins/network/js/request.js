// import {EventEmitter} from '@dokit/web-core'

// class Request extends EventEmitter{
//   constructor(){
//     super()
//     this.initialize()
//   }
//   initialize(){
//     let _this = this
//     let {send:originSend, open:originOpen} = window.XMLHttpRequest.prototype;
//     // TODO 增加请求拦截器，增加单测
//     window.XMLHttpRequest.prototype.open = function(method, url){
//       originOpen.apply(this, arguments);
//     }
//     window.XMLHttpRequest.prototype.send = function(){
//       originSend.apply(this, arguments);
//     }
//   }
// }

// // 单例，保证有且只有一个
// export default new Request()