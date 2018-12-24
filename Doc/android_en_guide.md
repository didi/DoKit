## How To Use

### 1： Use Gradle to Get latest version of DoraemonKit

```
debugImplementation 'com.didichuxing.doraemonkit:doraemonkit:1.0.0'
```

Tip: Use DoraemonKit in debug model.

### 2： Access method using DoraemonKit's built-in toolset

Add code when the app starts.

```
@Override
public void onCreate() {
    DoraemonKit.install(application）

    // If you need H5 debug
    DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
    @Override
    public void overrideUrlLoading(String s) {
        // Open this link with your H5 container
    }
    ...
}
```

  Through the above steps you can use all of the built-in tools of DorameonKit. If you want to add some of your customized tools, see chapter 3.

### 3: Add a custom test module to the Doraemon panel (non-essential)

For example, we want to add an environment switch module to the Doraemon panel.

Step 1: create a new class, implement the interface IKit, this interface describes a test module on the panel.

Taking our app as an example, after clicking the button, it will enter the environment switching page.

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

Step 2: Add the "Environment Switch" module in the step where Doraemon is installed.

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