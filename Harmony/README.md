# DoKit for HarmonyOS

鸿蒙版 DoKit，目录结构与 Android 对齐。

## 当前进度（v0.1.0）

- [x] 工程骨架：`entry`（Demo）+ `dokit`（核心 HAR）
- [x] 主窗口浮层：悬浮球、工具面板、全屏页、自定义浮窗（`OverlayManager`）
- [x] 内置工具目录 `dokit_system_kits.json` 共 41 项；其中 17 项已在 `SystemKitRegistry` 接入
- [ ] 其余 24 项（平台、LBS、Weex、日志、大图、弱网、启动耗时、布局层级、函数耗时、内存泄漏等）尚无 Kit 实现

已接入的 17 项：

- 常用：App信息、三方库信息、开发者选项、本地语言、沙盒浏览、H5任意门、清理缓存
- 性能：帧率、CPU、内存、网络、Crash、卡顿
- 视觉：取色器、对齐标尺、控件检查、布局边框

帧率、CPU、内存、网络四个入口打开同一张性能监测页。

## 目录结构

```
Harmony/
├── AppScope/
├── entry/                              # Demo 宿主（对标 Android/app）
│   └── src/main/ets/
│       ├── App.ets                     # 对标 App.kt
│       ├── pages/Index.ets             # UI 入口（对标 MainDoKitActivity.kt）
│       ├── DoKitDemoConfig.ets
│       ├── DoKitGradleConfig.ets
│       ├── entryability/EntryAbility.ets
│       ├── dokit/                      # 对标 doraemondemo/dokit/
│       └── module/                     # 对标 doraemondemo/module/
└── dokit/                              # 核心 SDK HAR（对标 Android/dokit）
    └── src/main/ets/
        ├── DoKit.ets                   # 对外 Builder API（对标 DoKit.kt）
        ├── DoKitReal.ets               # 内部安装逻辑（对标 DoKitReal.kt）
        ├── DoKitCallBack.ets           # 回调接口（对标根包 DoKitCallBack.kt）
        ├── constant/DoKitConstants.ets
        ├── aop/DokitThirdLibInfo.ets
        ├── layer/                      # 主窗口浮层
        │   ├── DoKitOverlayManager.ets # 挂到 OverlayManager，按状态对齐各层
        │   ├── DoKitOverlayRoot.ets    # 叠层根
        │   ├── DoKitOverlayPage.ets    # 全屏页
        │   ├── DoKitOverlayPanel.ets   # 工具面板
        │   ├── DoKitOverlayBall.ets    # 悬浮球
        │   └── DoKitOverlayFloat.ets   # 自定义浮窗
        ├── config/                     # GlobalConfig、KitIconResolver、KitDisplayMeta
        ├── core/                       # 对标 kit/core
        │   ├── DoKitManager.ets
        │   ├── DoKitLauncher.ets       # 对外导出名 SimpleDoKitLauncher
        │   ├── DoKitViewManager.ets
        │   ├── DoKitPageStack.ets      # 全屏页栈；按名字先查系统表，没有再记业务页
        │   ├── SystemKitRegistry.ets   # 对外导出名 InnerKitRegistry
        │   ├── SystemFragmentRigistry.ets
        │   ├── CustomFragmentRegistry.ets
        │   └── toolpanel/              # ToolPanelUtil、KitWrapItem（读 dokit_system_kits.json）
        ├── kit/
        │   ├── IKit.ets                # 对标 IKit.kt
        │   ├── AbstractKit.ets
        │   ├── sysinfo/                # App信息、三方库、开发者选项、本地语言
        │   ├── dataclean/              # 清理缓存
        │   ├── fileexplorer/           # 沙盒浏览
        │   ├── webdoor/                # H5任意门
        │   ├── crash/                  # Crash
        │   ├── blockmonitor/           # 卡顿
        │   ├── performance/            # 性能监测页与浮窗
        │   ├── network/                # 网络监测
        │   ├── parameter/              # 帧率、CPU、内存入口
        │   ├── colorpick/              # 取色器
        │   ├── alignruler/             # 对齐标尺
        │   ├── viewcheck/              # 控件检查
        │   └── layoutborder/           # 布局边框
        ├── util/                       # DataCleanUtil、DoKitFileUtil、DoKitSystemUtil、DoKitWindowUtil
        └── model/
```

## 运行

DevEco Studio 打开 `Harmony/` → Sync → 运行 `entry` 模块。

## 接入方式（主窗口 OverlayManager）

业务页不用放 DoKit 控件。在主 Ability 绑定 `WindowStage`，主窗 `loadContent` 成功后再把浮层挂上：

```typescript
import { DoKit } from '@didi-hummer/dokit';

// onWindowStageCreate
DoKit.Builder(context)
  .windowStage(windowStage)
  .productId('your_product_id')
  .debug(true)
  .build();

windowStage.loadContent('pages/Index', (err) => {
  if (!err.code) {
    DoKit.addRootComponentContent();
  }
});

// onWindowStageDestroy
DoKit.removeRootComponentContent();
```

`build()` 只记下主窗口。`addRootComponentContent()` 才把浮层挂到主窗口的 `OverlayManager`。Demo 的调用在 `entry/src/main/ets/entryability/EntryAbility.ets`。

自定义全屏 / 浮窗：

- `DoKit.launchFloatingView(name, wrapBuilder(...), params?, options?)` — 可与悬浮球并存。层表重排后悬浮球在最上面
- `DoKit.launchFullScreen(name, wrapBuilder(...), params?, isSystemFragment?)` — 清栈后打开全屏根页
- `DoKit.pushFullScreen(name, wrapBuilder(...), params?, isSystemFragment?)` — 在当前全屏页上压入下级页

传入的 `wrapBuilder` 在 `isSystemFragment` 为 false 时登记进 `CustomFragmentRegistry`。入栈时 `DoKitPageStack` 按页面名先查 `SystemFragmentRegistry`：系统表有这个名字就记成系统页，没有再记成业务页。系统页的画法在 `SystemFragmentRigistry.ets` 的 `initDefaultFragments` 里登记。

API：

- `DoKit.attachMainIcon()` / `DoKit.detachMainIcon()` — 显示 / 隐藏悬浮球
- `DoKit.showToolPanel()` / `DoKit.detachToolPanel()` — 打开 / 关闭工具面板
- `DoKit.popFullScreen()` / `DoKit.closeFullScreen()` — 退一层 / 关掉全部全屏页
- `DoKit.isMainIconShow` — 悬浮球开关是否开着。全屏页盖住时，开关仍可能是开的

## 图片资源（对标 Android）

| Android | HarmonyOS |
|---------|-------------|
| `mipmap/dk_doraemon` | `dokit/.../media/dk_doraemon.png`（悬浮球） |
| `mipmap/dk_dokit_big` | `entry/.../media/dk_dokit_big.png`（Demo 主页 Logo） |
| `mipmap/dk_app_icon` | `AppScope/.../media/app_icon.png`（应用图标） |
| 各 Kit `R.mipmap.dk_*` | `dokit/.../media/` + `KitIconResolver.ets` |
| `dk_item_kit.xml` 34dp 图标 | 工具面板 `Image` 34vp |

## Demo 配置说明

### 1. 初始化（对标 Android `App.kt`）

见 `entry/src/main/ets/App.ets`：

```typescript
App.onCreate(context, windowStage);
// 内部等价于 Android App.onCreate 中的 DoKit.Builder(this)...
```

`Builder` 上还挂了 `customKits(buildDemoCustomKits())`。`build()` 之后，`App.onCreate` 又用 `DoKit.registerCustomKits` 登记了「业务专区一」「业务专区二」「业务专区」。

### 2. 模块依赖（对标 `doraemonkit.gradle`）

见 `entry/dokit.config.json5`，控制各扩展模块是否启用（当前仅 `dokit` 核心为 true）。

### 3. 权限（对标 AndroidManifest.xml）

`entry/src/main/module.json5` 已声明：

- `INTERNET` — 网络抓包 / Mock
- `GET_NETWORK_INFO` — 网络状态
- `APPROXIMATELY_LOCATION` / `LOCATION` — 位置模拟

### 4. 自定义业务 Kit

`DemoCustomKits.ets` 提供「业务专区1 / 业务专区2」，对标 Android 的 `DemoKit`、`TestSimpleDokitFloatViewKit` 等。`App.onCreate` 里另有一组「业务专区一 / 业务专区二 / 业务专区」。

## 与 Android 配置对照

| Android | HarmonyOS |
|---------|-------------|
| `App.kt` | `App.ets` |
| `doraemonkit.gradle` | `dokit.config.json5` + `DoKitGradleConfig.ets` |
| `DoKit.Builder(...).productId()` | 同 API |
| `customKits(mapKits)` | `customKits(Map<string, AbstractKit[]>)` |
| `MainDoKitActivity.kt` | `pages/Index.ets` |
| 系统悬浮窗 / 页面 Overlay | 主窗口 `OverlayManager` + `DoKitOverlayRoot`（Page / Panel / Ball / Float） |
| `AndroidManifest` 权限 | `module.json5` requestPermissions |

## entry 目录对照（对标 Android app/src/main/java/.../doraemondemo）

| Android | HarmonyOS entry/src/main/ets |
|---------|------------------------------|
| `App.kt` | `App.ets` |
| `MainDoKitActivity.kt` | `pages/Index.ets` |
| `dokit/DemoKit.kt` | `dokit/DemoKit.ets` |
| `dokit/DemoDoKitView.kt` | `dokit/DemoDoKitView.ets` |
| `dokit/TestSimpleDokitFloatViewKit.kt` | `dokit/TestSimpleDokitFloatViewKit.ets` |
| `dokit/TestSimpleDokitFragmentKit.kt` | `dokit/TestSimpleDokitFragmentKit.ets` |
| `dokit/TestSimpleDoKitFloatView.kt` | `dokit/TestSimpleDoKitFloatView.ets` |
| `dokit/CustomDokitFragment.kt` | `dokit/CustomDokitFragment.ets` |
| `dokit/ViewSetupHelper.java` | `dokit/ViewSetupHelper.ets` |
| `module/DoKitItemView.java` | `module/DoKitItemView.ets` |
| `module/CrashTest.kt` | `module/CrashTest.ets` |
| `module/MethodCostTest.kt` | `module/MethodCostTest.ets` |
| `res/layout/activity_dokit_main.xml` | `module/DemoMenuData.ets` |
| — | `entryability/EntryAbility.ets`（鸿蒙 Ability 入口） |
| — | `dokit/MultiPageKit.ets`、`dokit/MultiPagePages.ets`（多级全屏页） |
| — | `dokit/FloatWindow1Kit.ets`、`dokit/FloatWindow1View.ets`（自定义浮窗） |
| — | `dokit/DemoCustomKitRenderers.ets`（文件还在，当前没有引用） |

## 后续开发

`pages/Index.ets` 的 `onItemClick` 已接上显示/隐藏悬浮球、打开工具面板、崩溃模拟、耗时函数、取色器、对齐标尺、控件检查、布局边框。其余菜单项仍弹出开发中提示。

工具面板里未接入的 24 个内置项，仍按 `dokit_system_kits.json` 占位，点击没有对应 Kit。
