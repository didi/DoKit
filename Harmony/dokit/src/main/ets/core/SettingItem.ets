/**
 * 工具页里的一行设置，对标 Android SettingItem。
 * 崩溃、卡顿、帧率、CPU、内存页用它画可点击的行。
 */
export class SettingItem {
  /** 左侧文案。 */
  desc: string = '';
  /** 右侧补充文案。空着时行尾显示箭头。 */
  rightDesc: string = '';
  /** 左侧图标，对标 Android SettingItem.icon。 */
  icon: Resource | null = null;
  /** 开关是否打开。 */
  isChecked: boolean = false;
  /** 这一行是否可以勾选。 */
  canCheck: boolean = false;

  constructor(desc: string, isChecked: boolean = false, canCheck: boolean = false, rightDesc: string = '') {
    this.desc = desc;
    this.isChecked = isChecked;
    this.canCheck = canCheck;
    this.rightDesc = rightDesc;
  }
}
