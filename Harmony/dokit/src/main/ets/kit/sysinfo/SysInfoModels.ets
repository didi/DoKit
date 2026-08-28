/** App 信息列表项，对标 Android SysInfoItem / TitleItem */
export class SysInfoListItem {
  kind: 'title' | 'info' = 'info';
  name: string = '';
  value: string = '';
  /** 权限项，点击跳转系统设置 */
  isPermission: boolean = false;

  static title(name: string): SysInfoListItem {
    const item = new SysInfoListItem();
    item.kind = 'title';
    item.name = name;
    return item;
  }

  static info(name: string, value: string, isPermission: boolean = false): SysInfoListItem {
    const item = new SysInfoListItem();
    item.kind = 'info';
    item.name = name;
    item.value = value;
    item.isPermission = isPermission;
    return item;
  }
}
