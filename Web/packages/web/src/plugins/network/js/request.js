export const LogMap = {
  0: 'All',
  1: 'Log',
  2: 'Info',
  3: 'Warn',
  4: 'Error'
}
export const LogEnum = {
  ALL: 0,
  LOG: 1,
  INFO: 2,
  WARN: 3,
  ERROR: 4
}

export const ConsoleLogMap = {
  'log': LogEnum.LOG,
  'info': LogEnum.INFO,
  'warn': LogEnum.WARN,
  'error': LogEnum.ERROR
}

export const CONSOLE_METHODS = ["log", "info", 'warn', 'error']

export const LogTabs = Object.keys(LogMap).map(key => {
  return {
    type: parseInt(key),
    name: LogMap[key]
  }
})

export const excuteScript = function(command){
  let ret 
  try{
    ret = eval.call(window, `(${command})`)
  }catch(e){
    ret = eval.call(window, command)
  }
  return ret
}

export const origConsole = {}
export const noop = () => {}
export const overrideConsole = function(callback) {
  const winConsole = window.console
  CONSOLE_METHODS.forEach((name) => {
    let origin = (origConsole[name] = noop)
    if (winConsole[name]) {
      origin = origConsole[name] = winConsole[name].bind(winConsole)
    }

    winConsole[name] = (...args) => {
      callback({
        name: name,
        type: ConsoleLogMap[name],
        value: args
      })
      origin(...args)
    }
  })
}

export const restoreConsole = function(){
  const winConsole = window.console
  CONSOLE_METHODS.forEach((name) => {
    winConsole[name] = origConsole[name]
  })
}
