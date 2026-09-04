/**
 * 工具面板设置项，对标 Android SettingItem
 */
export class SettingItem {
  desc: string = '';
  rightDesc: string = '';
  icon: Resource | null = null;
  isChecked: boolean = false;
  canCheck: boolean = false;

  constructor(desc: string, isChecked: boolean = false, canCheck: boolean = false, rightDesc: string = '') {
    this.desc = desc;
    this.isChecked = isChecked;
    this.canCheck = canCheck;
    this.rightDesc = rightDesc;
  }
}
