import {
  throttle,
  debounce,
  uuid,
  ActionObject,
} from './util'
import xpath from './xpath/index';
import cssesc from 'cssesc';
import finder from './finder';
import eventsToRecord from './dom-events-to-record';
import UIController from './UIController';
import getNodeText from './node';
import moment from 'moment'
import {
  getGlobalData
} from '../../store'
var xpathFinder;
if (document.readyState === "loading") {
  // window.addEventListener("load", function (_event) {
  //   xpathFinder = xpath()
  // });
  document.addEventListener("readystatechange", function () {
    if (document.readyState === "interactive") {
      xpathFinder = xpath()
    }
  });
}else{
  xpathFinder = xpath()
}
export default class EventRecorder {
  constructor(socketUrl) {
    this.state = getGlobalData();
    this._boundedMessageListener = null;
    this._eventErrLog = [];
    this._eventLog = [];
    this._eventSendLog = [];
    this._previousEvent = null;
    this._dataAttribute = null;
    this._uiController = null;
    this._screenShotMode = false;
    this._isTopFrame = (window.location === window.parent.location);
    this._isRecordingClicks = true;
    this.socketUrl = socketUrl;
    this.observer = null;
    this.inputEventListenerFun = debounce(this._recordEvent.bind(this), 200);
  }

  boot() {
    this.state.startRecorder = true
    if (document.readyState === "loading") {
      document.addEventListener("readystatechange", ()=>{
        if (document.readyState === "complete") {
          this._initializeRecorder();
          this.ovserverDom();
        }
      });
    }else{
      this._initializeRecorder();
      this.ovserverDom()
    }
  }

  off() {
    this.state.startRecorder = false
    this.observer.disconnect()
  }
  ovserverDom(){
    this.observer = new MutationObserver((mutations) => {
      for (let i = 0; i < mutations.length; i++) {
        let mutation = mutations[i];
        if(mutation.addedNodes.length>0){
          let inputList = document.getElementsByTagName("input");
          let textareaList = document.getElementsByTagName("textarea");
          let list = [...inputList, ...textareaList];
          list.forEach((item) => {
            switch (item.type) {
              case 'text':
              case 'number':
              case 'password':
              case 'textarea':
                item.addEventListener('input', this.inputEventListenerFun, true);
              default:
                break;
            }
          })
        }
      }
    });
    this.observer.observe(document.body, {
      childList: true,
      subtree: true,
    });
  }

  _initializeRecorder() {
    // console.debug('event recorder init:', this._isTopFrame);
    const events = Object.values(eventsToRecord);
    const popstateCallback = (e) =>{
      console.log('页面返回:',e);
      const msg = {
        type: 'action',
        action: 'navigatorBack',
        timestamp: new Date().getTime(),
        trudid: uuid(),
        location: window.location ? window.location : null,
      };
      this._eventLog.push(msg);
      this._sendMessage(msg);
    }
    if (!window.screenRecorderAddedControlListeners) {
      console.log('_addAllListeners:', events);
      this._addAllListeners(events);
      // window.addEventListener('popstate', popstateCallback);
      // this._boundedMessageListener = this._boundedMessageListener || this._handleBackgroundMessage.bind(this);
      // chrome.runtime.onMessage.addListener(this._boundedMessageListener);
      window.screenRecorderAddedControlListeners = true;
    }

    if (!window.document.screenRecorderAddedControlListeners) {
      window.document.screenRecorderAddedControlListeners = true;
    }

    // if (this._isTopFrame) {
    //   this._sendMessage({
    //     control: ctrl.EVENT_RECORDER_STARTED
    //   });
    //   this._sendMessage({
    //     control: ctrl.GET_CURRENT_URL,
    //     href: window.location.href
    //   });
    //   this._sendMessage({
    //     control: ctrl.GET_VIEWPORT_SIZE,
    //     coordinates: {
    //       width: window.innerWidth,
    //       height: window.innerHeight
    //     }
    //   });
    // }
  }

  // _handleBackgroundMessage(msg, sender, sendResponse) {
  //   console.debug('content-script 22: message from background', msg);
  //   if (msg && msg.action) {
  //     switch (msg.action) {
  //       case actions.TOGGLE_SCREENSHOT_MODE:
  //         this._handleScreenshotMode(false);
  //         break;
  //       case actions.TOGGLE_SCREENSHOT_CLIPPED_MODE:
  //         this._handleScreenshotMode(true);
  //         break;
  //       default:
  //     }
  //   }
  // }

  _addAllListeners(events) {
    events.forEach((type) => {
      let boundedRecordEvent = this._recordEvent.bind(this);
      console.log('type:',type)
      if (type === 'input') {
        let inputList = document.getElementsByTagName("input");
        let textareaList = document.getElementsByTagName("textarea");
        let list = [...inputList, ...textareaList];
        console.log('inputList:',list)
        list.forEach((item) => {
          console.log(item.type)
          switch (item.type) {
            case 'text':
            case 'number':
            case 'password':
            case 'textarea':
              item.addEventListener('input', this.inputEventListenerFun, true);
            default:
              break;
          }
        })
        return;
      }
      if (type === 'touchmove') {
        boundedRecordEvent = throttle(boundedRecordEvent, 50);
      }
      if (type === 'scroll') {
        boundedRecordEvent = throttle(boundedRecordEvent, 50);
      }
      window.addEventListener(type, boundedRecordEvent, true);
    })
  }

  _sendMessage(msg) {
    if (this.state.mySocket && this.state.mySocket.webSocketState) {
      console.log('send message:', msg, window.eventRecorder);
      this.state.mySocket.send({
        type: 'BROADCAST',
        contentType:'action',
        channelSerial: this.state.channelSerial,
        data: JSON.stringify({
          ...msg,
          connectSerial: this.state.connectSerial
        })
      })
      this._eventSendLog.push(msg);
    }
  }

  _recordEvent(e) {
    this.state.aid = uuid()
    // console.log('startRecorder:',this.state.startRecorder)
    if (!this.state.startRecorder) {
      return;
    }
    try {
      // console.log(e);
      let selector = '';
      // let dangerSelector = '';
      let el = e.target;
      if (e.target === document) {
        selector = 'html';
        el = e.target.scrollingElement;
      } else {
        const optimizedMinLength = (e.target.id) ? 2 : 10;
        selector = this._dataAttribute && e.target.hasAttribute && e.target.hasAttribute(this._dataAttribute) ?
          EventRecorder._formatDataSelector(e.target, this._dataAttribute) :
          xpathFinder.find(e.target);
        // dangerSelector = finder(e.target, {
        //   seedMinLength: 5,
        //   optimizedMinLength,
        //   className: (className, input) => {
        //     if (input.tagName.toLowerCase() === 'body') {
        //       return false;
        //     }
        //     return true;
        //   },
        // });
        // console.log('selector:',selector)
      }
      if (selector.indexOf('body')<0&&selector!=='html') {
        return;
      }
      let attrs = {};
      if (e.type === 'scroll') {
        attrs = {
          scrollLeft: el.scrollLeft,
          scrollTop: el.scrollTop,
        };
      }
      const names = Array.from(e.target.classList || [])
        .map(cName => `.${cssesc(cName, { isIdentifier: true })}`);
      const cloneEvent = {}
      switch (e.type) {
        case 'click':
        case 'click':
        case 'touchstart':
        case 'touchmove':
        case 'touchend':
          cloneEvent.altKey = e?.altKey
          cloneEvent.metaKey = e?.metaKey
          cloneEvent.ctrlKey = e?.ctrlKey
          e?.targetTouches&&(cloneEvent.targetTouches={
            clientX: e?.targetTouches[0]?.clientX,
            clientY: e?.targetTouches[0]?.clientY,
            force: e?.targetTouches[0]?.force,
            identifier: e?.targetTouches[0]?.identifier,
            pageX: e?.targetTouches[0]?.pageX,
            pageY: e?.targetTouches[0]?.pageY,
            radiusX: e?.targetTouches[0]?.radiusX,
            radiusY: e?.targetTouches[0]?.radiusY,
            rotationAngle: e?.targetTouches[0]?.rotationAngle,
            screenX: e?.targetTouches[0]?.screenX,
            screenY: e?.targetTouches[0]?.screenY
          })
          e?.changedTouches&&(cloneEvent.changedTouches={
            clientX: e?.changedTouches[0]?.clientX,
            clientY: e?.changedTouches[0]?.clientY,
            force: e?.changedTouches[0]?.force,
            identifier: e?.changedTouches[0]?.identifier,
            pageX: e?.changedTouches[0]?.pageX,
            pageY: e?.changedTouches[0]?.pageY,
            radiusX: e?.changedTouches[0]?.radiusX,
            radiusY: e?.changedTouches[0]?.radiusY,
            rotationAngle: e?.changedTouches[0]?.rotationAngle,
            screenX: e?.changedTouches[0]?.screenX,
            screenY: e?.changedTouches[0]?.screenY
          })
          break;
        default:
          break;
      }
      let dateTime = new Date().getTime();
      let coordinates = EventRecorder._getCoordinates(e);
      const msg = {
        eventId: uuid(),
        eventType:'VIEW_COMMON_EVENT',
        dateTime: moment(dateTime).format("YYYY-MM-DD HH:mm:ss.SSS"),
        diffTime:this.state.actionTime?(dateTime - this.state.actionTime):0,
        params:{},
        viewC12c:{
          actionType: ActionObject[e.type].actionType,
          actionName: ActionObject[e.type].actionName,
          params:{},
          viewPath:selector,
          viewPathDetail:selector,
          text:getNodeText(el),
          touchX:coordinates?.x,
          touchY:coordinates?.y,
          scrollX:attrs?.scrollTop,
          scrollY:attrs?.scrollLeft,
          inputValue:el?.value,
          position:this.getPositon(el),
          cloneEvent,
        },
        // type: 'action',
        // e: cloneEvent,
        // selector,
        // aid: this.state.aid,
        // className: names.join(''),
        // value: el.value,
        // innerHTML: getNodeText(el),
        // tagName: el.tagName,
        // action: e.type,
        // keyCode: e.keyCode ? e.keyCode : null,
        // href: el.href ? el.href : null,
        // coordinates: EventRecorder._getCoordinates(e),
        // position: this.getPositon(el),
        // location: window.location ? window.location : null,
        // ...attrs,
      };
      this.state.actionTime = dateTime
      this._eventLog.push(msg);
      this._sendMessage(msg);
    } catch (event) {
      this._eventErrLog.push(event);
      console.log('[eventErrLog]:', event);
    }
  }

  getPositon(elem) {
    let top = -window.scrollY;
    let left = -window.scrollX;
    const width = elem.offsetWidth;
    const height = elem.offsetHeight;

    while (elem && elem !== document.body) {
      top += elem.offsetTop;
      left += elem.offsetLeft;
      elem = elem.offsetParent;
    }
    return {
      width: `${width}px`,
      height: `${height}px`,
      top: `${top - 1}px`,
      left: `${left - 1}px`,
    };
  }

  _getEventLog() {
    return this._eventLog;
  }

  _clearEventLog() {
    this._eventLog = [];
  }

  // _handleScreenshotMode(isClipped) {
  //   this._disableClickRecording();
  //   this._uiController = new UIController({
  //     showSelector: isClipped
  //   });
  //   this._screenShotMode = !this._screenShotMode;
  //   document.body.style.cursor = 'crosshair';

  //   console.debug('screenshot mode:', this._screenShotMode);

  //   if (this._screenShotMode) {
  //     this._uiController.showSelector();
  //   } else {
  //     this._uiController.hideSelector();
  //   }

  //   this._uiController.on('click', (event) => {
  //     this._screenShotMode = false;
  //     document.body.style.cursor = DEFAULT_MOUSE_CURSOR;
  //     this._sendMessage({
  //       control: ctrl.GET_SCREENSHOT,
  //       value: event.clip
  //     });
  //     this._enableClickRecording();
  //   });
  // }

  // _disableClickRecording() {
  //   this._isRecordingClicks = false;
  // }

  // _enableClickRecording() {
  //   this._isRecordingClicks = true;
  // }

  sendEventRight() {
    return this._eventLog.length === this._eventSendLog.length;
  }

  static _getCoordinates(evt) {
    const eventsWithCoordinates = {
      mouseup: true,
      mousedown: true,
      mousemove: true,
      mouseover: true,
      touchstart: true,
      touchend: true,
      touchmove: true,
    };
    return eventsWithCoordinates[evt.type] ? {
      x: evt.clientX || evt.changedTouches[0].clientX,
      y: evt.clientY || evt.changedTouches[0].clientY
    } : null;
  }

  static _formatDataSelector(element, attribute) {
    return `[${attribute}="${element.getAttribute(attribute)}"]`;
  }
}