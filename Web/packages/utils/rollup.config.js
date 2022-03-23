import {terser} from 'rollup-plugin-terser'
import commonjs from 'rollup-plugin-commonjs'
const extendPlugins = []
if(process.env.NODE_ENV === 'production'){
  extendPlugins.push(terser())
}

export default {
  input: 'src/index.js',
  output: {
    name: 'index.js',
    file: 'dist/index.js',
    format: 'es'
  },

  plugins: [
    commonjs(),
    ...extendPlugins
  ]
}