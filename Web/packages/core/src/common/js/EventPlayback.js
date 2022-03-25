import {
    getGlobalData
} from '../../store'
import mySocket from './socket'
export default class EventPlayback {
    constructor(url) {
        this.socketUrl = url
        this.state = getGlobalData();
        this.init()
    }
    init() {
        !this.state.mySocket && (this.state.mySocket = new mySocket(this.socketUrl))
        if (this.state.mySocket && this.state.mySocket.socket) {
            this.state.startPlayback = true
            this.state.mySocket.onmessage((e) => {
                try {
                    let data, msg = JSON.parse(e.data);
                    if (msg.type === "LOGIN") {
                        data = JSON.parse(msg.data)
                        this.state.connectSerial = data.connectSerial
                    } else if (msg.type === "BROADCAST") {
                        data = JSON.parse(msg.data)
                        if (data.type === 'action') {
                            this.state.aid = data.aid
                            if (this.state.startPlayback) {
                                let node = document.querySelector(data.selector)
                                let event;
                                switch (data.action) {
                                    case "navigatorBack":
                                        window.history.back()
                                        break;
                                    case "click":
                                        node.click();
                                        break;
                                    case "touchstart":
                                        event = document.createEvent('MouseEvents');
                                        event.initMouseEvent('touchstart', true, true, window, 0, data.e.targetTouches.screenX, data.e.targetTouches.screenY, data.e.targetTouches.clientX, data.e.targetTouches.clientY, data.e.ctrlKey, data.e.altKey, data.e.shiftKey, data.e.metaKey);
                                        event.changedTouches = [{
                                            clientX: data.e.targetTouches.clientX,
                                            clientY: data.e.targetTouches.clientY,
                                            force: data.e.targetTouches.force,
                                            identifier: data.e.targetTouches.identifier,
                                            pageX: data.e.targetTouches.pageX,
                                            pageY: data.e.targetTouches.pageY,
                                            radiusX: data.e.targetTouches.radiusX,
                                            radiusY: data.e.targetTouches.radiusY,
                                            rotationAngle: data.e.targetTouches.rotationAngle,
                                            screenX: data.e.targetTouches.screenX,
                                            screenY: data.e.targetTouches.screenY
                                        }]
                                        event.touches = event.changedTouches
                                        event.targetTouches = event.changedTouches
                                        console.log('fyq', event)
                                        node.dispatchEvent(event);
                                        break;
                                    case "touchmove":
                                        event = document.createEvent('MouseEvents');
                                        event.initMouseEvent('touchmove', true, true, window, 0, data.e.targetTouches.screenX, data.e.targetTouches.screenY, data.e.targetTouches.clientX, data.e.targetTouches.clientY, data.e.ctrlKey, data.e.altKey, data.e.shiftKey, data.e.metaKey);
                                        event.changedTouches = [{
                                            clientX: data.e.targetTouches.clientX,
                                            clientY: data.e.targetTouches.clientY,
                                            force: data.e.targetTouches.force,
                                            identifier: data.e.targetTouches.identifier,
                                            pageX: data.e.targetTouches.pageX,
                                            pageY: data.e.targetTouches.pageY,
                                            radiusX: data.e.targetTouches.radiusX,
                                            radiusY: data.e.targetTouches.radiusY,
                                            rotationAngle: data.e.targetTouches.rotationAngle,
                                            screenX: data.e.targetTouches.screenX,
                                            screenY: data.e.targetTouches.screenY
                                        }]
                                        event.touches = event.changedTouches
                                        event.targetTouches = event.changedTouches
                                        node.dispatchEvent(event);
                                        break;
                                    case "touchend":
                                        event = document.createEvent('MouseEvents');
                                        event.initMouseEvent('touchend', true, true, window, 0, data.e.changedTouches.screenX, data.e.changedTouches.screenY, data.e.changedTouches.clientX, data.e.changedTouches.clientY, data.e.ctrlKey, data.e.altKey, data.e.shiftKey, data.e.metaKey);
                                        event.changedTouches = [{
                                            clientX: data.e.targetTouches.clientX,
                                            clientY: data.e.targetTouches.clientY,
                                            force: data.e.targetTouches.force,
                                            identifier: data.e.targetTouches.identifier,
                                            pageX: data.e.targetTouches.pageX,
                                            pageY: data.e.targetTouches.pageY,
                                            radiusX: data.e.targetTouches.radiusX,
                                            radiusY: data.e.targetTouches.radiusY,
                                            rotationAngle: data.e.targetTouches.rotationAngle,
                                            screenX: data.e.targetTouches.screenX,
                                            screenY: data.e.targetTouches.screenY
                                        }]
                                        event.touches = event.changedTouches
                                        event.targetTouches = event.changedTouches
                                        node.dispatchEvent(event);
                                        break;
                                    case "input":
                                        event = document.createEvent('Events');
                                        event.initEvent('input', true, true);
                                        node.value = data.value
                                        node.dispatchEvent(event);
                                        break;
                                    case "scroll":
                                        node.scrollTop = data.scrollTop;
                                        node.scrollLeft = data.scrollLeft;
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } else if (data.type === 'switchState') {
                            this.state.isMaster = false;
                            this.state.startPlayback = true
                        }
                    }
                } catch (error) {
                    console.error(error);
                }
            })
        }
    }
    close() {
        this.state.mySocket && (this.state.mySocket.close(), this.state.mySocket = null)
        this.state.startPlayback = false
    }
}