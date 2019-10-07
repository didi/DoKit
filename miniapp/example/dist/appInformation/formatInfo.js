function bol2chn(bol) {
    return bol ? "是" : "否";
}

function getGender(gender) {
    return gender === 1 ? "男" : "女";
}

const setSystemInfo = function (info) {
    const getBenchMarkLevel = function (lev) {
        if(lev === -2 || lev === 0) return "该设备无法运行小游戏";
        if(lev === -1) return "性能未知";
        if(lev>1 && lev<10) return "低";
        if(lev >11 && lev < 25) return "中";
        if(lev > 26 && lev < 50) return "优";
    };
    return [
        {
            name: "设备品牌",
            value: info.brand,
        },
        {
            name: "设备型号",
            value: info.model,
        },
        {
            name: "设备像素比",
            value: info.pixelRatio,
        },
        {
            name: "屏幕宽高",
            value: info.screenWidth + '/' + info.screenHeight,
        },
        {
            name: "可视区域宽高",
            value: info.windowWidth + '/' + info.windowHeight,
        },
        {
            name: "微信版本号",
            value: info.version
        },
        {
            name: "操作系统及版本号",
            value: info.system
        },
        {
            name: "客户端平台",
            value: info.platform
        },
        {
            name: '设备性能值',
            value: getBenchMarkLevel(info.benchmarkLevel)
        },
        {
            name: "允许微信使用摄像头的开关",
            value: bol2chn(info.cameraAuthorized)
        },
        {
            name: "允许微信使用定位的开关",
            value: bol2chn(info.locationAuthorized)
        },
        {
            name: "允许微信使用麦克风的开关",
            value: bol2chn(info.microphoneAuthorized)
        },
        {
            name: "允许微信通知的开关",
            value: bol2chn(info.notificationAuthorized)
        },
        {
            name: "地理位置的系统开关",
            value: bol2chn(info.locationEnabled)
        },
        {
            name: "Wi-Fi 的系统开关",
            value: bol2chn(info.wifiEnabled)
        }
    ]
};
const setUserInfo = function (info) {
    return [
        {
            name: "昵称",
            value: info.nickName
        },
        {
            name: "性别",
            value: getGender(info.gender)
        },
        {
            name: "地区",
            value: info.city + " " + info.province + " " + info.country
        },
        {
            name: "头像",
            type: "img",
            value: info.avatarUrl
        }
    ]
};

const setAuthInfo = function (info) {
    return [
        {
            name: "是否授权用户信息",
            value: bol2chn(info['scope.userInfo'])
        },
        {
            name: "是否授权地理位置",
            value: bol2chn(info['scope.userLocation'])
        },
        {
            name: "是否授权通讯地址",
            value: bol2chn(info['scope.address'])
        },
        {
            name: "是否授权发票抬头",
            value: bol2chn(info['scope.invoiceTitle'])
        },
        {
            name: "是否授权获取发票",
            value: bol2chn(info['scope.invoice'])
        },
        {
            name: "是否授权微信运动步数",
            value: bol2chn(info['scope.werun'])
        },
        {
            name: "是否授权录音功能",
            value: bol2chn(info['scope.record'])
        },
        {
            name: "是否授权保存到相册",
            value: bol2chn(info['scope.writePhotosAlbum'])
        },
        {
            name: "是否授权摄像头",
            value: bol2chn(info['scope.camera'])
        }
    ]
}

module.exports = {
    setSystemInfo,
    setUserInfo,
    setAuthInfo
};
