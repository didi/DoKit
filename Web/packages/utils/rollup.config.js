import {terser} from 'rollup-plugin-terser'

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
    ...extendPlugins
  ]
}