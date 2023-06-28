import {
    getGlobalData
} from '../../store'
import mySocket from './socket'
export default class EventPlayback {
    constructor(url) {
        this.socketUrl = url
        this.state = getGlobalData();
        this.init()
        this.isAutoTest = false
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
                        if(this.state.isNative){
                            localStorage.setItem('nativeConnectSerial', data.connectSerial)
                        }
                    } else if (msg.type === "BROADCAST") {
                        data = JSON.parse(msg.data)

                        console.log(data)
                        if (msg.contentType === 'action') {
                            this.state.aid = data.eventId
                            if (this.state.startPlayback) {
                                const nodeOps = () => {
                                    let node = document.querySelector(data.viewC12c.viewPath)
                                    let event;
                                    switch (data.viewC12c.actionType) {
                                        // case "navigatorBack":
                                        //     window.history.back()
                                        //     break;
                                        case "ON_CLICK":
                                            node.click();
                                            break;
                                        case "ON_TOUCH_START":
                                            event = document.createEvent('MouseEvents');
                                            event.initMouseEvent('touchstart', true, true, window, 0, data.viewC12c.cloneEvent.targetTouches.screenX, data.viewC12c.cloneEvent.targetTouches.screenY, data.viewC12c.cloneEvent.targetTouches.clientX, data.viewC12c.cloneEvent.targetTouches.clientY, data.viewC12c.cloneEvent.ctrlKey, data.viewC12c.cloneEvent.altKey, data.viewC12c.cloneEvent.shiftKey, data.viewC12c.cloneEvent.metaKey);
                                            event.changedTouches = [{
                                                clientX: data.viewC12c.cloneEvent.targetTouches.clientX,
                                                clientY: data.viewC12c.cloneEvent.targetTouches.clientY,
                                                force: data.viewC12c.cloneEvent.targetTouches.force,
                                                identifier: data.viewC12c.cloneEvent.targetTouches.identifier,
                                                pageX: data.viewC12c.cloneEvent.targetTouches.pageX,
                                                pageY: data.viewC12c.cloneEvent.targetTouches.pageY,
                                                radiusX: data.viewC12c.cloneEvent.targetTouches.radiusX,
                                                radiusY: data.viewC12c.cloneEvent.targetTouches.radiusY,
                                                rotationAngle: data.viewC12c.cloneEvent.targetTouches.rotationAngle,
                                                screenX: data.viewC12c.cloneEvent.targetTouches.screenX,
                                                screenY: data.viewC12c.cloneEvent.targetTouches.screenY
                                            }]
                                            event.touches = event.changedTouches
                                            event.targetTouches = event.changedTouches
                                            console.log('fyq', event)
                                            node.dispatchEvent(event);
                                            break;
                                        case "ON_TOUCH_MOVE":
                                            event = document.createEvent('MouseEvents');
                                            event.initMouseEvent('touchmove', true, true, window, 0, data.viewC12c.cloneEvent.targetTouches.screenX, data.viewC12c.cloneEvent.targetTouches.screenY, data.viewC12c.cloneEvent.targetTouches.clientX, data.viewC12c.cloneEvent.targetTouches.clientY, data.viewC12c.cloneEvent.ctrlKey, data.viewC12c.cloneEvent.altKey, data.viewC12c.cloneEvent.shiftKey, data.viewC12c.cloneEvent.metaKey);
                                            event.changedTouches = [{
                                                clientX: data.viewC12c.cloneEvent.targetTouches.clientX,
                                                clientY: data.viewC12c.cloneEvent.targetTouches.clientY,
                                                force: data.viewC12c.cloneEvent.targetTouches.force,
                                                identifier: data.viewC12c.cloneEvent.targetTouches.identifier,
                                                pageX: data.viewC12c.cloneEvent.targetTouches.pageX,
                                                pageY: data.viewC12c.cloneEvent.targetTouches.pageY,
                                                radiusX: data.viewC12c.cloneEvent.targetTouches.radiusX,
                                                radiusY: data.viewC12c.cloneEvent.targetTouches.radiusY,
                                                rotationAngle: data.viewC12c.cloneEvent.targetTouches.rotationAngle,
                                                screenX: data.viewC12c.cloneEvent.targetTouches.screenX,
                                                screenY: data.viewC12c.cloneEvent.targetTouches.screenY
                                            }]
                                            event.touches = event.changedTouches
                                            event.targetTouches = event.changedTouches
                                            node.dispatchEvent(event);
                                            break;
                                        case "ON_TOUCH_END":
                                            event = document.createEvent('MouseEvents');
                                            event.initMouseEvent('touchend', true, true, window, 0, data.viewC12c.cloneEvent.changedTouches.screenX, data.viewC12c.cloneEvent.changedTouches.screenY, data.viewC12c.cloneEvent.changedTouches.clientX, data.viewC12c.cloneEvent.changedTouches.clientY, data.viewC12c.cloneEvent.ctrlKey, data.viewC12c.cloneEvent.altKey, data.viewC12c.cloneEvent.shiftKey, data.viewC12c.cloneEvent.metaKey);
                                            event.changedTouches = [{
                                                clientX: data.viewC12c.cloneEvent.targetTouches.clientX,
                                                clientY: data.viewC12c.cloneEvent.targetTouches.clientY,
                                                force: data.viewC12c.cloneEvent.targetTouches.force,
                                                identifier: data.viewC12c.cloneEvent.targetTouches.identifier,
                                                pageX: data.viewC12c.cloneEvent.targetTouches.pageX,
                                                pageY: data.viewC12c.cloneEvent.targetTouches.pageY,
                                                radiusX: data.viewC12c.cloneEvent.targetTouches.radiusX,
                                                radiusY: data.viewC12c.cloneEvent.targetTouches.radiusY,
                                                rotationAngle: data.viewC12c.cloneEvent.targetTouches.rotationAngle,
                                                screenX: data.viewC12c.cloneEvent.targetTouches.screenX,
                                                screenY: data.viewC12c.cloneEvent.targetTouches.screenY
                                            }]
                                            event.touches = event.changedTouches
                                            event.targetTouches = event.changedTouches
                                            node.dispatchEvent(event);
                                            break;
                                        case "ON_INPUT_CHANGE":
                                            event = document.createEvent('Events');
                                            event.initEvent('input', true, true);
                                            node.value = data.viewC12c.inputValue;
                                            node.dispatchEvent(event);
                                            break;
                                        case "ON_SCROLL":
                                            node.scrollTop = data.viewC12c.scrollX;
                                            node.scrollLeft = data.viewC12c.scrollY;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                if (this.isAutoTest) {
                                    setTimeout(() => {
                                        nodeOps()
                                        this.state.mySocket.send({
                                            type: 'AUTOTEST',
                                            contentType:'action_response',
                                            channelSerial: this.state.channelSerial,
                                            data: JSON.stringify({ command: 'action_response',message: 'success', params: {eventId: data.eventId}})
                                        })
                                    }, data.diffTime);
                                } else {
                                    nodeOps()
                                }
                            }
                        } else if (msg.contentType === 'mc_host') {
                            this.state.isHost = false;
                            this.state.startPlayback = true
                        }
                    } else if (msg.type === "AUTOTEST") {
                        console.log('message', msg)
                        data = JSON.parse(msg.data)
                        if (msg.contentType === 'auto_test_control') {
                            if (data.command === 'startAutoTest' || data.command === 'stopAutoTest') {
                                this.isAutoTest = data.command === 'startAutoTest' ? true : false
                                this.state.mySocket.send({
                                    type: 'AUTOTEST',
                                    contentType:'control_response',
                                    channelSerial: this.state.channelSerial,
                                    data: JSON.stringify({...data, command: 'control_response',message: 'success', params: {command:data.command}})
                                })
                            }
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