export const ResourceMap = {
    0: 'css',
    1: 'script',
    2: 'img',
    // 3: 'other',
}
export const ResourceEnum = {
    CSS: 0,
    SCRIPT: 1,
    IMG: 2,
    OTHER: 3,
}

export const ResourceEntriesMap = {
    'css': ResourceEnum.CSS,
    'script': ResourceEnum.SCRIPT,
    'img': ResourceEnum.IMG,
    // 'other': ResourceEnum.OTHER,
}

export const Resource_METHODS = ["css", "script", "img"]

export const ResourceTabs = Object.keys(ResourceMap).map(key => {
    return {
        type: parseInt(key),
        name: ResourceMap[key]
    }
})

export const regImg = /\.(jpeg|jpg|gif|png|bmp)$/
export const regCss = /\.(css|less)$/

export const isImg = (url) => regImg.test(url)
export const isCss = (url) => regCss.test(url)

export const getResourceEntries = function(callback) {
    const performance = window.performance || window.webkitPerformance || window.msPerformance
    if (performance && performance.getEntriesByType) {
        const entries = performance.getEntriesByType("resource")
            //const curtime = performance.now()
        let arr = []
        entries.forEach((entry) => {
                let tmp = {
                    initiatorType: entry.initiatorType,
                    name: entry.name,
                }
                let flag = 0
                for (let i = 0; i < arr.length; i += 1) {
                    if (arr[i].initiatorType == tmp.initiatorType && arr[i].name == tmp.name) {
                        flag = 1;
                        break;
                    }
                }

                if (!flag) arr.push(tmp)
            })
            //arr = Array.from(new Set(arr))
        arr.forEach((entry) => {
            let entryType = ResourceEntriesMap['other']
            Resource_METHODS.forEach((type) => {
                if (entry.initiatorType === type || (type === "img" && entry.initiatorType === "css" && isImg(entry.name)) || (type === "css" && entry.initiatorType === "link" && isCss(entry.name))) {
                    entryType = ResourceEntriesMap[type];
                }
            })
            callback({
                type: entryType,
                initiatorType: entry.initiatorType,
                entryName: entry.name,
                base64: ''
            })
            // if (entryType === ResourceEntriesMap['img']) {
            //     getBase64Image(entry.name, (res) => {
            //         callback({
            //             type: entryType,
            //             initiatorType: entry.initiatorType,
            //             entryName: entry.name,
            //             base64: res
            //         })
            //     })
            // } else {
            //     callback({
            //         type: entryType,
            //         initiatorType: entry.initiatorType,
            //         entryName: entry.name,
            //         base64: ''
            //     })
            // }
        })
    }
}

export const imgLoad = function(url, callback) {
    var img = new Image();
    img.src = url;
    if (img.complete) {
        callback(img.width, img.height);
    } else {
        img.onload = function() {
            callback(img.width, img.height);
            img.onload = null;
        };
    };
}

export const getBase64Image = function(imgUrl, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open("get", imgUrl, true);
    xhr.responseType = "blob";
    xhr.onload = function() {
        if (this.status == 200) {
            //得到一个blob对象
            var blob = this.response;
            //console.log("blob", blob)
            let oFileReader = new FileReader();
            oFileReader.onloadend = function(e) {
                let base64 = e.target.result;
                callback(base64)
            };
            oFileReader.readAsDataURL(blob);
        } else {
            callback('')
        }
    }
    xhr.onerror = function(e) {
        callback('')
    }
    xhr.send();
}


export const url2blob = function(file_url, callback) {
    let xhr = new XMLHttpRequest();
    xhr.open("get", file_url, true);
    xhr.responseType = "blob";
    xhr.onload = function() {
        if (this.status == 200) {
            const reader = new FileReader()
            reader.onload = function() {
                callback(reader.result)
            }
            reader.readAsText(this.response);
        }
    };
    xhr.send();
}

export const url2blobPromise = function(file_url) {
    return new Promise(function(resolve, reject) {
        let xhr = new XMLHttpRequest();
        xhr.open("get", file_url);
        xhr.responseType = "blob";
        xhr.addEventListener('load', function() {
            if (this.status == 200) {
                const reader = new FileReader()
                reader.onload = function() {
                    resolve(reader.result)
                }
                reader.readAsText(this.response);
            } else {
                reject()
            }
        })

        xhr.addEventListener('error', function() {
            reject()
        })

        xhr.send();
    });
}

export const trimLeft = function(s) {
    if (s == null) {
        return "";
    }
    var whitespace = new String(" \t\n\r");
    var str = new String(s);
    if (whitespace.indexOf(str.charAt(0)) != -1) {
        var j = 0,
            i = str.length;
        while (j < i && whitespace.indexOf(str.charAt(j)) != -1) {
            j++;
        }
        str = str.substring(j, i);
        str = (new Array(j).join("&nbsp;")) + str

    }
    return str;
}
