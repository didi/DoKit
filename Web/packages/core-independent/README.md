# `dokit-core`
提供 Dokit 无侵入式容器，供 Dokit Web 定制化。

支持外部注入功能的配置，来自定义 Dokit 容器。

提供插件注册的能力、提供基础的 runtime 能力。

> Dokit Web Core，负责提供容器，供Dokit Web定制化能力。

## Usage

```javascript
import dokit from '@dokit/web-core';
dokit({
  routes: routes
});

```
