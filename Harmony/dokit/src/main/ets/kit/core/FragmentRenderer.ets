
/** 对标 Android Bundle extras */
export type DoKitBundle = Record<string, Object>;

export type FragmentCloseHandler = () => void;

export type SystemFragmentBuilder = WrappedBuilder<[DoKitBundle, FragmentCloseHandler]>;
