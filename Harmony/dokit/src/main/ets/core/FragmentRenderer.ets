import { DoKitParams } from './CustomViewRegistry';

/** 关闭全屏页。全屏层退一层时调用。 */
export type FragmentCloseHandler = () => void;

/** 全屏页画法。参数是打开时的数据和关闭函数。系统页和业务页共用这个类型。 */
export type SystemFragmentBuilder = WrappedBuilder<[DoKitParams, FragmentCloseHandler]>;
