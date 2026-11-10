import { DoKitParams } from './CustomViewRegistry';

export type FragmentCloseHandler = () => void;

export type SystemFragmentBuilder = WrappedBuilder<[DoKitParams, FragmentCloseHandler]>;
