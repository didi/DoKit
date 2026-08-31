import { BlockInfo } from '../bean/BlockInfo';

/**
 * 卡顿列表更新回调，对标 Android OnBlockInfoUpdateListener
 */
export type OnBlockInfoUpdateListener = (blockInfo: BlockInfo) => void;
