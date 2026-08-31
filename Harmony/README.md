# DoKit for HarmonyOS

鸿蒙版 DoKit，目录结构与 Android 对齐。

## 当前进度（v0.1.0）

- [x] 工程骨架：`entry`（Demo）+ `dokit`（核心 HAR）
- [x] 悬浮球 + 工具面板 Overlay（41 个内置工具占位）
- [x] **App信息**（常用工具，对标 Android SysInfoKit）
- [x] **H5任意门**（常用工具，对标 Android WebDoorKit / harmonydriver H5AnyDoorPage）
- [x] **沙盒浏览**（常用工具，对标 Android FileExplorerKit）
- [ ] 其余工具真实功能

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
        ├── components/DoKitFloatingWindow.ets
        ├── config/                     # GlobalConfig、KitIconResolver、KitDisplayMeta
        ├── kit/
        │   ├── IKit.ets                # 对标 IKit.kt
        │   ├── AbstractKit.ets
        │   ├── core/                   # DoKitManager、SimpleDoKitLauncher、InnerKitRegistry
        │   ├── toolpanel/              # ToolPanelUtil、KitWrapItem（读 dokit_system_kits.json）
        │   ├── sysinfo/                # SysInfoKit、ThirdLibInfoKit、DevelopmentPageKit + Page
        │   ├── dataclean/              # DataCleanKit + DataCleanPage
        │   ├── fileexplorer/           # FileExplorerKit + FileExplorerPage
        │   └── webdoor/                # WebDoorKit + WebDoorPage
        ├── util/                       # DataCleanUtil、DoKitFileUtil、DoKitSystemUtil
        └── model/
```

## 运行

DevEco Studio 打开 `Harmony/` → Sync → 运行 `entry` 模块。

## 接入方式（Overlay 方案）

对标 harmonydriver，不再使用 SubWindow，而是在宿主页面根 `Stack` 叠加 `DoKitFloatingWindow`：

```typescript
import { DoKit, DoKitFloatingWindow } from '@didi/dokit';

// 1. Ability 中初始化（无需 windowStage）
DoKit.Builder(context)
  .productId('your_product_id')
  .debug(true)
  .build();

// 2. 主页 Stack 叠加
Stack() {
  // 业务内容
  YourMainPage()

  DoKitFloatingWindow()
}
.width('100%')
.height('100%')
```

API：

- `DoKit.show()` / `DoKit.hide()` — 显示/隐藏悬浮球
- `DoKit.showToolPanel()` — 远程打开工具面板
- `DoKit.isMainIconShow` — 悬浮球是否可见

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
App.onCreate(context);
// 内部等价于 Android App.onCreate 中的 DoKit.Builder(this)...
```

### 2. 模块依赖（对标 `doraemonkit.gradle`）

见 `entry/dokit.config.json5`，控制各扩展模块是否启用（当前仅 `dokit` 核心为 true）。

### 3. 权限（对标 AndroidManifest.xml）

`entry/src/main/module.json5` 已声明：

- `INTERNET` — 网络抓包 / Mock
- `GET_NETWORK_INFO` — 网络状态
- `APPROXIMATELY_LOCATION` / `LOCATION` — 位置模拟

### 4. 自定义业务 Kit

工具面板「业务专区1 / 业务专区2」来自 `DemoCustomKits.ets`，对标 Android 的 `DemoKit`、`TestSimpleDokitFloatViewKit` 等。

## 与 Android 配置对照

| Android | HarmonyOS |
|---------|-------------|
| `App.kt` | `App.ets` |
| `doraemonkit.gradle` | `dokit.config.json5` + `DoKitGradleConfig.ets` |
| `DoKit.Builder(...).productId()` | 同 API |
| `customKits(mapKits)` | `customKits(Map<string, AbstractKit[]>)` |
| `MainDoKitActivity.kt` | `pages/Index.ets` |
| SubWindow 悬浮球 | `DoKitFloatingWindow` Overlay |
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
| — | `dokit/DemoCustomKitRenderers.ets`（鸿蒙 Overlay 渲染分发） |

## 后续开发

在 `pages/Index.ets` 的 `onItemClick` 中按菜单项逐个接入；工具 Kit 在 `DoKitFloatingWindow` 面板点击后路由。
