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
import { AbstractKit, DoKit } from '@didi/dokit';
import { MyToolFragment } from './MyToolFragment';
import { MyFloatView } from './MyFloatView';

export class MyToolKit extends AbstractKit {
  onClickWithReturn(context: common.UIAbilityContext): boolean {
    // 全屏页，对标 DoKit.launchFullScreen(MyToolFragment::class, activity, bundle)
    DoKit.launchFullScreen(MyToolFragment.CLASS_NAME, { "key": 'value' });
    // 悬浮窗：DoKit.launchFloatingView(MyFloatView.KIT_NAME, wrapBuilder(...));
    return true;
  }
}
```

---

## 第三步：注册到分组（初始化时注入）

```typescript
DoKit.Builder(context)
  .windowStage(windowStage)
  .build();

DoKit.registerCustomKits('业务专区一', [new MyToolKit()]);
```

---

## 第四步：实现 UI（@Builder）并注册到 DoKit（SubWindow 方案）

业务页不用放 DoKit 控件。在 Ability 里 `windowStage` + `attach`，自定义能力分别注册：

```typescript
// 自定义全屏：Ability 初始化时注册一次
DoKit.registerCustomFragment(
  MyToolFragment.CLASS_NAME,
  wrapBuilder(buildMyToolFragment)
);

// 自定义浮窗：Kit 点击时启动（可多实例，球在最上）
DoKit.launchFloatingView(MyFloatView.KIT_NAME, wrapBuilder(MyFloatViewBuilder));

// 全屏根页 / 下级压栈
DoKit.launchFullScreen(MyToolFragment.CLASS_NAME, wrapBuilder(buildMyToolFragment), params);
DoKit.pushFullScreen(MyToolFragment.CLASS_NAME, wrapBuilder(buildMyToolFragment), nestedParams);
```

```typescript
// entry/src/main/ets/dokit/MyToolFragment.ets
@Builder
export function buildMyToolFragment(params: DoKitParams, onClose: FragmentCloseHandler) {
  // UI 实现，params 对标 Android Fragment.arguments
}
```

关闭自定义浮窗：`DoKit.removeFloatingView(name)`。

---

## 快速对照

| 步骤 | Android | Harmony |
|------|---------|---------|
| 页面标识 | `CustomDokitFragment::class.java` | `CustomDokitFragment.CLASS_NAME` |
| 启动全屏 | `DoKit.launchFullScreen(Class, activity, bundle)` | `DoKit.launchFullScreen(CLASS_NAME, bundle)` |
| 下级压栈 | Activity 内 Fragment 栈 | `DoKit.pushFullScreen(CLASS_NAME, bundle)` |
| 启动悬浮 | `DoKit.launchFloating(Class)` | `DoKit.launchFloatingView(name, wrapBuilder(...))` |
| 关闭悬浮 | `DoKit.removeFloating(Class)` | `DoKit.removeFloatingView(name)` |
| 内置 Kit | `startUniversalActivity(Class, activity, null, true)` | `startUniversalActivity(CLASS_NAME, context, undefined, true)` |
| UI 渲染 | 反射 `newInstance()` | `registerCustomFragment` / `launchFloatingView` 的 `@Builder` |

## 参考实现

- `dokit/DemoKit.ets` — `DoKit.launchFloatingView(...)`
- `dokit/TestSimpleDokitFragmentKit.ets` — `DoKit.launchFullScreen(...)`
- `dokit/CustomDokitFragment.ets` — `registerCustomFragment` + 页内 `pushFullScreen`
- `dokit/TestSimpleDoKitFloatView.ets` / `DemoDoKitView.ets`
