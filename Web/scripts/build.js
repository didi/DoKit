/**
 * build 脚本
 */
 const execa = require('execa') 
 run()
 async function run(){
   await build()
 }
 
 async function build(){
   execa(
     'rollup',
     [
       '-c'
     ],
     {
       stdio: 'inherit'
     }
   )
 }
 
 