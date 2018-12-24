## How To Use

### 1： Gradle依赖

```
debugImplementation 'com.didichuxing.doraemonkit:doraemonkit:1.0.0'
```

Tip: 只在Debug环境中进行集成，不要带到线上。有一些hook操作会污染线上代码。

### 2： 使用DoraemonKit内置工具集的接入方式

在App启动的时候添加一下代码

```
@Override
public void onCreate() {
    DoraemonKit.install(application）

    // 需要H5任意门功能时
    DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
    @Override
    public void overrideUrlLoading(String s) {
        // 使用自己的H5容器打开这个链接
    }
    ...
}
```

通过以上步骤你就可以使用DorameonKit所有的内置工具集合。如果你想把自己与业务相关的一些工具代码加入到DoraemonKit中做统一管理的话，你可以按照3的步骤来做。

### 3: 添加自定义测试模块到Doraemon面板中（非必要）

比如我们要在Doraemon面板中添加一个环境切换的功能。

第一步：新建一个类，实现IKit的接口，该接口描述哆啦A梦面板中的一个组件。

比如以代驾司机端为例，点击按钮之后会进入环境切换页面。

```
public class EnvSwitchKit implements IKit {
    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getName() {
        return R.string.bh_env_switch;
    }

    @Override
    public int getIcon() {
        return R.drawable.bh_roadbit;
    }

    @Override
    public void onClick(Context context) {
        DebugService service = ServiceManager.getInstance().getService(context, DebugService.class);
        PageManager.getInstance().startFragment(service.getContainer(), EnvSwitchFragment.class);
    }

    @Override
    public void onInit(Context context) {

    }
}
```

第二步: 在Doraemon初始化的地方添加第一步中添加的“环境切换”插件

```
@Override
public void onCreate() {
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits);
    }
    ...
@Override
public void onCreate() {
    List<IKit> kits = new ArrayList<>();
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits);
    ...
}
```