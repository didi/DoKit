import { DoKitBundle } from './FragmentRenderer';

/** 对标 Android DoKitViewLaunchMode */
export enum DoKitViewLaunchMode {
  SINGLE_INSTANCE = 0,
  COUNTDOWN = 1
}

/**
 * 对标 Android kit/core/DoKitIntent
 * targetClass 对应 Android Class<out AbsDoKitView> 的简单类名
 */
export class DoKitIntent {
  targetClass: string;
  bundle: DoKitBundle = {};
  tag: string;
  mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE;

  constructor(targetClass: string) {
    this.targetClass = targetClass;
    this.tag = targetClass;
  }
}

export type DoKitViewCloseHandler = () => void;

/** 宿主渲染自定义 DoKitView，对标 Android AbsDoKitView 反射实例化 */
export type CustomDoKitViewRenderer = (
  targetClass: string,
  bundle: DoKitBundle,
  onClose: DoKitViewCloseHandler
) => void;

/** 宿主渲染自定义 Fragment，对标 UniversalActivity FRAGMENT_CUSTOM */
export type CustomFragmentRenderer = (
  fragmentClass: string,
  bundle: DoKitBundle,
  onClose: DoKitViewCloseHandler
) => void;
