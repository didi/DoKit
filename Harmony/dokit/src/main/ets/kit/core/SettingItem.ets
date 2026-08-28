/**
 * 工具面板设置项，对标 Android SettingItem
 */
export class SettingItem {
  name: string = '';
  desc: string = '';
  checked: boolean = false;

  constructor(name: string, desc: string = '', checked: boolean = false) {
    this.name = name;
    this.desc = desc;
    this.checked = checked;
  }
}
