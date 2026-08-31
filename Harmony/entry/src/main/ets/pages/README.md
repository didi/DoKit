# Entry 注入自定义 DoKit 工具

本文说明如何在鸿蒙壳工程（entry）中注册一个新的业务 Kit，并在悬浮窗面板中打开自定义页面。

鸿蒙与 Android 的差异：Android 通过 `DoKit.launchFullScreen(Fragment::class)` 由 SDK 反射创建 Fragment；鸿蒙通过 **字符串 pageId + 宿主 `@Builder`** 渲染 UI，因此 entry 必须实现页面 Builder 并挂到 `DoKitFloatingWindow`。

---

## 第一步：定义 pageId

在 entry 模块中定义页面标识，与 Android 的 Fragment Class 名作用类似。

```typescript
// entry/src/main/ets/dokit/MyKitPageIds.ets
export class MyKitPageIds {
  static readonly MY_TOOL: string = 'my_tool';
  static readonly MY_FLOAT: string = 'my_float'; // 悬浮窗 Kit 时使用
}
```

---

## 第二步：实现 Kit 类

继承 `AbstractKit`，实现名称、图标和点击逻辑。

```typescript
// entry/src/main/ets/dokit/MyToolKit.ets
import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, DoKit } from '@didi/dokit';
import { MyKitPageIds } from './MyKitPageIds';

export class MyToolKit extends AbstractKit {
  constructor() {
    super();
    this.category = Category.BIZ;
  }

  getName(): string {
    return '我的工具';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    // 全屏页
    DoKit.launchFullScreen(MyKitPageIds.MY_TOOL);
    // 悬浮窗 Kit 则改用：DoKit.launchFloating(MyKitPageIds.MY_FLOAT);
    return true; // true = 点击后收起工具面板
  }
}
```

对标 Android Demo：`TestSimpleDokitFragmentKit` 中 `DoKit.launchFullScreen(CustomDokitFragment::class, ...)`.

---

## 第三步：注册到分组（初始化时注入）

将 Kit 实例放入分组 Map，在 Application 初始化时传给 DoKit。

```typescript
// entry/src/main/ets/dokit/DemoCustomKits.ets
import { AbstractKit } from '@didi/dokit';
import { MyToolKit } from './MyToolKit';

export function buildDemoCustomKits(): Map<string, AbstractKit[]> {
  const map = new Map<string, AbstractKit[]>();
  map.set('业务专区1', [new MyToolKit()]);
  return map;
}
```

在 `App.onCreate`（或 EntryAbility 初始化逻辑）中调用：

```typescript
DoKit.Builder(context)
  .customKits(buildDemoCustomKits())
  .build();
```

也可使用 `customKitList([...])` 注入单个列表，默认归入「业务专区」。

---

## 第四步：实现 UI（@Builder）

业务页面的 UI 由 entry 实现，SDK 不负责渲染。

```typescript
// entry/src/main/ets/dokit/MyToolPage.ets
import { CustomKitCloseHandler } from '@didi/dokit';

@Builder
export function buildMyToolPage(onClose: CustomKitCloseHandler) {
  Column() {
    Row() {
      Text('我的工具')
        .layoutWeight(1)
        .textAlign(TextAlign.Center)
      Text('✕')
        .onClick(() => {
          onClose();
        })
    }
    .width('100%')
    .height(56)
    .padding({ left: 16, right: 16 })

    Text('业务内容放这里')
      .margin({ top: 24 })
  }
  .width('100%')
  .height('100%')
  .backgroundColor(Color.White)
}
```

---

## 第五步：挂到 DoKitFloatingWindow 分发器

在根页面的 `DoKitFloatingWindow` 上通过 `@BuilderParam` 按 pageId 路由到对应 UI。

```typescript
// entry/src/main/ets/dokit/DemoCustomKitRenderers.ets
import { CustomKitCloseHandler } from '@didi/dokit';
import { MyKitPageIds } from './MyKitPageIds';
import { buildMyToolPage } from './MyToolPage';

@Builder
export function buildEntryCustomFullScreen(pageId: string, onClose: CustomKitCloseHandler) {
  if (pageId === MyKitPageIds.MY_TOOL) {
    buildMyToolPage(onClose);
  }
}

@Builder
export function buildEntryCustomFloating(pageId: string, onClose: CustomKitCloseHandler) {
  // 悬浮窗 Kit 在此按 pageId 分发
}
```

根页面挂载（参考 `pages/Index.ets`）：

```typescript
Stack() {
  // 业务主页面 ...
  DoKitFloatingWindow({
    customFullScreenPage: buildEntryCustomFullScreen,
    customFloatingPage: buildEntryCustomFloating
  })
}
```

---

## 快速对照

| 步骤 | 文件 | 作用 |
|------|------|------|
| 1 | `MyKitPageIds.ets` | 定义 pageId |
| 2 | `MyToolKit.ets` | Kit 逻辑，点击时 `launchFullScreen` / `launchFloating` |
| 3 | `DemoCustomKits.ets` + `App.ets` | 注入面板分组 |
| 4 | `MyToolPage.ets` | `@Builder` 实现 UI |
| 5 | `DemoCustomKitRenderers.ets` + `pages/Index.ets` | pageId → Builder 路由 |

## 参考实现

entry 内已有完整 Demo，可直接对照：

- `dokit/DemoKit.ets` — demo Kit
- `dokit/TestSimpleDokitFragmentKit.ets` — 全屏 Kit
- `dokit/TestSimpleDokitFloatViewKit.ets` — 悬浮 Kit
- `dokit/CustomDokitFragment.ets` / `DemoDoKitView.ets` / `TestSimpleDoKitFloatView.ets` — UI Builder
- `dokit/DemoCustomKitRenderers.ets` — 分发器
- `dokit/DemoCustomKits.ets` — 分组注册

## 注意事项

- **业务 Kit** 不要修改 dokit 模块内的 `InnerKitRegistry` 或 `UniversalActivity`。
- 只有向 SDK 贡献新的**内置工具**时，才需要在 dokit 内注册 `renderXxxFragment`。
- `onClickWithReturn` 返回 `true` 表示点击后自动收起工具面板，与 Android 行为一致。
