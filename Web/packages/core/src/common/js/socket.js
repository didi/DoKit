import {
    getGlobalData
} from '../../store'
import {
    getCurrentInstance
} from 'vue';
import {
    $bus,
  } from '@dokit/web-utils'
export default class Socket {
    constructor(url) {
        this.instance = getCurrentInstance();
        this.state = getGlobalData();
        this.socket = null;
        this.webSocketState = null;
        this.reconnectTimer = null;
        this.waitingServerTime = null;
        this.startHeartBeatTimee = null;
        this.heartBeat = {
            time: 30 * 1000,
            timeout: 3 * 1000,
            reconnect: 10 * 1000,
        }
        this.isReconnect = true;
        this.wsUrl = url;
        this.init();
        if (this.socket) {
            this.onopen(() => {
                this.state = getGlobalData();
                this.webSocketState = true
                this.send({
                    type: 'LOGIN',
                    channelSerial: this.state.channelSerial,
                    data: JSON.stringify({
                        manufacturer: window.location.host,
                        connectSerial: this.state.connectSerial || undefined
                    })
                })
                $bus.emit("webSocketState")
                this.startHeartBeat(this.heartBeat.time) // 心跳机制
            })
            this.onerror((e) => {
                console.log(e);
                this.webSocketState = false
                this.socket = null
                this.state.socketConnect = false
            })
            this.onclose((e) => {
                console.log(e)
                this.socket = null
                this.state.socketConnect = false
                this.webSocketState = false
                this.reconnectTimer && clearTimeout(this.reconnectTimer);
                this.waitingServerTime && clearTimeout(this.waitingServerTime);
                this.startHeartBeatTime && clearTimeout(this.startHeartBeatTime);
            })
            this.onmessage((e) => {
                let msg = JSON.parse(e.data);
                if (msg.type === "HEART_BEAT") {
                    this.webSocketState = true
                }
            })
        }
    }
    init() {
        try {
            if (!this.socket) {
                this.socket = new WebSocket(this.wsUrl)
            }
            if (this.socket && this.reconnectTimer) {
                clearTimeout(this.reconnectTimer)
                this.reconnectTimer = null
                this.socket = new WebSocket(this.wsUrl)
            }
        } catch (error) {
            console.error(error)
        }
    }
    onopen(callBack) {
        this.socket.addEventListener('open', callBack)
    }
    onclose(callBack) {
        this.socket.addEventListener('close', callBack)
    }
    onmessage(callBack) {
        this.socket.addEventListener('message', callBack)
    }
    onerror(callBack) {
        this.socket.addEventListener('error', callBack)
    }
    close() {
        this.socket && this.socket?.close()
        this.socket = null
        this.reconnectTimer && clearTimeout(this.reconnectTimer);
        this.waitingServerTime && clearTimeout(this.waitingServerTime);
        this.startHeartBeatTime && clearTimeout(this.startHeartBeatTime);
    }
    send(msg) {
        this.socket && this.socket.send(JSON.stringify(msg))
    }
    startHeartBeat(time) {
        this.startHeartBeatTime = setTimeout(() => {
            this.send({
                type: 'HEART_BEAT',
                data: JSON.stringify({
                    time: new Date(),
                    connectSerial: this.state.connectSerial
                })
            })
            this.waitingServer()
        }, time)
    }
    //延时等待服务端响应，通过webSocketState判断是否连线成功
    waitingServer() {
        this.webSocketState = false
        this.waitingServerTime = setTimeout(() => {
            if (this.webSocketState) {
                this.startHeartBeat(this.heartBeat.time)
                return
            }
            console.log('心跳无响应，已断线')
            try {
                this.close()
                this.instance.proxy.$toast("连接已经断开", 2000);
            } catch (e) {
                console.log('连接已关闭，无需关闭')
            }
            this.reconnectWebSocket()
        }, this.heartBeat.timeout)
    }
    reconnectWebSocket() {
        if (!this.isReconnect) {
            return;
        }
        this.reconnectTimer = setTimeout(() => {
            this.init()
        }, this.heartBeat.reconnect)
    }
}