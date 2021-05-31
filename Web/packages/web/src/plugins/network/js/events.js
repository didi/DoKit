export const networkMethods = {
  'deleteCookies': Network.deleteCookies,
  'enable': Network.enable,
  'getCookies': Network.getCookies,
  'getResponseBody': Network.getResponseBody,
}

export const reqWillBeSent = function (params){
    this._requests[params.requestId] = {
      name: getFileName(params.request.url),
      url: params.request.url,
      status: 'pending',
      type: 'unknown',
      subType: 'unknown',
      size: 0,
      data: params.request.postData,
      method: params.request.method,
      startTime: params.timestamp * 1000,
      time: 0,
      resTxt: '',
      done: false,
      reqHeaders: params.request.headers || {},
      resHeaders: {},
    }
}

export const resReceivedExtraInfo = function (params){
    const target = this._requests[params.requestId]
    if (!target) {
      return
    }

    target.resHeaders = params.headers

    this._updateType(target)
    this._render()
}

export function updateType(target) {
    const contentType = target.resHeaders['content-type'] || ''
    const { type, subType } = getType(contentType)
    target.type = type
    target.subType = subType
}

export const resReceived = function(params){
    const target = this._requests[params.requestId]
    if (!target) {
      return
    }

    const { response } = params
    const { status, headers } = response
    target.status = status
    if (status < 200 || status >= 300) {
      target.hasErr = true
    }
    if (headers) {
      target.resHeaders = headers
      this._updateType(target)
    }

    this._render()
}

export const loadingFinished = function(params){
    const target = this._requests[params.requestId]
    if (!target) {
      return
    }

    const time = params.timestamp * 1000
    target.time = time - target.startTime
    target.displayTime = ms(target.time)

    target.size = params.encodedDataLength
    target.done = true
    target.resTxt = chobitsu.domain('Network').getResponseBody({
      requestId: params.requestId,
    }).body

    this._render()
}