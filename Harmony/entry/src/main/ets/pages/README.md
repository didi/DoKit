# Entry 注入自定义 DoKit 工具

本文说明如何在鸿蒙壳工程（entry）中注册一个新的业务 Kit，并在悬浮窗面板中打开自定义页面。

鸿蒙与 Android 的差异：Android 通过 `DoKit.launchFullScreen(Fragment::class)` 由 SDK 反射创建 Fragment；鸿蒙无法反射，需用 **与 Android 相同的类名（CLASS_NAME）** 作为路由标识，并在宿主 `@Builder` 中渲染 UI。

---

## 第一步：定义页面类（对标 Android Fragment / DoKitView 类）

类名与 Android 保持一致，通过 `CLASS_NAME` 作为路由标识：

```typescript
// entry/src/main/ets/dokit/MyToolFragment.ets
export class MyToolFragment {
  static readonly CLASS_NAME: string = 'MyToolFragment';
}
```

悬浮窗对标 `AbsDoKitView`：

```typescript
export class MyFloatView {
  static readonly CLASS_NAME: string = 'MyFloatView';
}
```

---

## 第二步：实现 Kit 类

继承 `AbstractKit`，点击逻辑与 Android 完全一致：

```typescript
// entry/src/main/ets/dokit/MyToolKit.ets
import { common } from '@kit.AbilityKit';
import { AbstractKit, Category, DoKit } from '@didi/dokit';
import { MyToolFragment } from './MyToolFragment';
import { MyFloatView } from './MyFloatView';

export class MyToolKit extends AbstractKit {
  onClickWithReturn(context: common.UIAbilityContext): boolean {
    // 全屏页，对标 DoKit.launchFullScreen(MyToolFragment::class, activity, bundle)
    DoKit.launchFullScreen(MyToolFragment.CLASS_NAME, { "key": 'value' });
    // 悬浮窗：DoKit.launchFloating(MyFloatView.CLASS_NAME);
    return true;
  }
}
```

---

## 第三步：注册到分组（初始化时注入）

```typescript
DoKit.Builder(context)
  .customKits(buildDemoCustomKits())
  .build();
```

---

## 第四步：实现 UI（@Builder）并挂到 DoKitFloatingWindow

```typescript
// entry/src/main/ets/dokit/MyToolFragment.ets
@Builder
export function buildMyToolFragment(bundle: DoKitBundle, onClose: CustomKitCloseHandler) {
  // UI 实现，bundle 对标 Android Fragment.arguments
}
```

分发器（对标 Android 反射创建，鸿蒙由宿主手动映射）：

```typescript
// entry/src/main/ets/dokit/DemoCustomKitRenderers.ets
@Builder
export function buildEntryCustomFullScreen(fragmentClass: string, bundle: DoKitBundle, onClose: CustomKitCloseHandler) {
  if (fragmentClass === MyToolFragment.CLASS_NAME) {
    buildMyToolFragment(bundle, onClose);
  }
}
```

根页面挂载：

```typescript
DoKitFloatingWindow({
  customFullScreenPage: buildEntryCustomFullScreen,
  customFloatingPage: buildEntryCustomFloating
})
```

关闭悬浮窗使用 `DoKit.removeFloating(MyFloatView.CLASS_NAME)`，对标 Android `DoKit.removeFloating(Class)`。

---

## 快速对照

| 步骤 | Android | Harmony |
|------|---------|---------|
| 页面标识 | `CustomDokitFragment::class.java` | `CustomDokitFragment.CLASS_NAME` |
| 启动全屏 | `DoKit.launchFullScreen(Class, activity, bundle)` | `DoKit.launchFullScreen(CLASS_NAME, bundle)` |
| 启动悬浮 | `DoKit.launchFloating(Class)` | `DoKit.launchFloating(CLASS_NAME)` |
| 关闭悬浮 | `DoKit.removeFloating(Class)` | `DoKit.removeFloating(CLASS_NAME)` |
| 内置 Kit | `startUniversalActivity(Class, activity, null, true)` | `startUniversalActivity(CLASS_NAME, context, undefined, true)` |
| UI 渲染 | 反射 `newInstance()` | 宿主 `@Builder` + `DemoCustomKitRenderers` 分发 |

## 参考实现

- `dokit/DemoKit.ets` — `DoKit.launchFloating(DemoDoKitView.CLASS_NAME)`
- `dokit/TestSimpleDokitFragmentKit.ets` — `DoKit.launchFullScreen(CustomDokitFragment.CLASS_NAME, bundle)`
- `dokit/CustomDokitFragment.ets` / `DemoDoKitView.ets` / `TestSimpleDoKitFloatView.ets`
- `dokit/DemoCustomKitRenderers.ets` — 按 CLASS_NAME 分发
