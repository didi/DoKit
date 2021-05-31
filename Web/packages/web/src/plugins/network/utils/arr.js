/*
 * @Author: yanghui 
 * @Date: 2021-05-31 11:19:50 
 * @Last Modified by: yanghui
 * @Last Modified time: 2021-05-31 13:42:09
 */
//数组操作库
export function last(arr) {
    const len = arr ? arr.length : 0;
  
    if (len) return arr[len - 1];
};